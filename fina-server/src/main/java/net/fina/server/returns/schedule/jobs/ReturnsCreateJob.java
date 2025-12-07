package net.fina.server.returns.schedule.jobs;

import net.fina.common.client.filter.ReturnsScheduleFilter;
import net.fina.common.client.filter.ScheduleFilter;
import net.fina.common.client.property.PropertyKeys;
import net.fina.common.client.returns.ProcessResult;
import net.fina.common.shared.schedule.ScheduledTaskStatus;
import net.fina.server.i18n.api.LanguageLocal;
import net.fina.server.i18n.entity.Language;
import net.fina.server.mdt.api.MDTNodeLocal;
import net.fina.server.mdt.entity.MDTComparison;
import net.fina.server.mdt.entity.MDTDependentNode;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.processing.api.ProcessingLocal;
import net.fina.server.processing.dependency.DependencyItem;
import net.fina.server.returns.api.ReturnDefinitionLocal;
import net.fina.server.returns.api.ReturnLocal;
import net.fina.server.returns.api.ReturnVersionLocal;
import net.fina.server.returns.api.ScheduleLocal;
import net.fina.server.returns.entity.Return;
import net.fina.server.returns.entity.ReturnDefinition;
import net.fina.server.returns.entity.Schedule;
import net.fina.server.returns.schedule.api.ReturnsScheduleLocal;
import net.fina.server.returns.schedule.entity.ReturnSchedule;
import net.fina.server.security.api.PropertyLocal;
import net.fina.server.security.api.UserLocal;
import org.jboss.logging.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.*;

public class ReturnsCreateJob implements ReturnsCreateJobContract {

    private final Logger log = Logger.getLogger(getClass());

    private boolean initialized;
    private int jobId;
    private ReturnsScheduleLocal scheduledTaskLocal;
    private ReturnVersionLocal returnVersionLocal;
    private UserLocal userLocal;
    private ReturnDefinitionLocal returnDefinitionLocal;
    private LanguageLocal languageLocal;
    private PropertyLocal propertyLocal;
    private ReturnLocal returnLocal;
    private ScheduleLocal scheduleLocal;
    private ProcessingLocal processingLocal;
    private ReturnSchedule scheduledTask;
    private MDTNodeLocal mdtNodeLocal;

    private List<Long> scheduleIds;

    private List<Schedule> schedules;
    private List<Long> createdReturnIds;

    private Map<Long, Integer> taskByScheduleMap;
    private Map<Long, Integer> taskByReturnMap;

