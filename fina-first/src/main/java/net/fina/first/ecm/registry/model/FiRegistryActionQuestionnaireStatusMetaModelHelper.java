package net.fina.first.ecm.registry.model;

import net.fina.ecm.alfresco.api.common.representation.ResultPaging;
import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import net.fina.first.FirstUtil;
import net.fina.first.ecm.EcmConstants;
import net.fina.first.ecm.questionnaire.model.QuestionnaireMetaModel;
import net.fina.first.ecm.questionnaire.model.QuestionnaireModelHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class FiRegistryActionQuestionnaireStatusMetaModelHelper {

    public static List<FiRegistryActionQuestionnaireStatusMetaModel> getModels(ResultPaging<NodeRepresentation> actionQuestionnaires) {
        List<FiRegistryActionQuestionnaireStatusMetaModel> result = new ArrayList<>();
        if (actionQuestionnaires != null) {
            for (NodeRepresentation questionnaire : actionQuestionnaires.getObjects()) {
                result.add(getModel(questionnaire.getProperties(), questionnaire.getId()));
            }
        }
        return result;
    }

    public static FiRegistryActionQuestionnaireStatusMetaModel getModel(Map<String, Object> properties, String id) {
        FiRegistryActionQuestionnaireStatusMetaModel model = new FiRegistryActionQuestionnaireStatusMetaModel();
        model.setId(id);

        if (properties != null && !properties.isEmpty()) {

            model.setStatus(FirstUtil.getValue(properties.get(EcmConstants.ACTION_QUESTIONNAIRE_PROP_STATUS), String.class));
            model.setState(FiRegistryActionQuestionnaireStatusState.CURRENT);
            model.setNote(FirstUtil.getValue(properties.get(EcmConstants.ACTION_QUESTIONNAIRE_PROP_NOTE), String.class));

            model.setQuestion(FirstUtil.getValue(properties.get(EcmConstants.ACTION_QUESTIONNAIRE_PROP_QUESTION), String.class));
            model.setQuestionnaireId(id);
            Boolean obligatory = FirstUtil.getValue(properties.get(EcmConstants.ACTION_QUESTIONNAIRE_PROP_OBLIGATORY), Boolean.class);
            model.setObligatory(obligatory != null ? obligatory : false);
            model.setRelevanceTime(FirstUtil.getValue(properties.get(EcmConstants.ACTION_QUESTIONNAIRE_MODIFIED_AT), Date.class));
            Boolean isPredefined = FirstUtil.getValue(properties.get(EcmConstants.ACTION_QUESTIONNAIRE_PROP_PREDEFINED), Boolean.class);
            model.setPredefined(isPredefined != null ? isPredefined : false);
            model.setQuestionnaireParentId(FirstUtil.getValue(properties.get(EcmConstants.ACTION_QUESTIONNAIRE_PARENT_ID), String.class));
            model.setQuestionnaireGroupName(FirstUtil.getValue(properties.get(EcmConstants.ACTION_QUESTIONNAIRE_GROUP_NAME), String.class));
            Integer checkSize = FirstUtil.getValue(properties.get(EcmConstants.ACTION_QUESTIONNAIRE_GROUP_CHECK_SIZE), Integer.class);
            model.setCheckSize(checkSize != null ? checkSize : 1);
            Integer sequence = FirstUtil.getValue(properties.get(EcmConstants.ACTION_QUESTIONNAIRE_SEQUENCE), Integer.class);
            model.setSequence(sequence != null ? sequence : 1);
            model.setCode(FirstUtil.getValue(properties.get(EcmConstants.ACTION_QUESTIONNAIRE_CODE), String.class));
            model.setQuestionnaireTypeGroup(model.isPredefined() ? EcmConstants.QUESTIONNAIRE_TYPE_MAIN_TITLE : EcmConstants.QUESTIONNAIRE_TYPE_EXTRA_TITLE);

            if (!model.isPredefined()) {
                model.setQuestionnairePropertyName(EcmConstants.ACTION_QUESTIONNAIRE_PROP_STATUS);
            }
        }

        return model;
    }

    public static FiRegistryActionQuestionnaireStatusMetaModel getMetaModel(Map<String, Object> properties, Map<String, Object> questionnaireProperties, String id, String questionnaireId) {
        FiRegistryActionQuestionnaireStatusMetaModel model = new FiRegistryActionQuestionnaireStatusMetaModel();
        model.setId(id);

        if (properties != null && !properties.isEmpty()) {
            model.setStatus(FirstUtil.getValue(properties.get(EcmConstants.ACTION_QUESTIONNAIRE_PROP_STATUS), String.class));
            model.setState(FiRegistryActionQuestionnaireStatusState.CURRENT);
            model.setNote(FirstUtil.getValue(properties.get(EcmConstants.ACTION_QUESTIONNAIRE_PROP_NOTE), String.class));

            QuestionnaireMetaModel questionnaireMetaModel = QuestionnaireModelHelper.getModel(questionnaireProperties, questionnaireId, null, null);
            model.setQuestion(questionnaireMetaModel.getQuestion());
            model.setObligatory(questionnaireMetaModel.isObligatory());
            model.setQuestionnaireId(questionnaireId);
            model.setRelevanceTime(FirstUtil.getValue(properties.get(EcmConstants.ACTION_QUESTIONNAIRE_MODIFIED_AT), Date.class));
            Boolean isPredefined = FirstUtil.getValue(properties.get(EcmConstants.ACTION_QUESTIONNAIRE_PROP_PREDEFINED), Boolean.class);
            model.setPredefined(isPredefined != null ? isPredefined : false);
            model.setQuestionnaireParentId(FirstUtil.getValue(properties.get(EcmConstants.ACTION_QUESTIONNAIRE_PARENT_ID), String.class));
            model.setQuestionnaireGroupName(FirstUtil.getValue(properties.get(EcmConstants.ACTION_QUESTIONNAIRE_GROUP_NAME), String.class));
            Integer checkSize = FirstUtil.getValue(properties.get(EcmConstants.ACTION_QUESTIONNAIRE_GROUP_CHECK_SIZE), Integer.class);
            model.setCheckSize(checkSize != null ? checkSize : 1);
            Integer sequence = FirstUtil.getValue(properties.get(EcmConstants.ACTION_QUESTIONNAIRE_SEQUENCE), Integer.class);
            model.setSequence(sequence != null ? sequence : 1);
            model.setCode(FirstUtil.getValue(properties.get(EcmConstants.ACTION_QUESTIONNAIRE_CODE), String.class));
            model.setQuestionnaireTypeGroup(model.isPredefined() ? EcmConstants.QUESTIONNAIRE_TYPE_MAIN_TITLE : EcmConstants.QUESTIONNAIRE_TYPE_EXTRA_TITLE);

            if (!model.isPredefined()) {
                model.setQuestionnairePropertyName(EcmConstants.ACTION_QUESTIONNAIRE_PROP_STATUS);
            }
        }

        return model;
    }
}
