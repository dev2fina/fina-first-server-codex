package net.fina.server.returns.proxy;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.enterprise.concurrent.ManagedExecutorService;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.fis.FiTypeModel;
import net.fina.common.client.fis.RegionModel;
import net.fina.common.client.returns.*;
import net.fina.common.server.StatisticsLogger;
import net.fina.common.shared.*;
import net.fina.common.shared.mi.ManualInputCreateReturnMetaModel;
import net.fina.common.shared.mi.MiChartDataModel;
import net.fina.messages.MessagesUtil;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.dcs.uploadfile.model.ImportStatusDetailModel;
import net.fina.server.dcs.uploadfile.model.helper.ImportStatusMetaModelHelper;
import net.fina.server.dcs.uploadfile.proxy.UploadFileProxySession;
import net.fina.server.fi.api.FiLocal;
import net.fina.server.fi.api.RegionLocal;
import net.fina.server.fi.entity.Fi;
import net.fina.server.fi.entity.FiType;
import net.fina.server.fi.entity.Region;
import net.fina.server.fi.util.RegionLevelPrintHelper;
import net.fina.server.i18n.helper.Description;
import net.fina.server.i18n.proxy.LanguageProxySession;
import net.fina.server.jcr.impl.FileContentManagementSession;
import net.fina.server.mdt.api.MDTNodeLocal;
import net.fina.server.mdt.entity.MDTComparison;
import net.fina.server.mdt.entity.MDTDependentNode;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.misc.ProductHelper;
import net.fina.server.processing.ProcessException;
import net.fina.server.processing.api.ProcessingLocal;
import net.fina.server.processing.dependency.DependencyItem;
import net.fina.server.reg.api.RegFileLocal;
import net.fina.server.reports.util.ReportPrintUtil;
import net.fina.server.returns.api.*;
import net.fina.server.returns.entity.*;
import net.fina.server.returns.model.*;
import net.fina.server.returns.model.helper.PackageModelHelper;
import net.fina.server.returns.model.helper.ReturnTypeModelHelper;
import net.fina.server.returns.qualifier.AooReturnReview;
import net.fina.server.returns.qualifier.PoiReturnReview;
import net.fina.server.returns.util.ReturnStatusPrintUtil;
import net.fina.server.security.product.OSTLicense;
import net.fina.server.security.proxy.UserProxySession;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static net.fina.common.client.exception.FinATypeException.Type.*;

@Stateless
@SecurityDomain("FinASecurityDomain")
@RolesAllowed({PermissionIdNames.DCS_MANULAINPUT, PermissionIdNames.DCS_FILEUPLOAD, PermissionIdNames.DCS_FILEREVIEW})
public class ReturnProxySession {

    private final Logger log = Logger.getLogger(getClass().getName());

    @Inject
    private ReturnLocal returnLocal;

    @Inject
    private ReturnDefinitionLocal returnDefinitionLocal;

    @Inject
    private LanguageProxySession languageProxySession;
    @Inject
    private UploadFileProxySession uploadFileProxySession;

    @Inject
    @Any
    private Instance<ReturnReviewLocal> returnReviewLocalInstance;

    @Inject
    private ReturnItemLocal returnItemLocal;

    @Inject
    private UserProxySession userProxySession;

    @Inject
    private ProcessingLocal processingLocal;
    @Inject
    private RegFileLocal regFileLocal;
    @Inject
    private PackageLocal packageLocal;
    @Inject
    private ImportLocal importLocal;
    @Inject
    private FileContentManagementSession fileSession;

    @Inject
    private RegionLocal regionLocal;

    @Inject
    private FiLocal fiLocal;
    @Inject
    private MDTNodeLocal mdtNodeLocal;

    @Resource
    private ManagedExecutorService managedExecutorService;


    public PaginatedListWrapper<ReturnModel> loadPackages(String filter, int limit, int offset, SortField sortField) throws Exception {
        FilterConfig config = getFilterConfig(filter);
        long packageCount = returnLocal.getPackagesCount(config);
        List<ReturnModel> notCreatedPackages = returnLocal.loadNotCreatedPackages(config);

        List<ReturnModel> list = new ArrayList<>();
        if (offset < notCreatedPackages.size()) {
            list.addAll(notCreatedPackages.subList(offset, Math.min(offset + limit, notCreatedPackages.size())));
        }
        if (limit > list.size()) {
            list.addAll(returnLocal.loadPackages(config, limit - list.size(), !list.isEmpty() ? 0 : offset - notCreatedPackages.size(), sortField));
        }

        return new PaginatedListWrapper<>(list, limit, (int) (packageCount + notCreatedPackages.size()));
    }

    public List<ReturnModel> loadPackageReturns(long fiId, long periodId, long returnTypeId, long returnVersionId, String filter, String locale, boolean sortByDependencies) throws Exception {
        FilterConfig config = getFilterConfig(filter);

        List<Map<String, Object>> list;
        list = returnLocal.laodPackageReturns(fiId, periodId, returnTypeId, returnVersionId, config);

        Map<String, ReturnModel> returnMap = constructReturnModelMap(list, fiId, periodId, returnTypeId, returnVersionId, locale);

        Map<String, Integer> orderReturns = returnDefinitionLocal.orderReturnDefinitionIds(Arrays.asList(returnMap.keySet().toArray(new String[0])));
        List<DependencyItem> items = new ArrayList<>();

        for (Map.Entry<String, ReturnModel> entry : returnMap.entrySet()) {
            DependencyItem item = new DependencyItem();
            item.code = entry.getKey();
            item.end = orderReturns.get(entry.getKey());

            items.add(item);
        }

        if (sortByDependencies) {
            Collections.sort(items);
        }

        List<ReturnModel> returns = new ArrayList<>();
        for (DependencyItem dependencyItem : items) {
            returns.add(returnMap.get(dependencyItem.code));
        }

        return returns;
    }

