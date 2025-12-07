package net.fina.server.store.impl;

import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.store.api.RepositoryLocal;
import net.fina.server.store.model.RepositoryFile;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.commons.JcrUtils;
import org.jboss.logging.Logger;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;
import javax.jcr.version.VersionManager;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Stateless
@Local(RepositoryLocal.class)
@Interceptors(RecordingAuditor.class)
public class JackrabbitRepositorySession implements RepositoryLocal {

    @Inject
    private Logger log;

    @Override
    public void saveFile(Session repositorySession, RepositoryFile repositoryFile) throws IOException, RepositoryException {

        //Retrieve repo default workspace version manager
        VersionManager vm = repositorySession.getWorkspace().getVersionManager();

        // Create folder node
        JcrUtils.getOrCreateByPath(repositoryFile.getPath(), JcrConstants.NT_FOLDER, repositorySession);
        // File node
        Node fileNode = JcrUtils.getOrCreateByPath(repositoryFile.getPath() + "/" + repositoryFile.getFileName(), JcrConstants.NT_FILE, repositorySession);

        if (fileNode.isNew()) {
            //Add versional property
            fileNode.addMixin(JcrConstants.MIX_VERSIONABLE);
        }

        if (!fileNode.isCheckedOut()) {
            vm.checkout(fileNode.getPath());
        }

        try (InputStream stream = new ByteArrayInputStream(repositoryFile.getContent())) {
            Node contentNode = JcrUtils.getOrAddNode(fileNode, JcrConstants.JCR_CONTENT, JcrConstants.NT_RESOURCE);
            Binary binary = repositorySession.getValueFactory().createBinary(stream);
            contentNode.setProperty(JcrConstants.JCR_DATA, binary);
        }

        //Save current session changes
        repositorySession.save();

        Version version = vm.checkin(fileNode.getPath());

        repositoryFile.setFileVersionId(version.getIdentifier());
    }

    @Override
    public InputStream getLatestVersionFileStream(Session repositorySession, String parentPath, String fileName) {

        try {
            String filePath = parentPath + "/" + fileName;
            String contentPath = filePath + "/"+JcrConstants.JCR_CONTENT;

            if (!repositorySession.nodeExists(filePath)) {
                log.warn("File node does not exist: " + filePath);
                return null;
            }

            if (!repositorySession.nodeExists(contentPath)) {
                log.warn("jcr:content does not exist under file node: " + contentPath);
                return null;
            }

            Node content = repositorySession.getNode(contentPath);
            Binary binary = content.getProperty(JcrConstants.JCR_DATA).getBinary();
            return binary.getStream();
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
        return null;
    }

    @Override
    public InputStream getFileStreamByVersion(Session repositorySession, String parentPath, String fileName, String versionId) {
        try {

            VersionManager vm = repositorySession.getWorkspace().getVersionManager();
            VersionHistory vh = vm.getVersionHistory(parentPath + "/" + fileName);
            VersionIterator iterator = vh.getAllVersions();
            while (iterator.hasNext()) {
                Version version = iterator.nextVersion();
                if (version.getIdentifier().equals(versionId)) {
                    Node vNode = version.getFrozenNode();
                    Node cNode = vNode.getNode(JcrConstants.JCR_CONTENT);
                    Binary binary = cNode.getProperty(JcrConstants.JCR_DATA).getBinary();
                    return binary.getStream();
                }
            }

        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
        return null;
    }

    @Override
    public String getLatestVersionId(Session repositorySession, String path) {
        try {
            if (!repositorySession.nodeExists(path)) {
                log.warn("File node does not exist: " + path);
                return null;
            }
            VersionManager vm = repositorySession.getWorkspace().getVersionManager();
            Version version = vm.getBaseVersion(path);
            return version.getIdentifier();
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
        return null;
    }
}
