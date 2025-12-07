package net.fina.server.fsop.event;


import net.fina.server.fsop.entity.UploadFileQueue;

public class RemoveUploadFileQueueEvent {
    private final UploadFileQueue uploadFileQueue;

    public RemoveUploadFileQueueEvent(UploadFileQueue uploadFileQueue) {
        this.uploadFileQueue = uploadFileQueue;
    }

    public UploadFileQueue getUploadFileQueue() {
        return uploadFileQueue;
    }
}
