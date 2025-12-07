package net.fina.server.dcs.uploadfile.impl.reader.fina;

import net.fina.common.client.exception.DcsTypeException;
import net.fina.common.server.util.ConfigurationUtil;
import net.fina.server.dcs.uploadfile.impl.converter.ConverterUtil;
import net.fina.server.dcs.uploadfile.impl.reader.DocumentReader;
import net.fina.server.dcs.uploadfile.impl.util.FinaFileUtil;
import net.fina.server.returns.xml.Header;
import net.fina.server.returns.xml.Return;
import net.fina.server.st.crypto.CertificateMode;
import net.fina.server.st.crypto.SecurityManagerBase;
import net.fina.server.st.crypto.SecurityManagerFactory;
import net.fina.server.st.crypto.SecurityManagerUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.NoSuchProviderException;
import java.util.*;

public class FinADocumentReader extends DocumentReader {

    public FinADocumentReader(Map<String, Object> propeties) {
        super(propeties);
    }

    @Override
    public List<Return> getReturns() throws DcsTypeException {
        List<Return> returns = new ArrayList<>();
        try {
            Map<String, byte[]> files = ConverterUtil.extractZip(file.getUploadedFile());

            byte[] versionFileContent = files.get(properties.get("versionFileName"));
            FinaFileUtil.checkVersion(versionFileContent, properties);

            boolean activateSign = ConverterUtil.isActivateSign(properties);
            boolean activateEncrypt = ConverterUtil.isActivateEncrypt(properties);

            SecurityManagerBase securityManager = null;
            if (activateSign | activateEncrypt) {
                securityManager = initializeSecurityManager(files);
            }

            for (Map.Entry<String, byte[]> entry : files.entrySet()) {
                String fileName = entry.getKey();
                byte[] content = entry.getValue();

                if (ConverterUtil.isExtension(fileName, "xml")) {
                    if (activateEncrypt) {
                        content = decrypt(securityManager, content);
                    }

                    if (activateSign) {
                        byte[] sign = files.get(fileName + properties.get("dcs.security.sign.extension"));
                        verifySignature(securityManager, content, sign);
                    }

                    Return r = ConverterUtil.xmlToReturnV2(content);
                    returns.add(r);
                }
            }

            if (returns.isEmpty()) {
                throw new DcsTypeException(DcsTypeException.Type.EMPTY_ZIP_ERROR);
            }

            Header header = (Header) properties.get("dcs.xml.header");
            checkFi(returns, header);
            checkPeriod(returns, header);
            checkDuplicatedReturns(returns);

        } catch (DcsTypeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new DcsTypeException(ex, DcsTypeException.Type.GENERAL_ERROR);
        }

        return returns;
    }

    private SecurityManagerBase initializeSecurityManager(Map<String, byte[]> files) throws DcsTypeException {
        try {
            CertificateMode certificateMode = SecurityManagerUtil.getCertificateMode();
            byte[] keyStoreBytes = (byte[]) properties.get("dcs.security.certificate");
            ConfigurationUtil util = ConfigurationUtil.get();
            String keyInfoProperty = util.get("KeyStoreInfo");
            char[] password = null;

            if ((keyInfoProperty != null) && (!keyInfoProperty.trim().isEmpty()) && certificateMode.equals(CertificateMode.LEGACY)) {
                File keyInfoFile = new File(keyInfoProperty);
                if (keyInfoFile.exists()) {
                    Properties keyProperties = new Properties();
                    try (InputStream in = new FileInputStream(keyInfoFile)) {
                        keyProperties.load(in);
                        password = keyProperties.getProperty(properties.get("dcs.user.login").toString()).trim().toCharArray();
                    }
                }
            }

            return SecurityManagerFactory.get(
                    keyStoreBytes,
                    util.get("KeyStoreAlias"),
                    password,
                    files.get("stamp"),
                    files.get("clientCertificate"),
                    (String) properties.get("dcs.user.login")
            );
        } catch (CertificateException | NoSuchProviderException ex) {
            throw new DcsTypeException(ex, DcsTypeException.Type.INVALID_DIGITAL_SIGNATURE);
        } catch (Exception ex) {
            throw new DcsTypeException(ex, DcsTypeException.Type.SECURITY_INVALID_ENCRYPT);
        }
    }

