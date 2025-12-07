package net.fina.server.dcs.uploadfile.impl;

import com.google.gson.Gson;
import jakarta.ejb.*;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import net.fina.common.client.constants.ImportStatus;
import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.client.dcs.DocumentType;
import net.fina.common.client.dcs.UploadFileStatus;
import net.fina.common.client.dcs.UploadType;
import net.fina.common.client.exception.ConverterDcsTypeException;
import net.fina.common.client.exception.DcsTypeException;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.property.PropertyKeys;
import net.fina.common.client.returns.ImportedFileType;
import net.fina.common.server.util.ConfigurationUtil;
import net.fina.common.shared.jms.model.UserInfoMessage;
import net.fina.messages.MessagesUtil;
import net.fina.security.api.AuthorizationLocal;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.calendar.api.CalendarLocal;
import net.fina.server.dcs.jms.DcsJMSClient;
import net.fina.server.dcs.service.MatrixReferenceMappingService;
import net.fina.server.dcs.uploadfile.api.UploadFileLocal;
import net.fina.server.dcs.uploadfile.api.UploadFileStreamable;
import net.fina.server.dcs.uploadfile.entity.SortInfo;
import net.fina.server.dcs.uploadfile.entity.UploadFile;
import net.fina.server.dcs.uploadfile.entity.UploadFile_;
import net.fina.server.dcs.uploadfile.impl.converter.AbstractConverter;
import net.fina.server.dcs.uploadfile.impl.converter.AbstractConverterFactory;
import net.fina.server.dcs.uploadfile.impl.converter.ConverterInfo;
import net.fina.server.dcs.uploadfile.impl.converter.ConverterUtil;
import net.fina.server.dcs.uploadfile.impl.event.UploadFileConvertEvent;
import net.fina.server.dcs.uploadfile.impl.event.UploadFileErrorEvent;
import net.fina.server.dcs.uploadfile.impl.reader.excel.*;
import net.fina.server.dcs.uploadfile.impl.reader.excel.util.MatrixMappingUtil;
import net.fina.server.dcs.uploadfile.impl.util.FileAnalyzer;
import net.fina.server.dcs.uploadfile.model.ProcessEngine;
import net.fina.server.dcs.uploadfile.model.UploadFileMetaModel;
import net.fina.server.dcs.uploadfile.model.helper.UploadFileModelHelper;
import net.fina.server.fi.api.FiLocal;
import net.fina.server.fi.entity.Fi;
import net.fina.server.fsop.api.UploadFileQueueStoreLocal;
import net.fina.server.fsop.entity.UploadFileQueue;
import net.fina.server.fsop.entity.UploadFileQueueStatus;
import net.fina.server.fsop.event.DcsUploadFileProcessStatusUpdateEvent;
import net.fina.server.fsop.event.ImportedReturnSaveEvent;
import net.fina.server.i18n.api.LanguageLocal;
import net.fina.server.i18n.entity.Language;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.jcr.impl.FileContentManagementSession;
import net.fina.server.matrix.entity.Matrix;
import net.fina.server.matrix.entity.SubMatrix;
import net.fina.server.returns.api.ImportLocal;
import net.fina.server.returns.api.ReturnDefinitionLocal;
import net.fina.server.returns.api.ScheduleLocal;
import net.fina.server.returns.entity.ImportedReturn;
import net.fina.server.returns.entity.ImportedReturn_;
import net.fina.server.returns.xml.Header;
import net.fina.server.returns.xml.ObjectFactory;
import net.fina.server.returns.xml.Return;
import net.fina.server.security.api.PermissionLocal;
import net.fina.server.security.api.PropertyLocal;
import net.fina.server.security.api.UserLocal;
import net.fina.server.security.entity.User;
import net.fina.server.st.impl.MdtReleaseVersion;
import net.fina.server.util.DBUtil;
import net.fina.server.util.MappingUtil;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.logging.Logger;

import java.io.*;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Stateless
@Local(UploadFileLocal.class)
@Interceptors(RecordingAuditor.class)
public class UploadFileSession implements UploadFileLocal {

    private final Logger log = Logger.getLogger(getClass());

    @Inject
    private EntityManager em;

    @EJB
    private UserLocal current;
    @EJB
    private LanguageLocal languageLocal;
    @EJB
    private ImportLocal importLocal;
    @EJB
    private FiLocal fiLocal;
    @EJB
    private AuthorizationLocal authorizationLocal;
    @EJB
    private PropertyLocal propertyLocal;
    @EJB
    private ScheduleLocal scheduleLocal;
    @EJB
    private ReturnDefinitionLocal returnDefinitionLocal;
    @EJB
    private PermissionLocal permissionLocal;
    @EJB
    private UploadFileQueueStoreLocal uploadFileQueueStoreLocal;
    @EJB
    private MatrixReferenceMappingService mappingServiceSingleton;
    @Inject
    private CalendarLocal calendarLocal;

    @Inject
    private Event<ImportedReturnSaveEvent> importedReturnSaveEvent;
    @Inject
    private Event<UploadFileConvertEvent> uploadFileConvertEvent;
    @Inject
    private Event<DcsUploadFileProcessStatusUpdateEvent> dcsUploadFileProcessStatusUpdateEvent;

    @Inject
    private FileContentManagementSession fileContentManagementSession;
    @Inject
    private DcsJMSClient dcsJMSClient;

    @Override
    public UploadFile saveUploadFile(UploadFile uploadFile, String languageCode, boolean fireConvertEvent) throws FinATypeException {
        return saveUploadFileStream(uploadFile, new ByteArrayInputStream(uploadFile.getUploadedFile()), languageCode, fireConvertEvent, String.valueOf(uploadFile.getUploadedFile().length));
    }

    @Override
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public UploadFile saveUploadFileStream(UploadFile uploadFile, InputStream inputStream, String languageCode, boolean fireConvertEvent, String contentLength) throws FinATypeException {
        if (uploadFile.getUser() != null && checkUserUploadLimit(uploadFile)) {
            User user = uploadFile.getUser();
            user.setBlocked(true);
            em.merge(user);
            uploadFile.setStatus(UploadFileStatus.ERROR);
            fileContentManagementSession.saveUploadFileStream(uploadFile, inputStream, contentLength);
            log.error("User [" + uploadFile.getUser().getLogin() + "] Reached Max File Upload Limit, Blocking it!");

            boolean isExternalUser = authorizationLocal.hasUserPermission(user.getLogin(), PermissionIdNames.FINA_WEB_EXTERNAL_USER);
            //send logout jms message
            if (isExternalUser) {
                dcsJMSClient.sendUserInfoMessage(new UserInfoMessage(user.getLogin(), true));
            }

            return uploadFile;
        }

        fileContentManagementSession.saveUploadFileStream(uploadFile, inputStream, contentLength);


        if (fireConvertEvent) {
            uploadFileConvertEvent.fire(new UploadFileConvertEvent(uploadFile.getId(), languageCode, uploadFile.getFileName()));
        }
        return uploadFile;
    }

