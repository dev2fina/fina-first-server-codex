package net.fina.server.returns.schedule.impl;

import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.filter.ReturnsScheduleFilter;
import net.fina.common.client.filter.ScheduleFilter;
import net.fina.common.shared.SortField;
import net.fina.common.shared.schedule.ScheduledTaskStatus;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.returns.api.ScheduleLocal;
import net.fina.server.returns.entity.ReturnVersion;
import net.fina.server.returns.entity.Schedule;
import net.fina.server.returns.entity.Schedule_;
import net.fina.server.returns.schedule.api.ReturnsScheduleLocal;
import net.fina.server.returns.schedule.entity.ReturnSchedule;
import net.fina.server.returns.schedule.entity.ReturnSchedule_;
import net.fina.server.security.api.UserLocal;
import org.jboss.logging.Logger;

import java.util.*;

@Stateless
@Local(ReturnsScheduleLocal.class)
@Interceptors(RecordingAuditor.class)
public class ReturnsScheduleSession implements ReturnsScheduleLocal {

    private Logger log = Logger.getLogger(getClass());

    @Inject
    private EntityManager em;

    @EJB
    private UserLocal userLocal;

    @EJB
    private ScheduleLocal scheduleLocal;

    @EJB
    private QuartzManager schedulerManager;

