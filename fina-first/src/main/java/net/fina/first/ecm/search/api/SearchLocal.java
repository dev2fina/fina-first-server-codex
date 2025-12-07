package net.fina.first.ecm.search.api;

import net.fina.common.shared.PaginatedListWrapper;
import net.fina.ecm.alfresco.api.search.body.QueryBody;
import net.fina.ecm.alfresco.api.search.body.RequestSortDefinition;
import net.fina.ecm.alfresco.api.search.model.ResultNodeRepresentation;
import net.fina.ecm.alfresco.api.search.model.ResultSetRepresentation;
import net.fina.first.ecm.node.model.NodeMetaModel;

import java.util.List;

public interface SearchLocal {
    ResultSetRepresentation<ResultNodeRepresentation> searchRecentFiles(Integer start, Integer limit, String orderBy);

    ResultSetRepresentation<ResultNodeRepresentation> searchAFTS(String acceptLanguage, String query, int start, int limit);

    ResultSetRepresentation<ResultNodeRepresentation> searchAFTS(String acceptLanguage, String query, int start, int limit, List<RequestSortDefinition> sort, String... filterQueries);

    PaginatedListWrapper<NodeMetaModel> searchNodes(String acceptLanguage, String query, int start, int limit);

    PaginatedListWrapper<NodeMetaModel> searchNodes(String acceptLanguage, String query, int start, int limit, List<RequestSortDefinition> sort, String... filterQueries);

    PaginatedListWrapper<ResultNodeRepresentation> searchUsersAndGroups(String acceptLanguage, String filter, int start, int limit);

    PaginatedListWrapper<ResultNodeRepresentation> searchLinkedInformation(String acceptLanguage, String fiRegistryId, int start, int limit, String filter, String filterJson);

    PaginatedListWrapper<ResultNodeRepresentation> searchByText(String searchStr, String filterStr);

    PaginatedListWrapper<ResultNodeRepresentation> searchByCMIS(String searchStr);

    ResultSetRepresentation<ResultNodeRepresentation> searchByCMIS(String searchStr, int page, int start, int limit);

    PaginatedListWrapper<ResultNodeRepresentation> getFilteredNodes(String parentId, String nodeType, String filterJson);

    String getCMISFilterString(String filterJson);

    String getAFTSFilterString(String filterJson);

    QueryBody getAFTSQuery(String query, int start, int limit, List<RequestSortDefinition> sortDefinitions, List<String> includeParams, String... filterQueries);

    PaginatedListWrapper<NodeMetaModel> searchNodesByProperties(String acceptLanguage, String query, String searchByProps, String distinctProps, String fiRegistryCode, String relativePath, Boolean exactMatch, Integer start, Integer limit, SearchPropertyWildcardCondition searchPropertyWildcardCondition);

    PaginatedListWrapper<NodeMetaModel> searchByTemplate(String acceptLanguage, String templateName, List<String> searchValues, String fiRegistryCode, String relativePath, Integer start, Integer limit);

    PaginatedListWrapper<NodeMetaModel> searchPersonalFiles(String acceptLanguage, String query, Integer start, Integer limit);

    List<RequestSortDefinition> getSortDefinition(String sort, String group);

    PaginatedListWrapper<NodeMetaModel> searchFiRegistryDocument(String acceptLanguage, String query, String registryCode, int start, int limit);
}
