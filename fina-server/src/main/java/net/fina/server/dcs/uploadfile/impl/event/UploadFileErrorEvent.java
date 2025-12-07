package net.fina.server.dcs.uploadfile.impl.event;

public class UploadFileErrorEvent  {
    private final long uploadFileId;
    private final String reason;

    public UploadFileErrorEvent(long uploadFileId, String reason) {
        this.uploadFileId = uploadFileId;
        this.reason = reason;
    }

    public long getUploadFileId() {
        return uploadFileId;
    }

    public String getReason() {
        return reason;
    }
}
