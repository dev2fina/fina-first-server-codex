package net.fina.server.reg.model;


import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputsMetaModel implements Serializable {

    protected String sheetName;
    protected String returnCode;
    protected String tableName;
    protected int startRow;
    protected String password;
    protected Map<String, Integer> columnPrecisionMap = new HashMap<>();

    protected List<InputMetaModel> inputs;

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<InputMetaModel> getInputs() {
        return inputs;
    }

    public void setInputs(List<InputMetaModel> inputs) {
        this.inputs = inputs;
    }

    public int getStartRow() {
        return startRow;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isProtected() {
        return this.password != null && !this.password.trim().isEmpty();
    }

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public Map<String, Integer> getColumnPrecisionMap() {
        return columnPrecisionMap;
    }

    public void setColumnPrecisionMap(Map<String, Integer> columnPrecisionMap) {
        this.columnPrecisionMap = columnPrecisionMap;
    }

    @Override
    public String toString() {
        return "InputsMetaModel{" +
                "sheetName='" + sheetName + '\'' +
                ", returnCode='" + returnCode + '\'' +
                ", tableName='" + tableName + '\'' +
                '}';
    }
}
