package net.fina.server.feedback.model;

import net.fina.common.client.exception.FinATypeException;
import net.fina.server.feedback.entity.Feedback;
import net.fina.server.i18n.helper.Description;

import java.util.List;
import java.util.stream.Collectors;

public class FeedbackMetaModelHelper {

    public static Feedback toEntity(FeedbackMetaModel model, long langId) throws FinATypeException {
        Feedback entity = new Feedback();

        entity.setDescription(new Description(langId, model.getNameStrId(), model.getDescription()));
        entity.setFeedbackCategory(FeedbackCategoryMetaModelHelper
                .toEntity(model.getFeedbackCategory(), langId));
        if (model.getRating() > 0 && model.getRating() <= 5) {
            entity.setRating(model.getRating());
        } else {
            throw new FinATypeException("Rating not valid");
        }
        return entity;
    }

    public static FeedbackMetaModel toModel(Feedback entity, long langId) {
        FeedbackMetaModel model = new FeedbackMetaModel();

        model.setId(entity.getId());
        model.setDescription(entity.getDescription().getDescription(langId));
        model.setNameStrId(entity.getDescription().getNameStrId());
        model.setFeedbackCategory(FeedbackCategoryMetaModelHelper
                .toModel(entity.getFeedbackCategory(), langId));
        model.setRating(entity.getRating());
        return model;
    }

    public static List<FeedbackMetaModel> toModel(List<Feedback> entities, long langId) {
        if (entities == null) {
            return null;
        }

        return entities.stream()
                .map((v) -> toModel(v, langId))
                .collect(Collectors.toList());
    }
}
