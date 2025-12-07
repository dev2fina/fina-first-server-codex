package net.fina.server.returns.impl;

import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.*;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.filter.PeriodFilter;
import net.fina.common.shared.SortField;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.returns.api.PeriodLocal;
import net.fina.server.returns.entity.*;
import net.fina.server.util.SortUtil;
import org.jboss.logging.Logger;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
@Local(PeriodLocal.class)
@Interceptors(RecordingAuditor.class)
public class PeriodSession implements PeriodLocal {

    @Inject
    private EntityManager em;
    @Inject
    private Logger log;

    @Override
    public List<Period> load(Map<PeriodFilter, Object> filter) {
        return load(filter, false, null);
    }

    @Override
    public List<Period> load(Map<PeriodFilter, Object> filter, boolean asc, SortField sortField) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Period> query = cb.createQuery(Period.class);
        Root<Period> period = query.from(Period.class);
        period.fetch(Period_.periodType, JoinType.LEFT);

        if (asc) {
            query.orderBy(cb.asc(period.get(Period_.periodType).get(PeriodType_.id)), cb.asc(period.get(Period_.fromDate)));
        } else {
            query.orderBy(cb.desc(period.get(Period_.fromDate)));
        }

        if (sortField != null) {
            String sortProperty = sortField.getProperty();
            boolean isAsc = sortField.isAsc();

            if (!sortProperty.isBlank() && SortUtil.isPropertyExistOnEntity(period, sortProperty)) {
                query.orderBy(isAsc ? cb.asc(period.get(sortProperty)) : cb.desc(period.get(sortProperty)));

            }
        }

        List<Predicate> predicates = getFilterPredicates(cb, period, filter);
        if (!predicates.isEmpty()) {
            query.where(predicates.toArray(new Predicate[0]));
        }

        Query loadQuery = em.createQuery(query);

        /**
         * Offset
         */
        if (filter != null) {
            if (filter.get(PeriodFilter.Offset) != null) {
                int offset = (int) filter.get(PeriodFilter.Offset);
                if (offset >= 0) {
                    loadQuery.setFirstResult(offset);
                }
            }

            /**
             * Limit
             */
            if (filter.get(PeriodFilter.limit) != null) {
                int limit = (int) filter.get(PeriodFilter.limit);
                if (limit > 0) {
                    loadQuery.setMaxResults(limit);
                }
            }
        }

