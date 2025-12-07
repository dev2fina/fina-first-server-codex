package net.fina.first.ecm.fi.proxy;

import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.common.shared.ecm.model.ECMPersonMetaModel;
import net.fina.ecm.alfresco.AlfrescoClient;
import net.fina.ecm.alfresco.api.common.parameters.FieldsParam;
import net.fina.ecm.alfresco.api.common.parameters.IncludeParam;
import net.fina.ecm.alfresco.api.common.parameters.OrderByParam;
import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import net.fina.ecm.alfresco.api.search.model.ResultNodeRepresentation;
import net.fina.first.common.exception.NodeException;
import net.fina.first.ecm.fi.api.FiLocalEcm;
import net.fina.first.ecm.fi.model.*;
import net.fina.first.ecm.node.model.NodeMetaModel;
import net.fina.first.ecm.registry.model.FiRegistryMetaModel;
import org.jboss.ejb3.annotation.SecurityDomain;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.List;
import java.util.TreeMap;

@Stateless
@RolesAllowed({PermissionIdNames.FIRST_REVIEW})
@SecurityDomain("FinASecurityDomain")
public class FiProxySession {

    @Inject
    private FiLocalEcm fiLocal;

    public PaginatedListWrapper<FiRegistryMetaModel> loadFiRegistry(String acceptLanguage, String query, int page, int start, int limit, FiRegistryFilterModel filter, String sort, String group) {
        return fiLocal.loadFiRegistry(acceptLanguage, query, page, start, limit, filter, sort, group);
    }

    public PaginatedListWrapper<NodeMetaModel> loadFiRegistryAssociations(String acceptLanguage, String fiRegistryId) {
        return fiLocal.loadFiRegistryAssociations(acceptLanguage, fiRegistryId);
    }

    public PaginatedListWrapper<NodeMetaModel> loadFiDetails(String acceptLanguage, String folderId, String parentId,
                                                             String query, String filter, int start, int pageSize) {
        return fiLocal.loadFiDetails(acceptLanguage, folderId, parentId, query, filter, start, pageSize);
    }

    public PaginatedListWrapper<NodeMetaModel> loadFiActions(String acceptLanguage, Integer start, Integer pageSize, String fiRegistryId) {
        return fiLocal.loadFiActions(acceptLanguage, start, pageSize, fiRegistryId);
    }

    public NodeMetaModel getFi(String acceptLanguage, String id) {
        return fiLocal.getFi(acceptLanguage, id);
    }

    public NodeMetaModel saveProperties(String acceptLanguage, String id, TreeMap<String, Object> properties) throws NodeException {
        return fiLocal.saveProperties(acceptLanguage, id, properties);
    }

    public PaginatedListWrapper<NodeMetaModel> saveFiDetail(String acceptLanguage, String id, TreeMap<String, Object> properties) throws NodeException {
        return fiLocal.saveFiDetail(acceptLanguage, id, properties);
    }

    public PaginatedListWrapper<NodeMetaModel> createFiDetail(String acceptLanguage, String fiRegistryId, String parentId, String nodeType, TreeMap<String, Object> properties) throws NodeException {
        return fiLocal.createFiDetail(acceptLanguage, fiRegistryId, parentId, nodeType, properties);
    }

    public void removeFiDetail(String acceptLanguage, String itemId) {
        fiLocal.removeFiDetail(acceptLanguage, itemId);
    }

    public String getFiRegistryStatus(String acceptLanguage, String fiRegistryId) {
        return fiLocal.getFiRegistryStatus(acceptLanguage, fiRegistryId);
    }

    public byte[] getFiExportContent(String acceptLanguage, FiRegistryFilterModel filter) throws Throwable {
        return fiLocal.getFiExportContent(acceptLanguage, filter);
    }

    public PaginatedListWrapper<ResultNodeRepresentation> getFiRegistryDocument(String processId, String documentType, String actionId) {
        return fiLocal.getFiRegistryDocument(processId, documentType, actionId);
    }

    public String filterToQuery(FiRegistryFilterModel filter) {
        return fiLocal.filterToQuery(filter);
    }

    public void syncGapsFromQuestionnaires(String acceptLanguage, String fiRegistryId, String registryActionId) throws NodeException {
        fiLocal.syncGapsFromQuestionnaires(acceptLanguage, fiRegistryId, registryActionId);
    }

    public NodeMetaModel generateDocument(FiDocumentParameterModel parameterModel) throws NodeException {
        return fiLocal.generateDocument(parameterModel);
    }

    public PaginatedListWrapper<NodeRepresentation> loadBranchDecreeDocuments(String fiRegistryId, boolean headOfficeOnly, boolean showActiveBranchesOnly) {
        return fiLocal.loadBranchDecreeDocuments(fiRegistryId, headOfficeOnly, showActiveBranchesOnly);
    }

    public NodeMetaModel generateDecreeDocumentsForBranch(FiDocumentParameterModel parameterModel) throws NodeException {
        return fiLocal.generateDecreeDocumentsForBranch(parameterModel);
    }

