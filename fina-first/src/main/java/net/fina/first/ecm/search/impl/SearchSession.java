package net.fina.first.ecm.search.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.fina.common.shared.FilterField;
import net.fina.common.shared.FilterType;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.common.shared.SortField;
import net.fina.ecm.alfresco.AlfrescoClient;
import net.fina.ecm.alfresco.api.common.constant.APIConstants;
import net.fina.ecm.alfresco.api.common.constant.ContentModel;
import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import net.fina.ecm.alfresco.api.core.model.representation.PathElementRepresentation;
import net.fina.ecm.alfresco.api.search.body.*;
import net.fina.ecm.alfresco.api.search.model.ResultNodeRepresentation;
import net.fina.ecm.alfresco.api.search.model.ResultSetRepresentation;
import net.fina.ecm.util.AlfrescoConfiguration;
import net.fina.ecm.util.AlfrescoPropConstants;
import net.fina.ecm.util.AlfrescoUtil;
import net.fina.first.FirstUtil;
import net.fina.first.ecm.EcmConstants;
import net.fina.first.ecm.client.proxy.EcmClientProxySession;
import net.fina.first.ecm.dictionary.api.DictionaryLocal;
import net.fina.first.ecm.node.model.NodeMetaModel;
import net.fina.first.ecm.node.model.NodeModelHelper;
import net.fina.first.ecm.search.api.SearchLocal;
import net.fina.first.ecm.search.api.SearchPropertyWildcardCondition;
import net.fina.first.interceptors.FirstRecordingAuditor;
import org.apache.commons.collections4.ListUtils;
import org.jboss.logging.Logger;

import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
@Local(SearchLocal.class)
@Interceptors(FirstRecordingAuditor.class)
public class SearchSession implements SearchLocal {
    @Inject
    private EcmClientProxySession ecmClientProxySession;

    @Inject
    private DictionaryLocal dictionaryProxySession;

    private final Logger log = Logger.getLogger(getClass().getName());

    @Override
    public ResultSetRepresentation<ResultNodeRepresentation> searchRecentFiles(Integer start, Integer limit, String orderBy) {
        QueryBody queryBody = new QueryBody();
        RequestQuery query = new RequestQuery();
        query.setLanguage(RequestQuery.LanguageEnum.AFTS);
        query.setQuery("*");

        queryBody.setQuery(query);

        RequestSortDefinition sort = new RequestSortDefinition();
        sort.setType(RequestSortDefinition.TypeEnum.FIELD);
        sort.setField("cm:modified");

        queryBody.setSort(Collections.singletonList(sort));

        RequestPagination pagination = new RequestPagination();
        pagination.setMaxItems((orderBy != null && !orderBy.isEmpty()) ? Integer.MAX_VALUE : limit);
        pagination.setSkipCount((orderBy != null && !orderBy.isEmpty()) ? 0 : start);

        queryBody.setPaging(pagination);
        List<String> include = Arrays.asList("properties", "path");

        queryBody.setInclude(include);

        //TODO generate query dynamically
        queryBody.setFilterQueries(Arrays.asList(new RequestFilterQuery().query("cm:modified:[NOW/DAY-30DAYS TO NOW/DAY+1DAY]"),
                new RequestFilterQuery().query("cm:modifier:admin OR cm:creator:admin"),
                new RequestFilterQuery().query("TYPE:\"content\" AND -PNAME:\"0/wiki\" AND -TYPE:\"app:filelink\" AND -TYPE:\"fm:post\" "
                        + "AND -TYPE:\"cm:thumbnail\" AND -TYPE:\"cm:failedThumbnail\" AND -TYPE:\"cm:rating\""
                        + " AND -TYPE:\"dl:dataList\" AND -TYPE:\"dl:todoList\" AND -TYPE:\"dl:issue\""
                        + " AND -TYPE:\"dl:contact\" AND -TYPE:\"dl:eventAgenda\" AND -TYPE:\"dl:event\""
                        + " AND -TYPE:\"dl:task\" AND -TYPE:\"dl:simpletask\" AND -TYPE:\"dl:meetingAgenda\""
                        + " AND -TYPE:\"dl:location\" AND -TYPE:\"fm:topic\" AND -TYPE:\"fm:post\" AND -TYPE:\"ia:calendarEvent\" AND -TYPE:\"lnk:link\""))
        );

        ResultSetRepresentation<ResultNodeRepresentation> queryResult = ecmClientProxySession.getAlfrescoClient().getSearchAPI().search(queryBody);
        if (orderBy != null && !orderBy.isEmpty()) {
            List<ResultNodeRepresentation> orderedResults = FirstUtil.getPaginatedList(getOrderedResults(queryResult.getObjects(), orderBy), start, limit);
            queryResult.setObjects(orderedResults);
        }
        return queryResult;
    }

