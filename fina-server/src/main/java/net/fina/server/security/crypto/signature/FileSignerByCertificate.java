package net.fina.server.security.crypto.signature;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.security.*;
import net.fina.common.server.util.ConfigurationUtil;
import net.fina.common.server.util.EncryptionUtilWildFly;
import net.fina.common.shared.FileSignerException;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.poifs.crypt.dsig.SignatureConfig;
import org.apache.poi.poifs.crypt.dsig.SignatureInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.XMLSignatureException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;

public class FileSignerByCertificate implements FileSigner {
    private static final String KEY_STORE_TYPE = "PKCS12";

    private java.security.cert.Certificate[] chain;
    private X509Certificate x509;
    private PrivateKey privateKey;

    FileSignerByCertificate() throws FileSignerException {
        String keyStoreFile = ConfigurationUtil.get().get("FINA_FILE_SIGNATURE.keystore_file");

        if (keyStoreFile == null || keyStoreFile.trim().isEmpty()) {
            throw new FileSignerException("Key store file location is not presented !");
        }

        try {
            byte[] keyStoreBytes = Files.readAllBytes(new File(keyStoreFile).toPath());

            char[] keyStorePassword = EncryptionUtilWildFly.decode(ConfigurationUtil.get().get("FINA_FILE_SIGNATURE.keystore_password"));
            char[] keyPassword = EncryptionUtilWildFly.decode(ConfigurationUtil.get().get("FINA_FILE_SIGNATURE.key_password"));
            String keyStoreAlias = ConfigurationUtil.get().get("FINA_FILE_SIGNATURE.keystore_alias");

            loadCertificate(keyStoreBytes, keyStoreAlias, keyStorePassword, keyPassword);

        } catch (NoSuchFileException nsfe) {
            throw new FileSignerException("Certificate not found");
        } catch (Exception e) {
            throw new FileSignerException(e);
        }
    }

    private void loadCertificate(byte[] keyStoreBytes, String alias, char[] keyStorePassword, char[] keyPassword) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableEntryException {
        try (ByteArrayInputStream in = new ByteArrayInputStream(keyStoreBytes)) {

            KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE);
            keyStore.load(in, keyStorePassword);

            if (keyStore.containsAlias(alias)) {
                this.x509 = (X509Certificate) keyStore.getCertificate(alias);
                this.chain = keyStore.getCertificateChain(alias);
            }

            KeyStore.ProtectionParameter protectionParameter = new KeyStore.PasswordProtection(keyPassword);
            KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(alias, protectionParameter);

            this.privateKey = pkEntry.getPrivateKey();
        }
    }

    @Override
    public byte[] sign(String fileName, byte[] file, String fileType) throws FileSignerException {

        try (ByteArrayInputStream in = new ByteArrayInputStream(file)) {

            String extension = FilenameUtils.getExtension(fileName).toLowerCase();

            switch (fileType) {
                case "application/pdf": {
                    return signingPdf(in);
                }
                case "application/zip":
                case "application/x-tika-ooxml":
                case "application/x-tika-msoffice":
                    switch (extension) {
                        case "xlsx":
                        case "docx":
                            break;
                        default:
                            throw new FileSignerException(extension + " sing not supported!");
                    }
                    return signingOfficeDocument(in);

                default:
                    throw new FileSignerException(extension + " sing not supported!");
            }
        } catch (Throwable e) {
            throw new FileSignerException(e);
        }
    }

    /*
         xlsx/docx
     */
    private byte[] signingOfficeDocument(InputStream fileInputStream) throws IOException, InvalidFormatException, XMLSignatureException, MarshalException {
        // filling the SignatureConfig entries (minimum fields, more options are available ...)
        SignatureConfig signatureConfig = new SignatureConfig();
        ZipSecureFile.setMinInflateRatio(0);

        signatureConfig.setKey(this.privateKey);
        signatureConfig.setSigningCertificateChain(Collections.singletonList(x509));
        signatureConfig.setDigestAlgo(HashAlgorithm.sha256);

        try (OPCPackage pkg = OPCPackage.open(fileInputStream)) {

            // adding the signature document to the package
            SignatureInfo si = new SignatureInfo();
            si.setOpcPackage(pkg);
            si.setSignatureConfig(signatureConfig);
            si.confirmSignature();
            // optionally verify the generated signature
            boolean b = si.verifySignature();
            assert (b);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            pkg.save(out);

            return out.toByteArray();
        }
    }

    //PDF
    private byte[] signingPdf(InputStream fileInputStream) throws IOException, DocumentException, GeneralSecurityException {
        BouncyCastleProvider provider = new BouncyCastleProvider();
        Security.addProvider(provider);

        // Creating the reader and the stamper
        PdfReader reader = new PdfReader(fileInputStream);

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfStamper stamper = PdfStamper.createSignature(reader, os, '\0');

            // Creating the appearance
            PdfSignatureAppearance appearance = stamper.getSignatureAppearance();

            // Creating the signature
            ExternalDigest digest = new BouncyCastleDigest();
            ExternalSignature signature = new PrivateKeySignature(privateKey, DigestAlgorithms.SHA256, provider.getName());

            MakeSignature.signDetached(appearance, digest, signature, chain, null, null, null, 0, MakeSignature.CryptoStandard.CMS);

            return os.toByteArray();
        }
    }
}
