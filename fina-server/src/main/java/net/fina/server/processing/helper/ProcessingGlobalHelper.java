package net.fina.server.processing.helper;

import fina2.period.PeriodPK;
import net.fina.common.client.constants.CalendarPeriodType;
import net.fina.common.client.returns.ReturnModel;
import net.fina.server.returns.entity.Period;
import net.fina.server.returns.entity.PeriodType;
import net.fina.server.returns.entity.Schedule;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.util.*;

public class ProcessingGlobalHelper {
    private final Set<String> regMdtCodes;
    private final Map<String, String> regMdtCodeTableNameMap;
    private final Map<String, String> mdtCodeReturnCodeMap;
    private final Map<CalendarPeriodType, String> calendarPeriodTypCodeMap;
    private final Map<Long, Schedule> scheduleIdMap;
    private final Map<String, Schedule> uniqueScheduleMap;
    private final Map<String, Map<Long, Period>> allPeriodsByTypeCode;
    private final Map<String, Long> scheduleVersionReturnMap;

    public ProcessingGlobalHelper(Set<String> regMdtCodes,
                                  Map<String, String> regMdtCodeTableNameMap,
                                  Map<String, String> mdtCodeReturnCodeMap,
                                  List<Period> currentYearPeriods,
                                  List<Schedule> currentYearSchedules,
                                  List<PeriodType> allPeriodTypes,
                                  List<ReturnModel> returnModels) {
        this.regMdtCodes = regMdtCodes;
        this.regMdtCodeTableNameMap = regMdtCodeTableNameMap;
        this.mdtCodeReturnCodeMap = mdtCodeReturnCodeMap;
        this.calendarPeriodTypCodeMap = new HashMap<>();
        this.scheduleIdMap = new HashMap<>();
        this.uniqueScheduleMap = new HashMap<>();
        this.allPeriodsByTypeCode = new HashMap<>();
        this.scheduleVersionReturnMap = new HashMap<>();

        for (Period period : currentYearPeriods) {
            String pTypeCode = period.getPeriodType().getCode();
            if (!allPeriodsByTypeCode.containsKey(pTypeCode)) {
                allPeriodsByTypeCode.put(pTypeCode, new HashMap<>());
            }
            allPeriodsByTypeCode.get(pTypeCode).put(period.getToDate().getTime(), period);

        }

        currentYearSchedules.forEach(s -> {
            if (s.getFi() != null) {
                scheduleIdMap.put(s.getId(), s);
                uniqueScheduleMap.put(String.format("%d%d%s", s.getPeriod().getId(), s.getFi().getId(), s.getReturnDefinition().getCode()), s);
            }
        });

        allPeriodTypes.forEach(pt -> calendarPeriodTypCodeMap.put(pt.getPeriodType(), pt.getCode()));

        for (ReturnModel rm : returnModels) {
            scheduleVersionReturnMap.put(rm.getScheduleId() + "-" + rm.getVersionId(), rm.getId());
        }

    }

    public PeriodPK calcOffset(Period period, String periodOffsetPattern) {
        PeriodPK pk = new PeriodPK((int) period.getId());
        if (periodOffsetPattern.trim().equals("0")) {
            return pk;
        }

        int offset = getPeriodOffset(periodOffsetPattern);
        String periodTypeCode = getPeriodTypeCode(periodOffsetPattern);

        if (period.getPeriodType().getCode().equals(periodTypeCode) && offset == 0) {
            return pk;
        }

        if (!period.getPeriodType().getCode().equals(periodTypeCode)) {
            pk = getPeriodPkByType(period, periodOffsetPattern, offset, periodTypeCode);
        }

        return pk;
    }

