package net.fina.first.ecm.dcs.proxy;

import net.fina.common.client.exception.FinATypeException;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.ecm.alfresco.api.common.constant.APIConstants;
import net.fina.ecm.alfresco.api.common.parameters.FieldsParam;
import net.fina.ecm.alfresco.api.common.parameters.IncludeParam;
import net.fina.ecm.alfresco.api.core.model.body.DownloadBodyCreate;
import net.fina.ecm.alfresco.api.core.model.body.PersonBodyUpdate;
import net.fina.ecm.alfresco.api.core.model.representation.DownloadRepresentation;
import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import net.fina.ecm.alfresco.api.search.body.QueryBody;
import net.fina.ecm.alfresco.api.search.body.RequestQuery;
import net.fina.ecm.alfresco.api.search.model.ResultNodeRepresentation;
import net.fina.ecm.alfresco.api.search.model.ResultSetRepresentation;
import net.fina.first.FirstUtil;
import net.fina.first.common.exception.NodeException;
import net.fina.first.common.exception.WorkflowProcessException;
import net.fina.first.ecm.EcmConstants;
import net.fina.first.ecm.client.proxy.EcmClientProxySession;
import net.fina.first.ecm.config.api.FirstConfigLocal;
import net.fina.first.ecm.dictionary.api.DictionaryLocal;
import net.fina.first.ecm.dictionary.model.ClassPropertyMetaModel;
import net.fina.first.ecm.fi.api.FiDocumentRequestLocal;
import net.fina.first.ecm.fi.api.FiLocalEcm;
import net.fina.first.ecm.fi.api.FiTypeLocal;
import net.fina.first.ecm.fi.model.FiDocumentRequestMetaModel;
import net.fina.first.ecm.fi.model.FiDocumentRequestModelHelper;
import net.fina.first.ecm.fi.model.FiTypeMetaModel;
import net.fina.first.ecm.node.api.NodeLocal;
import net.fina.first.ecm.node.model.NodeMetaModel;
import net.fina.first.ecm.node.model.NodeModelHelper;
import net.fina.first.ecm.people.api.PeopleLocal;
import net.fina.first.ecm.workflow.api.WorkflowLocal;
import net.fina.first.ecm.workflow.model.WorkflowProcessExternalMetaModel;
import net.fina.first.interceptors.FirstRecordingAuditor;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.core.Response;
import java.util.*;

//@RolesAllowed({PermissionIdNames.FIRST_REVIEW, PermissionIdNames.DCS_FI_PROFILE_REVIEW})
//@SecurityDomain("DCSSecurityDomain")
@Interceptors(FirstRecordingAuditor.class)
@Stateless
public class DcsEcmProxySession {

    @Inject
    private NodeLocal nodeProxySession;

    @Inject
    private DictionaryLocal dictionaryProxySession;

    @Inject
    private FiLocalEcm fiProxySession;

    @Inject
    private FiTypeLocal fiTypeProxySession;

    @Inject
    private WorkflowLocal workflowProxySession;

    @Inject
    private FirstConfigLocal firstConfigProxySession;

    @Inject
    private PeopleLocal peopleLocal;

    @Inject
    private FiDocumentRequestLocal fiDocumentRequestLocal;

    @Inject
    private EcmClientProxySession ecmClientProxySession;


    public NodeMetaModel getFullNodeById(String acceptLanguage, String nodeId, String relativePath) {
        return nodeProxySession.getFullNodeById(acceptLanguage, nodeId, relativePath);
    }

    public PaginatedListWrapper<NodeMetaModel> getNodeChildren(String acceptLanguage, String nodeId, Integer start, Integer limit, String filter, String where, String relativePath, String includeSource, FieldsParam fields) {
        return nodeProxySession.getNodeChildren(acceptLanguage, nodeId, start, limit, filter, null, where, new IncludeParam(Arrays.asList("properties")), relativePath, includeSource, null);
    }
    //TODO
    public NodeMetaModel createNodeFromPropertiesChecked(String nodeType, TreeMap<String, Object> properties, String relativePath) throws NodeException {
        return nodeProxySession.createNodeFromPropertiesChecked(nodeType, properties, relativePath);
    }
    //TODO
    public NodeMetaModel updateNodeFromProperties(String nodeId, TreeMap<String, Object> properties, String relativePath) throws NodeException {
        return nodeProxySession.updateNodeFromProperties(nodeId, properties, relativePath);
    }

