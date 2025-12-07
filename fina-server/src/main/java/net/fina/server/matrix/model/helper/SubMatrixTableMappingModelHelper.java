package net.fina.server.matrix.model.helper;

import net.fina.server.matrix.entity.SubMatrixTable;
import net.fina.server.matrix.entity.SubMatrixTableMapping;
import net.fina.server.matrix.model.SubMatrixTableMappingModel;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.misc.ObjectUtil;

import java.util.List;

public class SubMatrixTableMappingModelHelper {
    public static SubMatrixTableMappingModel toModel(SubMatrixTableMapping entity, long langId) {
        SubMatrixTableMappingModel result = new SubMatrixTableMappingModel();
        ObjectUtil.copyProperties(entity, result);

        result.setMdtNodeId(entity.getMdtNode().getId());
        result.setMdtNodeCode(entity.getMdtNode().getCode());
        result.setMdtNodeDescription(entity.getMdtNode().getDescription().getDescription(langId));

        return result;
    }

    public static SubMatrixTableMapping toEntity(SubMatrixTableMappingModel model, long tableId) {
        SubMatrixTableMapping result = new SubMatrixTableMapping();
        ObjectUtil.copyProperties(model, result);

        result.setMdtNode(new MDTNode(model.getMdtNodeId(), model.getMdtNodeCode()));
        result.setSubMatrixTable(new SubMatrixTable(tableId));

        return result;
    }

    public static List<SubMatrixTableMappingModel> toModels(List<SubMatrixTableMapping> entities, long langId) {
        return entities.stream().map(e -> toModel(e, langId)).toList();
    }

    public static List<SubMatrixTableMapping> toEntities(List<SubMatrixTableMappingModel> models, long tableId) {
        return models.stream().map(m -> toEntity(m, tableId)).toList();
    }
}
