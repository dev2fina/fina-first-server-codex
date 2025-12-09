package net.fina.ecm.alfresco.api.search.body;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

import java.util.Objects;

/**
 * Scope
 */
public class RequestScope implements BaseRepresentation {
    /**
     * The locations to include in the query
     */
    public enum LocationsEnum {
        @JsonProperty("nodes") NODES("nodes"),

        @JsonProperty("versions") VERSIONS("versions"),

        @JsonProperty("deleted-nodes") DELETED_NODES("deleted-nodes");

        private String value;

        LocationsEnum(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

    @JsonProperty("locations")
    private LocationsEnum locations = null;

    public RequestScope locations(LocationsEnum locations) {
        this.locations = locations;
        return this;
    }

    /**
     * The locations to include in the query
     *
     * @return locations
     **/
    public LocationsEnum getLocations() {
        return locations;
    }

    public void setLocations(LocationsEnum locations) {
        this.locations = locations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RequestScope requestScope = (RequestScope) o;
        return Objects.equals(this.locations, requestScope.locations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(locations);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class RequestScope {, ");

        sb.append("    locations: ").append(toIndentedString(locations)).append(", ");
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
