package net.fina.server.returns.model.helper;

import net.fina.common.client.returns.ScheduleModel;
import net.fina.common.client.returns.ScheduleModelSimple;
import net.fina.server.fi.model.FiModelHelper;
import net.fina.server.returns.entity.Schedule;
import net.fina.server.returns.entity.ScheduleParameter;

import java.util.List;
import java.util.stream.Collectors;

public class ScheduleModelHelper {

    public static ScheduleModel toModel(Schedule schedule, long langId) {
        ScheduleModel result = new ScheduleModel();

        result.setId(schedule.getId());
        result.setVersion(schedule.getVersion());
        result.setDelay(schedule.getDelay());
        result.setDelayHour(schedule.getDelayHour());
        result.setDelayMinute(schedule.getDelayMinute());
        result.setFi(FiModelHelper.toModel(schedule.getFi(), langId));
        result.setReturnDefinition(ReturnDefinitionModelHelper.toModel(schedule.getReturnDefinition(), langId));
        result.setPeriod(PeriodModelHelper.toModel(schedule.getPeriod(), langId));
        result.setComment(schedule.getComment());

        return result;
    }

    public static ScheduleParameter scheduleModelToParameter(ScheduleModelSimple model) {
        return new ScheduleParameter(
                model.getId(), model.getFiId(), model.getPeriodId(), model.getReturnDefinitionId(),
                0, model.getDelay(), model.getDelayHour(), model.getDelayMinute(), null
        );
    }
    public static ScheduleParameter scheduleModelToParameter(ScheduleModel model) {
        return new ScheduleParameter(
                model.getId(), model.getFi().getId(), model.getPeriod().getId(), model.getReturnDefinition().getId(),
                model.getVersion(), model.getDelay(), model.getDelayHour(), model.getDelayMinute(),model.getComment()
        );
    }

    public static List<ScheduleParameter> toParameterModelList(List<ScheduleModelSimple> schedules) {
        return schedules.stream().map(ScheduleModelHelper::scheduleModelToParameter).collect(Collectors.toList());
    }

    public static List<ScheduleModel> toModelList(List<Schedule> schedules, long langId) {
        return schedules.stream().map(s -> toModel(s, langId)).collect(Collectors.toList());
    }
}
