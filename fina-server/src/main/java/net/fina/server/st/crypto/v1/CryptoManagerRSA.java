package net.fina.server.st.crypto.v1;

import net.fina.server.st.crypto.CryptoManagerBase;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import java.io.*;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;

public class CryptoManagerRSA extends CryptoManagerBase {

    /**
     * Constructor: creates ciphers
     */
    public CryptoManagerRSA(String algorithm) throws GeneralSecurityException {
        this.pkAlgorithm = algorithm;

        // create RSA public key cipher
        pkCipher = Cipher.getInstance(algorithm);
        // create AES shared key cipher
        aesCipher = Cipher.getInstance(cipherAlgorithm);
    }


    /**
     * Encrypts and then copies the contents of a given file.
     */
    public void encrypt(byte[] in, ByteArrayOutputStream out) throws IOException, InvalidKeyException {
        aesCipher.init(Cipher.ENCRYPT_MODE, aesKeySpec);
        try (CipherOutputStream os = new CipherOutputStream(out, aesCipher)) {
            copy(new ByteArrayInputStream(in), os);
        }
    }

    /**
     * Decrypts and then copies the contents of a given file.
     */
    public void decrypt(byte[] in, ByteArrayOutputStream out) throws IOException, InvalidKeyException {
        aesCipher.init(Cipher.DECRYPT_MODE, aesKeySpec);
        try (CipherInputStream is = new CipherInputStream(new ByteArrayInputStream(in), aesCipher)) {
            copy(is, out);
        }
    }

    @Override
    public InputStream decrypt(InputStream inputStream) throws InvalidKeyException {
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
