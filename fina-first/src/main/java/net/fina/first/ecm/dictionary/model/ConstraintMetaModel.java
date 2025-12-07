package net.fina.first.ecm.dictionary.model;

import java.util.List;
import java.util.Map;

public class ConstraintMetaModel {
    private String type;
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
