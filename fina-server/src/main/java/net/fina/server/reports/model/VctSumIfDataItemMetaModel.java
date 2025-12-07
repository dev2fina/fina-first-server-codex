package net.fina.server.reports.model;

public class VctSumIfDataItemMetaModel {

    private long returnId;
    private long tableId;
    private long rowNumber;
    private double nValue;
    private String value;
    private long nodeId;

    public long getReturnId() {
        return returnId;
    }

    public void setReturnId(long returnId) {
        this.returnId = returnId;
    }

    public long getTableId() {
        return tableId;
    }

    public void setTableId(long tableId) {
        this.tableId = tableId;
    }

    public long getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(long rowNumber) {
        this.rowNumber = rowNumber;
    }

    public double getnValue() {
        return nValue;
    }

    public void setnValue(double nValue) {
        this.nValue = nValue;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getNodeId() {
        return nodeId;
    }

    public void setNodeId(long nodeId) {
        this.nodeId = nodeId;
    }
}
