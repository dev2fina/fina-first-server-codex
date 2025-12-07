package net.fina.first.ecm.questionnaire.proxy;


import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.ecm.alfresco.AlfrescoClient;
import net.fina.ecm.alfresco.api.common.constant.APIConstants;
import net.fina.ecm.alfresco.api.common.parameters.FieldsParam;
import net.fina.ecm.alfresco.api.common.parameters.IncludeParam;
import net.fina.ecm.alfresco.api.common.parameters.OrderByParam;
import net.fina.ecm.alfresco.api.common.representation.ResultPaging;
import net.fina.ecm.alfresco.api.core.model.body.AssociationBody;
import net.fina.ecm.alfresco.api.core.model.body.NodeBodyCreate;
import net.fina.ecm.alfresco.api.core.model.body.NodeBodyUpdate;
import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import net.fina.ecm.alfresco.api.search.body.QueryBody;
import net.fina.ecm.alfresco.api.search.body.RequestPagination;
import net.fina.ecm.alfresco.api.search.body.RequestQuery;
import net.fina.ecm.alfresco.api.search.model.ResultNodeRepresentation;
import net.fina.ecm.alfresco.api.search.model.ResultSetRepresentation;
import net.fina.ecm.util.AlfrescoConfiguration;
import net.fina.ecm.util.AlfrescoPropConstants;
import net.fina.first.common.exception.NodeException;
import net.fina.first.ecm.EcmConstants;
import net.fina.first.ecm.client.proxy.EcmClientProxySession;
import net.fina.first.ecm.fi.model.FiTypeMetaModel;
import net.fina.first.ecm.fi.model.FiTypeModelHelper;
import net.fina.first.ecm.node.api.NodeLocal;
import net.fina.first.ecm.questionnaire.model.*;
import net.fina.first.interceptors.FirstRecordingAuditor;
import org.jboss.ejb3.annotation.SecurityDomain;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import java.util.*;

@Stateless
@RolesAllowed({PermissionIdNames.FIRST_REVIEW})
@SecurityDomain("FinASecurityDomain")
@Interceptors(FirstRecordingAuditor.class)
public class QuestionnaireProxySession {

    @Inject
    private EcmClientProxySession ecmClientProxySession;

    @Inject
    private NodeLocal nodeProxySession;

    @RolesAllowed({PermissionIdNames.FIRST_CONFIG_REVIEW})
    public PaginatedListWrapper<QuestionnaireMetaModel> loadQuestionnaires(int start, int limit) {

        List<QuestionnaireMetaModel> resultModels = new ArrayList<>();

        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient();
        String fiQuestionnaireRootFolderPath = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.FI_QUESTIONNAIRE_ROOT_FOLDER_PATH_KEY);

