package net.fina.server.processing.impl;

import jakarta.ejb.*;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import net.fina.common.client.mdt.MDTNodeTypes;
import net.fina.common.client.returns.CanProcessResult;
import net.fina.common.client.returns.ProcessResult;
import net.fina.common.client.returns.ProcessStatus;
import net.fina.common.client.returns.ReturnModel;
import net.fina.common.server.StatisticsLogger;
import net.fina.common.shared.FilterConfig;
import net.fina.common.shared.FilterConfigKey;
import net.fina.server.classifier.api.MDTCatalogLocal;
import net.fina.server.classifier.entity.MDTCatalog;
import net.fina.server.classifier.model.MDTCatalogMetaModel;
import net.fina.server.i18n.entity.Language;
import net.fina.server.interceptors.LogDescription;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.mdt.api.MDTNodeLocal;
import net.fina.server.mdt.entity.MDTComparison;
import net.fina.server.mdt.entity.MDTDependentNode;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.processing.ProcessException;
import net.fina.server.processing.ProcessingBase;
import net.fina.server.processing.ProcessingFactory;
import net.fina.server.processing.ProcessingUtil;
import net.fina.server.processing.api.FinaCrossFileValidator;
import net.fina.server.processing.api.ProcessingLocal;
import net.fina.server.processing.api.ProcessingStoreLocal;
import net.fina.server.processing.dependency.DependencyItem;
import net.fina.server.processing.dependency.DependencyItemTopologicalSort;
import net.fina.server.processing.model.ComparisonItem;
import net.fina.server.processing.model.ProcessItem;
import net.fina.server.processing.model.ProcessReturnModel;
import net.fina.server.returns.api.ReturnDefinitionLocal;
import net.fina.server.returns.api.ReturnLocal;
import net.fina.server.returns.entity.ReturnDefinition;
import net.fina.server.returns.entity.ReturnStatus;
import net.fina.server.security.api.UserLocal;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.logging.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Stateless
@Local(ProcessingLocal.class)
@Interceptors(RecordingAuditor.class)
public class ProcessingSession implements ProcessingLocal {

    private final Logger log = Logger.getLogger(getClass());

    @EJB
    private ProcessingStoreLocal processingStoreLocal;
    @EJB
    private ReturnLockSingleton returnCreateLock;
    @EJB
    private ReturnLocal returnLocal;
    @EJB
    private ReturnDefinitionLocal definitionLocal;
    @EJB
    private UserLocal userLocal;

    @Inject
    private MDTCatalogLocal catalogLocal;
    @Inject
    private FinaCrossFileValidator finaCrossFileValidator;
    @Inject
    private MDTNodeLocal mdtNodeLocal;

