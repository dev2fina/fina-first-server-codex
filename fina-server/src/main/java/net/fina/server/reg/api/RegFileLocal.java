package net.fina.server.reg.api;

import net.fina.common.shared.reg.GenerateSourceResult;
import net.fina.common.shared.reg.GenerateSourceType;
import net.fina.server.reg.entity.RegFileStage;
import net.fina.server.reg.entity.RegFileSchedule;
import net.fina.server.reg.model.InputsMetaModel;

import java.util.List;

public interface RegFileLocal {
    void createRegFileSchedulesRelation(long fileId, List<Long> scheduleIds);

    GenerateSourceResult generateTable(GenerateSourceType type, List<InputsMetaModel> inputsMetaModels);

    List<Long> loadUniqueScheduleIds();

    List<RegFileSchedule> loadByScheduleId(long scheduleId);

    RegFileStage loadByFileId(long fileId);

    void createQueueFile(long fileId);

    void deleteQueueFile(long fileId);
}
