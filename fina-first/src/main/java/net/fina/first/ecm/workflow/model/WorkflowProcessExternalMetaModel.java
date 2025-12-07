package net.fina.first.ecm.workflow.model;

import java.util.Date;

public class WorkflowProcessExternalMetaModel {
    private String processDefinitionId;
    private String processDefinitionKey;
    private Date startedAt;
    private boolean completed;
    private String description;
    private Boolean isProcessSubmittedExternal;

    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }

    public void setProcessDefinitionKey(String processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
    }

    public Date getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Date startedAt) {
        this.startedAt = startedAt;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean isProcessSubmittedExternal() {
        return isProcessSubmittedExternal;
    }

    public void setProcessSubmittedExternal(Boolean processSubmittedExternal) {
        isProcessSubmittedExternal = processSubmittedExternal;
    }
}
