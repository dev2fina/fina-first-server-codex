package net.fina.server.fsop.impl;

import jakarta.ejb.*;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TemporalType;
import net.fina.common.client.constants.ImportStatus;
import net.fina.common.client.dcs.UploadFileStatus;
import net.fina.common.client.returns.ImportedXmlReturnStatus;
import net.fina.common.client.returns.ProcessStatus;
import net.fina.common.server.util.CommonUtil;
import net.fina.server.dcs.jms.DcsJMSClient;
import net.fina.server.dcs.uploadfile.entity.UploadFile;
import net.fina.server.dcs.uploadfile.impl.event.UploadFileStatusEvent;
import net.fina.server.fi.api.FiLocal;
import net.fina.server.fi.entity.Fi;
import net.fina.server.fsop.api.FsopImportStoreLocal;
import net.fina.server.fsop.event.DcsUploadFileProcessStatusUpdateEvent;
import net.fina.server.fsop.event.ImportedReturnUpdateEvent;
import net.fina.server.fsop.event.ProcessMailMessageEvent;
import net.fina.server.fsop.model.FsopImportedReturnMetaModel;
import net.fina.server.i18n.api.LanguageLocal;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.jcr.impl.FileContentManagementSession;
import net.fina.server.mdt.api.MDTNodeLocal;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.returns.api.ReturnLocal;
import net.fina.server.returns.api.ReturnVersionLocal;
import net.fina.server.returns.api.ScheduleLocal;
import net.fina.server.returns.entity.*;
import net.fina.server.returns.util.ImportedReturnErrorTextUtil;

import java.util.*;
import java.util.stream.Collectors;

@Stateless
@Local(FsopImportStoreLocal.class)
@Interceptors(RecordingAuditor.class)
public class FsopImportStoreSession implements FsopImportStoreLocal {

    @Inject
    private EntityManager em;

    @EJB
    private ScheduleLocal scheduleLocal;
    @EJB
    private ReturnVersionLocal returnVersionLocal;
    @EJB
    private MDTNodeLocal mdtNodeLocal;
    @EJB
    private ReturnLocal returnLocal;
    @EJB
    private FileContentManagementSession fileContentManagementSession;

    @Inject
    private FiLocal fiLocal;

    @Inject
    private LanguageLocal languageLocal;

    @Inject
    private Event<ProcessMailMessageEvent> processMailMessageEvent;

    @Inject
    private Event<DcsUploadFileProcessStatusUpdateEvent> dcsUploadFileProcessStatusUpdateEvent;

