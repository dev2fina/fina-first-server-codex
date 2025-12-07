package net.fina.server.processing.model;

import java.util.Date;

public class ProcessReturnModel {
    private long id;
    private long versionId;
    private long periodId;
    private Date fromDate;
    private Date toDate;
    private long fiId;
    private long returnDefinitionId;
    private String returnDefinitionCode;
    private String periodTypeCode;

    public ProcessReturnModel() {
    }

    public ProcessReturnModel(long id) {
        this.id = id;
    }

    public ProcessReturnModel(long id, long versionId, long periodId, Date fromDate, Date toDate, long fiId, long returnDefinitionId, String returnDefinitionCode) {
        this.id = id;
        this.versionId = versionId;
        this.periodId = periodId;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.fiId = fiId;
        this.returnDefinitionId = returnDefinitionId;
        this.returnDefinitionCode = returnDefinitionCode;
    }

    public ProcessReturnModel(long id, long versionId, long periodId, Date fromDate, Date toDate, long fiId, long returnDefinitionId, String returnDefinitionCode, String periodTypeCode) {
        this.id = id;
        this.versionId = versionId;
        this.periodId = periodId;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.fiId = fiId;
        this.returnDefinitionId = returnDefinitionId;
        this.returnDefinitionCode = returnDefinitionCode;
        this.periodTypeCode = periodTypeCode;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getVersionId() {
        return versionId;
    }

    public void setVersionId(long versionId) {
        this.versionId = versionId;
    }

    public long getPeriodId() {
        return periodId;
    }

    public void setPeriodId(long periodId) {
        this.periodId = periodId;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public long getFiId() {
        return fiId;
    }

    public void setFiId(long fiId) {
        this.fiId = fiId;
    }

    public long getReturnDefinitionId() {
        return returnDefinitionId;
    }

    public void setReturnDefinitionId(long returnDefinitionId) {
        this.returnDefinitionId = returnDefinitionId;
    }

    public String getReturnDefinitionCode() {
        return returnDefinitionCode;
    }

    public void setReturnDefinitionCode(String returnDefinitionCode) {
        this.returnDefinitionCode = returnDefinitionCode;
    }

    public String getPeriodTypeCode() {
        return periodTypeCode;
    }

    public void setPeriodTypeCode(String periodTypeCode) {
        this.periodTypeCode = periodTypeCode;
    }
}
