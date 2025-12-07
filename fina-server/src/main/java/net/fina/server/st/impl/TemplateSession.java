/*
 * Copyright (C) 2014 FINA Ltd.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package net.fina.server.st.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import net.fina.common.client.filter.FiFilter;
import net.fina.common.client.property.PropertyKeys;
import net.fina.common.server.util.CommonUtil;
import net.fina.common.server.util.ConfigurationUtil;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.classifier.api.MDTCatalogLocal;
import net.fina.server.classifier.entity.MDTCatalog;
import net.fina.server.fi.api.FiLocal;
import net.fina.server.fi.entity.Fi;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.misc.ProductHelper;
import net.fina.server.returns.api.ReturnDefinitionLocal;
import net.fina.server.security.api.PropertyLocal;
import net.fina.server.security.api.UserLocal;
import net.fina.server.st.api.TemplateException;
import net.fina.server.st.api.TemplateLocal;
import net.fina.server.st.crypto.SecurityManagerBase;
import net.fina.server.st.crypto.SecurityManagerFactory;
import net.fina.server.st.crypto.SecurityManagerUtil;
import net.fina.server.st.json.ExcelConverterMetaInfo;
import net.fina.server.util.VersionUtil;
import org.apache.commons.lang.SerializationUtils;
import org.jboss.logging.Logger;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author nikoloz
 */
@Stateless
@Local(TemplateLocal.class)
@Interceptors(RecordingAuditor.class)
public class TemplateSession implements TemplateLocal {

    private final Logger log = Logger.getLogger(getClass());

    @EJB
    private PropertyLocal propertyLocal;

    @EJB
    private ReturnDefinitionLocal returnDefinitionLocal;

    @EJB
    private FiLocal fiLocal;
    @EJB
    private UserLocal userLocal;
    @Inject
    private MDTCatalogLocal mdtCatalogLocal;

