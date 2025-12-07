package net.fina.common.server.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.HttpHeaders;
import org.jboss.logging.Logger;

public class RestUtil {
    private static final Logger log = Logger.getLogger(RestUtil.class.getName());
    private static final String DEFAULT_LANG_CODE = "en_US";

    public static String getLanguageCodeFromRequest(HttpServletRequest request) {
        try {
            String locale = request.getHeader(HttpHeaders.ACCEPT_LANGUAGE);
            String user = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "anonymous";

            log.debug("User [" + user + "] Locale - " + locale);

            return locale == null ? DEFAULT_LANG_CODE : locale;
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }

        return DEFAULT_LANG_CODE;
    }
}
