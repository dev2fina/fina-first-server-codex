package net.fina.first.ecm.property;

import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.server.util.ConfigurationUtil;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.ecm.alfresco.AlfrescoClient;
import net.fina.ecm.alfresco.api.common.parameters.IncludeParam;
import net.fina.ecm.alfresco.api.core.model.body.NodeBodyCreate;
import net.fina.ecm.alfresco.api.core.model.body.NodeBodyUpdate;
import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import net.fina.ecm.alfresco.api.search.model.ResultNodeRepresentation;
import net.fina.ecm.alfresco.api.search.model.ResultSetRepresentation;
import net.fina.ecm.util.AlfrescoConfiguration;
import net.fina.first.FirstUtil;
import net.fina.first.common.exception.NodeException;
import net.fina.first.ecm.EcmConstants;
import net.fina.first.ecm.client.proxy.EcmClientProxySession;
import net.fina.first.ecm.node.api.NodeLocal;
import net.fina.first.ecm.node.model.NodeMetaModel;
import net.fina.first.ecm.search.proxy.SearchProxySession;
import net.fina.first.interceptors.FirstRecordingAuditor;
import org.jboss.ejb3.annotation.SecurityDomain;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;

import java.util.Arrays;
import java.util.Collections;
import java.util.TreeMap;

@Stateless
@RolesAllowed({PermissionIdNames.FIRST_REVIEW})
@SecurityDomain("FinASecurityDomain")
@Interceptors(FirstRecordingAuditor.class)
public class PropertyProxySession {

    @Inject
    private EcmClientProxySession ecmClientProxySession;
    @Inject
    private NodeLocal nodeProxySession;
    @Inject
    private SearchProxySession searchProxySession;

    @RolesAllowed({PermissionIdNames.FIRST_CONFIG_REVIEW})
    public PaginatedListWrapper<ResultNodeRepresentation> loadFirstConfigProperties(Integer start, Integer limit, Integer page) {
        String query = "select * from fina:configuration order by cmis:creationDate DESC";
        ResultSetRepresentation<ResultNodeRepresentation> searchResult = searchProxySession.searchByCMIS(query,page,start,limit);

        PaginatedListWrapper<ResultNodeRepresentation> result = new PaginatedListWrapper<>();
        result.setList(searchResult.getObjects());
        result.setTotalResults(searchResult.getPagination().getTotalItems());

        return result;
    }

    @RolesAllowed({PermissionIdNames.FIRST_CONFIG_AMEND})
    public NodeMetaModel createProperty(NodeRepresentation node) throws NodeException {
        NodeMetaModel result = nodeProxySession.createChildNode(node.getParentId(), new NodeBodyCreate(node.getName(), node.getNodeType(), node.getProperties(), node.getAspects()));
        AlfrescoConfiguration.get().addProperty(result.getName(), FirstUtil.getValue(result.getProperties().get(EcmConstants.PROPERTY_VALUE_NAME), String.class));

        return result;
    }

    @RolesAllowed({PermissionIdNames.FIRST_CONFIG_AMEND})
    public NodeMetaModel update(NodeRepresentation node, String nodeId) throws NodeException {
        NodeMetaModel result = nodeProxySession.updateNode(nodeId, new NodeBodyUpdate(node.getName(), null, new TreeMap(node.getProperties()), null));
        AlfrescoConfiguration.get().addProperty(result.getName(), FirstUtil.getValue(result.getProperties().get(EcmConstants.PROPERTY_VALUE_NAME), String.class));
        return result;
    }

    @RolesAllowed({PermissionIdNames.FIRST_CONFIG_DELETE})
    public void delete(String nodeId) throws NodeException {
        nodeProxySession.deleteNodeById(nodeId);
    }

    @RolesAllowed({PermissionIdNames.FIRST_CONFIG_REVIEW})
    public void restartCache() {
        AlfrescoConfiguration.get().sync();
    }


    @RolesAllowed({PermissionIdNames.FIRST_CONFIG_REVIEW})
    public NodeRepresentation loadConfigurationFolderNode() {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient();
        return client.getNodesAPI().getNodeCall("-root-", new IncludeParam(Collections.singletonList("path")).toString(), ConfigurationUtil.get().get("ECM.configurationFolderPath"), null);
    }

    public PaginatedListWrapper<String> getListConfigValue(String configName) {
        String[] propValue = AlfrescoConfiguration.get().getAlfrescoArrayProperty(configName);

        PaginatedListWrapper<String> result = new PaginatedListWrapper<>();
        result.setList(Arrays.asList(propValue));
        result.setTotalResults(propValue.length);

        return result;
    }
}
