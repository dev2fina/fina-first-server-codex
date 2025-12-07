package net.fina.server.reg.util.processor;

import net.fina.server.reg.model.CellConfigModel;
import net.fina.server.reg.model.InputMetaModel;
import net.fina.server.reg.model.InputsMetaModel;
import net.fina.server.reg.model.RegProcessConfig;
import net.fina.server.reg.validator.InputValidator;
import net.fina.server.reg.validator.ValidationErrorType;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class RegFileProcessorUtilBase {

    public static boolean isValid(Map<String, InputValidator> inputValidatorMap) {
        for (Map.Entry<String, InputValidator> e : inputValidatorMap.entrySet()) {
            if (!e.getValue().isValid()) {
                return false;
            }
        }
        return true;
    }

    public static Map<String, List<String>> getErrorList(Map<String, InputValidator> inputValidatorMap) {
        Map<String, List<String>> errors = new HashMap<>();
        for (Map.Entry<String, InputValidator> e : inputValidatorMap.entrySet()) {
            errors.put(e.getKey(), e.getValue().getErrorList());
        }
        return errors;
    }

    public void prePersistCheck(final InputsMetaModel inputs, Map<String, CellConfigModel> currentRowValues, PreparedStatement ps,
                                int currentRow, InputValidator inputValidator,
                                String sheetName, RegProcessConfig config, boolean persistData) throws Exception {

        if (inputValidator.checkErrorLimit()) {
            String maxErrorLimitMessage = "Reached Error Limit " + sheetName + " : errors > " + inputValidator.getSheetErrorLimit();
            inputValidator.addError(ValidationErrorType.OTHER, maxErrorLimitMessage);
            throw new InterruptedException(maxErrorLimitMessage);
        }

        checkAndPersistData(inputs, currentRowValues, ps, currentRow, inputValidator, sheetName, config, persistData);

    }

    protected int initRowStatement(List<InputMetaModel> inputMetaModels, Map<String, CellConfigModel> cellConfigModelMap, int excelRow, PreparedStatement ps, int psStartIndex) throws Exception {
        int index = psStartIndex;
        for (InputMetaModel model : inputMetaModels) {
            CellConfigModel cellValue = cellConfigModelMap.get(model.getColumn() + excelRow);
            switch (model.getTypeEnum()) {
                case TIMESTAMP:
                case DATETIME:
                    Timestamp timestamp = (cellValue != null && cellValue.getOriginalValue() != null) ? new Timestamp(Long.parseLong(cellValue.getOriginalValue())) : null;
                    ps.setTimestamp(index, timestamp);
                    break;
                case DATE:
                    Date date = (cellValue != null && cellValue.getOriginalValue() != null) ? new Date(Long.parseLong(cellValue.getOriginalValue())) : null;
                    ps.setDate(index, date);
                    break;
                case NUMBER:
                case BIGNUMBER:
                case INTEGER:
                    ps.setDouble(index, cellValue != null ? cellValue.getNumericValue() : 0D);
                    break;
                case BOOLEAN:
                case STRING:
                    String value = (cellValue != null ? cellValue.getOriginalValue() : null);
                    ps.setObject(index, value);
                    break;

            }
            index++;
        }
        return index;
    }

    protected abstract void checkAndPersistData(final InputsMetaModel input, Map<String, CellConfigModel> currentRowValues, PreparedStatement ps, int currentRow, InputValidator inputValidator, String sheetName, RegProcessConfig config, boolean persistDate) throws Exception;

    protected abstract void persist(final InputsMetaModel input, PreparedStatement ps, RegProcessConfig config, int excelRow, Map<String, CellConfigModel> result) throws Exception;

}
