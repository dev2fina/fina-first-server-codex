package net.fina.common.client.rvc;

import net.fina.common.client.mdt.MDTNodeDataTypes;
import net.fina.common.client.mdt.MDTNodeEvalMethods;
import net.fina.common.client.mdt.MDTNodeTypes;

import java.util.Collection;
import java.util.List;

public class NodeXmlMetaModel {

    private String code;

    private MDTNodeTypes type;

    private MDTNodeDataTypes dataType;

    private String equation;

    private Long sequence;

    private MDTNodeEvalMethods evalMethod;

    private Boolean disabled;

    private Boolean required;

    private List<NodeXmlMetaModel> children;

    private Collection<String> dependentNodeCodes;

    private Collection<DescriptionXmlMetaModel> descriptions;

    private Collection<ComparisonXmlMetaModel> comparisons;

    private OptionalXmlMetaModel optional;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public MDTNodeTypes getType() {
        return type;
    }

    public void setType(MDTNodeTypes type) {
        this.type = type;
    }

    public MDTNodeDataTypes getDataType() {
        return dataType;
    }

    public void setDataType(MDTNodeDataTypes dataType) {
        this.dataType = dataType;
    }

    public String getEquation() {
        return equation;
    }

    public void setEquation(String equation) {
        this.equation = equation;
    }

    public Long getSequence() {
        return sequence;
    }

    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }

    public MDTNodeEvalMethods getEvalMethod() {
        return evalMethod;
    }

    public void setEvalMethod(MDTNodeEvalMethods evalMethod) {
        this.evalMethod = evalMethod;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public List<NodeXmlMetaModel> getChildren() {
        return children;
    }

    public void setChildren(List<NodeXmlMetaModel> children) {
        this.children = children;
    }

    public Collection<String> getDependentNodeCodes() {
        return dependentNodeCodes;
    }

    public void setDependentNodeCodes(Collection<String> dependentNodeCodes) {
        this.dependentNodeCodes = dependentNodeCodes;
    }

    public Collection<DescriptionXmlMetaModel> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(Collection<DescriptionXmlMetaModel> descriptions) {
        this.descriptions = descriptions;
    }

    public Collection<ComparisonXmlMetaModel> getComparisons() {
        return comparisons;
    }

    public void setComparisons(Collection<ComparisonXmlMetaModel> comparisons) {
        this.comparisons = comparisons;
    }

    public OptionalXmlMetaModel getOptional() {
        return optional;
    }

    public void setOptional(OptionalXmlMetaModel optional) {
        this.optional = optional;
    }
}
