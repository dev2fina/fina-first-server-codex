package net.fina.ecm.alfresco.api.core.model.representation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.fina.ecm.alfresco.api.common.representation.AbstractRepresentation;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PathInfoRepresentation extends AbstractRepresentation {
    @JsonProperty("name")
    private String name;

    @JsonProperty("isComplete")
    private Boolean isComplete;

    @JsonProperty("elements")
    private List<PathElementRepresentation> element = new ArrayList<>();

    public PathInfoRepresentation() {
    }


    // ///////////////////////////////////////////////////////////////////////////
    // GETTERS & SETTERS
    // ///////////////////////////////////////////////////////////////////////////
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getComplete() {
        return isComplete;
    }

    public void setComplete(Boolean complete) {
        isComplete = complete;
    }

    public List<PathElementRepresentation> getElement() {
        return element;
    }

    public void setElement(List<PathElementRepresentation> path) {
        this.element = path;
    }
}
