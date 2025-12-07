package net.fina.first.ecm.node.impl;

import net.fina.common.shared.PaginatedListWrapper;
import net.fina.common.shared.SortField;
import net.fina.ecm.alfresco.AlfrescoClient;
import net.fina.ecm.alfresco.api.common.constant.APIConstants;
import net.fina.ecm.alfresco.api.common.constant.ContentModel;
import net.fina.ecm.alfresco.api.common.parameters.FieldsParam;
import net.fina.ecm.alfresco.api.common.parameters.IncludeParam;
import net.fina.ecm.alfresco.api.common.parameters.OrderByParam;
import net.fina.ecm.alfresco.api.common.representation.ResultPaging;
import net.fina.ecm.alfresco.api.common.representation.UnexpectedErrorRepresentation;
import net.fina.ecm.alfresco.api.core.model.body.NodeBodyCopy;
import net.fina.ecm.alfresco.api.core.model.body.NodeBodyCreate;
import net.fina.ecm.alfresco.api.core.model.body.NodeBodyUpdate;
import net.fina.ecm.alfresco.api.core.model.representation.ContentInfoRepresentation;
import net.fina.ecm.alfresco.api.core.model.representation.DeletedNodeRepresentation;
import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import net.fina.ecm.alfresco.api.core.model.representation.UserInfoRepresentation;
import net.fina.ecm.alfresco.api.search.body.QueryBody;
import net.fina.ecm.alfresco.api.search.body.RequestSortDefinition;
import net.fina.ecm.alfresco.api.search.model.ResultNodeRepresentation;
import net.fina.ecm.util.AlfrescoConfiguration;
import net.fina.ecm.util.AlfrescoPropConstants;
import net.fina.first.FirstUtil;
import net.fina.first.common.exception.NodeException;
import net.fina.first.ecm.EcmConstants;
import net.fina.first.ecm.client.proxy.EcmClientProxySession;
import net.fina.first.ecm.dictionary.api.DictionaryLocal;
import net.fina.first.ecm.fi.api.FiLocalEcm;
import net.fina.first.ecm.fi.model.FiRegistryFilterModel;
import net.fina.first.ecm.node.api.NodeExporter;
import net.fina.first.ecm.node.api.NodeLocal;
import net.fina.first.ecm.node.model.ExportTemplate;
import net.fina.first.ecm.node.model.NodeMetaModel;
import net.fina.first.ecm.node.model.NodeModelHelper;
import net.fina.first.ecm.search.api.SearchLocal;
import net.fina.first.interceptors.FirstRecordingAuditor;
import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;
import org.jboss.resteasy.plugins.providers.multipart.OutputPart;

import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;


@Stateless
@Local(NodeLocal.class)
@Interceptors(FirstRecordingAuditor.class)
public class NodeSession implements NodeLocal {

    private final String MODIFIED_BY_USER_DESCRIPTION_FIELD_NAME = "modifiedByUserDescription";
    private final String DOCUMENT_DESCRIPTION_FIELD_NAME = "description";
    private final String DOCUMENT_CONTENT_SIZE_FIELD_NAME = "size";
    private final String DOCUMENT_NAME_FIELD_NAME = "name";
    private final String DOCUMENT_LOCATION_FIELD_NAME = "location";


    private final Logger log = Logger.getLogger(getClass().getName());

    @Inject
    private EcmClientProxySession ecmClientProxySession;

    @Inject
    private DictionaryLocal dictionaryProxySession;

    @Inject
    private SearchLocal searchProxySession;

    @Inject
    private FiLocalEcm fiProxySession;

    @Override
    public PaginatedListWrapper<NodeMetaModel> getNodeChildrenWithClassProperties(String acceptLanguage, String nodeId, Integer start, Integer limit, OrderByParam orderBy, String where, IncludeParam include, String relativePath, String includeSource, FieldsParam fields) {
        PaginatedListWrapper<NodeMetaModel> listWrapper = getNodeChildren(acceptLanguage, nodeId, start, limit, null, orderBy, where, include, relativePath, includeSource, fields);
        for (NodeMetaModel nodeMetaModel : listWrapper.getList()) {
            nodeMetaModel.setClassProperties(dictionaryProxySession.getClassProperties(acceptLanguage, nodeMetaModel.getNodeType().replace(":", "_")));
        }

        return listWrapper;
    }

