package net.fina.server.dcs.uploadfile.impl.util;

import com.google.gson.Gson;
import net.fina.common.client.exception.DcsTypeException;
import net.fina.server.st.impl.MdtReleaseVersion;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class FinaFileUtil {

    public static void checkVersion(byte[] versionFileContent, Map<String, Object> properties) throws IOException {
        Properties versionProperties = new Properties();
        String systemMdtVersion = properties.get("mdtReleaseVersion") != null ? properties.get("mdtReleaseVersion").toString() : null;
        String systemOstVersion = properties.get("ostVersion") != null ? properties.get("ostVersion").toString() : null;

        if (versionFileContent != null) {
            try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(versionFileContent)) {
                versionProperties.load(byteArrayInputStream);
            }

            String fileMdtVersion = versionProperties.getProperty("mdtVersion");
            String fileOstVersion = versionProperties.getProperty("submissionToolVersion");

            if (systemOstVersion != null && !systemOstVersion.equalsIgnoreCase(fileOstVersion)) {
                String[] params = new String[]{fileOstVersion, systemOstVersion};
                throw new DcsTypeException(DcsTypeException.Type.INVALID_OST_VERSION, params);
            }

            Gson gson = new Gson();
            MdtReleaseVersion.Version fileMdtReleaseVersion = null;
            try {
                fileMdtReleaseVersion = gson.fromJson(fileMdtVersion, MdtReleaseVersion.Version.class);
            } catch (Exception ex) {
                throw new DcsTypeException(DcsTypeException.Type.SUBMITED_FILE_INVALID_VERSION);
            }

            if (systemMdtVersion != null && fileMdtReleaseVersion != null) {
                MdtReleaseVersion systemMdtReleaseVersion = gson.fromJson(systemMdtVersion, MdtReleaseVersion.class);
                boolean exist = false;
                if (systemMdtReleaseVersion != null) {
                    for (MdtReleaseVersion.Version version : systemMdtReleaseVersion.getVersions()) {
                        if (version.getT().trim().equalsIgnoreCase(fileMdtReleaseVersion.getT().trim())
                                && version.getV() == fileMdtReleaseVersion.getV()) {
                            exist = true;
                            break;
                        }
                    }
                    if (!exist) {
                        String[] params = new String[]{fileMdtVersion, systemMdtVersion};
                        throw new DcsTypeException(DcsTypeException.Type.SUBMITED_FILE_INVALID_VERSION, params);
                    }
                }
            }

        } else {
            throw new DcsTypeException(DcsTypeException.Type.SUBMITED_FILE_INVALID_VERSION);
        }
    }
}
