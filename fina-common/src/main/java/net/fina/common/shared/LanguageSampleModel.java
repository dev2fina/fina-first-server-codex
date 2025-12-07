package net.fina.common.shared;

import java.io.Serializable;

public class LanguageSampleModel implements Serializable {
    private long id;
    private String code;
    private String name;
    private String encoding;
    private boolean defaultLanguage;

    public LanguageSampleModel() {
    }

    public LanguageSampleModel(long id, String code) {
        this.id = id;
        this.code = code;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public boolean isDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(boolean defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    @Override
    public String toString() {
        return "LanguageSampleModel{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", encoding='" + encoding + '\'' +
                ", defaultLanguage=" + defaultLanguage +
                '}';
    }
}