    @Override
    public PaginatedListWrapper<NodeMetaModel> getNodeChildren(String acceptLanguage, String nodeId, Integer start, Integer limit, String filter, OrderByParam orderBy, String where, IncludeParam include, String relativePath, String includeSource, FieldsParam fields) {
        PaginatedListWrapper<NodeMetaModel> listWrapper = new PaginatedListWrapper<>();

        if (filter != null && !filter.isEmpty()) {
            NodeRepresentation node = getNodeById(nodeId, include.toString(), relativePath, (fields != null) ? fields.getValues() : null);
            String childNodeType = node.getProperties().get("fina:folderConfigChildType").toString();
            PaginatedListWrapper<ResultNodeRepresentation> filteredNodes = searchProxySession.getFilteredNodes(node.getId(), childNodeType, filter);
            List<NodeMetaModel> filteredMetaModels = NodeModelHelper.getMetaModels(filteredNodes.getList());
            listWrapper.setList(filteredMetaModels);
            listWrapper.setTotalResults(filteredNodes.getTotalResults());
        } else {
            ResultPaging<NodeRepresentation> resultNodes;
            SortField orderByField = getSortField(orderBy);
            if (orderByField != null && fieldNeedsCustomSorting(orderByField.getProperty())) {
                resultNodes = getSortedNodeChildren(acceptLanguage, nodeId, start, limit, orderByField, where, include, relativePath, includeSource, fields);
            } else {
                resultNodes = ecmClientProxySession.getAlfrescoClient(acceptLanguage).getNodesAPI().listNodeChildrenCall(nodeId, start, limit, orderBy, where, include, relativePath, includeSource, fields);
            }

            List<NodeMetaModel> nodeMetaModels = NodeModelHelper.getMetaModels(resultNodes.getObjects());
            listWrapper.setList(nodeMetaModels);
            listWrapper.setTotalResults(resultNodes.getPagination().getTotalItems());
        }

        return listWrapper;
    }

    private boolean fieldNeedsCustomSorting(String orderBy) {
        if (orderBy == null || orderBy.isEmpty()) {
            return false;
        }
        return MODIFIED_BY_USER_DESCRIPTION_FIELD_NAME.equals(orderBy) || DOCUMENT_CONTENT_SIZE_FIELD_NAME.equals(orderBy) || DOCUMENT_DESCRIPTION_FIELD_NAME.equals(orderBy);
    }

    private ResultPaging<NodeRepresentation> getSortedNodeChildren(String acceptLanguage, String nodeId, Integer start, Integer limit, SortField orderByField, String where, IncludeParam include, String relativePath, String includeSource, FieldsParam fields) {
        ResultPaging<NodeRepresentation> allNodes = ecmClientProxySession.getAlfrescoClient(acceptLanguage).getNodesAPI().listNodeChildrenCall(nodeId, null, null, null, where, include, relativePath, includeSource, fields);
        List<NodeRepresentation> nodeList = allNodes.getObjects();
        ResultPaging<NodeRepresentation> result = new ResultPaging<>();

        if (nodeList != null) {
            List<NodeRepresentation> sortedList;
            if (orderByField != null) {
                boolean isAscending = orderByField.getDirection().equals("ASC");
                sortedList = nodeList.stream().sorted((nr1, nr2) -> {
                    int cmpResult = customComparator(nr1, nr2, orderByField.getProperty());
                    return isAscending ? cmpResult : -cmpResult;
                }).collect(Collectors.toList());
            } else {
                sortedList = nodeList;
            }

            result.setPagination(allNodes.getPagination());
            result.setObjects(FirstUtil.getPaginatedList(sortedList, start, limit));
        }

        return result;
    }

