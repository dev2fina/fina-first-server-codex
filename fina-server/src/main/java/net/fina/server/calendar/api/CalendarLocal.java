package net.fina.server.calendar.api;

import net.fina.server.calendar.entity.CalendarEvent;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface CalendarLocal {
    List<CalendarEvent> loadCalendarEvents(Date from, Date to);
    List<CalendarEvent> loadHolidays(Date from, Date to);
    CalendarEvent save(CalendarEvent calendarEvent);
    List<CalendarEvent> save(List<CalendarEvent> calendarEvents);
    CalendarEvent getById(long id);
    void delete(long id);
    void deleteAll(String uuid);
    void deleteAfter(Date fromDate, String uuid);
    long getNumberOfHoliday(LocalDate from, LocalDate to);
    LocalDate getWorkingDay(LocalDate initialDate, int days);
}
