
package net.fina.ecm.alfresco.api.core.model.representation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import net.fina.ecm.alfresco.api.common.representation.AbstractRepresentation;

import java.util.Objects;

/*
@JsonTypeName(value = "entry")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)*/
@JsonIgnoreProperties(ignoreUnknown = true)
public class SiteRepresentation extends AbstractRepresentation {
    @JsonProperty("id")
    private String id = null;

    @JsonProperty("guid")
    private String guid = null;

    @JsonProperty("title")
    private String title = null;

    @JsonProperty("description")
    private String description = null;

    @JsonProperty("visibility")
    private SiteVisibilityEnum visibility = null;

    @JsonProperty("role")
    private String role = null;

    @JsonProperty("preset")
    private String preset = null;

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
     * Get guid
     *
     * @return guid
     **/
    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    /**
     * Get title
     *
     * @return title
     **/
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get description
     *
     * @return description
     **/
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get visibility
     *
     * @return visibility
     **/
    public SiteVisibilityEnum getVisibilityEnum() {
        return visibility;
    }

    public String getVisibility() {
        return visibility != null ? visibility.value() : null;
    }

    public void setVisibility(SiteVisibilityEnum visibility) {
        this.visibility = visibility;
    }

    public void setVisibilityEnum(String visibility) {
        this.visibility = SiteVisibilityEnum.valueOf(visibility);
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

    /**
     * Get preset
     *
     * @return preset
     **/
    public String getPreset() {
        return preset;
    }

    public void setPreset(String preset) {
        this.preset = preset;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SiteRepresentation site = (SiteRepresentation) o;
        return Objects.equals(this.id, site.id) && Objects.equals(this.guid, site.guid)
                && Objects.equals(this.title, site.title) && Objects.equals(this.description, site.description)
                && Objects.equals(this.visibility, site.visibility) && Objects.equals(this.preset, site.preset)
                && Objects.equals(this.role, site.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, guid, title, description, visibility, preset, role);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class SiteRepresentation {, ");

        sb.append("    id: ").append(toIndentedString(id)).append(", ");
        sb.append("    guid: ").append(toIndentedString(guid)).append(", ");
        sb.append("    title: ").append(toIndentedString(title)).append(", ");
        sb.append("    description: ").append(toIndentedString(description)).append(", ");
        sb.append("    visibility: ").append(toIndentedString(visibility)).append(", ");
        sb.append("    preset: ").append(toIndentedString(preset)).append(", ");
        sb.append("    role: ").append(toIndentedString(role)).append(", ");
        sb.append("}");
        return sb.toString();
    }

}
