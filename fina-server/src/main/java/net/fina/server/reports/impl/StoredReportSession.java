package net.fina.server.reports.impl;

import global.namespace.truelicense.api.LicenseManagementException;
import jakarta.ejb.*;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.Root;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.filter.ReportFilter;
import net.fina.common.client.reports.ReportType;
import net.fina.common.client.reports.ScheduledReportInfo;
import net.fina.common.shared.SortField;
import net.fina.common.shared.report.StoredReportModelSimple;
import net.fina.report.core.ReportUtil;
import net.fina.server.i18n.helper.Description;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.jcr.impl.FileContentManagementSession;
import net.fina.server.reports.api.ReportLocal;
import net.fina.server.reports.api.StoredReportLocal;
import net.fina.server.reports.entity.Report;
import net.fina.server.reports.entity.StoredReport;
import net.fina.server.reports.entity.StoredReportPk;
import net.fina.server.reports.entity.StoredReport_;
import net.fina.server.reports.event.StoredReportEvent;
import net.fina.server.security.product.Product;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.logging.Logger;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Singleton
@Interceptors(RecordingAuditor.class)
public class StoredReportSession implements StoredReportLocal {

    private final Logger log = Logger.getLogger(StoredReportSession.class);

    @Inject
    private EntityManager em;
    @EJB
    private ReportLocal reportLocal;
    @Inject
    private FileContentManagementSession fileContentManagementSession;

    @Override
    public StoredReport loadGeneratedReport(StoredReportPk reportPk) {
        try {
            Product.getInstance().check();
        } catch (LicenseManagementException e) {
            throw new RuntimeException(e.getMessage());
        }
        StoredReport storedReport = null;
        List<StoredReport> storedReports = em.createQuery("SELECT storedReps FROM OUT_STORED_REPORTS storedReps WHERE storedReps.reportPk.reportId=:reportId and storedReps.reportPk.langId=:langId and storedReps.reportPk.hashCode=:hashCode", StoredReport.class)
                .setParameter("reportId", reportPk.getReportId())
                .setParameter("langId", reportPk.getLangId())
                .setParameter("hashCode", reportPk.getHashCode())
                .getResultList();
        if (!storedReports.isEmpty()) {
            storedReport = storedReports.get(0);
        }
        return fileContentManagementSession.loadGeneratedReport(storedReport);
    }

    @Override
    public StoredReport loadGeneratedReportLite(StoredReportPk reportPk) {
        try {
            Product.getInstance().check();
        } catch (LicenseManagementException e) {
            throw new RuntimeException(e.getMessage());
        }
        StoredReport storedReport = null;
        List<StoredReport> storedReports = em.createQuery("SELECT new " + StoredReport.class.getName() + "(storedReps.userId,storedReps.storeDate) FROM OUT_STORED_REPORTS storedReps WHERE storedReps.reportPk.reportId=:reportId and storedReps.reportPk.langId=:langId and storedReps.reportPk.hashCode=:hashCode", StoredReport.class)
                .setParameter("reportId", reportPk.getReportId())
                .setParameter("langId", reportPk.getLangId())
                .setParameter("hashCode", reportPk.getHashCode())
                .getResultList();
        if (storedReports.size() > 0) {
            storedReport = storedReports.get(0);

        }
        return storedReport;
    }

    @Override

