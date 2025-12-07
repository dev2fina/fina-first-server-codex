package net.fina.server.dashboard.legacy.proxy;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.server.util.CommonUtil;
import net.fina.common.server.util.ConfigurationUtil;
import net.fina.common.shared.dashboard.ChartContentMetaModel;
import net.fina.server.dashboard.legacy.api.LegacyDashboardLocal;
import net.fina.server.dashboard.legacy.model.LegacyAggregatedNodeData;
import net.fina.server.dashboard.legacy.model.FilterModel;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.mdt.impl.MDTCacheManager;
import net.fina.server.returns.api.ReturnDefinitionLocal;
import net.fina.server.returns.entity.Schedule;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.logging.Logger;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
@SecurityDomain("FinASecurityDomain")
public class LegacyDashboardProxySession {

    private final String PERIOD_TYPE_MONTH_FORMAT_PATTERN = "MM-yyyy";
    private final String PERIOD_TYPE_MONTH = "M";
    private final String PERIOD_TYPE_QUARTER = "Q";
    private final String PERIOD_TYPE_QUARTER_CUMULATIVE = "C";
    private final String PERIOD_TYPE_QUARTER_NON_CUMULATIVE = "N";

    private final Logger logger = Logger.getLogger(getClass().getName());

    @EJB
    private LegacyDashboardLocal legacyDashboardLocal;

    @EJB
    private MDTCacheManager mdtCacheManager;

    @EJB
    private ReturnDefinitionLocal returnDefinitionLocal;

    @RolesAllowed(PermissionIdNames.FINA_RETURNS_REVIEW)
    public Collection<Map<String, String>> loadAggregatedNodesData(String periodType, String nodeCodesString, String periodQuarterType, String filter) {
        String[] nodeCodes = nodeCodesString.split(",");
        FilterModel filterModel = getFilterModelFromString(filter);
        return loadAndPrepareAggregatedNodesData(periodType, nodeCodes, periodQuarterType, filterModel);
    }

    @RolesAllowed(PermissionIdNames.FINA_RETURNS_REVIEW)
    public Collection<Map<String, String>> loadAggregatedNodesDataWithYearlyCoefficient(String periodType, String nodeCodesString, String periodQuarterType, String filter) {
        String[] nodeCodes = nodeCodesString.split(",");
        FilterModel filterModel = getFilterModelFromString(filter);
        return loadAndPrepareAggregatedNodesDataWithYearlyCoefficient(periodType, nodeCodes, periodQuarterType, filterModel);
    }

    @RolesAllowed(PermissionIdNames.FINA_RETURNS_REVIEW)
    public Collection<Map<String, String>> loadAggregatedNodesDataMonthMinusPreviousMonth(String periodType, String nodeCodesString) {
        Map<Long, String> nodeIds = new HashMap<>();
        String[] nodeCodes = nodeCodesString.split(",");

        for (String nodeCode : nodeCodes) {
            nodeIds.put(getMdtNodeIdByCode(nodeCode), nodeCode);
        }

        Map<Long, List<LegacyAggregatedNodeData>> nodesData = legacyDashboardLocal.getAggregatedNodesDataMonthMinusPreviousMonth(nodeIds.keySet());

        return prepareAggregatedNodeResult(periodType, nodeIds, nodesData, null);
    }

    @RolesAllowed(PermissionIdNames.FINA_RETURNS_REVIEW)
    public List<Map<String, String>> loadConcatenatedTextNodeData(String periodType, String nodeCode, String periodQuarterType) {
        long nodeId = getMdtNodeIdByCode(nodeCode);

        Map<Date, List<String>> data = legacyDashboardLocal.getTextNodeData(nodeId);

        List<Map<String, String>> result = new ArrayList<>();

        // Transform data to result list
        switch (periodType) {
            case PERIOD_TYPE_QUARTER:

                //<Year,<Q1,Q2,Q3,Q4 -val>>
                Map<Integer, Map<String, List<String>>> groupedData = groupTextDataByYearAndQuarter(data, periodQuarterType);
                Map<String, Map<String, String>> resultMap = getDataGroupedByUniqueQuarters(nodeCode, groupedData);
                result = new ArrayList<>(resultMap.values());
                break;

            case PERIOD_TYPE_MONTH:
                SimpleDateFormat format = new SimpleDateFormat(PERIOD_TYPE_MONTH_FORMAT_PATTERN);
                for (Map.Entry<Date, List<String>> entry : data.entrySet()) {
                    Map<String, String> valueMap = new HashMap<>();

                    Date toDate = entry.getKey();
                    valueMap.put("quarter", format.format(toDate));

                    String concatenatedData = concatenateList(data.get(toDate));
                    valueMap.put(nodeCode, concatenatedData);

                    result.add(valueMap);
                }
                break;
        }

        return result;
    }

