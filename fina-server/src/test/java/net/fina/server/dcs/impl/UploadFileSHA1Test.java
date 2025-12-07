package net.fina.server.dcs.impl;


import net.fina.server.dcs.uploadfile.util.UploadFileUtil;
import net.fina.server.dcs.uploadfile.entity.UploadFile;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class UploadFileSHA1Test {
    @Test
    public void test() throws IOException {
        UploadFile uf = new UploadFile();
        File testFile = new File("./src/test/resources/net/fina/server/dcs/impl/UploadFileSHA1Test01.xls");
        uf.setUploadedFile(Files.readAllBytes(testFile.toPath()));
        System.out.println(UploadFileUtil.sha1(uf));
    }
}
