package net.fina.server.dcs.uploadfile.impl.reader.vipnet;

import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;

public class VipNetDocumentReaderTest {

    @Test
    public void test() throws CertificateException, IOException, CRLException {
        String crlURL = "./src/test/resources/VipNetDocumentReaderTest/CRL_CAVIP.crl";
        try (InputStream crlStream = new BufferedInputStream(new FileInputStream(crlURL))) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509CRL x509CRL = (X509CRL) cf.generateCRL(crlStream);

            System.out.println(x509CRL.getRevokedCertificates());
        }
    }
}
