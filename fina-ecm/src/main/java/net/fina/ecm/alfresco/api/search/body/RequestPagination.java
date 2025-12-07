package net.fina.ecm.alfresco.api.search.body;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

import java.util.Objects;

/**
 * RequestPagination
 */
public class RequestPagination implements BaseRepresentation {
    @JsonProperty("maxItems")
    private Integer maxItems = null;

    @JsonProperty("skipCount")
    private Integer skipCount = null;

    public RequestPagination maxItems(Integer maxItems) {
        this.maxItems = maxItems;
        return this;
    }

    /**
     * The maximum number of items to return in the query results minimum: 1.0
     *
     * @return maxItems
     **/
    public Integer getMaxItems() {
        return maxItems;
    }

    public void setMaxItems(Integer maxItems) {
        this.maxItems = maxItems;
    }

    public RequestPagination skipCount(Integer skipCount) {
        this.skipCount = skipCount;
        return this;
    }

    /**
     * The number of items to skip from the start of the query set minimum: 0.0
     *
     * @return skipCount
     **/
    public Integer getSkipCount() {
        return skipCount;
    }

    public void setSkipCount(Integer skipCount) {
        this.skipCount = skipCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RequestPagination requestPagination = (RequestPagination) o;
        return Objects.equals(this.maxItems, requestPagination.maxItems)
                && Objects.equals(this.skipCount, requestPagination.skipCount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maxItems, skipCount);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class RequestPagination {, ");

        sb.append("    maxItems: ").append(toIndentedString(maxItems)).append(", ");
        sb.append("    skipCount: ").append(toIndentedString(skipCount)).append(", ");
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