    @RolesAllowed(PermissionIdNames.FINA_RETURNS_REVIEW)
    public ChartContentMetaModel getChartContent(String fileName, String base64ChartContent) {
        return CommonUtil.getChartContent(fileName, base64ChartContent);
    }

    @RolesAllowed(PermissionIdNames.FINA_RETURNS_REVIEW)
    public Map<String, Object> loadConfiguration() throws Exception {
        String configurationFilePath = ConfigurationUtil.get().get("SUPERVISION_DASHBOARD_CONFIG_FILE");
        return new ObjectMapper().readValue(new File(configurationFilePath), new TypeReference<Map<String, Object>>() {
        });
    }

    @RolesAllowed(PermissionIdNames.FINA_RETURNS_REVIEW)
    public Collection<Map<String, String>> loadTwelveMonthAverage(String nodeCodesString) {
        String[] nodeCodes = nodeCodesString.split(",");
        Map<Long, String> nodeIds = new HashMap<>();

        for (String nodeCode : nodeCodes) {
            nodeIds.put(getMdtNodeIdByCode(nodeCode), nodeCode);
        }

        Map<Long, List<LegacyAggregatedNodeData>> nodesData = legacyDashboardLocal.getTwelveMonthAverage(nodeIds.keySet());


        return prepareAggregatedNodeResult("M", nodeIds, nodesData, null);
    }

    @RolesAllowed(PermissionIdNames.FINA_RETURNS_REVIEW)
    public Collection<Map<String, String>> twoMonthAverage(String nodeCodesString) {
        String[] nodeCodes = nodeCodesString.split(",");
        Map<Long, String> nodeIds = new HashMap<>();

        for (String nodeCode : nodeCodes) {
            nodeIds.put(getMdtNodeIdByCode(nodeCode), nodeCode);
        }

        Map<Long, List<LegacyAggregatedNodeData>> nodesData = legacyDashboardLocal.getTwoMonthAverage(nodeIds.keySet());

        return prepareAggregatedNodeResult("M", nodeIds, nodesData, null);
    }

    @RolesAllowed(PermissionIdNames.FINA_RETURNS_REVIEW)
    public Collection<Map<String, String>> loadSummedColumns(String columnNodeCodesString, String periodType, String periodQuarterType) {
        String[] columnNodeCodes = columnNodeCodesString.split(",");

        Map<Long, String> columnCodeIdMap = new HashMap<>();

        for (String code : columnNodeCodes) {
            columnCodeIdMap.put(getMdtNodeIdByCode(code), code);
        }

        Map<Long, List<LegacyAggregatedNodeData>> columnData = legacyDashboardLocal.getSummedUpColumnData(columnCodeIdMap.keySet());

        return prepareAggregatedNodeResult(periodType, columnCodeIdMap, columnData, periodQuarterType);
    }

    @RolesAllowed(PermissionIdNames.FINA_RETURNS_REVIEW)
    public Collection<Map<String, String>> loadAggregatedNodesDataForFinishedPeriods(String periodType, String nodeCodes,
                                                                                     String returnCode, String periodQuarterType) {
        Collection<Map<String, String>> allResults = loadAggregatedNodesData(periodType, nodeCodes, periodQuarterType, null);

        if (returnCode == null || returnCode.isEmpty()) {
            return allResults;
        }

        long returnDefinitionId = returnDefinitionLocal.getReturnDefinitionIdByCode(returnCode);
        List<Map<String, String>> lastPeriodData = new ArrayList<>(allResults);

        int finishedPeriodDataIndex = 0;
        Date now = new Date();

        for (int i = lastPeriodData.size() - 1; i > 0; i--) {
            Map<String, String> dataEntry = lastPeriodData.get(i);
            String periodString = dataEntry.get("quarter");
            Date periodToMonthStart = LegacyDateCalculationsUtil.getFirstDayOfPeriodFromString(periodString, periodType);
            Date nextMonthStart = LegacyDateCalculationsUtil.addMonths(periodToMonthStart, 1);

            List<Schedule> schedules = legacyDashboardLocal.loadDistinctSchedulesByPeriodEndDateRange(returnDefinitionId, periodToMonthStart, nextMonthStart);

            boolean returnSubmissionDeadlinePassed = true;
            for (Schedule s : schedules) {
                Date deadline = LegacyDateCalculationsUtil.addDays(s.getPeriod().getToDate(), s.getDelay());
                deadline = LegacyDateCalculationsUtil.addHours(deadline, s.getDelayHour());
                deadline = LegacyDateCalculationsUtil.addMinutes(deadline, s.getDelayMinute());
                if (deadline.after(now)) { // this means that return definitions might still be imported
                    returnSubmissionDeadlinePassed = false;
                    break;
                }
            }

            if (returnSubmissionDeadlinePassed) {
                break;
            } else {
                finishedPeriodDataIndex++;
            }
        }

        lastPeriodData = finishedPeriodDataIndex >= 0 ? lastPeriodData.subList(0, allResults.size() - finishedPeriodDataIndex) : new ArrayList<>();

        return lastPeriodData;
    }

