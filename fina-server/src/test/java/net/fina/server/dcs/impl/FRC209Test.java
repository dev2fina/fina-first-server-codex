package net.fina.server.dcs.impl;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class FRC209Test extends CBN118Test {

    @Test
    public void test() throws IOException, InvalidFormatException {
        File testFile = new File("./src/test/resources/net/fina/server/dcs/impl/FRC209Files/FXB2599279m022016.xls");
        readFile(testFile);
    }

    @Test
    public void test2() throws IOException, InvalidFormatException {
        File dir = new File("./src/test/resources/net/fina/server/dcs/impl/FRC209Files");

        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                readFile(file);
            }
        }
    }
}
