package net.fina.ecm.alfresco.api.core.model.body;

import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

public class NodeBodyCopy implements BaseRepresentation {
    private String name;
    private String targetParentId;

    public NodeBodyCopy() {
    }

    public NodeBodyCopy(String targetParentId, String name) {
        this.targetParentId = targetParentId;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTargetParentId() {
        return targetParentId;
    }

    public void setTargetParentId(String targetParentId) {
        this.targetParentId = targetParentId;
    }
}
