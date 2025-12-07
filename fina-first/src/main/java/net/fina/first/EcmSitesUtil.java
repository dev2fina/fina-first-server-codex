package net.fina.first;

import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import net.fina.ecm.alfresco.api.core.model.representation.SiteRepresentation;
import net.fina.first.ecm.node.model.NodeMetaModel;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class EcmSitesUtil {
    public static List<NodeMetaModel> getOrderedList(List<NodeMetaModel> allNodes, String orderBy) {
        List<NodeMetaModel> result;

        if (orderBy != null && !orderBy.isEmpty()) {
            String[] orderByParts = orderBy.split(" ");
            String orderByField = orderByParts[0];
            String orderByDirection = orderByParts.length > 1 ? orderByParts[1] : "ASC";

            boolean isAscending = orderByDirection.toUpperCase().equals("ASC");
            result = allNodes.stream().sorted((ar1, ar2) -> {
                int cmp = compareFields(ar1, ar2, orderByField);
                return isAscending ? cmp : -cmp;
            }).collect(Collectors.toList());
        } else {
            result = allNodes;
        }

        return result;
    }

    public static int compareFields(NodeMetaModel n1, NodeMetaModel n2, String fieldName) {
        int res = 0;
        switch (fieldName) {
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
            case "modifiedByUserDescription":
                String modifiedBy1 = n1.getModifiedBy() == null ? "" : n1.getModifiedBy().getDisplayName();
                String modifiedBy2 = n2.getModifiedBy() == null ? "" : n2.getModifiedBy().getDisplayName();
                res = FirstUtil.safeAlphabeticStringCmp(modifiedBy1, modifiedBy2);
                break;
            case "role":
                String role1 = n1.getSite() == null ? null : ((SiteRepresentation) n1.getSite()).getRole();
                String role2 = n2.getSite() == null ? null : ((SiteRepresentation) n2.getSite()).getRole();
                res = FirstUtil.safeAlphabeticStringCmp(role1, role2);
                break;
            case "visibility":
                String visibility1 = n1.getSite() == null ? null : ((SiteRepresentation) n1.getSite()).getVisibility();
                String visibility2 = n2.getSite() == null ? null : ((SiteRepresentation) n2.getSite()).getVisibility();
                res = FirstUtil.safeAlphabeticStringCmp(visibility1, visibility2);
                break;
            case "location":
                String location1 = getNodeLocationText(n1);
                String location2 = getNodeLocationText(n2);
                res = FirstUtil.safeAlphabeticStringCmp(location1, location2);
            default:
                break;
        }

        return res;
    }

    private static String getNodeLocationText(NodeMetaModel nodeMetaModel) {
        String pathName = nodeMetaModel.getPath().getName();
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
}
