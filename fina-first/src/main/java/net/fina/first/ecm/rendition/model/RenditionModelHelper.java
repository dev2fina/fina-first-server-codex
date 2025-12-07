package net.fina.first.ecm.rendition.model;

import net.fina.ecm.alfresco.api.core.model.representation.RenditionRepresentation;

import java.util.ArrayList;
import java.util.List;

public class RenditionModelHelper {

    public static List<RenditionMetaModel> getMetaModels(List<RenditionRepresentation> representations) {
        List<RenditionMetaModel> result = new ArrayList<>();
        if (representations != null && !representations.isEmpty()) {
            for (RenditionRepresentation representation : representations) {
                result.add(getMetaModel(representation));
            }
        }
        return result;
    }

    public static RenditionMetaModel getMetaModel(RenditionRepresentation representation) {
        RenditionMetaModel metaModel = new RenditionMetaModel();

        if (representation != null) {
            metaModel.setId(representation.getId());
            metaModel.setContent(metaModel.getContent());
            metaModel.setStatus(representation.getStatus());
        }

        return metaModel;
    }

}
