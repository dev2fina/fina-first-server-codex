package net.fina.server.dcs.uploadfile.impl;

import net.fina.common.client.dcs.UploadType;
import net.fina.common.shared.ContentModel;
import net.fina.server.dcs.uploadfile.entity.UploadFile;
import net.fina.server.dcs.uploadfile.util.UploadFilePrintUtil;
import net.fina.server.returns.converter.ConvertOptions;
import net.fina.server.security.entity.User;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UploadFilePrintTest {
    private String outFolderPath;
    private List<UploadFile> uploadFiles;

    @Before
    public void before() throws Exception {
        outFolderPath = "./target/UploadFilePrint/";
        File outFile = new File(outFolderPath);
        if (!outFile.exists()) {
            outFile.mkdir();
        }

        uploadFiles = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            uploadFiles.add(createUploadFile(i));
        }
    }

    @Test
    public void testUploadFilePrintHtml() throws Exception {
        printUploadFileByType(ConvertOptions.HTML);
    }

    @Test
    public void testUploadFilePrintCSV() throws Exception {
        printUploadFileByType(ConvertOptions.CSV);
    }

    @Test
    public void testUploadFilePrintXlsx() throws Exception {
        printUploadFileByType("xlsx");
    }

    private void printUploadFileByType(ConvertOptions options) throws Exception {
       printUploadFileByType(options.toString());
    }
    private void printUploadFileByType(String fileType) throws Exception {
        ContentModel contentModel = UploadFilePrintUtil.generateFile(fileType, uploadFiles, "", "en_US");
        byte[] content = contentModel.getContent();

        String outFileName = outFolderPath + "convert_"
                + new SimpleDateFormat("dd_HH_mm_ss").format(new Date()) + "." + fileType.toLowerCase();
        Files.write(new File(outFileName).toPath(), content);
    }


    private UploadFile createUploadFile(int index) {
        UploadFile uploadFile = new UploadFile();
        uploadFile.setFileName("File_" + index + ".xlsx");
        uploadFile.setType(UploadType.MANUAL);
        uploadFile.setUploadedTime(new Date());
        uploadFile.setStatus(Integer.toString(index));
        uploadFile.setUser(createTestUser("user" + index));
        uploadFile.setBankCode("FI_CODE_" + index);
        return uploadFile;
    }

    private User createTestUser(String login) {
        User user = new User();
        user.setLogin(login);
        return user;
    }
}