    private int customComparator(NodeRepresentation nr1, NodeRepresentation nr2, String property) {
        int res = 0;
        switch (property) {
            case MODIFIED_BY_USER_DESCRIPTION_FIELD_NAME:
                UserInfoRepresentation modifiedByUser1 = nr1.getModifiedByUser();
                String userDisplayName1 = modifiedByUser1 != null ? modifiedByUser1.getDisplayName() : null;
                UserInfoRepresentation modifiedByUser2 = nr2.getModifiedByUser();
                String userDisplayName2 = modifiedByUser2 != null ? modifiedByUser2.getDisplayName() : null;

                res = FirstUtil.safeAlphabeticStringCmp(userDisplayName1, userDisplayName2);
                break;
            case DOCUMENT_NAME_FIELD_NAME:
            case DOCUMENT_DESCRIPTION_FIELD_NAME:
                res = FirstUtil.safeAlphabeticStringCmp(nr1.getName(), nr2.getName());
                break;
            case DOCUMENT_CONTENT_SIZE_FIELD_NAME:
                ContentInfoRepresentation content1 = nr1.getContent();
                long size1 = content1 == null ? 0 : content1.getSizeInBytes();
                ContentInfoRepresentation content2 = nr2.getContent();
                long size2 = content2 == null ? 0 : content2.getSizeInBytes();
                res = Long.compare(size1, size2);
                break;
            case DOCUMENT_LOCATION_FIELD_NAME:
                String location1 = FirstUtil.getNodeLocationText(nr1);
                String location2 = FirstUtil.getNodeLocationText(nr2);
                res = FirstUtil.safeAlphabeticStringCmp(location1, location2);
                break;
            case "modifiedAt":
                Date modifiedAt1 = nr1.getModifiedAt();
                Date modifiedAt2 = nr2.getModifiedAt();
                res = FirstUtil.safeDateCmp(modifiedAt1, modifiedAt2);
                break;
            default:
                break;
        }
        return res;
    }

    private SortField getSortField(OrderByParam orderBy) {
        if (orderBy == null || orderBy.getValues() == null || orderBy.getValues().isEmpty()) {
            return null;
        }
        String orderByValue = orderBy.getValues().get(0);
        if (orderByValue == null || orderByValue.isEmpty()) {
            return null;
        }

        String[] orderByParamParts = orderByValue.split(" ");
        String fieldName = orderByParamParts[0];
        String direction = "ASC";
        if (orderByParamParts.length > 1) {
            direction = orderByParamParts[1];
        }

        return new SortField(fieldName, direction);
    }

    @Override
    public NodeMetaModel createChildNode(String parentNodeId, NodeBodyCreate nodeBodyCreate) throws NodeException {
        String nodeName = nodeBodyCreate.getName();
        nodeBodyCreate.setName(nodeName != null && !nodeName.trim().isEmpty() ? nodeName : UUID.randomUUID().toString());
        return NodeModelHelper.getMetaModel(createNode(parentNodeId, nodeBodyCreate, true, null, null, null));
    }

    @Override
    public NodeMetaModel updateNode(String nodeId, NodeBodyUpdate nodeBodyUpdate) throws NodeException {
        return NodeModelHelper.getMetaModel(updateNode(nodeId, nodeBodyUpdate, null));
    }

    @Override
    public void deleteNodeById(String nodeId) throws NodeException {
        Response response = ecmClientProxySession.getAlfrescoClient().getNodesAPI().deleteNodeCall(nodeId);
        extractNodeException(response);
    }

    @Override
    public void deleteNode(String nodeId, Boolean permanen) throws NodeException {
        Response response = ecmClientProxySession.getAlfrescoClient().getNodesAPI().deleteNodeCall(nodeId, permanen);
        extractNodeException(response);
    }

    @Override
    public void deleteNodeHierarchy(String acceptLanguage, String nodeId, Boolean permanent) throws NodeException {
        List<NodeMetaModel> nodes = getNodeChildren(acceptLanguage, nodeId, null, null, null, null, null, null, null, null, null).getList();
        if (nodes != null && nodes.size() > 0) {
            for (NodeMetaModel node : nodes) {
                deleteNodeHierarchy(acceptLanguage, node.getId(), permanent);
            }
        }
        deleteNode(nodeId, true);
    }

    @Override
    public NodeRepresentation getNodeById(String nodeId, String include, String relativePath, List<String> fields) {
        return ecmClientProxySession.getAlfrescoClient().getNodesAPI().getNodeCall(nodeId, include, relativePath, fields);
    }

    @Override
    public NodeMetaModel getNodeById(String nodeId, String include, String relativePath) {
        return NodeModelHelper.getMetaModel(ecmClientProxySession.getAlfrescoClient().getNodesAPI().getNodeCall(nodeId, include, relativePath, null));
    }

