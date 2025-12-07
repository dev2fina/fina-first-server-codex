package net.fina.first.ecm.dictionary.model;

public class AssociationSourceTargetMetaModel {
    private String className;
    private boolean mandatory;
    private boolean many;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public boolean isMany() {
        return many;
    }

    public void setMany(boolean many) {
        this.many = many;
    }
}
