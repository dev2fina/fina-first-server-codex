package net.fina.server.reg.impl;

import jakarta.annotation.Resource;
import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionManagement;
import jakarta.ejb.TransactionManagementType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import net.fina.common.client.constants.ImportStatus;
import net.fina.common.client.dcs.UploadFileStatus;
import net.fina.common.client.exception.DcsTypeException;
import net.fina.common.client.property.PropertyKeys;
import net.fina.common.client.returns.ProcessStatus;
import net.fina.common.shared.mdt.ProcessStage;
import net.fina.common.shared.reg.GenerateSourceType;
import net.fina.server.dcs.uploadfile.entity.UploadFile;
import net.fina.server.dcs.uploadfile.impl.reader.excel.MatrixOptionBase;
import net.fina.server.dcs.uploadfile.model.ProcessEngine;
import net.fina.server.processing.model.ComparisonItem;
import net.fina.server.reg.api.*;
import net.fina.server.reg.entity.RegFileSchedule;
import net.fina.server.reg.entity.RegFileStage;
import net.fina.server.reg.model.InputMetaModel;
import net.fina.server.reg.model.InputsMetaModel;
import net.fina.server.reg.model.RegProcessConfig;
import net.fina.server.reg.util.RegQueryGeneratorUtil;
import net.fina.server.reg.util.processor.RegAdvancedFileProcessorUtil;
import net.fina.server.reg.util.processor.RegFileProcessorUtil;
import net.fina.server.reg.util.processor.RegFileProcessorUtilBase;
import net.fina.server.reg.validator.InputValidator;
import net.fina.server.util.DBUtil;
import net.fina.server.util.RegDS;
import net.fina.common.server.StatisticsLogger;
import org.jboss.logging.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
@Local(RegDataProcessorLocal.class)
@TransactionManagement(value = TransactionManagementType.BEAN)
public class RegDataProcessorSession implements RegDataProcessorLocal {

    @Inject
    private Logger log;
    @Inject
    private RegFileProcessorBaseLocal regFileProcessorBaseLocal;
    @Resource(mappedName = "java:jboss/datasources/FinaRegDS")
    private DataSource dataSource;
    @Inject
    @RegDS
    private EntityManager regEm;
    @Inject
    private RegCrossFileValidator regCrossFileValidator;
    @Inject
    private RegFileLocal regFileLocal;

