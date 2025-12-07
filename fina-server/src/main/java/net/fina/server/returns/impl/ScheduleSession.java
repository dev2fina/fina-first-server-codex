package net.fina.server.returns.impl;

import jakarta.annotation.Resource;
import jakarta.ejb.*;
import jakarta.enterprise.concurrent.ManagedExecutorService;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TemporalType;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import net.fina.common.client.constants.CalendarPeriodType;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.filter.ScheduleFilter;
import net.fina.common.client.returns.OverdueReturnModel;
import net.fina.common.server.util.CommonUtil;
import net.fina.server.calendar.api.CalendarLocal;
import net.fina.server.dcs.uploadfile.entity.SortInfo;
import net.fina.server.fi.entity.Fi;
import net.fina.server.fi.entity.Fi_;
import net.fina.server.i18n.impl.DescriptionManager;
import net.fina.server.interceptors.LogDescription;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.returns.api.ReturnDefinitionLocal;
import net.fina.server.returns.api.ScheduleBatchLocal;
import net.fina.server.returns.api.ScheduleLocal;
import net.fina.server.returns.entity.*;
import net.fina.server.returns.entity.Schedule;
import net.fina.server.returns.event.ScheduleCreateEvent;
import net.fina.server.returns.model.helper.ScheduleModelHelper;
import net.fina.server.security.api.UserLocal;
import net.fina.server.util.DBUtil;
import net.fina.common.server.StatisticsLogger;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.logging.Logger;
import org.joda.time.DateTime;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Stateless
@Local(ScheduleLocal.class)
@Interceptors(RecordingAuditor.class)
public class ScheduleSession implements ScheduleLocal {

    private final Logger log = Logger.getLogger(getClass());

    @Inject
    private EntityManager em;

    @EJB
    private ReturnDefinitionLocal returnDefinitionLocal;

    @EJB
    private UserLocal userLocal;

    @EJB
    private CalendarLocal calendarLocal;
    @Inject
    private Event<ScheduleCreateEvent> scheduleCreateEventEvent;

    @Resource
    private ManagedExecutorService managedExecutorService;
    @Inject
    private ScheduleBatchLocal scheduleBatchLocal;

    @Override
    public Schedule save(ScheduleParameter schedule) throws FinATypeException {
        if (checkScheduleNotUnique(schedule)) {
            throw new FinATypeException(FinATypeException.Type.SCHEDULE_UNIQUE_ERROR);
        }

        if (schedule.getDelay() == null || schedule.getDelay() < 0 || schedule.getDelayHour() == null || schedule.getDelayHour() < 0 || schedule.getDelayHour() > 23 || schedule.getDelayMinute() == null || schedule.getDelayMinute() < 0 || schedule.getDelayMinute() > 59 || (schedule.getDelay() < 1 && (schedule.getDelayHour() == null || schedule.getDelayHour() < 1) && (schedule.getDelayMinute() == null || schedule.getDelayMinute() < 1))) {
            throw new FinATypeException(FinATypeException.Type.INVALID_DUE_DATE);
        }

        Schedule entity = getScheduleFromParameter(schedule);

        validateSchedule(entity);

        if (schedule.getId() == 0) {
            em.persist(entity);
        } else {
            Schedule existing = em.find(Schedule.class, schedule.getId());
            entity.setVersion(existing.getVersion());
            entity = em.merge(entity);
        }
        return entity;
    }

    @Override
    @LogDescription(ignore = true)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Schedule save(ScheduleParameter schedule, Map<Long, Fi> fiMap, Map<Long, Period> periodMap, Map<Long, ReturnDefinition> returnDefinitionMap) throws FinATypeException {
        if (schedule.getDelay() == null || schedule.getDelay() < 0 || schedule.getDelayHour() == null || schedule.getDelayHour() < 0 || schedule.getDelayHour() > 23 || schedule.getDelayMinute() == null || schedule.getDelayMinute() < 0 || schedule.getDelayMinute() > 59 || (schedule.getDelay() < 1 && (schedule.getDelayHour() == null || schedule.getDelayHour() < 1) && (schedule.getDelayMinute() == null || schedule.getDelayMinute() < 1))) {
            throw new FinATypeException(FinATypeException.Type.INVALID_DUE_DATE);
        }

        Schedule entity = getScheduleFromParameter(schedule, fiMap.get(schedule.getFiId()), periodMap.get(schedule.getPeriodId()), returnDefinitionMap.get(schedule.getReturnDefinitionId()));


        if (schedule.getId() == 0) {
            em.persist(entity);
        } else {
            Schedule existing = em.find(Schedule.class, schedule.getId());
            entity.setVersion(existing.getVersion());
            entity = em.merge(entity);
        }
        return entity;
    }

