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
public class ImportedReturnMigrationTest extends MigrationBaseTest {

    @Test
    public void migrateUploadFiles() throws SQLException {

        List<Long> xmlIds = new ArrayList<>();
        try (CachedRowSet crs = executeQuery("SELECT * FROM IN_IMPORTED_RETURNS")) {
            while (crs.next()) {
                byte[] content = crs.getBytes("content");
                Long id = crs.getLong("id");
                if (content != null && content.length > 0 && id > 0) {
                    RepositoryNodeMetaModel fileNode = new RepositoryNodeMetaModel(FileRepositoryUtil.getImportedReturnRootFolderPath(), UUID.randomUUID().toString());
                    fileNode.setContent(content);

                    try {
                        RepositoryNodeMetaModel repositoryNodeMetaModel = saveUploadFileToJackRabbit(fileNode);
                        updateRepositoryFileId(id, repositoryNodeMetaModel.getId());

                        System.out.println("SUCCESS - ID: " + id + ", REPOSITORY FILE ID: " + repositoryNodeMetaModel.getId());
                    } catch (Throwable t) {
                        xmlIds.add(id);
                        System.out.println("FAILURE - ID: " + id);
                        System.err.println(t.getMessage());
                    }
                }
            }
        } finally {
            if (!xmlIds.isEmpty()) {
                System.out.println("FAILED IDS:");
                System.out.println(xmlIds);
            }
        }
    }

    private void updateRepositoryFileId(Long uploadFileId, String repositoryFileId) throws SQLException {
        String sql = "UPDATE IN_IMPORTED_RETURNS set REPOSITORY_FILE_ID = '" + repositoryFileId + "' where id = " + uploadFileId;
        executeUpdateQuery(sql);
    }

}
