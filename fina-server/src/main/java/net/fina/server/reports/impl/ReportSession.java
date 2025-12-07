package net.fina.server.reports.impl;

import fina2.period.OOPeriodPK;
import fina2.period.PeriodPK;
import fina2.reportoo.ReportInfo;
import fina2.ui.sheet.openoffice.OOIterator;
import global.namespace.truelicense.api.LicenseManagementException;
import jakarta.annotation.Resource;
import jakarta.ejb.*;
import jakarta.enterprise.concurrent.ManagedExecutorService;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import net.fina.common.client.constants.AuditLogLevel;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.exception.OfficeTypeException;
import net.fina.common.client.filter.ReportFilter;
import net.fina.common.client.mdt.MDTNodeDataTypes;
import net.fina.common.client.property.PropertyKeys;
import net.fina.common.client.reports.ReportConstants;
import net.fina.common.client.reports.ReportType;
import net.fina.common.client.reports.ScheduleReportStatus;
import net.fina.common.server.util.ObjectSerializer;
import net.fina.common.shared.LanguageSampleModel;
import net.fina.common.shared.ReturnConstants;
import net.fina.common.shared.event.ReportContentManagementEvent;
import net.fina.messages.MessagesUtil;
import net.fina.odstoolkit.AbstractFactory;
import net.fina.odstoolkit.AooDocumentPatcher;
import net.fina.odstoolkit.FactoryProducer;
import net.fina.odstoolkit.writer.AooWriterBase;
import net.fina.report.core.*;
import net.fina.report.core.aoo.SpreadsheetManagerImpl;
import net.fina.report.core.api.ReportData;
import net.fina.report.core.api.ReportProcessorConfig;
import net.fina.report.core.api.SpreadsheetManager;
import net.fina.report.model.*;
import net.fina.reporting.impl.ExcelReportGenerator;
import net.fina.reporting.util.ReportingUtil;
import net.fina.server.aoo.AOOServiceManager;
import net.fina.server.aoo.OfficeConfigurationUtil;
import net.fina.server.fi.api.RegionLocal;
import net.fina.server.fi.entity.*;
import net.fina.server.i18n.api.LanguageLocal;
import net.fina.server.i18n.entity.Language;
import net.fina.server.i18n.helper.Description;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.mdt.impl.MDTCacheManager;
import net.fina.server.reg.api.RegReportDataLocal;
import net.fina.server.reports.api.ReportLocal;
import net.fina.server.reports.api.ScheduleReportLocal;
import net.fina.server.reports.api.StoredReportLocal;
import net.fina.server.reports.entity.*;
import net.fina.server.reports.event.ReportAlertsEvent;
import net.fina.server.reports.event.ReportScheduleEvent;
import net.fina.server.reports.model.CriterionFunctionOperation;
import net.fina.server.reports.model.VctSumIfDataItemMetaModel;
import net.fina.server.reports.model.VctSumIfHelper;
import net.fina.server.returns.api.ReturnVersionLocal;
import net.fina.server.returns.entity.Period;
import net.fina.server.returns.entity.PeriodType_;
import net.fina.server.returns.entity.Period_;
import net.fina.server.returns.entity.ReturnStatus;
import net.fina.server.security.api.PropertyLocal;
import net.fina.server.security.api.UserLocal;
import net.fina.server.security.entity.User;
import net.fina.server.security.product.Product;
import net.fina.server.util.DBUtil;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.artofsolving.jodconverter.office.OfficeException;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.logging.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import static net.fina.server.processing.ProcessingUtil.stringTodouble;

/**
 * Created with IntelliJ IDEA.
 * User: nikoloz
 * Date: 7/23/13
 * Time: 12:37 PM
 */
@Stateless
@Local({ReportLocal.class, ReportDataProcessor.class})
@Interceptors(RecordingAuditor.class)
public class ReportSession implements ReportLocal, ReportDataProcessor {

    public static final int REPORT_GENERATE_TRANSACTION_TIMEOUT_HOURS = 24;
    private final Logger log = Logger.getLogger(getClass());
    @Inject
    private EntityManager em;

    @EJB
    private UserLocal current;
    @EJB
    private MDTCacheManager mdtCacheManager;
    @EJB
    private AOOServiceManager aooServiceManager;
    @EJB
    private RegionLocal regionLocal;
    @EJB
    private StoredReportLocal storedReportLocal;
    @EJB
    private ScheduleReportLocal scheduleReportLocal;
    @EJB
    private PropertyLocal propertyLocal;
    @EJB
    private ReportGeneratorProcessor reportGeneratorProcessor;
    @EJB
    private LanguageLocal languageLocal;

    @Inject
    private ReportModificationAuditLogger reportModificationAuditLogger;

    @Inject
    private Event<ReportAlertsEvent> reportAlertsEvent;

    @Inject
    private Event<ReportContentManagementEvent> reportContentManagementEventEvent;

    @Inject
    private Event<ReportScheduleEvent> reportScheduleEvent;

    @Inject
    private RegReportDataLocal regReportDataLocal;

    @Resource
    private ManagedExecutorService managedExecutorService;

    @Inject
    private ReturnVersionLocal versionLocal;

    @Override
    public List<Report> load(Map<ReportFilter, Object> filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Report> query = cb.createQuery(Report.class);
        Root<Report> report = query.from(Report.class);

        query.orderBy(cb.asc(report.get(Report_.sequence)), cb.asc(cb.lower(report.get(Report_.code))));

        Selection[] selections = new Selection[]{report.get(Report_.id), report.get(Report_.version), report.get(Report_.parentId), report.get(Report_.code), report.get(Report_.description), report.get(Report_.sequence), report.get(Report_.type), report.get(Report_.reportType)};

        query.select(cb.construct(Report.class, selections));

        List<Predicate> predicates = getFilterPredicate(cb, report, filter);
        boolean loadAll = (filter.get(ReportFilter.LOAD_ALL) instanceof Boolean && (boolean) filter.get(ReportFilter.LOAD_ALL));

        Integer reportType = filter.get(ReportFilter.TYPE) != null ? (int) filter.get(ReportFilter.TYPE) : null;

        if ((!loadAll) && (reportType == null || reportType == ReportConstants.NODETYPE_REPORT)) {
            List<Predicate> userReportPredicates = DBUtil.get().buildPredicate(report.get(Report_.id), current.getCallerPrincipal().getReports());
            userReportPredicates.add(cb.equal(report.get(Report_.type), ReportConstants.NODETYPE_FOLDER));
            predicates.add(cb.and(cb.or(userReportPredicates.toArray(new Predicate[0]))));
        }
        if (!predicates.isEmpty()) {
            query.where(predicates.toArray(new Predicate[0]));
        }
        Query loadQuery = em.createQuery(query);

        if (filter != null) {
            Integer offset = (Integer) filter.get(ReportFilter.OFFSET);
            if (offset != null && offset >= 0) {
                loadQuery.setFirstResult(offset);
            }

            Integer limit = (Integer) filter.get(ReportFilter.LIMIT);
            if (limit != null && limit > 0) {
                loadQuery.setMaxResults(limit);
            }
        }

        return loadQuery.getResultList();
    }

    @Override
    public long count(Map<ReportFilter, Object> filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Report> report = query.from(Report.class);

        query.select(cb.count(report.get(Report_.id)));

        List<Predicate> predicates = getFilterPredicate(cb, report, filter);
        if (!predicates.isEmpty()) {
            query.where(predicates.toArray(new Predicate[0]));
        }

        return em.createQuery(query).getSingleResult();
    }

