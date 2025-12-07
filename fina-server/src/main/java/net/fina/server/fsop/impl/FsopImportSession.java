package net.fina.server.fsop.impl;

import jakarta.annotation.Resource;
import jakarta.ejb.*;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.jms.JMSContext;
import net.fina.common.client.constants.ImportStatus;
import net.fina.common.client.dcs.UploadFileStatus;
import net.fina.common.client.property.PropertyKeys;
import net.fina.common.client.returns.ProcessResult;
import net.fina.common.client.returns.ProcessStatus;
import net.fina.common.server.StatisticsLogger;
import net.fina.server.fsop.FsopXmlParser;
import net.fina.server.fsop.ImportedReturnValidationEventHandler;
import net.fina.server.fsop.api.FsopImportLocal;
import net.fina.server.fsop.api.FsopImportStoreLocal;
import net.fina.server.fsop.api.FsopTemplateLocal;
import net.fina.server.fsop.api.UploadFileQueueStoreLocal;
import net.fina.server.fsop.entity.UploadFileQueue;
import net.fina.server.fsop.entity.UploadFileQueueStatus;
import net.fina.server.fsop.event.ImportedReturnUpdateEvent;
import net.fina.server.fsop.event.RemoveUploadFileQueueEvent;
import net.fina.server.fsop.model.FsopImportedReturnMetaModel;
import net.fina.server.i18n.entity.Language;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.mdt.api.MDTNodeLocal;
import net.fina.server.mdt.entity.MDTComparison;
import net.fina.server.mdt.entity.MDTDependentNode;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.processing.ProcessException;
import net.fina.server.processing.api.FinaCrossFileValidator;
import net.fina.server.processing.api.ProcessingLocal;
import net.fina.server.processing.api.ProcessingStoreLocal;
import net.fina.server.processing.impl.FspFileProcessDoneEvent;
import net.fina.server.processing.impl.ReturnLockSingleton;
import net.fina.server.processing.impl.ReturnStatusChangeEvent;
import net.fina.server.processing.model.ComparisonItem;
import net.fina.server.processing.model.ProcessItem;
import net.fina.server.returns.xml.Return;
import net.fina.server.security.api.PropertyLocal;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.logging.Logger;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Stateless
@Local(FsopImportLocal.class)
@Interceptors(RecordingAuditor.class)
public class FsopImportSession implements FsopImportLocal {
    private final Logger log = Logger.getLogger(getClass());

    @EJB
    private FsopImportStoreLocal fsopImportStoreLocal;
    @EJB
    private FsopTemplateLocal fsopTemplateLocal;
    @EJB
    private ProcessingLocal processingLocal;
    @EJB
    private ProcessingStoreLocal processingStoreLocal;
    @EJB
    private PropertyLocal propertyLocal;
    @EJB
    private ReturnLockSingleton returnLock;

    @Resource
    private EJBContext ejbContext;

    @Inject
    private JMSContext context;

    @Resource(lookup = "java:/jms/queue/FsopProcessQueue")
    private jakarta.jms.Queue queue;

    @Inject
    private Event<ImportedReturnUpdateEvent> importedReturnUpdateEvent;
    @Inject
    private Event<RemoveUploadFileQueueEvent> removeUploadFileQueueEvent;
    @Inject
    private FinaCrossFileValidator finaCrossFileValidator;
    @Inject
    private Event<FspFileProcessDoneEvent> fsopFileProcessDoneEvent;
    @Inject
    private MDTNodeLocal mdtNodeLocal;
    @Inject
    private UploadFileQueueStoreLocal uploadFileQueueStoreLocal;

    @Override
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void processFiles(List<UploadFileQueue> queueFiles) {
        for (UploadFileQueue queueFile : queueFiles) {
            try {
                //Process file
                log.info("Sending file to JMS process queue : " + queueFile);
                context.createProducer().send(queue, context.createObjectMessage(queueFile));
                log.info("File to JMS process queue Sent : " + queueFile);
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
                log.error("queueFile:" + queueFile);
                log.error("queueFiles:" + queueFiles);
                queueFile.setStatus(UploadFileQueueStatus.UNDEFINED);
                log.info("Reset Upload File QUEUE status : " + queueFile);
                uploadFileQueueStoreLocal.changeStatus(Collections.singletonList(queueFile), UploadFileQueueStatus.UNDEFINED);
            }
        }
    }

