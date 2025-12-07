package net.fina.ecm.alfresco.api.dictionary.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClassAssociationRepresentation implements BaseRepresentation {

    @JsonProperty("name")
    private String name;

    @JsonProperty("title")
    private String title;

    @JsonProperty("url")
    private String url;

    @JsonProperty("source")
    private AssociationSource source;

    @JsonProperty("target")
    private AssociationTarget target;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public AssociationSource getSource() {
        return source;
    }

    public void setSource(AssociationSource source) {
        this.source = source;
    }

    public AssociationTarget getTarget() {
        return target;
    }

    public void setTarget(AssociationTarget target) {
        this.target = target;
    }
}