    private List<Predicate> getFilterPredicate(CriteriaBuilder cb, Root<Report> report, Map<ReportFilter, Object> filter) {
        List<Predicate> predicates = new ArrayList<>();
        if (filter != null) {
            for (Map.Entry<ReportFilter, Object> entry : filter.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }
                switch (entry.getKey()) {
                    case FOLDER_ID:
                        predicates.add(cb.equal(report.get(Report_.parentId), entry.getValue()));
                        break;
                    case USER_ID:
                        predicates.add(report.get(Report_.id).in(current.getCallerPrincipal().getReports()));
                        break;
                    case ROLE_ID:
                        break;
                    case CODE:
                        predicates.add(cb.like(report.get(Report_.code), entry.getValue().toString()));
                        break;
                    case REPORT_TYPE:

                        Object reportTypeObject = entry.getValue();

                        if (reportTypeObject == ReportType.EXCEL) {
                            predicates.add(cb.or(cb.equal(report.get(Report_.reportType), reportTypeObject), cb.equal(report.get(Report_.type), 1)));
                        } else if (reportTypeObject instanceof ReportType) {
                            predicates.add(cb.equal(report.get(Report_.reportType), reportTypeObject));
                        } else if (reportTypeObject instanceof List) {

                            List<ReportType> reportTypes = (List<ReportType>) reportTypeObject;

                            Predicate[] reportTypePredicates = new Predicate[reportTypes.size()];

                            for (int i = 0; i < reportTypes.size(); i++) {
                                reportTypePredicates[i] = cb.equal(report.get(Report_.reportType), reportTypes.get(i));
                            }

                            predicates.add(cb.or(reportTypePredicates));
                        }

                        break;
                    case TYPE:
                        predicates.add(cb.equal(report.get(Report_.type), entry.getValue()));
                        break;
                }
            }
        }
        return predicates;
    }

    @Override

    public Report createEmptyReport(int parentId, String code, Description description, Integer sequence, int type, ReportType reportType) throws FinATypeException {

        sequence = findMaxChildSequence(parentId) + 1;

        Report report = new Report();
        report.setCode(code);
        report.setDescription(description);
        report.setParentId(parentId);
        report.setSequence(sequence);
        report.setReportType(reportType == null ? ReportType.DEFAULT : reportType);

        //1 - folder
        //2 - report
        report.setType(type);

        if (!checkIsCodeUnique(report)) {
            throw new FinATypeException(FinATypeException.Type.CODE_UNIQUE);
        }

        try {
            if (type == 2) {
                if (reportType == ReportType.EXCEL) {
                    report.setTemplate(ReportingUtil.createEmptySpreadsheetDocument());
                } else {
                    AbstractFactory factory = FactoryProducer.getFactory("odftoolkit");
                    AooWriterBase aooReader = factory.getAooWriter();

                    report.setTemplate(aooReader.createEmptySpreadsheetDocument());
                }
                ReportInfo reportInfo = new ReportInfo();
                report.setInfo(ObjectSerializer.serialize(reportInfo));
            }

            em.persist(report);

            //Add Current user report
            User user = em.find(User.class, current.getCurrentUserId());
            user.getReports().add(report);

            //Caller principal
            current.getCallerPrincipal().getReports().add(report.getId());

            if (reportType == ReportType.EXCEL) {
                ReportTemplate reportTemplate = new ReportTemplate();
                ReportTemplatePk templatePk = new ReportTemplatePk();
                templatePk.setLangId((long) report.getDescription().getDescriptions().keySet().toArray()[0]);
                templatePk.setReportId(report.getId());
                reportTemplate.setReportTemplatePk(templatePk);
                reportTemplate.setTemplate(report.getTemplate());
                saveReportTemplate(reportTemplate);
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return report;
    }

    @Override

    public void delete(Integer... reportIds) throws FinATypeException {
        if (reportIds != null && reportIds.length != 0) {
            List<Integer> ids = new ArrayList<>();
            Map<Integer, List<String>> usedIds = new HashMap<>();
            List<String> errors;

            for (Integer id : reportIds) {
                errors = new ArrayList<>();
                if (checkStoredReport(id)) {
                    errors.add(MessagesUtil.getString("net.fina.exception.reportDependencyInOutStoredReports"));
                }
                if (checkScheduledReport(id)) {
                    errors.add(MessagesUtil.getString("net.fina.exception.reportDependencyInScheduledReports"));
                }

                if (!errors.isEmpty()) {
                    usedIds.put(id, errors);
                } else {
                    ids.add(id);
                }
            }

            if (!usedIds.isEmpty()) {
                StringBuilder errorMessage = new StringBuilder();

                for (Map.Entry<Integer, List<String>> entry : usedIds.entrySet()) {
                    int id = entry.getKey();
                    errorMessage.append("Report: ").append(findById(id).getCode()).append('\n');
                    entry.getValue().forEach(message -> errorMessage.append(message).append('\n'));
                }

                throw new FinATypeException(errorMessage.toString());
            }
            //Delete from user reports
            em.createNativeQuery("delete from SYS_USER_REPORTS where REPORTID in (:reportIds)").setParameter("reportIds", ids).executeUpdate();

            //Delete Report Templates
            em.createQuery("delete from OUT_REPORTS_LANG  rl where rl.reportTemplatePk.reportId in(:reportIds) and rl.reportTemplatePk.reportId in(:userAndRoleReports) ").setParameter("reportIds", ids).setParameter("userAndRoleReports", current.getCallerPrincipal().getReports()).executeUpdate();

            //Delete report(s)
            List<Integer> reportsIds = em.createQuery("select r.id from OUT_REPORTS r where r.id in(:reportIds) and r.id in(:userAndRoleReports)", Integer.class).setParameter("reportIds", ids).setParameter("userAndRoleReports", current.getCallerPrincipal().getReports()).getResultList();
            deleteOutReportByIds(reportsIds);

            // Delete folders if it has no children.
            List<Integer> folders = em.createQuery("select r.id from OUT_REPORTS r where r.id in(:reportIds) and r.type = 1 ", Integer.class).setParameter("reportIds", ids).getResultList();

            if (!folders.isEmpty()) {
                ids = new ArrayList<>();
                for (Integer fId : folders) {
                    if (loadChildReportId(fId).isEmpty()) {
                        ids.add(fId);
                    }
                }
                if (!ids.isEmpty()) {
                    deleteOutReportByIds(ids);
                }
            }
        }
    }

    @Override
    public void deleteEmptyReportFolder(int folderId) throws FinATypeException {
        Report report = em.find(Report.class, folderId);
        if (report == null || report.getType() != 1 || !loadChildReportId(folderId).isEmpty()) {
            throw new FinATypeException(FinATypeException.Type.GENERAL_ERROR);
        }
        em.remove(report);
    }

    private boolean checkStoredReport(Integer reportId) {
        return em.createQuery("select sr.reportPk.reportId from OUT_STORED_REPORTS sr where sr.reportPk.reportId=:reportId").setParameter("reportId", reportId).getResultList().size() != 0;
    }

    private boolean checkScheduledReport(Integer reportId) {
        return em.createQuery("select rs.reportId from OUT_REPORTS_SCHEDULE rs where rs.reportId=:reportId").setParameter("reportId", reportId).getResultList().size() != 0;
    }

    @Override

    public List<Integer> loadChildReportId(int parentId) {//TODO
        Query query = em.createQuery("SELECT r.id FROM  OUT_REPORTS r WHERE r.parentId=:parentId");
        query.setParameter("parentId", parentId);
        return query.getResultList();
    }

    @Override
    @TransactionTimeout(unit = TimeUnit.HOURS, value = REPORT_GENERATE_TRANSACTION_TIMEOUT_HOURS)
    public Report generate(final ReportModel reportModel, final long langId) throws Exception {
        final long userId = current.getCurrentUserId();
        return generate(reportModel, langId, userId);
    }

    /**
     * @param reportModel report model for generate
     * @param langId      generate report template language
     * @param userId      actor user id
     * @return generated report entity
     * @throws Exception
     */
    @Override
    @TransactionTimeout(unit = TimeUnit.HOURS, value = REPORT_GENERATE_TRANSACTION_TIMEOUT_HOURS)
    public Report generate(final ReportModel reportModel, final long langId, final long userId) throws Exception {
        ReportProcessorConfig config = new ReportProcessorConfig();
        config.setLangId(langId);
        config.setUserId(userId);
        config.setReplaceAllFormulas(true);
        return generate(reportModel, config);
    }

    /**
     * @param reportModel report model for generate
     * @param config      report generation config
     * @return generated report entity
     * @throws Exception
     */
    @Override
    @TransactionTimeout(unit = TimeUnit.HOURS, value = REPORT_GENERATE_TRANSACTION_TIMEOUT_HOURS)
    public Report generate(final ReportModel reportModel, final ReportProcessorConfig config) throws Exception {
        try {
            Product.getInstance().check();
        } catch (LicenseManagementException e) {
            throw new RuntimeException(e.getMessage());
        }

        final Report report = findById(reportModel.getId());

        ReportTemplate rt = loadReportTemplates(report.getId(), config.getLangId());
        if (rt != null) {
            reportModel.setLanguageTemplate(rt.getTemplate());
        }

        if (report.getReportType() == ReportType.EXCEL) {
            ReportGeneratorBase reportGenerator = new ExcelReportGenerator(ReportSession.this);
            ReportModel rm = reportGenerator.generate(reportModel, config);

            report.setGeneratedContent(rm.getGeneratedContent());

            createAndSaveStoredReport(ReportType.EXCEL, rm, config, reportModel.getUserId());

            ReportAlertsEvent reportAlerts = new ReportAlertsEvent(rm.getAlerts());
            reportAlertsEvent.fire(reportAlerts);
        } else {
            try {
                aooServiceManager.getOfficeManager().execute(context -> {
                    SpreadsheetManager manager = new SpreadsheetManagerImpl(context);
                    FunctionCalculationDoneListenerFactory factory = new FunctionCalculationDoneListenerFactory();

                    ReportGeneratorBase reportGenerator = new ReportGenerator(ReportSession.this, manager, factory.getFunctionCalculationDoneListener(), true, OfficeConfigurationUtil.isMacroEnable(), OfficeConfigurationUtil.isCloseDocumentEnable(), OfficeConfigurationUtil.getOfficeTemplatePath());

                    try {
                        ReportModel rm = reportGenerator.generate(reportModel, config.getLangId());
                        report.setGeneratedContent(rm.getGeneratedContent());

                        createAndSaveStoredReport(ReportType.DEFAULT, rm, config, reportModel.getUserId());
                    } catch (Throwable t) {
                        log.error(t.getMessage(), t);
                        throw new OfficeException(t.getMessage(), t);
                    } finally {
                        if (OfficeConfigurationUtil.isCloseDocumentEnable()) {
                            manager.disposeSheets();
                        }
                    }
                });
            } catch (Exception e) {
                throw new OfficeTypeException(e.getMessage());
            }
        }
        return report;
    }

    private void createAndSaveStoredReport(ReportType reportType, ReportModel rm, ReportProcessorConfig config, long userId) throws IOException, ClassNotFoundException {
        StoredReportPk storedReportPk = new StoredReportPk();
        storedReportPk.setHashCode(ObjectSerializer.deSerialize(rm.getInfo()).hashCode());
        storedReportPk.setLangId((int) config.getLangId());
        storedReportPk.setReportId(rm.getId());

        StoredReport storedReport = new StoredReport();
        storedReport.setReportPk(storedReportPk);
        storedReport.setInfo(rm.getInfo());
        storedReport.setReportResult(rm.getGeneratedContent());
        storedReport.setStoreDate(new Date());

        if (userId == 0) {
            storedReport.setUserId(config.getUserId());
        } else {
            storedReport.setUserId(userId);
        }

        storedReport.setReportType(reportType);

        try {
            storedReportLocal.saveGeneratedReport(storedReport);
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
    }

    /**
     * @param reportId
     * @param langId
     * @return
     * @throws Exception
     */
    @Override
    @TransactionTimeout(unit = TimeUnit.HOURS, value = REPORT_GENERATE_TRANSACTION_TIMEOUT_HOURS)
    public Report generate(int reportId, final long langId) throws Exception {
        return generate(reportId, langId, current.getCurrentUserId());
    }

    @Override
    @TransactionTimeout(unit = TimeUnit.HOURS, value = REPORT_GENERATE_TRANSACTION_TIMEOUT_HOURS)
    public Report generate(final int reportId, final long langId, long userId) throws Exception {
        ReportProcessorConfig config = new ReportProcessorConfig();
        config.setLangId(langId);
        config.setUserId(userId);
        config.setReplaceAllFormulas(true);
        return generate(reportId, config);
    }

    @Override
    @TransactionTimeout(unit = TimeUnit.HOURS, value = REPORT_GENERATE_TRANSACTION_TIMEOUT_HOURS)
    public Report generate(final int reportId, final ReportProcessorConfig config) throws Exception {
        final ReportModel reportModel = new ReportModel();

        final Report report = findById(reportId);
        reportModel.setId(report.getId());
        reportModel.setVersion(report.getVersion());
        reportModel.setParentId(report.getParentId());
        reportModel.setType(report.getType());
        reportModel.setReportType(report.getReportType());
        reportModel.setTemplate(report.getTemplate());
        reportModel.setInfo(report.getInfo());
        reportModel.setSequence(report.getSequence());
        reportModel.setNameStrId(report.getDescription().getNameStrId());
        reportModel.setName(report.getDescription().getDescription(config.getLangId()));
        reportModel.setUserId(config.getUserId());
        reportModel.setCode(report.getCode());

        return generate(reportModel, config);
    }

    @Override
    @TransactionTimeout(unit = TimeUnit.HOURS, value = REPORT_GENERATE_TRANSACTION_TIMEOUT_HOURS)
    public void generateScheduleReport(ScheduleReportId scheduleReportId) {
        ScheduleReport scheduleReport = em.find(ScheduleReport.class, scheduleReportId);
        managedExecutorService.submit(() -> {
            generate(scheduleReport);
            reportScheduleEvent.fire(new ReportScheduleEvent(scheduleReportId.getReportId(), current.getCurrentUserLogin(), scheduleReport.getStatus()));
        });
    }

    @Override
    @TransactionTimeout(unit = TimeUnit.HOURS, value = REPORT_GENERATE_TRANSACTION_TIMEOUT_HOURS)
    public void generate(ScheduleReport scheduleReport) {
        try {
            scheduleReportLocal.updateStatus(scheduleReport, ScheduleReportStatus.STATUS_PROCESSING);

            final ReportModel reportModel = new ReportModel();

            final Report report = findById(scheduleReport.getReportId());
            reportModel.setId(report.getId());
            reportModel.setVersion(report.getVersion());
            reportModel.setParentId(report.getParentId());
            reportModel.setType(report.getType());
            reportModel.setReportType(report.getReportType());
            reportModel.setTemplate(report.getTemplate());
            reportModel.setInfo(scheduleReport.getInfo());
            reportModel.setSequence(report.getSequence());
            reportModel.setNameStrId(report.getDescription().getNameStrId());
            reportModel.setName(report.getDescription().getDescription(scheduleReport.getLangId()));
            reportModel.setUserId(scheduleReport.getUserId());

            Report generated = generate(reportModel, scheduleReport.getLangId(), scheduleReport.getUserId());

            distributeReport(generated, scheduleReport);

            scheduleReportLocal.updateStatus(scheduleReport, ScheduleReportStatus.STATUS_DONE);
        } catch (Exception e) {
            scheduleReportLocal.updateStatus(scheduleReport, ScheduleReportStatus.STATUS_ERROR);
            log.error(e.getMessage(), e);
        } finally {
            checkAndCompileScheduledReportFolderReports(scheduleReport);
        }
    }

    private void checkAndCompileScheduledReportFolderReports(ScheduleReport scheduleReport) {

        if (scheduleReport.getState() != null) {
            List<Integer> stateScheduleReportIds = em.createQuery("select rs.reportId from OUT_REPORTS_SCHEDULE rs where rs.state=:state ", Integer.class).setParameter("state", scheduleReport.getState()).getResultList();

            List<Integer> doneScheduleReportIds = em.createQuery("select rs.reportId from OUT_REPORTS_SCHEDULE rs where rs.state=:state and rs.status=:status", Integer.class).setParameter("state", scheduleReport.getState()).setParameter("status", ScheduleReportStatus.STATUS_DONE).getResultList();

            if (stateScheduleReportIds.size() == doneScheduleReportIds.size()) {

                List<ScheduleReport> scheduleReports = em.createQuery("select rs from OUT_REPORTS_SCHEDULE rs where rs.state=:state", ScheduleReport.class).setParameter("state", scheduleReport.getState()).getResultList();

                if (!scheduleReports.isEmpty()) {

                    Map<Integer, ScheduleReport> scheduleReportIds = new HashMap<>();
                    for (ScheduleReport sr : scheduleReports) {
                        scheduleReportIds.put(sr.getReportId(), sr);
                    }

                    List<Integer> folderIds = em.createQuery("select distinct r.parentId from OUT_REPORTS r where r.id in(:reportIds)", Integer.class).setParameter("reportIds", scheduleReportIds.keySet()).getResultList();

                    List<Report> reports = em.createQuery("select new " + Report.class.getName() + "(r.id, r.parentId, r.reportType) from OUT_REPORTS r where r.parentId in(:folderIds)", Report.class).setParameter("folderIds", folderIds).getResultList();

                    nextFolder:
                    for (Integer folderId : folderIds) {

                        List<ScheduleReport> folderScheduleReports = new ArrayList<>();

                        boolean containsExcelReport = false;
                        for (Report report : reports) {
                            if (report.getParentId() == folderId) {
                                ScheduleReport temp = scheduleReportIds.get(report.getId());
                                if (temp == null) {
                                    break nextFolder;
                                }

                                folderScheduleReports.add(temp);
                                containsExcelReport |= (report.getReportType() == ReportType.EXCEL);
                            }
                        }

                        Map<String, Object> parametersMap = new HashMap<>();
                        String[] ids = new String[scheduleReports.size()];
                        long langId = 0;

                        int index = 0;
                        for (ScheduleReport sr : folderScheduleReports) {
                            langId = sr.getLangId();
                            String idString = "" + sr.getReportId();
                            ids[index++] = idString;

                            net.fina.report.model.ReportModel legacyReportModel = new net.fina.report.model.ReportModel();
                            legacyReportModel.setId(sr.getReportId());
                            legacyReportModel.setInfo(sr.getInfo());

                            parametersMap.put(idString, legacyReportModel);
                        }

                        try {
                            if (containsExcelReport) {
                                for (String id : ids) {
                                    reportGeneratorProcessor.compileExcelReport(parametersMap, id, -1, langId, "xlsx", false, scheduleReport.getUserId());
                                }
                            } else {
                                reportGeneratorProcessor.compileReport(parametersMap, ids, -1, langId, "ods", folderId, scheduleReport.getUserId());
                            }

                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                }
            }
        }

    }


    @Override
    public Report save(Report report, long langId) throws FinATypeException {
        if (!checkIsCodeUnique(report)) {
            throw new FinATypeException(FinATypeException.Type.CODE_UNIQUE);
        }

        try {
            if (report.getTemplate() != null && report.getReportType() != ReportType.EXCEL) {
                AooDocumentPatcher patcher = new AooDocumentPatcher();
                report.setTemplate(patcher.patchDocView(report.getTemplate()));
            }
        } catch (Throwable t) {
            Logger.getLogger(getClass()).error(t.getMessage(), t);
        }

        if (report.getId() > 0) {
            Report r = em.find(Report.class, report.getId());
            r.setTemplate(report.getTemplate());
            r.setCode(report.getCode());
            r.setDescription(report.getDescription());
            r.setInfo(report.getInfo());
            r.setGeneratedContent(report.getGeneratedContent());
            r.setSequence(report.getSequence());
            r.setReportType(report.getReportType() == null ? ReportType.DEFAULT : report.getReportType());
            r.setType(report.getType());
            r.setParentId(report.getParentId());
            report = r;
        } else {
            em.persist(report);

            //Add Current user report
            User user = em.find(User.class, current.getCurrentUserId());
            user.getReports().add(report);

            //Caller principal
            current.getCallerPrincipal().getReports().add(report.getId());
        }

        ReportTemplate reportTemplate = new ReportTemplate();
        ReportTemplatePk templatePk = new ReportTemplatePk();
        templatePk.setLangId(langId);
        templatePk.setReportId(report.getId());
        reportTemplate.setReportTemplatePk(templatePk);
        reportTemplate.setTemplate(report.getTemplate());
        saveReportTemplate(reportTemplate);
        return report;
    }

    private void saveReportTemplate(ReportTemplate reportTemplate) {
        ReportTemplate template = em.find(ReportTemplate.class, reportTemplate.getReportTemplatePk());
        if (template == null) {
            em.persist(reportTemplate);
        } else {
            try {
                String propVal = propertyLocal.getSystemProperty(PropertyKeys.AUDIT_LOG_LEVEL);
                if (propVal != null && AuditLogLevel.values()[Integer.parseInt(propVal)] == AuditLogLevel.DETAILED) {
                    reportModificationAuditLogger.compare(template, reportTemplate);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            template.setTemplate(reportTemplate.getTemplate());
        }
    }

    @Override

    public ReportTemplate loadReportTemplates(int reportId, long langId) {
        List<ReportTemplate> reportTemplates = em.createQuery("select rl from OUT_REPORTS_LANG  rl where rl.reportTemplatePk.reportId=:reportId and rl.reportTemplatePk.langId=:langId", ReportTemplate.class).setParameter("reportId", reportId).setParameter("langId", langId).getResultList();
        if (!reportTemplates.isEmpty()) {
            return reportTemplates.iterator().next();
        }
        ReportTemplate defaultTemplate = new ReportTemplate();

        Report report = em.createQuery("select r from OUT_REPORTS r where r.id=:reportId", Report.class).setParameter("reportId", reportId).getSingleResult();
        defaultTemplate.setTemplate(report.getTemplate());

        ReportTemplatePk pk = new ReportTemplatePk();
        pk.setReportId(reportId);
        pk.setLangId(langId);
        defaultTemplate.setReportTemplatePk(pk);

        return defaultTemplate;
    }

    @Override

    public Report findById(int reportId) {
        return em.find(Report.class, reportId);
    }


    @Override

    public List<Object[]> executeNativeQuery(String sql) {
        return em.createNativeQuery(sql).getResultList();
    }

    @Override
    public List<DimensionModel> loadPeriodsAndGroups(DimensionFilterModel dimensionFilterModel) throws FinATypeException {
        //TODO Not implemented
        return null;
    }

    @Override
    public List<DimensionModel> loadPeriods(DimensionFilterModel dimensionFilterModel) throws FinATypeException {
        //TODO Not implemented
        return null;
    }

    @Override
    public ReportData loadReportsGeneralData(String reportSheetId, long langId, int reportId) {
        ReportData reportData = new ReportData();
        selectBanksAndBankGroupsData(reportSheetId, reportData, langId);
        selectNodesData(reportSheetId, reportData, langId);
        selectPeriodsData(reportSheetId, reportData, langId);
        selectPeriodTypePriority(reportSheetId, reportData, langId);
        setReportProperties(reportSheetId, reportData, reportId);
        setVersionCodeIdMap(reportData);
        return reportData;
    }

    private void setVersionCodeIdMap(ReportData reportData) {
        versionLocal.loadRetrunVersionCodeIdMap().forEach((key, value) -> reportData.versionCodeIdMap.put(key.toUpperCase(), value));
    }

    private void selectBanksAndBankGroupsData(String reportId, ReportData reportData, long langId) {

        //Load Fis
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Fi> query = cb.createQuery(Fi.class);
        Root<Fi> fiRoot = query.from(Fi.class);
        query.select(cb.construct(Fi.class, fiRoot.get(Fi_.id), fiRoot.get(Fi_.code), fiRoot.get(Fi_.description), fiRoot.get(Fi_.shortName)));
        for (Fi fi : em.createQuery(query).getResultList()) {
            String fiCode = fi.getCode().trim();
            Map hashMap = reportData.getHash(reportData.allBanksInfo, reportId);
            OOBank o = (OOBank) hashMap.get(fiCode);
            if (o == null) {
                o = new OOBank(fiCode);
                hashMap.put(fiCode, o);
            }
            o.setName(fi.getDescription().getDescription(langId));
            o.setShortName(fi.getShortName().getDescription(langId));
            o.setId(fi.getId());
        }

        //Load Fi criterion and groups
        List<Object[]> resultList = em.createQuery("select b.code,bg.code,c.code,c.isDefault from IN_BANKS b,IN(b.peerGroup) bg, IN_CRITERION c where bg.parentId=c.id ").getResultList();
        for (Object[] objects : resultList) {
            String fiCode = objects[0].toString().trim();
            String fiGroupCode = objects[1].toString().trim();
            String criterionCode = objects[2].toString().trim();
            boolean isDefault = objects[3] != null && (boolean) objects[3];

            reportData.getHash(reportData.criterionBanks, reportId).put(reportData.getBankCriterionKey(fiCode, criterionCode), fiGroupCode);
            List banks = (List) reportData.getHash(reportData.peersBanks, reportId).get(fiGroupCode);
            if (banks == null) {
                banks = new LinkedList();
            }
            banks.add(fiCode);
            reportData.getHash(reportData.peersBanks, reportId).put(fiGroupCode, banks);

            if (isDefault) {
                reportData.defCriterions.put(reportId, criterionCode);
            }
        }

        //Load  Fi groups
        CriteriaQuery<PeerGroup> fiGroupsQuery = cb.createQuery(PeerGroup.class);
        Root<PeerGroup> peerGroupRoot = fiGroupsQuery.from(PeerGroup.class);
        fiGroupsQuery.select(cb.construct(PeerGroup.class, peerGroupRoot.get(PeerGroup_.id), peerGroupRoot.get(PeerGroup_.code), peerGroupRoot.get(PeerGroup_.description)));
        for (PeerGroup peerGroup : em.createQuery(fiGroupsQuery).getResultList()) {
            String peerGroupCode = peerGroup.getCode().trim();
            Map h = reportData.getHash(reportData.allPeersInfo, reportId);
            OOPeer o = (OOPeer) h.get(peerGroupCode);
            if (o == null) {
                o = new OOPeer(peerGroupCode);
                h.put(peerGroupCode, o);
            }
            o.setName(peerGroup.getDescription().getDescription(langId));
            o.setId(peerGroup.getId());
        }
    }

    private void selectPeriodsData(String reportId, ReportData reportData, long langId) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Period> query = cb.createQuery(Period.class);
        Root<Period> periodRoot = query.from(Period.class);
        periodRoot.fetch(Period_.periodType, JoinType.LEFT);

        query.orderBy(cb.asc(periodRoot.get(Period_.periodType).get(PeriodType_.id)), cb.asc(periodRoot.get(Period_.fromDate)));

        List<Period> periods = em.createQuery(query).getResultList();
        int pSeq = 1;
        String oldCode = "";
        for (Period period : periods) {
            PeriodPK _pk = new PeriodPK((int) period.getId());

            String periodTypeCode = period.getPeriodType().getCode().trim();

            Map h = reportData.getHash(reportData.allPeriods, reportId);
            OOPeriodPK o = (OOPeriodPK) h.get(_pk);
            if (o == null) {
                o = new OOPeriodPK(_pk.getId());
                h.put(_pk, o);
            }
            o.setTypeCode(periodTypeCode);
            o.setTypeName(period.getPeriodType().getDescription().getDescription(langId));
            o.setFromDate(period.getFromDate());
            o.setToDate(period.getToDate());
            o.setNumber((int) period.getPeriodNumber());

            if (!oldCode.equals(periodTypeCode)) {
                pSeq = 1;
                oldCode = periodTypeCode;
            }
            h = reportData.getHash(reportData.allOffsetsPK, reportId);
            Map hh = (Map) h.get(periodTypeCode);
            if (hh == null) {
                hh = new HashMap();
                h.put(periodTypeCode, hh);
            }
            hh.put(_pk, pSeq);

            h = reportData.getHash(reportData.allOffsetsSeq, reportId);
            hh = (HashMap) h.get(periodTypeCode);
            if (hh == null) {
                hh = new HashMap();
                h.put(periodTypeCode, hh);
            }
            hh.put(pSeq, _pk);
            pSeq++;
        }
    }


    private void selectNodesData(String reportId, ReportData reportData, long langId) {
        for (MDTNode mdtNode : mdtCacheManager.getMdtNodes()) {
            String nodeCode = mdtNode.getCode().trim();
            Map hashMap = reportData.getHash(reportData.allNodesInfo, reportId);
            OONode o = (OONode) hashMap.get(nodeCode);
            if (o == null) {
                o = new OONode(nodeCode);
                hashMap.put(nodeCode, o);
            }
            o.setName(mdtNode.getDescription().getDescription(langId));
            o.setId(mdtNode.getId());
        }
    }

    private void selectPeriodTypePriority(String reportId, ReportData reportData, long langId) {
        List<Long> result = em.createQuery("select p.periodType.id from IN_PERIODS p group by p.periodType.id order by max(p.toDate-p.fromDate) ", Long.class).getResultList();
        int index = 0;
        for (long periodTypeId : result) {
            Map h = reportData.getHash(reportData.periodTypePriority, reportId);
            h.put(periodTypeId, index++);
        }
    }

    private void setReportProperties(String reportSheetId, ReportData reportData, int reportId) {
        Map<String, ReportPropertyModel> properties = new HashMap<>();
        //load default properties for all reports wildcard =-1
        List<ReportPropertyModel> defaultProperties = loadReportProperties(-1);
        for (ReportPropertyModel model : loadReportProperties(reportId)) {
            properties.put(model.getKey(), model);
        }

        for (ReportPropertyModel model : defaultProperties) {
            properties.put(model.getKey(), model);
        }
        reportData.reportProperties.put(reportSheetId, properties);
    }


    @Override

    public double bankValueAgregate(ReportData reportData, String reportId, long nodeId, long fiId, Date periodStart, Date periodEnd, int periodFunc, String versionCode) {
        StringBuffer sqlString = new StringBuffer().append("SELECT tr.periodtypeid, ").append(PeriodFunction.getSqlFunction(periodFunc)).append("(tr.tagr) FROM (SELECT t.periodtypeid, sum(t.nvalue) as tagr ").append("FROM " + ReportGeneratorBase.DEFAULT_SOURCE_TABLE_NAME + " t WHERE t.nodeid=? AND t.bankid=? AND t.nvalue is not NULL ").append("AND t.fromdate >=? AND t.todate<=? ").append(versionFilter(versionCode)).append("GROUP BY t.periodtypeid, t.periodid) tr GROUP BY tr.periodtypeid");

        Query query = em.createNativeQuery(sqlString.toString());
        query.setParameter(1, nodeId);
        query.setParameter(2, fiId);
        query.setParameter(3, periodStart);
        query.setParameter(4, periodEnd);

        return retriveResult(reportData, reportId, query.getResultList());
    }

    @Override
    public double allBankValueLast(ReportData reportData, String reportId, long nodeId, int bankFunc, Date periodStart, Date periodEnd, int periodFunc, String versionCode) {
        String sqlString = "SELECT periodtypeid, " + AgregateFunction.getSqlFunction(bankFunc) + "(agr) from (SELECT t.bankid, t.periodtypeid, " + "sum(nvalue) as agr FROM " + ReportGeneratorBase.DEFAULT_SOURCE_TABLE_NAME + " t WHERE t.nodeid=? AND t.nvalue is not NULL " + "AND t.fromdate >=? AND t.todate=? " + versionFilter(versionCode) + "GROUP BY t.bankid, t.periodtypeid, t.periodid) res group by res.periodtypeid";
        Query query = em.createNativeQuery(sqlString);
        query.setParameter(1, nodeId);
        query.setParameter(2, periodStart);
        query.setParameter(3, periodEnd);
        return retriveResult(reportData, reportId, query.getResultList());
    }

    @Override
    public double allBankValueAgregate(ReportData reportData, String reportId, long nodeId, int bankFunc, Date periodStart, Date periodEnd, int periodFunc, String versionCode) {
        StringBuilder sqlString = new StringBuilder().append("SELECT periodtypeid, ").append(AgregateFunction.getSqlFunction(bankFunc)).append("(agr) from (SELECT tr.bankid, tr.periodtypeid, ").append(PeriodFunction.getSqlFunction(periodFunc)).append("(tr.tagr) as agr FROM (SELECT t.bankid, t.periodtypeid, sum(t.nvalue) as tagr ").append("FROM " + ReportGeneratorBase.DEFAULT_SOURCE_TABLE_NAME + " t WHERE t.nodeid=? AND t.nvalue is not NULL AND t.fromdate >=? AND t.todate<=? ").append(versionFilter(versionCode)).append("GROUP BY t.bankid, t.periodtypeid, t.periodid) tr GROUP BY tr.bankid, tr.periodtypeid) res group by res.periodtypeid");

        Query query = em.createNativeQuery(sqlString.toString());
        query.setParameter(1, nodeId);
        query.setParameter(2, periodStart);
        query.setParameter(3, periodEnd);

        return retriveResult(reportData, reportId, query.getResultList());
    }

    @Override

    public double selBankValueAgregate(ReportData reportData, String reportId, long nodeId, String banks, int bankFunc, Date periodStart, Date periodEnd, int periodFunc, String versionCode) {
        StringBuilder sqlString = new StringBuilder()

                .append("SELECT periodtypeid, ").append(AgregateFunction.getSqlFunction(bankFunc)).append("(agr) from (SELECT tr.bankid, tr.periodtypeid, ").append(PeriodFunction.getSqlFunction(periodFunc)).append("(tr.tagr) as agr FROM (SELECT t.bankid, t.periodtypeid, sum(t.nvalue) as tagr ").append("FROM " + ReportGeneratorBase.DEFAULT_SOURCE_TABLE_NAME + " t WHERE t.nodeid=? AND t.nvalue is not NULL AND t.fromdate >=? AND t.todate<=? ").append("AND t.bankid in (").append(banks).append(") ").append(versionFilter(versionCode)).append("GROUP BY t.bankid, t.periodtypeid, t.periodid) tr ").append("GROUP BY tr.bankid, tr.periodtypeid) res group by res.periodtypeid");


        Query query = em.createNativeQuery(sqlString.toString()).setParameter(1, nodeId).setParameter(2, periodStart).setParameter(3, periodEnd);

        return retriveResult(reportData, reportId, query.getResultList());
    }

    @Override

    public double selBankValueLast(ReportData reportData, String reportId, long nodeId, String banks, int bankFunc, Date periodStart, Date periodEnd, int periodFunc, String versionCode) {
        StringBuilder sqlString = new StringBuilder().append("SELECT periodtypeid, ").append(AgregateFunction.getSqlFunction(bankFunc)).append("(agr) from (SELECT t.bankid, t.periodtypeid, sum(nvalue) as agr ").append("FROM " + ReportGeneratorBase.DEFAULT_SOURCE_TABLE_NAME + " t WHERE t.nodeid=? AND t.nvalue is not NULL AND t.bankid in (").append(banks).append(") AND t.fromdate >=? AND t.todate=? ").append(versionFilter(versionCode)).append("GROUP BY t.bankid, t.periodtypeid, t.periodid) res group by res.periodtypeid");

        Query query = em.createNativeQuery(sqlString.toString()).setParameter(1, nodeId).setParameter(2, periodStart).setParameter(3, periodEnd);

        return retriveResult(reportData, reportId, query.getResultList());
    }

    @Override
    public int numoffisbytype(ReportData reportData, String reportId, String fiType) {
        Object queryResult = em.createNativeQuery("SELECT COUNT(*) AS FICOUNT FROM IN_BANKS b ,IN_BANK_TYPES bt WHERE b.TYPEID=bt.ID and RTRIM(bt.CODE)=?").setParameter(1, fiType).getSingleResult();
        return extractIntSingleResult(queryResult);
    }

    @Override
    public double bankValueLast(ReportData reportData, String reportId, long nodeId, long fiId, Date periodStart, Date periodEnd, int periodFunc, String versionCode) {
        String sqlString = "SELECT t.periodtypeid, sum(nvalue) " + "FROM " + ReportGeneratorBase.DEFAULT_SOURCE_TABLE_NAME + " t WHERE t.nodeid=? AND t.bankid=? AND t.nvalue is not NULL AND " + "t.fromdate >=? AND t.todate=? " + versionFilter(versionCode) + "GROUP BY t.periodtypeid, t.periodid ";

        Query query = em.createNativeQuery(sqlString).setParameter(1, nodeId).setParameter(2, fiId).setParameter(3, periodStart).setParameter(4, periodEnd);

        return retriveResult(reportData, reportId, query.getResultList());
    }

    @Override
    public double bankValueVctSumIf(ReportData reportData, String reportId, long nodeId, long fiId, Date periodStart, Date periodEnd, int periodFunc, String versionCode, String criterion, long sumNodeId, boolean aggregate) {

        String sqlString = "select t.RETURNID,t.ITEMTABLEID,t.ITEMROWNUMBER,t.NVALUE,t.VALUE,t.NODEID from RESULT_VIEW_FULL t where t.bankid=:bankId and t.nodeid=:nodeId and t.fromdate =:fromDate and t.todate=:toDate ";

        sqlString += " AND ";
        if (versionCode == null || versionCode.equalsIgnoreCase(ReportConstants.LATEST_VERSION)) {
            sqlString += "t.versionid=t.latestversionid ";
        } else {
            sqlString += "t.versionId = (select id from IN_RETURN_VERSIONS where code='" + versionCode + "') ";
        }

        Query nativeQuery = em.createNativeQuery(sqlString);

        List<VctSumIfDataItemMetaModel> criterionList = VctSumIfHelper.extractModel(nativeQuery.setParameter("bankId", fiId).setParameter("nodeId", nodeId).setParameter("fromDate", periodStart).setParameter("toDate", periodEnd).getResultList());

        List<VctSumIfDataItemMetaModel> sumList = VctSumIfHelper.extractModel(nativeQuery.setParameter("bankId", fiId).setParameter("nodeId", sumNodeId).setParameter("fromDate", periodStart).setParameter("toDate", periodEnd).getResultList());

        return calculatSumifValueWithMetaModel(criterionList, sumList, criterion, CriterionFunctionOperation.SUM);
    }

    @Override
    public double bankValueVctCountIf(ReportData reportData, String reportId, long nodeId, long bankId, Date startDate, Date toDate, int avg, String versionCode, String criterion, long sumNodeId, boolean aggregate) {
        String sqlString = "select t.RETURNID,t.ITEMTABLEID,t.ITEMROWNUMBER,t.NVALUE,t.VALUE,t.NODEID from RESULT_VIEW_FULL t where t.bankid=:bankId and t.nodeid=:nodeId and t.fromdate =:fromDate and t.todate=:toDate ";

        sqlString += " AND ";
        if (versionCode == null || versionCode.equalsIgnoreCase(ReportConstants.LATEST_VERSION)) {
            sqlString += "t.versionid=t.latestversionid ";
        } else {
            sqlString += "t.versionId = (select id from IN_RETURN_VERSIONS where code='" + versionCode + "') ";
        }

        Query nativeQuery = em.createNativeQuery(sqlString);

        List<VctSumIfDataItemMetaModel> criterionList = VctSumIfHelper.extractModel(nativeQuery.setParameter("bankId", bankId).setParameter("nodeId", nodeId).setParameter("fromDate", startDate).setParameter("toDate", toDate).getResultList());

        List<VctSumIfDataItemMetaModel> sumList = VctSumIfHelper.extractModel(nativeQuery.setParameter("bankId", bankId).setParameter("nodeId", sumNodeId).setParameter("fromDate", startDate).setParameter("toDate", toDate).getResultList());

        return calculatSumifValueWithMetaModel(criterionList, sumList, criterion, CriterionFunctionOperation.COUNT);
    }


    private char getCriterionOperation(String criterion) {
        if (criterion != null && criterion.length() > 0) {
            return criterion.charAt(0);
        }
        return ' ';
    }

    private String getCriterionValue(String criterion, char operation) {
        if (criterion != null && criterion.length() > 0) {
            switch (operation) {
                case '>':
                case '<':
                case '=':
                    return criterion.substring(1);
            }
        }
        return criterion;
    }

    @Override

    public double peerValueAgregate(ReportData reportData, String reportId, long nodeId, String peerCode, int bankFunc, Date periodStart, Date periodEnd, int periodFunc, String versionCode) {
        StringBuilder sqlString = new StringBuilder()

                .append("SELECT periodtypeid, ").append(AgregateFunction.getSqlFunction(bankFunc)).append("(agr) from (SELECT tr.bankid, tr.periodtypeid, ").append(PeriodFunction.getSqlFunction(periodFunc)).append("(tr.tagr) as agr FROM (SELECT t.bankid, t.periodtypeid, sum(t.nvalue) as tagr ").append("FROM " + ReportGeneratorBase.DEFAULT_SOURCE_TABLE_NAME + " t, IN_BANKS b, IN_BANK_GROUPS bg, MM_BANK_GROUP mbg ").append("WHERE t.nodeid=? AND t.nvalue is not NULL AND t.bankid=b.id ").append("AND t.fromdate >=? AND t.todate<=? ").append(versionFilter(versionCode)).append("AND mbg.bankID=b.ID AND mbg.bankgroupID=bg.ID AND RTRIM(bg.code)=? ").append("GROUP BY t.bankid, t.periodtypeid, t.periodid) tr ").append("GROUP BY tr.bankid, tr.periodtypeid) res group by res.periodtypeid");

        Query query = em.createNativeQuery(sqlString.toString()).setParameter(1, nodeId).setParameter(2, periodStart).setParameter(3, periodEnd).setParameter(4, peerCode);

        return retriveResult(reportData, reportId, query.getResultList());
    }

    @Override

    public double peerValueLast(ReportData reportData, String reportId, long nodeId, int bankFunc, Date periodStart, Date periodEnd, String peerCode, String versionCode) {
        StringBuilder sqlString = new StringBuilder().append("SELECT periodtypeid, ").append(AgregateFunction.getSqlFunction(bankFunc)).append("(agr) from (SELECT t.bankid, t.periodtypeid, sum(nvalue) as agr ").append("FROM " + ReportGeneratorBase.DEFAULT_SOURCE_TABLE_NAME + " t, IN_BANKS b, IN_BANK_GROUPS bg, MM_BANK_GROUP mbg ").append("WHERE t.nodeid=? AND t.nvalue is not NULL AND t.bankid = b.ID ").append("AND t.fromdate >=? AND t.todate=? ").append(versionFilter(versionCode)).append("AND mbg.bankID=b.ID AND mbg.bankgroupID=bg.ID AND RTRIM(bg.code)=? ").append("GROUP BY t.bankid, t.periodtypeid, t.periodid) res group by res.periodtypeid");

        Query query = em.createNativeQuery(sqlString.toString()).setParameter(1, nodeId).setParameter(2, periodStart).setParameter(3, periodEnd).setParameter(4, peerCode);

        return retriveResult(reportData, reportId, query.getResultList());
    }

    @Override
    public double regionValueAgregate(ReportData reportData, String reportId, long nodeId, String regionCode, String fiType, int bankFunc, Date periodStart, Date periodEnd, int periodFunc, String versionCode) {
        List<Long> regionIds = regionLocal.loadRegionChildrenIdsByCode(regionCode);

        double result = Double.NaN;

        if (!regionIds.isEmpty()) {
            StringBuilder sqlString;
            if (reportData.isAll(fiType)) {
                sqlString = new StringBuilder().append("SELECT periodtypeid, ").append(AgregateFunction.getSqlFunction(bankFunc)).append("(agr) from (SELECT tr.bankid, tr.periodtypeid, ").append(PeriodFunction.getSqlFunction(periodFunc)).append("(tr.tagr) as agr FROM (SELECT t.bankid, t.periodtypeid, sum(t.nvalue) as tagr ").append("FROM " + ReportGeneratorBase.DEFAULT_SOURCE_TABLE_NAME + " t, IN_BANKS b,  in_country_data cd ").append("WHERE t.nodeid=? AND t.nvalue is not NULL AND t.bankid=b.id ").append("AND t.fromdate >=? AND t.todate<=? ").append(versionFilter(versionCode)).append("AND   cd.id=b.regionid AND cd.id in ").append(regionIds.toString().replace("[", "(").replace("]", ")")).append(" GROUP BY t.bankid, t.periodtypeid, t.periodid) tr ").append("GROUP BY tr.bankid, tr.periodtypeid) res group by res.periodtypeid");
            } else {
                sqlString = new StringBuilder().append("SELECT periodtypeid, ").append(AgregateFunction.getSqlFunction(bankFunc)).append("(agr) from (SELECT tr.bankid, tr.periodtypeid, ").append(PeriodFunction.getSqlFunction(periodFunc)).append("(tr.tagr) as agr FROM (SELECT t.bankid, t.periodtypeid, sum(t.nvalue) as tagr ").append("FROM " + ReportGeneratorBase.DEFAULT_SOURCE_TABLE_NAME + " t, IN_BANKS b,in_bank_types bt, in_country_data cd ").append("WHERE t.nodeid=? AND t.nvalue is not NULL AND t.bankid=b.id AND bt.id=b.typeid ").append("AND t.fromdate >=? AND t.todate<=? ").append(versionFilter(versionCode)).append("AND   cd.id=b.regionid AND RTRIM(bt.code)=? AND cd.id in ").append(regionIds.toString().replace("[", "(").replace("]", ")")).append(" GROUP BY t.bankid, t.periodtypeid, t.periodid) tr ").append("GROUP BY tr.bankid, tr.periodtypeid) res group by res.periodtypeid");
            }

            Query query = em.createNativeQuery(sqlString.toString()).setParameter(1, nodeId).setParameter(2, periodStart).setParameter(3, periodEnd);
            if (!reportData.isAll(fiType)) {
                query.setParameter(4, fiType);
            }
            result = retriveResult(reportData, reportId, query.getResultList());
        }
        return result;
    }

    @Override
    public double regionValueLast(ReportData reportData, String reportId, long nodeId, String regionCode, String fiType, int bankFunc, Date periodStart, Date periodEnd, String versionCode) {
        List<Long> regionIds = regionLocal.loadRegionChildrenIdsByCode(regionCode);
        double result = Double.NaN;

        if (!regionIds.isEmpty()) {
            StringBuilder sqlString;
            if (reportData.isAll(fiType)) {
                sqlString = new StringBuilder().append("SELECT periodtypeid, ").append(AgregateFunction.getSqlFunction(bankFunc)).append("(agr) from (SELECT t.bankid, t.periodtypeid, sum(nvalue) as agr ").append("FROM " + ReportGeneratorBase.DEFAULT_SOURCE_TABLE_NAME + " t, IN_BANKS b,  in_country_data cd ").append("WHERE t.nodeid=? AND t.nvalue is not NULL AND t.bankid = b.ID ").append("AND t.fromdate >=? AND t.todate=? ").append(versionFilter(versionCode)).append("AND cd.id=b.regionid AND cd.id in ").append(regionIds.toString().replace("[", "(").replace("]", ")")).append("GROUP BY t.bankid, t.periodtypeid, t.periodid) res group by res.periodtypeid");
            } else {
                sqlString = new StringBuilder().append("SELECT periodtypeid, ").append(AgregateFunction.getSqlFunction(bankFunc)).append("(agr) from (SELECT t.bankid, t.periodtypeid, sum(nvalue) as agr ").append("FROM " + ReportGeneratorBase.DEFAULT_SOURCE_TABLE_NAME + " t, IN_BANKS b,in_bank_types bt, in_country_data cd ").append("WHERE t.nodeid=? AND t.nvalue is not NULL AND t.bankid = b.ID AND bt.id=b.typeid ").append("AND t.fromdate >=? AND t.todate=? ").append(versionFilter(versionCode)).append("AND cd.id=b.regionid AND RTRIM(bt.code)=? and cd.id in ").append(regionIds.toString().replace("[", "(").replace("]", ")")).append("GROUP BY t.bankid, t.periodtypeid, t.periodid) res group by res.periodtypeid");
            }
            Query query = em.createNativeQuery(sqlString.toString()).setParameter(1, nodeId).setParameter(2, periodStart).setParameter(3, periodEnd);

            if (!reportData.isAll(fiType)) {
                query.setParameter(4, fiType);
            }
            result = retriveResult(reportData, reportId, query.getResultList());
        }
        return result;
    }

    @Override
    public String textvaluever(ReportData reportData, String reportId, long nodeId, long bankId, int periodId, String versionCode) {

        String result = null;

        StringBuffer versionSql = new StringBuffer();
        versionSql.append(" AND ");
        if (versionCode == null || versionCode.equalsIgnoreCase(ReportConstants.LATEST_VERSION)) {
            versionSql.append("rv.viewPk.versionCode = rv.viewPk.latestVersionCode ");
        } else {
            versionSql.append("rv.viewPk.versionCode = '");
            versionSql.append(versionCode);
            versionSql.append("' ");
        }

        Iterator<String> resultIterator = em.createQuery("select rv.viewPk.value from " + ReportGeneratorBase.DEFAULT_SOURCE_TABLE_NAME + " rv where rv.viewPk.nodeId=:nodeId and rv.viewPk.bankId=:bankId and rv.viewPk.periodId=:periodId " + versionSql, String.class).setParameter("nodeId", nodeId).setParameter("bankId", bankId).setParameter("periodId", periodId).getResultList().iterator();

        if (resultIterator.hasNext()) {
            result = resultIterator.next();
        }
        return result;
    }

    @Override
    public int numoffisubmited(ReportData reportData, String reportId, String fiTypeCode, String retCode, String periodStart, String periodEnd, String status, String version) {
        int n = 0;
        java.sql.Date periodFrom;
        java.sql.Date periodTo;
        int statusOrdinal = -1;
        String datePattern = "dd/MM/yyyy";
        DateFormat df = new SimpleDateFormat(datePattern);
        String sql = "";
        try {
            periodFrom = new java.sql.Date(df.parse(periodStart).getTime());
            periodTo = new java.sql.Date(df.parse(periodEnd).getTime());
            status = status.trim().toLowerCase();

            switch (status) {
                case "created": {
                    statusOrdinal = ReturnConstants.STATUS_CREATED;
                    break;
                }
                case "amended": {
                    statusOrdinal = ReturnConstants.STATUS_AMENDED;
                    break;
                }
                case "imported": {
                    statusOrdinal = ReturnConstants.STATUS_IMPORTED;
                    break;
                }
                case "processed": {
                    statusOrdinal = ReturnConstants.STATUS_PROCESSED;
                    break;
                }
                case "reset": {
                    statusOrdinal = ReturnConstants.STATUS_RESETED;
                    break;
                }
                case "accepted": {
                    statusOrdinal = ReturnConstants.STATUS_ACCEPTED;
                    break;
                }
                case "rejected": {
                    statusOrdinal = ReturnConstants.STATUS_REJECTED;
                    break;
                }
                case "errors": {
                    statusOrdinal = ReturnConstants.STATUS_ERRORS;
                    break;
                }
            }

            sql = "select COUNT(returns.id) as numofreturns from IN_BANKS banks,IN_RETURNS returns, IN_PERIODS periods,IN_SCHEDULES schedules,   IN_RETURN_STATUSES retStats, IN_RETURN_TYPES retTypes,IN_RETURN_VERSIONS retVersion,IN_RETURN_DEFINITIONS definitions where  returns.ID=retStats.RETURNID and ";
            String fiTypeCodeSql = "";
            String retCodeSql = "";
            String periodFromSql = " periods.fromDate>=? and ";
            String periodToSql = "  periods.toDate<=? and ";
            String statusSql = "";
            String versionSql = "";
            if (!fiTypeCode.toLowerCase().equals("all")) {
                fiTypeCodeSql = " banks.ID in(select b.id from IN_BANKS b,IN_BANK_TYPES bt where RTRIM(bt.CODE)='" + fiTypeCode.trim() + "' and b.TYPEID=bt.ID ) and ";
            }
            if (!retCode.toLowerCase().trim().equals("all")) {
                retCodeSql = " RTRIM(definitions.CODE)='" + retCode.trim() + "' and ";
            }
            if (statusOrdinal != -1) {
                statusSql = " retStats.STATUS=" + statusOrdinal + " and ";
            }
            if (!version.toLowerCase().trim().equals("all")) {
                versionSql = " RTRIM(retVersion.CODE)='" + version.trim() + "' and ";
            }
            sql += fiTypeCodeSql + " " + retCodeSql + " " + periodFromSql + " " + periodToSql + " " + statusSql + " " + versionSql + " ";
            sql += " banks.id = schedules.bankID and returns.scheduleID = schedules.id  and periods.id = schedules.periodID " + " and definitions.id = schedules.definitionID and retStats.returnID = returns.id and definitions.typeID = retTypes.id and retVersion.id = retStats.versionId" + " and retStats.ID in(select max(j.id) from IN_RETURN_STATUSES j where j.returnID=returns.id group by j.versionid)  ";

            Object count = em.createNativeQuery(sql).setParameter(1, periodFrom).setParameter(2, periodTo).getSingleResult();
            n = extractIntSingleResult(count);
        } catch (Exception ex) {
            log.error(sql);
            log.error(ex.getMessage(), ex);
        }
        return n;
    }

    @Override
    public List<Object[]> getBankValuesForPctAgregate(String reportId, OONode node, String banks, Date periodStart, Date periodEnd, int periodFunc, String versionCode) {
        StringBuffer sql = new StringBuffer(300);
        sql.append("SELECT tr.bankid, tr.periodtypeid, ").append(PeriodFunction.getSqlFunction(periodFunc)).append("(tr.tagr) FROM (SELECT t.bankid, t.periodtypeid, sum(t.nvalue) as tagr ").append("FROM " + ReportGeneratorBase.DEFAULT_SOURCE_TABLE_NAME + " t WHERE t.nodeid=? AND t.bankid in (").append(banks).append(") ").append("AND t.nvalue is not NULL AND t.fromdate >=? AND t.todate<=? ").append(versionFilter(versionCode)).append("GROUP BY t.bankid, t.periodtypeid, t.periodid) tr GROUP BY tr.bankid, tr.periodtypeid ORDER BY tr.bankid");
        return em.createNativeQuery(sql.toString()).setParameter(1, node.getId()).setParameter(2, periodStart).setParameter(3, periodEnd).getResultList();
    }

    @Override
    public List<Object[]> getBankValuesForPctLast(String reportId, OONode node, String banks, Date periodStart, Date periodEnd, int periodFunc, String versionCode) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT t.bankid, t.periodtypeid, sum(nvalue) ").append("FROM " + ReportGeneratorBase.DEFAULT_SOURCE_TABLE_NAME + " t WHERE t.nodeid=? AND t.bankid in ( ").append(banks).append(" ) ").append("AND t.nvalue is not NULL AND t.fromdate >=? AND t.todate=? ").append(versionFilter(versionCode)).append("GROUP BY t.bankid, t.periodtypeid, t.periodid ORDER BY t.bankid");
        return em.createNativeQuery(sql.toString()).setParameter(1, node.getId()).setParameter(2, periodStart).setParameter(3, periodEnd).getResultList();
    }

    @Override
    public List<Object[]> getRawVctData(OOIterator vctIterator, ReportData reportData, String reportId) {
        if (vctIterator.getAggregateValues().isEmpty() || vctIterator.getPeriodValues().isEmpty()) {
            return Collections.emptyList();
        }

        // form banks sql list
        StringBuffer strBuf = new StringBuffer();
        for (java.util.Iterator iter = vctIterator.getAggregateValues().iterator(); iter.hasNext(); ) {
            strBuf.append('\'').append(iter.next()).append('\'');
            if (iter.hasNext()) {
                strBuf.append(", ");
            }
        }
        String bankSql = strBuf.toString();
        if (vctIterator.getAggregateType() != net.fina.report.model.Iterator.BANK_ITERATOR) { // is a list of bank groups
            bankSql = "select b.code from IN_BANKS  b where b.id in (select m.bankGroupPK.bankId from MM_BANK_GROUP  m,IN_BANK_GROUPS g " + "where m.bankGroupPK.bankGroupId=g.id and trim(g.code) in(" + bankSql + "))";
        }

        // form periods sql list
        strBuf = new StringBuffer();
        for (java.util.Iterator iter = vctIterator.getPeriodValues().iterator(); iter.hasNext(); ) {
            Object per = iter.next();
            PeriodPK periodPk = new PeriodPK(-1);
            if (per instanceof PeriodPK) {
                periodPk = (PeriodPK) per;
            } else if (per instanceof Number) {
                periodPk = new PeriodPK(((Number) per).intValue());
            }
            strBuf.append(periodPk.getId());
            if (iter.hasNext()) {
                strBuf.append(", ");
            }
        }
        String periodsSql = strBuf.toString();

        List<Object[]> result = em.createQuery("select ril.value,ril.rowNumber,mn.code,mn.dataType,ril.returnId,r.schedule.fi.code " + "from ReturnItemLite ril left join IN_MDT_NODES mn on ril.nodeId=mn.id left join IN_RETURNS r on ril.returnId=r.id " + "where r.id=ril.returnId and mn.id=ril.nodeId and mn.parentId=:nodeId and ril.tableId=:tableId " + "and r.schedule.period.id in(" + periodsSql + ") and trim(r.schedule.fi.code) in (" + bankSql + ") " + vctVersionFilter(vctIterator.getVersionCode()) + " order by ril.returnId,ril.rowNumber,mn.sequence", Object[].class).setParameter("nodeId", (long) vctIterator.getTable().getNodeID()).setParameter("tableId", (long) vctIterator.getTable().getTableID()).getResultList();


        return result;
    }

    @Override
    public List<DimensionModel> loadPeriodsAndFis(DimensionFilterModel dimensionFilterModel) throws FinATypeException {
        return null;
    }

    private String vctVersionFilter(String versionCode) {
        StringBuffer sql = new StringBuffer();
        sql.append(" AND ");
        if (versionCode == null || versionCode.equalsIgnoreCase(ReportConstants.LATEST_VERSION)) {
            sql.append(" ril.versionId =(select max(ri.versionId) from ReturnItemLite ri where ri.returnId=r.id ) ");
        } else {
            sql.append("ri.versionId = (select id from IN_RETURN_VERSIONS where code='").append(versionCode).append("') ");
        }
        return sql.toString();
    }

    private String versionFilter(String versionCode) {
        StringBuffer sql = new StringBuffer();
        sql.append(" AND ");
        if (versionCode == null || versionCode.equalsIgnoreCase(ReportConstants.LATEST_VERSION)) {
            sql.append("t.versionCode = t.latestVersionCode ");
        } else {
            sql.append("t.versionCode = '");
            sql.append(versionCode);
            sql.append("' ");
        }
        return sql.toString();
    }

    private double retriveResult(ReportData reportData, String reportId, List<Object[]> result) {
        double retVal = getDefaultNumberResult(reportId, reportData);
        for (Object[] objects : result) {
            Object zeroObject = objects[0];
            Long zeroValue = null;
            if (zeroObject instanceof BigDecimal) {
                zeroValue = ((BigDecimal) zeroObject).longValue();
            } else if (zeroObject instanceof BigInteger) {
                zeroValue = ((BigInteger) zeroObject).longValue();
            } else {
                zeroValue = ((Number) zeroObject).longValue();
            }

            if (comparePeriodTypePriorities(reportData, reportId, zeroValue, null) > 0) {
                retVal = ((Number) objects[1]).doubleValue();
            }
        }
        return retVal;
    }

    private double getDefaultNumberResult(String reportId, ReportData reportData) {
        Map<String, ReportPropertyModel> properties = reportData.reportProperties.get(reportId);
        if (properties != null) {
            ReportPropertyModel model = properties.get("defaultFormulaNumberResult");
            if (model != null) {
                try {
                    return Double.parseDouble(model.getValue());
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return Double.NaN;
    }

    private String getDefaultTextResult(String reportId, ReportData reportData) {
        Map<String, ReportPropertyModel> properties = reportData.reportProperties.get(reportId);
        if (properties != null) {
            ReportPropertyModel model = properties.get("defaultFormulaTextResult");
            if (model != null) {
                return model.getValue();
            }
        }
        return "#N/A";
    }

    private int comparePeriodTypePriorities(ReportData reportData, String reportId, Long firsPeriodTypId, Long secondPeriodTypId) {
        int retVal = 0;
        Map h = reportData.getHash(reportData.periodTypePriority, reportId);
        Integer first = (firsPeriodTypId != null) ? (Integer) h.get(firsPeriodTypId) : null;
        Integer second = (secondPeriodTypId != null) ? (Integer) h.get(secondPeriodTypId) : null;
        if (first != null && second != null) {
            retVal = first.compareTo(second);
        } else {
            if (first == null && second != null) {
                retVal = -1;
            } else if (first != null && second == null) {
                retVal = 1;
            }
        }
        return retVal;
    }

    @Override
    public boolean checkIsCodeUnique(Report report) {
        Query query = em.createNamedQuery("REPORT.checkCodeUnique");
        query.setParameter("code", report.getCode());
        query.setParameter("id", report.getId());
        return query.getResultList().size() == 0;
    }

    @Override

    public int findMaxChildSequence(int parentId) {
        TypedQuery<Integer> query = em.createQuery("select max (r.sequence) from OUT_REPORTS r where r.parentId=:parentid", Integer.class);
        query.setParameter("parentid", parentId);
        List<Integer> result = query.getResultList();
        return result.isEmpty() || result.get(0) == null ? 0 : result.get(0);
    }

    @Override
    public boolean hasUserAccess(int reportId) {
        return current.getCallerPrincipal().getReports().contains(reportId);
    }

    @Override
    public String returnStatus(ReportData reportData, String reportId, String rdCode, long fiId, long periodId, String versionCode) {

        final String defaultResult = getDefaultTextResult(reportId, reportData);

        List<ReturnStatus> statuses = em.createQuery("select rs from IN_RETURNS  r, IN(r.statuses) rs where r.schedule.returnDefinition.code=:returnCode and r.schedule.period.id=:periodId and r.schedule.fi.id=:fiId and r.returnVersion.code=:versionCode order by rs.statusDate desc", ReturnStatus.class).setParameter("fiId", fiId).setParameter("periodId", periodId).setParameter("returnCode", rdCode).setParameter("versionCode", versionCode).getResultList();

        ReturnStatus status = null;
        if (!statuses.isEmpty()) {
            status = statuses.stream().findFirst().get();
        }
        return status != null ? status.getStatus().toString() : defaultResult;
    }

    @Override
    public List<LanguageSampleModel> getLanguage() {
        return null;
    }

    @Override
    public double allbanksvaluevctsumif(ReportData reportData, long nodeId, Date fromDate, Date toDate, String periodFunction, int periodOffset, String criterion, long sumNodeId, String versionCode) {
        List<Long> allFis = em.createQuery("select fi.id from IN_BANKS fi", Long.class).getResultList();

        List<ResultViewFull> criterionList = loadVctItems(reportData, allFis, nodeId, fromDate, toDate, versionCode);

        List<ResultViewFull> sumList = loadVctItems(reportData, allFis, sumNodeId, fromDate, toDate, versionCode);

        return calculatSumifValue(criterionList, sumList, criterion, CriterionFunctionOperation.SUM);
    }

    @Override
    public int allbanksvaluevctcountif(ReportData reportData, long nodeId, Date startDate, Date toDate, String fYdtaverage, int periodOffset, String criterion, long countNodeId, String versionCode) {

        List<Long> allFis = em.createQuery("select fi.id from IN_BANKS fi", Long.class).getResultList();

        List<ResultViewFull> criterionList = loadVctItems(reportData, allFis, nodeId, startDate, toDate, versionCode);
        List<ResultViewFull> sumList = loadVctItems(reportData, allFis, countNodeId, startDate, toDate, versionCode);

        return (int) calculatSumifValue(criterionList, sumList, criterion, CriterionFunctionOperation.COUNT);
    }


    @Override
    public double groupvaluevctsumif(ReportData reportData, long nodeId, long peerGroupId, Date fromDate, Date toDate, String periodFunction, int periodOffset, String criterion, long sumNodeId, String version) {

        List<Long> allFis = em.createQuery("select b.bankGroupPK.bankId from MM_BANK_GROUP b where b.bankGroupPK.bankGroupId=:groupId").setParameter("groupId", peerGroupId).getResultList();

        List<ResultViewFull> criterionList = loadVctItems(reportData, allFis, nodeId, fromDate, toDate, version);

        List<ResultViewFull> sumList = loadVctItems(reportData, allFis, sumNodeId, fromDate, toDate, version);

        return calculatSumifValue(criterionList, sumList, criterion, CriterionFunctionOperation.SUM);
    }

    @Override
    public String bankaddress(long bankId, long langId) {
        Fi fi = em.find(Fi.class, bankId);
        List<Region> regions = regionLocal.loadRegions();
        return getRegionalAddress(regions, fi.getAddressDescription().getDescription(langId), fi.getRegionId(), langId);
    }


    @Override
    @SuppressWarnings("unchecked")
    public String topvalue(TopValueHelper tvh) {
        MDTNode sortNode = em.find(MDTNode.class, tvh.getSortNodeId());

        List<TopValueHelper.RowDataModel> resultValue = new ArrayList<>();

        String sourceTableName = "RESULT_VIEW_FULL";

        Vector<Long> sortNodesIds = tvh.getSortNodeAggrIds();
        Vector<Long> resultNodesIds = tvh.getResultNodeAggrIds();

        List<Object[]> topByConditionValues = tvh.getConditionValues();

        Query query = em.createNativeQuery("select ri.value,ri.nvalue,ri.nodeid,ri.itemrownumber  from " + sourceTableName + " ri  where ri.nodeid =:nodeid and ri.bankid=:bankid and ri.periodid=:periodid and ri.versionid=ri.latestversionid order by ri.nodeid,ri.itemrownumber ASC ");
        if (tvh.getSortNodeAggrCodes().size() > 1) {


            List<Object[]> sortNodesResult = new ArrayList<>();
            sortNodesIds.forEach(nodeId -> {
                sortNodesResult.addAll(query.setParameter("nodeid", nodeId).setParameter("bankid", tvh.getBankId()).setParameter("periodid", tvh.getPeriodId()).getResultList());
            });


            String aggregateTemplate = tvh.getTopValueAggregateTemplate(tvh.getSortNodeAggrCodes(), tvh.getSortNodeAggrOperations());

            List<TopValueHelper.RowDataModel> values = new ArrayList<>();
            int offset = sortNodesResult.size() / tvh.getSortNodeCodesSize();

            if (!sortNodesResult.isEmpty()) {
                for (int i = 0; i < offset; i++) {
                    int j = i;
                    try {
                        values.add(new TopValueHelper.RowDataModel(((BigDecimal) sortNodesResult.get(i)[3]).intValue(), tvh.eval(MessageFormat.format(aggregateTemplate, tvh.getAggregateValues(sortNodesResult, j, offset == 1 ? 0 : offset, tvh.getSortNodeCodesSize(), sortNodesIds)))));
                    } catch (Throwable t) {
                        log.error(t.getMessage(), t);
                    }
                }
            }

            Collections.sort(values, (o1, o2) -> {
                if (o2.getValue() instanceof Double) {
                    return ((Double) o2.getValue()).compareTo((Double) o1.getValue());
                } else {
                    return ((String) o2.getValue()).compareTo((String) o1.getValue());
                }
            });

            long rowNumber = values.get(tvh.getResultRowNum() - 1).getRownum();
            if (topByConditionValues != null) {
                rowNumber = tvh.filterRowData(values, topByConditionValues, tvh.getResultRowNum());
            }

            if (tvh.getResultNodeAggrCodes().size() > 1) {

                if (values.size() < tvh.getResultRowNum()) {
                    return ReportGenerator.DEFAULT_FORMULA_RESULT;
                }

                List<Object[]> resultNodes = new ArrayList<>();

                for (Long nodId : resultNodesIds) {
                    resultNodes.addAll(em.createNativeQuery("SELECT ri.value,ri.nvalue,ri.nodeid,ri.itemrownumber from " + sourceTableName + " ri where ri.nodeid =:nodeid and ri.itemrownumber=:rowNume and ri.bankid=:bankid and ri.periodid=:periodid and ri.versionid=ri.latestversionid ").setParameter("bankid", tvh.getBankId()).setParameter("periodid", tvh.getPeriodId()).setParameter("nodeid", nodId).setParameter("rowNume", rowNumber).getResultList());
                }

                resultValue = new ArrayList<>();

                aggregateTemplate = tvh.getTopValueAggregateTemplate(tvh.getResultNodeAggrCodes(), tvh.getResultNodeAggrOperations());
                offset = resultNodes.size() / tvh.getResultNodeCodesSize();

                if (!resultNodes.isEmpty()) {
                    try {
                        resultValue.add(new TopValueHelper.RowDataModel(((BigDecimal) resultNodes.get(0)[3]).intValue(), tvh.eval(MessageFormat.format(aggregateTemplate, tvh.getAggregateValues(resultNodes, 0, offset, tvh.getResultNodeCodesSize(), resultNodesIds)))));
                    } catch (Throwable t) {
                        log.error(t.getMessage(), t);
                    }
                }

                return resultValue.isEmpty() ? ReportGenerator.DEFAULT_FORMULA_RESULT : String.valueOf(resultValue.get(0).getValue());

            } else {

                if (values.size() < tvh.getResultRowNum()) {
                    return ReportGenerator.DEFAULT_FORMULA_RESULT;
                }

                return (String) em.createNativeQuery("SELECT ri.value from " + sourceTableName + " ri where ri.nodeid =:nodeid and ri.itemrownumber=:rowNume and ri.bankid=:bankid and ri.periodid=:periodid and ri.versionid=ri.latestversionid ").setParameter("bankid", tvh.getBankId()).setParameter("periodid", tvh.getPeriodId()).setParameter("nodeid", tvh.getResultNodeId()).setParameter("rowNume", rowNumber).getSingleResult();
            }

        } else if (tvh.getResultNodeAggrCodes().size() > 1) {
            String aggregateTemplate = tvh.getTopValueAggregateTemplate(tvh.getResultNodeAggrCodes(), tvh.getResultNodeAggrOperations());

            List<Object[]> sortNodeResult = em.createNativeQuery("SELECT ri.value,ri.nvalue,ri.nodeid,ri.itemrownumber from " + sourceTableName + " ri where ri.nodeid =:nodeid and ri.bankid=:bankid and ri.periodid=:periodid and ri.versionid=ri.latestversionid ORDER BY" + (sortNode.getDataType().equals(MDTNodeDataTypes.TEXT) ? " ri.value ASC" : " ri.nvalue DESC ")).setParameter("nodeid", tvh.getSortNodeId()).setParameter("bankid", tvh.getBankId()).setParameter("periodid", tvh.getPeriodId()).getResultList();

            long rowNumber = ((BigDecimal) sortNodeResult.get(tvh.getResultRowNum() - 1)[3]).intValue();
            if (topByConditionValues != null) {
                rowNumber = tvh.filter(sortNodeResult, topByConditionValues, tvh.getResultRowNum());
            }

            List<Object[]> resultList = new ArrayList<>();

            for (Long nodeId : resultNodesIds) {
                resultList.addAll(em.createNativeQuery("SELECT ri.value,ri.nvalue,ri.nodeid,ri.itemrownumber from " + sourceTableName + " ri where ri.itemrownumber=:rownum and ri.nodeid =:nodeid and ri.bankid=:bankid and ri.periodid=:periodid and ri.versionid=ri.latestversionid ").setParameter("nodeid", nodeId).setParameter("bankid", tvh.getBankId()).setParameter("periodid", tvh.getPeriodId()).setParameter("rownum", rowNumber).getResultList());
            }


            int offset = resultList.size() / tvh.getResultNodeCodesSize();
            if (!resultList.isEmpty()) {
                try {
                    resultValue.add(new TopValueHelper.RowDataModel(((BigDecimal) resultList.get(0)[3]).intValue(), tvh.eval(MessageFormat.format(aggregateTemplate, tvh.getAggregateValues(resultList, 0, offset, tvh.getResultNodeCodesSize(), resultNodesIds)))));
                } catch (Throwable t) {
                    log.error(t.getMessage(), t);
                }
            }

            return resultValue.isEmpty() ? ReportGenerator.DEFAULT_FORMULA_RESULT : resultValue.get(0).getValue().toString();

        } else {
            List<Object[]> sortResult = em.createNativeQuery("select ri.value,ri.NVALUE,ri.nodeid,ri.itemrownumber from " + sourceTableName + " ri WHERE ri.nodeid =:nodeid and ri.bankid=:bankid and ri.periodid=:periodid and ri.versionid=ri.latestversionid ORDER BY " + (sortNode.getDataType().equals(MDTNodeDataTypes.TEXT) ? " ri.value ASC" : " ri.nvalue DESC ")).setParameter("nodeid", tvh.getSortNodeId()).setParameter("bankid", tvh.getBankId()).setParameter("periodid", tvh.getPeriodId()).getResultList();
            long rowNumber = ((BigDecimal) sortResult.get(tvh.getResultRowNum() - 1)[3]).intValue();
            if (topByConditionValues != null) {
                rowNumber = tvh.filter(sortResult, topByConditionValues, tvh.getResultRowNum());
            }

            List<Object[]> result = em.createNativeQuery("select ri.value,ri.NVALUE,ri.itemrownumber from " + sourceTableName + " ri WHERE ri.itemrownumber=:rowNum and ri.nodeid =:nodeid and ri.bankid=:bankid and ri.periodid=:periodid and ri.versionid=ri.latestversionid").setParameter("nodeid", tvh.getResultNodeId()).setParameter("bankid", tvh.getBankId()).setParameter("periodid", tvh.getPeriodId()).setParameter("rowNum", rowNumber).getResultList();

            return (String) result.get(0)[0];
        }

    }


    @Override
    public String topbyvalue(TopValueHelper tvh) {
        MDTNode listElNode = null;
        MDTNode defaultListElNode = null;
        long listElementNodeId;

        long defaultbank = tvh.getDefaultBankid() <= 0 ? tvh.getBankId() : tvh.getDefaultBankid();
        if (tvh.getDataElementFolderId() >= 0) {

            listElNode = em.createQuery("select n from IN_MDT_NODES n where n.equation=:dataNodeId and n.parentId=(select parentId from IN_MDT_NODES where id=:nodeId)", MDTNode.class).setParameter("nodeId", tvh.getSortNodeId()).setParameter("dataNodeId", String.valueOf(tvh.getDataElementFolderId())).getResultList().get(0);
            listElementNodeId = listElNode.getId();
            defaultListElNode = em.createQuery("select n from IN_MDT_NODES n where n.equation=:dataNodeId and n.parentId=(select parentId from IN_MDT_NODES where id=:nodeId)", MDTNode.class).setParameter("nodeId", tvh.getConditionNode()).setParameter("dataNodeId", String.valueOf(tvh.getDataElementFolderId())).getResultList().get(0);

        } else {
            listElementNodeId = tvh.getConditionNode();
        }

        List conditionList = em.createNativeQuery("SELECT ri.value,ri.itemrownumber from RESULT_VIEW_FULL ri WHERE ri.bankid=:bankid and ri.periodid=:periodId and ri.nodeid=:nodeid and ri.versionid=ri.latestversionid").setParameter("nodeid", listElementNodeId).setParameter("bankid", tvh.getBankId()).setParameter("periodId", tvh.getPeriodId()).getResultList();


        List<String> splitConditions = Arrays.asList(tvh.getConditionValue().split(","));

        MDTNode conditionMdtNode = em.find(MDTNode.class, tvh.getConditionNode());

        String resultType = "value";
        if (conditionMdtNode != null) {
            resultType = conditionMdtNode.getDataType() == MDTNodeDataTypes.NUMERIC ? "nvalue" : "value";
        }

        List result = em.createNativeQuery("select ri.value,ri.itemrownumber,(select value from RESULT_VIEW_FULL where nodeid=:defNode and bankid=:defBankid and periodid=:perioid and itemrownumber=ri.itemrownumber and versionid=ri.latestversionid) as dataelement from RESULT_VIEW_FULL ri where ri.nodeid=:nodeId and ri.bankid=:defBankid and ri.periodid=:perioid and ri." + resultType + " IN(:condition) and ri.versionid=ri.latestversionid").setParameter("nodeId", tvh.getConditionNode()).setParameter("defBankid", defaultbank).setParameter("perioid", tvh.getPeriodId()).setParameter("condition", splitConditions).setParameter("defNode", defaultListElNode != null ? defaultListElNode.getId() : tvh.getConditionNode()).getResultList();
        List<Object[]> objects = new ArrayList<>();

        for (int i = 0; i < conditionList.size(); i++) {
            Object[] o1 = (Object[]) conditionList.get(i);
            if (o1[0] == null) {
                continue;
            }
            for (int j = 0; j < result.size(); j++) {
                Object[] o2 = (Object[]) result.get(j);
                if (o1[0].equals(o2[2]) && !objects.contains(o1)) {
                    objects.add(o1);
                }
            }
        }

        tvh.setConditionValues(objects);

        return topvalue(tvh);
    }

    @Override
    public String getFiTypeCodeByFiCode(String bankCode) {
        List<String> result = em.createQuery("select bt.code from IN_BANK_TYPES bt where bt.id=(select b.fiType.id from IN_BANKS b where b.code=:code)", String.class).setParameter("code", bankCode).getResultList();

        if (result.size() == 1) {
            return result.get(0);
        }

        return ReportGenerator.DEFAULT_FORMULA_RESULT;
    }

    @Override
    public String getFiTypeNameByFiCode(String bankCode, long langId) {
        List<Description> result = em.createQuery("select bt.description from IN_BANK_TYPES bt where bt.id=(select b.fiType.id from IN_BANKS b where b.code=:code)", Description.class).setParameter("code", bankCode).getResultList();

        if (result.size() == 1) {
            return result.get(0).getDescription(langId);
        }

        return ReportGenerator.DEFAULT_FORMULA_RESULT;
    }

    @Override
    public String getRegionCodeByFiCode(String bankCode) {
        Region region = getFiRegion(bankCode);
        return region != null ? region.getCode() : ReportGeneratorBase.DEFAULT_FORMULA_RESULT;
    }

    @Override
    public String getRegionNameByFiCode(String bankCode, long langId) {
        Region region = getFiRegion(bankCode);
        return region != null && region.getDescription() != null ? region.getDescription().getDescription(langId) : ReportGeneratorBase.DEFAULT_FORMULA_RESULT;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public double regvaluevctsumif(long bankId, int periodId, String tableName, String sumColumnName, String columnName, String criterion) {
        try {
            List<Long> scheduleIDs = em.createQuery("select s.id from IN_SCHEDULES s where s.fi.id=:bankId and s.period.id=:periodId", Long.class).setParameter("bankId", bankId).setParameter("periodId", (long) periodId).getResultList();

            char operation = getCriterionOperation(criterion);
            String criterionValue = getCriterionValue(criterion, operation);


            if (scheduleIDs.isEmpty()) {
                return 0;
            }

            return regReportDataLocal.cregvaluevctsumif(tableName, columnName, sumColumnName, criterion, operation, criterionValue, scheduleIDs);

        } catch (Throwable t) {
            log.error(t);
        }
        return 0;
    }

    @Override
    public double regvaluevctsumifs(long bankId, long periodId, String tableName, String sumColumnName, List<String> criterionParams) {
        List<Long> scheduleIDs = em.createQuery("select s.id from IN_SCHEDULES s where s.fi.id=:bankId and s.period.id=:periodId", Long.class).setParameter("bankId", bankId).setParameter("periodId", (long) periodId).getResultList();

        Map<String, String> criterions = new HashMap<>();

        for (int i = 0; i < criterionParams.size(); i = i + 2) {
            String columnName = criterionParams.get(i);
            String criterion = criterionParams.get(i + 1);
            char operation = getCriterionOperation(criterion);

            String criterionValue = getCriterionValue(criterion, operation);

            criterions.put(columnName, criterionValue);
        }


        if (scheduleIDs.isEmpty()) {
            return 0;
        }
        return regReportDataLocal.cregvaluevctsumifs(tableName, sumColumnName, criterions, scheduleIDs);
    }

    private double calculatSumifValueWithMetaModel(List<VctSumIfDataItemMetaModel> criterionList, List<VctSumIfDataItemMetaModel> sumList, String criterion, CriterionFunctionOperation criterionFunctionOperation) {
        double result = 0;

        //< return id, < table id ,< row number, nValue > > >
        Map<Long, Map<Long, Map<Long, Double>>> sumMap = new HashMap<>();

        for (VctSumIfDataItemMetaModel item : sumList) {

            Map<Long, Map<Long, Double>> returnValues = sumMap.get(item.getReturnId());
            if (returnValues == null) {
                returnValues = new HashMap<>();
                sumMap.put(item.getReturnId(), returnValues);
            }

            Map<Long, Double> tableValues = returnValues.get(item.getTableId());
            if (tableValues == null) {
                tableValues = new HashMap<>();
                returnValues.put(item.getTableId(), tableValues);
            }

            tableValues.put(item.getRowNumber(), item.getnValue());
        }

        char operation = getCriterionOperation(criterion);
        String criterionValue = getCriterionValue(criterion, operation);

        boolean match;

        for (VctSumIfDataItemMetaModel item : criterionList) {

            MDTNode cachedNode = mdtCacheManager.getNode(item.getNodeId());

            double criterionDoubleValue = Double.NaN;
            if (cachedNode.getDataType() == MDTNodeDataTypes.NUMERIC) {
                criterionDoubleValue = stringTodouble(criterionValue);
            }
            match = false;

            switch (cachedNode.getDataType()) {
                case NUMERIC:
                    switch (operation) {
                        case '>':
                            match = item.getnValue() > criterionDoubleValue;
                            break;
                        case '<':
                            match = item.getnValue() < criterionDoubleValue;
                            break;
                        default:
                            match = item.getnValue() == criterionDoubleValue;
                    }
                    break;
                case DATE:
                case DATE_TIME:
                case TEXT:
                    match = Objects.equals(criterionValue, item.getValue());
                    break;
            }
            if (match) {
                Map<Long, Map<Long, Double>> returnValues = sumMap.get(item.getReturnId());
                if (returnValues != null) {
                    Map<Long, Double> tableValues = returnValues.get(item.getTableId());
                    if (tableValues != null) {
                        Object temp = tableValues.get(item.getRowNumber());
                        if (temp != null) {
                            switch (criterionFunctionOperation) {
                                case COUNT -> result++;
                                case SUM -> result += (double) (temp);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    private double calculatSumifValue(List<ResultViewFull> criterionList, List<ResultViewFull> sumList, String criterion, CriterionFunctionOperation criterionFunctionOperation) {
        double result = 0;

        //< return id, < table id ,< row number, nValue > > >
        Map<Long, Map<Long, Map<Long, Double>>> sumMap = new HashMap<>();

        for (ResultViewFull item : sumList) {

            Map<Long, Map<Long, Double>> returnValues = sumMap.computeIfAbsent(item.getReturnId(), k -> new HashMap<>());

            Map<Long, Double> tableValues = returnValues.computeIfAbsent(item.getTableId(), k -> new HashMap<>());

            tableValues.put(item.getRowNumber(), item.getnValue());
        }

        char operation = getCriterionOperation(criterion);
        String criterionValue = getCriterionValue(criterion, operation);

        boolean match;

        for (ResultViewFull item : criterionList) {

            double criterionDoubleValue = Double.NaN;
            MDTNode node = mdtCacheManager.getMdtNodesById().get(item.getNodeId());
            if (node.getDataType() == MDTNodeDataTypes.NUMERIC) {
                criterionDoubleValue = stringTodouble(criterionValue);
            }
            match = false;

            switch (node.getDataType()) {
                case NUMERIC:
                    switch (operation) {
                        case '>':
                            match = item.getnValue() > criterionDoubleValue;
                            break;
                        case '<':
                            match = item.getnValue() < criterionDoubleValue;
                            break;
                        default:
                            match = item.getnValue() == criterionDoubleValue;
                    }
                    break;
                case DATE:
                case DATE_TIME:
                case TEXT:
                    match = Objects.equals(criterionValue, item.getValue());
                    break;
            }
            if (match) {
                Map<Long, Map<Long, Double>> returnValues = sumMap.get(item.getReturnId());
                if (returnValues != null) {
                    Map<Long, Double> tableValues = returnValues.get(item.getTableId());
                    if (tableValues != null) {
                        Object temp = tableValues.get(item.getRowNumber());
                        if (temp != null) {
                            switch (criterionFunctionOperation) {
                                case SUM -> result += (double) (temp);
                                case COUNT -> result++;
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    private int extractIntSingleResult(Object queryResult) {
        if (queryResult instanceof BigDecimal) {
            return ((BigDecimal) queryResult).intValue();
        } else if (queryResult instanceof BigInteger) {
            return ((BigInteger) queryResult).intValue();
        } else {
            return (int) queryResult;
        }
    }

    private String getRegionalAddress(List<Region> regions, String lastAddress, long regionId, long langId) {
        StringBuilder sb = new StringBuilder();
        try {
            Map<Long, Region> regionMap = new HashMap<>();
            for (Region r : regions) {
                regionMap.put(r.getId(), r);
            }

            sb.append(regionMap.get(regionId).getDescription().getDescription(langId)).append(", ");
            int i = 0;
            while (i < regions.size()) {
                i++;
                regionId = regionMap.get(regionId).getParentId();
                if (regionId == 0) break;
                String addr = regionMap.get(regionId).getDescription().getDescription(langId);
                String tmp = sb.toString();
                sb.replace(0, sb.length(), addr);
                sb.append(", ").append(tmp);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        return sb.append(lastAddress).toString();
    }

    @Override
    public List<ReportPropertyModel> loadReportProperties(int reportId) {
        List<ReportPropertyModel> result = new ArrayList<>();
        List<ReportProperty> properties = em.createQuery("select rp from OUT_REPORT_PROPERTIES rp where rp.reportId=:reportId", ReportProperty.class).setParameter("reportId", reportId).getResultList();
        for (ReportProperty property : properties) {
            ReportPropertyModel model = new ReportPropertyModel();
            model.setReportId(property.getReportId());
            model.setKey(property.getKey());
            model.setName(property.getName());
            model.setValue(property.getValue());
            model.setRenewable(property.isRenewable());
            result.add(model);
        }
        return result;
    }

    @Override
    public void reorder(int neigbourReportId, int selectedReportId, boolean upDown) {
        Report neighbourReport = findById(neigbourReportId);
        Report selectedReport = findById(selectedReportId);
        int id;
        if (upDown) {
            id = selectedReportId;
            selectedReport.setSequence(neighbourReport.getSequence());
        } else {
            id = neigbourReportId;
            neighbourReport.setSequence(selectedReport.getSequence());
        }

        List<Report> reports = em.createQuery("SELECT r FROM OUT_REPORTS r where r.parentId=:parentId AND r.sequence>=:reportSequence ORDER BY r.sequence asc", Report.class).setParameter("parentId", selectedReport.getParentId()).setParameter("reportSequence", selectedReport.getSequence()).getResultList();

        for (Report r : reports) {
            if (r.getId() != id) {
                r.setSequence(r.getSequence() + 1);
            }
        }
    }

    @Override
    public List<Integer> loadReportsByIdSorted(Collection<Integer> ids) {
        return em.createQuery("select r.id from OUT_REPORTS r where r.id in :ids order by r.sequence", Integer.class).setParameter("ids", ids).getResultList();
    }

    @Override
    public void moveReport(Report report) {
        em.merge(report);
    }

    @Override
    public ReportType getReportTypeById(int reportId) {
        return em.find(Report.class, reportId).getReportType();
    }

    @Override
    public String getReportPath(int reportId) {
        StringBuilder path = new StringBuilder("/0");
        getReportPathRecursive(reportId, path);
        return path.toString();
    }

    @Override
    public List<Report> loadReportPathArray(int id) {
        List<Report> path = new ArrayList<>();
        Report current = em.find(Report.class, id);
        getReportParent(current, path);
        return path;
    }

    @Override
    public boolean reorderReports(int id, int parentId, boolean upDown) {
        Report report = em.find(Report.class, id);
        List<Report> children = em.createQuery("select r from OUT_REPORTS r where r.parentId=:parentId order by r.sequence asc ", Report.class).setParameter("parentId", parentId).getResultList();

        int reportIndex = children.indexOf(report);
        Report neighbourReport = null;
        Integer reportSequence = report.getSequence();
        Integer neighbourSequence;
        if (upDown && reportIndex > 0) {
            neighbourReport = children.get(reportIndex - 1);
            neighbourSequence = neighbourReport.getSequence();
            neighbourReport.setSequence(reportSequence);
            report.setSequence(neighbourSequence);


        } else if (!upDown && reportIndex < children.size() - 1) {
            neighbourReport = children.get(reportIndex + 1);
            neighbourSequence = neighbourReport.getSequence();
            neighbourReport.setSequence(reportSequence);
            report.setSequence(neighbourSequence);
        }
        if (neighbourReport != null) {
            em.merge(report);
            em.merge(neighbourReport);

            return true;
        }

        return false;
    }

    @Override
    public void updateReportInfo(int reportId, ReportInfo reportInfo) throws FinATypeException {
        try {

            em.createQuery("update  OUT_REPORTS r set r.info=:info where r.id=:reportId")
                    .setParameter("info", ObjectSerializer.serialize(reportInfo))
                    .setParameter("reportId", reportId)
                    .executeUpdate();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new FinATypeException(FinATypeException.Type.GENERAL_ERROR);
        }
    }

    @Override
    public void updateReportTemplate(int reportId, byte[] template) throws FinATypeException {
        try {

            em.createQuery("update  OUT_REPORTS r set r.template=:template where r.id=:reportId")
                    .setParameter("template", template)
                    .setParameter("reportId", reportId)
                    .executeUpdate();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new FinATypeException(FinATypeException.Type.GENERAL_ERROR);
        }
    }

    @Override
    public byte[] renameNamedRange(ReportType reportType, byte[] template, String oldName, String newName) throws FinATypeException {
        try {

            if (reportType.equals(ReportType.EXCEL)) {

                try (Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(template))) {

                    Name name = wb.getName(oldName);

                    if (name == null) {

                        for (Name n : wb.getAllNames()) {
                            if (n.getNameName().equalsIgnoreCase(oldName) && n.getSheetIndex() == 0) { // sheet 0 scope
                                name = n;
                                break;
                            }
                        }
                    }

                    if (name == null) throw new IllegalArgumentException("Named range '" + oldName + "' not found");

                    int scope = name.getSheetIndex();
                    for (Name n : wb.getAllNames()) {
                        if (n.getNameName().equalsIgnoreCase(newName) && n.getSheetIndex() == scope) {
                            throw new FinATypeException("A name '" + newName + "' already exists in this scope");
                        }
                    }

                    // Rename
                    name.setNameName(newName);

                    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                        wb.write(out);
                        return out.toByteArray();
                    }
                }
            } else if (reportType.equals(ReportType.DEFAULT)) {
                AooRenameNamedRangeTask renameTask = new AooRenameNamedRangeTask(template, oldName, newName);
                aooServiceManager.getOfficeManager().execute(renameTask);

                return renameTask.getContent();
            }
        } catch (FinATypeException fe) {
            throw fe;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new FinATypeException(FinATypeException.Type.GENERAL_ERROR);
        }

        throw new FinATypeException(FinATypeException.Type.GENERAL_ERROR, "Cannot rename Pentaho Report range");
    }

    private void getReportParent(Report current, List<Report> path) {
        if (current != null) {
            if (current.getParentId() != 0) {
                Report tmpNode = em.createQuery("select r from OUT_REPORTS r where r.id=:parentId", Report.class).setParameter("parentId", current.getParentId()).getSingleResult();
                getReportParent(tmpNode, path);
            }
            path.add(current);
        }
    }

    private void getReportPathRecursive(int id, StringBuilder path) {
        Report report = em.find(Report.class, id);
        if (report != null) {
            path.append("/").append(report.getId());
            if (report.getParentId() > 0) {
                getReportPathRecursive(report.getParentId(), path);
            }
        }

    }

    private Region getFiRegion(String bankCode) {
        try {
            Fi fi = em.createQuery("select fi from IN_BANKS fi where fi.code=:code", Fi.class).setParameter("code", bankCode.trim()).getSingleResult();

            return em.find(Region.class, fi.getRegionId());
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
        return null;
    }


    private void distributeReport(Report report, ScheduleReport scheduleReport) {
        try {
            Language language = languageLocal.getLanguageById(scheduleReport.getLangId());
            String reportName = report.getDescription().getDescription(scheduleReport.getLangId()).trim() + "(" + (language.getName() != null ? language.getName().trim() : language.getCode().trim()) + ")";
            reportName = reportName.trim() + getExtensionByType(report.getReportType());
            saveReportIntoFolder(report, scheduleReport.getFileStorageLocation(), language, reportName);
            String userLogin = current.findUserbyId(scheduleReport.getUserId()).getLogin();
            reportContentManagementEventEvent.fire(new ReportContentManagementEvent(scheduleReport.getRepositoryNodeId(), report.getReportType(), reportName.trim(), scheduleReport.getNotificationMails(), userLogin, report.getGeneratedContent()));
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
    }

    private void saveReportIntoFolder(Report report, String path, Language language, String fileName) {
        try {
            if (path != null && !path.trim().isEmpty()) {
                File reportStorageDirectory = new File(path);
                if (reportStorageDirectory.exists() && reportStorageDirectory.isDirectory()) {
                    log.info("Saving Report (" + report.getCode() + ")[" + fileName + "] into directory [" + path + "]");
                    FileUtils.writeByteArrayToFile(new File(reportStorageDirectory.getPath() + File.separator + fileName), report.getGeneratedContent());
                } else {
                    log.warn("Specified path [" + path + "] does not exists or is not directory to store generated report!");
                }
            }
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
    }

    private String getExtensionByType(ReportType reportType) {
        String extension = ".ods";
        switch (reportType) {
            case EXCEL:
                extension = ".xlsx";
                break;
            case DEFAULT:
                extension = ".ods";
                break;
        }

        return extension;
    }

    private void deleteOutReportByIds(List<Integer> reportsIds) {
        if (reportsIds != null && !reportsIds.isEmpty()) {
            for (Integer reportId : reportsIds) {
                deleteOutReportById(reportId);
            }
        }
    }

    private void deleteOutReportById(int id) {
        Report report = em.find(Report.class, id);
        if (report != null) {
            em.remove(report);
        }
    }

    private List<ResultViewFull> loadVctItems(ReportData reportData, List<Long> fiIds, long nodeId, Date startDate, Date toDate, String versionCode) {
        String sqlString = "select ri.RETURNID,ri.NODEID,ri.ITEMTABLEID,ri.ITEMROWNUMBER,ri.NVALUE,ri.VALUE,ri.VERSIONID from RESULT_VIEW_FULL ri where ri.bankid IN :bankIds and ri.nodeid=:nodeId and ri.fromdate =:periodFrom and ri.todate=:periodTo and " + (versionCode == null || versionCode.trim().isEmpty() || versionCode.equalsIgnoreCase(ReportConstants.LATEST_VERSION) ? " ri.VERSIONID=ri.LATESTVERSIONID " : " ri.VERSIONID=" + reportData.versionCodeIdMap.get(versionCode.toUpperCase()));
        Query query = em.createNativeQuery(sqlString, ResultViewFull.class);
        List<ResultViewFull> result = query.setParameter("bankIds", fiIds).setParameter("nodeId", nodeId).setParameter("periodFrom", startDate).setParameter("periodTo", toDate).getResultList();

        return result;
    }
}
