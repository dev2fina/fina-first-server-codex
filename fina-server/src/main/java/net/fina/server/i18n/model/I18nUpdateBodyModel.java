package net.fina.server.i18n.model;

public class I18nUpdateBodyModel {
    private  String key;
    private  String value;
    private  String langCode;

    public I18nUpdateBodyModel() {
    }

    public I18nUpdateBodyModel(String key, String value, String langCode) {
        this.key = key;
        this.value = value;
        this.langCode = langCode;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public String getLangCode() {
        return langCode;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setLangCode(String langCode) {
        this.langCode = langCode;
    }
}