    @Override
    @LogDescription(ignore = true)
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void batchSave(List<ScheduleParameter> parameters, Map<Long, Fi> fiMap, Map<Long, Period> periodMap, Map<Long, ReturnDefinition> returnDefinitionHashMap, List<Schedule> notSaved, String currentUser, long langId) {
        int threadCount = 8;

        try (StatisticsLogger statLog = new StatisticsLogger("Schedule Generation");) {

            statLog.logStage("Start [" + parameters.size() + "] Schedule Generation : ");
            AtomicInteger progress = new AtomicInteger();
            // Split the list into sub lists
            List<List<ScheduleParameter>> partitions = splitList(parameters, threadCount);

            List<Future<?>> futures = new ArrayList<>();

            Set<String> existingKeys = new HashSet<>();
            statLog.logStage("load existing schedules");
            List<Object[]> res = em.createNativeQuery("SELECT BANKID, DEFINITIONID, PERIODID FROM IN_SCHEDULES").getResultList();


            res.forEach(r -> {
                String key = r[0] + "-" + r[1] + "-" + r[2];
                existingKeys.add(key);
            });
            res.clear();

            statLog.logStage("Create new schedules");
            try (ScheduledExecutorService websocketNotifier = Executors.newSingleThreadScheduledExecutor()) {

                for (List<ScheduleParameter> sublist : partitions) {
                    futures.add(managedExecutorService.submit(() -> {
                        scheduleBatchLocal.saveSchedules(sublist, parameters, progress, fiMap, periodMap, returnDefinitionHashMap, notSaved, currentUser, existingKeys);
                    }));
                }

                websocketNotifier.scheduleAtFixedRate(() -> {
                    scheduleCreateEventEvent.fire(new ScheduleCreateEvent(parameters.size(), progress.get(), currentUser, null)); // Non-blocking, batched, or throttled
                }, 0, 1000, TimeUnit.MILLISECONDS);

                // Wait for all tasks to complete
                for (Future<?> future : futures) {
                    try {
                        future.get();
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }

            existingKeys.clear();

            List<Schedule> notSavedSchedulesTruncated = notSaved.size() > 500 ? notSaved.subList(0, 500) : notSaved;
            scheduleCreateEventEvent.fire(new ScheduleCreateEvent(parameters.size(), parameters.size(), currentUser, ScheduleModelHelper.toModelList(notSavedSchedulesTruncated, langId)));
            notSaved.clear();
        }
    }

    @Override
    @TransactionTimeout(unit = TimeUnit.DAYS, value = 1)
    @Transactional
    public void saveSchedules(List<ScheduleParameter> parameters, Map<Long, Fi> fiMap, Map<Long, Period> periodMap, Map<Long, ReturnDefinition> returnDefinitionHashMap, List<Schedule> notSaved, String currentUser, long langId) {
        AtomicInteger progress = new AtomicInteger(0);


        try (StatisticsLogger statLog = new StatisticsLogger("Schedule Generation");
             ScheduledExecutorService websocketNotifier = Executors.newSingleThreadScheduledExecutor()) {
             statLog.logStage("Start [" + parameters.size() + "] Schedule Generation : ");

            websocketNotifier.scheduleAtFixedRate(() -> {
                scheduleCreateEventEvent.fire(new ScheduleCreateEvent(parameters.size(), progress.get(), currentUser, null)); // Non-blocking, batched, or throttled
            }, 0, 1000, TimeUnit.MILLISECONDS);

            for (ScheduleParameter schedule : parameters) {

                try {
                    progress.incrementAndGet();

                    if (checkScheduleNotUnique(schedule)) {
                        throw new FinATypeException(FinATypeException.Type.SCHEDULE_UNIQUE_ERROR);
                    }

                    save(schedule, fiMap, periodMap, returnDefinitionHashMap);

                } catch (Throwable ex) {
                    Schedule notSavedSchedule = getScheduleFromParameter(schedule, fiMap.get(schedule.getFiId()), periodMap.get(schedule.getPeriodId()), returnDefinitionHashMap.get(schedule.getReturnDefinitionId()));
                    notSaved.add(notSavedSchedule);
                    log.error("Schedule Already Exists : " + notSavedSchedule);
                }
            }
        } finally {
            List<Schedule> notSavedSchedulesTruncated = notSaved.size() > 500 ? notSaved.subList(0, 500) : notSaved;
            scheduleCreateEventEvent.fire(new ScheduleCreateEvent(parameters.size(), parameters.size(), currentUser, ScheduleModelHelper.toModelList(notSavedSchedulesTruncated, langId)));
        }

    }

    private List<List<ScheduleParameter>> splitList(List<ScheduleParameter> list, int parts) {
        List<List<ScheduleParameter>> partitions = new ArrayList<>();
        if (list.size() <= parts) {
            partitions.add(list);

        } else {

            int chunkSize = (int) Math.ceil((double) list.size() / parts);
            for (int i = 0; i < list.size(); i += chunkSize) {
                partitions.add(list.subList(i, Math.min(i + chunkSize, list.size())));
            }
        }
        return partitions;
    }

    private boolean checkScheduleNotUnique(ScheduleParameter schedule) {
        TypedQuery<Long> query = em.createQuery("select 1 from IN_SCHEDULES s where s.fi.id=:bankId and s.returnDefinition.id=:definitionId and s.period.id=:periodId and s.id<>:id", Long.class);
        query.setParameter("bankId", schedule.getFiId());
        query.setParameter("definitionId", schedule.getReturnDefinitionId());
        query.setParameter("periodId", schedule.getPeriodId());
        query.setParameter("id", schedule.getId());
        query.setMaxResults(1);
        return !query.getResultList().isEmpty();
    }

    @Override
    public void delete(Long id) throws FinATypeException {
        if (id == null) {
            return;
        }
        if (!checkDependantSchedules(Collections.singletonList(id)).isEmpty()) {
            throw new FinATypeException(FinATypeException.Type.DEPENDENCY_ERROR);
        }
        em.createQuery("delete from IN_SCHEDULES  where id=:id").setParameter("id", id).executeUpdate();
    }


    @Override

    public Schedule findScheduleByPeriod(Date from, Date to, String fiCode, String returnDefinitionCode) {
        Query getScheduleQuery = em.createQuery("select s from IN_SCHEDULES as s where s.period.fromDate=:from and s.period.toDate=:to and trim(s.fi.code)=:fiCode and trim(s.returnDefinition.code)=:definitionCode ", Schedule.class);
        getScheduleQuery.setParameter("from", from, TemporalType.DATE);
        getScheduleQuery.setParameter("to", to, TemporalType.DATE);
        getScheduleQuery.setParameter("fiCode", fiCode.trim());
        getScheduleQuery.setParameter("definitionCode", returnDefinitionCode.trim());
        return (Schedule) getScheduleQuery.getSingleResult();
    }

    @Override
    public long findScheduleIdByPeriod(Date from, Date to, String fiCode, String returnDefinitionCode) {
        List<Long> result = em.createQuery("select s.id from IN_SCHEDULES as s where s.period.fromDate=:from and s.period.toDate=:to and trim(s.fi.code)=:fiCode and trim(s.returnDefinition.code)=:definitionCode ", Long.class).setParameter("from", from, TemporalType.DATE).setParameter("to", to, TemporalType.DATE).setParameter("fiCode", fiCode).setParameter("definitionCode", returnDefinitionCode).getResultList();
        if (result.size() > 0) {
            return result.stream().findFirst().get();
        }
        return -1;
    }

    @Override

    public Schedule loadSimpleSchedule(long id) {
        StringBuilder qlString = new StringBuilder();
        qlString.append("SELECT ");
        qlString.append(" NEW ");
        qlString.append(Schedule.class.getName());
        qlString.append("(");
        qlString.append("s.id, ");
        qlString.append("s.version, ");
        qlString.append("s.returnDefinition ");
        qlString.append(")");
        qlString.append(" from IN_SCHEDULES as s WHERE s.id=:id ");
        Query query = em.createQuery(qlString.toString());
        query.setParameter("id", id);
        return (Schedule) query.getSingleResult();
    }

    @Override

    public List<DefinitionTable> loadScheduleReturnDefinitionTables(long scheduleId) {
        StringBuilder qlString = new StringBuilder();
        qlString.append("SELECT ");
        qlString.append("s.returnDefinition.id ");
        qlString.append(" from IN_SCHEDULES as s WHERE s.id=:id ");
        Query query = em.createQuery(qlString.toString());
        query.setParameter("id", scheduleId);
        long returnDefinitionId = (long) query.getSingleResult();
        return returnDefinitionLocal.loadDefinitontables(returnDefinitionId);
    }

    @Override
    public List<Schedule> load(Map<ScheduleFilter, Object> filter, boolean loadAll, Collection<Long> fiIds, Collection<Long> rDefIds) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Schedule> query = cb.createQuery(Schedule.class);
        Root<Schedule> root = query.from(Schedule.class);

        Join<Schedule, Fi> scheduleFiJoin = root.join(Schedule_.fi);
        Join<Schedule, ReturnDefinition> scheduleReturnDefinitionJoin = root.join(Schedule_.returnDefinition);

        query.orderBy(cb.desc(root.get(Schedule_.period).get(Period_.fromDate)));

        List<Predicate> predicates = getFilterPredicate(cb, root, filter);

        if (filter.get(ScheduleFilter.LOAD_ALL_PERIOD_DATA) != null && !(Boolean) filter.get(ScheduleFilter.LOAD_ALL_PERIOD_DATA)) {
            DateTime dateTime = DateTime.now().minusYears(1).withDayOfMonth(1).withMonthOfYear(1);
            predicates.add(cb.greaterThanOrEqualTo(root.get(Schedule_.PERIOD).get(Period_.FROM_DATE), dateTime.toDate()));
        }

        Collection<Long> fis = (fiIds == null || fiIds.isEmpty() ? userLocal.getCallerPrincipal().getFis() : fiIds);
        List<Predicate> fiPredicates = DBUtil.get().buildAndSplitPredicates(cb, scheduleFiJoin.get(Fi_.id), Long.class, fis);

        Selection[] selections = new Selection[]{root.get(Schedule_.ID), scheduleReturnDefinitionJoin.get(ReturnDefinition_.ID), scheduleReturnDefinitionJoin.get(ReturnDefinition_.CODE), scheduleReturnDefinitionJoin.get(ReturnDefinition_.DESCRIPTION), root.get(Schedule_.PERIOD).get(Period_.ID), root.get(Schedule_.PERIOD).get(Period_.FROM_DATE), root.get(Schedule_.PERIOD).get(Period_.TO_DATE), root.get(Schedule_.PERIOD).get(Period_.PERIOD_TYPE).get(PeriodType_.ID), root.get(Schedule_.PERIOD).get(Period_.PERIOD_TYPE).get(PeriodType_.CODE), root.get(Schedule_.PERIOD).get(Period_.PERIOD_TYPE).get(PeriodType_.DESCRIPTION), scheduleFiJoin.get(Fi_.ID), scheduleFiJoin.get(Fi_.CODE), scheduleFiJoin.get(Fi_.DESCRIPTION), root.get(Schedule_.delay), root.get(Schedule_.delayHour), root.get(Schedule_.delayMinute), root.get(Schedule_.comment), root.get(Schedule_.RETURN_DEFINITION).get(ReturnDefinition_.RETURN_TYPE).get(ReturnType_.ID), root.get(Schedule_.RETURN_DEFINITION).get(ReturnDefinition_.RETURN_TYPE).get(ReturnType_.CODE), root.get(Schedule_.VERSION), root.get(Schedule_.PERIOD).get(Period_.VERSION), root.get(Schedule_.PERIOD).get(Period_.PERIOD_TYPE).get(PeriodType_.VERSION), root.get(Schedule_.RETURN_DEFINITION).get(ReturnDefinition_.VERSION), root.get(Schedule_.RETURN_DEFINITION).get(ReturnDefinition_.RETURN_TYPE).get(ReturnType_.VERSION), root.get(Schedule_.delayToDate),

        };

        query.multiselect(selections);

        Collection<Long> rDefs = (rDefIds == null || rDefIds.isEmpty() ? userLocal.getCallerPrincipal().getReturnDefinitions() : rDefIds);
        predicates.add(scheduleReturnDefinitionJoin.get(ReturnDefinition_.id).in(rDefs));
        predicates.add(cb.and(cb.or(fiPredicates.toArray(new Predicate[0]))));

        query.where(predicates.toArray(new Predicate[0]));
        TypedQuery<Schedule> loadQuery = em.createQuery(query);

        if (loadAll) {
            return loadQuery.getResultList();
        }

        if (filter != null) {
            if (filter.get(ScheduleFilter.OFFSET) instanceof Integer) {
                loadQuery.setFirstResult((int) filter.get(ScheduleFilter.OFFSET));
            }

            if (filter.get(ScheduleFilter.LIMIT) instanceof Integer) {
                loadQuery.setMaxResults((Integer) filter.get(ScheduleFilter.LIMIT));
            }
        }

        return loadQuery.getResultList();
    }

    @Override
    public List<Schedule> loadPaginated(Map<ScheduleFilter, Object> filter, Collection<Long> fiIds, Collection<Long> rDefIds) {
        StringBuilder query = new StringBuilder("select " + "        sch.ID, " + "         sch.DEFINITIONID, " + "         r.CODE, " + "         r.NAMESTRID, " + "         sch.PERIODID, " + "         p.FROMDATE, " + "         p.TODATE, " + "         p.PERIODTYPEID, " + "         pt.CODE, " + "         pt.NAMESTRID, " + "         sch.BANKID, " + "         fi.CODE, " + "         fi.NAMESTRID, " + "         sch.DELAY, " + "         sch.DELAY_HOUR, " + "         sch.DELAY_MINUTE, " + "         sch.\"COMMENT\", " + "         r.TYPEID, " + "         rt.CODE," + "         sch.OPTLOCK," + "         p.OPTLOCK," + "         pt.OPTLOCK," + "         r.OPTLOCK," + "         rt.OPTLOCK,  " + "         sch.DELAY_TO_DATE" +

                "     from " + "         IN_SCHEDULES sch  " + "     join " + "         IN_BANKS fi  " + "             on fi.ID=sch.BANKID  " + "     join " + "         IN_RETURN_DEFINITIONS r  " + "             on r.ID=sch.DEFINITIONID  " + "     join " + "         IN_RETURN_TYPES rt  " + "             on rt.ID=r.TYPEID  " + "     join " + "         IN_PERIODS p  " + "             on p.ID=sch.PERIODID  " + "     join " + "         IN_PERIOD_TYPES pt  " + "             on pt.ID=p.PERIODTYPEID  where ");

        Collection<Long> rDefs = (rDefIds == null || rDefIds.isEmpty() ? userLocal.getCallerPrincipal().getReturnDefinitions() : rDefIds);
        Collection<Long> fis = (fiIds == null || fiIds.isEmpty() ? userLocal.getCallerPrincipal().getFis() : fiIds);

        query.append(" (").append(DBUtil.get().generateConcatenatedInStatement("sch.DEFINITIONID", rDefs.stream().toList(), Long.class)).append(")");
        query.append(" and (").append(DBUtil.get().generateConcatenatedInStatement("sch.BANKID", fis.stream().toList(), Long.class)).append(")");

        constructQueryParams(query, filter);
//        query.append(" order by p.TODATE desc ");

        Query scheduleQuery = em.createNativeQuery(query.toString());

        setQueryParameters(scheduleQuery, filter);

        if (filter != null) {
            if (filter.get(ScheduleFilter.OFFSET) instanceof Integer) {
                scheduleQuery.setFirstResult((int) filter.get(ScheduleFilter.OFFSET));
            }

            if (filter.get(ScheduleFilter.LIMIT) instanceof Integer) {
                scheduleQuery.setMaxResults((Integer) filter.get(ScheduleFilter.LIMIT));
            }
        }
        List<Schedule> res = new ArrayList<>();
        List<Object[]> queryResult = scheduleQuery.getResultList();
        for (Object[] o : queryResult) {

            long id = ((Number) o[0]).longValue();
            long definitionId = ((Number) o[1]).longValue();
            String definitionCode = (String) o[2];
            long namestrId = ((Number) o[3]).longValue();
            long periodId = ((Number) o[4]).longValue();
            Date fromDate = ((Date) o[5]);
            Date toDate = ((Date) o[6]);
            long periodTypeId = ((Number) o[7]).longValue();
            String periodTypeCode = (String) o[8];
            long periodTypeNameStrId = ((Number) o[9]).longValue();
            long fiId = ((Number) o[10]).longValue();
            String fiCode = (String) o[11];
            long fiName = ((Number) o[12]).longValue();
            int delay = ((Number) o[13]).intValue();
            int delayHour = ((Number) o[14]).intValue();
            int delayMinute = ((Number) o[15]).intValue();
            String comment = (String) o[16];
            long returnTypeId = ((Number) o[17]).longValue();
            String returnTypeCode = (String) o[18];
            int scheduleVersion = ((Number) o[19]).intValue();
            int periodVersion = ((Number) o[20]).intValue();
            int periodTypeVersion = ((Number) o[21]).intValue();
            int definitionVersion = ((Number) o[22]).intValue();
            int returnTypeVersion = ((Number) o[23]).intValue();
            Timestamp delayToDate = (Timestamp) o[24];

            res.add(new Schedule(id, definitionId, definitionCode, DescriptionManager.getInstance().getDescription(namestrId), periodId, fromDate, toDate, periodTypeId, periodTypeCode, DescriptionManager.getInstance().getDescription(periodTypeNameStrId), fiId, fiCode, DescriptionManager.getInstance().getDescription(fiName), delay, delayHour, delayMinute, comment, returnTypeId, returnTypeCode, scheduleVersion, periodVersion, periodTypeVersion, definitionVersion, returnTypeVersion, delayToDate));


        }

        return res;

    }

    @Override
    public long count(Map<ScheduleFilter, Object> filter, List<Long> fiIds) {
        Collection<Long> fis = (fiIds == null || fiIds.isEmpty() ? userLocal.getCallerPrincipal().getFis() : fiIds);

        StringBuilder query = new StringBuilder("select " + "     count(sch.ID)" + "     from " + "         IN_SCHEDULES sch  " + "     join " + "         IN_BANKS fi  " + "             on fi.ID=sch.BANKID  " + "     join " + "         IN_RETURN_DEFINITIONS r  " + "             on r.ID=sch.DEFINITIONID  " + "     join " + "         IN_RETURN_TYPES rt  " + "             on rt.ID=r.TYPEID  " + "     join " + "         IN_PERIODS p  " + "             on p.ID=sch.PERIODID  " + "     join " + "         IN_PERIOD_TYPES pt  " + "             on pt.ID=p.PERIODTYPEID  where ");

        Collection<Long> rDefs = userLocal.getCallerPrincipal().getReturnDefinitions();

        query.append(" sch.DEFINITIONID in ").append(rDefs.toString().replace("[", "(").replace("]", ")"));
        query.append(" and (").append(DBUtil.get().generateConcatenatedInStatement("sch.BANKID", fis.stream().toList(), Long.class)).append(")");

        constructQueryParams(query, filter);

        Query countQuery = em.createNativeQuery(query.toString());

        setQueryParameters(countQuery, filter);

        return ((Number) countQuery.getSingleResult()).longValue();
    }

    private List<Predicate> getFilterPredicate(CriteriaBuilder cb, Root<Schedule> schedule, Map<ScheduleFilter, Object> filter) {
        List<Predicate> predicates = new ArrayList<>();
        if (filter != null) {
            for (Map.Entry<ScheduleFilter, Object> entry : filter.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }
                switch (entry.getKey()) {
                    case SCHEDULE_ID:
                        Collection<Long> scheduleIds = new ArrayList<>((Collection<Long>) entry.getValue());
                        predicates.add(schedule.get(Schedule_.id).in(scheduleIds));
                        break;
                    case DEFINITION_ID:
                        predicates.add(cb.equal(schedule.get(Schedule_.returnDefinition).get(ReturnDefinition_.id), (long) entry.getValue()));
                        break;
                    case RETURN_TYPE_ID:
                        predicates.add(cb.equal(schedule.get(Schedule_.returnDefinition).get(ReturnDefinition_.returnType).get(ReturnType_.id), (long) entry.getValue()));
                        break;
                    case FI_CODE:
                        predicates.add(cb.like(schedule.get(Schedule_.fi).get(Fi_.code), "%" + entry.getValue().toString() + "%"));
                        break;
                    case PERIOD_TYPE_ID:
                        predicates.add(cb.equal(schedule.get(Schedule_.period).get(Period_.periodType).get(PeriodType_.id), (long) entry.getValue()));
                        break;
                    case PERIOD_FROM:
                        predicates.add(cb.greaterThanOrEqualTo(schedule.get(Schedule_.period).get(Period_.fromDate), (Date) entry.getValue()));
                        break;
                    case PERIOD_TO:
                        predicates.add(cb.lessThanOrEqualTo(schedule.get(Schedule_.period).get(Period_.toDate), (Date) entry.getValue()));
                        break;
                    case DELAY:
                        predicates.add(cb.equal(schedule.get(Schedule_.delay), (int) entry.getValue()));
                        break;
                    case DELAY_HOUR:
                        predicates.add(cb.equal(schedule.get(Schedule_.delayHour), (int) entry.getValue()));
                        break;
                    case DELAY_MINUTE:
                        predicates.add(cb.equal(schedule.get(Schedule_.delayMinute), (int) entry.getValue()));
                        break;
                    case COMMENT:
                        predicates.add(cb.like(schedule.get(Schedule_.comment), "%" + entry.getValue().toString() + "%"));
                        break;
                }
            }
        }
        return predicates;
    }