    private List<ResultNodeRepresentation> getOrderedResults(List<ResultNodeRepresentation> list, String orderBy) {
        if (list == null || list.isEmpty()) {
            return list;
        }

        List<ResultNodeRepresentation> result;
        String[] orderByParts = orderBy.split(" ");
        String orderByField = orderByParts[0];
        String orderByDirection = orderByParts.length > 1 ? orderByParts[1] : "ASC";
        boolean isAscending = orderByDirection.toUpperCase().equals("ASC");
        result = list.stream().sorted((rnr1, rnr2) -> {
            int cmp = compareFields(rnr1, rnr2, orderByField);
            return isAscending ? cmp : -cmp;
        }).collect(Collectors.toList());

        return result;
    }

    private int compareFields(ResultNodeRepresentation nodeRepresentation1, ResultNodeRepresentation nodeRepresentation2, String orderByField) {
        int res = 0;
        switch (orderByField) {
            case "name":
                res = FirstUtil.safeAlphabeticStringCmp(nodeRepresentation1.getName(), nodeRepresentation2.getName());
                break;
            case "size":
                long size1 = nodeRepresentation1.getContent() == null ? 0 : nodeRepresentation1.getContent().getSizeInBytes();
                long size2 = nodeRepresentation2.getContent() == null ? 0 : nodeRepresentation2.getContent().getSizeInBytes();
                res = size1 > size2 ? 1 : -1;
                break;
            case "modifiedAt":
                Date d1 = nodeRepresentation1.getModifiedAt();
                Date d2 = nodeRepresentation2.getModifiedAt();
                res = FirstUtil.safeDateCmp(d1, d2);
                break;
            case "location":
                String location1 = FirstUtil.getNodeLocationText(nodeRepresentation1);
                String location2 = FirstUtil.getNodeLocationText(nodeRepresentation2);
                res = FirstUtil.safeAlphabeticStringCmp(location1, location2);
                break;
            default:
                break;
        }

        return res;
    }

    @Override
    public ResultSetRepresentation<ResultNodeRepresentation> searchAFTS(String acceptLanguage, String query, int start, int limit) {
        QueryBody queryBody = getAFTSQuery(query, start, limit, Collections.singletonList(new RequestSortDefinition().field("createdAt").ascending(false)), Arrays.asList(APIConstants.PROPERTIES_VALUE, ContentModel.PROP_PATH), null);
        return ecmClientProxySession.getAlfrescoClient(acceptLanguage).getSearchAPI().search(queryBody);
    }

    @Override
    public ResultSetRepresentation<ResultNodeRepresentation> searchAFTS(String acceptLanguage, String query, int start, int limit, List<RequestSortDefinition> sort, String... filterQueries) {
        QueryBody queryBody = getAFTSQuery(query, start, limit, sort, Arrays.asList(APIConstants.PROPERTIES_VALUE, ContentModel.PROP_PATH), filterQueries);
        return ecmClientProxySession.getAlfrescoClient(acceptLanguage).getSearchAPI().search(queryBody);
    }


