package net.fina.first.ecm.search.proxy;

import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.ecm.alfresco.api.search.body.QueryBody;
import net.fina.ecm.alfresco.api.search.body.RequestSortDefinition;
import net.fina.ecm.alfresco.api.search.model.ResultNodeRepresentation;
import net.fina.ecm.alfresco.api.search.model.ResultSetRepresentation;
import net.fina.first.ecm.node.model.NodeMetaModel;
import net.fina.first.ecm.search.api.SearchLocal;
import net.fina.first.ecm.search.api.SearchPropertyWildcardCondition;
import org.jboss.ejb3.annotation.SecurityDomain;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.List;

@Stateless
@RolesAllowed({PermissionIdNames.FIRST_REVIEW})
@SecurityDomain("FinASecurityDomain")
public class SearchProxySession {

    @Inject
    private SearchLocal searchLocal;

    public ResultSetRepresentation<ResultNodeRepresentation> searchRecentFiles(Integer start, Integer limit, String orderBy) {
        return searchLocal.searchRecentFiles(start, limit, orderBy);
    }

    public ResultSetRepresentation<ResultNodeRepresentation> searchAFTS(String acceptLanguage, String query, int start, int limit) {
        return searchLocal.searchAFTS(acceptLanguage, query, start, limit);
    }

    public PaginatedListWrapper<NodeMetaModel> searchNodes(String acceptLanguage, String query, int start, int limit) {
        return searchLocal.searchNodes(acceptLanguage, query, start, limit);
    }

    public PaginatedListWrapper<ResultNodeRepresentation> searchUsersAndGroups(String acceptLanguage, String filter, int start, int limit) {
        return searchLocal.searchUsersAndGroups(acceptLanguage, filter, start, limit);
    }

    public PaginatedListWrapper<ResultNodeRepresentation> searchLinkedInformation(String acceptLanguage, String fiRegistryId, int start, int limit, String filter, String filterJson) {
        return searchLocal.searchLinkedInformation(acceptLanguage, fiRegistryId, start, limit, filter, filterJson);
    }

    public PaginatedListWrapper<ResultNodeRepresentation> searchByText(String searchStr, String filterStr) {
        return searchLocal.searchByText(searchStr, filterStr);
    }

    public PaginatedListWrapper<ResultNodeRepresentation> searchByCMIS(String searchStr) {
        return searchLocal.searchByCMIS(searchStr);
    }

    public ResultSetRepresentation<ResultNodeRepresentation> searchByCMIS(String searchStr, int page, int start, int limit) {
        return searchLocal.searchByCMIS(searchStr, page, start, limit);
    }

    public PaginatedListWrapper<ResultNodeRepresentation> getFilteredNodes(String parentId, String nodeType, String filterJson) {
        return searchLocal.getFilteredNodes(parentId, nodeType, filterJson);
    }

    public QueryBody getAFTSQuery(String query, int start, int limit, List<RequestSortDefinition> sortDefinitions, List<String> includeParams, String... filterQueries) {
        return searchLocal.getAFTSQuery(query, start, limit, sortDefinitions, includeParams, filterQueries);
    }

    public PaginatedListWrapper<NodeMetaModel> searchNodesByProperties(String acceptLanguage, String query, String searchByProps, String distinctProps, String fiRegistryCode, String relativePath, Boolean exactMatch, Integer start, Integer limit, SearchPropertyWildcardCondition searchPropertyWildcardCondition) {
        return searchLocal.searchNodesByProperties(acceptLanguage, query, searchByProps, distinctProps, fiRegistryCode, relativePath, exactMatch, start, limit, searchPropertyWildcardCondition);
    }

    public PaginatedListWrapper<NodeMetaModel> searchByTemplate(String acceptLanguage, String templateName, List<String> searchValues, String fiRegistryCode, String relativePath, Integer start, Integer limit) {
        return searchLocal.searchByTemplate(acceptLanguage, templateName, searchValues, fiRegistryCode, relativePath, start, limit);
    }

    public PaginatedListWrapper<NodeMetaModel> searchPersonalFiles(String acceptLanguage, String query, Integer start, Integer limit) {
        return searchLocal.searchPersonalFiles(acceptLanguage, query, start, limit);
    }

    public PaginatedListWrapper<NodeMetaModel> searchFiRegistryDocument(String acceptLanguage, String query, String registryCode, int start, int limit) {
        return searchLocal.searchFiRegistryDocument(acceptLanguage, query, registryCode, start, limit);
    }
}
