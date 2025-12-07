package net.fina.first.ecm.questionnaire.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionnaireManagementModel extends QuestionnaireMetaModel implements Serializable {
    private List<String> groupedIds;
    private QuestionnaireMetaModel parentNode;

    public QuestionnaireManagementModel() {
    }

    public QuestionnaireManagementModel(QuestionnaireMetaModel model) {
        this.setId(model.getId());
        this.setQuestion(model.getQuestion());
        this.setGroup(model.getGroup());
        this.setFiType(model.getFiType());
        this.setObligatory(model.isObligatory());
        this.setQuestionnaireParentId(model.getQuestionnaireParentId());
        this.setQuestionnaireGroupName(model.getQuestionnaireGroupName());
        this.setSequence(model.getSequence());
        this.setCheckSize(model.getCheckSize());
    }


    public QuestionnaireManagementModel(List<String> groupedIds) {
        this.groupedIds = groupedIds;
    }

    public List<String> getGroupedIds() {
        return groupedIds != null ? groupedIds : new ArrayList<>();
    }

    public void setGroupedIds(List<String> groupedIds) {
        this.groupedIds = groupedIds;
    }

    public QuestionnaireMetaModel getParentNode() {
        return parentNode;
    }

    public void setParentNode(QuestionnaireMetaModel parentNode) {
        this.parentNode = parentNode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        QuestionnaireManagementModel that = (QuestionnaireManagementModel) o;
        return getGroupedIds().equals(that.getGroupedIds()) &&
                getParentNode().equals(that.getParentNode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }
}

