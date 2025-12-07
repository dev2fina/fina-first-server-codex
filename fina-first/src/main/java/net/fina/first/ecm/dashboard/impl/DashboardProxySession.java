package net.fina.first.ecm.dashboard.impl;

import net.fina.common.server.util.CommonUtil;
import net.fina.common.shared.dashboard.ChartContentMetaModel;
import net.fina.ecm.alfresco.api.common.constant.APIConstants;
import net.fina.ecm.alfresco.api.common.parameters.IncludeParam;
import net.fina.ecm.alfresco.api.common.parameters.OrderByParam;
import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import net.fina.ecm.alfresco.api.search.model.ResultNodeRepresentation;
import net.fina.ecm.alfresco.api.search.model.ResultSetRepresentation;
import net.fina.ecm.util.AlfrescoConfiguration;
import net.fina.ecm.util.AlfrescoPropConstants;
import net.fina.first.FirstUtil;
import net.fina.first.ecm.EcmConstants;
import net.fina.first.ecm.client.proxy.EcmClientProxySession;
import net.fina.first.ecm.dashboard.model.FiRegistrationCountCountryMapMetaModel;
import net.fina.first.ecm.dashboard.model.FiRegistryStatusByYearCountMetaModel;
import net.fina.first.ecm.dashboard.model.FiRegistryStatusCountMetaModel;
import net.fina.first.ecm.fi.api.FiLocalEcm;
import net.fina.first.ecm.fi.api.FiTypeLocal;
import net.fina.first.ecm.fi.model.FiRegistryFilterModel;
import net.fina.first.ecm.fi.model.FiTypeMetaModel;
import net.fina.first.ecm.node.api.NodeLocal;
import net.fina.first.ecm.node.api.NodeStatus;
import net.fina.first.ecm.node.model.NodeMetaModel;
import net.fina.first.ecm.node.model.NodeModelHelper;
import net.fina.first.ecm.registry.model.FiRegistryActionType;
import net.fina.first.ecm.search.api.SearchLocal;
import net.fina.first.interceptors.FirstRecordingAuditor;
import org.jboss.logging.Logger;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
@Interceptors(FirstRecordingAuditor.class)
public class DashboardProxySession {

    private final Logger log = Logger.getLogger(getClass().getName());
    private final String PERIOD_TYPE_MONTH_FORMAT_PATTERN = "MM-yyyy";
    private final String PERIOD_TYPE_QUARTER = "Q";
    private final String PERIOD_TYPE_YEAR = "Y";
    private final SimpleDateFormat format = new SimpleDateFormat(PERIOD_TYPE_MONTH_FORMAT_PATTERN);
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(PERIOD_TYPE_MONTH_FORMAT_PATTERN);
    @Inject
    private EcmClientProxySession ecmClientProxySession;
    @Inject
    private FiTypeLocal fiTypeLocal;
    @Inject
    private FiLocalEcm fiLocal;
    @Inject
    private SearchLocal searchLocal;
    @Inject
    private NodeLocal nodeLocal;

