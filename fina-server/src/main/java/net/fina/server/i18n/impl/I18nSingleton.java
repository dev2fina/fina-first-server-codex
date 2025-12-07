package net.fina.server.i18n.impl;

import net.fina.server.i18n.entity.I18nEntity;
import net.fina.server.i18n.model.I18nModel;
import net.fina.server.i18n.model.I18nUpdateBodyModel;
import net.fina.server.i18n.proxy.I18nProxySession;
import net.fina.server.i18n.proxy.LanguageProxySession;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
@Startup
public class I18nSingleton {

    private final Map<String, I18nModel> translations = new HashMap<>();

    @Inject
    private LanguageProxySession languageProxySession;
    @Inject
    private I18nProxySession i18nProxySession;

    @PostConstruct
    public void init() {
        languageProxySession.loadLanguages().forEach(l -> {
            translations.put(l.getCode(), new I18nModel());
        });


        for (Map.Entry<String, I18nModel> entry : translations.entrySet()) {
            List<I18nEntity> translationData = i18nProxySession.loadByLanguageCode(entry.getKey());

            entry.getValue().setTranslations(translationData.stream().collect(Collectors.toMap(I18nEntity::getKey, I18nEntity::getValue)));
        }
    }

    public Map<String, I18nModel> getTranslations() {
        return translations;
    }

    public Map<String, String> getTranslations(String locale) {
        return translations.getOrDefault(locale, new I18nModel(new HashMap<String, String>() {{
            put("empty", "empty");
        }})).getTranslations();
    }

    public void update(List<I18nUpdateBodyModel> models) {
        i18nProxySession.update(models);

        for (I18nUpdateBodyModel model : models) {
            if (translations.get(model.getLangCode()) != null) {
                translations.get(model.getLangCode()).getTranslations().put(model.getKey(), model.getValue());
            }
        }
    }

    public List<String> create(List<I18nUpdateBodyModel> models) {
        List<String> response = i18nProxySession.create(models);
        for (I18nUpdateBodyModel model : models) {
            String val = translations.get(model.getLangCode()).getTranslations().get(model.getKey());
            if (translations.get(model.getLangCode()) != null && val == null) {
                translations.get(model.getLangCode()).getTranslations().put(model.getKey(), model.getValue());
            }
        }
        return response;
    }
}
