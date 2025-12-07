package net.fina.first.ecm.workflow.proxy;

import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.ecm.alfresco.api.workflow.body.TaskItemBodyCreate;
import net.fina.ecm.alfresco.api.workflow.model.TaskRepresentation;
import net.fina.ecm.alfresco.api.workflow.model.VariableRepresentation;
import net.fina.ecm.alfresco.api.workflow.model.VariableRepresentationSeparate;
import net.fina.ecm.alfresco.api.workflow.model.WorkflowProcessRepresentation;
import net.fina.first.common.exception.NodeException;
import net.fina.first.common.exception.WorkflowProcessException;
import net.fina.first.ecm.node.model.NodeMetaModel;
import net.fina.first.ecm.workflow.api.WorkflowLocal;
import net.fina.first.ecm.workflow.model.*;
import org.jboss.ejb3.annotation.SecurityDomain;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;

@Stateless
@RolesAllowed({PermissionIdNames.FIRST_REVIEW})
@SecurityDomain("FinASecurityDomain")
public class WorkflowProxySession {

    @Inject
    private WorkflowLocal workflowLocal;

    public WorkflowDetailsMetaModel getWorkflowDetails(String acceptLanguage, String processId) {
        return workflowLocal.getWorkflowDetails(acceptLanguage, processId);
    }

    public List<WorkflowProcessDefinitionMetaModel> getLastWorkflowProcessDefinitions(String acceptLanguage) {
        return workflowLocal.getLastWorkflowProcessDefinitions(acceptLanguage);
    }

    public WorkflowProcessDefinitionMetaModel getLastWorkflowProcessDefinition(String acceptLanguage, String processDefinitionKey) {
        return workflowLocal.getLastWorkflowProcessDefinition(acceptLanguage, processDefinitionKey);
    }

    public WorkflowProcessRepresentation createWorkflow(String acceptLanguage, String processDefinitionKey, WorkflowProcessBodyCreateMetaModel metaModel) throws WorkflowProcessException {
        return workflowLocal.createWorkflow(acceptLanguage, processDefinitionKey, metaModel);
    }

    public PaginatedListWrapper<WorkflowProcessMetaModel> getWorkflowProcesses(Integer start, Integer limit, String where) {
        return workflowLocal.getWorkflowProcesses(start, limit, where);
    }

    public PaginatedListWrapper<TaskMetaModel> getTasks(String acceptLanguage, Integer start, Integer limit, String where, String sort) {
        return workflowLocal.getTasks(acceptLanguage, start, limit, where, sort);
    }

    public PaginatedListWrapper<TaskItemMetaModel> getTaskItems(String taskId, Integer start, Integer limit) {
        return workflowLocal.getTaskItems(taskId, start, limit);
    }


    public PaginatedListWrapper<TaskItemMetaModel> getProcessItems(String processId, Integer start, Integer limit) {
        return workflowLocal.getProcessItems(processId, start, limit);
    }

    public void deleteProcessItem(String processId, String itemId) {
        workflowLocal.deleteProcessItem(processId, itemId);
    }


    public PaginatedListWrapper<TaskItemMetaModel> createProcessItems(String processId, List<TaskItemBodyCreate> taskItemBodyCreates) {
        return workflowLocal.createProcessItems(processId, taskItemBodyCreates);
    }

    public void deleteTaskItem(String taskId, String itemId) {
        workflowLocal.deleteTaskItem(taskId, itemId);
    }

    public PaginatedListWrapper<TaskItemMetaModel> createTaskItems(String taskId, List<TaskItemBodyCreate> taskItemBodyCreates) {
        return workflowLocal.createTaskItems(taskId, taskItemBodyCreates);
    }

    public PaginatedListWrapper<VariableRepresentationSeparate> createOrUpdateTaskVariables(String acceptLanguage, String taskId, List<VariableRepresentation> variables) {
        return workflowLocal.createOrUpdateTaskVariables(acceptLanguage, taskId, variables);
    }

    public TaskRepresentation updateTaskState(String taskId, String select, Map<String, Object> taskBody) throws WorkflowProcessException {
        return workflowLocal.updateTaskState(taskId, select, taskBody);
    }

    public Map<String, Object> addOrRemoveTaskVariableAssociation(String taskId, Map<String, Object> associations) {
        return workflowLocal.addOrRemoveTaskVariableAssociation(taskId, associations);
    }

    public String getProcessDefinitionImage(String processDefinitionId) {
        return workflowLocal.getProcessDefinitionImage(processDefinitionId);
    }

    public String getProcessImage(String processId) {
        return workflowLocal.getProcessImage(processId);
    }

    public TaskMetaModel getTask(String acceptLanguage, String taskId) {
        return workflowLocal.getTask(acceptLanguage, taskId);
    }

    public PaginatedListWrapper<TaskMetaModel> getProcessActiveTasks(String acceptLanguage, String processId) {
        return workflowLocal.getProcessActiveTasks(acceptLanguage, processId);
    }

    public Map<String, Object> getWorkflowVariables(String acceptLanguage, String processId) {
        return workflowLocal.getWorkflowVariables(acceptLanguage, processId);
    }

    public void deleteWorkflowProcess(String processId) {
        workflowLocal.deleteWorkflowProcess(processId);
    }

    public WorkflowProcessExternalMetaModel startBranchCreateWorkflowExternal(String acceptLanguage, String fiRegistryNodeId) throws WorkflowProcessException {
        return workflowLocal.startBranchCreateWorkflowExternal(acceptLanguage, fiRegistryNodeId);
    }

    public PaginatedListWrapper<WorkflowProcessExternalMetaModel> getWorkflowProcessesExternal(String acceptLanguage, NodeMetaModel actionNode, Integer start, Integer limit) {
        return workflowLocal.getWorkflowProcessesExternal(acceptLanguage, actionNode, start, limit);
    }

    public Map<String, Object> createWorkflowVariables(String acceptLanguage, String processId, List<VariableRepresentation> variables) {
        return workflowLocal.createWorkflowVariables(acceptLanguage, processId, variables);
    }

    public void finishWorkflow(String acceptLanguage, String processId, List<VariableRepresentation> variables) throws WorkflowProcessException {
        workflowLocal.finishWorkflow(acceptLanguage, processId, variables);
    }

    public void changeProcessAssignee(String acceptLanguage, String processId, Map<String, Object> newAssigneeData) throws WorkflowProcessException, NodeException {
        workflowLocal.changeProcessAssignee(acceptLanguage, processId, newAssigneeData);
    }
}

