package net.fina.first.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "ecm.alfresco")
public class EcmConfig {

    private String endpoint;
    private String adminUsername;
    private String adminPassword;
    private String userHashSalt;
    private String fiRegistryRootPath = "/Sites/FINA/DocumentLibrary/FI_REGISTRY";
    private int connectionTimeout = 30000;
    private int readTimeout = 60000;
    private int maxConnections = 50;
    private boolean enabled = true;
}
