package net.fina.server.inputs.api;

import net.fina.server.dcs.uploadfile.entity.UploadFile;
import net.fina.server.fi.entity.Fi;
import net.fina.server.fi.entity.FiType;
import net.fina.server.returns.entity.*;

import java.util.*;

public interface InputManagerLocal {

    List<Fi> loadFis(long periodId, int delay, long fiTypeId, List<Long> fiIds, long userId);

    List<FiType> loadFiTypes(long periodId, int delay, int delayHour, int delayMinute, List<Long> fiTypeIds);

    Fi getFiById(long fiId);

    UploadFile getUploadedFileById(long fileId);

    List<Schedule> loadDistinctSchedules(int start, int limit, Map<String, Object> filter);

    Schedule getScheduleById(long scheduleId);

    List<ImportedReturn> loadImportedReturns(long fileId, Map<String, Object> filter);

    List<UploadFile> loadUploadedFiles(String fiCode, Period period, Map<String, Object> fileFilter);

    ReturnStatus getReturnByCode(long scheduleId, String returnCode);

    long countDistinctSchedules(Map<String, Object> schedule);

    List<Fi> loadFisByIds(List<Long> idFilter);

    List<Object[]> loadFileUploadData(List<String> returnTypeCode, Date periodStart, Date periodEnd, String periodTypeCode, List<Long> fiIds);
}
