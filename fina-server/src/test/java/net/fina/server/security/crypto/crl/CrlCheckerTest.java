package net.fina.server.security.crypto.crl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.security.Security;
import java.security.cert.*;
import java.util.Collections;

public class CrlCheckerTest {

    private CertificateFactory cf;
    private X509CRL crl;

    @Before
    public void before() throws Exception {
        cf = CertificateFactory.getInstance("X.509");

        // Load CRL
        crl = (X509CRL) cf.generateCRL(new FileInputStream("./src/test/resources/net.fina.server.security.crypto.crl/crl.crl"));

    }


    @Test
    public void checkRevokedCertificate() throws Exception {
        X509Certificate cert = (X509Certificate) cf.generateCertificate(new FileInputStream("./src/test/resources/net.fina.server.security.crypto.crl/bnk-138001.crt"));

        // Check if certificate is revoked
        X509CRLEntry revokedEntry = crl.getRevokedCertificate(cert.getSerialNumber());
        if (revokedEntry != null) {
            System.out.println("❌ Certificate is revoked");
        } else {
            System.out.println("✅ Certificate is NOT revoked");
        }

        BigInteger serialNumber = cert.getSerialNumber();

        // Check if certificate is in the CRL
        X509CRLEntry entry = crl.getRevokedCertificate(serialNumber);

        if (entry != null) {
            System.out.println("❌ Certificate is REVOKED.");
            System.out.println("Reason: " + entry.getRevocationReason());
            System.out.println("Revocation Date: " + entry.getRevocationDate());
        } else {
            System.out.println("✅ Certificate is NOT revoked.");
        }

        Assert.assertTrue(entry != null && revokedEntry != null);
    }

    @Test
    public void testNonRevokedCertificate() throws Exception {
        X509Certificate cert = (X509Certificate) cf.generateCertificate(new FileInputStream("./src/test/resources/net.fina.server.security.crypto.crl/bnk-baga.crt"));

        // Check if certificate is revoked
        X509CRLEntry revokedEntry = crl.getRevokedCertificate(cert.getSerialNumber());
        if (revokedEntry != null) {
            System.out.println("❌ Certificate is revoked");
        } else {
            System.out.println("✅ Certificate is NOT revoked");
        }

        BigInteger serialNumber = cert.getSerialNumber();

        // Check if certificate is in the CRL
        X509CRLEntry entry = crl.getRevokedCertificate(serialNumber);

        if (entry != null) {
            System.out.println("❌ Certificate is REVOKED.");
            System.out.println("Reason: " + entry.getRevocationReason());
            System.out.println("Revocation Date: " + entry.getRevocationDate());
        } else {
            System.out.println("✅ Certificate is NOT revoked.");
        }

        Assert.assertTrue(entry == null && revokedEntry == null);
    }

    @Test
    @Ignore
    public void crlCheckFromURL() throws Exception {
        X509Certificate cert = (X509Certificate) cf.generateCertificate(new FileInputStream("./src/test/resources/net.fina.server.security.crypto.crl/bnk-baga.crt"));

        URL crlUrl = new URL("http://localhost:8380/fina-app/rest/v1/anonymous/crl");
        InputStream crlStream = crlUrl.openStream();
        X509CRL crl = (X509CRL) cf.generateCRL(crlStream);
        System.out.println(crl.getThisUpdate());
        System.out.println(crl.getNextUpdate());
        crlStream.close();

        // Check if the certificate is revoked
        BigInteger serialNumber = cert.getSerialNumber();
        X509CRLEntry entry = crl.getRevokedCertificate(serialNumber);

        if (entry != null) {
            System.out.println("❌ Certificate is REVOKED");
            System.out.println("Reason: " + entry.getRevocationReason());
            System.out.println("Revoked at: " + entry.getRevocationDate());
        } else {
            System.out.println("✅ Certificate is NOT revoked");
        }
    }

    @Test
    public void testOCSP() throws Exception {
        Security.setProperty("ocsp.enable", "true");

        // Load the certificate to validate
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate) cf.generateCertificate(new FileInputStream("./src/test/resources/net.fina.server.security.crypto.crl/bnk-baga-ocsp-revoked.crt"));

        // Load issuer certificate
        X509Certificate issuer = (X509Certificate) cf.generateCertificate(new FileInputStream("./src/test/resources/net.fina.server.security.crypto.crl/rootCA.crt"));

        // Create trust anchor and parameters
        TrustAnchor anchor = new TrustAnchor(issuer, null);
        PKIXParameters params = new PKIXParameters(Collections.singleton(anchor));
        params.setRevocationEnabled(true);

        // Validate cert path
        CertPath certPath = cf.generateCertPath(Collections.singletonList(cert));
        CertPathValidator validator = CertPathValidator.getInstance("PKIX");

        try {
            validator.validate(certPath, params);
            System.out.println("✅ Certificate is VALID (not revoked via OCSP)");
        } catch (CertPathValidatorException e) {
            System.out.println("❌ Certificate validation failed: " + e.getMessage());
        }
    }
}
