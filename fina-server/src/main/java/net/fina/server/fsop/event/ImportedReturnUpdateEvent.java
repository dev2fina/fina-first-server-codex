package net.fina.server.fsop.event;


import net.fina.common.client.dcs.UploadFileStatus;
import net.fina.server.fsop.entity.UploadFileQueue;
import net.fina.server.fsop.model.FsopImportedReturnMetaModel;

import java.util.List;

public class ImportedReturnUpdateEvent {
    private final List<FsopImportedReturnMetaModel> importedReturns;
    private final UploadFileQueue uploadFileQueue;

    private String message;
    private UploadFileStatus status;

    public ImportedReturnUpdateEvent(List<FsopImportedReturnMetaModel> importedReturns, UploadFileQueue uploadFileQueue) {
        this.importedReturns = importedReturns;
        this.uploadFileQueue = uploadFileQueue;
    }

    public List<FsopImportedReturnMetaModel> getImportedReturns() {
        return importedReturns;
    }

    public UploadFileQueue getUploadFileQueue() {
        return uploadFileQueue;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UploadFileStatus getStatus() {
        return status;
    }

    public void setStatus(UploadFileStatus status) {
        this.status = status;
    }
}