    @Override
    public List<ReturnSchedule> load(Map<ReturnsScheduleFilter, Object> taskFilterObjectMap) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ReturnSchedule> criteriaQuery = cb.createQuery(ReturnSchedule.class);
        Root<ReturnSchedule> scheduledTasksRoot = criteriaQuery.from(ReturnSchedule.class);
        List<Predicate> predicates = getFilterPredicate(cb, scheduledTasksRoot, taskFilterObjectMap);
        criteriaQuery.where(predicates.toArray(new Predicate[0]));
        return em.createQuery(criteriaQuery).getResultList();
    }

    private List<Predicate> getFilterPredicate(CriteriaBuilder cb, Root<ReturnSchedule> returnsScheduleRoot, Map<ReturnsScheduleFilter, Object> filter) {
        List<Predicate> predicates = new ArrayList<>();
        if (filter != null) {
            for (Map.Entry<ReturnsScheduleFilter, Object> entry : filter.entrySet()) {
                if (entry.getValue() != null) {
                    switch (entry.getKey()) {
                        case PARENT_ID: {
                            predicates.add(cb.equal(returnsScheduleRoot.get(ReturnSchedule_.parentId), entry.getValue()));
                            break;
                        }
                        case STATUS: {
                            predicates.add(cb.equal(returnsScheduleRoot.get(ReturnSchedule_.status), entry.getValue()));
                            break;
                        }
                        case ONDEMAND: {
                            predicates.add(cb.equal(returnsScheduleRoot.get(ReturnSchedule_.onDemand), entry.getValue()));
                            break;
                        }
                        case TASK_NAME:
                            predicates.add(cb.like(returnsScheduleRoot.get(ReturnSchedule_.taskName), "%" + entry.getValue().toString() + "%"));
                            break;
                    }
                }
            }
        }
        return predicates;
    }

    @Override
    public ReturnSchedule save(ReturnSchedule scheduledTask) throws FinATypeException {
        return save(scheduledTask, 0);
    }

    public ReturnSchedule save(ReturnSchedule returnSchedule, long parentId) throws FinATypeException {
        //Remove existing scheduled task
        delete(returnSchedule.getId());

        //Set user ID
        int currentUserId = Long.valueOf(userLocal.getCurrentUserId()).intValue();
        returnSchedule.setUserId(currentUserId);

        validate(returnSchedule);

        if (returnSchedule.getId() == 0) {
            Collection<Long> scheduleIds = new ArrayList<>();

            try {
                Map<ScheduleFilter, Object> filter = returnSchedule.getFilterMap();

                if (filter != null) {
                    if (filter.get(ScheduleFilter.SCHEDULE_ID) != null && !((Collection<Long>) filter.get(ScheduleFilter.SCHEDULE_ID)).isEmpty()) {
                        scheduleIds.addAll((Collection<? extends Long>) filter.get(ScheduleFilter.SCHEDULE_ID));
                    } else {
                        Collection<Long> fiIds = new ArrayList<>();
                        if (filter.get(ScheduleFilter.FI_IDS) != null) {
                            fiIds = (Collection<Long>) filter.get(ScheduleFilter.FI_IDS);
                        }

                        Long returnDefinitionId = null;
                        if (filter.get(ScheduleFilter.DEFINITION_ID) != null) {
                            returnDefinitionId = (long) filter.get(ScheduleFilter.DEFINITION_ID);
                        }

                        Collection<Long> returnDefinitionIds = new ArrayList<>();
                        if (returnDefinitionId != null) {
                            returnDefinitionIds.add(returnDefinitionId);
                        }

                        Collection<Schedule> schedules = scheduleLocal.load(filter, true, fiIds, returnDefinitionIds);

                        for (Schedule schedule : schedules) {
                            scheduleIds.add(schedule.getId());
                        }
                    }
                }
            } catch (Throwable ex) {
                log.error(ex.getMessage(), ex);
                returnSchedule.setMessage(ex.getMessage());
            }

            em.persist(returnSchedule);
            em.flush();

            if (returnSchedule.getParentId() == 0) {
                for (Long scheduleId : scheduleIds) {
                    ReturnSchedule st = new ReturnSchedule();
                    st.setParentId(returnSchedule.getId());
                    st.setUserId(currentUserId);
                    st.setScheduleId(scheduleId);
                    st.setStatus(ScheduledTaskStatus.STATUS_SCHEDULED);
                    st.setVersionId(returnSchedule.getVersionId());
                    st.setTaskName(returnSchedule.getTaskName());

                    save(st, returnSchedule.getId());
                }
            }

        } else {
            em.merge(returnSchedule);
        }

        if (returnSchedule.getParentId() == 0 && returnSchedule.getOnDemand() != null && returnSchedule.getOnDemand() == 0) {
            schedulerManager.createJob(returnSchedule);
        }

        return returnSchedule;
    }

    @Override
    public void updateStatus(int scheduledTaskId, ScheduledTaskStatus status) {
        ReturnSchedule task = em.find(ReturnSchedule.class, scheduledTaskId);
        if (task != null) {
            task.setStatus(status);
        } else {
            log.error("Task is null for id: " + scheduledTaskId, new NullPointerException());
        }
    }

    @Override
    public void updateMessage(int scheduledTaskId, String message, boolean append) {
        ReturnSchedule task = em.find(ReturnSchedule.class, scheduledTaskId);
        if (task != null) {
            if (message != null) {
                if (append) {
                    String composedMessage = task.getMessage();
                    if (composedMessage == null) {
                        composedMessage = message;
                    } else {
                        composedMessage = String.join("\n", composedMessage, message);
                        composedMessage = composedMessage.substring(0, Math.min(2000, composedMessage.length()));
                    }
                    task.setMessage(composedMessage);
                } else {
                    task.setMessage(message);
                }
            }
        } else {
            log.error("Task is null for id: " + scheduledTaskId, new NullPointerException());
        }
    }

    @Override
    public void delete(int scheduledTaskId) throws FinATypeException {
        ReturnSchedule scheduledTask = em.find(ReturnSchedule.class, scheduledTaskId);
        if (scheduledTask != null) {
            Map<ReturnsScheduleFilter, Object> filter = new HashMap<>();
            filter.put(ReturnsScheduleFilter.PARENT_ID, scheduledTask.getId());
            Collection<ReturnSchedule> children = load(filter);
            for (ReturnSchedule task : children) {
                em.remove(task);
            }

            em.remove(scheduledTask);

            //Remove from scheduler
            schedulerManager.removeJob(scheduledTaskId);
        }
    }

    @Override
    public ReturnSchedule findById(int id) {
        return em.find(ReturnSchedule.class, id);
    }

    @Override
    public List<ReturnSchedule> load(Map<ReturnsScheduleFilter, Object> filterMap, int offset, int limit, SortField sortField) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ReturnSchedule> criteriaQuery = cb.createQuery(ReturnSchedule.class);
        Root<ReturnSchedule> scheduledTasksRoot = criteriaQuery.from(ReturnSchedule.class);

        List<Predicate> predicates = getFilterPredicate(cb, scheduledTasksRoot, filterMap);
        criteriaQuery.where(predicates.toArray(new Predicate[0]));
        criteriaQuery.orderBy(sortField.getDirection().equalsIgnoreCase("asc") ? cb.asc(scheduledTasksRoot.get(sortField.getProperty())) : cb.desc(scheduledTasksRoot.get(sortField.getProperty())));

        return em.createQuery(criteriaQuery)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    @Override
    public long getReturnScheduledTaskCount(Map<ReturnsScheduleFilter, Object> filterMap) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<ReturnSchedule> root = cq.from(ReturnSchedule.class);

        List<Predicate> predicates = getFilterPredicate(cb, root, filterMap);
        cq.where(predicates.toArray(new Predicate[0]));

        cq.select(cb.count(root.get(ReturnSchedule_.id))).distinct(true);

        return em.createQuery(cq).getSingleResult();
    }

    @Override
    public List<Schedule> loadTaskSchedulesByParentId(int id) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Schedule> cq = cb.createQuery(Schedule.class);
        Root<Schedule> root = cq.from(Schedule.class);

        Subquery<Long> subquery = cq.subquery(Long.class);
        Root<ReturnSchedule> returnScheduleRoot = subquery.from(ReturnSchedule.class);
        subquery.select(returnScheduleRoot.get(ReturnSchedule_.scheduleId));
        subquery.where(cb.equal(returnScheduleRoot.get(ReturnSchedule_.parentId), id));

        cq.select(root).distinct(true);
        cq.where(root.get(Schedule_.id).in(subquery));

        return em.createQuery(cq).getResultList();
    }

    @Override
    public long count(HashMap<ReturnsScheduleFilter, Object> filterMap) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
        Root<ReturnSchedule> scheduledTasksRoot = criteriaQuery.from(ReturnSchedule.class);
        criteriaQuery.select(cb.countDistinct(scheduledTasksRoot.get(ReturnSchedule_.id)));

        List<Predicate> predicates = getFilterPredicate(cb, scheduledTasksRoot, filterMap);
        criteriaQuery.where(predicates.toArray(new Predicate[0]));
        TypedQuery<Long> countQuery = em.createQuery(criteriaQuery);

        return countQuery.getSingleResult();
    }

    private void validate(ReturnSchedule returnSchedule) throws FinATypeException {
        if (returnSchedule.getTaskName() == null || returnSchedule.getTaskName().isBlank()) {
            throw new FinATypeException(FinATypeException.Type.INVALID_VALUE, "Task name is required");
        }
        long returnVersionId = returnSchedule.getVersionId();
        if (em.find(ReturnVersion.class, returnVersionId) == null) {
            throw new FinATypeException(FinATypeException.Type.INVALID_VALUE, "Invalid Return Version");
        }
    }

}
