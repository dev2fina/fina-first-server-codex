package net.fina.server.processing.api;

import net.fina.common.client.returns.CanProcessResult;
import net.fina.common.client.returns.ProcessResult;
import net.fina.common.client.returns.ReturnModel;
import net.fina.server.mdt.entity.MDTComparison;
import net.fina.server.mdt.entity.MDTDependentNode;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.processing.impl.ReturnStatusChangeEvent;
import net.fina.server.processing.model.ComparisonItem;
import net.fina.server.processing.model.ProcessItem;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * User: nikoloz
 * Date: 8/10/13
 * Time: 2:46 PM
 */
public interface ProcessingLocal {

    Map<Long, ProcessResult> processSameTransaction(long userId, long langId, boolean reprocess, Map<Long, ProcessItem> packageReturnItemsById, final List<MDTDependentNode> mdtDependentNodes, final Map<Long, List<MDTNode>> allMdtNodesByParentId, final Map<Long, List<ComparisonItem>> comparisons, List<MDTComparison> mdtComparisons, Long... returnIds) throws net.fina.server.processing.ProcessException;

    Map<Long, ProcessResult> process(long userId, long langId, boolean reprocess, List<MDTComparison> mdtComparisons, Long... returnIds) throws net.fina.server.processing.ProcessException;

    void updateProcessItems(Collection<ProcessItem> values, String processId);

    void updateProcessItems(Collection<ProcessItem> values, long returnId, List<MDTDependentNode> mdtDependentNodes, final Map<Long, List<MDTNode>> allMdtNodesByParentId, final List<MDTComparison> comparisons);

    void changeReturnStatus(ReturnStatusChangeEvent event);

    void changeReturnStatusEventHandler(ReturnStatusChangeEvent event);

    Map<Long, ProcessResult> processPackage(long userId, long langId, ReturnModel filterModel) throws net.fina.server.processing.ProcessException;

    Map<Long, ProcessItem> loadReturnNodesValue(long returnId, long versionId, List<MDTDependentNode> mdtDependentNodes, long langId, Map<Long, List<MDTNode>> allMdtNodesByParentId);

    CanProcessResult canProcess(long scheduleId, long langId, String versionCode);

    Collection<Long> getCurrentReturnDirectDependentReturnIds(long returnId);
}
