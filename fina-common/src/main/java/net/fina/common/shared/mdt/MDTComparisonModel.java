package net.fina.common.shared.mdt;

import net.fina.common.client.mdt.MDTComparisonConditions;

import java.io.Serializable;

public class MDTComparisonModel implements Serializable {

    private long id;
    private MDTNodeModel node;
    private MDTComparisonConditions condition;
    private String leftEquation;
    private String equation;
    private String template;
    private String numberPattern;
    private Integer version;
    private ProcessStage processStage;

    public MDTComparisonModel() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public MDTNodeModel getNode() {
        return node;
    }

    public void setNode(MDTNodeModel node) {
        this.node = node;
    }

    public MDTComparisonConditions getCondition() {
        return condition;
    }

    public void setCondition(MDTComparisonConditions condition) {
        this.condition = condition;
    }

    public String getLeftEquation() {
        return leftEquation;
    }

    public void setLeftEquation(String leftEquation) {
        this.leftEquation = leftEquation;
    }

    public String getEquation() {
        return equation;
    }

    public void setEquation(String equation) {
        this.equation = equation;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getNumberPattern() {
        return numberPattern;
    }

    public void setNumberPattern(String numberPattern) {
        this.numberPattern = numberPattern;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public ProcessStage getProcessStage() {
        return processStage;
    }

    public void setProcessStage(ProcessStage processStage) {
        this.processStage = processStage;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((node == null) ? 0 : node.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MDTComparisonModel other = (MDTComparisonModel) obj;
        if (id != other.id)
            return false;
        if (node == null) {
            if (other.node != null)
                return false;
        } else if (!node.equals(other.node))
            return false;
        return true;
    }

}
