package net.fina.first.ecm.registry.model;

import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import net.fina.first.FirstUtil;
import net.fina.first.ecm.EcmConstants;
import net.fina.first.ecm.fi.model.FiTypeMetaModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FiRegistryMetaModelHelper {

    public static FiRegistryMetaModel getModel(NodeRepresentation registryNode) {
        FiRegistryMetaModel model = new FiRegistryMetaModel();

        model.setId(registryNode.getId());

        Map<String, Object> properties = registryNode.getProperties();

        if (properties != null && !properties.isEmpty()) {
            model.setCode(FirstUtil.getValue(properties.get(EcmConstants.REGISTRY_PROP_CODE), String.class));
            model.setIdentity(FirstUtil.getValue(properties.get(EcmConstants.REGISTRY_PROP_IDENTITY), String.class));
            model.setName(FirstUtil.getValue(properties.get(EcmConstants.REGISTRY_PROP_NAME), String.class));

            model.setCreatedAt(registryNode.getCreatedAt());

            String author = FirstUtil.getValue(properties.get(EcmConstants.REGISTRY_PROP_LAST_EDITOR_FULL_NAME), String.class);
            if(author != null) {
                author = author.replaceAll("NONAME", "");
                author = author.replaceAll(" ", "").length() == 0 ? FirstUtil.getValue(properties.get(EcmConstants.REGISTRY_PROP_LAST_EDITOR_ID), String.class) : author;
                model.setAuthor(author.trim());
            } else {
                model.setAuthor(FirstUtil.getValue(properties.get(EcmConstants.REGISTRY_PROP_LAST_EDITOR_ID), String.class));
            }

            model.setStatus(FirstUtil.getValue(properties.get(EcmConstants.REGISTRY_PROP_STATUS), String.class));
            model.setActionType(FiRegistryActionType.valueOf(FirstUtil.getValue(properties.get(EcmConstants.REGISTRY_PROP_ACTION_TYPE), String.class)));
            model.setFiTypeCode(FirstUtil.getValue(properties.get(EcmConstants.REGISTRY_PROP_TYPE_CODE), String.class));
            model.setLastProcessId(FirstUtil.getValue(properties.get(EcmConstants.REGISTRY_PROP_LAST_PROCESS_ID), String.class));
            model.setLastActionId(FirstUtil.getValue(properties.get(EcmConstants.REGISTRY_PROP_LAST_ACTION_ID), String.class));
            String lastActionDate = FirstUtil.getValue(properties.get(EcmConstants.REGISTRY_PROP_LAST_ACTION_DATE), String.class);
            model.setLastActionDate(FirstUtil.getDateValue(lastActionDate, FirstUtil.DATE_FORMAT_LONG_STRING));

            String licenseStatus = FirstUtil.getValue(properties.get(EcmConstants.REGISTRY_PROP_LICENSE_STATUS), String.class);
            model.setLicenseStatus(licenseStatus == null ? FiRegistryLicenseStatus.INACTIVE : FiRegistryLicenseStatus.valueOf(licenseStatus));

            model.setIsHistoricData((properties.get(EcmConstants.REGISTRY_PROP_IS_HISTORIC_DATA) != null) ? FirstUtil.getValue(properties.get(EcmConstants.REGISTRY_PROP_IS_HISTORIC_DATA), Boolean.class) : false);
            model.setPhone(FirstUtil.getValue(properties.get(EcmConstants.REGISTRY_PROP_PHONE), String.class));
            model.setMail(FirstUtil.getValue(properties.get(EcmConstants.REGISTRY_PROP_EMAIL), String.class));
            model.setBinder(FirstUtil.getValue(properties.get(EcmConstants.REGISTRY_PROP_BINDER), String.class));

            Integer fiRegistryArchivedGapTaskCount = FirstUtil.getValue(properties.get(EcmConstants.REGISTRY_PROP_ARCHIVED_GAP_TASK_COUNT), Integer.class);
            model.setArchivedGapTaskCount(fiRegistryArchivedGapTaskCount != null ? fiRegistryArchivedGapTaskCount : 0);

            model.setLegalFormType(FirstUtil.getValue(properties.get(EcmConstants.REGISTRY_PROP_LEGAL_FORM_TYPE), String.class));

            // legal address
            model.setLegalAddressRegion(FirstUtil.getValue(properties.get(EcmConstants.REGISTRY_PROP_LEGAL_ADDRESS_REGION), String.class));
            model.setLegalAddressCity(FirstUtil.getValue(properties.get(EcmConstants.REGISTRY_PROP_LEGAL_ADDRESS_CITY), String.class));
            model.setLegalAddress(FirstUtil.getValue(properties.get(EcmConstants.REGISTRY_PROP_LEGAL_ADDRESS), String.class));

            // registration date in case of NBG
            String registrationDate = FirstUtil.getValue(properties.get(EcmConstants.REGISTRY_PROP_TASK_DATE), String.class);
            model.setRegistrationDate(FirstUtil.getDateValue(registrationDate, FirstUtil.DATE_FORMAT_LONG_STRING));

            String legalActDate = FirstUtil.getValue(properties.get(EcmConstants.REGISTRY_PROP_ACT_DATE), String.class);
            model.setLastLegalActDate(FirstUtil.getDateValue(legalActDate, FirstUtil.DATE_FORMAT_LONG_STRING));

            model.setLastLegalActNumber(FirstUtil.getValue(properties.get(EcmConstants.REGISTRY_PROP_ACT_NUMBER), String.class));
        }

        return model;
    }

    public static List<FiRegistryMetaModel> getAll(List<? extends NodeRepresentation> nodes) {
        List<FiRegistryMetaModel> result = new ArrayList<>();

        nodes.forEach(node -> {
            result.add(getModel(node));
        });

        return result;
    }

    public static FiRegistryMetaModel convertFiType(FiTypeMetaModel type) {
        FiRegistryMetaModel model = new FiRegistryMetaModel();
        model.setId(type.getId());
        model.setFiTypeCode(type.getCode());
        model.setCode(type.getCode());
        model.setName(type.getDescription());

        return model;
    }

}
