package net.fina.server.dashboard.legacy.impl;

import net.fina.server.dashboard.legacy.api.LegacyDashboardLocal;
import net.fina.server.dashboard.legacy.model.LegacyAggregatedNodeData;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.returns.entity.Period;
import net.fina.server.returns.entity.Schedule;

import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TemporalType;
import java.util.*;
import java.util.function.BiFunction;

@Stateless
@Local(LegacyDashboardLocal.class)
@Interceptors(RecordingAuditor.class)
public class LegacyDashboardSession implements LegacyDashboardLocal {

    @Inject
    private EntityManager em;

    @Override
    public List<Object[]> getNodeAggregatedData(long nodeId) {
        Calendar prevYear = Calendar.getInstance();
        prevYear.setTime(new Date());
        prevYear.set(Calendar.MONTH, 0);
        prevYear.set(Calendar.DAY_OF_MONTH, 1);
        prevYear.add(Calendar.YEAR, -2);

        List<Object[]> data = em.createQuery("select sum(rv.viewPk.nvalue),rv.viewPk.toDate from RESULT_VIEW rv where rv.viewPk.toDate>=:prevYear and rv.viewPk.nodeId=:nodeId group by rv.viewPk.toDate,rv.viewPk.periodTypeId order by rv.viewPk.toDate ", Object[].class)
                .setParameter("prevYear", prevYear.getTime(), TemporalType.DATE)
                .setParameter("nodeId", nodeId)
                .getResultList();

        return data;
    }

    @Override
    public Map<Long, List<LegacyAggregatedNodeData>> getAggregatedNodesDataMonthMinusPreviousMonth(Set<Long> nodeIds) {

        Map<Long, List<LegacyAggregatedNodeData>> nodesData = getNodeAggregatedData(nodeIds);
        Map<Long, List<LegacyAggregatedNodeData>> result = new HashMap<>();
        Calendar calendar = Calendar.getInstance();

        for (Map.Entry<Long, List<LegacyAggregatedNodeData>> entry : nodesData.entrySet()) {
            List<LegacyAggregatedNodeData> calculatedData = new ArrayList<>();
            Date prevDate = null;
            double prevVal = 0;
            for (LegacyAggregatedNodeData data : entry.getValue()) {
                if(prevDate != null) {
                    calendar.setTime(prevDate);
                    int prevDateYear = calendar.get(Calendar.YEAR);
                    calendar.setTime(data.getToDate());
                    int currDateYear = calendar.get(Calendar.YEAR);
                    if(prevDateYear != currDateYear) {
                        prevVal = 0;
                    }
                }

                calculatedData.add(new LegacyAggregatedNodeData(data.getVal() - prevVal ,data.getToDate()));

                prevDate = data.getToDate();
                prevVal = data.getVal();
            }
            result.put(entry.getKey(), calculatedData);
        }

        return result;
    }

    @Override
    public Map<Date, List<String>> getTextNodeData(long nodeId) {
        Calendar prevYear = Calendar.getInstance();
        prevYear.setTime(new Date());
        prevYear.set(Calendar.MONTH, 0);
        prevYear.set(Calendar.DAY_OF_MONTH, 1);
        prevYear.add(Calendar.YEAR, -2);

        List<Object[]> data = em.createQuery("select rv.viewPk.toDate, rv.viewPk.value from RESULT_VIEW rv " +
                "where rv.viewPk.toDate>=:prevYear and rv.viewPk.nodeId=:nodeId " +
                "order by rv.viewPk.toDate", Object[].class)
                .setParameter("prevYear", prevYear.getTime(), TemporalType.DATE)
                .setParameter("nodeId", nodeId)
                .getResultList();

        Map<Date, List<String>> aggregatedDataMap = new LinkedHashMap<>();
        for (Object[] objects : data) {
            Date toDate = (Date) objects[0];
            String curValue = (String) objects[1];

            List<String> values = aggregatedDataMap.computeIfAbsent(toDate, k -> new ArrayList<>());
            values.add(curValue);
        }

        return aggregatedDataMap;
    }

    @Override
    public Map<Long, List<LegacyAggregatedNodeData>> getNodeAggregatedData(Collection<Long> nodeIds) {
        Calendar prevYear = Calendar.getInstance();
        prevYear.setTime(new Date());
        prevYear.set(Calendar.MONTH, 0);
        prevYear.set(Calendar.DAY_OF_MONTH, 1);
        prevYear.add(Calendar.YEAR, -2);

        return loadNodes(nodeIds, prevYear);
    }

