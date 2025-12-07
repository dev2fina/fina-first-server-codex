package net.fina.first.ecm.workflow.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.fina.ecm.alfresco.api.workflow.body.TaskItemBodyCreate;

import java.util.List;
import java.util.Map;

public class WorkflowProcessBodyCreateMetaModel {

    @JsonProperty("variables")
    private Map<String, Object> variables;

    @JsonProperty("items")
    private List<TaskItemBodyCreate> items;

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    public List<TaskItemBodyCreate> getItems() {
        return items;
    }

    public void setItems(List<TaskItemBodyCreate> items) {
        this.items = items;
    }
}
