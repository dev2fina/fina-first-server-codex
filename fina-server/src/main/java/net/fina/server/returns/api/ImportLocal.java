package net.fina.server.returns.api;

import net.fina.common.client.constants.ImportStatus;
import net.fina.common.client.filter.ImportFilter;
import net.fina.common.client.returns.ImportModel;
import net.fina.server.returns.entity.ImportedReturn;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ImportLocal {

    void saveXmls(List<ImportedReturn> xmls);

    void reImport(int importedReturnId);

    List<ImportModel> loadPackages(Map<ImportFilter, Object> filterMap, List<String> fiCodes);

    List<ImportModel> loadPackagesV2(Map<ImportFilter, Object> filterMap, List<String> fiCodes);

    List<ImportModel> load(Map<ImportFilter, Object> filterMap);

    long count(Map<ImportFilter, Object> filterMap, boolean countPackages, List<String> fiCodes);

    String getImportMessage(long importedReturnId);

    void deleteReturn(int importedReturnId);

    void deleteReturns(String fiCode, String versionCode, Date periodStart, Date periodEnd, Date uploadTime, long uploadFileId, String uploadFileName);

    String getReturnXML(int importId);

    Map<ImportStatus, Integer> loadImportStatusStatistic(Date fromDate, Date toDate);

    ImportedReturn findById(int id);
}
