package net.fina.first.ecm.node.api;

import net.fina.common.shared.PaginatedListWrapper;
import net.fina.ecm.alfresco.AlfrescoClient;
import net.fina.ecm.alfresco.api.common.parameters.FieldsParam;
import net.fina.ecm.alfresco.api.common.parameters.IncludeParam;
import net.fina.ecm.alfresco.api.common.parameters.OrderByParam;
import net.fina.ecm.alfresco.api.core.model.body.NodeBodyCopy;
import net.fina.ecm.alfresco.api.core.model.body.NodeBodyCreate;
import net.fina.ecm.alfresco.api.core.model.body.NodeBodyUpdate;
import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import net.fina.first.common.exception.NodeException;
import net.fina.first.ecm.fi.model.FiRegistryFilterModel;
import net.fina.first.ecm.node.model.ExportTemplate;
import net.fina.first.ecm.node.model.NodeMetaModel;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;

import jakarta.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.TreeMap;

public interface NodeLocal {
    PaginatedListWrapper<NodeMetaModel> getNodeChildrenWithClassProperties(String acceptLanguage, String nodeId, Integer start, Integer limit, OrderByParam orderBy, String where, IncludeParam include, String relativePath, String includeSource, FieldsParam fields);

    PaginatedListWrapper<NodeMetaModel> getNodeChildren(String acceptLanguage, String nodeId, Integer start, Integer limit, String filter, OrderByParam orderBy, String where, IncludeParam include, String relativePath, String includeSource, FieldsParam fields);

    NodeMetaModel createChildNode(String parentNodeId, NodeBodyCreate nodeBodyCreate) throws NodeException;

    NodeMetaModel updateNode(String nodeId, NodeBodyUpdate nodeBodyUpdate) throws NodeException;

    NodeRepresentation createNode(String nodeId, NodeBodyCreate nodeBodyCreate, AlfrescoClient client) throws NodeException;

    NodeRepresentation createNode(String nodeId, NodeBodyCreate nodeBodyCreate, boolean autoRename, IncludeParam include, FieldsParam fields, AlfrescoClient client) throws NodeException;

    NodeRepresentation createNode(String nodeId, MultipartFormDataInput multipartForm, AlfrescoClient client) throws NodeException;

    NodeRepresentation createUploadNode(String nodeId, MultipartFormDataOutput output, boolean autoRename, IncludeParam include, FieldsParam fields, AlfrescoClient client) throws NodeException;

    NodeRepresentation updateNode(String nodeId, NodeBodyUpdate nodeBodyUpdate, AlfrescoClient client) throws NodeException;

    NodeRepresentation updateNode(String nodeId, NodeBodyUpdate nodeBodyUpdate, IncludeParam include, FieldsParam fields, AlfrescoClient client) throws NodeException;

    void deleteNodeById(String nodeId) throws NodeException;

    void deleteNode(String nodeId, Boolean permanen) throws NodeException;

    void deleteNodeHierarchy(String acceptLanguage, String nodeId, Boolean permanent) throws NodeException;

    NodeRepresentation getNodeById(String nodeId, String include, String relativePath, List<String> fields);

    NodeMetaModel getNodeById(String nodeId, String include, String relativePath);

    NodeMetaModel getFullNodeById(String acceptLanguage, String nodeId, String relativePath);

    PaginatedListWrapper<NodeMetaModel> loadDeletedNodes(Integer start, Integer limit, String orderBy);

    void purgeDeletedNodeCall(String nodeId);

    NodeRepresentation restoreDeletedNode(String nodeId);

    byte[] getExportNodeHierarchyContent(String acceptLanguage, ExportTemplate exportTemplate, String rootFolderId, String childNodeType, FiRegistryFilterModel filter) throws Throwable;

    NodeMetaModel copyNode(String acceptLanguage, String nodeId, NodeBodyCopy nodeBodyCopy);

    NodeMetaModel moveNode(String acceptLanguage, String nodeId, NodeBodyCopy nodeBodyMove);

    NodeMetaModel createNodeFromPropertiesChecked(String nodeType, TreeMap<String, Object> properties, String relativePath) throws NodeException;

    NodeMetaModel updateNodeFromProperties(String nodeId, TreeMap<String, Object> properties, String relativePath) throws NodeException;

    NodeMetaModel submitProcessActionExternal(String actionId) throws NodeException;

    void deleteNodeChecked(String nodeId, String relativePath) throws NodeException;

    NodeRepresentation uploadFile(String nodeId, String fileName, MultipartFormDataInput multipartForm);

    Response getNodeContent(String nodeId, Boolean attachment) throws UnsupportedEncodingException;

    void deleteNodes(List<String> nodeIds) throws NodeException;
}
