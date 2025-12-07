package net.fina.server.reg.validator;

import net.fina.common.client.mdt.MDTNodeTypes;
import net.fina.common.server.util.ConfigurationUtil;
import net.fina.common.shared.mdt.ProcessStage;
import net.fina.messages.MessagesUtil;
import net.fina.server.processing.ErrorHandler;
import net.fina.server.processing.ListElementUtil;
import net.fina.server.processing.impl.ComparisonMessageTemplateUtil;
import net.fina.server.processing.model.ComparisonItem;
import net.fina.server.processing.script.ScriptEngineBase;
import net.fina.server.processing.script.ScriptEngineFactory;
import net.fina.server.reg.model.*;
import net.fina.server.reg.util.RegExcelProcessingUtil;
import net.fina.server.reg.util.RegJSTreeRow;
import net.fina.server.reg.util.RegUtil;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class RegInputValidator implements InputValidator {
    private final InputsMetaModel inputsMetaModel;
    private final List<Integer> emptyRows = new ArrayList<>();
    /*
     * ** Error Store
     * ** @Key: Column
     * ** @Value: @Key: ValidationErrorType, @Value: List of errors
     * */
    private final Map<String, Map<ValidationErrorType, List<String>>> errorStore = new HashMap<>();
    private final Logger log = Logger.getLogger(getClass());
    private int REG_MAX_SHEET_ERROR_LIMIT;
    private int errorCounter;

    public RegInputValidator(InputsMetaModel inputsMetaModel) {
        initSheetErrorLimit();
        this.inputsMetaModel = inputsMetaModel;

        Map<ValidationErrorType, List<String>> vet = new HashMap<>();
        vet.put(ValidationErrorType.OTHER, new ArrayList<>());
        errorStore.put(ValidationErrorType.OTHER.name(), vet);

        for (InputMetaModel model : inputsMetaModel.getInputs()) {
            errorStore.put(model.getColumn(), new HashMap<>());
            for (ValidationErrorType errorType : ValidationErrorType.values()) {
                errorStore.get(model.getColumn()).put(errorType, new ArrayList<>());
            }
        }

    }


    public int getSheetErrorLimit() {
        return REG_MAX_SHEET_ERROR_LIMIT;
    }

    private void initSheetErrorLimit() {
        try {
            this.REG_MAX_SHEET_ERROR_LIMIT = Integer.parseInt(ConfigurationUtil.get().get("REG_MAX_SHEET_ERROR_LIMIT"));
        } catch (Throwable t) {
            REG_MAX_SHEET_ERROR_LIMIT = 100;
        }

    }

    @Override
    public Map<String, CellConfigModel> validate(Map<String, CellConfigModel> currentRowValues, int currentRow, RegProcessConfig config, String sheetName) throws IOException {
        Map<String, CellConfigModel> result = new HashMap<>();
        Map<String, CellConfigModel> mdtCodeValues = new HashMap<>();

        for (InputMetaModel model : inputsMetaModel.getInputs()) {
            String column = model.getColumn();
            String cell = column + currentRow;
            CellConfigModel cellConfigModel = currentRowValues.get(cell);
            mdtCodeValues.put(model.getCode(), cellConfigModel);
        }

        for (InputMetaModel model : inputsMetaModel.getInputs()) {
            String column = model.getColumn();
            String cell = column + currentRow;

            CellConfigModel cellConfigModel = currentRowValues.getOrDefault(cell, new CellConfigModel("", ""));

            if (!model.isOptional() && (cellConfigModel == null || cellConfigModel.getFormattedValue() == null || cellConfigModel.getOriginalValue().isEmpty())) {   // check required fields
                String xmlLineReference = getXmlLineReferenceString(cellConfigModel);
                if (cellConfigModel == null) {
                    addError(ValidationErrorType.REQUIRED_FIELD, model.getColumn(), cell, sheetName);
                } else {
                    addError(ValidationErrorType.REQUIRED_FIELD, model.getColumn(), cell + " " + xmlLineReference, sheetName);
                }
            } else if (cellConfigModel != null && cellConfigModel.getOriginalValue() != null && !cellConfigModel.getOriginalValue().isEmpty()) {
                try {
                    switch (model.getMdtNode().getDataType()) {
                        case DATE:  // date field MUST have it's own date pattern
                            validateDateFieldPattern(cellConfigModel, model, currentRow, config.getDateFormat(), sheetName);
                            result.put(cell, cellConfigModel);
                            break;
                        case DATE_TIME:  // date field MUST have it's own date pattern
                            validateDateFieldPattern(cellConfigModel, model, currentRow, config.getDateTimeFormat(), sheetName);
                            result.put(cell, cellConfigModel);
                            break;
                        case NUMERIC:
                            validateNumberField(cellConfigModel, model, currentRow, sheetName);
                            currentRowValues.put(cell, cellConfigModel);
                            result.put(cell, cellConfigModel);
                            break;
                        case TEXT:
                            if (cellConfigModel.getOriginalValue() != null && cellConfigModel.getOriginalValue().length() > model.getLength()) {
                                addError(ValidationErrorType.LENGTH, column, getErrorMessage(cellConfigModel, cell, null, "Max Length : " + model.getLength()), sheetName);
                            }
                            if (model.getMdtNode().getType().equals(MDTNodeTypes.LIST)) {
                                validateListElementValues(model, cell, column, cellConfigModel, sheetName);
                            }
                            result.put(cell, cellConfigModel);
                            break;
                    }
                    validateComparisons(cellConfigModel, model, currentRowValues, cell, currentRow, column, config, sheetName, mdtCodeValues);
                } catch (Throwable t) {
                    log.error(t.getMessage(), t);
                    addError(ValidationErrorType.OTHER, column, getErrorMessage(cellConfigModel, cell, cellConfigModel.getOriginalValue(), t.getMessage()), sheetName);
                }
            }
        }
        return result;
    }

    @Override
    public void checkRegAdvancedRow(Map<String, CellConfigModel> currentRowValues, RegProcessConfig config, String sheetName, int currentRow) {
        StringBuilder sb = new StringBuilder();

        for (InputMetaModel model : inputsMetaModel.getInputs()) {
            if (model.isKey()) {
                String cell = model.getColumn() + currentRow;
                CellConfigModel cellConfigModel = currentRowValues.get(cell);
                sb.append(cellConfigModel.getOriginalValue()).append(RegExcelProcessingUtil.REG_ADVANCED_KEY_SEPARATOR);
            }
        }

        sb.setLength(sb.length() - 1);

        switch (config.getRegAdvancedFileType()) {
            case CREATE:
                if (config.getRegAdvancedKeyData().get(inputsMetaModel.getReturnCode()).contains(sb.toString())) {
                    addError(ValidationErrorType.OTHER, "Record on the row: " + currentRow + ", already exists in the system");
                }
                break;
            case UPDATE:
            case DELETE:
                if (!config.getRegAdvancedKeyData().get(inputsMetaModel.getReturnCode()).contains(sb.toString())) {
                    addError(ValidationErrorType.OTHER, "Record on the row: " + currentRow + ", does not exist in the system");
                }
                break;
        }
    }

    private void validateListElementValues(InputMetaModel model, String cell, String column, CellConfigModel cellConfigModel, String sheetName) {
        if (model.getListElementItems().isEmpty()) {
            addError(ValidationErrorType.COMPARISON, column, getErrorMessage(cellConfigModel, cell, cellConfigModel.getOriginalValue(), "List Element Values Are Absent"), sheetName);
        } else if (!model.getListElementItems().contains(cellConfigModel.getOriginalValue())) {
            String message = " Is not from the ";
            if (model.getCatalog() != null) {
                message += " Catalog : " + model.getCatalog().toString();
            } else {
                message = " Is not from the " + ListElementUtil.truncateListElementMessage(model.getListElementItems());
            }

            addError(ValidationErrorType.COMPARISON, column, getErrorMessage(cellConfigModel, cell, cellConfigModel.getOriginalValue(), message), sheetName);
        }
    }

    private Long parseDateSafe(String value) {
        try {
            return Long.parseLong(value);
        } catch (Throwable ignore) {
        }
        return null;
    }


    private void validateDateFieldPattern(CellConfigModel cellConfigModel, InputMetaModel metaModel, int currentRow, String dateFormatPattern, String sheetName) {
        DateFormat dateFormat = new SimpleDateFormat(dateFormatPattern);
        try {

            Long dateTimeMillis = parseDateSafe(cellConfigModel.getOriginalValue());
            Date date;
            if (dateTimeMillis != null) {
                date = new Date(dateTimeMillis);
            } else {
                date = dateFormat.parse(cellConfigModel.getOriginalValue());
            }
            if (!dateFormat.format(date).equals(cellConfigModel.getFormattedValue())) {
                throw new Exception("Invalid Date Format : " + cellConfigModel.getFormattedValue());
            }
            cellConfigModel.setOriginalValue(String.valueOf(date.getTime()));
        } catch (Throwable e) {
//            String xmlLineReference = getXmlLineReferenceString(cellConfigModel);

            addError(ValidationErrorType.DATE_FORMAT, metaModel.getColumn(), getErrorMessage(cellConfigModel, metaModel.getColumn() + currentRow, cellConfigModel.getFormattedValue(), "Does not match the pattern: " + dateFormatPattern), sheetName);
        }
    }

    private void validateNumberField(CellConfigModel cellConfigModel, InputMetaModel metaModel, int currentRow, String sheetName) {
        try {
            if ((cellConfigModel.getOriginalValue() == null || cellConfigModel.getOriginalValue().isEmpty())) {
                cellConfigModel.setOriginalValue(metaModel.getDefaultValue());
            }

            double ov = Double.parseDouble(cellConfigModel.getOriginalValue());
            cellConfigModel.setNumericValue(ov);

        } catch (Exception ex) {
            addError(ValidationErrorType.NUMBER_FORMAT, metaModel.getColumn(), getErrorMessage(cellConfigModel, metaModel.getColumn() + currentRow, cellConfigModel.getOriginalValue(), "Is not a number"), sheetName);
        }
    }

    private String getRowDescription(int row, String description) {
        String message = " row %d - %s ";
        return String.format(message, row, description);
    }

    private void validateComparisons(CellConfigModel cellConfigModel, InputMetaModel input, Map<String, CellConfigModel> values, String cell, int row, String column, RegProcessConfig config, String sheetName, Map<String, CellConfigModel> mdtCodeValues) {

        for (ComparisonItem comp : input.getComparisons()) {
            if (comp.processStage.equals(ProcessStage.POST_PROCESS)) {
                continue;
            }
            String itemValue = cellConfigModel.getOriginalValue();
            if (comp.leftEquation != null && !comp.leftEquation.trim().isEmpty()) {
                itemValue = ScriptEngineFactory.get().call(new RegJSTreeRow(config, mdtCodeValues, cellConfigModel), ScriptEngineBase.createFunction(comp.leftEquation));
            }

            String compValue = ScriptEngineFactory.get().call(new RegJSTreeRow(config, mdtCodeValues, cellConfigModel), ScriptEngineBase.createFunction(comp.equation));

            compValue = compValue != null ? compValue.trim() : null;
            itemValue = itemValue != null ? itemValue.trim() : null;

            switch (input.getTypeEnum()) {
                case BIGNUMBER:
                case NUMBER:
                case INTEGER:
                    if (itemValue != null) {
                        itemValue = Double.toString(RegUtil.convertAndRoundNumber(comp, itemValue, config));
                    }
                    if (compValue != null) {
                        compValue = Double.toString(RegUtil.convertAndRoundNumber(comp, compValue, config));
                    }
                    break;
            }

            int compResult = RegUtil.compare(compValue, itemValue, input.getTypeEnum(), config);
            String compStr = (comp.leftEquation != null && !comp.leftEquation.trim().isEmpty() ? equationToUserFriendly(comp.leftEquation) : "");

            String validationMessage = null;

            //format date for error message
            if (input.getTypeEnum() == InputTypeEnum.TIMESTAMP || input.getTypeEnum() == InputTypeEnum.DATE) {
                try {
                    SimpleDateFormat df = new SimpleDateFormat(config.getDateFormat());
                    itemValue = itemValue != null ? df.format(new Date(new BigDecimal(itemValue).longValue())) : itemValue;
                    compValue = compValue != null ? df.format(new Date(new BigDecimal(compValue).longValue())) : compValue;
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }

            switch (comp.condition) {
                case EQUALS: {
                    if (compResult != 0) {
                        validationMessage = this.onComparisonError(comp, input, ErrorHandler.MessageId.COMPARISON_EQUALS, input.getCode(), getRowDescription(row, input.getDescription()), itemValue, compStr, compValue, equationToUserFriendly(comp.equation), row);
                    }
                    break;
                }
                case NOT_EQUALS: {
                    if (compResult == 0) {
                        validationMessage = this.onComparisonError(comp, input, ErrorHandler.MessageId.COMPARISON_NOT_EQUALS, input.getCode(), getRowDescription(row, input.getDescription()), itemValue, compStr, compValue, equationToUserFriendly(comp.equation), row);
                    }
                    break;

                }
                case GREATER: {
                    if (compResult <= 0) {
                        validationMessage = this.onComparisonError(comp, input, ErrorHandler.MessageId.COMPARISON_GREATER, input.getCode(), getRowDescription(row, input.getDescription()), itemValue, compStr, compValue, equationToUserFriendly(comp.equation), row);
                    }
                    break;
                }
                case GREATER_EQUALS: {
                    if (compResult < 0) {
                        validationMessage = this.onComparisonError(comp, input, ErrorHandler.MessageId.COMPARISON_GREATER_EQUALS, input.getCode(), getRowDescription(row, input.getDescription()), itemValue, compStr, compValue, equationToUserFriendly(comp.equation), row);
                    }
                    break;
                }
                case LESS: {
                    if (compResult >= 0) {
                        validationMessage = this.onComparisonError(comp, input, ErrorHandler.MessageId.COMPARISON_LESS, input.getCode(), getRowDescription(row, input.getDescription()), itemValue, compStr, compValue, equationToUserFriendly(comp.equation), row);
                    }
                    break;
                }
                case LESS_EQUALS: {
                    if (compResult > 0) {
                        validationMessage = this.onComparisonError(comp, input, ErrorHandler.MessageId.COMPARISON_LESS_EQUALS, input.getCode(), getRowDescription(row, input.getDescription()), itemValue, compStr, compValue, equationToUserFriendly(comp.equation), row);
                    }
                    break;
                }

                case UNKNOWN: {
                    validationMessage = this.onComparisonError(comp, input, ErrorHandler.MessageId.UNKNOWN_COMPARISON, input.getCode(), getRowDescription(row, input.getDescription()), itemValue, compStr, compValue, equationToUserFriendly(comp.equation), row);
                }

            }

            if (validationMessage != null) {
                String value = cellConfigModel.getOriginalValue();
                if (input.getTypeEnum() == InputTypeEnum.DATE || input.getTypeEnum() == InputTypeEnum.TIMESTAMP) {
                    value = cellConfigModel.getFormattedValue();
                }
                addError(ValidationErrorType.COMPARISON, column, getErrorMessage(cellConfigModel, cell, value, validationMessage), sheetName);
            }
        }
    }


    public String onComparisonError(ComparisonItem comparisonItem, InputMetaModel item, ErrorHandler.MessageId messageId, Object... params) {

        if (comparisonItem.messageTemplate != null && !Objects.equals(comparisonItem.messageTemplate.trim(), "")) {
            try {
                return new ComparisonMessageTemplateUtil().process(comparisonItem, item, params);
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
            }
        }
        return onError(item, messageId, params);
    }


    private String onError(InputMetaModel item, ErrorHandler.MessageId messageId, Object... params) {
        return compileMessage(messageId, params);

    }


    private String compileMessage(ErrorHandler.MessageId messageId, Object... params) {
        String message = MessagesUtil.getString(messageId.getCode());
        if (params != null && params.length > 0) {
            message = String.format(message, params);
        }
        return message;
    }


    private String equationToUserFriendly(String equation) {
        if (equation != null) {
            equation = equation.replace("tree.lookup", "lookup");
        }
        return equation;
    }


    /**
     * Returns whether input is valid or not, on the method invocation time. Input is valid, if error store is empty.
     *
     * @return input validness
     */
    @Override
    public boolean isValid() {
        for (Map.Entry<String, Map<ValidationErrorType, List<String>>> entry : errorStore.entrySet()) {
            for (ValidationErrorType errorType : ValidationErrorType.values()) {
                List<String> error = entry.getValue().get(errorType);
                if (error != null && !error.isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns whether existing row is empty or not. <br> Row is empty, when all containing cells values are empty.
     * <br> If row is empty, method adds it to the empty rows list.
     *
     * @param currentRowValues values from the current row (key: column reference; value: cell config file)
     * @param currentRow       current row number
     * @return empty rows list
     */
    @Override
    public boolean isRowEmpty(Map<String, CellConfigModel> currentRowValues, int currentRow) {
        for (CellConfigModel model : currentRowValues.values()) {
            if (model.getOriginalValue() != null && !model.getOriginalValue().isEmpty()) {
                return false;
            }
        }
        currentRowValues.clear();
        emptyRows.add(currentRow);
        return true;
    }

    /**
     * Returns existing empty rows list, which contains row indexes of the empty rows.
     *
     * @return empty rows list
     */
    @Override
    public List<Integer> getEmptyRows() {
        return emptyRows;
    }

    @Override
    public boolean checkErrorLimit() {
        return errorCounter > REG_MAX_SHEET_ERROR_LIMIT;
    }

    /**
     * Returns existing formatted errors list from error store.
     *
     * @return formatted errors list
     */
    @Override
    public List<String> getErrorList() {
        List<String> errorList = new ArrayList<>();
        for (Map.Entry<String, Map<ValidationErrorType, List<String>>> entry : errorStore.entrySet()) {
            for (ValidationErrorType errorType : ValidationErrorType.values()) {
                List<String> error = entry.getValue().get(errorType);
                if (error != null && !error.isEmpty()) {
                    StringBuilder sb = new StringBuilder(errorType.getMessage());
                    sb.append(" [").append("\n");
                    for (int i = 0; i < error.size() && i < REG_MAX_SHEET_ERROR_LIMIT; i++) {
                        sb.append(error.get(i)).append(" |\n");
                    }
                    if (error.size() > REG_MAX_SHEET_ERROR_LIMIT) {
                        errorList.add(sb.toString());
                        sb.append(" (").append(error.size() - REG_MAX_SHEET_ERROR_LIMIT).append(" More...)").append("\n").append("]");
                    }
                    sb.append("]");
                    errorList.add(sb.toString());
                }
            }
        }
        return errorList;
    }

    /**
     * Adds error message to the error store, with appropriate column and type.
     *
     * @param errorType    type of the occurred error
     * @param column       corresponding column name
     * @param errorMessage description of the error
     */
    @Override
    public void addError(ValidationErrorType errorType, String column, String errorMessage) {
        errorCounter++;
        errorStore.get(column).get(errorType).add(errorMessage);
    }

    /**
     * @param errorType
     * @param column
     * @param errorMessage
     * @param sheetName
     */
    @Override
    public void addError(ValidationErrorType errorType, String column, String errorMessage, String sheetName) {
        errorCounter++;
        String sheetIndicatorMessage = "";
        if (sheetName != null) {
            sheetIndicatorMessage = "'Sheet : " + sheetName + "'  ";
        }
        errorStore.get(column).get(errorType).add(sheetIndicatorMessage + errorMessage);
    }

    /**
     * Adds error message to the error store, with appropriate type.
     *
     * @param errorType    type of the occurred error
     * @param errorMessage description of the error
     */
    @Override
    public void addError(ValidationErrorType errorType, String errorMessage) {
        addError(errorType, errorType.name(), errorMessage);
    }

    /**
     * Returns formatted error message.
     *
     * @param cell    input file cell e.g. A12: A - Column; 12 - row; in the input file.
     * @param value   provided value in the input file cell
     * @param message error message description
     * @return formatted error message
     */
    private String getErrorMessage(CellConfigModel cellConfigModel, String cell, String value, String message) {
        return "'CELL: " + cell + "' " + (value != null ? "'VALUE: " + value + "' " : "  ") + message + "" + getXmlLineReferenceString(cellConfigModel);
    }

    private String getXmlLineReferenceString(CellConfigModel cellConfigModel) {
        if (cellConfigModel != null && cellConfigModel.getRegFileType().equals(RegFileType.XML)) {
            return " Line '" + cellConfigModel.getXmlLineNumber() + "' Column '" + cellConfigModel.getXmlColumnNumber() + "' ";
        }
        return "";
    }
}

