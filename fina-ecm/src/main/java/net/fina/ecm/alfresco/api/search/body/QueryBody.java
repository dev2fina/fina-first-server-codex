package net.fina.ecm.alfresco.api.search.body;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

import java.util.List;
import java.util.Objects;

/**
 * QueryBody
 */
public class QueryBody implements BaseRepresentation {
    @JsonProperty("query")
    private RequestQuery query = null;

    @JsonProperty("paging")
    private RequestPagination paging = null;

    @JsonProperty("include")
    private List<String> include = null;

    @JsonProperty("fields")
    private List<String> fields = null;

    @JsonProperty("sort")
    private List<RequestSortDefinition> sort = null;

    @JsonProperty("templates")
    private List<RequestTemplate> templates = null;

    @JsonProperty("defaults")
    private RequestDefaults defaults = null;

    @JsonProperty("filterQueries")
    private List<RequestFilterQuery> filterQueries = null;

    @JsonProperty("facetQueries")
    private List<RequestFacetQuery> facetQueries = null;

    @JsonProperty("facetFields")
    private RequestFacetFields facetFields = null;

    @JsonProperty("spellcheck")
    private RequestSpellcheck spellcheck = null;

    @JsonProperty("scope")
    private RequestScope scope = null;

    @JsonProperty("limits")
    private RequestLimits limits = null;

    @JsonProperty("highlight")
    private RequestHighlight highlight = null;

    public QueryBody query(RequestQuery query) {
        this.query = query;
        return this;
    }

    /**
     * Get query
     *
     * @return query
     **/
    public RequestQuery getQuery() {
        return query;
    }

    public void setQuery(RequestQuery query) {
        this.query = query;
    }

    public QueryBody paging(RequestPagination paging) {
        this.paging = paging;
        return this;
    }

    /**
     * Get paging
     *
     * @return paging
     **/
    public RequestPagination getPaging() {
        return paging;
    }

    public void setPaging(RequestPagination paging) {
        this.paging = paging;
    }

    public QueryBody include(List<String> include) {
        this.include = include;
        return this;
    }

    /**
     * Get include
     *
     * @return include
     **/
    public List<String> getInclude() {
        return include;
    }

    public void setInclude(List<String> include) {
        this.include = include;
    }

    public QueryBody fields(List<String> fields) {
        this.fields = fields;
        return this;
    }

    /**
     * Get fields
     *
     * @return fields
     **/
    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public QueryBody sort(List<RequestSortDefinition> sort) {
        this.sort = sort;
        return this;
    }

    /**
     * Get sort
     *
     * @return sort
     **/
    public List<RequestSortDefinition> getSort() {
        return sort;
    }

    public void setSort(List<RequestSortDefinition> sort) {
        this.sort = sort;
    }

    public QueryBody templates(List<RequestTemplate> templates) {
        this.templates = templates;
        return this;
    }

    /**
     * Get templates
     *
     * @return templates
     **/
    public List<RequestTemplate> getTemplates() {
        return templates;
    }

    public void setTemplates(List<RequestTemplate> templates) {
        this.templates = templates;
    }

    public QueryBody defaults(RequestDefaults defaults) {
        this.defaults = defaults;
        return this;
    }

    /**
     * Get defaults
     *
     * @return defaults
     **/
    public RequestDefaults getDefaults() {
        return defaults;
    }

    public void setDefaults(RequestDefaults defaults) {
        this.defaults = defaults;
    }

    public QueryBody filterQueries(List<RequestFilterQuery> filterQueries) {
        this.filterQueries = filterQueries;
        return this;
    }

    /**
     * Get filterQueries
     *
     * @return filterQueries
     **/
    public List<RequestFilterQuery> getFilterQueries() {
        return filterQueries;
    }

    public void setFilterQueries(List<RequestFilterQuery> filterQueries) {
        this.filterQueries = filterQueries;
    }

    public QueryBody facetQueries(List<RequestFacetQuery> facetQueries) {
        this.facetQueries = facetQueries;
        return this;
    }

    /**
     * Get facetQueries
     *
     * @return facetQueries
     **/
    public List<RequestFacetQuery> getFacetQueries() {
        return facetQueries;
    }

