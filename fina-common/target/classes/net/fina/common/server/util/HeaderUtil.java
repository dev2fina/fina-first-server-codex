package net.fina.common.server.util;

import net.fina.common.shared.HeaderConfig;
import org.jboss.logging.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class HeaderUtil {
    private Logger log = Logger.getLogger(getClass());

    /**
     * Returns the header image base64 value at the specified header config (FINA_HEADER_IMAGE or DCS_HEADER_IMAGE) <br>
     * null if config is null or header config value from fina.xml is not specified
     *
     * @param headerConfig HeaderConfig either FINA_HEADER_IMAGE or DCS_HEADER_IMAGE
     * @return the header image base64 value at the specified header config (FINA_HEADER_IMAGE or DCS_HEADER_IMAGE)
     */
    public String getBase64HeaderImage(HeaderConfig headerConfig) {
        if (headerConfig != null && (headerConfig.equals(HeaderConfig.FINA_HEADER_IMAGE) || headerConfig.equals(HeaderConfig.DCS_HEADER_IMAGE))) {
            try {
                String imagePath = ConfigurationUtil.get().get(headerConfig.getTag());
                if (imagePath != null) {
                    Path path = Paths.get(ConfigurationUtil.get().get(headerConfig.getTag()));
                    byte[] data = Files.readAllBytes(path);
                    return Base64.getEncoder().encodeToString(data);
                }
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
            }
        }
        return null;
    }

    /**
     * Returns the header text at the specified header config (FINA_HEADER_TEXT or DCS_HEADER_TEXT) <br>
     * null if config is null or header config value from fina.xml is not valid (length is more than 120)
     *
     * @param headerConfig HeaderConfig either FINA_HEADER_TEXT or DCS_HEADER_TEXT
     * @return the header text at the specified header config (FINA_HEADER_TEXT or DCS_HEADER_TEXT)
     */
    public String getHeaderText(HeaderConfig headerConfig) {
        if (headerConfig != null && (headerConfig.equals(HeaderConfig.FINA_HEADER_TEXT) || headerConfig.equals(HeaderConfig.DCS_HEADER_TEXT))) {
            String result = ConfigurationUtil.get().get(headerConfig.getTag());
            return result != null && checkTextLength(result) ? result : null;
        }
        return null;
    }

    /**
     * Checks header text against 120, which is max header text length for the system
     *
     * @param headerText the location of the image, relative to the url argument
     * @return is header text valid (length <= 120)
     */
    private boolean checkTextLength(String headerText) {
        return headerText.length() <= 120;
    }
}
