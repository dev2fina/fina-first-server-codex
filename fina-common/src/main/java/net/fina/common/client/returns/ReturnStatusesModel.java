package net.fina.common.client.returns;

import java.io.Serializable;
import java.util.Date;

public class ReturnStatusesModel implements Serializable {
    private long id;
    private long fiId;
    private String fiCode;
    private long definitionId;
    private String definitionCode;
    private ProcessStatus status;
    private Date statusDate;

    public ReturnStatusesModel(long id, long fiId, String fiCode, long definitionId, String definitionCode, ProcessStatus status, Date statusDate) {
        this.id = id;
        this.fiId = fiId;
        this.fiCode = fiCode;
        this.definitionId = definitionId;
        this.definitionCode = definitionCode;
        this.status = status;
        this.statusDate = statusDate;
    }

    public ReturnStatusesModel() {
    }


    public ReturnStatusesModel(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getFiId() {
        return fiId;
    }

    public void setFiId(long fiId) {
        this.fiId = fiId;
    }

    public String getFiCode() {
        return fiCode;
    }

    public void setFiCode(String fiCode) {
        this.fiCode = fiCode;
    }

    public String getDefinitionCode() {
        return definitionCode;
    }

    public void setDefinitionCode(String definitionCode) {
        this.definitionCode = definitionCode;
    }

    public ProcessStatus getStatus() {
        return status;
    }

    public void setStatus(ProcessStatus status) {
        this.status = status;
    }

    public long getDefinitionId() {
        return definitionId;
    }

    public void setDefinitionId(long definitionId) {
        this.definitionId = definitionId;
    }

    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }
}
