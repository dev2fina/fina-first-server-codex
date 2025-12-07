package net.fina.server.jcr.impl;

import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.server.util.ConfigurationUtil;
import net.fina.common.server.util.FileTypeCheckUtil;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.common.shared.WrongFileTypeException;
import net.fina.common.shared.jcr.DescriptionModel;
import net.fina.common.shared.jcr.JcrCustomConstants;
import net.fina.common.shared.jcr.RepositoryNodeMetaModel;
import net.fina.ecm.alfresco.AlfrescoClient;
import net.fina.ecm.alfresco.api.common.constant.APIConstants;
import net.fina.ecm.alfresco.api.common.parameters.IncludeParam;
import net.fina.ecm.alfresco.api.common.parameters.OrderByParam;
import net.fina.ecm.alfresco.api.common.representation.ResultPaging;
import net.fina.ecm.alfresco.api.common.representation.UnexpectedErrorRepresentation;
import net.fina.ecm.alfresco.api.core.model.body.NodeBodyCreate;
import net.fina.ecm.alfresco.api.core.model.body.NodeBodyUpdate;
import net.fina.ecm.alfresco.api.core.model.body.PermissionsBodyUpdate;
import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import net.fina.ecm.alfresco.api.core.model.representation.PermissionElementRepresentation;
import net.fina.ecm.alfresco.api.core.model.representation.VersionRepresentation;
import net.fina.ecm.alfresco.api.search.body.*;
import net.fina.ecm.alfresco.api.search.model.ResultNodeRepresentation;
import net.fina.ecm.alfresco.api.search.model.ResultSetRepresentation;
import net.fina.ecm.util.AlfrescoUtil;
import net.fina.server.fi.api.FiLocal;
import net.fina.server.fi.model.FiMetaModel;
import net.fina.server.i18n.api.LanguageLocal;
import net.fina.server.i18n.helper.Description;
import net.fina.server.i18n.model.DescriptionModelHelper;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.jcr.api.FileRepositoryLocal;
import net.fina.server.jcr.model.RepositoryNodeMetaModelHelper;
import net.fina.server.jcr.qualifier.AlfrescoRepo;
import net.fina.server.jcr.util.FileRepositoryUtil;
import net.fina.server.security.api.UserLocal;
import org.apache.commons.io.IOUtils;
import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;
import org.jboss.resteasy.plugins.providers.multipart.OutputPart;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.*;

@Stateless
@Local(FileRepositoryLocal.class)
@AlfrescoRepo
@Interceptors(RecordingAuditor.class)
public class AlfrescoFileRepositorySession implements FileRepositoryLocal {
    private final Logger log = Logger.getLogger(getClass().getName());

    @Inject
    private UserLocal userLocal;
    @Inject
    private FiLocal fiLocal;
    @Inject
    private LanguageLocal languageLocal;


    private AlfrescoClient getAlfrescoClient(String acceptLanguage) {
        return new AlfrescoClient.Builder().connectExternal(acceptLanguage, ConfigurationUtil.get().get("ECM.endpoint"), userLocal.getCurrentUserLogin(), ConfigurationUtil.get().get("ECM.userHashSalt")).build();
    }

