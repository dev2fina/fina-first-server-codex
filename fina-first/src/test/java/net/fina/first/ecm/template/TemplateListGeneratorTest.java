package net.fina.first.ecm.template;

import freemarker.template.TemplateException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TemplateListGeneratorTest {

    @Test
    public void test() throws IOException, InvalidFormatException, TemplateException {

        String testFile = "./src/test/resources/TemplateProcessor/TemplateList.docx";

        byte[] fileContent = Files.readAllBytes(new File(testFile).toPath());

        TemplateProcessor processor = new TemplateProcessor();

        String[] testData = new String[]{
                "Abeid Karume",
                "Aboud Jumbe",
                "Ali Hassan Mwinyi"
        };
        ArrayList<String> testList = new ArrayList<>(Arrays.asList(testData));

        Map<String, Object> model = new HashMap<>();
        model.put("testSimpleList", testList);
        model.put("testBulletList", testList);
        model.put("testNumberList", testList);

        byte[] result = processor.process(fileContent, model, null);

        Files.write(new File("./target/TemplateListGeneratorTest.docx").toPath(), result);
    }

}
