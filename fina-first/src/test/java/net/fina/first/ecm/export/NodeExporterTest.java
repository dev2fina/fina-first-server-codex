package net.fina.first.ecm.export;

import net.fina.AlfrescoAPITestCase;
import net.fina.ecm.alfresco.api.common.constant.APIConstants;
import net.fina.first.ecm.node.api.NodeExporter;
import net.fina.first.ecm.node.impl.ExcelNodeExporter;
import net.fina.first.ecm.node.model.ExportTemplate;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.util.UUID;

@Ignore
public class NodeExporterTest extends AlfrescoAPITestCase {

    @Before
    public void init() {
        client = getClient();
    }

    @After
    public void cleanUp() {
        client.getRestClient().client.close();
    }

    @Test
    public void export2ExcelTest() throws Throwable {
        NodeExporter nodeExporter = new ExcelNodeExporter(client);
        byte[] result = nodeExporter.getExportNodeHierarchyContent(ExportTemplate.FI_REGISTRY, APIConstants.FOLDER_ROOT, null, null);

        String filePath = "./target/ExcelNodeExporterTest_out/" + UUID.randomUUID().toString() + ".xlsx";
        FileUtils.writeByteArrayToFile(new File(filePath), result);
    }

}
