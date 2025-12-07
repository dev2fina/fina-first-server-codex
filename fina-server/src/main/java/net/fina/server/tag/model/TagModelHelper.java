package net.fina.server.tag.model;

import net.fina.server.tag.entity.Tag;

import java.util.ArrayList;
import java.util.List;

public class TagModelHelper {

    public static TagMetaModel get(Tag tag) {
        TagMetaModel model = new TagMetaModel();
        model.setId(tag.getId());
        model.setName(tag.getName());
        return model;
    }

    public static Tag get(TagMetaModel model) {
        Tag tag = new Tag();
        tag.setId(model.getId());
        tag.setName(model.getName());
        return tag;
    }

    public static List<TagMetaModel> get(List<Tag> tags) {
        List<TagMetaModel> models = new ArrayList<>();
        for (Tag tag : tags) {
            models.add(get(tag));
        }
        return models;
    }
}
