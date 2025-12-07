package net.fina.first.ecm.fi.model;

import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import net.fina.first.FirstUtil;
import net.fina.first.ecm.EcmConstants;
import net.fina.first.ecm.node.model.NodeMetaModel;

import java.util.*;

public class FiDocumentRequestModelHelper {

    public static List<FiDocumentRequestMetaModel> getModels(List<NodeMetaModel> nodeMetaModels, Map<String, String> fiIdNameMap) {
        List<FiDocumentRequestMetaModel> result = new ArrayList<>();
        if (nodeMetaModels != null && !nodeMetaModels.isEmpty()) {
            for (NodeMetaModel nodeMetaModel : nodeMetaModels) {
                result.add(getModel(nodeMetaModel, fiIdNameMap));
            }
        }
        return result;
    }

    public static List<FiDocumentRequestMetaModel> getDocumentModels(List<? extends NodeRepresentation> data, Map<String, String> fiIdNameMap) {
        List<FiDocumentRequestMetaModel> result = new ArrayList<>();
        if (data != null && !data.isEmpty()) {
            for (NodeRepresentation node : data) {
                result.add(getModel(node, fiIdNameMap));
            }
        }
        return result;
    }

    public static FiDocumentRequestMetaModel getModel(NodeRepresentation nodeRepresentation, Map<String, String> fiIdNameMap) {
        if (nodeRepresentation.getNodeType().equalsIgnoreCase(EcmConstants.FI_DOCUMENT_REQUEST_TYPE)) {
            return getModel(nodeRepresentation.getId(), nodeRepresentation.getCreatedAt(), nodeRepresentation.getModifiedAt(), nodeRepresentation.getProperties(), fiIdNameMap);
        }
        return new FiDocumentRequestMetaModel();
    }

    public static FiDocumentRequestMetaModel getModel(NodeMetaModel nodeMetaModel, Map<String, String> fiIdNameMap) {
        if (nodeMetaModel.getNodeType().equalsIgnoreCase(EcmConstants.FI_DOCUMENT_REQUEST_TYPE)) {
            return getModel(nodeMetaModel.getId(), nodeMetaModel.getCreatedAt(), nodeMetaModel.getModifiedAt(), nodeMetaModel.getProperties(), fiIdNameMap);
        }
        return new FiDocumentRequestMetaModel();
    }

    private static FiDocumentRequestMetaModel getModel(String id, Date createdAt, Date modifiedAt, Map<String, Object> properties, Map<String, String> fiIdNameMap) {
        FiDocumentRequestMetaModel metaModel = new FiDocumentRequestMetaModel();

        metaModel.setId(id);
        metaModel.setCreatedAt(createdAt);
        metaModel.setModifiedAt(modifiedAt);

        if (properties != null && !properties.isEmpty()) {
            metaModel.setName(FirstUtil.getValue(properties.get(EcmConstants.FI_DOCUMENT_REQUEST_PROP_NAME), String.class));
            metaModel.setDescription(FirstUtil.getValue(properties.get(EcmConstants.FI_DOCUMENT_REQUEST_PROP_DESCRIPTION), String.class));
            metaModel.setAssigneeFiCode(FirstUtil.getValue(properties.get(EcmConstants.FI_DOCUMENT_REQUEST_PROP_ASSIGNEE_FI_CODE), String.class));
            metaModel.setSubmitted(FirstUtil.getValue(properties.get(EcmConstants.FI_DOCUMENT_REQUEST_PROP_IS_SUBMITTED), Boolean.class));
            metaModel.setSubmissionDate(FirstUtil.getDateValue(FirstUtil.getValue(properties.get(EcmConstants.FI_DOCUMENT_REQUEST_PROP_SUBMISSION_DATE), String.class), FirstUtil.DATE_FORMAT_LONG_STRING));
            metaModel.setDueDate(FirstUtil.getDateValue(FirstUtil.getValue(properties.get(EcmConstants.FI_DOCUMENT_REQUEST_PROP_DUE_DATE), String.class), FirstUtil.DATE_FORMAT_LONG_STRING));
            metaModel.setAssigneeFiId(FirstUtil.getValue(properties.get(EcmConstants.FI_DOCUMENT_REQUEST_PROP_FI_ID), String.class));
            metaModel.setComment(FirstUtil.getValue(properties.get(EcmConstants.FI_DOCUMENT_REQUEST_PROP_COMMENT), String.class));
            metaModel.setFiObjectTypes(FirstUtil.getValue(properties.get(EcmConstants.FI_DOCUMENT_REQUEST_PROP_FI_OBJECT_TYPES), List.class));

            if (fiIdNameMap != null) {
                String fiRegistryName = fiIdNameMap.get(metaModel.getAssigneeFiId());
                metaModel.setAssigneeFiName(fiRegistryName);
            }
        }
        return metaModel;
    }

    public static TreeMap<String, Object> convertModelToMap(FiDocumentRequestMetaModel metaModel) {
        TreeMap<String, Object> properties = new TreeMap<>();
        properties.put(EcmConstants.FI_DOCUMENT_REQUEST_PROP_NAME, metaModel.getName());
        properties.put(EcmConstants.FI_DOCUMENT_REQUEST_PROP_DESCRIPTION, metaModel.getDescription());
        properties.put(EcmConstants.FI_DOCUMENT_REQUEST_PROP_ASSIGNEE_FI_CODE, metaModel.getAssigneeFiCode());
        properties.put(EcmConstants.FI_DOCUMENT_REQUEST_PROP_IS_SUBMITTED, metaModel.isSubmitted());
        properties.put(EcmConstants.FI_DOCUMENT_REQUEST_PROP_SUBMISSION_DATE, metaModel.getSubmissionDate());
        properties.put(EcmConstants.FI_DOCUMENT_REQUEST_PROP_DUE_DATE, metaModel.getDueDate());
        properties.put(EcmConstants.FI_DOCUMENT_REQUEST_PROP_FI_ID, metaModel.getAssigneeFiId());
        properties.put(EcmConstants.FI_DOCUMENT_REQUEST_PROP_COMMENT, metaModel.getComment());
        properties.put(EcmConstants.FI_DOCUMENT_REQUEST_PROP_FI_OBJECT_TYPES, metaModel.getFiObjectTypes());

        return properties;
    }
}