    @Override
    public PaginatedListWrapper<NodeMetaModel> searchNodes(String acceptLanguage, String query, int start, int limit) {
        PaginatedListWrapper<NodeMetaModel> result = new PaginatedListWrapper<>();
        if (query != null) {
            QueryBody queryBody = getAFTSQuery(query, start, limit, Collections.singletonList(new RequestSortDefinition().field("createdAt").ascending(false)), Arrays.asList(APIConstants.PROPERTIES_VALUE, ContentModel.PROP_PATH), "+TYPE:'cm:folder' OR +TYPE:'cm:content'");


            ResultSetRepresentation<ResultNodeRepresentation> searchResult = ecmClientProxySession.getAlfrescoClient(acceptLanguage).getSearchAPI().search(queryBody);

            TreeSet<String> customModelPrefixes = new TreeSet<>(Arrays.asList(AlfrescoConfiguration.get().getAlfrescoArrayProperty(AlfrescoPropConstants.MODEL_GRID_CHECK_PREFIXES_KEY)));

            List<NodeMetaModel> nodeMetaModels = new ArrayList<>();

            searchResult.getObjects().forEach(node -> {
                NodeMetaModel model = NodeModelHelper.getMetaModel(node);
                if (customModelPrefixes.contains(model.getNodeType().split(":")[0])) {
                    model.setClassProperties(dictionaryProxySession.getClassProperties(acceptLanguage, model.getNodeType().replace(":", "_")));
                }
                nodeMetaModels.add(model);
            });

            result.setList(nodeMetaModels);
            result.setTotalResults(searchResult.getPagination().getTotalItems());
        }

        return result;
    }

    @Override
    public PaginatedListWrapper<NodeMetaModel> searchNodes(String acceptLanguage, String query, int start, int limit,
                                                           List<RequestSortDefinition> sort, String... filterQueries) {
        PaginatedListWrapper<NodeMetaModel> result = new PaginatedListWrapper<>();
        if (query != null) {
            sort = sort == null || sort.isEmpty()
                    ? Collections.singletonList(new RequestSortDefinition().field("createdAt").ascending(false)) : sort;
            QueryBody queryBody = getAFTSQuery(query, start, limit, sort,
                    Arrays.asList(APIConstants.PROPERTIES_VALUE, ContentModel.PROP_PATH), filterQueries);

            ResultSetRepresentation<ResultNodeRepresentation> searchResult =
                    ecmClientProxySession.getAlfrescoClient(acceptLanguage).getSearchAPI().search(queryBody);
            result.setList(NodeModelHelper.getMetaModels(searchResult.getObjects()));
            result.setTotalResults(searchResult.getPagination().getTotalItems());
        }

        return result;
    }

    @Override
    public PaginatedListWrapper<ResultNodeRepresentation> searchUsersAndGroups(String acceptLanguage, String filter, int start, int limit) {
        String queryTemplate = "authorityName:\"*{0}*\" OR userName:\"*{0}*\"";

        QueryBody queryBody = getAFTSQuery(MessageFormat.format(queryTemplate, filter), start, limit,
                Collections.singletonList(new RequestSortDefinition().field("createdAt").ascending(false)),
                Arrays.asList(APIConstants.PROPERTIES_VALUE, ContentModel.PROP_PATH, "association"),
                "TYPE:'cm:authority'", "-cm:authorityName:'GROUP_site_*'");

        ResultSetRepresentation<ResultNodeRepresentation> searchResult = ecmClientProxySession.getAlfrescoClient(acceptLanguage).getSearchAPI().search(queryBody);

        if (filter != null && "GROUP_EVERYONE".contains(filter.toUpperCase())) {

            ResultNodeRepresentation groupEveryOne = new ResultNodeRepresentation();
            groupEveryOne.setId("GROUP_EVERYONE");
            groupEveryOne.setName("GROUP_EVERYONE");
            groupEveryOne.setNodeType("cm:authorityContainer");
            Map<String, Object> props = new HashMap<>();
            props.put("cm:authorityName", "GROUP_EVERYONE");
            groupEveryOne.setProperties(props);
            searchResult.getObjects().add(groupEveryOne);
            searchResult.getPagination().setTotalItems(searchResult.getPagination().getTotalItems() + 1);
        }

        return getPaging(searchResult);
    }


