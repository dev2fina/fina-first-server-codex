package net.fina.server.returns.model;


import net.fina.server.mdt.entity.MDTDependentNode;

import java.io.Serializable;

public class MDependentMetaModel implements Serializable {
    private long nodeId;
    private long dependentNodeId;

    public long getNodeId() {
        return nodeId;
    }

    public void setNodeId(long nodeId) {
        this.nodeId = nodeId;
    }

    public long getDependentNodeId() {
        return dependentNodeId;
    }

    public void setDependentNodeId(long dependentNodeId) {
        this.dependentNodeId = dependentNodeId;
    }

    public MDependentMetaModel setMDTDependentNode(MDTDependentNode dependentNode) {
        this.nodeId = dependentNode.getDepNode().getNodeId();
        this.dependentNodeId = dependentNode.getDepNode().getDependentNodeId();
        return this;
    }
}
