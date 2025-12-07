package net.fina.first.ecm.tag.model;

import net.fina.ecm.alfresco.api.core.model.representation.TagRepresentation;

import java.util.ArrayList;
import java.util.List;

public class TagModelHelper {

    public static List<TagMetaModel> getMetaModels(List<TagRepresentation> representations) {
        List<TagMetaModel> metaModels = new ArrayList<>();
        if (representations != null && !representations.isEmpty()) {
            for (TagRepresentation representation : representations) {
                metaModels.add(getMetaModel(representation));
            }
        }
        return metaModels;
    }

    public static TagMetaModel getMetaModel(TagRepresentation representation) {
        TagMetaModel metaModel = new TagMetaModel();
        metaModel.setId(representation.getId());
        metaModel.setTag(representation.getTag());
        return metaModel;
    }

    public static List<TagRepresentation> getRepresentations(List<TagMetaModel> metaModels) {
        List<TagRepresentation> representations = new ArrayList<>();
        if (metaModels != null && !metaModels.isEmpty()) {
            for (TagMetaModel metaModel : metaModels) {
                representations.add(getRepresentation(metaModel));
            }
        }
        return representations;
    }

    public static TagRepresentation getRepresentation(TagMetaModel metaModel) {
        TagRepresentation representation = new TagRepresentation();
        representation.setId(metaModel.getId());
        representation.setTag(metaModel.getTag());
        return representation;
    }

}
