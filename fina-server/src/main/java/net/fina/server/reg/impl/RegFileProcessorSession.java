package net.fina.server.reg.impl;

import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import net.fina.common.client.dcs.DocumentType;
import net.fina.common.client.dcs.UploadFileStatus;
import net.fina.common.client.exception.ConverterDcsTypeException;
import net.fina.common.client.exception.DcsTypeException;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.property.PropertyKeys;
import net.fina.common.server.util.ConfigurationUtil;
import net.fina.security.api.AuthorizationLocal;
import net.fina.server.dcs.uploadfile.api.UploadFileLocal;
import net.fina.server.dcs.uploadfile.entity.UploadFile;
import net.fina.server.dcs.uploadfile.impl.converter.ConverterUtil;
import net.fina.server.dcs.uploadfile.impl.reader.excel.*;
import net.fina.server.dcs.uploadfile.impl.reader.excel.util.MatrixMappingUtil;
import net.fina.server.dcs.uploadfile.impl.util.FileAnalyzer;
import net.fina.server.dcs.uploadfile.model.UploadFileMetaModel;
import net.fina.server.dcs.uploadfile.model.helper.UploadFileModelHelper;
import net.fina.server.fi.api.FiLocal;
import net.fina.server.fsop.entity.UploadFileQueue;
import net.fina.server.fsop.event.RemoveUploadFileQueueEvent;
import net.fina.server.i18n.api.LanguageLocal;
import net.fina.server.i18n.entity.Language;
import net.fina.server.matrix.entity.Matrix;
import net.fina.server.matrix.entity.SubMatrix;
import net.fina.server.matrix.entity.SubMatrixTable;
import net.fina.server.reg.api.RegDataProcessorLocal;
import net.fina.server.reg.api.RegFileLocal;
import net.fina.server.reg.api.RegFileProcessorBaseLocal;
import net.fina.server.reg.api.RegFileProcessorLocal;
import net.fina.server.reg.model.RegProcessConfig;
import net.fina.server.reg.util.processor.RegFileProcessorUtilBase;
import net.fina.server.reg.validator.InputValidator;
import net.fina.server.reg.validator.ValidationErrorType;
import net.fina.server.returns.api.ReturnDefinitionLocal;
import net.fina.server.returns.api.ReturnLocal;
import net.fina.server.returns.api.ReturnVersionLocal;
import net.fina.server.returns.xml.Header;
import net.fina.server.security.api.PropertyLocal;
import net.fina.server.st.crypto.CertificateMode;
import net.fina.server.st.crypto.SecurityManagerUtil;
import net.fina.server.util.FinaDS;
import net.fina.common.server.StatisticsLogger;
import org.jboss.logging.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
@Local(RegFileProcessorLocal.class)
public class RegFileProcessorSession implements RegFileProcessorLocal {

    @Inject
    private Logger log;
    @Inject
    private LanguageLocal languageLocal;
    @Inject
    private UploadFileLocal uploadFileLocal;
    @Inject
    private ReturnLocal returnLocal;
    @Inject
    private RegFileLocal regFileLocal;
    @Inject
    private RegFileProcessorBaseLocal regFileProcessorBaseLocal;
    @Inject
    @FinaDS
    private EntityManager finaEm;
    @Inject
    private PropertyLocal propertyLocal;
    @Inject
    private RegDataProcessorLocal regDataProcessorLocal;
    @Inject
    private FiLocal fiLocal;
    @Inject
    private ReturnDefinitionLocal returnDefinitionLocal;
    @Inject
    private AuthorizationLocal authorizationLocal;
    @Inject
    private Event<RemoveUploadFileQueueEvent> removeUploadFileQueueEvent;
    @Inject
    private ReturnVersionLocal returnVersionLocal;

