package net.fina.ecm.alfresco.api.core.model.body;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

public class RevertBody implements BaseRepresentation {
    @JsonProperty("comment")
    public final String comment;

    @JsonProperty("majorVersion")
    public final Boolean majorVersion;

    public RevertBody(String comment, Boolean majorVersion) {
        this.comment = comment;
        this.majorVersion = majorVersion;
    }
}
