package net.fina.common.client.returns;

import java.io.Serializable;
import java.util.List;

public class ScheduleDueDateParameterModel implements Serializable {
    private List<Long> fiIds;
    private long rdId;
    private long returnTypeId;
    private long periodTypeId;
    private long periodFrom;
    private long periodTo;
    private int delay;
    private int delayHour;
    private int delayMinute;
    private String comment;
    private Integer newDueDate;
    private Integer newDueDateHour;
    private Integer newDueDateMinute;
    private String newComment;

    public ScheduleDueDateParameterModel() {
    }


    public List<Long> getFiIds() {
        return fiIds;
    }

    public void setFiIds(List<Long> fiIds) {
        this.fiIds = fiIds;
    }

    public long getRdId() {
        return rdId;
    }

    public void setRdId(long rdId) {
        this.rdId = rdId;
    }

    public long getReturnTypeId() {
        return returnTypeId;
    }

    public void setReturnTypeId(long returnTypeId) {
        this.returnTypeId = returnTypeId;
    }

    public long getPeriodTypeId() {
        return periodTypeId;
    }

    public void setPeriodTypeId(long periodTypeId) {
        this.periodTypeId = periodTypeId;
    }

    public long getPeriodFrom() {
        return periodFrom;
    }

    public void setPeriodFrom(long periodFrom) {
        this.periodFrom = periodFrom;
    }

    public long getPeriodTo() {
        return periodTo;
    }

    public void setPeriodTo(long periodTo) {
        this.periodTo = periodTo;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getDelayHour() {
        return delayHour;
    }

    public void setDelayHour(int delayHour) {
        this.delayHour = delayHour;
    }

    public int getDelayMinute() {
        return delayMinute;
    }

    public void setDelayMinute(int delayMinute) {
        this.delayMinute = delayMinute;
    }

    public Integer getNewDueDate() {
        return newDueDate;
    }

    public void setNewDueDate(Integer newDueDate) {
        this.newDueDate = newDueDate;
    }

    public Integer getNewDueDateHour() {
        return newDueDateHour;
    }

    public void setNewDueDateHour(Integer newDueDateHour) {
        this.newDueDateHour = newDueDateHour;
    }

    public Integer getNewDueDateMinute() {
        return newDueDateMinute;
    }

    public void setNewDueDateMinute(Integer newDueDateMinute) {
        this.newDueDateMinute = newDueDateMinute;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getNewComment() {
        return newComment;
    }

    public void setNewComment(String newComment) {
        this.newComment = newComment;
    }
}
