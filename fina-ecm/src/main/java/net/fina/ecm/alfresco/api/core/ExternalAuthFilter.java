package net.fina.ecm.alfresco.api.core;

import org.jboss.logging.Logger;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ExternalAuthFilter implements ClientRequestFilter {
    private String userName;
    private String userHashSalt;

    public ExternalAuthFilter(String userName, String userHashSalt) {
        this.userName = userName;
        this.userHashSalt = userHashSalt;
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        MultivaluedMap<String, Object> headers = requestContext.getHeaders();

        headers.add("X-Alfresco-Remote-User", this.userName);
        headers.add("X-Remote-User-Hash", encodePassword(userHashSalt + userName));
    }

    private String encodePassword(String pass) {
        String encodedPass = null;
        if (pass != null) {
            byte[] password = pass.getBytes();
            MessageDigest md = null;
            try {
                md = MessageDigest.getInstance("SHA-1");
            } catch (NoSuchAlgorithmException e) {
                Logger.getLogger("FinALoginModule").error(e.getMessage(), e);
            }
            password = md.digest(password);

            StringBuffer str = new StringBuffer();
            for (int i = 0; i < password.length; i++) {
                str.append(Integer.toHexString((password[i] & 0xf0) >> 4));
                str.append(Integer.toHexString(password[i] & 0x0f));
            }
            encodedPass = str.toString();
        }
        return encodedPass;
    }
}
