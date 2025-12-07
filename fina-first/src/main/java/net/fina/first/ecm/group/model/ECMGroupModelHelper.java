package net.fina.first.ecm.group.model;

import net.fina.common.shared.ecm.model.ECMGroupMetaModel;
import net.fina.ecm.alfresco.api.core.model.representation.GroupRepresentation;

import java.util.ArrayList;
import java.util.List;

public class ECMGroupModelHelper {

    public static ECMGroupMetaModel getGroupMetaModel(GroupRepresentation groupRepresentation) {
        ECMGroupMetaModel result = new ECMGroupMetaModel();
        result.setId(groupRepresentation.getId());
        result.setDisplayName(groupRepresentation.getDisplayName());
        result.setParentIds(groupRepresentation.getParentIds());
        result.setRoot(groupRepresentation.getIsRoot());

        return result;
    }

    public static List<ECMGroupMetaModel> getGroupMetaModels(List<GroupRepresentation> groupRepresentationList) {
        List<ECMGroupMetaModel> result = new ArrayList<>();
        if (groupRepresentationList != null) {
            groupRepresentationList.forEach(gr -> {
                result.add(getGroupMetaModel(gr));
            });

        }
        return result;
    }
}
