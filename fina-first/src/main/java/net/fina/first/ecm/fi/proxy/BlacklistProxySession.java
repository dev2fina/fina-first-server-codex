package net.fina.first.ecm.fi.proxy;

import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.ecm.alfresco.api.common.constant.APIConstants;
import net.fina.ecm.alfresco.api.common.parameters.IncludeParam;
import net.fina.ecm.alfresco.api.core.model.body.NodeBodyCreate;
import net.fina.ecm.alfresco.api.core.model.body.NodeBodyUpdate;
import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import net.fina.ecm.util.AlfrescoConfiguration;
import net.fina.ecm.util.AlfrescoPropConstants;
import net.fina.first.FirstUtil;
import net.fina.first.common.exception.NodeException;
import net.fina.first.ecm.node.api.NodeLocal;
import net.fina.first.ecm.node.model.NodeMetaModel;
import net.fina.first.ecm.search.api.SearchLocal;
import org.jboss.ejb3.annotation.SecurityDomain;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.*;

@Stateless
@SecurityDomain("FinASecurityDomain")
@RolesAllowed({PermissionIdNames.FIRST_BLACKLIST_REVIEW})
public class BlacklistProxySession {

    @Inject
    private NodeLocal nodeProxySession;

    @Inject
    private SearchLocal searchLocal;

    public PaginatedListWrapper<NodeMetaModel> load(String acceptLanguage, int page, int start, int limit, String sort, String group, String query, String filter) {

        StringBuilder sb = new StringBuilder("(TYPE:'" + AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.DATA_TYPE_BLACKLIST_KEY) + "' ");
        if (query != null && !query.isEmpty()) {
            String searchTemplate = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.BLACKLIST_SEARCH_TEMPLATE);
            if (searchTemplate != null && !searchTemplate.isEmpty()) {
                sb.append(" AND (")
                        .append(searchTemplate.replace("{0}", query))
                        .append(")");
            }
        }
        sb.append(")");

        List<NodeMetaModel> resultModels = new ArrayList<>();

        PaginatedListWrapper<NodeMetaModel> result = new PaginatedListWrapper<>();
        result.setList(resultModels);
        result.setCurrentPage(page);

        if ((query == null || query.isEmpty()) && (filter == null || filter.isEmpty())) {
            NodeRepresentation blacklistNode = getBlacklistNode();
            if (blacklistNode != null) {
                result = nodeProxySession.getNodeChildren(acceptLanguage, blacklistNode.getId(), start, limit,
                        null,
                        FirstUtil.getOrderByParam(sort, group),
                        "(nodeType=" + blacklistNode.getProperties().get("fina:folderConfigChildType") + ")",
                        new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)),
                        null,
                        null,
                        null);
            }
        } else {
            result = searchLocal.searchNodes(acceptLanguage, sb.toString(), start, limit,
                    searchLocal.getSortDefinition(sort, group), searchLocal.getAFTSFilterString(filter));
        }

        return result;
    }

    @RolesAllowed(PermissionIdNames.FIRST_BLACKLIST_AMEND)
    public NodeMetaModel save(String acceptLanguage, TreeMap<String, Object> properties) throws NodeException {
        NodeRepresentation blacklistNode = getBlacklistNode();
        properties.remove("id");
        if (blacklistNode != null) {
            NodeBodyCreate nodeBodyCreate = new NodeBodyCreate(UUID.randomUUID().toString(),
                    FirstUtil.getValue(blacklistNode.getProperties().get("fina:folderConfigChildType"), String.class),
                    properties,
                    null);
            NodeMetaModel newNode = nodeProxySession.createChildNode(blacklistNode.getId(), nodeBodyCreate);
            return newNode;
        }

        return null;
    }

    @RolesAllowed(PermissionIdNames.FIRST_BLACKLIST_AMEND)
    public NodeMetaModel update(String nodeId, TreeMap<String, Object> properties, String acceptLanguage) throws NodeException {
        properties.remove("id");
        NodeBodyUpdate nodeBodyUpdate = new NodeBodyUpdate(properties);
        NodeMetaModel updatedNode = nodeProxySession.updateNode(nodeId, nodeBodyUpdate);
        return updatedNode;
    }

    @RolesAllowed(PermissionIdNames.FIRST_BLACKLIST_DELETE)
    public void delete(String nodeId) throws NodeException {
        nodeProxySession.deleteNodeById(nodeId);
    }

    private NodeRepresentation getBlacklistNode() {
        String blackListRootFolderPath = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.BLACKLIST_ROOT_FOLDER_PATH_KEY);
        return nodeProxySession.getNodeById(APIConstants.FOLDER_ROOT, null, blackListRootFolderPath, null);
    }
}
