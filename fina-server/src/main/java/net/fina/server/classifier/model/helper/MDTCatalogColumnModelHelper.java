package net.fina.server.classifier.model.helper;

import net.fina.server.classifier.entity.MDTCatalogColumn;
import net.fina.server.classifier.model.MDTCatalogColumnMetaModel;
import net.fina.server.i18n.helper.Description;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MDTCatalogColumnModelHelper {

    public static MDTCatalogColumn toEntity(MDTCatalogColumnMetaModel model, long langId) {
        MDTCatalogColumn catalog = new MDTCatalogColumn();
        catalog.setId(model.getId());
        if (model.getNames() != null && !model.getNames().isEmpty()) {
            Description description = new Description();
            description.setNameStrId(model.getNameStrId());
            for (Map.Entry<Long, String> entry : model.getNames().entrySet()) {
                description.addDescription(entry.getKey(), entry.getValue());
            }
            catalog.setName(description);
        } else {
            catalog.setName(new Description(langId, model.getNameStrId(), model.getName()));
        }
        catalog.setDataType(model.getDataType());
        catalog.setKey(model.isKey());
        catalog.setSequence(model.getSequence());
        catalog.setDataFormat(model.getDataFormat());
        catalog.setIsRequired(model.getIsRequired());

        return catalog;
    }

    public static MDTCatalogColumnMetaModel toModel(MDTCatalogColumn entity, long langId) {
        MDTCatalogColumnMetaModel catalog = new MDTCatalogColumnMetaModel();
        catalog.setId(entity.getId());
        catalog.setName(entity.getName().getDescription(langId));
        catalog.setNameStrId(entity.getName().getNameStrId());
        catalog.setSequence(entity.getSequence());
        catalog.setKey(entity.isKey());
        catalog.setDataType(entity.getDataType());
        catalog.setDataFormat(entity.getDataFormat());
        catalog.setIsRequired(entity.isRequired());

        return catalog;
    }

    public static List<MDTCatalogColumnMetaModel> toModels(List<MDTCatalogColumn> entities, long langId) {
        List<MDTCatalogColumnMetaModel> result = new ArrayList<>();
        entities.forEach(e -> result.add(toModel(e, langId)));
        return result;
    }

    public static List<MDTCatalogColumn> toEntities(List<MDTCatalogColumnMetaModel> models, long langId) {
        List<MDTCatalogColumn> result = new ArrayList<>();
        models.forEach(m -> result.add(toEntity(m, langId)));
        return result;
    }

}
