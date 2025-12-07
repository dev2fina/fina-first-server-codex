package net.fina.server.rvc.event;

import net.fina.server.mdt.entity.MDTComparison;
import net.fina.server.mdt.entity.MDTDependentNode;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.processing.model.ProcessItem;
import net.fina.server.returns.entity.ReturnItemLite;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ReturnItemsStoreEvent extends ReturnVersionControlEvent {
    private final Collection<ProcessItem> values;
    private final long returnId;

    private final Collection<ReturnItemLite> returnItemLites;
    private final List<MDTDependentNode> allMdtDependentNodes;
    private final Map<Long, List<MDTNode>> allMdtNodesByParentId;
    private final List<MDTComparison> comparisons;

    public ReturnItemsStoreEvent(Collection<ProcessItem> values,
                                 Collection<ReturnItemLite> returnItemLites,
                                 long returnId, String processId,
                                 final List<MDTDependentNode> allMdtDependentNodes,
                                 final Map<Long, List<MDTNode>> allMdtNodesByParentId,
                                 final List<MDTComparison> comparisons) {
        super(processId, "ITEM");
        this.values = values;
        this.returnItemLites = returnItemLites;
        this.returnId = returnId;
        this.allMdtDependentNodes = allMdtDependentNodes;
        this.allMdtNodesByParentId = allMdtNodesByParentId;
        this.comparisons = comparisons;
    }

    public Collection<ProcessItem> getValues() {
        return values;
    }

    public long getReturnId() {
        return returnId;
    }

    public Collection<ReturnItemLite> getReturnItemLites() {
        return returnItemLites;
    }

    public List<MDTDependentNode> getAllMdtDependentNodes() {
        return allMdtDependentNodes;
    }

    public Map<Long, List<MDTNode>> getAllMdtNodesByParentId() {
        return allMdtNodesByParentId;
    }

    public List<MDTComparison> getComparisons() {
        return comparisons;
    }
}
