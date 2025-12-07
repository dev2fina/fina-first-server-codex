package net.fina.first.ecm.workflow.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.fina.common.server.util.ConfigurationUtil;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.common.shared.SortField;
import net.fina.common.shared.ecm.model.ECMGroupMetaModel;
import net.fina.common.shared.ecm.model.ECMPersonMetaModel;
import net.fina.ecm.alfresco.AlfrescoClient;
import net.fina.ecm.alfresco.api.common.constant.APIConstants;
import net.fina.ecm.alfresco.api.common.parameters.IncludeParam;
import net.fina.ecm.alfresco.api.common.parameters.OrderByParam;
import net.fina.ecm.alfresco.api.common.representation.ResultPaging;
import net.fina.ecm.alfresco.api.common.representation.UnexpectedErrorRepresentation;
import net.fina.ecm.alfresco.api.core.model.body.NodeBodyUpdate;
import net.fina.ecm.alfresco.api.core.model.representation.PersonRepresentation;
import net.fina.ecm.alfresco.api.dictionary.model.ClassPropertyRepresentation;
import net.fina.ecm.alfresco.api.workflow.body.TaskItemBodyCreate;
import net.fina.ecm.alfresco.api.workflow.body.WorkflowProcessBodyCreate;
import net.fina.ecm.alfresco.api.workflow.model.*;
import net.fina.ecm.util.AlfrescoConfiguration;
import net.fina.ecm.util.AlfrescoPropConstants;
import net.fina.first.FirstUtil;
import net.fina.first.common.exception.NodeException;
import net.fina.first.common.exception.WorkflowProcessException;
import net.fina.first.ecm.EcmConstants;
import net.fina.first.ecm.client.proxy.EcmClientProxySession;
import net.fina.first.ecm.dictionary.model.ClassPropertyModelHelper;
import net.fina.first.ecm.group.proxy.GroupProxySession;
import net.fina.first.ecm.node.api.NodeLocal;
import net.fina.first.ecm.node.api.NodeStatus;
import net.fina.first.ecm.node.model.NodeMetaModel;
import net.fina.first.ecm.notification.proxy.ECMNotificationInfoProxySession;
import net.fina.first.ecm.people.proxy.PeopleProxySession;
import net.fina.first.ecm.workflow.api.WorkflowLocal;
import net.fina.first.ecm.workflow.model.*;
import net.fina.first.interceptors.FirstRecordingAuditor;
import org.jboss.logging.Logger;

import jakarta.annotation.Resource;
import jakarta.ejb.Local;
import jakarta.ejb.SessionContext;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.core.Response;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
@Local(WorkflowLocal.class)
@Interceptors(FirstRecordingAuditor.class)
public class WorkflowSession implements WorkflowLocal {

    private final Logger log = Logger.getLogger(getClass());

    @Inject
    private EcmClientProxySession ecmClientProxySession;

    @Inject
    private NodeLocal nodeProxySession;

    @Inject
    private PeopleProxySession peopleProxySession;

    @Inject
    private GroupProxySession groupProxySession;

    @Inject
    private ECMNotificationInfoProxySession ECMNotificationInfoProxySession;

    @Resource
    private SessionContext sc;

    @Override
    public WorkflowDetailsMetaModel getWorkflowDetails(String acceptLanguage, String processId) {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClientByUser(acceptLanguage, ConfigurationUtil.get().get("ECM.adminUsername"));

        List<TaskMetaModel> activeTasks = getTaskMetaModelsWithForm(client, client.getWorkflowAPI().getTasksCall(null, null, null, new OrderByParam(Collections.singletonList("id desc")), "(processId=" + processId + " and status='active' and includeTaskVariables=true)"));
        List<TaskMetaModel> completedTasks = getTaskMetaModelsWithForm(client, client.getWorkflowAPI().getTasksCall(null, null, null, new OrderByParam(Collections.singletonList("id desc")), "(processId=" + processId + " and status='completed' and includeTaskVariables=true)"));

        WorkflowProcessRepresentation workflowProcessRepresentation = client.getWorkflowAPI().getProcessesCall(processId);
        List<VariableRepresentation> variableRepresentations = new ArrayList<>();
        for (VariableRepresentationSeparate vrs : client.getWorkflowAPI().getProcessesVariablesCall(processId).getObjects()) {
            VariableRepresentation vr = new VariableRepresentation();
            vr.setScope(vrs.getScope());
            vr.setValue(vrs.getValue());
            vr.setType(vrs.getType());
            vr.setName(vrs.getName());
            variableRepresentations.add(vr);
        }
        workflowProcessRepresentation.setProcessVariables(variableRepresentations);

        ResultPaging<FormRepresentation> workflowProcessStartForm = client.getWorkflowAPI().getProcessDefinitionStartFormModelCall(workflowProcessRepresentation.getProcessDefinitionId());
        WorkflowProcessMetaModel workflowProcessMetaModel = WorkflowProcessModelHelper.getWorkflowProcessMetaModel(workflowProcessRepresentation, workflowProcessStartForm.getObjects());

        WorkflowProcessDefinitionRepresentation workflowProcessDefinition = client.getWorkflowAPI().getProcessDefinitionsCall(workflowProcessMetaModel.getProcessDefinitionId());

        WorkflowDetailsMetaModel result = new WorkflowDetailsMetaModel();
        result.setProcessMetaModel(workflowProcessMetaModel);
        result.setActiveTasks(activeTasks);
        result.setCompletedTasks(completedTasks);
        result.setName(workflowProcessDefinition.getName());
        result.setDescription(workflowProcessDefinition.getDescription());
        result.setTitle(workflowProcessDefinition.getTitle());
        result.setProcessDefinitionId(workflowProcessDefinition.getId());
        result.setStartFormResourceKey(workflowProcessDefinition.getStartFormResourceKey());

        return result;
    }

