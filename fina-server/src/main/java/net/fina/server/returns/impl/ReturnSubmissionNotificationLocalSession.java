package net.fina.server.returns.impl;

import net.fina.server.returns.entity.Schedule;
import net.fina.server.returns.api.ReturnSubmissionNotificationLocal;
import net.fina.server.returns.api.ReturnSubmissionNotificationType;
import net.fina.server.returns.entity.ReturnSubmissionNotification;

import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
@Local(ReturnSubmissionNotificationLocal.class)
public class ReturnSubmissionNotificationLocalSession implements ReturnSubmissionNotificationLocal {
    @Inject
    private EntityManager em;

    @Override
    public void save(ReturnSubmissionNotification returnNotification) {
        em.persist(returnNotification);
    }

    @Override
    public List<Schedule> getOverdueReturnSchedules(Date currentDate, int daysBeforeDueDate) {
        List<Schedule> schedules = schedulesWithoutNotificationAfterToDate(currentDate, ReturnSubmissionNotificationType.OVERDUE);
        return schedules.stream()
                .filter(schedule -> filterByDate(schedule, currentDate, daysBeforeDueDate))
                .collect(Collectors.toList());
    }

    @Override
    public List<Schedule> getNotSubmittedReturnSchedules() {
        Date currentDate = new Date();
        List<Schedule> schedules = schedulesWithoutNotificationAfterToDate(currentDate, ReturnSubmissionNotificationType.NOT_SUBMITTED);
        return schedules.stream()
                .filter(schedule -> dateIsAfterDue(schedule, currentDate))
                .collect(Collectors.toList());
    }

    @Override
    public List<Schedule> getActiveSubmissionSchedules(Date currentDate) {
        List<Schedule> schedules = schedulesWithoutNotificationAfterToDate(currentDate, ReturnSubmissionNotificationType.SUBMISSION_PERIOD);
        return schedules.stream()
                .filter(schedule -> filterByDate(schedule, currentDate, 0))
                .collect(Collectors.toList());
    }

    private boolean filterByDate(Schedule schedule, Date date, int daysBeforeDueDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(schedule.getPeriod().getToDate());
        if (daysBeforeDueDate <= 0) {
            c.add(Calendar.DATE, 1);
            return date.before(c.getTime());
        }
        c.add(Calendar.DATE, schedule.getDelay());
        c.add(Calendar.HOUR, schedule.getDelayHour());
        Date dueDate = c.getTime();
        if (daysBeforeDueDate > 0) {
            c.add(Calendar.DATE, -daysBeforeDueDate);
            return date.after(c.getTime()) && date.before(dueDate);
        }
        return date.before(c.getTime());
    }

    private List<Schedule> schedulesWithoutNotificationAfterToDate(Date currentDate, ReturnSubmissionNotificationType notificationType) {
        // period older than current date, does not have a return and there is no notification sent yet:
        String queryString =
                "select sch from IN_SCHEDULES sch " +
                        "where " +
                        "sch.period.toDate <= :currentDate and " +
                        "(select count(return) from IN_RETURNS return where return.schedule = sch) = 0 and " +
                        "sch not in (select submissionnotification.schedule from IN_OVERDUE_RETURN_NOTIFICATIONS submissionnotification where submissionnotification.notificationType = :notificationType)";
        return em.createQuery(queryString, Schedule.class)
                .setParameter("currentDate", currentDate)
                .setParameter("notificationType", notificationType)
                .getResultList();
    }

    private boolean dateIsAfterDue(Schedule schedule, Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(schedule.getPeriod().getToDate());
        c.add(Calendar.DATE, schedule.getDelay());
        Date dueDate = c.getTime();
        c.setTime(date);
        c.add(Calendar.DATE, -7);
        Date dateMinusOneWeek = c.getTime();
        return date.after(dueDate) && dueDate.after(dateMinusOneWeek);
    }
}
