package net.fina.server.jcr.proxy;

import jakarta.ejb.Stateless;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.inject.Inject;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.property.PropertyKeys;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.common.shared.WrongFileTypeException;
import net.fina.common.shared.jcr.RepositoryNodeMetaModel;
import net.fina.server.jcr.api.FileRepositoryLocal;
import net.fina.server.jcr.qualifier.AlfrescoRepo;
import net.fina.server.jcr.qualifier.JackRabbitRepo;
import net.fina.server.security.api.PropertyLocal;
import org.jboss.ejb3.annotation.SecurityDomain;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Stateless
@SecurityDomain("FinASecurityDomain")
public class FileRepositoryProxySession {

    @Inject
    @Any
    private Instance<FileRepositoryLocal> fileRepositoryLocals;

    @Inject
    private PropertyLocal propertyLocal;

    private FileRepositoryLocal getInstance() {
        String repositoryProvider = propertyLocal.getSystemProperty(PropertyKeys.CONTENT_STORAGE_REPOSITORY_PROVIDER);

        switch (repositoryProvider) {
            case "ALFRESCO":
                return fileRepositoryLocals.select(new AnnotationLiteral<AlfrescoRepo>() {
                }).get();
            case "JCR":
            default:
                return fileRepositoryLocals.select(new AnnotationLiteral<JackRabbitRepo>() {
                }).get();
        }
    }

    public PaginatedListWrapper<RepositoryNodeMetaModel> loadFiles(String nodeId, int offset, int limit, String orderBy, List<String> fiCodes, String filterByName) throws FinATypeException {
        nodeId = nodeId.equals("root") ? null : nodeId;
        return getInstance().loadAll(nodeId, offset, limit, orderBy, fiCodes, filterByName);
    }


    public RepositoryNodeMetaModel getFileContent(String nodeId, String version) throws IOException, FinATypeException {
        return getInstance().getVersionFileWithContent(nodeId, version);
    }

    public InputStream getFileContentStream(String nodeId, String version) throws IOException, FinATypeException {
        return getInstance().getVersionFileStreamWithContent(nodeId, version);
    }

    public List<RepositoryNodeMetaModel> getFilesContentsLastVersion(List<String> nodeIds) throws IOException, FinATypeException {
        List<RepositoryNodeMetaModel> result = new ArrayList<>();
        if (nodeIds != null && !nodeIds.isEmpty()) {
            for (String nodeId : nodeIds) {
                RepositoryNodeMetaModel nodeMetaModel = getFileContent(nodeId, null);
                result.add(nodeMetaModel);
            }
        }
        return result;
    }


    public List<RepositoryNodeMetaModel> loadNodeVersions(String nodeId, String orderBy) {
        return getInstance().getNodeVersions(nodeId, orderBy);
    }

    public PaginatedListWrapper<RepositoryNodeMetaModel> loadExternalUserFiles(int offset, int limit, String orderBy, String filter) throws FinATypeException {
        return getInstance().loadExternalUserFiles(offset, limit, orderBy, filter);
    }

    public RepositoryNodeMetaModel uploadFiDocument(RepositoryNodeMetaModel nodeMetaModel) throws IOException, FinATypeException, WrongFileTypeException {
        return getInstance().saveFiDocument(nodeMetaModel);
    }

    public void markFileAsDeleted(String nodeId, boolean deleted) {
        getInstance().markFileAsDeleted(nodeId, deleted);
    }

    public RepositoryNodeMetaModel updateFileDescription(RepositoryNodeMetaModel model) throws FinATypeException {
        return getInstance().updateFileDescription(model);
    }

    public RepositoryNodeMetaModel saveFile(RepositoryNodeMetaModel nodeMetaModel, InputStream inputStream) throws Exception {
        return getInstance().saveFile(nodeMetaModel, inputStream);
    }

    public RepositoryNodeMetaModel saveFile(RepositoryNodeMetaModel nodeMetaModel) throws Exception {
        return getInstance().saveFile(nodeMetaModel);
    }

    public void markFilesAsDeleted(List<String> ids) {
        if (ids != null && !ids.isEmpty()) {
            ids.forEach(id -> markFileAsDeleted(id, true));
        }
    }

    public List<RepositoryNodeMetaModel> loadFiles(String path) {
        return getInstance().loadFilesByRelativePath(path);
    }

    public void deleteFolder(String path) {
        getInstance().deleteFolder(path);
    }

    public void delete(String nodeId) {
        getInstance().deleteFile(nodeId);
    }
}
