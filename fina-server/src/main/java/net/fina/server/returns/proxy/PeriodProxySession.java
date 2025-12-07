package net.fina.server.returns.proxy;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import net.fina.common.client.constants.CalendarPeriodType;
import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.filter.PeriodFilter;
import net.fina.common.client.returns.PeriodModel;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.common.shared.SortField;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.misc.ServiceUtil;
import net.fina.server.returns.api.PeriodLocal;
import net.fina.server.returns.entity.Period;
import net.fina.server.returns.model.helper.PeriodModelHelper;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.logging.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@Stateless
@SecurityDomain("FinASecurityDomain")
@RolesAllowed({PermissionIdNames.FINA_PERIODS_REVIEW})
public class PeriodProxySession {
    private final Logger log = Logger.getLogger(getClass().getName());

    @Inject
    private PeriodLocal periodLocal;

    @RolesAllowed({PermissionIdNames.FINA_RETURNS_SCHEDULE_REVIEW, PermissionIdNames.FINA_REPORT_GENERATE})
    public PaginatedListWrapper<PeriodModel> loadPeriods(Map<PeriodFilter, Object> filter, SortField sortField) {
        long langId = ThreadLocalHolder.getLanguage().getId();
        List<Period> periods = periodLocal.load(filter, false, sortField);
        int totalCount = (int) periodLocal.count(filter);

        return new PaginatedListWrapper<>(PeriodModelHelper.toModel(periods, langId), (int) filter.get(PeriodFilter.limit), totalCount);
    }


    @RolesAllowed(PermissionIdNames.FINA_PERIODS_AMEND)
    public List<PeriodModel> generatePeriods(PeriodModel periodModel, int startPeriodNumber, int periodAmount, int daysBetweenPeriod, int daysInPeriods) throws FinATypeException {
        List<PeriodModel> periodModels = new ArrayList<>();
        String timeZoneId = ServiceUtil.getTimeZoneId(ThreadLocalHolder.getThreadLocalRequest());


        DateTimeZone timeZone = DateTimeZone.forID(timeZoneId);
        DateTime dateTime = new DateTime(periodModel.getFromDate(), timeZone);

        Calendar calendar = dateTime.toGregorianCalendar();
        calendar.setTime(dateTime.toDate());

        int selectedYear = dateTime.getYear();
        int calendarField = 0;
        int offset = 0;
        boolean invalidStartDate = false;
        switch (periodModel.getPeriodType().getPeriodType()) {
            case DAILY:
                calendarField = Calendar.DATE;
                offset = 1;
                daysBetweenPeriod = 1;
                break;
            case WEEKLY:
                calendarField = Calendar.DATE;
                offset = 7;
                daysBetweenPeriod = 1;
                //FIXME: manage first day of week
                invalidStartDate |= dateTime.getDayOfWeek() != DateTimeConstants.MONDAY;
                break;
            case TEN_DAY:
                invalidStartDate |= dateTime.getDayOfMonth() % 10 != 1;
                calendarField = Calendar.DAY_OF_MONTH;
                offset = 10;
                daysBetweenPeriod = 1;
                break;
            case MONTHLY:
                calendarField = Calendar.MONTH;
                offset = 1;
                daysBetweenPeriod = 1;
                invalidStartDate |= dateTime.getDayOfMonth() != 1;
                break;
            case QUARTERLY:
                calendarField = Calendar.MONTH;
                offset = 3;
                daysBetweenPeriod = 1;
                invalidStartDate |= dateTime.getDayOfMonth() != 1;
                invalidStartDate |= dateTime.getMonthOfYear() - 1 % 3 != 0;
                break;
            case SEMIANNUAL:
                calendarField = Calendar.MONTH;
                offset = 6;
                daysBetweenPeriod = 1;
                invalidStartDate |= dateTime.getDayOfMonth() != 1;
                invalidStartDate |= ((dateTime.getMonthOfYear() - 1) % 6) != 0;
                break;
            case ANNUAL:
                calendarField = Calendar.YEAR;
                offset = 1;
                periodAmount = 1;
                invalidStartDate |= dateTime.getDayOfMonth() != 1;
                invalidStartDate |= dateTime.getMonthOfYear() != DateTimeConstants.JANUARY;
                break;
            case ANY: {
                calendarField = Calendar.DATE;
                offset = daysInPeriods;
                daysBetweenPeriod += 1;
            }
        }

        if (invalidStartDate) {
            throw new FinATypeException(FinATypeException.Type.INVALID_START_DATE);
        }
        int periodAmountCount = -1;

        while ((periodAmount > ++periodAmountCount) && (calendar.get(Calendar.YEAR) == selectedYear)) {
            PeriodModel model = new PeriodModel();

            model.setId(0L);
            model.setVersion(0);
            model.setPeriodNumber(startPeriodNumber + periodAmountCount);
            model.setPeriodType(periodModel.getPeriodType());

            model.setFromDate(calendar.getTime());

            if (periodModel.getPeriodType().getPeriodType() == CalendarPeriodType.TEN_DAY) {
                if (dateTime.getDayOfMonth() < 20) {
                    calendar.add(Calendar.DAY_OF_MONTH, 9);
                } else {
                    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                }
            } else {
                calendar.add(calendarField, offset);
                calendar.add(Calendar.DATE, -1);
            }
            model.setToDate(calendar.getTime());

            PeriodModel savedModel = save(model);
            if (savedModel.getId() == 0L) {
                periodModels.add(savedModel);
            }
            calendar.add(Calendar.DATE, daysBetweenPeriod);
        }

        return periodModels;
    }

    @RolesAllowed(PermissionIdNames.FINA_PERIODS_DELETE)
    public void delete(long periodId) throws FinATypeException {
        periodLocal.delete(periodId);
    }

    @RolesAllowed(PermissionIdNames.FINA_PERIODS_DELETE)
    public void delete(List<Long> periodIds) throws FinATypeException {
        periodLocal.deletePeriods(periodIds);
    }


    private PeriodModel save(PeriodModel model) {
        long langId = ThreadLocalHolder.getLanguage().getId();
        Period saved = PeriodModelHelper.toPeriod(model, langId);
        try {
            saved = periodLocal.savePeriod(PeriodModelHelper.toPeriod(model, langId));
        } catch (FinATypeException ex) {
            log.error(ex.getMessage(), ex);
        }
        return PeriodModelHelper.toModel(saved, langId);
    }
}
