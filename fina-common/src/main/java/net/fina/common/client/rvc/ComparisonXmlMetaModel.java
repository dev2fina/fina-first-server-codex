package net.fina.common.client.rvc;

import net.fina.common.client.mdt.MDTComparisonConditions;

public class ComparisonXmlMetaModel {

    private String leftEquation;
    private String rightEquation;
    private MDTComparisonConditions condition;
    private String template;

    public String getLeftEquation() {
        return leftEquation;
    }

    public void setLeftEquation(String leftEquation) {
        this.leftEquation = leftEquation;
    }

    public String getRightEquation() {
        return rightEquation;
    }

    public void setRightEquation(String rightEquation) {
        this.rightEquation = rightEquation;
    }

    public MDTComparisonConditions getCondition() {
        return condition;
    }

    public void setCondition(MDTComparisonConditions condition) {
        this.condition = condition;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }
}
