package net.fina.first.ecm.workflow.model;

import net.fina.ecm.alfresco.api.workflow.model.FormRepresentation;
import net.fina.ecm.alfresco.api.workflow.model.TaskRepresentation;
import net.fina.ecm.alfresco.api.workflow.model.VariableRepresentation;
import net.fina.ecm.alfresco.api.workflow.model.VariableRepresentationSeparate;
import net.fina.ecm.util.AlfrescoConfiguration;
import net.fina.ecm.util.AlfrescoPropConstants;
import net.fina.first.FirstUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class TaskModelHelper {

    public static TaskMetaModel getTaskMetaModel(TaskRepresentation taskRepresentation, List<FormRepresentation> taskForm, List<VariableRepresentationSeparate> taskProcessVariables) {
        TaskMetaModel taskMetaModel = new TaskMetaModel();
        taskMetaModel.setId(taskRepresentation.getId());
        taskMetaModel.setProcessId(taskRepresentation.getProcessId());
        taskMetaModel.setProcessDefinitionId(taskRepresentation.getProcessDefinitionId());
        taskMetaModel.setActivityDefinitionId(taskRepresentation.getActivityDefinitionId());
        taskMetaModel.setName(taskRepresentation.getName());
        taskMetaModel.setDescription(taskRepresentation.getDescription());
        taskMetaModel.setDueAt(taskRepresentation.getDueAt());
        taskMetaModel.setStartedAt(taskRepresentation.getStartedAt());
        taskMetaModel.setEndedAt(taskRepresentation.getEndedAt());
        taskMetaModel.setDurationInMs(taskRepresentation.getDurationInMs());
        taskMetaModel.setPriority(taskRepresentation.getPriority());
        taskMetaModel.setOwner(taskRepresentation.getOwner());
        taskMetaModel.setAssignee(taskRepresentation.getAssignee());
        taskMetaModel.setFormResourceKey(taskRepresentation.getFormResourceKey());
        taskMetaModel.setState(taskRepresentation.getState());
        taskMetaModel.setTaskVariables(taskRepresentation.getVariables());
        taskMetaModel.setTaskForm(taskForm);

        // detect fi registry id
        if (taskProcessVariables != null && !taskProcessVariables.isEmpty()) {
            Set<String> fiRegistryDataTypes = new TreeSet<>(Arrays.asList(AlfrescoConfiguration.get().getAlfrescoArrayProperty(AlfrescoPropConstants.DATA_TYPE_FI_REGISTRIES_KEY)));
            for (VariableRepresentation taskProcessVariable : taskProcessVariables) {
                if (fiRegistryDataTypes.contains(taskProcessVariable.getType())) {
                    taskMetaModel.setFiRegistryId(FirstUtil.getValue(taskProcessVariable.getValue(), String.class));
                    break;
                }
            }
        }

        return taskMetaModel;
    }

}
