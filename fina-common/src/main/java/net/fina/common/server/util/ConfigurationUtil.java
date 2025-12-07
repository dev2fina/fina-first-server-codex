package net.fina.common.server.util;

import org.apache.commons.configuration.XMLConfiguration;
import org.jboss.logging.Logger;

import java.io.File;

public class ConfigurationUtil {
    private static final String JBOSS_SERVER_CONFIG_DIR_PROPERTY = "jboss.server.config.dir";
    private static final String JBOSS_SERVER_NAME = "jboss.server.name";
    private static final String JBOSS_SERVER_HOST = "jboss.host.name";
    public static final String FINA_CONFIG_DIR = "fina.config.dir";
    private static final String CONFIG_FILE = "fina.xml";
    private static final String CONFIG_VERSION = "8.0.2";

    private final Logger log = Logger.getLogger(getClass());

    private XMLConfiguration xmlConfiguration;
    private String serverName;

    private ConfigurationUtil() {
        postLoad();
    }

    public static synchronized ConfigurationUtil get() {
        return new ConfigurationUtil();
    }

    private void postLoad() {
        try {
            String finaConfigDir = System.getProperty(FINA_CONFIG_DIR);
            if (finaConfigDir == null) {
                finaConfigDir = System.getProperty(JBOSS_SERVER_CONFIG_DIR_PROPERTY);
            }
            String confFileLocation = finaConfigDir + File.separator + CONFIG_FILE;
            xmlConfiguration = new XMLConfiguration(confFileLocation);
            xmlConfiguration.load();

            serverName = System.getProperty(JBOSS_SERVER_NAME);
            if (serverName != null && serverName.equalsIgnoreCase(System.getProperty(JBOSS_SERVER_HOST))) {
                serverName = null;
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    public String get(String key) {
        if (serverName != null) {
            key = serverName + "." + key;
        }
        return xmlConfiguration.getString(key);
    }

    public String getConfigVersion() {
        return xmlConfiguration.getString("ConfigurationVersion");
    }

    public String getCurrentConfigurationVersion() {
        return CONFIG_VERSION;
    }


    public boolean isStandaloneMode() {
        return System.getProperty(JBOSS_SERVER_NAME) != null && System.getProperty(JBOSS_SERVER_NAME).equalsIgnoreCase(System.getProperty(JBOSS_SERVER_HOST));
    }
}
