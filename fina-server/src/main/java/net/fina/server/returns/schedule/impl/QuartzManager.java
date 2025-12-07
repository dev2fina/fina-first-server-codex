package net.fina.server.returns.schedule.impl;

import net.fina.common.client.filter.ReturnsScheduleFilter;
import net.fina.common.shared.schedule.JobDataConstants;
import net.fina.common.shared.schedule.ScheduledTaskStatus;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.returns.schedule.api.QuartzJobFactoryLocal;
import net.fina.server.returns.schedule.api.ReturnsScheduleLocal;
import net.fina.server.returns.schedule.entity.ReturnSchedule;
import org.jboss.logging.Logger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.interceptor.Interceptors;
import java.io.Serializable;
import java.util.*;

@Singleton
@Startup
@Interceptors(RecordingAuditor.class)
public class QuartzManager implements Serializable {

    private final Logger log = Logger.getLogger(getClass());
    private static final String QUARTZ_GROUP = "group_2";
    private static final Map<Integer, JobKey> JOBS = Collections.synchronizedMap(new HashMap<Integer, JobKey>());

    private Scheduler scheduler;

    @EJB
    private ReturnsScheduleLocal schedulerLocal;

    @EJB
    private QuartzJobFactoryLocal jobFactoryLocal;

    @PostConstruct
    public void start() {
        try {
            Properties props = new Properties();
            props.put(StdSchedulerFactory.PROP_SCHED_INSTANCE_NAME, "FinA_Automation_Scheduler");
            props.put("org.quartz.threadPool.threadCount", "1");

            StdSchedulerFactory sf = new StdSchedulerFactory();
            sf.initialize(props);

            scheduler = sf.getScheduler();
            scheduler.startDelayed(60);

            // Load Scheduled Tasks
            Map<ReturnsScheduleFilter, Object> taskFilterObjectMap = new HashMap<>();
            taskFilterObjectMap.put(ReturnsScheduleFilter.PARENT_ID, 0);
            taskFilterObjectMap.put(ReturnsScheduleFilter.STATUS, ScheduledTaskStatus.STATUS_SCHEDULED);
            taskFilterObjectMap.put(ReturnsScheduleFilter.ONDEMAND, 0);
            List<ReturnSchedule> tasks = schedulerLocal.load(taskFilterObjectMap);
            createJobs(tasks);
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
    }

    private void createJobs(List<ReturnSchedule> tasks) {
        for (ReturnSchedule task : tasks) {
            try {
                removeJob(task.getId());
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            } finally {
                createJob(task);
            }
        }
    }

    public void createJob(ReturnSchedule task) {
        try {
            JobDetail job = jobFactoryLocal.getJob("job_" + task.toString(), QUARTZ_GROUP);
            job.getJobDataMap().put(JobDataConstants.JOB_DATA, task);

            Date date = task.getScheduleTime();
            if (date.before(new Date())) {
                java.util.Calendar cal = new GregorianCalendar();
                cal.add(java.util.Calendar.MINUTE, 2);
                date = cal.getTime();
            }

            TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
            triggerBuilder.withIdentity("trigger_" + task.toString(), QUARTZ_GROUP);
            triggerBuilder.startAt(date);

            Trigger trigger = triggerBuilder.build();
            scheduler.scheduleJob(job, trigger);

            JOBS.put(task.getId(), job.getKey());
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
    }

    public void removeJob(int taskId) {
        try {
            JobKey jobKey = JOBS.get(taskId);
            scheduler.deleteJob(jobKey);
            JOBS.remove(taskId);
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
    }

    @PreDestroy
    public void stop() {
        try {
            scheduler.shutdown(true);
            JOBS.clear();
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
    }

    public void restart() {
        try {
            scheduler.shutdown(false);
            JOBS.clear();
            start();
            log.info("Scheduler service successfully restarted.");
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    public void triggerJobWithTaskId(int id) {
        try {
            JobKey jobKey = JOBS.get(id);
            scheduler.triggerJob(jobKey);
            log.info("Job with taskId (" + id + ") triggered.");
            removeJob(id);
        } catch (SchedulerException ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    public boolean jobScheduled(int taskId) {
        return JOBS.get(taskId) != null;
    }

    public int getScheduledReturnsSize() {
        return JOBS.size();
    }

    public void clearSchedulerReturns() {
        try {
            scheduler.clear();
            JOBS.clear();
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
        }
    }

}