    @Override
    public NodeMetaModel getFullNodeById(String acceptLanguage, String nodeId, String relativePath) {
        NodeMetaModel nodeMetaModel = NodeModelHelper.getMetaModel(ecmClientProxySession.getAlfrescoClient(acceptLanguage).getNodesAPI().getNodeCall(nodeId, new IncludeParam(Arrays.asList("path", "properties", "permissions")).toString(), relativePath, null));
        nodeMetaModel.setClassProperties(dictionaryProxySession.getClassProperties(acceptLanguage, nodeMetaModel.getNodeType().replace(":", "_")));
        return nodeMetaModel;
    }

    @Override
    public PaginatedListWrapper<NodeMetaModel> loadDeletedNodes(Integer start, Integer limit, String orderBy) {
        ResultPaging<DeletedNodeRepresentation> deletedNodes;

        List<DeletedNodeRepresentation> orderedNodes;
        if (orderBy != null && !orderBy.isEmpty()) {
            deletedNodes = ecmClientProxySession.getAlfrescoClient().getTrashAPI().listDeletedNodesCall(null, Integer.MAX_VALUE, new IncludeParam(Arrays.asList("path", "properties", "allowableOperations", "permissions", "aspectNames")));
            orderedNodes = FirstUtil.getPaginatedList(getOrderedList(deletedNodes.getObjects(), orderBy), start, limit);
        } else {
            deletedNodes = ecmClientProxySession.getAlfrescoClient().getTrashAPI().listDeletedNodesCall(start, limit, new IncludeParam(Arrays.asList("path", "properties", "allowableOperations", "permissions", "aspectNames")));
            orderedNodes = deletedNodes.getObjects();
        }

        List<NodeMetaModel> nodeMetaModels = new ArrayList<>();

        if (orderedNodes != null) {
            orderedNodes.forEach(node -> {
                nodeMetaModels.add(NodeModelHelper.getMetaModel(node));
            });
        }

        PaginatedListWrapper<NodeMetaModel> result = new PaginatedListWrapper<>();
        result.setList(nodeMetaModels);
        result.setTotalResults(deletedNodes.getPagination().getTotalItems());
        return result;
    }

    private List<DeletedNodeRepresentation> getOrderedList(List<DeletedNodeRepresentation> list, String orderBy) {
        if (list == null || list.isEmpty()) {
            return list;
        }

        List<DeletedNodeRepresentation> result;

        String[] orderByParts = orderBy.split(" ");
        String property = orderByParts[0];
        String direction = orderByParts.length > 1 ? orderByParts[1] : "ASC";
        boolean isAscending = direction.toUpperCase().equals("ASC");
        result = list.stream().sorted((dnr1, dnr2) -> {
            int cmpRes = customComparator(dnr1, dnr2, property);
            return isAscending ? cmpRes : -cmpRes;
        }).collect(Collectors.toList());

        return result;
    }

    @Override
    public void purgeDeletedNodeCall(String nodeId) {
        ecmClientProxySession.getAlfrescoClient().getTrashAPI().purgeDeletedNodeCall(nodeId);
    }

    @Override
    public NodeRepresentation restoreDeletedNode(String nodeId) {
        return ecmClientProxySession.getAlfrescoClient().getTrashAPI().restoreDeletedNodeCall(nodeId, null);
    }

    @Override
    public byte[] getExportNodeHierarchyContent(String acceptLanguage, ExportTemplate exportTemplate, String rootFolderId, String childNodeType, FiRegistryFilterModel filter) throws Throwable {
        NodeExporter nodeExporter = new ExcelNodeExporter(ecmClientProxySession.getAlfrescoClient());
        String filterQueryStr;
        QueryBody filterQueryBody = null;
        if (filter != null) {
            filterQueryStr = fiProxySession.filterToQuery(filter);
            filterQueryBody = searchProxySession.getAFTSQuery(filterQueryStr, 0, Integer.MAX_VALUE, Collections.singletonList(new RequestSortDefinition().field("createdAt").ascending(false)), Arrays.asList(APIConstants.PROPERTIES_VALUE, ContentModel.PROP_PATH));
        }
        return nodeExporter.getExportNodeHierarchyContent(exportTemplate, rootFolderId, childNodeType, filterQueryBody);
    }

