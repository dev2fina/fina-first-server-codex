package net.fina.server.dashboard.legacy.api;

import net.fina.server.dashboard.legacy.model.LegacyAggregatedNodeData;
import net.fina.server.returns.entity.Schedule;

import java.util.*;

public interface LegacyDashboardLocal {
    List<Object[]> getNodeAggregatedData(long nodeId);

    Map<Long, List<LegacyAggregatedNodeData>> getNodeAggregatedData(Collection<Long> nodeIds);

    Map<Date, List<String>> getTextNodeData(long nodeId);

    Map<Long, List<LegacyAggregatedNodeData>> getTwelveMonthAverage(Collection<Long> nodeIds);

    Map<Long, List<LegacyAggregatedNodeData>> getTwoMonthAverage(Collection<Long> nodeIds);

    Map<Long, List<LegacyAggregatedNodeData>> getSummedUpColumnData(Collection<Long> columnNodeIds);

    List<Schedule> loadDistinctSchedulesByPeriodEndDateRange(long returnDefinitionId, Date fromDate, Date toDate);

    Map<Long, List<LegacyAggregatedNodeData>> getAggregatedNodesDataMonthMinusPreviousMonth(Set<Long> nodeIds);

    Map<Long, List<LegacyAggregatedNodeData>> getNodeAggregatedDataFiltered(Collection<Long> nodeIds, long filterNodeId, String filterNodeValue);

    double yearlyCoefficient(Date date);
}
