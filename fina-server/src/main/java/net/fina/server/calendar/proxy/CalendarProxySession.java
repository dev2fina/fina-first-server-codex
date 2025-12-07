package net.fina.server.calendar.proxy;

import net.fina.common.client.constants.PermissionIdNames;
import net.fina.server.calendar.api.CalendarLocal;
import net.fina.server.calendar.entity.CalendarEvent;
import net.fina.server.calendar.entity.CalendarEventDeleteType;
import net.fina.server.calendar.model.CalendarEventMetaModel;
import net.fina.server.calendar.model.CalendarEventMetaModelHelper;
import net.fina.server.calendar.model.CalendarEventMultipleCreateMetaModel;
import org.jboss.ejb3.annotation.SecurityDomain;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Stateless
@SecurityDomain("FinASecurityDomain")
@RolesAllowed(PermissionIdNames.FINA_CALENDAR_REVIEW)
public class CalendarProxySession {
    @Inject
    private CalendarLocal calendarLocal;

    public List<CalendarEventMetaModel> loadCalendarEvents(Date from, Date to) {
        return CalendarEventMetaModelHelper.fromEntity(calendarLocal.loadCalendarEvents(from, to));
    }

    public List<CalendarEventMetaModel> loadHolidays(Date from, Date to) {
        return CalendarEventMetaModelHelper.fromEntity(calendarLocal.loadHolidays(from, to));
    }

    @RolesAllowed(PermissionIdNames.FINA_CALENDAR_AMEND)
    public CalendarEventMetaModel save(CalendarEventMetaModel calendarEvent) {
        return CalendarEventMetaModelHelper.fromEntity(
                calendarLocal.save(
                        CalendarEventMetaModelHelper.toEntity(calendarEvent)
                ));
    }

    @RolesAllowed(PermissionIdNames.FINA_CALENDAR_AMEND)
    public List<CalendarEventMetaModel> createMultiple(CalendarEventMultipleCreateMetaModel createModel) {
        List<CalendarEvent> calendarEvents = new ArrayList<>();

        Date from = createModel.getFrom();
        Date to = createModel.getTo();

        Calendar a = getCalendar(from);
        Calendar b = getCalendar(to);

        String uuid = UUID.randomUUID().toString();

        switch (createModel.getCalendarPeriodType()) {
            case ANNUAL:
                for (int i = a.get(Calendar.YEAR); i <= b.get(Calendar.YEAR); i++) {
                    Date d = new GregorianCalendar(i, createModel.getMonth().ordinal(), createModel.getDay()).getTime();
                    if (validateDate(from, to, d)) {
                        calendarEvents.add(new CalendarEvent(d, createModel.getEventType(), createModel.getComment(), uuid));
                    }
                }
                break;
            case MONTHLY:
                int yearsInBetween = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
                int monthsDiff = b.get(Calendar.MONTH) - a.get(Calendar.MONTH);

                for (int i = 0; i <= yearsInBetween * 12 + monthsDiff; i++) {
                    if (a.getActualMaximum(Calendar.DAY_OF_MONTH) >= createModel.getDay()) {
                        Date d = new GregorianCalendar(a.get(Calendar.YEAR), a.get(Calendar.MONTH), createModel.getDay()).getTime();
                        if (validateDate(from, to, d)) {
                            calendarEvents.add(new CalendarEvent(d, createModel.getEventType(), createModel.getComment(), uuid));
                        }
                    }
                    a.add(Calendar.MONTH, 1);
                }
                break;
            case WEEKLY:
                long differenceWeek = to.getTime() - from.getTime();
                int dayDiff = createModel.getCalendarWeekDay().ordinal() + 1 - a.get(Calendar.DAY_OF_WEEK);
                a.add(Calendar.DAY_OF_MONTH, dayDiff);

                for (int i = 0; i <= (int) (TimeUnit.DAYS.convert(differenceWeek, TimeUnit.MILLISECONDS) / 7) + 1; i++) {
                    Date d = a.getTime();
                    if (validateDate(from, to, d)) {
                        calendarEvents.add(new CalendarEvent(d, createModel.getEventType(), createModel.getComment(), uuid));
                    }
                    a.add(Calendar.WEEK_OF_MONTH, 1);
                }
                break;
        }

        return CalendarEventMetaModelHelper.fromEntity(calendarLocal.save(calendarEvents));
    }

    private boolean validateDate(Date from, Date to, Date d) {
        return (from.before(d) || from.equals(d)) && (to.after(d) || to.equals(d));
    }

    private Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTime(date);
        return cal;
    }

    @RolesAllowed(PermissionIdNames.FINA_CALENDAR_DELETE)
    public void delete(long id, CalendarEventDeleteType calendarEventDeleteType) {
        CalendarEvent calendarEvent = calendarLocal.getById(id);
        if(calendarEvent.getGroupUUID() == null) {
            calendarLocal.delete(id);
        }

        switch (calendarEventDeleteType) {
            case DELETE:
                calendarLocal.delete(id);
                break;
            case DELETE_ALL:
                calendarLocal.deleteAll(calendarEvent.getGroupUUID());
                break;
            case DELETE_AFTER:
                calendarLocal.deleteAfter(calendarEvent.getDate(), calendarEvent.getGroupUUID());
                break;
        }
    }
}
