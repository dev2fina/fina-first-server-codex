package net.fina.server.fi.model;

import net.fina.server.fi.entity.FiBranch;
import net.fina.server.fi.entity.FiBranchType;
import net.fina.server.fi.entity.FiPerson;
import net.fina.server.fi.entity.Region;
import net.fina.server.i18n.helper.Description;
import net.fina.server.person.model.helper.PersonModelHelper;

import java.util.ArrayList;
import java.util.List;

public class FiBranchMetaModelHelper {

    public static List<FiBranchMetaModel> toModels(List<FiBranch> fiBranches, long langId) {
        List<FiBranchMetaModel> result = new ArrayList<>();
        if (fiBranches != null && !fiBranches.isEmpty()) {
            for (FiBranch fiBranch : fiBranches) {
                result.add(toModel(fiBranch, langId));
            }
        }
        return result;
    }

    public static FiBranchMetaModel toModel(FiBranch fiBranch, long langId) {
        FiBranchMetaModel model = new FiBranchMetaModel();
        model.setId(fiBranch.getId());
        model.setBankId(fiBranch.getBankId());
        model.setCreateDate(fiBranch.getCreateDate());
        model.setChangeDate(fiBranch.getChangeDate());
        model.setVersion(fiBranch.getVersion());
        model.setDisable(fiBranch.isDisable());
        model.setDeleted(fiBranch.isDeleted());

        model.setName(fiBranch.getName().getDescription(langId));
        model.setNameStrId(fiBranch.getName().getNameStrId());

        model.setShortName(fiBranch.getShortName().getDescription(langId));
        model.setShortNameStrId(fiBranch.getShortName().getNameStrId());

        model.setAddress(fiBranch.getAddress().getDescription(langId));
        model.setAddressStrId(fiBranch.getAddress().getNameStrId());

        model.setComment(fiBranch.getComment().getDescription(langId));
        model.setCommentStrId(fiBranch.getComment().getNameStrId());

        model.setCloseDate(fiBranch.getCloseDate());
        model.setSuspensionDate(fiBranch.getSuspensionDate());
        model.setRenewalDate(fiBranch.getRenewalDate());
        model.setEmail(fiBranch.getEmail());
        model.setPhone(fiBranch.getPhone());
        model.setRegistrationNumber(fiBranch.getRegistrationNumber());
        model.setIsStorageAvailable(fiBranch.getStorageAvailable());
        model.setCode(fiBranch.getCode());

        Region region = fiBranch.getRegion();
        if (region != null) {
            RegionMetaModel regionModel = RegionMetaModelHelper.toModel(region, langId);
            model.setRegionModel(regionModel);
            model.setRegion(regionModel.getId());
        }
        FiBranchType fiBranchType = fiBranch.getFiBranchType();
        if (fiBranchType != null) {
            model.setFiBranchTypeId(fiBranchType.getId());
            model.setFiBranchTypeName(fiBranchType.getName() != null ? fiBranchType.getName().getDescription(langId) : null);
        }

        if (fiBranch.getManager() != null && fiBranch.getManager().getPerson() != null) {
            model.setManager(PersonModelHelper.toModel(fiBranch.getManager().getPerson(), langId));
            model.getManager().setFiPersonId(fiBranch.getManager().getId());
        }
        model.setManagerAppointmentDate(fiBranch.getManagerAppointmentDate());

        if (fiBranch.getChiefAccountant() != null && fiBranch.getChiefAccountant().getPerson() != null) {
            model.setChiefAccountant(PersonModelHelper.toModel(fiBranch.getChiefAccountant().getPerson(), langId));
            model.getChiefAccountant().setFiPersonId(fiBranch.getChiefAccountant().getId());
        }
        model.setChiefAccountantAppointmentDate(fiBranch.getChiefAccountantAppointmentDate());

        model.setDisable(fiBranch.isDisable());

        return model;
    }

    public static FiBranch toEntity(FiBranchMetaModel model, FiPerson manager, FiPerson chiefAccountant, long langId) {
        FiBranch entity = new FiBranch();
        entity.setId(model.getId());
        entity.setBankId(model.getBankId());
        entity.setCreateDate(model.getCreateDate());
        entity.setChangeDate(model.getChangeDate());
        entity.setCloseDate(model.getCloseDate());
        entity.setSuspensionDate(model.getSuspensionDate());
        entity.setRenewalDate(model.getRenewalDate());
        entity.setEmail(model.getEmail());
        entity.setPhone(model.getPhone());
        entity.setRegistrationNumber(model.getRegistrationNumber());
        entity.setStorageAvailable(model.getIsStorageAvailable());
        entity.setVersion(model.getVersion());
        entity.setDisable(model.isDisable());
        entity.setCode(model.getCode());

        entity.setName(new Description(langId, model.getNameStrId(), model.getName()));
        entity.setShortName(new Description(langId, model.getShortNameStrId(), model.getShortName()));
        entity.setAddress(new Description(langId, model.getAddressStrId(), model.getAddress()));
        entity.setComment(new Description(langId, model.getCommentStrId(), model.getComment()));

        Region region = null;
        if (model.getRegionModel() != null) {
            region = new Region();
            region.setId(model.getRegionModel().getId());
        }
        entity.setRegion(region);

        FiBranchType type = new FiBranchType();
        type.setId(model.getFiBranchTypeId());
        entity.setFiBranchType(type);

        entity.setManager(manager);
        entity.setManagerAppointmentDate(model.getManagerAppointmentDate());

        entity.setChiefAccountant(chiefAccountant);
        entity.setChiefAccountantAppointmentDate(model.getChiefAccountantAppointmentDate());

        return entity;
    }
}
