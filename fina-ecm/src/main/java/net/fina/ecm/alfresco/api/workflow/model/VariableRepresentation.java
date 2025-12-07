package net.fina.ecm.alfresco.api.workflow.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VariableRepresentation implements BaseRepresentation {

    @JsonProperty("scope")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String scope;

    @JsonProperty("name")
    private String name;

    @JsonProperty("value")
    private Object value;

    @JsonProperty("type")
    private String type;

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class VariableRepresentation {");
        sb.append("    scope: ").append(toIndentedString(scope)).append(", ");
        sb.append("    name: ").append(toIndentedString(name)).append(", ");
        sb.append("    value: ").append(toIndentedString(value)).append(", ");
        sb.append("    type: ").append(toIndentedString(type));
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
