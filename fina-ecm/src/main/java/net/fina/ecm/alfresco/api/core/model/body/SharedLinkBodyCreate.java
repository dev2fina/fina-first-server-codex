package net.fina.ecm.alfresco.api.core.model.body;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SharedLinkBodyCreate implements BaseRepresentation {
    public  String nodeId;
    public  Date expiresAt;

    public SharedLinkBodyCreate() {
    }

    public SharedLinkBodyCreate(String nodeId) {
        this.nodeId = nodeId;
        this.expiresAt = null;
    }

    public SharedLinkBodyCreate(String nodeId, Date expiresAt) {
        this.nodeId = nodeId;
        this.expiresAt = expiresAt;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }
}
