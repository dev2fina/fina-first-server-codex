package net.fina.ecm.alfresco.api.dictionary.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AssociationTarget implements BaseRepresentation {

    @JsonProperty("class")
    private String targetClass;

    @JsonProperty("mandatory")
    private boolean mandatory;

    @JsonProperty("many")
    private boolean many;

    public String getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(String sourceClass) {
        this.targetClass = sourceClass;
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
