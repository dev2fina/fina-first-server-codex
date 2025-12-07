package net.fina.server.st.crypto;

import net.fina.common.client.exception.DcsTypeException;
import net.fina.common.server.util.ConfigurationUtil;
import org.jboss.logging.Logger;

import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.security.cert.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CrlChecker {

    private static final Logger log = Logger.getLogger(CrlChecker.class);


    public static void validateCertificateInCrl(Certificate clientCertificate) throws DcsTypeException {
        String crlUrl = getCrlURL();
        if (crlUrl != null && !crlUrl.isBlank()) {
            try {

                log.info("Download CRL From : " + crlUrl);
                long start = System.currentTimeMillis();
                CertificateFactory cf = CertificateFactory.getInstance("X.509");

                URL crlEndpoint = new URL(crlUrl);
                InputStream crlStream = crlEndpoint.openStream();
                X509CRL crl = (X509CRL) cf.generateCRL(crlStream);
                crlStream.close();
                long end = System.currentTimeMillis();

                log.info("CRL File Downloaded Successfully in : " + (end - start) + " ms");

                log.info("Validate Certificate in CRL");
                // Check if the certificate is revoked
                BigInteger serialNumber = ((X509Certificate) clientCertificate).getSerialNumber();
                X509CRLEntry entry = crl.getRevokedCertificate(serialNumber);

                if (entry != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Certificate is REVOKED : ")
                            .append(getCommonName((X509Certificate) clientCertificate))
                            .append(", Reason : ")
                            .append(entry.getRevocationReason() != null ? entry.getRevocationReason() : "")
                            .append("Revocation Time : ")
                            .append(entry.getRevocationDate());

                    log.error(sb.toString());
                    throw new DcsTypeException(DcsTypeException.Type.CERTIFICATE_REVOKED);
                }

            } catch (DcsTypeException dcsTypeException) {
                throw dcsTypeException;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new DcsTypeException(DcsTypeException.Type.GENERAL_ERROR);
            }

        }
    }

    private static String getCrlURL() {
        try {
            return ConfigurationUtil.get().get("SubmissionTool.CRLUrl");
        } catch (Throwable e) {
            log.warn("CRL check is disabled!!! because of CRLUrl is not provided in fina.xml");
            log.error(e);
        }

        return null;
    }


    private static String getCommonName(X509Certificate cert) {
        String dn = cert.getSubjectX500Principal().getName();
        Pattern cnPattern = Pattern.compile("CN=([^,]+)");
        Matcher matcher = cnPattern.matcher(dn);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
