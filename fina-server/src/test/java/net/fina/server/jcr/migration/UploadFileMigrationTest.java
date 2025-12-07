package net.fina.server.jcr.migration;

import net.fina.common.shared.jcr.RepositoryNodeMetaModel;
import net.fina.server.jcr.util.FileRepositoryUtil;
import org.junit.Ignore;
import org.junit.Test;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Ignore
public class UploadFileMigrationTest extends MigrationBaseTest {

    @Test
    public void migrateUploadFiles() throws SQLException {

        List<Long> failedUploadFilesIds = new ArrayList<>();
        try (CachedRowSet crs = executeQuery("SELECT * FROM SYS_UPLOADEDFILE")) {
            while (crs.next()) {
                byte[] uploadedFile = crs.getBytes("uploadedFile");
                Long id = crs.getLong("id");
                if (uploadedFile != null && uploadedFile.length > 0 && id > 0) {
                    RepositoryNodeMetaModel fileNode = new RepositoryNodeMetaModel(FileRepositoryUtil.getUploadedFilesRootFolderPath(), UUID.randomUUID().toString());
                    fileNode.setContent(uploadedFile);

                    try {
                        RepositoryNodeMetaModel repositoryNodeMetaModel = saveUploadFileToAlfresco(fileNode);
                        updateRepositoryFileId(id, repositoryNodeMetaModel.getId());

                        System.out.println("SUCCESS - ID: " + id + ", REPOSITORY FILE ID: " + repositoryNodeMetaModel.getId());
                    } catch (Throwable t) {
                        failedUploadFilesIds.add(id);
                        System.out.println("FAILURE - ID: " + id);
                        System.err.println(t.getMessage());
                    }
                }
            }
        } finally {
            if (!failedUploadFilesIds.isEmpty()) {
                System.out.println("FAILED IDS:");
                System.out.println(failedUploadFilesIds);
            }
        }
    }

    private void updateRepositoryFileId(Long uploadFileId, String repositoryFileId) throws SQLException {
        String sql = "UPDATE SYS_UPLOADEDFILE set REPOSITORY_FILE_ID = '" + repositoryFileId + "' where id = " + uploadFileId;
        executeUpdateQuery(sql);
    }

}
