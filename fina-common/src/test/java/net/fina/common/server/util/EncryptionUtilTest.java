package net.fina.common.server.util;


import org.junit.Test;

public class EncryptionUtilTest {

    @Test
    public void test() throws Exception {
        String encryptionKey = "a";

        String plainText = "blah";
        EncryptionUtil encryptionUtil = new EncryptionUtil(encryptionKey);
        String cipherText = encryptionUtil.encrypt(plainText);
        String decryptedCipherText = encryptionUtil.decrypt(cipherText);

        System.out.println(plainText);
        System.out.println(cipherText);
        System.out.println(decryptedCipherText);
    }

    @Test
    public void test2() throws Exception {
        String plainText = "blah";
        EncryptionUtil encryptionUtil = new EncryptionUtil();
        String cipherText = encryptionUtil.encrypt(plainText);
        String decryptedCipherText = encryptionUtil.decrypt(cipherText);

        System.out.println(plainText);
        System.out.println(cipherText);
        System.out.println(decryptedCipherText);
    }

    @Test
    public void encryptDecryptTest() throws Exception {
        String encoded = EncryptionUtilWildFly.encode("fina2demo");

        System.out.println(encoded);

        System.out.println(EncryptionUtilWildFly.decode(encoded));

    }
}
