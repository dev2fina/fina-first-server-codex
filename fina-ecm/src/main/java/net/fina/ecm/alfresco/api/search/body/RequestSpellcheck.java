package net.fina.ecm.alfresco.api.search.body;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

import java.util.Objects;

/**
 * Request that spellcheck fragments to be added to result set rows The
 * properties reflect SOLR spellcheck parameters.
 */
public class RequestSpellcheck implements BaseRepresentation {
    @JsonProperty("query")
    private String query = null;

    public RequestSpellcheck query(String query) {
        this.query = query;
        return this;
    }

    /**
     * Get query
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
        RequestSpellcheck requestSpellcheck = (RequestSpellcheck) o;
        return Objects.equals(this.query, requestSpellcheck.query);
    }

    @Override
    public int hashCode() {
        return Objects.hash(query);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class RequestSpellcheck {, ");

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
