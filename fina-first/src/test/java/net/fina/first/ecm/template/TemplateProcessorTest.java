package net.fina.first.ecm.template;

import freemarker.template.TemplateException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class TemplateProcessorTest {

    @Test
    public void test() throws IOException, InvalidFormatException, TemplateException {

        String testFile = "./src/test/resources/TemplateProcessor/Test1.docx";

        byte[] fileContent = Files.readAllBytes(new File(testFile).toPath());

        TemplateProcessor processor = new TemplateProcessor();

        Map<String, Object> model = new HashMap<>();
        model.put("test1", "The Test One");
        model.put("test2", "The Test Two");

        Map<String,Object>firegistry=new HashMap<>();
        firegistry.put("fiRegistryCode","testCode1");
        model.put("fiRegistry",firegistry);


        byte[] result = processor.process(fileContent, model, null);

        Files.write(new File("./target/TemplateProcessorTest.docx").toPath(), result);
    }

    @Test
    public void testIteratorTable() throws IOException, InvalidFormatException, TemplateException {

        String testFile = "./src/test/resources/TemplateProcessor/Test3.docx";

        byte[] fileContent = Files.readAllBytes(new File(testFile).toPath());

        Map<String, Object> model = new HashMap<>();
        model.put("test", "Test");

        Map<String, Object> model2 = new HashMap<>();
        model2.put("string", "String Value 1");
        model2.put("number", "Number Value 1");
        model2.put("date", "Date Value 1");

        Map<String, Object> model3 = new HashMap<>();
        model3.put("string", "String Value 2");
        model3.put("number", "Number Value 2");
        model3.put("date", "Date Value 2");

        Map<String, Object> model4 = new HashMap<>();
        model4.put("string", "String Value 3");
        model4.put("number", "Number Value 3");
        model4.put("date", "Date Value 3");

        byte[] result = new TemplateProcessor().process(fileContent, model, Arrays.asList(model2, model3, model4));

        Files.write(new File("./target/TemplateProcessorTest3.docx").toPath(), result);
    }

    @Test
    public void testIteratorTable2() throws IOException, InvalidFormatException, TemplateException {

        String testFile = "./src/test/resources/TemplateProcessor/Test4.docx";

        byte[] fileContent = Files.readAllBytes(new File(testFile).toPath());

        Map<String, Object> model = new HashMap<>();

        Map<String, Object> model2 = new HashMap<>();
        model2.put("IT_s0", "String Zero");
        model2.put("IT_s1", "String One");
        model2.put("IT_s2", "String Two");
        model2.put("IT_s3", "String Three");
        model2.put("IT_s4", "String Four");
        model2.put("IT_s5", "String Five");
        model2.put("IT_s6", "String Six");
        model2.put("IT_n1", 666);
        model2.put("IT_n2", 999);
        model2.put("IT_d1", new Date());

        Map<String, Object> model3 = new HashMap<>();
        model3.put("IT_s0", "String Zero");
        model3.put("IT_s1", "String One");
        model3.put("IT_s2", "String Two");
        model3.put("IT_s3", "String Three");
        model3.put("IT_s4", "String Four");
        model3.put("IT_s5", "String Five");
        model3.put("IT_s6", "String Six");
        model3.put("IT_n1", 666);
        model3.put("IT_n2", 999);
        model3.put("IT_d1", new Date());

        byte[] result = new TemplateProcessor().process(fileContent, model, Arrays.asList(model2, model3));

        Files.write(new File("./target/TemplateProcessorTest4.docx").toPath(), result);
    }

    @Test
    public void replaceParagraphValues() {
        XWPFDocument doc = new XWPFDocument();
        XWPFParagraph paragraph = doc.createParagraph();

        XWPFRun run = paragraph.createRun();
        run.setText("pre [=test");
        run.setColor("FFFFFF");
        run = paragraph.createRun();
        run.setText("1] post");
        run.setColor("000000");

        new TemplateProcessor().replaceParagraphValues(paragraph,
                Collections.singletonMap("test1", "This Is A Value"));

        paragraph.getRuns().forEach(xwpfRun -> System.out.println("|" + xwpfRun.getText(0) + "| " + xwpfRun.getColor()));

        System.out.println(paragraph.getText());

        assert paragraph.getRuns().size() == 3;
    }


    @Test
    public void testReadKeys() throws Exception {
        String testFile = "./src/test/resources/TemplateProcessor/Test1.docx";

        byte[] fileContent = Files.readAllBytes(new File(testFile).toPath());
        System.out.println(new TemplateKeyExtractor().getKeysFromDocument(fileContent));
    }

}
