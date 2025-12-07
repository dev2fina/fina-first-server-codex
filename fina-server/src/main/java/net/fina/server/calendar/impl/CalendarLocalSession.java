package net.fina.server.calendar.impl;

import net.fina.server.calendar.api.CalendarLocal;
import net.fina.server.calendar.entity.CalendarEvent;
import net.fina.server.interceptors.RecordingAuditor;

import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Stateless
@Local(CalendarLocal.class)
@Interceptors(RecordingAuditor.class)
public class CalendarLocalSession implements CalendarLocal {
    @Inject
    private EntityManager em;

    @Override
    public List<CalendarEvent> loadCalendarEvents(Date from, Date to) {
        return em.createQuery("select c from CalendarEvent c where c.date >=:from and c.date<=:to", CalendarEvent.class)
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();
    }

    @Override
    public List<CalendarEvent> loadHolidays(Date from, Date to) {
        return em.createQuery("select c from CalendarEvent c where c.date >=:from and c.date<=:to and (c.eventType=0 or c.eventType=1)", CalendarEvent.class)
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();
    }

    @Override
    public CalendarEvent save(CalendarEvent calendarEvent) {
        if (calendarEvent.getId() > 0) {
            calendarEvent = em.merge(calendarEvent);
        } else {
            em.persist(calendarEvent);
        }
        return calendarEvent;
    }

    @Override
    public List<CalendarEvent> save(List<CalendarEvent> calendarEvents) {
        for (CalendarEvent event : calendarEvents) {
            em.persist(event);
        }
        return calendarEvents;
    }

    @Override
    public CalendarEvent getById(long id) {
        return  em.find(CalendarEvent.class, id);
    }

    @Override
    public void delete(long id) {
        em.createQuery("delete from CalendarEvent c where c.id=:id")
                .setParameter("id", id)
                .executeUpdate();
    }

    @Override
    public void deleteAll(String uuid) {
        em.createQuery("delete from CalendarEvent c where c.groupUUID=:groupUUID")
                .setParameter("groupUUID", uuid)
                .executeUpdate();
    }

    @Override
    public void deleteAfter(Date fromDate, String uuid) {
        em.createQuery("delete from CalendarEvent c where c.groupUUID=:groupUUID and c.date>=:fromDate")
                .setParameter("groupUUID", uuid)
                .setParameter("fromDate", fromDate)
                .executeUpdate();
    }

    @Override
    public long getNumberOfHoliday(LocalDate from, LocalDate to) {
        //TODO fix timezone pass corect date from caller method
        return em.createQuery("select count(distinct c.date) from CalendarEvent c where c.date >=:from and c.date<=:to and (c.eventType=0 or c.eventType=1)", Long.class)
                .setParameter("from", Date.from(from.atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .setParameter("to", Date.from(to.atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .getSingleResult();
    }

    @Override
    public LocalDate getWorkingDay(LocalDate initialDate, int days) {
        Date from = localDateToDate(initialDate);
        Date to = localDateToDate(initialDate.plusDays(days* 2L));
        Set<Date> holidaySet = getHolidaySet(from, to);

        while (days != 0) {
            initialDate = initialDate.plusDays(1);
            if(to.before(localDateToDate(initialDate))) {
                holidaySet = getHolidaySet(from, localDateToDate(initialDate.plusDays(days* 2L)));
            }
            if(!holidaySet.contains(localDateToDate(initialDate))) {
                days--;
            }
        }


        return initialDate;
    }

    private Date localDateToDate(LocalDate date) {
        return Date.from(date.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    private Set<Date> getHolidaySet(Date from, Date to) {
        List<CalendarEvent> holidays = loadHolidays(from, to);
         return holidays.stream().map(CalendarEvent::getDate).collect(Collectors.toSet());
    }
}
