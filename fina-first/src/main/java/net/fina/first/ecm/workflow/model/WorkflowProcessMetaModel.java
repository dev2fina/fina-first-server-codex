package net.fina.first.ecm.workflow.model;

import net.fina.ecm.alfresco.api.workflow.model.FormRepresentation;
import net.fina.ecm.alfresco.api.workflow.model.VariableRepresentation;

import java.util.Date;
import java.util.List;

public class WorkflowProcessMetaModel {
    private String processDefinitionId;
    private String startUserId;
    private String startActivityId;
    private Date startedAt;
    private String id;
    private boolean completed;
    private String processDefinitionKey;
    private List<VariableRepresentation> processVariables;
    private List<FormRepresentation> processStartForm;

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

    public List<FormRepresentation> getProcessStartForm() {
        return processStartForm;
    }

    public void setProcessStartForm(List<FormRepresentation> processStartForm) {
        this.processStartForm = processStartForm;
    }
}
