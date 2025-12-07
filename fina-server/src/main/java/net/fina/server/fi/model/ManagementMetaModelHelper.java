package net.fina.server.fi.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.fina.server.fi.entity.Management;
import net.fina.server.fi.model.configuration.FiConfigurationStepModelHelper;
import net.fina.server.i18n.helper.Description;

import java.io.IOException;

public class ManagementMetaModelHelper extends FiConfigurationStepModelHelper {

    public static Management toEntity(ManagementMetaModel model, long langId) throws JsonProcessingException {
        Management management = new Management();
        management.setId(model.getId());
        management.setCode(model.getCode());
        management.setVersion(model.getVersion() == null ? 0 : model.getVersion());
        management.setDescription(new Description(langId, model.getNameStrId(), model.getName()));
        if(model.getSteps() != null) {
            management.setJsonConfig(getJsonStringFromSteps(model.getSteps()));
        }
        return management;
    }

    public static ManagementMetaModel toModel(Management entity, long langId) throws IOException {
        ManagementMetaModel model = new ManagementMetaModel();
        model.setId(entity.getId());
        model.setCode(entity.getCode());
        model.setVersion(entity.getVersion() == null ? 0 : entity.getVersion());
        model.setNameStrId(entity.getDescription().getNameStrId());
        model.setName(entity.getDescription().getDescription(langId));
        if(entity.getJsonConfig() != null) {
            model.setSteps(getStepsFromJsonString(entity.getJsonConfig()));
        }
        return model;
    }
}
