package net.fina.first.ecm.workflow.api;

import net.fina.common.shared.PaginatedListWrapper;
import net.fina.ecm.alfresco.api.workflow.body.TaskItemBodyCreate;
import net.fina.ecm.alfresco.api.workflow.model.TaskRepresentation;
import net.fina.ecm.alfresco.api.workflow.model.VariableRepresentation;
import net.fina.ecm.alfresco.api.workflow.model.VariableRepresentationSeparate;
import net.fina.ecm.alfresco.api.workflow.model.WorkflowProcessRepresentation;
import net.fina.first.common.exception.NodeException;
import net.fina.first.common.exception.WorkflowProcessException;
import net.fina.first.ecm.node.model.NodeMetaModel;
import net.fina.first.ecm.workflow.model.*;

import java.util.List;
import java.util.Map;

public interface WorkflowLocal {
    WorkflowDetailsMetaModel getWorkflowDetails(String acceptLanguage, String processId);

    List<WorkflowProcessDefinitionMetaModel> getLastWorkflowProcessDefinitions(String acceptLanguage);

    WorkflowProcessDefinitionMetaModel getLastWorkflowProcessDefinition(String acceptLanguage, String processDefinitionKey);

    WorkflowProcessRepresentation createWorkflow(String acceptLanguage, String processDefinitionKey, WorkflowProcessBodyCreateMetaModel metaModel) throws WorkflowProcessException;

    PaginatedListWrapper<WorkflowProcessMetaModel> getWorkflowProcesses(Integer start, Integer limit, String where);

    PaginatedListWrapper<TaskMetaModel> getTasks(String acceptLanguage, Integer start, Integer limit, String where, String sort);

    PaginatedListWrapper<TaskItemMetaModel> getTaskItems(String taskId, Integer start, Integer limit);

    PaginatedListWrapper<TaskItemMetaModel> getProcessItems(String processId, Integer start, Integer limit);

    void deleteProcessItem(String processId, String itemId);

    PaginatedListWrapper<TaskItemMetaModel> createProcessItems(String processId, List<TaskItemBodyCreate> taskItemBodyCreates);

    void deleteTaskItem(String taskId, String itemId);

    PaginatedListWrapper<TaskItemMetaModel> createTaskItems(String taskId, List<TaskItemBodyCreate> taskItemBodyCreates);

    PaginatedListWrapper<VariableRepresentationSeparate> createOrUpdateTaskVariables(String acceptLanguage, String taskId, List<VariableRepresentation> variables);

    TaskRepresentation updateTaskState(String taskId, String select, Map<String, Object> taskBody) throws WorkflowProcessException;

    Map<String, Object> addOrRemoveTaskVariableAssociation(String taskId, Map<String, Object> associations);

    String getProcessDefinitionImage(String processDefinitionId);

    String getProcessImage(String processId);

    TaskMetaModel getTask(String acceptLanguage, String taskId);

    PaginatedListWrapper<TaskMetaModel> getProcessActiveTasks(String acceptLanguage, String processId);

    Map<String, Object> getWorkflowVariables(String acceptLanguage, String processId);

    void deleteWorkflowProcess(String processId);

    // currently used for DCS
    WorkflowProcessExternalMetaModel startBranchCreateWorkflowExternal(String acceptLanguage, String fiRegistryNodeId) throws WorkflowProcessException;

    // currently used for DCS
    WorkflowProcessExternalMetaModel startBranchEditWorkflowExternal(String acceptLanguage, String fiRegistryNodeId) throws WorkflowProcessException;

    // currently used for DCS
    PaginatedListWrapper<WorkflowProcessExternalMetaModel> getWorkflowProcessesExternal(String acceptLanguage, NodeMetaModel actionNode, Integer start, Integer limit);

    Map<String, Object> createWorkflowVariables(String acceptLanguage, String processId, List<VariableRepresentation> variables);

    void finishWorkflow(String acceptLanguage, String processId, List<VariableRepresentation> variables) throws WorkflowProcessException;

    void changeProcessAssignee(String acceptLanguage, String processId, Map<String, Object> newAssigneeData) throws WorkflowProcessException, NodeException;
}
