package net.fina.server.st.crypto;

import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;

public abstract class SecurityManagerBase {
    public final String CERTIFICATE_TYPE = "X.509";

    public abstract boolean verifySign(byte[] data, byte[] sign) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, CertificateException, NoSuchProviderException;

    public abstract boolean verifySign(InputStream inputStream, byte[] sign) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, IOException, CertificateException, NoSuchProviderException;

    public abstract byte[] encrypt(byte[] file) throws Exception;

    public abstract byte[] decrypt(byte[] data) throws Exception;

    public abstract InputStream decrypt(InputStream inputStream) throws GeneralSecurityException, IOException;

    public abstract byte[] getEncryptKey() throws CertificateEncodingException;

    public abstract String getCertificateSubjectDN();

    public byte[] getServersPublicKey() throws Exception {
        return SecurityManagerUtil.loadServerCertificate(SecurityManagerUtil.loadServerKeyStore()).getPublicKey().getEncoded();
    }
}