    public void deleteNodeChecked(String nodeId, String relativePath) throws NodeException {
        nodeProxySession.deleteNodeChecked(nodeId, relativePath);
    }

    public PaginatedListWrapper<NodeMetaModel> loadFiDetails(String acceptLanguage, String folderId, String parentId, String filter) {
        return fiProxySession.loadFiDetails(acceptLanguage, folderId, parentId, null, filter, 0, 0);
    }


    public PaginatedListWrapper<NodeMetaModel> loadComplexStructureBeneficiaries(String fiRegistryId) {
        return fiProxySession.loadComplexStructureBeneficiaries(fiRegistryId);
    }

    public List<ClassPropertyMetaModel> getClassProperties(String acceptLanguage, String className) {
        return dictionaryProxySession.getClassProperties(acceptLanguage, className);
    }

    public FiTypeMetaModel getFiTypeById(String registryId) {
        return fiTypeProxySession.getFiTypeById(registryId);
    }

    public PaginatedListWrapper<WorkflowProcessExternalMetaModel> getWorkflowProcessesExternal(String acceptLanguage, NodeMetaModel actionNode, String userFiCode, Integer start, Integer limit) {
        List<WorkflowProcessExternalMetaModel> userProcesses = new ArrayList<>();

        if (userFiCode != null) {
            PaginatedListWrapper<WorkflowProcessExternalMetaModel> allTasksPaginated = workflowProxySession.getWorkflowProcessesExternal(acceptLanguage, actionNode, null, null);

            List<WorkflowProcessExternalMetaModel> allTasks = allTasksPaginated.getList();
            if (allTasks != null && !allTasks.isEmpty()) {
                for (WorkflowProcessExternalMetaModel model : allTasks) {
                    Map<String, Object> processVariables = workflowProxySession.getWorkflowVariables(acceptLanguage, model.getProcessDefinitionId());
                    String processFiCode = FirstUtil.getValue(processVariables.get("fwf_fiStartTaskBaseFiCode"), String.class);
                    if (userFiCode.equals(processFiCode)) {
                        userProcesses.add(model);
                    }
                }
            }
        }

        PaginatedListWrapper<WorkflowProcessExternalMetaModel> result = new PaginatedListWrapper<>();
        result.setList(FirstUtil.getPaginatedList(userProcesses, start, limit));
        result.setTotalResults(userProcesses.size());

        return result;
    }

    public NodeMetaModel submitProcessActionExternal(String actionId) throws NodeException {
        return nodeProxySession.submitProcessActionExternal(actionId);
    }

    public WorkflowProcessExternalMetaModel startBranchCreateWorkflowExternal(String acceptLanguage, String fiRegistryNodeId) throws WorkflowProcessException {
        return workflowProxySession.startBranchCreateWorkflowExternal(acceptLanguage, fiRegistryNodeId);
    }

    public WorkflowProcessExternalMetaModel startBranchEditWorkflowExternal(String acceptLanguage, String fiRegistryNodeId) throws WorkflowProcessException {
        return workflowProxySession.startBranchEditWorkflowExternal(acceptLanguage, fiRegistryNodeId);
    }

    public String getProperty(String key) {
        return firstConfigProxySession.getProperty(key);
    }

    public void changePassword(String login, String password) throws FinATypeException {
        peopleLocal.updatePerson(new PersonBodyUpdate(login, null, null, null, null, password));
    }

    public NodeRepresentation uploadFile(String nodeId, String fileName, MultipartFormDataInput multipartForm) {
        return nodeProxySession.uploadFile(nodeId, fileName, multipartForm);
    }