    public Return createReturn(ManualInputCreateReturnMetaModel model) throws FinATypeException {
        final List<MDTDependentNode> allMdtDependentNodes = mdtNodeLocal.loadAllMdtDependentNodes();
        final List<MDTComparison> comparisons = mdtNodeLocal.loadComparisons(new HashMap<>(), null);
        Map<Long, List<MDTNode>> allMdtNodesByParentId = mdtNodeLocal.loadAllNodesByParentId();
        return returnLocal.createReturn(model.getScheduleId(), model.getVersionId(), model.getUserId(), allMdtDependentNodes, allMdtNodesByParentId, comparisons);
    }

    public List<String> getReturnHeader(long returnId, String langCode) {
        long langId = languageProxySession.getLanguageByCode(langCode).getId();
        return returnLocal.getReturnHeader(returnId, langId);
    }


    public List<ReturnModel> loadNotCreatedPackages(String filter) throws IOException {
        return returnLocal.loadNotCreatedPackages(getFilterConfig(filter));
    }

    public RReviewModel loadReturnsContent(List<Long> returnIds, long returnTypeId, String type, String reviewEngine, long versionId, long fiId, long periodId, String locale) throws Exception {

        LanguageSampleModel language = languageProxySession.getLanguageByCode(locale);

        ReturnReviewLocal returnReviewLocal;
        ReturnReviewEngineType reviewEngineType = getReviewEngineType(reviewEngine);
        if (reviewEngineType.equals(ReturnReviewEngineType.POI)) {
            returnReviewLocal = returnReviewLocalInstance.select(new AnnotationLiteral<PoiReturnReview>() {
            }).get();
        } else {
            returnReviewLocal = returnReviewLocalInstance.select(new AnnotationLiteral<AooReturnReview>() {
            }).get();
        }


        if (returnTypeId > 0) {
            List<ReturnModel> returns = loadPackageReturns(fiId, periodId, returnTypeId, versionId, "{}", locale, false);
            List<Long> ids = returns.stream().map(ReturnModel::getId).collect(Collectors.toList());
            return returnReviewLocal.loadConvertedReturnsContent(ids, type, versionId, language.getId());
        } else {
            return returnReviewLocal.loadConvertedReturnsContent(returnIds, type, versionId, language.getId());
        }

    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1L)
    public ContentModel returnReview(List<Long> returnIds, long returnTypeId, String reviewEngine, long versionId, long fiId, long periodId, String contentPath, String fileType) throws Exception {
        String locale = ThreadLocalHolder.getLanguage().getCode();

        Future<ContentModel> job = managedExecutorService.submit(() -> {
            RReviewModel reviewModel = loadReturnsContent(returnIds, returnTypeId, fileType, reviewEngine, versionId, fiId, periodId, locale);
            byte[] content = reviewModel.getContent();
            String fileName = reviewModel.getFileName();
            if (fileType.equalsIgnoreCase("html")) {
                return ReportPrintUtil.printHtml(null, content, fileName, contentPath);
            } else {
                return ReportPrintUtil.print(null, content, fileType, fileName, contentPath);
            }
        });

        try {
            return job.get(30, TimeUnit.MINUTES);
        } catch (TimeoutException e) {
            job.cancel(true);
            throw new FinATypeException(GENERAL_ERROR);
        }

    }

    private FilterConfig getFilterConfig(String filter) throws IOException {
        TypeReference<HashMap<FilterConfigKey, Object>> typeRef = new TypeReference<HashMap<FilterConfigKey, Object>>() {
        };
        Map<FilterConfigKey, Object> filterConfig = new ObjectMapper().readValue(filter, typeRef);
        if (filterConfig.containsKey(FilterConfigKey.PERIOD_FROM_DATE)) {
            filterConfig.put(FilterConfigKey.PERIOD_FROM_DATE, new Date((Long) filterConfig.get(FilterConfigKey.PERIOD_FROM_DATE)));
        }
        if (filterConfig.containsKey(FilterConfigKey.PERIOD_TO_DATE)) {
            filterConfig.put(FilterConfigKey.PERIOD_TO_DATE, new Date((Long) filterConfig.get(FilterConfigKey.PERIOD_TO_DATE)));
        }
        return new FilterConfig(filterConfig);
    }

