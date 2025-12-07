package net.fina.server.i18n.impl;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.*;
import net.fina.server.i18n.api.LanguageLocal;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
@Startup
@Lock(LockType.READ)
public class LanguageListSingleton {

    private final Logger log = Logger.getLogger(getClass());

    private List<Long> languages;
    private Map<String, Long> languageIdMap;
    private Map<Long, String> languageIdCodeMap;
    @EJB
    private LanguageLocal languageLocal;

    @PostConstruct
    public void reload() {
        log.info("Reload Languages.");
        languages = languageLocal.getLanguageIds();
        languageIdMap = languageLocal.loadLanguageCodeIdMap();
        languageIdCodeMap = languageIdMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    }

    public List<Long> getLanguageIds() {
        return new ArrayList<>(languages);
    }

    public Map<String, Long> getLanguageIdMap() {
        return new HashMap<>(languageIdMap);
    }

    public Map<Long, String> getLanguageIdCodeMap() {
        return new HashMap<>(languageIdCodeMap);
    }
}