    @Override
    public PaginatedListWrapper<ResultNodeRepresentation> searchLinkedInformation(String acceptLanguage, String fiRegistryId, int start, int limit, String filter, String filterJson) {
        String customSearchTpl = "((fina:fiPersonFirstName:\"{0}\" AND fina:fiPersonLastName:\"{1}\") OR fina:fiPersonPersonalNumber:\"{2}\")";
        String registrySearchTpl = "(fina:fiRegistryIdentity:\"{0}\")";
        StringBuilder queryTemplate = new StringBuilder();
        NodeRepresentation fiRegistry = ecmClientProxySession.getAlfrescoClient(acceptLanguage).getNodesAPI().getNodeCall(fiRegistryId);

        queryTemplate.append(MessageFormat.format(registrySearchTpl, fiRegistry.getProperties().get("fina:fiRegistryIdentity")));

        QueryBody beneficiariesAndAuthpersons = getAFTSQuery("*", 0, 1000, null, Arrays.asList(APIConstants.PROPERTIES_VALUE, ContentModel.PROP_PATH), "+PATH:'//cm:" + AlfrescoUtil.getISO9075String(fiRegistry.getName()) + "//*'", "+TYPE:'fina:fiComplexStructure' OR +TYPE:'nbg:fiAuthorizedPerson'");
        ResultSetRepresentation<ResultNodeRepresentation> currentBeneficiariesAndAuthorizedPersons = ecmClientProxySession.getAlfrescoClient(acceptLanguage).getSearchAPI().search(beneficiariesAndAuthpersons);

        if (!currentBeneficiariesAndAuthorizedPersons.getObjects().isEmpty()) {
            currentBeneficiariesAndAuthorizedPersons.getObjects().forEach(node -> {
                queryTemplate.append(" OR ")
                        .append(MessageFormat.format(customSearchTpl, node.getProperties().get("fina:fiPersonFirstName"), node.getProperties().get("fina:fiPersonLastName"), node.getProperties().get("fina:fiPersonPersonalNumber")));
            });
        }

        if (filterJson != null) {
            queryTemplate.reverse().append('(').reverse().append(')');
            List<FilterField> filterFields = getFilterFields(filterJson);

            filterFields.forEach(field -> {
                if (field.getOperator().equals("!=")) {
                    queryTemplate.append(" AND ")
                            .append(field.getProperty())
                            .append(":(!\"")
                            .append(field.getValue())
                            .append("\")");
                }
            });
        }

        QueryBody queryBody = getAFTSQuery(queryTemplate.toString(), start, limit,
                Collections.singletonList(new RequestSortDefinition().field("cm:name")),
                Arrays.asList(APIConstants.PROPERTIES_VALUE, ContentModel.PROP_PATH),
                "-PATH:'//cm:" + AlfrescoUtil.getISO9075String(fiRegistry.getName()) + "//*'",
                "-cm:name:'" + fiRegistry.getName() + "'",
                "+TYPE:'fina:fiBeneficiary' OR +TYPE:'fina:fiAuthorizedPerson' OR +TYPE:'fina:fiRegistry' OR +TYPE:'fina:fiComplexStructure' ",
                filter != null && !filter.trim().isEmpty() ? MessageFormat.format("(fina:fiRegistryIdentity:\"*{0}*\" OR fina:fiRegistryCode:\"*{0}*\" OR fina:fiRegistryLegalActNumber:\"*{0}*\") OR (fina:fiPersonFirstName:\"*{0}*\" OR fina:fiPersonLastName:\"*{0}*\" OR fina:fiPersonPersonalNumber:\"*{0}*\")", filter) : null);

        ResultSetRepresentation<ResultNodeRepresentation> searchResult = ecmClientProxySession.getAlfrescoClient(acceptLanguage).getSearchAPI().search(queryBody);

        Set<String> fiRegistryDataTypes = new TreeSet<>(Arrays.asList(AlfrescoConfiguration.get().getAlfrescoArrayProperty(AlfrescoPropConstants.DATA_TYPE_FI_REGISTRIES_KEY)));
        searchResult.getObjects().forEach(node -> {
            if (!fiRegistryDataTypes.contains(node.getNodeType())) {
                node.getProperties().put(EcmConstants.REGISTRY_TYPE_REGISTRY, getRegistryNodeFromPath(node, acceptLanguage));
            }
        });

        return getPaging(searchResult);
    }

