package net.fina.common.shared.jcr;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class DescriptionModel implements Serializable {
    @JsonProperty("lc")
    private String langCode;
    @JsonProperty("dc")
    private String description;

    public DescriptionModel() {
    }

    public DescriptionModel(String langCode, String description) {
        this.langCode = langCode;
        this.description = description;
    }


    public String getLangCode() {
        return langCode;
    }

    public void setLangCode(String langCode) {
        this.langCode = langCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
