package net.fina.server.returns.entity;

import java.io.Serializable;
import java.util.Objects;

@SuppressWarnings("serial")
public class ReturnItemLiteId implements Serializable {

    private long id;
    private long returnId;
    private long nodeId;
    private long versionId;
    private long tableId;
    private long rowNumber;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getReturnId() {
        return returnId;
    }

    public void setReturnId(long returnId) {
        this.returnId = returnId;
    }

    public long getNodeId() {
        return nodeId;
    }

    public void setNodeId(long nodeId) {
        this.nodeId = nodeId;
    }

    public long getVersionId() {
        return versionId;
    }

    public void setVersionId(long versionId) {
        this.versionId = versionId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReturnItemLiteId that = (ReturnItemLiteId) o;

        if (id != that.id) return false;
        if (nodeId != that.nodeId) return false;
        if (returnId != that.returnId) return false;
        if (rowNumber != that.rowNumber) return false;
        if (tableId != that.tableId) return false;
        if (versionId != that.versionId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, returnId, nodeId, versionId, tableId, rowNumber);
    }

    @Override
    public String toString() {
        return "ReturnItemLiteId{" +
                "id=" + id +
                ", returnId=" + returnId +
                ", nodeId=" + nodeId +
                ", versionId=" + versionId +
                ", tableId=" + tableId +
                ", rowNumber=" + rowNumber +
                '}';
    }
}
