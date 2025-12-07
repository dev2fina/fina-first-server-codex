package net.fina.server.returns.api;

import net.fina.server.fi.entity.Fi;
import net.fina.server.returns.entity.Period;
import net.fina.server.returns.entity.ReturnDefinition;
import net.fina.server.returns.entity.Schedule;
import net.fina.server.returns.entity.ScheduleParameter;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public interface ScheduleBatchLocal {
    void saveSchedules(List<ScheduleParameter> sublist, List<ScheduleParameter> parameters, AtomicInteger progress,
                       Map<Long, Fi> fiMap, Map<Long, Period> periodMap, Map<Long, ReturnDefinition> returnDefinitionHashMap,
                       List<Schedule> notSaved, String currentUser,
                       Set<String> existingKeys);
}
