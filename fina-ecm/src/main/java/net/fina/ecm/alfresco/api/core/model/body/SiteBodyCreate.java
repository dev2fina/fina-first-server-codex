
package net.fina.ecm.alfresco.api.core.model.body;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SiteBodyCreate implements BaseRepresentation {
    @JsonProperty("id")
    private String id = null;

    @JsonProperty("title")
    private String title = null;

    @JsonProperty("description")
    private String description = null;

    @JsonProperty("visibility")
    private SiteVisibilityEnum visibility = null;

    public SiteBodyCreate() {
    }

    public SiteBodyCreate(String id, String title, String description, SiteVisibilityEnum visibility) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.visibility = visibility;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SiteVisibilityEnum getVisibility() {
        return visibility;
    }

    public void setVisibility(SiteVisibilityEnum visibility) {
        this.visibility = visibility;
    }
}
