package net.fina.server.fsop.event;

import net.fina.server.fsop.entity.UploadFileQueue;
import net.fina.server.returns.entity.ImportedReturn;

import java.util.List;

public class ImportedReturnSaveEvent {
    private final UploadFileQueue uploadFileQueue;
    private final List<ImportedReturn> xmls;

    public ImportedReturnSaveEvent(UploadFileQueue uploadFileQueue, List<ImportedReturn> xmls) {
        this.uploadFileQueue = uploadFileQueue;
        this.xmls = xmls;
    }

    public UploadFileQueue getUploadFileQueue() {
        return uploadFileQueue;
    }

    public List<ImportedReturn> getXmls() {
        return xmls;
    }
}
