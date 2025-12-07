package net.fina.server.fsop.api;

import net.fina.common.client.returns.ProcessStatus;
import net.fina.server.fsop.event.DcsUploadFileProcessStatusUpdateEvent;
import net.fina.server.fsop.event.ImportedReturnUpdateEvent;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.returns.entity.DefinitionTable;
import net.fina.server.returns.entity.Return;
import net.fina.server.fsop.model.FsopImportedReturnMetaModel;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface FsopImportStoreLocal {

    List<FsopImportedReturnMetaModel> getFileImportedReturns(long fileId);

    void updateImportedReturns(ImportedReturnUpdateEvent event);

    void sendDcsUploadFileProcessStatusUpdateMessage(DcsUploadFileProcessStatusUpdateEvent event);

    Return createReturn(long scheduleId, long versionId);

    long findReturnVersionByCode(String versionCode);

    long findSchedule(Date fromDate, Date toDate, String fiCode, String definitionCode);

    Return findReturn(long scheduleId, long versionId);

    void saveReturnXmlRelation(int importedReturnId, long returnId);

    List<ProcessStatus> getReturnStatuses(long returnId, long versionId);

    Map<Long, List<MDTNode>> loadAllNodesByParentId();

    List<DefinitionTable> getDefinitionTables(long scheduleId);

    void updateLastVersion(long scheduleId, long lastVersionId);
}
