package net.fina.server.reports.impl;

import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.filter.ScheduleReportFilter;
import net.fina.common.client.reports.ScheduleReportStatus;
import net.fina.common.client.reports.ScheduledReportInfo;
import net.fina.common.server.util.ObjectSerializer;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.reports.api.ScheduleReportLocal;
import net.fina.server.reports.entity.Report;
import net.fina.server.reports.entity.ScheduleReport;
import net.fina.server.reports.entity.ScheduleReportId;
import net.fina.server.reports.entity.ScheduleReport_;
import net.fina.server.security.api.UserLocal;
import org.jboss.logging.Logger;

import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Stateless
@Local(ScheduleReportLocal.class)
@Interceptors(RecordingAuditor.class)
public class ScheduleReportSession implements ScheduleReportLocal {

    @Inject
    private EntityManager em;

    @Inject
    private Logger log;

    @EJB
    private UserLocal current;

    @EJB
    private ReportScheduleManager reportScheduleManager;

    @Override
    public List<ScheduleReport> load(Map<ScheduleReportFilter, Object> scheduleReportFilterObjectMap) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ScheduleReport> criteriaQuery = cb.createQuery(ScheduleReport.class);
        Root<ScheduleReport> scheduleReportRoot = criteriaQuery.from(ScheduleReport.class);
        List<Predicate> predicates = getFilterPredicate(cb, scheduleReportRoot, scheduleReportFilterObjectMap);
        criteriaQuery.where(predicates.toArray(new Predicate[0]));
        return em.createQuery(criteriaQuery).getResultList();
    }

    private List<Predicate> getFilterPredicate(CriteriaBuilder cb, Root<ScheduleReport> scheduleReportRoot, Map<ScheduleReportFilter, Object> filter) {
        List<Predicate> predicates = new ArrayList<>();
        if (filter != null) {
            for (Map.Entry<ScheduleReportFilter, Object> entry : filter.entrySet()) {
                if (entry.getValue() != null) {
                    switch (entry.getKey()) {
                        case STATUS: {
                            predicates.add(cb.equal(scheduleReportRoot.get(ScheduleReport_.status), entry.getValue()));
                            break;
                        }
                        case REPORT_ID: {
                            predicates.add(cb.equal(scheduleReportRoot.get(ScheduleReport_.reportId), entry.getValue()));
                        }
                        case SCHEDULE_REPORT_ID: {
                            ScheduleReportId srId = (ScheduleReportId) entry.getValue();
                            predicates.add(cb.equal(scheduleReportRoot.get(ScheduleReport_.reportId), srId.getReportId()));
                            predicates.add(cb.equal(scheduleReportRoot.get(ScheduleReport_.langId), srId.getLangId()));
                            predicates.add(cb.equal(scheduleReportRoot.get(ScheduleReport_.hashcode), srId.getHashcode()));
                        }
                    }
                }
            }
        }
        return predicates;
    }

    @SuppressWarnings("unchecked")
    @Override

    @Deprecated
    public List<ScheduledReportInfo> load() {

        List<ScheduledReportInfo> infos = new ArrayList<ScheduledReportInfo>();

        Query query = em.createQuery("select ors , ork ,lang.name, u.login from OUT_REPORTS_SCHEDULE ors,  OUT_REPORTS ork, SYS_LANGUAGES lang, SYS_USERS u where ors.reportId=ork.id and lang.id = ors.langId and u.id = ors.userId");
        List<Object[]> objects = query.getResultList();

        List<Integer> reportids = new ArrayList<Integer>();
        for (Object[] object1 : objects) {
            ScheduleReport object = (ScheduleReport) object1[0];
            ScheduledReportInfo scheduleInfo = new ScheduledReportInfo();
            scheduleInfo.setReportId(object.getReportId());
            scheduleInfo.setLangId(object.getLangId());
            scheduleInfo.setStatus(object.getStatus().ordinal());
            scheduleInfo.setOnDemand(object.getOnDemand() != null && object.getOnDemand() == 1);
            scheduleInfo.setScheduleTime(object.getScheduleTime());
            scheduleInfo.setHashcode(object.getHashcode());
//            scheduleInfo.setId(object.getId());
            scheduleInfo.setFolder(false);
            scheduleInfo.setUserId(object.getUserId());

            Report report = (Report) object1[1];
            scheduleInfo.setParentId(report.getParentId());
            scheduleInfo.setCode(report.getCode());
            scheduleInfo.setDescription(report.getDescription().getDescriptions());
            scheduleInfo.setReportType(report.getReportType());
            reportids.add(scheduleInfo.getParentId());

            scheduleInfo.setLanguageName(object1[2].toString());
            scheduleInfo.setCreatorUser(object1[3].toString());

            scheduleInfo.setFileStorageLocation(object.getFileStorageLocation());
            scheduleInfo.setRepositoryNodeId(object.getRepositoryNodeId());
            scheduleInfo.setNotificationMails(object.getNotificationMails());
            scheduleInfo.setRepositoryFolderName(object.getRepositoryFolderName());

            infos.add(scheduleInfo);
        }
        if (reportids.size() > 0) {
            //TODO
            Query getfolder = em.createQuery("select ors from  OUT_REPORTS ors where ors.id in(:ids) and ors.type=1");
            getfolder.setParameter("ids", reportids);
            List<Report> reports = getfolder.getResultList();
            for (Report report : reports) {
                ScheduledReportInfo reportInfo = new ScheduledReportInfo();
                reportInfo.setDescription(report.getDescription().getDescriptions());
                reportInfo.setReportId(report.getId());
                reportInfo.setCode(report.getCode());
                reportInfo.setFolder(true);
                infos.add(reportInfo);
            }
        }
        return infos;
    }

    @Override
    public void save(ScheduleReport scheduleReport) throws FinATypeException {

        //Remove existing report schedule
        ScheduleReportId srId = new ScheduleReportId(scheduleReport.getReportId(), scheduleReport.getLangId(), scheduleReport.getHashcode());
        ScheduleReport existingReport = em.find(ScheduleReport.class, srId);
        if (existingReport != null) {
            em.remove(existingReport);
        }

        //Set user and info
        scheduleReport.setUserId((int) current.getCurrentUserId());
        if (scheduleReport.getInfo() == null) {
            Report report = em.find(Report.class, scheduleReport.getReportId());
            scheduleReport.setInfo(report.getInfo());
        }

        //Calculate report info hash
        try {
            scheduleReport.setHashcode(ObjectSerializer.deSerialize(scheduleReport.getInfo()).hashCode());
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }

        //Save report schedule
        em.persist(scheduleReport);

        //Create scheduler
        reportScheduleManager.createReportSchedule(new ScheduleReportId(scheduleReport.getReportId(), scheduleReport.getLangId(), scheduleReport.getHashcode()));
    }

    @Override
    public void updateStatus(ScheduleReport scheduleReport, ScheduleReportStatus status) {
        ScheduleReportId srId = new ScheduleReportId(scheduleReport.getReportId(), scheduleReport.getLangId(), scheduleReport.getHashcode());
        ScheduleReport sr = em.find(ScheduleReport.class, srId);
        if (sr != null) {
            sr.setStatus(status);
        }
    }

    @Override
    public void delete(List<ScheduleReportId> scheduledReportIds) throws FinATypeException {

        if (scheduledReportIds != null) {
            for (ScheduleReportId srId : scheduledReportIds) {
                em.remove(em.find(ScheduleReport.class, srId));
            }

            //Remove rom scheduler
            for (ScheduleReportId scheduledReportId : scheduledReportIds) {
                reportScheduleManager.removeReportScheduleJob(scheduledReportId);
            }
        }
    }

    @Override
    public byte[] loadScheduleReportInfo(ScheduleReportId scheduleReportId) {
        ScheduleReport report = em.find(ScheduleReport.class, scheduleReportId);
        return report.getInfo();
    }
}
