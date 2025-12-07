package net.fina.server.reg.model;

import net.fina.server.dcs.uploadfile.entity.UploadFile;
import net.fina.server.dcs.uploadfile.model.ProcessEngine;
import net.fina.server.dcs.uploadfile.model.RegAdvancedFileType;
import net.fina.server.reg.validator.InputValidator;

import java.util.*;

public class RegProcessConfig {
    private final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy";
    private final String DEFAULT_DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm";
    private final String DEFAULT_NUMBER_FORMAT = "#,##0.0000";
    private Map<String, Long> scheduleMap = new HashMap<>();
    private String fileName;
    private UploadFile uploadFile;
    private String user;
    private List<InputsMetaModel> inputs;
    private Map<String, Object> properties;
    private Map<String, String> insertQuery;
    private Map<String, String> deleteQuery;
    private Map<String, String> deleteAdvancedQuery;
    private Map<String, String> updateQuery;
    private Map<String, String> selectKeysQuery;
    private int maxEmptyRowNumber = 10;
    private Map<String, InputValidator> inputValidatorMap = new HashMap<>();
    private String dateFormat;
    private String dateTimeFormat;
    private String numberFormat;
    private ProcessEngine processEngine;
    private RegAdvancedFileType regAdvancedFileType;
    private Map<String, List<String>> regAdvancedKeys;
    private Map<String, List<String>> regAdvancedKeyData;
    private String returnVersionCode;
    private long returnVersionId;
    private boolean encryptEnabled;
    private int insertBatchSize = 50_000;
    private int deleteBatchSize = 50_000;
    private boolean packageRejectEnabled;
    private List<String> acceptedDefinitionCodes = new ArrayList<>();

    public RegProcessConfig() {
    }


    public RegProcessConfig(Map<String, Long> definitionScheduleMap) {
        this.scheduleMap = definitionScheduleMap;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public UploadFile getUploadFile() {
        return uploadFile;
    }

    public void setUploadFile(UploadFile uploadFile) {
        this.uploadFile = uploadFile;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public List<InputsMetaModel> getInputs() {
        return inputs;
    }

    public void setInputs(List<InputsMetaModel> inputs) {
        this.inputs = inputs;
    }

    public Map<String, String> getInsertQuery() {
        return insertQuery;
    }

    public void setInsertQuery(Map<String, String> insertQuery) {
        this.insertQuery = insertQuery;
    }

    public Map<String, String> getDeleteQuery() {
        return deleteQuery;
    }

    public void setDeleteQuery(Map<String, String> deleteQuery) {
        this.deleteQuery = deleteQuery;
    }

    public Map<String, String> getDeleteAdvancedQuery() {
        return deleteAdvancedQuery;
    }

    public void setDeleteAdvancedQuery(Map<String, String> deleteAdvancedQuery) {
        this.deleteAdvancedQuery = deleteAdvancedQuery;
    }

    public Map<String, String> getUpdateQuery() {
        return updateQuery;
    }

    public void setUpdateQuery(Map<String, String> updateQuery) {
        this.updateQuery = updateQuery;
    }

    public Map<String, String> getSelectKeysQuery() {
        return selectKeysQuery;
    }

    public void setSelectKeysQuery(Map<String, String> selectKeysQuery) {
        this.selectKeysQuery = selectKeysQuery;
    }

    public String getActionQuery(String returnCode) {
        if (!ProcessEngine.REG_ADVANCED.equals(processEngine)) {
            return insertQuery.get(returnCode);
        }

        switch (regAdvancedFileType) {
            case CREATE:
                return insertQuery.get(returnCode);
            case UPDATE:
                return updateQuery.get(returnCode);
            case DELETE:
                return deleteAdvancedQuery.get(returnCode);
        }

        throw new RuntimeException("Action query is not available.");
    }

    public int getMaxEmptyRowNumber() {
        return maxEmptyRowNumber;
    }

    public void setMaxEmptyRowNumber(int maxEmptyRowNumber) {
        this.maxEmptyRowNumber = maxEmptyRowNumber;
    }

    public Map<String, InputValidator> getInputValidatorMap() {
        return inputValidatorMap;
    }

    public void setInputValidatorMap(Map<String, InputValidator> inputValidatorMap) {
        this.inputValidatorMap = inputValidatorMap;
    }

    public Map<String, Long> getShceduleMap() {
        return scheduleMap;
    }


    public String getDateFormat() {
        return dateFormat == null ? DEFAULT_DATE_FORMAT : dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getDateTimeFormat() {
        return dateTimeFormat == null ? DEFAULT_DATE_TIME_FORMAT : dateTimeFormat;
    }

    public void setDateTimeFormat(String dateTimeFormat) {
        this.dateTimeFormat = dateTimeFormat;
    }

    public String getNumberFormat() {
        return numberFormat == null ? DEFAULT_NUMBER_FORMAT : numberFormat;
    }

    public void setNumberFormat(String numberFormat) {
        this.numberFormat = numberFormat;
    }

    public ProcessEngine getProcessEngine() {
        return processEngine;
    }

    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    public RegAdvancedFileType getRegAdvancedFileType() {
        return regAdvancedFileType;
    }

    public void setRegAdvancedFileType(RegAdvancedFileType regAdvancedFileType) {
        this.regAdvancedFileType = regAdvancedFileType;
    }

    public Map<String, List<String>> getRegAdvancedKeys() {
        return regAdvancedKeys;
    }

    public void setRegAdvancedKeys(Map<String, List<String>> regAdvancedKeys) {
        this.regAdvancedKeys = regAdvancedKeys;
    }

    public Map<String, List<String>> getRegAdvancedKeyData() {
        return regAdvancedKeyData;
    }

    public void setRegAdvancedKeyData(Map<String, List<String>> regAdvancedKeyData) {
        this.regAdvancedKeyData = regAdvancedKeyData;
    }

    public String getReturnVersionCode() {
        return returnVersionCode;
    }

    public void setReturnVersionCode(String returnVersionCode) {
        this.returnVersionCode = returnVersionCode;
    }

    public long getReturnVersionId() {
        return returnVersionId;
    }

    public void setReturnVersionId(long returnVersionId) {
        this.returnVersionId = returnVersionId;
    }

    public boolean isEncryptEnabled() {
        return encryptEnabled;
    }

    public void setEncryptEnabled(boolean encryptEnabled) {
        this.encryptEnabled = encryptEnabled;
    }

    public int getInsertBatchSize() {
        return insertBatchSize;
    }

    public void setInsertBatchSize(int insertBatchSize) {
        this.insertBatchSize = insertBatchSize;
    }

    public int getDeleteBatchSize() {
        return deleteBatchSize;
    }

    public void setDeleteBatchSize(int deleteBatchSize) {
        this.deleteBatchSize = deleteBatchSize;
    }

    public boolean isPackageRejectEnabled() {
        return packageRejectEnabled;
    }

    public void setPackageRejectEnabled(boolean packageRejectEnabled) {
        this.packageRejectEnabled = packageRejectEnabled;
    }

    public List<String> getAcceptedDefinitionCodes() {
        return acceptedDefinitionCodes;
    }
}
