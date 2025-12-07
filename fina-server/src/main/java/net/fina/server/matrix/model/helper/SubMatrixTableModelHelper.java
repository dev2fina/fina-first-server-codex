package net.fina.server.matrix.model.helper;

import net.fina.server.matrix.entity.SubMatrixTable;
import net.fina.server.matrix.model.SubMatrixTableModel;
import net.fina.server.misc.ObjectUtil;
import net.fina.server.returns.model.helper.DefinitionTableModelHelper;

import java.util.List;

public class SubMatrixTableModelHelper {

    public static SubMatrixTable toEntity(SubMatrixTableModel model) {
        SubMatrixTable result = new SubMatrixTable();
        ObjectUtil.copyProperties(model, result);

        if (model.getVctTableEndConditions() != null) {
            result.setVctTableEndConditions(model.getVctTableEndConditions().stream().map(SubMatrixModelHelper::toEntity).toList());
        }
        if (model.getDefinitionTable() != null) {
            result.setDefinitionTable(DefinitionTableModelHelper.toEntity(model.getDefinitionTable(), -1));
        }

        return result;
    }

    public static SubMatrixTableModel toModel(SubMatrixTable entity) {
        SubMatrixTableModel result = new SubMatrixTableModel();

        ObjectUtil.copyProperties(entity, result);

        if (entity.getVctTableEndConditions() != null) {
            result.setVctTableEndConditions(entity.getVctTableEndConditions().stream().map(SubMatrixModelHelper::toModel).toList());
        }
        if (entity.getDefinitionTable() != null) {
            result.setDefinitionTable(DefinitionTableModelHelper.toModel(entity.getDefinitionTable(), -1));
        }
        return result;

    }

    public static List<SubMatrixTableModel> toList(List<SubMatrixTable> entities) {
        return entities.stream().map(SubMatrixTableModelHelper::toModel).toList();
    }
}
