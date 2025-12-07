package net.fina.ecm.alfresco.api.core.model.representation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import net.fina.ecm.alfresco.api.common.representation.AbstractRepresentation;

import java.util.Objects;


@JsonTypeName(value = "entry")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SiteMemberRepresentation extends AbstractRepresentation {
    private String id = null;
    private PersonRepresentationPlain person = null;
    private String role = null;

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
     * Get person
     *
     * @return person
     **/
    public PersonRepresentationPlain getPerson() {
        return person;
    }

    public void setPerson(PersonRepresentationPlain person) {
        this.person = person;
    }

    /**
     * Get role
     *
     * @return role
     **/
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public SiteRoleEnum getRoleEnum() {
        return role != null ? SiteRoleEnum.fromString(role) : null;
    }

    public void setRoleEnum(SiteRoleEnum siteRoleEnum) {
        this.role = siteRoleEnum.toString();
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SiteMemberRepresentation siteMember = (SiteMemberRepresentation) o;
        return Objects.equals(this.id, siteMember.id) && Objects.equals(this.person, siteMember.person)
                && Objects.equals(this.role, siteMember.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, person, role);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class SiteMember {, ");

        sb.append("    id: ").append(toIndentedString(id)).append(", ");
        sb.append("    person: ").append(toIndentedString(person)).append(", ");
        sb.append("    role: ").append(toIndentedString(role)).append(", ");
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
