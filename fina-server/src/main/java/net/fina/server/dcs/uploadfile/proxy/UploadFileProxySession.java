package net.fina.server.dcs.uploadfile.proxy;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.client.dcs.UploadFileStatus;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.returns.UploadFileStatusModel;
import net.fina.common.server.util.PagingUtil;
import net.fina.common.shared.ContentModel;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.messages.MessagesUtil;
import net.fina.security.api.AuthorizationLocal;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.dcs.uploadfile.api.UploadFileLocal;
import net.fina.server.dcs.uploadfile.entity.SortInfo;
import net.fina.server.dcs.uploadfile.entity.UploadFile;
import net.fina.server.dcs.uploadfile.model.ImportStatusDetailModel;
import net.fina.server.dcs.uploadfile.model.UploadFileMetaModel;
import net.fina.server.dcs.uploadfile.model.UploadFileRequestMetaModel;
import net.fina.server.dcs.uploadfile.model.helper.ImportStatusMetaModelHelper;
import net.fina.server.dcs.uploadfile.model.helper.UploadFileModelHelper;
import net.fina.server.dcs.uploadfile.util.UploadFilePrintUtil;
import net.fina.server.fi.api.FiLocal;
import net.fina.server.fi.entity.Fi;
import net.fina.server.fsop.event.DcsUploadFileProcessStatusUpdateEvent;
import net.fina.server.i18n.api.LanguageLocal;
import net.fina.server.i18n.entity.Language;
import net.fina.server.i18n.helper.Description;
import net.fina.server.returns.api.ReturnLocal;
import net.fina.server.returns.entity.ImportedReturn;
import net.fina.server.returns.entity.ImportedReturnError;
import net.fina.server.security.api.PermissionLocal;
import net.fina.server.security.api.UserLocal;
import net.fina.server.security.entity.User;
import org.apache.commons.lang.text.StrSubstitutor;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.logging.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Stateless
@SecurityDomain("FinASecurityDomain")
@RolesAllowed(PermissionIdNames.DCS_FILEREVIEW)
public class UploadFileProxySession {
    private final Logger log = Logger.getLogger(getClass().getName());

    @Inject
    private UploadFileLocal uploadFileLocal;
    @Inject
    private FiLocal fiLocal;
    @Inject
    private LanguageLocal languageLocal;
    @Inject
    private UserLocal userLocal;
    @Inject
    private Event<DcsUploadFileProcessStatusUpdateEvent> dcsUploadFileProcessStatusUpdateEvent;
    @Inject
    private ReturnLocal returnLocal;
    @Inject
    private AuthorizationLocal authorizationLocal;
    @Inject
    private PermissionLocal permissionLocal;

    public UploadFileMetaModel checkUploadFileSchedule(UploadFileMetaModel model,
                                                       String fiCode,
                                                       String periodFrom,
                                                       String periodTo,
                                                       List<String> returnCodes,
                                                       boolean updateFileStatus,
                                                       String dateFormat) {
        UploadFileMetaModel result = uploadFileLocal.checkUploadFileSchedule(model, fiCode, periodFrom, periodTo, returnCodes, dateFormat);


        //Update file status
        if (updateFileStatus && !result.getStatus().equalsIgnoreCase(Integer.toString(UploadFileStatus.UPLOADED.ordinal()))) {
            uploadFileLocal.updateUploadFile(result);
        }

        return result;
    }