    private UploadFile convert(UploadFile uploadFile, String languageCode) {
        try {
            log.info("Convert File Id = " + uploadFile.getId());

            MatrixMappingSource matrixMappingSource = MatrixMappingUtil.getMatrixMappingSource();

            if (matrixMappingSource.equals(MatrixMappingSource.UNKNOWN)) {
                log.error("MATRIX_MAPPING_SOURCE property is not set in fina.xml ");
                throw new DcsTypeException(DcsTypeException.Type.GENERAL_ERROR, "MATRIX_MAPPING_SOURCE property is not set in fina.xml ");
            }
            Language language = languageLocal.getLanguageByCode(languageCode.trim());

            MatrixOptionBase option = getSelectedPattern(uploadFile, language.getId());
            uploadFile.setNameValid(option != null);

            if (uploadFile.isNameValid()) {
                log.info("Upload File Name is Valid");

                Header header = getGeneratedHeader(option, uploadFile.getFileName(), language);

                List<String> userFis = fiLocal.loadFiCodes(uploadFile.getUser().getLogin());

                if (hashUserFi(userFis, header)) {
                    uploadFile.setHasUserBank(true);

                    // Set Upload File FI Code
                    log.info("Upload File [ " + uploadFile.getFileName() + " ] Fi code = " + header.getBankCode());
                    uploadFile.setBankCode(header.getBankCode());

                    //TODO file level permission validation


                    //Check File unique
                    if (checkFileUnique(uploadFile)) {

                        // Generate Properties
                        Map<String, Object> properties = new HashMap<>();

                        FileAnalyzer analyzer = new FileAnalyzer(option.getPattern(), uploadFile.getFileName(), option, language);
                        String extension = analyzer.getExtension().toLowerCase();
                        properties.put("dcs.file.extension", extension);
                        properties.put("mdtReleaseVersion", propertyLocal.getSystemProperty(PropertyKeys.MDT_RELEASE_VERSION));

                        DocumentType documentType = ConverterUtil.detectDocumentType(extension);

                        properties.put("documentType", documentType);
                        log.info("Convert file type - " + documentType);

                        switch (documentType) {
                            case ZIP:
                                properties.put("dcs.zip.oneFileControl.enable", getZipConverterOneFileControlEnable());
                            case FINA:
                            case VIP_NET:
                            case XML:
                            case EXCEL: {
                                properties.put("dcs.matrix.mapping.source", matrixMappingSource);
                                properties.put("dcs.fi", fiLocal.findFiByCode(header.getBankCode()));
                                properties.put("dcs.primary.matrix.option", option);

                                if (matrixMappingSource.equals(MatrixMappingSource.EXCEL)) {
                                    String selectedMatrix = option.getMatrixForEachType();
                                    log.info("Selected Matrix - " + selectedMatrix);
                                    String matrixPath = getMatrixPath();
                                    properties.put("dcs.main.matrix", matrixPath + "Matrix.xls");
                                    properties.put("dcs.primary.matrix", matrixPath + selectedMatrix);
                                } else if (matrixMappingSource.equals(MatrixMappingSource.DATABASE)) {
                                    properties.put("net.fina.matrix.mapping.options.data", getMatrixMappingOptions((MatrixOption) option));
                                    uploadFile.setMatrixId(((MatrixOption) option).getMatrixId());
                                }
                                properties.put("dcs.language", language);
                                properties.put("dcs.returnDefCodesList", returnDefinitionLocal.loadDefinitionCodes());
                                properties.put("converter.VCT.emptyLine", propertyLocal.getSystemProperty(PropertyKeys.VCT_EMPTY_LINES) == null ? 0 : propertyLocal.getSystemProperty(PropertyKeys.VCT_EMPTY_LINES));
                                properties.put("dcs.excelSheetControl", propertyLocal.getSystemProperty(PropertyKeys.UPLOAD_FILE_EXCEL_SHEET_CONTROL));

                                String password = propertyLocal.getSystemProperty(PropertyKeys.DEFAULT_PROTECTION_PASSWORD);
                                properties.put("dcs.excel.sheetProtection.password.string", (password == null ? "" : password));

                                String passwords = propertyLocal.getSystemProperty(PropertyKeys.PROTECTION_PASSWORDS);
                                properties.put("dcs.excel.sheetProtection.passwordByFiType.json", (passwords == null ? "" : passwords));
                                properties.put(PropertyKeys.FILE_SIGNATURE_CHECKER_SIGNER_PROPERTY, propertyLocal.getSystemProperty(PropertyKeys.FILE_SIGNATURE_CHECKER_SIGNER_PROPERTY));

                                break;
                            }
                        }

                        properties.put("dcs.xml.header", header);

                        // Security Properties
                        properties.put("net.fina.dcs.security.encrypt", option.isEncryptEnabled());
                        properties.put("net.fina.dcs.security.sign", option.isDigitalSignatureCheckEnabled());
                        properties.put("dcs.security.sign.extension", ".sign");
                        properties.put("versionFileName", "version.properties");

                        properties.put("dcs.user.login", uploadFile.getUser().getLogin().trim().toLowerCase());

                        // Load Certificate
                        ConfigurationUtil util = ConfigurationUtil.get();
                        String keyStoreRepositoryPath = util.get("KeyStoreRepositoryPath");
                        if ((keyStoreRepositoryPath != null) && (!keyStoreRepositoryPath.isEmpty())) {
                            File keyStoreFiles = new File(keyStoreRepositoryPath);
                            if (keyStoreFiles.exists() && (keyStoreFiles.isDirectory())) {
                                File keyStoreFile = new File(keyStoreFiles, uploadFile.getUser().getLogin().trim() + ".pfx");
                                if (keyStoreFile.exists()) {
                                    try (FileInputStream in = new FileInputStream(keyStoreFile)) {
                                        byte[] keyStoreBytes = new byte[in.available()];
                                        in.read(keyStoreBytes);
                                        properties.put("dcs.security.certificate", keyStoreBytes);
                                    }
                                }
                            }
                        }

                        properties.put("dcs.security.sign.rootCA", util.get("DCS_FILE_SIGN_ROOT_CA_FILE"));

                        log.info("Execute Convert");

                        ProcessEngine processEngine = option.getProcessEngine();
                        processEngine = processEngine == null ? ProcessEngine.FINA : processEngine;
                        uploadFile.setProcessEngine(processEngine);
                        if (processEngine.equals(ProcessEngine.REG) || processEngine.equals(ProcessEngine.REG_ADVANCED)) {
                            log.info("Save UPLOAD FILE QUEUE " + processEngine.name() + " File : " + uploadFile.getFileName());
                            uploadFile.setProcessEngine(processEngine);
                            uploadFile.setStatus(String.valueOf(UploadFileStatus.WORKING.ordinal()));
                            uploadFile.setMatrixValid(true);
                            UploadFileQueue uploadFileQueue = new UploadFileQueue();
                            uploadFileQueue.setFileId(uploadFile.getId());
                            uploadFileQueue.setFileName(uploadFile.getFileName());
                            uploadFileQueue.setUserId(uploadFile.getUser().getId());
                            uploadFileQueue.setStatus(UploadFileQueueStatus.UNDEFINED);
                            uploadFileQueue.setProcessEngine(processEngine);

                            Date toDate = new SimpleDateFormat(language.getDateFormat()).parse(header.getPeriodEnd());
                            //validate future date
                            if (!checkRegFileDueDate(toDate)) {
                                log.info("Upload file " + uploadFile.getFileName() + " ; Future date not allowed.");
                                uploadFile.setStatus(UploadFileStatus.REJECTED);
                                uploadFile.setReason("${net.fina.dcs.upload.future.date.error}");
                            }
                            em.persist(uploadFileQueue);


                        } else {
                            //load file content stream

                            fileContentManagementSession.loadUploadFileStreamInto(uploadFile, new UploadFileStreamable() {
                                @Override
                                public void readFileStream(InputStream inputStream) throws Exception {
                                    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();) {
                                        byte[] buffer = new byte[1024]; // Buffer size of 1 KB
                                        int bytesRead;

                                        // Read from InputStream and write to ByteArrayOutputStream
                                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                                            byteArrayOutputStream.write(buffer, 0, bytesRead);
                                        }

                                        // Return the byte array
                                        uploadFile.setUploadedFile(byteArrayOutputStream.toByteArray());
                                    } catch (Throwable t) {
                                        log.error(t.getMessage(), t);
                                    }
                                }
                            });

                            //convert & process Fina File
                            AbstractConverterFactory abstractConverterFactory = AbstractConverterFactory.getInstance();
                            AbstractConverter converter = abstractConverterFactory.createAbstractConverter(uploadFile, properties, documentType);
                            ConverterInfo info = converter.convert();
                            List<Return> returns = info.getReturns();
                            List<String> reasons = info.getReasons();
                            uploadFile.setReason(String.join(";", reasons));
                            uploadFile.setMatrixValid(uploadFile.getReason().isEmpty());
                            uploadFile.setStatus(UploadFileStatus.CONVERTED);
                            uploadFile.setProtectioninfo("[]");

                            List<ImportedReturn> importedReturns = getImportedReturns(returns, uploadFile);
                            log.info("Status " + info.getStatus().name() + " | Ordinal = " + uploadFile.getStatus());

                            if (checkFileDate(importedReturns)) {
                                if (checkUploadFileDueDate(importedReturns)) {
                                    log.info("Saving XMLs...");
                                    UploadFileQueue uploadFileQueue = new UploadFileQueue();
                                    uploadFileQueue.setFileId(uploadFile.getId());
                                    uploadFileQueue.setFileName(uploadFile.getFileName());
                                    uploadFileQueue.setUserId(uploadFile.getUser().getId());
                                    uploadFileQueue.setStatus(UploadFileQueueStatus.UNDEFINED);
                                    uploadFileQueue.setProcessEngine(processEngine);
                                    ImportedReturnSaveEvent saveEvent = new ImportedReturnSaveEvent(uploadFileQueue, importedReturns);
                                    importedReturnSaveEvent.fire(saveEvent);
                                } else {
                                    log.info("Upload file " + uploadFile.getFileName() + " invalid due date.");
                                    uploadFile.setStatus(UploadFileStatus.REJECTED);
                                    uploadFile.setReason("${net.fina.dcs.upload.due.date.error}");
                                }
                            } else {
                                log.info("Upload file " + uploadFile.getFileName() + " ; Future date not allowed.");
                                uploadFile.setStatus(UploadFileStatus.REJECTED);
                                uploadFile.setReason("${net.fina.dcs.upload.future.date.error}");
                            }
                        }
                    } else {
                        log.info("Upload file " + uploadFile.getFileName() + " isn't unique.");
                        uploadFile.setStatus(UploadFileStatus.NOT_UNIQUE);
                        uploadFile.setReason("${" + UploadFileStatus.NOT_UNIQUE.getCode() + "}");
                    }
                } else {
                    uploadFile.setStatus(UploadFileStatus.USER_DOES_NOT_HAVE_FI);
                    uploadFile.setHasUserBank(false);
                }
            }

        } catch (DcsTypeException e) {
            log.error(e.getMessage(), e);
            long startTime = System.currentTimeMillis();
            manageDcsTypeException(uploadFile, e);
            long processTime = System.currentTimeMillis() - startTime;

            log.info("manageDcsTypeException - 1 " + uploadFile.getFileName() + " took ::: " + processTime + " ms");

        } catch (Throwable t) {
            log.error(t.getMessage(), t);

            DcsTypeException dcsTypeException = siftDcsTypeException(t);
            if (dcsTypeException == null) {
                uploadFile.setStatus(UploadFileStatus.MATRIX_ERROR);
            } else {
                long startTime = System.currentTimeMillis();
                manageDcsTypeException(uploadFile, dcsTypeException);
                long processTime = System.currentTimeMillis() - startTime;
                log.info("manageDcsTypeException - 2 " + uploadFile.getFileName() + " took ::: " + processTime + " ms");

            }
        } finally {
            if (fileContentManagementSession.isRepositoryProviderActive()) {
                uploadFile.setUploadedFile(null);
            }

            long startTime = System.currentTimeMillis();
            //Update Upload file state
            updateUploadFile(new UploadFileMetaModel().setUploadFile(uploadFile, UploadFileModelHelper.DEFAULT_DATETIME_FORMAT));
            long processTime = System.currentTimeMillis() - startTime;
            log.info(uploadFile.getFileName() + " Finally updateUploadFile took ::: " + processTime + " ms");

        }
        return uploadFile;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public UploadFile convert(long uploadFileId, String languageCode) {
        UploadFile uploadFile = em.find(UploadFile.class, uploadFileId);
        em.detach(uploadFile);
        return convert(uploadFile, languageCode);
    }

    @Override
    public void saveImportedReturns(@Observes ImportedReturnSaveEvent event) {
        importLocal.saveXmls(event.getXmls());
        em.persist(event.getUploadFileQueue());
    }

    @Override
    public void updateUploadFile(UploadFileMetaModel uploadFileMetaModel) {
        UploadFile uploadFIle = em.find(UploadFile.class, uploadFileMetaModel.getId());
        uploadFIle.setReason(uploadFileMetaModel.getReason());
        uploadFIle.setProtectioninfo(uploadFileMetaModel.getProtectioninfo());
        uploadFIle.setBankCode(uploadFileMetaModel.getBankCode());
        uploadFIle.setHasUserBank(uploadFileMetaModel.getHasUserBank());
        uploadFIle.setNameValid(uploadFileMetaModel.getNameValid());
        uploadFIle.setVersionValid(uploadFileMetaModel.getVersionValid());
        uploadFIle.setMatrixValid(uploadFileMetaModel.getMatrixValid());
        uploadFIle.setStatus(!Objects.equals(uploadFIle.getStatus(), Integer.toString(UploadFileStatus.DELETE.ordinal())) ? uploadFileMetaModel.getStatus() : uploadFIle.getStatus());
        uploadFIle.setProcessEngine(uploadFileMetaModel.getProcessEngine());
        if (uploadFileMetaModel.getMatrixId() != null && uploadFileMetaModel.getMatrixId() > 0) {
            uploadFIle.setMatrixId(uploadFileMetaModel.getMatrixId());
        }
        em.merge(uploadFIle);

        //Update DCS file status
        DcsUploadFileProcessStatusUpdateEvent processStatusUpdateEvent = new DcsUploadFileProcessStatusUpdateEvent(uploadFileMetaModel.getId());
        this.dcsUploadFileProcessStatusUpdateEvent.fire(processStatusUpdateEvent);
    }

    /**
     * Checks upload file unique if fina2.mfb.uploaded.file.unique property is
     * more then 0(zero number)
     *
     * @param uploadFile
     * @return is
     */
    @Override
    public boolean checkFileUnique(UploadFile uploadFile) {
        String uploadFileUnique = propertyLocal.getSystemProperty(PropertyKeys.UPLOADED_FILE_UNIQUE);
        if (uploadFileUnique != null && (!uploadFileUnique.isEmpty()) && (Integer.parseInt(uploadFileUnique) > 0)) {
            log.info(PropertyKeys.UPLOADED_FILE_UNIQUE + " property is activated.");

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
            Root<UploadFile> root = criteriaQuery.from(UploadFile.class);
            List<Predicate> predicates = new ArrayList<>();

            String uniqueStatuses = propertyLocal.getSystemProperty(PropertyKeys.UPLOADED_FILE_UNIQUE_STATUS);
            if (uniqueStatuses != null && !uniqueStatuses.trim().isEmpty()) {
                String[] statusOrdinals = uniqueStatuses.replaceAll(";", ",").split(",");
                StringBuilder invalidStatusOrdinals = new StringBuilder();
                List<String> ordinals = new ArrayList<>();
                int statusMaxOrdinal = UploadFileStatus.values().length;
                for (String ordinal : statusOrdinals) {
                    ordinal = ordinal.trim();
                    if (ordinal.isEmpty()) {
                        continue;
                    }
                    int intOrdinal = Integer.valueOf(ordinal);
                    if (-1 < intOrdinal && intOrdinal <= statusMaxOrdinal) {
                        ordinals.add(ordinal);
                    } else {
                        invalidStatusOrdinals.append(ordinal).append(", ");
                    }
                }
                if (invalidStatusOrdinals.length() > 0) {
                    log.error("Invalid property values");
                    log.error("Property key: " + PropertyKeys.UPLOADED_FILE_UNIQUE_STATUS);
                    log.error("Invalid  value: " + invalidStatusOrdinals.replace(invalidStatusOrdinals.length() - 2, invalidStatusOrdinals.length(), "."));
                    // TODO: throw new
                    // FinATypeException(FinATypeException.Type.GENERAL_ERROR);
                }
                if (!ordinals.isEmpty()) {
                    predicates.add(cb.not(root.get(UploadFile_.status).in(ordinals)));
                }
            }

            predicates.add(cb.equal(cb.upper(root.get(UploadFile_.fileName)), uploadFile.getFileName() != null ? uploadFile.getFileName().toUpperCase() : null));
            predicates.add(cb.notEqual(root.get(UploadFile_.id), uploadFile.getId()));
            predicates.add(cb.notEqual(root.get(UploadFile_.status), Integer.toString(UploadFileStatus.DELETE.ordinal())));

            criteriaQuery.select(root.get(UploadFile_.id));
            criteriaQuery.where(predicates.toArray(new Predicate[0]));

            return em.createQuery(criteriaQuery).getResultList().isEmpty();
        }
        return true;
    }

    @Override
    public void manageDcsTypeException(UploadFile uploadFile, DcsTypeException ex) {

        DcsTypeException.Type type = ex.getType();

        if (type == null) {
            uploadFile.setReason(ex.getMessage());
            uploadFile.setStatus(UploadFileStatus.MATRIX_ERROR);
        } else {
            switch (type) {
                case CELL_IS_NULL_OR_IS_EMPTY: {
                    uploadFile.setStatus(UploadFileStatus.MATRIX_ERROR);
                    uploadFile.setReason(ex.getMessage());
                    break;
                }
                case MATRIX_DUPLICATED_PATTERN: {
                    uploadFile.setStatus(UploadFileStatus.MATRIX_ERROR);
                    uploadFile.setReason(ex.getMessage());
                    break;
                }
                case CELL_TYPE_ERROR: {
                    uploadFile.setStatus(UploadFileStatus.MATRIX_ERROR);
                    break;
                }
                case WRONG_FILE_NAME_ERROR: {
                    uploadFile.setStatus(UploadFileStatus.WRONG_FILE_NAME);
                    break;
                }
                case FILE_READ_ERROR: {
                    uploadFile.setStatus(UploadFileStatus.WRONG_FILE_TYPE);
                    break;
                }

                case SUBMITED_FILE_RETURNS_INVALID_FI:
                case SUBMITED_FILE_RETURNS_INVALID_PERIOD:
                case INVALID_FILE_NAME_FI_AND_FILE_CONTENT_FI:
                case SUBMITED_FILE_DUPLICATE_RETURNS: {
                    uploadFile.setStatus(UploadFileStatus.WRONG_FILE_CONTENT);
                    uploadFile.setReason(ex.getMessage());
                    break;
                }

                case TABLE_TYPE_ERROR: {
                    uploadFile.setStatus(UploadFileStatus.WRONG_FILE_CONTENT);
                    uploadFile.setReason(ex.getMessage());
                    break;
                }

                case EXCEL_FILE_BUT_WRONG_EXCEL_EXTENSION: {
                    uploadFile.setStatus(UploadFileStatus.WRONG_FILE_CONTENT);
                    uploadFile.setReason(ex.getMessage());
                    break;
                }
                case UNKNOWN_CONTENT:
                case EMPTY_CONTENT_ERROR: {
                    uploadFile.setStatus(UploadFileStatus.WRONG_FILE_CONTENT);
                    break;
                }

                case SECURITY_INVALID_SIGN:
                case SECURITY_INVALID_ENCRYPT: {
                    uploadFile.setStatus(UploadFileStatus.INVALID_SECURITY);
                    uploadFile.setProtectioninfo(type.getCode());
                    uploadFile.setReason(type.getReplaceableCode());
                    break;
                }
                case INVALID_PASSWORD: {
                    uploadFile.setStatus(UploadFileStatus.INVALID_SECURITY);
                    uploadFile.setProtectioninfo(type.getCode());
                    uploadFile.setReason(ex.getMessage());
                    break;
                }
                case INVALID_XML_STRUCTURE: {
                    uploadFile.setStatus(UploadFileStatus.INVALID_STRUCTURE);
                    break;
                }
                case SUBMITED_FILE_INVALID_VERSION: {
                    uploadFile.setStatus(UploadFileStatus.INVALID_VERSION);
                    uploadFile.setReason(type.getReplaceableCode());
                    if (ex.getParams() != null) {
                        Gson gson = new Gson();
                        MdtReleaseVersion.Version version = gson.fromJson(ex.getParams()[0], MdtReleaseVersion.Version.class);
                        MdtReleaseVersion mdtReleaseVersion = gson.fromJson(ex.getParams()[1], MdtReleaseVersion.class);
                        for (MdtReleaseVersion.Version v : mdtReleaseVersion.getVersions()) {
                            if (v.getT().equalsIgnoreCase(version.getT())) {
                                uploadFile.setReason(type.getReplaceableCode() + " : " + version.getV() + " , required version: " + v.getV());
                            }
                        }
                    }
                    break;
                }
                case INVALID_OST_VERSION: {
                    uploadFile.setStatus(UploadFileStatus.INVALID_OST_VERSION);
                    uploadFile.setReason("OST version: " + ex.getParams()[0] + " was used for file generation, required OST version is: " + ex.getParams()[1]);
                    break;
                }

                case INVALID_DIGITAL_SIGNATURE: {
                    uploadFile.setStatus(UploadFileStatus.INVALID_SECURITY);
                    if (ex.getParams() != null && ex.getParams().length > 0) {
                        uploadFile.setReason(ex.getParams()[0]);
                    }
                    break;
                }
                case CERTIFICATE_REVOKED: {
                    uploadFile.setStatus(UploadFileStatus.INVALID_SECURITY);
                    uploadFile.setReason(type.getReplaceableCode());
                    break;
                }
                default: {
                    uploadFile.setStatus(UploadFileStatus.MATRIX_ERROR);
                    String reason = ex.getMessage();
                    List<String> reasons = ex.getReasonsList();
                    if (reasons != null && (!reasons.isEmpty())) {
                        reason = String.join(", ", reasons);
                    }
                    uploadFile.setReason(reason);
                    break;
                }
            }
        }

    }

    @Override
    public DcsTypeException siftDcsTypeException(Throwable t) {
        while (!(t instanceof DcsTypeException)) {
            t = t.getCause();
            if (t == null) {
                return null;
            }
        }
        return (DcsTypeException) t;
    }

    @Override
    public ImportedReturn loadImportedReturnDetails(int returnId, String fileName) {
        ImportedReturn ir = null;
        try {
            ir = em.createQuery("select ir from IN_IMPORTED_RETURNS ir where ir.id=:returnId", ImportedReturn.class)
                    .setParameter("returnId", returnId).getSingleResult();
            Map<String, String> result = mappingServiceSingleton.getReferenceMap(fileName);
            em.detach(ir);
            if (result != null && ir.getStatus() == ImportStatus.ERRORS && ir.getMessage() != null && !ir.getMessage().isEmpty()) {
                Set<String> mdtCodes = MappingUtil.extractMDTCodes(ir.getMessage(), Pattern.compile("(?<=\\\")([a-zA-Z0-9_\\-\\.]+)(?=\\\")|(?<=\\')([a-zA-Z0-9_\\-\\.]+)(?=\\')|(?<=Code:)([a-zA-Z0-9_\\-\\.\\s]+)(?=\\,)|(?<=\\[)([a-zA-Z0-9_\\-\\.\\s]+)(?=\\])"));
                ir.setMessage(ir.getMessage().replaceAll("[{]", "'{'").replaceAll("[}]", "'}'"));
                String finalMessage = ir.getMessage();
                List<String> mappedCodes = new ArrayList<>();
                int index = 0;
                Map<String, String> variableMap = mappingServiceSingleton.getVariableMap();
                for (String s : mdtCodes) {
                    if (ir.getMessage().contains(s)) {
                        String value = result.get(s);
                        value = value == null ? variableMap.get(s) : value;
                        if (value != null) {
                            mappedCodes.add(value.replaceAll("return |tree.lookup|lookup|[\",;]", ""));
                            finalMessage = finalMessage.replaceAll(s, "{" + (index++) + "}");
                        }
                    }
                }
                ir.setMessage(MessageFormat.format(finalMessage.replaceAll("tree.lookup|lookup|[\",;]", ""), mappedCodes.toArray()));
            }
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
        return ir != null ? ir : new ImportedReturn();
    }

    @Override
    public boolean checkAllFileProcessingDone() {
        User user = current.getCurrentUser();

        if (!user.getFis().isEmpty()) {

            Fi fi = user.getFis().iterator().next();
            Collection<Long> workingFiles = uploadFileQueueStoreLocal.getWorkingFiles().values();
            workingFiles = workingFiles.isEmpty() ? Collections.singletonList(1L) : workingFiles;

            //Load Uploaded,Converted,Working files by statuses
            TypedQuery<Long> query = em.createQuery("select count(uf) from SYS_UPLOADEDFILE uf " +
                    "where uf.user.id=:userId and (uf.status in(:statuses) or uf.id in(:workingFiles))", Long.class);

            query.setParameter("userId", user.getId())
                    .setParameter("workingFiles", workingFiles)
                    .setParameter("statuses", Arrays.asList("0", "1", "24"));


            return query.getSingleResult() == 0;
        }

        return true;
    }

    @Override
    public void updateUploadFile(long fileId, byte[] content) throws FinATypeException {
        UploadFile uploadFile = em.find(UploadFile.class, fileId);
        if (uploadFile.getUser().getId() == current.getCurrentUserId()) {
            uploadFile.setUploadedFile(content);
            fileContentManagementSession.saveUploadFile(uploadFile);
        }
    }

    @Override
    public void setStatFileErrorStatus(long fileId) {
        UploadFile uploadFile = em.find(UploadFile.class, fileId);
        if (uploadFile.getType() == UploadType.STAT_FILE &&
                uploadFile.getUser().getId() == current.getCurrentUserId() &&
                uploadFile.getStatus().equals(String.valueOf(UploadFileStatus.UPLOADED.ordinal()))) {
            uploadFile.setStatus(UploadFileStatus.ERROR);
            uploadFile.setReason("Unexpected Error, Please Contact System Administrator!");
            em.merge(uploadFile);
        }
    }


    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void onFileUploadErrorEvent(@Observes UploadFileErrorEvent uploadFileErrorEvent) {
        em.createQuery("update SYS_UPLOADEDFILE  set status=:status, reason=:reason where id=:id")
                .setParameter("status", Integer.toString(UploadFileStatus.ERROR.ordinal()))
                .setParameter("reason", uploadFileErrorEvent.getReason())
                .setParameter("id", uploadFileErrorEvent.getUploadFileId())
                .executeUpdate();
    }

    @Override
    public UploadFileMetaModel loadUploadFileById(long fileId) {
        StringBuffer buff = new StringBuffer();
        buff.append("select ");
        buff.append(" new ").append(UploadFile.class.getName()).append("(");
        buff.append("uf.id, ");
        buff.append("uf.user.id, ");
        buff.append("uf.user.login, ");
        buff.append("uf.bankCode, ");
        buff.append("uf.fileName, ");
        buff.append("uf.uploadedTime, ");
        buff.append("uf.status, ");
        buff.append("uf.hasUserBank, ");
        buff.append("uf.nameValid, ");
        buff.append("uf.versionValid, ");
        buff.append("uf.matrixValid, ");
        buff.append("uf.type, ");
        buff.append("uf.reason, ");
        buff.append("uf.protectioninfo, ");
        buff.append("uf.processEngine ");
        buff.append(" )");
        buff.append(" from SYS_UPLOADEDFILE as uf ");
        buff.append("WHERE ");
        buff.append("uf.id=:fileId");

        List<String> fiCodes = fiLocal.loadFiCodes();

        //if user has no fi, system loaded empty upload files list.
        if (fiCodes.isEmpty()) {
            return null;
        }

        List<String> userPermissions = authorizationLocal.loadUserPermission(current.getCurrentUserLogin());
        boolean showUndefinedBanks = (permissionLocal.loadPermissionByIdName(PermissionIdNames.DCS_UNDEFINED_BANK) == null || userPermissions.contains(PermissionIdNames.DCS_UNDEFINED_BANK));

        if (userPermissions.contains(PermissionIdNames.FINA_WEB_EXTERNAL_USER)) {
            buff.append(" AND uf.user.id=:userId ");

            if (fiCodes.size() != 1) {
                throw new RuntimeException("External User - " + current.getCurrentUserLogin() + "  has more than one FI ");
            }

            buff.append(" AND ( uf.bankCode=:bankCode ").append(showUndefinedBanks ? " OR uf.bankCode is NULL ) " : ")");

        } else {
            buff.append(" and (").append(DBUtil.get().generateConcatenatedInStatement("uf.bankCode", fiCodes, String.class)).append(showUndefinedBanks ? " OR uf.bankCode is NULL " : "").append(" OR uf.user.id=:userId )");
        }

        Query loadUploadFilesQuery = em.createQuery(buff.toString());


        if (userPermissions.contains(PermissionIdNames.FINA_WEB_EXTERNAL_USER)) {
            loadUploadFilesQuery.setParameter("bankCode", fiCodes.get(0));
        }
        loadUploadFilesQuery.setParameter("userId", current.getCurrentUserId());

        loadUploadFilesQuery.setParameter("fileId", fileId);


        UploadFile uploadFile = new UploadFile();
        try {
            uploadFile = (UploadFile) loadUploadFilesQuery.getSingleResult();
        } catch (PersistenceException ex) {
            log.error("Cannot Find File with id : " + fileId);
            log.error(ex.getMessage(), ex);
        }

        return UploadFileModelHelper.toMetaModel(uploadFile);

    }

    @Override
    public UploadFile loadUploadFile(long fileId) {
        return em.find(UploadFile.class, fileId);
    }

    @Override
    public UploadFileMetaModel checkUploadFileSchedule(UploadFileMetaModel model,
                                                       String fiCode,
                                                       String periodFrom,
                                                       String periodTo,
                                                       List<String> returnCodes,
                                                       String dateFormat) {
        List<String> userFis = fiLocal.loadFiCodes(model.getUserLogin());
        UploadFileMetaModel result = model.clone();
        //Check user fi
        if (!hashUserFi(userFis, fiCode)) {
            result.setStatus(Integer.toString(UploadFileStatus.USER_DOES_NOT_HAVE_FI.ordinal()));
            result.setHasUserBank(false);
        } else {

            //Check file Name unique
            UploadFile uploadFile = new UploadFile();
            uploadFile.setId(model.getId());
            uploadFile.setFileName(model.getFileName());

            if (!checkFileUnique(uploadFile)) {
                log.info("Upload file " + uploadFile.getFileName() + " isn't unique.");
                result.setStatus(Integer.toString(UploadFileStatus.NOT_UNIQUE.ordinal()));
                result.setReason("${" + UploadFileStatus.NOT_UNIQUE.getCode() + "}");
            } else {

                //Check file Schedule
                DateFormat tmpDateFormat = new SimpleDateFormat(dateFormat != null && !dateFormat.trim().isEmpty() ? dateFormat.trim() : "dd/MM/yyyy");

                Date fromDate = parseDate(tmpDateFormat, periodFrom);
                Date toDate = parseDate(tmpDateFormat, periodTo);

                List<String> notFoundReturnCodes = new ArrayList<>();

                List<Long> scheduleIds = new ArrayList<>();

                for (String returnCode : returnCodes) {
                    long scheduleId = -1;
                    try {
                        scheduleId = scheduleLocal.findScheduleIdByPeriod(fromDate, toDate, fiCode, returnCode);
                        scheduleIds.add(scheduleId);
                    } catch (Throwable t) {
                        log.error(t.getMessage(), t);
                    } finally {
                        if (scheduleId <= 0) {
                            notFoundReturnCodes.add(returnCode);
                        }
                    }
                }

                result.setScheduleIds(scheduleIds);

                if (!notFoundReturnCodes.isEmpty()) {
                    result.setReason(MessagesUtil.getString("net.fina.dcs.import.xml.message.scheduleDoesNotExist")
                            + " [ " + MessagesUtil.getString("net.fina.from") + " = "
                            + periodFrom + " , " + MessagesUtil.getString("net.fina.to") + " = "
                            + periodTo
                            + " ]. " + MessagesUtil.getString("net.fina.returns") + ": " + notFoundReturnCodes);
                    result.setStatus(Integer.toString(UploadFileStatus.ERROR.ordinal()));
                } else if (!scheduleIds.isEmpty()) {
                    //check due date
                    String dueDateCheckEnable = propertyLocal.getSystemProperty(PropertyKeys.UPLOAD_FILE_DUE_DATE_CONTROL_ENABLE);
                    if (dueDateCheckEnable != null && !dueDateCheckEnable.isEmpty() && (Integer.parseInt(dueDateCheckEnable) > 0)) {
                        int due = scheduleLocal.getReturnDefinitionsDueDate(returnCodes, fiCode.trim(), fromDate, toDate);
                        int dueHour = scheduleLocal.getReturnDefinitionsDueDateHour(returnCodes, fiCode.trim(), fromDate, toDate);
                        int dueMinute = scheduleLocal.getReturnDefinitionsDueDateMinute(returnCodes, fiCode.trim(), fromDate, toDate);

                        //Resolve schedule doesn't exist problem
                        if (due >= 0) {

                            LocalDateTime currentDateTime = LocalDateTime.now();

                            LocalDateTime toDateLocalTime = toDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                            LocalDate endDate = calendarLocal.getWorkingDay(toDateLocalTime.toLocalDate().plusDays(1), due);

                            LocalDateTime endDateTime = endDate.atStartOfDay();
                            endDateTime = endDateTime.plusHours(toDateLocalTime.getHour());
                            endDateTime = endDateTime.plusMinutes(toDateLocalTime.getMinute());
                            endDateTime = endDateTime.plusSeconds(toDateLocalTime.getSecond());
                            endDateTime = endDateTime.plusHours(dueHour);
                            endDateTime = endDateTime.plusMinutes(dueMinute);

                            boolean dueDateValid = !currentDateTime.isAfter(endDateTime);

                            if (!dueDateValid) {
                                log.info("Upload file " + uploadFile.getFileName() + " invalid due date.");
                                result.setStatus(Integer.toString(UploadFileStatus.REJECTED.ordinal()));
                                result.setReason("${net.fina.dcs.upload.due.date.error}");
                            }
                        }
                    }

                }

            }
        }

        return result;
    }

    @Override
    public void changeStatus(long fileId, UploadFileStatus status, String reason) {
        em.createQuery("update SYS_UPLOADEDFILE u set u.status=:status,u.reason=:reason where u.id=:id")
                .setParameter("id", fileId)
                .setParameter("reason", reason)
                .setParameter("status", String.valueOf(status.ordinal()))
                .executeUpdate();
    }

    private boolean hashUserFi(List<String> userFis, Header header) {
        if (userFis != null) {
            for (String fi : userFis) {
                if (fi != null && header.getBankCode() != null && fi.trim().equals(header.getBankCode().trim())) {
                    return true;
                }
            }
        }
        return false;
    }

    private MatrixOptionBase getSelectedPattern(UploadFile uploadFile, long langId) throws ConverterDcsTypeException {

        MatrixOptionBase option = null;

        List<? extends MatrixOptionBase> options = getMatrixOptions(langId);

        for (MatrixOptionBase o : options) {
            if (o.getPattern() != null) {
                if (uploadFile.getFileName().matches(o.getPattern())) {
                    if (option == null) {
                        option = o;
                    } else {
                        DcsTypeException.Type type = DcsTypeException.Type.MATRIX_DUPLICATED_PATTERN;
                        throw new ConverterDcsTypeException(type, type.getReplaceableCode());
                    }
                }
            }

        }

        if (option == null) {
            uploadFile.setStatus(UploadFileStatus.WRONG_FILE_NAME);
            uploadFile.setReason(UploadFileStatus.WRONG_FILE_NAME.getCode());
        } else {
            uploadFile.setNameValid(true);
        }

        return option;
    }

    private String getMatrixPath() throws ConverterDcsTypeException {
        String matrixPath = propertyLocal.getSystemProperty(PropertyKeys.MATRIX_PATH);
        char separator = (matrixPath.contains("/") ? '/' : '\\');
        if (matrixPath.charAt(matrixPath.length() - 1) != separator) {
            matrixPath += separator;
        }
        return matrixPath;
    }

    private List<ImportedReturn> getImportedReturns(List<Return> returns, UploadFile uploadFile) throws Exception {
        JAXBContext context = JAXBContext.newInstance(ObjectFactory.class.getPackage().getName());
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        List<ImportedReturn> importedReturns = new ArrayList<ImportedReturn>();

        Language lang = null;
        if (returns != null && returns.size() > 0) {
            lang = languageLocal.getLanguageByCode(returns.get(0).getHeader().getLng().trim());

            User curUser = em.find(User.class, uploadFile.getUser().getId());
            for (Return r : returns) {
                Header h = r.getHeader();
                ImportedReturn importedReturn = new ImportedReturn();
                importedReturn.setBankCode(h.getBankCode());

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                marshaller.marshal(r, outputStream);
                importedReturn.setContent(outputStream.toByteArray());

                importedReturn.setImportEnd(new Date());
                importedReturn.setImportStart(new Date());

                importedReturn.setLanguage(lang);
                importedReturn.setMessage("Uploaded");
                String periodEnd = h.getPeriodEnd();
                String periodFrom = h.getPeriodFrom();

                DateFormat format = new SimpleDateFormat(lang.getDateFormat().trim());

                Date periodEndDate = format.parse(periodEnd);
                Date periodStartDate = format.parse(periodFrom);

                //checkTimestamp(periodEndDate);
                importedReturn.setPeriodEnd(periodEndDate);
                importedReturn.setPeriodStart(periodStartDate);
                importedReturn.setReturnCode(h.getReturnCode());
                importedReturn.setStatus(ImportStatus.UPLOADED);
                importedReturn.setUploadTime(new Date());
                importedReturn.setVersionCode(h.getVer());
                importedReturn.setType(getImportedFileType(uploadFile.getType()));
                importedReturn.setUploadFile(uploadFile);
                importedReturn.setUser(curUser);

                importedReturns.add(importedReturn);
            }
        }


        return importedReturns;
    }

    private ImportedFileType getImportedFileType(UploadType uploadType) {

        ImportedFileType importedFileType = ImportedFileType.UNKNOWN;

        switch (uploadType) {
            case MANUAL: {
                importedFileType = ImportedFileType.MANUAL;
                break;
            }
            case DCS: {
                importedFileType = ImportedFileType.DCS;
                break;
            }
            case EMAIL_ROBOT: {
                importedFileType = ImportedFileType.MAIL;
                break;
            }
            case SUBMISSION_TOOL: {
                importedFileType = ImportedFileType.SUBMISSION_TOOL;
                break;
            }
        }

        return importedFileType;

    }

    @Override

    public List<String> getAllPatterns() throws ConverterDcsTypeException {
        List<String> patterns = new ArrayList<String>();
        long langId = ThreadLocalHolder.getLanguage().getId();
        for (MatrixOptionBase opt : getMatrixOptions(langId)) {
            patterns.add(opt.getPattern());
        }

        return patterns;
    }

    @Override
    public Map<String, String> getPeriodTypePatternsForFiType(String fiTypeCode) throws FinATypeException {
        Map<String, String> patterns = new HashMap<>();
        long langId = ThreadLocalHolder.getLanguage().getId();
        //TODO fix for v1
        if (MatrixMappingUtil.getMatrixMappingSource().equals(MatrixMappingSource.EXCEL)) {
            getMatrixOptions(langId).stream().filter(p -> p.getFIType().equalsIgnoreCase(fiTypeCode)).
                    forEach(pattern -> {
                        String returnTypeCode = getReturnTypeCodeFromMatrixName(pattern.getMatrixForEachType());
                        patterns.put(returnTypeCode.toUpperCase() + "&" + pattern.getPeriod().toUpperCase(), pattern.getPattern());
                    });
        }

        return patterns;
    }

    private String getReturnTypeCodeFromMatrixName(String matrixName) {
        String[] splitted = matrixName.split("\\.");
        if (splitted.length > 1) {
            return splitted[1];
        }
        return splitted[0];
    }

    private List<MatrixOptionBase> getExcelMatrixOptions(String matrixPath) throws ConverterDcsTypeException {
        ExcelMatrixReader excelMatrixReader = new ExcelMatrixReader(matrixPath + "Matrix.xls", null);
        return excelMatrixReader.getOptions();
    }

    private List<? extends MatrixOptionBase> getMatrixOptions(long langId) throws ConverterDcsTypeException {
        MatrixMappingSource matrixMappingSource = MatrixMappingUtil.getMatrixMappingSource();
        switch (matrixMappingSource) {
            case EXCEL -> {
                String matrixPath = getMatrixPath();
                log.info("Matrix Path = " + matrixPath);
                return getExcelMatrixOptions(matrixPath);
            }
            case DATABASE -> {
                List<Matrix> data = em.createQuery("select m from SYS_MATRIX m where m.enable=true", Matrix.class).getResultList();
                return data.stream().map(d -> new MatrixOption(d, langId, d.getId())).toList();
            }
            //TODO Validation
//                    throw new DcsTypeException(DcsTypeException.Type.GENERAL_ERROR, "MATRIX_MAPPING_SOURCE property is not set in fina.xml ");

        }
        return getExcelMatrixOptions(getMatrixPath());

    }

    private Header getGeneratedHeader(MatrixOptionBase option, String fileName, Language language) throws DcsTypeException {
        log.info("-------------------------------------------");
        log.info("-----------File Header Generation----------");
        log.info("-------------------------------------------");
        FileAnalyzer fileAnalizer = new FileAnalyzer(option.getPattern(), fileName, option, language);
        Header header = fileAnalizer.getGeneratedHeader();
        log.info("----------Header generation completed successfully--------");
        return header;
    }

    @Override
    public List<UploadFile> loadUploadFiles(List<SortInfo> sortInfos, UploadFile filterConfig, List<String> fiCodes, List<String> userPermissions, boolean showUndefinedBanks, int limit, int offset) {
        List<UploadFile> uploadFiles = new ArrayList<>();

        //if user has no fi, system loaded empty upload files list.
        if (fiCodes.isEmpty()) {
            return uploadFiles;
        }

        String queryString = getUploadFilesQuery(filterConfig, sortInfos, fiCodes, showUndefinedBanks, userPermissions);
        TypedQuery<UploadFile> loadUploadFilesQuery = em.createQuery(queryString, UploadFile.class);

        if (userPermissions.contains(PermissionIdNames.FINA_WEB_EXTERNAL_USER)) {
            loadUploadFilesQuery.setParameter("deleteStatus", Integer.toString(UploadFileStatus.DELETE.ordinal()));
            loadUploadFilesQuery.setParameter("bankCode", fiCodes.get(0).trim());
        }

        if (queryString.contains(":userId")) {
            loadUploadFilesQuery.setParameter("userId", current.getCurrentUserId());
        }

        Map<String, Long> workingFiles = uploadFileQueueStoreLocal.getWorkingFiles();

        if (filterConfig != null) {
            if (filterConfig.getUser() != null && filterConfig.getUser().getId() > 0) {
                loadUploadFilesQuery.setParameter("userId", filterConfig.getUser().getId());
            } else if (filterConfig.getUserIds() != null && !filterConfig.getUserIds().isEmpty()) {
                loadUploadFilesQuery.setParameter("usersIds", filterConfig.getUserIds());
            }

            if (!userPermissions.contains(PermissionIdNames.FINA_WEB_EXTERNAL_USER) && filterConfig.getBankCode() != null) {
                loadUploadFilesQuery.setParameter("bankCode", filterConfig.getBankCode());
            }

            if (filterConfig.getStatus() != null) {
                if (Objects.equals(filterConfig.getStatus(), Integer.toString(UploadFileStatus.WORKING.ordinal()))) {
                    loadUploadFilesQuery.setParameter("workingFiles", workingFiles.isEmpty() ? Collections.singletonList(0L) : workingFiles.values().stream().limit(999).collect(Collectors.toList()));
                }
                loadUploadFilesQuery.setParameter("status", filterConfig.getStatus());
            }

            if (filterConfig.getFromDate() != null) {
                loadUploadFilesQuery.setParameter("fromDate", filterConfig.getFromDate());
            }
            if (filterConfig.getToDate() != null) {
                loadUploadFilesQuery.setParameter("toDate", filterConfig.getToDate());
            }
        }

        if (offset >= 0) {
            loadUploadFilesQuery.setFirstResult(offset);
        }
        if (limit >= 0) {
            loadUploadFilesQuery.setMaxResults(limit);
        }

        long start = System.currentTimeMillis();
        uploadFiles = loadUploadFilesQuery.getResultList();
        long end = System.currentTimeMillis();

        StringBuilder sb = new StringBuilder();
        sb.append("\n")
                .append("================================================================")
                .append("\n")
                .append("Upload File Load Query Took : ").append(end - start).append("ms")
                .append("\n")
                .append("Working File Size : ").append(workingFiles.size())
                .append("\n")
                .append("================================================================");

        log.info(sb.toString());
        //Check working files
        long startTime = System.currentTimeMillis();
        uploadFiles.forEach(uploadFile -> {
            Long workingFileId = workingFiles.get(uploadFile.getFileName());
            if (Objects.equals(uploadFile.getStatus(), String.valueOf(UploadFileStatus.CONVERTED.ordinal())) && workingFileId != null && workingFileId == uploadFile.getId()) {
                uploadFile.setStatus(Integer.toString(UploadFileStatus.WORKING.ordinal()));
            }
        });

        long endTime = System.currentTimeMillis();

        sb = new StringBuilder();
        sb.append("\n")
                .append("================================================================")
                .append("\n")
                .append("Check working files took : ")
                .append(endTime - startTime)
                .append(" ms")
                .append("\n")
                .append("================================================================");
        log.info(sb.toString());

        return uploadFiles;
    }

    @Override
    public long getUploadFilesCount(UploadFile filterConfig, List<String> fiCodes, List<String> userPermissions, boolean showUndefinedBanks) {
        long count = countUploadFiles(filterConfig, fiCodes, userPermissions, showUndefinedBanks);

        return count;
    }

    @Override
    public List<String> loadUploadFileFilterStatuses() {
        List<String> uploadFiles = new ArrayList<>();

        StringBuilder buff = new StringBuilder();
        buff.append("select ");
        buff.append(" distinct (uf.status) ");
        buff.append(" from SYS_UPLOADEDFILE as uf ");
        buff.append(" WHERE ");

        List<String> fiCodes = fiLocal.loadFiCodes();

        //if user has no fi, system loaded empty upload files list.
        if (fiCodes.isEmpty()) {
            return uploadFiles;
        }

        List<String> userPermissions = authorizationLocal.loadUserPermission(current.getCurrentUserLogin());
        boolean showUndefinedBanks = (permissionLocal.loadPermissionByIdName(PermissionIdNames.DCS_UNDEFINED_BANK) == null || userPermissions.contains(PermissionIdNames.DCS_UNDEFINED_BANK));

        if (userPermissions.contains(PermissionIdNames.FINA_WEB_EXTERNAL_USER)) {
            buff.append(" uf.status<>:status AND uf.user.id=:userId ");

            if (fiCodes.size() != 1) {
                throw new RuntimeException("External User - " + current.getCurrentUserLogin() + "  has more than one FI ");
            }

            buff.append(" AND ( uf.bankCode=:bankCode ").append(showUndefinedBanks ? " OR uf.bankCode is NULL ) " : ")");

        } else {
            buff.append("( ").append(DBUtil.get().generateConcatenatedInStatement("uf.bankCode", fiCodes, String.class)).append(showUndefinedBanks ? " OR uf.bankCode is NULL " : "").append(" OR uf.user.id=:userId )");
        }

        TypedQuery<String> loadUploadFilesQuery = em.createQuery(buff.toString(), String.class);

        if (userPermissions.contains(PermissionIdNames.FINA_WEB_EXTERNAL_USER)) {
            loadUploadFilesQuery.setParameter("status", Integer.toString(UploadFileStatus.DELETE.ordinal()));
            loadUploadFilesQuery.setParameter("bankCode", fiCodes.get(0).trim());
        }

        if (buff.toString().contains(":userId")) {
            loadUploadFilesQuery.setParameter("userId", current.getCurrentUserId());
        }

        return loadUploadFilesQuery.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<Integer, Integer> loadDcsEmailStatistic(Date fromDate, Date toDate) {
        Map<Integer, Integer> datas = new HashMap<Integer, Integer>();

        boolean header = true;
        String qString = "select up.type from SYS_UPLOADEDFILE up";
        if (fromDate != null) {
            qString += " where up.uploadedTime >=:fromDate";
            header = false;
        }
        if (toDate != null) {
            if (header) {
                header = false;
                qString += " where up.uploadedTime <=:toDate";
            } else {
                qString += " and  up.uploadedTime <=:toDate";
            }
        }
        Query query = em.createQuery(qString);
        if (fromDate != null) {
            query.setParameter("fromDate", fromDate);
        }
        if (toDate != null) {
            query.setParameter("toDate", toDate);
        }
        List<UploadType> types = query.getResultList();

        Integer emailCounter = 0;
        Integer dscCounter = 0;
        for (UploadType type : types) {
            if (type.ordinal() == 1) {
                dscCounter++;
            } else {
                emailCounter++;
            }
        }
        datas.put(1, dscCounter);
        datas.put(2, emailCounter);

        return datas;
    }

    @SuppressWarnings("unchecked")
    @Override

    public Map<Integer, Integer> loadEmailCounter(Date fromDate, Date toDate) {
        Map<Integer, Integer> datas = new HashMap<Integer, Integer>();

        boolean header = true;
        String qString = "select up.status from SYS_UPLOADEDFILE up";
        if (fromDate != null) {
            qString += " where up.uploadedTime >=:fromDate";
            header = false;
        }
        if (toDate != null) {
            if (header) {
                header = false;
                qString += " where up.uploadedTime <=:toDate";
            } else {
                qString += " and  up.uploadedTime <=:toDate";
            }
        }
        if (fromDate == null && toDate == null) {
            qString += "  where up.type=:emailStatus";
        } else {
            qString += "  and  up.type=:emailStatus";
        }
        Query query = em.createQuery(qString);
        if (fromDate != null) {
            query.setParameter("fromDate", fromDate);
        }
        if (toDate != null) {
            query.setParameter("toDate", toDate);
        }
        query.setParameter("emailStatus", UploadType.EMAIL_ROBOT);
        List<String> types = query.getResultList();

        Integer validAndConverted = 0;
        Integer invalidAtachment = 0;
        Integer error = 0;
        for (String s : types) {
            Integer i = Integer.parseInt(s);
            if (i == UploadFileStatus.IMPORTED.ordinal()) {
                validAndConverted++;
            } else if (i == UploadFileStatus.CONVERTED.ordinal()) {
                invalidAtachment++;
            } else {
                error++;
            }
        }
        datas.put(1, validAndConverted);
        datas.put(2, invalidAtachment);
        datas.put(3, error);

        return datas;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ImportedReturn> loadImportedReturnsByUploadFile(long uploadFileId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ImportedReturn> query = cb.createQuery(ImportedReturn.class);
        Root<ImportedReturn> root = query.from(ImportedReturn.class);
        Join<ImportedReturn, UploadFile> uploadFileJoin = root.join(ImportedReturn_.uploadFile);
        Selection[] selections = new Selection[]{
                root.get(ImportedReturn_.id),
                root.get(ImportedReturn_.returnCode),
                root.get(ImportedReturn_.versionCode),
                root.get(ImportedReturn_.importStart),
                root.get(ImportedReturn_.importEnd),
                root.get(ImportedReturn_.periodStart),
                root.get(ImportedReturn_.periodEnd),
                root.get(ImportedReturn_.message),
                root.get(ImportedReturn_.type),
                root.get(ImportedReturn_.status),
                root.get(ImportedReturn_.bankCode)
        };
        query.select(cb.construct(ImportedReturn.class, selections));
        query.where(cb.equal(uploadFileJoin.get(UploadFile_.id), uploadFileId));
        return em.createQuery(query).getResultList();
    }

    @Override
    public UploadFile loadUploadFileContent(long fileId) {
        User currentUser = current.getCurrentUser();

        List<String> fiCodes = fiLocal.loadFiCodes();
        //add empty string for query in statement execution
        if (fiCodes.isEmpty()) {
            fiCodes.add("~");
        }
        List<String> userPermissions = authorizationLocal.loadUserPermission(currentUser.getLogin());

        boolean internalUser = userPermissions.contains(PermissionIdNames.FINA_WEB_INTERNAL_USER);
        boolean showUndefinedBanks = userPermissions.contains(PermissionIdNames.DCS_UNDEFINED_BANK);

        String fiCodesInStatement = "(" + DBUtil.get().generateConcatenatedInStatement("uf.bankCode", fiCodes, String.class) + ")";

        StringBuilder query = new StringBuilder("select uf.id,uf.fileName,uf.type,uf.status,uf.bankCode, uf.repositoryFileId, uf.repositoryFileVersionId, uf.uploadedTime from SYS_UPLOADEDFILE as uf ");
        if (internalUser && showUndefinedBanks) {
            query.append("where (").append(fiCodesInStatement).append(" or uf.user.id=:currentUserId or uf.bankCode is null) and uf.id=:id");
        } else {
            query.append("where (").append(fiCodesInStatement).append(" or uf.user.id=:currentUserId) and uf.id=:id");
        }


        Query loadFileQuery = em.createQuery(query.toString())
                .setParameter("id", fileId)
                .setParameter("currentUserId", currentUser.getId());

        Object[] objects = (Object[]) loadFileQuery.getSingleResult();

        UploadFile uf = new UploadFile();

        uf.setId((Long) objects[0]);
        uf.setFileName(objects[1].toString());
        uf.setType(UploadType.valueOf(objects[2].toString()));
        uf.setStatus((String) objects[3]);
        uf.setBankCode((String) objects[4]);
        uf.setRepositoryFileId((String) objects[5]);
        uf.setRepositoryFileVersionId((String) objects[6]);
        uf.setUploadedTime((Date) objects[7]);


        return uf;
    }

    @Override
    public Map<Integer, Integer> loadStatisticData(Date fromDate, Date toDate, String fiType) {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("select uf.status from SYS_UPLOADEDFILE uf ");

        prepareUploadFileQuery(fromDate, toDate, fiType, strBuilder);

        Map<Integer, Integer> resultMap = new HashMap<>();
        for (UploadFileStatus status : UploadFileStatus.values()) {
            resultMap.put(status.ordinal(), 0);
        }

        String queryStr = strBuilder.toString();
        TypedQuery<String> query = em.createQuery(queryStr, String.class);
        if (fiType != null) {
            query = query.setParameter("fiTypeCode", fiType);
        }
        if (fromDate != null) {
            query = query.setParameter("fromDate", fromDate);
        }
        if (toDate != null) {
            query = query.setParameter("toDate", toDate);
        }

        List<String> resultList = query.getResultList();
        for (String s : resultList) {
            int ordinal = Integer.parseInt(s.trim());
            int count = resultMap.get(ordinal) + 1;
            resultMap.put(ordinal, count);
        }

        return resultMap;
    }

    @Override
    public List<Object[]> loadUploadFileDateAndStatus(Date fromDate, Date toDate, String fiType, List<UploadType> types) {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("select uf.uploadedTime, uf.status from SYS_UPLOADEDFILE uf ");

        prepareUploadFileQuery(fromDate, toDate, fiType, strBuilder);

        if (types != null && !types.isEmpty()) {
            strBuilder.append(" and (");
            strBuilder.append(
                    types.stream()
                            .map(t -> "uf.type=:type" + t.ordinal())
                            .collect(Collectors.joining(" or ")));
            strBuilder.append(")");
        }


        String queryStr = strBuilder.toString();
        Query query = em.createQuery(queryStr);
        if (fiType != null) {
            query = query.setParameter("fiTypeCode", fiType);
        }
        if (fromDate != null) {
            query = query.setParameter("fromDate", fromDate);
        }
        if (toDate != null) {
            query = query.setParameter("toDate", toDate);
        }
        if (types != null && !types.isEmpty()) {
            for (UploadType type : types) {
                query = query.setParameter("type" + type.ordinal(), type);
            }
        }

        return query.getResultList();
    }

    @Override
    public void deleteUploadFiles(List<Long> filesId) {
        List<String> fiCodes = fiLocal.loadFiCodes();
        //for audit logging
        for (Long id : filesId) {
            UploadFile uf = em.find(UploadFile.class, id);
            if (fiCodes.contains(uf.getBankCode()) || uf.getBankCode() == null || uf.getUser().getId() == current.getCurrentUserId()) {
                uf.setStatus(UploadFileStatus.DELETE);
            }
            em.merge(uf);
        }
    }

    private boolean checkUploadFileDueDate(List<ImportedReturn> importedReturns) {
        String dueDateCheckEnable = propertyLocal.getSystemProperty(PropertyKeys.UPLOAD_FILE_DUE_DATE_CONTROL_ENABLE);
        if (dueDateCheckEnable == null || dueDateCheckEnable.isEmpty() || (Integer.parseInt(dueDateCheckEnable) < 0)) {
            return true;
        }

        List<String> returnCodes = new ArrayList<>();
        for (ImportedReturn importedReturn : importedReturns) {
            returnCodes.add(importedReturn.getReturnCode().trim());
        }
        Iterator<ImportedReturn> returnIterator = importedReturns.iterator();
        if (returnIterator.hasNext()) {
            ImportedReturn importedReturn = returnIterator.next();
            Date fromDate = importedReturn.getPeriodStart();
            Date toDate = importedReturn.getPeriodEnd();
            int due = scheduleLocal.getReturnDefinitionsDueDate(returnCodes, importedReturn.getBankCode().trim(), fromDate, toDate);
            int dueHour = scheduleLocal.getReturnDefinitionsDueDateHour(returnCodes, importedReturn.getBankCode().trim(), fromDate, toDate);
            int dueMinute = scheduleLocal.getReturnDefinitionsDueDateMinute(returnCodes, importedReturn.getBankCode().trim(), fromDate, toDate);

            //Resolve schedule doesn't exist problem
            if (due < 0) {
                return true;
            }

            LocalDateTime currentDateTime = LocalDateTime.now();

            LocalDateTime toDateLocalTime = toDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            LocalDate endDate = calendarLocal.getWorkingDay(toDateLocalTime.toLocalDate().plusDays(1), due);

            LocalDateTime endDateTime = endDate.atStartOfDay();
            endDateTime = endDateTime.plusHours(toDateLocalTime.getHour());
            endDateTime = endDateTime.plusMinutes(toDateLocalTime.getMinute());
            endDateTime = endDateTime.plusSeconds(toDateLocalTime.getSecond());
            endDateTime = endDateTime.plusHours(dueHour);
            endDateTime = endDateTime.plusMinutes(dueMinute);

            return (!currentDateTime.isAfter(endDateTime));
        }

        return false;
    }

    private void prepareUploadFileQuery(Date fromDate, Date toDate, String fiType, StringBuilder strBuilder) {
        if (fiType != null) {
            strBuilder.append("join IN_BANKS b on uf.bankCode = b.code ");
            strBuilder.append("where b.fiType.code =: fiTypeCode ");
        }

        if (fromDate != null || toDate != null) {
            if (fiType == null) {
                strBuilder.append(" where ");
            } else {
                strBuilder.append(" and ");
            }
        }

        if (fromDate != null && toDate != null) {
            strBuilder.append(" uf.uploadedTime between :fromDate and :toDate ");
        } else if (fromDate != null) {
            strBuilder.append("uf.uploadedTime >= :fromDate ");
        } else if (toDate != null) {
            strBuilder.append("uf.uploadedTime <= :toDate ");
        }
    }

    private boolean checkRegFileDueDate(Date toDate) {
        String dateCheckEnableProp = propertyLocal.getSystemProperty(PropertyKeys.UPLOAD_FILE_DATE_CONTROL_ENABLE);
        boolean dateCheckEnable = !(dateCheckEnableProp == null || dateCheckEnableProp.isEmpty() || (Integer.parseInt(dateCheckEnableProp) < 0));
        if (dateCheckEnable) {

            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.setTime(new Date());

            Calendar toCalendar = Calendar.getInstance();
            toCalendar.setTime(toDate);

            return currentCalendar.compareTo(toCalendar) >= 0;
        }

        return true;
    }

    private boolean checkFileDate(List<ImportedReturn> importedReturns) {
        String dateCheckEnable = propertyLocal.getSystemProperty(PropertyKeys.UPLOAD_FILE_DATE_CONTROL_ENABLE);
        if (dateCheckEnable == null || dateCheckEnable.isEmpty() || (Integer.parseInt(dateCheckEnable) < 0)) {
            return true;
        }

        Iterator<ImportedReturn> returnIterator = importedReturns.iterator();
        if (returnIterator.hasNext()) {
            ImportedReturn importedReturn = returnIterator.next();
            Date toDate = importedReturn.getPeriodEnd();

            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.setTime(new Date());

            Calendar toCalendar = Calendar.getInstance();
            toCalendar.setTime(toDate);

            return currentCalendar.compareTo(toCalendar) >= 0;
        }
        return false;
    }

    @Override
    public void checkUploadFileStatus(UploadFile file) {
        List<ImportedReturn> importedReturns = loadImportedReturnsByUploadFile(file.getId());
        for (ImportedReturn importedReturn : importedReturns) {
            if (importedReturn.getStatus() != ImportStatus.IMPORTED) {
                return;
            }
        }
        file.setStatus(UploadFileStatus.IMPORTED);
    }

    /**
     * @return dcs converter zip file one file control
     */
    private int getZipConverterOneFileControlEnable() {
        String property = propertyLocal.getSystemProperty(PropertyKeys.UPLOAD_FILE_ZIP_ONE_FILE_CONTROL);
        if (property != null) {
            try {
                return Integer.parseInt(property);
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
            }
        }
        return 1;
    }

    private boolean checkUserUploadLimit(UploadFile uploadFile) {
        try {
            String limitValue = ConfigurationUtil.get().get("DCS_USER_MAX_FILE_UPLOAD_COUNT");
            String periodValue = ConfigurationUtil.get().get("DCS_USER_MAX_FILE_UPLOAD_COUNT_PERIOD_DAYS");
            if (limitValue != null && periodValue != null) {
                int maxFileCount = Integer.valueOf(limitValue);
                int periodDays = Integer.valueOf(periodValue);
                LocalDate localDate = LocalDate.now().minusDays(periodDays);
                Date toDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

                long count = em.createQuery("select count(uf.id) from SYS_UPLOADEDFILE uf where uf.user=:user and uf.uploadedTime>=:uploadTime and uf.fileName=:fileName", Long.class)
                        .setParameter("user", uploadFile.getUser())
                        .setParameter("uploadTime", toDate)
                        .setParameter("fileName", uploadFile.getFileName())
                        .getSingleResult();
                return count >= maxFileCount;
            }

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        return false;
    }

    private boolean hashUserFi(List<String> userFis, String fiCode) {
        if (userFis != null) {
            for (String fi : userFis) {
                if (fi != null && fiCode != null && fi.trim().equals(fiCode.trim())) {
                    return true;
                }
            }
        }
        return false;
    }

    private Date parseDate(DateFormat dateFormat, String period) {
        try {
            return dateFormat.parse(period);
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }

        return null;
    }

    private List<MatrixMappingOption> getMatrixMappingOptions(MatrixOption option) {
        List<SubMatrix> result = em.createQuery("select sm from SYS_SUB_MATRIX sm where sm.mainMatrix.id=:matrixId", SubMatrix.class)
                .setParameter("matrixId", option.getMatrixId())
                .getResultList();

        return result.stream().map(m -> new MatrixMappingOption(m.getSheetName(), m.getReturnDefinition().getCode(), m.getMatrixTableType(), m.isProtected(), option.getMatrixId(), m.getTables())).toList();
    }


    private String getUploadFilesQuery(UploadFile filterConfig, List<SortInfo> sortInfos, List<String> fiCodes, boolean showUndefinedBanks, List<String> userPermissions) {

        StringBuilder buff = new StringBuilder();
        buff.append("select ");

        buff.append(" new ").append(UploadFile.class.getName()).append("(");

        buff.append("uf.id, ");
        buff.append("uf.user.id, ");
        buff.append("uf.user.login, ");
        buff.append("uf.bankCode, ");
        buff.append("uf.fileName, ");
        buff.append("uf.uploadedTime, ");
        buff.append("uf.status, ");
        buff.append("uf.hasUserBank, ");
        buff.append("uf.nameValid, ");
        buff.append("uf.versionValid, ");
        buff.append("uf.matrixValid, ");
        buff.append("uf.type, ");
        buff.append("uf.reason, ");
        buff.append("uf.protectioninfo, ");
        buff.append("uf.processEngine ");

        buff.append(" )");

        buff.append(" from SYS_UPLOADEDFILE as uf ");
        buff.append(" WHERE ");


        //filter permitted fi codes
        if (filterConfig != null && filterConfig.getBankCodes() != null) {
            filterConfig.getBankCodes().retainAll(fiCodes);
        }


        if (userPermissions.contains(PermissionIdNames.FINA_WEB_EXTERNAL_USER)) {
            buff.append(" uf.status<>:deleteStatus AND uf.user.id=:userId ");

            if (fiCodes.size() != 1) {
                throw new RuntimeException("External User - " + current.getCurrentUserLogin() + "  has more than one FI ");
            }

            buff.append(" AND ( uf.bankCode=:bankCode ").append(showUndefinedBanks ? " OR uf.bankCode is NULL ) " : ")");

        } else {
            if (filterConfig != null && filterConfig.getBankCodes() != null && !filterConfig.getBankCodes().isEmpty()) {
                buff.append("( ").append(DBUtil.get().generateConcatenatedInStatement("uf.bankCode", filterConfig.getBankCodes(), String.class)).append(")");
            } else {
                buff.append("( ").append(DBUtil.get().generateConcatenatedInStatement("uf.bankCode", fiCodes, String.class)).append(showUndefinedBanks ? " OR uf.bankCode is NULL " : "").append(" OR uf.user.id=:userId )");
            }
        }

        // Filter Parameters
        if (filterConfig != null) {
            if (filterConfig.getUser() != null && filterConfig.getUser().getId() > 0) {
                buff.append(" and uf.user.id=:userId ");
            } else if (filterConfig.getUserIds() != null && !filterConfig.getUserIds().isEmpty()) {
                buff.append(" and uf.user.id IN(:usersIds) ");
            }

            if (!userPermissions.contains(PermissionIdNames.FINA_WEB_EXTERNAL_USER)) {
                if (filterConfig.getBankCode() != null) {
                    buff.append(" and uf.bankCode=:bankCode ");
                } else if (filterConfig.getBankCodes() != null && !filterConfig.getBankCodes().isEmpty()) {
                    //filter permitted fi codes
                    filterConfig.getBankCodes().retainAll(fiCodes);
                }
            }

            if (filterConfig.getStatus() != null) {
                if (Objects.equals(filterConfig.getStatus(), Integer.toString(UploadFileStatus.WORKING.ordinal()))) {
                    buff.append(" and (uf.id IN(:workingFiles) or uf.status=:status)");
                } else {
                    buff.append(" and uf.status=:status ");
                }
            }

            if (filterConfig.getFromDate() != null) {
                buff.append(" and (uf.uploadedTime>=:fromDate) ");
            }
            if (filterConfig.getToDate() != null) {
                buff.append(" and (uf.uploadedTime<=:toDate)");
            }
            if (filterConfig.getFileName() != null && !filterConfig.getFileName().trim().isEmpty()) {
                buff.append(" and LOWER(uf.fileName) LIKE '%").append(filterConfig.getFileName().toLowerCase()).append("%' ");
            }
        }

        /**
         * Sort
         */
        if (sortInfos != null) {
            for (int i = 0; i < sortInfos.size(); i++) {
                SortInfo si = sortInfos.get(i);

                if (i == 0) {
                    buff.append(" order by ");
                }

                if (si.getSortField().equals("login")) {
                    buff.append("uf.user.").append(si.getSortField()).append(" ");
                } else {
                    buff.append("uf.").append(si.getSortField()).append(" ");
                }

                buff.append(si.getSortDir());

                if ((i + 1) != sortInfos.size()) {
                    buff.append(", ");
                }

            }
        }

        return buff.toString();
    }

    private long countUploadFiles(UploadFile filterConfig, List<String> fiCodes, List<String> userPermissions, boolean showUndefinedBanks) {

        StringBuilder buff = new StringBuilder();
        buff.append("select ");
        buff.append("count(uf.id) ");
        buff.append(" from SYS_UPLOADEDFILE as uf ");
        buff.append(" WHERE ");


        //if user has no fi, system loaded empty upload files list.
        if (fiCodes.isEmpty()) {
            return 0;
        }

        //filter permitted fi codes
        if (filterConfig != null && filterConfig.getBankCodes() != null) {
            filterConfig.getBankCodes().retainAll(fiCodes);
        }


        if (userPermissions.contains(PermissionIdNames.FINA_WEB_EXTERNAL_USER)) {
            buff.append(" uf.status<>:deleteStatus AND uf.user.id=:userId ");

            if (fiCodes.size() != 1) {
                throw new RuntimeException("External User - " + current.getCallerPrincipal().getName() + "  has more than one FI ");
            }

            buff.append(" AND ( uf.bankCode=:bankCode ").append(showUndefinedBanks ? " OR uf.bankCode is NULL ) " : ")");

        } else {
            if (filterConfig != null && filterConfig.getBankCodes() != null && !filterConfig.getBankCodes().isEmpty()) {
                buff.append("( ").append(DBUtil.get().generateConcatenatedInStatement("uf.bankCode", filterConfig.getBankCodes(), String.class)).append(")");
            } else {
                buff.append("( ").append(DBUtil.get().generateConcatenatedInStatement("uf.bankCode", fiCodes, String.class)).append(showUndefinedBanks ? " OR uf.bankCode is NULL " : "").append(" OR uf.user.id=:userId )");
            }
        }

        // Filter Parameters
        if (filterConfig != null) {
            if (filterConfig.getUser() != null && filterConfig.getUser().getId() > 0) {
                buff.append(" and uf.user.id=:userId ");
            } else if (filterConfig.getUserIds() != null && !filterConfig.getUserIds().isEmpty()) {
                buff.append(" and uf.user.id IN(:usersIds) ");
            }

            if (!userPermissions.contains(PermissionIdNames.FINA_WEB_EXTERNAL_USER)) {
                if (filterConfig.getBankCode() != null) {
                    buff.append(" and uf.bankCode=:bankCode ");
                } else if (filterConfig.getBankCodes() != null && !filterConfig.getBankCodes().isEmpty()) {
                    //filter permitted fi codes
                    filterConfig.getBankCodes().retainAll(fiCodes);
                }
            }

            if (filterConfig.getStatus() != null) {
                if (Objects.equals(filterConfig.getStatus(), Integer.toString(UploadFileStatus.WORKING.ordinal()))) {
                    buff.append(" and (uf.id IN(:workingFiles) or uf.status=:status)");
                } else {
                    buff.append(" and uf.status=:status ");
                }
            }

            if (filterConfig.getFromDate() != null) {
                buff.append(" and (uf.uploadedTime>=:fromDate) ");
            }
            if (filterConfig.getToDate() != null) {
                buff.append(" and (uf.uploadedTime<=:toDate)");
            }
            if (filterConfig.getFileName() != null && !filterConfig.getFileName().trim().isEmpty()) {
                buff.append(" and LOWER(uf.fileName) LIKE '%").append(filterConfig.getFileName().toLowerCase()).append("%' ");
            }
        }


        TypedQuery<Long> loadUploadFilesQuery = em.createQuery(buff.toString(), Long.class);

        if (userPermissions.contains(PermissionIdNames.FINA_WEB_EXTERNAL_USER)) {
            loadUploadFilesQuery.setParameter("deleteStatus", Integer.toString(UploadFileStatus.DELETE.ordinal()));
            loadUploadFilesQuery.setParameter("bankCode", fiCodes.get(0).trim());
        }

        if (buff.toString().contains(":userId")) {
            loadUploadFilesQuery.setParameter("userId", current.getCurrentUserId());
        }

        Map<String, Long> workingFiles = uploadFileQueueStoreLocal.getWorkingFiles();

        if (filterConfig != null) {
            if (filterConfig.getUser() != null && filterConfig.getUser().getId() > 0) {
                loadUploadFilesQuery.setParameter("userId", filterConfig.getUser().getId());
            } else if (filterConfig.getUserIds() != null && !filterConfig.getUserIds().isEmpty()) {
                loadUploadFilesQuery.setParameter("usersIds", filterConfig.getUserIds());
            }

            if (!userPermissions.contains(PermissionIdNames.FINA_WEB_EXTERNAL_USER) && filterConfig.getBankCode() != null) {
                loadUploadFilesQuery.setParameter("bankCode", filterConfig.getBankCode());
            }

            if (filterConfig.getStatus() != null) {
                if (Objects.equals(filterConfig.getStatus(), Integer.toString(UploadFileStatus.WORKING.ordinal()))) {
                    loadUploadFilesQuery.setParameter("workingFiles", workingFiles.isEmpty() ? Collections.singletonList(0L) : workingFiles.values().stream().limit(999).collect(Collectors.toList()));
                }
                loadUploadFilesQuery.setParameter("status", filterConfig.getStatus());
            }

            if (filterConfig.getFromDate() != null) {
                loadUploadFilesQuery.setParameter("fromDate", filterConfig.getFromDate());
            }
            if (filterConfig.getToDate() != null) {
                loadUploadFilesQuery.setParameter("toDate", filterConfig.getToDate());
            }
        }


        return loadUploadFilesQuery.getSingleResult();

    }

}
