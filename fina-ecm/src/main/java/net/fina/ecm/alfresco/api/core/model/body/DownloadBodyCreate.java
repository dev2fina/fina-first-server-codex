package net.fina.ecm.alfresco.api.core.model.body;

import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

import java.util.ArrayList;
import java.util.List;

public class DownloadBodyCreate implements BaseRepresentation {
    List<String> nodeIds = new ArrayList<>();

    public DownloadBodyCreate() {
    }

    public List<String> getNodeIds() {
        return nodeIds;
    }

    public void setNodeIds(List<String> nodeIds) {
        this.nodeIds = nodeIds;
    }
}
