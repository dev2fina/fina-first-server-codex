package net.fina.first.ecm.questionnaire.proxy;

import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.ecm.alfresco.AlfrescoClient;
import net.fina.ecm.alfresco.api.common.constant.APIConstants;
import net.fina.ecm.alfresco.api.common.parameters.IncludeParam;
import net.fina.ecm.alfresco.api.common.representation.ResultPaging;
import net.fina.ecm.alfresco.api.core.model.body.NodeBodyCreate;
import net.fina.ecm.alfresco.api.core.model.body.NodeBodyUpdate;
import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import net.fina.ecm.util.AlfrescoConfiguration;
import net.fina.ecm.util.AlfrescoPropConstants;
import net.fina.first.common.exception.NodeException;
import net.fina.first.ecm.EcmConstants;
import net.fina.first.ecm.client.proxy.EcmClientProxySession;
import net.fina.first.ecm.node.api.NodeLocal;
import net.fina.first.ecm.questionnaire.model.QuestionnaireGroupMetaModel;
import net.fina.first.ecm.questionnaire.model.QuestionnaireGroupModelHelper;
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
public class QuestionnaireGroupProxySession {

    @Inject
    private EcmClientProxySession ecmClientProxySession;

    @Inject
    private NodeLocal nodeProxySession;

    @RolesAllowed({PermissionIdNames.FIRST_CONFIG_REVIEW})
    public PaginatedListWrapper<QuestionnaireGroupMetaModel> loadQuestionnaireGroups(Integer start, Integer pageSize) {

        List<QuestionnaireGroupMetaModel> resultModels = new ArrayList<>();

        String fiQuestionnaireGroupsRootFolderPath = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.FI_QUESTIONNAIRE_GROUPS_ROOT_FOLDER_PATH_KEY);

        long totalResult = 0;
        ResultPaging<NodeRepresentation> questionnaireGroupNodes = ecmClientProxySession.getAlfrescoClient().getNodesAPI().listNodeChildrenCall(APIConstants.FOLDER_ROOT,
                start, pageSize, null, "(nodeType='" + EcmConstants.GROUP_TYPE_GROUP + "')",
                new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)), fiQuestionnaireGroupsRootFolderPath, null, null);
        if (questionnaireGroupNodes != null && questionnaireGroupNodes.getObjects() != null) {
            totalResult = (long) questionnaireGroupNodes.getCount();
            for (NodeRepresentation questionnaireGroupNode : questionnaireGroupNodes.getObjects()) {
                QuestionnaireGroupMetaModel questionnaireGroupMetaModel = QuestionnaireGroupModelHelper.getModel(questionnaireGroupNode.getProperties(), questionnaireGroupNode.getId());
                resultModels.add(questionnaireGroupMetaModel);
            }
        }

        PaginatedListWrapper<QuestionnaireGroupMetaModel> listWrapper = new PaginatedListWrapper<>();
        listWrapper.setList(resultModels);
        listWrapper.setTotalResults(totalResult);
        return listWrapper;
    }

    @RolesAllowed({PermissionIdNames.FIRST_CONFIG_AMEND})
    public QuestionnaireGroupMetaModel saveQuestionnaireGroup(QuestionnaireGroupMetaModel model) throws NodeException {
        TreeMap<String, Object> properties = new TreeMap<>();
        properties.put(EcmConstants.GROUP_PROP_CODE, model.getCode());
        properties.put(EcmConstants.GROUP_PROP_DESCRIPTION, model.getDescription());

        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient();
        NodeRepresentation questionnaireGroupNode;
        if (model.getId() != null && !model.getId().trim().isEmpty()) { // update
            NodeRepresentation currentQuestionnaireGroupeNode = client.getNodesAPI().getNodeCall(String.valueOf(model.getId()));
            NodeBodyUpdate nodeBodyUpdate = new NodeBodyUpdate(currentQuestionnaireGroupeNode.getName(), null, properties, null);
            questionnaireGroupNode = nodeProxySession.updateNode(currentQuestionnaireGroupeNode.getId(), nodeBodyUpdate, client);
        } else { // create
            properties.put(APIConstants.PROP_AUTO_VERSION_ON_UPDATE_PROPS, true);
            String questionnaireGroupRootFolderPath = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.FI_QUESTIONNAIRE_GROUPS_ROOT_FOLDER_PATH_KEY);
            NodeRepresentation questionnaireGroupRootNode = client.getNodesAPI().getNodeCall(APIConstants.FOLDER_ROOT, null, questionnaireGroupRootFolderPath, null);

            NodeBodyCreate nodeBodyCreate = new NodeBodyCreate(UUID.randomUUID().toString(), EcmConstants.GROUP_TYPE_GROUP, properties, null);
            questionnaireGroupNode = nodeProxySession.createNode(questionnaireGroupRootNode.getId(), nodeBodyCreate, true, null, null, client);
        }

        return QuestionnaireGroupModelHelper.getModel(questionnaireGroupNode.getProperties(), questionnaireGroupNode.getId());
    }

    @RolesAllowed({PermissionIdNames.FIRST_CONFIG_DELETE})
    public void deleteQuestionnaireGroup(String id) throws Throwable {
        nodeProxySession.deleteNodeById(id);
    }
}
