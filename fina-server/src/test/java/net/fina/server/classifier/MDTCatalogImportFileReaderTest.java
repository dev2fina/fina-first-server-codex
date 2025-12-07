package net.fina.server.classifier;

import net.fina.server.classifier.model.MDTCatalogImportStatusMetaModel;
import net.fina.server.classifier.model.MDTCatalogMetaModel;
import net.fina.server.classifier.model.MDTCatalogRowItemMetaModel;
import net.fina.server.classifier.util.MDTCatalogImportUtil;
import net.fina.common.server.StatisticsLogger;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class MDTCatalogImportFileReaderTest {

    @Test
    public void readFromExcel() throws Exception {
        MDTCatalogImportUtil util = new MDTCatalogImportUtil();
        String file = getClass().getPackage().getName().replace('.', '/') + "/Catalog_Import_Template.xlsx";
        Map<MDTCatalogMetaModel, List<MDTCatalogRowItemMetaModel>> data = new HashMap<>();
        Map<String, Long> langCodeIdMap = new HashMap<String, Long>() {{
            put("en_US", 1l);
            put("ka_GE", 2l);
        }};

        MDTCatalogImportStatusMetaModel result = util.readExcelFile(this.getClass().getClassLoader().getResourceAsStream(file), 1, "en_US", new StatisticsLogger("import data"), data, langCodeIdMap);
        assertTrue(result.getErrors().isEmpty());

//        assertEquals(result.getWarnings().size(), 1);

    }
}
