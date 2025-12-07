package net.fina.server.fi.model;

import net.fina.common.client.fis.FiGroupModel;
import net.fina.server.fi.entity.Fi;
import net.fina.server.fi.entity.PeerGroup;
import net.fina.server.i18n.helper.Description;
import net.fina.server.i18n.model.DescriptionModelHelper;
import net.fina.server.legalperson.model.helper.LegalPersonMetaInfoModelHelper;
import net.fina.server.license.entity.Licence;
import net.fina.server.person.model.helper.CriminalRecordModelHelper;

import java.util.*;
import java.util.stream.Collectors;

public class FiModelFullHelper {

    public static FiModelFull toModel(Fi fi, long langId) {
        FiModelFull result = new FiModelFull();
        result.setId(fi.getId());
        result.setCode(fi.getCode());
        result.setName(fi.getDescription().getDescription(langId));
        result.setNameStrId(fi.getDescription().getNameStrId());
        result.setVersion(fi.getVersion());
        result.setAddressString(fi.getAddressDescription() != null ? fi.getAddressDescription().getDescription(langId) : null);
        result.setDisable(fi.isDisable());
        result.setPhone(fi.getPhone());
        result.setEmail(fi.getEmail());
        result.setFax(fi.getFax());
        result.setDescriptionModel(DescriptionModelHelper.toModel(fi.getDescription()).stream().filter(f -> f.getLangId() == langId).findAny().orElse(null));
        result.setLegalForm(fi.getLegalForm());
        result.setFiTypeModel(FiTypeModelHelper.toModel(fi.getFiType(), langId));
        result.setIdentificationCode(fi.getIdentificationCode());
        result.setReorganization(fi.getReorganization());
        result.setRepresentativePerson(fi.getRepresentativePerson());
        result.setSwiftCode(fi.getSwiftCode());
        result.setWebSite(fi.getWebSite());
        result.setRegionId(fi.getRegionId());
        result.setShortNameString(fi.getShortName().getDescription(langId));
        result.setShortNameStrId(fi.getShortName().getNameStrId());
        result.setRegistrationDate(fi.getRegistrationDate());
        result.setCloseDate(fi.getCloseDate());
        result.setNumberOfMobileOffices(fi.getNumberOfMobileOffices());
        result.setNumberOfEmploys(fi.getNumberOfEmploys());
        result.setLegalForm(fi.getLegalForm());
        result.setWebSite(fi.getWebSite());
        result.setPhone(fi.getPhone());
        result.setModifiedAt(fi.getModifiedAt());

        if (fi.getRegionId() != null) {
            result.setRegionId(fi.getRegionId());
        }

        if (fi.getLicences() != null) {
            Optional<Licence> licence = fi.getLicences().stream().filter(Licence::getIsDefault).findAny();

            licence.ifPresent(value -> result.setLicenseCode(value.getCode()));
        }

        if (fi.getCriminalRecords() != null) {
            result.setCriminalRecords(CriminalRecordModelHelper.toModels(fi.getCriminalRecords(), langId));
        }

        if (fi.getFiAdditionalInfo() != null) {
            result.setAdditionalInfo(LegalPersonMetaInfoModelHelper.toModel(fi.getFiAdditionalInfo()));
        }
        if (fi.getPeerGroup() != null) {
            result.setFiGroupModels(FiGroupModelHelper.toModelsFromPeerGroups(fi.getPeerGroup(), langId));
        }

        return result;
    }

    public static Fi toEntity(FiModelFull fiModel, long langId) {
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
        result.setRegionId(fiModel.getRegionId());
        result.setRegionId(fiModel.getRegionId());

        if (fiModel.getCriminalRecords() != null) {
            result.setCriminalRecords(CriminalRecordModelHelper.toEntities(fiModel.getCriminalRecords(), langId));
        }


        if (fiModel.getAdditionalInfo() != null) {
            result.setFiAdditionalInfo(LegalPersonMetaInfoModelHelper.toEntity(fiModel.getAdditionalInfo()));
        }

        return result;
    }

    public static List<FiModelFull> toModels(List<Fi> fiList, long langId) {
        return fiList.stream().map(fi -> toModel(fi, langId)).collect(Collectors.toList());
    }

    public static List<Fi> toEntities(List<FiModelFull> fiModels, long langId) {
        return fiModels.stream().map(fi -> toEntity(fi, langId)).collect(Collectors.toList());
    }
}
