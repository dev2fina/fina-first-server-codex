package net.fina.server.fi.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.fina.server.fi.entity.FiBranchType;
import net.fina.server.fi.model.configuration.FiConfigurationStepModelHelper;
import net.fina.server.i18n.helper.Description;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FiBranchTypeModelHelper extends FiConfigurationStepModelHelper {

    public static FiBranchType getEntity(FiBranchTypeMetaModel model, long langId) throws JsonProcessingException {
        FiBranchType entity = new FiBranchType();
        entity.setId(model.getId());
        entity.setCode(model.getCode());
        entity.setName(new Description(langId, model.getNameStrId(), model.getName()));
        entity.setJsonConfig(getJsonStringFromSteps(model.getSteps()));

        return entity;
    }

    public static List<FiBranchTypeMetaModel> getModels(List<FiBranchType> entities, long langId) throws IOException {
        List<FiBranchTypeMetaModel> result = new ArrayList<>();
        if (entities != null && !entities.isEmpty()) {
            for (FiBranchType entity : entities) {
                result.add(getModel(entity, langId));
            }
        }
        return result;
    }

    public static FiBranchTypeMetaModel getModel(FiBranchType entity, long langId) throws IOException {
        FiBranchTypeMetaModel model = new FiBranchTypeMetaModel();
        model.setId(entity.getId());
        model.setCode(entity.getCode());
        model.setName(entity.getName().getDescription(langId));
        model.setNameStrId(entity.getName().getNameStrId());
        if (entity.getJsonConfig() != null) {
            model.setSteps(getStepsFromJsonString(entity.getJsonConfig()));
        }
        model.setCount(entity.getCount());

        return model;
    }


}