    @Override
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 3)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void processFile(UploadFileQueue queueFile) {
        StatisticsLogger statLog = new StatisticsLogger("FSOP Package import. File id:" + queueFile.getFileId() + ", User id:" + queueFile.getUserId(), log, Logger.Level.INFO);
        statLog.logMessage("Start upload file process");

        List<FsopImportedReturnMetaModel> importedReturns = null;

        String uploadFileStatusMessage = null;

        Map<FsopImportedReturnMetaModel, net.fina.server.returns.entity.Return> returns = new HashMap<>();
        Map<FsopImportedReturnMetaModel, Return> xmlReturns = new HashMap<>();

        try {
            statLog.logStage("Load file imported returns");

            importedReturns = fsopImportStoreLocal.getFileImportedReturns(queueFile.getFileId());

            if (!importedReturns.isEmpty()) {

                statLog.logStage("Check file imported returns");
                if (checkFileImportedReturns(importedReturns, returns, xmlReturns)) {

                    statLog.logStage("Import...");
                    statLog.logStage("Load all mdt nodes by parentId");
                    Map<Long, List<MDTNode>> allMdtNodesByParentId = fsopImportStoreLocal.loadAllNodesByParentId();

                    log.info("Get all mdt dependent nodes");
                    final List<MDTDependentNode> mdtDependentNodes = processingStoreLocal.loadAllMdtDependentNodes();

                    log.info("Load Comparisons.");
                    final Map<Long, List<ComparisonItem>> comparisons = processingStoreLocal.loadComparisons();
                    List<MDTComparison> mdtComparisons = mdtNodeLocal.loadComparisons(new HashMap<>(), null);

                    Map<Long, ProcessItem> packageReturnItemsById = new HashMap<>();

                    int index = 1;
                    for (Map.Entry<FsopImportedReturnMetaModel, net.fina.server.returns.entity.Return> e : returns.entrySet()) {

                        net.fina.server.returns.entity.Return ret = e.getValue();
                        FsopImportedReturnMetaModel ir = e.getKey();

                        StatisticsLogger importStatLog = new StatisticsLogger("Import XML. Xml Id:" + ir.getId() + ", Return id:" + ret.getId() + ", new:" + ret.isCreate() + ", " + index++ + "/" + returns.size(), log, Logger.Level.INFO);
                        importStatLog.logMessage("Start import");

                        importStatLog.logStage("Load template");
                        List<ProcessItem> returnTemplate = fsopTemplateLocal.getReturnTemplate(ret, allMdtNodesByParentId, ir.getLangId(), mdtDependentNodes);

                        importStatLog.logStage("Xml items to return");
                        Map<Long, ProcessItem> returnItems = fsopTemplateLocal.setReturnValues(ir, xmlReturns.get(ir), returnTemplate);
                        packageReturnItemsById.putAll(returnItems);

                        importStatLog.logStage("Save return and xml relation");
                        fsopImportStoreLocal.saveReturnXmlRelation(ir.getId(), ret.getId());

                        importStatLog.close();
                    }

                    statLog.logStage("Process file returns");
                    Long[] returnIds = new Long[returns.size()];
                    int i = 0;
                    for (net.fina.server.returns.entity.Return r : returns.values()) {
                        returnIds[i] = r.getId();
                        i++;
                    }

                    long langId = importedReturns.stream().findFirst().get().getLangId();
                    Map<Long, ProcessResult> processResultMap = processingLocal.processSameTransaction(queueFile.getUserId(), langId, false, packageReturnItemsById, mdtDependentNodes, allMdtNodesByParentId, comparisons, mdtComparisons, returnIds);

                    statLog.logStage("Check process result");
                    checkProcessResult(processResultMap, returns);

                    // validate cross file comparisons
                    statLog.logStage("Validate Cross File Comparisons ");
                    fsopFileProcessDoneEvent.fire(new FspFileProcessDoneEvent(returns, processResultMap, comparisons, packageReturnItemsById, importedReturns, queueFile, langId, statLog));
                    return;
                } else {
                    //Find error files
                    List<String> errorReturnCodes = importedReturns
                            .stream()
                            .filter(importedReturn -> importedReturn.getStatus() != ImportStatus.IN_PROGRESS)
                            .map(FsopImportedReturnMetaModel::getReturnCode)
                            .toList();

                    //Set statuses
                    importedReturns.stream().filter(importedReturn -> importedReturn.getStatus() == ImportStatus.IN_PROGRESS).forEach(importedReturn -> {
                        importedReturn.setStatus(ImportStatus.DECLINED);
                        importedReturn.setMessage("Package isn't full valid. Error file(s): " + errorReturnCodes);
                    });
                }
                postFileProcess(statLog, returns, UploadFileStatus.ERROR, uploadFileStatusMessage, importedReturns, queueFile);

            } else {
                this.removeUploadFileQueueEvent.fire(new RemoveUploadFileQueueEvent(queueFile));
            }
        } catch (Throwable t) {

            //Ignore Lock exception
            if (t.getCause() instanceof jakarta.persistence.PersistenceException) {
                statLog.logStage("Unlock returns");
                for (net.fina.server.returns.entity.Return r : returns.values()) {
                    returnLock.unlock(r.getSchedule().getId(), r.getReturnVersion().getId(), r.getId());
                }
                throw new RuntimeException(t);
            }

            statLog.logStage("Change imported return statuses");
            String errorMessage = t.getMessage();
            if (errorMessage == null) {
                errorMessage = "Unexpected error. Please contact administrator.";
            }

            uploadFileStatusMessage = errorMessage;

            if (importedReturns != null) {
                FsopImportedReturnMetaModel errorImportedReturn = null;
                if (t instanceof FsopTemplateException) {
                    errorImportedReturn = ((FsopTemplateException) t).getImportedReturn();
                }

                //Check process exception
                ProcessException processException = null;
                if (t instanceof ProcessException) {
                    processException = (ProcessException) t;
                }

                for (FsopImportedReturnMetaModel importedReturn : importedReturns) {

                    //Issue: BOM-60
                    if (importedReturn.getStatus() != ImportStatus.DECLINED) {

                        if (errorImportedReturn != null && errorImportedReturn.getId() != importedReturn.getId()) {
                            errorMessage = "Error(s) occurred in return " + errorImportedReturn.getReturnCode() + " " + errorMessage;
                        }
                        if (processException == null || processException.getErrorImportedReturnIds() == null || !processException.getErrorImportedReturnIds().contains(importedReturn.getId())) {
                            importedReturn.setMessage(errorMessage);
                            importedReturn.setStatus(ImportStatus.ERRORS);
                        }
                    }
                }
            }

            log.error(t.getMessage(), t);
            postFileProcess(statLog, returns, UploadFileStatus.ERROR, uploadFileStatusMessage, importedReturns, queueFile);
            //javax.ejb.EJBException: java.lang.RuntimeException: javax.transaction.RollbackException:
            //ARJUNA016083: Can't register synchronization because the transaction is in aborted state
            if (!ejbContext.getRollbackOnly()) {
                ejbContext.setRollbackOnly();
            }
        } finally {
            statLog.close();
        }
    }

    private void postFileProcess(StatisticsLogger statLog, Map<FsopImportedReturnMetaModel, net.fina.server.returns.entity.Return> returns,
                                 UploadFileStatus uploadFileStatus, String uploadFileStatusMessage,
                                 List<FsopImportedReturnMetaModel> importedReturns, UploadFileQueue queueFile) {
        statLog.logStage("Unlock returns");
        for (net.fina.server.returns.entity.Return r : returns.values()) {
            returnLock.unlock(r.getSchedule().getId(), r.getReturnVersion().getId(), r.getId());
        }

        statLog.logStage("Change imported return statuses, upload file status and save file process mail message");
        ImportedReturnUpdateEvent importedReturnEvent = new ImportedReturnUpdateEvent(importedReturns, queueFile);
        importedReturnEvent.setStatus(uploadFileStatus);
        importedReturnEvent.setMessage(uploadFileStatusMessage);
        this.importedReturnUpdateEvent.fire(importedReturnEvent);

        statLog.logStage("Remove upload file queue");
        RemoveUploadFileQueueEvent uploadFileQueue = new RemoveUploadFileQueueEvent(queueFile);
        this.removeUploadFileQueueEvent.fire(uploadFileQueue);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void afterProcessDoneEventHandler(@Observes(during = TransactionPhase.AFTER_SUCCESS) FspFileProcessDoneEvent event) {
        StatisticsLogger statLog = event.statLog;
        UploadFileStatus uploadFileStatus = null;
        String uploadFileStatusErrorMessage = null;
        try {
            boolean hasErrors = event.processResultMap.values().stream().anyMatch(pr -> pr.getStatus().equals(ProcessStatus.STATUS_ERRORS));
            boolean hasCrossFileComparisons = event.processResultMap.values().stream().anyMatch(ProcessResult::validatePostProcessComparisons);

            if (!hasErrors && hasCrossFileComparisons) {
                final Language language = processingStoreLocal.getLanguage(event.langId);
                Collection<net.fina.server.returns.entity.Return> processedReturns = event.returns.values();

                //validate cross file comparisons
                Map<Long, ProcessResult> result = finaCrossFileValidator.validatePostProcessComparisons(processedReturns, event.comparisons, event.packageReturnItemsById.values(), language);

                for (Map.Entry<Long, ProcessResult> entry : result.entrySet()) {
                    long returnId = entry.getKey();
                    ProcessResult pr = entry.getValue();
                    if (pr.getStatus().equals(ProcessStatus.STATUS_ERRORS)) {
                        event.processResultMap.put(returnId, pr);
                        String errorMessage = pr.getProcessNote();

                        ReturnStatusChangeEvent rStatusChangeEvent = new ReturnStatusChangeEvent(returnId, ProcessStatus.STATUS_ERRORS, processingStoreLocal.getVersionId(returnId), event.userId, errorMessage, null);
                        processingLocal.changeReturnStatus(rStatusChangeEvent);
                    }
                }

                checkProcessResult(result, event.returns);
            }
        } catch (Exception t) {
            uploadFileStatus = UploadFileStatus.ERROR;
            uploadFileStatusErrorMessage = t.getMessage();
            if (uploadFileStatusErrorMessage == null) {
                uploadFileStatusErrorMessage = "Unexpected error. Please contact administrator.";
            }
            log.error(t.getMessage(), t);
        } finally {
            postFileProcess(statLog, event.returns, uploadFileStatus, uploadFileStatusErrorMessage, event.importedReturns, event.queueFile);
        }
    }

    private boolean checkFileImportedReturns(List<FsopImportedReturnMetaModel> importedReturns, Map<FsopImportedReturnMetaModel, net.fina.server.returns.entity.Return> returns, Map<FsopImportedReturnMetaModel, Return> xmlReturns) {
        for (FsopImportedReturnMetaModel importedReturn : importedReturns) {
            try (StatisticsLogger statLog = new StatisticsLogger("Imported return validation. id: " + importedReturn.getId());) {

                statLog.logMessage("Start");

                importedReturn.setStatus(ImportStatus.IN_PROGRESS);
                importedReturn.setImportStart(new Date());
                importedReturn.setMessage("In Progress");
                try {
                    statLog.logStage("Parse xml");
                    ImportedReturnValidationEventHandler handler = new ImportedReturnValidationEventHandler();
                    Return ret = FsopXmlParser.getInstance().convert(importedReturn.getContent(), handler);

                    validateImportedReturnParsing(importedReturn, handler);

                    if (ret != null) {

                        xmlReturns.put(importedReturn, ret);

                        statLog.logStage("Check return version");
                        long versionId = checkReturnVersion(importedReturn, ret);
                        if (versionId > 0) {
                            statLog.logStage("Check schedule");
                            long scheduleId = checkReturnSchedule(importedReturn, ret);
                            if (scheduleId > 0) {
                                statLog.logStage("Find return");
                                net.fina.server.returns.entity.Return returnEntity = findReturn(scheduleId, versionId);

                                boolean acceptToProcess = false;

                                if (returnEntity != null) {
                                    statLog.logStage("Check return status");
                                    if (checkReturnStatus(importedReturn, returnEntity)) {
                                        acceptToProcess = true;
                                    }
                                } else {
                                    statLog.logStage("Create new return");
                                    returnEntity = fsopImportStoreLocal.createReturn(scheduleId, versionId);
                                    acceptToProcess = true;
                                }

                                if (acceptToProcess) {

                                    //Update last version
                                    //ISSUE: CBK-271
                                    fsopImportStoreLocal.updateLastVersion(scheduleId, versionId);

                                    if (returnLock.isLock(scheduleId, versionId)) {
                                        throw new ConcurrentModificationException("Return is lock. schedule id:" + scheduleId + ", version Id:" + versionId);
                                    }

                                    //Lock return
                                    returnLock.lock(scheduleId, versionId, returnEntity.getId());

                                    returns.put(importedReturn, returnEntity);
                                }
                            }
                        }
                    }
                } catch (Throwable t) {
                    importedReturn.setStatus(ImportStatus.ERRORS);
                    String errorMessage = t.getMessage();
                    if (errorMessage == null) {
                        errorMessage = "Unexpected error. Please contact administrator.";
                    }
                    importedReturn.setMessage(errorMessage);
                }
            }
        }

        boolean packageRejectEnable = packageRejectEnable();

        for (FsopImportedReturnMetaModel importedReturn : importedReturns) {
            if (packageRejectEnable) {
                if (importedReturn.getStatus() != ImportStatus.IN_PROGRESS) {
                    return false;
                }
            } else {
                if (importedReturn.getStatus() == ImportStatus.IN_PROGRESS) {
                    return true;
                }
            }
        }

        return true;
    }

    private void validateImportedReturnParsing(FsopImportedReturnMetaModel importedReturn, ImportedReturnValidationEventHandler handler) {
        if (!handler.getMessages().isEmpty()) {
            StringBuilder errorMessage = new StringBuilder();
            for (String message : handler.getMessages()) {
                errorMessage.append(message);
                errorMessage.append("\n");
            }
            importedReturn.setMessage(errorMessage.toString());
            importedReturn.setStatus(ImportStatus.ERRORS);
        }
    }

    private long checkReturnVersion(FsopImportedReturnMetaModel importedReturn, Return ret) {
        long versionId = -1;
        String versionCode = "";
        if (ret.getHeader() != null && ret.getHeader().getVer() != null) {
            versionCode = ret.getHeader().getVer().trim();
            versionId = fsopImportStoreLocal.findReturnVersionByCode(versionCode);
        }
        if (versionId > 0) {
            return versionId;
        }
        importedReturn.setMessage("Return version does not exists or doesn't have right. code: " + versionCode);
        importedReturn.setStatus(ImportStatus.DECLINED);
        return versionId;
    }

    private long checkReturnSchedule(FsopImportedReturnMetaModel importedReturn, Return ret) {
        long scheduleId = -1;
        if (ret.getHeader().getBankCode() != null && ret.getHeader().getReturnCode() != null) {
            scheduleId = fsopImportStoreLocal.findSchedule(importedReturn.getPeriodStart(), importedReturn.getPeriodEnd(), ret.getHeader().getBankCode().trim(), ret.getHeader().getReturnCode().trim());
        }
        if (scheduleId > 0) {
            return scheduleId;
        }
        importedReturn.setMessage("Schedule does not exists. Period [ from = " + importedReturn.getPeriodStart() + " , to = " + importedReturn.getPeriodEnd() + " ]");
        importedReturn.setStatus(ImportStatus.DECLINED);
        return scheduleId;

    }

    private net.fina.server.returns.entity.Return findReturn(long scheduleId, long versionId) {
        return fsopImportStoreLocal.findReturn(scheduleId, versionId);
    }

    private boolean checkReturnStatus(FsopImportedReturnMetaModel importedReturn, net.fina.server.returns.entity.Return ret) {
        List<ProcessStatus> returnStatuses = fsopImportStoreLocal.getReturnStatuses(ret.getId(), ret.getReturnVersion().getId());
        for (ProcessStatus returnStatus : returnStatuses) {
            if (returnStatus == ProcessStatus.STATUS_VALIDATED) {
                importedReturn.setStatus(ImportStatus.DECLINED);
                importedReturn.setMessage("Error: Unable import validated return\n");
                return false;
            }
            if (returnStatus == ProcessStatus.STATUS_ACCEPTED) {
                importedReturn.setStatus(ImportStatus.DECLINED);
                importedReturn.setMessage("Warn: Unable to import accepted return\n");
                return false;
            }
            String statusesString = propertyLocal.getSystemProperty(PropertyKeys.UNCHANGED_RETURN_STATUSES);
            if (statusesString != null && (!statusesString.isEmpty())) {
                String[] statuses = statusesString.split("[,|;]");
                for (String statusString : statuses) {
                    if (Integer.parseInt(statusString.trim()) == returnStatus.ordinal()) {
                        importedReturn.setStatus(ImportStatus.DECLINED);
                        importedReturn.setMessage("Status Declined: Unable to re-import existing  return with the status:  " + returnStatus.name() + ".   Please contact FinA Administrator\n");
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void checkProcessResult(Map<Long, ProcessResult> processResultMap, Map<FsopImportedReturnMetaModel, net.fina.server.returns.entity.Return> returns) throws ProcessException {
        boolean packageReject = packageRejectEnable();

        List<String> errorFiles = new ArrayList<>();
        List<Integer> errorImportedReturnIds = new ArrayList<>();

        if (packageReject) {
            for (Map.Entry<FsopImportedReturnMetaModel, net.fina.server.returns.entity.Return> e : returns.entrySet()) {

                ProcessResult processResult = processResultMap.get(e.getValue().getId());

                if (processResult.getStatus() != ProcessStatus.STATUS_PROCESSED) {
                    errorFiles.add(e.getKey().getReturnCode());
                    errorImportedReturnIds.add(e.getKey().getId());
                }
            }
        }

        for (Map.Entry<FsopImportedReturnMetaModel, net.fina.server.returns.entity.Return> e : returns.entrySet()) {
            changeImportedReturnStatus(e.getKey(), processResultMap.get(e.getValue().getId()));
        }

        for (Map.Entry<FsopImportedReturnMetaModel, net.fina.server.returns.entity.Return> e : returns.entrySet()) {

            ProcessResult processResult = processResultMap.get(e.getValue().getId());

            if (packageReject && processResult.getStatus() != ProcessStatus.STATUS_PROCESSED) {
                throw new ProcessException("Reject Package. Error file(s):" + errorFiles, errorImportedReturnIds);
            }
        }
    }

    private void changeImportedReturnStatus(FsopImportedReturnMetaModel importedReturn, ProcessResult processResult) throws ProcessException {
        if (processResult != null) {
            if (importedReturn.getStatus() == ImportStatus.IMPORTED) {
                switch (processResult.getStatus()) {
                    case STATUS_SCHEDULED:
                    case STATUS_CREATED:
                    case STATUS_AMENDED:
                    case STATUS_IMPORTED:
                    case STATUS_PROCESSED:
                    case STATUS_VALIDATED:
                    case STATUS_RESETED:
                    case STATUS_ACCEPTED:
                    case STATUS_REJECTED:
                    case STATUS_LOADED:
                    case STATUS_QUEUED: {
                        importedReturn.setMessage(processResult.getProcessNote());
                        break;
                    }
                    case STATUS_ERRORS: {
                        importedReturn.setStatus(ImportStatus.ERRORS);
                        importedReturn.setMessage(processResult.getProcessNote());
                        break;
                    }
                }
            }
        } else {
            throw new ProcessException("Processing result is NULL");
        }
    }

    private boolean packageRejectEnable() {
        String enable = propertyLocal.getSystemProperty(PropertyKeys.PROCESSING_PACKAGE_REJECT_ENABLE);
        return enable != null && (!enable.isEmpty()) && (Integer.parseInt(enable) > 0);
    }
}