    private List<TaskMetaModel> getTaskMetaModelsWithForm(AlfrescoClient client, ResultPaging<TaskRepresentation> taskRepresentations) {
        List<TaskMetaModel> result = new ArrayList<>();

        if (taskRepresentations != null && taskRepresentations.getObjects() != null) {
            List<PersonRepresentation> people = client.getPeopleAPI().loadAllUsers().getObjects();
            Map<String, List<VariableRepresentationSeparate>> processVariablesMap = new HashMap<>();
            for (TaskRepresentation activeTaskRepresentation : taskRepresentations.getObjects()) {
                ResultPaging<FormRepresentation> activeTaskForm = client.getWorkflowAPI().getTaskFormModelCall(activeTaskRepresentation.getId(), null, null, null);

                String processId = activeTaskRepresentation.getProcessId();
                if (processVariablesMap.get(processId) == null) {
                    processVariablesMap.put(processId, client.getWorkflowAPI().getProcessesVariablesCall(activeTaskRepresentation.getProcessId()).getObjects());
                }

                TaskMetaModel metaModel = TaskModelHelper.getTaskMetaModel(activeTaskRepresentation, activeTaskForm.getObjects(), processVariablesMap.get(processId));
                metaModel.setAssignee(getPersonDisplayName(metaModel.getAssignee(), people));
                result.add(metaModel);
            }
        }


        return result;
    }

    @Override
    public List<WorkflowProcessDefinitionMetaModel> getLastWorkflowProcessDefinitions(String acceptLanguage) {
        Map<String, WorkflowProcessDefinitionMetaModel> resultMap = new HashMap<>();

        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient(acceptLanguage);
        ResultPaging<WorkflowProcessDefinitionRepresentation> processDefinitions = client.getWorkflowAPI().getProcessDefinitionsCall(null, null, null, new OrderByParam(Collections.singletonList("version asc")), null);
        if (processDefinitions != null && processDefinitions.getObjects() != null) {

            for (WorkflowProcessDefinitionRepresentation processDefinition : processDefinitions.getObjects()) {
                WorkflowProcessDefinitionMetaModel metaModel = new WorkflowProcessDefinitionMetaModel();
                metaModel.setProcessDefinition(processDefinition);

                ResultPaging<FormRepresentation> workflowProcessStartForm = client.getWorkflowAPI().getProcessDefinitionStartFormModelCall(processDefinition.getId());
                if (workflowProcessStartForm != null && workflowProcessStartForm.getObjects() != null) {
                    metaModel.setForm(workflowProcessStartForm.getObjects());
                }

                if (processDefinition.getStartFormResourceKey() != null) {
                    List<ClassPropertyRepresentation> classPropertiesResult = client.getDictionaryAPI().getClassPropertiesCall(processDefinition.getStartFormResourceKey().replace(":", "_"));
                    metaModel.setClassProperties(ClassPropertyModelHelper.getMetaModels(classPropertiesResult));
                }

                resultMap.put(processDefinition.getKey(), metaModel);
            }

        }

        return new ArrayList<>(resultMap.values());
    }

    @Override
    public WorkflowProcessDefinitionMetaModel getLastWorkflowProcessDefinition(String acceptLanguage, String processDefinitionKey) {

        WorkflowProcessDefinitionMetaModel resultModel = new WorkflowProcessDefinitionMetaModel();

        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient(acceptLanguage);
        ResultPaging<WorkflowProcessDefinitionRepresentation> processDefinitions = client.getWorkflowAPI().getProcessDefinitionsCall(null, 1, null, new OrderByParam(Collections.singletonList("version desc")), "(key='" + processDefinitionKey + "')");
        if (processDefinitions != null && processDefinitions.getObjects() != null) {
            WorkflowProcessDefinitionRepresentation processDefinition = processDefinitions.getObjects().get(0);
            resultModel.setProcessDefinition(processDefinition);

            ResultPaging<FormRepresentation> workflowProcessStartForm = client.getWorkflowAPI().getProcessDefinitionStartFormModelCall(processDefinition.getId());
            if (workflowProcessStartForm != null && workflowProcessStartForm.getObjects() != null) {
                resultModel.setForm(workflowProcessStartForm.getObjects());
            }
        }

        return resultModel;
    }

