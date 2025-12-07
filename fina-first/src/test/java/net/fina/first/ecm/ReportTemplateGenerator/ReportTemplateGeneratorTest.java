package net.fina.first.ecm.ReportTemplateGenerator;

import net.fina.AlfrescoAPITestCase;
import net.fina.ecm.alfresco.AlfrescoClient;
import net.fina.first.ecm.report.generator.FirstReportTemplateGenerator;
import net.fina.first.ecm.report.model.FirstReportConfig;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Ignore
public class ReportTemplateGeneratorTest extends AlfrescoAPITestCase {

    private AlfrescoClient client;

    @Before
    public void init() {
        client = getClient();
    }

    @After
    public void cleanUp() {
        client.getRestClient().client.close();
    }

    @Test
    public void testReportData() throws Exception {
        String reportName = "ShareHolders";
        List<Map<String, Object>> result = client.getWebScriptApi().getReportData(reportName, null, null);

        for (Map<String, Object> map : result) {

            for (Map.Entry<String, Object> e : map.entrySet()) {
                if (e.getKey().endsWith("Date")) {
                    if (e.getValue() != null && (!e.getValue().toString().isEmpty())) {
                        Date d = new Date((Long) ((LinkedHashMap) e.getValue()).get("milliseconds"));
                        map.put(e.getKey(), d);
                    }
                }
            }
        }

        System.out.println(result);

        String testFile = "./src/test/resources/ReportTemplateGenerator/ShareHolders.xlsx";


        FirstReportConfig config = new FirstReportConfig()
                .fileName("test.xlsx")
                .startColumn(0)
                .startRow(1)
                .template(Files.readAllBytes(new File(testFile).toPath()))
                .data(result);

        File outFileDir = new File("./target/reportDataTest/");

        if (!outFileDir.exists()) {
            outFileDir.mkdir();
        }

        File file = new File(outFileDir, reportName + ".xlsx");
        FileUtils.writeByteArrayToFile(file, new FirstReportTemplateGenerator().generate(config));
    }

}
