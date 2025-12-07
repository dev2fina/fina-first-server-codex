package net.fina.server.reports.impl;

import net.fina.common.client.reports.ScheduleReportStatus;
import net.fina.common.client.reports.ScheduledReportInfo;

import net.fina.report.model.ReportModel;
import net.fina.server.reports.api.ReportLocal;
import net.fina.server.reports.api.ScheduleReportLocal;
import net.fina.server.reports.entity.Report;
import net.fina.server.reports.entity.ScheduleReport;
import org.jboss.logging.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class ReportJob implements Job {

    public static final String REPORT_DATA = "report.data";

    private Logger log = Logger.getLogger(getClass());

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("Execute Report Scheduler");
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        ScheduleReport scheduleReport = (ScheduleReport) jobDataMap.get(REPORT_DATA);
        ReportLocal reportLocal = (ReportLocal) jobDataMap.get("reportLocal");
        reportLocal.generate(scheduleReport);
    }
}