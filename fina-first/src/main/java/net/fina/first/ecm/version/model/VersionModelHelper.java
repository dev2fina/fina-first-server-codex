package net.fina.first.ecm.version.model;

import net.fina.ecm.alfresco.api.core.model.representation.VersionRepresentation;

import java.util.ArrayList;
import java.util.List;

public class VersionModelHelper {

    public static List<VersionMetaModel> getMetaModels(List<VersionRepresentation> representations) {
        List<VersionMetaModel> result = new ArrayList<>();
        if (representations != null && !representations.isEmpty()) {
            for (VersionRepresentation representation : representations) {
                result.add(getMetaModel(representation));
            }
        }
        return result;
    }

    public static VersionMetaModel getMetaModel(VersionRepresentation representation) {
        VersionMetaModel metaModel = new VersionMetaModel();
        metaModel.setId(representation.getId());
        metaModel.setVersionComment(representation.getVersionComment());
        metaModel.setName(representation.getName());
        metaModel.setNodeType(representation.getNodeType());
        metaModel.setFolder(representation.getIsFolder());
        metaModel.setFile(representation.getIsFile());
        metaModel.setModifiedAt(representation.getModifiedAt());
        metaModel.setModifiedByUser(representation.getModifiedByUser());
        metaModel.setContent(representation.getContent());
        metaModel.setAspectNames(representation.getAspectNames());
        metaModel.setProperties(representation.getProperties());
        return metaModel;
    }

}
