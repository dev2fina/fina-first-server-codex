package net.fina.server.returns.entity;

import net.fina.auditlog.api.Audited;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity(name = "IN_PERIODS")
@Table(name = "IN_PERIODS")
@NamedQueries({
        @NamedQuery(name = "Period.findAll", query = "select c from IN_PERIODS as c "),
        @NamedQuery(name = "Period.checkUse", query = "select s.id from IN_SCHEDULES s where s.period.id=:periodId")
})
public class Period implements Serializable, Audited {

    @Id
    @SequenceGenerator(name = "in_periods_sequence", sequenceName = "in_periods_sequence", allocationSize = 1)
    @GeneratedValue(generator = "in_periods_sequence", strategy = GenerationType.SEQUENCE)
    @Column(name = "ID")
    private long id;

    @Version
    @Column(name = "OPTLOCK")
    private Integer version;

    @Column(name = "PERIODNUMBER")
    private long periodNumber;

    @Column(name = "FROMDATE")
    @Temporal(TemporalType.DATE)
    private Date fromDate;

    @Column(name = "TODATE")
    @Temporal(TemporalType.DATE)
    private Date toDate;

    @OneToOne
    @JoinColumn(name = "PERIODTYPEID")
    private PeriodType periodType;

    public Period() {
    }

    public Period(long id, Date fromDate, Date toDate, int version) {
        this.id = id;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.version = version;
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

    public long getPeriodNumber() {
        return periodNumber;
    }

    public void setPeriodNumber(long periodNumber) {
        this.periodNumber = periodNumber;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public PeriodType getPeriodType() {
        return periodType;
    }

    public void setPeriodType(PeriodType periodType) {
        this.periodType = periodType;
    }

    @Override
    public String toString() {
        return "Period{" +
                "id=" + id +
                ", version=" + version +
                ", periodNumber=" + periodNumber +
                ", fromDate=" + fromDate +
                ", toDate=" + toDate +
                ", periodType=" + periodType +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Period period = (Period) o;
        return getId() == period.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