    @Override

    public int getReturnDefinitionsDueDate(List<String> returnDefinitionCodes, String fiCode, Date fromDate, Date toDate) {
        List<Integer> result = em.createQuery("select max(s.delay) from IN_SCHEDULES s where trim(s.returnDefinition.code) in(:returnDefinitionCodes) and trim(s.fi.code)=:fiCode and s.period.fromDate=:fromDate and s.period.toDate=:toDate ", Integer.class).setParameter("returnDefinitionCodes", returnDefinitionCodes).setParameter("fiCode", fiCode).setParameter("fromDate", fromDate).setParameter("toDate", toDate).getResultList();
        if (result != null) {
            Iterator<Integer> resultIterator = result.iterator();
            if (resultIterator.hasNext()) {
                Integer dueDate = resultIterator.next();
                if (dueDate != null) {
                    return dueDate;
                }
            }
        }
        return -1;
    }

    @Override
    public int getReturnDefinitionsDueDateHour(List<String> returnDefinitionCodes, String fiCode, Date fromDate, Date toDate) {
        List<Integer> result = em.createQuery("select max(s.delayHour) from IN_SCHEDULES s where trim(s.returnDefinition.code) in(:returnDefinitionCodes) and trim(s.fi.code)=:fiCode and s.period.fromDate=:fromDate and s.period.toDate=:toDate ", Integer.class).setParameter("returnDefinitionCodes", returnDefinitionCodes).setParameter("fiCode", fiCode).setParameter("fromDate", fromDate).setParameter("toDate", toDate).getResultList();
        if (result != null) {
            Iterator<Integer> resultIterator = result.iterator();
            if (resultIterator.hasNext()) {
                Integer dueDateHour = resultIterator.next();
                if (dueDateHour != null) {
                    return dueDateHour;
                }
            }
        }
        return 0;
    }

