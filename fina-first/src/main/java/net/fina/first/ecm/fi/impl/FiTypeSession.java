package net.fina.first.ecm.fi.impl;

import net.fina.common.shared.PaginatedListWrapper;
import net.fina.ecm.alfresco.AlfrescoClient;
import net.fina.ecm.alfresco.api.common.constant.APIConstants;
import net.fina.ecm.alfresco.api.common.parameters.FieldsParam;
import net.fina.ecm.alfresco.api.common.parameters.IncludeParam;
import net.fina.ecm.alfresco.api.common.parameters.OrderByParam;
import net.fina.ecm.alfresco.api.common.representation.ResultPaging;
import net.fina.ecm.alfresco.api.core.model.body.NodeBodyCreate;
import net.fina.ecm.alfresco.api.core.model.body.NodeBodyUpdate;
import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import net.fina.ecm.util.AlfrescoConfiguration;
import net.fina.ecm.util.AlfrescoPropConstants;
import net.fina.first.FirstUtil;
import net.fina.first.common.exception.NodeException;
import net.fina.first.ecm.EcmConstants;
import net.fina.first.ecm.client.proxy.EcmClientProxySession;
import net.fina.first.ecm.fi.api.FiTypeLocal;
import net.fina.first.ecm.fi.model.FiTypeMetaModel;
import net.fina.first.ecm.fi.model.FiTypeModelHelper;
import net.fina.first.ecm.node.api.NodeLocal;
import net.fina.first.interceptors.FirstRecordingAuditor;

import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;
import java.util.Map;
import java.util.Arrays;

@Stateless
@Local(FiTypeLocal.class)
@Interceptors(FirstRecordingAuditor.class)
public class FiTypeSession implements FiTypeLocal {

    @Inject
    private EcmClientProxySession ecmClientProxySession;

    @Inject
    private NodeLocal nodeProxySession;

    @Override
    public PaginatedListWrapper<FiTypeMetaModel> loadFiTypes(int start, int pageSize) {

        List<FiTypeMetaModel> resultModels = new ArrayList<>();

        String fiTypesRootFolderPath = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.FI_TYPE_ROOT_FOLDER_PATH_KEY);

