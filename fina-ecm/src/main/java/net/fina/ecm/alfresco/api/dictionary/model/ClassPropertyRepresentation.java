package net.fina.ecm.alfresco.api.dictionary.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClassPropertyRepresentation implements BaseRepresentation {

    @JsonProperty("name")
    private String name;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("defaultValues")
    private String defaultValues;

    @JsonProperty("dataType")
    private String dataType;

    @JsonProperty("multiValued")
    private boolean multiValued;

    @JsonProperty("mandatory")
    private boolean mandatory;

    @JsonProperty("enforced")
    private boolean enforced;

    @JsonProperty("indexed")
    private boolean indexed;

    @JsonProperty("indexedAtomically")
    private boolean indexedAtomically;

    @JsonProperty("constraints")
    private List<ConstraintRepresentation> constraints;

    @JsonProperty("url")
    private String url;

    @JsonProperty("protected")
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

    public List<ConstraintRepresentation> getConstraints() {
        return constraints;
    }

    public void setConstraints(List<ConstraintRepresentation> constraints) {
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ClassPropertyRepresentation {");
        sb.append("    name: ").append(toIndentedString(name)).append(", ");
        sb.append("    title: ").append(toIndentedString(title)).append(", ");
        sb.append("    description: ").append(toIndentedString(description)).append(", ");
        sb.append("    defaultValues: ").append(toIndentedString(defaultValues)).append(", ");
        sb.append("    dataType: ").append(toIndentedString(dataType)).append(", ");
        sb.append("    multiValued: ").append(toIndentedString(multiValued)).append(", ");
        sb.append("    mandatory: ").append(toIndentedString(mandatory)).append(", ");
        sb.append("    enforced: ").append(toIndentedString(enforced)).append(", ");
        sb.append("    indexed: ").append(toIndentedString(indexed)).append(", ");
        sb.append("    indexedAtomically: ").append(toIndentedString(indexedAtomically)).append(", ");
        sb.append("    constraints: ").append(toIndentedString(constraints)).append(", ");
        sb.append("    url: ").append(toIndentedString(url));
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
