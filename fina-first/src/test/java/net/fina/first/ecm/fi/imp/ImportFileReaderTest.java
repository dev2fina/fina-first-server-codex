package net.fina.first.ecm.fi.imp;

import net.fina.first.ecm.fi.imp.model.RegistryModel;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

public class ImportFileReaderTest {

    @Test
    public void testImportByMapping() throws Exception {
        try (InputStream inputStream = new FileInputStream("./src/test/resources/ImportFileReader/სგს-ს რეესტრი.V2_small.xlsx")) {
            ImportFileReader importFileReader = new ImportFileReader();
            List<RegistryModel> registryList = importFileReader.readFileByMapping(inputStream);

            System.out.println(registryList.size());
        }
    }

}
