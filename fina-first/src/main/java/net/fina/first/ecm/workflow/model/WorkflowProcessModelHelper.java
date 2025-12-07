package net.fina.first.ecm.workflow.model;

import net.fina.ecm.alfresco.api.workflow.model.FormRepresentation;
import net.fina.ecm.alfresco.api.workflow.model.WorkflowProcessRepresentation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WorkflowProcessModelHelper {

    public static List<WorkflowProcessMetaModel> getWorkflowProcessMetaModels(Map<WorkflowProcessRepresentation, List<FormRepresentation>> representationListMap) {
        List<WorkflowProcessMetaModel> result = new ArrayList<>();
        if (representationListMap != null && !representationListMap.isEmpty()) {
            for (Map.Entry<WorkflowProcessRepresentation, List<FormRepresentation>> entry : representationListMap.entrySet()) {
                result.add(getWorkflowProcessMetaModel(entry.getKey(), entry.getValue()));
            }
        }
        return result;
    }

    public static WorkflowProcessMetaModel getWorkflowProcessMetaModel(WorkflowProcessRepresentation workflowProcessRepresentation, List<FormRepresentation> processStartForm) {
        WorkflowProcessMetaModel workflowProcessMetaModel = new WorkflowProcessMetaModel();
        workflowProcessMetaModel.setProcessDefinitionId(workflowProcessRepresentation.getProcessDefinitionId());
        workflowProcessMetaModel.setStartUserId(workflowProcessRepresentation.getStartUserId());
        workflowProcessMetaModel.setStartActivityId(workflowProcessRepresentation.getStartActivityId());
        workflowProcessMetaModel.setStartedAt(workflowProcessRepresentation.getStartedAt());
        workflowProcessMetaModel.setId(workflowProcessRepresentation.getId());
        workflowProcessMetaModel.setCompleted(workflowProcessRepresentation.isCompleted());
        workflowProcessMetaModel.setProcessDefinitionKey(workflowProcessRepresentation.getProcessDefinitionKey());
        workflowProcessMetaModel.setProcessVariables(workflowProcessRepresentation.getProcessVariables());
        workflowProcessMetaModel.setProcessStartForm(processStartForm);

        return workflowProcessMetaModel;
    }

}
