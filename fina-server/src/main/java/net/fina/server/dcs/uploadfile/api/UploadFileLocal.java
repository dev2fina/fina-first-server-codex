package net.fina.server.dcs.uploadfile.api;

import net.fina.common.client.dcs.UploadFileStatus;
import net.fina.common.client.dcs.UploadType;
import net.fina.common.client.exception.DcsTypeException;
import net.fina.common.client.exception.FinATypeException;
import net.fina.server.dcs.uploadfile.entity.SortInfo;
import net.fina.server.dcs.uploadfile.entity.UploadFile;
import net.fina.server.dcs.uploadfile.impl.event.UploadFileErrorEvent;
import net.fina.server.dcs.uploadfile.model.UploadFileMetaModel;
import net.fina.server.fsop.event.ImportedReturnSaveEvent;
import net.fina.server.returns.entity.ImportedReturn;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface UploadFileLocal {

    UploadFile saveUploadFile(UploadFile uploadFile, String languageCode, boolean fireConvertEvent) throws FinATypeException;

    UploadFile saveUploadFileStream(UploadFile uploadFile, InputStream inputStream, String languageCode, boolean fireConvertEvent, String contentLength) throws FinATypeException;

    UploadFile convert(long uploadFileId, String languageCode);

    List<UploadFile> loadUploadFiles(List<SortInfo> sortInfos, UploadFile filterConfig, List<String> fiCodes, List<String> userPermissions, boolean showUndefinedBanks, int limit, int offset);

    long getUploadFilesCount(UploadFile filterConfig, List<String> fiCodes, List<String> userPermissions, boolean showUndefinedBanks);

    List<String> loadUploadFileFilterStatuses();

    Map<Integer, Integer> loadDcsEmailStatistic(Date fromDate, Date toDate);

    Map<Integer, Integer> loadEmailCounter(Date fromDate, Date toDate);

    List<ImportedReturn> loadImportedReturnsByUploadFile(long uploadFileId);

    UploadFile loadUploadFileContent(long fileId);

    void deleteUploadFiles(List<Long> filesId);

    List<String> getAllPatterns() throws FinATypeException;

    Map<String, String> getPeriodTypePatternsForFiType(String fiTypeCode) throws FinATypeException;

    List<Object[]> loadUploadFileDateAndStatus(Date fromDate, Date toDate, String fiType, List<UploadType> types);

    Map<Integer, Integer> loadStatisticData(Date fromDate, Date toDate, String fiType);

    void checkUploadFileStatus(UploadFile file);

    void saveImportedReturns(ImportedReturnSaveEvent event);

    void updateUploadFile(UploadFileMetaModel uploadFileMetaModel);

    boolean checkFileUnique(UploadFile uploadFile);

    void manageDcsTypeException(UploadFile uploadFile, DcsTypeException ex);

    DcsTypeException siftDcsTypeException(Throwable t);

    ImportedReturn loadImportedReturnDetails(int returnId, String fileName);

    boolean checkAllFileProcessingDone();

    void updateUploadFile(long fileId, byte[] content) throws FinATypeException;

    void setStatFileErrorStatus(long fileId);

    void onFileUploadErrorEvent(UploadFileErrorEvent uploadFileErrorEvent);

    UploadFileMetaModel loadUploadFileById(long fileId);

    UploadFile loadUploadFile(long fileId);

    UploadFileMetaModel checkUploadFileSchedule(UploadFileMetaModel model,
                                                String fiCode,
                                                String periodFrom,
                                                String periodTo,
                                                List<String> returnCodes,
                                                String dateFormat);

    void changeStatus(long fileId, UploadFileStatus status, String reason);

}
