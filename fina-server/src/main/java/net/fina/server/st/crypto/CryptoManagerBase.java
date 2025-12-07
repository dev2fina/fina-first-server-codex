package net.fina.server.st.crypto;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public abstract class CryptoManagerBase {
    public static final int AES_Key_Size = 128;
    protected Cipher pkCipher, aesCipher;
    protected byte[] aesKey;
    protected SecretKeySpec aesKeySpec;
    protected String pkAlgorithm;
    protected String cipherAlgorithm = "AES";

    /**
     * Creates a new AES key
     */
    public void makeKey() throws NoSuchAlgorithmException {
        KeyGenerator kgen = KeyGenerator.getInstance(cipherAlgorithm);
        kgen.init(AES_Key_Size);
        SecretKey key = kgen.generateKey();
        aesKey = key.getEncoded();
        aesKeySpec = new SecretKeySpec(aesKey, cipherAlgorithm);
    }

    /**
     * Decrypts an AES key from a file using an RSA private key
     */
    public void loadKey(byte[] in, byte[] encodedKey) throws GeneralSecurityException, IOException {
        // create private key
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedKey);
        KeyFactory kf = KeyFactory.getInstance(pkAlgorithm);
        PrivateKey pk = kf.generatePrivate(privateKeySpec);

        // read AES key
        pkCipher.init(Cipher.DECRYPT_MODE, pk);
        aesKey = new byte[AES_Key_Size / 8];
        CipherInputStream is = new CipherInputStream(new ByteArrayInputStream(in), pkCipher);
        is.read(aesKey);
        aesKeySpec = new SecretKeySpec(aesKey, cipherAlgorithm);
    }

    public void loadKey(byte[] in, PrivateKey pk) throws GeneralSecurityException, IOException {
        // read AES key
        pkCipher.init(Cipher.DECRYPT_MODE, pk);
        aesKey = new byte[AES_Key_Size / 8];
        CipherInputStream is = new CipherInputStream(new ByteArrayInputStream(in), pkCipher);
        is.read(aesKey);
        aesKeySpec = new SecretKeySpec(aesKey, cipherAlgorithm);
    }


    /**
     * Decrypts an AES key from a file using an RSA private key
     */

    /**
     * Encrypts the AES key to a file using an RSA public key
     */
    public void saveKey(OutputStream out, byte[] encodedKey) throws IOException, GeneralSecurityException {

        // create public key
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedKey);
        KeyFactory kf = KeyFactory.getInstance(pkAlgorithm);
        PublicKey pk = kf.generatePublic(publicKeySpec);

        // write AES key
        pkCipher.init(Cipher.ENCRYPT_MODE, pk);
        try (CipherOutputStream os = new CipherOutputStream(out, pkCipher)) {
            os.write(aesKey);
        }
    }

    public abstract void encrypt(byte[] in, ByteArrayOutputStream out) throws Exception;

    public abstract void decrypt(byte[] in, ByteArrayOutputStream out) throws Exception;

    public abstract InputStream decrypt(InputStream inputStream) throws GeneralSecurityException, IOException;
}
