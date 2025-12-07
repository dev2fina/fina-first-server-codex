package net.fina.server.processing.impl;


import net.fina.server.fsop.impl.FsopTemplateSession;
import net.fina.server.mdt.entity.MDTDependentNode;
import net.fina.server.mdt.entity.MDTDependentNodePK;
import net.fina.server.processing.model.Dependent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DependentProcessorTest {
    private long nodeId;
    private int callCount;

    private List<MDTDependentNode> allMdtDependentNodes;

    private FsopTemplateSession fsopTemplateSession = new FsopTemplateSession();

    @Before
    public void before() {

        int size = 103_043;
        nodeId = 1;
        callCount = 10_000;

        allMdtDependentNodes = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            MDTDependentNode node = new MDTDependentNode();
            MDTDependentNodePK pk = new MDTDependentNodePK();
            pk.setDependentNodeId(1);
            pk.setNodeId(i);
            node.setDepNode(pk);
            allMdtDependentNodes.add(node);
        }

    }

    @Test
    public void fast() {
        DependentProcessor dependentProcessor = new DependentProcessor(allMdtDependentNodes);
        for (int i = 0; i < callCount; i++) {
            dependentProcessor.getNodeDependent(nodeId);
        }
    }

    @Test
    public void slow() {
        for (int i = 0; i < callCount; i++) {
            getNodeDependent(nodeId, allMdtDependentNodes);
        }
    }

    @Test
    public void verify() {
        allMdtDependentNodes = new ArrayList<>();

        MDTDependentNodePK pk = new MDTDependentNodePK();
        pk.setDependentNodeId(0);
        pk.setNodeId(1);
        MDTDependentNode node = new MDTDependentNode();
        node.setDepNode(pk);
        allMdtDependentNodes.add(node);

        MDTDependentNodePK pk1 = new MDTDependentNodePK();
        pk1.setDependentNodeId(1);
        pk1.setNodeId(2);
        MDTDependentNode node1 = new MDTDependentNode();
        node1.setDepNode(pk1);
        allMdtDependentNodes.add(node1);


        MDTDependentNodePK pk2 = new MDTDependentNodePK();
        pk2.setDependentNodeId(1);
        pk2.setNodeId(3);
        MDTDependentNode node2 = new MDTDependentNode();
        node2.setDepNode(pk2);
        allMdtDependentNodes.add(node2);

        long findNode = 1;

        DependentProcessor dependentProcessor = new DependentProcessor(allMdtDependentNodes);
        Dependent d1 = dependentProcessor.getNodeDependent(findNode);
        Dependent d2 = getNodeDependent(findNode, allMdtDependentNodes);

        Assert.assertArrayEquals(d1.dependentIds.toArray(), d2.dependentIds.toArray());

        System.out.println(d1.dependentIds);
        System.out.println(d2.dependentIds);
    }


    /**
     * Performance problem; changed method;
     */
    @Deprecated
    private static Dependent getNodeDependent(long nodeId, List<MDTDependentNode> allMdtDependentNodes) {
        Dependent dependent = new Dependent();
        dependent.status = Dependent.Status.UNKNOWN;
        dependent.dependentIds.addAll(allMdtDependentNodes.stream().filter(dependentNode -> nodeId == dependentNode.getDepNode().getDependentNodeId()).map(dependentNode -> dependentNode.getDepNode().getNodeId()).collect(Collectors.toList()));
        return dependent;
    }

}
