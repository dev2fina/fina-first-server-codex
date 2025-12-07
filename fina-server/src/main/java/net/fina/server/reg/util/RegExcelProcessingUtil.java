package net.fina.server.reg.util;


import net.fina.server.reg.model.InputsMetaModel;

import java.util.List;

public class RegExcelProcessingUtil {

    public static final String MULTI_SHEET_INDICATOR = "*";
    public static final String SHEET_NAME_SPLIT_INDICATOR = "^";

    public static final String REG_ADVANCED_KEY_SEPARATOR = "_";

    public static boolean checkSheetNamePattern(String sheetName, InputsMetaModel inputsMetaModel) {
        if (inputsMetaModel.getSheetName().endsWith(MULTI_SHEET_INDICATOR)) {
            String sName = sheetName.split("\\" + SHEET_NAME_SPLIT_INDICATOR)[0];
            return sName.equals(inputsMetaModel.getSheetName().substring(0, inputsMetaModel.getSheetName().length() - 1));
        }
        return sheetName.equalsIgnoreCase(inputsMetaModel.getSheetName());
    }

    public static boolean checkSheetNamePattern(String sheetName, List<InputsMetaModel> inputsMetaModel) {
        boolean contains = false;
        for (InputsMetaModel inp : inputsMetaModel) {
            contains |= checkSheetNamePattern(sheetName, inp);
        }
        return contains;
    }

    public static boolean isMultiSheet(String sheetName, InputsMetaModel inputsMetaModel) {
        return inputsMetaModel.getSheetName().endsWith(MULTI_SHEET_INDICATOR) && sheetName.contains(SHEET_NAME_SPLIT_INDICATOR);
    }

    public static String getActualSheetName(String sheetName){
        return sheetName.split("\\" + SHEET_NAME_SPLIT_INDICATOR)[0];
    }

    public static InputsMetaModel getBySheetName(String sheetName, List<InputsMetaModel> inputsMetaModel) {
        for (InputsMetaModel inp : inputsMetaModel) {
            if(checkSheetNamePattern(sheetName, inp)){
                return inp;
            }
        }
        return null;
    }
}
