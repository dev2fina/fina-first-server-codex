package net.fina.server.reg.impl;

import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import net.fina.common.client.exception.DcsTypeException;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.server.util.ConfigurationUtil;
import net.fina.server.dcs.uploadfile.api.UploadFileStreamable;
import net.fina.server.dcs.uploadfile.impl.converter.ConverterUtil;
import net.fina.server.dcs.uploadfile.impl.reader.excel.MatrixOptionBase;
import net.fina.server.dcs.uploadfile.impl.util.FinaFileUtil;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.jcr.impl.FileContentManagementSession;
import net.fina.server.reg.api.RegProcessor;
import net.fina.server.reg.model.RegProcessConfig;
import net.fina.server.reg.qualifier.RegFinaXmlProcessor;
import net.fina.server.reg.util.RegXmlFileXsdHelper;
import net.fina.server.reg.util.processor.RegFileProcessorUtilBase;
import net.fina.server.st.crypto.CertificateMode;
import net.fina.server.st.crypto.SecurityManagerBase;
import net.fina.server.st.crypto.SecurityManagerFactory;
import net.fina.server.st.crypto.SecurityManagerUtil;
import net.fina.server.util.TempFileUtil;
import org.apache.commons.io.FileUtils;
import org.jboss.logging.Logger;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.nio.file.Files;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.sql.Connection;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.apache.xerces.jaxp.JAXPConstants.*;

//import static com.sun.org.apache.xerces.internal.jaxp.JAXPConstants.*;

@Stateless
@Local(RegProcessor.class)
@RegFinaXmlProcessor
@Interceptors(RecordingAuditor.class)
public class RegFinaFileProcessor implements RegProcessor {
    @Inject
    private Logger log;

    @Inject
    private FileContentManagementSession fileContentManagementSession;


