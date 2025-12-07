package net.fina.server.reg.impl;

import net.fina.common.client.constants.ImportStatus;
import net.fina.common.client.returns.ProcessStatus;

import java.util.Date;
import java.util.Objects;

public class RegProcessStatus {
    private ProcessStatus processStatus;
    private ImportStatus importStatus;
    private Date importStart;
    private Date importEnd;

    public RegProcessStatus(ProcessStatus processStatus, ImportStatus importStatus, Date importStart, Date importEnd) {
        this.processStatus = processStatus;
        this.importStart = importStart;
        this.importEnd = importEnd;
        this.importStatus = importStatus;
    }

    public ProcessStatus getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(ProcessStatus processStatus) {
        this.processStatus = processStatus;
    }

    public Date getImportStart() {
        return importStart;
    }

    public void setImportStart(Date importStart) {
        this.importStart = importStart;
    }

    public Date getImportEnd() {
        return importEnd;
    }

    public void setImportEnd(Date importEnd) {
        this.importEnd = importEnd;
    }

    public ImportStatus getImportStatus() {
        return importStatus;
    }

    public void setImportStatus(ImportStatus importStatus) {
        this.importStatus = importStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegProcessStatus that = (RegProcessStatus) o;
        return getProcessStatus() == that.getProcessStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProcessStatus());
    }
}
