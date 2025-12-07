package net.fina.first.ecm.workflow.model;

import net.fina.ecm.alfresco.api.workflow.model.WorkflowProcessRepresentation;

import java.util.ArrayList;
import java.util.List;

public class WorkflowProcessExternalModelHelper {

    public static List<WorkflowProcessExternalMetaModel> getMetaModels(List<WorkflowProcessRepresentation> representations) {
        List<WorkflowProcessExternalMetaModel> result = new ArrayList<>();
        if (representations != null && !representations.isEmpty()) {
            for (WorkflowProcessRepresentation representation : representations) {
                result.add(getMetaModel(representation));
            }
        }
        return result;
    }

    public static WorkflowProcessExternalMetaModel getMetaModel(WorkflowProcessRepresentation representation) {
        WorkflowProcessExternalMetaModel metaModel = new WorkflowProcessExternalMetaModel();
        metaModel.setProcessDefinitionId(representation.getId());
        metaModel.setProcessDefinitionKey(representation.getProcessDefinitionKey());
        metaModel.setStartedAt(representation.getStartedAt());
        metaModel.setCompleted(representation.isCompleted());
        return metaModel;
    }

}
