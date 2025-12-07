package net.fina.server.misc;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.fina.server.i18n.entity.Language;
import net.fina.server.i18n.helper.Description;
import org.jgroups.protocols.TIME;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.TimeZone;

import static net.fina.common.shared.SessionAttributeNames.LOCALE_PARAMETER_NAME;
import static net.fina.common.shared.SessionAttributeNames.TIME_ZONE_ID;

public class ServiceUtil {

    public static long getLanguageId(HttpServletRequest request) {
        /**
         * Default Value is 1
         */
        long langId = 1;
        Language language = getLanguage(request);
        if (language != null) {
            langId = language.getId();
        }
        return langId;
    }

    public static Language getLanguage(HttpServletRequest request) {
        Language language = null;
        Object languageObject = request.getSession().getAttribute(LOCALE_PARAMETER_NAME);
        if (languageObject != null) {
            language = (Language) languageObject;
        }
        return language;
    }

    public static String getLanguageCode(HttpServletRequest request) {
        Language language = getLanguage(request);
        if (language != null) {
            return language.getCode();
        } else {
            return "en_US";
        }
    }

    public static String getUserPrincipal(HttpServletRequest request) {
        return request.getUserPrincipal().getName();
    }

    public static String toName(Description description, long langId) {
        return description == null ? "" : description.getDescription(langId);
    }

    public static String getTimeZoneId(HttpServletRequest request) {
        Object timeZoneObject = request.getSession().getAttribute(TIME_ZONE_ID);
        if (timeZoneObject == null) {
            timeZoneObject = request.getHeader(TIME_ZONE_ID);
        }
        return timeZoneObject == null ? TimeZone.getDefault().getID() : timeZoneObject.toString();
    }

    public static boolean handleThrowable(Throwable throwable, HttpServletResponse response) throws IOException {
        Class[] throwableClassTypes = new Class[]{
                FileNotFoundException.class
        };
        while (throwable != null) {
            for (Class c : throwableClassTypes) {
                if (c.isInstance(throwable)) {
                    String message = throwable.getMessage();
                    message = message == null ? "" : message;
                    message = message.replace(".prpt ", ".prpt</br>");
                    response.sendError(500, "Internal Server Error</br></br>" + message);
                    return true;
                }
            }
            throwable = throwable.getCause();
        }
        return false;
    }


}
