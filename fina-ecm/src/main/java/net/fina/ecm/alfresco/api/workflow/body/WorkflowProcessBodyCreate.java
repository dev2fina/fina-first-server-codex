package net.fina.ecm.alfresco.api.workflow.body;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkflowProcessBodyCreate implements BaseRepresentation {

    @JsonProperty("processDefinitionKey")
    protected String processDefinitionKey;

    @JsonProperty("variables")
    protected Map<String, Object> variables;

    public WorkflowProcessBodyCreate(String processDefinitionKey, Map<String, Object> variables) {
        this.processDefinitionKey = processDefinitionKey;
        this.variables = variables;
    }

    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }

    public void setProcessDefinitionKey(String processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }
}
