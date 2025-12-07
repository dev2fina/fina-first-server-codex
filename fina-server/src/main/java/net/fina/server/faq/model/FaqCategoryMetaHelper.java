package net.fina.server.faq.model;

import net.fina.server.faq.entity.FaqCategory;
import net.fina.server.faq.entity.FaqItem;
import net.fina.server.i18n.helper.Description;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FaqCategoryMetaHelper {
    public static FaqCategory get(FaqCategoryMetaModel model, long langId) {
        FaqCategory category = new FaqCategory();

        if (model.getId() < 0) {
            category.setId(0);
        } else {
            category.setId(model.getId());
        }

        category.setParentId(model.getParentId());
        category.setLeaf(model.isLeaf());
        category.setName(new Description(langId, model.getNameStrId(), model.getName()));

        return category;
    }

    public static FaqCategoryMetaModel get(FaqCategory entity, long langId) {
        FaqCategoryMetaModel model = new FaqCategoryMetaModel();
        model.setId(entity.getId());
        model.setParentId(entity.getParentId());
        model.setLeaf(entity.isLeaf());
        model.setName(entity.getName().getDescription(langId));
        model.setNameStrId(entity.getName().getNameStrId());
        return model;
    }

    public static List<FaqCategoryMetaModel> getCategoryModels(List<FaqCategory> list, long langId) {
        List<FaqCategoryMetaModel> result = new ArrayList<>();

        list.forEach(entity -> {
            result.add(get(entity, langId));
        });

        return result;
    }

    public static List<FaqCategory> getCategoryEntities(List<FaqCategoryMetaModel> list, long langId) {
        List<FaqCategory> result = new ArrayList<>();

        list.forEach(entity -> {
            result.add(get(entity, langId));
        });

        return result;
    }


}
