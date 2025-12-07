package net.fina.server.jcr.api;

import net.fina.common.client.exception.FinATypeException;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.common.shared.WrongFileTypeException;
import net.fina.common.shared.jcr.RepositoryNodeMetaModel;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface FileRepositoryLocal {

    PaginatedListWrapper<RepositoryNodeMetaModel> loadAll(String nodeId, int offset, int limit, String orderBy, List<String> filterByFis, String filterByName) throws FinATypeException;

    PaginatedListWrapper<RepositoryNodeMetaModel> loadExternalUserFiles(int offset, int limit, String orderBy, String filter) throws FinATypeException;

    RepositoryNodeMetaModel saveFiDocument(RepositoryNodeMetaModel repositoryFile) throws IOException, FinATypeException, WrongFileTypeException;

    RepositoryNodeMetaModel saveFile(RepositoryNodeMetaModel repositoryFile) throws Exception;

    RepositoryNodeMetaModel saveFile(RepositoryNodeMetaModel repositoryFile, InputStream inputStream) throws Exception;

    RepositoryNodeMetaModel getVersionFileWithContent(String nodeId, String version) throws IOException, FinATypeException;

    InputStream getVersionFileStreamWithContent(String nodeId, String version) throws IOException, FinATypeException;

    void markFileAsDeleted(String id, boolean deleted);

    List<RepositoryNodeMetaModel> getNodeVersions(String nodeId, String orderBy);

    RepositoryNodeMetaModel updateFileDescription(RepositoryNodeMetaModel model) throws FinATypeException;

    List<RepositoryNodeMetaModel> loadFilesByRelativePath(String path);

    void deleteFolder(String path);

    void deleteFile(String nodeId);
}
