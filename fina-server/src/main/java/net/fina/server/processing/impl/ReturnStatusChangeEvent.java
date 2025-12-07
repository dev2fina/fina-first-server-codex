package net.fina.server.processing.impl;

import net.fina.common.client.returns.ProcessResult;
import net.fina.common.client.returns.ProcessStatus;

/**
 * nikoloz on 8/17/15.
 */
public class ReturnStatusChangeEvent {

    private final String processId;
    private final long returnId;
    private final ProcessStatus status;
    private final long versionId;
    private final long userId;
    private final String note;

    public ReturnStatusChangeEvent(long returnId, ProcessStatus status, long versionId, long userId, String note, String processId) {
        this.processId = processId;
        this.returnId = returnId;
        this.status = status;
        this.versionId = versionId;
        this.userId = userId;
        this.note = note;
    }

    public long getReturnId() {
        return returnId;
    }

    public ProcessStatus getStatus() {
        return status;
    }

    public long getVersionId() {
        return versionId;
    }

    public long getUserId() {
        return userId;
    }

    public String getNote() {
        return note;
    }

    public String getProcessId() {
        return processId;
    }
}
