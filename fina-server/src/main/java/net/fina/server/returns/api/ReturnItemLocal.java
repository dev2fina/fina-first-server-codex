package net.fina.server.returns.api;

import net.fina.server.returns.entity.ReturnItemLite;

import java.util.Collection;
import java.util.List;

public interface ReturnItemLocal {

    void insertReturnItems(List<ReturnItemLite> items);

    void deleteReturnItems(List<ReturnItemLite> items);

    void deleteReturnItemsBatch(long returnId, long versionId, long tableId, long rowNUmber, List<Long> nodeIds);

    void updateReturnItemRowNumber(ReturnItemLite item, long rowNumber);

    void updateReturnItemRowNumberBatch(long returnId, long versionId, long tableId, long fromRowNUmber, List<Long> nodeIds, boolean increase);

    void updateReturnItems(Collection<ReturnItemLite> values, long returnId, long versionId, boolean lock, String note, String processId);

    long getItemMaxId(long returnId, long versionId);
}