    @Override
    public int getReturnDefinitionsDueDateMinute(List<String> returnDefinitionCodes, String fiCode, Date fromDate, Date toDate) {
        List<Integer> result = em.createQuery("select max(s.delayMinute) from IN_SCHEDULES s where trim(s.returnDefinition.code) in(:returnDefinitionCodes) and trim(s.fi.code)=:fiCode and s.period.fromDate=:fromDate and s.period.toDate=:toDate ", Integer.class).setParameter("returnDefinitionCodes", returnDefinitionCodes).setParameter("fiCode", fiCode).setParameter("fromDate", fromDate).setParameter("toDate", toDate).getResultList();
        if (result != null) {
            Iterator<Integer> resultIterator = result.iterator();
            if (resultIterator.hasNext()) {
                Integer dueDateMinute = resultIterator.next();
                if (dueDateMinute != null) {
                    return dueDateMinute;
                }
            }
        }
        return 0;
    }

    @Override
    public List<OverdueReturnModel> loadNotSubmittedReturnModels(Date fromDate, Date toDate, CalendarPeriodType periodType, long fiType, String fiCode, SortInfo sortInfo) {
//        em.createQuery("select s from IN_SCHEDULES s where s.period.fromDate")
        StringBuilder queryBuilder = new StringBuilder("select new net.fina.common.client.returns.OverdueReturnModel(").append("max(s.id),s.period.id,max(s.delay),max(s.delayHour),max(s.delayMinute),max(s.period.fromDate),max(s.period.toDate),s.period.periodType.code,s.fi.code, s.fi.fiType.code) from IN_SCHEDULES s where 1=1 ");


        if (periodType != null) {
            queryBuilder.append(" and s.period.periodType.periodType=:periodType ");
        }

        if (fiType > 0) {
            queryBuilder.append(" and s.fi.fiType.id=:fiTypeId");
        }
        if (fiCode != null && !fiCode.trim().isEmpty()) {
            queryBuilder.append(" and s.fi.code like :fiCode");
        }
        if (fromDate != null) {
            queryBuilder.append(" and s.period.fromDate >=:fromDate");
        }
        if (toDate != null) {
            queryBuilder.append(" and s.period.toDate <=:toDate ");
        }

        queryBuilder.append(" and s.id not in (select distinct r.schedule.id from IN_RETURNS r) and s.fi.disable=false ").append(" and s.delayToDate<:currDate ").append(" group by s.fi.code,s.fi.fiType.code,s.period.id,s.period.periodType.code,s.period.fromDate,s.period.toDate,s.delay  ").append("order by max(s.period.toDate) desc");


        constructOverdueReturnsSortPath(queryBuilder, sortInfo);

        Query query = em.createQuery(queryBuilder.toString(), OverdueReturnModel.class);

        if (periodType != null) {
            query.setParameter("periodType", periodType);
        }
        if (fiCode != null && !fiCode.trim().isEmpty()) {
            query.setParameter("fiCode", "%" + fiCode + "%");
        }

        if (fiType > 0) {
            query.setParameter("fiTypeId", fiType);
        }
        if (fromDate != null) {
            query.setParameter("fromDate", fromDate);
        }
        if (toDate != null) {
            query.setParameter("toDate", toDate);
        }

        query.setParameter("currDate", new Date());

        return query.getResultList();
    }

