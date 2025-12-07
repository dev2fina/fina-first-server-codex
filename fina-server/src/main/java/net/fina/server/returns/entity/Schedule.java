package net.fina.server.returns.entity;

import jakarta.persistence.*;
import net.fina.auditlog.api.Audited;
import net.fina.server.fi.entity.Fi;
import net.fina.server.i18n.helper.Description;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;

@Entity(name = "IN_SCHEDULES")
@Table(name = "IN_SCHEDULES")
@NamedQueries({@NamedQuery(name = "loadSchedules", query = "select s from IN_SCHEDULES as s ")})
public class Schedule implements Serializable, Audited {

    @Id
    @SequenceGenerator(name = "in_schedules_sequence", sequenceName = "in_schedules_sequence", allocationSize = 1)
    @GeneratedValue(generator = "in_schedules_sequence", strategy = GenerationType.SEQUENCE)
    @Column(name = "ID")
    private long id;

    @Version
    @Column(name = "OPTLOCK")
    private Integer version;

    @Column(name = "DELAY")
    private int delay;

    @Column(name = "DELAY_HOUR")
    private int delayHour;

    @Column(name = "DELAY_MINUTE")
    private int delayMinute;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BANKID")
    private Fi fi;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEFINITIONID")
    private ReturnDefinition returnDefinition;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERIODID")
    private Period period;

    @Column(name = "\"COMMENT\"")
    private String comment;

    @Column(name = "DELAY_TO_DATE")
    private Date delayToDate;


    public Schedule() {
    }

    public Schedule(long id, Integer version) {
        this.id = id;
        this.version = version;
    }

    public Schedule(long id, Integer version, ReturnDefinition returnDefinition) {
        this.id = id;
        this.version = version;
        this.returnDefinition = returnDefinition;
    }

    public Schedule(long id, Integer version, ReturnDefinition returnDefinition,long fiId,long periodId,Date fromDate,Date toDate,long periodTypeId) {
        this.id = id;
        this.version = version;
        this.returnDefinition = returnDefinition;
        this.fi=new Fi(fiId);
        this.period=new Period(periodId,fromDate,toDate,0);
        this.period.setPeriodType(new PeriodType(periodTypeId));
    }


    public Schedule(long id, long definitionId, String definitionCode, Description definitionName,
                    long periodId, Date fromDate, Date toDate,
                    long periodTypeId, String periodTypeCode, Description periodTypeName,
                    long fiId, String fiCode, Description fiName,
                    int delay, int delayHour, int delayMinute, String comment,
                    long returnTypeId, String returnTypeCode,
                    int scheduleVersion,
                    int periodVersion,
                    int periodTypeVersion,
                    int definitionVersion,
                    int returnTypeVersion,
                    Timestamp delayToDateTimestamp
    ) {
        this.id = id;
        this.delay = delay;
        this.delayHour = delayHour;
        this.delayMinute = delayMinute;
        this.comment = comment;
        this.returnDefinition = new ReturnDefinition(definitionId, definitionVersion, definitionCode, definitionName);
        returnDefinition.setReturnType(new ReturnType(returnTypeId, returnTypeCode, returnTypeVersion));
        this.period = new Period(periodId, fromDate, toDate, periodVersion);
        this.period.setPeriodType(new PeriodType(periodTypeId, periodTypeCode, periodTypeName, periodTypeVersion));
        this.fi = new Fi(fiId, fiCode, fiName);
        this.version = scheduleVersion;
        this.delayToDate = delayToDateTimestamp;
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

    public Fi getFi() {
        return fi;
    }

    public void setFi(Fi fi) {
        this.fi = fi;
    }

    public ReturnDefinition getReturnDefinition() {
        return returnDefinition;
    }

    public void setReturnDefinition(ReturnDefinition returnDefinition) {
        this.returnDefinition = returnDefinition;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getDelayToDate() {
        return delayToDate;
    }

    public void setDelayToDate(Date delayToDate) {
        this.delayToDate = delayToDate;
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "id=" + id +
                ", version=" + version +
                ", delay=" + delay +
                ", delayHour=" + delayHour +
                ", delayMinute=" + delayMinute +
                ", period=" + period +
                ", returnDefinition=" + returnDefinition +
                ", fi=" + fi +
                ", comment=" + comment +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Schedule)) return false;
        Schedule other = (Schedule) obj;
        return Objects.equals(this.getId(), other.getId());
    }
}
