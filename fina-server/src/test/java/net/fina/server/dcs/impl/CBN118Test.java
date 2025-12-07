package net.fina.server.dcs.impl;

import org.apache.poi.Version;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;

public class CBN118Test {

    @Test
    public void test() throws IOException, InvalidFormatException {
        File testFile = new File("./src/test/resources/net/fina/server/dcs/impl/MFB51221m052016.xls");
        readFile(testFile);
    }

    @Test
    public void test2() throws IOException, InvalidFormatException {
        File dir = new File("./src/test/resources/net/fina/server/dcs/impl/invalid_signature");

        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                readFile(file);
            }
        }
    }

    protected void readFile(File testFile) throws IOException, InvalidFormatException {

        System.out.println(Version.getVersion());

        byte[] fileContent = Files.readAllBytes(testFile.toPath());
        try (InputStream dataStream = new ByteArrayInputStream(fileContent); BufferedInputStream buff = new BufferedInputStream(dataStream)) {
            try {
                WorkbookFactory.create(buff);
                System.out.println(testFile.getName() + ":DONE");
            } catch (Throwable t) {
                System.err.println(testFile.getName() + ":" + t.getMessage());
            }
        }
    }
}
