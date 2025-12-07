package net.fina.first.ecm.fi.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.core.MediaType;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.ecm.alfresco.api.common.constant.APIConstants;
import net.fina.ecm.alfresco.api.common.parameters.IncludeParam;
import net.fina.ecm.alfresco.api.common.parameters.OrderByParam;
import net.fina.ecm.alfresco.api.core.model.body.NodeBodyCreate;
import net.fina.ecm.alfresco.api.core.model.body.NodeBodyUpdate;
import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import net.fina.ecm.alfresco.api.search.model.ResultNodeRepresentation;
import net.fina.ecm.alfresco.api.search.model.ResultSetRepresentation;
import net.fina.ecm.util.AlfrescoConfiguration;
import net.fina.ecm.util.AlfrescoPropConstants;
import net.fina.first.FirstUtil;
import net.fina.first.common.exception.NodeException;
import net.fina.first.ecm.EcmConstants;
import net.fina.first.ecm.fi.api.FiDocumentRequestLocal;
import net.fina.first.ecm.fi.model.FiDocumentRequestFileModel;
import net.fina.first.ecm.fi.model.FiDocumentRequestMetaModel;
import net.fina.first.ecm.fi.model.FiDocumentRequestModelHelper;
import net.fina.first.ecm.node.api.NodeLocal;
import net.fina.first.ecm.node.model.NodeMetaModel;
import net.fina.first.ecm.node.model.NodeModelHelper;
import net.fina.first.ecm.search.api.SearchLocal;
import net.fina.first.interceptors.FirstRecordingAuditor;
import org.apache.commons.io.IOUtils;
import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Stateless
@Local(FiDocumentRequestLocal.class)
@Interceptors(FirstRecordingAuditor.class)
public class FiDocumentRequestSession implements FiDocumentRequestLocal {
    private final Logger log = Logger.getLogger(FiDocumentRequestSession.class);
    @Inject
    private NodeLocal nodeProxySession;

    @Inject
    private SearchLocal searchLocal;

    @Override
    public PaginatedListWrapper<FiDocumentRequestMetaModel> loadFiDocumentRequests(String acceptLanguage, int page, int start, int limit) throws NodeException {

        List<FiDocumentRequestMetaModel> resultModels = new ArrayList<>();

        PaginatedListWrapper<FiDocumentRequestMetaModel> result = new PaginatedListWrapper<>();
        result.setList(resultModels);
        result.setCurrentPage(page);

        NodeRepresentation fiDocumentRequestRootNode = getFiDocumentRequestRootFolder();
        if (fiDocumentRequestRootNode != null) {
            PaginatedListWrapper<NodeMetaModel> fiDocumentRequestNodes = nodeProxySession.getNodeChildren(acceptLanguage, fiDocumentRequestRootNode.getId(), start, limit, null, new OrderByParam(Collections.singletonList("createdAt desc")), "(nodeType=" + EcmConstants.FI_DOCUMENT_REQUEST_TYPE + ")", new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)), null, null, null);
            List<NodeMetaModel> fiDocumentRequestNodeList = fiDocumentRequestNodes.getList();
            Map<String, String> fiIdNameMap = getFiRegistryIdNameMap(fiDocumentRequestNodeList);