    @Override
    public PaginatedListWrapper<RepositoryNodeMetaModel> loadAll(String nodeId, int offset, int limit, String orderBy, List<String> filterByFis, String filterByName) throws FinATypeException {
        PaginatedListWrapper<RepositoryNodeMetaModel> result = new PaginatedListWrapper<>();

        AlfrescoClient alfrescoClient = getAlfrescoClient("*");
        nodeId = nodeId == null ? APIConstants.FOLDER_ROOT : nodeId;
        String relativePath = nodeId.equals(APIConstants.FOLDER_ROOT) ? FileRepositoryUtil.getFiDocumentsRootFolderPath() : null;
        if (nodeId.equals(APIConstants.FOLDER_ROOT)) {
            NodeRepresentation finaRepositoryNode = getOrCreateUserRootFolder(alfrescoClient, APIConstants.FOLDER_ROOT, JcrCustomConstants.USER_ROOT_FOLDER, new TreeMap<>());
            getOrCreateUserRootFolder(alfrescoClient, finaRepositoryNode.getId(), JcrCustomConstants.USER_FILES_FOLDER, new TreeMap<>());
        }

        ResultPaging<? extends NodeRepresentation> nodes = new ResultPaging<>();
        if (filterByFis.isEmpty() && filterByName == null) {
            OrderByParam orderByParam = new OrderByParam(Collections.singletonList(getOrderByParam(orderBy)));

            nodes = alfrescoClient.getNodesAPI().listNodeChildrenCall(nodeId, offset, limit, orderByParam, null,
                    new IncludeParam(Arrays.asList("properties", "path")), relativePath, null, null);
        } else if (!filterByFis.isEmpty() && filterByName == null) {
            nodes = filterNodesByFis(nodeId, filterByFis, orderBy, offset, limit);
        } else if (!filterByFis.isEmpty()) {
            nodes = filterNodesByFiAndName(filterByFis, filterByName, orderBy, offset, limit);
        } else {
            nodes = filterNodesByName(filterByName, null, false, orderBy, offset, limit, nodeId);
        }

        result.setList(RepositoryNodeMetaModelHelper.toList(nodes.getObjects()));
        result.setTotalResults(nodes.getPagination().getTotalItems());
        result.setPageSize(nodes.getPagination().getMaxItems());

        return result;
    }


    @Override
    public PaginatedListWrapper<RepositoryNodeMetaModel> loadExternalUserFiles(int offset, int limit, String orderBy, String filter) throws FinATypeException {
        offset = Math.max(offset, 0);
        limit = limit < 0 ? Integer.MAX_VALUE : limit;

        PaginatedListWrapper<RepositoryNodeMetaModel> result = new PaginatedListWrapper<>();
        AlfrescoClient alfrescoClient = getAlfrescoClient("*");

        NodeRepresentation userRootNode = getOrCreateUserRootFolder(alfrescoClient, APIConstants.FOLDER_ROOT, JcrCustomConstants.USER_ROOT_FOLDER, new TreeMap<>());
        NodeRepresentation userFilesNode = getOrCreateUserRootFolder(alfrescoClient, userRootNode.getId(), JcrCustomConstants.USER_FILES_FOLDER, new TreeMap<>());

        FiMetaModel fiMetaModel = getCurrentUseFi();

        List<DescriptionModel> fiDescriptions = new ArrayList<>();
        fiMetaModel.getDescription().forEach(d -> {
            DescriptionModel model = new DescriptionModel(languageLocal.getLanguageCodeById(d.getLangId()), d.getDescription());
            fiDescriptions.add(model);
        });

        String orderByParam = getOrderByParamCMIS(orderBy);

        TreeMap<String, Object> properties = new TreeMap<>();
        properties.put("cm:description", RepositoryNodeMetaModelHelper.getJsonDescription(fiDescriptions));
        NodeRepresentation fiNode = getOrCreateUserRootFolder(alfrescoClient, userFilesNode.getId(), fiMetaModel.getCode(), properties);

        ResultPaging<? extends NodeRepresentation> resultSet;

        if (filter == null || filter.isEmpty()) {
            String queryString = "select * from fina:fiDocument where  IN_FOLDER('" + fiNode.getId() + "') and " + JcrCustomConstants.PROP_MARK_DELETED + "=false order by " + orderByParam;
            RequestQuery query = new RequestQuery().query(queryString).language(RequestQuery.LanguageEnum.CMIS);

            QueryBody body = new QueryBody().query(query).include(Arrays.asList("properties", "path")).paging(new RequestPagination().
                    skipCount(offset).
                    maxItems(limit));

            resultSet = alfrescoClient.getSearchAPI().search(body);
        } else {
            resultSet = filterNodesByName(filter, fiNode.getName(), true, orderBy, offset, limit, null);
        }

        result.setList(RepositoryNodeMetaModelHelper.toList(resultSet.getObjects()));
        result.setTotalResults(resultSet.getPagination().getTotalItems());
        result.setPageSize(resultSet.getPagination().getMaxItems());

        return result;
    }


