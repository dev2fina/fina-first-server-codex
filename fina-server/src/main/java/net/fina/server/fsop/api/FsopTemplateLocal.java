package net.fina.server.fsop.api;

import net.fina.server.mdt.entity.MDTDependentNode;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.processing.model.ProcessItem;
import net.fina.server.returns.entity.DefinitionTable;
import net.fina.server.returns.entity.Return;
import net.fina.server.returns.entity.ReturnItemLite;
import net.fina.server.fsop.model.FsopImportedReturnMetaModel;

import java.util.List;
import java.util.Map;

public interface FsopTemplateLocal {

    List<ProcessItem> getReturnTemplate(Return ret, Map<Long, List<MDTNode>> allMdtNodesByParentId, long langId, List<MDTDependentNode> mdtDependentNodes);

    Map<Long, ProcessItem> setReturnValues(FsopImportedReturnMetaModel importedReturn, net.fina.server.returns.xml.Return returnXml, List<ProcessItem> items);

    List<ProcessItem> getReturnTemplate(Return ret, Map<Long, List<MDTNode>> allMdtNodesByParentId, long langId, List<MDTDependentNode> mdtDependentNodes, List<DefinitionTable> definitionTables);

    Map<String, ReturnItemLite> getReturnTemplate(Return ret, Map<Long, List<MDTNode>> allMdtNodesByParentId);
}
