package net.fina.server.st.crypto.v2;

import net.fina.server.st.crypto.CryptoManagerBase;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import java.io.*;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;

public class CryptoManagerV2 extends CryptoManagerBase {


    public CryptoManagerV2(X509Certificate certificate) throws Exception {

        this.pkAlgorithm = certificate.getPublicKey().getAlgorithm();

        // create RSA public key cipher
        pkCipher = Cipher.getInstance(this.pkAlgorithm);
        // create AES shared key cipher
        aesCipher = Cipher.getInstance(cipherAlgorithm);
        makeKey();
    }

    @Override
    public void encrypt(byte[] in, ByteArrayOutputStream out) throws Exception {
        aesCipher.init(Cipher.ENCRYPT_MODE, aesKeySpec);
        try (CipherOutputStream os = new CipherOutputStream(out, aesCipher)) {
            copy(new ByteArrayInputStream(in), os);
        }
    }

    @Override
    public void decrypt(byte[] in, ByteArrayOutputStream out) throws Exception {
        aesCipher.init(Cipher.DECRYPT_MODE, aesKeySpec);
        try (CipherInputStream is = new CipherInputStream(new ByteArrayInputStream(in), aesCipher)) {
            copy(is, out);
        }
    }

    @Override
    public InputStream decrypt(InputStream inputStream) throws GeneralSecurityException, IOException {
        aesCipher.init(Cipher.DECRYPT_MODE, aesKeySpec);
        return new CipherInputStream(inputStream, aesCipher);
    }

    /**
     * Copies a stream.
     */
    private void copy(InputStream is, OutputStream os) throws IOException {
        int i;
        byte[] b = new byte[1024];
        while ((i = is.read(b)) != -1) {
            os.write(b, 0, i);
        }
    }
}
