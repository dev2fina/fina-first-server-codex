package net.fina.server.jcr.impl;

import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.NotFoundException;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.server.util.FileTypeCheckUtil;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.common.shared.WrongFileTypeException;
import net.fina.common.shared.jcr.DescriptionModel;
import net.fina.common.shared.jcr.JcrCustomConstants;
import net.fina.common.shared.jcr.RepositoryNodeMetaModel;
import net.fina.server.fi.api.FiLocal;
import net.fina.server.fi.model.FiMetaModel;
import net.fina.server.i18n.api.LanguageLocal;
import net.fina.server.i18n.helper.Description;
import net.fina.server.i18n.model.DescriptionModelHelper;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.jcr.api.FileRepositoryLocal;
import net.fina.server.jcr.model.RepositoryNodeMetaModelHelper;
import net.fina.server.jcr.qualifier.JackRabbitRepo;
import net.fina.server.jcr.util.FileRepositoryUtil;
import net.fina.server.security.api.UserLocal;
import net.fina.server.store.api.RepositoryLocal;
import net.fina.server.store.qualifier.FileRepository;
import org.apache.commons.io.IOUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.core.query.QueryImpl;
import org.apache.jackrabbit.util.ISO9075;
import org.jboss.logging.Logger;

import javax.jcr.*;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;
import javax.jcr.version.VersionManager;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@Stateless
@Local(FileRepositoryLocal.class)
@Default
@JackRabbitRepo
@Interceptors(RecordingAuditor.class)
public class JackRabbitFileRepositorySession implements FileRepositoryLocal {
    private final Logger log = Logger.getLogger(getClass().getName());

    @Inject
    private UserLocal userLocal;
    @Inject
    private LanguageLocal languageLocal;
    @Inject
    private FiLocal fiLocal;
    @Inject
    private RepositoryLocal repositoryLocal;

    @Inject
    @FileRepository
    private Session fileRepositorySession;


