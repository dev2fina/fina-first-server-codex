package net.fina.server.returns.schedule.api;

import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.filter.ReturnsScheduleFilter;
import net.fina.common.shared.SortField;
import net.fina.common.shared.schedule.ScheduledTaskStatus;
import net.fina.server.returns.entity.Schedule;
import net.fina.server.returns.schedule.entity.ReturnSchedule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ReturnsScheduleLocal {

    public List<ReturnSchedule> load(Map<ReturnsScheduleFilter, Object> taskFilterObjectMap);

    public ReturnSchedule save(ReturnSchedule scheduledTask) throws FinATypeException;

    void updateStatus(int scheduledTaskId, ScheduledTaskStatus status);

    void updateMessage(int scheduledTaskId, String message, boolean append);

    public void delete(int scheduledTaskId) throws FinATypeException;

    public ReturnSchedule findById(int id);

    List<ReturnSchedule> load(Map<ReturnsScheduleFilter, Object> filterMap, int offset, int limit, SortField sortField);

    long getReturnScheduledTaskCount(Map<ReturnsScheduleFilter, Object> filterMap);

    List<Schedule> loadTaskSchedulesByParentId(int id);

    long count(HashMap<ReturnsScheduleFilter, Object> filterMap);
}