    @Override
    public byte[] loadTemplate(String identity, String fiType) throws TemplateException {
        try {
            Map<FiFilter, Object> filterObjectMap = new HashMap<>();
            filterObjectMap.put(FiFilter.USER_ID, userLocal.findUserbyLogin(identity).getId());
            filterObjectMap.put(FiFilter.LOAD_All, true);
            List<Fi> userFis = fiLocal.load(filterObjectMap);

            if (fiType == null || fiType.trim().isEmpty()) {
                if (!userFis.isEmpty()) {
                    fiType = userFis.get(0).getFiType().getCode();
                }
            }

            //validate license
            ProductHelper.validateOstLicense(fiType);

            boolean taxonomyEncryptEnabled = taxonomyEncryptEnabled();

            SecurityManagerBase securityManager;
            if (taxonomyEncryptEnabled) {
                securityManager = SecurityManagerFactory.get(SecurityManagerFactory.getKeyStoreBytes(identity), ConfigurationUtil.get().get("KeyStoreAlias"), SecurityManagerUtil.getKeyStorePassword(identity), null, null, identity);
            } else {
                securityManager = getPlainSecurityManager();
            }

            String submissionToolTemplatePath = ConfigurationUtil.get().get("SubmissionTool.TemplatesPath");
            submissionToolTemplatePath += submissionToolTemplatePath.endsWith(File.separator) ? "" : File.separator;
            File fileDirectory = new File(submissionToolTemplatePath);
            File templatesDirectory = new File(fileDirectory, "templates/");
            File templateDirectory = new File(templatesDirectory, fiType);
            File allMdtTemplates = new File(templatesDirectory, "mdt");

            if (templateDirectory.exists()) {
                Map<String, byte[]> files = new HashMap<>(getMatrices(securityManager)); // init matrices

                for (File file : templateDirectory.listFiles()) {
                    try (FileInputStream in = new FileInputStream(file)) {
                        byte[] fileBytes = new byte[in.available()];
                        in.read(fileBytes);
                        files.put(file.getName(), securityManager.encrypt(fileBytes));
                    }
                }

                //serialize mdt xml-s
                for (File file : allMdtTemplates.listFiles()) {
                    try (FileInputStream in = new FileInputStream(file)) {
                        byte[] fileBytes = new byte[in.available()];
                        in.read(fileBytes);
                        files.put(file.getName(), securityManager.encrypt(fileBytes));
                    }
                }

                byte[] excelConverterMetaInfo = getExcelConverterMetaInfo(securityManager);
                files.put("excelConverterMetaInfo.json", excelConverterMetaInfo);

                files.put("fiCode", securityManager.encrypt(userFis.get(0).getCode().getBytes()));
                files.put("fiType", securityManager.encrypt(fiType.getBytes()));

                List<MDTCatalog> catalogList = mdtCatalogLocal.load(-1, -1, null);
                HashMap<Long, String> catalogMap = new HashMap<>();
                catalogList.forEach(c -> catalogMap.put(c.getCatalogNode().getId(), c.getCode() + " - " + c.getCatalogNode().getDescription().getDescription(ThreadLocalHolder.getLanguage().getId())));
                files.put("catalogNames", securityManager.encrypt(SerializationUtils.serialize(catalogMap)));

                // store properties
                Properties properties = new Properties();
                properties.setProperty(PropertyKeys.OST_TAXONOMY_ENCRYPT_ENABLE, String.valueOf(taxonomyEncryptEnabled));
                properties.setProperty("net.fina.version", VersionUtil.getVersion().getFinaVersion());
                properties.setProperty("maxPostSize", Long.toString(CommonUtil.getServerMaxPostSizeProperty(ManagementFactory.getPlatformMBeanServer())));
                properties.setProperty("license", new ObjectMapper().writeValueAsString(ProductHelper.getInstance().getOstLicense()));
                properties.setProperty("syncDate", String.valueOf(System.currentTimeMillis()));

                ByteArrayOutputStream propsOut = new ByteArrayOutputStream();
                properties.store(propsOut, "Fina Properties");

                files.put("properties", securityManager.encrypt(propsOut.toByteArray()));

                // put server public key
                files.put("spk", securityManager.getServersPublicKey());

                return generateTemplateFile(files, securityManager);
            }
        } catch (IOException | CertificateEncodingException e) {
            log.error(e.getMessage(), e);
            throw new TemplateException(e, TemplateException.Reason.DAMAGED_CERTIFICATE);
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            throw new TemplateException(t, TemplateException.Reason.UNEXPECTED_EXCEPTION);
        }
        return new byte[0];
    }

    @Override
    public String getVersion(String identity, String fiType) {
        Gson gson = new Gson();
        MdtReleaseVersion releaseVersion = gson.fromJson(propertyLocal.getSystemProperty(PropertyKeys.MDT_RELEASE_VERSION), MdtReleaseVersion.class);
        for (MdtReleaseVersion.Version version : releaseVersion.getVersions()) {
            if (version.getT().trim().equalsIgnoreCase(fiType)) {
                return Integer.toString(version.getV());
            }
        }
        return "1";
    }

    @Override
    public String getOstVersion() throws TemplateException {
        return getSubmissionToolSecurityPropertyValue("ostVersion");
    }

    @Override
    public String getCertificateCN(String userLogin) throws TemplateException {
        try {
            SecurityManagerBase securityManager = SecurityManagerFactory.get(SecurityManagerFactory.getKeyStoreBytes(userLogin), ConfigurationUtil.get().get("KeyStoreAlias"), SecurityManagerUtil.getKeyStorePassword(userLogin), null, null, userLogin);
            return CommonUtil.parseMap(securityManager.getCertificateSubjectDN(), ",", "=").get("CN");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new TemplateException(e, TemplateException.Reason.DAMAGED_CERTIFICATE);
        } catch (NullPointerException e) {
            log.error(e.getMessage(), e);
            throw new TemplateException(e, TemplateException.Reason.UNIDENTIFIED_CERTIFICATE);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new TemplateException(e, TemplateException.Reason.UNEXPECTED_EXCEPTION);
        }
    }

