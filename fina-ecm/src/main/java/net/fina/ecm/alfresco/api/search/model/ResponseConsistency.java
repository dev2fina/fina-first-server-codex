package net.fina.ecm.alfresco.api.search.model;

import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

import java.util.Objects;

/**
 * The consistency state of the index used to execute the query
 */
public class ResponseConsistency implements BaseRepresentation {
    private Integer lastTxId = null;

    public ResponseConsistency lastTxId(Integer lastTxId) {
        this.lastTxId = lastTxId;
        return this;
    }

    /**
     * The id of the last indexed transaction
     *
     * @return lastTxId
     **/
    public Integer getLastTxId() {
        return lastTxId;
    }

    public void setLastTxId(Integer lastTxId) {
        this.lastTxId = lastTxId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ResponseConsistency responseConsistency = (ResponseConsistency) o;
        return Objects.equals(this.lastTxId, responseConsistency.lastTxId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lastTxId);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ResponseConsistency {, ");

        sb.append("    lastTxId: ").append(toIndentedString(lastTxId)).append(", ");
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
