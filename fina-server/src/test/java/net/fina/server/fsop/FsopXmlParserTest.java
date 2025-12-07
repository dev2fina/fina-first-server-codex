package net.fina.server.fsop;


import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FsopXmlParserTest {

    @Test
    public void test1() {
        ImportedReturnValidationEventHandler validationEventHandler = new ImportedReturnValidationEventHandler();
        FsopXmlParser.getInstance().convert("BLAH".getBytes(), validationEventHandler);
        printErrorMessage(validationEventHandler);
    }

    @Test
    public void test2() throws IOException {
        ImportedReturnValidationEventHandler validationEventHandler = new ImportedReturnValidationEventHandler();
        FsopXmlParser.getInstance().convert(Files.readAllBytes(new File("./src/test/resources/fsop/Return_B.I-info.xml").toPath()), validationEventHandler);
        printErrorMessage(validationEventHandler);
    }

    @Test
    public void testWarn() throws IOException {
        ImportedReturnValidationEventHandler validationEventHandler = new ImportedReturnValidationEventHandler();
        FsopXmlParser.getInstance().convert(Files.readAllBytes(new File("./src/test/resources/fsop/Return_B.I-warn.xml").toPath()), validationEventHandler);
        printErrorMessage(validationEventHandler);
    }

    @Test
    public void testError() throws IOException {
        ImportedReturnValidationEventHandler validationEventHandler = new ImportedReturnValidationEventHandler();
        FsopXmlParser.getInstance().convert(Files.readAllBytes(new File("./src/test/resources/fsop/Return_B.I-error.xml").toPath()), validationEventHandler);
        printErrorMessage(validationEventHandler);
    }

    private void printErrorMessage(ImportedReturnValidationEventHandler validationEventHandler) {

        String errorMessage = "";
        for (String message : validationEventHandler.getMessages()) {
            errorMessage += message;
            errorMessage += "\n";
        }

        System.out.println(errorMessage);
    }

}
