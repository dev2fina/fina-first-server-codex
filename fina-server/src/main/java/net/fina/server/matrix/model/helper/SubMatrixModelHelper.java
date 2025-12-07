package net.fina.server.matrix.model.helper;

import net.fina.server.ThreadLocalHolder;
import net.fina.server.matrix.entity.SubMatrix;
import net.fina.server.matrix.entity.TableEndCondition;
import net.fina.server.matrix.model.SubMatrixModel;
import net.fina.server.matrix.model.TableEndConditionModel;
import net.fina.server.misc.ObjectUtil;
import net.fina.server.returns.model.helper.ReturnDefinitionModelHelper;

import java.util.List;

public class SubMatrixModelHelper {

    public static TableEndCondition toEntity(TableEndConditionModel model) {
        TableEndCondition result = new TableEndCondition();
        ObjectUtil.copyProperties(model, result);
        return result;
    }

    public static TableEndConditionModel toModel(TableEndCondition entity) {
        TableEndConditionModel result = new TableEndConditionModel();
        ObjectUtil.copyProperties(entity, result);
        return result;
    }


    public static SubMatrix toEntity(SubMatrixModel model) {
        SubMatrix result = new SubMatrix();
        long langId = ThreadLocalHolder.getLanguage().getId();
        ObjectUtil.copyProperties(model, result);

        if (model.getReturnDefinition() != null) {
            result.setReturnDefinition(ReturnDefinitionModelHelper.toEntity(model.getReturnDefinition(), langId));
        }
        if (model.getMainMatrix() != null) {
            result.setMainMatrix(MatrixModelHelper.toEntity(model.getMainMatrix()));
        }

        return result;
    }

    public static SubMatrixModel toModel(SubMatrix entity) {
        SubMatrixModel result = new SubMatrixModel();
        long langId = ThreadLocalHolder.getLanguage().getId();

        ObjectUtil.copyProperties(entity, result);

        if (entity.getReturnDefinition() != null) {
            result.setReturnDefinition(ReturnDefinitionModelHelper.toModel(entity.getReturnDefinition(), langId));
        }
        if (entity.getMainMatrix() != null) {
            result.setMainMatrix(MatrixModelHelper.toModel(entity.getMainMatrix(), langId));
        }

        return result;
    }

    public static List<SubMatrixModel> toList(List<SubMatrix> entities) {
        return entities.stream().map(SubMatrixModelHelper::toModel).toList();
    }

}
