package net.fina.server.i18n.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Entity(name = "SYS_TRANSLATIONS")
@Table(name = "SYS_TRANSLATIONS")
public class I18nEntity implements Serializable {
    @Id
    @Column(name = "KEY_CODE")
    private String key;
    @Id
    @Column(name = "LANG_CODE", nullable = false)
    private String langCode;
    @Column(name = "VALUE")
    private String value;

    public I18nEntity() {
    }

    public I18nEntity(String key, String value, String langCode) {
        this.key = key;
        this.value = value;
        this.langCode = langCode;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLangCode() {
        return langCode;
    }

    public void setLangCode(String langCode) {
        this.langCode = langCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        I18nEntity that = (I18nEntity) o;
        return Objects.equals(getKey(), that.getKey()) && Objects.equals(getLangCode(), that.getLangCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getLangCode());
    }
}
