package net.fina.first.ecm.sync.model;

import net.fina.common.server.fi.sync.model.FiManagementSyncMetaModel;
import net.fina.first.FirstUtil;
import net.fina.first.ecm.EcmConstants;
import net.fina.first.ecm.sync.util.FiSyncUtil;
import net.fina.messages.MessagesUtil;

import java.util.List;
import java.util.Map;

public class FiAdministratorSyncModelHelper {

    public static FiManagementSyncMetaModel getFiManagementSyncMetaModel(List<Long> languageIds, long managementBodyId, Map<String, Object> properties) {
        FiManagementSyncMetaModel metaModel = new FiManagementSyncMetaModel();
        metaModel.setManagementBodyId(managementBodyId);

        String firstName = FirstUtil.getValue(properties.get(EcmConstants.FI_PERSON_PROP_FIRSTNAME), String.class);
        metaModel.setFirstNameDescriptions(FiSyncUtil.getDescriptionsByLanguages(languageIds, firstName));

        String lastName = FirstUtil.getValue(properties.get(EcmConstants.FI_PERSON_PROP_LASTNAME), String.class);
        metaModel.setLastNameDescriptions(FiSyncUtil.getDescriptionsByLanguages(languageIds, lastName));

        String positionPropName = properties.keySet().stream().filter(item -> item.endsWith("fiAuthorizedPersonPosition")).findAny().orElse(null);
        if (positionPropName != null) {
            String position = MessagesUtil.getString(FirstUtil.getValue(properties.get(positionPropName), String.class));
            metaModel.setPositionDescriptions(FiSyncUtil.getDescriptionsByLanguages(languageIds, position));
        }

        metaModel.setPersonalNumber(FirstUtil.getValue(properties.get(EcmConstants.FI_PERSON_PROP_PERSONAL_NUMBER), String.class));
        metaModel.setPhone(FirstUtil.getValue(properties.get(EcmConstants.FI_PERSON_PROP_PHONE), String.class));
        metaModel.setFinalStatus(FirstUtil.getValue(properties.get(EcmConstants.COMMON_PROP_STATUS_FINAL_STATUS), String.class));
        metaModel.setNonResidentDocumentNumber(FirstUtil.getValue(properties.get(EcmConstants.FI_PERSON_PROP_NON_RESIDENT_DOC_NUMBER), String.class));

        return metaModel;
    }

}