    @Override
    public RepositoryNodeMetaModel saveFiDocument(RepositoryNodeMetaModel repositoryFile) throws IOException, FinATypeException, WrongFileTypeException {
        FileTypeCheckUtil.checkDefault(repositoryFile.getContent());
        repositoryFile.setPath(getCurrentUserDocumentsPath());
        AlfrescoClient alfrescoClient = getAlfrescoClient("*");
        NodeRepresentation finaRepoNode = getOrCreateUserRootFolder(alfrescoClient, APIConstants.FOLDER_ROOT, JcrCustomConstants.USER_ROOT_FOLDER, new TreeMap<>());
        NodeRepresentation userFolderNode = getOrCreateUserRootFolder(alfrescoClient, finaRepoNode.getId(), JcrCustomConstants.USER_FILES_FOLDER, new TreeMap<>());

        FiMetaModel fiMetaModel = getCurrentUseFi();

        List<DescriptionModel> fiDescriptions = new ArrayList<>();
        fiMetaModel.getDescription().forEach(d -> {
            DescriptionModel model = new DescriptionModel(languageLocal.getLanguageCodeById(d.getLangId()), d.getDescription());
            fiDescriptions.add(model);
        });

        TreeMap<String, Object> properties = new TreeMap<>();
        properties.put("cm:description", RepositoryNodeMetaModelHelper.getJsonDescription(fiDescriptions));
        getOrCreateUserRootFolder(alfrescoClient, userFolderNode.getId(), fiMetaModel.getCode(), properties);

        return saveFile(repositoryFile);
    }

    @Override
    public RepositoryNodeMetaModel saveFile(RepositoryNodeMetaModel repositoryFile) throws IOException, FinATypeException {
        AlfrescoClient alfrescoClient = getAlfrescoClient("*");
        return saveFile(alfrescoClient, repositoryFile);
    }

    @Override
    public RepositoryNodeMetaModel saveFile(RepositoryNodeMetaModel repositoryFile, InputStream inputStream) throws Exception {
        AlfrescoClient alfrescoClient = getAlfrescoClient("*");
        return saveFile(alfrescoClient, repositoryFile, inputStream);
    }

    public RepositoryNodeMetaModel saveFile(AlfrescoClient alfrescoClient, RepositoryNodeMetaModel repositoryFile) throws IOException, FinATypeException {
        return saveFile(alfrescoClient, repositoryFile, new ByteArrayInputStream(repositoryFile.getContent()));
    }