    public PaginatedListWrapper<FiDocumentRequestMetaModel> loadFiDocumentRequests(String acceptLanguage, String fiRegistryNodeId, int page, int start, int limit) {
        return fiDocumentRequestLocal.loadFiDocumentRequestsByCode(acceptLanguage, fiRegistryNodeId, page, start, limit);
    }

    public DownloadRepresentation createTemplatesDownload(DownloadBodyCreate downloadBodyCreate, String documentRequestId, String fiRegistryCode) {
        NodeRepresentation documentRequestNode = nodeProxySession.getNodeById(documentRequestId, new IncludeParam(Collections.singletonList("properties")).toString(), null, null);
        if (FirstUtil.getValue(documentRequestNode.getProperties().get(EcmConstants.FI_DOCUMENT_REQUEST_PROP_ASSIGNEE_FI_CODE), String.class).equals(fiRegistryCode)) {
            NodeRepresentation templatesNode = nodeProxySession.getNodeById(documentRequestId, null, "Templates", null);
            downloadBodyCreate.setNodeIds(Collections.singletonList(templatesNode.getId()));
            return ecmClientProxySession.getAlfrescoClient().getDownloadAPI().createDownloadRepresentation(downloadBodyCreate);
        }
        throw new ForbiddenException("Invocation Not Allowed");
    }

    public DownloadRepresentation getDownloadInfo(String downloadId) {
        return ecmClientProxySession.getAlfrescoClient().getDownloadAPI().getDownloadRepresentation(downloadId);
    }

    public Response getNodeContent(String nodeId, Boolean attachment) {
        return ecmClientProxySession.getAlfrescoClient().getNodesAPI().getNodeContent(nodeId, attachment);
    }

    public FiDocumentRequestMetaModel updateDocumentRequestStatus(String nodeId) throws NodeException {
        NodeRepresentation documentRequestNode = nodeProxySession.getNodeById(nodeId, new IncludeParam(Collections.singletonList("properties")).toString(), null, null);
        FiDocumentRequestMetaModel model = FiDocumentRequestModelHelper.getModel(documentRequestNode, getFiRegistryIdNameMap(NodeModelHelper.getMetaModel(documentRequestNode)));
        model.setSubmitted(true);
        model.setSubmissionDate(new Date());
        return fiDocumentRequestLocal.updateFiDocumentRequest(nodeId, model);
    }

    private Map<String, String> getFiRegistryIdNameMap(NodeMetaModel fiDocumentRequestNode) {
        Map<String, String> fiIdNameMap = new HashMap<>();
        if (fiDocumentRequestNode != null) {
            String fiRegistryNodeId = FirstUtil.getValue(fiDocumentRequestNode.getProperties().get(EcmConstants.FI_DOCUMENT_REQUEST_PROP_FI_ID), String.class);
            if (fiIdNameMap.get(fiRegistryNodeId) == null) {
                NodeMetaModel fiNodeMetaModel = nodeProxySession.getNodeById(fiRegistryNodeId, new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)).toString(), null);
                String fiRegistryName = FirstUtil.getValue(fiNodeMetaModel.getProperties().get(EcmConstants.REGISTRY_PROP_NAME), String.class);
                fiIdNameMap.put(fiRegistryNodeId, fiRegistryName);
            }
        }
        return fiIdNameMap;
    }

    public String getCurrentFiRegistryPath(String fiCode) {
        String searchStr = "select * from fina:fiRegistry where  CONTAINS('fina:fiRegistryCode:*-" + fiCode + "')";
        RequestQuery query = new RequestQuery().query(searchStr).language(RequestQuery.LanguageEnum.CMIS);
        QueryBody queryBody = new QueryBody().query(query).include(Arrays.asList("properties", "path", "association"));

        ResultSetRepresentation<ResultNodeRepresentation> resultSearch = ecmClientProxySession.getAlfrescoClient().getSearchAPI().search(queryBody);

        if (!resultSearch.getObjects().isEmpty()) {
            return resultSearch.getObjects().get(0).getName();
        }
        return null;
    }
}
