package net.fina.server.util;

import net.fina.common.client.exception.FinATypeException;
import net.fina.common.server.util.ConfigurationUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class PostboxFileUtil {
    private static final String REPOSITORY_PATH_KEY = "POSTBOX_REPOSITORY_PATH";

    private String getFilePath(long fileId, String fileName) {
        return ConfigurationUtil.get().get(REPOSITORY_PATH_KEY) + File.separator + Long.toString(fileId) + File.separator + fileName;
    }

    public void saveContent(long fileId, String fileName, byte[] content) throws FinATypeException {
        String path = getFilePath(fileId, fileName);

        try {
            File file = new File(path);
            file.getParentFile().mkdirs();
            file.createNewFile();

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(content);
            fos.close();
        } catch (IOException e) {
            throw new FinATypeException(e, FinATypeException.Type.GENERAL_ERROR);
        }
    }

    public byte[] getContent(long fileId, String fileName) throws FinATypeException {
        String path = getFilePath(fileId, fileName);

        byte fileContent[];
        try {
            File file = new File(path);
            fileContent = new byte[(int) file.length()];

            FileInputStream fin = new FileInputStream(file);
            fin.read(fileContent);
            fin.close();
        } catch (IOException e) {
            throw new FinATypeException(e, FinATypeException.Type.GENERAL_ERROR);
        }

        return fileContent;
    }


    public void removeContent(long fileId, String fileName) throws FinATypeException {
        String path = getFilePath(fileId, fileName);

        File file = new File(path);
        if (file.exists()) {
            File folder = file.getParentFile();

            boolean fileDeleted = file.delete();
            boolean folderDeleted = folder.delete();
            if (!fileDeleted || !folderDeleted) {
                throw new FinATypeException("Failed to remove content.");
            }
        }
    }
}