    private ReturnReviewEngineType getReviewEngineType(String reviewEngine) {
        try {
            if (reviewEngine != null) {
                return ReturnReviewEngineType.valueOf(reviewEngine.toUpperCase().trim());
            }
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
        return ReturnReviewEngineType.AOO;
    }

    public ProcessResult updateProcessItems(ReturnItemUpdateRequestModel requestModel) {
        Collection<ReturnItemLite> itemLites = new ArrayList<>();

        for (RItemMetaModel inputItem : requestModel.getInputItems()) {
            ReturnItemLite itemLite = new ReturnItemLite();
            itemLite.setId(inputItem.getId());
            itemLite.setReturnId(inputItem.getReturnId());
            itemLite.setNodeId(inputItem.getNodeId());
            itemLite.setNodeCode(inputItem.getCode());
            itemLite.setVersionId(inputItem.getVersionId());
            itemLite.setTableId(inputItem.getTableId());
            itemLite.setRowNumber(inputItem.getRowNumber());
            itemLite.setValue(inputItem.getValue(), inputItem.getDataType());
            itemLite.setDataType(inputItem.getDataType());
            try {
                itemLite.setnValue(Double.parseDouble(inputItem.getValue()));
            } catch (Exception ex) {
                itemLite.setnValue(inputItem.getNvalue());
            }
            itemLites.add(itemLite);
        }

        ReturnStatus rs = returnLocal.loadReturnCurrentStatus(requestModel.getReturnId());

        ProcessResult result = new ProcessResult();
        result.setStatus(ProcessStatus.STATUS_AMENDED);
        result.setReturnId(requestModel.getReturnId());
        result.setProcessNote("-");

        if (ProcessStatus.canChangeStatus(rs.getStatus(), ProcessStatus.STATUS_AMENDED)) {

            try {
                String processId = UUID.randomUUID().toString();
                //Update process items
                returnItemLocal.updateReturnItems(itemLites, requestModel.getReturnId(), requestModel.getReturnVersionId(), true, "Amended from DCS[Manual Input].", processId);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }


        return result;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Map<Long, ProcessResult> processReturn(ReturnItemUpdateRequestModel requestModel, String locale) {
        try {
            long langId = languageProxySession.getLanguageByCode(locale).getId();
            List<MDTComparison> comparisons = mdtNodeLocal.loadComparisons(new HashMap<>(), null);
            //Process Return
            return processingLocal.process(userProxySession.getCurrentUserId(), langId, true, comparisons, requestModel.getReturnId());
        } catch (Exception t) {
            log.error(t.getMessage(), t);
        }

        return Collections.emptyMap();
    }

    public List<ReturnModel> loadAwaitingReturns() {
        return returnLocal.loadFiAwaitingReturns(null);
    }

    public List<ReturnModel> loadFiInProgressReturns() {
        return returnLocal.loadFiInProgressReturns(null);
    }

    public MiChartDataModel loadMiChartData() {
        return returnLocal.loadMiChartData();
    }

    public List<ReturnStatusModel> loadReturnStatuses(long returnId, String locale) {
        List<ReturnStatus> returnStatuses = returnLocal.loadReturnStatuses(returnId);

        List<ReturnStatusModel> result = new ArrayList<>();

        List<String> header = getReturnHeader(returnId, locale);

        ResourceBundle messageBundle = MessagesUtil.loadMessageBundle(locale);

        returnStatuses.forEach(rs -> {
            ReturnStatusModel model = new ReturnStatusModel();
            model.setId(rs.getId());
            model.setReturnCode(header.get(0));
            model.setReturnDescription(header.get(1));
            model.setFiCode(header.get(2));
            model.setFiDescription(header.get(3));
            model.setFromDate(header.get(4));
            model.setToDate(header.get(5));
            model.setNote(rs.getNote());
            model.setStatus(messageBundle.getString(rs.getStatus().getCode()));
            model.setUser(rs.getUser().getLogin());
            model.setStatusDate(rs.getStatusDate());

            result.add(model);
        });

        return result;
    }

    public String loadCurrentReturnStatus(long importedXmlReturnId, String locale) {
        ReturnStatus returnStatus = returnLocal.loadReturnCurrentStatus(returnLocal.getReturnId(importedXmlReturnId));
        return returnStatus != null ? MessagesUtil.getString(returnStatus.getStatus().getCode(), locale) : "";
    }

    public RTableRowMetaModel insertRow(RTableRowMetaModel rowModel, int maxRowNumber) {
        List<ReturnItemLite> rowItems = new ArrayList<>();

        try (StatisticsLogger statLog = new StatisticsLogger("MI INSERT ROW!");) {

            RItemMetaModel rItem = rowModel.getRowItems().getFirst();
            ReturnStatus rs = returnLocal.loadReturnCurrentStatus(rItem.getReturnId());

            assert rs.getStatus() != ProcessStatus.STATUS_ACCEPTED : "Unable to insert accepted return row. Return Id:" + rItem.getReturnId();

            statLog.logStage("Get Item Max Id");
            long itemMaxId = returnItemLocal.getItemMaxId(rItem.getReturnId(), rItem.getVersionId());
            statLog.logStage("Update Model Row Numbers");
            List<Long> nodeIds = new ArrayList<>();
            for (RItemMetaModel inputItem : rowModel.getRowItems()) {
                ReturnItemLite itemLite = toItemLite(inputItem);
                itemLite.setId(++itemMaxId);
                rowItems.add(itemLite);
                inputItem.setId(itemLite.getId());
                nodeIds.add(inputItem.getNodeId());
            }

            ReturnItemLite itemLite = rowItems.get(0);
            long rowNumber = itemLite.getRowNumber();
            statLog.logStage("Update Item Row Numbers");

            if (maxRowNumber >= rowNumber) {
                returnItemLocal.updateReturnItemRowNumberBatch(itemLite.getReturnId(), itemLite.getVersionId(), itemLite.getTableId(), rowNumber, nodeIds, true);
            }

            statLog.logStage("Insert Row");
            returnItemLocal.insertReturnItems(rowItems);

            return rowModel;
        }
    }

    public void deleteRow(RTableRowMetaModel rowModel, int maxRowNumber) {
        List<ReturnItemLite> rowItems = new ArrayList<>();
        try (StatisticsLogger statLog = new StatisticsLogger("MI DELETE ROW!");) {


            statLog.logStage("Convert rowItems to Models");
            List<Long> nodeIds = new ArrayList<>();

            for (RItemMetaModel inputItem : rowModel.getRowItems()) {
                rowItems.add(toItemLite(inputItem));
                nodeIds.add(inputItem.getNodeId());
            }

            ReturnItemLite itemLite = rowItems.getFirst();
            ReturnStatus rs = returnLocal.loadReturnCurrentStatus(itemLite.getReturnId());

            assert rs.getStatus() != ProcessStatus.STATUS_ACCEPTED : "Unable to delete accepted return Row. Return Id:" + itemLite.getReturnId();

            long rowNumber = itemLite.getRowNumber();

            statLog.logStage("Delete row items!");
            returnItemLocal.deleteReturnItemsBatch(itemLite.getReturnId(), itemLite.getVersionId(), itemLite.getTableId(), rowNumber, nodeIds);

            statLog.logStage("Update Row Item Numbers!");
            returnItemLocal.updateReturnItemRowNumberBatch(itemLite.getReturnId(), itemLite.getVersionId(), itemLite.getTableId(), rowNumber, nodeIds, false);

        }
    }

    private ReturnItemLite toItemLite(RItemMetaModel inputItem) {
        ReturnItemLite itemLite = new ReturnItemLite();
        itemLite.setId(inputItem.getId());
        itemLite.setReturnId(inputItem.getReturnId());
        itemLite.setNodeId(inputItem.getNodeId());
        itemLite.setVersionId(inputItem.getVersionId());
        itemLite.setTableId(inputItem.getTableId());
        itemLite.setRowNumber(inputItem.getRowNumber());
        itemLite.setValue(inputItem.getValue(), inputItem.getDataType());
        itemLite.setDataType(inputItem.getDataType());
        try {
            itemLite.setnValue(Double.parseDouble(inputItem.getValue()));
        } catch (Exception ex) {
            itemLite.setnValue(inputItem.getNvalue());
        }

        return itemLite;
    }

    public List<ReturnModel> loadReturnsBySchedules(List<Long> scheduleIds) {
        return returnLocal.loadReturnsBySchedules(scheduleIds);
    }

    public byte[] getImportedReturnError(long returnId) throws FinATypeException {
        byte[] content = returnLocal.getImportedReturnErrorContent(returnId);
        if (content != null && content.length > 0) {
            return content;
        }
        StringBuilder sb = new StringBuilder();
        ImportStatusDetailModel model = getImportedReturn((int) returnId);

        return uploadFileProxySession.buildErrorLogTemplate(sb, model.getCode(),
                model.getVersion(),
                model.getPeriodStart(),
                model.getPeriodEnd(),
                model.getStatus(),
                model.getMessage(),
                model.getImportStart(),
                model.getImportEnd()
        ).getBytes(StandardCharsets.UTF_8);

    }

    public ImportStatusDetailModel getImportedReturn(int returnId) throws FinATypeException {
        return ImportStatusMetaModelHelper.toModel(returnLocal.getImportedReturnById(returnId));
    }

    public PaginatedListWrapper<ReturnModel> loadPackages(FilterConfig filterConfig, int limit, int offset, SortField sortField) throws Exception {
        List<ReturnModel> packages = returnLocal.loadPackages(filterConfig, limit, offset, sortField);

        return new PaginatedListWrapper<>(packages, limit, 0);
    }

    public List<ReturnModel> loadReturns(FilterConfig filterConfig, long fiId, long periodId, long returnTypeId, long versionId) {
        long langId = ThreadLocalHolder.getLanguage().getId();
        List<Map<String, Object>> returns = returnLocal.laodPackageReturns(fiId, periodId, returnTypeId, versionId, filterConfig);
        List<Long> regSchedules = regFileLocal.loadUniqueScheduleIds();

        List<ReturnModel> resultList = new ArrayList<>();

        ReturnModel newReturn;
        for (Map<String, Object> singleReturn : returns) {
            newReturn = new ReturnModel();
//            newReturn.setIdentifier(filterModel.getIdentifier());

            newReturn.setId((long) singleReturn.get("id"));

            newReturn.setPeriodId(periodId);
            newReturn.setFromDate((Date) singleReturn.get("periodFromDate"));
            newReturn.setToDate((Date) singleReturn.get("periodToDate"));

            newReturn.setDefinitionId((Long) singleReturn.get("retrunDefinitionId"));
            newReturn.setDefinitionCode((String) singleReturn.get("retrunDefinitionCode"));
            newReturn.setDefinitionDescription(((Description) singleReturn.get("retrunDefinitionDescription")).getDescription(langId));

            newReturn.setFiId(fiId);
            newReturn.setFiCode((String) singleReturn.get("fiCode"));
            newReturn.setFiDescription(((Description) singleReturn.get("fiName")).getDescription(langId));

            newReturn.setVersionId(versionId);
            newReturn.setVersionCode((String) singleReturn.get("returnVersionCode"));

            newReturn.setReturnTypeId(returnTypeId);
            newReturn.setReturnTypeCode((String) singleReturn.get("returnTypeCode"));

            newReturn.setStatus((ProcessStatus) singleReturn.get("returnStatus"));
            newReturn.setStatusDate((Date) singleReturn.get("returnStatusDate"));
            newReturn.setExcelTemplate((boolean) singleReturn.get("excelTemplate"));

            newReturn.setReg((regSchedules.contains((Long) singleReturn.get("scheduleId"))));
            newReturn.setPeriodTypeCode((String) singleReturn.get("periodTypeCode"));

            resultList.add(newReturn);
        }

        return resultList;
    }

    @Transactional(rollbackOn = FinATypeException.class)
    public List<ReturnModel> createReturns(List<ScheduleModel> schedules, long versionId) throws FinATypeException {
        if (versionId <= 0) {
            throw new FinATypeException("Return Version Id is required");
        }

        long userId = userProxySession.getCurrentUserId();
        List<MDTDependentNode> mdtDependentNodes = mdtNodeLocal.loadAllMdtDependentNodes();
        final List<MDTComparison> comparisons = mdtNodeLocal.loadComparisons(new HashMap<>(), null);
        Map<Long, List<MDTNode>> allMdtNodesByParentId = mdtNodeLocal.loadAllNodesByParentId();
        Map<String, List<Long>> definitionsMap = new HashMap<>();
        for (ScheduleModel scheduleModel : schedules) {
            definitionsMap.computeIfAbsent(scheduleModel.getReturnDefinition().getCode(), k -> new ArrayList<Long>());
            definitionsMap.get(scheduleModel.getReturnDefinition().getCode()).add(scheduleModel.getId());
        }

        List<DependencyItem> items = new ArrayList<>();

        List<String> definitionCodesList = new ArrayList<>(definitionsMap.keySet());
        Map<String, Integer> orderedReturns = returnDefinitionLocal.orderReturnDefinitionIds(definitionCodesList);
        for (Map.Entry<String, Integer> entry : orderedReturns.entrySet()) {
            DependencyItem item = new DependencyItem();
            item.code = entry.getKey();
            item.end = entry.getValue();

            items.add(item);
        }
        Collections.sort(items);

        for (DependencyItem item : items) {
            for (long id : definitionsMap.get(item.code)) {
                returnLocal.createReturn(id, versionId, userId, mdtDependentNodes, allMdtNodesByParentId, comparisons);
            }
        }

        return new ArrayList<>();
    }

    @Transactional(rollbackOn = FinATypeException.class)
    public void saveAsReturn(SaveAsReturnModel saveAsModel) throws FinATypeException {
        long currentUserId = userProxySession.getCurrentUserId();
        long langId = ThreadLocalHolder.getLanguage().getId();

        for (ReturnModel model : saveAsModel.getReturnModels()) {
            if (model.isGroup()) {
                for (ReturnModel rm : loadReturns(new FilterConfig(), model.getFiId(), model.getPeriodId(), model.getReturnTypeId(), model.getVersionId())) {
                    returnLocal.saveAsReturn(rm.getId(), saveAsModel.getVersionId(), currentUserId, langId, saveAsModel.getNote());
                }
            } else {
                returnLocal.saveAsReturn(model.getId(), saveAsModel.getVersionId(), currentUserId, langId, saveAsModel.getNote());
            }
        }
    }

    public String changeReturnStatus(ReturnChangeStatusModel changeStatusModel) {

        long userId = userProxySession.getCurrentUserId();

        ProcessStatus status = changeStatusModel.getStatus();
        Map<String, String> result = new HashMap<>();

        for (ReturnModel model : changeStatusModel.getReturnModels()) {
            if (model.isGroup()) {
                for (ReturnModel child : loadReturns(new FilterConfig(), model.getFiId(), model.getPeriodId(), model.getReturnTypeId(), model.getVersionId())) {
                    if (ProcessStatus.canChangeStatus(child.getStatus(), status)) {
                        child.setStatus(status);
                        returnLocal.addReturnStatus(child.getId(), status, model.getVersionId(), userId, changeStatusModel.getNote(), null);
                    } else {
                        result.put(child.getDefinitionCode(), "Current: " + child.getStatus() + ", Requested: " + status);
                    }
                }
            } else {
                if (ProcessStatus.canChangeStatus(model.getStatus(), status)) {
                    model.setStatus(status);
                    returnLocal.addReturnStatus(model.getId(), status, model.getVersionId(), userId, changeStatusModel.getNote(), null);
                } else {
                    result.put(model.getDefinitionCode(), "Current: " + model.getStatus() + ", Requested: " + status);
                }
            }
        }


        if (!result.isEmpty()) {
            return generateErrorMessage(result);
        }

        if (changeStatusModel.getStatus() == ProcessStatus.STATUS_REJECTED || changeStatusModel.getStatus() == ProcessStatus.STATUS_ACCEPTED) {
            notifyUploadedFilesUsers(changeStatusModel.getReturnModels(), changeStatusModel.getStatus(), changeStatusModel.getNote());
        }

        return "Success";
    }


    private void notifyUploadedFilesUsers(List<ReturnModel> returnModels, ProcessStatus status, String note) {
        if (returnModels != null && !returnModels.isEmpty()) {
            Set<Long> returnIds = new HashSet<>();
            for (ReturnModel rModel : returnModels) {
                returnIds.add(rModel.getId());
            }
            returnLocal.sendAcceptRejectNotificationToUsers(returnIds.stream().toList(), status, note);
        }
    }

    private ReturnModel getModelByDefinitionCode(String code, List<ReturnModel> returnModels) {
        for (ReturnModel r : returnModels) {
            if (r.getDefinitionCode().equals(code)) {
                return r;
            }
        }
        return null;
    }

    private String generateErrorMessage(Map<String, String> returnDefinitionNewStatusMap) {
        StringBuilder sb = new StringBuilder("Can not change status of Return(s):");
        sb.append("\n");

        for (Map.Entry<String, String> entry : returnDefinitionNewStatusMap.entrySet()) {
            sb.append(entry.getKey()).append(" -> {");
            sb.append(entry.getValue());
            sb.append("}\n");
        }

        return sb.toString();
    }

    public void deleteReturn(ReturnModel rm) throws FinATypeException {
        Set<Long> returnIds = new HashSet<>();
        Set<String> returnsCantDeleted = new HashSet<>();

        if (rm.isGroup()) {
            for (ReturnModel rModel : loadReturns(new FilterConfig(), rm.getFiId(), rm.getPeriodId(), rm.getReturnTypeId(), rm.getVersionId())) {
                if (ProcessStatus.canDelete(rModel.getStatus())) {
                    returnIds.add(rModel.getId());
                } else {
                    returnsCantDeleted.add(rModel.getFiDescription());
                }
            }
        } else {
            if (ProcessStatus.canDelete(rm.getStatus())) {
                returnIds.add(rm.getId());
            } else {
                returnsCantDeleted.add(rm.getFiDescription());
            }
        }

        boolean hadDependencies = false;
        if (!rm.isGroup()) {

            for (long id : returnIds) {
                hadDependencies = !processingLocal.getCurrentReturnDirectDependentReturnIds(id).isEmpty();
                if (hadDependencies) {
                    break;
                }

            }
        }

        if (hadDependencies) {
            //TODO return codes
            throw new FinATypeException(DEPENDENCY_ERROR);
        }

        returnLocal.delete(returnIds);

        if (!returnsCantDeleted.isEmpty()) {
            String[] params = new String[]{
                    String.join(", ", returnsCantDeleted)
            };
            throw new FinATypeException(RETURN_DELETE_ERROR, params);
        }

    }


    public ReturnModel loadReturnHeaderInfo(long returnId) {
        List<String> header = returnLocal.getReturnHeader(returnId, ThreadLocalHolder.getLanguage().getId());
        ReturnModel model = new ReturnModel();
        model.setId(returnId);
        model.setLabel(header.get(1));
        model.setFiCode(header.get(2));
        model.setFiDescription(header.get(3));
        model.setDefinitionCode(header.get(0));
        return model;
    }

    public List<ImportedXmlReturnModel> loadImportedXmlReturns(long returnId) {
        return returnLocal.loadImportedXmlReturns(returnId);
    }

    public List<ImportedXmlReturnModel> loadReturnsXMLsByImportedReturnId(List<Integer> importedReturnIds) {
        return returnLocal.loadReturnsXMLsByImportedReturnId(importedReturnIds);
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 3)
    public Map<Long, ProcessResult> processReturns(List<ReturnModel> returnModels) throws FinATypeException {
        Map<Long, ProcessResult> resultMap = new HashMap<>();
        long langId = ThreadLocalHolder.getLanguage().getId();
        try {
            List<MDTComparison> comparisons = mdtNodeLocal.loadComparisons(new HashMap<>(), null);
            //Single package Process
            if (returnModels.size() == 1 && returnModels.get(0).isGroup()) {
                resultMap.putAll(processingLocal.processPackage(-1, langId, returnModels.get(0)));
                return resultMap;
            }

            for (ReturnModel returnModel : returnModels) {
                List<Long> ids = new ArrayList<>();
                if (!returnModel.isGroup()) {
                    if (ProcessStatus.canChangeStatus(returnModel.getStatus(), ProcessStatus.STATUS_PROCESSED)) {
                        ids.add(returnModel.getId());
                    }
                }
                resultMap.putAll(processingLocal.process(-1, langId, true, comparisons, ids.toArray(new Long[0])));
            }
        } catch (ProcessException e) {
            throw new FinATypeException(e.getMessage());
        }
        return resultMap;
    }

    public PackageMetaModel saveOSTPackage(PackageMetaModel packageModel) throws FinATypeException {
        List<FiType> fiTypes = new ArrayList<>();
        List<ReturnDefinition> returnDefinitions = new ArrayList<>();

        ReturnPackage pack = new ReturnPackage();
        pack.setId(packageModel.getId());
        pack.setCode(packageModel.getCode());
        pack.setNote(packageModel.getNote());

        for (FiTypeModel model : packageModel.getFiTypes()) {
            FiType fiType = new FiType();
            fiType.setId(model.getId());
            fiType.setCode(model.getCode());
            fiTypes.add(fiType);
        }

        OSTLicense ostLicense = ProductHelper.getInstance().getOstLicense();
        switch (ostLicense.getLicenseKind()) {
            case PAID -> {
                fiTypes = fiTypes.stream().filter(t -> ostLicense.getFiTypes().contains(t.getCode().trim().toUpperCase())).toList();
            }
            case PROHIBITED -> {
                fiTypes = new ArrayList<>();
            }
        }

        for (ReturnDefinitionModel rd : packageModel.getReturnDefinitions()) {
            ReturnDefinition returnDefinition = new ReturnDefinition();
            returnDefinition.setId(rd.getId());
            returnDefinitions.add(returnDefinition);
        }

        pack.setFiTypes(fiTypes);
        pack.setReturnDefinitions(returnDefinitions);

        long langId = ThreadLocalHolder.getLanguage().getId();

        return PackageModelHelper.toModel(packageLocal.savePackage(pack), langId);
    }

    public List<PackageMetaModel> loadOSTPackages() {
        long langId = ThreadLocalHolder.getLanguage().getId();

        List<ReturnPackage> data = packageLocal.load();
        List<PackageMetaModel> result = new ArrayList<>();

        PackageMetaModel model;
        ReturnDefinitionModel rdModel;
        FiTypeModel fiTypeModel;

        List<ReturnDefinitionModel> rdModels;
        List<FiTypeModel> fiTypeModels;
        if (data != null) {
            for (ReturnPackage returnPackage : data) {
                rdModels = new ArrayList<>();
                fiTypeModels = new ArrayList<>();

                model = new PackageMetaModel();
                model.setId(returnPackage.getId());
                model.setCode(returnPackage.getCode());
                model.setNote(returnPackage.getNote());
                if (returnPackage.getReturnDefinitions() != null && !returnPackage.getReturnDefinitions().isEmpty()) {
                    model.setHasReturnDefinitions(true);

                    for (ReturnDefinition rd : returnPackage.getReturnDefinitions()) {
                        rdModel = new ReturnDefinitionModel();
                        rdModel.setId(rd.getId());
                        rdModel.setCode(rd.getCode());
                        rdModel.setName(rd.getDescription().getDescription(langId));
                        rdModel.setVersion(rd.getVersion());
                        rdModel.setNameStrId(langId);

                        ReturnTypeModel returnType = ReturnTypeModelHelper.toModel(rd.getReturnType(), langId);

                        rdModel.setReturnType(returnType);
                        rdModels.add(rdModel);
                    }
                } else {
                    model.setHasReturnDefinitions(false);
                }
                if (returnPackage.getFiTypes() != null && !returnPackage.getFiTypes().isEmpty()) {
                    model.setHasFiTypes(true);
                    for (FiType fiType : returnPackage.getFiTypes()) {
                        fiTypeModel = new FiTypeModel();
                        fiTypeModel.setId(fiType.getId());
                        fiTypeModel.setCode(fiType.getCode());
                        fiTypeModel.setName(fiType.getDescription().getDescription(langId));
                        fiTypeModel.setVersion(fiType.getVersion());

                        fiTypeModels.add(fiTypeModel);
                    }
                } else {
                    model.setHasFiTypes(false);
                }

                model.setReturnDefinitions(rdModels);
                model.setFiTypes(fiTypeModels);
                result.add(model);
            }
        }

        return result;
    }

    public void deleteOSTPackage(long packageId) {
        packageLocal.deletePackage(packageId);
    }

    public String getImportedReturnXmlContent(int returnId, String dataType) {
        ImportedReturn importedReturn = importLocal.findById(returnId);

        byte[] content = " ".getBytes();

        switch (dataType) {
            case "content":
                content = fileSession.loadImportedReturnContent(importedReturn);
                break;
            case "header":
                content = getImportdReturnHeader(importedReturn).getBytes(StandardCharsets.UTF_8);
                break;
        }

        return new String(content);
    }

    public ContentModel printReturnStatusReport(long periodId, long rVersionId, List<Long> fiIds, List<Long> definitionIds, List<Long> returnTypeIds, List<Long> fiTypeIds, Long fiGroupId) {

        try {
            List<Fi> processedFis = processFiParameters(fiIds, fiTypeIds, fiGroupId);
            List<ReturnDefinition> processedDefinitions = processDefinitionParameters(definitionIds, returnTypeIds);

            ReturnStatusPrintModel printModel = getTableModel(periodId, rVersionId, processedFis, processedDefinitions);
            return ReturnStatusPrintUtil.createReturnStatusExcelReport(printModel);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return new ContentModel(new byte[0], "ReturnStatuses.xslx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }


    private ReturnStatusPrintModel getTableModel(long periodId, long rVersionId, List<Fi> fis, List<ReturnDefinition> definitions) {
        LanguageSampleModel language = ThreadLocalHolder.getLanguage();
        long langId = language.getId();
        String langCode = language.getCode();

        fis = fis == null ? new ArrayList<>() : fis;
        definitions = definitions == null ? new ArrayList<>() : definitions;

        List<Long> fiIds = fis.stream().map(Fi::getId).toList();

        List<Long> definitionIds = new ArrayList<>();
        List<String> returnCodes = new ArrayList<>();

        for (ReturnDefinition definition : definitions) {
            definitionIds.add(definition.getId());
            returnCodes.add(definition.getCode());
        }


        List<ReturnStatusesModel> returnStatuses = returnLocal.getReturnStatuses(periodId, fiIds, definitionIds, rVersionId);


        Map<Long, Map<Long, ProcessStatusDateWrapper>> result = new HashMap<>();
        for (ReturnStatusesModel statusesModel : returnStatuses) {
            Map<Long, ProcessStatusDateWrapper> fiData = result.computeIfAbsent(statusesModel.getDefinitionId(), k -> new HashMap<>());
            fiData.put(statusesModel.getFiId(), new ProcessStatusDateWrapper(statusesModel.getStatus(), statusesModel.getStatusDate()));
        }

        List<String> currUserFiCodes = fis.stream().map(Fi::getCode).toList();
        List<String> currUserFiNames = fis.stream().map(fi -> fi.getDescription().getDescription(langId)).toList();


        List<Region> regions = regionLocal.loadRegions();
        Map<Long, RegionModel> regionModelMap = new HashMap<>();
        for (Region region : regions) {
            RegionModel model = new RegionModel();
            model.setId(region.getId());
            model.setParentId(region.getParentId());
            model.setName(region.getDescription().getDescription(langId));
            model.setCode(region.getCode());

            regionModelMap.put(model.getId(), model);
        }

        Map<Long, Long> fisAndRegionsMap = fiLocal.getFisAndRegionIds();


        Map<Integer, String> regionLevelStore = regionLocal.getProperties(langId);
        int maxRegionLevel = Integer.parseInt(regionLevelStore.get(0));
        regionLevelStore.remove(0);

        RegionLevelPrintHelper regionLevelPrintHelper = new RegionLevelPrintHelper(maxRegionLevel, regionLevelStore, regionModelMap, 2, langCode);

        return new ReturnStatusPrintModel(result, fiIds, definitionIds, currUserFiCodes, returnCodes,
                currUserFiNames, fisAndRegionsMap, regionLevelPrintHelper, "Return_Statuses", langCode);
    }

    private List<ReturnDefinition> processDefinitionParameters(List<Long> definitionIds, List<Long> returnTypeIds) {
        List<ReturnDefinition> result = new ArrayList<>();
        if (definitionIds != null && !definitionIds.isEmpty()) {
            result = returnDefinitionLocal.loadReturnDefinitionsById(definitionIds);
        } else if (returnTypeIds != null && !returnTypeIds.isEmpty()) {
            result = returnDefinitionLocal.loadReturnDefinitionsByReturnType(returnTypeIds);
        }
        return result;
    }

    private List<Fi> processFiParameters(List<Long> fiIds, List<Long> fiTypeIds, Long fiGroupId) {
        List<Fi> result = new ArrayList<>();

        if (fiTypeIds != null && !fiTypeIds.isEmpty()) {
            result = fiLocal.loadActiveFisByType(fiTypeIds, null);
        } else if (fiIds != null && !fiIds.isEmpty()) {
            result = fiLocal.loadFisByIds(fiIds);
        } else if (fiGroupId != null) {
            result = fiLocal.loadFisByPeerGroup(fiGroupId);
        }

        return result;
    }


    private String getImportdReturnHeader(ImportedReturn importedReturn) {
        StringBuilder sb = new StringBuilder()
                .append("   ")
                .append(importedReturn.getBankCode())
                .append(" -> ")
                .append(importedReturn.getReturnCode())
                .append(" -> ")
                .append(importedReturn.getPeriodStart())
                .append(" _ ")
                .append(importedReturn.getPeriodEnd())
                .append("(Imported:")
                .append(importedReturn.getImportEnd())
                .append(")");
        return sb.toString();
    }

    public ReturnModel getReturnById(long returnId) {
        return returnLocal.getReturnById(returnId);
    }

    public void createPackageReturns(long fiId, long periodId, long returnTypeId, long versionId) throws FinATypeException {
        FilterConfig config = new FilterConfig();
        config.setLangId(ThreadLocalHolder.getLanguage().getId());
        config.setFilterParam(FilterConfigKey.RETURN_DEFINITION_MANUAL_INPUT, true);
        List<Map<String, Object>> list = returnLocal.loadNotCreatedPackageReturns(fiId, periodId, returnTypeId, versionId, config, new ArrayList<>());

        List<ScheduleModel> scheduleModels = new ArrayList<>();
        for (Map<String, Object> map : list) {
            ScheduleModel model = new ScheduleModel();
            model.setId(((Number) map.get("scheduleId")).longValue());

            String definitionCode = map.get("retrunDefinitionCode").toString();
            ReturnDefinitionModel rd = new ReturnDefinitionModel();
            rd.setCode(definitionCode);
            model.setReturnDefinition(rd);

            scheduleModels.add(model);
        }

        createReturns(scheduleModels, versionId);

    }

    private Map<String, ReturnModel> constructReturnModelMap(List<Map<String, Object>> returnPackageList, long fiId, long periodId, long returnTypeId, long returnVersionId, String locale) {
        Map<String, ReturnModel> returnMap = new LinkedHashMap<>();

        for (Map<String, Object> map : returnPackageList) {
            ReturnModel model = new ReturnModel();

            model.setFiId(fiId);
            model.setPeriodId(periodId);
            model.setReturnTypeId(returnTypeId);
            model.setVersionId(returnVersionId);
            model.setId((Long) map.get("id"));
            model.setReturnTypeCode(map.get("returnTypeCode").toString());
            model.setDefinitionCode(map.get("retrunDefinitionCode").toString());
            model.setDefinitionDescription(map.get("retrunDefinitionDescription").toString());
            model.setVersionCode(map.get("returnVersionCode").toString());
            model.setToDate((Date) map.get("periodToDate"));
            model.setFiCode(map.get("fiCode").toString());
            model.setExcelTemplate((boolean) map.get("excelTemplate"));
            model.setPeriodTypeCode(map.get("periodTypeCode").toString());
            Object scheduleId = map.get("scheduleId");
            model.setScheduleId(scheduleId != null ? (long) scheduleId : 0L);

            model.setDelay((Integer) map.getOrDefault("due", 0));
            model.setDelayHour((Integer) map.getOrDefault("dueHour", 0));
            model.setDelayMinute((Integer) map.getOrDefault("dueMinute", 0));
            ProcessStatus status = (ProcessStatus) map.getOrDefault("returnStatus", ProcessStatus.STATUS_SCHEDULED);
            model.setStatus(status);
            model.setStatusDate((Date) map.getOrDefault("returnStatusDate", null));
            ResourceBundle messageBundle = MessagesUtil.loadMessageBundle(locale);
            model.setStatusDescription(messageBundle.getString(status.getCode()));

            returnMap.put(map.get("retrunDefinitionCode").toString(), model);
        }

        return returnMap;
    }
}
