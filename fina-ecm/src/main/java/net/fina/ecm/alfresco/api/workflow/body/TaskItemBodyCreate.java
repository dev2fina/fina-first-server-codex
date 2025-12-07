package net.fina.ecm.alfresco.api.workflow.body;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

public class TaskItemBodyCreate implements BaseRepresentation {
    @JsonProperty("id")
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
