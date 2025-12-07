package net.fina.server.tools.mdt.v2.tester.api;

import net.fina.common.client.tools.mdt.tester.MdtTesterResultMetaModel;
import net.fina.server.mdt.entity.MDTDependentNode;
import net.fina.server.mdt.entity.MDTNode;

import java.util.List;
import java.util.Map;

public interface MdtTester {
    List<MdtTesterResultMetaModel> validate(List<MDTNode> nodeData, Map<Long, MDTNode> mdtNodeByIdMap, List<MDTDependentNode> allDependentNodes);
}
