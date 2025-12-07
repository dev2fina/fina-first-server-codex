package net.fina.first.ecm.sync.model;

import net.fina.common.server.fi.sync.model.FiDescriptionSyncMetaModel;
import net.fina.common.server.fi.sync.model.FiManagementSyncMetaModel;
import net.fina.first.FirstUtil;
import net.fina.first.ecm.EcmConstants;
import net.fina.first.ecm.sync.util.FiSyncUtil;

import java.util.List;
import java.util.Map;

public class FiStructureBeneficiarySyncModelHelper {

    public static FiManagementSyncMetaModel getFiManagementPhysicalSyncMetaModel(List<Long> languageIds, long managementBodyId, Map<String, Object> properties) {
        FiManagementSyncMetaModel metaModel = new FiManagementSyncMetaModel();
        metaModel.setManagementBodyId(managementBodyId);

        String firstName = FirstUtil.getValue(properties.get(EcmConstants.FI_PERSON_PROP_FIRSTNAME), String.class);
        metaModel.setFirstNameDescriptions(FiSyncUtil.getDescriptionsByLanguages(languageIds, firstName));

        String lastName = FirstUtil.getValue(properties.get(EcmConstants.FI_PERSON_PROP_LASTNAME), String.class);
        metaModel.setLastNameDescriptions(FiSyncUtil.getDescriptionsByLanguages(languageIds, lastName));

        metaModel.setPersonalNumber(FirstUtil.getValue(properties.get(EcmConstants.FI_PERSON_PROP_PERSONAL_NUMBER), String.class));
        metaModel.setPhone(FirstUtil.getValue(properties.get(EcmConstants.FI_PERSON_PROP_PHONE), String.class));

        return metaModel;
    }

}
