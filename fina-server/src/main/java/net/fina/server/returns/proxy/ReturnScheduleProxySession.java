package net.fina.server.returns.proxy;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.filter.ReturnsScheduleFilter;
import net.fina.common.client.filter.ScheduleFilter;
import net.fina.common.client.returns.ReturnScheduleMetaModel;
import net.fina.common.client.returns.ReturnScheduleTaskDetail;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.common.shared.SortField;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.misc.ObjectUtil;
import net.fina.server.returns.api.ReturnVersionLocal;
import net.fina.server.returns.api.ScheduleLocal;
import net.fina.server.returns.entity.ReturnVersion;
import net.fina.server.returns.entity.Schedule;
import net.fina.server.returns.schedule.api.ReturnsScheduleLocal;
import net.fina.server.returns.schedule.entity.ReturnSchedule;
import net.fina.server.returns.schedule.impl.QuartzManager;
import net.fina.server.security.api.UserLocal;
import org.jboss.ejb3.annotation.SecurityDomain;

import java.util.*;

@Stateless
@SecurityDomain("FinASecurityDomain")
@RolesAllowed(PermissionIdNames.RETURN_SCHEDULER_REVIEW)
public class ReturnScheduleProxySession {

    @Inject
    private ReturnsScheduleLocal returnsScheduleLocal;
    @Inject
    private UserLocal userLocal;
    @EJB
    private QuartzManager quartzManager;
    @Inject
    private ScheduleLocal scheduleLocal;
    @Inject
    private ReturnVersionLocal returnVersionLocal;

    public PaginatedListWrapper<ReturnScheduleMetaModel> loadReturnSchedules(Map<ReturnsScheduleFilter, Object> filterMap, int offset, int limit, SortField sortField) {
        long langID = ThreadLocalHolder.getLanguage().getId();
        filterMap.put(ReturnsScheduleFilter.PARENT_ID, 0);
        List<ReturnSchedule> rSchedules = returnsScheduleLocal.load(filterMap, offset, limit, sortField);
        List<ReturnScheduleMetaModel> models = new ArrayList<>();
        for (ReturnSchedule rSchedule : rSchedules) {
            ReturnScheduleMetaModel model = new ReturnScheduleMetaModel();
            ObjectUtil.copyProperties(rSchedule, model);
            model.setUserName(userLocal.findUserbyId(rSchedule.getUserId()).getDescription().getDescription(langID));
            models.add(model);
        }

        long count = returnsScheduleLocal.count(new HashMap<>(filterMap));

        PaginatedListWrapper<ReturnScheduleMetaModel> result = new PaginatedListWrapper<>();
        result.setList(models);
        result.setTotalResults(count);
        return result;
    }

    @RolesAllowed(PermissionIdNames.RETURN_SCHEDULER_AMEND)
    public ReturnScheduleMetaModel save(ReturnScheduleMetaModel model) throws FinATypeException {
        ReturnSchedule task = new ReturnSchedule();
        ObjectUtil.copyProperties(model, task);
        task.setOnDemand(model.isOnDemand() ? 1 : 0);
        task.setFilterMap(constructFilterMap(model));
        task = returnsScheduleLocal.save(task);
        model.setId(task.getId());

        return model;
    }

    @RolesAllowed(PermissionIdNames.RETURN_SCHEDULER_DELETE)
    public void delete(List<Integer> ids) throws FinATypeException {
        for (Integer id : ids) {
            returnsScheduleLocal.delete(id);
        }
    }

    @RolesAllowed(PermissionIdNames.RETURN_SCHEDULER_AMEND)
    public void runScheduledTasks(List<Integer> taskIds) {
        for (Integer taskId : taskIds) {
            if (!quartzManager.jobScheduled(taskId)) {
                ReturnSchedule task = returnsScheduleLocal.findById(taskId);
                task.setScheduleTime(new Date());
                quartzManager.createJob(task);
//                quartzManager.triggerJobWithTaskId(task.getId());
            } else {
                quartzManager.triggerJobWithTaskId(taskId);
            }
        }
    }

    public List<ReturnScheduleTaskDetail> loadTaskSchedules(int taskId) {
        Map<ReturnsScheduleFilter, Object> filterMap = new HashMap<>();
        filterMap.put(ReturnsScheduleFilter.PARENT_ID, taskId);
        Map<Long, ReturnSchedule> returnSchedulesByScheduleId = new HashMap<>();

        Collection<Long> scheduleIds = new ArrayList<>(0);
        for (ReturnSchedule child : returnsScheduleLocal.load(filterMap)) {
            scheduleIds.add(child.getScheduleId());
            returnSchedulesByScheduleId.put(child.getScheduleId(), child);
        }

        List<ReturnScheduleTaskDetail> result = new ArrayList<>();
        if (!scheduleIds.isEmpty()) {

            Map<ScheduleFilter, Object> filter = new HashMap<>();
            filter.put(ScheduleFilter.SCHEDULE_ID, scheduleIds);

            Map<Long, ReturnVersion> returnVersionMap = new HashMap<>();

            for (ReturnVersion rv : returnVersionLocal.loadReturnVersions(true)) {
                returnVersionMap.put(rv.getId(), rv);
            }

            for (Schedule schedule : scheduleLocal.load(filter, true, null, null)) {
                ReturnScheduleTaskDetail detail = new ReturnScheduleTaskDetail();
                detail.setId(schedule.getId());
                detail.setDefinitionCode(schedule.getReturnDefinition().getCode());
                detail.setFiCode(schedule.getFi().getCode());
                detail.setPeriodFrom(schedule.getPeriod().getFromDate());
                detail.setPeriodTo(schedule.getPeriod().getToDate());
                detail.setDueDate(schedule.getDelay());

                ReturnSchedule rsm = returnSchedulesByScheduleId.get(schedule.getId());
                if (rsm != null) {
                    detail.setStatus(rsm.getStatus());
                    ReturnVersion version = returnVersionMap.get(rsm.getVersionId());
                    detail.setVersionCode(version != null ? version.getCode() : null);
                    detail.setMessage(rsm.getMessage());
                }

                result.add(detail);
            }
        }

        return result;
    }


    private HashMap<ScheduleFilter, Object> constructFilterMap(ReturnScheduleMetaModel model) {
        HashMap<ScheduleFilter, Object> filterMap = new HashMap<>();
        if (!model.getFiIDs().isEmpty()) {
            filterMap.put(ScheduleFilter.FI_IDS, model.getFiIDs());
        }
        if (!model.getDefinitionIds().isEmpty()) {
            filterMap.put(ScheduleFilter.DEFINITION_ID, model.getDefinitionIds());
        }

        if (!model.getScheduleIds().isEmpty()) {
            filterMap.put(ScheduleFilter.SCHEDULE_ID, model.getScheduleIds());
        }
        if (model.getDelay() > 0) {
            filterMap.put(ScheduleFilter.DELAY, model.getDelay());
        }


        if (model.getPeriodFrom() != null) {
            filterMap.put(ScheduleFilter.PERIOD_FROM, model.getPeriodFrom());
        }

        if (model.getPeriodTo() != null) {
            filterMap.put(ScheduleFilter.PERIOD_TO, model.getPeriodTo());
        }

        if (model.getPeriodTypeId() > 0) {
            filterMap.put(ScheduleFilter.PERIOD_TYPE_ID, model.getPeriodTypeId());
        }

        if (model.getReturnTypeId() > 0) {
            filterMap.put(ScheduleFilter.RETURN_TYPE_ID, model.getReturnTypeId());
        }

        return filterMap;
    }
}
