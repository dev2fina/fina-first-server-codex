package net.fina.first.ecm.share;

import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.server.util.ConfigurationUtil;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.ecm.alfresco.AlfrescoClient;
import net.fina.ecm.alfresco.api.common.parameters.IncludeParam;
import net.fina.ecm.alfresco.api.common.representation.ResultPaging;
import net.fina.ecm.alfresco.api.core.model.body.NodeBodyUpdate;
import net.fina.ecm.alfresco.api.core.model.body.SharedLinkBodyCreate;
import net.fina.ecm.alfresco.api.core.model.representation.SharedLinkRepresentation;
import net.fina.first.FirstUtil;
import net.fina.first.common.exception.NodeException;
import net.fina.first.ecm.client.proxy.EcmClientProxySession;
import net.fina.first.ecm.node.model.NodeMetaModel;
import net.fina.first.ecm.node.proxy.NodeProxySession;
import net.fina.first.interceptors.FirstRecordingAuditor;
import org.jboss.ejb3.annotation.SecurityDomain;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
@RolesAllowed({PermissionIdNames.FIRST_REVIEW})
@SecurityDomain("FinASecurityDomain")
@Interceptors(FirstRecordingAuditor.class)
public class ShareLinksProxySession {

    @Inject
    private EcmClientProxySession ecmClientProxySession;
    @Inject
    private NodeProxySession nodeProxySession;


    public PaginatedListWrapper<SharedLinkRepresentation> getSharedFiles(Integer page, Integer start, Integer limit, String orderBy) {
        ResultPaging<SharedLinkRepresentation> resultPaging;

        if (orderBy != null && !orderBy.isEmpty()) {
            resultPaging = ecmClientProxySession.getAlfrescoClient().getSharedLinksAPI()
                    .listSharedLinks(0, Integer.MAX_VALUE, null, new IncludeParam(Arrays.asList("path", "allowableOperations", "permissions")), null);
            List<SharedLinkRepresentation> sortedLinks = getOrderedList(resultPaging.getObjects(), orderBy);
            resultPaging.setObjects(FirstUtil.getPaginatedList(sortedLinks, start, limit));
        } else {
            resultPaging = ecmClientProxySession.getAlfrescoClient().getSharedLinksAPI()
                    .listSharedLinks(start, limit, null, new IncludeParam(Arrays.asList("path", "allowableOperations", "permissions")), null);
        }

        PaginatedListWrapper<SharedLinkRepresentation> result = new PaginatedListWrapper<>();
        result.setList(resultPaging.getObjects());
        result.setPageSize(limit);
        result.setCurrentPage(page);
        result.setTotalResults(resultPaging.getPagination().getTotalItems());

        return result;
    }

    private List<SharedLinkRepresentation> getOrderedList(List<SharedLinkRepresentation> list, String orderBy) {
        if (list == null || list.isEmpty()) {
            return list;
        }

        List<SharedLinkRepresentation> result = list;
        if (orderBy != null && !orderBy.isEmpty()) {
            String[] orderByParts = orderBy.split(" ");
            String orderByField = orderByParts[0];
            String orderByDirection = orderByParts.length > 1 ? orderByParts[1] : "ASC";
            boolean isAscending = orderByDirection.toUpperCase().equals("ASC");
            result = list.stream().sorted((nr1, nr2) -> {
                int cmp = compareFields(nr1, nr2, orderByField);
                return isAscending ? cmp : -cmp;
            }).collect(Collectors.toList());
        }

        return result;
    }

    private int compareFields(SharedLinkRepresentation n1, SharedLinkRepresentation n2, String orderByField) {
        int res = 0;
        switch (orderByField) {
            case "name":
                res = FirstUtil.safeAlphabeticStringCmp(n1.getName(), n2.getName());
                break;
            case "size":
                long size1 = n1.getContent() == null ? 0 : n1.getContent().getSizeInBytes();
                long size2 = n2.getContent() == null ? 0 : n2.getContent().getSizeInBytes();
                res = size1 > size2 ? 1 : -1;
                break;
            case "modifiedAt":
                Date d1 = n1.getModifiedAt();
                Date d2 = n2.getModifiedAt();
                res = FirstUtil.safeDateCmp(d1, d2);
                break;
            case "location":
                String location1 = getNodeLocation(n1);
                String location2 = getNodeLocation(n2);
                res = FirstUtil.safeAlphabeticStringCmp(location1, location2);
                break;
            case "sharedByUser":
                String sharedBy1 = n1.getSharedByUser() == null ? "" : n1.getSharedByUser().getDisplayName();
                String sharedBy2 = n2.getSharedByUser() == null ? "" : n2.getSharedByUser().getDisplayName();
                res = FirstUtil.safeAlphabeticStringCmp(sharedBy1, sharedBy2);
                break;
            default:
                break;
        }

        return res;
    }

    private String getNodeLocation(SharedLinkRepresentation nr) {
        String pathName = nr.getPath().getName();
        String result = null;
        if (pathName != null) {
            if (pathName.lastIndexOf("/") == 0) {
                result = "Personal Files";
            } else {
                result = pathName.substring(pathName.lastIndexOf("/") + 1);
            }
        }

        return result;
    }

    public SharedLinkRepresentation shareFile(SharedLinkBodyCreate sharedLinkBodyCreate) {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient();
        return ecmClientProxySession.getAlfrescoClient().getSharedLinksAPI().createSharedLink(sharedLinkBodyCreate,
                new IncludeParam(Arrays.asList("path", "allowableOperations", "permissions")), null);
    }

    public NodeMetaModel updateShareFile(String modeId, NodeBodyUpdate nodeBodyUpdate) throws NodeException {
        return nodeProxySession.updateNode(modeId, nodeBodyUpdate);
    }

    public void removeFromSharedFiles(String nodeId) {
        ecmClientProxySession.getAlfrescoClientByUser(ConfigurationUtil.get().get("ECM.adminUsername")).getSharedLinksAPI().deleteSharedLink(nodeId);
    }
}