    @Override
    public List<Schedule> loadSchedulesByDate(Date fromDate, Date toDate) {
        return em.createQuery("select s from IN_SCHEDULES s where s.period.toDate between :fromDate and :toDate", Schedule.class).setParameter("fromDate", fromDate).setParameter("toDate", toDate).getResultList();
    }

    @Override
    public List<Schedule> loadNotSubmittedSchedules(Date fromDate, Date toDate, CalendarPeriodType periodType, long fiTypeId) {
        fromDate = fromDate == null ? em.createQuery("select min(p.fromDate) from IN_PERIODS p ", Date.class).getSingleResult() : fromDate;
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("select s from IN_SCHEDULES s where 1=1 ");

        if (periodType != null) {
            queryBuilder.append(" and s.period.periodType.periodType=:periodType ");
        }

        if (fiTypeId > 0) {
            queryBuilder.append(" and s.fi.fiType.id=:fiTypeId");
        }


        if (fromDate != null && periodType != null) {
            queryBuilder.append(" and s.period.fromDate >=:fromDate").append("  and s.period.toDate <=:toDate and s.id not in " + "(select distinct r.schedule.id from IN_RETURNS r " + "where r.schedule.period.periodType.periodType=:periodType) ").append(" order by s.period.toDate");
        } else if (fromDate != null) {
            queryBuilder.append(" and s.period.fromDate >=:fromDate ").append("and s.period.toDate <=:toDate and s.id not in ").append("(select distinct r.schedule.id from IN_RETURNS r) ").append("order by s.period.toDate");
        }

        Query query = em.createQuery(queryBuilder.toString(), Schedule.class);

        if (periodType != null) {
            query.setParameter("periodType", periodType);
        }

        if (fiTypeId > 0) {
            query.setParameter("fiTypeId", fiTypeId);
        }
        if (fromDate != null) {
            query.setParameter("fromDate", fromDate, TemporalType.DATE).setParameter("toDate", toDate, TemporalType.DATE);
        }

        return query.getResultList();
    }

