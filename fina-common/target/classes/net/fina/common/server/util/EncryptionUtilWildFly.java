package net.fina.common.server.util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class EncryptionUtilWildFly {

    public static char[] decode(String secret) throws Exception {
        byte[] kbytes = "jaas is the way".getBytes("UTF-8");
        SecretKeySpec key = new SecretKeySpec(kbytes, "Blowfish");

        // Convert hex string back to byte array
        int len = secret.length();
        byte[] encrypted = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            encrypted[i / 2] = (byte) ((Character.digit(secret.charAt(i), 16) << 4)
                    + Character.digit(secret.charAt(i + 1), 16));
        }

        Cipher cipher = Cipher.getInstance("Blowfish");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decrypted = cipher.doFinal(encrypted);

        return new String(decrypted, "UTF-8").toCharArray();
    }

    public static String encode(String plaintext) throws Exception {
        byte[] kbytes = "jaas is the way".getBytes();
        SecretKeySpec key = new SecretKeySpec(kbytes, "Blowfish");

        Cipher cipher = Cipher.getInstance("Blowfish");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(plaintext.getBytes());

        // Convert encrypted bytes to BigInteger and then to hex string
        BigInteger n = new BigInteger(1, encrypted);
        return n.toString(16);
    }
}