    @Override
    public NodeMetaModel copyNode(String acceptLanguage, String nodeId, NodeBodyCopy nodeBodyCopy) {
        NodeRepresentation result = ecmClientProxySession.getAlfrescoClient(acceptLanguage).getNodesAPI().copyNodeCall(nodeId, nodeBodyCopy);
        return NodeModelHelper.getMetaModel(result);
    }

    @Override
    public NodeMetaModel moveNode(String acceptLanguage, String nodeId, NodeBodyCopy nodeBodyMove) {
        NodeRepresentation result = ecmClientProxySession.getAlfrescoClient(acceptLanguage).getNodesAPI().moveNodeCall(nodeId, nodeBodyMove);
        return NodeModelHelper.getMetaModel(result);
    }

    @Override
    public NodeMetaModel createNodeFromPropertiesChecked(String nodeType, TreeMap<String, Object> properties, String relativePath) throws NodeException {
        NodeBodyCreate nodeBodyCreate = new NodeBodyCreate();
        nodeBodyCreate.setNodeType(nodeType);
        nodeBodyCreate.setProperties(properties);
        nodeBodyCreate.setRelativePath(relativePath);
        nodeBodyCreate.setName(UUID.randomUUID().toString());
        return NodeModelHelper.getMetaModel(createNode("-root-", nodeBodyCreate, null));
    }

    @Override
    public NodeMetaModel updateNodeFromProperties(String nodeId, TreeMap<String, Object> properties, String relativePath) throws NodeException {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient();
        NodeBodyUpdate nodeBodyCreate = new NodeBodyUpdate();
        nodeBodyCreate.setProperties(properties);
        NodeRepresentation updateNode = client.getNodesAPI().getNodeCall(nodeId, new IncludeParam(Collections.singletonList("path")).toString(), null, null);
        if (updateNode != null && updateNode.getPath().getName().contains(relativePath)) {
            return NodeModelHelper.getMetaModel(updateNode(nodeId, nodeBodyCreate, null));
        }

        return null;
    }

