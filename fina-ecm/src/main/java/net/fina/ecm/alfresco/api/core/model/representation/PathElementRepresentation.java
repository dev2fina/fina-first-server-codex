package net.fina.ecm.alfresco.api.core.model.representation;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.fina.ecm.alfresco.api.common.representation.AbstractRepresentation;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PathElementRepresentation extends AbstractRepresentation {
    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    public PathElementRepresentation() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