    @Override
    public PaginatedListWrapper<ResultNodeRepresentation> searchByText(String searchStr, String filterStr) {
        RequestQuery query = new RequestQuery().query(searchStr).language(RequestQuery.LanguageEnum.AFTS);
        QueryBody queryBody = new QueryBody().query(query).include(Arrays.asList("properties", "path", "association"))
                .filterQueries(Arrays.asList(
                        new RequestFilterQuery().query(filterStr)
                ));

        ResultSetRepresentation<ResultNodeRepresentation> resultSearch = ecmClientProxySession.getAlfrescoClient().getSearchAPI().search(queryBody);
        return getPaging(resultSearch);
    }

    @Override
    public PaginatedListWrapper<ResultNodeRepresentation> searchByCMIS(String searchStr) {
        RequestQuery query = new RequestQuery().query(searchStr).language(RequestQuery.LanguageEnum.CMIS);
        QueryBody queryBody = new QueryBody().query(query).include(Arrays.asList("properties", "path", "association"));

        ResultSetRepresentation<ResultNodeRepresentation> resultSearch = ecmClientProxySession.getAlfrescoClient().getSearchAPI().search(queryBody);
        return getPaging(resultSearch);
    }

    @Override
    public ResultSetRepresentation<ResultNodeRepresentation> searchByCMIS(String searchStr, int page, int start, int limit) {
        RequestQuery query = new RequestQuery().query(searchStr).language(RequestQuery.LanguageEnum.CMIS);
        QueryBody queryBody = new QueryBody()
                .query(query)
                .paging(new RequestPagination().skipCount(start).maxItems(APIConstants.CMIS_MAX_PAGE_SIZE))
                .include(Arrays.asList("properties", "path", "association"));

        ResultSetRepresentation<ResultNodeRepresentation> resultSearch = ecmClientProxySession.getAlfrescoClient().getSearchAPI().search(queryBody);
        resultSearch.getPagination().setSkipCount(start);
        resultSearch.getPagination().setMaxItems(limit);
        resultSearch.getPagination().setTotalItems(resultSearch.getPagination().getTotalItems());
        if (resultSearch.getObjects().size() < limit) {
            resultSearch.setObjects(resultSearch.getObjects());
        } else {
            resultSearch.setObjects(resultSearch.getObjects().subList(0, limit));
        }
        return resultSearch;
    }

    @Override
    public PaginatedListWrapper<ResultNodeRepresentation> getFilteredNodes(String parentId, String nodeType, String filterJson) {
        StringBuilder filterQuery = new StringBuilder("select * from " + nodeType + " where cmis:parentId='" + parentId + "' ");

        String filterString = getCMISFilterString(filterJson);
        if (!filterString.isEmpty()) {
            filterQuery.append("and ").append(filterString);
        }

        return searchByCMIS(filterQuery.toString());
    }

    @Override
    public String getCMISFilterString(String filterJson) {
        StringBuilder filterQuery = new StringBuilder();
        List<FilterField> filterFields = getFilterFields(filterJson);
        for (int i = 0; i < filterFields.size(); i++) {
            FilterField field = filterFields.get(i);

            String operator = validateOperator(field.getOperator());
            if (field.getValue() instanceof List) {
                filterQuery.append(getMultipleValuesQuery(field, "in".equals(operator)));
            } else {
                boolean exactMatch = field.getValue() instanceof Boolean || field.getValue() instanceof Date
                        || (field.getType() != null && (field.getType().equals(FilterType.DATE) || field.getType().equals(FilterType.BOOLEAN)));
                filterQuery.append(field.getProperty())
                        .append(operator == null ? " =" : " " + operator)
                        .append(" '")
                        .append(exactMatch ? "" : "%")
                        .append(field.getValue())
                        .append(exactMatch ? "" : "%")
                        .append("' ");
            }

            if (i < filterFields.size() - 1) {
                filterQuery.append(" and ");
            }
        }

        return filterQuery.toString();
    }

