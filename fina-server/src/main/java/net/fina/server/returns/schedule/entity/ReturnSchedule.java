package net.fina.server.returns.schedule.entity;

import net.fina.auditlog.api.Audited;
import net.fina.common.shared.schedule.ScheduledTaskStatus;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import net.fina.common.client.filter.ScheduleFilter;

@Entity(name = "IN_RETURNS_SCHEDULE")
@Table(name = "IN_RETURNS_SCHEDULE")
public class ReturnSchedule implements Serializable, Audited {

    @Id
    @SequenceGenerator(name = "in_returns_schedule_sequence", sequenceName = "in_returns_schedule_sequence", allocationSize = 1)
    @GeneratedValue(generator = "in_returns_schedule_sequence", strategy = GenerationType.SEQUENCE)
    @Column(name = "ID")
    private int id;
    
    @Column(name = "PARENTID")
    private int parentId;
    
    @Column(name = "TASKNAME")
    private String taskName;

    @Column(name = "ONDEMAND")
    private Integer onDemand;

    @Column(name = "SCHEDULETIME")
    private Date scheduleTime;
    
    @Column(name = "SCHEDULEID")
    private long scheduleId;
    
    @Column(name = "VERSIONID")
    private long versionId;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "STATUS")
    private ScheduledTaskStatus status;

    @Column(name = "MESSAGE")
    private String message;
    
    @Column(name = "USERID")
    private long userId;
    
    @Transient
    private Map<ScheduleFilter, Object> filterMap;

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

    public Integer getOnDemand() {
        return onDemand;
    }

    public void setOnDemand(Integer onDemand) {
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

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Map<ScheduleFilter, Object> getFilterMap() {
        return filterMap;
    }

    public void setFilterMap(Map<ScheduleFilter, Object> filterMap) {
        this.filterMap = filterMap;
    }
    

    @Override
    public String toString() {
        return "ReturnSchedule{" + "id=" + id + ", parentId=" + parentId + ", taskName=" + taskName + ", onDemand=" + onDemand + ", scheduleTime=" + scheduleTime + ", scheduleId=" + scheduleId + ", versionId=" + versionId + ", status=" + status + ", message=" + message + ", userId=" + userId + '}';
    }
}
