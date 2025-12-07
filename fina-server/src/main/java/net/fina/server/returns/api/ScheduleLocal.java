package net.fina.server.returns.api;

import net.fina.common.client.constants.CalendarPeriodType;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.filter.ScheduleFilter;
import net.fina.common.client.returns.OverdueReturnModel;
import net.fina.server.dcs.uploadfile.entity.SortInfo;
import net.fina.server.fi.entity.Fi;
import net.fina.server.returns.entity.*;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ScheduleLocal {

    Schedule save(ScheduleParameter schedule) throws FinATypeException;

    Schedule save(ScheduleParameter schedule, Map<Long, Fi> fiMap, Map<Long, Period> periodMap, Map<Long, ReturnDefinition> returnDefinitionMap) throws FinATypeException;

    void batchSave(List<ScheduleParameter> parameters,
                   Map<Long, Fi> fiMap,
                   Map<Long, Period> periodMap,
                   Map<Long, ReturnDefinition> returnDefinitionHashMap,
                   List<Schedule> notSaved,
                   String currentUser,
                   long langId) throws FinATypeException;

    void saveSchedules(List<ScheduleParameter> parameters,
                       Map<Long, Fi> fiMap,
                       Map<Long, Period> periodMap,
                       Map<Long, ReturnDefinition> returnDefinitionHashMap,
                       List<Schedule> notSaved,
                       String currentUser,
                       long langId) throws FinATypeException;

    void delete(Long id) throws FinATypeException;

    Schedule findScheduleByPeriod(Date from, Date to, String fiCode, String returnDefinitionCode);

    long findScheduleIdByPeriod(Date from, Date to, String fiCode, String returnDefinitionCode);

    Schedule loadSimpleSchedule(long id);

    List<DefinitionTable> loadScheduleReturnDefinitionTables(long scheduleId);

    long count(Map<ScheduleFilter, Object> filter, List<Long> fiIds);

    List<Schedule> load(Map<ScheduleFilter, Object> filter, boolean loadAll, Collection<Long> fiIds, Collection<Long> returnDefinitionIds);

    List<Schedule> loadPaginated(Map<ScheduleFilter, Object> filter, Collection<Long> fiIds, Collection<Long> returnDefinitionIds);

    int getReturnDefinitionsDueDate(List<String> returnDefinitionCodes, String fiCode, Date fromDate, Date toDate);

    int getReturnDefinitionsDueDateHour(List<String> returnDefinitionCodes, String fiCode, Date fromDate, Date toDate);

    List<Schedule> loadNotSubmittedSchedules(Date fromDate, Date toDate, CalendarPeriodType periodType, long fitypeId);

    List<Schedule> loadFiScheduleList(long bankId, long periodId);

    void deleteSchedules(List<Long> scheduleIds) throws FinATypeException;

    boolean deleteAllSchedules(Map<ScheduleFilter, Object> filter, List<Long> fiIds);

    void editDueDate(Integer newDueDate, Integer newDueDateHour, Integer newDueDateMinute, String newComment, Map<ScheduleFilter, Object> filter, List<Long> fiIds) throws FinATypeException;

    int getOverdueNumberOfDays(int dueDate, int dueDateHour, int dueDateMinute, Date toDate, Date uploadTime);

    int getReturnDefinitionsDueDateMinute(List<String> returnDefinitionCodes, String fiCode, Date fromDate, Date toDate);

    List<OverdueReturnModel> loadNotSubmittedReturnModels(Date fromDate, Date toDate, CalendarPeriodType periodType, long fiType, String fiCode, SortInfo sortInfo);

    List<Schedule> loadSchedulesByDate(Date fromDate, Date toDate);
}
