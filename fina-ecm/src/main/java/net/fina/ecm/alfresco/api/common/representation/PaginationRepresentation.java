package net.fina.ecm.alfresco.api.common.representation;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class PaginationRepresentation extends AbstractRepresentation {
    @JsonProperty("count")
    private int count;

    @JsonProperty("hasMoreItems")
    private Boolean hasMoreItems;

    @JsonProperty("totalItems")
    private int totalItems;

    @JsonProperty("skipCount")
    private int skipCount;

    @JsonProperty("maxItems")
    private int maxItems;

    // ///////////////////////////////////////////////////////////////////////////
    // GETTERS & SETTERS
    // ///////////////////////////////////////////////////////////////////////////

    /**
     * The number of objects in the entries array.
     *
     * @return count
     **/
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    /**
     * A boolean value which is **true** if there are more entities in the
     * collection beyond those in this response. A true value means a body with
     * a larger value for the **skipCount** or the **maxItems** parameter will
     * return more entities.
     *
     * @return hasMoreItems
     **/
    public Boolean getHasMoreItems() {
        return hasMoreItems;
    }

    public void setHasMoreItems(Boolean hasMoreItems) {
        this.hasMoreItems = hasMoreItems;
    }

    /**
     * An integer describing the total number of entities in the collection. The
     * API might not be able to determine this value, in which case this
     * property will not be present.
     *
     * @return totalItems
     **/
    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    /**
     * An integer describing how many entities exist in the collection before
     * those included in this list.
     *
     * @return skipCount
     **/
    public int getSkipCount() {
        return skipCount;
    }

    public void setSkipCount(int skipCount) {
        this.skipCount = skipCount;
    }

    /**
     * The value of the **maxItems** parameter used to generate this list, or if
     * there was no **maxItems** parameter the default value is 100
     *
     * @return maxItems
     **/
    public int getMaxItems() {
        return maxItems;
    }

    public void setMaxItems(int maxItems) {
        this.maxItems = maxItems;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PaginationRepresentation pagination = (PaginationRepresentation) o;
        return Objects.equals(this.count, pagination.count)
                && Objects.equals(this.hasMoreItems, pagination.hasMoreItems)
                && Objects.equals(this.totalItems, pagination.totalItems)
                && Objects.equals(this.skipCount, pagination.skipCount)
                && Objects.equals(this.maxItems, pagination.maxItems);
    }

    @Override
    public int hashCode() {
        return Objects.hash(count, hasMoreItems, totalItems, skipCount, maxItems);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Pagination {");

        sb.append("    count: ").append(toIndentedString(count)).append(",");
        sb.append("    hasMoreItems: ").append(toIndentedString(hasMoreItems)).append(",");
        sb.append("    totalItems: ").append(toIndentedString(totalItems)).append(",");
        sb.append("    skipCount: ").append(toIndentedString(skipCount)).append(",");
        sb.append("    maxItems: ").append(toIndentedString(maxItems));
        sb.append("}");
        return sb.toString();
    }

}
