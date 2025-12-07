package net.fina.server.dcs.uploadfile.util;

import net.fina.server.dcs.uploadfile.entity.UploadFile;
import org.jboss.logging.Logger;

import java.security.MessageDigest;

public class UploadFileUtil {
    private static Logger log = Logger.getLogger(UploadFileUtil.class);


    public static String sha1(UploadFile uploadFile) {
        return calculateHash(uploadFile, "SHA1");
    }

    private static String calculateHash(UploadFile uploadFile, String algorithm) {
        String result = "";
        if (uploadFile != null && uploadFile.getUploadedFile() != null) {
            try {
                MessageDigest md5 = MessageDigest.getInstance(algorithm);
                md5.update(uploadFile.getUploadedFile());
                byte[] digest = md5.digest();
                StringBuilder sb = new StringBuilder();
                for (byte b : digest) {
                    sb.append(String.format("%02x", b & 0xff).toUpperCase());
                }
                result = sb.toString();
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
            }
        }
        return result;
    }
}
