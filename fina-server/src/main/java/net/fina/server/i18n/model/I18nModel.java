package net.fina.server.i18n.model;

import java.util.HashMap;
import java.util.Map;

public class I18nModel {
    private Map<String, String> translations = new HashMap<>();

    public Map<String, String> getTranslations() {
        return translations;
    }

    public void setTranslations(Map<String, String> translations) {
        this.translations = translations;
    }

    public I18nModel() {
    }

    public I18nModel(Map<String, String> translations) {
        this.translations = translations;
    }
}