    @Override
    public WorkflowProcessRepresentation createWorkflow(String acceptLanguage, String processDefinitionKey, WorkflowProcessBodyCreateMetaModel metaModel) throws WorkflowProcessException {
        WorkflowProcessRepresentation createdProcess = createWorkflow(acceptLanguage, processDefinitionKey, metaModel.getVariables());
        if (metaModel.getItems() != null && !metaModel.getItems().isEmpty()) {
            createProcessItems(createdProcess.getId(), metaModel.getItems());
        }
        return createdProcess;
    }

    @Override
    public PaginatedListWrapper<WorkflowProcessMetaModel> getWorkflowProcesses(Integer start, Integer limit, String where) {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient();

        List<WorkflowProcessMetaModel> resultModels = new ArrayList<>();
        ResultPaging<WorkflowProcessRepresentation> resultPaging = client.getWorkflowAPI().getProcessesCall(start, limit, null, new OrderByParam(Collections.singletonList("id desc")), where);
        if (resultPaging != null && resultPaging.getObjects() != null) {
            for (WorkflowProcessRepresentation workflowProcessRepresentation : resultPaging.getObjects()) {

                ResultPaging<FormRepresentation> workflowProcessStartForm = client.getWorkflowAPI().getProcessDefinitionStartFormModelCall(workflowProcessRepresentation.getProcessDefinitionId());

                resultModels.add(WorkflowProcessModelHelper.getWorkflowProcessMetaModel(workflowProcessRepresentation, workflowProcessStartForm.getObjects()));
            }
        }

        PaginatedListWrapper<WorkflowProcessMetaModel> listWrapper = new PaginatedListWrapper<>();
        listWrapper.setList(resultModels);
        listWrapper.setTotalResults(resultPaging != null ? resultPaging.getCount() : 0);
        return listWrapper;
    }

    @Override
    public PaginatedListWrapper<TaskMetaModel> getTasks(String acceptLanguage, Integer start, Integer limit, String where, String sort) {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClientByUser(acceptLanguage, ConfigurationUtil.get().get("ECM.adminUsername"));

        OrderByParam order = new OrderByParam(Collections.singletonList("id desc"));

        sort = sort != null ? sort : "[{\"property\":\"startedAt\",\"direction\":\"DESC\"}]";
        List<SortField> remoteSort = FirstUtil.getSortFieldsFromParam(sort);
        if (!remoteSort.isEmpty()) {
            SortField sortField = remoteSort.get(0);
            order = new OrderByParam(Collections.singletonList(sortField.toString()));
        }

        ResultPaging<TaskRepresentation> resultPaging = client.getWorkflowAPI().getTasksCall(start, limit, null, order, where);
        List<TaskMetaModel> resultModels = getTaskMetaModelsWithForm(client, resultPaging);

        PaginatedListWrapper<TaskMetaModel> listWrapper = new PaginatedListWrapper<>();
        listWrapper.setList(resultModels);
        listWrapper.setTotalResults(resultPaging.getPagination().getTotalItems());
        return listWrapper;
    }

    @Override
    public PaginatedListWrapper<TaskItemMetaModel> getTaskItems(String taskId, Integer start, Integer limit) {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClientByUser(ConfigurationUtil.get().get("ECM.adminUsername"));
        ResultPaging<TaskItemRepresentation> taskItemRepresentationResultPaging = client.getWorkflowAPI().getTaskItemsCall(taskId, start, limit, null);

        return getTaskItemMetaModelsWrapper(taskItemRepresentationResultPaging);
    }

    @Override
    public PaginatedListWrapper<TaskItemMetaModel> getProcessItems(String processId, Integer start, Integer limit) {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClientByUser(ConfigurationUtil.get().get("ECM.adminUsername"));
        if (processId != null) {
            ResultPaging<TaskItemRepresentation> taskItemRepresentationResultPaging = client.getWorkflowAPI().getProcessItemsCall(processId, start, limit, null);

            return getTaskItemMetaModelsWrapper(taskItemRepresentationResultPaging);
        } else {
            return new PaginatedListWrapper<>();
        }
    }

    @Override
    public void deleteProcessItem(String processId, String itemId) {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient();
        client.getWorkflowAPI().deleteProcessItemCall(processId, itemId);
    }

