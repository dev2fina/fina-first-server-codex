package net.fina.server.returns;

import net.fina.common.client.exception.FinATypeException;
import net.fina.common.server.util.ConfigurationUtil;
import net.fina.server.returns.impl.SubmissionNotificationsSession;
import org.jboss.logging.Logger;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.ejb.*;

@Startup
@Singleton
public class SubmissionNotificationsAutoStartService {
    private Logger log = Logger.getLogger(getClass());

    @Resource
    private TimerService timerService;

    @EJB
    private SubmissionNotificationsSession submissionNotificationsSession;

    private Timer timer;
    
    @PostConstruct
    private void start() {
        ConfigurationUtil configurationUtil = ConfigurationUtil.get();
        String serviceEnable = configurationUtil.get("SUBMISSION_NOTIFICATION.enable");
        log.info("SUBMISSION_NOTIFICATION.enable: " + serviceEnable);
        if (serviceEnable == null || 
            serviceEnable.isEmpty() || 
            Integer.parseInt(serviceEnable) <= 0
            ) {
            return;
        }

        ScheduleExpression schedule = new ScheduleExpression();
        String second = configurationUtil.get("SUBMISSION_NOTIFICATION.second"), 
            minute = configurationUtil.get("SUBMISSION_NOTIFICATION.minute"), 
            hour = configurationUtil.get("SUBMISSION_NOTIFICATION.hour"), 
            dayOfWeek = configurationUtil.get("SUBMISSION_NOTIFICATION.dayOfWeek"), 
            dayOfMonth = configurationUtil.get("SUBMISSION_NOTIFICATION.dayOfMonth"), 
            month = configurationUtil.get("SUBMISSION_NOTIFICATION.month"), 
            year = configurationUtil.get("SUBMISSION_NOTIFICATION.year");
        if (second != null && !second.isEmpty() ) {
            schedule.second(second);
        }
        if (minute != null && !minute.isEmpty() ) {
            schedule.minute(minute);
        }
        if (hour != null && !hour.isEmpty() ) {
            schedule.hour(hour);
        }
        if (dayOfWeek != null && !dayOfWeek.isEmpty() ) {
            schedule.dayOfWeek(dayOfWeek);
        }
        if (dayOfMonth != null && !dayOfMonth.isEmpty() ) {
            schedule.dayOfMonth(dayOfMonth);
        }
        if (month != null && !month.isEmpty() ) {
            schedule.month(month);
        }
        if (year != null && !year.isEmpty() ) {
            schedule.year(year);
        }
        timer = timerService.createCalendarTimer(schedule, new TimerConfig(null, false));
    }

    @Timeout
    public void automaticTimeout() throws FinATypeException {
        long startTime = System.currentTimeMillis();
        ConfigurationUtil configurationUtil = ConfigurationUtil.get();
        int daysBeforeDueDate = 5;
        try {
            daysBeforeDueDate = Integer.parseInt(configurationUtil.get("SUBMISSION_NOTIFICATION.daysBeforeDueDate"));
        } catch (Exception e) {
            log.error("SUBMISSION_NOTIFICATION.daysBeforeDueDate is invalid! Default value (5) will be used.");
        }

        submissionNotificationsSession.sendNotSubmittedReturnNotifications();
        submissionNotificationsSession.sendOverdueReturnNotifications(daysBeforeDueDate);
        submissionNotificationsSession.sendSubmissionPeriodStartedNotifications();

        log.info("Return Submission Notification Service");
        log.info("Total Time: " + (System.currentTimeMillis() - startTime) + "ms");
        log.info("Next Fire Time - " + timer.getNextTimeout());
    }
}
