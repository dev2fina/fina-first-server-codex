package net.fina.server.reg.validator;

import net.fina.server.reg.model.CellConfigModel;
import net.fina.server.reg.model.RegProcessConfig;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface InputValidator {

    String GENERAL_ERROR = "Unexpected Error, Contact Administrator!";

    Map<String, CellConfigModel> validate(Map<String, CellConfigModel> currentRowValues, int currentRow, RegProcessConfig config, String sheetName) throws IOException;

    void checkRegAdvancedRow(Map<String, CellConfigModel> currentRowValues, RegProcessConfig config, String sheetName, int currentRow);

    void addError(ValidationErrorType errorType, String column, String errorMessage);

    void addError(ValidationErrorType errorType, String column, String errorMessage, String sheetName);

    void addError(ValidationErrorType errorType, String errorMessage);

    List<String> getErrorList();

    boolean isValid();

    boolean isRowEmpty(Map<String, CellConfigModel> currentRowValues, int currentRow);

    List<Integer> getEmptyRows();

    boolean checkErrorLimit();

    int getSheetErrorLimit();

}