    private boolean hasTaskError = false;

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        try {
            init(jec.getJobDetail().getJobDataMap());

            if (!initialized) {
                scheduledTaskLocal.updateStatus(jobId, ScheduledTaskStatus.STATUS_ERROR);
                scheduledTaskLocal.updateMessage(jobId, "Job Not Initialized. Quitting.", true);
                return;
            }

            scheduledTaskLocal.updateStatus(jobId, ScheduledTaskStatus.STATUS_PROCESSING);

            createReturns();

            processReturns();

            scheduledTaskLocal.updateStatus(jobId, hasTaskError ? ScheduledTaskStatus.STATUS_ERROR : ScheduledTaskStatus.STATUS_DONE);
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            scheduledTaskLocal.updateStatus(jobId, ScheduledTaskStatus.STATUS_ERROR);
            scheduledTaskLocal.updateMessage(jobId, "Failed to create job. \n" + t.getMessage(), true);
        }
    }

    private void init(JobDataMap jdm) {
        scheduledTaskLocal = (ReturnsScheduleLocal) jdm.get(SCHEDULED_TASK_LOCAL);
        returnVersionLocal = (ReturnVersionLocal) jdm.get(RETURN_VERSION_LOCAL);
        userLocal = (UserLocal) jdm.get(USER_LOCAL);
        languageLocal = (LanguageLocal) jdm.get(LANGUAGE_LOCAL);
        propertyLocal = (PropertyLocal) jdm.get(PROPERTY_LOCAL);
        returnDefinitionLocal = (ReturnDefinitionLocal) jdm.get(RETURN_DEFINITION_LOCAL);
        returnLocal = (ReturnLocal) jdm.get(RETURN_LOCAL);
        scheduleLocal = (ScheduleLocal) jdm.get(SCHEDULE_LOCAL);
        processingLocal = (ProcessingLocal) jdm.get(PROCESSING_LOCAL);

        scheduledTask = (ReturnSchedule) jdm.get(JOB_DATA);
        mdtNodeLocal = (MDTNodeLocal) jdm.get(MDT_NODE_LOCAL);

        jobId = scheduledTask.getId();

        scheduleIds = new ArrayList<>();

        taskByScheduleMap = new HashMap<>();
        taskByReturnMap = new HashMap<>();

        try {
            Map<ReturnsScheduleFilter, Object> filter = new HashMap<>();
            filter.put(ReturnsScheduleFilter.PARENT_ID, scheduledTask.getId());
            Collection<ReturnSchedule> children = scheduledTaskLocal.load(filter);
            for (ReturnSchedule child : children) {
                if (child.getStatus() == ScheduledTaskStatus.STATUS_SCHEDULED) {
                    scheduleIds.add(child.getScheduleId());
                    taskByScheduleMap.put(child.getScheduleId(), child.getId());
                }
            }
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
            scheduledTaskLocal.updateMessage(scheduledTask.getId(), "Exception during initialization. " + ex.getMessage(), false);
            return;
        }

        schedules = new ArrayList<>();
        createdReturnIds = new ArrayList<>();

        initialized = true;
    }

    private void createReturns() {

        long userId = scheduledTask.getUserId();
        List<MDTDependentNode> mdtDependentNodes = mdtNodeLocal.loadAllMdtDependentNodes();
        List<MDTComparison> comparisons = mdtNodeLocal.loadComparisons(new HashMap<>(), null);
        Map<Long, List<MDTNode>> allMdtNodesByParentId = mdtNodeLocal.loadAllNodesByParentId();
        Collection<Long> userFiIds = userLocal.getUserFis(userId);
        Collection<Long> userRDefIds = new HashSet<>();

        Collection<ReturnDefinition> returnDefinitions = new ArrayList<>();
        returnDefinitions.addAll(userLocal.loadUserReturnDefinitions(userId));
        returnDefinitions.addAll(userLocal.loadUserRoleReturnDefinitions(userId));
        for (ReturnDefinition rDef : returnDefinitions) {
            userRDefIds.add(rDef.getId());
        }

        Map<ScheduleFilter, Object> filterMap = new HashMap<>(1);
        filterMap.put(ScheduleFilter.SCHEDULE_ID, scheduleIds);
        schedules = scheduleLocal.load(filterMap, true, userFiIds, userRDefIds);

        Map<String, List<Long>> definitionsMap = new HashMap<>();
        for (Schedule schedule : schedules) {
            definitionsMap.computeIfAbsent(schedule.getReturnDefinition().getCode(), k -> new ArrayList<>());
            definitionsMap.get(schedule.getReturnDefinition().getCode()).add(schedule.getId());
        }

        List<DependencyItem> items = new ArrayList<>();

        List<String> definitionCodesList = new ArrayList<>(definitionsMap.keySet());
        Map<String, Integer> orderedReturns = returnDefinitionLocal.orderReturnDefinitionIds(definitionCodesList);
        for (Map.Entry<String, Integer> entry : orderedReturns.entrySet()) {
            DependencyItem item = new DependencyItem();
            item.code = entry.getKey();
            item.end = entry.getValue();

            items.add(item);
        }
        Collections.sort(items);

        for (DependencyItem item : items) {
            for (long id : definitionsMap.get(item.code)) {
                int taskId = taskByScheduleMap.get(id);
                scheduledTaskLocal.updateStatus(taskId, ScheduledTaskStatus.STATUS_PROCESSING);
                scheduledTaskLocal.updateMessage(taskId, "", false);
                try {
                    Return ret = returnLocal.createReturn(id, scheduledTask.getVersionId(), userId, mdtDependentNodes, allMdtNodesByParentId, comparisons);
                    createdReturnIds.add(ret.getId());
                    taskByReturnMap.put(ret.getId(), taskId);

                    scheduledTaskLocal.updateStatus(taskId, ScheduledTaskStatus.STATUS_DONE);
                    scheduledTaskLocal.updateMessage(taskId, "Return created.", false);
                } catch (Throwable ex) {
                    scheduledTaskLocal.updateStatus(taskId, ScheduledTaskStatus.STATUS_ERROR);
                    scheduledTaskLocal.updateMessage(taskId, "Return creation failed with message: \n " + ex.getMessage(), false);

                    log.error(ex.getMessage(), ex);
                    hasTaskError = true;
                }
            }
        }

    }

    private void processReturns() {
        String defaultLanguageCode = propertyLocal.getSystemProperty(PropertyKeys.DEFAULT_LANGUAGE);
        if (defaultLanguageCode != null) {
            defaultLanguageCode = defaultLanguageCode.trim();
        }

        Language defaultLanguage = languageLocal.getLanguageByCode(defaultLanguageCode);
        List<MDTComparison> comparisons = mdtNodeLocal.loadComparisons(new HashMap<>(), null);
        for (Long returnId : createdReturnIds) {
            int taskId = taskByReturnMap.get(returnId);
            scheduledTaskLocal.updateStatus(taskId, ScheduledTaskStatus.STATUS_PROCESSING);
            try {
                Map<Long, ProcessResult> results = processingLocal.process(scheduledTask.getUserId(), defaultLanguage.getId(), true, comparisons, new Long[]{returnId});
                ProcessResult processResult = results.get(returnId);

                scheduledTaskLocal.updateStatus(taskId, ScheduledTaskStatus.STATUS_DONE);
                scheduledTaskLocal.updateMessage(taskId, "Return processed.\n" + processResult.getStatus().name() + "\n" + processResult.getProcessNote(), true);
            } catch (Throwable t) {
                scheduledTaskLocal.updateStatus(taskId, ScheduledTaskStatus.STATUS_ERROR);
                scheduledTaskLocal.updateMessage(taskId, "Return processing failed with message: \n " + t.getMessage(), true);
                log.error(t.getMessage(), t);
                hasTaskError = true;
            }
        }
    }
}
