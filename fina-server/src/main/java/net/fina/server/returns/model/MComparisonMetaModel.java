package net.fina.server.returns.model;


import net.fina.common.client.mdt.MDTComparisonConditions;
import net.fina.common.shared.mdt.ProcessStage;
import net.fina.server.processing.model.ComparisonItem;

import java.io.Serializable;

public class MComparisonMetaModel implements Serializable {
    private long nodeId;
    private MDTComparisonConditions condition;
    private String leftEquation;
    private String equation;
    private String numberPattern;
    private String messageTemplate;
    private ProcessStage processStage;

    public long getNodeId() {
        return nodeId;
    }

    public void setNodeId(long nodeId) {
        this.nodeId = nodeId;
    }

    public MDTComparisonConditions getCondition() {
        return condition;
    }

    public void setCondition(MDTComparisonConditions condition) {
        this.condition = condition;
    }

    public String getEquation() {
        return equation;
    }

    public void setEquation(String equation) {
        this.equation = equation;
    }

    public String getLeftEquation() {
        return leftEquation;
    }

    public void setLeftEquation(String leftEquation) {
        this.leftEquation = leftEquation;
    }

    public String getNumberPattern() {
        return numberPattern;
    }

    public void setNumberPattern(String numberPattern) {
        this.numberPattern = numberPattern;
    }

    public String getMessageTemplate() {
        return messageTemplate;
    }

    public void setMessageTemplate(String messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    public ProcessStage getProcessStage() {
        return processStage;
    }

    public void setProcessStage(ProcessStage processStage) {
        this.processStage = processStage;
    }

    public MComparisonMetaModel setComparisonItem(ComparisonItem comparisonItem) {
        this.nodeId = comparisonItem.nodeId;
        this.condition = comparisonItem.condition;
        this.leftEquation = comparisonItem.leftEquation;
        this.equation = comparisonItem.equation;
        this.numberPattern = comparisonItem.numberPattern;
        this.messageTemplate = comparisonItem.messageTemplate;
        this.processStage = comparisonItem.processStage;
        return this;
    }
}
