package net.fina.common.shared.dashboard;

import net.fina.common.client.constants.CalendarPeriodType;

import java.util.Date;

public class NotSubmittedFileMetaModel {

    private String bankCode;
    private String bankName;
    private Date periodFrom;
    private Date periodTo;
    private CalendarPeriodType periodType;
    private Integer delay;
    private Integer delayHour;
    private Integer delayMinute;

    public NotSubmittedFileMetaModel() {}

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public Date getPeriodFrom() {
        return periodFrom;
    }

    public void setPeriodFrom(Date periodFrom) {
        this.periodFrom = periodFrom;
    }

    public Date getPeriodTo() {
        return periodTo;
    }

    public void setPeriodTo(Date periodTo) {
        this.periodTo = periodTo;
    }

    public CalendarPeriodType getPeriodType() {
        return periodType;
    }

    public void setPeriodType(CalendarPeriodType periodType) {
        this.periodType = periodType;
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
}
