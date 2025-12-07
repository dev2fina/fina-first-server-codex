package net.fina.server.dcs.uploadfile.impl.reader.fina;

import net.fina.server.BaseTest;
import net.fina.server.dcs.uploadfile.entity.UploadFile;
import net.fina.server.returns.xml.Header;
import net.fina.server.returns.xml.Return;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class FinaDocumentReaderTest extends BaseTest {

    @Test
    void test() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put("versionFileName", "version.properties");
        properties.put("dcs.user.login", "cmb00011");
        properties.put("net.fina.dcs.security.encrypt", false);
        properties.put("net.fina.dcs.security.sign", false);

        Header header = new Header();
        header.setBankCode("12345");
        header.setPeriodFrom("01/03/2009");
        header.setPeriodEnd("31/03/2009");
        properties.put("dcs.xml.header", header);

        File f = new File("./src/test/resources/net/fina/server/dcs/uploadfile/impl/reader/fina/Test-12345.fina");

        UploadFile file = new UploadFile();
        file.setFileName("Plc-CMB00011s012025.fina");
        file.setUploadedFile(FileUtils.readFileToByteArray(f));

        FinADocumentReader documentReader = new FinADocumentReader(properties);
        documentReader.setFile(file);

        List<Return> rets = documentReader.getReturns();


        Assertions.assertEquals(2, rets.size());


    }
}
