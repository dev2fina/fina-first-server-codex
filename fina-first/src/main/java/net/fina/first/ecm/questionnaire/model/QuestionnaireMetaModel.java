package net.fina.first.ecm.questionnaire.model;

import net.fina.first.ecm.fi.model.FiTypeMetaModel;

import java.io.Serializable;
import java.util.Objects;

public class QuestionnaireMetaModel implements Serializable {
    private String id;
    private String question;
    private QuestionnaireGroupMetaModel group;
    private FiTypeMetaModel fiType;
    private boolean obligatory;
    private String questionnaireParentId;
    private String questionnaireGroupName;
    private int sequence;
    private int checkSize;
    private String defaultValue;
    private String code;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public QuestionnaireGroupMetaModel getGroup() {
        return group;
    }

    public void setGroup(QuestionnaireGroupMetaModel group) {
        this.group = group;
    }

    public FiTypeMetaModel getFiType() {
        return fiType;
    }

    public void setFiType(FiTypeMetaModel fiType) {
        this.fiType = fiType;
    }

    public boolean isObligatory() {
        return obligatory;
    }

    public void setObligatory(boolean obligatory) {
        this.obligatory = obligatory;
    }

    public String getQuestionnaireParentId() {
        return questionnaireParentId;
    }

    public void setQuestionnaireParentId(String questionnaireParentId) {
        this.questionnaireParentId = questionnaireParentId;
    }

    public String getQuestionnaireGroupName() {
        return questionnaireGroupName;
    }

    public void setQuestionnaireGroupName(String questionnaireGroupName) {
        this.questionnaireGroupName = questionnaireGroupName;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public int getCheckSize() {
        return checkSize;
    }

    public void setCheckSize(int checkSize) {
        this.checkSize = checkSize;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuestionnaireMetaModel)) return false;
        QuestionnaireMetaModel that = (QuestionnaireMetaModel) o;
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
