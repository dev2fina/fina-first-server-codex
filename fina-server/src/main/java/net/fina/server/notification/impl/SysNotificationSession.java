package net.fina.server.notification.impl;

import net.fina.common.shared.notification.NotificationFilter;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.notification.api.SysNotificationLocal;
import net.fina.server.notification.entity.SystemNotification;
import net.fina.server.notification.entity.SystemNotification_;

import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
@Local(SysNotificationLocal.class)
@Interceptors(RecordingAuditor.class)
public class SysNotificationSession implements SysNotificationLocal {

    @Inject
    private EntityManager em;

    @Override
    public SystemNotification save(SystemNotification systemNotification) {

        if (systemNotification.getId() > 0) {
            systemNotification = em.merge(systemNotification);
        } else {
            em.persist(systemNotification);
        }

        return systemNotification;
    }

    @Override
    public List<SystemNotification> findByUserId(long userId, int offset, int limit, NotificationFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<SystemNotification> query = cb.createQuery(SystemNotification.class);
        Root<SystemNotification> root = query.from(SystemNotification.class);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get(SystemNotification_.notify), userId));

        if (filter != null) {
            switch (filter) {
                case READ:
                    predicates.add(cb.isNotNull(root.get(SystemNotification_.DATETIME_READ)));
                    break;
                case UNREAD:
                    predicates.add(cb.isNull(root.get(SystemNotification_.DATETIME_READ)));
                    break;
            }
        }

        query.where(predicates.toArray(new Predicate[0]));
        query.orderBy(cb.desc(root.get(SystemNotification_.datetimeAdded)));

        TypedQuery<SystemNotification> loadQuery = em.createQuery(query);

        if (limit > 0) {
            loadQuery.setMaxResults(limit);
        }

        if (offset > 0) {
            loadQuery.setFirstResult(offset);
        }

        return loadQuery.getResultList();
    }

    @Override
    public long countUnreadNotifications(long userId) {
        return em.createQuery("select count(n) from SYS_NOTIFICATIONS n " +
                        "where n.notify=:userId and n.datetimeRead is null ", Long.class)
                .setParameter("userId", userId)
                .getSingleResult();
    }

    @Override
    public long countNotifications(long userId, NotificationFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<SystemNotification> root = query.from(SystemNotification.class);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get(SystemNotification_.notify), userId));

        if (filter != null) {
            switch (filter) {
                case READ:
                    predicates.add(cb.isNotNull(root.get(SystemNotification_.DATETIME_READ)));
                    break;
                case UNREAD:
                    predicates.add(cb.isNull(root.get(SystemNotification_.DATETIME_READ)));
                    break;
            }
        }

        query.where(predicates.toArray(new Predicate[0]));
        query.select(cb.countDistinct(root.get(SystemNotification_.ID)));

        TypedQuery<Long> loadQuery = em.createQuery(query);

        return loadQuery.getSingleResult();
    }

    @Override
    public void markNotificationAsRead(long notificationId, long userId) {
        em.createQuery("update SYS_NOTIFICATIONS n set n.datetimeRead = :readDate where n.id = :id and n.notify = :userId ")
                .setParameter("id", notificationId)
                .setParameter("userId", userId)
                .setParameter("readDate", new Date())
                .executeUpdate();
    }

    @Override
    public void markAllNotificationsAsRead(long userId) {
        em.createQuery("update SYS_NOTIFICATIONS n set n.datetimeRead = :readDate where n.notify = :userId ")
                .setParameter("userId", userId)
                .setParameter("readDate", new Date())
                .executeUpdate();
    }
}
