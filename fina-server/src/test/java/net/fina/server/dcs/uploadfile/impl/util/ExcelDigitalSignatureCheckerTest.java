package net.fina.server.dcs.uploadfile.impl.util;

import net.fina.common.client.exception.DcsTypeException;
import net.fina.common.server.util.ConfigurationUtil;
import net.fina.server.BaseTest;
import net.fina.server.dcs.uploadfile.impl.reader.excel.ExcelMatrixReader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ExcelDigitalSignatureCheckerTest extends BaseTest {

    private Map<String, Object> properties;

    @Before
    public void before() {
        properties = new HashMap<>();
        ExcelMatrixReader matrixReader = new ExcelMatrixReader("./src/test/resources/net/fina/server/dcs/uploadfile/impl/util/Matrix.xls", null);
        properties.put("dcs.primary.matrix.option", matrixReader.getOptions().get(0));
        properties.put("dcs.user.login", "dev-testuser");
    }

    @After
    public void after() {
        properties.clear();
        System.clearProperty(ConfigurationUtil.FINA_CONFIG_DIR);
    }

    @Test
    public void testTrue() throws IOException {
        properties.put("dcs.security.sign.rootCA", "./src/test/resources/net/fina/server/dcs/uploadfile/impl/util/NBG_Class_2_INT_Sub_CA_NBG_Class_1_Root_CA_.cer");
        test();
    }

    @Test(expected = DcsTypeException.class)
    public void testFalse() throws IOException {
        properties.put("dcs.security.sign.rootCA", "./src/test/resources/net/fina/server/dcs/uploadfile/impl/util/NBG_Class_1_Root_CA.cer");
        test();
    }

    private void test() throws IOException {
        ExcelDigitalSignatureChecker excelDigitalSignatureChecker = new ExcelDigitalSignatureChecker(properties);
        if (excelDigitalSignatureChecker.isCheckDigitalSignature()) {
            excelDigitalSignatureChecker.checkDigitalSignature(Files.readAllBytes(new File("./src/test/resources/net/fina/server/dcs/uploadfile/impl/util/test_sign.xlsx").toPath()));
        }
    }
}
