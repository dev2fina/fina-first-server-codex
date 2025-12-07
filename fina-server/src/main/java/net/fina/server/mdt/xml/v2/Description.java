package net.fina.server.mdt.xml.v2;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DESCRIPTION")
public class Description {
    @XmlAttribute(name = "LANG_CODE")
    private String langCode;

    @XmlAttribute(name = "VALUE")
    private String value;

    public String getLangCode() {
        return langCode;
    }

    public Description() {
    }

    public Description(String langCode, String value) {
        this.langCode = langCode;
        this.value = value;
    }

    public void setLangCode(String langCode) {
        this.langCode = langCode;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
