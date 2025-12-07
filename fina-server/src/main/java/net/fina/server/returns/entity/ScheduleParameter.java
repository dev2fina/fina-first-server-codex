package net.fina.server.returns.entity;

public class ScheduleParameter {

    Long id;
    Long fiId;
    Long periodId;
    Long returnDefinitionId;
    Integer version;
    Integer delay;
    Integer delayHour;
    Integer delayMinute;
    String comment;

    public ScheduleParameter() {
    }

    public ScheduleParameter(Long id, Long fiId, Long periodId, Long returnDefinitionId, Integer version, Integer delay, Integer delayHour, Integer delayMinute, String comment) {
        this.fiId = fiId;
        this.periodId = periodId;
        this.returnDefinitionId = returnDefinitionId;
        this.id = id;
        this.delay = delay;
        this.delayHour = delayHour;
        this.delayMinute = delayMinute;
        this.version = version;
        this.comment = comment;
    }

    public Long getFiId() {
        return fiId;
    }

    public void setFiId(Long fiId) {
        this.fiId = fiId;
    }

    public Long getPeriodId() {
        return periodId;
    }

    public void setPeriodId(Long periodId) {
        this.periodId = periodId;
    }

    public Long getReturnDefinitionId() {
        return returnDefinitionId;
    }

    public void setReturnDefinitionId(Long returnDefinitionId) {
        this.returnDefinitionId = returnDefinitionId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getDelay() {
        return delay;
    }

    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    public Integer getDelayHour() {
        return delayHour;
    }

    public void setDelayHour(Integer delayHour) {
        this.delayHour = delayHour;
    }

    public Integer getDelayMinute() {
        return delayMinute;
    }

    public void setDelayMinute(Integer delayMinute) {
        this.delayMinute = delayMinute;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "ScheduleParameter{" +
                "id=" + id +
                ", fiId=" + fiId +
                ", periodId=" + periodId +
                ", returnDefinitionId=" + returnDefinitionId +
                ", version=" + version +
                ", delay=" + delay +
                ", comment=" + comment +
                '}';
    }
}
