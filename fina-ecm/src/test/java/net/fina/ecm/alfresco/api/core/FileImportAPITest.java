package net.fina.ecm.alfresco.api.core;

import net.fina.ecm.alfresco.AlfrescoClient;
import net.fina.ecm.alfresco.api.AlfrescoAPITestCase;
import net.fina.ecm.alfresco.api.core.model.body.BulkFileImportBodyInitiate;
import net.fina.ecm.alfresco.api.core.model.body.BulkFileImportExistingFileMode;
import net.fina.ecm.alfresco.api.core.model.body.NodeBodyCreate;
import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import net.fina.ecm.alfresco.api.core.model.representation.xml.fileimport.bulk.BulkFilesystemImportStatus;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import jakarta.ws.rs.core.Response;

public class FileImportAPITest extends AlfrescoAPITestCase {

    private final String TEST_ROOT_FOLDER_NAME = "TEST-IMPORTED-FILES";
    private NodeRepresentation rootFolder;

    @Before
    public void init() {
        client = getClient();

        rootFolder = client.getNodesAPI().createNodeCall("-root-", new NodeBodyCreate(TEST_ROOT_FOLDER_NAME, "cm:folder")).readEntity(NodeRepresentation.class);
        Assert.assertNotNull(rootFolder);
    }

    @After
    public void cleanUp() {
        if (rootFolder != null) {
            AlfrescoClient newClient = getClient();
            newClient.getNodesAPI().deleteNodeCall(rootFolder.getId());
            newClient.getRestClient().client.close();
        }

        client.getRestClient().client.close();
    }

    @Test
    public void getBulkImportStatusTest() {
        BulkFilesystemImportStatus status = getBulkFilesystemImportStatus();

        Assert.assertNotNull(status);
        Assert.assertEquals("Idle", status.getCurrentStatus());
    }

    @Test
    public void initiateBulkImportTest() {
        BulkFileImportBodyInitiate bulkFileImportBodyInitiate = new BulkFileImportBodyInitiate();
        bulkFileImportBodyInitiate.setSourceDirectory("/usr/local/tomcat/conf");
        bulkFileImportBodyInitiate.setTargetPath("/Company Home/" + TEST_ROOT_FOLDER_NAME);
        bulkFileImportBodyInitiate.setExistingFileMode(BulkFileImportExistingFileMode.SKIP);
        bulkFileImportBodyInitiate.setBatchSize(10);
        bulkFileImportBodyInitiate.setNumThreads(20);

        Response rs = client.getFileImportAPI().initiateBulkImport(bulkFileImportBodyInitiate);
        int status = rs.getStatus();
        Assert.assertEquals(HttpStatus.SC_MOVED_TEMPORARILY, status);

        BulkFilesystemImportStatus finalStatus = getBulkFileInitiateFinishStatus();
        Assert.assertNotNull(finalStatus);
        Assert.assertEquals("Idle", finalStatus.getCurrentStatus());
    }

    private BulkFilesystemImportStatus getBulkFileInitiateFinishStatus() {
        BulkFilesystemImportStatus status = getBulkFilesystemImportStatus();
        if (status.getCurrentStatus().equalsIgnoreCase("In progress")) {
            return getBulkFileInitiateFinishStatus();
        }
        return status;
    }

    private BulkFilesystemImportStatus getBulkFilesystemImportStatus() {
        return getClient().getFileImportAPI().getBulkImportStatus("xml");
    }

}
