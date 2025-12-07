package net.fina.server.i18n.model;

import net.fina.common.shared.i18n.DescriptionMetaModel;
import net.fina.server.i18n.helper.Description;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DescriptionModelHelper {

    public static List<DescriptionMetaModel> toModel(Description description) {
        List<DescriptionMetaModel> result = new ArrayList<>();
        if (description != null) {
            for (Map.Entry<Long, String> e : description.getDescriptions().entrySet()) {
                DescriptionMetaModel model = new DescriptionMetaModel();
                model.setNameStrId(description.getNameStrId());
                model.setLangId(e.getKey());
                model.setDescription(e.getValue());
                result.add(model);
            }
        }
        return result;
    }

    public static Description toEntity(List<DescriptionMetaModel> models) {
        Description description = new Description();
        if (models != null) {
            for (DescriptionMetaModel model : models) {
                description.setNameStrId(model.getNameStrId());
                description.addDescription(model.getLangId(), model.getDescription());
            }
        }
        return description;

    }
}