    private byte[] decrypt(SecurityManagerBase securityManager, byte[] content) throws DcsTypeException {
        try {
            return securityManager.decrypt(content);
        } catch (Exception ex) {
            throw new DcsTypeException(ex, DcsTypeException.Type.SECURITY_INVALID_ENCRYPT);
        }
    }

    private void verifySignature(SecurityManagerBase securityManager, byte[] content, byte[] sign) throws DcsTypeException {
        try {
            if (!securityManager.verifySign(content, sign)) {
                throw new DcsTypeException(DcsTypeException.Type.SECURITY_INVALID_SIGN);
            }
        } catch (DcsTypeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new DcsTypeException(ex, DcsTypeException.Type.INVALID_DIGITAL_SIGNATURE);
        }
    }

    private void checkFi(List<Return> returns, Header header) {
        List<String> invalidFiCodes = new ArrayList<>();
        for (Return r : returns) {
            if (!r.getHeader().getBankCode().trim().equalsIgnoreCase(header.getBankCode().trim())) {
                invalidFiCodes.add(r.getHeader().getBankCode());
            }
        }
        if (!invalidFiCodes.isEmpty()) {
            String message = String.format(
                    "${net.fina.dcs.file.returns.invalid.fi.1} %s ${net.fina.dcs.file.returns.invalid.fi.2} %s ${net.fina.dcs.file.returns.invalid.fi.3} %s",
                    file.getFileName(),
                    header.getBankCode(),
                    invalidFiCodes.toString()
            );
            throw DcsTypeException.create(DcsTypeException.Type.SUBMITED_FILE_RETURNS_INVALID_FI, message);
        }
    }

    /**
     * Check returns and file period
     *
     * @param returns
     */
    private void checkPeriod(List<Return> returns, Header header) {
        List<String> invalidPeriodReturns = new ArrayList<>();
        for (Return r : returns) {
            if ((!r.getHeader().getPeriodFrom().trim().equalsIgnoreCase(header.getPeriodFrom().trim())) ||
                    (!r.getHeader().getPeriodEnd().trim().equalsIgnoreCase(header.getPeriodEnd().trim()))) {
                invalidPeriodReturns.add(r.getHeader().getReturnCode() + "[" + r.getHeader().getPeriodFrom() + " - " + r.getHeader().getPeriodEnd() + "]");
            }
        }
        if (!invalidPeriodReturns.isEmpty()) {
            String message = String.format(
                    "${net.fina.dcs.file.returns.invalid.period.1} %s ${net.fina.dcs.file.returns.invalid.period.2} %s ${net.fina.dcs.file.returns.invalid.period.3} $s",
                    file.getFileName(),
                    header.getPeriodFrom() + " - " + header.getPeriodEnd(),
                    invalidPeriodReturns
            );
            throw DcsTypeException.create(DcsTypeException.Type.SUBMITED_FILE_RETURNS_INVALID_PERIOD, message);
        }
    }

    private void checkDuplicatedReturns(List<Return> returns) {
        Map<String, Return> codeReturnMap = new HashMap<>();
        List<String> duplicatedReturns = new ArrayList<>();
        for (Return r : returns) {
            if (codeReturnMap.get(r.getHeader().getReturnCode().trim()) != null) {
                duplicatedReturns.add(r.getHeader().getReturnCode());
            }
            codeReturnMap.put(r.getHeader().getReturnCode().trim(), r);
        }
        if (!duplicatedReturns.isEmpty()) {
            String message = String.format(
                    "${net.fina.dcs.file.returns.duplicate.1} %s ${net.fina.dcs.file.returns.duplicate.2} %s",
                    file.getFileName(),
                    duplicatedReturns
            );
            throw DcsTypeException.create(DcsTypeException.Type.SUBMITED_FILE_DUPLICATE_RETURNS, message);
        }
    }
}