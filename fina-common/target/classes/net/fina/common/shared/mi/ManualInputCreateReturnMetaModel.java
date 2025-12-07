package net.fina.common.shared.mi;

import java.io.Serializable;

public class ManualInputCreateReturnMetaModel implements Serializable {
    private long scheduleId;
    private long versionId;
    private long userId;


    public ManualInputCreateReturnMetaModel() {
    }

    public ManualInputCreateReturnMetaModel(long scheduleId, long versionId, long userId) {
        this.scheduleId = scheduleId;
        this.versionId = versionId;
        this.userId = userId;
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

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ManualInputCreateReturnMetaModel that = (ManualInputCreateReturnMetaModel) o;
        return scheduleId == that.scheduleId && versionId == that.versionId && userId == that.userId;
    }

}
