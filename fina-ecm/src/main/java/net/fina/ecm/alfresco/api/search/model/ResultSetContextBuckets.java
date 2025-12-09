package net.fina.ecm.alfresco.api.search.model;

import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * ResultSetContextBuckets
 */
public class ResultSetContextBuckets implements BaseRepresentation {
    private String label = null;

    private BigDecimal count = null;

    private Object display = null;

    public ResultSetContextBuckets label(String label) {
        this.label = label;
        return this;
    }

    /**
     * The bucket label
     *
     * @return label
     **/
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public ResultSetContextBuckets count(BigDecimal count) {
        this.count = count;
        return this;
    }

    /**
     * The count for the bucket
     *
     * @return count
     **/
    public BigDecimal getCount() {
        return count;
    }

    public void setCount(BigDecimal count) {
        this.count = count;
    }

    public ResultSetContextBuckets display(Object display) {
        this.display = display;
        return this;
    }

    /**
     * An optional field for additional display information
     *
     * @return display
     **/
    public Object getDisplay() {
        return display;
    }

    public void setDisplay(Object display) {
        this.display = display;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ResultSetContextBuckets resultSetContextBuckets = (ResultSetContextBuckets) o;
        return Objects.equals(this.label, resultSetContextBuckets.label)
                && Objects.equals(this.count, resultSetContextBuckets.count)
                && Objects.equals(this.display, resultSetContextBuckets.display);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, count, display);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ResultSetContextBuckets {, ");

        sb.append("    label: ").append(toIndentedString(label)).append(", ");
        sb.append("    count: ").append(toIndentedString(count)).append(", ");
        sb.append("    display: ").append(toIndentedString(display)).append(", ");
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
