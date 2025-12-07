package net.fina.first.ecm.fi.api;

import net.fina.common.shared.PaginatedListWrapper;
import net.fina.common.shared.ecm.model.ECMPersonMetaModel;
import net.fina.ecm.alfresco.AlfrescoClient;
import net.fina.ecm.alfresco.api.common.parameters.FieldsParam;
import net.fina.ecm.alfresco.api.common.parameters.IncludeParam;
import net.fina.ecm.alfresco.api.common.parameters.OrderByParam;
import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import net.fina.ecm.alfresco.api.search.model.ResultNodeRepresentation;
import net.fina.first.common.exception.NodeException;
import net.fina.first.ecm.fi.model.FiDocumentParameterModel;
import net.fina.first.ecm.fi.model.FiFinishModel;
import net.fina.first.ecm.fi.model.FiRegistryActionCancelResultMetaModel;
import net.fina.first.ecm.fi.model.FiRegistryFilterModel;
import net.fina.first.ecm.node.model.NodeMetaModel;
import net.fina.first.ecm.registry.model.FiRegistryMetaModel;

import java.util.List;
import java.util.TreeMap;

public interface FiLocalEcm {
    PaginatedListWrapper<FiRegistryMetaModel> loadFiRegistry(String acceptLanguage, String query, int page, int start, int limit, FiRegistryFilterModel filter, String sort, String group);

    PaginatedListWrapper<NodeMetaModel> loadFiRegistryAssociations(String acceptLanguage, String fiRegistryId);

    PaginatedListWrapper<NodeMetaModel> loadFiDetails(String acceptLanguage, String folderId, String parentId, String query, String filter, int start, int pageSize);

    PaginatedListWrapper<NodeMetaModel> loadFiActions(String acceptLanguage, Integer start, Integer pageSize, String fiRegistryId);

    NodeMetaModel getFi(String acceptLanguage, String id);

    NodeMetaModel saveProperties(String acceptLanguage, String id, TreeMap<String, Object> properties) throws NodeException;

    PaginatedListWrapper<NodeMetaModel> saveFiDetail(String acceptLanguage, String id, TreeMap<String, Object> properties) throws NodeException;

    PaginatedListWrapper<NodeMetaModel> createFiDetail(String acceptLanguage, String fiRegistryId, String parentId, String nodeType, TreeMap<String, Object> properties) throws NodeException;

    void removeFiDetail(String acceptLanguage, String itemId);

    String getFiRegistryStatus(String acceptLanguage, String fiRegistryId);

    byte[] getFiExportContent(String acceptLanguage, FiRegistryFilterModel filter) throws Throwable;

    PaginatedListWrapper<ResultNodeRepresentation> getFiRegistryDocument(String processId, String documentType, String actionId);

    String filterToQuery(FiRegistryFilterModel filter);

    void syncGapsFromQuestionnaires(String acceptLanguage, String fiRegistryId, String registryActionId) throws NodeException;

    NodeMetaModel generateDocument(FiDocumentParameterModel parameterModel) throws NodeException;

    PaginatedListWrapper<NodeRepresentation> loadBranchDecreeDocuments(String fiRegistryId, boolean headOfficeOnly, boolean showActiveBranchesOnly);

    NodeMetaModel generateDecreeDocumentsForBranch(FiDocumentParameterModel parameterModel) throws NodeException;

    PaginatedListWrapper<NodeMetaModel> getFiRegistryDocumentChildrenWithClassProperties(String acceptLanguage, String nodeId, String fiRegistryId, int page, Integer start, Integer limit, OrderByParam orderBy, String where, IncludeParam include, String relativePath, String includeSource, FieldsParam fields);

    PaginatedListWrapper<NodeMetaModel> filterFiDetails(AlfrescoClient client, String folderId, NodeRepresentation folderNode, String filterJson, int start, int pageSize);

    PaginatedListWrapper<NodeMetaModel> loadComplexStructureBeneficiaries(String fiRegistryId);

    PaginatedListWrapper<NodeRepresentation> loadActionLiquidatorObjects(String registryActionId, int start, int limit);

    PaginatedListWrapper<NodeRepresentation> loadBranchChangedObjects(String registryActionId, String relativePath, int start, int limit);

    FiRegistryActionCancelResultMetaModel cancelOrRevertCurrentFiRegistryAction(String acceptLanguage, String fiRegistryId, boolean isCancellation) throws NodeException;

    void finishBranchChangeAction(String acceptLanguage, String registryActionId, String relativePath, String gapCorrectionDeadline, String gapCorrectionDeadlineDays) throws NodeException;

    PaginatedListWrapper<NodeRepresentation> loadBranchChangeGaps(String registryActionId, String changesRelativePath, int start, int limit);

    void restartBranchChangeAction(String registryActionId, String relativePath);

    List<NodeRepresentation> loadReportTemplates(List<String> tags);

    List<FiRegistryMetaModel> loadFiRegistryByType(String fiTypeNodeId);

    PaginatedListWrapper<FiRegistryMetaModel> searchFi(String queryText, int page, int start, int limit);

    String restoreFiRegistryGapActionTask(String acceptLanguage, String fiRegistryId, String fiRegistryGapDetailNodeId) throws NodeException;

    NodeMetaModel deleteFiRegistryGapActionDetail(String acceptLanguage, String fiRegistryGapDetailId, String comment) throws NodeException;

    ECMPersonMetaModel sendToController(String acceptLanguage, String fiRegistryId, String groupId) throws Exception;

    NodeMetaModel getRegionByName(String name);

    NodeMetaModel getCityByName(String name);

    void finishFiProcess(String acceptLanguage, String fiRegistryId, FiFinishModel finishModel) throws NodeException;

    NodeMetaModel getFiHeadOffice(String fiRegistryId);

    void syncQuestionnaireBasedOnGaps(String acceptLanguage, String fiRegistryId) throws NodeException;

    List<NodeMetaModel> loadFiBranchesSortedByTypeInOneYear(String fiType);

    List<NodeMetaModel> loadFiBranchesByType(String type, String branchNodeTypeAlfrescoProperty);

    ECMPersonMetaModel changeController(String acceptLanguage, String fiRegistryId, String groupId, String newControllerId) throws Exception;

    NodeRepresentation getFiRepresentation(String fiRegistryId);
}
