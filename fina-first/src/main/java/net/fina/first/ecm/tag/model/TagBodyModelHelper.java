package net.fina.first.ecm.tag.model;

import net.fina.ecm.alfresco.api.core.model.body.TagBody;

import java.util.ArrayList;
import java.util.List;

public class TagBodyModelHelper {

    public static TagBody[] getTagBodies(List<TagBodyMetaModel> tagBodyMetaModels) {
        List<TagBody> tagBodies = new ArrayList<>();
        if (tagBodyMetaModels != null && !tagBodyMetaModels.isEmpty()) {
            for (TagBodyMetaModel metaModel : tagBodyMetaModels) {
                tagBodies.add(getTagBody(metaModel));
            }
        }

        TagBody[] result = new TagBody[tagBodies.size()];
        return tagBodies.toArray(result);
    }

    public static List<TagBodyMetaModel> getTagBodiesMetaModel(List<TagMetaModel> tagMetaModels) {
        List<TagBodyMetaModel> tagBodies = new ArrayList<>();
        if (tagMetaModels != null && !tagMetaModels.isEmpty()) {
            for (TagMetaModel tagMetaModel : tagMetaModels) {
                tagBodies.add(getTagBodyMetaModel(tagMetaModel));
            }
        }
        return tagBodies;
    }

    public static TagBody getTagBody(TagBodyMetaModel metaModel) {
        return new TagBody(metaModel.getTag());
    }

    public static TagBodyMetaModel getTagBodyMetaModel(TagMetaModel tagMetaModel) {
        TagBodyMetaModel metaModel = new TagBodyMetaModel();
        metaModel.setTag(tagMetaModel.getTag());
        return metaModel;
    }

}