            result.setTotalResults(fiDocumentRequestNodes.getTotalResults());
            resultModels.addAll(FiDocumentRequestModelHelper.getModels(fiDocumentRequestNodeList, fiIdNameMap));
        }

        return result;
    }

    @Override
    public FiDocumentRequestMetaModel createFiDocumentRequest(FiDocumentRequestMetaModel fiDocumentRequestMetaModel) throws NodeException {
        NodeRepresentation fiDocumentRequestRootNode = getFiDocumentRequestRootFolder();
        if (fiDocumentRequestRootNode != null) {
            NodeBodyCreate nodeBodyCreate = new NodeBodyCreate(UUID.randomUUID().toString(), EcmConstants.FI_DOCUMENT_REQUEST_TYPE, FiDocumentRequestModelHelper.convertModelToMap(fiDocumentRequestMetaModel), null);
            NodeMetaModel newNode = nodeProxySession.createChildNode(fiDocumentRequestRootNode.getId(), nodeBodyCreate);
            fiDocumentRequestMetaModel.setId(newNode.getId());
            fiDocumentRequestMetaModel.setCreatedAt(newNode.getCreatedAt());
            fiDocumentRequestMetaModel.setModifiedAt(newNode.getModifiedAt());

            // create templates and submitted documents folders
            nodeProxySession.createChildNode(newNode.getId(), new NodeBodyCreate("Templates", "cm:folder"));
            nodeProxySession.createChildNode(newNode.getId(), new NodeBodyCreate("Submitted Documents", "cm:folder"));
            nodeProxySession.createChildNode(newNode.getId(), new NodeBodyCreate("Fi Objects", "cm:folder"));

            initFiObjectData(fiDocumentRequestMetaModel);
        }

        return fiDocumentRequestMetaModel;
    }

    @Override
    public List<FiDocumentRequestMetaModel> createFiDocumentRequest(MultipartFormDataInput multipartForm) throws NodeException {
        boolean autoRename = false;
        List<String> fiIds = new ArrayList<>();
        List<String> fiCodes = new ArrayList<>();
        Map<String, Object> data = new HashMap<>();
        List<String> fileNames = new ArrayList<>();
        List<MultipartFormDataOutput> files = new ArrayList<>();
        try {
            for (Map.Entry<String, List<InputPart>> inputPartEntry : multipartForm.getFormDataMap().entrySet()) {
                String partId = inputPartEntry.getKey();
                List<InputPart> inputParts = inputPartEntry.getValue();

                for (InputPart part : inputParts) {
                    InputStream inputStream;

                    if (partId.equals(APIConstants.AUTO_RENAME_VALUE)) {
                        autoRename = part.getBody(Boolean.class, Boolean.TYPE);
                    } else if (partId.equals("fiIds")) {
                        fiIds.add(part.getBodyAsString());
                    } else if (partId.equals("fiCodes")) {
                        fiCodes.add(IOUtils.toString(part.getBody(InputStream.class, null), StandardCharsets.UTF_8.name()));
                    } else if (partId.equals("fileNames")) {
                        fileNames.add(IOUtils.toString(part.getBody(InputStream.class, null), StandardCharsets.UTF_8.name()));
                    } else if (partId.startsWith(APIConstants.MULTIPART_FILE_DATA)) {
                        inputStream = part.getBody(InputStream.class, null);
                        MultipartFormDataOutput mdo = new MultipartFormDataOutput();
                        mdo.addFormData(partId, inputStream, part.getMediaType());
                        files.add(mdo);
                    } else {
                        data.put(partId, IOUtils.toString(part.getBody(InputStream.class, null), StandardCharsets.UTF_8.name()));
                    }
                }
            }

            for (int i = 0; i < files.size(); i++) {
                MultipartFormDataOutput formData = files.get(i);
                formData.getParts().get(0).getHeaders().putSingle("Content-Disposition",
                        "form-data; name=" + APIConstants.MULTIPART_FILE_DATA + "; filename=" + fileNames.get(i));
                formData.addFormData("name", fileNames.get(i), MediaType.TEXT_PLAIN_TYPE);

            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ArrayList<>();
        }
        return createFiDocumentRequests(data, fiIds, fiCodes, autoRename, files);
    }

    private List<FiDocumentRequestMetaModel> createFiDocumentRequests(Map<String, Object> data, List<String> fiIds, List<String> fiCodes,
                                                                      boolean autoRename, List<MultipartFormDataOutput> files) throws NodeException {
        ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        FiDocumentRequestMetaModel model = objectMapper.convertValue(data, FiDocumentRequestMetaModel.class);
        if (model.getFiObjectTypes() != null && !model.getFiObjectTypes().isEmpty()) {
            model.setFiObjectTypes(Arrays.asList(model.getFiObjectTypes().get(0).split(",")));
        }

        NodeRepresentation fiDocumentRequestRootNode = getFiDocumentRequestRootFolder();
        List<FiDocumentRequestMetaModel> createdModels = new ArrayList<>();
        if (fiDocumentRequestRootNode != null) {
            for (int i = 0; i < fiIds.size(); i++) {
                model.setAssigneeFiId(fiIds.get(i));
                model.setAssigneeFiCode(fiCodes.get(i));

                NodeBodyCreate nodeBodyCreate = new NodeBodyCreate(UUID.randomUUID().toString(), EcmConstants.FI_DOCUMENT_REQUEST_TYPE, FiDocumentRequestModelHelper.convertModelToMap(model), null);
                NodeMetaModel newNode = nodeProxySession.createChildNode(fiDocumentRequestRootNode.getId(), nodeBodyCreate);
                Map<String, String> fiIdNameMap = getFiRegistryIdNameMap(Collections.singletonList(newNode));
                createdModels.add(FiDocumentRequestModelHelper.getModel(newNode, fiIdNameMap));

                // create templates and submitted documents folders
                nodeProxySession.createChildNode(newNode.getId(), new NodeBodyCreate("Submitted Documents", "cm:folder"));
                nodeProxySession.createChildNode(newNode.getId(), new NodeBodyCreate("Fi Objects", "cm:folder"));

                NodeMetaModel templatesFolder = nodeProxySession.createChildNode(newNode.getId(), new NodeBodyCreate("Templates", "cm:folder"));
                for (MultipartFormDataOutput file : files) {
                    nodeProxySession.createUploadNode(templatesFolder.getId(), file, autoRename, null, null, null);
                }

                initFiObjectData(FiDocumentRequestModelHelper.getModel(newNode, fiIdNameMap));
            }
        }

        return createdModels;
    }

    @Override
    public FiDocumentRequestMetaModel updateFiDocumentRequest(String fiDocumentRequestNodeId, FiDocumentRequestMetaModel fiDocumentRequestMetaModel) throws NodeException {
        String fiId = searchLocal.searchByCMIS("select * from fina:fiRegistry where fina:fiRegistryCode='" + fiDocumentRequestMetaModel.getAssigneeFiCode() + "'", 1, 0, Integer.MAX_VALUE).getObjects().get(0).getId();

        if (!fiId.equals(fiDocumentRequestMetaModel.getAssigneeFiId())) {
            fiDocumentRequestMetaModel.setAssigneeFiId(fiId);
            nodeProxySession.deleteNodeById(nodeProxySession.getNodeById(fiDocumentRequestMetaModel.getId(), null, "/Fi Objects", null).getId());
            nodeProxySession.createChildNode(fiDocumentRequestMetaModel.getId(), new NodeBodyCreate("Fi Objects", "cm:folder"));
            initFiObjectData(fiDocumentRequestMetaModel);
        }

        NodeBodyUpdate nodeBodyUpdate = new NodeBodyUpdate(FiDocumentRequestModelHelper.convertModelToMap(fiDocumentRequestMetaModel));
        NodeMetaModel updatedNode = nodeProxySession.updateNode(fiDocumentRequestNodeId, nodeBodyUpdate);
        fiDocumentRequestMetaModel.setModifiedAt(updatedNode.getModifiedAt());
        return fiDocumentRequestMetaModel;
    }

    @Override
    public void deleteFiDocumentRequest(String fiDocumentRequestNodeId) throws NodeException {
        NodeRepresentation nodeRepresentation = nodeProxySession.getNodeById(fiDocumentRequestNodeId, null, null, null);
        if (nodeRepresentation.getNodeType().equalsIgnoreCase(EcmConstants.FI_DOCUMENT_REQUEST_TYPE)) {
            nodeProxySession.deleteNodeById(fiDocumentRequestNodeId);
        }
    }

    @Override
    public List<NodeMetaModel> loadFiDocumentRequestDocuments(String acceptLanguage, String fiDocumentRequestNodeId) {
        List<NodeMetaModel> result = new ArrayList<>();

        if (fiDocumentRequestNodeId != null && !fiDocumentRequestNodeId.trim().isEmpty()) {
            NodeRepresentation nodeRepresentation = nodeProxySession.getNodeById(fiDocumentRequestNodeId, null, null, null);
            if (nodeRepresentation.getNodeType().equalsIgnoreCase(EcmConstants.FI_DOCUMENT_REQUEST_TYPE)) {

                // load submitted documents
                PaginatedListWrapper<NodeMetaModel> submittedDocuments = nodeProxySession.getNodeChildren(acceptLanguage, fiDocumentRequestNodeId, null, null, null, null, null, new IncludeParam(Arrays.asList("properties", "permissions", "path")), "Submitted Documents", null, null);
                if (submittedDocuments != null && submittedDocuments.getList() != null) {
                    for (NodeMetaModel submittedDocument : submittedDocuments.getList()) {
                        submittedDocument.getProperties().put("fiRequestDocumentType", "SUBMITTED_DOCUMENT");
                    }
                    result.addAll(submittedDocuments.getList());
                }

                // load templates
                PaginatedListWrapper<NodeMetaModel> templates = nodeProxySession.getNodeChildren(acceptLanguage, fiDocumentRequestNodeId, null, null, null, null, null, new IncludeParam(Arrays.asList("properties", "permissions", "path")), "Templates", null, null);
                if (templates != null && templates.getList() != null) {
                    for (NodeMetaModel template : templates.getList()) {
                        template.getProperties().put("fiRequestDocumentType", "TEMPLATE");
                    }
                    result.addAll(templates.getList());
                }

            }
        }

        return result;
    }

    @Override
    public PaginatedListWrapper<FiDocumentRequestMetaModel> loadFiDocumentRequestsByCode(String acceptLanguage, String fiRegistryNodeId, int page, int start, int limit) {
        List<FiDocumentRequestMetaModel> resultModels = new ArrayList<>();

        PaginatedListWrapper<FiDocumentRequestMetaModel> result = new PaginatedListWrapper<>();
        result.setList(resultModels);

        NodeRepresentation fiRegistryNode = nodeProxySession.getNodeById(fiRegistryNodeId, new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)).toString(), null, null);
        String fiCode = FirstUtil.getValue(fiRegistryNode.getProperties().get(EcmConstants.REGISTRY_PROP_CODE), String.class);

        String query = "select * from fina:fiDocumentRequest where fina:fiDocumentRequestAssigneeFiCode='" + fiCode + "' order by fina:fiDocumentRequestIsSubmitted asc,cmis:creationDate DESC";

        ResultSetRepresentation<ResultNodeRepresentation> searchResult = searchLocal.searchByCMIS(query, page, start, limit);


        if (searchResult != null && searchResult.getObjects() != null) {
            result.setTotalResults(searchResult.getPagination().getTotalItems());
            result.setCurrentPage(page);
            searchResult.getObjects().forEach(node -> {
                NodeMetaModel nodeMetaModel = NodeModelHelper.getMetaModel(node);
                FiDocumentRequestMetaModel fiDocumentRequestMetaModel = FiDocumentRequestModelHelper.getModel(node, getFiRegistryIdNameMap(Collections.singletonList(nodeMetaModel)));
                List<FiDocumentRequestFileModel> files = new ArrayList<>();
                List<NodeMetaModel> documents = getFiSubmittedDocuments(node, acceptLanguage);
                fiDocumentRequestMetaModel.setFiles(files);
                if (documents != null) {
                    documents.forEach(doc -> {
                        files.add(new FiDocumentRequestFileModel(doc.getId(), doc.getName()));
                    });
                }
                resultModels.add(fiDocumentRequestMetaModel);
            });
        }

        return result;

    }

    private List<NodeMetaModel> getFiSubmittedDocuments(NodeRepresentation documentRequestNode, String acceptLanguage) {
        PaginatedListWrapper<NodeMetaModel> documents = nodeProxySession.getNodeChildren(acceptLanguage, documentRequestNode.getId(), 0, 1000, null, new OrderByParam(Collections.singletonList("createdAt desc")), null, null, "Submitted Documents", null, null);

        return documents.getList();
    }

    private NodeRepresentation getFiDocumentRequestRootFolder() {
        String fiDocumentRequestRootFolderPath = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.FI_DOCUMENT_REQUEST_ROOT_FOLDER_PATH_KEY);
        return nodeProxySession.getNodeById(APIConstants.FOLDER_ROOT, null, fiDocumentRequestRootFolderPath, null);
    }

    private void initFiObjectData(FiDocumentRequestMetaModel documentMetaModel) throws NodeException {
        String objectFolderId = nodeProxySession.getNodeById(documentMetaModel.getId(), null, "/Fi Objects", null).getId();

        if (documentMetaModel.getFiObjectTypes() != null) {
            for (String type : documentMetaModel.getFiObjectTypes()) {
                try {
                    switch (type) {
                        case EcmConstants.FI_DOCUMENT_REQUEST_PROP_FI_OBJECT_TYPE_BRANCH:
                            PaginatedListWrapper<NodeMetaModel> branchNodes = nodeProxySession.getNodeChildren("*", documentMetaModel.getAssigneeFiId(), 0, Integer.MAX_VALUE, null, null, null, new IncludeParam(Arrays.asList("properties")), "/Branches", null, null);
                            if (branchNodes.getList() != null && !branchNodes.getList().isEmpty()) {
                                for (NodeMetaModel nodeMetaModel : branchNodes.getList()) {
                                    String region = FirstUtil.getValue(nodeMetaModel.getProperties().get(EcmConstants.BRANCH_PROP_ADDRESS_REGION), String.class);
                                    String city = FirstUtil.getValue(nodeMetaModel.getProperties().get(EcmConstants.BRANCH_PROP_ADDRESS_CITY), String.class);
                                    String address = FirstUtil.getValue(nodeMetaModel.getProperties().get(EcmConstants.BRANCH_PROP_ADDRESS_ADDRESS), String.class);
                                    TreeMap<String, Object> properties = new TreeMap<>();

                                    properties.put(EcmConstants.FI_DOCUMENT_REQUEST_PROP_FI_OBJECT_NAME, getSafeStringValue(region) + ", " + getSafeStringValue(city) + ", " + getSafeStringValue(address));
                                    properties.put(EcmConstants.FI_DOCUMENT_REQUEST_PROP_FI_OBJECT_TYPE, EcmConstants.FI_DOCUMENT_REQUEST_PROP_FI_OBJECT_TYPE_BRANCH);
                                    nodeProxySession.createChildNode(objectFolderId, new NodeBodyCreate(UUID.randomUUID().toString(), EcmConstants.FI_DOCUMENT_REQUEST_PROP_FI_OBJECT, properties, null));
                                }
                            }
                            break;
                        case EcmConstants.FI_DOCUMENT_REQUEST_PROP_FI_OBJECT_TYPE_BENEFICIARY:
                            PaginatedListWrapper<NodeMetaModel> authorizedPersonNodes = nodeProxySession.getNodeChildren("*", documentMetaModel.getAssigneeFiId(), 0, Integer.MAX_VALUE, null, null, null, new IncludeParam(Arrays.asList("properties")), "/Authorized Persons", null, null);
                            if (authorizedPersonNodes.getList() != null && !authorizedPersonNodes.getList().isEmpty()) {
                                for (NodeMetaModel nodeMetaModel : authorizedPersonNodes.getList()) {
                                    String firstName = FirstUtil.getValue(nodeMetaModel.getProperties().get(EcmConstants.FI_PERSON_PROP_FIRSTNAME), String.class);
                                    String lastName = FirstUtil.getValue(nodeMetaModel.getProperties().get(EcmConstants.FI_PERSON_PROP_LASTNAME), String.class);
                                    String personalNumber = FirstUtil.getValue(nodeMetaModel.getProperties().get(EcmConstants.FI_PERSON_PROP_PERSONAL_NUMBER), String.class);
                                    TreeMap<String, Object> properties = new TreeMap<>();

                                    properties.put(EcmConstants.FI_DOCUMENT_REQUEST_PROP_FI_OBJECT_NAME, (getSafeStringValue(firstName) + " " + getSafeStringValue(lastName) + " " + getSafeStringValue(personalNumber)).trim());
                                    properties.put(EcmConstants.FI_DOCUMENT_REQUEST_PROP_FI_OBJECT_TYPE, EcmConstants.FI_DOCUMENT_REQUEST_PROP_FI_OBJECT_TYPE_BENEFICIARY);
                                    nodeProxySession.createChildNode(objectFolderId, new NodeBodyCreate(UUID.randomUUID().toString(), EcmConstants.FI_DOCUMENT_REQUEST_PROP_FI_OBJECT, properties, null));
                                }
                            }

                            break;
                        case EcmConstants.FI_DOCUMENT_REQUEST_PROP_FI_OBJECT_TYPE_COMPLEX_STRUCTURE:
                            PaginatedListWrapper<NodeMetaModel> complexStructureNodes = nodeProxySession.getNodeChildren("*", documentMetaModel.getAssigneeFiId(), 0, Integer.MAX_VALUE, null, null, null, new IncludeParam(Arrays.asList("properties")), "/Complex Structures", null, null);
                            if (complexStructureNodes.getList() != null && !complexStructureNodes.getList().isEmpty()) {
                                for (NodeMetaModel nodeMetaModel : complexStructureNodes.getList()) {
                                    String firstName = FirstUtil.getValue(nodeMetaModel.getProperties().get(EcmConstants.FI_PERSON_PROP_FIRSTNAME), String.class);
                                    String lastName = FirstUtil.getValue(nodeMetaModel.getProperties().get(EcmConstants.FI_PERSON_PROP_LASTNAME), String.class);
                                    String personalNumber = FirstUtil.getValue(nodeMetaModel.getProperties().get(EcmConstants.FI_PERSON_PROP_PERSONAL_NUMBER), String.class);
                                    TreeMap<String, Object> properties = new TreeMap<>();

                                    properties.put(EcmConstants.FI_DOCUMENT_REQUEST_PROP_FI_OBJECT_NAME, (getSafeStringValue(firstName) + " " + getSafeStringValue(lastName) + " " + getSafeStringValue(personalNumber)).trim());
                                    properties.put(EcmConstants.FI_DOCUMENT_REQUEST_PROP_FI_OBJECT_TYPE, EcmConstants.FI_DOCUMENT_REQUEST_PROP_FI_OBJECT_TYPE_COMPLEX_STRUCTURE);
                                    nodeProxySession.createChildNode(objectFolderId, new NodeBodyCreate(UUID.randomUUID().toString(), EcmConstants.FI_DOCUMENT_REQUEST_PROP_FI_OBJECT, properties, null));
                                }
                            }
                            break;
                    }
                } catch (Throwable t) {
                    log.error(t.getMessage(), t);
                }
            }
        }
    }

    @Override
    public PaginatedListWrapper<NodeMetaModel> loadFiDocumentRequestObjects(String acceptLanguage, String fiRegistryNodeId) {
        return nodeProxySession.getNodeChildren(acceptLanguage, fiRegistryNodeId, 0, Integer.MAX_VALUE, null, null, null, new IncludeParam(Arrays.asList("properties")), "/Fi Objects", null, null);
    }

    private Map<String, String> getFiRegistryIdNameMap(List<NodeMetaModel> fiDocumentRequestNodeList) {
        Map<String, String> fiIdNameMap = new HashMap<>();
        if (fiDocumentRequestNodeList != null && !fiDocumentRequestNodeList.isEmpty()) {
            for (NodeMetaModel nodeMetaModel : fiDocumentRequestNodeList) {
                String fiRegistryNodeId = FirstUtil.getValue(nodeMetaModel.getProperties().get(EcmConstants.FI_DOCUMENT_REQUEST_PROP_FI_ID), String.class);
                if (fiIdNameMap.get(fiRegistryNodeId) == null) {
                    NodeMetaModel fiNodeMetaModel = nodeProxySession.getNodeById(fiRegistryNodeId, new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)).toString(), null);
                    String fiRegistryName = FirstUtil.getValue(fiNodeMetaModel.getProperties().get(EcmConstants.REGISTRY_PROP_NAME), String.class);
                    fiIdNameMap.put(fiRegistryNodeId, fiRegistryName);
                }
            }
        }
        return fiIdNameMap;
    }

    private String getSafeStringValue(String value) {
        String result = "";
        if (value != null) {
            result = value.trim();
        }
        return result;
    }
}
