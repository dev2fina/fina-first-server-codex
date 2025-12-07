package net.fina.server.i18n.cache;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import jakarta.ejb.*;
import net.fina.server.i18n.api.LanguageLocal;
import net.fina.server.i18n.api.SysStringLocal;
import net.fina.server.i18n.entity.Language;
import net.fina.server.i18n.entity.SysString;
import net.fina.server.i18n.entity.SysStringId;
import org.jboss.logging.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
@Startup
@Lock(LockType.READ)
public class SysStringCacheManager {
    private final Logger log = Logger.getLogger(getClass());

    @Resource(lookup = "java:jboss/infinispan/cache/fina-strings-cache-container/fina-strings-cache")
    private org.infinispan.Cache<Object, Object> cache;

    @EJB
    private SysStringLocal sysStringLocal;
    @EJB
    private LanguageLocal languageLocal;

    @PostConstruct
    public void autoStart() {
        createCache();
    }

    public void createCache() {
        log.info("Constructing object of " + this.getClass().getName());

        if (this.cache.isEmpty()) {
            // TODO when landId is NULL in DB throw NullPointerException.
            // Load SYS Strings
            List<SysString> descriptions = sysStringLocal.loadSysStrings();

            // Load Languages
            List<Language> languages = languageLocal.loadAllLanguages();

            /**
             * <language Id, Language>
             */
            Map<Long, Language> languagesMap = new HashMap<>();

            for (Language language : languages) {
                languagesMap.put(language.getId(), language);
            }

            for (SysString sysString : descriptions) {

                Language l = languagesMap.get(sysString.getLangId());
                //ignore deleted language
                if (l != null && l.isDeleted()) {
                    continue;
                }

                String description = null;

                if (l != null) {
                    description = sysString.getValue();
                } else {
                    log.warn(String.format("Unknown Language String: %s", sysString));
                }

                SysStringId sysStringId = new SysStringId();
                sysStringId.setId(sysString.getId());
                sysStringId.setLangId(sysString.getLangId());

                if (description != null) {
                    cache.put(sysStringId, description);
                }
            }

            log.info("Cache Size : " + cache.size() + " | SysString size: " + descriptions.size());
        }
    }

    @PreDestroy
    public void clearCache() {
        cache.clear();
        log.info(getClass().getName() + " Cache Cleared.");
    }

    public Map<Object, Object> getCache() {
        return cache;
    }

    public Object removeCache(Object object) {
        log.info("Remove cache Object - " + object);
        return cache.remove(object);
    }

    public Object getDescription(Object key) {
        return cache.get(key);
    }

    public void addCache(long nameStrId, long langId, String value) {
        SysStringId sysStringId = new SysStringId();
        sysStringId.setId(nameStrId);
        sysStringId.setLangId(langId);
        cache.put(sysStringId, value);
        log.info("Update Cache : " + sysStringId + " value = " + value);
    }
}