    @Override
    public Map<Long, List<LegacyAggregatedNodeData>> getTwelveMonthAverage(Collection<Long> nodeIds) {
        Calendar calendar = Calendar.getInstance();

        return computeAverage(nodeIds, (legacyAggregatedNodeData, map) -> {

            calendar.setTime(legacyAggregatedNodeData.getToDate());
            int currMonth = calendar.get(Calendar.MONTH);
            int currYear = calendar.get(Calendar.YEAR);
            int counter = 0;
            double value = 0;
            for(int i = 0; i < 12; i++) {
                Map<Integer, Double> tmp = map.get(currYear);

                if(tmp != null) {
                    Double currVal = tmp.get(currMonth);

                    if(currVal != null) {
                        value += currVal;
                        counter++;
                    }

                    currMonth--;
                    if (currMonth < 0) {
                        currMonth = 11;
                        currYear --;
                    }
                } else {
                    break;
                }
            }

            return new LegacyAggregatedNodeData(value / counter, legacyAggregatedNodeData.getToDate());
        });
    }


    @Override
    public Map<Long, List<LegacyAggregatedNodeData>> getTwoMonthAverage(Collection<Long> nodeIds) {
        Calendar calendar = Calendar.getInstance();

        return computeAverage(nodeIds, (legacyAggregatedNodeData, map) -> {

            calendar.setTime(legacyAggregatedNodeData.getToDate());
            int currMonth = calendar.get(Calendar.MONTH);
            int currYear = calendar.get(Calendar.YEAR);


            Map<Integer, Double> prevYear = map.get(currYear - 1);
            if (prevYear != null) {
                Double montInPrevYear = prevYear.get(currMonth);
                if (montInPrevYear != null) {
                    return new LegacyAggregatedNodeData((legacyAggregatedNodeData.getVal() + prevYear.get(currMonth)) / 2, legacyAggregatedNodeData.getToDate());
                }
            }
            return null;
        });
    }

