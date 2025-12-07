package net.fina.server.processing.impl;


import net.fina.server.mdt.entity.MDTDependentNode;
import net.fina.server.processing.model.Dependent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DependentProcessor {

    private final Map<Long, Dependent> dependentMap;

    public DependentProcessor(List<MDTDependentNode> mdtDependentNodes) {
        this.dependentMap = dependentNodesToMap(mdtDependentNodes);
    }

    public Dependent getNodeDependent(long nodeId) {
        Dependent dependent = dependentMap.get(nodeId);
        if (dependent == null) {
            dependent = new Dependent();
            dependent.status = Dependent.Status.UNKNOWN;
        }
        return dependent;
    }

    private Map<Long, Dependent> dependentNodesToMap(List<MDTDependentNode> allMdtDependentNodes) {
        Map<Long, Dependent> dependentMap = new HashMap<>();
        if (allMdtDependentNodes != null) {
            for (MDTDependentNode node : allMdtDependentNodes) {

                if (node.getDepNode() != null) {
                    Dependent dependent = dependentMap.get(node.getDepNode().getDependentNodeId());

                    if (dependent == null) {
                        dependent = new Dependent();
                        dependent.status = Dependent.Status.UNKNOWN;
                        dependentMap.put(node.getDepNode().getDependentNodeId(), dependent);
                    }

                    dependent.dependentIds.add(node.getDepNode().getNodeId());
                }
            }
        }
        return dependentMap;
    }
}
