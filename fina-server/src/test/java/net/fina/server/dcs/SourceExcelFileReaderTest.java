package net.fina.server.dcs;

import net.fina.common.client.dcs.DocumentType;
import net.fina.common.client.exception.ConverterDcsTypeException;
import net.fina.common.client.exception.DcsTypeException;
import net.fina.common.server.util.ConfigurationUtil;
import net.fina.server.BaseTest;
import net.fina.server.dcs.uploadfile.entity.UploadFile;
import net.fina.server.dcs.uploadfile.impl.UploadFileSession;
import net.fina.server.dcs.uploadfile.impl.converter.AbstractConverter;
import net.fina.server.dcs.uploadfile.impl.converter.AbstractConverterFactory;
import net.fina.server.dcs.uploadfile.impl.converter.ConverterInfo;
import net.fina.server.dcs.uploadfile.impl.converter.ConverterUtil;
import net.fina.server.dcs.uploadfile.impl.reader.excel.MatrixMappingSource;
import net.fina.server.dcs.uploadfile.impl.reader.excel.MatrixOptionBase;
import net.fina.server.dcs.uploadfile.impl.util.FileAnalyzer;
import net.fina.server.i18n.entity.Language;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SourceExcelFileReaderTest extends BaseTest {

    @Test
    public void test1() throws NoSuchMethodException, InvalidFormatException, IOException, IllegalAccessException, InvocationTargetException {
        test("./src/test/resources/net/fina/server/dcs/test1_2016_pass.xlsx");
    }

    @Test
    public void test2() throws NoSuchMethodException, InvalidFormatException, IOException, IllegalAccessException, InvocationTargetException {
        test("./src/test/resources/net/fina/server/dcs/test2_2016_no_pass.xlsx");
    }

    @Test
    public void test3() throws NoSuchMethodException, InvalidFormatException, IOException, IllegalAccessException, InvocationTargetException {
        test("./src/test/resources/net/fina/server/dcs/test3_2007_pass.xlsx");
    }

    private void test(String testFile) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, IOException, InvalidFormatException {
        try {
            Language language = new Language();
            language.setCode("en_US");
            language.setDateFormat("dd/MM/yyyy");

            Map<String, Object> properties = new HashMap<>();
            properties.put("dcs.excel.sheetProtection.password.string", "");
            properties.put("dcs.language", language);

            UploadFile uploadFile = new UploadFile();
            uploadFile.setUploadedFile(Files.readAllBytes(new File(testFile).toPath()));
            uploadFile.setFileName("SBS12345m021992.xls");

            UploadFileSession uploadFileSession = new UploadFileSession();

            Method method = uploadFileSession.getClass().getDeclaredMethod("getExcelMatrixOptions", String.class);
            method.setAccessible(true);
            Object r = method.invoke(uploadFileSession, "./src/test/resources/net/fina/server/dcs/");

            List<MatrixOptionBase> options = (List<MatrixOptionBase>) r;
            MatrixOptionBase option = null;


            for (MatrixOptionBase o : options) {
                if (o.getPattern() != null) {
                    if (uploadFile.getFileName().matches(o.getPattern())) {
                        if (option == null) {
                            option = o;
                        } else {
                            DcsTypeException.Type type = DcsTypeException.Type.MATRIX_DUPLICATED_PATTERN;
                            throw new ConverterDcsTypeException(type, type.getReplaceableCode());
                        }
                    }
                }

            }

            String mappingSourceProperty = ConfigurationUtil.get().get("MATRIX_MAPPING_SOURCE");
            MatrixMappingSource matrixMappingSource = mappingSourceProperty == null ? MatrixMappingSource.EXCEL : MatrixMappingSource.valueOf(mappingSourceProperty.toUpperCase());

            properties.put("dcs.primary.matrix.option", option);

            properties.put("dcs.main.matrix", "./src/test/resources/net/fina/server/dcs/Matrix.xls");
            properties.put("dcs.primary.matrix", "./src/test/resources/net/fina/server/dcs/" + option.getMatrixForEachType());
            properties.put("dcs.matrix.mapping.source", matrixMappingSource);
            properties.put("dcs.user.login", "demo");

            FileAnalyzer analyzer = new FileAnalyzer(option.getPattern(), uploadFile.getFileName(), option, language);
            String extension = analyzer.getExtension().toLowerCase();

            DocumentType documentType = ConverterUtil.detectDocumentType(extension);

            AbstractConverterFactory abstractConverterFactory = AbstractConverterFactory.getInstance();
            AbstractConverter converter = abstractConverterFactory.createAbstractConverter(uploadFile, properties, documentType);
            ConverterInfo info = converter.convert();

            System.out.println(info.getReasons());
        } catch (Throwable t) {
            System.err.println(t.getMessage());
        }
    }
}
