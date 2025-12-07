package net.fina.first.ecm.questionnaire.model;

import java.io.Serializable;

public class QuestionnaireGroupMetaModel implements Serializable {
    private String id;
    private String code;
    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
