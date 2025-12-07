package net.fina.server.dcs.uploadfile.impl.reader.excel;

import java.util.Map;

public class MatrixMappingOptionAdapter {
    private String sheetName;
    private String returnCode;
    private int startRow;
    private Map<String, String> mdtCodeCellReferenceMap;
    private Map<String, Integer> columnPrecisionMap;

    public MatrixMappingOptionAdapter() {
    }

    public MatrixMappingOptionAdapter(String sheetName, String returnCode, int startRow, Map<String, String> mdtCodeCellReferenceMap,Map<String, Integer> columnPrecisionMap) {
        this.sheetName = sheetName;
        this.returnCode = returnCode;
        this.startRow = startRow;
        this.mdtCodeCellReferenceMap = mdtCodeCellReferenceMap;
        this.columnPrecisionMap = columnPrecisionMap;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public int getStartRow() {
        return startRow;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public Map<String, String> getMdtCodeCellReferenceMap() {
        return mdtCodeCellReferenceMap;
    }

    public void setMdtCodeCellReferenceMap(Map<String, String> mdtCodeCellReferenceMap) {
        this.mdtCodeCellReferenceMap = mdtCodeCellReferenceMap;
    }

    public void setColumnPrecisionMap(Map<String, Integer> columnPrecisionMap) {
        this.columnPrecisionMap = columnPrecisionMap;
    }

    public Map<String, Integer> getColumnPrecisionMap() {
        return columnPrecisionMap;
    }
}
