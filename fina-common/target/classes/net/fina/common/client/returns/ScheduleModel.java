package net.fina.common.client.returns;

import net.fina.common.client.fis.FiModel;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 7/3/13
 * Time: 3:57 PM
 */
public class ScheduleModel implements Serializable {

    private int innerId;
    private static int COUNTER = 0;

    private long id;
    private Integer version;
    //Due Date
    private int delay;
    private int delayHour;
    private int delayMinute;
    private FiModel fi;
    private ReturnDefinitionModel returnDefinition;
    private PeriodModel period;
    private String comment;

    public ScheduleModel() {
        super();
        innerId = COUNTER++;
    }

    public int getInnerId() {
        return innerId;
    }

    public void setInnerId(int innerId) {
        this.innerId = innerId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
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

    public FiModel getFi() {
        return fi;
    }

    public void setFi(FiModel fi) {
        this.fi = fi;
    }

    public ReturnDefinitionModel getReturnDefinition() {
        return returnDefinition;
    }

    public void setReturnDefinition(ReturnDefinitionModel returnDefinition) {
        this.returnDefinition = returnDefinition;
    }

    public PeriodModel getPeriod() {
        return period;
    }

    public void setPeriod(PeriodModel period) {
        this.period = period;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScheduleModel model = (ScheduleModel) o;

        if (id != model.id) return false;
        if (innerId != model.innerId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = innerId;
        result = 31 * result + (int) (id ^ (id >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "ScheduleModel{" +
                "id=" + id +
                ", version=" + version +
                ", delay=" + delay +
                ", delayHour=" + delayHour +
                ", delayMinute=" + delayMinute +
                ", fi=" + (fi == null ? null : fi.getCode()) +
                ", returnDefinition=" + (returnDefinition == null ? null : returnDefinition.getCode()) +
                ", period=" + period +
                ", comment=" + comment +
                '}';
    }
}
