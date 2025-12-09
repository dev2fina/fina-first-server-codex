package net.fina.ecm.alfresco.api.core.model.representation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.fina.ecm.alfresco.api.common.representation.AbstractRepresentation;

import java.util.Date;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SiteMembershipRequestRepresentation extends AbstractRepresentation {
    private String id = null;
    private Date createdAt = null;
    private SiteRepresentation site = null;
    private String message = null;

    // ///////////////////////////////////////////////////////////////////////////
    // GETTERS & SETTERS
    // ///////////////////////////////////////////////////////////////////////////

    /**
     * Get id
     *
     * @return id
     **/
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get createdAt
     *
     * @return createdAt
     **/
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Get site
     *
     * @return site
     **/
    public SiteRepresentation getSite() {
        return site;
    }

    public void setSite(SiteRepresentation site) {
        this.site = site;
    }

    /**
     * Get message
     *
     * @return message
     **/
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SiteMembershipRequestRepresentation siteMembershipRequest = (SiteMembershipRequestRepresentation) o;
        return Objects.equals(this.id, siteMembershipRequest.id)
                && Objects.equals(this.createdAt, siteMembershipRequest.createdAt)
                && Objects.equals(this.site, siteMembershipRequest.site)
                && Objects.equals(this.message, siteMembershipRequest.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdAt, site, message);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class SiteMembershipRequestBodyCreate {, ");

        sb.append("    id: ").append(toIndentedString(id)).append(", ");
        sb.append("    createdAt: ").append(toIndentedString(createdAt)).append(", ");
        sb.append("    site: ").append(toIndentedString(site)).append(", ");
        sb.append("    message: ").append(toIndentedString(message)).append(", ");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    public String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace(", ", ",     ");
    }
}