        return loadQuery.getResultList();
    }

    @Override
    public long count(Map<PeriodFilter, Object> filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Period> period = query.from(Period.class);

        query.select(cb.count(period.get(Period_.id)));

        List<Predicate> predicates = getFilterPredicates(cb, period, filter);
        if (!predicates.isEmpty()) {
            query.where(predicates.toArray(new Predicate[0]));
        }

        return em.createQuery(query).getSingleResult();
    }


    @SuppressWarnings({"rawtypes", "deprecation", "static-access", "unchecked"})
    public ArrayList<Map<Integer, String>> getPeriodInsertRows(String type, int frequencyType, java.util.Date fromDate, int startPeriodNumber, int numberOfPeriods) {
        ArrayList v = new ArrayList();
        try {
            GregorianCalendar gc;
            Date d;
            // Calendar
            Date date = fromDate;
            int y = fromDate.getYear();
            int f;
            int p = startPeriodNumber;
            switch (frequencyType) {
                case 0: {
                    f = 1;
                    break;
                }
                case 1: {
                    f = 3;
                    break;
                }
                case 2: {
                    f = 6;
                    break;
                }
                case 5: {
                    f = 52;
                    break;
                }
                default:
                    f = 12;
            }
            for (int i = 1; i <= 12; i++) {
                Map<Integer, String> prevRow = new HashMap<Integer, String>();
                if ((i - ((int) (i / f)) * f) == 0) {
                    gc = new GregorianCalendar(y, i - 1, 1);
                    d = new Date(y, i - 1, 1);
                    if ((date.equals(d) || date.before(d)) && p <= numberOfPeriods) {
                        prevRow.put(0, type);
                        prevRow.put(1, String.valueOf(p));
                        prevRow.put(2, date2string(new Date(y, i - (f - 1) - 1, 1)));
                        prevRow.put(3, date2string(new Date(y, i - 1, gc.getActualMaximum(gc.DAY_OF_MONTH))));
                        v.add(prevRow);
                        p++;
                    }
                }
            }
        } catch (Exception ignore) {
        }
        return v;
    }

    @SuppressWarnings({"rawtypes", "deprecation", "static-access", "unchecked"})
    public ArrayList<Map<Integer, String>> getPeriodInsertRows(String type, java.util.Date fromDate, int startPeriodNumber, int numberOfPeriods, int daysInPeriods, int daysBetweenPeriods) {
        ArrayList v = new ArrayList();
        try {
            GregorianCalendar gc;
            Date d;
            int y = fromDate.getYear();
            int p = startPeriodNumber;
            Date date = fromDate;

            gc = new GregorianCalendar(y, 0, 1);

            int increment = 1;
            for (int i = 1; i <= gc.getActualMaximum(gc.DAY_OF_YEAR); i += increment) {
                // Get First Date
                gc = new GregorianCalendar(y, 0, i);
                d = new Date(y, 0, i);

                if ((date.equals(d) || date.before(d)) && p <= numberOfPeriods) {

                    Map<Integer, String> row = new HashMap<Integer, String>();
                    row.put(0, type);
                    row.put(1, String.valueOf(p));
                    row.put(2, date2string(new Date(y, 0, i)));
                    row.put(3, date2string(new Date(y, 0, i + (daysInPeriods - 1))));
                    v.add(row);
                    p++;
                    increment = daysInPeriods;
                    i += daysBetweenPeriods;
                }

            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return v;
    }

    //TODO
    @Deprecated
    private String date2string(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        format.applyPattern("dd/MM/yyyy");
        return format.format(date);
    }

    @Override
    public PeriodType save(PeriodType periodType) throws FinATypeException {
        if (!isCodeUnique(periodType.getCode(), periodType.getId())) {
            throw new FinATypeException(FinATypeException.Type.CODE_UNIQUE);
        }
        if (periodType.getId() > 0) {
            if (isPeriodTypeUsed(periodType.getId())) {
                PeriodType existing = em.find(periodType.getClass(), periodType.getId());
                if (existing.getPeriodType() != periodType.getPeriodType()) {
                    throw new FinATypeException("Calendar type could not be modified.");
                }
            }
            periodType = em.merge(periodType);
        } else {
            em.persist(periodType);
        }
        return periodType;
    }


    public void deletePeriodType(long id) throws FinATypeException {
        if (isPeriodTypeUsed(id)) {
            throw new FinATypeException(FinATypeException.Type.DEPENDENCY_ERROR);
        }
        PeriodType periodType = em.find(PeriodType.class, id);
        em.remove(periodType);
    }

    @Override
    public Period savePeriod(Period period) throws FinATypeException {
        PeriodType periodType = em.find(PeriodType.class, period.getPeriodType().getId());
        period.setPeriodType(periodType);
        if (!isUniquePeriod(period)) {
            throw new FinATypeException(FinATypeException.Type.PERIOD_UNIQUE);
        }
        if (period.getId() <= 0) {
            em.persist(period);
        }
        return period;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<PeriodType> loadPeriodTypes() {
        Query query = em.createNamedQuery("loadperiodtypes", PeriodType.class);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Period> loadPeriodByIds(List<Long> ids) {
        if (ids.size() < 1) {
            return new ArrayList<Period>();
        }
        Query query = em.createQuery("select p from IN_PERIODS p where p.id in(:ids)");
        query.setParameter("ids", ids);
        return query.getResultList();
    }

    @Override
    public void delete(long periodId) throws FinATypeException {
        if (isUsePeriod(periodId)) {
            throw new FinATypeException(FinATypeException.Type.DEPENDENCY_ERROR);
        }

        Period period = em.find(Period.class, periodId);
        em.remove(period);
    }

    private boolean isUsePeriod(long periodId) {
        return !em.createNamedQuery("Period.checkUse").setParameter("periodId", periodId).getResultList().isEmpty();
    }

    @Override
    public PeriodType getPeriodTypeByCode(String code) {
        return (PeriodType) em.createQuery("select pt from IN_PERIOD_TYPES pt where trim(pt.code)=:code").setParameter("code", code.trim()).getSingleResult();
    }

    @Override
    public boolean hasSchedules(Period period) {
        List<Schedule> schedules = em.createQuery("select s from IN_SCHEDULES s where s.period.id=:periodId", Schedule.class)
                .setParameter("periodId", period.getId())
                .getResultList();
        return schedules.isEmpty();
    }

    @Override
    public void deletePeriods(List<Long> periodIds) throws FinATypeException {
        boolean hasDependencies = false;
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/YYYY");
        StringBuilder sb = new StringBuilder();
        sb.append("Current Periods have dependencies :").append("\n");
        Query deleteQuery = em.createQuery("delete from IN_PERIODS p where p.id=:id");
        for (Long id : periodIds) {
            if (isUsePeriod(id)) {
                hasDependencies = true;
                Period p = em.find(Period.class, id);
                sb.append("Type : ").append(p.getPeriodType().getCode())
                        .append(" , ").append("From : ")
                        .append(df.format(p.getFromDate()))
                        .append(",")
                        .append("To : ")
                        .append(df.format(p.getToDate()))
                        .append("\n");
                continue;
            }
            deleteQuery.setParameter("id", id);
            deleteQuery.executeUpdate();
        }
        if (hasDependencies) {
            throw new FinATypeException(sb.toString());
        }
    }

    @Override
    public boolean exists(Date fromDate, Date toDate) {
        return !em.createQuery("select p.id from IN_PERIODS p where p.fromDate = :fromDate and p.toDate = :toDate")
                .setParameter("fromDate", fromDate)
                .setParameter("toDate", toDate)
                .getResultList().isEmpty();
    }

    @Override
    public Map<String, Long> loadPeriodTypeCodeIdMap() {
        return em.createQuery("select code,id  from IN_PERIOD_TYPES ", Tuple.class)
                .getResultStream().collect(
                        Collectors.toMap(
                                tuple -> (String) tuple.get(0),
                                tuple -> ((Number) tuple.get(1)).longValue()
                        )
                );
    }

    private List<Predicate> getFilterPredicates(CriteriaBuilder cb, Root<Period> period, Map<PeriodFilter, Object> filter) {
        List<Predicate> predicates = new ArrayList<>();
        if (filter != null) {

            for (Map.Entry<PeriodFilter, Object> entry : filter.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }
                switch (entry.getKey()) {
                    case number:
                        predicates.add(cb.equal(period.get(Period_.periodNumber), entry.getValue()));
                        break;
                    case from:
                        predicates.add(cb.greaterThanOrEqualTo(period.get(Period_.fromDate), (Date) entry.getValue()));
                        break;
                    case to:
                        predicates.add(cb.lessThanOrEqualTo(period.get(Period_.toDate), (Date) entry.getValue()));
                        break;
                    case type:
                        predicates.add(cb.like(period.get(Period_.periodType).get(PeriodType_.code), entry.getValue().toString()));
                        break;
                    case typeIds:
                        Collection<Long> typeIds = new ArrayList<>((Collection<Long>) entry.getValue());
                        predicates.add(period.get(Period_.periodType).get(PeriodType_.id).in(typeIds));
                        break;
                    case ids:
                        Collection<Long> ids = new ArrayList<>((Collection<Long>) entry.getValue());
                        if (ids.isEmpty()) {
                            ids = Collections.singletonList(0L);
                        }
                        predicates.add(period.get(Period_.id).in(ids));
                        break;
                    case limit:
                    case Offset:
                        break;
                }
            }
        }
        return predicates;
    }

    private boolean isUniquePeriod(Period period) {
        return em.createQuery("select p.id from IN_PERIODS p where p.fromDate=:fromdate and p.toDate=:todate and p.periodType.code=:periodTypeCode and p.id<>:id", Long.class)
                .setParameter("fromdate", period.getFromDate())
                .setParameter("todate", period.getToDate())
                .setParameter("periodTypeCode", period.getPeriodType().getCode())
                .setParameter("id", period.getId())
                .getResultList()
                .isEmpty();

    }

    private boolean isCodeUnique(String code, long id) {
        return em.createQuery("select c.id from IN_PERIOD_TYPES c where trim(c.code)=:code and c.id !=:id", Long.class)
                .setParameter("id", id)
                .setParameter("code", code)
                .getResultList()
                .isEmpty();
    }

    private boolean isPeriodTypeUsed(long id) {
        return (0L < em.createQuery("select count (p.id) from IN_PERIODS p where p.periodType.id=:typeId", Long.class).setParameter("typeId", id).getSingleResult());
    }


}
