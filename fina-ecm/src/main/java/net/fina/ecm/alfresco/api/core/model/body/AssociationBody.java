package net.fina.ecm.alfresco.api.core.model.body;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

public class AssociationBody implements BaseRepresentation {
    @JsonProperty("targetId")
    private String targetId;

    @JsonProperty("assocType")
    private String assocType;

    public AssociationBody() {
    }

    public AssociationBody(String targetId, String assocType) {
        this.targetId = targetId;
        this.assocType = assocType;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getAssocType() {
        return assocType;
    }

    public void setAssocType(String assocType) {
        this.assocType = assocType;
    }
}
