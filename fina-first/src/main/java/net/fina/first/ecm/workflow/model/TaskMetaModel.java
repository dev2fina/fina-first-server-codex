package net.fina.first.ecm.workflow.model;

import net.fina.ecm.alfresco.api.workflow.model.FormRepresentation;
import net.fina.ecm.alfresco.api.workflow.model.VariableRepresentation;

import java.util.Date;
import java.util.List;

public class TaskMetaModel {
    private String id;
    private String processId;
    private String processDefinitionId;
    private String activityDefinitionId;
    private String name;
    private String description;
    private Date dueAt;
    private Date startedAt;
    private Date endedAt;
    private long durationInMs;
    private int priority;
    private String owner;
    private String assignee;
    private String formResourceKey;
    private String state;
    private List<VariableRepresentation> taskVariables;
    private List<FormRepresentation> taskForm;
    private String fiRegistryId;

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

    public List<VariableRepresentation> getTaskVariables() {
        return taskVariables;
    }

    public void setTaskVariables(List<VariableRepresentation> taskVariables) {
        this.taskVariables = taskVariables;
    }

    public List<FormRepresentation> getTaskForm() {
        return taskForm;
    }

    public void setTaskForm(List<FormRepresentation> taskForm) {
        this.taskForm = taskForm;
    }

    public String getFiRegistryId() {
        return fiRegistryId;
    }

    public void setFiRegistryId(String fiRegistryId) {
        this.fiRegistryId = fiRegistryId;
    }
}
