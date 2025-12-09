package net.fina.ecm.alfresco.api.core.model.body;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

public class NodeBodyUnLock implements BaseRepresentation {
    @JsonProperty("includeChildren")
    private final Boolean includeChildren;

    @JsonProperty("allowCheckedOut")
    private final Boolean allowCheckedOut;

    public NodeBodyUnLock() {
        this.includeChildren = null;
        this.allowCheckedOut = null;
    }

    public NodeBodyUnLock(Boolean includeChildren, Boolean allowCheckedOut) {
        this.includeChildren = includeChildren != null ? includeChildren : false;
        this.allowCheckedOut = allowCheckedOut != null ? allowCheckedOut : false;
    }
}
