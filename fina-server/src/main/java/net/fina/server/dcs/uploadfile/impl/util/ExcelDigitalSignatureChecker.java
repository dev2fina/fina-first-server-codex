package net.fina.server.dcs.uploadfile.impl.util;

import net.fina.common.client.exception.DcsTypeException;
import net.fina.server.dcs.uploadfile.impl.reader.excel.MatrixOptionBase;
import net.fina.server.st.crypto.CrlChecker;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.crypt.dsig.SignatureConfig;
import org.apache.poi.poifs.crypt.dsig.SignatureInfo;
import org.apache.poi.poifs.crypt.dsig.SignaturePart;
import org.jboss.logging.Logger;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExcelDigitalSignatureChecker {

    private static final String CERTIFICATE_TYPE = "X.509";
    private final String CN_DELIMITER = "/";
    private Logger log = Logger.getLogger(getClass());
    private boolean checkDigitalSignature;
    private String rootCAPath;
    private String userLogin;

    public ExcelDigitalSignatureChecker(Map<String, Object> properties) throws DcsTypeException {
        MatrixOptionBase option = (MatrixOptionBase) properties.get("dcs.primary.matrix.option");
        checkDigitalSignature = option.isDigitalSignatureCheckEnabled();

        Object rootCAPath = properties.get("dcs.security.sign.rootCA");
        if (rootCAPath != null) {
            this.rootCAPath = rootCAPath.toString();
        }

        this.userLogin = properties.get("dcs.user.login").toString();
    }

    private X509Certificate readRootCA() throws DcsTypeException {
        try {

            CertificateFactory fact = CertificateFactory.getInstance(CERTIFICATE_TYPE);
            try (FileInputStream is = new FileInputStream(rootCAPath)) {
                return (X509Certificate) fact.generateCertificate(is);
            }
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            throw new DcsTypeException(DcsTypeException.Type.INVALID_DIGITAL_SIGNATURE, new String[]{"Cannot load rootCa Certificate"});
        }
    }

    public void checkDigitalSignature(byte[] excelFile) throws DcsTypeException {
        if (checkDigitalSignature) {

            try {

                try (InputStream is = new ByteArrayInputStream(excelFile); OPCPackage opcPackage = OPCPackage.open(is)) {
                    checkDigitalSignature(opcPackage);
                }
            } catch (DcsTypeException e) {
                log.error(e.getMessage(), e);
                throw e;
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
                throw new DcsTypeException(DcsTypeException.Type.INVALID_DIGITAL_SIGNATURE, new String[]{"Invalid digital signature"});
            }
        }
    }

    public void checkDigitalSignature(OPCPackage opcPackage) throws DcsTypeException {
        if (checkDigitalSignature) {
            X509Certificate rootCAX509Certificate = readRootCA();
            SignatureConfig config = new SignatureConfig();
//            config.setOpcPackage(opcPackage);
//            https://santuario.apache.org/faq.html#faq-4.SecureValidation
//            starting with xmlsec 2.3.0 disabling secure validation was necessary because of limitations
//              on the amount of processed internal references (max. 30)
            config.setSecureValidation(false);

            SignatureInfo info = new SignatureInfo();
            info.setSignatureConfig(config);
            info.setOpcPackage(opcPackage);

            List<X509Certificate> certificates = new ArrayList<>();

            for (SignaturePart sp : info.getSignatureParts()) {
                if (sp.validate()) {
                    X509Certificate signer = sp.getSigner();
                    //validate CLR
                    CrlChecker.validateCertificateInCrl(signer);

                    try {
                        String signerCN = getCommonName(signer).trim().toLowerCase();
                        // when cn contains fi type, fi code and user identity
                        if (signerCN.contains(CN_DELIMITER)) {
                            String[] splitted = signerCN.split(CN_DELIMITER);
                            if (splitted.length != 3) {
                                throw new DcsTypeException("Invalid certificate cn attribute. must contain delimited code type and cn");
                            }
                            signerCN = splitted[2].trim();
                        }
                        if (!signerCN.equals(this.userLogin)) {
                            throw new DcsTypeException(DcsTypeException.Type.INVALID_DIGITAL_SIGNATURE, new String[]{"Invalid user certificate (Certificate CN=" + signerCN + " doesn't match " + this.userLogin + ")"});
                        }
                        signer.verify(rootCAX509Certificate.getPublicKey());
                    } catch (DcsTypeException dc) {
                        throw dc;
                    } catch (Throwable e) {
                        log.error(e.getMessage(), e);
                        throw new DcsTypeException(DcsTypeException.Type.INVALID_DIGITAL_SIGNATURE, new String[]{"Invalid digital signature"});
                    }

                    certificates.add(signer);
                }
            }

            int certificatesSize = certificates.size();

            if (certificates.isEmpty()) {
                log.error("Invalid number of signer certificates: " + certificatesSize);
                throw new DcsTypeException(DcsTypeException.Type.INVALID_DIGITAL_SIGNATURE, new String[]{"Is not digitally signed"});
            }
        }
    }

    private String getCommonName(X509Certificate signerCertificate) throws InvalidNameException {
        LdapName ldapName = new LdapName(signerCertificate.getSubjectX500Principal().getName());

        // Iterate through the RDNs (Relative Distinguished Names)
        for (int i = 0; i < ldapName.size(); i++) {
            Rdn rdn = ldapName.getRdn(i);
            // Check for CN attribute
            if ("CN".equals(rdn.getType())) {
                return (String) rdn.getValue();
            }
        }
        throw new InvalidNameException("Invalid certificate : cannot extract common name");
    }


    public boolean isCheckDigitalSignature() {
        return checkDigitalSignature;
    }
}
