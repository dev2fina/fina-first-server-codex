package net.fina.first.ecm.dictionary.model;

import java.util.List;

public class ClassPropertyMetaModel {
    private String name;
    private String title;
    private String description;
    private String defaultValues;
    private String dataType;
    private boolean multiValued;
    private boolean mandatory;
    private boolean enforced;
    private boolean indexed;
    private boolean indexedAtomically;
    private List<ConstraintMetaModel> constraints;
    private String url;
    private boolean protectedValue;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDefaultValues() {
        return defaultValues;
    }

    public void setDefaultValues(String defaultValues) {
        this.defaultValues = defaultValues;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public boolean isMultiValued() {
        return multiValued;
    }

    public void setMultiValued(boolean multiValued) {
        this.multiValued = multiValued;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public boolean isEnforced() {
        return enforced;
    }

    public void setEnforced(boolean enforced) {
        this.enforced = enforced;
    }

    public boolean isIndexed() {
        return indexed;
    }

    public void setIndexed(boolean indexed) {
        this.indexed = indexed;
    }

    public boolean isIndexedAtomically() {
        return indexedAtomically;
    }

    public void setIndexedAtomically(boolean indexedAtomically) {
        this.indexedAtomically = indexedAtomically;
    }

    public List<ConstraintMetaModel> getConstraints() {
        return constraints;
    }

    public void setConstraints(List<ConstraintMetaModel> constraints) {
        this.constraints = constraints;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isProtectedValue() {
        return protectedValue;
    }

    public void setProtectedValue(boolean protectedValue) {
        this.protectedValue = protectedValue;
    }
}
