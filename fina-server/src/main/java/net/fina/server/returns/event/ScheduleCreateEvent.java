package net.fina.server.returns.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.fina.common.client.returns.ScheduleModel;

import java.io.Serializable;
import java.util.List;

public class ScheduleCreateEvent implements Serializable {
    private final String userLogin;
    private int total;
    private int progress;
    private List<ScheduleModel> notSavedSchedules;

    @JsonCreator
    public ScheduleCreateEvent(@JsonProperty("total") int total,
                               @JsonProperty("progress") int progress,
                               @JsonProperty("userLogin") String userLogin,
                               @JsonProperty("notSavedSchedules") List<ScheduleModel> notSavedSchedules) {
        this.total = total;
        this.progress = progress;
        this.userLogin = userLogin;
        this.notSavedSchedules = notSavedSchedules;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public List<ScheduleModel> getNotSavedSchedules() {
        return notSavedSchedules;
    }

    public void setNotSavedSchedules(List<ScheduleModel> notSavedSchedules) {
        this.notSavedSchedules = notSavedSchedules;
    }
}