    private Map<Long, List<LegacyAggregatedNodeData>> computeAverage(Collection<Long> nodeIds,
                                                                     BiFunction<LegacyAggregatedNodeData,
                                                                       Map<Integer, Map<Integer, Double>>,
                                                                             LegacyAggregatedNodeData> handler) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.YEAR, -3);
        Map<Long, List<LegacyAggregatedNodeData>> nodesMap = loadNodes(nodeIds, calendar);
        Map<Long, List<LegacyAggregatedNodeData>> resultMap = new HashMap<>();


        for (Map.Entry<Long, List<LegacyAggregatedNodeData>> nodes : nodesMap.entrySet()) {
            List<LegacyAggregatedNodeData> list = nodes.getValue();
            List<LegacyAggregatedNodeData> resultList = new ArrayList<>();
            Map<Integer, Map<Integer, Double>> map = new HashMap<>();

            for (LegacyAggregatedNodeData curr : list) {
                calendar.setTime(curr.getToDate());
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                map.computeIfAbsent(year, k -> new HashMap<Integer, Double>()).put(month, curr.getVal());
            }

            for (int i = list.size() - 1; i > 0; i--) {
                if (i - 12 < 0) {
                    break;
                }
                LegacyAggregatedNodeData res = handler.apply(list.get(i), map);
                if (res != null) {
                    resultList.add(res);
                }
            }

            resultMap.put(nodes.getKey(), resultList);
        }
        return resultMap;
    }

    private Map<Long, List<LegacyAggregatedNodeData>> loadNodes(Collection<Long> nodeIds, Calendar period) {
        List<Object[]> data = em.createQuery("select sum(rv.viewPk.nvalue),rv.viewPk.toDate,rv.viewPk.nodeId from RESULT_VIEW rv " +
                "where rv.viewPk.toDate>=:prevYear and rv.viewPk.nodeId in :nodeIds " +
                "group by rv.viewPk.nodeId,rv.viewPk.toDate,rv.viewPk.periodTypeId " +
                "order by rv.viewPk.toDate ", Object[].class)
                .setParameter("prevYear", period.getTime(), TemporalType.DATE)
                .setParameter("nodeIds", nodeIds)
                .getResultList();

        return getAggregatedDataMap(data);
    }

    @Override
    public Map<Long, List<LegacyAggregatedNodeData>> getNodeAggregatedDataFiltered(Collection<Long> nodeIds, long filterNodeId, String filterNodeValue) {
        Calendar prevYear = Calendar.getInstance();
        prevYear.setTime(new Date());
        prevYear.set(Calendar.MONTH, 0);
        prevYear.set(Calendar.DAY_OF_MONTH, 1);
        prevYear.add(Calendar.YEAR, -2);

        return loadNodeDataForFilteredFis(nodeIds, prevYear, filterNodeId, filterNodeValue);
    }

    /**
     * Gets aggregated filtered by value of specified node;
     * Data entry will be used in calculations only if node with id @filterNodeId had value of @filterValue for that period
     * */
    private Map<Long, List<LegacyAggregatedNodeData>> loadNodeDataForFilteredFis(Collection<Long> nodeIds, Calendar period, long filterNodeId, String filterNodeValue) {
        List<Object[]> data = em.createQuery("select sum(rv.viewPk.nvalue),rv.viewPk.toDate,rv.viewPk.nodeId from RESULT_VIEW rv " +
                "inner join RESULT_VIEW rv1 on rv1.viewPk.periodId=rv.viewPk.periodId and rv1.viewPk.bankId=rv.viewPk.bankId " +
                "and rv1.viewPk.versionCode=rv.viewPk.versionCode and rv1.viewPk.latestVersionCode=rv.viewPk.latestVersionCode " +
                "and rv1.viewPk.nodeId=:filterNodeId " +
                "where rv1.viewPk.value=:filterValue and (rv.viewPk.toDate>=:prevYear and rv.viewPk.nodeId in :nodeIds) " +
                "group by rv.viewPk.nodeId,rv.viewPk.toDate,rv.viewPk.periodTypeId " +
                "order by rv.viewPk.toDate ", Object[].class)
                .setParameter("filterNodeId", filterNodeId)
                .setParameter("filterValue", filterNodeValue)
                .setParameter("prevYear", period.getTime(), TemporalType.DATE)
                .setParameter("nodeIds", nodeIds)
                .getResultList();

        return getAggregatedDataMap(data);
    }


    @Override
    public Map<Long, List<LegacyAggregatedNodeData>> getSummedUpColumnData(Collection<Long> columnNodeIds) {
        Calendar prevYear = Calendar.getInstance();
        prevYear.setTime(new Date());
        prevYear.set(Calendar.MONTH, 0);
        prevYear.set(Calendar.DAY_OF_MONTH, 1);
        prevYear.add(Calendar.YEAR, -2);

        List<Object[]> data = em.createQuery("select sum(rv.viewPk.nvalue),rv.viewPk.toDate, mn.parentId from RESULT_VIEW rv " +
                "join IN_MDT_NODES mn on mn.id = rv.viewPk.nodeId " +
                "where rv.viewPk.toDate>=:prevYear and mn.parentId in :columnNodeIds " +
                "group by mn.parentId, rv.viewPk.toDate, rv.viewPk.periodTypeId " +
                "order by rv.viewPk.toDate ", Object[].class)
                .setParameter("prevYear", prevYear.getTime(), TemporalType.DATE)
                .setParameter("columnNodeIds", columnNodeIds)
                .getResultList();

        return getAggregatedDataMap(data);
    }

    @Override
    public List<Schedule> loadDistinctSchedulesByPeriodEndDateRange(long returnDefinitionId, Date fromDate, Date toDate) {
        List<Object[]> rawResult = em.createQuery("select distinct s.period, s.delay,s.delayHour, s.delayMinute from IN_SCHEDULES s " +
                "where s.returnDefinition.id=:definitionId and s.period.toDate>=:fromDate and s.period.toDate<=:toDate", Object[].class)
                .setParameter("definitionId", returnDefinitionId)
                .setParameter("fromDate", fromDate, TemporalType.DATE)
                .setParameter("toDate", toDate, TemporalType.DATE)
                .getResultList();

        List<Schedule> result = new ArrayList<>();
        if (rawResult != null && !rawResult.isEmpty()) {
            for (Object[] o : rawResult) {
                Schedule s = new Schedule();
                Period p = (Period) o[0];
                s.setPeriod(p);
                int delay = (int) o[1];
                s.setDelay(delay);
                int delayHour = (int) o[2];
                s.setDelayHour(delayHour);
                int delayMinute = (int) o[3];
                s.setDelayMinute(delayMinute);

                result.add(s);
            }
        }

        return result;
    }

    private Map<Long, List<LegacyAggregatedNodeData>> getAggregatedDataMap(List<Object[]> data) {
        Map<Long, List<LegacyAggregatedNodeData>> result = new LinkedHashMap<>();

        for (Object[] objects : data) {

            double val = (double) objects[0];
            Date toDate = (Date) objects[1];
            long parentId = (Long) objects[2];

            LegacyAggregatedNodeData nodeData = new LegacyAggregatedNodeData();
            nodeData.setVal(val);
            nodeData.setToDate(toDate);

            result.computeIfAbsent(parentId, k -> new ArrayList<>()).add(nodeData);
        }

        return result;
    }

    @Override
    public double yearlyCoefficient(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int dayInYear = c.get(Calendar.DAY_OF_YEAR);
        c.setTime(new Date());
        int year = c.get(Calendar.YEAR);
        Calendar calTwo = new GregorianCalendar(year, Calendar.DECEMBER, 31);

        return (double) calTwo.get(Calendar.DAY_OF_YEAR) / dayInYear;
    }
}
