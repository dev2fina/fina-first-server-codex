package net.fina.server.mdt.xml.v2;

import net.fina.common.client.mdt.MDTComparisonConditions;
import net.fina.server.mdt.entity.MDTComparison;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "COMPARISON", propOrder = {"condition", "template", "leftEquation", "rightEquation", "numberPattern"})
public class Comparison {

    @XmlAttribute(name = "LEFT_EQUATION")
    private String leftEquation;

    @XmlAttribute(name = "RIGHT_EQUATION")
    private String rightEquation;

    @XmlAttribute(name = "CONDITION")
    private MDTComparisonConditions condition;

    @XmlAttribute(name = "TEMPLATE")
    private String template;

    @XmlAttribute(name = "NUMBER_PATTERN")
    private String numberPattern;

    public Comparison() {
    }

    public Comparison(String leftEquation, String rightEquation, MDTComparisonConditions condition, String template, String numberPattern) {
        setLeftEquation(leftEquation);
        setRightEquation(rightEquation);
        setCondition(condition);
        setTemplate(template);
        setNumberPattern(numberPattern);
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

    public String getNumberPattern() {
        return numberPattern;
    }

    public void setNumberPattern(String numberPattern) {
        this.numberPattern = numberPattern;
    }

    public MDTComparison asMDTComparison() {
        MDTComparison comparison = new MDTComparison();
        comparison.setLeftEquation(getLeftEquation().trim());
        comparison.setRightEquation(getRightEquation().trim());
        comparison.setCondition(condition);
        comparison.setTemplate(template);
        comparison.setNumberPattern(numberPattern);
        return comparison;
    }

}
