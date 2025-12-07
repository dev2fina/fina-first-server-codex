package net.fina.server.license.model;

import net.fina.server.license.entity.LicenceType;
import net.fina.server.i18n.helper.Description;

import java.util.List;
import java.util.stream.Collectors;

public class LicenseTypeMetaModelHelper {
    public static LicenceType toEntity(LicenseTypeMetaModel model, long langId) {
        LicenceType type = new LicenceType();
        type.setId(model.getId());
        type.setCode(model.getCode());
        type.setVersion(model.getVersion() == null ? 0 : model.getVersion());
        type.setDescription(new Description(langId, model.getNameStrId(), model.getName()));
        type.setOperations(BankingOperationModelHelper.toEntities(model.getOperations(), langId));
        return type;
    }

    public static LicenseTypeMetaModel toModel(LicenceType entity, long langId) {
        LicenseTypeMetaModel model = new LicenseTypeMetaModel();
        model.setId(entity.getId());
        model.setCode(entity.getCode());
        model.setVersion(entity.getVersion() == null ? 0 : entity.getVersion());
        model.setNameStrId(entity.getDescription().getNameStrId());
        model.setName(entity.getDescription().getDescription(langId));
        model.setOperations(BankingOperationModelHelper.toModels(entity.getOperations(), langId));
        return model;
    }

    public static List<LicenseTypeMetaModel> toModels(List<LicenceType> licenceTypes, long langId) {
        return licenceTypes.stream().map(l -> toModel(l, langId)).collect(Collectors.toList());
    }
}
