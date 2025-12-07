/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.fina.server.st.crypto.v1;

import net.fina.server.st.crypto.CrlChecker;
import net.fina.server.st.crypto.CryptoManagerBase;
import net.fina.server.st.crypto.SecurityManagerBase;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.X509Certificate;

/**
 * @author nick
 */
public class SecurityManagerRSA extends SecurityManagerBase {

    private static final String KEY_STORE_TYPE = "PKCS12";
    private static final String CERTIFICATE_TYPE = "X.509";
    //Encrypt
    private final CryptoManagerBase cm;
    private final byte[] outEncryptKey;
    private KeyStore keyStore;
    private X509Certificate certificate;
    private PrivateKey privateKey;

    public SecurityManagerRSA(byte[] keyStoreBytes, char[] password, String alias) throws Exception {
        try (ByteArrayInputStream in = new ByteArrayInputStream(keyStoreBytes)) {
            keyStore = KeyStore.getInstance(KEY_STORE_TYPE);
            keyStore.load(in, password);
        }
        init(password, alias);

        cm = new CryptoManagerRSA(certificate.getPublicKey().getAlgorithm());
        cm.makeKey();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            cm.saveKey(out, certificate.getPublicKey().getEncoded());
            outEncryptKey = out.toByteArray();
        }
    }

    public SecurityManagerRSA(byte[] keyStoreBytes, String alias, char[] password, byte[] encodedKey) throws Exception {
        try (ByteArrayInputStream in = new ByteArrayInputStream(keyStoreBytes)) {
            keyStore = KeyStore.getInstance(KEY_STORE_TYPE);
            keyStore.load(in, password);

            if (keyStore.containsAlias(alias)) {
                certificate = (X509Certificate) keyStore.getCertificate(alias);
            }
            certificate.checkValidity();
            CrlChecker.validateCertificateInCrl(certificate);

            KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(password);
            KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(alias, protParam);

            privateKey = pkEntry.getPrivateKey();
        }

        cm = new CryptoManagerRSA(certificate.getPublicKey().getAlgorithm());
        if (encodedKey == null || encodedKey.length == 0) {
            cm.makeKey();
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                cm.saveKey(out, certificate.getPublicKey().getEncoded());
                outEncryptKey = out.toByteArray();
            }
        } else {
            this.outEncryptKey = encodedKey;
            try {
                cm.loadKey(encodedKey, privateKey);
            } catch (Exception ex) {
                throw new GeneralSecurityException();
            }
        }
    }

    private void init(char[] password, String alias) throws Exception {
        if (keyStore.containsAlias(alias)) {
            certificate = (X509Certificate) keyStore.getCertificate(alias);
        }

        certificate.checkValidity();
        CrlChecker.validateCertificateInCrl(certificate);

        KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(password);
        KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(alias, protParam);

        privateKey = pkEntry.getPrivateKey();
    }


    public String getCertificateSubjectDN() {
        return certificate.getSubjectDN().getName();
    }

    public byte[] getEncryptKey() {
        return this.outEncryptKey;
    }

    public boolean verifySign(byte[] data, byte[] sign) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        java.security.Signature signature = Signature.getInstance(certificate.getSigAlgName());
        signature.initVerify(certificate);
        signature.update(data);
        return signature.verify(sign);
    }

    @Override
    public boolean verifySign(InputStream inputStream, byte[] sign) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, IOException {
        java.security.Signature signature = Signature.getInstance(certificate.getSigAlgName());
        signature.initVerify(certificate);
        byte[] buffer = new byte[4096];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            signature.update(buffer, 0, len);
        }

        return signature.verify(sign);
    }

    //Encrypt File
    public byte[] encrypt(byte[] file) throws Exception {
        try (ByteArrayOutputStream outFile = new ByteArrayOutputStream()) {
            cm.encrypt(file, outFile);
            return outFile.toByteArray();
        }
    }

    public byte[] decrypt(byte[] data) throws Exception {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
//            cm.loadKey(outEncryptKey, privateKey.getEncoded());
            cm.decrypt(data, out);
            return out.toByteArray();
        }
    }

    @Override
    public InputStream decrypt(InputStream inputStream) throws GeneralSecurityException, IOException {
        return cm.decrypt(inputStream);
    }

    private String extractCNfromCertificate() {
        try {
            X500Name x500name = new JcaX509CertificateHolder(certificate).getSubject();
            RDN cn = x500name.getRDNs(BCStyle.CN)[0];
            return IETFUtils.valueToString(cn.getFirst().getValue());
        } catch (Throwable ignore) {
        }

        return certificate.getSubjectDN().getName();
    }
}
