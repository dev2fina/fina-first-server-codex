package net.fina.server.feedback.model;

import net.fina.server.feedback.entity.FeedbackCategory;
import net.fina.server.i18n.helper.Description;

import java.util.List;
import java.util.stream.Collectors;

public class FeedbackCategoryMetaModelHelper {

    public static FeedbackCategory toEntity(FeedbackCategoryMetaModel model, long langId) {
        FeedbackCategory entity = new FeedbackCategory();

        entity.setId(model.getId());
        entity.setName(new Description(langId, model.getNameStrId(), model.getName()));
        return entity;
    }

    public static FeedbackCategoryMetaModel toModel(FeedbackCategory entity, long langId) {
        FeedbackCategoryMetaModel model = new FeedbackCategoryMetaModel();

        model.setId(entity.getId());
        model.setName(entity.getName().getDescription(langId));
        model.setNameStrId(entity.getName().getNameStrId());
        return model;
    }

    public static List<FeedbackCategoryMetaModel> toModel(List<FeedbackCategory> entities, long langId) {
        if(entities == null) {
            return null;
        }

        return entities.stream()
                .map((v) -> toModel(v, langId))
                .collect(Collectors.toList());
    }
}
