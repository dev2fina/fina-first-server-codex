package net.fina.common.client.rvc;

public class StatusTypeXmlMetaModel {

    protected FileTypeXmlMetaModel file;
    protected String id;
    protected String returnId;
    protected String returnVersionId;
    protected String note;
    protected String userId;
    protected String statusDate;
    protected String status;

    public FileTypeXmlMetaModel getFile() {
        return file;
    }

    public void setFile(FileTypeXmlMetaModel file) {
        this.file = file;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReturnId() {
        return returnId;
    }

    public void setReturnId(String returnId) {
        this.returnId = returnId;
    }

    public String getReturnVersionId() {
        return returnVersionId;
    }

    public void setReturnVersionId(String returnVersionId) {
        this.returnVersionId = returnVersionId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(String statusDate) {
        this.statusDate = statusDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
