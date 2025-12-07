package net.fina.server.returns.impl;

import net.fina.server.processing.model.ProcessItem;
import net.fina.server.returns.entity.DefinitionTable;
import net.fina.server.returns.model.RDataMetaModel;
import net.fina.server.returns.model.RItemMetaModel;
import net.fina.server.returns.model.RTableMetaModel;

import java.util.List;
import java.util.Map;

public interface ReturnTableProcessorBase {

    RTableMetaModel process(final DefinitionTable definitionTable, Map<Long, ProcessItem> processItemMap, long langId, RDataMetaModel dataMetaModel);

    List<RItemMetaModel> processItemToRItemMetaModel(ProcessItem pItem);
}
