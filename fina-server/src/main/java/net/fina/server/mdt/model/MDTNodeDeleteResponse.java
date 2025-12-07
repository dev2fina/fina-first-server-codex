package net.fina.server.mdt.model;

import net.fina.common.client.mdt.MDTDeleteResult;

import java.io.Serializable;

public class MDTNodeDeleteResponse implements Serializable {
    private  final long nodeId;
    private final MDTDeleteResult deleteResult;

    public MDTNodeDeleteResponse(long nodeId, MDTDeleteResult deleteResult) {
        this.nodeId = nodeId;
        this.deleteResult = deleteResult;
    }

    public long getNodeId() {
        return nodeId;
    }

    public MDTDeleteResult getDeleteResult() {
        return deleteResult;
    }
}
