package net.fina.common.client.returns;

import java.io.Serializable;
import java.util.Date;

public class OverdueReturnModel implements Serializable {
    private long scheduleId;
    private long periodId;
    private String definitionCode;
    private String name;
    private String fiCode;
    private String fiName;
    private String fiType;
    private String region;
    private String address;
    private Date fromDate;
    private Date toDate;
    private String periodType;
    private String returnVersion;
    private String fileName;
    private long fileId;
    private Date uploadedTime;
    private int dueDate;
    private int dueDateHour;
    private int dueDateMinute;
    private int delay;
    private String fiLegalForm;
    private String fiIdentificationCode;

    public OverdueReturnModel() {
    }

    public OverdueReturnModel( String fiCode, int dueDate, int dueDateHour, int dueDateMinute, Date fromDate, Date toDate, String periodType, String returnVersion, String fileName, Date uploadedTime, long fileId, String fiTypeCode) {
        this.fiCode = fiCode;
        this.dueDate = dueDate;
        this.dueDateHour = dueDateHour;
        this.dueDateMinute = dueDateMinute;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.periodType = periodType;
        this.returnVersion = returnVersion;
        this.fileName = fileName;
        this.uploadedTime = uploadedTime;
        this.fileId = fileId;
        this.name = fileName;
        this.fiType = fiTypeCode;
    }

    public OverdueReturnModel(String fiCode, int dueDate, int dueDateHour, int dueDateMinute, Date fromDate, Date toDate, String periodType, String returnVersion, String fileName, Date uploadedTime, long fileId, String fiLegalForm, String fiIdentificationCode) {
        this.fiCode = fiCode;
        this.dueDate = dueDate;
        this.dueDateHour = dueDateHour;
        this.dueDateMinute = dueDateMinute;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.periodType = periodType;
        this.returnVersion = returnVersion;
        this.fileName = fileName;
        this.uploadedTime = uploadedTime;
        this.fileId = fileId;
        this.name = fileName;
        this.fiLegalForm = fiLegalForm;
        this.fiIdentificationCode = fiIdentificationCode;
    }

    public OverdueReturnModel(long scheduleId, long periodId, int delay, int delayHour, int delayMinute, Date fromDate, Date toDate, String periodTypeCOde, String fiCode, String fiType) {
        this.scheduleId = scheduleId;
        this.periodId = periodId;
        this.fiCode = fiCode;
        this.dueDate = delay;
        this.dueDateHour = delayHour;
        this.dueDateMinute = delayMinute;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.periodType = periodTypeCOde;
        this.fiType = fiType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFiCode() {
        return fiCode;
    }

    public void setFiCode(String fiCode) {
        this.fiCode = fiCode;
    }

    public String getFiName() {
        return fiName;
    }

    public void setFiName(String fiName) {
        this.fiName = fiName;
    }

    public String getFiType() {
        return fiType;
    }

    public void setFiType(String fiType) {
        this.fiType = fiType;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getPeriodType() {
        return periodType;
    }

    public void setPeriodType(String periodType) {
        this.periodType = periodType;
    }

    public String getReturnVersion() {
        return returnVersion;
    }

    public void setReturnVersion(String returnVersion) {
        this.returnVersion = returnVersion;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileId() {
        return fileId;
    }

    public void setFileId(long fileId) {
        this.fileId = fileId;
    }

    public Date getUploadedTime() {
        return uploadedTime;
    }

    public void setUploadedTime(Date uploadedTime) {
        this.uploadedTime = uploadedTime;
    }

    public int getDueDate() {
        return dueDate;
    }

    public void setDueDate(int dueDate) {
        this.dueDate = dueDate;
    }

    public int getDueDateHour() {
        return dueDateHour;
    }

    public void setDueDateHour(int dueDateHour) {
        this.dueDateHour = dueDateHour;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getDueDateMinute() {
        return dueDateMinute;
    }

    public void setDueDateMinute(int dueDateMinute) {
        this.dueDateMinute = dueDateMinute;
    }

    public String getFiLegalForm() {
        return fiLegalForm;
    }

    public void setFiLegalForm(String fiLegalForm) {
        this.fiLegalForm = fiLegalForm;
    }

    public String getFiIdentificationCode() {
        return fiIdentificationCode;
    }

    public void setFiIdentificationCode(String fiIdentificationCode) {
        this.fiIdentificationCode = fiIdentificationCode;
    }

    public long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public long getPeriodId() {
        return periodId;
    }

    public void setPeriodId(long periodId) {
        this.periodId = periodId;
    }

    public String getDefinitionCode() {
        return definitionCode;
    }

    public void setDefinitionCode(String definitionCode) {
        this.definitionCode = definitionCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OverdueReturnModel that = (OverdueReturnModel) o;
        return getScheduleId() == that.getScheduleId();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (scheduleId ^ (scheduleId >>> 32));
        return result;
    }
}