        List<String> orderBy = Arrays.asList(EcmConstants.QUESTIONNAIRE_ASSOCIATED_GROUP_CODE, EcmConstants.QUESTIONNAIRE_ASSOCIATED_FI_TYPE_CODE, EcmConstants.QUESTIONNAIRE_SEQUENCE);
        ResultPaging<NodeRepresentation> questionnaireNodes = client.getNodesAPI().listNodeChildrenCall(APIConstants.FOLDER_ROOT,
                start, limit, new OrderByParam(orderBy), "(nodeType='" + EcmConstants.QUESTIONNAIRE_TYPE_QUESTIONNAIRE + "')",
                new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)), fiQuestionnaireRootFolderPath, null, null);
        if (questionnaireNodes != null && questionnaireNodes.getObjects() != null) {
            for (NodeRepresentation questionnaireNode : questionnaireNodes.getObjects()) {

                FiTypeMetaModel fiTypeMetaModel = null;
                QuestionnaireGroupMetaModel groupMetaModel = null;
                ResultPaging<NodeRepresentation> assocs = client.getNodesAPI().listTargetAssociationsCall(questionnaireNode.getId(), null, new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)), new FieldsParam(Arrays.asList(APIConstants.PROPERTIES_VALUE, "id", "nodeType")));
                if (assocs != null && assocs.getObjects() != null) {
                    for (NodeRepresentation assoc : assocs.getObjects()) {
                        if (assoc.getNodeType().equalsIgnoreCase(EcmConstants.TYPE_FI_TYPE)) {
                            fiTypeMetaModel = FiTypeModelHelper.getModel(assoc.getProperties(), assoc.getId());
                        } else if (assoc.getNodeType().equalsIgnoreCase(EcmConstants.GROUP_TYPE_GROUP)) {
                            groupMetaModel = QuestionnaireGroupModelHelper.getModel(assoc.getProperties(), assoc.getId());
                        }
                    }
                }
                QuestionnaireMetaModel questionnaireMetaModel = QuestionnaireModelHelper.getModel(questionnaireNode.getProperties(), questionnaireNode.getId(), groupMetaModel, fiTypeMetaModel);
                resultModels.add(questionnaireMetaModel);
            }
        }

        PaginatedListWrapper<QuestionnaireMetaModel> listWrapper = new PaginatedListWrapper<>();
        listWrapper.setList(resultModels);
        listWrapper.setTotalResults(questionnaireNodes != null ? questionnaireNodes.getPagination().getTotalItems() : 0);
        return listWrapper;
    }

    @RolesAllowed({PermissionIdNames.FIRST_CONFIG_AMEND})
    public QuestionnaireMetaModel saveQuestionnaire(QuestionnaireMetaModel model) throws NodeException {
        TreeMap<String, Object> properties = new TreeMap<>();
        properties.put(EcmConstants.QUESTIONNAIRE_PROP_QUESTION, model.getQuestion());
        properties.put(EcmConstants.QUESTIONNAIRE_PROP_OBLIGATORY, model.isObligatory());
        properties.put(APIConstants.PROP_AUTO_VERSION_ON_UPDATE_PROPS, true);
        properties.put(EcmConstants.QUESTIONNAIRE_SEQUENCE, model.getSequence() == 0 ? getQuestionnaireLastSequence(model) : model.getSequence());
        properties.put(EcmConstants.QUESTIONNAIRE_PARENT_ID, model.getQuestionnaireParentId());
        properties.put(EcmConstants.QUESTIONNAIRE_GROUP_CHECK_SIZE, model.getCheckSize());
        properties.put(EcmConstants.QUESTIONNAIRE_GROUP_NAME, model.getQuestionnaireGroupName());
        properties.put(EcmConstants.QUESTIONNAIRE_DEFAULT_VALUE, model.getDefaultValue());
        properties.put(EcmConstants.QUESTIONNAIRE_CODE, model.getCode());

        // associations
        AssociationBody associationFiTypeBody = new AssociationBody(model.getFiType().getId(), EcmConstants.QUESTIONNAIRE_ASSOC_FI_TYPE);
        AssociationBody associationGroupBody = new AssociationBody(model.getGroup().getId(), EcmConstants.QUESTIONNAIRE_ASSOC_GROUP);

        List<AssociationBody> associationBodyList = new ArrayList<>();
        associationBodyList.add(associationFiTypeBody);
        associationBodyList.add(associationGroupBody);

        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient();
        NodeRepresentation questionnaireNode;
        if (model.getId() != null && !model.getId().trim().isEmpty()) { // update
            NodeRepresentation currentQuestionnaireItem = client.getNodesAPI().getNodeCall(String.valueOf(model.getId()));

            ResultPaging<NodeRepresentation> assocs = client.getNodesAPI().listTargetAssociationsCall(currentQuestionnaireItem.getId(), null, null, new FieldsParam(Arrays.asList("id", "nodeType")));
            if (assocs != null && assocs.getObjects() != null) {
                for (NodeRepresentation assoc : assocs.getObjects()) {
                    if (assoc.getNodeType().equalsIgnoreCase(EcmConstants.QUESTIONNAIRE_TYPE_QUESTIONNAIRE) && !assoc.getId().equalsIgnoreCase(model.getFiType().getId())) {
                        client.getNodesAPI().deleteAssocationCall(currentQuestionnaireItem.getId(), assoc.getId(), EcmConstants.QUESTIONNAIRE_ASSOC_FI_TYPE);
                        client.getNodesAPI().createAssocationCall(currentQuestionnaireItem.getId(), associationFiTypeBody, null);
                    } else if (assoc.getNodeType().equalsIgnoreCase(EcmConstants.GROUP_TYPE_GROUP) && !assoc.getId().equalsIgnoreCase(model.getGroup().getId())) {
                        client.getNodesAPI().deleteAssocationCall(currentQuestionnaireItem.getId(), assoc.getId(), EcmConstants.QUESTIONNAIRE_ASSOC_GROUP);
                        client.getNodesAPI().createAssocationCall(currentQuestionnaireItem.getId(), associationGroupBody, null);
                    }
                }
            } else {
                client.getNodesAPI().createAssocationCall(currentQuestionnaireItem.getId(), associationBodyList, null);
            }

            NodeBodyUpdate nodeBodyUpdate = new NodeBodyUpdate(currentQuestionnaireItem.getName(), null, properties, null);
            questionnaireNode = nodeProxySession.updateNode(currentQuestionnaireItem.getId(), nodeBodyUpdate, client);
        } else { // create
            NodeBodyCreate nodeBodyCreate = new NodeBodyCreate(UUID.randomUUID().toString(), EcmConstants.QUESTIONNAIRE_TYPE_QUESTIONNAIRE, properties, null);
            nodeBodyCreate.setTargets(associationBodyList);

            String fiQuestionnaireRootFolderPath = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.FI_QUESTIONNAIRE_ROOT_FOLDER_PATH_KEY);
            NodeRepresentation questionnaireRootNode = client.getNodesAPI().getNodeCall(APIConstants.FOLDER_ROOT, null, fiQuestionnaireRootFolderPath, null);

            questionnaireNode = nodeProxySession.createNode(questionnaireRootNode.getId(), nodeBodyCreate, true, null, null, client);
        }

        return QuestionnaireModelHelper.getModel(questionnaireNode.getProperties(), questionnaireNode.getId(), model.getGroup(), model.getFiType());
    }

    @RolesAllowed({PermissionIdNames.FIRST_CONFIG_DELETE})
    public void deleteQuestionnaire(String id) throws Throwable {
        nodeProxySession.deleteNodeById(id);
    }

    @RolesAllowed({PermissionIdNames.FIRST_CONFIG_AMEND})
    public List<QuestionnaireMetaModel> getQuestionnairesByGroupAndType(String groupId, String typeId) {
        List<QuestionnaireMetaModel> result = new ArrayList<>();
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient();
        String fiQuestionnaireRootFolderPath = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.FI_QUESTIONNAIRE_ROOT_FOLDER_PATH_KEY);

        ResultPaging<NodeRepresentation> questionnaireNodes = client.getNodesAPI().listNodeChildrenCall(APIConstants.FOLDER_ROOT,
                0, Integer.MAX_VALUE - 1, new OrderByParam(Collections.singletonList(EcmConstants.QUESTIONNAIRE_SEQUENCE)), "(nodeType='" + EcmConstants.QUESTIONNAIRE_TYPE_QUESTIONNAIRE + "')",
                new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)), fiQuestionnaireRootFolderPath, null, null);
        if (questionnaireNodes != null && questionnaireNodes.getObjects() != null) {
            for (NodeRepresentation questionnaireNode : questionnaireNodes.getObjects()) {

                FiTypeMetaModel fiTypeMetaModel = null;
                QuestionnaireGroupMetaModel groupMetaModel = null;
                ResultPaging<NodeRepresentation> assocs = client.getNodesAPI().listTargetAssociationsCall(questionnaireNode.getId(), null, new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)), new FieldsParam(Arrays.asList(APIConstants.PROPERTIES_VALUE, "id", "nodeType")));
                if (assocs != null && assocs.getObjects() != null) {
                    for (NodeRepresentation assoc : assocs.getObjects()) {
                        if (assoc.getNodeType().equalsIgnoreCase(EcmConstants.TYPE_FI_TYPE) && assoc.getId().equals(typeId)) {
                            fiTypeMetaModel = FiTypeModelHelper.getModel(assoc.getProperties(), assoc.getId());
                        } else if (assoc.getNodeType().equalsIgnoreCase(EcmConstants.GROUP_TYPE_GROUP) && assoc.getId().equals(groupId)) {
                            groupMetaModel = QuestionnaireGroupModelHelper.getModel(assoc.getProperties(), assoc.getId());
                        }
                    }
                }
                if (groupMetaModel != null && fiTypeMetaModel != null) {
                    QuestionnaireMetaModel questionnaireMetaModel = QuestionnaireModelHelper.getModel(questionnaireNode.getProperties(), questionnaireNode.getId(), groupMetaModel, fiTypeMetaModel);
                    result.add(questionnaireMetaModel);

                }
            }
        }
        return result;
    }

    @RolesAllowed({PermissionIdNames.FIRST_CONFIG_AMEND})
    public QuestionnaireMetaModel updateSequence(QuestionnaireMetaModel model, int sequenceCount) {

        List<QuestionnaireMetaModel> groupedNodes = getQuestionnairesByGroupAndType(model.getGroup().getId(), model.getFiType().getId());

        int index = groupedNodes.indexOf(model);
        if ((index + sequenceCount) >= 0 && (index + sequenceCount) < groupedNodes.size()) {
            QuestionnaireMetaModel swapQuestionnaire = groupedNodes.get(index + sequenceCount);
            int oldSequence = model.getSequence();
            model.setSequence(swapQuestionnaire.getSequence());
            swapQuestionnaire.setSequence(oldSequence);

            ecmClientProxySession.getAlfrescoClient().getNodesAPI().updateNodeCall(swapQuestionnaire.getId(), new NodeBodyUpdate(new TreeMap<>(QuestionnaireModelHelper.getNode(swapQuestionnaire).getProperties())));
            ecmClientProxySession.getAlfrescoClient().getNodesAPI().updateNodeCall(model.getId(), new NodeBodyUpdate(new TreeMap<>(QuestionnaireModelHelper.getNode(model).getProperties())));

        }
        return model;
    }

    @RolesAllowed({PermissionIdNames.FIRST_CONFIG_AMEND})
    private int getQuestionnaireLastSequence(QuestionnaireMetaModel model) {
        List<QuestionnaireMetaModel> groupedNodes = getQuestionnairesByGroupAndType(model.getGroup().getId(), model.getFiType().getId());

        return groupedNodes.isEmpty() ? 1 : groupedNodes.get(groupedNodes.size() - 1).getSequence() + 1;
    }

    @RolesAllowed({PermissionIdNames.FIRST_CONFIG_AMEND})
    public void manageQuestionnaires(QuestionnaireManagementModel model) {

        if (model.getQuestionnaireParentId() != null && !model.getQuestionnaireParentId().trim().isEmpty()) {
            NodeRepresentation parent = ecmClientProxySession.getAlfrescoClient().getNodesAPI().getNodeCall(model.getQuestionnaireParentId());
            parent.getProperties().put(EcmConstants.QUESTIONNAIRE_GROUP_CHECK_SIZE, model.getCheckSize());

            ecmClientProxySession.getAlfrescoClient().getNodesAPI().updateNodeCall(parent.getId(), new NodeBodyUpdate(new TreeMap<>(parent.getProperties())));

            for (String id : model.getGroupedIds()) {
                NodeRepresentation node = ecmClientProxySession.getAlfrescoClient().getNodesAPI().getNodeCall(id);
                node.getProperties().put(EcmConstants.QUESTIONNAIRE_PARENT_ID, model.getQuestionnaireParentId());
                node.getProperties().put(EcmConstants.QUESTIONNAIRE_GROUP_CHECK_SIZE, model.getCheckSize());

                ecmClientProxySession.getAlfrescoClient().getNodesAPI().updateNodeCall(node.getId(), new NodeBodyUpdate(new TreeMap<>(node.getProperties())));
            }
        } else if (model.getGroupedIds() != null) {
            String groupName = UUID.randomUUID().toString();
            for (String id : model.getGroupedIds()) {
                NodeRepresentation node = ecmClientProxySession.getAlfrescoClient().getNodesAPI().getNodeCall(id);
                node.getProperties().put(EcmConstants.QUESTIONNAIRE_GROUP_NAME, groupName);
                node.getProperties().put(EcmConstants.QUESTIONNAIRE_GROUP_CHECK_SIZE, model.getCheckSize());
                ecmClientProxySession.getAlfrescoClient().getNodesAPI().updateNodeCall(node.getId(), new NodeBodyUpdate(new TreeMap<>(node.getProperties())));
            }
        }
    }

    @RolesAllowed({PermissionIdNames.FIRST_CONFIG_REVIEW})
    public PaginatedListWrapper<QuestionnaireManagementModel> loadManageQuestionnaires(int start, int limit, String group) {
        String propertyName = EcmConstants.QUESTIONNAIRE_GROUP_NAME;
        propertyName = group != null && group.equalsIgnoreCase("SUBGROUP") ? EcmConstants.QUESTIONNAIRE_PARENT_ID : propertyName;
        RequestQuery query = new RequestQuery().query("select * from fina:questionnaire where " + propertyName + " IS NOT NULL order by fina:questionnaireSequence asc").language(RequestQuery.LanguageEnum.CMIS);
        QueryBody body = new QueryBody().query(query).include(Collections.singletonList("properties")).paging(new RequestPagination().skipCount(start).maxItems(limit));

        ResultSetRepresentation<ResultNodeRepresentation> resultSet = ecmClientProxySession.getAlfrescoClient().getSearchAPI().search(body);

        List<QuestionnaireManagementModel> resultModels = new ArrayList<>();
        if (resultSet != null && resultSet.getObjects() != null) {
            for (NodeRepresentation questionnaireNode : resultSet.getObjects()) {

                FiTypeMetaModel fiTypeMetaModel = null;
                QuestionnaireGroupMetaModel groupMetaModel = null;
                ResultPaging<NodeRepresentation> assocs = ecmClientProxySession.getAlfrescoClient().getNodesAPI().listTargetAssociationsCall(questionnaireNode.getId(), null, new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)), new FieldsParam(Arrays.asList(APIConstants.PROPERTIES_VALUE, "id", "nodeType")));
                if (assocs != null && assocs.getObjects() != null) {
                    for (NodeRepresentation assoc : assocs.getObjects()) {
                        if (assoc.getNodeType().equalsIgnoreCase(EcmConstants.TYPE_FI_TYPE)) {
                            fiTypeMetaModel = FiTypeModelHelper.getModel(assoc.getProperties(), assoc.getId());
                        } else if (assoc.getNodeType().equalsIgnoreCase(EcmConstants.GROUP_TYPE_GROUP)) {
                            groupMetaModel = QuestionnaireGroupModelHelper.getModel(assoc.getProperties(), assoc.getId());
                        }
                    }
                }
                QuestionnaireMetaModel questionnaireMetaModel = QuestionnaireModelHelper.getModel(questionnaireNode.getProperties(), questionnaireNode.getId(), groupMetaModel, fiTypeMetaModel);
                QuestionnaireManagementModel resultModel = new QuestionnaireManagementModel(questionnaireMetaModel);
                resultModel.setQuestionnaireParentId(propertyName.equals(EcmConstants.QUESTIONNAIRE_GROUP_NAME) ? null : questionnaireMetaModel.getQuestionnaireParentId());

                if (resultModel.getQuestionnaireParentId() != null && !resultModel.getQuestionnaireParentId().trim().isEmpty()) {
                    NodeRepresentation parentNode = ecmClientProxySession.getAlfrescoClient().getNodesAPI().getNodeCall(resultModel.getQuestionnaireParentId());
                    resultModel.setParentNode(QuestionnaireModelHelper.getModel(parentNode.getProperties(), parentNode.getId(), null, null));
                }
                resultModels.add(resultModel);
            }
        }
        PaginatedListWrapper<QuestionnaireManagementModel> listWrapper = new PaginatedListWrapper<>();
        listWrapper.setList(resultModels);
        listWrapper.setTotalResults(resultSet != null ? resultSet.getPagination().getTotalItems() : 0);
        return listWrapper;
    }

    @RolesAllowed({PermissionIdNames.FIRST_CONFIG_DELETE})
    public void removeGroupingFromQuestionnaires(List<QuestionnaireManagementModel> models) {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient();
        models.forEach(model -> {
            NodeRepresentation node = QuestionnaireModelHelper.getNode(model);
            node.getProperties().put(EcmConstants.QUESTIONNAIRE_PARENT_ID, null);
            node.getProperties().put(EcmConstants.QUESTIONNAIRE_GROUP_NAME, null);
            node.getProperties().put(EcmConstants.QUESTIONNAIRE_GROUP_CHECK_SIZE, 0);
            client.getNodesAPI().updateNodeCall(node.getId(), new NodeBodyUpdate(new TreeMap<>(node.getProperties())));
        });
    }

}
