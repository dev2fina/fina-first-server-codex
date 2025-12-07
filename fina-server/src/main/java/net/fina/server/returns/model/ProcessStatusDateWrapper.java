package net.fina.server.returns.model;

import net.fina.common.client.returns.ProcessStatus;

import java.util.Date;

public class ProcessStatusDateWrapper {
    private ProcessStatus processStatus;

    private Date statusDate;

    public ProcessStatusDateWrapper(ProcessStatus processStatus, Date statusDate) {
        this.processStatus = processStatus;
        this.statusDate = statusDate;
    }

    public ProcessStatusDateWrapper() {
    }

    public ProcessStatus getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(ProcessStatus processStatus) {
        this.processStatus = processStatus;
    }

    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }
}