    @Override

    public List<Schedule> loadFiScheduleList(long bankId, long periodId) {

        List<Schedule> packages = em.createQuery("select s from IN_SCHEDULES s,IN_RETURN_DEFINITIONS rd where s.returnDefinition.id=rd.id and s.fi.id=:bankId and s.period.id=:periodId", Schedule.class).setParameter("bankId", bankId).setParameter("periodId", periodId).getResultList();

        return packages;
    }

    @Override
    public void deleteSchedules(List<Long> scheduleIds) throws FinATypeException {

        if (scheduleIds == null || scheduleIds.isEmpty()) {
            return;
        }

        List<Schedule> schedulesWithDependency = checkDependantSchedules(scheduleIds);
        if (!schedulesWithDependency.isEmpty()) {
            StringBuilder message = new StringBuilder();
            schedulesWithDependency.forEach(s -> {
                String pattern = "Schedule with return definition {0} and period [ {1} - {2} ] has dependency!";
                message.append(MessageFormat.format(pattern, s.getReturnDefinition().getCode(), s.getPeriod().getFromDate(), s.getPeriod().getToDate())).append("\n");
            });
            throw new FinATypeException(message.toString());
        }


        em.createQuery("delete from IN_SCHEDULES where id in (:scheduleIds)").setParameter("scheduleIds", scheduleIds).executeUpdate();
    }

