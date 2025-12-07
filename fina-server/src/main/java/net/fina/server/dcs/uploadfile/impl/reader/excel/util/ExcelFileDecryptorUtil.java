package net.fina.server.dcs.uploadfile.impl.reader.excel.util;

import net.fina.common.client.exception.DcsTypeException;
import net.fina.common.server.util.ConfigurationUtil;
import net.fina.server.reg.impl.RegFileProcessException;
import net.fina.server.st.crypto.CertificateMode;
import net.fina.server.st.crypto.SecurityManagerBase;
import net.fina.server.st.crypto.SecurityManagerFactory;
import net.fina.server.st.crypto.SecurityManagerUtil;
import net.fina.server.util.TempFileUtil;
import org.apache.commons.io.FileUtils;
import org.jboss.logging.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ExcelFileDecryptorUtil {

    private static final Logger log = Logger.getLogger(ExcelFileDecryptorUtil.class.getName());

    public static byte[] extractFileContent(byte[] uploadFileContent, byte[] keyStoreBytes, String currentUser) throws DcsTypeException {

        Map<String, byte[]> entryDataMap = new HashMap<>();
        try {
            entryDataMap = extractZip(uploadFileContent);
            CertificateMode certificateMode = SecurityManagerUtil.getCertificateMode();

            ConfigurationUtil util = ConfigurationUtil.get();
            String keyInfoProperty = util.get("KeyStoreInfo");
            char[] password = null;
            if ((keyInfoProperty != null) && (!keyInfoProperty.trim().isEmpty()) && certificateMode.equals(CertificateMode.LEGACY)) {
                File keyInfoFile = new File(keyInfoProperty);
                if (keyInfoFile.exists()) {
                    Properties keyProperties = new Properties();
                    try (InputStream in = new FileInputStream(keyInfoFile)) {
                        keyProperties.load(in);
                        password = keyProperties.getProperty(currentUser).trim().toCharArray();
                    }
                }
            }
            SecurityManagerBase securityManager = SecurityManagerFactory.get(keyStoreBytes, util.get("KeyStoreAlias"), password, entryDataMap.get("stamp"), entryDataMap.get("clientCertificate"), currentUser);

            byte[] decryptedExcel = securityManager.decrypt(entryDataMap.get("file"));

            return decryptedExcel;
        } catch (DcsTypeException dcsTypeException) {
            throw dcsTypeException;
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            throw new DcsTypeException(DcsTypeException.Type.SECURITY_INVALID_ENCRYPT);
        }
    }

    public static byte[] extractDecryptedExcelFile(byte[] uploadFileContent, byte[] keyStoreBytes, char[] password) throws Exception {
        Map<String, byte[]> entryDataMap = new HashMap<>();
        try {
            entryDataMap = extractZip(uploadFileContent);

            String author = new String(entryDataMap.get("author"));

            ConfigurationUtil util = ConfigurationUtil.get();

            SecurityManagerBase securityManager = SecurityManagerFactory.get(keyStoreBytes, util.get("KeyStoreAlias"), password, entryDataMap.get("stamp"), entryDataMap.get("clientCertificate"), author);

            byte[] decryptedExcel = securityManager.decrypt(entryDataMap.get("file"));

            return decryptedExcel;
        } catch (DcsTypeException dcsTypeException) {
            throw dcsTypeException;
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            throw new DcsTypeException(DcsTypeException.Type.SECURITY_INVALID_ENCRYPT);
        }

    }

    private static Map<String, byte[]> extractZip(byte[] zipContent) throws Exception {
        Map<String, byte[]> entryDataMap = new HashMap<>();
        List<String> entryNames = List.of("file", "stamp", "author", "clientCertificate");
        try (ByteArrayInputStream fis = new ByteArrayInputStream(zipContent);
             ZipInputStream zis = new ZipInputStream(fis)) {

            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                for (String entryName : entryNames) {
                    if (entry.getName().equals(entryName)) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        byte[] buffer = new byte[4096];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            baos.write(buffer, 0, len);
                        }
                        entryDataMap.put(entry.getName(), baos.toByteArray());
                        break;
                    }
                }
                zis.closeEntry();
            }
        }

        return entryDataMap;
    }

    public static void extractFileContentToFile(InputStream fileInputStream, byte[] keyStoreBytes, String currentUser, File file) throws DcsTypeException {
        File tmpFile = null;

        Map<String, byte[]> entryDataMap = new HashMap<>();
        try {
            CertificateMode certificateMode = SecurityManagerUtil.getCertificateMode();

            ConfigurationUtil util = ConfigurationUtil.get();
            String keyInfoProperty = util.get("KeyStoreInfo");
            char[] password = null;
            if ((keyInfoProperty != null) && (!keyInfoProperty.trim().isEmpty()) && certificateMode.equals(CertificateMode.LEGACY)) {
                File keyInfoFile = new File(keyInfoProperty);
                if (keyInfoFile.exists()) {
                    Properties keyProperties = new Properties();
                    try (InputStream in = new FileInputStream(keyInfoFile)) {
                        keyProperties.load(in);
                        password = keyProperties.getProperty(currentUser).trim().toCharArray();
                    }
                }
            }


            List<String> entryNames = List.of("stamp", "author", "clientCertificate");

            try (InputStream in = fileInputStream;
                 ZipInputStream zis = new ZipInputStream(in)) {
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    if (entry.getName().equals("file")) {
                        tmpFile = TempFileUtil.createTempFile("extracted");
                        try (FileOutputStream fos = new FileOutputStream(tmpFile)) {
                            byte[] buffer = new byte[4096];
                            int len;
                            while ((len = zis.read(buffer)) > 0) {
                                fos.write(buffer, 0, len);
                            }
                        }
                    } else {
                        for (String entryName : entryNames) {
                            if (entry.getName().equals(entryName)) {
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                byte[] buffer = new byte[4096];
                                int len;
                                while ((len = zis.read(buffer)) > 0) {
                                    baos.write(buffer, 0, len);
                                }
                                entryDataMap.put(entry.getName(), baos.toByteArray());
                                break;
                            }
                        }
                    }

                    zis.closeEntry();
                }
            }

            SecurityManagerBase securityManager = SecurityManagerFactory.get(keyStoreBytes, util.get("KeyStoreAlias"), password, entryDataMap.get("stamp"), entryDataMap.get("clientCertificate"), currentUser);

            InputStream decryptedStream = securityManager.decrypt(new FileInputStream(tmpFile));
            FileUtils.copyInputStreamToFile(decryptedStream, file);


        } catch (DcsTypeException dcsTypeException) {
            throw dcsTypeException;
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            throw new RegFileProcessException(DcsTypeException.Type.SECURITY_INVALID_ENCRYPT);
        } finally {
            if (tmpFile != null) {
                tmpFile.delete();
            }
        }
    }
}
