package net.fina.ecm.alfresco.api.workflow.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import net.fina.ecm.alfresco.api.common.representation.AbstractRepresentation;
import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

import java.util.List;

@JsonTypeName(value = "entry")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FormRepresentation extends AbstractRepresentation {

    @JsonProperty("dataType")
    private String dataType;

    @JsonProperty("title")
    private String title;

    @JsonProperty("qualifiedName")
    private String qualifiedName;

    @JsonProperty("name")
    private String name;

    @JsonProperty("required")
    private boolean required;

    @JsonProperty("defaultValue")
    private String defaultValue;

    @JsonProperty("allowedValues")
    private List<String> allowedValues;

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public void setQualifiedName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public List<String> getAllowedValues() {
        return allowedValues;
    }

    public void setAllowedValues(List<String> allowedValues) {
        this.allowedValues = allowedValues;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class FormRepresentation {");
        sb.append("    dataType: ").append(toIndentedString(dataType)).append(", ");
        sb.append("    title: ").append(toIndentedString(title)).append(", ");
        sb.append("    qualifiedName: ").append(toIndentedString(qualifiedName)).append(", ");
        sb.append("    name: ").append(toIndentedString(name)).append(", ");
        sb.append("    required: ").append(toIndentedString(required)).append(", ");
        sb.append("    defaultValue: ").append(toIndentedString(defaultValue)).append(", ");
        sb.append("    allowedValues: ").append(toIndentedString(allowedValues)).append(" ");
        sb.append("}");
        return sb.toString();
    }



}