    @Override
    public Map<Long, RegProcessStatus> processData(RegProcessConfig config, UploadFile uploadFile, Map<String, Object> properties, List<Long> fileIds, StatisticsLogger statLog) throws Exception {
        List<String> errors;
        Map<Long, RegProcessStatus> returnProcessStatusMap = new HashMap<>();

        try {

            statLog.logStage("Get database connection");
            try (Connection conn = dataSource.getConnection()) {
                conn.setAutoCommit(false); // 🚀 Transaction control

                config.setUploadFile(uploadFile);
                config.setFileName(uploadFile.getFileName());
                config.setUser(uploadFile.getUser().getLogin());
                config.setProperties(properties);
                config.setInsertQuery(RegQueryGeneratorUtil.generate(GenerateSourceType.INSERT, config.getInputs(), conn).getSource());

                MatrixOptionBase option = (MatrixOptionBase) properties.get("dcs.primary.matrix.option");
                config.setProcessEngine(option.getProcessEngine());
                config.setRegAdvancedFileType(option.getRegAdvancedFileType());

                RegFileProcessorUtilBase regFileProcessorUtil;

                if (config.getProcessEngine().equals(ProcessEngine.REG_ADVANCED)) {

                    if (config.getRegAdvancedFileType() == null) {
                        throw new RuntimeException("Reg Advanced file type is not defined.");
                    }

                    config.setSelectKeysQuery(RegQueryGeneratorUtil.generate(GenerateSourceType.SELECT_KEYS, config.getInputs(), conn, uploadFile.getBankCode()).getSource());

                    // keys are mandatory for REG_ADVANCED processing
                    for (InputsMetaModel inputs : config.getInputs()) {
                        if (config.getSelectKeysQuery().get(inputs.getReturnCode()) == null || config.getSelectKeysQuery().get(inputs.getReturnCode()).isEmpty()) {
                            throw new DcsTypeException("Keys for: " + inputs.getReturnCode() + " are not defined.");
                        }
                    }

                    switch (config.getRegAdvancedFileType()) {
                        case UPDATE:
                            config.setUpdateQuery(RegQueryGeneratorUtil.generate(GenerateSourceType.UPDATE, config.getInputs(), conn, uploadFile.getBankCode()).getSource());
                            break;
                        case DELETE:
                            config.setDeleteAdvancedQuery(RegQueryGeneratorUtil.generate(GenerateSourceType.DELETE_ADVANCED, config.getInputs(), conn, uploadFile.getBankCode()).getSource());
                            break;
                    }
                    initRegAdvancedKeys(config, fileIds);

                    regFileProcessorUtil = new RegAdvancedFileProcessorUtil();
                } else {
                    regFileProcessorUtil = new RegFileProcessorUtil();
                }

                String fileType = uploadFile.getFileName().substring(uploadFile.getFileName().lastIndexOf(".") + 1);
                RegProcessor regProcessor = regFileProcessorBaseLocal.getInstance(fileType);


                if (config.getProcessEngine().equals(ProcessEngine.REG)) {
                    config.setDeleteQuery(RegQueryGeneratorUtil.generate(GenerateSourceType.DELETE, config.getInputs(), conn, config.getDeleteBatchSize()).getSource());
                }

                RegFileStage existingUploadFileStage = regFileLocal.loadByFileId(uploadFile.getId());
                if (existingUploadFileStage != null || properties.getOrDefault("messageRedelivered", false).equals(true)) {
                    log.info("Existing Upload File Stage found, fileId: " + uploadFile.getId());
                    deleteByFileId(config, statLog, conn);
                }

                regFileLocal.createQueueFile(uploadFile.getId());

                statLog.logStage("Start Import Data");

                Date start = new Date();
                if (parallelSheetProcessEnabled(config.getProperties())) {
                    errors = regProcessor.processParallel(config, dataSource, conn, regFileProcessorUtil);
                } else {
                    errors = regProcessor.process(config, conn, regFileProcessorUtil);

                }

                if (!errors.isEmpty()) {
                    //rollback manually delete
                    deleteByFileId(config, statLog, conn);
                    uploadFile.setStatus(String.valueOf(UploadFileStatus.ERROR.ordinal()));
                    uploadFile.setReason(regFileProcessorBaseLocal.getReason(errors.toString()));
                    return returnProcessStatusMap;
                }

                Map<String, List<InputMetaModel>> postProcessComparisonInputs = new HashMap<>();
                for (InputsMetaModel table : config.getInputs()) {
                    table.getInputs().forEach(i -> {
                        List<ComparisonItem> comparisons = i.getComparisons().stream().filter(c -> c.processStage.equals(ProcessStage.POST_PROCESS)).collect(Collectors.toList());
                        if (!comparisons.isEmpty()) {
                            i.setComparisons(comparisons);
                            postProcessComparisonInputs.putIfAbsent(table.getReturnCode(), new ArrayList<>());
                            postProcessComparisonInputs.get(table.getReturnCode()).add(i);
                        }
                    });
                }

                boolean isValid;

                isValid = RegFileProcessorUtilBase.isValid(config.getInputValidatorMap());

                if (!isValid) {
                    statLog.logStage("File Data Validation Failed");
                    statLog.logStage("Rollback transaction");
                    //rollback manually delete inserted data
                    deleteByFileId(config, statLog, conn);

                } else {

                    //check package reject
                    if (config.isPackageRejectEnabled() && !config.getAcceptedDefinitionCodes().isEmpty()) {
                        for (Map.Entry<String, InputValidator> entry : config.getInputValidatorMap().entrySet()) {
                            RegProcessStatus processStatus = getProcessStatus(config.getAcceptedDefinitionCodes(), entry.getKey(), entry.getValue().isValid(), start, new Date());

                            returnProcessStatusMap.put(config.getShceduleMap().get(entry.getKey()), processStatus);
                        }

                        uploadFile.setStatus(String.valueOf(UploadFileStatus.ERROR.ordinal()));

                        return returnProcessStatusMap;

                    }

                    if (config.getProcessEngine().equals(ProcessEngine.REG)) {
                        // if an error list is empty, delete previous records by scheduleId ;
                        boolean preExistingData = false;
                        for (Map.Entry<String, Long> entry : config.getShceduleMap().entrySet()) {

                            List<RegFileSchedule> res = regFileLocal.loadByScheduleId(entry.getValue());
                            if (!res.isEmpty()) {
                                preExistingData = true;
                                break;
                            }
                        }
                        if (preExistingData) {
                            deleteOldData(config, statLog, conn);
                        }
                    }

                    //Post process validation
                    statLog.logStage("Reg Validate Cross File Comparisons ");
                    regCrossFileValidator.validatePostProcessComparisons(config, postProcessComparisonInputs);

                    // validate unique columns
                    statLog.logStage("Reg Validate unique column data ");
                    regCrossFileValidator.validateUniqueColumnData(config);
                    isValid = RegFileProcessorUtilBase.isValid(config.getInputValidatorMap());
                    if (!isValid) {
                        statLog.logStage("Cross File Validation Failed");
                    }

                }


                Date processEnd = new Date();
                for (Map.Entry<String, InputValidator> entry : config.getInputValidatorMap().entrySet()) {
                    RegProcessStatus processStatus = getProcessStatus(config.getAcceptedDefinitionCodes(), entry.getKey(), entry.getValue().isValid(), start, processEnd);
                    isValid &= processStatus.getProcessStatus().equals(ProcessStatus.STATUS_PROCESSED);
                    returnProcessStatusMap.put(config.getShceduleMap().get(entry.getKey()), processStatus);
                }

                uploadFile.setStatus(isValid ? String.valueOf(UploadFileStatus.IMPORTED.ordinal()) : String.valueOf(UploadFileStatus.ERROR.ordinal()));

            }
        } catch (Exception t) {
            log.error(t.getMessage(), t);
            throw t;
        }

        return returnProcessStatusMap;
    }

