package net.fina.first.ecm.sync.model;

import net.fina.common.server.fi.sync.model.FiBranchSyncMetaModel;
import net.fina.common.server.fi.sync.model.FiDescriptionSyncMetaModel;
import net.fina.first.FirstUtil;
import net.fina.first.ecm.EcmConstants;
import net.fina.first.ecm.sync.util.FiSyncUtil;

import java.util.List;
import java.util.Map;

public class FiBranchSyncModelHelper {

    public static FiBranchSyncMetaModel getMetaModel(List<Long> languageIds, Map<String, Object> properties) {
        String region = FirstUtil.getValue(properties.get(EcmConstants.BRANCH_PROP_ADDRESS_REGION), String.class);
        String city = FirstUtil.getValue(properties.get(EcmConstants.BRANCH_PROP_ADDRESS_CITY), String.class);
        String address = FirstUtil.getValue(properties.get(EcmConstants.BRANCH_PROP_ADDRESS_ADDRESS), String.class);
        String status = FirstUtil.getValue(properties.get(EcmConstants.COMMON_PROP_STATUS), String.class);

        List<FiDescriptionSyncMetaModel> branchNameDescriptions = FiSyncUtil.getDescriptionsByLanguages(languageIds, getBranchName(region, city, address));

        FiBranchSyncMetaModel metaModel = new FiBranchSyncMetaModel();
        metaModel.setNameDescriptions(branchNameDescriptions);
        metaModel.setShortNameDescription(branchNameDescriptions);
        metaModel.setRegionName(region);
        metaModel.setAddressDescriptions(FiSyncUtil.getDescriptionsByLanguages(languageIds, address));
        metaModel.setStatus(status);
        metaModel.setRegistrationDate(FirstUtil.getDateValue((String) properties.get(EcmConstants.BRANCH_PROP_REGISTRATION_DATE), FirstUtil.DATE_FORMAT_LONG_STRING));
        metaModel.setLegalActDate(FirstUtil.getDateValue((String) properties.get(EcmConstants.BRANCH_PROP_LEGAL_ACT_DATE), FirstUtil.DATE_FORMAT_LONG_STRING));


        return metaModel;
    }

    private static String getBranchName(String region, String city, String address) {
        return (region != null ? region + " - " : "") + (city != null ? city + " - " : "") + (address != null ? address : "");
    }

}
