package net.fina.server.dcs.dashboard.impl;

import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TemporalType;
import jakarta.persistence.TypedQuery;
import net.fina.server.dcs.dashboard.api.DcsDashboardLocal;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.returns.entity.Schedule;
import net.fina.server.security.api.UserLocal;
import net.fina.server.util.DBUtil;

import java.util.Date;
import java.util.List;

@Stateless
@Local(DcsDashboardLocal.class)
@Interceptors(RecordingAuditor.class)
public class DcsDashboardSession implements DcsDashboardLocal {

    @Inject
    private EntityManager em;

    @Inject
    private UserLocal userLocal;

    @Override
    public long countUserNotifications() {
        return em.createQuery("select count(n.id) from IN_COMMUNICATOR_NOTIFICATIONS n " +
                        "where n.user.id = :currentUserId", Long.class)
                .setParameter("currentUserId", userLocal.getCurrentUserId())
                .getSingleResult();
    }

    @Override
    public long countUserReceivedNotifications() {
        return em.createQuery("select count(n.id) from IN_COMMUNICATOR_NOTIFICATIONS n left outer join n.notificationUsers u " +
                        "where u.userNotificationId.user.id = :currentUserId", Long.class)
                .setParameter("currentUserId", userLocal.getCurrentUserId())
                .getSingleResult();
    }

    @Override
    public long countUserMessages() {
        return em.createQuery("select count(m.id) from IN_COMMUNICATOR_MESSAGES m where m.user.id = :currentUserId", Long.class)
                .setParameter("currentUserId", userLocal.getCurrentUserId())
                .getSingleResult();
    }

    @Override
    public long countUserReceivedMessages() {
        return em.createQuery("select count(m.id) from IN_COMMUNICATOR_MESSAGES m left outer join m.users u " +
                        "where u.messageUserId.user = :currentUserId", Long.class)
                .setParameter("currentUserId", userLocal.getCurrentUserId())
                .getSingleResult();
    }

    @Override
    public long countFiles(Date fromDate, Date toDate, List<Long> fiIds, boolean submitted) {
        fromDate = fromDate == null ? em.createQuery("select min(p.fromDate) from IN_PERIODS p ", Date.class).getSingleResult() : fromDate;

        List<Long> scheduleIds = em.createQuery("select min(s.id) from IN_SCHEDULES s group by s.returnDefinition.returnType.id", Long.class).getResultList();

        String fiIn = "(" + DBUtil.get().generateConcatenatedInStatement("s.fi.id", fiIds, Long.class) + ")";
        String queryString = "select count(s) from IN_SCHEDULES s where " +
                "s.id " + (!submitted ? "not" : "") + " in (select distinct r.schedule.id from IN_RETURNS r) " +
                "and s.id in (:scheduleIds) and " + fiIn;

        if (fromDate != null) queryString += " and s.period.fromDate >=:fromDate ";
        if (toDate != null) queryString += " and s.period.toDate <=:toDate ";

        TypedQuery<Long> query = em.createQuery(queryString, Long.class)
                .setParameter("scheduleIds", scheduleIds);

        if (fromDate != null) query.setParameter("fromDate", fromDate, TemporalType.DATE);
        if (toDate != null) query.setParameter("toDate", toDate, TemporalType.DATE);

        return query.getSingleResult();
    }

    @Override
    public List<Schedule> loadNotSubmittedFiles(Date fromDate, Date toDate, List<Long> fiIds) {
        fromDate = fromDate == null ? em.createQuery("select min(p.fromDate) from IN_PERIODS p ", Date.class).getSingleResult() : fromDate;

        List<Long> scheduleIds = em.createQuery("select min(s.id) from IN_SCHEDULES s group by s.returnDefinition.returnType.id", Long.class).getResultList();

        String queryString = "select s from IN_SCHEDULES s where " +
                "s.id not in (select distinct r.schedule.id from IN_RETURNS r) and s.id in (:scheduleIds) and s.fi.id in (:fiIds) ";

        if (fromDate != null) queryString += " and s.period.fromDate >=:fromDate ";
        if (toDate != null) queryString += " and s.period.toDate <=:toDate ";

        TypedQuery<Schedule> query = em.createQuery(queryString, Schedule.class)
                .setParameter("fiIds", fiIds)
                .setParameter("scheduleIds", scheduleIds);

        if (fromDate != null) query.setParameter("fromDate", fromDate, TemporalType.DATE);
        if (toDate != null) query.setParameter("toDate", toDate, TemporalType.DATE);

        return query.getResultList();
    }

}
