package net.fina.server.faq.model;

import net.fina.server.faq.entity.FaqItem;
import net.fina.server.i18n.helper.Description;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FaqItemModelHelper {
    public static FaqItem get(FaqItemMetaModel model, long langId) {
        FaqItem entity = new FaqItem();

        if (model.getId() < 0) {
            entity.setId(0);
        } else {
            entity.setId(model.getId());
        }

        if (model.getCategory() != null) {
            entity.setCategory(FaqCategoryMetaHelper.get(model.getCategory(), langId));
        }
        entity.setQuestion(new Description(langId, model.getQuestionStrId(), model.getQuestion()));
        entity.setAnswer(new Description(langId, model.getAnswerStrId(), model.getAnswer()));
        entity.setPublish(new Date());
        entity.setSequence(model.getSequence());

        return entity;
    }

    public static FaqItemMetaModel get(FaqItem entity, long langId) {
        FaqItemMetaModel model = new FaqItemMetaModel();
        model.setId(entity.getId());
        model.setCategory(FaqCategoryMetaHelper.get(entity.getCategory(), langId));
        model.setAnswer(entity.getAnswer().getDescription(langId));
        model.setQuestion(entity.getQuestion().getDescription(langId));
        model.setPublish(entity.getPublish());
        model.setUser(entity.getUser()!=null?entity.getUser().getDescription().getDescription(langId):"");
        model.setSequence(entity.getSequence());

        return model;
    }

    public static List<FaqItemMetaModel> getFaqModels(List<FaqItem> list, long langId) {
        List<FaqItemMetaModel> result = new ArrayList<>();

        list.forEach(entity -> {
            result.add(get(entity, langId));
        });

        return result;
    }

    public static List<FaqItem> getFaqEntities(List<FaqItemMetaModel> list, long langId) {
        List<FaqItem> result = new ArrayList<>();

        list.forEach(entity -> {
            result.add(get(entity, langId));
        });

        return result;
    }
}