    private RegProcessStatus getProcessStatus(List<String> acceptedDefinitionCodes, String returnCode, boolean isValid, Date start, Date end) {
        ProcessStatus processStatus = ProcessStatus.STATUS_PROCESSED;
        ImportStatus importStatus = ImportStatus.IMPORTED;
        if (!isValid) {
            processStatus = ProcessStatus.STATUS_ERRORS;
            importStatus = ImportStatus.ERRORS;
            return new RegProcessStatus(processStatus, importStatus, start, end);
        }

        if (acceptedDefinitionCodes.contains(returnCode)) {
            processStatus = ProcessStatus.STATUS_ACCEPTED;
            importStatus = ImportStatus.DECLINED;
            return new RegProcessStatus(processStatus, importStatus, start, end);
        }

        return new RegProcessStatus(processStatus, importStatus, start, end);
    }

    private void initRegAdvancedKeys(RegProcessConfig config, List<Long> fileIds) {
        Map<String, List<String>> resultKeys = new HashMap<>();
        Map<String, List<String>> resultKeyData = new HashMap<>();
        for (InputsMetaModel inputs : config.getInputs()) {
            List<String> keys = resultKeys.get(inputs.getReturnCode()) != null ? resultKeys.get(inputs.getReturnCode()) : new ArrayList<>();
            for (InputMetaModel inputMetaModel : inputs.getInputs()) {
                if (inputMetaModel.isKey()) {
                    keys.add(inputMetaModel.getCode());
                }
            }
            resultKeys.put(inputs.getReturnCode(), keys);

            if (resultKeyData.get(inputs.getReturnCode()) == null) {
                String selectKeysQuery = config.getSelectKeysQuery().get(inputs.getReturnCode());
                selectKeysQuery += DBUtil.get().generateConcatenatedInStatement("", fileIds, Long.class);
                if (!selectKeysQuery.isBlank()) {
                    List<String> keyData = regEm.createNativeQuery(selectKeysQuery).getResultList();
                    resultKeyData.put(inputs.getReturnCode(), keyData);
                }
            }
        }

        config.setRegAdvancedKeys(resultKeys);
        config.setRegAdvancedKeyData(resultKeyData);
    }

