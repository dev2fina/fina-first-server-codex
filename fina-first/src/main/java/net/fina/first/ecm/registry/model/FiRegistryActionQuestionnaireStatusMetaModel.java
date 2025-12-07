package net.fina.first.ecm.registry.model;

import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public class FiRegistryActionQuestionnaireStatusMetaModel implements Serializable {
    private String id;
    private int sequence;
    private String question;
    private Object questionnaireId;
    private String note;
    private FiRegistryActionQuestionnaireStatusState state;
    private String status;
    private Date relevanceTime;
    private boolean predefined;
    private boolean obligatory;
    private String questionnaireParentId;
    private String questionnaireGroupName;
    private int checkSize;
    private String questionnaireTypeGroup;
    private boolean isSubTypeQuestionnaire;
    private NodeRepresentation relativeNode;
    private String questionnairePropertyName;
    private String code;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Object getQuestionnaireId() {
        return questionnaireId;
    }

    public void setQuestionnaireId(Object questionnaireId) {
        this.questionnaireId = questionnaireId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public FiRegistryActionQuestionnaireStatusState getState() {
        return state;
    }

    public void setState(FiRegistryActionQuestionnaireStatusState state) {
        this.state = state;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getRelevanceTime() {
        return relevanceTime;
    }

    public void setRelevanceTime(Date relevanceTime) {
        this.relevanceTime = relevanceTime;
    }

    public boolean isPredefined() {
        return predefined;
    }

    public void setPredefined(boolean predefined) {
        this.predefined = predefined;
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

    public int getCheckSize() {
        return checkSize;
    }

    public void setCheckSize(int checkSize) {
        this.checkSize = checkSize;
    }

    public String getQuestionnaireTypeGroup() {
        return questionnaireTypeGroup;
    }

    public void setQuestionnaireTypeGroup(String questionnaireTypeGroup) {
        this.questionnaireTypeGroup = questionnaireTypeGroup;
    }

    public boolean isSubTypeQuestionnaire() {
        return isSubTypeQuestionnaire;
    }

    public void setSubTypeQuestionnaire(boolean subTypeQuestionnaire) {
        isSubTypeQuestionnaire = subTypeQuestionnaire;
    }

    public NodeRepresentation getRelativeNode() {
        return relativeNode;
    }

    public void setRelativeNode(NodeRepresentation relativeNode) {
        this.relativeNode = relativeNode;
    }

    public String getQuestionnairePropertyName() {
        return questionnairePropertyName;
    }

    public void setQuestionnairePropertyName(String questionnairePropertyName) {
        this.questionnairePropertyName = questionnairePropertyName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
