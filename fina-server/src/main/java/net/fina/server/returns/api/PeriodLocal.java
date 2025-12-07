package net.fina.server.returns.api;

import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.filter.PeriodFilter;
import net.fina.common.shared.SortField;
import net.fina.server.returns.entity.Period;
import net.fina.server.returns.entity.PeriodType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface PeriodLocal {

    List<Period> load(Map<PeriodFilter, Object> filter);

    List<Period> load(Map<PeriodFilter, Object> filter, boolean asc, SortField sortField);

    PeriodType save(PeriodType periodType) throws FinATypeException;

    public ArrayList<Map<Integer, String>> getPeriodInsertRows(String type, int frequencyType, java.util.Date fromDate, int startPeriodNumber, int numberOfPeriods);

    public ArrayList<Map<Integer, String>> getPeriodInsertRows(String type, java.util.Date fromDate, int startPeriodNumber, int numberOfPeriods, int daysInPeriods, int daysBetweenPeriods);

    public void deletePeriodType(long id) throws FinATypeException;

    public Period savePeriod(Period period) throws FinATypeException;

    public List<PeriodType> loadPeriodTypes();

    public List<Period> loadPeriodByIds(List<Long> ids);

    void delete(long periodId) throws FinATypeException;

    long count(Map<PeriodFilter, Object> filter);

    PeriodType getPeriodTypeByCode(String code);

    boolean hasSchedules(Period period);

    void deletePeriods(List<Long> periodIds) throws FinATypeException;

    boolean exists(java.util.Date fromDate, java.util.Date toDate);

    Map<String, Long> loadPeriodTypeCodeIdMap();
}
