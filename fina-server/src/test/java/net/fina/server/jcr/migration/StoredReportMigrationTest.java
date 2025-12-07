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
public class StoredReportMigrationTest extends MigrationBaseTest {

    @Test
    public void migrateStoredReports() throws SQLException {

        List<Integer> storedReportsIds = new ArrayList<>();
        try (CachedRowSet crs = executeQuery("SELECT * FROM OUT_STORED_REPORTS")) {
            while (crs.next()) {
                byte[] storedReport = crs.getBytes("reportResult");
                int reportId = crs.getInt("reportId");
                int langId = crs.getInt("langId");
                int hashCode = crs.getInt("hashCode");
                if (storedReport != null && storedReport.length > 0) {
                    RepositoryNodeMetaModel fileNode = new RepositoryNodeMetaModel(FileRepositoryUtil.getStoredReportRootFolderPath(), UUID.randomUUID().toString());
                    fileNode.setContent(storedReport);

                    try {
                        RepositoryNodeMetaModel repositoryNodeMetaModel = saveUploadFileToAlfresco(fileNode);
                        updateRepositoryFileId(reportId, repositoryNodeMetaModel.getId(), langId, hashCode);

                        System.out.println("SUCCESS - ID: " + reportId + ", REPOSITORY FILE ID: " + repositoryNodeMetaModel.getId());
                    } catch (Throwable t) {
                        storedReportsIds.add(reportId);
                        System.out.println("FAILURE - ID: " + reportId);
                        System.err.println(t.getMessage());
                    }
                }
            }
        } finally {
            if (!storedReportsIds.isEmpty()) {
                System.out.println("FAILED IDS:");
                System.out.println(storedReportsIds);
            }
        }
    }

    private void updateRepositoryFileId(int reportId, String repositoryFileId, int langId, int hashCode) throws SQLException {
        String sql = "UPDATE OUT_STORED_REPORTS set REPOSITORY_FILE_ID = '" + repositoryFileId + "' where reportId = " + reportId + " and langId=" + langId + " and hashCode=" + hashCode;
        executeUpdateQuery(sql);
    }
}