    public RepositoryNodeMetaModel saveFile(AlfrescoClient alfrescoClient, RepositoryNodeMetaModel repositoryFile, InputStream inStream) throws IOException, FinATypeException {
        try (InputStream inputStream = inStream) {
            MultipartFormDataOutput mdo = new MultipartFormDataOutput();
            OutputPart objPart = mdo.addFormData(APIConstants.MULTIPART_FILE_DATA, inputStream, MediaType.APPLICATION_OCTET_STREAM_TYPE);
            objPart.getHeaders().putSingle("Content-Disposition", "form-data; name=" + APIConstants.MULTIPART_FILE_DATA + "; filename=" + repositoryFile.getName());
            mdo.addFormData("name", repositoryFile.getName(), MediaType.TEXT_PLAIN_TYPE);
            mdo.addFormData("nodeType", "fina:fiDocument", MediaType.TEXT_PLAIN_TYPE);
            mdo.addFormData("relativePath", repositoryFile.getPath(), MediaType.TEXT_PLAIN_TYPE);
            mdo.addFormData("cm:description", RepositoryNodeMetaModelHelper.getJsonDescription(repositoryFile.getDescriptions()), MediaType.TEXT_PLAIN_TYPE);
            repositoryFile.setDeleted(repositoryFile.isDeleted() != null && repositoryFile.isDeleted());
            mdo.addFormData(JcrCustomConstants.PROP_MARK_DELETED, repositoryFile.isDeleted(), MediaType.TEXT_PLAIN_TYPE);

            String relativePath = repositoryFile.getPath() == null ? "" : repositoryFile.getPath();

            NodeRepresentation uploadedFileNode;
            if (repositoryFile.getName() != null) {
                ResultNodeRepresentation searchedNode = searchContent(alfrescoClient, repositoryFile.getName(), relativePath.split("/"));
                if (searchedNode != null) {
                    uploadedFileNode = alfrescoClient.getNodesAPI().updateUploadNodeCall(searchedNode.getId(), outputStream -> outputStream.write(repositoryFile.getContent()), false, null, null);
                    markFileAsDeleted(uploadedFileNode.getId(), false);
                } else {
                    uploadedFileNode = alfrescoClient.getNodesAPI().createUploadNodeCall(APIConstants.FOLDER_ROOT, mdo, false, new IncludeParam(Arrays.asList("properties", "path")), null)
                            .readEntity(NodeRepresentation.class);
                }
            } else {
                uploadedFileNode = alfrescoClient.getNodesAPI().createUploadNodeCall(APIConstants.FOLDER_ROOT, mdo, false, new IncludeParam(Arrays.asList("properties", "path")), null)
                        .readEntity(NodeRepresentation.class);
            }
            repositoryFile.setId(uploadedFileNode.getId());
            repositoryFile.setVersionId((String) uploadedFileNode.getProperties().get("cm:versionLabel"));

            return repositoryFile;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public RepositoryNodeMetaModel getVersionFileWithContent(String nodeId, String version) throws IOException {
        RepositoryNodeMetaModel result = new RepositoryNodeMetaModel();

        AlfrescoClient client = getAlfrescoClient("*");

        Response response;
        String displayName;
        if (version == null) {
            response = client.getNodesAPI().getNodeContent(nodeId, false);
            NodeRepresentation nodeRepresentation = client.getNodesAPI().getNodeCall(nodeId);
            displayName = nodeRepresentation.getName();
        } else {
            response = client.getVersionAPI().getNodeVersionContent(nodeId, version, false);
            VersionRepresentation versionRepresentation = client.getVersionAPI().getVersionCall(nodeId, version);
            displayName = versionRepresentation.getName();
        }
        result.setName(displayName);

        InputStream inputStream = response.readEntity(InputStream.class);
        result.setContent(IOUtils.toByteArray(inputStream));

        return result;
    }

    @Override
    public InputStream getVersionFileStreamWithContent(String nodeId, String version) throws IOException, FinATypeException {
        RepositoryNodeMetaModel result = new RepositoryNodeMetaModel();

        AlfrescoClient client = getAlfrescoClient("*");

        Response response;
        String displayName;
        if (version == null) {
            response = client.getNodesAPI().getNodeContent(nodeId, false);
            NodeRepresentation nodeRepresentation = client.getNodesAPI().getNodeCall(nodeId);
            displayName = nodeRepresentation.getName();
        } else {
            response = client.getVersionAPI().getNodeVersionContent(nodeId, version, false);
            VersionRepresentation versionRepresentation = client.getVersionAPI().getVersionCall(nodeId, version);
            displayName = versionRepresentation.getName();
        }
        result.setName(displayName);

        return response.readEntity(InputStream.class);
    }


    @Override
    public void markFileAsDeleted(String id, boolean deleted) {
        AlfrescoClient client = getAlfrescoClient("*");

        TreeMap<String, Object> properities = new TreeMap<>();
        properities.put(JcrCustomConstants.PROP_MARK_DELETED, deleted);
        NodeBodyUpdate nbu = new NodeBodyUpdate();
        nbu.setProperties(properities);

        client.getNodesAPI().updateNodeCall(id, nbu);
    }

    @Override
    public List<RepositoryNodeMetaModel> getNodeVersions(String nodeId, String orderBy) {
        AlfrescoClient client = getAlfrescoClient("*");

        ResultPaging<VersionRepresentation> versionHistory = client.getVersionAPI().listVersionHistoryCall(nodeId, null, null,
                new IncludeParam(Arrays.asList("properties", "path")), null);
        List<RepositoryNodeMetaModel> result = RepositoryNodeMetaModelHelper.versionsToList(versionHistory.getObjects());
        RepositoryNodeMetaModelHelper.sort(result, orderBy);
        return result;
    }

    @Override
    public RepositoryNodeMetaModel updateFileDescription(RepositoryNodeMetaModel model) throws FinATypeException {
        AlfrescoClient client = getAlfrescoClient("*");

        NodeRepresentation node = client.getNodesAPI().getNodeCall(model.getId(), new IncludeParam(Collections.singletonList("path")).toString(), null, null);
        Object descriptionsObject = node.getProperties().get("cm:description");
        List<DescriptionModel> existingDescriptions = RepositoryNodeMetaModelHelper.parseDescriptionFromJson(descriptionsObject != null ? descriptionsObject.toString() : null);

        for (DescriptionModel existingDescriptionModel : existingDescriptions) {
            String langCode = existingDescriptionModel.getLangCode();
            boolean found = false;
            for (DescriptionModel dm : model.getDescriptions()) {
                if (dm.getLangCode().equals(langCode)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                model.getDescriptions().add(existingDescriptionModel);
            }
        }

        NodeBodyUpdate nbu = new NodeBodyUpdate();
        TreeMap<String, Object> properties = new TreeMap<>();
        properties.put("cm:description", RepositoryNodeMetaModelHelper.getJsonDescription(model.getDescriptions()));
        nbu.setProperties(properties);
        Response response = client.getNodesAPI().updateNodeCall(model.getId(), nbu, new IncludeParam(Arrays.asList("properties", "path")), null);
        NodeRepresentation nodeRepresentation = extractNodeRepresentation(response);
        return RepositoryNodeMetaModelHelper.nodeRepresentationToModel(nodeRepresentation);
    }

    @Override
    public List<RepositoryNodeMetaModel> loadFilesByRelativePath(String path) {
        try {

            AlfrescoClient alfrescoClient = getAlfrescoClient("*");
            OrderByParam orderByParam = new OrderByParam(Collections.singletonList(getOrderByParam(null)));

            ResultPaging<NodeRepresentation> nodes = alfrescoClient.getNodesAPI().listNodeChildrenCall(APIConstants.FOLDER_ROOT, 0, 100, orderByParam, null, null, path, null, null);

            return RepositoryNodeMetaModelHelper.toList(nodes.getObjects());
        } catch (NotFoundException e) {
            log.error(e.getMessage(), e);
        }

        return new ArrayList<>();
    }

    @Override
    public void deleteFolder(String path) {
        try {
            AlfrescoClient alfrescoClient = getAlfrescoClient("*");
            NodeRepresentation node = alfrescoClient.getNodesAPI().getNodeCall(APIConstants.FOLDER_ROOT, null, path, null);
            alfrescoClient.getNodesAPI().deleteNodeCall(node.getId());
        } catch (NotFoundException e) {
            log.error(e.getMessage(), e);
        }

    }

    @Override
    public void deleteFile(String nodeId) {
        AlfrescoClient alfrescoClient = getAlfrescoClient("*");
        NodeRepresentation node = alfrescoClient.getNodesAPI().getNodeCall(nodeId);
        if (node.isFile()) {
            alfrescoClient.getNodesAPI().deleteNodeCall(node.getId());
        }
    }


    private NodeRepresentation getOrCreateUserRootFolder(AlfrescoClient client, String parentId, String folderName, TreeMap<String, Object> properties) throws FinATypeException {
        NodeRepresentation parentNode = client.getNodesAPI().getNodeCall(parentId);
        String queryString = "select * from cmis:folder where IN_FOLDER('" + parentNode.getId() + "') and cmis:name='" + folderName + "'";
        RequestQuery query = new RequestQuery().query(queryString).language(RequestQuery.LanguageEnum.CMIS);

        QueryBody body = new QueryBody().query(query).include(Collections.singletonList("properties")).paging(new RequestPagination().skipCount(0).maxItems(Integer.MAX_VALUE));

        ResultSetRepresentation<ResultNodeRepresentation> resultSet = client.getSearchAPI().search(body);

        if (resultSet.getCount() == 0) {
            NodeBodyCreate nodeBodyCreate = new NodeBodyCreate();
            nodeBodyCreate.setName(folderName);
            nodeBodyCreate.setNodeType("cm:folder");
            nodeBodyCreate.setProperties(properties);
            NodeRepresentation res = extractNodeRepresentation(client.getNodesAPI().createNodeCall(parentNode.getId(), nodeBodyCreate));

            PermissionElementRepresentation per = new PermissionElementRepresentation();
            per.setAccessStatus(PermissionElementRepresentation.AccessStatusEnum.ALLOWED);
            per.setAuthorityId("GROUP_EVERYONE");
            per.setName("Collaborator");

            PermissionsBodyUpdate pbu = new PermissionsBodyUpdate();
            pbu.setLocallySet(Collections.singletonList(per));
            pbu.setInheritanceEnabled(false);

            NodeBodyUpdate nbu = new NodeBodyUpdate();
            nbu.setPermissions(pbu);

            return extractNodeRepresentation(client.getNodesAPI().updateNodeCall(res.getId(), nbu));
        } else {
            return resultSet.getObjects().get(0);
        }
    }

    private NodeRepresentation extractNodeRepresentation(Response response) throws FinATypeException {
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            UnexpectedErrorRepresentation unexpectedErrorRepresentation = response.readEntity(UnexpectedErrorRepresentation.class);
            log.error(unexpectedErrorRepresentation);
            throw new FinATypeException(unexpectedErrorRepresentation.getBriefSummary());
        }

        return response.readEntity(NodeRepresentation.class);
    }

    private String getCurrentUserDocumentsPath() {
        Map<String, Description> fis = fiLocal.loadFiCodeAndNames(userLocal.getCurrentUserLogin());
        Map.Entry<String, Description> fiOptional = fis.entrySet().stream().findFirst().get();
        return "/" + FileRepositoryUtil.getFiDocumentsRootFolderPath() + "/" + fiOptional.getKey();
    }

    private FiMetaModel getCurrentUseFi() {
        FiMetaModel fiModel = new FiMetaModel();
        Map<String, Description> fis = fiLocal.loadFiCodeAndNames(userLocal.getCurrentUserLogin());
        Map.Entry<String, Description> fiOptional = fis.entrySet().stream().findFirst().get();
        fiModel.setCode(fiOptional.getKey());
        fiModel.setDescription(DescriptionModelHelper.toModel(fiOptional.getValue()));

        return fiModel;
    }

    private String getOrderByParam(String orderBy) {
        if (orderBy == null || orderBy.trim().isEmpty()) {
            return "modifiedAt desc";
        }

        String[] params = orderBy.split(" ");
        String orderByParamName = params[0];

        switch (orderByParamName) {
            case "descriptions":
                orderByParamName = "cm:description";
                break;
            case "lastModified":
                orderByParamName = "modifiedAt";
                break;
        }

        return orderByParamName + " " + (params.length == 2 ? params[1] : "ASC");
    }


    private String getOrderByParamCMIS(String orderBy) {
        if (orderBy == null || orderBy.trim().isEmpty()) {
            return "cmis:lastModificationDate desc";
        }

        String[] params = orderBy.split(" ");
        String orderByParamName = params[0];

        switch (orderByParamName) {
            case "descriptions":
                orderByParamName = "cm:description";
                break;
            case "lastModified":
                orderByParamName = "cmis:lastModificationDate";
                break;
        }

        return orderByParamName + " " + (params.length == 2 ? params[1] : "ASC");
    }

    private ResultPaging<? extends NodeRepresentation> filterNodesByFis(String nodeId, List<String> filterByFis, String orderBy, int offset, int limit) {
        AlfrescoClient alfrescoClient = getAlfrescoClient("*");
        if (!nodeId.equals(APIConstants.FOLDER_ROOT)) {

            return alfrescoClient.getNodesAPI().listNodeChildrenCall(nodeId, offset, limit,
                    new OrderByParam(Collections.singletonList(getOrderByParam(orderBy))),
                    null,
                    new IncludeParam(Arrays.asList("properties", "path")), null, null,
                    null);
        }

        StringBuilder searchStr = new StringBuilder();
        for (int i = 0; i < filterByFis.size(); i++) {
            String fiCode = filterByFis.get(i);
            searchStr.append("cm:name:\"").append(fiCode).append("\"");
            if (i != filterByFis.size() - 1) {
                searchStr.append(" OR ");
            }
        }
        RequestQuery query = new RequestQuery().query(searchStr.toString()).language(RequestQuery.LanguageEnum.AFTS);
        String path = buildSearchPath(JcrCustomConstants.USER_ROOT_FOLDER, JcrCustomConstants.USER_FILES_FOLDER);

        QueryBody queryBody = new QueryBody().query(query).include(Arrays.asList("properties", "path", "association"))
                .filterQueries(Arrays.asList(
                        new RequestFilterQuery().query(path + "//*'"),
                        new RequestFilterQuery().query("+TYPE:'cm:folder'")
                ))
                .paging(new RequestPagination().maxItems(limit).skipCount(offset))
                .sort(Collections.singletonList(getFTSSort(orderBy)));

        ResultSetRepresentation<ResultNodeRepresentation> resultSearch = alfrescoClient.getSearchAPI().search(queryBody);

        return resultSearch;
    }

    private ResultPaging<? extends NodeRepresentation> filterNodesByName(String filterByName, String fiFolderName, boolean excludeDeleted, String orderBy, int offset, int limit, String nodeId) {
        String searchStr = "cm:name:\"*" + filterByName + "*\" OR cm:description:\"*" + filterByName + "*\"";
        if (excludeDeleted) {
            searchStr = "(" + searchStr + ")" + " AND " + JcrCustomConstants.PROP_MARK_DELETED + ":false";
        }
        RequestQuery query = new RequestQuery().query(searchStr).language(RequestQuery.LanguageEnum.AFTS);
        String path = buildSearchPath(JcrCustomConstants.USER_ROOT_FOLDER, JcrCustomConstants.USER_FILES_FOLDER);
        if (fiFolderName != null && !fiFolderName.isEmpty()) {
            path += "//cm:" + AlfrescoUtil.getISO9075String(fiFolderName);
        }

        if (nodeId != null && !nodeId.isEmpty() && !nodeId.equals(APIConstants.FOLDER_ROOT) && (filterByName != null && !filterByName.isBlank())) {
            AlfrescoClient client = getAlfrescoClient("*");
            NodeRepresentation node = client.getNodesAPI().getNodeCall(nodeId);
            if (node != null && (node.getName() != null && !node.getName().isBlank())) {
                path += "//cm:" + AlfrescoUtil.getISO9075String(node.getName());
            }
        }

        List<RequestFilterQuery> filterQueries = new ArrayList<>();
        filterQueries.add(new RequestFilterQuery().query(path + "//*'"));

        if (nodeId != null && !nodeId.equals(APIConstants.FOLDER_ROOT)) {
            filterQueries.add(new RequestFilterQuery().query("+TYPE:'cm:content'"));
        }

        QueryBody queryBody = new QueryBody().query(query)
                .include(Arrays.asList("properties", "path", "association"))
                .filterQueries(filterQueries)
                .paging(new RequestPagination().maxItems(limit).skipCount(offset))
                .sort(Collections.singletonList(getFTSSort(orderBy)));

        ResultSetRepresentation<ResultNodeRepresentation> resultSearch = getAlfrescoClient("*").getSearchAPI().search(queryBody);

        return resultSearch;
    }

    private ResultPaging<? extends NodeRepresentation> filterNodesByFiAndName(List<String> filterByFis, String filterByName, String orderBy, int offset, int limit) {
        String searchStr = "cm:name:\"*" + filterByName + "*\" OR cm:description:\"*" + filterByName + "*\"";
        RequestQuery query = new RequestQuery().query(searchStr).language(RequestQuery.LanguageEnum.AFTS);

        StringBuilder filter = new StringBuilder();

        for (int i = 0; i < filterByFis.size(); i++) {
            String fiCode = filterByFis.get(i);
            String path = buildSearchPath(JcrCustomConstants.USER_ROOT_FOLDER, JcrCustomConstants.USER_FILES_FOLDER, fiCode);
            filter.append(path).append("/*'");
            if (i != filterByFis.size() - 1) {
                filter.append(" OR ");
            }
        }


        QueryBody queryBody = new QueryBody().query(query).include(Arrays.asList("properties", "path", "association"))
                .filterQueries(Arrays.asList(
                        new RequestFilterQuery().query(filter.toString()),
                        new RequestFilterQuery().query("+TYPE:'cm:folder' OR +TYPE:'cm:content'")
                ))
                .paging(new RequestPagination().maxItems(limit).skipCount(offset))
                .sort(Collections.singletonList(getFTSSort(orderBy)));

        ResultSetRepresentation<ResultNodeRepresentation> resultSearch = getAlfrescoClient("*").getSearchAPI().search(queryBody);
        return resultSearch;
    }

    private String buildSearchPath(String... paths) {
        StringBuilder sb = new StringBuilder("+PATH:'/app:company_home/");
        for (int i = 0; i < paths.length; i++) {
            sb.append("cm:").append(AlfrescoUtil.getISO9075String(paths[i]));

            if (i != paths.length - 1) {
                sb.append("/");
            }
        }
        return sb.toString();
    }

    private RequestSortDefinition getFTSSort(String orderBy) {
        if (orderBy == null || orderBy.isBlank()) {
            return new RequestSortDefinition().field("modifiedAt")
                    .ascending(false);
        }
        String[] params = orderBy.split(" ");
        String orderByParamName = params[0];

        orderByParamName = switch (orderByParamName) {
            case "descriptions" -> "cm:description";
            case "lastModified" -> "modifiedAt";
            case "name" -> "cm:name";
            default -> orderByParamName;
        };

        return new RequestSortDefinition()
                .field(orderByParamName).ascending(params[1].equalsIgnoreCase("ASC"));
    }

    private ResultNodeRepresentation searchContent(AlfrescoClient alfrescoClient, String fileName, String... paths) {
        String searchStr = "select * from cmis:document where CONTAINS({0}) and cmis:name={1}";

        StringBuilder path = new StringBuilder("'PATH:\"/app:company_home");
        for (String p : paths) {
            if (p.isBlank()) {
                continue;
            }
            path.append("/cm:").append(AlfrescoUtil.getISO9075String(p));
        }
        path.append("//*\"'");

        RequestQuery query = new RequestQuery().query(MessageFormat.format(searchStr, path.toString(), "'" + fileName + "'")).language(RequestQuery.LanguageEnum.CMIS);
        QueryBody queryBody = new QueryBody().query(query).include(Arrays.asList("properties", "path"));

        ResultSetRepresentation<ResultNodeRepresentation> resultSearch = alfrescoClient.getSearchAPI().search(queryBody);

        return resultSearch.getObjects().isEmpty() ? null : resultSearch.getObjects().get(0);
    }
}