    @Override
    public String getAFTSFilterString(String filterJson) {
        List<String> filterStrings = new ArrayList<>();
        List<FilterField> filterFields = getFilterFields(filterJson);

        for (FilterField filter : filterFields) {
            StringBuilder sb = new StringBuilder();
            if (filter.getType() == null) {
                filter.setType(FilterType.STRING);
            }
            switch (filter.getType()) {
                case DATE: {
                    sb.append(filter.getProperty()).append(":");
                    switch (filter.getOperator()) {
                        case "lt": {
                            sb.append("[MIN TO \"").append(filter.getValue()).append("\"]");
                            break;
                        }
                        case "gt": {
                            sb.append("[\"").append(filter.getValue()).append("\" TO MAX]");
                            break;
                        }
                        case "eq":
                        default: {
                            sb.append("\"").append(filter.getValue()).append("\"");
                        }
                    }
                    break;
                }
                case LIST: {
                    List<Object> values = (List<Object>) filter.getValue();
                    if (values.size() > 0) {
                        sb.append("(").append(filter.getProperty()).append(":");
                        for (int i = 0; i < values.size(); i++) {
                            if (i > 0) {
                                sb.append(" OR ").append(filter.getProperty()).append(":");
                            }
                            sb.append("\"").append(values.get(i)).append("\"");
                        }
                        sb.append(")");
                    }
                    break;
                }
                default: {
                    sb.append(filter.getProperty()).append(":\"*").append(filter.getValue()).append("*\"");
                }
            }
            filterStrings.add(sb.toString());
        }

        return filterStrings.stream().reduce((s, s2) -> s + " and " + s2).orElse("");
    }

    private String getMultipleValuesQuery(FilterField field, boolean equals) {
        StringBuilder sb = new StringBuilder();
        String operator = equals ? "=" : "!=";
        String conjunction = equals ? " or " : " and ";
        ArrayList<Object> possibleValues = (ArrayList) field.getValue();

        if (possibleValues != null && possibleValues.size() > 0) {
            sb.append("(");
            for (int i = 0; i < possibleValues.size(); i++) {
                String value = (String) possibleValues.get(i);
                if (i > 0) {
                    sb.append(conjunction);
                }
                sb.append(field.getProperty())
                        .append(operator)
                        .append(" '")
                        .append(value)
                        .append("' ");
            }
            sb.append(")");
        }

        return sb.toString();
    }

    private List<FilterField> getFilterFields(String filterJson) {
        if (filterJson != null) {
            try {
                FilterField[] filterFields = new ObjectMapper().readValue(filterJson, FilterField[].class);
                return Arrays.asList(filterFields);
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
            }
        }

        return new ArrayList<>();
    }

    private NodeRepresentation getRegistryNodeFromPath(ResultNodeRepresentation node, String acceptLanguage) {
        for (int i = 0; i < node.getPath().getElement().size(); i++) {
            PathElementRepresentation pe = node.getPath().getElement().get(i);
            if (pe.getName().equalsIgnoreCase("Registry")) {
                return ecmClientProxySession.getAlfrescoClient(acceptLanguage).getNodesAPI().getNodeCall(node.getPath().getElement().get(i + 1).getId());
            }
        }

        return null;
    }

    @Override
    public QueryBody getAFTSQuery(String query, int start, int limit, List<RequestSortDefinition> sortDefinitions, List<String> includeParams, String... filterQueries) {
        RequestQuery requestQuery = new RequestQuery().query(query).language(RequestQuery.LanguageEnum.AFTS);
        QueryBody queryBody = new QueryBody()
                .query(requestQuery)
                .include(includeParams != null && includeParams.isEmpty() ? Arrays.asList(APIConstants.PROPERTIES_VALUE, ContentModel.PROP_PATH) : includeParams)
                .sort(sortDefinitions != null && sortDefinitions.isEmpty() ? Collections.singletonList(new RequestSortDefinition().field("createdAt").ascending(false)) : sortDefinitions);

        if (limit > 0) {
            queryBody = queryBody.paging(new RequestPagination().maxItems(limit).skipCount(start));
        }

        List<RequestFilterQuery> requestFilterQueries = new ArrayList<>();
        if (filterQueries != null) {
            for (String q : filterQueries) {
                if (q != null && !q.trim().isEmpty()) {
                    requestFilterQueries.add(new RequestFilterQuery().query(q));
                }
            }
        }

        if (!requestFilterQueries.isEmpty()) {
            queryBody.filterQueries(requestFilterQueries);
        }

        return queryBody;
    }

