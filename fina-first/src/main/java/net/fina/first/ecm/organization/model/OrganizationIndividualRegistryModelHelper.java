package net.fina.first.ecm.organization.model;

import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import net.fina.first.FirstUtil;
import net.fina.first.ecm.EcmConstants;
import net.fina.first.ecm.node.model.NodeMetaModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class OrganizationIndividualRegistryModelHelper {

    public static List<OrganizationIndividualRegistryMetaModel> getModels(List<NodeMetaModel> nodeMetaModels) {
        List<OrganizationIndividualRegistryMetaModel> result = new ArrayList<>();
        if (nodeMetaModels != null && !nodeMetaModels.isEmpty()) {
            for (NodeMetaModel nodeMetaModel : nodeMetaModels) {
                result.add(getModel(nodeMetaModel));
            }
        }
        return result;
    }

    public static List<OrganizationIndividualRegistryMetaModel> getModelsFromRepresentations(List<? extends NodeRepresentation> nodeRepresentations) {
        List<OrganizationIndividualRegistryMetaModel> result = new ArrayList<>();
        for (NodeRepresentation nodeRepresentation : nodeRepresentations) {
            result.add(getModel(nodeRepresentation));
        }
        return result;
    }

    public static OrganizationIndividualRegistryMetaModel getModel(NodeMetaModel nodeMetaModel) {
        OrganizationIndividualRegistryMetaModel model = new OrganizationIndividualRegistryMetaModel();

        if (nodeMetaModel.getNodeType().equalsIgnoreCase(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_TYPE)) {
            model.setId(nodeMetaModel.getId());
            model.setCreatedAt(nodeMetaModel.getCreatedAt());
            model.setModifiedAt(nodeMetaModel.getModifiedAt());

            setModelProperties(model, nodeMetaModel.getProperties());
        }

        return model;
    }

    public static OrganizationIndividualRegistryMetaModel getModel(NodeRepresentation nodeRepresentation) {
        OrganizationIndividualRegistryMetaModel model = new OrganizationIndividualRegistryMetaModel();

        if (nodeRepresentation.getNodeType().equalsIgnoreCase(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_TYPE)) {
            model.setId(nodeRepresentation.getId());
            model.setCreatedAt(nodeRepresentation.getCreatedAt());
            model.setModifiedAt(nodeRepresentation.getModifiedAt());

            setModelProperties(model, nodeRepresentation.getProperties());
        }
        return model;
    }


    public static TreeMap<String, Object> convertModelToMap(OrganizationIndividualRegistryMetaModel metaModel) {
        TreeMap<String, Object> properties = new TreeMap<>();
        properties.put(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_NAME, metaModel.getName());
        properties.put(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_NAME_LATIN, metaModel.getNameLatin());
        properties.put(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_SURNAME, metaModel.getSurname());
        properties.put(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_TYPE, metaModel.getType());
        properties.put(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_TAX_ID, metaModel.getTaxId());
        properties.put(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_PERSONAL_ID, metaModel.getPersonalId());
        properties.put(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_STATE_REG_OR_DOC_NUMBER, metaModel.getStateRegOrDocNumber());
        properties.put(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_STATE_REG_OR_BIRTH_DATE, metaModel.getStateRegOrBirthDate());
        properties.put(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_ADDRESS, metaModel.getAddress());
        properties.put(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_ID_TYPE, metaModel.getIdType());
        properties.put(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_ORGANIZATIONAL_FORM, metaModel.getOrganizationalForm());
        properties.put(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_GENDER, metaModel.getGender());
        properties.put(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_PHONE, metaModel.getPhone());
        properties.put(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_EMAIL, metaModel.getEmail());
        properties.put(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_EDUCATION, metaModel.getEducation());
        properties.put(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_WEBSITE, metaModel.getWebsite());

        properties.put(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_ASSIGNMENT_DATE, metaModel.getAssignmentDate());
        properties.put(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_DOC_NUMBER, metaModel.getDocumentNumber());
        properties.put(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_POSITION, metaModel.getPosition());
        properties.put(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_BIRTH_PLACE, metaModel.getBirthPlace());
        properties.put(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_CITIZENSHIP, metaModel.getCitizenship());
        properties.put(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_COMMENTS, metaModel.getComments());
        properties.put(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_REGISTRY, metaModel.getRegistryId());
        properties.put(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_REGISTRY_BRANCH, metaModel.getBranchId());
        properties.put(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_ATTESTATION_STATUS, metaModel.getAttestationStatus());

        return properties;
    }

    private static void setModelProperties(OrganizationIndividualRegistryMetaModel model, Map<String, Object> properties) {
        if (properties != null && !properties.isEmpty()) {
            model.setName(FirstUtil.getValue(properties.get(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_NAME), String.class));
            model.setNameLatin(FirstUtil.getValue(properties.get(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_NAME_LATIN), String.class));
            model.setSurname(FirstUtil.getValue(properties.get(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_SURNAME), String.class));
            model.setType(FirstUtil.getValue(properties.get(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_TYPE), String.class));
            model.setTaxId(FirstUtil.getValue(properties.get(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_TAX_ID), String.class));
            model.setPersonalId(FirstUtil.getValue(properties.get(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_PERSONAL_ID), String.class));
            model.setStateRegOrDocNumber(FirstUtil.getValue(properties.get(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_STATE_REG_OR_DOC_NUMBER), String.class));

            String stateRegOrBirthDate = FirstUtil.getValue(properties.get(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_STATE_REG_OR_BIRTH_DATE), String.class);
            model.setStateRegOrBirthDate(FirstUtil.getDateValue(stateRegOrBirthDate, FirstUtil.DATE_FORMAT_LONG_STRING));

            model.setAddress(FirstUtil.getValue(properties.get(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_ADDRESS), String.class));
            model.setIdType(FirstUtil.getValue(properties.get(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_ID_TYPE), String.class));
            model.setOrganizationalForm(FirstUtil.getValue(properties.get(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_ORGANIZATIONAL_FORM), String.class));
            model.setGender(FirstUtil.getValue(properties.get(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_GENDER), String.class));
            model.setPhone(FirstUtil.getValue(properties.get(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_PHONE), String.class));
            model.setEmail(FirstUtil.getValue(properties.get(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_EMAIL), String.class));
            model.setEducation(FirstUtil.getValue(properties.get(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_EDUCATION), String.class));
            model.setWebsite(FirstUtil.getValue(properties.get(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_WEBSITE), String.class));

            model.setAssignmentDate(FirstUtil.getDateValue(FirstUtil.getValue(properties.get(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_ASSIGNMENT_DATE), String.class), FirstUtil.DATE_FORMAT_LONG_STRING));
            model.setDocumentNumber(FirstUtil.getValue(properties.get(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_DOC_NUMBER), String.class));
            model.setPosition(FirstUtil.getValue(properties.get(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_POSITION), String.class));
            model.setBirthPlace(FirstUtil.getValue(properties.get(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_BIRTH_PLACE), String.class));
            model.setCitizenship(FirstUtil.getValue(properties.get(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_CITIZENSHIP), String.class));
            model.setComments(FirstUtil.getValue(properties.get(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_COMMENTS), String.class));
            model.setRegistryId(FirstUtil.getValue(properties.get(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_REGISTRY), String.class));
            model.setBranchId(FirstUtil.getValue(properties.get(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_REGISTRY_BRANCH), String.class));
            model.setAttestationStatus(FirstUtil.getValue(properties.get(EcmConstants.ORGANIZATION_INDIVIDUAL_REGISTRY_PROP_ATTESTATION_STATUS), String.class));
        }
    }

}
