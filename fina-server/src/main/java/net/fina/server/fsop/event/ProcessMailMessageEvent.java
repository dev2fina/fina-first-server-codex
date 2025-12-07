package net.fina.server.fsop.event;


import net.fina.server.fsop.entity.UploadFileQueue;
import net.fina.server.fsop.model.FsopImportedReturnMetaModel;

import java.util.List;

public class ProcessMailMessageEvent {
    private final UploadFileQueue uploadFileQueue;
    private final List<FsopImportedReturnMetaModel> importedReturns;

    public ProcessMailMessageEvent(UploadFileQueue uploadFileQueue, List<FsopImportedReturnMetaModel> importedReturns) {
        this.uploadFileQueue = uploadFileQueue;
        this.importedReturns = importedReturns;
    }

    public UploadFileQueue getUploadFileQueue() {
        return uploadFileQueue;
    }

    public List<FsopImportedReturnMetaModel> getImportedReturns() {
        return importedReturns;
    }
}
