package net.fina.first.ecm.organization.model;

import net.fina.first.FirstUtil;
import net.fina.first.ecm.EcmConstants;
import net.fina.first.ecm.node.model.NodeMetaModel;

import java.util.*;

public class OrganizationIndividualLicenseCertificateModelHelper {

    public static List<OrganizationIndividualLicenseCertificateMetaModel> getModels(List<NodeMetaModel> nodeMetaModels) {
        List<OrganizationIndividualLicenseCertificateMetaModel> result = new ArrayList<>();
        if (nodeMetaModels != null && !nodeMetaModels.isEmpty()) {
            for (NodeMetaModel nodeMetaModel : nodeMetaModels) {
                result.add(getModel(nodeMetaModel));
            }
        }
        return result;
    }

    public static OrganizationIndividualLicenseCertificateMetaModel getModel(NodeMetaModel nodeMetaModel) {
        OrganizationIndividualLicenseCertificateMetaModel model = new OrganizationIndividualLicenseCertificateMetaModel();

        if (nodeMetaModel.getNodeType().equalsIgnoreCase(EcmConstants.ORGANIZATION_INDIVIDUAL_LICENSE_CERTIFICATE_TYPE)) {
            model.setId(nodeMetaModel.getId());
            model.setCreatedAt(nodeMetaModel.getCreatedAt());
            model.setModifiedAt(nodeMetaModel.getModifiedAt());

            Map<String, Object> properties = nodeMetaModel.getProperties();
            if (properties != null && !properties.isEmpty()) {
                model.setUniqueNumber(FirstUtil.getValue(properties.get(EcmConstants.ORGANIZATION_INDIVIDUAL_LICENSE_CERTIFICATE_PROP_UNIQUE_NUMBER), String.class));
                model.setStatus(FirstUtil.getValue(properties.get(EcmConstants.ORGANIZATION_INDIVIDUAL_LICENSE_CERTIFICATE_PROP_STATUS), String.class));

                String issueDate = FirstUtil.getValue(properties.get(EcmConstants.ORGANIZATION_INDIVIDUAL_LICENSE_CERTIFICATE_PROP_ISSUE_DATE), String.class);
                model.setIssueDate(FirstUtil.getDateValue(issueDate, FirstUtil.DATE_FORMAT_LONG_STRING));

                model.setResolutionDocNumber(FirstUtil.getValue(properties.get(EcmConstants.ORGANIZATION_INDIVIDUAL_LICENSE_CERTIFICATE_PROP_RESOLUTION_DOC_NUMBER), String.class));
                model.setType(new OrganizationIndividualLicenseCertificateTypeMetaModel(FirstUtil.getValue(properties.get(EcmConstants.ORGANIZATION_INDIVIDUAL_LICENSE_CERTIFICATE_PROP_TYPE_ID), String.class)));

                String expirationDate = FirstUtil.getValue(properties.get(EcmConstants.ORGANIZATION_INDIVIDUAL_LICENSE_CERTIFICATE_PROP_EXPIRATION_DATE), String.class);
                model.setExpirationDate(FirstUtil.getDateValue(expirationDate, FirstUtil.DATE_FORMAT_LONG_STRING));

                String suspendDate = FirstUtil.getValue(properties.get(EcmConstants.ORGANIZATION_INDIVIDUAL_LICENSE_CERTIFICATE_PROP_SUSPEND_DATE), String.class);
                model.setSuspendDate(FirstUtil.getDateValue(suspendDate, FirstUtil.DATE_FORMAT_LONG_STRING));

                model.setSuspendReason(FirstUtil.getValue(properties.get(EcmConstants.ORGANIZATION_INDIVIDUAL_LICENSE_CERTIFICATE_PROP_SUSPEND_REASON), String.class));
            }
        }

        return model;
    }

    public static TreeMap<String, Object> convertModelToMap(OrganizationIndividualLicenseCertificateMetaModel metaModel) {
        TreeMap<String, Object> properties = new TreeMap<>();
        properties.put(EcmConstants.ORGANIZATION_INDIVIDUAL_LICENSE_CERTIFICATE_PROP_UNIQUE_NUMBER, metaModel.getUniqueNumber());
        properties.put(EcmConstants.ORGANIZATION_INDIVIDUAL_LICENSE_CERTIFICATE_PROP_STATUS, metaModel.getStatus());
        properties.put(EcmConstants.ORGANIZATION_INDIVIDUAL_LICENSE_CERTIFICATE_PROP_ISSUE_DATE, metaModel.getIssueDate());
        properties.put(EcmConstants.ORGANIZATION_INDIVIDUAL_LICENSE_CERTIFICATE_PROP_RESOLUTION_DOC_NUMBER, metaModel.getResolutionDocNumber());
        properties.put(EcmConstants.ORGANIZATION_INDIVIDUAL_LICENSE_CERTIFICATE_PROP_TYPE_ID, metaModel.getType().getId());
        properties.put(EcmConstants.ORGANIZATION_INDIVIDUAL_LICENSE_CERTIFICATE_PROP_EXPIRATION_DATE, metaModel.getExpirationDate());
        properties.put(EcmConstants.ORGANIZATION_INDIVIDUAL_LICENSE_CERTIFICATE_PROP_SUSPEND_DATE, metaModel.getSuspendDate());
        properties.put(EcmConstants.ORGANIZATION_INDIVIDUAL_LICENSE_CERTIFICATE_PROP_SUSPEND_REASON, metaModel.getSuspendReason());

        return properties;
    }

