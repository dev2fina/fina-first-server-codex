package net.fina.server.license.model;

import net.fina.common.client.fis.FiModel;
import net.fina.server.fi.entity.Fi;
import net.fina.server.license.entity.Licence;

import java.util.List;
import java.util.stream.Collectors;

public class LicenseMetaModelHelper {
    public static LicenseMetaModel toModel(Licence entity, long langId) {
        LicenseMetaModel model = new LicenseMetaModel();
        model.setId(entity.getId());
        model.setVersion(entity.getVersion());
        model.setCode(entity.getCode());
        model.setChange(entity.getChange());
        model.setCreationDate(entity.getCreationDate());
        model.setDateOfChange(entity.getDateOfChange());
        model.setIsDefault(entity.getIsDefault());
        model.setLicenceStatus(entity.getLicenceStatus());
        model.setReason(entity.getReasons().getDescription(langId));
        model.setReasonStrId(entity.getReasons().getNameStrId());
        if (entity.getLicenseType() != null) {
            model.setLicenseType(LicenseTypeMetaModelHelper.toModel(entity.getLicenseType(), langId));
        }
        if (entity.getOperations() != null) {
            model.setOperations(LicenseBankingOperationModelHelper.toModels(entity.getOperations(), langId));
        }

        if (entity.getFi() != null) {

            Fi fi = entity.getFi();
            FiModel fiModel = new FiModel();

            // XXX you can convert all fields, if you need
            fiModel.setId(fi.getId());
            fiModel.setCode(fi.getCode());
            fiModel.setLevel(1);
            model.setFiModel(fiModel);
        }

        model.setComments(LicenseCommentModelHelper.toModels(entity.getComments()));

        return model;
    }


    public static Licence toEntity(LicenseMetaModel model, long langId) {
        Licence entity = new Licence();
        entity.setId(model.getId());
        entity.setVersion(model.getVersion());
        entity.setCode(model.getCode());
        entity.setChange(model.getChange());
        entity.setCreationDate(model.getCreationDate());
        entity.setDateOfChange(model.getDateOfChange());
        entity.setIsDefault(model.getIsDefault());
        entity.setLicenceStatus(model.getLicenceStatus());
        if (model.getOperations() != null) {
            entity.setOperations(LicenseBankingOperationModelHelper.toEntities(model.getOperations(), langId));
        }

        entity.setComments(LicenseCommentModelHelper.toEntities(model.getComments()));

        return entity;
    }

    public static List<LicenseMetaModel> toModels(List<Licence> licences, long langId) {
        return licences.stream().map(l -> toModel(l, langId)).collect(Collectors.toList());
    }

}