    private Collection<Map<String, String>> loadAndPrepareAggregatedNodesData(String periodType, String[] nodeCodes, String periodQuarterType, FilterModel filter) {

        Map<Long, String> nodeIds = new HashMap<>();

        for (String nodeCode : nodeCodes) {
            nodeIds.put(getMdtNodeIdByCode(nodeCode), nodeCode);
        }

        Map<Long, List<LegacyAggregatedNodeData>> nodesData = getNodesDataMap(filter, nodeIds);

        return prepareAggregatedNodeResult(periodType, nodeIds, nodesData, periodQuarterType);
    }

    private Collection<Map<String, String>> loadAndPrepareAggregatedNodesDataWithYearlyCoefficient(String periodType, String[] nodeCodes, String periodQuarterType, FilterModel filter) {

        Map<Long, String> nodeIds = new HashMap<>();

        for (String nodeCode : nodeCodes) {
            nodeIds.put(getMdtNodeIdByCode(nodeCode), nodeCode);
        }

        Map<Long, List<LegacyAggregatedNodeData>> nodesData = getNodesDataMap(filter, nodeIds);

        for (Map.Entry<Long, List<LegacyAggregatedNodeData>> nodeData : nodesData.entrySet()) {
            for (LegacyAggregatedNodeData nd : nodeData.getValue()) {
                double withYearlyCoefficient = nd.getVal() * legacyDashboardLocal.yearlyCoefficient(nd.getToDate());
                nd.setVal(withYearlyCoefficient);
            }
        }

        return prepareAggregatedNodeResult(periodType, nodeIds, nodesData, periodQuarterType);
    }

    private Map<Long, List<LegacyAggregatedNodeData>> getNodesDataMap(FilterModel filter, Map<Long, String> nodeIds) {
        Map<Long, List<LegacyAggregatedNodeData>> nodesData;
        if (filter != null) {
            long filterNodeId = getMdtNodeIdByCode(filter.getProperty());
            nodesData = legacyDashboardLocal.getNodeAggregatedDataFiltered(nodeIds.keySet(), filterNodeId, filter.getValue());
        } else {
            nodesData = legacyDashboardLocal.getNodeAggregatedData(nodeIds.keySet());
        }
        return nodesData;
    }

