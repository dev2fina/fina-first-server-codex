package net.fina.first.ecm.node.proxy;

import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.ecm.alfresco.api.common.parameters.FieldsParam;
import net.fina.ecm.alfresco.api.common.parameters.IncludeParam;
import net.fina.ecm.alfresco.api.common.parameters.OrderByParam;
import net.fina.ecm.alfresco.api.core.model.body.NodeBodyCopy;
import net.fina.ecm.alfresco.api.core.model.body.NodeBodyCreate;
import net.fina.ecm.alfresco.api.core.model.body.NodeBodyUpdate;
import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import net.fina.first.common.exception.NodeException;
import net.fina.first.ecm.fi.model.FiRegistryFilterModel;
import net.fina.first.ecm.node.api.NodeLocal;
import net.fina.first.ecm.node.model.ExportTemplate;
import net.fina.first.ecm.node.model.NodeMetaModel;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

@Stateless
@RolesAllowed({PermissionIdNames.FIRST_REVIEW})
@SecurityDomain("FinASecurityDomain")
public class NodeProxySession {

    @Inject
    private NodeLocal nodeLocal;

    public PaginatedListWrapper<NodeMetaModel> getNodeChildrenWithClassProperties(String acceptLanguage, String nodeId, Integer start, Integer limit, OrderByParam orderBy, String where, IncludeParam include, String relativePath, String includeSource, FieldsParam fields) {
        return nodeLocal.getNodeChildrenWithClassProperties(acceptLanguage, nodeId, start, limit, orderBy, where, include, relativePath, includeSource, fields);
    }

    public PaginatedListWrapper<NodeMetaModel> getNodeChildren(String acceptLanguage, String nodeId, Integer start, Integer limit, String filter, OrderByParam orderBy, String where, IncludeParam include, String relativePath, String includeSource, FieldsParam fields) {
        return nodeLocal.getNodeChildren(acceptLanguage, nodeId, start, limit, filter, orderBy, where, include, relativePath, includeSource, fields);
    }

    public NodeMetaModel createChildNode(String parentNodeId, NodeBodyCreate nodeBodyCreate) throws NodeException {
        return nodeLocal.createChildNode(parentNodeId, nodeBodyCreate);
    }

    public NodeMetaModel updateNode(String nodeId, NodeBodyUpdate nodeBodyUpdate) throws NodeException {
        return nodeLocal.updateNode(nodeId, nodeBodyUpdate);
    }

    public NodeRepresentation createUploadNode(String nodeId, MultipartFormDataOutput output, boolean autoRename, IncludeParam include, FieldsParam fields) throws NodeException {
        return nodeLocal.createUploadNode(nodeId, output, autoRename, include, fields, null);
    }

    public void deleteNodeById(String nodeId) throws NodeException {
        nodeLocal.deleteNodeById(nodeId);
    }

    public NodeRepresentation getNodeById(String nodeId, String include, String relativePath, List<String> fields) {
        return nodeLocal.getNodeById(nodeId, include, relativePath, fields);
    }

    public NodeMetaModel getFullNodeById(String acceptLanguage, String nodeId, String relativePath) {
        return nodeLocal.getFullNodeById(acceptLanguage, nodeId, relativePath);
    }

    public PaginatedListWrapper<NodeMetaModel> loadDeletedNodes(Integer start, Integer limit, String orderBy) {
        return nodeLocal.loadDeletedNodes(start, limit, orderBy);
    }

    public void purgeDeletedNodeCall(String nodeId) {
        nodeLocal.purgeDeletedNodeCall(nodeId);
    }

    public NodeRepresentation restoreDeletedNode(String nodeId) {
        return nodeLocal.restoreDeletedNode(nodeId);
    }

    public byte[] getExportNodeHierarchyContent(String acceptLanguage, ExportTemplate exportTemplate, String rootFolderId, String childNodeType, FiRegistryFilterModel filter) throws Throwable {
        return nodeLocal.getExportNodeHierarchyContent(acceptLanguage, exportTemplate, rootFolderId, childNodeType, filter);
    }

    public NodeMetaModel copyNode(String acceptLanguage, String nodeId, NodeBodyCopy nodeBodyCopy) {
        return nodeLocal.copyNode(acceptLanguage, nodeId, nodeBodyCopy);
    }

    public NodeMetaModel moveNode(String acceptLanguage, String nodeId, NodeBodyCopy nodeBodyMove) {
        return nodeLocal.moveNode(acceptLanguage, nodeId, nodeBodyMove);
    }

    public NodeMetaModel createNodeFromPropertiesChecked(String nodeType, TreeMap<String, Object> properties, String relativePath) throws NodeException {
        return nodeLocal.createNodeFromPropertiesChecked(nodeType, properties, relativePath);
    }

    public NodeMetaModel updateNodeFromProperties(String nodeId, TreeMap<String, Object> properties, String relativePath) throws NodeException {
        return nodeLocal.updateNodeFromProperties(nodeId, properties, relativePath);
    }

    public NodeMetaModel submitProcessActionExternal(String actionId) throws NodeException {
        return nodeLocal.submitProcessActionExternal(actionId);
    }

    public void deleteNodeChecked(String nodeId, String relativePath) throws NodeException {
        nodeLocal.deleteNodeChecked(nodeId, relativePath);
    }

    public Response getNodeContent(String nodeId, Boolean attachment) throws UnsupportedEncodingException {
        return nodeLocal.getNodeContent(nodeId, attachment);
    }

    public void deleteNodes(String nodeIds) throws NodeException {
        if(nodeIds != null) {
            nodeLocal.deleteNodes(Arrays.asList(nodeIds.split(",")));
        }
    }
}
