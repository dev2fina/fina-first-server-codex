package net.fina.server.fi.model;

import net.fina.common.client.fis.FiTypeModel;
import net.fina.server.fi.entity.FiType;
import net.fina.server.i18n.helper.Description;
import net.fina.server.i18n.model.DescriptionModelHelper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FiTypeModelHelper {
    public static FiTypeModel toModel(FiType fiType, long langId) {
        FiTypeModel result = new FiTypeModel();
        result.setId(fiType.getId());
        result.setCode(fiType.getCode());
        if (fiType.getDescription() != null) {
            result.setName(fiType.getDescription().getDescription(langId));
            result.setNameStrId(fiType.getDescription().getNameStrId());
        }
        result.setVersion(fiType.getVersion());

        return result;
    }

    public static FiType toEntity(FiTypeModel fiTypeModel) {
        FiType result = new FiType();
        result.setId(fiTypeModel.getId());
        result.setCode(fiTypeModel.getCode());
        if (fiTypeModel.getDescriptionModel() != null) {
            result.setDescription(DescriptionModelHelper.toEntity(Collections.singletonList(fiTypeModel.getDescriptionModel())));
        }

        return result;
    }

    public static FiType toEntity(FiTypeModel fiTypeModel, long langId) {
        FiType result = new FiType();
        result.setId(fiTypeModel.getId());
        result.setCode(fiTypeModel.getCode());
        result.setDescription(new Description(langId, fiTypeModel.getNameStrId(), fiTypeModel.getName()));
        result.setVersion(fiTypeModel.getVersion());
        return result;
    }


    public static List<FiTypeModel> toModels(List<FiType> fiTypeList, long langId) {
        return fiTypeList.stream().map(fiType -> toModel(fiType, langId)).collect(Collectors.toList());
    }

    public static List<FiType> toEntitiess(List<FiTypeModel> fiTypeModels) {
        return fiTypeModels.stream().map(fiType -> toEntity(fiType)).collect(Collectors.toList());
    }
}
