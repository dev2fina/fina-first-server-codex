package net.fina.common.server.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionUtil {
    private String encryptionKey;

    public EncryptionUtil(){
        this.encryptionKey = "vDRa0gDwnV92nKtV";
    }

    public EncryptionUtil(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    public String encrypt(String plainText) throws Exception {
        Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        return Base64.encodeBase64String(encryptedBytes);
    }

    public String decrypt(String encrypted) throws Exception {
        Cipher cipher = getCipher(Cipher.DECRYPT_MODE);
        byte[] plainBytes = cipher.doFinal(Base64.decodeBase64(encrypted));
        return new String(plainBytes);
    }

    private Cipher getCipher(int cipherMode) throws Exception {
        String encryptionAlgorithm = "AES";
        byte[] mask = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        byte[] originalKey = encryptionKey.getBytes("UTF-8");

        System.arraycopy(originalKey, 0, mask, 0, originalKey.length > 16 ? 16 : originalKey.length);

        SecretKeySpec keySpecification = new SecretKeySpec(mask, encryptionAlgorithm);
        Cipher cipher = Cipher.getInstance(encryptionAlgorithm);
        cipher.init(cipherMode, keySpecification);
        return cipher;
    }
}