package net.fina.server.dcs.uploadfile.impl.reader.excel.util;


import net.fina.common.client.exception.DcsTypeException;
import org.apache.commons.codec.binary.Base64;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.LittleEndianConsts;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.jboss.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.DigestException;
import java.security.MessageDigest;
import java.util.Arrays;


public class ExcelPasswordValidator {

    private static Logger log = Logger.getLogger(ExcelPasswordValidator.class);

    /**
     * @param sheet    witch password to check
     * @param password protection password
     * @return <b><boolean>true</boolean></b> if sheet is protected with the password value, otherwise <b><boolean>false</boolean></b>.
     * @note: this method also can be used if sheet is protected using Excel 2013.
     */
    public static boolean checkPasswordCorrection(Sheet sheet, String password) {
        boolean b = false;
        try {
            if (sheet instanceof HSSFSheet) {
                b = hssfPassword((HSSFSheet) sheet) == excelSimplePassword(password);
            } else if (sheet instanceof XSSFSheet) {
                if (sheet.getProtect() && password != null && !password.isEmpty()) {
                    b = ((XSSFSheet) sheet).validateSheetPassword(password);
                }
            }
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
        return b;
    }

    private static short hssfPassword(HSSFSheet sheet) {
        return sheet.getPassword();
    }

    @Deprecated
    private static short xssfPassword(XSSFSheet sheet) {
        byte[] pwd = sheet.getCTWorksheet().getSheetProtection().getPassword();
        if (pwd != null) {
            return java.nio.ByteBuffer.wrap(pwd).getShort();
        }
        return 0;
    }

    private static short excelSimplePassword(String password) {
//        return PasswordRecord.hashPassword(password);
        //TODO Fix apache poi upgrade
        return -1;
    }

    @Deprecated
    private static boolean excel2013PassCheck(XSSFSheet sheet, String password) {
        boolean b = false;

        try {
            InputStream inputStream = sheet.getCTWorksheet().newInputStream();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);

            NamedNodeMap nodeMap = document.getElementsByTagName("main:sheetProtection").item(0).getAttributes();

            String algorithmName = nodeMap.getNamedItem("algorithmName").getNodeValue();
            String hashV = nodeMap.getNamedItem("hashValue").getNodeValue();
            String saltV = nodeMap.getNamedItem("saltValue").getNodeValue();
            int spinCount = Integer.parseInt(nodeMap.getNamedItem("spinCount").getNodeValue());

            byte hashValue[] = Base64.decodeBase64(hashV);
            byte saltValue[] = Base64.decodeBase64(saltV);

            /* FixMe Use This when new POI 3.10 appears
            org.apache.poi.poifs.crypt.HashAlgorithm hashAlgo = HashAlgorithm.sha512;
            MessageDigest hashAlg = CryptoFunctions.getMessageDigest(hashAlgo);*/

            MessageDigest hashAlg = MessageDigest.getInstance(algorithmName);

            hashAlg.update(saltValue);
            byte[] hash = hashAlg.digest(password.getBytes("UTF-16LE"));
            byte[] iterator = new byte[LittleEndianConsts.INT_SIZE];

            try {
                for (int i = 0; i < spinCount; i++) {
                    LittleEndian.putInt(iterator, 0, i);
                    hashAlg.reset();
                    hashAlg.update(hash);
                    hashAlg.update(iterator);
                    hashAlg.digest(hash, 0, hash.length);
                }
            } catch (DigestException e) {
                throw new EncryptedDocumentException("error in password hashing");
            }
            b = Arrays.equals(hashValue, hash);
        } catch (Exception e) {
            throw new DcsTypeException(e, DcsTypeException.Type.INVALID_PASSWORD);
        }
        return b;
    }
}