    @Override
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 3)
    public Map<Long, ProcessResult> processPackage(long userId, long langId, ReturnModel filterModel) throws net.fina.server.processing.ProcessException {
        FilterConfig filterConfig = new FilterConfig();
        if (filterModel.getDefinitionId() != 0)
            filterConfig.setFilterParam(FilterConfigKey.RETURN_DEFINITION_ID, filterModel.getDefinitionId());
        filterConfig.setFilterParam(FilterConfigKey.PERIOD_FROM_DATE, filterModel.getFromDate());
        filterConfig.setFilterParam(FilterConfigKey.PERIOD_TO_DATE, filterModel.getToDate());
        if (filterModel.getVersionId() != 0)
            filterConfig.setFilterParam(FilterConfigKey.RETURN_VERSION_ID, filterModel.getVersionId());
        if (filterModel.getStatus() != null)
            filterConfig.setFilterParam(FilterConfigKey.RETURN_STATUS, filterModel.getStatus());
        if (filterModel.getReturnTypeId() != 0)
            filterConfig.setFilterParam(FilterConfigKey.RETURN_TYPE_ID, filterModel.getReturnTypeId());

        List<Long> fi = new ArrayList<>();
        fi.add(filterModel.getFiId());

        filterConfig.setFilterParam(FilterConfigKey.FI_ID_LIST, fi);
        List<Long> ids = new ArrayList<>();

        List<Map<String, Object>> returns = returnLocal.laodPackageReturns(filterModel.getFiId(), filterModel.getPeriodId(), filterModel.getReturnTypeId(), filterModel.getVersionId(), filterConfig);
        for (Map<String, Object> singleReturn : returns) {
            ids.add((long) singleReturn.get("id"));
        }
        List<MDTComparison> mdtComparisons = mdtNodeLocal.loadComparisons(new HashMap<>(), null);

        return process(userId, langId, true, mdtComparisons, ids.toArray(new Long[0]));
    }

    @Override
    @LogDescription(logMethodParameters = false)
    public Map<Long, ProcessItem> loadReturnNodesValue(long returnId, long versionId, List<MDTDependentNode> mdtDependentNodes, long langId, Map<Long, List<MDTNode>> allMdtNodesByParentId) {
        return processingStoreLocal.loadReturnNodesValue(Collections.singletonList(returnId), versionId, mdtDependentNodes, langId, allMdtNodesByParentId);
    }

    @Override
    public void updateProcessItems(Collection<ProcessItem> values, String processId) {
        processingStoreLocal.updateProcessItems(values, processId);
    }

    @Override
    public void updateProcessItems(Collection<ProcessItem> values, long returnId, List<MDTDependentNode> mdtDependentNodes, final Map<Long, List<MDTNode>> allMdtNodesByParentId, final List<MDTComparison> comparisons) {
        processingStoreLocal.updateProcessItems(values, returnId, null, mdtDependentNodes, allMdtNodesByParentId, comparisons);
    }

    @Override

    public CanProcessResult canProcess(long scheduleId, long langId, String versionCode) {
        Map<String, String> codeNames = new ConcurrentHashMap<>();
        Map<String, Integer> returnNodes = new ConcurrentHashMap<>();

        Map<Long, List<MDTNode>> allMDtNodes = processingStoreLocal.loadMdtNodesByParentId();

        //Load schedules return definition tables nodes.
        List<Long> scheduleReturnDefinitionTableNodeId = processingStoreLocal.getScheduleReturnDefinitionTableNodeIds(scheduleId);

        List<Long> nodes = new ArrayList<>();
        for (Long nodeId : scheduleReturnDefinitionTableNodeId) {
            selectChildNodes(allMDtNodes, nodes, returnNodes, nodeId);
        }

        List<MDTNode> mdtNodes = processingStoreLocal.getDependentReturnsNotExistingNodes(nodes, scheduleId, versionCode);
        for (MDTNode node : mdtNodes) {
            codeNames.put(node.getCode(), node.getDescription().getDescription(langId));
        }

        //Result
        CanProcessResult canProcessResult = new CanProcessResult();
        canProcessResult.setExpectedCodeNames(codeNames);
        canProcessResult.setReturnNodes(returnNodes);
        return canProcessResult;
    }

    private void selectChildNodes(Map<Long, List<MDTNode>> allMdtNodes, List<Long> nodes, Map<String, Integer> nodesMap, long parentId) {
        List<MDTNode> children = allMdtNodes.get(parentId);
        if (children != null) {
            for (MDTNode mdtNode : children) {
                if (mdtNode.getType() == MDTNodeTypes.NODE) {
                    selectChildNodes(allMdtNodes, nodes, nodesMap, mdtNode.getId());
                } else {
                    nodesMap.put(mdtNode.getCode() == null ? mdtNode.getCode() : mdtNode.getCode().trim(), mdtNode.getType().ordinal());
                    nodes.add(mdtNode.getId());
                }
            }
        }
    }

    @Override
    public void changeReturnStatus(ReturnStatusChangeEvent event) {
        returnLocal.addReturnStatus(event.getReturnId(), event.getStatus(), event.getVersionId(), event.getUserId(), event.getNote(), event.getProcessId());
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void changeReturnStatusEventHandler(@Observes ReturnStatusChangeEvent event) {
        returnLocal.addReturnStatus(event.getReturnId(), event.getStatus(), event.getVersionId(), event.getUserId(), event.getNote(), event.getProcessId());
    }

    @Override
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 3)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Map<Long, ProcessResult> process(long userId, long langId, boolean reprocess, List<MDTComparison> mdtComparisons, Long... returnIds) throws net.fina.server.processing.ProcessException {
        log.info("Load Comparisons.");
        final Map<Long, List<ComparisonItem>> comparisons = processingStoreLocal.loadComparisons();

        log.info("Get all mdt dependent nodes");
        final List<MDTDependentNode> mdtDependentNodes = processingStoreLocal.loadAllMdtDependentNodes();
        log.info("Load all mdt nodes by parentId");
        final Map<Long, List<MDTNode>> allMdtNodesByParentId = processingStoreLocal.loadMdtNodesByParentId();

        Map<Long, ProcessResult> processResultMap = processSameTransaction(userId, langId, reprocess, null, mdtDependentNodes, allMdtNodesByParentId, comparisons, mdtComparisons, returnIds);

        long currUserId = userLocal.getCurrentUserId();
        currUserId = currUserId < 0 ? userId : currUserId;

        // validate cross file comparisons
        validateCrossFileComparisons(processResultMap,
                comparisons,
                currUserId,
                langId,
                mdtDependentNodes,
                allMdtNodesByParentId,
                returnIds);

        return processResultMap;
    }

    private void validateCrossFileComparisons(Map<Long, ProcessResult> processResultMap,
                                              final Map<Long, List<ComparisonItem>> comparisons,
                                              long userId,
                                              long langId,
                                              final List<MDTDependentNode> mdtDependentNodes,
                                              final Map<Long, List<MDTNode>> allMdtNodesByParentId,
                                              final Long... returnIds) {
        try {
            boolean hasErrors = processResultMap.values().stream().anyMatch(pr -> pr.getStatus().equals(ProcessStatus.STATUS_ERRORS));

            boolean hasCrossFileComparisons = processResultMap.values().stream().anyMatch(ProcessResult::validatePostProcessComparisons);

            if (!hasErrors && hasCrossFileComparisons) {
                Map<Long, ProcessItem> packageReturnItemsById = processingStoreLocal.loadReturnNodesValue(Arrays.asList(returnIds), detectVersionId(returnIds), mdtDependentNodes, langId, allMdtNodesByParentId);
                Collection<net.fina.server.returns.entity.Return> processedReturns = returnLocal.loadReturnsById(returnIds);

                //validate cross file comparisons
                final Language language = processingStoreLocal.getLanguage(langId);
                log.info("Validate Cross File Comparisons ");

                Map<Long, ProcessResult> result = finaCrossFileValidator.validatePostProcessComparisons(processedReturns, comparisons, packageReturnItemsById.values(), language);
                for (Map.Entry<Long, ProcessResult> entry : result.entrySet()) {
                    long returnId = entry.getKey();
                    ProcessResult pr = entry.getValue();
                    if (pr.getStatus().equals(ProcessStatus.STATUS_ERRORS)) {
                        processResultMap.put(returnId, pr);
                        String errorMessage = pr.getProcessNote();
                        ReturnStatusChangeEvent rStatusChangeEvent = new ReturnStatusChangeEvent(returnId, ProcessStatus.STATUS_ERRORS, processingStoreLocal.getVersionId(returnId), userId, errorMessage, null);
                        changeReturnStatus(rStatusChangeEvent);
                    }
                }
            }
        } catch (Exception t) {
            log.error(t.getMessage(), t);
        }
    }

    @Override
    public Map<Long, ProcessResult> processSameTransaction(long userId, long langId, boolean reprocess, Map<Long, ProcessItem> packageReturnItemsById, final List<MDTDependentNode> mdtDependentNodes, final Map<Long, List<MDTNode>> allMdtNodesByParentId, final Map<Long, List<ComparisonItem>> comparisons, List<MDTComparison> mdtComparisons, Long... returnIds) throws net.fina.server.processing.ProcessException {

        //Result map
        final Map<Long, ProcessResult> resultMap = new HashMap<>();

        if (returnIds != null && returnIds.length > 0) {
            try {

                final Language language = processingStoreLocal.getLanguage(langId);

                if (userId <= 0) {
                    userId = userLocal.getCurrentUserId();
                }

                final List<Long> orderedReturnIds;

                boolean processOne = false;
                if (reprocess) {
                    processOne = returnIds.length == 1;
                }

                //Stored returns
                List<Long> storedReturnIds = new ArrayList<>();

                if (processOne) {
                    //loads all dependent return ids
                    long returnId = returnIds[0];

                    List<Long> cachedDependentReturnIds = processingStoreLocal.getReturnDependencyTreeByReturnId(returnId);
                    List<Long> dependentReturnIds = cachedDependentReturnIds == null ? getAllDependentReturnIds(returnId, langId) : cachedDependentReturnIds;

                    orderedReturnIds = orderReturns(language, storedReturnIds, dependentReturnIds.toArray(new Long[0]));
                    processingStoreLocal.updateReturnDependencyTreeCache(returnId, orderedReturnIds);
                } else {
                    log.info("Order Return(s)");
                    orderedReturnIds = orderReturns(language, storedReturnIds, returnIds);
                }

                if (!processOne) {
                    if (packageReturnItemsById == null) {
                        log.info("Get package return items.");
                        packageReturnItemsById = processingStoreLocal.loadReturnNodesValue(orderedReturnIds, detectVersionId(returnIds), mdtDependentNodes, langId, allMdtNodesByParentId);
                    }
                    if (!storedReturnIds.isEmpty()) {
                        log.info("Get stored return items.");
                        packageReturnItemsById.putAll(processingStoreLocal.loadReturnNodesValue(storedReturnIds, detectVersionId(returnIds), mdtDependentNodes, langId, allMdtNodesByParentId));
                    }
                }

                int index = 1;
                for (long returnId : orderedReturnIds) {
                    processOne(resultMap, returnId, processOne, mdtDependentNodes, reprocess, userId, language, allMdtNodesByParentId, new ArrayList<>(orderedReturnIds), packageReturnItemsById, index++, orderedReturnIds.size(), comparisons, mdtComparisons);
                }
            } catch (ProcessException pe) {
                log.error(pe.getMessage(), pe);
                if (reprocess) {
                    for (long returnId : returnIds) {
                        changeReturnStatus(new ReturnStatusChangeEvent(returnId, ProcessStatus.STATUS_ERRORS, processingStoreLocal.getVersionId(returnId), userId, pe.getMessage(), null));
                        ProcessResult processResult = new ProcessResult();
                        processResult.setProcessNote(pe.getMessage());
                        processResult.setReturnId(returnId);
                        processResult.setStatus(ProcessStatus.STATUS_ERRORS);

                        resultMap.put(returnId, processResult);
                    }
                } else {
                    throw pe;
                }
            } catch (Exception t) {
                String message = t.getMessage();
                if (message == null) {
                    message = "Unexpected error. Please contact administrator.";
                }
                if (reprocess) {
                    for (long returnId : returnIds) {
                        changeReturnStatus(new ReturnStatusChangeEvent(returnId, ProcessStatus.STATUS_ERRORS, processingStoreLocal.getVersionId(returnId), userId, message, null));
                    }
                }

                for (long returnId : returnIds) {

                    ProcessResult processResult = new ProcessResult();
                    processResult.setProcessNote(message);
                    processResult.setReturnId(returnId);
                    processResult.setStatus(ProcessStatus.STATUS_ERRORS);

                    resultMap.put(returnId, processResult);
                }

                log.error(t.getMessage(), t);
            }
        }
        return resultMap;
    }

    private ProcessReturnModel processOne(final Map<Long, ProcessResult> resultMap,
                                          final long returnId,
                                          final boolean processOne,
                                          final List<MDTDependentNode> mdtDependentNodes,
                                          final boolean reprocess,
                                          final long userId,
                                          final Language language,
                                          final Map<Long, List<MDTNode>> allMdtNodesByParentId,
                                          List<Long> orderedReturnIds,
                                          Map<Long, ProcessItem> packageReturnItemsById,
                                          final int index,
                                          final int totalCount,
                                          final Map<Long, List<ComparisonItem>> comparisons,
                                          final List<MDTComparison> mdtComparisons
    ) {
        final ProcessReturnModel returnModel = processingStoreLocal.getReturnModel(returnId);

        try (StatisticsLogger statisticsLogger = new StatisticsLogger("Process return:" + returnId + ", " + index + "/" + totalCount, Logger.getLogger(getClass()), Logger.Level.INFO);) {
            statisticsLogger.logMessage("Get return model");

            //Lock return
            if (reprocess) {
                if (returnCreateLock.isLock(returnId)) {
                    throw new ConcurrentModificationException("Return is lock. return id:" + returnId);
                }
                returnCreateLock.lock(returnId);
            }

            statisticsLogger.logMessage("Check return status");
            if (checkReturnStatus(returnId)) {

                String processId = UUID.randomUUID().toString();
                statisticsLogger.logMessage("Process Id: " + processId);

                statisticsLogger.logStage("Load Package returnIds");
                final List<Long> packageReturnIds;
                if (processOne) {
                    packageReturnIds = loadDependedReturnIds(returnModel);
                    packageReturnIds.add(returnId);
                } else {
                    packageReturnIds = orderedReturnIds;
                }

                if (processOne) {
                    try (StatisticsLogger loadStatLog = new StatisticsLogger("Load Package return items.");) {
                        loadStatLog.logMessage("Return package ids:" + packageReturnIds);
                        loadStatLog.logStage("Start load...");
                        packageReturnItemsById = processingStoreLocal.loadReturnNodesValue(packageReturnIds, returnModel.getVersionId(), mdtDependentNodes, language.getId(), allMdtNodesByParentId);
                    }

                }
                final Map<Long, Map<Long, ProcessItem>> packageReturnItemsByReturnId = new HashMap<>();
                final Map<String, ProcessItem> packageReturnItemsByCode = new HashMap<>();
                Map<Long, MDTCatalogMetaModel> catalogMap = new HashMap<>();

                List<MDTCatalog> catalogList = catalogLocal.load(-1, -1, null);
                catalogList.forEach(c -> catalogMap.put(c.getCatalogNode().getId(), new MDTCatalogMetaModel(c.getId(), c.getCatalogNode().getDescription().getDescription(language.getId()), c.getCode())));

                for (ProcessItem pItem : packageReturnItemsById.values()) {
                    Map<Long, ProcessItem> returnItemsMap = packageReturnItemsByReturnId.get(pItem.returnId);
                    if (returnItemsMap == null) {
                        returnItemsMap = new HashMap<>();
                        packageReturnItemsByReturnId.put(pItem.returnId, returnItemsMap);
                    }
                    if (pItem.nodeType == MDTNodeTypes.LIST) {
                        if (pItem.equation == null || pItem.equation.isBlank()) {
                            String errorMessage = "List Element " + pItem.code + " is empty!";
                            log.error(errorMessage);
                            throw new ProcessException(errorMessage);
                        }
                        long listElementFolderId = Long.parseLong(pItem.equation.trim());
                        MDTCatalogMetaModel catalogMetaModel = catalogMap.get(listElementFolderId);
                        if (catalogMetaModel != null) {
                            pItem.setCatalog(catalogMetaModel);
                        }
                    }
                    returnItemsMap.put(pItem.nodeId, pItem);
                    packageReturnItemsByCode.put(pItem.code, pItem);
                }

                //Detect empty returns
                for (long retId : packageReturnIds) {
                    Map<Long, ProcessItem> processItemsMap = packageReturnItemsByReturnId.get(retId);
                    if (processItemsMap == null) {
                        processItemsMap = new HashMap<>();
                        packageReturnItemsByReturnId.put(retId, processItemsMap);
                    }
                }

                statisticsLogger.logStage("Create Processing Base");
                ProcessingErrorHandler processingErrorHandler = new ProcessingErrorHandler(returnModel, language.getCode());
                ProcessingUtil processingUtil = new ProcessingUtil(processingErrorHandler, language.getNumberFormat().trim(), language.getDateFormat().trim(), language.getDateTimeFormat().trim(), language.getId());
                ProcessingBase processing = ProcessingFactory.createRecursionProcessing(packageReturnItemsById, packageReturnItemsByCode, processingStoreLocal.getMdtNodesById(), processingUtil, reprocess, returnModel, this.processingStoreLocal);

                statisticsLogger.logStage("Process...");

                Map<Long, ProcessItem> processItemsMap = packageReturnItemsByReturnId.get(returnId);

                Collection<ProcessItem> processItems = processItemsMap.values();
                ProcessResult processResult = processing.process(processItems);
                resultMap.put(returnId, processResult);

                statisticsLogger.logStage("Rule Validation...");
                if (processResult.getStatus() == ProcessStatus.STATUS_PROCESSED) {
                    processItems = packageReturnItemsByReturnId.get(returnId).values();
                    processResult = processing.ruleValidation(processItems, comparisons);
                    resultMap.put(returnId, processResult);
                }

                statisticsLogger.logStage("Update return items.");

                processItems = packageReturnItemsByReturnId.get(returnId).values();

                processingStoreLocal.updateProcessItems(processItems, returnId, processId, mdtDependentNodes, allMdtNodesByParentId, mdtComparisons);

                statisticsLogger.logStage("Update Return Statuses");
                processResult.setReturnDefinitionCode(returnModel.getReturnDefinitionCode());
                processResult.setReturnId(returnModel.getId());

                changeReturnStatus(new ReturnStatusChangeEvent(returnId, processResult.getStatus(), returnModel.getVersionId(), userId, processResult.getProcessNote(), processId));

            }
        } catch (Throwable t) {
            String message = t.getMessage();
            if (message == null) {
                message = "Unexpected error. Please contact administrator.";
            }
            if (reprocess) {
                changeReturnStatus(new ReturnStatusChangeEvent(returnId, ProcessStatus.STATUS_ERRORS, returnModel.getVersionId(), userId, message, null));
            }

            ProcessResult processResult = new ProcessResult();
            processResult.setProcessNote(message);
            processResult.setReturnId(returnId);
            processResult.setStatus(ProcessStatus.STATUS_ERRORS);

            resultMap.put(returnId, processResult);

            log.error(t.getMessage(), t);
        } finally {
            //Unlock return process
            if (reprocess) {
                returnCreateLock.unlock(returnId);
            }
        }

        return returnModel;
    }

    private List<Long> orderReturns(Language language, List<Long> storedReturnIds, Long... returnIds) throws ProcessException {
        List<Long> returnIdsList = Arrays.asList(returnIds);

        Map<Long, Long> definitionIdReturnIdMap = processingStoreLocal.gerReturnReturnDefinitionIds(returnIdsList);

        List<DependencyItem> orderReturns = new ArrayList<>();

        for (Map.Entry<Long, Long> entry : definitionIdReturnIdMap.entrySet()) {
            Collection<ReturnDefinition> definitions = processingStoreLocal.loadReturnDefinitionDependencies().get(entry.getKey());
            List<Long> dependentIds = new ArrayList<>();
            for (ReturnDefinition returnDefinition : definitions) {
                Object dependentDefinition = definitionIdReturnIdMap.get(returnDefinition.getId());
                if (dependentDefinition != null) {
                    dependentIds.add(returnDefinition.getId());
                } else {

                    Long storedReturnId = null;

                    if (storedReturnIds != null) {
                        storedReturnId = processingStoreLocal.findStoredDependentReturns(returnIdsList, returnDefinition.getId()).get(returnDefinition.getId());
                    }

                    if (storedReturnId == null) {
                        processingStoreLocal.updateReturnDependencyTreeCache(entry.getValue(), null);
                        throw new ProcessException("Missing depended return. Return code: " + returnDefinition.getCode() + ", Return Name: " + returnDefinition.getDescription().getDescription(language.getId()));
                    } else {
                        storedReturnIds.add(storedReturnId);
                        dependentIds.add(returnDefinition.getId());
                    }
                }
            }
            DependencyItem item = new DependencyItem();
            item.id = entry.getKey();
            item.externalId = entry.getValue();
            item.dependentIds.addAll(dependentIds);
            orderReturns.add(item);
        }

        DependencyItemTopologicalSort topologicalSort = new DependencyItemTopologicalSort();
        topologicalSort.DFS(orderReturns);

        //Sort returns
        orderReturns.sort((o1, o2) -> {
            if (o1 == null || o2 == null) {
                return 0;
            }
            return Integer.compare(o1.end, o2.end);
        });

        return orderReturns.stream().map(dependencyItem -> dependencyItem.externalId).toList();
    }

    private boolean checkReturnStatus(long returnId) {
        ReturnStatus returnStatus = processingStoreLocal.loadReturnCurrentStatus(returnId);
        if (returnStatus != null && (returnStatus.getStatus() == ProcessStatus.STATUS_ACCEPTED || returnStatus.getStatus() == ProcessStatus.STATUS_VALIDATED)) {
            log.error("Warn: Unable to process accepted return");
            return false;
        }
        return true;
    }

    private List<Long> loadDependedReturnIds(ProcessReturnModel processReturnModel) {
        List<Long> packageReturnIds = new ArrayList<>();
        Collection<Long> returnDefinitions = processingStoreLocal.getReturnDefinitionDependencies(processReturnModel.getReturnDefinitionId());
        if (!returnDefinitions.isEmpty()) {
            packageReturnIds = processingStoreLocal.loadDependentReturns(processReturnModel.getFiId(), processReturnModel.getPeriodId(), returnDefinitions, processReturnModel.getVersionId());
        }
        return packageReturnIds;
    }

    /**
     * Return package all items have same version
     */
    private long detectVersionId(Long... returnIds) {
        if (returnIds.length > 0) {
            return processingStoreLocal.getVersionId(returnIds[0]);
        }
        return -1;
    }

    @Override
    public Collection<Long> getCurrentReturnDirectDependentReturnIds(long returnId) {
        ProcessReturnModel returnModel = processingStoreLocal.getReturnModel(returnId);

        return processingStoreLocal.getUsedReturnDefinitions(returnModel.getReturnDefinitionId());

    }

    private List<Long> getAllDependentReturnIds(long returnId, long langId) throws ProcessException {
        return resolveAllDependentReturnIds(returnId, langId);
    }

    private List<Long> resolveAllDependentReturnIds(long returnId, long langId) throws ProcessException {
        Set<Long> returnIdsSet = new HashSet<>();
        returnIdsSet.add(returnId);

        ProcessReturnModel returnModel = processingStoreLocal.getReturnModel(returnId);
        collectDependentReturnIdsRecursively(returnModel.getReturnDefinitionId(), returnModel.getFiId(), returnModel.getPeriodId(), returnModel.getVersionId(), returnIdsSet, new HashSet<>(), langId);

        return new ArrayList<>(returnIdsSet);
    }

    private void collectDependentReturnIdsRecursively(long definitionId, long fiId, long periodId, long versionId, Set<Long> result, Set<Long> defIds, long langId) throws ProcessException {
        Collection<Long> usedDefinitionIDs = processingStoreLocal.getUsedReturnDefinitions(definitionId);
        usedDefinitionIDs.forEach(defIds::remove);
        defIds.addAll(usedDefinitionIDs);

        if (!usedDefinitionIDs.isEmpty()) {
            Map<Long, Long> returnIdDefinitionIdMap = processingStoreLocal.loadDependentReturnIdDefinitionIdMap(fiId, periodId, usedDefinitionIDs, versionId);
            Set<Long> retIds = returnIdDefinitionIdMap.keySet();
            if (returnIdDefinitionIdMap.size() != usedDefinitionIDs.size()) {
                usedDefinitionIDs.removeAll(returnIdDefinitionIdMap.values());

                List<ReturnDefinition> retDefs = definitionLocal.loadReturnDefinitionsByIds(usedDefinitionIDs);
                StringBuilder errorMessage = new StringBuilder();
                for (ReturnDefinition returnDefinition : retDefs) {
                    errorMessage.append("Missing depended return. Return code: ")
                            .append(returnDefinition.getCode())
                            .append(", Return Name: ")
                            .append(returnDefinition.getDescription().getDescription(langId))
                            .append("\n");
                }
                throw new ProcessException(errorMessage.toString());
            }
            result.addAll(retIds);
            for (long retId : retIds) {
                collectDependentReturnIdsRecursively(returnIdDefinitionIdMap.get(retId), fiId, periodId, versionId, result, defIds, langId);
            }
        }
    }
}