    public static TreeMap<String, Object> convertModelToMap(OrganizationIndividualLicenseCertificateTypeMetaModel metaModel) {
        TreeMap<String, Object> properties = new TreeMap<>();
        properties.put(EcmConstants.ORGANIZATION_INDIVIDUAL_LICENSE_CERTIFICATETYPE_PROP_TYPE, metaModel.getType());
        properties.put(EcmConstants.ORGANIZATION_INDIVIDUAL_LICENSE_CERTIFICATETYPE_PROP_NAME, metaModel.getName());
        properties.put(EcmConstants.ORGANIZATION_INDIVIDUAL_LICENSE_CERTIFICATETYPE_PROP_IDENTIFIER, metaModel.getIdentifier());
        properties.put(EcmConstants.ORGANIZATION_INDIVIDUAL_LICENSE_CERTIFICATETYPE_PROP_DOCUMENT_NUMBER, metaModel.getDocumentNumber());
        properties.put(EcmConstants.ORGANIZATION_INDIVIDUAL_LICENSE_CERTIFICATETYPE_PROP_ALLOWED_OPS, metaModel.getAllowedOperations());
        properties.put(EcmConstants.ORGANIZATION_INDIVIDUAL_LICENSE_CERTIFICATETYPE_PROP_REGISTRATION_DATE, metaModel.getRegistrationDate() != null ? metaModel.getRegistrationDate() : new Date());

        return properties;
    }

    public static OrganizationIndividualLicenseCertificateTypeMetaModel getTypeModel(NodeMetaModel nodeMetaModel) {
        OrganizationIndividualLicenseCertificateTypeMetaModel model = new OrganizationIndividualLicenseCertificateTypeMetaModel();

        if (nodeMetaModel.getNodeType().equalsIgnoreCase(EcmConstants.ORGANIZATION_INDIVIDUAL_LICENSE_CERTIFICATETYPE_TYPE)) {
            Map<String, Object> properties = nodeMetaModel.getProperties();
            model.setId(nodeMetaModel.getId());

            if (properties != null && !properties.isEmpty()) {
                model.setType(FirstUtil.getValue(properties.get(EcmConstants.ORGANIZATION_INDIVIDUAL_LICENSE_CERTIFICATETYPE_PROP_TYPE), String.class));
                model.setName(FirstUtil.getValue(properties.get(EcmConstants.ORGANIZATION_INDIVIDUAL_LICENSE_CERTIFICATETYPE_PROP_NAME), String.class));
                model.setIdentifier(FirstUtil.getValue(properties.get(EcmConstants.ORGANIZATION_INDIVIDUAL_LICENSE_CERTIFICATETYPE_PROP_IDENTIFIER), String.class));
                model.setDocumentNumber(FirstUtil.getValue(properties.get(EcmConstants.ORGANIZATION_INDIVIDUAL_LICENSE_CERTIFICATETYPE_PROP_DOCUMENT_NUMBER), String.class));
                model.setAllowedOperations(FirstUtil.getValue(properties.get(EcmConstants.ORGANIZATION_INDIVIDUAL_LICENSE_CERTIFICATETYPE_PROP_ALLOWED_OPS), List.class));
                model.setRegistrationDate(nodeMetaModel.getCreatedAt());

            }
        }

        return model;
    }

    public static List<OrganizationIndividualLicenseCertificateTypeMetaModel> getTypeModels(List<NodeMetaModel> nodeMetaModels) {
        List<OrganizationIndividualLicenseCertificateTypeMetaModel> result = new ArrayList<>();
        if (nodeMetaModels != null && !nodeMetaModels.isEmpty()) {
            for (NodeMetaModel nodeMetaModel : nodeMetaModels) {
                result.add(getTypeModel(nodeMetaModel));
            }
        }
        return result;
    }


}
