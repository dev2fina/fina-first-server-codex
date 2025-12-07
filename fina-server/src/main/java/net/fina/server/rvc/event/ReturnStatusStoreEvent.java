package net.fina.server.rvc.event;

import net.fina.common.client.returns.ProcessStatus;

import java.util.Date;

public class ReturnStatusStoreEvent extends ReturnVersionControlEvent {

    private final long id;
    private final long returnId;
    private final long versionId;
    private final ProcessStatus status;
    private final Date statusDate;
    private final long userId;
    private final String note;

    public ReturnStatusStoreEvent(String processId, long id, long returnId, long versionId, ProcessStatus status, Date statusDate, long userId, String note) {
        super(processId, "STATUS");
        this.id = id;
        this.returnId = returnId;
        this.versionId = versionId;
        this.status = status;
        this.statusDate = statusDate;
        this.userId = userId;
        this.note = note;
    }

    public long getId() {
        return id;
    }

    public long getReturnId() {
        return returnId;
    }

    public long getVersionId() {
        return versionId;
    }

    public ProcessStatus getStatus() {
        return status;
    }

    public Date getStatusDate() {
        return statusDate;
    }

    public long getUserId() {
        return userId;
    }

    public String getNote() {
        return note;
    }
}