    @Override
    public PaginatedListWrapper<TaskItemMetaModel> createProcessItems(String processId, List<TaskItemBodyCreate> taskItemBodyCreates) {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient();
        if (taskItemBodyCreates.size() == 1) {
            TaskItemMetaModel taskMetaModel = createProcessItem(processId, taskItemBodyCreates.get(0));

            PaginatedListWrapper<TaskItemMetaModel> listWrapper = new PaginatedListWrapper<>();
            listWrapper.setList(Collections.singletonList(taskMetaModel));
            listWrapper.setTotalResults(1);
            return listWrapper;
        } else {
            ResultPaging<TaskItemRepresentation> taskItemRepresentationResultPaging = client.getWorkflowAPI().createProcessItemsCall(processId, taskItemBodyCreates);
            return getTaskItemMetaModelsWrapper(taskItemRepresentationResultPaging);
        }
    }

    @Override
    public void deleteTaskItem(String taskId, String itemId) {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient();
        client.getWorkflowAPI().deleteTaskItemCall(taskId, itemId);
    }

    @Override
    public PaginatedListWrapper<TaskItemMetaModel> createTaskItems(String taskId, List<TaskItemBodyCreate> taskItemBodyCreates) {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient();
        if (taskItemBodyCreates.size() == 1) {
            TaskItemMetaModel taskMetaModel = createTaskItem(taskId, taskItemBodyCreates.get(0));

            PaginatedListWrapper<TaskItemMetaModel> listWrapper = new PaginatedListWrapper<>();
            listWrapper.setList(Collections.singletonList(taskMetaModel));
            listWrapper.setTotalResults(1);
            return listWrapper;
        } else {
            ResultPaging<TaskItemRepresentation> taskItemRepresentationResultPaging = client.getWorkflowAPI().createTaskItemsCall(taskId, taskItemBodyCreates);
            return getTaskItemMetaModelsWrapper(taskItemRepresentationResultPaging);
        }
    }

    @Override
    public PaginatedListWrapper<VariableRepresentationSeparate> createOrUpdateTaskVariables(String acceptLanguage, String taskId, List<VariableRepresentation> variables) {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient(acceptLanguage);

        long totalResult;
        List<VariableRepresentationSeparate> resultModels = new ArrayList<>();

        if (variables.size() == 1) {
            VariableRepresentationSeparate variableRepresentation = client.getWorkflowAPI().createOrUpdateTaskVariablesCall(taskId, variables.get(0));
            resultModels.add(variableRepresentation);
            totalResult = 1;
        } else {

            ResultPaging<VariableRepresentationSeparate> resultPaging = client.getWorkflowAPI().createOrUpdateTaskVariablesCall(taskId, variables);
            if (resultPaging != null && resultPaging.getObjects() != null) {
                resultModels.addAll(resultPaging.getObjects());
            }
            totalResult = (resultPaging != null ? resultPaging.getCount() : 0);
        }

        PaginatedListWrapper<VariableRepresentationSeparate> listWrapper = new PaginatedListWrapper<>();
        listWrapper.setList(resultModels);
        listWrapper.setTotalResults(totalResult);
        return listWrapper;
    }

    @Override
    public TaskRepresentation updateTaskState(String taskId, String select, Map<String, Object> taskBody) throws WorkflowProcessException {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient();
        try {
            return client.getWorkflowAPI().updateTaskStateCall(taskId, select, taskBody);
        } catch (Exception e) {
            UnexpectedErrorRepresentation errorRepresentation = ((InternalServerErrorException) e).getResponse().readEntity(UnexpectedErrorRepresentation.class);
            log.error(errorRepresentation);
            throw new WorkflowProcessException(errorRepresentation);
        }
    }

    @Override
    public Map<String, Object> addOrRemoveTaskVariableAssociation(String taskId, Map<String, Object> associations) {
        if (!taskId.startsWith("activiti$")) {
            taskId = ("activiti$" + taskId);
        }
        return ecmClientProxySession.getAlfrescoClient().getWorkflowAPI().addOrRemoveTaskVariableAssociationCall(taskId, associations);
    }

    @Override
    public String getProcessDefinitionImage(String processDefinitionId) {
        byte[] processDefinitionImage = ecmClientProxySession.getAlfrescoClient().getWorkflowAPI().getProcessDefinitionImageCall(processDefinitionId);
        return FirstUtil.binaryToBase64(processDefinitionImage);
    }

    @Override
    public String getProcessImage(String processId) {
        processId = processId.startsWith("activiti$") ? processId : "activiti$" + processId;
        byte[] processDefinitionImage = ecmClientProxySession.getAlfrescoClient().getWorkflowAPI().getProcessImageCall(processId);
        return FirstUtil.binaryToBase64(processDefinitionImage);
    }