    private PeriodPK getPeriodPkByType(Period period, String periodOffsetPattern, int offset, String periodTypeCode) {
        String periodTypeIdentifier = periodOffsetPattern.substring(periodOffsetPattern.length() - 1);

        offset = Math.abs(offset);
        Date periodToDate;
        LocalDate toDate;
        switch (periodTypeIdentifier.toUpperCase()) {
            case "D":
                 toDate= LocalDate.fromDateFields(period.getToDate()).minusDays(offset);
                periodToDate = toDate.toDate();
                break;
            case "W":
                toDate = LocalDate.fromDateFields(period.getToDate()).minusWeeks(offset);
                periodToDate = toDate.toDate();
                break;
            case "T":
                toDate = LocalDate.fromDateFields(period.getToDate()).minusDays(offset * 10);
                periodToDate = toDate.toDate();
                break;
            case "M":
                toDate = LocalDate.fromDateFields(period.getToDate()).minusMonths(offset).dayOfMonth().withMaximumValue();
                periodToDate = toDate.toDate();
                break;
            case "Q":
                toDate = LocalDate.fromDateFields(period.getToDate()).minusMonths(offset * 3);
                periodToDate = toDate.toDate();
                break;
            case "S":
                toDate = LocalDate.fromDateFields(period.getToDate()).minusMonths(offset * 6);
                periodToDate = toDate.toDate();
                break;
            case "A":
                toDate = LocalDate.fromDateFields(period.getToDate()).minusYears(offset);
                periodToDate = toDate.toDate();
                break;
            case "*":
            default:
                LocalDate fromDate = LocalDate.fromDateFields(period.getFromDate());
                LocalDate td = LocalDate.fromDateFields(period.getToDate());

                int periodDaysBetween = Days.daysBetween(fromDate, td).getDays();
                td = td.minusDays(offset * periodDaysBetween);
                periodToDate = td.toDate();
                break;
        }

        Period offsetPeriod = allPeriodsByTypeCode.get(periodTypeCode).get(periodToDate.getTime());

        if (offsetPeriod == null) {
            return new PeriodPK(-1);
        }

        return new PeriodPK((int) offsetPeriod.getId());
    }

    private String getPeriodTypeCode(String periodPatternOffset) {
        String periodTypeIdentifier = periodPatternOffset.substring(periodPatternOffset.length() - 1);
        CalendarPeriodType calendarPeriodType;
        String periodTypeCode;
        switch (periodTypeIdentifier.toUpperCase()) {
            case "D":
                calendarPeriodType = CalendarPeriodType.DAILY;
                break;
            case "W":
                calendarPeriodType = CalendarPeriodType.WEEKLY;
                break;
            case "T":
                calendarPeriodType = CalendarPeriodType.TEN_DAY;
                break;
            case "M":
                calendarPeriodType = CalendarPeriodType.MONTHLY;
                break;
            case "Q":
                calendarPeriodType = CalendarPeriodType.QUARTERLY;
                break;
            case "S":
                calendarPeriodType = CalendarPeriodType.SEMIANNUAL;
                break;
            case "A":
                calendarPeriodType = CalendarPeriodType.ANNUAL;
                break;
            case "*":
            default:
                calendarPeriodType = CalendarPeriodType.ANY;
        }

        periodTypeCode = calendarPeriodTypCodeMap.get(calendarPeriodType);

        return periodTypeCode;
    }

    public int getPeriodOffset(String periodPatternOffset) {
        return Integer.parseInt(periodPatternOffset.substring(0, periodPatternOffset.length() - 1));
    }

    public Set<String> getRegMdtCodes() {
        return regMdtCodes;
    }

    public Map<String, String> getRegMdtCodeTableNameMap() {
        return regMdtCodeTableNameMap;
    }

    public Map<Long, Schedule> getScheduleIdMap() {
        return scheduleIdMap;
    }

    public Map<String, String> getMdtCodeReturnCodeMap() {
        return mdtCodeReturnCodeMap;
    }

    public Map<String, Schedule> getUniqueScheduleMap() {
        return uniqueScheduleMap;
    }

    public Map<String, Long> getScheduleVersionReturnMap() {
        return scheduleVersionReturnMap;
    }
}
