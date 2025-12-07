package net.fina.first.ecm.fi.impl;

import net.fina.common.shared.PaginatedListWrapper;
import net.fina.ecm.alfresco.AlfrescoClient;
import net.fina.ecm.alfresco.api.common.constant.APIConstants;
import net.fina.ecm.alfresco.api.common.parameters.IncludeParam;
import net.fina.ecm.alfresco.api.common.representation.UnexpectedErrorRepresentation;
import net.fina.ecm.alfresco.api.dictionary.model.ClassPropertyRepresentation;
import net.fina.ecm.util.AlfrescoConfiguration;
import net.fina.ecm.util.AlfrescoPropConstants;
import net.fina.first.FirstUtil;
import net.fina.first.common.exception.NodeException;
import net.fina.first.ecm.EcmConstants;
import net.fina.first.ecm.client.proxy.EcmClientProxySession;
import net.fina.first.ecm.fi.api.AttestationLocal;
import net.fina.first.ecm.fi.model.AttestationDocumentType;
import net.fina.first.ecm.node.api.NodeLocal;
import net.fina.first.ecm.node.model.NodeMetaModel;
import net.fina.first.ecm.node.model.NodeModelHelper;
import net.fina.first.ecm.search.api.SearchLocal;
import net.fina.first.ecm.template.TemplateProcessor;
import net.fina.first.interceptors.FirstRecordingAuditor;
import net.fina.messages.MessagesUtil;
import org.apache.commons.io.IOUtils;
import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;
import org.jboss.resteasy.plugins.providers.multipart.OutputPart;

import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Stateless
@Local(AttestationLocal.class)
@Interceptors(FirstRecordingAuditor.class)
public class AttestationSession implements AttestationLocal {
    private final Logger log = Logger.getLogger(getClass().getName());

    @Inject
    private NodeLocal nodeLocal;

    @Inject
    private SearchLocal searchLocal;

    @Inject
    private EcmClientProxySession ecmClientProxySession;

