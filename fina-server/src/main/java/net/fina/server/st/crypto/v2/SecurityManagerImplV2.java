package net.fina.server.st.crypto.v2;

import net.fina.common.client.exception.DcsTypeException;
import net.fina.common.server.util.ConfigurationUtil;
import net.fina.server.st.crypto.CrlChecker;
import net.fina.server.st.crypto.CryptoManagerBase;
import net.fina.server.st.crypto.SecurityManagerBase;
import net.fina.server.st.crypto.SecurityManagerUtil;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.jboss.logging.Logger;

import java.io.*;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class SecurityManagerImplV2 extends SecurityManagerBase {
    private final Logger log = Logger.getLogger(getClass().getName());

    private final CryptoManagerBase cm;
    private final byte[] outEncryptKey;
    private final X509Certificate clientCertificate;
    private final X509Certificate rootCaCert;
    private final X509Certificate serverCertificate;

    public SecurityManagerImplV2(byte[] encodedKey, byte[] clientCertificateBytes) throws Exception {
        if (clientCertificateBytes != null && clientCertificateBytes.length > 0) {
            this.clientCertificate = loadClientCertificate(clientCertificateBytes);
            clientCertificate.checkValidity();
            CrlChecker.validateCertificateInCrl(clientCertificate);

        } else {
            this.clientCertificate = null;
        }
        rootCaCert = readRootCA();

        KeyStore serverKeystore = SecurityManagerUtil.loadServerKeyStore();
        this.serverCertificate = SecurityManagerUtil.loadServerCertificate(serverKeystore);
        assert clientCertificate != null;

        if (encodedKey == null || encodedKey.length == 0) {
            this.cm = new CryptoManagerV2(clientCertificate);
            cm.makeKey();
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                cm.saveKey(out, clientCertificate.getPublicKey().getEncoded());
                outEncryptKey = out.toByteArray();
            }
        } else {

            this.cm = new CryptoManagerV2(serverCertificate);

            PrivateKey serverPrivateKey = SecurityManagerUtil.loadServerPrivateKey(serverKeystore);
            this.outEncryptKey = encodedKey;
            try {
                cm.loadKey(encodedKey, serverPrivateKey);
            } catch (Exception ex) {
                throw new GeneralSecurityException(ex);
            }
        }
    }

    @Override
    public boolean verifySign(byte[] data, byte[] sign) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, CertificateException, NoSuchProviderException {
        java.security.Signature signature = Signature.getInstance(clientCertificate.getSigAlgName());
        signature.initVerify(clientCertificate);
        signature.update(data);
        boolean verified = signature.verify(sign);

        //verify client certificate is signed by root ca
        if (verified) {
            clientCertificate.verify(rootCaCert.getPublicKey());
        }

        return verified;
    }

    @Override
    public boolean verifySign(InputStream inputStream, byte[] sign) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, IOException, CertificateException, NoSuchProviderException {
        java.security.Signature signature = Signature.getInstance(clientCertificate.getSigAlgName());
        signature.initVerify(clientCertificate);
        byte[] buffer = new byte[4096];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            signature.update(buffer, 0, len);
        }

        boolean verified = signature.verify(sign);

        //verify client certificate is signed by root ca
        if (verified) {
            clientCertificate.verify(rootCaCert.getPublicKey());
        }
        return verified;
    }

    @Override
    //Encrypt File
    public byte[] encrypt(byte[] file) throws Exception {
        try (ByteArrayOutputStream outFile = new ByteArrayOutputStream()) {
            cm.encrypt(file, outFile);
            return outFile.toByteArray();
        }
    }

    public byte[] decrypt(byte[] data) throws Exception {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            cm.decrypt(data, out);
            return out.toByteArray();
        }
    }

    @Override
    public InputStream decrypt(InputStream inputStream) throws GeneralSecurityException, IOException {
        return cm.decrypt(inputStream);
    }

    @Override
    public byte[] getEncryptKey() throws CertificateEncodingException {
        return this.outEncryptKey;
    }

    @Override
    public String getCertificateSubjectDN() {
        try {
            X500Name x500name = new JcaX509CertificateHolder(clientCertificate).getSubject();
            RDN cn = x500name.getRDNs(BCStyle.CN)[0];
            return IETFUtils.valueToString(cn.getFirst().getValue());
        } catch (Throwable ignore) {
        }

        return clientCertificate.getSubjectDN().getName();
    }

    public byte[] getServersPublicKey() {
        return serverCertificate.getPublicKey().getEncoded();
    }


    private X509Certificate readRootCA() throws DcsTypeException {
        try {
            String fileSignRootCaPath = ConfigurationUtil.get().get("DCS_FILE_SIGN_ROOT_CA_FILE");

            CertificateFactory fact = CertificateFactory.getInstance(CERTIFICATE_TYPE);
            try (FileInputStream is = new FileInputStream(fileSignRootCaPath)) {
                return (X509Certificate) fact.generateCertificate(is);
            }
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            throw new DcsTypeException(DcsTypeException.Type.INVALID_DIGITAL_SIGNATURE, new String[]{"Cannot load rootCa Certificate"});
        }
    }


    private X509Certificate loadClientCertificate(byte[] clientCertificateBytes) throws Exception {

        try (InputStream fis = new ByteArrayInputStream(clientCertificateBytes)) {
            CertificateFactory certFactory = CertificateFactory.getInstance(CERTIFICATE_TYPE);
            return (X509Certificate) certFactory.generateCertificate(fis);
        }
    }

}