    public UploadFileMetaModel uploadFileWithLogin(UploadFileRequestMetaModel file, String login, String languageCode) throws FinATypeException {
        UploadFile uploadFile = getUploadFile(file);
        uploadFile.setUser(userLocal.findUserbyLogin(login));

        return UploadFileModelHelper.toMetaModel(uploadFileLocal.saveUploadFile(uploadFile, languageCode, false));
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public UploadFileMetaModel uploadFileStream(UploadFileRequestMetaModel file, InputStream inputStream, String languageCode, boolean convert, String contentLength) throws FinATypeException {
        UploadFile uploadFile = getUploadFile(file);
        uploadFile.setUser(userLocal.getCurrentUser());

        return UploadFileModelHelper.toMetaModel(uploadFileLocal.saveUploadFileStream(uploadFile, inputStream, languageCode, convert, contentLength));
    }


    private UploadFile getUploadFile(UploadFileRequestMetaModel file) {
        UploadFile uploadFile = new UploadFile();
        uploadFile.setFileName(file.getFileName());
        uploadFile.setUploadedTime(new Date());
        uploadFile.setUploadedFile(file.getContent());
        uploadFile.setStatus(Integer.toString(UploadFileStatus.UPLOADED.ordinal()));
        uploadFile.setType(file.getUploadType());
        uploadFile.setBankCode(file.getBankCode());

        return uploadFile;
    }

    public PaginatedListWrapper<UploadFileMetaModel> loadUploadedFiles(Integer page, Integer pageSize, String sortField,
                                                                       String sortDir, UploadFile filterObj) {
        long start = System.currentTimeMillis();
        Map<String, Description> codeAndNamesMap = fiLocal.loadFiCodeAndNames();
        String locale = ThreadLocalHolder.getLanguage().getCode();

        List<String> fiCodes = fiLocal.loadFiCodes();
        List<String> userPermissions = authorizationLocal.loadUserPermission(userLocal.getCurrentUserLogin());
        boolean showUndefinedBanks = (permissionLocal.loadPermissionByIdName(PermissionIdNames.DCS_UNDEFINED_BANK) == null || userPermissions.contains(PermissionIdNames.DCS_UNDEFINED_BANK));


        Language language = languageLocal.getLanguageByCode(locale);
        sortField = sortField == null || sortField.trim().isEmpty() ? "uploadedTime" : sortField;
        sortDir = sortDir == null || sortDir.trim().isEmpty() ? "DESC" : sortDir.toUpperCase();

        List<UploadFileMetaModel> models = UploadFileModelHelper.toMetaModel(
                uploadFileLocal.loadUploadFiles(Collections.singletonList(new SortInfo(sortField, sortDir)),
                        filterObj, fiCodes, userPermissions, showUndefinedBanks, pageSize, PagingUtil.getOffsetFromPage(page, pageSize)));

        Map<String, String> messageBundleMap = MessagesUtil.loadMessageBundleMap(locale);

        models.stream().filter(ufm -> ufm.getBankCode() != null && !ufm.getBankCode().trim().isEmpty())
                .forEach(ufm -> {
                    Description bankName = codeAndNamesMap.get(ufm.getBankCode());
                    if (bankName != null) {
                        ufm.setBankName(codeAndNamesMap.get(ufm.getBankCode()).getDescription(language.getId()));
                    }
                    if (ufm.getReason() != null && !ufm.getReason().trim().isEmpty()) {
                        String compiledReason = StrSubstitutor.replace(ufm.getReason(), messageBundleMap);
                        compiledReason = MessagesUtil.getString(compiledReason, language.getCode());
                        ufm.setReason(compiledReason);
                    }
                });


        PaginatedListWrapper<UploadFileMetaModel> result = new PaginatedListWrapper<>();
        result.setList(models);
        result.setTotalResults(0);
        result.setPageSize(pageSize);
        result.setCurrentPage(page);

        long end = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();
        sb.append("\n")
                .append("================================================")
                .append("\n")
                .append("Loading upload Files took : ").append(end - start).append("ms")
                .append("\n")
                .append("================================================");
        log.info(sb.toString());
        return result;
    }

    public UploadFileRequestMetaModel getUploadFileById(Long fileId, String locale) {
        Language language = languageLocal.getLanguageByCode(locale);

        UploadFile file = uploadFileLocal.loadUploadFile(fileId);
        UploadFileRequestMetaModel model = new UploadFileRequestMetaModel();
        model.setBankCode(file.getBankCode());
        model.setId(file.getId());
        model.setContent(file.getUploadedFile());
        model.setFileName(file.getFileName());
        model.setUploadType(file.getType());
        model.setRepositoryFileId(file.getRepositoryFileId());
        model.setRepositoryFileVersionId(file.getRepositoryFileVersionId());
        if (file.getBankCode() != null) {
            model.setBankName(fiLocal.findFiByCode(file.getBankCode()).getDescription().getDescription(language.getId()));
        }
        if (file.getUploadedTime() != null) {
            model.setUploadTime(file.getUploadedTime());
        }

        return model;
    }

    public List<UploadFileStatusModel> loadUploadFileStatuses(String locale) {
        return uploadFileLocal.loadUploadFileFilterStatuses().stream().map(statusString -> {
            UploadFileStatus status = UploadFileStatus.getStatus(statusString);
            return new UploadFileStatusModel(status, MessagesUtil.loadMessageBundle(locale).getString(status.getCode()));
        }).toList();
    }

    public void deleteFiles(List<Long> filesId) {
        uploadFileLocal.deleteUploadFiles(filesId);
    }

    public void updateUploadFile(long fileId, byte[] content) throws FinATypeException {
        uploadFileLocal.updateUploadFile(fileId, content);
    }

    public long countUploadFiles(UploadFile filter) {
        List<String> fiCodes = fiLocal.loadFiCodes();
        List<String> userPermissions = authorizationLocal.loadUserPermission(userLocal.getCurrentUserLogin());
        boolean showUndefinedBanks = (permissionLocal.loadPermissionByIdName(PermissionIdNames.DCS_UNDEFINED_BANK) == null || userPermissions.contains(PermissionIdNames.DCS_UNDEFINED_BANK));

        return uploadFileLocal.getUploadFilesCount(filter, fiCodes, userPermissions, showUndefinedBanks);
    }

    public List<ImportStatusDetailModel> loadImportStatusDetails(long fileId) {
        User currentUser = userLocal.getCurrentUser();
        List<String> fiCodes = Stream.concat(
                        currentUser
                                .getFis()
                                .stream()
                                .map(Fi::getCode),
                        currentUser
                                .getRoles()
                                .stream()
                                .flatMap((e) -> e.getFis().stream().map(Fi::getCode)))
                .collect(Collectors.toList());

        List<ImportedReturn> res = uploadFileLocal.loadImportedReturnsByUploadFile(fileId)
                .stream()
                .filter((e) -> fiCodes.contains(e.getBankCode())).collect(Collectors.toList());
        Collections.sort(res, new Comparator<ImportedReturn>() {
            @Override
            public int compare(ImportedReturn o1, ImportedReturn o2) {
                return o1.getReturnCode().compareTo(o2.getReturnCode());
            }
        });
        return ImportStatusMetaModelHelper.toModels(res);
    }

    public void updateStatFileError(UploadFileRequestMetaModel file, long uploadFileId) throws FinATypeException {
        uploadFileLocal.setStatFileErrorStatus(uploadFileId);
        uploadFileLocal.updateUploadFile(uploadFileId, file.getContent());
        //Update DCS file status
        DcsUploadFileProcessStatusUpdateEvent processStatusUpdateEvent = new DcsUploadFileProcessStatusUpdateEvent(uploadFileId);
        this.dcsUploadFileProcessStatusUpdateEvent.fire(processStatusUpdateEvent);
    }

    public UploadFileMetaModel getUploadFileModelById(long fileId) {
        return uploadFileLocal.loadUploadFileById(fileId);
    }

    public byte[] getFileErrorLog(long fileId) {

        List<ImportedReturnError> errors = returnLocal.getImportedReturnErrorsByFileId(fileId);
        if (!errors.isEmpty()) {
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                errors.forEach(error -> out.write(error.getErrorContent(), 0, error.getErrorContent().length));

                return out.toByteArray();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }

        StringBuilder sb = new StringBuilder();
        List<ImportStatusDetailModel> statusDetailModels = loadImportStatusDetails(fileId);

        for (ImportStatusDetailModel model : statusDetailModels) {

            buildErrorLogTemplate(sb, model.getCode(),
                    model.getVersion(),
                    model.getPeriodStart(),
                    model.getPeriodEnd(),
                    model.getStatus(),
                    model.getMessage(),
                    model.getImportStart(),
                    model.getImportEnd());
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    public Map<String, Object> getConfig() {
        Map<String, Object> result = new HashMap<>();
        try {
            result.put("fileNamePatterns", uploadFileLocal.getAllPatterns());
            List<String> userFiCodes = userLocal.getUserFiCodes(userLocal.getCurrentUserLogin());
            if (!userFiCodes.isEmpty()) {
                String fiTypeCode = fiLocal.getFiTypeCodeByFiCode(userFiCodes.get(0));
                result.put("periodTypePattern", uploadFileLocal.getPeriodTypePatternsForFiType(fiTypeCode));
            }
        } catch (FinATypeException e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    public String buildErrorLogTemplate(StringBuilder sb, String code, String version, Date periodStart, Date periodEnd, String status, String message, Date importStart, Date importEnd) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        sb.append("Timestamp : ")
                .append(formatter.format(new Date()))
                .append("\r\n\r\n");

        SimpleDateFormat shortFormatter = new SimpleDateFormat("yyyy/MM/dd");

        sb.append("---------------------------------------------\n")
                .append("\t").append("Code : ").append(code)
                .append("\n").append("\t").append("Version : ").append(version)
                .append("\n").append("\t").append("Period Start : ").append(periodStart != null ? shortFormatter.format(periodStart) : "")
                .append("\n").append("\t").append("Period End : ").append(periodEnd != null ? shortFormatter.format(periodEnd) : "")
                .append("\n").append("\t").append("Status : ").append(status)
                .append("\n").append("\t").append("Import Start : ").append(importStart != null ? formatter.format(importStart) : "")
                .append("\n").append("\t").append("Import End : ").append(importEnd != null ? formatter.format(importEnd) : "")
                .append("\n").append("\t").append("Message : ").append(message)
                .append("\n");

        return sb.toString();
    }

    public ContentModel generateUploadedFilesReport(UploadFile filter, String fileType, String contextPath) throws FinATypeException {
        List<String> fiCodes = fiLocal.loadFiCodes();
        List<String> userPermissions = authorizationLocal.loadUserPermission(userLocal.getCurrentUserLogin());
        boolean showUndefinedBanks = (permissionLocal.loadPermissionByIdName(PermissionIdNames.DCS_UNDEFINED_BANK) == null || userPermissions.contains(PermissionIdNames.DCS_UNDEFINED_BANK));

        List<UploadFile> uploadFiles = uploadFileLocal.loadUploadFiles(Collections.emptyList(), filter, fiCodes, userPermissions, showUndefinedBanks, -1, -1);

        uploadFiles = uploadFiles.stream()
                .peek(uf -> {
                    if (uf.getBankCode() == null) {
                        uf.setBankCode("InvalidFi");
                    }
                })
                .collect(Collectors.toList());

        return UploadFilePrintUtil.generateFile(fileType, uploadFiles, contextPath, ThreadLocalHolder.getLanguage().getCode());
    }

}