    @Override
    public PaginatedListWrapper<NodeMetaModel> load(String acceptLanguage, String query, String filter, String sort,
                                                    String group, int page, int start, int limit) {
        String nodeType = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.DATA_TYPE_ATTESTATION_KEY);
        StringBuilder sb = new StringBuilder("(TYPE:'" + nodeType + "' ");
        if (query != null && !query.isEmpty()) {
            String searchTemplate = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.ATTESTATION_SEARCH_TEMPLATE_PROP_NAME);
            if (searchTemplate != null && !searchTemplate.isEmpty()) {
                sb.append(" AND (")
                        .append(searchTemplate.replace("{0}", query))
                        .append(")");
            }
        }
        sb.append(")");

        PaginatedListWrapper<NodeMetaModel> result;
        if ((query == null || query.isEmpty()) && (filter == null || filter.isEmpty())) {
            String attestationFolderPath = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.ATTESTATION_ROOT_FOLDER_PATH_KEY);
            result = nodeLocal.getNodeChildren(acceptLanguage, APIConstants.FOLDER_ROOT, start, limit, null, FirstUtil.getOrderByParam(sort, group),
                    "(nodeType='" + nodeType + "')", new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)),
                    attestationFolderPath, null, null);
        } else {
            result = searchLocal.searchNodes(acceptLanguage, sb.toString(), start, limit,
                    searchLocal.getSortDefinition(sort, group), searchLocal.getAFTSFilterString(filter));
        }

        result.setList(result.getList().stream().peek(item -> {
            List<NodeMetaModel> files = loadDocuments(acceptLanguage, item);
            for (NodeMetaModel file : files) {
                String docType = FirstUtil.getValue(file.getProperties().get(EcmConstants.DOCUMENT_PROP_DOCUMENT_TYPE), String.class);
                if (docType != null && item.getFile()) {
                    item.getProperties().put(docType, file);
                }
            }
        }).collect(Collectors.toList()));

        return result;
    }

    @Override
    public NodeMetaModel generateDocument(String acceptLanguage, AttestationDocumentType documentType, Map<String, Object> filter) throws NodeException {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient();
        String templateNameConstant;
        switch (documentType) {
            case ATTESTATION_IN_QUEUE_CANDIDATES_SHORT:
                templateNameConstant = AlfrescoPropConstants.ATTESTATION_IN_QUEUE_CANDIDATES_SHORT_TEMPLATE_PROP_NAME;
                break;
            case ATTESTATION_IN_QUEUE_CANDIDATES_LONG:
                templateNameConstant = AlfrescoPropConstants.ATTESTATION_IN_QUEUE_CANDIDATES_LONG_TEMPLATE_PROP_NAME;
                break;
            default:
                throw new NodeException(new UnexpectedErrorRepresentation(500, "Template not found"));
        }

        StringBuilder sb = new StringBuilder("(TYPE:'" + AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.DATA_TYPE_ATTESTATION_KEY) + "' ");
        if (!filter.isEmpty()) {
            for (Map.Entry<String, Object> entry : filter.entrySet()) {
                sb.append(" AND ").append(entry.getKey()).append(":'").append(entry.getValue().toString()).append("'");
            }
        }
        sb.append(")");
        List<NodeMetaModel> nodes = searchLocal.searchNodes(acceptLanguage, sb.toString(), 0, 0).getList();
        NodeMetaModel templateNode = getTemplateByName(acceptLanguage, AlfrescoConfiguration.get().getAlfrescoProperty(templateNameConstant));
        byte[] fileContent = getGeneratedDocumentContent(client, null, nodes, templateNode);

        NodeMetaModel attestationDocFolder = nodeLocal.getNodeById(APIConstants.FOLDER_ROOT, null,
                AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.ATTESTATION_DOCUMENT_FOLDER_PATH_KEY));

        return generateCustomDocument(acceptLanguage, client, attestationDocFolder, documentType, templateNode != null ? templateNode.getName() : null, fileContent);
    }

    @Override
    public NodeMetaModel generateDocument(String acceptLanguage, String nodeId, AttestationDocumentType documentType) throws NodeException {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient();
        String templateNameConstant;
        switch (documentType) {
            case ATTESTATION_SHEET:
                templateNameConstant = AlfrescoPropConstants.ATTESTATION_SHEET_TEMPLATE_PROP_NAME;
                break;
            case ATTESTATION_REJECTION_LETTER:
                templateNameConstant = AlfrescoPropConstants.ATTESTATION_REJECTION_LETTER_TEMPLATE_PROP_NAME;
                break;
            case ATTESTATION_REJECTION_INTERVIEW_LETTER:
                templateNameConstant = AlfrescoPropConstants.ATTESTATION_REJECTION_INTERVIEW_LETTER_TEMPLATE_PROP_NAME;
                break;
            case ATTESTATION_CONFIRMATION_LETTER:
                templateNameConstant = AlfrescoPropConstants.ATTESTATION_CONFIRMATION_LETTER_TEMPLATE_PROP_NAME;
                break;
            case ATTESTATION_INVITATION_LETTER:
                templateNameConstant = AlfrescoPropConstants.ATTESTATION_INVITATION_LETTER_TEMPLATE_PROP_NAME;
                break;
            case ATTESTATION_BOARD_DECISION_DRAFT:
                templateNameConstant = AlfrescoPropConstants.ATTESTATION_BOARD_DECISION_DRAFT_TEMPLATE_PROP_NAME;
                break;
            default:
                throw new NodeException(new UnexpectedErrorRepresentation(500, "Template not found"));
        }

        NodeMetaModel currentNode = nodeLocal.getNodeById(nodeId, new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)).toString(), null);
        NodeMetaModel templateNode = getTemplateByName(acceptLanguage, AlfrescoConfiguration.get().getAlfrescoProperty(templateNameConstant));
        byte[] fileContent = getGeneratedDocumentContent(client, currentNode, null, templateNode);

        return generateCustomDocument(acceptLanguage, client, currentNode, documentType, templateNode != null ? templateNode.getName() : null, fileContent);
    }

    private NodeMetaModel getTemplateByName(String acceptLanguage, String templateName) {
        PaginatedListWrapper<NodeMetaModel> templates = nodeLocal.getNodeChildren(acceptLanguage, APIConstants.FOLDER_ROOT,
                null, null, null, null, null, null,
                AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.ATTESTATION_TEMPLATE_FOLDER_PATH_KEY), null, null);

        for (NodeMetaModel n : templates.getList()) {
            if (n.getName().equals(templateName)) {
                return n;
            }
        }

        return null;
    }

    private NodeMetaModel generateCustomDocument(String acceptLanguage, AlfrescoClient client, NodeMetaModel parentNode, AttestationDocumentType documentType, String fileName, byte[] content)
            throws NodeException {
        NodeMetaModel existingDocument = checkExistingCustomDocument(acceptLanguage, parentNode, documentType, fileName);

        if (existingDocument != null) { // regenerate
            InputStream targetStream = new ByteArrayInputStream(content);
            StreamingOutput out = outputStream -> outputStream.write(IOUtils.toByteArray(targetStream));

            return NodeModelHelper.getMetaModel(client.getNodesAPI().updateUploadNodeCall(existingDocument.getId(), out, false,
                    new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)), null));
        } else { // create new
            MultipartFormDataOutput mdo = new MultipartFormDataOutput();

            InputStream targetStream = new ByteArrayInputStream(content);
            OutputPart objPart = mdo.addFormData("filedata", targetStream, MediaType.TEXT_PLAIN_TYPE);
            objPart.getHeaders().putSingle("Content-Disposition", "form-data; name=" + "filedata" + "; filename=" + fileName);
            mdo.addFormData("name", fileName, MediaType.TEXT_PLAIN_TYPE);

            mdo.addFormData("majorVersion", true, MediaType.TEXT_PLAIN_TYPE);
            mdo.addFormData("nodeType", EcmConstants.DOCUMENT_TYPE_DOCUMENT, MediaType.TEXT_PLAIN_TYPE);
            mdo.addFormData(EcmConstants.DOCUMENT_PROP_DOCUMENT_TYPE, documentType.toString(), MediaType.TEXT_PLAIN_TYPE);

            return NodeModelHelper.getMetaModel(nodeLocal.createUploadNode(parentNode.getId(), mdo, true, null, null, client));
        }
    }

    private NodeMetaModel checkExistingCustomDocument(String acceptLanguage, NodeMetaModel parentNode, AttestationDocumentType documentType, String fileName) {
        PaginatedListWrapper<NodeMetaModel> children = nodeLocal.getNodeChildren(acceptLanguage, parentNode.getId(),
                null, null, null, null, null, new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)),
                null, null, null);

        for (NodeMetaModel child : children.getList()) {
            String childDocType = FirstUtil.getValue(child.getProperties().get(EcmConstants.DOCUMENT_PROP_DOCUMENT_TYPE), String.class);
            if (child.getName().equals(fileName) && documentType.toString().equals(childDocType)) {
                return child;
            }
        }

        return null;
    }

    private byte[] getGeneratedDocumentContent(AlfrescoClient client, NodeMetaModel dataNode, List<NodeMetaModel> dataNodes, NodeMetaModel templateNode) {
        if (templateNode != null) {
            Response contentResponse = client.getNodesAPI().getNodeContent(templateNode.getId(), true);
            List<ClassPropertyRepresentation> classProps = client.getDictionaryAPI().getClassPropertiesCall(
                    AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.DATA_TYPE_ATTESTATION_KEY).replace(":", "_"));

            try {
                InputStream inputStream = contentResponse.readEntity(InputStream.class);
                byte[] templateContent = IOUtils.toByteArray(inputStream);

                TemplateProcessor processor = new TemplateProcessor();

                Map<String, Object> data = getNodeProps(dataNode, classProps);
                data.put("DATE", new Date());
                return processor.process(templateContent, data, getNodeProps(dataNodes, classProps));
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
            }
        }
        return null;
    }

    private Map<String, Object> getNodeProps(NodeMetaModel dataNode, List<ClassPropertyRepresentation> classProps) {
        Map<String, Object> data = new HashMap<>();
        if (dataNode != null) {
            for (Map.Entry<String, Object> entry : dataNode.getProperties().entrySet()) {
                Object value = entry.getValue();
                if (value != null) {
                    ClassPropertyRepresentation classProp = classProps.stream()
                            .filter(item -> item.getName().equals(entry.getKey())).findFirst().orElse(null);
                    if (classProp != null) {
                        if (classProp.getDataType().equals("d:date")) {
                            value = FirstUtil.getDateValue(value.toString(), FirstUtil.DATE_FORMAT_LONG_STRING);
                        } else if (!classProp.getConstraints().isEmpty() &&
                                classProp.getConstraints().stream().anyMatch(item -> item.getType().equals("LIST"))) {
                            value = MessagesUtil.getString(value.toString());
                        }
                    }
                    data.put(entry.getKey().split(":")[1], value);
                }
            }
        }
        return data;
    }

    private List<Map<String, Object>> getNodeProps(List<NodeMetaModel> dataNodes, List<ClassPropertyRepresentation> classProps) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (dataNodes != null) {
            for (NodeMetaModel dataNode : dataNodes) {
                result.add(getNodeProps(dataNode, classProps));
            }
        }
        return result;
    }

    private List<NodeMetaModel> loadDocuments(String acceptLanguage, NodeMetaModel item) {
        return nodeLocal.getNodeChildren(acceptLanguage, item.getId(), null, null, null, null,
                null, new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)),
                null, null, null).getList();
    }

}
