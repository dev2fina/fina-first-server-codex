package net.fina.server.dcs.uploadfile.impl.reader.excel;

import net.fina.server.matrix.entity.MatrixTableType;
import net.fina.server.matrix.entity.SubMatrixTable;

import java.util.ArrayList;
import java.util.List;

public class MatrixMappingOption {
    private String sheetName;
    private String returnCode;
    private MatrixTableType tableType;
    private boolean isProtected;
    private long mainMatrixId;
    private List<SubMatrixTable> tableList;

    public MatrixMappingOption() {
    }

    public MatrixMappingOption(String sheetName, String returnCode, MatrixTableType tableType, boolean isProtected, long mainMatrixId, List<SubMatrixTable> tableList) {
        this.sheetName = sheetName;
        this.returnCode = returnCode;
        this.tableType = tableType;
        this.isProtected = isProtected;
        this.mainMatrixId = mainMatrixId;
        this.tableList = tableList;
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

    public MatrixTableType getTableType() {
        return tableType;
    }

    public void setTableType(MatrixTableType tableType) {
        this.tableType = tableType;
    }

    public boolean isProtected() {
        return isProtected;
    }

    public void setProtected(boolean aProtected) {
        isProtected = aProtected;
    }

    public long getMainMatrixId() {
        return mainMatrixId;
    }

    public void setMainMatrixId(long mainMatrixId) {
        this.mainMatrixId = mainMatrixId;
    }

    public List<SubMatrixTable> getTableList() {
        return tableList == null ? new ArrayList<>() : tableList;
    }

    public void setTableList(List<SubMatrixTable> tableList) {
        this.tableList = tableList;
    }
}