    @Override
    public byte[] loadSubmissionTool(String fileName) throws FileNotFoundException {
        byte[] result;
        try {
            String submissionToolPath = ConfigurationUtil.get().get("SubmissionTool.RepositoryPath");
            File submissionToolFile = new File(submissionToolPath + File.separator + fileName);

            result = Files.readAllBytes(submissionToolFile.toPath());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new FileNotFoundException();
        }
        return result;
    }


    @Override
    public long getOstLastModifiedDate(String fileName) {
        String submissionToolPath = ConfigurationUtil.get().get("SubmissionTool.RepositoryPath");
        File submissionToolFile = new File(submissionToolPath + File.separator + fileName);

        return submissionToolFile.lastModified();
    }

    @Override
    public long getOstFileLength(String fileName) {
        String submissionToolPath = ConfigurationUtil.get().get("SubmissionTool.RepositoryPath");
        File submissionToolFile = new File(submissionToolPath + File.separator + fileName);

        return submissionToolFile.length();
    }

    @Override
    public Boolean isSubmissionToolOnlineMode() throws TemplateException {
        return Boolean.parseBoolean(getSubmissionToolSecurityPropertyValue("IsOnlineMode"));
    }

    private byte[] getExcelConverterMetaInfo(SecurityManagerBase securityManager) throws TemplateException {
        ExcelConverterMetaInfo info = new ExcelConverterMetaInfo();
        info.setReturnDefCodes(returnDefinitionLocal.loadDefinitionCodes());

        String vctEmptyLines = propertyLocal.getSystemProperty(PropertyKeys.VCT_EMPTY_LINES);
        info.setVctEmptyLine(vctEmptyLines == null || vctEmptyLines.trim().isEmpty() ? 0 : Integer.parseInt(vctEmptyLines.trim()));
        info.setSheetControl(propertyLocal.getSystemProperty(PropertyKeys.UPLOAD_FILE_EXCEL_SHEET_CONTROL));

        String password = propertyLocal.getSystemProperty(PropertyKeys.DEFAULT_PROTECTION_PASSWORD);
        info.setSheetProtectionPassword((password == null ? "" : password));

        String passwords = propertyLocal.getSystemProperty(PropertyKeys.PROTECTION_PASSWORDS);
        info.setSheetProtectionPasswordByFiType(passwords);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            byte[] jsonInfoBytes = objectMapper.writeValueAsBytes(info);

            return securityManager.encrypt(jsonInfoBytes);

        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new TemplateException(e, TemplateException.Reason.DAMAGED_CERTIFICATE);
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            throw new TemplateException(t, TemplateException.Reason.UNEXPECTED_EXCEPTION);
        }
    }

    private Map<String, byte[]> getMatrices(SecurityManagerBase securityManager) throws TemplateException {
        String matrixPath = propertyLocal.getSystemProperty(PropertyKeys.MATRIX_PATH);
        char separator = (matrixPath.contains("/") ? '/' : '\\');
        if (matrixPath.charAt(matrixPath.length() - 1) != separator) {
            matrixPath += separator;
        }

        File matrixDirectory = new File(matrixPath);
        Map<String, byte[]> matrices = new HashMap<>();

        try {
            if (matrixDirectory.exists()) {
                for (File matrixFile : matrixDirectory.listFiles()) {
                    String fileName = matrixFile.getName();

                    if (fileName.startsWith("~$")) {
                        continue;
                    }

                    if (matrixFile.isFile() && (fileName.toLowerCase().endsWith(".xlsx") || fileName.toLowerCase().endsWith(".xls"))) {

                        try (FileInputStream in = new FileInputStream(matrixFile)) {
                            byte[] fileBytes = in.readAllBytes();
                            matrices.put(fileName, securityManager.encrypt(fileBytes));
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new TemplateException(e, TemplateException.Reason.DAMAGED_CERTIFICATE);
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            throw new TemplateException(t, TemplateException.Reason.UNEXPECTED_EXCEPTION);
        }

        return matrices;
    }


    public byte[] generateTemplateFile(Map<String, byte[]> files, SecurityManagerBase securityManager) throws IOException, CertificateEncodingException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (ZipOutputStream zipOut = new ZipOutputStream(out)) {
            for (Map.Entry<String, byte[]> e : files.entrySet()) {
                ZipEntry zipEntry = new ZipEntry(e.getKey());
                zipOut.putNextEntry(zipEntry);
                zipOut.write(e.getValue());
            }

            ZipEntry zeStamp = new ZipEntry("stamp");
            zipOut.putNextEntry(zeStamp);
            zipOut.write(securityManager.getEncryptKey());
        }

        return out.toByteArray();
    }

    private String getSubmissionToolSecurityPropertyValue(String key) throws TemplateException {
        String result = null;

        Properties securityProperties = new Properties();

        String submissionToolRepositoryPath = ConfigurationUtil.get().get("SubmissionTool.RepositoryPath");
        String appFileName = Optional.ofNullable(ConfigurationUtil.get().get("SubmissionTool.FileName"))
                .filter(name -> !name.isEmpty())
                .orElse("SubmissionTool.jar");
        submissionToolRepositoryPath += submissionToolRepositoryPath.endsWith(File.separator) ? "" : File.separator;

        String ostSecurityPropertiesPath = "jar:file:" +
                submissionToolRepositoryPath +
                appFileName +
                "!/net/fina/st/security/security.properties";

        try {
            URL inputURL = new URL(ostSecurityPropertiesPath);
            JarURLConnection connection = (JarURLConnection) inputURL.openConnection();
            InputStream inputStream = connection.getInputStream();
            securityProperties.load(inputStream);
            result = securityProperties.getProperty(key);
        } catch (MalformedURLException e) {
            log.error(e.getMessage(), e);
            throw new TemplateException(e, TemplateException.Reason.UNEXPECTED_EXCEPTION);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new TemplateException(e, TemplateException.Reason.DAMAGED_CERTIFICATE);
        }

        return result;
    }

    private boolean taxonomyEncryptEnabled() {
        boolean defaultEnabled = true;
        try {
            String enabled = propertyLocal.getSystemProperty(PropertyKeys.OST_TAXONOMY_ENCRYPT_ENABLE);
            if (enabled != null && !enabled.isBlank()) {
                return Boolean.parseBoolean(propertyLocal.getSystemProperty(PropertyKeys.OST_TAXONOMY_ENCRYPT_ENABLE));
            }

            return defaultEnabled;
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }

        return defaultEnabled;
    }

    private SecurityManagerBase getPlainSecurityManager() {
        return new SecurityManagerBase() {
            private X509Certificate serverCertificate = null;

            {
                try {
                    KeyStore serverKeystore = SecurityManagerUtil.loadServerKeyStore();
                    this.serverCertificate = SecurityManagerUtil.loadServerCertificate(serverKeystore);
                } catch (Exception e) {
                    log.error("Failed to load server certificate", e);
                }
            }

            @Override
            public boolean verifySign(byte[] data, byte[] sign) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, CertificateException, NoSuchProviderException {
                return false;
            }

            @Override
            public boolean verifySign(InputStream inputStream, byte[] sign) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, IOException, CertificateException, NoSuchProviderException {
                return false;
            }

            @Override
            public byte[] encrypt(byte[] file) throws Exception {
                return file;
            }

            @Override
            public byte[] decrypt(byte[] data) throws Exception {
                return data;
            }

            @Override
            public InputStream decrypt(InputStream inputStream) throws GeneralSecurityException, IOException {
                return inputStream;
            }

            @Override
            public byte[] getEncryptKey() throws CertificateEncodingException {
                return new byte[0];
            }

            @Override
            public String getCertificateSubjectDN() {
                return "";
            }

            @Override
            public byte[] getServersPublicKey() {
                return serverCertificate != null ? serverCertificate.getPublicKey().getEncoded() : new byte[0];
            }
        };
    }
}
