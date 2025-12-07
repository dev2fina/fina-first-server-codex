package net.fina.server.returns.entity;

import net.fina.common.client.returns.ProcessStatus;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class ReturnStatusLiteId implements Serializable {
    private long id;
    private long returnId;
    private long versionId;
    private ProcessStatus status;
    private Date statusDate;
    private long userId;
    private String note;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getReturnId() {
        return returnId;
    }

    public void setReturnId(long returnId) {
        this.returnId = returnId;
    }

    public long getVersionId() {
        return versionId;
    }

    public void setVersionId(long versionId) {
        this.versionId = versionId;
    }

    public ProcessStatus getStatus() {
        return status;
    }

    public void setStatus(ProcessStatus status) {
        this.status = status;
    }

    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReturnStatusLiteId that = (ReturnStatusLiteId) o;

        if (id != that.id) return false;
        if (returnId != that.returnId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (returnId ^ (returnId >>> 32));
        return result;
    }
}