    private PaginatedListWrapper<ResultNodeRepresentation> getPaging(ResultSetRepresentation<ResultNodeRepresentation> searchResult) {
        PaginatedListWrapper<ResultNodeRepresentation> result = new PaginatedListWrapper<>();
        result.setList(searchResult.getObjects());
        result.setTotalResults(searchResult.getPagination().getTotalItems());

        return result;
    }

    @Override
    public PaginatedListWrapper<NodeMetaModel> searchNodesByProperties(String acceptLanguage, String query, String searchByProps, String distinctProps, String fiRegistryCode, String relativePath, Boolean exactMatch, Integer start, Integer limit, SearchPropertyWildcardCondition searchPropertyWildcardCondition) {
        String searchString = "";
        query = query == null || query.isEmpty() ? "*" : query;

        if (searchByProps != null) {

            if (exactMatch != null && exactMatch) {
                searchPropertyWildcardCondition = searchPropertyWildcardCondition.EXACT_MATCH;
            }
            searchPropertyWildcardCondition = searchPropertyWildcardCondition != null ? searchPropertyWildcardCondition : searchPropertyWildcardCondition.ANY_MATCH;

            StringBuilder sb = new StringBuilder();
            String[] props = searchByProps.split(",");

            for (String prop : props) {
                sb.append(prop)
                        .append(": \"")
                        .append(searchPropertyWildcardCondition.getCondition())
                        .append("\" OR ");
            }

            searchString = sb.toString();

            searchString = searchString.substring(0, searchString.length() - 3);
            searchString = MessageFormat.format("(" + searchString + ")", query);
        }

        if (fiRegistryCode != null) {
            String path = "PATH:'//cm:Registry/cm:" + AlfrescoUtil.getISO9075String(fiRegistryCode) + (relativePath != null ? "/cm:" + AlfrescoUtil.getISO9075String(relativePath) : "") + "/*'";
            searchString = "(" + path + " AND " + searchString + ")";
        }

        PaginatedListWrapper<NodeMetaModel> result = searchNodes(acceptLanguage, searchString, 0, 0);

        List<String> distinctPropsList = distinctProps == null ? Collections.emptyList() : Arrays.asList(distinctProps.split(","));

        List<NodeMetaModel> distinctResult = new ArrayList<>();
        for (NodeMetaModel node : result.getList()) {
            boolean unique = true;
            for (NodeMetaModel dNode : distinctResult) {
                int nonUniqueProps = 0;
                for (String prop : distinctPropsList) {
                    String value = FirstUtil.getValue(node.getProperties().get(prop), String.class);
                    String dValue = FirstUtil.getValue(dNode.getProperties().get(prop), String.class);

                    if (value != null && value.equals(dValue)) {
                        nonUniqueProps++;
                    }
                }
                if (nonUniqueProps == distinctPropsList.size()) {
                    unique = false;
                    break;
                }
            }
            if (unique) {
                distinctResult.add(node);
            }
        }

        result.setList(limit > 0 && !distinctResult.isEmpty() ? distinctResult.subList(start, start + limit > distinctResult.size() ? distinctResult.size() : start + limit) : distinctResult);
        result.setTotalResults(distinctResult.size());

        return result;
    }

    @Override
    public PaginatedListWrapper<NodeMetaModel> searchByTemplate(String acceptLanguage,
                                                                String templateName,
                                                                List<String> searchValues,
                                                                String fiRegistryCode,
                                                                String relativePath,
                                                                Integer start,
                                                                Integer limit) {
        String queryString = AlfrescoConfiguration.get().getAlfrescoProperty("administratorsAndIndividualsQueryTemplate");
        queryString = queryString == null ? "" : queryString;
        searchValues = new ArrayList<>(searchValues);

        if (fiRegistryCode != null) {
            fiRegistryCode = AlfrescoUtil.getISO9075String(fiRegistryCode);
            searchValues.add(0, fiRegistryCode);
        }
        if (relativePath != null) {
            relativePath = AlfrescoUtil.getISO9075String(relativePath);
            searchValues.add(1, relativePath);
        }


        queryString = MessageFormat.format(queryString, searchValues.toArray());

        return searchNodes(acceptLanguage, queryString, start, limit);
    }