    private FilterModel getFilterModelFromString(String filterString) {
        FilterModel[] filterModel = null;
        if (filterString != null && !filterString.isEmpty()) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                filterModel = objectMapper.readValue(filterString, FilterModel[].class);
            } catch (IOException e) {
                logger.error(e);
            }
        }

        return (filterModel == null || filterModel.length < 1) ? null : filterModel[0];
    }

    private Collection<Map<String, String>> prepareAggregatedNodeResult(String periodType, Map<Long, String> nodeIds,
                                                                        Map<Long, List<LegacyAggregatedNodeData>> nodesData,
                                                                        String periodQuarterType) {
        SimpleDateFormat format = new SimpleDateFormat(PERIOD_TYPE_MONTH_FORMAT_PATTERN);

        Map<String, Map<String, String>> result = new LinkedHashMap<>();

        for (Map.Entry<Long, List<LegacyAggregatedNodeData>> nodesEntry : nodesData.entrySet()) {
            List<LegacyAggregatedNodeData> data = nodesEntry.getValue();
            String nodeCode = nodeIds.get(nodesEntry.getKey());

            switch (periodType) {
                case PERIOD_TYPE_MONTH:
                    for (LegacyAggregatedNodeData objects : data) {

                        String dateString = format.format(objects.getToDate());
                        String valString = Double.toString(objects.getVal());

                        Map<String, String> model = result.get(dateString);
                        if (model == null) {
                            model = new HashMap<>();
                            model.put("quarter", dateString);
                            result.put(dateString, model);
                        }
                        model.put(nodeCode, valString);
                    }
                    break;
                case PERIOD_TYPE_QUARTER:
                    Calendar calendar = Calendar.getInstance();

                    //<Year,<Q1,Q2,Q3,Q4 -val>>
                    Map<Integer, Map<String, Double>> aggregatedData = new LinkedHashMap<>();

                    for (LegacyAggregatedNodeData objects : data) {

                        calendar.setTime(objects.getToDate());

                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH);
                        String quarterName = getQuarterName(month);

                        Map<String, Double> temp = aggregatedData.computeIfAbsent(year, k -> new LinkedHashMap<>());

                        Double qVal;

                        if (PERIOD_TYPE_QUARTER_NON_CUMULATIVE.equals(periodQuarterType)) {
                            qVal = objects.getVal();
                        } else {
                            qVal = temp.get(quarterName);
                            if (qVal == null) {
                                qVal = objects.getVal();
                            } else {
                                qVal += objects.getVal();
                            }
                        }

                        temp.put(quarterName, qVal);
                    }

                    for (Map.Entry<Integer, Map<String, Double>> e : aggregatedData.entrySet()) {
                        for (Map.Entry<String, Double> valueEntry : e.getValue().entrySet()) {

                            String dateString = valueEntry.getKey() + " " + e.getKey();
                            String valString = Double.toString(valueEntry.getValue());

                            Map<String, String> model = result.get(dateString);

                            if (model == null) {
                                model = new HashMap<>();
                                model.put("quarter", dateString);
                                result.put(dateString, model);
                            }
                            model.put(nodeCode, valString);
                        }
                    }
                    break;
            }
        }

        // order by date
        return result.values()
                .stream()
                .sorted(periodType.equals(PERIOD_TYPE_MONTH) ? monthComparator(format) : quarterComparator())
                .collect(Collectors.toList());
    }

    private String getQuarterName(int month) {
        int quarter = (month / 3) + 1;
        return "Q" + quarter;
    }

    private long getMdtNodeIdByCode(String nodeCode) {
        long nodeId = -1;
        for (MDTNode mdtNode : mdtCacheManager.getMdtNodes()) {
            if (mdtNode.getCode().equalsIgnoreCase(nodeCode)) {
                nodeId = mdtNode.getId();
                break;
            }
        }

        return nodeId;
    }

    private Map<String, Map<String, String>> getDataGroupedByUniqueQuarters(String nodeCode, Map<Integer, Map<String, List<String>>> data) {
        Map<String, Map<String, String>> resultMap = new LinkedHashMap<>();
        for (Map.Entry<Integer, Map<String, List<String>>> e : data.entrySet()) {
            for (Map.Entry<String, List<String>> valueEntry : e.getValue().entrySet()) {

                String dateString = valueEntry.getKey() + " " + e.getKey();
                String valString = concatenateList(valueEntry.getValue());

                Map<String, String> model = resultMap.get(dateString);

                if (model == null) {
                    model = new HashMap<>();
                    model.put("quarter", dateString);
                    resultMap.put(dateString, model);
                }
                model.put(nodeCode, valString);
            }
        }

        return resultMap;
    }

    private Map<Integer, Map<String, List<String>>> groupTextDataByYearAndQuarter(Map<Date, List<String>> data, String periodQuarterType) {
        Calendar calendar = Calendar.getInstance();
        Map<Integer, Map<String, List<String>>> result = new LinkedHashMap<>();

        for (Map.Entry<Date, List<String>> entry : data.entrySet()) {
            Date d = entry.getKey();
            calendar.setTime(d);

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            String quarterName = getQuarterName(month);

            Map<String, List<String>> temp = result.computeIfAbsent(year, k -> new LinkedHashMap<>());

            List<String> qVal;

            if (PERIOD_TYPE_QUARTER_NON_CUMULATIVE.equals(periodQuarterType)) {
                qVal = data.get(d);
            } else {
                qVal = temp.get(quarterName);
                if (qVal == null) {
                    qVal = data.get(d);
                } else {
                    qVal.addAll(data.get(d));
                }
            }

            temp.put(quarterName, qVal);
        }

        return result;
    }

    private String concatenateList(List<String> list) {
        StringBuilder resultBuilder = new StringBuilder();
        if (list != null) {
            for (String s : list) {
                resultBuilder.append(s).append(",");
            }
        }

        if (resultBuilder.length() > 0) {
            resultBuilder.deleteCharAt(resultBuilder.length() - 1);
        }

        return resultBuilder.toString();
    }

    private Comparator<Map<String, String>> monthComparator(DateFormat format) {
        return (t1, t2) -> {
            try {
                Date d1 = format.parse(t1.get("quarter"));
                Date d2 = format.parse(t2.get("quarter"));
                return d1.compareTo(d2);
            } catch (ParseException e) {
                logger.error(e.getMessage(), e);
            }
            return 0;
        };
    }

    private Comparator<Map<String, String>> quarterComparator() {
        return (t1, t2) -> {
            String[] per1 = t1.get("quarter").split(" ");
            String[] per2 = t2.get("quarter").split(" ");
            int per1Year = Integer.parseInt(per1[1]);
            int per2Year = Integer.parseInt(per2[1]);

            return per1Year == per2Year ? per1[0].compareTo(per2[0]) : per1Year - per2Year;
        };
    }

}
