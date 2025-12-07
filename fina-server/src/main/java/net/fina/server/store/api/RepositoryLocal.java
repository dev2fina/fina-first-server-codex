package net.fina.server.store.api;

import net.fina.server.store.model.RepositoryFile;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.IOException;
import java.io.InputStream;

public interface RepositoryLocal {

    void saveFile(Session repositorySession, RepositoryFile repositoryFiles) throws IOException, RepositoryException;

    InputStream getLatestVersionFileStream(Session repositorySession, String parentPath, String fileName);

    InputStream getFileStreamByVersion(Session repositorySession, String parentPath, String fileName, String versionId);

    String getLatestVersionId(Session repositorySession, String path);
}
