package net.fina.common.client.returns;

import net.fina.common.shared.schedule.ScheduledTaskStatus;

import java.util.Date;

public class ReturnScheduleTaskDetail {
    private long id;
    private String definitionCode;
    private Date periodFrom;
    private Date periodTo;
    private String fiCode;
    private int dueDate;
    private ScheduledTaskStatus status;
    private String versionCode;
    private String message;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDefinitionCode() {
        return definitionCode;
    }

    public void setDefinitionCode(String definitionCode) {
        this.definitionCode = definitionCode;
    }

    public Date getPeriodFrom() {
        return periodFrom;
    }

    public void setPeriodFrom(Date periodFrom) {
        this.periodFrom = periodFrom;
    }

    public Date getPeriodTo() {
        return periodTo;
    }

    public void setPeriodTo(Date periodTo) {
        this.periodTo = periodTo;
    }

    public String getFiCode() {
        return fiCode;
    }

    public void setFiCode(String fiCode) {
        this.fiCode = fiCode;
    }

    public int getDueDate() {
        return dueDate;
    }

    public void setDueDate(int dueDate) {
        this.dueDate = dueDate;
    }

    public ScheduledTaskStatus getStatus() {
        return status;
    }

    public void setStatus(ScheduledTaskStatus status) {
        this.status = status;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
