package net.fina.server.returns.model.helper;

import net.fina.common.client.returns.DefinitionTableModel;
import net.fina.common.client.returns.ReturnDefinitionModel;
import net.fina.server.i18n.helper.Description;
import net.fina.server.returns.entity.ReturnDefinition;

import java.util.List;
import java.util.stream.Collectors;

public class ReturnDefinitionModelHelper {
    public static ReturnDefinitionModel toModel(ReturnDefinition rd, long langId) {
        ReturnDefinitionModel result = new ReturnDefinitionModel();
        result.setId(rd.getId());
        result.setVersion(rd.getVersion());
        result.setCode(rd.getCode());
        result.setName(rd.getDescription().getDescription(langId));
        result.setNameStrId(rd.getDescription().getNameStrId());
        result.setDisable(rd.getDisable());
        result.setManualInput(rd.isManualInput());
        result.setGeneralInfo(rd.getGeneralInfo());
        if (rd.getReturnType() != null) {
            result.setReturnType(ReturnTypeModelHelper.toModel(rd.getReturnType(), langId));
        }

        if (rd.getDefinitionTables() != null) {
            result.setTables(rd.getDefinitionTables().stream().map(rt -> new DefinitionTableModel(rt.getId(), rt.getCode(), rt.getType())).toList());
        }

        return result;
    }

    public static List<ReturnDefinitionModel> toModels(List<ReturnDefinition> returnDefinitionList, long langId) {
        return returnDefinitionList.stream().map(rd -> toModel(rd, langId)).collect(Collectors.toList());
    }

    public static ReturnDefinition toEntity(ReturnDefinitionModel rd, long langID) {
        ReturnDefinition result = new ReturnDefinition();
        result.setId(rd.getId());
        result.setVersion(rd.getVersion());
        result.setCode(rd.getCode());
        result.setDescription(new Description(langID, rd.getNameStrId(), rd.getName()));
        result.setDisable(rd.getDisable());
        result.setManualInput(rd.isManualInput());
        result.setGeneralInfo(rd.getGeneralInfo());
        if (rd.getReturnType() != null) {
            result.setReturnType(ReturnTypeModelHelper.toEntity(rd.getReturnType(), langID));
        }

        return result;
    }

    public static List<ReturnDefinition> toEntities(List<ReturnDefinitionModel> models, long langId) {
        return models.stream().map(rd -> toEntity(rd, langId)).collect(Collectors.toList());
    }
}
