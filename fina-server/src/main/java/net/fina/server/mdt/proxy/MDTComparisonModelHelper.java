package net.fina.server.mdt.proxy;

import net.fina.common.shared.mdt.MDTComparisonModel;
import net.fina.server.mdt.entity.MDTComparison;
import net.fina.server.mdt.model.helper.MdtNodeModelHelper;

import java.util.List;
import java.util.stream.Collectors;

public class MDTComparisonModelHelper {

    public static MDTComparison toEntity(MDTComparisonModel model, long langId) {
        MDTComparison result = new MDTComparison();
        result.setId(model.getId());
        result.setTemplate(model.getTemplate());
        result.setCondition(model.getCondition());
        result.setNumberPattern(model.getNumberPattern());
        result.setRightEquation(model.getEquation());
        result.setLeftEquation(model.getLeftEquation());
        result.setVersion(model.getVersion());
        result.setProcessStage(model.getProcessStage());
        if (model.getNode() != null) {
            result.setNode(MdtNodeModelHelper.toEntity(model.getNode(), langId));
        }
        return result;
    }

    public static MDTComparisonModel toModel(MDTComparison comparison, long langId) {
        MDTComparisonModel result = new MDTComparisonModel();

        result.setId(comparison.getId());
        result.setEquation(comparison.getRightEquation());
        result.setLeftEquation(comparison.getLeftEquation());
        result.setCondition(comparison.getCondition());
        result.setTemplate(comparison.getTemplate());
        result.setNumberPattern(comparison.getNumberPattern());
        result.setVersion(comparison.getVersion());
        result.setProcessStage(comparison.getProcessStage());
        if (comparison.getNode() != null) {
            result.setNode(MdtNodeModelHelper.toModel(comparison.getNode(), langId));
        }

        return result;
    }

    public static List<MDTComparisonModel> toModels(List<MDTComparison> comparisons, long langId) {
        return comparisons.stream().map(c -> toModel(c, langId)).collect(Collectors.toList());
    }

    public static List<MDTComparison> toEntities(List<MDTComparisonModel> models, long langId) {
        return models.stream().map(m -> toEntity(m, langId)).collect(Collectors.toList());
    }
}
