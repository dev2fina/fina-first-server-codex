package net.fina.first.ecm.favorites;

import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.ecm.alfresco.AlfrescoClient;
import net.fina.ecm.alfresco.api.common.parameters.IncludeParam;
import net.fina.ecm.alfresco.api.common.representation.ResultPaging;
import net.fina.ecm.alfresco.api.core.model.body.FavoriteBodyCreate;
import net.fina.ecm.alfresco.api.core.model.representation.FavoriteRepresentation;
import net.fina.first.EcmSitesUtil;
import net.fina.first.FirstUtil;
import net.fina.first.ecm.client.proxy.EcmClientProxySession;
import net.fina.first.ecm.node.model.NodeMetaModel;
import net.fina.first.ecm.node.model.NodeModelHelper;
import net.fina.first.interceptors.FirstRecordingAuditor;
import org.jboss.ejb3.annotation.SecurityDomain;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Stateless
@RolesAllowed({PermissionIdNames.FIRST_REVIEW})
@SecurityDomain("FinASecurityDomain")
@Interceptors(FirstRecordingAuditor.class)
public class FavoritesProxySession {

    @Inject
    private EcmClientProxySession ecmClientProxySession;

    public FavoriteRepresentation addToFavorites(String personId, FavoriteBodyCreate favoriteBodyCreate) {
        return ecmClientProxySession.getAlfrescoClient().getFavoritesAPI().createFavoriteCall(personId, favoriteBodyCreate, null);
    }

    public void removeFavorite(String personId, String nodeId) {
        ecmClientProxySession.getAlfrescoClient().getFavoritesAPI().deleteFavoriteCall(personId, nodeId);
    }

    public PaginatedListWrapper<NodeMetaModel> loadFavoritesNodesAndFolders(Integer start, Integer limit, String includeSource, String orderBy) {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient();
        ResultPaging<FavoriteRepresentation> resultNodes;
        if (orderBy == null || orderBy.isEmpty()) {
            resultNodes = client.getFavoritesAPI().listFavoritesCall("-me-", start, limit, "(EXISTS(target/file) OR EXISTS(target/folder))", null);
        } else {
            resultNodes = client.getFavoritesAPI().listFavoritesCall("-me-", null, Integer.MAX_VALUE, "(EXISTS(target/file) OR EXISTS(target/folder))", null);
        }

        List<NodeMetaModel> nodeMetaModels = new ArrayList<>();

        if (resultNodes.getObjects() != null && resultNodes.getObjects().size() > 0) {
            resultNodes.getObjects().forEach(node -> {
                nodeMetaModels.add(NodeModelHelper.getMetaModel(client.getNodesAPI().getNodeCall(node.getTargetGuid(), new IncludeParam(Arrays.asList("properties", "permissions", "path")).toString(), null, null)));
            });
        }

        List<NodeMetaModel> paginatedModelsList;
        if (orderBy != null && !orderBy.isEmpty()) {
            List<NodeMetaModel> orderedList = EcmSitesUtil.getOrderedList(nodeMetaModels, orderBy);
            paginatedModelsList = FirstUtil.getPaginatedList(orderedList, start, limit);
        } else {
            paginatedModelsList = nodeMetaModels;
        }

        PaginatedListWrapper<NodeMetaModel> result = new PaginatedListWrapper<>();
        result.setList(paginatedModelsList);
        result.setTotalResults(resultNodes.getCount());

        return result;
    }

    public PaginatedListWrapper<NodeMetaModel> loadFavoriteSites(int start, int limit, String orderBy) {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient();
        ResultPaging<FavoriteRepresentation> resultPaging = client.getFavoritesAPI().listFavoritesCall("-me-", null, null, "(EXISTS(target/site))", null);

        List<NodeMetaModel> siteNodeRepresentation = new ArrayList<>();

        if (resultPaging.getObjects() != null && resultPaging.getObjects().size() > 0) {
            resultPaging.getObjects().forEach(node -> {
                Map<Object, Object> favouriteKeyValueMap = (Map<Object, Object>) ((Map<Object, Object>) node.getTarget()).get("site");
                NodeMetaModel nodeMetaModel = NodeModelHelper.getMetaModel(client.getNodesAPI().getNodeCall((String) favouriteKeyValueMap.get("guid"), null, "documentLibrary", null));
                nodeMetaModel.setSite(favouriteKeyValueMap);

//                favouriteKeyValueMap.put("node", client.getNodesAPI().getNodeCall((String) favouriteKeyValueMap.get("guid"), null, "documentLibrary", null));
                siteNodeRepresentation.add(nodeMetaModel);
            });
        }

        List<NodeMetaModel> orderedPaginatedSiteNodeModels = FirstUtil.getPaginatedList(EcmSitesUtil.getOrderedList(siteNodeRepresentation, orderBy), start, limit);

        PaginatedListWrapper<NodeMetaModel> result = new PaginatedListWrapper<>();
        result.setList(orderedPaginatedSiteNodeModels);
        result.setTotalResults(resultPaging.getCount());

        return result;
    }


}