    public List<FiRegistryStatusCountMetaModel> loadFiRegistryStatusCount(List<Integer> filterYears) {
        List<FiRegistryStatusCountMetaModel> result = new ArrayList<>();
        if (filterYears == null || filterYears.isEmpty()) {
            return result;
        }

        List<FiTypeMetaModel> fiTypes = fiTypeLocal.loadTypes();
        if (fiTypes != null && !fiTypes.isEmpty()) {

            String fiRegistryRootFolderPath = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.FI_REGISTRY_ROOT_FOLDER_PATH_KEY);

            List<NodeRepresentation> fiRegistryNodes = ecmClientProxySession.getAlfrescoClient().getNodesAPI()
                    .listNodeChildrenCall(APIConstants.FOLDER_ROOT, null, Integer.MAX_VALUE, new OrderByParam(Collections.singletonList("createdAt desc")), null,
                            new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)), fiRegistryRootFolderPath, null, null).getObjects();

            boolean isAllYearsSelected = filterYears.size() == ChronoUnit.YEARS.between(LocalDate.of(1980, 1, 1),
                    LocalDate.now()) + 1;

            if (fiRegistryNodes != null && !fiRegistryNodes.isEmpty()) {

                Map<String, Integer> activeFiRegistriesCountMap = new HashMap<>();
                Map<String, Integer> inactiveFiRegistriesCountMap = new HashMap<>();
                Map<String, Integer> canceledFiRegistriesCountMap = new HashMap<>();


                for (FiTypeMetaModel fiType : fiTypes) {
                    activeFiRegistriesCountMap.put(fiType.getCode(), 0);
                    inactiveFiRegistriesCountMap.put(fiType.getCode(), 0);
                    canceledFiRegistriesCountMap.put(fiType.getCode(), 0);
                }

                for (NodeRepresentation fiRegistryNode : fiRegistryNodes) {
                    String activeStatus = fiRegistryNode.getProperties() != null ? FirstUtil.getValue(fiRegistryNode.getProperties().get(EcmConstants.REGISTRY_PROP_LICENSE_STATUS), String.class) : null;
                    if (activeStatus != null) {

                        Map<String, Object> fiRegistryProperties = fiRegistryNode.getProperties();
                        String legalActDateStringValue = FirstUtil.getValue(fiRegistryProperties.get(EcmConstants.REGISTRY_PROP_ACT_DATE), String.class);

                        String fiTypeCode = FirstUtil.getValue(fiRegistryProperties.get(EcmConstants.REGISTRY_PROP_TYPE_CODE), String.class);
                        switch (activeStatus.trim().toUpperCase()) {
                            case "ACTIVE":
                                initRegistryStatusCountMap(fiRegistryProperties, fiTypeCode, EcmConstants.REGISTRY_PROP_REGISTRATION_DATE, filterYears, activeFiRegistriesCountMap);
                                break;
                            case "INACTIVE":
                                String fiActionType = FirstUtil.getValue(fiRegistryProperties.get(EcmConstants.REGISTRY_PROP_ACTION_TYPE), String.class);
                                Date legalActDate = FirstUtil.getDateValue(legalActDateStringValue, FirstUtil.DATE_FORMAT_LONG_STRING);
                                String inactiveDatePropertyKey = (legalActDate != null ? EcmConstants.REGISTRY_PROP_ACT_DATE : EcmConstants.REGISTRY_PROP_TASK_DATE);


                                if ("CANCELLATION".equalsIgnoreCase(fiActionType)) {
                                    if (!isAllYearsSelected) {
                                        Date registrationDate = FirstUtil.getDateValue(FirstUtil.getValue(fiRegistryProperties.get(EcmConstants.REGISTRY_PROP_REGISTRATION_DATE), String.class), FirstUtil.DATE_FORMAT_LONG_STRING);

                                        if (registrationDate != null) {
                                            initRegistryStatusCountMap(fiRegistryProperties, fiTypeCode, EcmConstants.REGISTRY_PROP_REGISTRATION_DATE, filterYears, activeFiRegistriesCountMap);
                                        }
                                    }
                                    inactiveDatePropertyKey = EcmConstants.REGISTRY_PROP_ACT_DATE;
                                    initRegistryStatusCountMap(fiRegistryProperties, fiTypeCode, inactiveDatePropertyKey, filterYears, canceledFiRegistriesCountMap);
                                } else {
                                    initRegistryStatusCountMap(fiRegistryProperties, fiTypeCode, inactiveDatePropertyKey, filterYears, inactiveFiRegistriesCountMap);
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }

                for (FiTypeMetaModel fiType : fiTypes) {
                    String fiTypeCode = fiType.getCode();
                    int activeCount = activeFiRegistriesCountMap.get(fiTypeCode);
                    int inactiveCount = inactiveFiRegistriesCountMap.get(fiTypeCode);
                    int canceledCount = canceledFiRegistriesCountMap.get(fiTypeCode);
                    result.add(new FiRegistryStatusCountMetaModel(fiTypeCode, activeCount, inactiveCount, canceledCount));
                }

            }
        }


        return result;
    }

    public List<FiRegistryStatusByYearCountMetaModel> loadFiRegistryStatusByYearCount(List<Integer> years) {
        List<FiRegistryStatusByYearCountMetaModel> result = new ArrayList<>();

        if (years != null) {
            for (Integer year : years) {
                List<FiRegistryStatusCountMetaModel> fiRegistryStatusCount = loadFiRegistryStatusCount(Collections.singletonList(year));
                for (FiRegistryStatusCountMetaModel fiRegistryStatusCountMetaModel : fiRegistryStatusCount) {
                    result.add(new FiRegistryStatusByYearCountMetaModel(fiRegistryStatusCountMetaModel, year));
                }
            }
        }

        return result;
    }

    public NodeMetaModel getUserManualDocumentNode() {
        String path = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.USER_MANUAL_DOCUMENT_NODE_PATH_KEY);
        return getNodeMetaModelIfExistsByPath(path);
    }

    public FiRegistrationCountCountryMapMetaModel loadFiRegistrationCountByRegionCountryMap(String acceptLanguage, String fiType, String licenseStatus) {
        FiRegistrationCountCountryMapMetaModel result = new FiRegistrationCountCountryMapMetaModel();

        String path = AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.COUNTRY_MAP_LAYER_NODE_PATH_KEY);
        NodeMetaModel nodeMetaModel = getNodeMetaModelIfExistsByPath(path);
        if (nodeMetaModel != null) {
            result.setCountryMapLayerNodeId(nodeMetaModel.getId());

            // load regions && count registered fi registry
            List<NodeMetaModel> regions = nodeLocal.getNodeChildrenWithClassProperties(acceptLanguage, "-root-", null, null, null, null, new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)), "fina2first/Regional Structure", null, null).getList();

            Map<String, Long> countByRegion = new HashMap<>();
            if (regions != null && !regions.isEmpty()) {

                // count fis
                FiRegistryFilterModel fiRegistryFilterModel = new FiRegistryFilterModel();
                fiRegistryFilterModel.setTypes(Collections.singletonList(fiType));
                fiRegistryFilterModel.setFiRegistryLicenseStatus(licenseStatus);

                for (NodeMetaModel region : regions) {
                    String regionName = FirstUtil.getValue(region.getProperties().get(EcmConstants.REGIONAL_STRUCTURE_REGION_NAME), String.class);
                    fiRegistryFilterModel.setRegionName(regionName);

                    long count = getFiRegistryCountByFilter(fiRegistryFilterModel);
                    countByRegion.put(region.getName(), count);
                }
            }

            result.setCountByRegion(countByRegion);
        }

        return result;
    }

    public long getFiRegistryCountByFilter(FiRegistryFilterModel filter) {
        long result = 0L;

        if (filter != null) {
            String query = fiLocal.filterToQuery(filter);
            result = searchLocal.searchAFTS("*", query, 0, Integer.MAX_VALUE).getCount();
        }

        return result;
    }

    public Collection<Map<String, String>> loadBranchesCountByTypeInOneYear(String type, String periodType) {
        Map<String, Map<String, String>> result = new LinkedHashMap<>();

        List<NodeMetaModel> branches = fiLocal.loadFiBranchesSortedByTypeInOneYear(type);

        for (NodeMetaModel branch : branches) {

            Date date = FirstUtil.getDateValue(
                    FirstUtil.getValue(branch.getProperties().get(EcmConstants.BRANCH_PROP_LEGAL_ACT_DATE), String.class),
                    FirstUtil.DATE_FORMAT_LONG_STRING);

            if (date == null) {
                date = branch.getCreatedAt();
            }

            String period = getPeriodName(date, periodType);

            Map<String, String> model = result.get(period);

            if (model == null) {
                model = new HashMap<>();
                model.put("quarter", period);
                result.put(period, model);
            }

            String tempType = model.computeIfAbsent("value", k -> "0");

            model.put("value", Integer.toString(Integer.parseInt(tempType) + 1));
        }
        return result.values();
    }

    private void initRegistryStatusCountMap(Map<String, Object> fiRegistryProperties, String fiTypeCode, String datePropertyKey, List<Integer> filterYears, Map<String, Integer> countMap) {
        Integer currentCount = countMap.get(fiTypeCode);
        if (filterYears != null && !filterYears.isEmpty()) {
            String dateStringValue = FirstUtil.getValue(fiRegistryProperties.get(datePropertyKey), String.class);
            Date date = FirstUtil.getDateValue(dateStringValue, FirstUtil.DATE_FORMAT_LONG_STRING);

            if (date != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);

                int year = calendar.get(Calendar.YEAR);
                if (filterYears.contains(year)) {
                    countMap.put(fiTypeCode, currentCount + 1);
                }
            }
        } else {
            countMap.put(fiTypeCode, currentCount + 1);
        }
    }

    public Collection<Map<String, String>> getConsolidatedActionsData(String fiType, String actionType, String periodType, Integer periodLimit) {
        Collection<Map<String, String>> res = new ArrayList<>();
        if (fiType != null && !fiType.trim().isEmpty() && actionType != null && !actionType.trim().isEmpty() && periodType != null && !periodType.trim().isEmpty()) {
            FiRegistryFilterModel filterModel = new FiRegistryFilterModel();

            filterModel.setTypes(Collections.singletonList(fiType));

            LocalDate periodStart = getPeriodStartDate(PERIOD_TYPE_YEAR.equals(periodType)
                    ? periodLimit != null ? LocalDate.now().minusYears(periodLimit - 1) : LocalDate.of(1, 1, 1)
                    : LocalDate.now().minusYears(1));

            String datePropertyName = null;
            if (FiRegistryActionType.valueOf(actionType) == FiRegistryActionType.CANCELLATION) {
                datePropertyName = EcmConstants.REGISTRY_PROP_ACT_DATE;
                filterModel.setStatus(Collections.singletonList(NodeStatus.ACCEPTED.getValue()));
                filterModel.setFiRegistryActionType(Collections.singletonList(actionType)); // if fi has been canceled, last action type will be CANCELLATION
                filterModel.setFiRegistryLegalActDateFrom(Date.from(periodStart.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            } else if (FiRegistryActionType.valueOf(actionType) == FiRegistryActionType.REGISTRATION) {
                datePropertyName = EcmConstants.REGISTRY_PROP_REGISTRATION_DATE;
                filterModel.setRegistrationDateFrom(Date.from(periodStart.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            }

            String query = fiLocal.filterToQuery(filterModel);

            ResultSetRepresentation<ResultNodeRepresentation> registryNodes = searchLocal.searchAFTS("*", query, 0, Integer.MAX_VALUE);
            List<ResultNodeRepresentation> resultNodes = registryNodes.getObjects();
            if (resultNodes != null && !resultNodes.isEmpty()) {
                final String sortPropName = datePropertyName == null ? EcmConstants.REGISTRY_PROP_REGISTRATION_DATE : datePropertyName;
                resultNodes.sort((t1, t2) -> {
                    Date d1 = FirstUtil.getDateValue(
                            FirstUtil.getValue(t1.getProperties().get(sortPropName), String.class),
                            FirstUtil.DATE_FORMAT_LONG_STRING);
                    Date d2 = FirstUtil.getDateValue(
                            FirstUtil.getValue(t2.getProperties().get(sortPropName), String.class),
                            FirstUtil.DATE_FORMAT_LONG_STRING);
                    return d1.compareTo(d2);
                });

                res = getDataGroupedByPeriod(resultNodes, periodType, datePropertyName);
            }
        }

        return res;
    }

    public ChartContentMetaModel getChartContent(String fileName, String base64ChartContent) {
        return CommonUtil.getChartContent(fileName, base64ChartContent);
    }

    private Collection<Map<String, String>> getDataGroupedByPeriod(List<ResultNodeRepresentation> nodes, String periodType, String datePropertyName) {
        Map<String, Map<String, String>> res = new LinkedHashMap<>();
        if (nodes != null && !nodes.isEmpty() && periodType != null && !periodType.trim().isEmpty() && datePropertyName != null && !datePropertyName.trim().isEmpty()) {
            for (ResultNodeRepresentation nodeRepresentation : nodes) {
                String periodName = getPeriodName(FirstUtil.getDateValue(nodeRepresentation.getProperties().get(datePropertyName).toString(), FirstUtil.DATE_FORMAT_LONG_STRING), periodType);

                res.computeIfAbsent(periodName, k -> {
                    Map<String, String> tmp = new HashMap<>();
                    tmp.put("periodName", periodName);
                    tmp.put("value", Integer.toString(0));

                    return tmp;
                });

                Map<String, String> curValueObj = res.get(periodName);
                int newValue = Integer.parseInt(curValueObj.get("value")) + 1;
                curValueObj.put("value", Integer.toString(newValue));

                res.put(periodName, curValueObj);
            }
        }
        return res.values();
    }

    private String getPeriodName(Date dateValue, String periodType) {
        if (PERIOD_TYPE_YEAR.equals(periodType)) {
            Calendar c = Calendar.getInstance();
            c.setTime(dateValue);
            return "" + c.get(Calendar.YEAR);
        } else if (PERIOD_TYPE_QUARTER.equals(periodType)) {
            Calendar c = Calendar.getInstance();
            c.setTime(dateValue);
            int quarter = (c.get(Calendar.MONTH) / 3) + 1;
            return "Q" + quarter + "-" + c.get(Calendar.YEAR);
        } else {
            return this.format.format(dateValue);
        }
    }

    private LocalDate getPeriodStartDate(LocalDate date) {
        return date.with(date.getMonth().firstMonthOfQuarter()).with(TemporalAdjusters.firstDayOfMonth());
    }

    public Collection<Map<String, String>> loadBranchesRegionCountByType(String type) {
        List<NodeMetaModel> branches = fiLocal.loadFiBranchesByType(type, AlfrescoPropConstants.EXISTING_BRANCH_SUBDIVISION_FI_TYPES);
        return convertBranchesRegion(branches);
    }


    public Collection<Map<String, String>> loadHeadOfficesRegionCountByType(String type) {
        List<NodeMetaModel> branches = fiLocal.loadFiBranchesByType(type, AlfrescoPropConstants.EXISTING_BRANCH_HEAD_OFFICE_FI_TYPES);
        return convertBranchesRegion(branches);
    }

    private Collection<Map<String, String>> convertBranchesRegion(List<NodeMetaModel> branches) {
        Map<String, Integer> regionsCount = new HashMap<>();

        for (NodeMetaModel branch : branches) {
            String region = FirstUtil.getValue(branch.getProperties().get(EcmConstants.BRANCH_PROP_ADDRESS_REGION), String.class);

            Integer count = regionsCount.get(region);
            if (count == null) {
                count = 0;
            }

            count++;

            regionsCount.put(region, count);
        }

        Collection<Map<String, String>> result = new ArrayList<>();

        for (Map.Entry<String, Integer> e : regionsCount.entrySet()) {
            Map<String, String> m = new HashMap<>();

            m.put("region", e.getKey());
            m.put("value", Integer.toString(e.getValue()));

            result.add(m);
        }

        return result;
    }

    private NodeMetaModel getNodeMetaModelIfExistsByPath(String nodePath) {
        NodeMetaModel result = null;

        NodeRepresentation nodeRepresentation = ecmClientProxySession.getAlfrescoClient().getNodesAPI().getNodeCall(APIConstants.FOLDER_ROOT, null, nodePath, null);
        if (nodeRepresentation != null) {
            result = NodeModelHelper.getMetaModel(nodeRepresentation);
        }

        return result;
    }

    public Collection<Map<String, String>> loadBranchesCountByTypeOrderedByYear(String type) {
        Map<String, Map<String, String>> result = new LinkedHashMap<>();
        Calendar calendar = Calendar.getInstance();

        List<NodeMetaModel> branches = fiLocal.loadFiBranchesByType(type, AlfrescoPropConstants.EXISTING_BRANCH_SUBDIVISION_FI_TYPES);

        for (NodeMetaModel branch : branches) {

            Date date = FirstUtil.getDateValue(
                    FirstUtil.getValue(branch.getProperties().get(EcmConstants.BRANCH_PROP_LEGAL_ACT_DATE), String.class),
                    FirstUtil.DATE_FORMAT_LONG_STRING);

            if (date == null) {
                date = branch.getCreatedAt();
            }

            calendar.setTime(date);
            String period = Integer.toString(calendar.get(Calendar.YEAR));

            Map<String, String> model = result.get(period);

            if (model == null) {
                model = new HashMap<>();
                model.put("quarter", period);
                result.put(period, model);
            }

            String tempType = model.computeIfAbsent("value", k -> "0");

            model.put("value", Integer.toString(Integer.parseInt(tempType) + 1));
        }

        return result.values()
                .stream().sorted(Comparator.comparingInt(n -> Integer.parseInt(n.get("quarter"))))
                .collect(Collectors.toList());
    }

    public Collection<Map<String, String>> getActiveFiCount(String fiType, String periodType) {
        List<Map<String, String>> res = new ArrayList<>();
        if (fiType != null && !fiType.trim().isEmpty() && periodType != null && !periodType.trim().isEmpty()) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate periodStart = getPeriodStartDate(LocalDate.now().minusYears(1));
            LocalDate periodEnd = LocalDate.now();

            String query = "TYPE:'" + EcmConstants.REGISTRY_TYPE_REGISTRY + "' and " + EcmConstants.REGISTRY_PROP_TYPE_CODE + ":" + fiType
                    + "' and ISNOTNULL:'" + EcmConstants.REGISTRY_PROP_REGISTRATION_DATE + "' and (!" + EcmConstants.REGISTRY_PROP_ACTION_TYPE + ":'CANCELLATION' or " +
                    "(" + EcmConstants.REGISTRY_PROP_ACTION_TYPE + ":'CANCELLATION' and " +
                    EcmConstants.REGISTRY_PROP_ACT_DATE + ":['" + fmt.format(periodStart) + "' TO '" + fmt.format(periodEnd) + "']))";

            ResultSetRepresentation<ResultNodeRepresentation> registryNodes = searchLocal.searchAFTS("*", query, 0, Integer.MAX_VALUE);
            List<ResultNodeRepresentation> resultNodes = registryNodes.getObjects();
            if (resultNodes != null && !resultNodes.isEmpty()) {
                Map<Integer, Integer> data = new HashMap<>();

                for (ResultNodeRepresentation registry : resultNodes) {
                    String lastAction = FirstUtil.getValue(registry.getProperties().get(EcmConstants.REGISTRY_PROP_ACTION_TYPE), String.class);
                    LocalDate registrationDate = FirstUtil.getLocalDateValue(
                            FirstUtil.getValue(registry.getProperties().get(EcmConstants.REGISTRY_PROP_REGISTRATION_DATE), String.class),
                            FirstUtil.DATE_FORMAT_LONG_STRING);
                    LocalDate cancellationDate = "CANCELLATION".equals(lastAction)
                            ? FirstUtil.getLocalDateValue(FirstUtil.getValue(registry.getProperties().get(EcmConstants.REGISTRY_PROP_ACT_DATE), String.class),
                            FirstUtil.DATE_FORMAT_LONG_STRING) : null;

                    int curMonth = LocalDate.now().getMonthValue() - 1;
                    if (PERIOD_TYPE_QUARTER.equals(periodType)) {
                        // quarters
                        int quarterStart = curMonth / 3 * 3 + 3;
                        for (int i = 0, m = (quarterStart) % 12, y = LocalDate.now().getYear() - (1 - quarterStart / 12);
                             i < 12; y += (m + 3) / 12, m = (m + 3) % 12, i += 3) {

                            LocalDate start = LocalDate.of(y, m + 1, 1);
                            LocalDate end = start.plusMonths(3).minusDays(1);
                            addActiveFi(data, periodType, start, end, registrationDate, cancellationDate);
                        }
                    } else {
                        // months
                        for (int i = 0, m = (curMonth + 1) % 12, y = LocalDate.now().getYear() - (1 - curMonth / 11);
                             i < 12; y += (m + 1) / 12, m = (m + 1) % 12, i++) {

                            LocalDate start = LocalDate.of(y, m + 1, 1);
                            LocalDate end = start.plusMonths(1).minusDays(1);
                            addActiveFi(data, periodType, start, end, registrationDate, cancellationDate);
                        }
                    }
                }

                res = data.entrySet().stream().sorted(Map.Entry.comparingByKey()).map(item -> {
                    String periodName = PERIOD_TYPE_QUARTER.equalsIgnoreCase(periodType)
                            ? "Q" + (item.getKey() % 100) + "-" + item.getKey() / 100
                            : dateTimeFormatter.format(LocalDate.of(item.getKey() / 100, item.getKey() % 100, 1));

                    return new HashMap<String, String>() {{
                        put("periodName", periodName);
                        put("value", item.getValue().toString());
                    }};
                }).collect(Collectors.toList());
            }
        }

        return res;
    }

    private void addActiveFi(Map<Integer, Integer> data, String periodType, LocalDate startPeriod,
                             LocalDate endPeriod, LocalDate registrationDate, LocalDate cancellationDate) {

        if ((cancellationDate == null && (registrationDate.isBefore(endPeriod) || registrationDate.isEqual(endPeriod)))
                || (cancellationDate != null && cancellationDate.isAfter(startPeriod))) {

            Integer periodId = startPeriod.getYear() * 100 + (PERIOD_TYPE_QUARTER.equalsIgnoreCase(periodType)
                    ? (startPeriod.getMonthValue() / 3 + 1) : startPeriod.getMonthValue());

            data.put(periodId, data.getOrDefault(periodId, 0) + 1);
        }
    }

}
