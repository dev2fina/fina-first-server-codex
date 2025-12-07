package net.fina.first.ecm.questionnaire.model;

import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import net.fina.first.FirstUtil;
import net.fina.first.ecm.EcmConstants;
import net.fina.first.ecm.fi.model.FiTypeMetaModel;

import java.util.Map;
import java.util.TreeMap;

public class QuestionnaireModelHelper {

    public static QuestionnaireMetaModel getModel(Map<String, Object> properties, String id, QuestionnaireGroupMetaModel groupMetaModel, FiTypeMetaModel fiTypeMetaModel) {
        QuestionnaireMetaModel model = new QuestionnaireMetaModel();
        model.setId(id);
        model.setGroup(groupMetaModel);
        model.setFiType(fiTypeMetaModel);

        if (properties != null && !properties.isEmpty()) {
            model.setQuestion(FirstUtil.getValue(properties.get(EcmConstants.QUESTIONNAIRE_PROP_QUESTION), String.class));
            model.setObligatory(FirstUtil.getValue(properties.get(EcmConstants.QUESTIONNAIRE_PROP_OBLIGATORY), Boolean.class));
            model.setQuestionnaireParentId(FirstUtil.getValue(properties.get(EcmConstants.QUESTIONNAIRE_PARENT_ID), String.class));
            model.setQuestionnaireGroupName(FirstUtil.getValue(properties.get(EcmConstants.QUESTIONNAIRE_GROUP_NAME), String.class));
            model.setCheckSize(FirstUtil.getValue(properties.get(EcmConstants.QUESTIONNAIRE_GROUP_CHECK_SIZE), Integer.class));
            model.setSequence(FirstUtil.getValue(properties.get(EcmConstants.QUESTIONNAIRE_SEQUENCE), Integer.class));
            model.setDefaultValue(FirstUtil.getValue(properties.get(EcmConstants.QUESTIONNAIRE_DEFAULT_VALUE), String.class));
            model.setCode(FirstUtil.getValue(properties.get(EcmConstants.QUESTIONNAIRE_CODE), String.class));
        }

        return model;
    }

    public static NodeRepresentation getNode(QuestionnaireMetaModel model) {
        NodeRepresentation node = new NodeRepresentation();
        node.setId(model.getId());
        Map<String, Object> properties = new TreeMap<>();
        properties.put(EcmConstants.QUESTIONNAIRE_PROP_QUESTION, model.getQuestion());
        properties.put(EcmConstants.QUESTIONNAIRE_PROP_OBLIGATORY, model.isObligatory());
        properties.put(EcmConstants.QUESTIONNAIRE_PARENT_ID, model.getQuestionnaireParentId());
        properties.put(EcmConstants.QUESTIONNAIRE_GROUP_NAME, model.getQuestionnaireGroupName());
        properties.put(EcmConstants.QUESTIONNAIRE_GROUP_CHECK_SIZE, model.getCheckSize());
        properties.put(EcmConstants.QUESTIONNAIRE_SEQUENCE, model.getSequence());
        properties.put(EcmConstants.QUESTIONNAIRE_DEFAULT_VALUE, model.getDefaultValue());
        properties.put(EcmConstants.QUESTIONNAIRE_CODE, model.getCode());
        node.setProperties(properties);

        return node;
    }
}
