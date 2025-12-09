package net.fina.ecm.alfresco.api.search.body;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

import java.util.Objects;

/**
 * Query.
 */
public class RequestQuery implements BaseRepresentation {
    /**
     * The query language in which the query is written.
     */
    public enum LanguageEnum {
        @JsonProperty("afts") AFTS("afts"),

        @JsonProperty("lucene") LUCENE("lucene"),

        @JsonProperty("cmis") CMIS("cmis");

        private String value;

        LanguageEnum(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

    @JsonProperty("language")
    private LanguageEnum language = LanguageEnum.AFTS;

    @JsonProperty("userQuery")
    private String userQuery = null;

    @JsonProperty("query")
    private String query = null;

    public RequestQuery language(LanguageEnum language) {
        this.language = language;
        return this;
    }

    /**
     * The query language in which the query is written.
     *
     * @return language
     **/
    public LanguageEnum getLanguage() {
        return language;
    }

    public void setLanguage(LanguageEnum language) {
        this.language = language;
    }

    public RequestQuery userQuery(String userQuery) {
        this.userQuery = userQuery;
        return this;
    }

    /**
     * The exact search request typed in by the user
     *
     * @return userQuery
     **/
    public String getUserQuery() {
        return userQuery;
    }

    public void setUserQuery(String userQuery) {
        this.userQuery = userQuery;
    }

    public RequestQuery query(String query) {
        this.query = query;
        return this;
    }

    /**
     * The query which may have been generated in some way from the userQuery
     *
     * @return query
     **/
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RequestQuery requestQuery = (RequestQuery) o;
        return Objects.equals(this.language, requestQuery.language)
                && Objects.equals(this.userQuery, requestQuery.userQuery)
                && Objects.equals(this.query, requestQuery.query);
    }

    @Override
    public int hashCode() {
        return Objects.hash(language, userQuery, query);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class RequestQuery {, ");

        sb.append("    language: ").append(toIndentedString(language)).append(", ");
        sb.append("    userQuery: ").append(toIndentedString(userQuery)).append(", ");
        sb.append("    query: ").append(toIndentedString(query)).append(", ");
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
        return o.toString().replace(", ", ",     ");
    }
}
