package net.fina.ecm.alfresco.api.core.model.representation;

import com.google.gson.annotations.SerializedName;
import net.fina.ecm.alfresco.api.common.representation.AbstractRepresentation;

import java.util.Objects;

public class PermissionElementRepresentation extends AbstractRepresentation {
    @SerializedName("authorityId")
    private String authorityId = null;

    @SerializedName("name")
    private String name = null;

    public PermissionElementRepresentation() {
    }

    /**
     * Gets or Sets accessStatus
     */
    public enum AccessStatusEnum {
        @SerializedName("ALLOWED") ALLOWED("ALLOWED"),

        @SerializedName("DENIED") DENIED("DENIED");

        private String value;

        AccessStatusEnum(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

    @SerializedName("accessStatus")
    private AccessStatusEnum accessStatus = AccessStatusEnum.ALLOWED;

    /**
     * Get authorityId
     *
     * @return authorityId
     **/
    public String getAuthorityId() {
        return authorityId;
    }

    public void setAuthorityId(String authorityId) {
        this.authorityId = authorityId;
    }

    /**
     * Get name
     *
     * @return name
     **/
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get accessStatus
     *
     * @return accessStatus
     **/
    public AccessStatusEnum getAccessStatus() {
        return accessStatus;
    }

    public void setAccessStatus(AccessStatusEnum accessStatus) {
        this.accessStatus = accessStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PermissionElementRepresentation permissionElement = (PermissionElementRepresentation) o;
        return Objects.equals(this.authorityId, permissionElement.authorityId)
                && Objects.equals(this.name, permissionElement.name)
                && Objects.equals(this.accessStatus, permissionElement.accessStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authorityId, name, accessStatus);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class PermissionElement {");

        sb.append("    authorityId: ").append(toIndentedString(authorityId)).append(", ");
        sb.append("    name: ").append(toIndentedString(name)).append(", ");
        sb.append("    accessStatus: ").append(toIndentedString(accessStatus));
        sb.append("}");
        return sb.toString();
    }


}