    @Override
    public void convertAndProcessRegFile(UploadFileQueue uploadFileQueue, boolean jMSRedelivered) throws Exception {
        UploadFile uploadFile = null;
        try {
            uploadFile = uploadFileLocal.loadUploadFile(uploadFileQueue.getFileId());
            finaEm.detach(uploadFile);

            // Generate Properties
            Map<String, Object> properties = new HashMap<>();
            properties.put("messageRedelivered", jMSRedelivered);
            MatrixOptionBase option = getSelectedPattern(uploadFile);
            Language language = languageLocal.getLanguageByCodeOrDefault("en_US");
            properties.put("dcs.language", language);
            Header header = getGeneratedHeader(option, uploadFile.getFileName(), language);

            FileAnalyzer analyzer = new FileAnalyzer(option.getPattern(), uploadFile.getFileName(), option, language);
            String extension = analyzer.getExtension().toLowerCase();
            properties.put("dcs.file.extension", extension);
            properties.put("mdtReleaseVersion", propertyLocal.getSystemProperty(PropertyKeys.MDT_RELEASE_VERSION));

            DocumentType documentType = ConverterUtil.detectDocumentType(extension);

            properties.put("documentType", documentType);
            log.info("Convert file type - " + documentType);
            MatrixMappingSource matrixMappingSource = MatrixMappingUtil.getMatrixMappingSource();

            switch (documentType) {
                case FINA:
                case XML:
                case EXCEL: {
                    properties.put("dcs.fi", fiLocal.findFiByCode(header.getBankCode()));
                    properties.put("dcs.primary.matrix.option", option);

                    if (matrixMappingSource.equals(MatrixMappingSource.EXCEL)) {
                        String selectedMatrix = option.getMatrixForEachType();
                        log.info("Selected Matrix - " + selectedMatrix);
                        String matrixPath = getMatrixPath();
                        properties.put("dcs.main.matrix", matrixPath + "Matrix.xls");
                        properties.put("dcs.primary.matrix", matrixPath + selectedMatrix);
                    }
                    properties.put("net.fina.matrix.mapping.options.data", getMatrixMappingOptions(matrixMappingSource, option, properties));
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
            CertificateMode certificateMode = SecurityManagerUtil.getCertificateMode();

            if ((keyStoreRepositoryPath != null) && (!keyStoreRepositoryPath.isEmpty()) && certificateMode.equals(CertificateMode.LEGACY)) {
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

            properties.put(PropertyKeys.REG_SHEET_PARALLEL_PROCESS_ENABLE, propertyLocal.getSystemProperty(PropertyKeys.REG_SHEET_PARALLEL_PROCESS_ENABLE));

            convertAndProcess(uploadFile, properties, header, language.getCode(), option);

        } finally {
            log.info("Remove upload file queue");
            RemoveUploadFileQueueEvent uploadFileQueueEvent = new RemoveUploadFileQueueEvent(uploadFileQueue);
            this.removeUploadFileQueueEvent.fire(uploadFileQueueEvent);
        }

    }

    private void convertAndProcess(UploadFile uploadFile, Map<String, Object> properties, Header header, String languageCode, MatrixOptionBase optionBase) throws Exception {
        StatisticsLogger statLog = new StatisticsLogger("REG File Process");
        RegProcessConfig config = new RegProcessConfig();
        config.setReturnVersionCode(header.getVer());
        config.setReturnVersionId(returnVersionLocal.findByCode(header.getVer().trim()).getId());
        config.setReturnVersionCode(header.getVer());
        config.setEncryptEnabled(optionBase.isEncryptEnabled());
        config.setPackageRejectEnabled(packageRejectEnable());
        config.setInsertBatchSize(getBatchSize(PropertyKeys.REG_INSERT_BATCH_SIZE));
        config.setDeleteBatchSize(getBatchSize(PropertyKeys.REG_DELETE_BATCH_SIZE));

        Map<Long, RegProcessStatus> returnProcessStatusMap = new HashMap<>();
        List<Long> acceptedScheduleIds = new ArrayList<>();
        Language language = languageLocal.getLanguageByCode(languageCode);
        UploadFileMetaModel model = null;
        try {
            config.setDateFormat(language.getDateFormat());
            config.setDateTimeFormat(language.getDateTimeFormat());
            config.setNumberFormat(language.getNumberFormat());

            List<MatrixMappingOptionAdapter> optionAdapterList = (List<MatrixMappingOptionAdapter>) properties.get("net.fina.matrix.mapping.options.data");

            List<String> definitionCodes = (List) properties.get("dcs.returnDefCodesList");

            definitionCodes = regFileProcessorBaseLocal.checkDefinitionCode(optionAdapterList, definitionCodes);

            statLog.logStage("check Upload File Schedules");
            model = uploadFileLocal.checkUploadFileSchedule(UploadFileModelHelper.toMetaModel(uploadFile), header.getBankCode(), header.getPeriodFrom(), header.getPeriodEnd(), definitionCodes, language.getDateFormat());

            if (!model.getStatus().equals(String.valueOf(UploadFileStatus.UPLOADED.ordinal())) && !model.getStatus().equals(String.valueOf(UploadFileStatus.WORKING.ordinal()))) {
                uploadFile.setStatus(model.getStatus());
                uploadFile.setReason(regFileProcessorBaseLocal.getReason(model.getReason()));
                return;
            }

            model.setStatus(String.valueOf(UploadFileStatus.WORKING.ordinal()));

            acceptedScheduleIds = returnLocal.getAcceptedSchedules(model.getScheduleIds());
            List<String> inputDefinitionCodes = new ArrayList<>();

            for (int i = 0; i < definitionCodes.size(); i++) {
                long scheduleId = model.getScheduleIds().get(i);
                String definitionCode = definitionCodes.get(i);
                config.getShceduleMap().put(definitionCode, scheduleId);
                inputDefinitionCodes.add(definitionCode);

                if (acceptedScheduleIds.contains(scheduleId)) {
                    config.getAcceptedDefinitionCodes().add(definitionCode);
                }
            }

            config.setInputs(regFileProcessorBaseLocal.prepareInputs(optionAdapterList, inputDefinitionCodes, language.getId()));

            List<Long> fileIds = finaEm.createQuery("SELECT id from SYS_UPLOADEDFILE where bankCode = :bankCode", Long.class)
                    .setParameter("bankCode", uploadFile.getBankCode())
                    .getResultList();
            returnProcessStatusMap = regDataProcessorLocal.processData(config, uploadFile, properties, fileIds, statLog);


        } catch (FinATypeException ex) {
            uploadFileLocal.manageDcsTypeException(uploadFile, new DcsTypeException(ex.getMessage()));
        } catch (DcsTypeException ex) {
            log.error(ex.getMessage(), ex);
            uploadFileLocal.manageDcsTypeException(uploadFile, ex);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            uploadFile.setStatus(UploadFileStatus.ERROR);
            uploadFile.setStatus(String.valueOf(UploadFileStatus.ERROR.ordinal()));
            uploadFile.setReason("${net.fina.processing.generalError}");

            if (RegFileProcessorUtilBase.isValid(config.getInputValidatorMap()) && !config.getInputValidatorMap().isEmpty()) {
                config.getInputValidatorMap().values().stream().findFirst().get().addError(ValidationErrorType.OTHER, InputValidator.GENERAL_ERROR);
            }
            uploadFileLocal.updateUploadFile(UploadFileModelHelper.toMetaModel(uploadFile));

            throw ex;
        } finally {
            try {

                regFileLocal.createRegFileSchedulesRelation(uploadFile.getId(), model != null ? model.getScheduleIds() : new ArrayList<>());
                if (!returnProcessStatusMap.isEmpty()) {
                    statLog.logStage("Create REG Returns");
                    returnLocal.createReturns(returnProcessStatusMap, config, header.getVer(), uploadFile.getId(), language, header.getBankCode());
                }

                statLog.close();
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
                uploadFile.setStatus(String.valueOf(UploadFileStatus.ERROR.ordinal()));
                uploadFile.setReason("${net.fina.processing.generalError}");
            }
            statLog.logStage("Update Upload File Status");
            uploadFileLocal.updateUploadFile(UploadFileModelHelper.toMetaModel(uploadFile));
            statLog.close();
        }
        //remove from queue
        regFileLocal.deleteQueueFile(uploadFile.getId());

    }


    private List<? extends MatrixOptionBase> getMatrixOptions() throws ConverterDcsTypeException {
        MatrixMappingSource matrixMappingSource = MatrixMappingUtil.getMatrixMappingSource();
        switch (matrixMappingSource) {
            case EXCEL -> {
                String matrixPath = getMatrixPath();
                log.info("Matrix Path = " + matrixPath);
                return getExcelMatrixOptions(matrixPath);
            }
            case DATABASE -> {
                List<Matrix> data = finaEm.createQuery("select m from SYS_MATRIX m where m.enable=true", Matrix.class).getResultList();
                return data.stream().map(d -> new MatrixOption(d, 1, d.getId())).toList();
            }
        }

        return getExcelMatrixOptions(getMatrixPath());

    }

    private List<MatrixOptionBase> getExcelMatrixOptions(String matrixPath) throws ConverterDcsTypeException {
        ExcelMatrixReader excelMatrixReader = new ExcelMatrixReader(matrixPath + "Matrix.xls", null);
        return excelMatrixReader.getOptions();
    }

    private MatrixOptionBase getSelectedPattern(UploadFile uploadFile) throws ConverterDcsTypeException {

        MatrixOptionBase option = null;

        List<? extends MatrixOptionBase> options = getMatrixOptions();

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


    private Header getGeneratedHeader(MatrixOptionBase option, String fileName, Language language) throws DcsTypeException {
        log.info("-------------------------------------------");
        log.info("-----------File Header Generation----------");
        log.info("-------------------------------------------");
        FileAnalyzer fileAnalizer = new FileAnalyzer(option.getPattern(), fileName, option, language);
        Header header = fileAnalizer.getGeneratedHeader();
        log.info("----------Header generation completed successfully--------");
        return header;
    }

    private List<MatrixMappingOptionAdapter> getMatrixMappingOptions(MatrixMappingSource source, MatrixOptionBase option, Map<String, Object> properties) throws Exception {
        if (source.equals(MatrixMappingSource.DATABASE)) {
            List<MatrixMappingOptionAdapter> adapterList = new ArrayList<>();
            List<SubMatrix> result = finaEm.createQuery("select sm from SYS_SUB_MATRIX sm where sm.mainMatrix.id=:matrixId", SubMatrix.class).setParameter("matrixId", ((MatrixOption) option).getMatrixId()).getResultList();

            for (SubMatrix sm : result) {
                MatrixMappingOptionAdapter adapter = new MatrixMappingOptionAdapter();
                adapter.setReturnCode(sm.getReturnDefinition().getCode());
                adapter.setSheetName(sm.getSheetName());
                if (!sm.getTables().isEmpty()) {
                    SubMatrixTable regTable = sm.getTables().get(0);
                    adapter.setStartRow(regTable.getStartRow());
                    Map<String, String> codeCellRefMap = new HashMap<>();
                    Map<String, Integer> columnPrecisionMap = new HashMap<>();
                    regTable.getTableMappings().forEach(tm -> {
                        codeCellRefMap.put(tm.getMdtNode().getCode(), tm.getCell().trim());
                        if (tm.getPrecision() != null) {
                            columnPrecisionMap.put(tm.getCell().trim(), tm.getPrecision());
                        }
                    });
                    adapter.setMdtCodeCellReferenceMap(codeCellRefMap);
                    adapter.setColumnPrecisionMap(columnPrecisionMap);
                }
                adapterList.add(adapter);
            }
            return adapterList;

        }

        String PRIMARY = (String) properties.get("dcs.primary.matrix");

        ExcelMappingReader pmr = new ExcelMappingReader(PRIMARY, properties);
        List<ExcelMappingReader.Option> primaryOptions = pmr.getOptions();

        return primaryOptions.stream().map(o -> new MatrixMappingOptionAdapter(o.getSheetName(), o.getReturnCode(), o.getStartRow(), o.getMdtCodeCellReferenceMap(), o.getColumnPrecisionMap())).toList();
    }

    private boolean packageRejectEnable() {
        String enable = propertyLocal.getSystemProperty(PropertyKeys.PROCESSING_PACKAGE_REJECT_ENABLE);
        return enable != null && (!enable.isEmpty()) && (Integer.parseInt(enable) > 0);
    }

    private int getBatchSize(String batchKey) {
        try {
            String batchPropValue = propertyLocal.getSystemProperty(batchKey);
            if (batchPropValue != null) {
                return Integer.parseInt(batchPropValue);
            }
        } catch (Exception ignore) {
        }

        return 50_000;
    }

}