    public PaginatedListWrapper<NodeMetaModel> getFiRegistryDocumentChildrenWithClassProperties(String acceptLanguage, String nodeId, String fiRegistryId, int page, Integer start, Integer limit, OrderByParam orderBy, String where, IncludeParam include, String relativePath, String includeSource, FieldsParam fields) {
        return fiLocal.getFiRegistryDocumentChildrenWithClassProperties(acceptLanguage, nodeId, fiRegistryId, page, start, limit, orderBy, where, include, relativePath, includeSource, fields);
    }

    public PaginatedListWrapper<NodeMetaModel> filterFiDetails(AlfrescoClient client, String folderId, NodeRepresentation folderNode, String filterJson, int start, int pageSize) {
        return fiLocal.filterFiDetails(client, folderId, folderNode, filterJson, start, pageSize);
    }

    public PaginatedListWrapper<NodeMetaModel> loadComplexStructureBeneficiaries(String fiRegistryId) {
        return fiLocal.loadComplexStructureBeneficiaries(fiRegistryId);
    }

    public PaginatedListWrapper<NodeRepresentation> loadActionLiquidatorObjects(String registryActionId, int start, int limit) {
        return fiLocal.loadActionLiquidatorObjects(registryActionId, start, limit);
    }

    public PaginatedListWrapper<NodeRepresentation> loadBranchChangedObjects(String registryActionId, String relativePath, int start, int limit) {
        return fiLocal.loadBranchChangedObjects(registryActionId, relativePath, start, limit);
    }

    public FiRegistryActionCancelResultMetaModel cancelOrRevertCurrentFiRegistryAction(String acceptLanguage, String fiRegistryId, boolean isCancellation) throws NodeException {
        return fiLocal.cancelOrRevertCurrentFiRegistryAction(acceptLanguage, fiRegistryId, isCancellation);
    }

    public void finishBranchChangeAction(String acceptLanguage, String registryActionId, String relativePath, String gapCorrectionDeadline, String gapCorrectionDeadlineDays) throws NodeException {
        fiLocal.finishBranchChangeAction(acceptLanguage, registryActionId, relativePath, gapCorrectionDeadline, gapCorrectionDeadlineDays);
    }

    public PaginatedListWrapper<NodeRepresentation> loadBranchChangeGaps(String registryActionId, String changesRelativePath, int start, int limit) {
        return fiLocal.loadBranchChangeGaps(registryActionId, changesRelativePath, start, limit);
    }

    public void restartBranchChangeAction(String registryActionId, String relativePath) {
        fiLocal.restartBranchChangeAction(registryActionId, relativePath);
    }

    public List<NodeRepresentation> loadReportTemplates(List<String> tags) {
        return fiLocal.loadReportTemplates(tags);
    }

    public List<FiRegistryMetaModel> loadFiRegistryByType(String fiTypeNodeId) {
        return fiLocal.loadFiRegistryByType(fiTypeNodeId);
    }

    public PaginatedListWrapper<FiRegistryMetaModel> searchFi(String query, int page, int start, int limit) {
        return fiLocal.searchFi(query, page, start, limit);
    }

    public String restoreFiRegistryGapActionTask(String acceptLanguage, String fiRegistryId, String fiRegistryGapDetailNodeId) throws NodeException {
        return fiLocal.restoreFiRegistryGapActionTask(acceptLanguage, fiRegistryId, fiRegistryGapDetailNodeId);
    }

    public NodeMetaModel deleteFiRegistryGapActionDetail(String acceptLanguage, String fiRegistryGapDetailId, String comment) throws NodeException {
        return fiLocal.deleteFiRegistryGapActionDetail(acceptLanguage, fiRegistryGapDetailId, comment);
    }

    public ECMPersonMetaModel sendToController(String acceptLanguage, String fiRegistryId, String groupId) throws Exception {
        return fiLocal.sendToController(acceptLanguage, fiRegistryId, groupId);
    }

    public NodeMetaModel getRegionByName(String name) {
        return fiLocal.getRegionByName(name);
    }

    public NodeMetaModel getCityByName(String name) {
        return fiLocal.getCityByName(name);
    }

    public void finishFiProcess(String acceptLanguage, String fiRegistryId, FiFinishMetaModel finish) throws NodeException {
        fiLocal.finishFiProcess(acceptLanguage, fiRegistryId, FiFinishModelHelper.getModel(finish));
    }

    public NodeMetaModel getFiHeadOffice(String fiRegistryId) {
        return fiLocal.getFiHeadOffice(fiRegistryId);
    }

    public void syncQuestionnaireBasedOnGaps(String acceptLanguage, String fiRegistryId) throws NodeException {
        fiLocal.syncQuestionnaireBasedOnGaps(acceptLanguage, fiRegistryId);
    }

    public ECMPersonMetaModel changeController(String acceptLanguage, String fiRegistryId, String groupId, String newControllerId) throws Exception {
        return fiLocal.changeController(acceptLanguage, fiRegistryId, groupId, newControllerId);
    }
}
