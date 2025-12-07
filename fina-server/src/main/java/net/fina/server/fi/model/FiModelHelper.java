package net.fina.server.fi.model;

import net.fina.common.client.fis.FiModel;
import net.fina.server.fi.entity.Fi;
import net.fina.server.i18n.model.DescriptionModelHelper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FiModelHelper {

    public static FiModel toModel(Fi fi, long langId) {
        FiModel result = new FiModel();
        result.setId(fi.getId());
        result.setCode(fi.getCode());
        if (fi.getDescription() != null) {
            result.setName(fi.getDescription().getDescription(langId));
            result.setNameStrId(fi.getDescription().getNameStrId());
        }
        result.setVersion(fi.getVersion());
        if (fi.getAddressDescription() != null) {
            result.setAddressString(fi.getAddressDescription().getDescription(langId));
        }
        result.setDisable(fi.isDisable());
        result.setPhone(fi.getPhone());
        result.setEmail(fi.getEmail());
        result.setFax(fi.getFax());
        result.setDescriptionModel(DescriptionModelHelper.toModel(fi.getDescription()).stream().filter(f -> f.getLangId() == langId).findAny().get());
        result.setLegalForm(fi.getLegalForm());
        if (fi.getFiType() != null) {
            result.setFiTypeModel(FiTypeModelHelper.toModel(fi.getFiType(), langId));
        }
        result.setIdentificationCode(fi.getIdentificationCode());
        result.setReorganization(fi.getReorganization());
        result.setRepresentativePerson(fi.getRepresentativePerson());
        result.setSwiftCode(fi.getSwiftCode());
        result.setWebSite(fi.getWebSite());

        return result;
    }

    public static Fi toEntity(FiModel fiModel) {
        Fi result = new Fi();
        result.setId(fiModel.getId());
        result.setCode(fiModel.getCode());
        if (fiModel.getDescriptionModel() != null) {
            result.setDescription(DescriptionModelHelper.toEntity(Collections.singletonList(fiModel.getDescriptionModel())));
        }
        result.setDisable(fiModel.isDisable());
        result.setEmail(fiModel.getEmail());
        result.setPhone(fiModel.getPhone());
        result.setFax(fiModel.getFax());
        result.setLegalForm(fiModel.getLegalForm());
        result.setFiType(FiTypeModelHelper.toEntity(fiModel.getFiTypeModel()));
        result.setIdentificationCode(fiModel.getIdentificationCode());
        result.setPhone(fiModel.getPhone());
        result.setReorganization(fiModel.getReorganization());
        result.setRepresentativePerson(fiModel.getRepresentativePerson());
        result.setSwiftCode(fiModel.getSwiftCode());
        result.setRegistrationDate(fiModel.getRegistrationDate());
        result.setCloseDate(fiModel.getCloseDate());
        result.setNumberOfMobileOffices(fiModel.getNumberOfMobileOffices());
        result.setWebSite(fiModel.getWebSite());
        result.setNumberOfEmploys(fiModel.getNumberOfEmploys());
        result.setVersion(fiModel.getVersion());

        return result;
    }

    public static List<FiModel> toModels(List<Fi> fiList, long langId) {
        return fiList.stream().map(fi -> toModel(fi, langId)).collect(Collectors.toList());
    }

    public static List<Fi> toEntities(List<FiModel> fiModels) {
        return fiModels.stream().map(fi -> toEntity(fi)).collect(Collectors.toList());
    }
}
