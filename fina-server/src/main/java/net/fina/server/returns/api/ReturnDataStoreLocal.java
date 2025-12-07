package net.fina.server.returns.api;

import net.fina.server.processing.model.ProcessItem;
import net.fina.server.returns.entity.ImportedReturn;
import net.fina.server.returns.model.RDataMetaModel;

import jakarta.xml.bind.ValidationEventHandler;
import java.util.Map;

public interface ReturnDataStoreLocal {
    RDataMetaModel loadReturnData(long returnId, long versionId, long langId);

    RDataMetaModel loadReturnData(long returnId, long versionId, long langId, boolean review);

    RDataMetaModel loadReturnData(long returnId, long versionId, long langId, boolean review, Map<Long, ProcessItem> allReturnItems);

    RDataMetaModel loadReturnDataFromXml(ImportedReturn importedReturn, ValidationEventHandler handler, long langId);

    byte[] loadReturnDataView(int importedReturnId, long langId);

    void checkReturnPermission(long returnId);
}