    @Override
    public boolean deleteAllSchedules(Map<ScheduleFilter, Object> filter, List<Long> fiIds) {
        List<Schedule> filteredSchedules = load(filter, true, fiIds, null);
        List<Long> scheduleIds = new ArrayList<>();
        filteredSchedules.forEach(schedule -> scheduleIds.add(schedule.getId()));
        boolean hasDependencies = false;
        List<Long> toBeDeleted = new ArrayList<>();
        for (Long id : scheduleIds) {
            if (canDelete(id)) {
                hasDependencies = true;
                continue;
            }
            toBeDeleted.add(id);
        }
        Query deleteQuery = em.createNativeQuery("delete from IN_SCHEDULES where id in(:ids)");
        for (List<Long> pIds : CommonUtil.partitions(toBeDeleted)) {
            deleteQuery.setParameter("ids", pIds);
            deleteQuery.executeUpdate();
        }
        return hasDependencies;
    }

    @Override
    public void editDueDate(Integer newDueDate, Integer newDueDateHour, Integer newDueDateMinute, String newComment, Map<ScheduleFilter, Object> filter, List<Long> fiIds) throws FinATypeException {
        if (newDueDate != null && newDueDate < 1 || (newDueDateHour != null && (newDueDateHour < 0 || newDueDateHour > 23)) || (newDueDateMinute != null && (newDueDateMinute < 0 || newDueDateMinute > 59))) {
            throw new FinATypeException(FinATypeException.Type.INVALID_DUE_DATE);
        }

        List<Schedule> filteredSchedules = loadPaginated(filter, fiIds, null);

        filteredSchedules.forEach(schedule -> {
            updateScheduleDueDate(schedule, newDueDate, newDueDateHour, newDueDateMinute, newComment);
            em.merge(schedule);
        });

    }

    public int getOverdueNumberOfDays(int dueDate, int dueDateHour, int dueDateMinute, Date toDate, Date uploadTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(toDate);
        calendar.add(Calendar.DATE, dueDate);
        calendar.add(Calendar.HOUR, dueDateHour);
        calendar.add(Calendar.MINUTE, dueDateMinute);
        Date toDateDueDate = calendar.getTime();

        LocalDateTime toDateDueDateLocalDateTime = toDateDueDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime uploadTimeLocalDate = uploadTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        long result = ChronoUnit.HOURS.between(uploadTimeLocalDate, toDateDueDateLocalDateTime);

        if (uploadTimeLocalDate.isAfter(toDateDueDateLocalDateTime)) {
            Calendar toDateCalendar = Calendar.getInstance();
            toDateCalendar.setTime(toDate);
            LocalDate toDateLocalDate = toDateCalendar.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            long holidayHours = calendarLocal.getNumberOfHoliday(toDateLocalDate, toDateDueDateLocalDateTime.toLocalDate()) * 24;
            result += holidayHours;
        }

        return (int) (-1 * (result) / 24);
    }


    private void updateScheduleDueDate(Schedule schedule, Integer newDueDate, Integer newDueDateHour, Integer newDueDateMinute, String comment) {
        if (newDueDate != null) {
            schedule.setDelay(newDueDate);
        }
        if (newDueDateHour != null) {
            schedule.setDelayHour(newDueDateHour);
        }
        if (newDueDateMinute != null) {
            schedule.setDelayMinute(newDueDateMinute);
        }
        if (comment != null && !comment.trim().isEmpty()) {
            schedule.setComment(comment);
        }
        schedule.setDelayToDate(generateDelayToDate(schedule));
    }


    private Date generateDelayToDate(Schedule entity) {
        if (entity.getPeriod() == null) {
            return null;
        }
        DateTime dateTime = new DateTime(entity.getPeriod().getToDate());

        dateTime = dateTime.plusDays(entity.getDelay()).plusHours(entity.getDelayHour()).plusMinutes(entity.getDelayMinute());

        return dateTime.toDate();
    }

    private void constructOverdueReturnsSortPath(StringBuilder queryBuilder, SortInfo sortInfo) {
        if (sortInfo != null) {
            switch (sortInfo.getSortField()) {
                case "fiCode":
                    queryBuilder.append(", s.fi.code ").append(sortInfo.getSortDir());
                    break;
                case "fiType":
                    queryBuilder.append(", s.fi.fiType.code ").append(sortInfo.getSortDir());
                    break;
                case "fromDate":
                    queryBuilder.append(", s.period.fromDate ").append(sortInfo.getSortDir());
                    break;
                case "toDate":
                    queryBuilder.append(", s.period.toDate ").append(sortInfo.getSortDir());
                    break;
                case "dueDate":
                    queryBuilder.append(", s.delay ").append(sortInfo.getSortDir());
                    break;
            }
        }

    }


