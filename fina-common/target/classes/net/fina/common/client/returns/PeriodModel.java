package net.fina.common.client.returns;

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 7/3/13
 * Time: 3:59 PM
 */
public class PeriodModel implements Serializable {

    private long id;
    private Integer version;
    private long periodNumber;
    private Date fromDate;
    private Date toDate;
    private PeriodTypeModel periodType;

    public PeriodModel() {
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

    public Date getToDate() {
        return toDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public PeriodTypeModel getPeriodType() {
        return periodType;
    }

    public void setPeriodType(PeriodTypeModel periodType) {
        this.periodType = periodType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PeriodModel that = (PeriodModel) o;
        if (id != that.id) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "PeriodModel{" +
                "id=" + id +
                ", version=" + version +
                ", periodNumber=" + periodNumber +
                ", fromDate=" + fromDate +
                ", toDate=" + toDate +
                ", periodType=" + periodType +
                '}';
    }
}
