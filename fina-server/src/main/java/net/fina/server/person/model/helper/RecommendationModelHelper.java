package net.fina.server.person.model.helper;

import net.fina.server.i18n.helper.Description;
import net.fina.server.person.entity.Person;
import net.fina.server.person.entity.Recommendation;
import net.fina.server.person.model.RecommendationMetaModel;

import java.util.ArrayList;
import java.util.List;

public class RecommendationModelHelper {
    public static RecommendationMetaModel toModel(Recommendation entity, long langId) {
        RecommendationMetaModel model = new RecommendationMetaModel();
        model.setId(entity.getId());
        model.setCooperationPlace(entity.getCooperationPlace().getDescription(langId));
        model.setCooperationPlaceStrId(entity.getCooperationPlace().getNameStrId());
        model.setRecommendationDate(entity.getRecommendationDate());
        if (entity.getRecommender() != null) {
            model.setRecommender(PersonModelHelper.toModelSimple(entity.getRecommender(), langId));
        }
        model.setIdentificationNumber(entity.getIdentificationNumber());
        model.setPassportNumber(entity.getPassportNumber());
        model.setPhone(entity.getPhone());
        model.setRecommenderWorkspace(entity.getRecommenderWorkspace().getDescription(langId));
        model.setRecommenderWorkspaceStrId(entity.getRecommenderWorkspace().getNameStrId());

        return model;
    }

    public static Recommendation toEntity(RecommendationMetaModel model, long langId) {
        Recommendation entity = new Recommendation();
        entity.setId(model.getId());
        entity.setRecommendationDate(model.getRecommendationDate());
        entity.setIdentificationNumber(model.getIdentificationNumber());
        entity.setPassportNumber(model.getPassportNumber());
        entity.setPhone(model.getPhone());
        if (model.getRecommender() != null) {
            entity.setRecommender(new Person(model.getRecommender().getId()));
        }
        entity.setCooperationPlace(new Description(langId, model.getCooperationPlaceStrId(), model.getCooperationPlace()));
        entity.setRecommenderWorkspace(new Description(langId, model.getRecommenderWorkspaceStrId(), model.getRecommenderWorkspace()));

        return entity;
    }

    public static List<Recommendation> toEntities(List<RecommendationMetaModel> models, long langId) {
        List<Recommendation> result = new ArrayList<>();
        models.forEach(m -> result.add(toEntity(m, langId)));
        return result;
    }

    public static List<RecommendationMetaModel> toModels(List<Recommendation> entities, long langId) {
        List<RecommendationMetaModel> result = new ArrayList<>();
        entities.forEach(e -> result.add(toModel(e, langId)));
        return result;
    }
}
