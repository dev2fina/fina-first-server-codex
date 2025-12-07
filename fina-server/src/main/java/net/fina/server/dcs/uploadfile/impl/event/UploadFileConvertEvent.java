package net.fina.server.dcs.uploadfile.impl.event;

public class UploadFileConvertEvent {

    private final long uploadFileId;
    private final String languageCode;
    private final String fileName;

    public UploadFileConvertEvent(long uploadFileId, String languageCode, String fileName) {
        this.uploadFileId = uploadFileId;
        this.languageCode = languageCode;
        this.fileName = fileName;
    }

    public long getUploadFileId() {
        return uploadFileId;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public String getFileName() {
        return fileName;
    }
}
