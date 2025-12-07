package net.fina.server.processing;

import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.processing.model.ProcessItem;
import net.fina.server.processing.model.ProcessReturnModel;

import java.util.Map;

public abstract class ProcessingFactory {
    public static ProcessingBase createRecursionProcessing(Map<Long, ProcessItem> itemsById, Map<String, ProcessItem> itemsByCode, Map<Long, MDTNode> allIdNodes, ProcessingUtil util, boolean reprocess, ProcessReturnModel processReturnModel, JSTreeDateProcessor dateProcessor) {
        return new RecursionProcessing(itemsById, itemsByCode, allIdNodes, util, reprocess, processReturnModel, dateProcessor);
    }
}
