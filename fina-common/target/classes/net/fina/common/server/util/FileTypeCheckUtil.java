package net.fina.common.server.util;

import net.fina.common.shared.WrongFileTypeException;
import org.apache.tika.Tika;

import java.util.Arrays;
import java.util.List;

public class FileTypeCheckUtil {

    public static void check(byte[] file, List<String> fileTypes) throws WrongFileTypeException {

        Tika tika = new Tika();
        String fileType = tika.detect(file);
        if (!fileTypes.contains(fileType)) {
            throw new WrongFileTypeException("Wrong file type: " + fileType);
        }
    }

    public static void checkDefault(byte[] file) throws WrongFileTypeException {
        check(file, Arrays.asList("application/x-tika-msoffice", "application/pdf", "application/x-tika-ooxml",
                "application/zip", "application/vnd.ms-excel"));
    }

    public static String getFileType(byte[] file) {
        Tika tika = new Tika();
        return tika.detect(file);
    }

    public static void checkExecutable(byte[] file) throws WrongFileTypeException {
        List<String> notAllowedTypes = Arrays.asList("application/x-msdownload", "application/x-sh");
        Tika tika = new Tika();
        String fileType = tika.detect(file);
        for (String mimeType : notAllowedTypes) {
            if (fileType.trim().contains(mimeType)) {
                throw new WrongFileTypeException("Wrong file type: " + fileType);
            }
        }
    }
}
