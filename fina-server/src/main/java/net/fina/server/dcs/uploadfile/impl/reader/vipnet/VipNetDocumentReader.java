package net.fina.server.dcs.uploadfile.impl.reader.vipnet;

import net.fina.common.client.exception.DcsTypeException;
import net.fina.common.client.property.PropertyKeys;
import net.fina.common.server.util.ConfigurationUtil;
import net.fina.server.dcs.uploadfile.entity.UploadFile;
import net.fina.server.dcs.uploadfile.impl.converter.ConverterUtil;
import net.fina.server.dcs.uploadfile.impl.reader.DocumentReader;
import net.fina.server.dcs.uploadfile.impl.reader.excel.ExcelDocumentReaderFactory;
import net.fina.server.dcs.uploadfile.impl.reader.xml.XmlDocumentReader;
import net.fina.server.returns.xml.Return;
import org.jboss.logging.Logger;
import ru.infotecs.cms.input.CMSDataInputStream;
import ru.infotecs.cms.input.CMSSignedDataInputStream;
import ru.infotecs.cms.input.CMSSigner;
import ru.infotecs.cms.tools.SystemPKIXParameters;
import ru.infotecs.crypto.ViPNetProvider;
import sun.security.x509.X500Name;

import java.io.*;
import java.lang.reflect.Method;
import java.security.Security;
import java.security.cert.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VipNetDocumentReader extends DocumentReader {
    private static final String CERTIFICATE_TYPE = "X.509";
    private final Logger log = Logger.getLogger(getClass());
    private String rootCAPath;
    private String userLogin;
    private String signerProperty = "CommonName";

    public VipNetDocumentReader(Map<String, Object> properties) throws DcsTypeException {
        super(properties);

        Object rootCAPath = properties.get("dcs.security.sign.rootCA");
        if (rootCAPath != null) {
            this.rootCAPath = rootCAPath.toString();
        }

        this.userLogin = properties.get("dcs.user.login").toString();

        Object signerPropertyTmp = properties.get(PropertyKeys.FILE_SIGNATURE_CHECKER_SIGNER_PROPERTY);
        if (signerPropertyTmp != null && !((String) signerPropertyTmp).trim().isEmpty()) {
            signerProperty = ((String) signerPropertyTmp).trim();
        }

        log.info("VipNet document reader has been created");
    }

    /**
     * @return This list of xml files that have to be imported
     * @throws DcsTypeException if any error/exception occurs
     */
    @Override
    public List<Return> getReturns() throws DcsTypeException {
        String fileName = file.getFileName();
        List<Return> result = new ArrayList<>();

        try (InputStream inputStream = new ByteArrayInputStream(file.getUploadedFile()); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            boolean valid = verify(inputStream, out);

            if (!valid) {
                throw new DcsTypeException(DcsTypeException.Type.SECURITY_INVALID_SIGN);
            }

            byte[] fileContent = out.toByteArray();

            String contentFileName = fileName.replace(".sig", "");

            DocumentReader documentReader = null;
            if (ConverterUtil.isExtension(contentFileName, "xls") || ConverterUtil.isExtension(contentFileName, "xlsx")) {
                properties.put("dcs.file.extension", contentFileName.substring(contentFileName.lastIndexOf('.')));
                documentReader = ExcelDocumentReaderFactory.getInstance().createDocumentReader(properties);
            } else if (ConverterUtil.isExtension(contentFileName, "xml")) {
                documentReader = new XmlDocumentReader(properties);
            }
            if (documentReader != null) {
                UploadFile uploadFile = new UploadFile();
                uploadFile.setUploadedFile(fileContent);
                uploadFile.setFileName(contentFileName);

                documentReader.setFile(uploadFile);
                result.addAll(documentReader.getReturns());
            }

        } catch (DcsTypeException e) {
            log.error(e.getMessage(), e);
            throw e;
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            throw new DcsTypeException(DcsTypeException.Type.SECURITY_INVALID_SIGN, t.getMessage());
        }

        return result;
    }

    boolean verify(InputStream inputStream, ByteArrayOutputStream out) throws Exception {

        System.setProperty("jcrypto.home", ConfigurationUtil.get().get("DCS_FILE_SIGN_JCRYPTO_HOME"));

        ViPNetProvider provider = new ViPNetProvider();

        // регистрируем провайдер
        Security.insertProviderAt(provider, 1);

        // получаем ссылку на класс CMSSignedDataInputStream
        CMSSignedDataInputStream signedStream = new CMSSignedDataInputStream(inputStream);

        // инициализируем signedStream собственными параметрами PKIX
        PKIXParameters pkixParameters = new SystemPKIXParameters();
        signedStream.init(pkixParameters);

        // открываем поток, содержащий байты подписанного сообщения
        try (CMSDataInputStream dataStream = (CMSDataInputStream) signedStream.getInnerStream()) {
            // считываем сообщение
            byte[] buffer = new byte[1024];
            for (int len = dataStream.read(buffer); len != -1; len = dataStream.read(buffer)) {
                out.write(buffer, 0, len);
            }

            // закрываем поток
            // получаем список подписавших текущее CMS-сообщение
            List<CMSSigner> signers = signedStream.getSigners();

            X509Certificate rootCAX509Certificate = readRootCA();

            for (CMSSigner signer : signers) {

                if (signer.getCertificate() == null) {
                    throw new DcsTypeException(DcsTypeException.Type.INVALID_DIGITAL_SIGNATURE, new String[]{"Sig file doesn't have public certificate."});
                }

                X509Certificate cert = (X509Certificate) signer.getCertificate();

                //Check CRL
                this.verifyCertificateCRLs(cert);

                X500Name x500Name = X500Name.asX500Name(cert.getSubjectX500Principal());
                Method method = x500Name.getClass().getDeclaredMethod("get" + signerProperty);

                String singerResult = (String) method.invoke(x500Name);
                String signerObject = singerResult != null ? singerResult.trim().toLowerCase() : null;

                if (signerObject == null || !signerObject.equals(this.userLogin)) {
                    throw new DcsTypeException(DcsTypeException.Type.INVALID_DIGITAL_SIGNATURE, new String[]{"Invalid user certificate (Certificate " + signerProperty + "=" + signerObject + " doesn't match " + this.userLogin + ")"});
                }
                cert.verify(rootCAX509Certificate.getPublicKey());

                if (!signer.verify()) {
                    return false;
                }
            }
        }
        return true;
    }

    private X509Certificate readRootCA() throws CertificateException, IOException {
        CertificateFactory fact = CertificateFactory.getInstance(CERTIFICATE_TYPE);
        try (FileInputStream is = new FileInputStream(rootCAPath)) {
            return (X509Certificate) fact.generateCertificate(is);
        }
    }

    private void verifyCertificateCRLs(X509Certificate cert) {

        String crlURL = ConfigurationUtil.get().get("DCS_FILE_SIGN_CRL_FILE");

        try (InputStream crlStream = new BufferedInputStream(new FileInputStream(crlURL))) {

            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            X509CRL x509CRL = (X509CRL) cf.generateCRL(crlStream);

            if (x509CRL.isRevoked(cert)) {
                throw new DcsTypeException(DcsTypeException.Type.INVALID_DIGITAL_SIGNATURE, new String[]{"The certificate is revoked by CRL: " + x509CRL});
            }
        } catch (IOException | CertificateException | CRLException e) {
            log.error(e.getMessage(), e);
            throw new DcsTypeException(DcsTypeException.Type.INVALID_DIGITAL_SIGNATURE, new String[]{"Can not verify CRL for certificate: " + cert.getSubjectX500Principal()});
        }
    }
}
