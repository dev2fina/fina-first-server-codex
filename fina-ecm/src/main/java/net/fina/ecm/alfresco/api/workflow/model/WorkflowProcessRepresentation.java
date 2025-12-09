package net.fina.ecm.alfresco.api.workflow.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

import java.util.Date;
import java.util.List;

@JsonTypeName(value = "entry")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkflowProcessRepresentation implements BaseRepresentation {

    @JsonProperty("processDefinitionId")
    private String processDefinitionId;

    @JsonProperty("startUserId")
    private String startUserId;

    @JsonProperty("startActivityId")
    private String startActivityId;

    @JsonProperty("startedAt")
    private Date startedAt;

    @JsonProperty("id")
    private String id;

    @JsonProperty("completed")
    private boolean completed;

    @JsonProperty("processDefinitionKey")
    private String processDefinitionKey;

    @JsonProperty("processVariables")
    private List<VariableRepresentation> processVariables;

    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public String getStartUserId() {
        return startUserId;
    }

    public void setStartUserId(String startUserId) {
        this.startUserId = startUserId;
    }

    public String getStartActivityId() {
        return startActivityId;
    }

    public void setStartActivityId(String startActivityId) {
        this.startActivityId = startActivityId;
    }

    public Date getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Date startedAt) {
        this.startedAt = startedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }

    public void setProcessDefinitionKey(String processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
    }

    public List<VariableRepresentation> getProcessVariables() {
        return processVariables;
    }

    public void setProcessVariables(List<VariableRepresentation> processVariables) {
        this.processVariables = processVariables;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class WorkflowProcessRepresentation {");

        sb.append("    processDefinitionId: ").append(toIndentedString(processDefinitionId)).append(", ");
        sb.append("    startUserId: ").append(toIndentedString(startUserId)).append(", ");
        sb.append("    startActivityId: ").append(toIndentedString(startActivityId)).append(", ");
        sb.append("    startedAt: ").append(toIndentedString(startedAt)).append(", ");
        sb.append("    id: ").append(toIndentedString(id)).append(", ");
        sb.append("    completed: ").append(toIndentedString(completed)).append(", ");
        sb.append("    processDefinitionKey: ").append(toIndentedString(processDefinitionKey)).append(", ");
        sb.append("    processVariables: ").append(toIndentedString(processVariables));
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
