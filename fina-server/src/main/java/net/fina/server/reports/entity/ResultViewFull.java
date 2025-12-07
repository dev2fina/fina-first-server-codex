package net.fina.server.reports.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

import java.util.Objects;

@Entity(name = "RESULT_VIEW_FULL")
@Table(name = "RESULT_VIEW_FULL")
@Immutable // Marks the entity as read-only
public class ResultViewFull {

    @Id
    @Column(name = "RETURNID")
    private long returnId;
    @Id
    @Column(name = "NODEID")
    private long nodeId;
    @Id
    @Column(name = "ITEMTABLEID")
    private long tableId;
    @Id
    @Column(name = "ITEMROWNUMBER")
    private long rowNumber;
    @Id
    @Column(name = "VERSIONID")
    private long versionId;
    @Column(name = "NVALUE")
    private double nValue;
    @Column(name = "\"VALUE\"")
    private String value;

    public ResultViewFull() {
    }

    public ResultViewFull(long returnId, long nodeId, long tableId, long rowNumber, double nValue, String value, long versionId) {
        this.returnId = returnId;
        this.nodeId = nodeId;
        this.tableId = tableId;
        this.rowNumber = rowNumber;
        this.nValue = nValue;
        this.value = value;
    }

    public long getReturnId() {
        return returnId;
    }

    public long getNodeId() {
        return nodeId;
    }

    public long getTableId() {
        return tableId;
    }

    public long getRowNumber() {
        return rowNumber;
    }

    public double getnValue() {
        return nValue;
    }

    public long getVersionId() {
        return versionId;
    }

    public void setVersionId(long versionId) {
        this.versionId = versionId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ResultViewFull that = (ResultViewFull) o;
        return returnId == that.returnId
                && nodeId == that.nodeId
                && tableId == that.tableId
                && rowNumber == that.rowNumber
                && versionId == that.versionId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(returnId, nodeId, tableId, rowNumber, versionId);
    }
}
