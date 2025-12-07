package net.fina.server.legalperson.model.helper;

import net.fina.server.ThreadLocalHolder;
import net.fina.server.i18n.helper.Description;
import net.fina.server.legalperson.entity.metainfo.*;
import net.fina.common.shared.legalperson.metainfo.CodeDescriptionModel;
import net.fina.common.shared.legalperson.metainfo.LegalPersonMetaInfoModel;

public class LegalPersonMetaInfoModelHelper {

    public static LegalPersonMetaInfoModel toModel(LegalPersonMetaInfo metaInfo) {
        long langId = ThreadLocalHolder.getLanguage().getId();

        LegalPersonMetaInfoModel result = new LegalPersonMetaInfoModel();

        result.setId(metaInfo.getId());

        if (metaInfo.getBusinessEntityType() != null) {
            result.setBusinessEntity(new CodeDescriptionModel(metaInfo.getBusinessEntityType().getId(),
                    metaInfo.getBusinessEntityType().getCode(),
                    metaInfo.getBusinessEntityType().getDescription().getDescription(langId)));
        }
        if (metaInfo.getEconomicEntityType() != null) {
            result.setEconomicEntity(new CodeDescriptionModel(metaInfo.getEconomicEntityType().getId(),
                    metaInfo.getEconomicEntityType().getCode(),
                    metaInfo.getEconomicEntityType().getDescription().getDescription(langId)));
        }

        if (metaInfo.getEquityFormType() != null) {
            result.setEquityForm(new CodeDescriptionModel(metaInfo.getEquityFormType().getId(),
                    metaInfo.getEquityFormType().getCode(),
                    metaInfo.getEquityFormType().getDescription().getDescription(langId)));
        }

        if (metaInfo.getManagementFormType() != null) {
            result.setManagementForm(new CodeDescriptionModel(metaInfo.getManagementFormType().getId(),
                    metaInfo.getManagementFormType().getCode(),
                    metaInfo.getManagementFormType().getDescription().getDescription(langId)));
        }

        return result;
    }

    public static LegalPersonMetaInfo toEntity(LegalPersonMetaInfoModel metaInfo) {
        long langId = ThreadLocalHolder.getLanguage().getId();

        LegalPersonMetaInfo result = new LegalPersonMetaInfo();
        result.setId(metaInfo.getId());

        if (metaInfo.getBusinessEntity() != null) {
            result.setBusinessEntityType(new BusinessEntityType(metaInfo.getBusinessEntity().getId(),
                    metaInfo.getBusinessEntity().getCode(),
                    new Description(langId, metaInfo.getBusinessEntity().getNameStrId(), metaInfo.getBusinessEntity().getDescription())));
        }

        if (metaInfo.getEconomicEntity() != null) {
            result.setEconomicEntityType(new EconomicEntityType(metaInfo.getEconomicEntity().getId(),
                    metaInfo.getEconomicEntity().getCode(),
                    new Description(langId, metaInfo.getEconomicEntity().getNameStrId(), metaInfo.getEconomicEntity().getDescription())));
        }

        if (metaInfo.getEquityForm() != null) {
            result.setEquityFormType(new EquityFormType(metaInfo.getEquityForm().getId(),
                    metaInfo.getEquityForm().getCode(),
                    new Description(langId, metaInfo.getEquityForm().getNameStrId(), metaInfo.getEquityForm().getDescription())));
        }

        if (metaInfo.getManagementForm() != null) {
            result.setManagementFormType(new ManagementFormType(metaInfo.getManagementForm().getId(),
                    metaInfo.getManagementForm().getCode(),
                    new Description(langId, metaInfo.getManagementForm().getNameStrId(), metaInfo.getManagementForm().getDescription())));
        }

        return result;
    }
}
