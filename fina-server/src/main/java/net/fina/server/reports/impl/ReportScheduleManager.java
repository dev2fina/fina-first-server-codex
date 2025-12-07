package net.fina.server.reports.impl;

import net.fina.common.client.filter.ScheduleReportFilter;
import net.fina.common.client.reports.ScheduleReportStatus;
import net.fina.common.server.util.CommonUtil;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.reports.api.ReportLocal;
import net.fina.server.reports.api.ScheduleReportLocal;
import net.fina.server.reports.entity.ScheduleReport;
import net.fina.server.reports.entity.ScheduleReportId;
import org.jboss.logging.Logger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.DependsOn;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.interceptor.Interceptors;
import java.io.Serializable;
import java.util.*;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

@Singleton
@Startup
@DependsOn({"SysStringCacheManager"})
@Interceptors(RecordingAuditor.class)
public class ReportScheduleManager implements Serializable {

    private Logger log = Logger.getLogger(getClass());

    private Scheduler scheduler;
    private static final Map<ScheduleReportId, JobKey> jobs = Collections.synchronizedMap(new HashMap<ScheduleReportId, JobKey>());

    @EJB
    private ScheduleReportLocal scheduleReportLocal;
    @EJB
    private ReportLocal reportLocal;

    @PostConstruct

    public void start() {
        SchedulerFactory sf = new StdSchedulerFactory();
        try {
            scheduler = sf.getScheduler();
            scheduler.startDelayed(60);
            if (CommonUtil.enableReportScheduler()) {
                // Load Schedule Reports
                Map<ScheduleReportFilter, Object> scheduleReportFilterObjectMap = new HashMap<>();
                scheduleReportFilterObjectMap.put(ScheduleReportFilter.STATUS, ScheduleReportStatus.STATUS_SCHEDULED);
                List<ScheduleReport> scheduleReports = scheduleReportLocal.load(scheduleReportFilterObjectMap);
                createReportSchedule(scheduleReports);
            }
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
        }
    }


    public void createReportSchedule(ScheduleReportId srId) {
        Map<ScheduleReportFilter, Object> scheduleReportFilterObjectMap = new HashMap<>();
        scheduleReportFilterObjectMap.put(ScheduleReportFilter.SCHEDULE_REPORT_ID, srId);
        List<ScheduleReport> scheduleReports = scheduleReportLocal.load(scheduleReportFilterObjectMap);
        createReportSchedule(scheduleReports);
    }

    private void createReportSchedule(List<ScheduleReport> scheduleReports) {
        for (ScheduleReport scheduleReport : scheduleReports) {
            if (scheduleReport.getOnDemand() != null && scheduleReport.getOnDemand() != 1) {
                try {
                    ScheduleReportId scheduleReportId = new ScheduleReportId(scheduleReport.getReportId(), scheduleReport.getLangId(), scheduleReport.getHashcode());
                    removeReportScheduleJob(scheduleReportId);
                    JobDetail job = newJob(ReportJob.class).withIdentity("job" + "_" + scheduleReport.getHashcode() + "_" + scheduleReport.getReportId() + "_" + scheduleReport.getUserId(), "group_1").requestRecovery().build();
                    job.getJobDataMap().put(ReportJob.REPORT_DATA, scheduleReport);
                    job.getJobDataMap().put("reportLocal", reportLocal);
                    Date date = scheduleReport.getScheduleTime();
                    Trigger trigger = newTrigger().withIdentity("trigger_" + "_" + scheduleReport.getHashcode() + "_" + scheduleReport.getReportId() + "_" + scheduleReport.getUserId(), "group_1").startAt(date).build();
                    scheduler.scheduleJob(job, trigger);
                    jobs.put(scheduleReportId, job.getKey());
                } catch (Exception ex) {
                    log.error(ex.getMessage(), ex);
                }
            }
        }
    }


    public void removeReportScheduleJob(ScheduleReportId scheduleReportId) {
        Object jobKeyObject = jobs.get(scheduleReportId);
        if (jobKeyObject != null) {
            try {
                scheduler.deleteJob((JobKey) jobKeyObject);
            } catch (SchedulerException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @PreDestroy

    public void stop() {
        try {
            scheduler.shutdown(true);
            jobs.clear();
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
        }
    }


    public void restart() {
        try {
            scheduler.shutdown(false);
            jobs.clear();
            start();
            log.info("XML process service successfully restarted.");
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    public void clear() {
        try {
            scheduler.clear();
            jobs.clear();
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
        }
    }

    public int getScheduledJobsSize() {
        return jobs.size();
    }

}