    @Override
    public PaginatedListWrapper<RepositoryNodeMetaModel> loadAll(String nodeId, int offset, int limit, String orderBy, List<String> filterByFis, String filterByName) {
        PaginatedListWrapper<RepositoryNodeMetaModel> result = new PaginatedListWrapper<>();

        try {
            Node node;
            if (nodeId == null) {
                node = JcrUtils.getOrCreateByPath("/" + FileRepositoryUtil.getFiDocumentsRootFolderPath(),
                        JcrConstants.NT_FOLDER, fileRepositorySession);
                if (node.isNew()) {
                    node.addMixin(JcrConstants.MIX_VERSIONABLE);
                }
            } else {
                node = fileRepositorySession.getNodeByIdentifier(nodeId);
            }

            String queryString = "select * from [nt:base] as a where ischildnode(a,[{0}])";
            queryString = MessageFormat.format(queryString, node.getPath());

            StringBuilder qb = new StringBuilder(queryString);
            List<RepositoryNodeMetaModel> items = new ArrayList<>();

            filterByFis = filterByFis.isEmpty() ? null : filterByFis;

            if (filterByFis == null && filterByName == null) {
                qb.append(" order by ").append(getOrderByParam(orderBy));
                items = queryResult(qb.toString(), offset, limit);
                result.setList(items);
                result.setTotalResults(queryResult(queryString, -1, -1).size());
            } else if (filterByFis != null && filterByName == null) {
                return filterNodesByFis(node, nodeId, filterByFis, orderBy, offset, limit);
            } else if (filterByFis != null) {
                return filterNodesByFiAndName(filterByFis, filterByName, orderBy, offset, limit);
            } else {
                boolean isRoot = nodeId == null;
                String searchScopePath = isRoot ? FileRepositoryUtil.getFiDocumentsRootFolderPath() : node.getPath();
                String nodeTypeToSearch = isRoot ? JcrConstants.NT_FOLDER : JcrConstants.NT_FILE;

                return filterNodesByName(filterByName, orderBy, searchScopePath, nodeTypeToSearch, offset, limit, true, false);
            }

        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
        return result;
    }


    @Override
    public PaginatedListWrapper<RepositoryNodeMetaModel> loadExternalUserFiles(int offset, int limit, String orderBy, String filter) throws FinATypeException {
        PaginatedListWrapper<RepositoryNodeMetaModel> result = new PaginatedListWrapper<>();

        try {
            if (filter != null && !filter.isEmpty()) {
                return filterNodesByName(filter, orderBy, ISO9075.encodePath(getCurrentUserDocumentsPath()), JcrConstants.NT_FILE, offset, limit, false, true);
            } else {
                String queryString = "select * from [nt:base] as a where ischildnode(a,[{0}]) and [{1}]=false  order by " + getOrderByParam(orderBy);
                queryString = MessageFormat.format(queryString, ISO9075.encodePath(getCurrentUserDocumentsPath()), JcrCustomConstants.PROP_MARK_DELETED);
                List<RepositoryNodeMetaModel> items = queryResult(queryString, offset, limit);
                result.setList(items);
                result.setTotalResults(queryResult(queryString, -1, -1).size());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new FinATypeException(e.getMessage());
        }
        return result;
    }

    @Override
    public RepositoryNodeMetaModel saveFiDocument(RepositoryNodeMetaModel repositoryFile) throws FinATypeException, WrongFileTypeException {
        FileTypeCheckUtil.checkDefault(repositoryFile.getContent());

        repositoryFile.setPath(getCurrentUserDocumentsPath());

        try {
            // Get or create fi folder node ==========================
            Node fiNode = JcrUtils.getOrCreateByPath(ISO9075.encodePath(repositoryFile.getPath()), JcrConstants.NT_FOLDER, fileRepositorySession);
            fiNode.addMixin(JcrCustomConstants.FINA_NT_FOLDER_MIXIN);
            if (fiNode.isNew()) {
                fiNode.addMixin(JcrConstants.MIX_VERSIONABLE);
            }

            FiMetaModel fiMetaModel = getCurrentUseFiCode();
            List<DescriptionModel> fiDescriptions = new ArrayList<>();
            fiMetaModel.getDescription().forEach(d -> {
                DescriptionModel model = new DescriptionModel(languageLocal.getLanguageCodeById(d.getLangId()), d.getDescription());
                fiDescriptions.add(model);
            });
            fiNode.setProperty(JcrCustomConstants.PROP_DESCRIPTION, RepositoryNodeMetaModelHelper.getJsonDescription(fiDescriptions));

            if (fiNode.hasProperty(JcrConstants.JCR_LASTMODIFIED)) {
                fiNode.setProperty(JcrConstants.JCR_LASTMODIFIED, Calendar.getInstance());
            }

            // =========================================================

            return saveFile(repositoryFile);

        } catch (Exception re) {
            log.error(re);
            throw new FinATypeException(re.getMessage());
        }
    }

    @Override
    public RepositoryNodeMetaModel saveFile(RepositoryNodeMetaModel repositoryFile) throws Exception {
        return saveFile(fileRepositorySession, repositoryFile);
    }

    @Override
    public RepositoryNodeMetaModel saveFile(RepositoryNodeMetaModel repositoryFile, InputStream inputStream) throws Exception {
        return saveFile(fileRepositorySession, repositoryFile, inputStream);
    }

    public RepositoryNodeMetaModel saveFile(Session session, RepositoryNodeMetaModel repositoryFile) throws Exception {
        return saveFile(session, repositoryFile, new ByteArrayInputStream(repositoryFile.getContent()));
    }

    public RepositoryNodeMetaModel saveFile(Session session, RepositoryNodeMetaModel repositoryFile, InputStream inputStream) throws Exception {
        //Retrieve repo default workspace version manager
        VersionManager vm = session.getWorkspace().getVersionManager();
        // File node

        String path = repositoryFile.getPath().startsWith("/") ? repositoryFile.getPath() : "/" + repositoryFile.getPath();
        Node parentFolder = JcrUtils.getOrCreateByPath(ISO9075.encodePath(path), JcrConstants.NT_FOLDER, session);

        if (parentFolder.isNew()) {
            parentFolder.addMixin(JcrConstants.MIX_VERSIONABLE);
        }

        Node fileNode = JcrUtils.getOrAddNode(parentFolder, ISO9075.encodePath(repositoryFile.getName()), JcrCustomConstants.FINA_FILE_NODE_TYPE);

        if (fileNode.isNew()) {
            //Add versional property
            fileNode.addMixin(JcrConstants.MIX_VERSIONABLE);
        }

        if (!fileNode.isCheckedOut()) {
            vm.checkout(fileNode.getPath());
        }

        fileNode.setProperty(JcrCustomConstants.PROP_DESCRIPTION, RepositoryNodeMetaModelHelper.getJsonDescription(repositoryFile.getDescriptions()));
        fileNode.setProperty(JcrCustomConstants.PROP_MARK_DELETED, false);
        fileNode.setProperty(JcrCustomConstants.PROP_EXTERNAL_ID, repositoryFile.getExternalId());
        fileNode.setProperty(JcrConstants.JCR_LASTMODIFIED, Calendar.getInstance());

        try (InputStream stream = inputStream) {
            Binary binary = session.getValueFactory().createBinary(stream);
            Node contentNode = JcrUtils.getOrAddNode(fileNode, JcrConstants.JCR_CONTENT, JcrConstants.NT_RESOURCE);
            contentNode.setProperty(JcrConstants.JCR_DATA, binary);
        }

        //Save current session changes
        session.save();

        Version versionNode = vm.checkin(fileNode.getPath());
        repositoryFile.setId(fileNode.getIdentifier());
        repositoryFile.setVersionId(versionNode.getIdentifier());

        return repositoryFile;
    }


    @Override
    public RepositoryNodeMetaModel getVersionFileWithContent(String nodeId, String version) throws FinATypeException {
        RepositoryNodeMetaModel repositoryFile = new RepositoryNodeMetaModel();
        try {
            Node node = fileRepositorySession.getWorkspace().getSession().getNodeByIdentifier(nodeId);
            VersionManager vm = fileRepositorySession.getWorkspace().getVersionManager();
            InputStream inputStream = null;
            if (version != null && vm.getVersionHistory(node.getPath()).getAllVersions().getSize() > 1) {
                inputStream = repositoryLocal.getFileStreamByVersion(fileRepositorySession, node.getParent().getPath(), node.getName(), version);
            } else {
                inputStream = repositoryLocal.getLatestVersionFileStream(fileRepositorySession, node.getParent().getPath(), node.getName());
            }
            repositoryFile.setContent(IOUtils.toByteArray(inputStream));
            repositoryFile.setName(ISO9075.decode(node.getName()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new FinATypeException(e.getMessage());
        }
        return repositoryFile;
    }

    @Override
    public InputStream getVersionFileStreamWithContent(String nodeId, String version) throws IOException, FinATypeException {
        try {
            Node node = fileRepositorySession.getWorkspace().getSession().getNodeByIdentifier(nodeId);
            VersionManager vm = fileRepositorySession.getWorkspace().getVersionManager();
            InputStream inputStream = null;
            if (version != null && vm.getVersionHistory(node.getPath()).getAllVersions().getSize() > 1) {
                inputStream = repositoryLocal.getFileStreamByVersion(fileRepositorySession, node.getParent().getPath(), node.getName(), version);
            } else {
                inputStream = repositoryLocal.getLatestVersionFileStream(fileRepositorySession, node.getParent().getPath(), node.getName());
            }
            return inputStream;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new FinATypeException(e.getMessage());
        }
    }

    @Override
    public void markFileAsDeleted(String id, boolean deleted) {
        try {
            Node node = fileRepositorySession.getNodeByIdentifier(id);
            VersionManager vm = fileRepositorySession.getWorkspace().getVersionManager();
            if (!node.isCheckedOut()) {
                vm.checkout(node.getPath());
            }
            node.setProperty(JcrCustomConstants.PROP_MARK_DELETED, true);
            fileRepositorySession.save();
            vm.checkin(node.getPath());
        } catch (RepositoryException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public List<RepositoryNodeMetaModel> getNodeVersions(String nodeId, String orderBy) {
        List<RepositoryNodeMetaModel> result = new ArrayList<>();
        try {
            Node currentNode = fileRepositorySession.getNodeByIdentifier(nodeId);
            VersionManager vm = fileRepositorySession.getWorkspace().getVersionManager();
            VersionHistory versionHistory = vm.getVersionHistory(currentNode.getPath());
            VersionIterator versionIterator = versionHistory.getAllVersions();

            while (versionIterator.hasNext()) {
                Version curr = versionIterator.nextVersion();
                RepositoryNodeMetaModel model = RepositoryNodeMetaModelHelper.jcrNodeToModel(currentNode);
                model.setLastModified(curr.getCreated().getTime());
                model.setVersionId(curr.getIdentifier());
                result.add(model);
            }

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        if (!result.isEmpty()) {
            result.remove(0);
        }

        RepositoryNodeMetaModelHelper.sort(result, orderBy);
        return result;
    }

    @Override
    public RepositoryNodeMetaModel updateFileDescription(RepositoryNodeMetaModel model) throws FinATypeException {
        try {
            Node node = fileRepositorySession.getNodeByIdentifier(model.getId());
            VersionManager vm = fileRepositorySession.getWorkspace().getVersionManager();
            if (!node.isCheckedOut()) {
                vm.checkout(node.getPath());
            }
            node.setProperty(JcrCustomConstants.PROP_DESCRIPTION, RepositoryNodeMetaModelHelper.getJsonDescription(model.getDescriptions()));
            node.setProperty(JcrConstants.JCR_LASTMODIFIED, Calendar.getInstance());
            fileRepositorySession.save();
            vm.checkin(node.getPath());

            return model;
        } catch (RepositoryException e) {
            log.error(e.getMessage(), e);
            throw new FinATypeException(e.getMessage());
        }
    }

    @Override
    public List<RepositoryNodeMetaModel> loadFilesByRelativePath(String path) {
        try {
            Node node = JcrUtils.getOrCreateByPath("/" + path,
                    JcrConstants.NT_FOLDER, fileRepositorySession);

            PaginatedListWrapper<RepositoryNodeMetaModel> nodes = loadAll(node.getIdentifier(), 0, 100, null, new ArrayList<>(), null);
            return nodes.getList();
        } catch (RepositoryException e) {
            log.error(e.getMessage(), e);
        }
        return new ArrayList<>();
    }

    @Override
    public void deleteFolder(String path) {
        try {
            Node node = JcrUtils.getOrCreateByPath("/" + FileRepositoryUtil.getFiDocumentsRootFolderPath(),
                    JcrConstants.NT_FOLDER, fileRepositorySession);
            node.remove();
            fileRepositorySession.save();

        } catch (RepositoryException | NotFoundException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void deleteFile(String nodeId) {
        try {
            Node node = fileRepositorySession.getNodeByIdentifier(nodeId);
            node.remove();
            fileRepositorySession.save();
        } catch (RepositoryException e) {
            log.error(e.getMessage(), e);
        }
    }


    private String getCurrentUserDocumentsPath() {
        Map<String, Description> fis = fiLocal.loadFiCodeAndNames(userLocal.getCurrentUserLogin());
        Map.Entry<String, Description> fiOptional = fis.entrySet().stream().findFirst().get();
        return "/" + FileRepositoryUtil.getFiDocumentsRootFolderPath() + "/" + fiOptional.getKey();
    }

    private List<RepositoryNodeMetaModel> queryResult(String queryString, int offset, int limit) throws Exception {
        List<RepositoryNodeMetaModel> result = new ArrayList<>();
        QueryManager queryManager = fileRepositorySession.getWorkspace().getQueryManager();

        Query query = queryManager.createQuery(queryString, Query.JCR_SQL2);
        QueryImpl q = (QueryImpl) query;
        if (offset >= 0 && limit >= 0) {
            q.setOffset(offset);
            q.setLimit(limit);
        }

        QueryResult queryResult = q.execute();
        NodeIterator nodeIter = queryResult.getNodes();
        while (nodeIter.hasNext()) {
            Node current = nodeIter.nextNode();
            RepositoryNodeMetaModel model = RepositoryNodeMetaModelHelper.jcrNodeToModel(current);
            model.setVersionId(fileRepositorySession.getWorkspace().getVersionManager().getBaseVersion(current.getPath()).getIdentifier());
            result.add(model);
        }

        return result;
    }

    private FiMetaModel getCurrentUseFiCode() {
        FiMetaModel fiModel = new FiMetaModel();
        Map<String, Description> fis = fiLocal.loadFiCodeAndNames(userLocal.getCurrentUserLogin());
        Map.Entry<String, Description> fiOptional = fis.entrySet().stream().findFirst().get();
        fiModel.setCode(fiOptional.getKey());
        fiModel.setDescription(DescriptionModelHelper.toModel(fiOptional.getValue()));

        return fiModel;
    }


    private String getOrderByParam(String orderBy) {
        if (orderBy == null || orderBy.trim().isEmpty()) {
            return "[jcr:lastModified] desc";
        }

        String[] params = orderBy.split(" ");
        String orderByParamName = params[0];

        switch (orderByParamName) {
            case "descriptions":
                orderByParamName = JcrCustomConstants.PROP_DESCRIPTION;
                break;
            case "lastModified":
                orderByParamName = "jcr:lastModified";
                break;
        }

        return "[" + orderByParamName + "] " + (params.length == 2 ? params[1] : "ASC");
    }


    private PaginatedListWrapper<RepositoryNodeMetaModel> filterNodesByName(String filterByName,
                                                                            String orderBy,
                                                                            String parentNodePath,
                                                                            String nodeType,
                                                                            int offset,
                                                                            int limit,
                                                                            boolean includeDescendant,
                                                                            boolean excludeDeleted) throws Exception {
        PaginatedListWrapper<RepositoryNodeMetaModel> result = new PaginatedListWrapper<>();

        filterByName = filterByName.toLowerCase();
        String filter = "LOWER([fina:description]) LIKE '%" + filterByName + "%' or LOWER(LOCALNAME()) LIKE '%" + ISO9075.encode(filterByName) + "%'";
        String ancestryCheckFunction = includeDescendant ? "isdescendantnode" : "ischildnode";

        if (!parentNodePath.startsWith("/")) {
            parentNodePath = "/" + parentNodePath;
        }

        String queryString = "select * from [" + nodeType + "] as a where " + ancestryCheckFunction + "(a,[" + parentNodePath + "]) and (" + filter + ")";

        if (excludeDeleted) {
            queryString += " and [" + JcrCustomConstants.PROP_MARK_DELETED + "]=false ";
        }
        queryString += " order by " + getOrderByParam(orderBy);

        List<RepositoryNodeMetaModel> items = queryResult(queryString, offset, limit);
        result.setList(items);
        result.setTotalResults(queryResult(queryString, -1, -1).size());

        return result;
    }

    private PaginatedListWrapper<RepositoryNodeMetaModel> filterNodesByFiAndName(List<String> filterByFis,
                                                                                 String filterByName,
                                                                                 String orderBy,
                                                                                 int offset,
                                                                                 int limit) throws Exception {
        PaginatedListWrapper<RepositoryNodeMetaModel> result = new PaginatedListWrapper<>();

        String queryString = "select * from [nt:file] as a where ";

        StringBuilder filter = new StringBuilder();
        filter.append("(");
        filter.append("(");

        for (int i = 0; i < filterByFis.size(); i++) {
            String fiCode = filterByFis.get(i);
            String path = "/" + FileRepositoryUtil.getFiDocumentsRootFolderPath() + "/" + fiCode;
            path = ISO9075.encodePath(path);
            filter.append("ischildnode(a,[").append(path).append("])");
            if (i != filterByFis.size() - 1) {
                filter.append(" or ");
            }
        }
        filter.append(")");
        filter.append(" and ").append("([fina:description] LIKE '%").append(filterByName).append("%' or LOCALNAME() LIKE '%").append(filterByName).append("%')")
                .append(")")
                .append(" order by ")
                .append(getOrderByParam(orderBy));

        queryString += filter.toString();

        List<RepositoryNodeMetaModel> items = queryResult(queryString, offset, limit);
        result.setList(items);
        result.setTotalResults(queryResult(queryString, -1, -1).size());

        return result;
    }

    private PaginatedListWrapper<RepositoryNodeMetaModel> filterNodesByFis(Node node,
                                                                           String nodeId,
                                                                           List<String> filterByFis,
                                                                           String orderBy,
                                                                           int offset,
                                                                           int limit) throws Exception {
        PaginatedListWrapper<RepositoryNodeMetaModel> result = new PaginatedListWrapper<>();

        String fiFilterQuery = getFiFilterQuery(nodeId, filterByFis);
        String queryString = "select * from [nt:base] as a where ischildnode(a,[{0}])";
        queryString = MessageFormat.format(queryString, node.getPath());

        StringBuilder qb = new StringBuilder(queryString);
        qb.append(!fiFilterQuery.isEmpty() ? " and " : "")
                .append(fiFilterQuery)
                .append(" order by ")
                .append(getOrderByParam(orderBy));

        List<RepositoryNodeMetaModel> items = queryResult(qb.toString(), offset, limit);
        result.setList(items);
        result.setTotalResults(queryResult(qb.toString(), -1, -1).size());

        return result;
    }

    private String getFiFilterQuery(String nodeId, List<String> filterByFis) {
        StringBuilder filterQuery = new StringBuilder();
        if (nodeId == null && filterByFis != null && !filterByFis.isEmpty()) {
            filterQuery.append(" (");

            for (int i = 0; i < filterByFis.size(); i++) {
                filterQuery.append("LOCALNAME()=")
                        .append("'")
                        .append(ISO9075.encode(filterByFis.get(i)))
                        .append("'");
                if (i != filterByFis.size() - 1) {
                    filterQuery.append(" or ");
                }
            }
            filterQuery.append(")");
        }

        return filterQuery.toString();
    }
}
