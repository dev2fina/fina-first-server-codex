package net.fina.ecm.alfresco.api.core.model.representation;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.fina.ecm.alfresco.api.common.representation.AbstractRepresentation;

import java.util.Objects;

public class UserInfoRepresentation extends AbstractRepresentation {
    @JsonProperty("displayName")
    private String displayName = null;

    @JsonProperty("id")
    private String id = null;

    // ///////////////////////////////////////////////////////////////////////////
    // GETTERS & SETTERS
    // ///////////////////////////////////////////////////////////////////////////

    /**
     * Get displayName
     *
     * @return displayName
     **/
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Get identifier
     *
     * @return identifier
     **/
    public String getId() {
        return id;
    }

    public void setId(String identifier) {
        this.id = identifier;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserInfoRepresentation userInfo = (UserInfoRepresentation) o;
        return Objects.equals(this.displayName, userInfo.displayName) && Objects.equals(this.id, userInfo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(displayName, id);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class UserInfoRepresentation {, ");

        sb.append("    displayName: ").append(toIndentedString(displayName)).append(", ");
        sb.append("    identifier: ").append(toIndentedString(id)).append(", ");
        sb.append("}");
        return sb.toString();
    }

}