    private byte[] readContent(ZipInputStream zis) throws Exception {
        int n;
        byte[] buf = new byte[1024];
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            while ((n = zis.read(buf, 0, 1024)) > -1) {
                out.write(buf, 0, n);
            }

            return out.toByteArray();
        }
    }

    public ZipInputStream getXmlFileStream(InputStream in) throws Exception {
        ZipInputStream zis = new ZipInputStream(in);
        ZipEntry ze;

        while ((ze = zis.getNextEntry()) != null) {
            if (!ze.isDirectory()) {
                if (ConverterUtil.isExtension(ze.getName(), "xml")) {
                    return zis;
                }
            }
        }

        return null;
    }

    @Override
    public List<String> process(RegProcessConfig config,
                                Connection conn,
                                RegFileProcessorUtilBase regFileProcessorUtil) {
        DefaultHandler xmlHandler = new RegXmlHandler(config, config.getAcceptedDefinitionCodes(), conn, regFileProcessorUtil);
        File tmpFile = null;
        try {
            tmpFile = TempFileUtil.createTempFile(config.getFileName());
            final File finalTmpFile = tmpFile;

            fileContentManagementSession.loadUploadFileStreamInto(config.getUploadFile(), new UploadFileStreamable() {
                @Override
                public void readFileStream(InputStream inputStream) throws Exception {
                    FileUtils.copyInputStreamToFile(inputStream, finalTmpFile);
                }
            });

            Map<String, Object> properties = config.getProperties();
            byte[] versionFileContent = null;
            byte[] stamp = null;
            byte[] clientCertificate = null;
            byte[] sign = null;
            byte[] xmlContent = null;

            try (InputStream fileInputStream = new FileInputStream(finalTmpFile);
                 ZipInputStream zis = new ZipInputStream(fileInputStream)) {

                ZipEntry ze;
                while ((ze = zis.getNextEntry()) != null) {
                    if (!ze.isDirectory()) {
                        if (ze.getName().equals(properties.get("versionFileName"))) {
                            versionFileContent = readContent(zis);
                        } else if (ze.getName().equals("stamp")) {
                            stamp = readContent(zis);
                            stamp = stamp.length == 0 ? null : stamp;
                        } else if (ze.getName().endsWith((String) properties.get("dcs.security.sign.extension"))) {
                            sign = readContent(zis);
                        } else if (ze.getName().equals("clientCertificate")) {
                            clientCertificate = readContent(zis);
                            clientCertificate = clientCertificate.length == 0 ? null : clientCertificate;
                        } else if (ze.getName().endsWith(".xml") || ze.getName().endsWith(".reg.xml")) {
                            xmlContent = readContent(zis);
                        }
                    }
                }
            }

            if (xmlContent == null) {
                throw new DcsTypeException(DcsTypeException.Type.INVALID_XML_STRUCTURE);
            }

            FinaFileUtil.checkVersion(versionFileContent, properties);

            MatrixOptionBase option = (MatrixOptionBase) properties.get("dcs.primary.matrix.option");
            boolean activateSign = option.isDigitalSignatureCheckEnabled();
            boolean activateEncrypt = ConverterUtil.isActivateEncrypt(properties);

            SecurityManagerBase securityManager = null;
            if (activateSign || activateEncrypt) {
                securityManager = initSecurityManager(properties, stamp, clientCertificate);
            }

            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(true);
            factory.setNamespaceAware(true);

            SAXParser saxParser = factory.newSAXParser();
            saxParser.setProperty(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
            saxParser.setProperty(JAXP_SCHEMA_SOURCE, new RegXmlFileXsdHelper().getXsdFile());

            InputStream xmlInputStream = new ByteArrayInputStream(xmlContent);

            if (activateSign) {
                verifySignature(securityManager, xmlInputStream, sign, activateEncrypt);
                xmlInputStream = new ByteArrayInputStream(xmlContent);
            }

            InputStream inputStream = decryptIfNeeded(securityManager, xmlInputStream, activateEncrypt);
            saxParser.parse(inputStream, xmlHandler);

        } catch (DcsTypeException ex) {
            log.error(ex.getMessage(), ex);
            if (!config.getInputValidatorMap().isEmpty()) {
                return new ArrayList<>();
            }
            if (ex.getReasonsList() != null && !ex.getReasonsList().isEmpty()) {
                return ex.getReasonsList();
            }
            throw new RegFileProcessException(ex);
        } catch (FinATypeException ex) {
            log.error(ex.getMessage(), ex);
            if (!config.getInputValidatorMap().isEmpty()) {
                return new ArrayList<>();
            }
            return Collections.singletonList(ex.getMessage());
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            if (!config.getInputValidatorMap().isEmpty()) {
                return new ArrayList<>();
            }
            return Collections.singletonList("${" + FinATypeException.Type.GENERAL_ERROR.getCode() + "}");
        } finally {
            if (tmpFile != null) {
                tmpFile.delete();
            }
        }

        return new ArrayList<>();
    }

    private SecurityManagerBase initSecurityManager(Map<String, Object> properties,
                                                    byte[] stamp,
                                                    byte[] clientCertificate) throws DcsTypeException {
        try {
            byte[] keyStoreBytes = (byte[]) properties.get("dcs.security.certificate");
            ConfigurationUtil util = ConfigurationUtil.get();
            String keyInfoProperty = util.get("KeyStoreInfo");
            char[] password = null;
            CertificateMode certificateMode = SecurityManagerUtil.getCertificateMode();

            if ((keyInfoProperty != null) && (!keyInfoProperty.trim().isEmpty()) && certificateMode.equals(CertificateMode.LEGACY)) {
                File keyInfoFile = new File(keyInfoProperty);
                if (keyInfoFile.exists()) {
                    Properties keyProperties = new Properties();
                    try (InputStream in = Files.newInputStream(keyInfoFile.toPath())) {
                        keyProperties.load(in);
                        if (keyProperties.getProperty(properties.get("dcs.user.login").toString()) == null) {
                            throw new FinATypeException("Invalid User [" + properties.get("dcs.user.login") + "]");
                        }
                        password = keyProperties.getProperty(properties.get("dcs.user.login").toString()).trim().toCharArray();
                    }
                }
            }

            return SecurityManagerFactory.get(keyStoreBytes, util.get("KeyStoreAlias"),
                    password, stamp, clientCertificate,
                    (String) properties.get("dcs.user.login"));
        } catch (CertificateException | NoSuchProviderException ex) {
            log.error("Certificate validation failed: " + ex.getMessage(), ex);
            throw new DcsTypeException(DcsTypeException.Type.INVALID_DIGITAL_SIGNATURE);
        } catch (Exception ex) {
            log.error("Security manager initialization failed: " + ex.getMessage(), ex);
            throw new DcsTypeException(DcsTypeException.Type.SECURITY_INVALID_ENCRYPT);
        }
    }

    private void verifySignature(SecurityManagerBase securityManager,
                                 InputStream xmlInputStream,
                                 byte[] sign,
                                 boolean activateEncrypt) throws DcsTypeException {
        try {
            InputStream inputStream = activateEncrypt ? securityManager.decrypt(xmlInputStream) : xmlInputStream;

            if (!securityManager.verifySign(inputStream, sign)) {
                throw new DcsTypeException(DcsTypeException.Type.INVALID_DIGITAL_SIGNATURE);
            }
        } catch (Exception ex) {
            log.error("Signature verification failed: " + ex.getMessage(), ex);
            throw new DcsTypeException(new DcsTypeException(DcsTypeException.Type.INVALID_DIGITAL_SIGNATURE));
        }
    }

    private InputStream decryptIfNeeded(SecurityManagerBase securityManager,
                                        InputStream xmlInputStream,
                                        boolean activateEncrypt) throws DcsTypeException {
        if (!activateEncrypt) {
            return xmlInputStream;
        }

        try {
            return securityManager.decrypt(xmlInputStream);
        } catch (Exception ex) {
            log.error("Decryption failed: " + ex.getMessage(), ex);
            throw new DcsTypeException(DcsTypeException.Type.SECURITY_INVALID_ENCRYPT);
        }
    }
}
