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
public class TaskRepresentation implements BaseRepresentation {

    @JsonProperty("id")
    private String id;

    @JsonProperty("processId")
    private String processId;

    @JsonProperty("processDefinitionId")
    private String processDefinitionId;

    @JsonProperty("activityDefinitionId")
    private String activityDefinitionId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("dueAt")
    private Date dueAt;

    @JsonProperty("startedAt")
    private Date startedAt;

    @JsonProperty("endedAt")
    private Date endedAt;

    @JsonProperty("durationInMs")
    private long durationInMs;

    @JsonProperty("priority")
    private int priority;

    @JsonProperty("owner")
    private String owner;

    @JsonProperty("assignee")
    private String assignee;

    @JsonProperty("formResourceKey")
    private String formResourceKey;

    @JsonProperty("state")
    private String state;

    @JsonProperty("variables")
    private List<VariableRepresentation> variables;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public String getActivityDefinitionId() {
        return activityDefinitionId;
    }

    public void setActivityDefinitionId(String activityDefinitionId) {
        this.activityDefinitionId = activityDefinitionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDueAt() {
        return dueAt;
    }

    public void setDueAt(Date dueAt) {
        this.dueAt = dueAt;
    }

    public Date getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Date startedAt) {
        this.startedAt = startedAt;
    }

    public Date getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(Date endedAt) {
        this.endedAt = endedAt;
    }

    public long getDurationInMs() {
        return durationInMs;
    }

    public void setDurationInMs(long durationInMs) {
        this.durationInMs = durationInMs;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getFormResourceKey() {
        return formResourceKey;
    }

    public void setFormResourceKey(String formResourceKey) {
        this.formResourceKey = formResourceKey;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<VariableRepresentation> getVariables() {
        return variables;
    }

    public void setVariables(List<VariableRepresentation> variables) {
        this.variables = variables;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class TaskRepresentation {, ");

        sb.append("    id: ").append(toIndentedString(id)).append(", ");
        sb.append("    processId: ").append(toIndentedString(processId)).append(", ");
        sb.append("    processDefinitionId: ").append(toIndentedString(processDefinitionId)).append(", ");
        sb.append("    activityDefinitionId: ").append(toIndentedString(activityDefinitionId)).append(", ");
        sb.append("    name: ").append(toIndentedString(name)).append(", ");
        sb.append("    description: ").append(toIndentedString(description)).append(", ");
        sb.append("    dueAt: ").append(toIndentedString(dueAt)).append(", ");
        sb.append("    startedAt: ").append(toIndentedString(startedAt)).append(", ");
        sb.append("    endedAt: ").append(toIndentedString(endedAt)).append(", ");
        sb.append("    durationInMs: ").append(toIndentedString(durationInMs)).append(", ");
        sb.append("    priority: ").append(toIndentedString(priority)).append(", ");
        sb.append("    owner: ").append(toIndentedString(owner)).append(", ");
        sb.append("    assignee: ").append(toIndentedString(assignee)).append(", ");
        sb.append("    formResourceKey: ").append(toIndentedString(formResourceKey)).append(", ");
        sb.append("    state: ").append(toIndentedString(state)).append(", ");
        sb.append("    variables: ").append(toIndentedString(variables)).append(", ");
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
        return o.toString().replace(", ", ",     ");
    }
}
