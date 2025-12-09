package net.fina.ecm.alfresco.api.dictionary.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

import java.util.List;
import java.util.Map;

public class ConstraintRepresentation implements BaseRepresentation {

    @JsonProperty("type")
    private String type;

    @JsonProperty("parameters")
    private List<Map<String,Object>> parameters;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Map<String, Object>> getParameters() {
        return parameters;
    }

    public void setParameters(List<Map<String, Object>> parameters) {
        this.parameters = parameters;
    }
}