    private void setQueryParameters(Query scheduleQuery, Map<ScheduleFilter, Object> filter) {
        if (filter != null) {
            if (filter.get(ScheduleFilter.LOAD_ALL_PERIOD_DATA) != null && !(Boolean) filter.get(ScheduleFilter.LOAD_ALL_PERIOD_DATA)) {
                DateTime dateTime = DateTime.now().minusYears(1).withDayOfMonth(1).withMonthOfYear(1);
                scheduleQuery.setParameter("periodFromDate", dateTime.toDate());
            }

            for (Map.Entry<ScheduleFilter, Object> entry : filter.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }
                switch (entry.getKey()) {
                    case DEFINITION_ID:
                        scheduleQuery.setParameter("definitionId", entry.getValue());
                        break;
                    case RETURN_TYPE_ID:
                        scheduleQuery.setParameter("returnTypeId", entry.getValue());
                        break;
                    case FI_CODE:
                        scheduleQuery.setParameter("fiCode", "%" + entry.getValue() + "%");
                        break;
                    case PERIOD_TYPE_ID:
                        scheduleQuery.setParameter("periodTypeId", entry.getValue());
                        break;
                    case PERIOD_FROM:
                        scheduleQuery.setParameter("periodFromDate", (Date) entry.getValue());
                        break;
                    case PERIOD_TO:
                        scheduleQuery.setParameter("periodToDate", (Date) entry.getValue());
                        break;
                    case DELAY:
                        scheduleQuery.setParameter("delay", entry.getValue());
                        break;
                    case DELAY_HOUR:
                        scheduleQuery.setParameter("delayHour", entry.getValue());
                        break;
                    case DELAY_MINUTE:
                        scheduleQuery.setParameter("delayMinute", entry.getValue());
                        break;
                    case COMMENT:
                        scheduleQuery.setParameter("comment", entry.getValue());
                        break;
                }
            }
        }
    }

    private void constructQueryParams(StringBuilder query, Map<ScheduleFilter, Object> filter) {
        if (filter != null) {
            if (filter.get(ScheduleFilter.LOAD_ALL_PERIOD_DATA) != null && !(Boolean) filter.get(ScheduleFilter.LOAD_ALL_PERIOD_DATA)) {
                query.append(" and p.FROMDATE >=:periodFromDate");
            }

            for (Map.Entry<ScheduleFilter, Object> entry : filter.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }
                switch (entry.getKey()) {
                    case SCHEDULE_ID:
                        List<Long> scheduleIds = (List<Long>) entry.getValue();
                        query.append(" and (").append(DBUtil.get().generateConcatenatedInStatement("sch.id", scheduleIds, Long.class)).append(")");
                        break;
                    case DEFINITION_ID:
                        query.append(" and r.id=:definitionId");
                        break;
                    case RETURN_TYPE_ID:
                        query.append(" and rt.id=:returnTypeId");
                        break;
                    case FI_CODE:
                        query.append(" and fi.code like(:fiCode)");
                        break;
                    case PERIOD_TYPE_ID:
                        query.append(" and pt.id=:periodTypeId");
                        break;
                    case PERIOD_FROM:
                        query.append(" and p.FROMDATE >=:periodFromDate");
                        break;
                    case PERIOD_TO:
                        query.append(" and p.FROMDATE <=:periodToDate");
                        break;
                    case DELAY:
                        query.append(" and sch.delay =:delay");
                        break;
                    case DELAY_HOUR:
                        query.append(" and sch.DELAY_HOUR =:delayHour");
                        break;
                    case DELAY_MINUTE:
                        query.append(" and sch.DELAY_MINUTE =:delayMinute");
                        break;
                    case COMMENT:
                        query.append(" and sch.[COMMENT] like (:comment)");
                        break;
                }
            }
        }
    }


    private List<Schedule> checkDependantSchedules(List<Long> scheduleIds) {
        return em.createQuery("select distinct s from IN_SCHEDULES s " + "left join IN_RETURNS r on s.id = r.schedule.id " + "LEFT join IN_OVERDUE_RETURN_NOTIFICATIONS rn on s.id = rn.schedule.id " + "where s.id in (:scheduleIds) and (r.schedule is not null or rn.schedule is not null)", Schedule.class).setParameter("scheduleIds", scheduleIds).getResultList();

    }

    private boolean canDelete(Long id) {
        List<Long> result = em.createQuery("select r.id from IN_RETURNS r where r.schedule.id=:id", Long.class).setParameter("id", id).getResultList();
        return !result.isEmpty();
    }

    private Schedule getScheduleFromParameter(ScheduleParameter schedule) {
        Schedule entity = new Schedule();
        entity.setId(schedule.getId());
        entity.setVersion(schedule.getVersion());
        entity.setDelay(schedule.getDelay());
        entity.setDelayHour(schedule.getDelayHour());
        entity.setDelayMinute(schedule.getDelayMinute());
        entity.setFi(em.find(Fi.class, schedule.getFiId()));
        entity.setPeriod(em.find(Period.class, schedule.getPeriodId()));
        entity.setReturnDefinition(em.find(ReturnDefinition.class, schedule.getReturnDefinitionId()));
        entity.setComment(schedule.getComment());
        entity.setDelayToDate(generateDelayToDate(entity));
        return entity;
    }

    private Schedule getScheduleFromParameter(ScheduleParameter schedule, Fi fi, Period period, ReturnDefinition returnDefinition) {
        Schedule entity = new Schedule();
        entity.setId(schedule.getId());
        entity.setVersion(schedule.getVersion());
        entity.setDelay(schedule.getDelay());
        entity.setDelayHour(schedule.getDelayHour());
        entity.setDelayMinute(schedule.getDelayMinute());
        entity.setFi(fi);
        entity.setPeriod(period);
        entity.setReturnDefinition(returnDefinition);
        entity.setComment(schedule.getComment());
        entity.setDelayToDate(generateDelayToDate(entity));
        return entity;
    }

    private void validateSchedule(Schedule entity) throws FinATypeException {

        if (entity.getReturnDefinition() == null) {
            throw new FinATypeException(FinATypeException.Type.INVALID_VALUE, "Invalid Return Definition");
        }
        if (entity.getFi() == null) {
            throw new FinATypeException(FinATypeException.Type.INVALID_VALUE, "Invalid Fi");
        }
        if (entity.getPeriod() == null) {
            throw new FinATypeException(FinATypeException.Type.INVALID_VALUE, "Invalid Period");
        }

    }


}