    @Override
    public PaginatedListWrapper<NodeMetaModel> searchPersonalFiles(String acceptLanguage, String query, Integer start, Integer limit) {
        PaginatedListWrapper<NodeMetaModel> result = new PaginatedListWrapper<>();
        if (query != null) {
            AlfrescoClient client = ecmClientProxySession.getAlfrescoClient(acceptLanguage);
            QueryBody queryBody = getAFTSQuery("(cm:name:\"*" + query + "*\")", start, limit,
                    Collections.singletonList(new RequestSortDefinition().field("createdAt").ascending(false)),
                    Arrays.asList(APIConstants.PROPERTIES_VALUE, ContentModel.PROP_PATH),
                    "+TYPE:'cm:folder' OR +TYPE:'cm:content'");

            ResultSetRepresentation<ResultNodeRepresentation> searchResult = client.getSearchAPI().search(queryBody);

            result.setList(NodeModelHelper.getMetaModels(searchResult.getObjects()));
            result.setTotalResults(searchResult.getPagination().getTotalItems());
        }

        return result;
    }

    @Override
    public List<RequestSortDefinition> getSortDefinition(String sort, String group) {
        List<SortField> sortFields = ListUtils.union(
                FirstUtil.getSortFieldsFromParam(group != null ? "[" + group + "]" : null),
                FirstUtil.getSortFieldsFromParam(sort));

        List<RequestSortDefinition> result = new ArrayList<>();
        for (SortField sortField : sortFields) {
            result.add(new RequestSortDefinition()
                    .field(sortField.getProperty().replace("_", ":"))
                    .ascending(sortField.getDirection().equals("ASC")));
        }

        return result;
    }

    @Override
    public PaginatedListWrapper<NodeMetaModel> searchFiRegistryDocument(String acceptLanguage, String filterByName, String registryCode, int start, int limit) {
        String searchStr = "cm:name:\"*" + filterByName + "*\" OR cm:description:\"*" + filterByName + "*\"";
        String documentPath = "+PATH:'//cm:fina2first//cm:Registry//cm:" + AlfrescoUtil.getISO9075String(registryCode) + "//cm:Documents//*'";
        RequestQuery query = new RequestQuery().query(searchStr).language(RequestQuery.LanguageEnum.AFTS);

        QueryBody queryBody = new QueryBody().query(query).include(Arrays.asList("properties", "path", "association"))
                .filterQueries(Arrays.asList(
                        new RequestFilterQuery().query(documentPath),
                        new RequestFilterQuery().query("+TYPE:'fina:fiDocument'")
                )).sort(Collections.singletonList(new RequestSortDefinition().field("cm:modifiedAt")));

        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient(acceptLanguage);

        ResultSetRepresentation<ResultNodeRepresentation> searchResult = client.getSearchAPI().search(queryBody);
        List<NodeMetaModel> nodeMetaModels = new ArrayList<>();
        searchResult.getObjects().forEach(node -> {
            NodeMetaModel model = NodeModelHelper.getMetaModel(node);

            nodeMetaModels.add(model);
        });

        return new PaginatedListWrapper<>(nodeMetaModels, limit, searchResult.getPagination().getCount());
    }

    private String validateOperator(String operator) {
        if ("=".equals(operator)) {
            return "=";
        } else if ("!=".equals(operator)) {
            return "<>";
        } else if (">".equals(operator)) {
            return ">";
        } else if ("<".equals(operator)) {
            return "<";
        } else if ("in".equals(operator)) {
            return "in";
        } else if ("notin".equals(operator)) {
            return "notin";
        } else if ("like".equals(operator)) {
            return "like";
        }

        return null;
    }
}