    public void setFacetQueries(List<RequestFacetQuery> facetQueries) {
        this.facetQueries = facetQueries;
    }

    public QueryBody facetFields(RequestFacetFields facetFields) {
        this.facetFields = facetFields;
        return this;
    }

    /**
     * Get facetFields
     *
     * @return facetFields
     **/
    public RequestFacetFields getFacetFields() {
        return facetFields;
    }

    public void setFacetFields(RequestFacetFields facetFields) {
        this.facetFields = facetFields;
    }

    public QueryBody spellcheck(RequestSpellcheck spellcheck) {
        this.spellcheck = spellcheck;
        return this;
    }

    /**
     * Get spellcheck
     *
     * @return spellcheck
     **/
    public RequestSpellcheck getSpellcheck() {
        return spellcheck;
    }

    public void setSpellcheck(RequestSpellcheck spellcheck) {
        this.spellcheck = spellcheck;
    }

    public QueryBody scope(RequestScope scope) {
        this.scope = scope;
        return this;
    }

    /**
     * Get scope
     *
     * @return scope
     **/
    public RequestScope getScope() {
        return scope;
    }

    public void setScope(RequestScope scope) {
        this.scope = scope;
    }

    public QueryBody limits(RequestLimits limits) {
        this.limits = limits;
        return this;
    }

    /**
     * Get limits
     *
     * @return limits
     **/
    public RequestLimits getLimits() {
        return limits;
    }

    public void setLimits(RequestLimits limits) {
        this.limits = limits;
    }

    public QueryBody highlight(RequestHighlight highlight) {
        this.highlight = highlight;
        return this;
    }

    /**
     * Get highlight
     *
     * @return highlight
     **/
    public RequestHighlight getHighlight() {
        return highlight;
    }

    public void setHighlight(RequestHighlight highlight) {
        this.highlight = highlight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        QueryBody queryBody = (QueryBody) o;
        return Objects.equals(this.query, queryBody.query) && Objects.equals(this.paging, queryBody.paging)
                && Objects.equals(this.include, queryBody.include) && Objects.equals(this.fields, queryBody.fields)
                && Objects.equals(this.sort, queryBody.sort) && Objects.equals(this.templates, queryBody.templates)
                && Objects.equals(this.defaults, queryBody.defaults)
                && Objects.equals(this.filterQueries, queryBody.filterQueries)
                && Objects.equals(this.facetQueries, queryBody.facetQueries)
                && Objects.equals(this.facetFields, queryBody.facetFields)
                && Objects.equals(this.spellcheck, queryBody.spellcheck) && Objects.equals(this.scope, queryBody.scope)
                && Objects.equals(this.limits, queryBody.limits) && Objects.equals(this.highlight, queryBody.highlight);
    }

    @Override
    public int hashCode() {
        return Objects.hash(query, paging, include, fields, sort, templates, defaults, filterQueries, facetQueries,
                facetFields, spellcheck, scope, limits, highlight);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class QueryBody {");

        sb.append("    query: ").append(toIndentedString(query)).append(", ");
        sb.append("    paging: ").append(toIndentedString(paging)).append(", ");
        sb.append("    include: ").append(toIndentedString(include)).append(", ");
        sb.append("    fields: ").append(toIndentedString(fields)).append(", ");
        sb.append("    sort: ").append(toIndentedString(sort)).append(", ");
        sb.append("    templates: ").append(toIndentedString(templates)).append(", ");
        sb.append("    defaults: ").append(toIndentedString(defaults)).append(", ");
        sb.append("    filterQueries: ").append(toIndentedString(filterQueries)).append(", ");
        sb.append("    facetQueries: ").append(toIndentedString(facetQueries)).append(", ");
        sb.append("    facetFields: ").append(toIndentedString(facetFields)).append(", ");
        sb.append("    spellcheck: ").append(toIndentedString(spellcheck)).append(", ");
        sb.append("    scope: ").append(toIndentedString(scope)).append(", ");
        sb.append("    limits: ").append(toIndentedString(limits)).append(", ");
        sb.append("    highlight: ").append(toIndentedString(highlight)).append("");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}
