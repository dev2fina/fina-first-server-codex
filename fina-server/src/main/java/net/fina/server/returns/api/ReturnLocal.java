package net.fina.server.returns.api;

import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.filter.OverdueReturnFilter;
import net.fina.common.client.filter.ReturnItemsSearchFilter;
import net.fina.common.client.returns.*;
import net.fina.common.shared.FilterConfig;
import net.fina.common.shared.SortField;
import net.fina.common.shared.mi.MiChartDataModel;
import net.fina.server.dcs.uploadfile.entity.SortInfo;
import net.fina.server.i18n.entity.Language;
import net.fina.server.mdt.entity.MDTComparison;
import net.fina.server.mdt.entity.MDTDependentNode;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.reg.impl.RegProcessStatus;
import net.fina.server.reg.model.RegProcessConfig;
import net.fina.server.returns.entity.*;

import java.util.*;

public interface ReturnLocal {
    List<ReturnModel> loadPackages(FilterConfig filterConfig, int limit, int offset, SortField sortField);

    long getPackagesCount(FilterConfig filterConfig);

    List<Map<String, Object>> laodPackageReturns(long fiId, long periodId, long returnTypeId, long returnVersionId, FilterConfig filterConfig);

    TreeMap<Integer, Integer> loadReturnsStatisticInfo(Date statusDateFrom, Date statusDateTo);

    Return createReturn(long scheduleId, long versionId, long userId, final List<MDTDependentNode> mdtDependentNodes, Map<Long, List<MDTNode>> allMdtNodesByParentId, final List<MDTComparison> comparisons) throws FinATypeException;

    Return createReturn(long scheduleId, long versionId, long userId, boolean checkDependency, final List<MDTDependentNode> mdtDependentNodes, Map<Long, List<MDTNode>> allMdtNodesByParentId, final List<MDTComparison> comparisons) throws FinATypeException;

    void saveAsReturn(long returnId, long versionId, long userId, long langId, String note) throws FinATypeException;

    void updateLastVersion(long scheduleId, long lastVersionId);

    void addReturnStatus(long returnId, ProcessStatus status, long versionId, long userId, String note, String processId);

    List<ReturnModel> loadNotCreatedPackages(FilterConfig filterConfig);

    List<Map<String, Object>> loadNotCreatedPackageReturns(long fiId, long periodId, long returnTypeId, long returnVersionId, FilterConfig filterConfig, List<String> returnDefinitionCodes);

    net.fina.server.returns.xml.Return returnToXml(long returnId, long versionId, long langId);

    void saveReturnXml(long returnId, long versionId, long langId, String name, String outFolderLocation);

    ReturnStatus loadReturnCurrentStatus(long returnId);

    List<ReturnStatus> loadReturnStatuses(long returnId);

    List<String> getReturnHeader(long returnId, long langId);

    void delete(Collection<Long> returnIds);

    List<ImportedXmlReturnModel> loadImportedXmlReturns(long returnId);

    Map<Long, Long> gerReturnReturnDefinitionIds(List<Long> retruns);

    List<ReturnStatusesModel> getReturnStatuses(long periodId, List<Long> fiIds, List<Long> definitionIds, long returnVersionId);

    long getReturnsCount(List<Long> fiIds, long definitionId, long typeId, long versionId, ProcessStatus status, Date periodFrom, Date periodTo);

    long getReturnScheduleId(long returnId);

    ProcessStatus getXmlReturnProcessStatus(int importedReturnId, String returnVersionCode);

    long getReturnId(long importedXlretunId);

    List<Object[]> searchReturnItems(Map<ReturnItemsSearchFilter, Object> filterObjectMap, int limit, int offset);

    long getSearchReturnItemsCount(Map<ReturnItemsSearchFilter, Object> filterObjectMap);

    List<OverdueReturnModel> loadOverdueReturnModels(Map<OverdueReturnFilter, Object> filter, long langId);

    List<OverdueReturnModel> loadOverdueReturnModels(Map<OverdueReturnFilter, Object> filter, long langId, int offset, int limit, SortInfo sortInfo);

    List<ReturnSubmissionPackageModel> loadReturnSubmissionPackages(ReturnSubmissionPackageModel filterModel, long langId, int offset, int limit);

    int countReturnSubmissionPackages(ReturnSubmissionPackageModel filterModel);

    Long getUploadedFileIdByPackage(long fiId, long periodId, long returnTypeId, long returnVersionId);

    long getNotCreatedPackagesCount(FilterConfig config);

    MiChartDataModel loadMiChartData();

    List<ReturnModel> loadFiAwaitingReturns(List<Schedule> schedules);

    List<ReturnModel> loadFiInProgressReturns(List<Schedule> schedules);

    long countManualInputSubmittedReturns();

    List<ReturnModel> loadReturnsBySchedules(List<Long> schedules);

    void createReturns(Map<Long, RegProcessStatus> scheduleImportMap, RegProcessConfig regProcessConfig, String versionCode, long uploadFileId, Language language, String fiCode);

    List<Long> getAcceptedSchedules(List<Long> scheduleIds);

    byte[] getImportedReturnErrorContent(long returnId);

    List<ImportedReturnError> getImportedReturnErrorsByFileId(long fileId);

    ImportedReturn getImportedReturnById(int returnId) throws FinATypeException;

    List<ReturnModel> loadReturnSimple(Date fromDate, Date toDate);

    List<Return> loadReturnsById(Long... returnIds);

    void sendAcceptRejectNotificationToUsers(List<Long> returnIds, ProcessStatus status, String note);

    List<ImportedXmlReturnModel> loadReturnsXMLsByImportedReturnId(List<Integer> importedReturnIds);

    ReturnModel getReturnById(long returnId);
}
