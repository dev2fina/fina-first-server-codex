package net.fina.common.client.returns;

import net.fina.common.shared.schedule.ScheduledTaskStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReturnScheduleMetaModel {
    private int id;
    private int parentId;
    private String taskName;
    private boolean onDemand;
    private Date scheduleTime;
    private long scheduleId;
    private long versionId;
    private String versionCode;
    private ScheduledTaskStatus status;
    private String message;
    private String userName;
    private List<Long> scheduleIds;
    private List<Long> fiIDs;
    private List<Long> definitionIds;
    private int delay;
    private Date periodFrom;
    private Date periodTo;
    private long periodTypeId;
    private long returnTypeId;

    public ReturnScheduleMetaModel() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public boolean isOnDemand() {
        return onDemand;
    }

    public void setOnDemand(boolean onDemand) {
        this.onDemand = onDemand;
    }

    public Date getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(Date scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public long getVersionId() {
        return versionId;
    }

    public void setVersionId(long versionId) {
        this.versionId = versionId;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public ScheduledTaskStatus getStatus() {
        return status;
    }

    public void setStatus(ScheduledTaskStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<Long> getScheduleIds() {
        return scheduleIds == null ? new ArrayList<Long>() : scheduleIds;
    }

    public void setScheduleIds(List<Long> scheduleIds) {
        this.scheduleIds = scheduleIds;
    }

    public List<Long> getFiIDs() {
        return fiIDs == null ? new ArrayList<Long>() : fiIDs;
    }

    public void setFiIDs(List<Long> fiIDs) {
        this.fiIDs = fiIDs;
    }

    public List<Long> getDefinitionIds() {
        return definitionIds == null ? new ArrayList<Long>() : definitionIds;
    }

    public void setDefinitionIds(List<Long> definitionIds) {
        this.definitionIds = definitionIds;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
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

    public long getPeriodTypeId() {
        return periodTypeId;
    }

    public void setPeriodTypeId(long periodTypeId) {
        this.periodTypeId = periodTypeId;
    }

    public long getReturnTypeId() {
        return returnTypeId;
    }

    public void setReturnTypeId(long returnTypeId) {
        this.returnTypeId = returnTypeId;
    }
}