    @Override
    public TaskMetaModel getTask(String acceptLanguage, String taskId) {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClientByUser(acceptLanguage, ConfigurationUtil.get().get("ECM.adminUsername"));

        TaskRepresentation taskRepresentation = client.getWorkflowAPI().getTaskCall(taskId, null);
        ResultPaging<VariableRepresentationSeparate> taskVariables = client.getWorkflowAPI().getTaskVariables(taskId);

        List<VariableRepresentation> variableRepresentations = new ArrayList<>();
        for (VariableRepresentationSeparate vrs : taskVariables.getObjects()) {
            VariableRepresentation vr = new VariableRepresentation();
            vr.setScope(vrs.getScope());
            vr.setValue(vrs.getValue());
            vr.setType(vrs.getType());
            vr.setName(vrs.getName());
            variableRepresentations.add(vr);
        }

        taskRepresentation.setVariables(variableRepresentations);

        ResultPaging<FormRepresentation> activeTaskForm = client.getWorkflowAPI().getTaskFormModelCall(taskRepresentation.getId(), null, null, null);
        TaskMetaModel metaModel = TaskModelHelper.getTaskMetaModel(taskRepresentation, activeTaskForm.getObjects(), client.getWorkflowAPI().getProcessesVariablesCall(taskRepresentation.getProcessId()).getObjects());
        PersonRepresentation personRepresentation = client.getPeopleAPI().getPersonById(metaModel.getAssignee());
        metaModel.setAssignee(FirstUtil.getPersonDisplayName(personRepresentation));

        return metaModel;
    }

    @Override
    public PaginatedListWrapper<TaskMetaModel> getProcessActiveTasks(String acceptLanguage, String processId) {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient(acceptLanguage);

        ResultPaging<TaskRepresentation> representationResultPaging = client.getWorkflowAPI().getProcessActiveTasks(processId);
        List<TaskMetaModel> resultModels = new ArrayList<>();
        if (representationResultPaging.getObjects() != null) {
            for (TaskRepresentation tr : representationResultPaging.getObjects()) {
                resultModels.add(getTask(acceptLanguage, tr.getId()));
            }
        }

        PaginatedListWrapper<TaskMetaModel> listWrapper = new PaginatedListWrapper<>();
        listWrapper.setList(resultModels);
        listWrapper.setTotalResults(representationResultPaging.getPagination().getTotalItems());
        return listWrapper;
    }

    @Override
    public Map<String, Object> getWorkflowVariables(String acceptLanguage, String processId) {
        Map<String, Object> variables = new HashMap<>();
        try {
            for (VariableRepresentationSeparate vrs : ecmClientProxySession.getAlfrescoClientByUser(acceptLanguage, ConfigurationUtil.get().get("ECM.adminUsername")).getWorkflowAPI().getProcessesVariablesCall(processId).getObjects()) {
                variables.put(vrs.getName(), vrs.getValue());
            }
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
        return variables;
    }

    @Override
    public void deleteWorkflowProcess(String processId) {
        ecmClientProxySession.getAlfrescoClient().getWorkflowAPI().deleteProcessCall(processId);
    }

    @Override
    // currently used for DCS
    public WorkflowProcessExternalMetaModel startBranchCreateWorkflowExternal(String acceptLanguage, String fiRegistryNodeId) throws WorkflowProcessException {
        String processDefinitionKey = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.BRANCH_CHANGE_WORKFLOW_KEY);
        return startBranchChangeProcessExternal(acceptLanguage, fiRegistryNodeId, processDefinitionKey);
    }

