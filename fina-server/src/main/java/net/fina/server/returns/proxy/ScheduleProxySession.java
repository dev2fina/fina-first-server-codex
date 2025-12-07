package net.fina.server.returns.proxy;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Asynchronous;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.filter.ScheduleFilter;
import net.fina.common.client.returns.ScheduleDueDateParameterModel;
import net.fina.common.client.returns.ScheduleGenerateParameterModel;
import net.fina.common.client.returns.ScheduleModel;
import net.fina.common.client.returns.ScheduleModelSimple;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.fi.api.FiLocal;
import net.fina.server.fi.entity.Fi;
import net.fina.server.returns.api.PeriodLocal;
import net.fina.server.returns.api.ReturnDefinitionLocal;
import net.fina.server.returns.api.ScheduleLocal;
import net.fina.server.returns.entity.Period;
import net.fina.server.returns.entity.ReturnDefinition;
import net.fina.server.returns.entity.Schedule;
import net.fina.server.returns.entity.ScheduleParameter;
import net.fina.server.returns.model.helper.ScheduleModelHelper;
import net.fina.server.security.api.UserLocal;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ejb3.annotation.TransactionTimeout;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Stateless
@SecurityDomain("FinASecurityDomain")
public class ScheduleProxySession {

    @Inject
    private ScheduleLocal scheduleLocal;
    @Inject
    private UserLocal userLocal;
    @Inject
    private FiLocal fiLocal;
    @Inject
    private PeriodLocal periodLocal;
    @Inject
    private ReturnDefinitionLocal returnDefinitionLocal;

    public PaginatedListWrapper<ScheduleModel> load(Map<ScheduleFilter, Object> filterObjectMap) {
        List<Long> fiIDs = (List<Long>) filterObjectMap.get(ScheduleFilter.FI_IDS);
//        long count = scheduleLocal.count(filterObjectMap, fiIDs);

        List<Schedule> schedules = scheduleLocal.loadPaginated(filterObjectMap, fiIDs, null);

        PaginatedListWrapper<ScheduleModel> result = new PaginatedListWrapper<>();
        result.setTotalResults(0);
        result.setList(ScheduleModelHelper.toModelList(schedules, ThreadLocalHolder.getLanguage().getId()));

        return result;
    }

    @RolesAllowed(PermissionIdNames.FINA_RETURNS_SCHEDULE_AMEND)
    public List<ScheduleModelSimple> generateSchedules(ScheduleGenerateParameterModel parameterModel) {

        List<ScheduleModelSimple> scheduleModels = new ArrayList<>();
        for (long definitionId : parameterModel.getDefinitions()) {
            for (long periodId : parameterModel.getPeriods()) {
                for (long fiId : parameterModel.getFis()) {

                    ScheduleModelSimple model = new ScheduleModelSimple();
                    model.setId(0L);
                    model.setDelay(parameterModel.getDelay());
                    model.setDelayHour(parameterModel.getDelayHour());
                    model.setDelayMinute(parameterModel.getDelayMinute());
                    model.setReturnDefinitionId(definitionId);
                    model.setPeriodId(periodId);
                    model.setFiId(fiId);

                    scheduleModels.add(model);
                }
            }
        }

        return scheduleModels;
    }


    @PermitAll
    @TransactionTimeout(unit = TimeUnit.DAYS, value = 1)
    @Asynchronous
    public void saveScheduleAsync(ScheduleGenerateParameterModel parameterModel, long langId) throws FinATypeException {
        int maxSchedulesSize = 20_000;

        List<ScheduleModelSimple> models = generateSchedules(parameterModel);
        List<ScheduleParameter> parameters = ScheduleModelHelper.toParameterModelList(models);


        List<Schedule> notSaved = Collections.synchronizedList(new ArrayList<>());
        String currentUser = userLocal.getCurrentUserLogin();
        Map<Long, Fi> fiMap = new HashMap<>(parameters.size());
        Map<Long, Period> periodMap = new HashMap<>(parameters.size());
        Map<Long, ReturnDefinition> returnDefinitionHashMap = new HashMap<>(parameters.size());

        fiLocal.loadFisByIds(parameterModel.getFis()).forEach(fi -> fiMap.put(fi.getId(), fi));
        periodLocal.loadPeriodByIds(parameterModel.getPeriods()).forEach(period -> periodMap.put(period.getId(), period));
        returnDefinitionLocal.loadReturnDefinitionsById(parameterModel.getDefinitions()).forEach(retDef -> returnDefinitionHashMap.put(retDef.getId(), retDef));


        if (parameters.size() <= maxSchedulesSize) {
            scheduleLocal.saveSchedules(parameters, fiMap, periodMap, returnDefinitionHashMap, notSaved, currentUser, langId);
        } else {
            scheduleLocal.batchSave(parameters, fiMap, periodMap, returnDefinitionHashMap, notSaved, currentUser, langId);
        }
    }

    @RolesAllowed(PermissionIdNames.FINA_RETURNS_SCHEDULE_AMEND)
    public ScheduleModel saveSchedule(ScheduleModel model) throws FinATypeException {

        ScheduleParameter parameter = ScheduleModelHelper.scheduleModelToParameter(model);
        Schedule schedule = scheduleLocal.save(parameter);

        long langId = ThreadLocalHolder.getLanguage().getId();

        return ScheduleModelHelper.toModel(schedule, langId);

    }


    @RolesAllowed(PermissionIdNames.FINA_RETURNS_SCHEDULE_DELETE)
    public void deleteSchedule(long ScheduleID) throws FinATypeException {
        scheduleLocal.delete(ScheduleID);
    }

    @RolesAllowed(PermissionIdNames.FINA_RETURNS_SCHEDULE_DELETE)
    public void deleteSchedules(List<Long> scheduleIds) throws FinATypeException {
        scheduleLocal.deleteSchedules(scheduleIds);
    }

    @RolesAllowed(PermissionIdNames.FINA_RETURNS_SCHEDULE_DELETE)
    public boolean deleteAllSchedules(Map<ScheduleFilter, Object> filter) {
        return scheduleLocal.deleteAllSchedules(filter, (List<Long>) filter.get(ScheduleFilter.FI_IDS));
    }

    @RolesAllowed(PermissionIdNames.FINA_RETURNS_SCHEDULE_AMEND)
    public void editDueDateForAllSchedules(Map<ScheduleFilter, Object> filter, ScheduleDueDateParameterModel parameterModel) throws FinATypeException {
        scheduleLocal.editDueDate(parameterModel.getNewDueDate(), parameterModel.getNewDueDateHour(), parameterModel.getNewDueDateMinute(), parameterModel.getNewComment(), filter, parameterModel.getFiIds());
    }
}
