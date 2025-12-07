package net.fina.server.calendar.model;

import net.fina.server.calendar.entity.CalendarMonth;
import net.fina.server.calendar.entity.CalendarPeriodType;
import net.fina.server.calendar.entity.CalendarWeekDay;
import net.fina.server.calendar.entity.EventType;

import java.util.Date;

public class CalendarEventMultipleCreateMetaModel {
    private CalendarMonth month;
    private Integer day;
    private EventType eventType;
    private String comment;
    private CalendarPeriodType calendarPeriodType;
    private CalendarWeekDay calendarWeekDay;
    private Date from;
    private Date to;

    public CalendarMonth getMonth() {
        return month;
    }

    public void setMonth(CalendarMonth month) {
        this.month = month;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public CalendarPeriodType getCalendarPeriodType() {
        return calendarPeriodType;
    }

    public void setCalendarPeriodType(CalendarPeriodType calendarPeriodType) {
        this.calendarPeriodType = calendarPeriodType;
    }

    public CalendarWeekDay getCalendarWeekDay() {
        return calendarWeekDay;
    }

    public void setCalendarWeekDay(CalendarWeekDay calendarWeekDay) {
        this.calendarWeekDay = calendarWeekDay;
    }

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
}
