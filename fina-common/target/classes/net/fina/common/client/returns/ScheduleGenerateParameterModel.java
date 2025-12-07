package net.fina.common.client.returns;

import net.fina.common.client.fis.FiModel;

import java.io.Serializable;
import java.util.List;

public class ScheduleGenerateParameterModel implements Serializable {
    private List<Long> fis;
    private List<Long> definitions;
    private List<Long> periods;
    private int delay;
    private int delayHour;
    private int delayMinute;

    public ScheduleGenerateParameterModel() {
    }

    public ScheduleGenerateParameterModel(List<Long> fis, List<Long> definitions, List<Long> periods, int delay, int delayHour, int delayMinute) {
        this.fis = fis;
        this.definitions = definitions;
        this.periods = periods;
        this.delay = delay;
        this.delayHour = delayHour;
        this.delayMinute = delayMinute;
    }

    public List<Long> getFis() {
        return fis;
    }

    public void setFis(List<Long> fis) {
        this.fis = fis;
    }

    public List<Long> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(List<Long> definitions) {
        this.definitions = definitions;
    }

    public List<Long> getPeriods() {
        return periods;
    }

    public void setPeriods(List<Long> periods) {
        this.periods = periods;
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
}
