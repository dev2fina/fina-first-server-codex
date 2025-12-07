package net.fina.server.st.crypto;

import net.fina.common.server.util.ConfigurationUtil;
import org.jboss.logging.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Properties;

public class SecurityManagerUtil {
    private static final Logger log = Logger.getLogger(SecurityManagerUtil.class.getName());

    public static CertificateMode getCertificateMode() {
        try {
            return CertificateMode.valueOf(ConfigurationUtil.get().get("SubmissionTool.CertificateMode").toUpperCase());
        } catch (Throwable ignore) {
            //ignore
        }
        return CertificateMode.LEGACY;
    }


    public static char[] getKeyStorePassword(String identity) {
        char[] password = null;
        try {
            String keyInfoProperty = ConfigurationUtil.get().get("KeyStoreInfo");
            if ((keyInfoProperty != null) && (!keyInfoProperty.trim().isEmpty())) {
                File keyInfoFile = new File(keyInfoProperty);
                if (keyInfoFile.exists()) {
                    Properties keyProperties = new Properties();
                    try (InputStream inputStream = new FileInputStream(keyInfoFile)) {
                        keyProperties.load(inputStream);
                        if (keyProperties.getProperty(identity) != null) {
                            password = keyProperties.getProperty(identity).trim().toCharArray();
                        } else if (keyProperties.getProperty(identity.toLowerCase()) != null) {
                            password = keyProperties.getProperty(identity.toLowerCase()).trim().toCharArray();
                        } else {
                            password = keyProperties.getProperty(identity.toUpperCase()).trim().toCharArray();
                        }
                    }
                }
            }
        } catch (Throwable t) {

            if (SecurityManagerUtil.getCertificateMode() == CertificateMode.LEGACY) {
                log.warn("Cannot Read Keystore Password For identity : " + identity);
                log.error(t.getMessage(), t);
            }
        }
        return password;

    }

    public static X509Certificate loadServerCertificate(KeyStore keyStore) throws Exception {
        X509Certificate serverCertificate;
        String alias = ConfigurationUtil.get().get("ServerKeyStore.Alias");

        if (keyStore.containsAlias(alias)) {
            serverCertificate = (X509Certificate) keyStore.getCertificate(alias);
        } else {
            throw new GeneralSecurityException("Alias is not found in keystore");
        }

        serverCertificate.checkValidity();

        return serverCertificate;
    }

    public static KeyStore loadServerKeyStore() throws Exception {
        String keystoreFilePath = ConfigurationUtil.get().get("ServerKeyStore.KeyStoreFile");
        String password = ConfigurationUtil.get().get("ServerKeyStore.KeystorePassword");

        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(new FileInputStream(keystoreFilePath), password.toCharArray());


        return keyStore;
    }

    public static PrivateKey loadServerPrivateKey(KeyStore keyStore) throws Exception {
        String password = ConfigurationUtil.get().get("ServerKeyStore.KeystorePassword");
        String alias = ConfigurationUtil.get().get("ServerKeyStore.Alias");

        KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(password.toCharArray());
        KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(alias, protParam);

        return pkEntry.getPrivateKey();
    }

    public static byte[] sign(byte[] data) {
        try {
            KeyStore keystore = loadServerKeyStore();
            PrivateKey privateKey = loadServerPrivateKey(keystore);
            java.security.Signature signature = java.security.Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey, new SecureRandom());
            signature.update(data);
            return signature.sign();
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            throw new RuntimeException(t);
        }
    }
}