    @Override
    // currently used for DCS
    public WorkflowProcessExternalMetaModel startBranchEditWorkflowExternal(String acceptLanguage, String fiRegistryNodeId) throws WorkflowProcessException {
        String processDefinitionKey = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.BRANCH_EDIT_WORKFLOW_KEY);
        return startBranchChangeProcessExternal(acceptLanguage, fiRegistryNodeId, processDefinitionKey);
    }


    @Override
    // currently used for DCS
    public PaginatedListWrapper<WorkflowProcessExternalMetaModel> getWorkflowProcessesExternal(String acceptLanguage, NodeMetaModel actionNode, Integer start, Integer limit) {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient(acceptLanguage);

        List<WorkflowProcessExternalMetaModel> resultModels = new ArrayList<>();
        ResultPaging<WorkflowProcessRepresentation> resultPaging = client.getWorkflowAPI().getProcessesCall(start, limit, null, new OrderByParam(Collections.singletonList("startedAt desc")), "(startUserId='" + sc.getCallerPrincipal().getName().toUpperCase() + "' and status='any')");
        if (resultPaging != null && resultPaging.getObjects() != null) {
            Map<String, WorkflowProcessDefinitionRepresentation> processDefinitionRepresentationMap = new HashMap<>();
            List<WorkflowProcessRepresentation> processRepresentations = resultPaging.getObjects();
            for (WorkflowProcessRepresentation processRepresentation : processRepresentations) {
                String processDefinitionId = processRepresentation.getProcessDefinitionId();
                if (processDefinitionRepresentationMap.get(processDefinitionId) == null) {
                    processDefinitionRepresentationMap.put(processDefinitionId, client.getWorkflowAPI().getProcessDefinitionsCall(processDefinitionId));
                }

                WorkflowProcessDefinitionRepresentation processDefinitionRepresentation = processDefinitionRepresentationMap.get(processDefinitionId);
                WorkflowProcessExternalMetaModel metaModel = WorkflowProcessExternalModelHelper.getMetaModel(processRepresentation);
                metaModel.setDescription(processDefinitionRepresentation.getTitle() != null && !processDefinitionRepresentation.getTitle().trim().isEmpty() ? processDefinitionRepresentation.getTitle() : processDefinitionRepresentation.getDescription());
                metaModel.setProcessSubmittedExternal(FirstUtil.getValue(actionNode.getProperties().get(EcmConstants.FI_REGISTRY_ACTION_EXTERNAL_IS_SUBMIT_PROP_NAME), Boolean.class));
                resultModels.add(metaModel);
            }
        }

        PaginatedListWrapper<WorkflowProcessExternalMetaModel> listWrapper = new PaginatedListWrapper<>();
        listWrapper.setList(resultModels);
        listWrapper.setTotalResults(resultPaging != null ? resultPaging.getPagination().getTotalItems() : 0);
        return listWrapper;
    }

    @Override
    public Map<String, Object> createWorkflowVariables(String acceptLanguage, String processId, List<VariableRepresentation> variables) {
        Map<String, Object> variablesMap = new HashMap<>();
        try {
            for (VariableRepresentationSeparate vrs : ecmClientProxySession.getAlfrescoClient(acceptLanguage).getWorkflowAPI().createProcessesVariablesCall(processId, variables).getObjects()) {
                variablesMap.put(vrs.getName(), vrs.getValue());
            }
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
        return variablesMap;
    }


    private WorkflowProcessRepresentation createWorkflow(String acceptLanguage, String processDefinitionKey, Map<String, Object> variables) throws WorkflowProcessException {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient(acceptLanguage);
        Response response = client.getWorkflowAPI().createProcessCall(new WorkflowProcessBodyCreate(processDefinitionKey, variables));

        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            UnexpectedErrorRepresentation unexpectedErrorRepresentation = response.readEntity(UnexpectedErrorRepresentation.class);
            log.error(unexpectedErrorRepresentation);
            throw new WorkflowProcessException(unexpectedErrorRepresentation);
        }

        WorkflowProcessRepresentation result = response.readEntity(WorkflowProcessRepresentation.class);
        List<VariableRepresentation> processVariables = new ArrayList<>(client.getWorkflowAPI().getProcessesVariablesCall(result.getId()).getObjects());
        result.setProcessVariables(processVariables);

        return result;
    }

    private TaskItemMetaModel createTaskItem(String taskId, TaskItemBodyCreate taskItemBodyCreate) {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient();
        TaskItemRepresentation taskItemRepresentation = client.getWorkflowAPI().createTaskItemsCall(taskId, taskItemBodyCreate);

        TaskItemMetaModel resultModel = TaskItemModelHelper.getTaskItemMetaModel(taskItemRepresentation);

        resultModel.setCreatedBy(FirstUtil.getPersonDisplayName(client.getPeopleAPI().getPersonById(taskItemRepresentation.getCreatedBy())));
        resultModel.setModifiedBy(FirstUtil.getPersonDisplayName(client.getPeopleAPI().getPersonById(taskItemRepresentation.getModifiedBy())));

        return resultModel;
    }

    private TaskItemMetaModel createProcessItem(String processId, TaskItemBodyCreate taskItemBodyCreate) {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient();
        TaskItemRepresentation taskItemBodyCreateRepresentation = client.getWorkflowAPI().createProcessItemCall(processId, taskItemBodyCreate);

        TaskItemMetaModel metaModel = TaskItemModelHelper.getTaskItemMetaModel(taskItemBodyCreateRepresentation);

        metaModel.setCreatedBy(FirstUtil.getPersonDisplayName(client.getPeopleAPI().getPersonById(taskItemBodyCreateRepresentation.getCreatedBy())));
        metaModel.setModifiedBy(FirstUtil.getPersonDisplayName(client.getPeopleAPI().getPersonById(taskItemBodyCreateRepresentation.getModifiedBy())));

        return metaModel;
    }

    private PaginatedListWrapper<TaskItemMetaModel> getTaskItemMetaModelsWrapper(ResultPaging<TaskItemRepresentation> taskItemRepresentationResultPaging) {
        List<TaskItemMetaModel> resultModels = TaskItemModelHelper.getTaskItemMetaModels(taskItemRepresentationResultPaging.getObjects());
        List<PersonRepresentation> allUsers = ecmClientProxySession.getAlfrescoClient().getPeopleAPI().loadAllUsers().getObjects();
        for (TaskItemMetaModel model : resultModels) {
            model.setCreatedBy(getPersonDisplayName(model.getCreatedBy(), allUsers));
            model.setModifiedBy(getPersonDisplayName(model.getModifiedBy(), allUsers));
        }
        PaginatedListWrapper<TaskItemMetaModel> listWrapper = new PaginatedListWrapper<>();
        listWrapper.setList(resultModels);
        listWrapper.setTotalResults(taskItemRepresentationResultPaging.getCount());
        return listWrapper;
    }

    private String getPersonDisplayName(String authorId, List<PersonRepresentation> people) {
        List<PersonRepresentation> filteredPeople = people.stream().filter(pr -> pr.getId().equals(authorId)).collect(Collectors.toList());
        String displayName = authorId;
        if (!filteredPeople.isEmpty()) {
            displayName = FirstUtil.getPersonDisplayName(filteredPeople.get(0));
        }

        return displayName;
    }

    private WorkflowProcessExternalMetaModel startBranchChangeProcessExternal(String acceptLanguage, String fiRegistryNodeId, String processDefinitionKey) throws WorkflowProcessException {
        NodeMetaModel registryNode = nodeProxySession.getFullNodeById(acceptLanguage, fiRegistryNodeId, null);
        String registryStatus = FirstUtil.getValue(registryNode.getProperties().get(EcmConstants.REGISTRY_PROP_STATUS), String.class);
        String fiType = FirstUtil.getValue(registryNode.getProperties().get(EcmConstants.REGISTRY_PROP_TYPE_CODE), String.class);

        processDefinitionKey = parseProcessDefinitionKey(processDefinitionKey, fiType);

        if (registryStatus != null && registryStatus.trim().equalsIgnoreCase("Accepted")) {

            Map<String, Object> variables = new HashMap<>();
            variables.put("fwf_fiStartTaskBaseFiIdentity", FirstUtil.getValue(registryNode.getProperties().get(EcmConstants.REGISTRY_PROP_IDENTITY), String.class));
            variables.put("fwf_fiStartTaskBaseFiCode", FirstUtil.getValue(registryNode.getProperties().get(EcmConstants.REGISTRY_PROP_CODE), String.class));

            WorkflowProcessRepresentation workflowProcessRepresentation = createWorkflow(acceptLanguage, processDefinitionKey, variables);

            AlfrescoClient client = ecmClientProxySession.getAlfrescoClient(acceptLanguage);
            WorkflowProcessDefinitionRepresentation processDefinitionRepresentation = client.getWorkflowAPI().getProcessDefinitionsCall(workflowProcessRepresentation.getProcessDefinitionId());
            WorkflowProcessExternalMetaModel metaModel = WorkflowProcessExternalModelHelper.getMetaModel(workflowProcessRepresentation);
            metaModel.setDescription(processDefinitionRepresentation.getTitle() != null && !processDefinitionRepresentation.getTitle().trim().isEmpty() ? processDefinitionRepresentation.getTitle() : processDefinitionRepresentation.getDescription());
            return metaModel;
        }

        return null;
    }

    @Override
    public void finishWorkflow(String acceptLanguage, String processId, List<VariableRepresentation> variables) throws WorkflowProcessException {
        Map<String, Object> taskBody = new HashMap<>();
        taskBody.put("state", "completed");

        TaskMetaModel approveTaskModel = getTaskByProcessId(acceptLanguage, processId);
        if (!variables.isEmpty()) {
            createOrUpdateTaskVariables(acceptLanguage, Objects.requireNonNull(approveTaskModel).getId(), variables);
        }
        updateTaskState(Objects.requireNonNull(approveTaskModel).getId(), "state", taskBody);
    }

    private TaskMetaModel getTaskByProcessId(String acceptLanguage, String processId) {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClientByUser(acceptLanguage, ConfigurationUtil.get().get("ECM.adminUsername"));
        ResultPaging<TaskRepresentation> resultPaging = client.getWorkflowAPI()
                .getTasksCall(0, Integer.MAX_VALUE, null, null,
                        String.format("(status='active' and processId=%s and includeTaskVariables=true)", processId));
        List<TaskMetaModel> resultModels = getTaskMetaModelsWithForm(client, resultPaging);

        return resultModels.isEmpty() ? null : resultModels.get(0);
    }

    public void changeProcessAssignee(String acceptLanguage, String processId, Map<String, Object> newAssigneeData) throws WorkflowProcessException, NodeException {
        Object newAssigneeIdObj = newAssigneeData.get("login");
        if (newAssigneeIdObj != null) {
            String newAssigneeId = newAssigneeIdObj.toString();
            ECMPersonMetaModel currentUser = peopleProxySession.getCurrentPerson();

            boolean isSuperAdmin = currentUser.isSuperAdmin(),
                    newAssigneeCanEdit = canEdit(newAssigneeId);

            if (isSuperAdmin && newAssigneeCanEdit) {

                TaskMetaModel task = getTaskByProcessId(acceptLanguage, processId);
                if (task != null) {
                    NodeMetaModel fiRegistryModel = getTaskFiRegistryModel(task);
                    if (fiRegistryModel != null) {
                        String status = (fiRegistryModel.getProperties().get(EcmConstants.REGISTRY_PROP_STATUS) != null ?
                                FirstUtil.getValue(fiRegistryModel.getProperties().get(EcmConstants.REGISTRY_PROP_STATUS), String.class) : null);
                        if (NodeStatus.IN_PROGRESS.getValue().equals(status) || NodeStatus.GAP.getValue().equals(status) || NodeStatus.LIQUIDATION.getValue().equals(status)) {
                            // Update task assignee
                            Map<String, Object> taskBodyObject = new HashMap<>();
                            taskBodyObject.put("assignee", newAssigneeId);
                            taskBodyObject.put("owner", newAssigneeId);
                            updateTaskState(task.getId(), "assignee,owner", taskBodyObject);

                            // Update lastEditor of fiRegistry
                            TreeMap<String, Object> fiRegistryUpdatedProps = new TreeMap<>();
                            fiRegistryUpdatedProps.put(EcmConstants.REGISTRY_PROP_LAST_EDITOR_ID, newAssigneeId);
                            fiRegistryUpdatedProps.put(EcmConstants.REGISTRY_PROP_LAST_EDITOR_FULL_NAME, getFullName(newAssigneeId));
                            NodeBodyUpdate fiNbu = new NodeBodyUpdate(fiRegistryUpdatedProps);
                            nodeProxySession.updateNode(fiRegistryModel.getId(), fiNbu);

                            // Update action author
                            TreeMap<String, Object> fiRegistryActionUpdatedProps = new TreeMap<>();
                            fiRegistryActionUpdatedProps.put(EcmConstants.ACTION_PROP_AUTHOR, newAssigneeId);
                            NodeBodyUpdate actionNbu = new NodeBodyUpdate(fiRegistryActionUpdatedProps);
                            String lastActionId = FirstUtil.getValue(fiRegistryModel.getProperties().get(EcmConstants.REGISTRY_PROP_LAST_ACTION_ID), String.class);
                            nodeProxySession.updateNode(lastActionId, actionNbu);

                            // Change notifications' addressee
                            ECMNotificationInfoProxySession.changeActionNotificationsAddressee(lastActionId, newAssigneeId);
                        }
                    }
                }

            } else {
                throw new WorkflowProcessException(new UnexpectedErrorRepresentation(405, !isSuperAdmin ? "ErrorOnlySuperAdminCanReassignTasks" : "ErrorNewAssigneeIsNotEditor"));
            }
        }
    }

    private String getFullName(String newAssigneeId) {
        ECMPersonMetaModel person = peopleProxySession.getPersonById(newAssigneeId, new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)));
        String firstName = person.getFirstName();
        firstName = (firstName == null || firstName.isEmpty() || firstName.equals("NONAME")) ? "" : firstName;
        String lastName = person.getLastName();
        lastName = (lastName == null || lastName.isEmpty() || lastName.equals("NONAME")) ? "" : lastName;
        return firstName.isEmpty() ? person.getId() : firstName + (lastName.isEmpty() ? "" : " " + lastName);
    }

    private boolean canEdit(String assigneeId) {
        List<ECMGroupMetaModel> userGroups = groupProxySession.loadUserGroups(assigneeId);
        boolean canEdit = false;
        String editorsGroupName = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.EDITOR_GROUP_NAME_KEY);
        if (userGroups != null) {
            for (ECMGroupMetaModel group : userGroups) {
                String groupId = group.getId();
                if (groupId.equalsIgnoreCase(AlfrescoPropConstants.ALFRESCO_ADMIN_GROUP_NAME)
                        || groupId.equalsIgnoreCase("GROUP_" + editorsGroupName)) {
                    canEdit = true;
                    break;
                }
            }
        }

        return canEdit;
    }

    private NodeMetaModel getTaskFiRegistryModel(TaskMetaModel task) {
        String fiRegistryIdStr = task.getFiRegistryId(),
                fiRegistryId = fiRegistryIdStr;
        if (fiRegistryIdStr != null && fiRegistryIdStr.contains("SpacesStore/")) {
            String[] parts = fiRegistryIdStr.split("SpacesStore/");
            if (parts.length > 1) {
                fiRegistryId = parts[1];
            }
        }

        return nodeProxySession.getNodeById(fiRegistryId, new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)).toString(), null);
    }


    private String parseProcessDefinitionKey(String processDefinitionKey, String fiType) {
        try {
            String workflowKeyTypeMapProperty = AlfrescoConfiguration.get().getAlfrescoProperty("workflowKeyTypeMap");
            Map<String, String> workflowKeyTypeMap = new ObjectMapper().readValue(workflowKeyTypeMapProperty, Map.class);
            fiType = workflowKeyTypeMap.get(fiType);
        } catch (Throwable t) {
            log.error("Invalid  workflowKeyTypeMap property value !!!!");
            log.error(t.getMessage(), t);
        }

        return MessageFormat.format(processDefinitionKey, fiType);
    }


}