    @Inject
    private Event<UploadFileStatusEvent> uploadFileStatusEvent;
    @Inject
    private DcsJMSClient jmsClient;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<FsopImportedReturnMetaModel> getFileImportedReturns(long fileId) {
        List<ImportedReturn> returns = em.createQuery("select ir from IN_IMPORTED_RETURNS ir " +
                        "where ir.uploadFile.id=:fileId", ImportedReturn.class)
                .setParameter("fileId", fileId)
                .getResultList();

        return returns.stream().map(ir -> new FsopImportedReturnMetaModel(ir.getId(),
                fileContentManagementSession.loadImportedReturnContent(ir),
                ir.getReturnCode(), ir.getPeriodStart(), ir.getPeriodEnd(), ir.getLanguage().getId(),
                ir.getLanguage().getNumberFormat(), ir.getLanguage().getDateFormat(), ir.getLanguage().getDateTimeFormat(), ir.getVersionCode())).collect(Collectors.toList());
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateImportedReturns(@Observes ImportedReturnUpdateEvent event) {
        if (event.getImportedReturns() != null) {
            for (FsopImportedReturnMetaModel importedReturn : event.getImportedReturns()) {
                em.createQuery("update IN_IMPORTED_RETURNS ir set ir.status=:status, ir.message=:message, ir.importStart=:importStart,ir.importEnd=:importEnd where ir.id=:id")
                        .setParameter("status", importedReturn.getStatus())
                        .setParameter("message", importedReturn.getMessage())
                        .setParameter("importStart", importedReturn.getImportStart())
                        .setParameter("importEnd", new Date())
                        .setParameter("id", importedReturn.getId())
                        .executeUpdate();

                em.createQuery("update IN_IMPORTED_RETURN_ERRORS e set e.errorContent=:errorMessage where e.importedReturnId=:returnId")
                        .setParameter("returnId", (long) importedReturn.getId())
                        .setParameter("errorMessage", ImportedReturnErrorTextUtil.buildErrorMessage(importedReturn))
                        .executeUpdate();

            }
        }

        //Change upload file status
        updateUploadFileStatus(event);

        //Save file process mail message
        ProcessMailMessageEvent messageEvent = new ProcessMailMessageEvent(event.getUploadFileQueue(), event.getImportedReturns());
        this.processMailMessageEvent.fire(messageEvent);

        DcsUploadFileProcessStatusUpdateEvent processStatusUpdateEvent = new DcsUploadFileProcessStatusUpdateEvent(event.getUploadFileQueue().getFileId());
        this.dcsUploadFileProcessStatusUpdateEvent.fire(processStatusUpdateEvent);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void sendDcsUploadFileProcessStatusUpdateMessage(@Observes(during = TransactionPhase.AFTER_SUCCESS) DcsUploadFileProcessStatusUpdateEvent event) {
        UploadFile uploadFile = em.find(UploadFile.class, event.getFileId());

        List<String> users = em.createQuery("select uf.user.login from SYS_UPLOADEDFILE uf where uf.id=:uploadFileId", String.class)
                .setParameter("uploadFileId", event.getFileId())
                .getResultList();
        if (!users.isEmpty()) {
            switch (uploadFile.getType()) {
                case DCS:
                    jmsClient.sendFileUploadMessage(new ArrayList<>(users));
                    break;
                case IMPORT_MANAGER:
                    uploadFileStatusEvent.fire(new UploadFileStatusEvent(
                                    event.getFileId(),
                                    UploadFileStatus.getStatus(uploadFile.getStatus()),
                                    users,
                                    uploadFile.getBankCode(),
                                    getLangCodeFiNameMap(uploadFile.getBankCode()),
                                    uploadFile.getFileName()
                            )
                    );
                    break;
            }
        }
    }

    private void updateUploadFileStatus(ImportedReturnUpdateEvent event) {
        UploadFileStatus status = UploadFileStatus.IMPORTED;

        if (event.getStatus() != null) {
            status = event.getStatus();
        } else {
            if (event.getImportedReturns() != null) {
                for (FsopImportedReturnMetaModel ir : event.getImportedReturns()) {
                    if (ir.getStatus() != ImportStatus.IMPORTED) {
                        status = UploadFileStatus.ERROR;
                        break;
                    }
                }
            }
        }

        //Update reason
        if (event.getMessage() != null) {
            em.createQuery("update SYS_UPLOADEDFILE uf set uf.reason=:reason where uf.id=:fileId ")
                    .setParameter("fileId", event.getUploadFileQueue().getFileId())
                    .setParameter("reason", CommonUtil.left(event.getMessage(), 255))
                    .executeUpdate();
        }

        //Update status
        em.createQuery("update SYS_UPLOADEDFILE uf set uf.status=:status where uf.id=:fileId and uf.status<>:deleteStatus ")
                .setParameter("status", Integer.toString(status.ordinal()))
                .setParameter("fileId", event.getUploadFileQueue().getFileId())
                .setParameter("deleteStatus", Integer.toString(UploadFileStatus.DELETE.ordinal()))
                .executeUpdate();
    }

    @Override
    public Return createReturn(long scheduleId, long versionId) {
        Return ret = new Return();
        ret.setSchedule(scheduleLocal.loadSimpleSchedule(scheduleId));
        ReturnVersion rv = returnVersionLocal.loadSimpleReturnVersion(versionId);
        ret.setReturnVersion(rv);
        ret.setLatestVersion(rv.getId());
        ret.setStatus(ProcessStatus.STATUS_CREATED);
        em.persist(ret);
        ret.setCreate(true);

        em.flush();
        em.clear();

        return ret;
    }

    @Override
    public long findReturnVersionByCode(String versionCode) {
        List<Long> result = em.createQuery("select rv.id from IN_RETURN_VERSIONS  rv where trim(rv.code)=:versionCode", Long.class)
                .setParameter("versionCode", versionCode)
                .getResultList();
        if (result.size() > 0) {
            return result.stream().findFirst().get();
        }
        return -1;
    }

    @Override
    public long findSchedule(Date fromDate, Date toDate, String fiCode, String definitionCode) {
        List<Long> result = em.createQuery("select s.id from IN_SCHEDULES as s where s.period.fromDate=:from and s.period.toDate=:to and trim(s.fi.code)=:fiCode and trim(s.returnDefinition.code)=:definitionCode ", Long.class)
                .setParameter("from", fromDate, TemporalType.DATE)
                .setParameter("to", toDate, TemporalType.DATE)
                .setParameter("fiCode", fiCode)
                .setParameter("definitionCode", definitionCode)
                .getResultList();
        if (result.size() > 0) {
            return result.stream().findFirst().get();
        }
        return -1;
    }

    @Override
//    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Return findReturn(long scheduleId, long versionId) {
        List<Return> returns = em.createQuery("select r from  IN_RETURNS as r where r.schedule.id=:scheduleId and r.returnVersion.id=:returnVersionId ", Return.class)
                .setParameter("scheduleId", scheduleId)
                .setParameter("returnVersionId", versionId)
                .getResultList();
        if (returns.size() > 0) {
            return returns.stream().findFirst().get();
        }
        return null;
    }

    @Override
    public void saveReturnXmlRelation(int importedReturnId, long returnId) {
        em.createQuery("update IN_IMPORTED_XML_RETURN ixr set ixr.status=:status where ixr.returnId=:returnId")
                .setParameter("status", ImportedXmlReturnStatus.ARCHIVE)
                .setParameter("returnId", returnId)
                .executeUpdate();

        ImportedXmlReturn importedXmlReturn = new ImportedXmlReturn();
        importedXmlReturn.setImportedReturnId(importedReturnId);
        importedXmlReturn.setReturnId(returnId);
        importedXmlReturn.setImportTime(new Date());
        importedXmlReturn.setStatus(ImportedXmlReturnStatus.CURRENT);
        em.persist(importedXmlReturn);
        em.flush();
    }

    @Override
    public List<ProcessStatus> getReturnStatuses(long returnId, long versionId) {
        return em.createQuery("select rs.status from IN_RETURNS r, IN(r.statuses) rs where r.id=:returnId and r.returnVersion.id=:versionId and rs.id=(select max(rs.id) from IN_RETURN_STATUSES rs where rs.returns.id=r.id) ", ProcessStatus.class)
                .setParameter("returnId", returnId)
                .setParameter("versionId", versionId)
                .getResultList();
    }

    @Override
    public Map<Long, List<MDTNode>> loadAllNodesByParentId() {
        return mdtNodeLocal.loadAllNodesByParentId();
    }

    @Override
    public List<DefinitionTable> getDefinitionTables(long scheduleId) {
        return em.createQuery("select dt from IN_SCHEDULES s ,IN(s.returnDefinition.definitionTables) dt where s.id=:scheduleId", DefinitionTable.class)
                .setParameter("scheduleId", scheduleId)
                .getResultList();
    }

    @Override
    public void updateLastVersion(long scheduleId, long lastVersionId) {
        returnLocal.updateLastVersion(scheduleId, lastVersionId);
    }

    private Map<String, String> getLangCodeFiNameMap(String fiCode) {
        if (fiCode == null || fiCode.trim().isEmpty()) {
            return new HashMap<>();
        }

        Fi fi = fiLocal.findFiByCode(fiCode);
        Map<String, Long> languageCodeIdMap = languageLocal.getLanguagesCodeIdMap();
        Map<String, String> result = new HashMap<>();

        for (Map.Entry<Long, String> description : fi.getDescription().getDescriptions().entrySet()) {
            for (Map.Entry<String, Long> langCodeId : languageCodeIdMap.entrySet()) {
                if (description.getKey().longValue() == langCodeId.getValue().longValue()) {
                    result.put(langCodeId.getKey(), description.getValue());
                }
            }

        }
        return result;
    }
}
