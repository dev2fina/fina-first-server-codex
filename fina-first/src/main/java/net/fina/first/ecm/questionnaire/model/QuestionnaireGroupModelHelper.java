package net.fina.first.ecm.questionnaire.model;

import net.fina.first.FirstUtil;
import net.fina.first.ecm.EcmConstants;

import java.util.Map;

public class QuestionnaireGroupModelHelper {

    public static QuestionnaireGroupMetaModel getModel(Map<String, Object> properties, String id) {
        QuestionnaireGroupMetaModel model = new QuestionnaireGroupMetaModel();
        model.setId(id);

        if (properties != null && !properties.isEmpty()) {
            model.setCode(FirstUtil.getValue(properties.get(EcmConstants.GROUP_PROP_CODE), String.class));
            model.setDescription(FirstUtil.getValue(properties.get(EcmConstants.GROUP_PROP_DESCRIPTION), String.class));
        }

        return model;
    }

}
