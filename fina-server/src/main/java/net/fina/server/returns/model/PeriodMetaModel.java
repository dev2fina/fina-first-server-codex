package net.fina.server.returns.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.fina.server.returns.entity.Period;

import java.io.Serializable;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PeriodMetaModel implements Serializable {

    private long id;
    private Integer version;
    private long periodNumber;
    private Date fromDate;
    private Date toDate;
    private PeriodTypeMetaModel periodType;

    public PeriodMetaModel() {
    }

    public PeriodMetaModel setEntity(Period period) {
        this.id = period.getId();
        this.version = period.getVersion();
        this.periodNumber = period.getPeriodNumber();
        this.fromDate = period.getFromDate();
        this.toDate = period.getToDate();

        this.periodType = new PeriodTypeMetaModel().setEntity(period.getPeriodType());

        return this;
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

    public PeriodTypeMetaModel getPeriodType() {
        return periodType;
    }

    public void setPeriodType(PeriodTypeMetaModel periodType) {
        this.periodType = periodType;
    }
}
