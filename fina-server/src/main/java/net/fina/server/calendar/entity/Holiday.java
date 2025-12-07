package net.fina.server.calendar.entity;


import java.util.Date;

public class Holiday {
    private Date from;
    private Date to;
    private Integer date;
    private HolidayType holidayType;
    private CalendarPeriodType periodType;

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public Integer getDate() {
        return date;
    }

    public void setDate(Integer date) {
        this.date = date;
    }

    public HolidayType getHolidayType() {
        return holidayType;
    }

    public void setHolidayType(HolidayType holidayType) {
        this.holidayType = holidayType;
    }

    public CalendarPeriodType getPeriodType() {
        return periodType;
    }

    public void setPeriodType(CalendarPeriodType periodType) {
        this.periodType = periodType;
    }
}
