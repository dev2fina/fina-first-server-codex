package net.fina.server.matrix.model.helper;

import net.fina.common.client.fis.FiTypeSimpleModel;
import net.fina.server.dcs.uploadfile.impl.reader.excel.MatrixOptionBase;
import net.fina.server.fi.entity.FiType;
import net.fina.server.matrix.entity.Matrix;
import net.fina.server.matrix.model.MatrixModel;
import net.fina.server.returns.entity.PeriodType;
import net.fina.server.returns.entity.ReturnVersion;
import net.fina.server.returns.model.helper.PeriodModelHelper;
import net.fina.server.returns.model.helper.ReturnVersionModelHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatrixModelHelper {

    public static Matrix toEntity(MatrixModel model) {
        Matrix entity = new Matrix();

        entity.setId(model.getId());
        if (model.getFiTypeModel() != null) {
            entity.setFiType(new FiType(model.getFiTypeModel().getId()));
        }
        if (model.getReturnVersion() != null) {
            entity.setReturnVersion(new ReturnVersion(model.getReturnVersion().getId()));
        }
        if (model.getPeriodType() != null) {
            entity.setPeriodType(new PeriodType(model.getPeriodType().getId()));
        }
        entity.setPassword(model.getPassword());
        entity.setPattern(model.getPattern());
        entity.setRegFileType(model.getRegFileType());
        entity.setProcessEngine(model.getProcessEngine());
        entity.setDigitalSignatureCheckEnabled(model.isDigitalSignatureCheckEnabled());
        entity.setEnable(model.isEnable());
        return entity;
    }

    public static MatrixModel toModel(Matrix matrix, long langId) {
        if (matrix != null) {
            MatrixModel model = new MatrixModel();
            model.setId(matrix.getId());

            FiType fiType = matrix.getFiType();
            if (matrix.getFiType() != null) {
                model.setFiTypeModel(new FiTypeSimpleModel(fiType.getId(), fiType.getCode(), fiType.getDescription().getDescription(langId)));
            }

            model.setPattern(matrix.getPattern());
            model.setDigitalSignatureCheckEnabled(matrix.isDigitalSignatureCheckEnabled());
            if (matrix.getReturnVersion() != null) {
                model.setReturnVersion(ReturnVersionModelHelper.toModel(matrix.getReturnVersion(), langId));
            }
            if (matrix.getPeriodType() != null) {
                model.setPeriodType(PeriodModelHelper.toModel(matrix.getPeriodType(), langId));
            }

            model.setRegFileType(matrix.getRegFileType());
            model.setProcessEngine(matrix.getProcessEngine());
            model.setEnable(matrix.isEnable());

            return model;
        }
        return null;
    }

    public static Matrix toEntity(MatrixOptionBase matrix, Map<String, Long> fiTypeCodeIdMap, Map<String,
            Long> rvCodeIdMap, Map<String, Long> periodTypeCodeIdMap) {
        if (matrix != null) {
            Matrix entity = new Matrix();

            Long fiTypeId = fiTypeCodeIdMap.getOrDefault(matrix.getFIType(), -1L);
            Long rvId = rvCodeIdMap.getOrDefault(matrix.getVersion(), -1L);
            Long periodTypeId = periodTypeCodeIdMap.getOrDefault(matrix.getPeriod(), -1L);

            entity.setFiType(new FiType(fiTypeId));
            entity.setReturnVersion(new ReturnVersion(rvId));
            entity.setPeriodType(new PeriodType(periodTypeId));
            entity.setPassword(matrix.getWorkBookPassword());
            entity.setProcessEngine(matrix.getProcessEngine());
            entity.setRegFileType(matrix.getRegAdvancedFileType());
            entity.setPattern(matrix.getPattern());

            return entity;

        }
        return null;
    }

    public static List<MatrixModel> toModels(List<Matrix> matrixList, long langId) {
        return matrixList.stream().map(matrix -> toModel(matrix, langId)).toList();

    }

    public static List<Matrix> toEntities(List<MatrixModel> models) {
        return models.stream().map(MatrixModelHelper::toEntity).toList();
    }

    public static Map<String, Matrix> toEntities(List<MatrixOptionBase> models, Map<String, Long> fiTypeCodeIdMap, Map<String,
            Long> rvCodeIdMap, Map<String, Long> periodTypeCodeIdMap) {
        Map<String, Matrix> result = new HashMap<>();
        for (MatrixOptionBase option : models) {
            Matrix mainMatrix = toEntity(option, fiTypeCodeIdMap, rvCodeIdMap, periodTypeCodeIdMap);
            result.put(option.getMatrixForEachType(), mainMatrix);
        }
        return result;
    }
}
