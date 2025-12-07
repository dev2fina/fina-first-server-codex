package net.fina.common.client.returns;

import net.fina.common.client.fis.FiModel;

import java.io.Serializable;

public class ScheduleModelSimple implements Serializable {

    private long id;
    private int delay;
    private int delayHour;
    private int delayMinute;
    private long fiId;
    private long returnDefinitionId;
    private long periodId;

    public ScheduleModelSimple() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public long getPeriodId() {
        return periodId;
    }

    public void setPeriodId(long periodId) {
        this.periodId = periodId;
    }
}
