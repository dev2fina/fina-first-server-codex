package net.fina.first.ecm.workflow.model;

import java.util.List;

public class WorkflowDetailsMetaModel {
    private String name;
    private String description;
    private String title;
    private String processDefinitionId;
    private WorkflowProcessMetaModel processMetaModel;
    private List<TaskMetaModel> activeTasks;
    private List<TaskMetaModel> completedTasks;
    private String startFormResourceKey;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public WorkflowProcessMetaModel getProcessMetaModel() {
        return processMetaModel;
    }

    public void setProcessMetaModel(WorkflowProcessMetaModel processMetaModel) {
        this.processMetaModel = processMetaModel;
    }

    public List<TaskMetaModel> getActiveTasks() {
        return activeTasks;
    }

    public void setActiveTasks(List<TaskMetaModel> activeTasks) {
        this.activeTasks = activeTasks;
    }

    public List<TaskMetaModel> getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(List<TaskMetaModel> completedTasks) {
        this.completedTasks = completedTasks;
    }

    public String getStartFormResourceKey() {
        return startFormResourceKey;
    }

    public void setStartFormResourceKey(String startFormResourceKey) {
        this.startFormResourceKey = startFormResourceKey;
    }
}
