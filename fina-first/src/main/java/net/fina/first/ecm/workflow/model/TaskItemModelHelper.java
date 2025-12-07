package net.fina.first.ecm.workflow.model;

import net.fina.ecm.alfresco.api.workflow.model.TaskItemRepresentation;

import java.util.ArrayList;
import java.util.List;

public class TaskItemModelHelper {

    public static List<TaskItemMetaModel> getTaskItemMetaModels(List<TaskItemRepresentation> representations) {
        List<TaskItemMetaModel> result = new ArrayList<>();
        if (representations != null && !representations.isEmpty()) {
            for (TaskItemRepresentation representation : representations) {
                result.add(getTaskItemMetaModel(representation));
            }
        }
        return result;
    }

    public static TaskItemMetaModel getTaskItemMetaModel(TaskItemRepresentation representation) {
        TaskItemMetaModel metaModel = new TaskItemMetaModel();
        metaModel.setModifiedAt(representation.getModifiedAt());
        metaModel.setCreatedAt(representation.getCreatedAt());
        metaModel.setName(representation.getName());
        metaModel.setId(representation.getId());
        metaModel.setCreatedBy(representation.getCreatedBy());
        metaModel.setMimeType(representation.getMimeType());
        metaModel.setModifiedBy(representation.getModifiedBy());
        metaModel.setSize(representation.getSize());

        return metaModel;
    }

}
