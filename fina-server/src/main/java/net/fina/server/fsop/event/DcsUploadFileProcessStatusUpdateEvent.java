package net.fina.server.fsop.event;

public class DcsUploadFileProcessStatusUpdateEvent {

    private final long fileId;

    public DcsUploadFileProcessStatusUpdateEvent(long fileId) {
        this.fileId = fileId;
    }

    public long getFileId() {
        return fileId;
    }
}
