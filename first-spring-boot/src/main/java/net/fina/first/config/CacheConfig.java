package net.fina.first.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cache configuration for the FIRST application.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    public static final String FI_TYPES_CACHE = "fiTypes";
    public static final String REGIONS_CACHE = "regions";
    public static final String LEGAL_FORMS_CACHE = "legalForms";
    public static final String LICENSE_TYPES_CACHE = "licenseTypes";
    public static final String PERMISSIONS_CACHE = "permissions";
    public static final String LANGUAGES_CACHE = "languages";

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
                FI_TYPES_CACHE,
                REGIONS_CACHE,
                LEGAL_FORMS_CACHE,
                LICENSE_TYPES_CACHE,
                PERMISSIONS_CACHE,
                LANGUAGES_CACHE
        );
    }
}
