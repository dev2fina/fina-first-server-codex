package net.fina.server.st.crypto;

import net.fina.common.server.util.ConfigurationUtil;
import net.fina.server.st.crypto.v1.SecurityManagerRSA;
import net.fina.server.st.crypto.v2.SecurityManagerImplV2;
import org.jboss.logging.Logger;

import java.io.File;
import java.io.FileInputStream;

public class SecurityManagerFactory {
    private static final Logger log = Logger.getLogger(SecurityManagerFactory.class.getName());

    public static SecurityManagerBase get(byte[] keyStoreBytes, String alias, char[] password, byte[] encodedKey, byte[] clientCertificate, String currentUser) throws Exception {
        CertificateMode certificateMode = SecurityManagerUtil.getCertificateMode();

        switch (certificateMode) {
            case HYBRID:
                return new SecurityManagerImplV2(encodedKey, clientCertificate);
            default:
                if (encodedKey == null) {
                    return new SecurityManagerRSA(keyStoreBytes, password, alias);
                }
                return new SecurityManagerRSA(keyStoreBytes, alias, password, encodedKey);
        }
    }

    public static byte[] getKeyStoreBytes(String identity) {
        byte[] keyStoreBytes = null;
        try {
            String keyStoreRepositoryPath = ConfigurationUtil.get().get("KeyStoreRepositoryPath");
            if ((keyStoreRepositoryPath != null) && (!keyStoreRepositoryPath.isEmpty())) {
                File keyStoreFiles = new File(keyStoreRepositoryPath);
                if (keyStoreFiles.exists() && (keyStoreFiles.isDirectory())) {
                    File[] files = keyStoreFiles.listFiles();
                    if (files != null && files.length > 0) {
                        for (File file : files) {
                            if (file.getName().equalsIgnoreCase(identity + ".pfx")) {
                                try (FileInputStream in = new FileInputStream(file)) {
                                    keyStoreBytes = new byte[in.available()];
                                    in.read(keyStoreBytes);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Throwable t) {

            if (SecurityManagerUtil.getCertificateMode() == CertificateMode.LEGACY) {
                log.warn("Cannot Read Keystore For identity : " + identity);
                log.error(t.getMessage(), t);
            }
        }

        return keyStoreBytes;
    }


}