    private void deleteOldData(RegProcessConfig config, StatisticsLogger statLog, Connection connection) throws Exception {
        statLog.logMessage("Delete Existing Data");
        try {

            if (!config.getShceduleMap().isEmpty()) {
                for (InputsMetaModel inputs : config.getInputs()) {
                    //do not delete accepted return data
                    if (config.getAcceptedDefinitionCodes().contains(inputs.getReturnCode()) || (config.isPackageRejectEnabled() && !config.getAcceptedDefinitionCodes().isEmpty())) {
                        continue;
                    }

                    String deleteQuery = config.getDeleteQuery().get(inputs.getReturnCode());

                    log.info("DELETE START: " + deleteQuery + "; fileId: " + config.getUploadFile().getId() + " ; scheduleId: " + config.getShceduleMap().get(inputs.getReturnCode()));

                    statLog.logStage("Delete old data : " + inputs.getTableName());

                    try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
                        int totalDeleted = 0;
                        int deletedRows;

                        deleteStatement.setLong(1, config.getShceduleMap().get(inputs.getReturnCode()));
                        deleteStatement.setLong(2, config.getUploadFile().getId());
                        do {

                            deletedRows = executeWithRetry(deleteStatement);
                            connection.commit();
                            totalDeleted += deletedRows;
                            log.info("Deleted " + totalDeleted + " rows...");
                        } while (deletedRows > 0); // Loop until no more rows are deleted

                        log.info("DELETE FINISH, fileContentToDeleteId: " + config.getUploadFile().getId() + ", Total Deleted: " + totalDeleted);

                    }
                }
            }
        } catch (Exception e) {
            log.error("Delete Data Filed   fileId : " + config.getUploadFile().getId() + " , fileName : " + config.getUploadFile().getFileName(), e);
            throw new RegFileProcessException(e.getMessage());
        }
    }

    private void deleteByFileId(RegProcessConfig config, StatisticsLogger statLog, Connection connection) throws Exception {
        statLog.logMessage("Delete File Data FileId : " + config.getUploadFile().getId());
        try {

            if (!config.getShceduleMap().isEmpty()) {
                for (InputsMetaModel inputs : config.getInputs()) {
                    String deleteQuery = RegQueryGeneratorUtil.generate(GenerateSourceType.DELETE_BY_FILE_ID, config.getInputs(), connection, config.getDeleteBatchSize()).getSource().get(inputs.getReturnCode());

                    log.info("DELETE START: " + deleteQuery + "; fileId: " + config.getUploadFile().getId());

                    statLog.logStage("Delete file data : " + inputs.getTableName());

                    try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
                        int totalDeleted = 0;
                        int deletedRows;

                        deleteStatement.setLong(1, config.getUploadFile().getId());
                        do {

                            deletedRows = executeWithRetry(deleteStatement);
                            connection.commit();
                            totalDeleted += deletedRows;
                            log.info("Deleted " + totalDeleted + " rows...");
                        } while (deletedRows > 0);

                        log.info("DELETE FINISH, fileId: " + config.getUploadFile().getId() + ", Total Deleted: " + totalDeleted);

                    }
                }
            }
        } catch (Exception e) {
            log.error("Delete Data Filed   fileId : " + config.getUploadFile().getId() + " , fileName : " + config.getUploadFile().getFileName(), e);
            throw new RegFileProcessException(e.getMessage());
        }

    }

    int executeWithRetry(PreparedStatement ps) throws SQLException, InterruptedException {
        int attempts = 0, max = 10;
        long backoff = 2000;
        while (true) {
            try {
                return ps.executeUpdate();
            } catch (SQLException e) {
                log.error("Exception While Deleting data : attempt " + attempts + ", Max Attempts: " + max);
                log.error(e.getMessage(), e);
                if (++attempts > max) throw e;
                Thread.sleep(backoff);
            }
        }
    }


    private boolean parallelSheetProcessEnabled(Map<String, Object> properties) {
        try {
            Object enabled = properties.get(PropertyKeys.REG_SHEET_PARALLEL_PROCESS_ENABLE);
            if (enabled != null && !enabled.toString().isBlank()) {
                return Integer.parseInt((String) enabled) > 0;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return false;
    }
}
