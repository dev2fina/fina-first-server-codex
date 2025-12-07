package net.fina.server.misc;

import net.fina.common.client.fis.FiModel;
import net.fina.common.client.fis.FiTypeModel;
import net.fina.common.client.returns.*;
import net.fina.common.shared.NotSubmitedReturnModel;
import net.fina.server.fi.entity.Fi;
import net.fina.server.fi.entity.FiType;
import net.fina.server.returns.entity.Schedule;

import java.util.*;

public class NotSubmittedSchedulesHelper {

    private long langId;
    private List<Schedule> schedules;
    private ModelHelper<FiModel, Fi> fiHelper = new ModelHelper<>();
    private ModelHelper<FiTypeModel, FiType> fiTypeModelHelper = new ModelHelper<>();

    public NotSubmittedSchedulesHelper(List<Schedule> schedules, long langId) {
        this.langId = langId;
        this.schedules = schedules;
    }

    public List<ScheduleModel> getNotSubmittedReturnModels() {

        List<ScheduleModel> modelList = new ArrayList<>();

        FiModel fiModel;
        PeriodModel periodModel;
        PeriodTypeModel periodTypeModel;
        FiTypeModel fiTypeModel;
        ReturnDefinitionModel returnDefinitionModel;
        ReturnTypeModel returnTypeModel;
        ScheduleModel scheduleModel;
        Map<String, Set<String>> map = new HashMap<>();
        Map<String, Map<Long, Set<String>>> fiPeriodReturnCodes = new HashMap<>();

        for (Schedule s : schedules) {
            scheduleModel = new NotSubmitedReturnModel();
            scheduleModel.setId(s.getId());
            scheduleModel.setDelay(s.getDelay());
            scheduleModel.setDelayHour(s.getDelayHour());
            scheduleModel.setDelayMinute(s.getDelayMinute());

            fiTypeModel = new FiTypeModel();
            fiTypeModelHelper.toModel(fiTypeModel, s.getFi().getFiType());

            fiModel = new FiModel();
            fiHelper.toModel(fiModel, s.getFi());
            fiModel.setRegionId(s.getFi().getRegionId());
            fiModel.setAddressString(s.getFi().getAddressDescription() != null ? s.getFi().getAddressDescription().getDescription(langId) : null);
            fiModel.setShortNameString(s.getFi().getShortName() != null ? s.getFi().getShortName().getDescription(langId) : null);
            fiModel.setFiTypeModel(fiTypeModel);
            fiModel.setName(s.getFi().getDescription().getDescription(langId));

            periodTypeModel = new PeriodTypeModel();
            periodTypeModel.setId(s.getPeriod().getPeriodType().getId());
            periodTypeModel.setPeriodType(s.getPeriod().getPeriodType().getPeriodType());
            periodTypeModel.setCode(s.getPeriod().getPeriodType().getCode());

            periodModel = new PeriodModel();
            periodModel.setId(s.getPeriod().getId());
            periodModel.setPeriodType(periodTypeModel);
            periodModel.setFromDate(s.getPeriod().getFromDate());
            periodModel.setToDate(s.getPeriod().getToDate());

            scheduleModel.setFi(fiModel);
            scheduleModel.setPeriod(periodModel);

            returnTypeModel = new ReturnTypeModel();
            returnTypeModel.setId(s.getReturnDefinition().getReturnType().getId());
            returnTypeModel.setCode(s.getReturnDefinition().getReturnType().getCode());

            returnDefinitionModel = new ReturnDefinitionModel();
            returnDefinitionModel.setId(s.getReturnDefinition().getId());
            returnDefinitionModel.setCode(s.getReturnDefinition().getCode());
            returnDefinitionModel.setReturnType(returnTypeModel);

            scheduleModel.setReturnDefinition(returnDefinitionModel);

            if (modelList.isEmpty()) {
                modelList.add((NotSubmitedReturnModel) scheduleModel);
//                setReturnCodes(scheduleModel, map, fiPeriodReturnCodes);
            }

            filterAndGroupSchedules(modelList, scheduleModel, map, fiPeriodReturnCodes);
        }

        return modelList;
    }

    private void setReturnCodes(ScheduleModel scheduleModel, Map<String, Set<String>> map, Map<String, Map<Long, Set<String>>> fiPeriodReturnCodes) {
        Set<String> returnCodes = new HashSet<>();
        returnCodes.add(scheduleModel.getReturnDefinition().getCode());
        map.put(scheduleModel.getPeriod().getId() + "|" + scheduleModel.getFi().getCode(), returnCodes);


        Map<Long, Set<String>> periodReturnMap = fiPeriodReturnCodes.get(scheduleModel.getFi().getCode());
        if (periodReturnMap == null) {
            periodReturnMap = new HashMap<>();
            periodReturnMap.put(scheduleModel.getPeriod().getId(), returnCodes);
        }

        if (periodReturnMap.get(scheduleModel.getPeriod().getId()) == null) {
            periodReturnMap.put(scheduleModel.getPeriod().getId(), returnCodes);
        } else {
            periodReturnMap.get(scheduleModel.getPeriod().getId()).addAll(returnCodes);
        }

        fiPeriodReturnCodes.put(scheduleModel.getFi().getCode(), periodReturnMap);

        ((NotSubmitedReturnModel) scheduleModel).setFiReturnCodes(map);
        ((NotSubmitedReturnModel) scheduleModel).setFiPeriodReturnCodes(fiPeriodReturnCodes);
    }

    private void filterAndGroupSchedules(List<ScheduleModel> modelList, ScheduleModel scheduleModel, Map<String, Set<String>> map, Map<String, Map<Long, Set<String>>> fiPeriodReturnCodes) {
        for (ScheduleModel s : modelList) {
            if (s.getFi().equals(scheduleModel.getFi()) && s.getPeriod().getToDate().equals(scheduleModel.getPeriod().getToDate()) && s.getReturnDefinition().getReturnType().getCode().equals(scheduleModel.getReturnDefinition().getReturnType().getCode())) {
                String mapKey = scheduleModel.getPeriod().getId() + "|" + scheduleModel.getFi().getCode();
                if (map.get(mapKey) != null) {
                    map.get(mapKey).add(scheduleModel.getReturnDefinition().getCode());
                }

                if (fiPeriodReturnCodes.get(scheduleModel.getFi().getCode()) != null && fiPeriodReturnCodes.get(scheduleModel.getFi().getCode()).get(scheduleModel.getPeriod().getId()) != null) {
                    fiPeriodReturnCodes.get(scheduleModel.getFi().getCode()).get(scheduleModel.getPeriod().getId()).add(scheduleModel.getReturnDefinition().getCode());
                }
                return;
            }
        }
        modelList.add(scheduleModel);
        setReturnCodes(scheduleModel, map, fiPeriodReturnCodes);
    }
}