        long totalResult = 0;
        ResultPaging<NodeRepresentation> fiTypeNodes = ecmClientProxySession.getAlfrescoClient().getNodesAPI().listNodeChildrenCall(APIConstants.FOLDER_ROOT,
                start, pageSize, new OrderByParam(Collections.singletonList("createdAt")), "(nodeType='" + EcmConstants.TYPE_FI_TYPE + "')",
                new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)), fiTypesRootFolderPath, null, null);

        if (fiTypeNodes != null) {
            totalResult = fiTypeNodes.getCount();
            resultModels.addAll(FiTypeModelHelper.getModels(fiTypeNodes.getObjects()));
        }

        PaginatedListWrapper<FiTypeMetaModel> listWrapper = new PaginatedListWrapper<>();
        listWrapper.setList(resultModels);
        listWrapper.setTotalResults(totalResult);
        return listWrapper;
    }

    @Override
    public FiTypeMetaModel saveFiType(FiTypeMetaModel model) throws NodeException {
        TreeMap<String, Object> properties = new TreeMap<>();
        properties.put(EcmConstants.PROP_CODE, model.getCode());
        properties.put(EcmConstants.PROP_DESCRIPTION, model.getDescription());
        properties.put(EcmConstants.PROP_BRANCH_TYPES, model.getBranchTypes());
        properties.put(EcmConstants.PROP_REGISTRATION_WORKFLOW_KEY, model.getRegistrationWorkflowKey());
        properties.put(EcmConstants.PROP_CHANGE_WORKFLOW_KEY, model.getChangeWorkflowKey());
        properties.put(EcmConstants.PROP_DISABLE_WORKFLOW_KEY, model.getDisableWorkflowKey());
        properties.put(EcmConstants.PROP_BRANCH_CHANGE_WORKFLOW_KEY, model.getBranchChangeWorkflowKey());
        properties.put(EcmConstants.PROP_BRANCH_EDIT_WORKFLOW_KEY, model.getBranchEditWorkflowKey());
        properties.put(EcmConstants.PROP_DOCUMENT_WITHDRAWAL_WORKFLOW_KEY, model.getDocumentWithdrawalWorkflowKey());

        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient();
        NodeRepresentation fiTypeNode;
        if (model.getId() != null && !model.getId().trim().isEmpty()) { // update
            NodeRepresentation currentFiTypeNode = client.getNodesAPI().getNodeCall(String.valueOf(model.getId()));
            NodeBodyUpdate nodeBodyUpdate = new NodeBodyUpdate(currentFiTypeNode.getName(), null, properties, null);
            fiTypeNode = nodeProxySession.updateNode(currentFiTypeNode.getId(), nodeBodyUpdate, client);
        } else { // create
            properties.put(APIConstants.PROP_AUTO_VERSION_ON_UPDATE_PROPS, true);
            String fiTypeRootFolderPath = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.FI_TYPE_ROOT_FOLDER_PATH_KEY);
            NodeRepresentation fiTypeRootNode = client.getNodesAPI().getNodeCall(APIConstants.FOLDER_ROOT, null, fiTypeRootFolderPath, null);

            NodeBodyCreate nodeBodyCreate = new NodeBodyCreate(UUID.randomUUID().toString(), EcmConstants.TYPE_FI_TYPE, properties, null);
            fiTypeNode = nodeProxySession.createNode(fiTypeRootNode.getId(), nodeBodyCreate, true, null, null, client);
        }

        return FiTypeModelHelper.getModel(fiTypeNode.getProperties(), fiTypeNode.getId());
    }

    @Override
    public void deleteFiType(String id) throws NodeException {
        nodeProxySession.deleteNodeById(id);
    }

    @Override
    public String getAssociatedFiTypeCode(NodeRepresentation registryNode) {
        ResultPaging<NodeRepresentation> assocs = ecmClientProxySession.getAlfrescoClient().getNodesAPI().listTargetAssociationsCall(registryNode.getId(), null, new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)), new FieldsParam(Arrays.asList(APIConstants.PROPERTIES_VALUE, "id", "nodeType")));
        if (assocs != null && assocs.getObjects() != null) {
            for (NodeRepresentation assoc : assocs.getObjects()) {
                Map<String, Object> assocProperties = assoc.getProperties();
                if (assoc.getNodeType().equalsIgnoreCase(EcmConstants.TYPE_FI_TYPE)) {
                    return FirstUtil.getValue(assocProperties.get(EcmConstants.PROP_CODE), String.class);
                }
            }
        }
        return null;
    }

    @Override
    public FiTypeMetaModel getFiTypeById(String registryId) {
        ResultPaging<NodeRepresentation> assocs = ecmClientProxySession.getAlfrescoClient().getNodesAPI().listTargetAssociationsCall(registryId, null, new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)), new FieldsParam(Arrays.asList(APIConstants.PROPERTIES_VALUE, "id", "nodeType")));
        if (assocs != null && assocs.getObjects() != null) {
            for (NodeRepresentation assoc : assocs.getObjects()) {
                Map<String, Object> assocProperties = assoc.getProperties();
                if (assoc.getNodeType().equalsIgnoreCase(EcmConstants.TYPE_FI_TYPE)) {
                    return FiTypeModelHelper.getModel(assoc.getProperties(), assoc.getId());
                }
            }
        }
        return null;
    }

    @Override
    public List<FiTypeMetaModel> loadTypes() {

        String fiTypesRootFolderPath = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.FI_TYPE_ROOT_FOLDER_PATH_KEY);
        ResultPaging<NodeRepresentation> fiTypeNodes = ecmClientProxySession.getAlfrescoClient().getNodesAPI().listNodeChildrenCall(APIConstants.FOLDER_ROOT,
                0, Integer.MAX_VALUE, new OrderByParam(Collections.singletonList("createdAt")), "(nodeType='" + EcmConstants.TYPE_FI_TYPE + "')",
                new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)), fiTypesRootFolderPath, null, null);

        return FiTypeModelHelper.getModels(fiTypeNodes.getObjects());
    }
}
