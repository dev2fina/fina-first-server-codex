package net.fina.server.util;

import net.fina.common.server.util.ConfigurationUtil;

import java.io.File;
import java.util.UUID;

public class TempFileUtil {

    public static File createTempFile(String fileName) {
        String generatedString = UUID.randomUUID().toString();
        String tmpFileName = generatedString + fileName;
        if (tmpFileName.length() > 255) {
            tmpFileName = tmpFileName.substring(0, 254);
        }

        String configuredTempDirectory = ConfigurationUtil.get().get("TempFileLocation");
        String tmpFilePath = (configuredTempDirectory == null ? System.getProperty("java.io.tmpdir") : configuredTempDirectory) + File.separator + tmpFileName;
        File tmp = new File(tmpFilePath);
        tmp.deleteOnExit();

        return tmp;
    }
}