    @Override
    public NodeMetaModel submitProcessActionExternal(String actionId) throws NodeException {
        TreeMap<String, Object> properties = new TreeMap<>();
        properties.put(EcmConstants.FI_REGISTRY_ACTION_EXTERNAL_IS_SUBMIT_PROP_NAME, true);
        return updateNodeFromProperties(actionId, properties, AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.FI_REGISTRY_ROOT_FOLDER_PATH_KEY));
    }

    @Override
    public void deleteNodeChecked(String nodeId, String relativePath) throws NodeException {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient();
        NodeRepresentation updateNode = client.getNodesAPI().getNodeCall(nodeId, new IncludeParam(Collections.singletonList("path")).toString(), null, null);

        if (updateNode != null && updateNode.getPath().getName().contains(relativePath)) {
            deleteNodeById(nodeId);
        }
    }

    @Override
    public NodeRepresentation uploadFile(String nodeId, String fileName, MultipartFormDataInput multipartForm) {
        try {
            MultipartFormDataOutput mdo = new MultipartFormDataOutput();
            for (Map.Entry<String, List<InputPart>> inputPartEntry : multipartForm.getFormDataMap().entrySet()) {
                String partId = inputPartEntry.getKey();
                List<InputPart> inputParts = inputPartEntry.getValue();

                for (InputPart part : inputParts) {
                    InputStream inputStream;
                    if (partId.equals(APIConstants.MULTIPART_FILE_DATA)) {
                        inputStream = part.getBody(InputStream.class, null);
                        OutputPart objPart = mdo.addFormData(partId, inputStream, part.getMediaType());
                        mdo.addFormData("name", fileName, MediaType.TEXT_PLAIN_TYPE);

                        objPart.getHeaders().putSingle("Content-Disposition", "form-data; name=" + partId + "; filename=" + fileName);
                    } else {
                        mdo.addFormData(partId, part.getBodyAsString(), part.getMediaType());
                    }
                }
            }

            return createUploadNode(nodeId, mdo, true, null, null, null);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public NodeRepresentation createNode(String nodeId, NodeBodyCreate nodeBodyCreate, AlfrescoClient client) throws NodeException {
        AlfrescoClient alfrescoClient = client == null ? ecmClientProxySession.getAlfrescoClient() : client;
        return extractNodeRepresentation(alfrescoClient.getNodesAPI().createNodeCall(nodeId, nodeBodyCreate));
    }

    @Override
    public NodeRepresentation createNode(String nodeId, NodeBodyCreate nodeBodyCreate, boolean autoRename, IncludeParam include, FieldsParam fields, AlfrescoClient client) throws NodeException {
        AlfrescoClient alfrescoClient = client == null ? ecmClientProxySession.getAlfrescoClient() : client;
        return extractNodeRepresentation(alfrescoClient.getNodesAPI().createNodeCall(nodeId, nodeBodyCreate, autoRename, include, fields));
    }

    @Override
    public NodeRepresentation createNode(String nodeId, MultipartFormDataInput multipartForm, AlfrescoClient client) throws NodeException {
        AlfrescoClient alfrescoClient = client == null ? ecmClientProxySession.getAlfrescoClient() : client;
        return extractNodeRepresentation(alfrescoClient.getNodesAPI().createNodeCall(nodeId, multipartForm));
    }

    @Override
    public NodeRepresentation createUploadNode(String nodeId, MultipartFormDataOutput output, boolean autoRename, IncludeParam include, FieldsParam fields, AlfrescoClient client) throws NodeException {
        AlfrescoClient alfrescoClient = client == null ? ecmClientProxySession.getAlfrescoClient() : client;
        return extractNodeRepresentation(alfrescoClient.getNodesAPI().createUploadNodeCall(nodeId, output, autoRename, include, fields));
    }

    public NodeRepresentation updateNode(String nodeId, NodeBodyUpdate nodeBodyUpdate, AlfrescoClient client) throws NodeException {
        AlfrescoClient alfrescoClient = client == null ? ecmClientProxySession.getAlfrescoClient() : client;
        return extractNodeRepresentation(alfrescoClient.getNodesAPI().updateNodeCall(nodeId, nodeBodyUpdate));
    }

    @Override
    public NodeRepresentation updateNode(String nodeId, NodeBodyUpdate nodeBodyUpdate, IncludeParam include, FieldsParam fields, AlfrescoClient client) throws NodeException {
        AlfrescoClient alfrescoClient = client == null ? ecmClientProxySession.getAlfrescoClient() : client;
        return extractNodeRepresentation(alfrescoClient.getNodesAPI().updateNodeCall(nodeId, nodeBodyUpdate, include, fields));
    }

    @Override
    public Response getNodeContent(String nodeId, Boolean attachment) throws UnsupportedEncodingException {
        Response response = ecmClientProxySession.getAlfrescoClient().getNodesAPI().getNodeContent(nodeId, attachment);

        if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
            NodeRepresentation nodeRepresentation = getNodeById(nodeId, (new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE))).toString(), null, null);
            if (nodeRepresentation != null && nodeRepresentation.getProperties() != null) {
                Object displayNameObj = nodeRepresentation.getProperties().get(EcmConstants.DOCUMENT_PROP_DISPLAY_NAME);
                if (displayNameObj != null) {
                    String displayName = FirstUtil.getValue(displayNameObj, String.class);

                    if (displayName != null && !displayName.isEmpty()) {
                        displayName = URLEncoder.encode(displayName, "UTF-8").replace("+", "%20");

                        return Response
                                .status(response.getStatus())
                                .type(MediaType.APPLICATION_OCTET_STREAM)
                                .entity(response.getEntity())
                                .header("Content-Disposition", "attachment; filename=\"" + displayName + "\"")
                                .build();
                    }
                }
            }
        }

        return response;
    }

    private NodeRepresentation extractNodeRepresentation(Response response) throws NodeException {
        extractNodeException(response);
        return response.readEntity(NodeRepresentation.class);
    }

    private void extractNodeException(Response response) throws NodeException {
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            UnexpectedErrorRepresentation unexpectedErrorRepresentation = response.readEntity(UnexpectedErrorRepresentation.class);
            log.error(unexpectedErrorRepresentation);
            throw new NodeException(unexpectedErrorRepresentation);
        }
    }

    @Override
    public void deleteNodes(List<String> nodeIds) throws NodeException {
        for (String id : nodeIds) {
            deleteNodeById(id);
        }
    }
}