    public boolean isGenerated(StoredReportPk reportPk) {
        return em.find(StoredReport.class, reportPk) != null;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @TransactionTimeout(unit = TimeUnit.HOURS, value = ReportSession.REPORT_GENERATE_TRANSACTION_TIMEOUT_HOURS)
    public void saveGeneratedReport(StoredReport report) throws FinATypeException {
        saveGeneratedReportSameTransaction(report);
    }

    @Override
    @Lock(LockType.WRITE)
    public void saveGeneratedReportSameTransaction(StoredReport report) throws FinATypeException {
        em.createQuery("delete from OUT_STORED_REPORTS o where o.reportPk.reportId=:reportId and o.reportPk.langId=:langId and o.reportPk.hashCode=:hashcode")
                .setParameter("reportId", report.getReportPk().getReportId())
                .setParameter("langId", report.getReportPk().getLangId())
                .setParameter("hashcode", report.getReportPk().getHashCode())
                .executeUpdate();

        fileContentManagementSession.saveStoredReport(report);
    }

    @Override
    public void delete(StoredReportPk reportPk) throws FinATypeException {
        int removeCount = em.createNativeQuery("delete from OUT_STORED_REPORTS where reportID=:reportID and langID=:langID and hashCode=:hashCode")
                .setParameter("reportID", reportPk.getReportId())
                .setParameter("langID", reportPk.getLangId())
                .setParameter("hashCode", reportPk.getHashCode())
                .executeUpdate();
        log.info(removeCount + " stored report(s) removed.");
    }

    @Override
    public void delete(List<StoredReportPk> reportPks) throws FinATypeException {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<StoredReport> deleteQuery = cb.createCriteriaDelete(StoredReport.class);
        Root<StoredReport> root = deleteQuery.from(StoredReport.class);
        deleteQuery.where(root.get(StoredReport_.reportPk).in(reportPks));

        int count = em.createQuery(deleteQuery).executeUpdate();
        log.info(count + " stored report(s) removed.");
    }

    @Override
    public List<ScheduledReportInfo> loadStoredReports(Map<ReportFilter, Object> reportFilterObjectMap, SortField sortField) {
        List<ScheduledReportInfo> infos = new ArrayList<>();

        String sql = "select " +
                /*0*/ "ors.reportPk," +
                /*1*/ "ors.storeDate," +
                /*2*/ "ors.userId," +
                /*3*/ "ork.parentId," +
                /*4*/ "ork.code," +
                /*5*/ "ork.description," +
                /*6*/ "ork.reportType," +
                /*7*/ "lang.name," +
                /*8*/ "u.login " +
                "from OUT_STORED_REPORTS ors, OUT_REPORTS ork, SYS_LANGUAGES lang, SYS_USERS u " +
                "where ors.reportPk.reportId=ork.id and ors.reportPk.langId=lang.id  and ors.userId = u.id ";

        if (reportFilterObjectMap.get(ReportFilter.FOLDER_ID) != null) {
            sql += " and ork.parentId=:parentId";
        }
        if (sortField == null) {
            sql += " order by ors.storeDate desc";
        } else {
            sql += " order by ors." + sortField;
        }

        TypedQuery<Object[]> query = em.createQuery(sql, Object[].class);
        if (reportFilterObjectMap.get(ReportFilter.FOLDER_ID) != null) {
            int parentId = (int) reportFilterObjectMap.get(ReportFilter.FOLDER_ID);
            query.setParameter("parentId", parentId);
        }

        List<Object[]> resultList = query.getResultList();

        for (Object[] objects : resultList) {
            ScheduledReportInfo scheduleInfo = new ScheduledReportInfo();

            // OUT_STORED_REPORTS
            StoredReportPk reportPk = (StoredReportPk) objects[0];
            scheduleInfo.setReportInfoHashCode(reportPk.getHashCode());
            scheduleInfo.setHashcode(reportPk.getHashCode());
            scheduleInfo.setReportId(reportPk.getReportId());
            scheduleInfo.setLangId(reportPk.getLangId());
            scheduleInfo.setFolder(false);
            scheduleInfo.setScheduleTime((java.util.Date) objects[1]);
            scheduleInfo.setUserId((Long) objects[2]);

            //OUT_REPORTS
            scheduleInfo.setParentId((Integer) objects[3]);
            scheduleInfo.setDescription(((Description) (objects[5])).getDescriptions());
            scheduleInfo.setCode(objects[4] != null ? objects[4].toString() : null);

            // lang and user
            scheduleInfo.setLanguageName(objects[7].toString());
            scheduleInfo.setCreatorUser(objects[8].toString());
            scheduleInfo.setReportType(ReportType.valueOf(objects[6].toString()));

            infos.add(scheduleInfo);
        }
        return infos;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveReportHtmlResult(@Observes(during = TransactionPhase.AFTER_SUCCESS) StoredReportEvent event) {
        try {
            StoredReport storedReport = em.find(StoredReport.class, event.getStoredReportPk());
            storedReport.setReportHtmlResult(event.getHtmlResult());
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
    }

    @Override

    public StoredReport findGeneratedReport(int reportId, int langId) {
        try {
            Report report = reportLocal.findById(reportId);
            StoredReportPk storedReportPk = new StoredReportPk();
            storedReportPk.setReportId(reportId);
            storedReportPk.setHashCode(ReportUtil.getReportInfo(report.getInfo()).hashCode());
            storedReportPk.setLangId(langId);
            return loadGeneratedReport(storedReportPk);
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
        return null;
    }

    @Override

    public List<ScheduledReportInfo> loadStatistics(int id, long langId, String reportName) {
        StoredReportPk pk = new StoredReportPk();
        pk.setReportId(id);
        TypedQuery<Object[]> query = em.createQuery("select distinct(ost.userId),max(ost.storeDate),max(ost.reportPk.hashCode),max(u.login) from OUT_STORED_REPORTS  ost,SYS_USERS u where ost.reportPk.reportId=:id and ost.userId=u.id group by ost.userId order by max(ost.storeDate) desc", Object[].class)
                .setFirstResult(0)
                .setMaxResults(5)
                .setParameter("id", id);

        List<Object[]> queryResult = query.getResultList();
        Query reportsQuery = em.createQuery("select ost from OUT_STORED_REPORTS ost where ost.reportPk.reportId=:repId and ost.userId=:userId and ost.reportPk.langId=:langId order by ost.storeDate desc ", StoredReport.class);
//        Query countQuery = em.createQuery("select count(ost.reportPk.hashCode) from OUT_STORED_REPORTS  ost,SYS_USERS  u where ost.reportPk.reportId=:reportId and ost.userId=:userId and u.id=:userId", Long.class);

        List<ScheduledReportInfo> resultList = new ArrayList<>();

        ScheduledReportInfo reportInfo = null;
        for (Object[] obj : queryResult) {
            long userId = (Long) obj[0];
            reportsQuery
                    .setParameter("repId", id)
                    .setParameter("userId", userId)
                    .setParameter("langId", (int) langId)
                    .setFirstResult(0)
                    .setMaxResults(1);
            List<StoredReport> reports = reportsQuery.getResultList();
            if (reports.isEmpty()) {
                return resultList;
            }
            StoredReport sr = reports.get(0);
            reportInfo = convertReport(sr, langId, reportName);
            reportInfo.setCreatorUser((String) obj[3]);
            reportInfo.setName(reportName);
            reportInfo.setUserId(userId);
            reportInfo.setCount(((Number) obj[2]).intValue());
            resultList.add(reportInfo);
        }
        Collections.sort(resultList, new Comparator<ScheduledReportInfo>() {
            @Override
            public int compare(ScheduledReportInfo o1, ScheduledReportInfo o2) {
                return o2.getScheduleTime().compareTo(o1.getScheduleTime());
            }
        });
        return resultList;
    }

    @Override

    public List<ScheduledReportInfo> getGeneratedReports(int id, long langId, long userId, String reportName) {
        Query query = em.createQuery("select ost from OUT_STORED_REPORTS ost where ost.reportPk.reportId=:repId and ost.userId=:userId and ost.reportPk.langId=:lanId order by ost.storeDate desc ", StoredReport.class)
                .setParameter("repId", id)
                .setParameter("userId", userId)
                .setParameter("lanId", (int) langId)
                .setFirstResult(1);
        List<StoredReport> storedReports = query.getResultList();
        List<ScheduledReportInfo> models = new ArrayList<>();
        ScheduledReportInfo sc = null;
        for (StoredReport r : storedReports) {
            sc = convertReport(r, langId, reportName);
            models.add(sc);
        }
        return models;
    }

    @Override
    public boolean existGeneratedReport(StoredReportPk reportPk) {
        List<Long> storedReports = em.createQuery("SELECT storedReps.userId FROM OUT_STORED_REPORTS storedReps WHERE storedReps.reportPk.reportId=:reportId and storedReps.reportPk.langId=:langId and storedReps.reportPk.hashCode=:hashCode", Long.class)
                .setParameter("reportId", reportPk.getReportId())
                .setParameter("langId", reportPk.getLangId())
                .setParameter("hashCode", reportPk.getHashCode())
                .getResultList();
        return storedReports.size() > 0;
    }
    @SuppressWarnings("JpaQlInspection")
    @Override
    public List<StoredReportModelSimple> loadDistinctStoredReports(long langId) {
        List<StoredReportModelSimple> result = em.createQuery("select new " + StoredReportModelSimple.class.getName() + "(orr.id,orr.parentId,orr.code,ss.value,ors.reportPk.hashCode,orr.reportType) " +
                        "from OUT_STORED_REPORTS ors inner join OUT_REPORTS orr on ors.reportPk.reportId=orr.id left join SYS_STRINGS ss on ss.id=orr.description and ss.langId=:langID where ors.reportPk.langId=:langID  order by orr.code,ors.storeDate desc", StoredReportModelSimple.class)
                .setParameter("langID", langId)
                .getResultList();
        return new ArrayList<>(result.stream()
                .collect(Collectors.toMap(StoredReportModelSimple::getReportId, report -> report, (r1, r2) -> r1))
                .values());
    }

    private ScheduledReportInfo convertReport(StoredReport report, long langid, String reportName) {
        ScheduledReportInfo reportInfo = new ScheduledReportInfo();
        reportInfo.setReportId(report.getReportPk().getReportId());
        Map<Long, String> description = new HashMap<>();
        description.put(langid, reportName);
        reportInfo.setDescription(description);
        reportInfo.setReportInfoHashCode(report.getReportPk().getHashCode());
        reportInfo.setScheduleTime(report.getStoreDate());
        reportInfo.setUserId(report.getUserId());
        reportInfo.setHashcode(report.hashCode());
        reportInfo.setLangId(report.getReportPk().getLangId());
        reportInfo.setName(reportName);
        return reportInfo;

    }

}
