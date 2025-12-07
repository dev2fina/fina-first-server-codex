package net.fina.server.processing.impl;

import net.fina.common.client.returns.ProcessResult;
import net.fina.server.fsop.entity.UploadFileQueue;
import net.fina.server.fsop.model.FsopImportedReturnMetaModel;
import net.fina.server.processing.model.ComparisonItem;
import net.fina.server.processing.model.ProcessItem;
import net.fina.server.returns.entity.Return;
import net.fina.common.server.StatisticsLogger;

import java.util.List;
import java.util.Map;

public class FspFileProcessDoneEvent {
    public final Map<Long, List<ComparisonItem>> comparisons;
    public final StatisticsLogger statLog;
    public final Map<FsopImportedReturnMetaModel, net.fina.server.returns.entity.Return> returns;
    public final Map<Long, ProcessResult> processResultMap;
    public final Map<Long, ProcessItem> packageReturnItemsById;
    public final long userId;
    public final long langId;
    public final List<FsopImportedReturnMetaModel> importedReturns;
    public final UploadFileQueue queueFile;


    public FspFileProcessDoneEvent(Map<FsopImportedReturnMetaModel, Return> returns, Map<Long, ProcessResult> processResultMap,
                                   Map<Long, List<ComparisonItem>> comparisons, Map<Long, ProcessItem> packageReturnItemsById,
                                   List<FsopImportedReturnMetaModel> importedReturns, UploadFileQueue queueFile,
                                   long langId, StatisticsLogger statLog) {
        this.returns = returns;
        this.processResultMap = processResultMap;
        this.comparisons = comparisons;
        this.packageReturnItemsById = packageReturnItemsById;
        this.userId = queueFile.getUserId();
        this.langId = langId;
        this.statLog = statLog;
        this.importedReturns = importedReturns;
        this.queueFile = queueFile;
    }
}
