package net.fina.server.survey.model;

import net.fina.server.survey.entity.Survey;

import java.util.List;
import java.util.stream.Collectors;

public class SurveyMetaModelHelper {
    public static Survey toEntity(SurveyMetaModel model) {
        Survey entity = new Survey();

        entity.setId(model.getId());
        entity.setName(model.getName());
        entity.setSurvey(model.getSurvey());
        entity.setProgression(model.getProgression());
        return entity;
    }

    public static SurveyMetaModel toModel(Survey entity) {
        SurveyMetaModel model = new SurveyMetaModel();

        model.setId(entity.getId());
        model.setSurvey(entity.getSurvey());
        model.setName(entity.getName());
        model.setProgression(entity.getProgression());
        model.setUserName(entity.getUser() != null ? entity.getUser().getLogin() : "Anonymous");
        return model;
    }

    public static List<SurveyMetaModel> toModel(List<Survey> entities) {
        if (entities == null) {
            return null;
        }

        return entities.stream()
                .map(SurveyMetaModelHelper::toModel)
                .collect(Collectors.toList());
    }
}
