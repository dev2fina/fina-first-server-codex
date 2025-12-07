package net.fina.server.returns.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import net.fina.common.client.mdt.MDTNodeDataTypes;
import net.fina.common.client.mdt.MDTNodeTypes;
import net.fina.common.client.returns.ReturnTableType;

import java.io.Serializable;
import java.util.Objects;

/**
 * User: nikoloz
 * Date: 11/5/13
 * Time: 7:16 PM
 */
@Entity
@Table(name = "IN_RETURN_ITEMS")
@IdClass(ReturnItemLiteId.class)
public class ReturnItemLite implements Cloneable, Serializable {
    private static final int MAX_CHARS = readMaxCharsEnvSafe();
    @Id
    @Column(name = "ID")
    private long id;
    @Id
    @Column(name = "RETURNID")
    private long returnId;
    @Id
    @Column(name = "NODEID")
    private long nodeId;
    @Id
    @Column(name = "VERSIONID")
    private long versionId;
    @Id
    @Column(name = "TABLEID")
    private long tableId;
    @Id
    @Column(name = "ROWNUMBER")
    private long rowNumber;
    @Column(name = "VALUE")
    private String value;
    @Column(name = "NVALUE")
    private double nValue;
    // Transient Fields
    @Transient
    private String nodeCode;
    @Transient
    private ReturnTableType tableType;
    @Transient
    private MDTNodeTypes nodeType;
    @Transient
    private MDTNodeDataTypes dataType;
    @Transient
    private boolean init;
    @Transient
    private boolean required;

    private static int readMaxCharsEnvSafe() {
        try {
            Integer.parseInt(System.getenv().getOrDefault("FINA_RI_VALUE_MAX_LENGTH", "4000"));
        } catch (Exception ignore) {
        }

        return 4000;
    }

    @Override
    public ReturnItemLite clone() throws CloneNotSupportedException {
        return (ReturnItemLite) super.clone();
    }

    public int getRowNumberCodeTableIdHash() {
        return Objects.hash(tableId, rowNumber, nodeId, nodeCode);
    }

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

    public String getValue() {
        return value;
    }

    public void setValue(String value, @NotNull MDTNodeDataTypes dataType) {
        if (dataType == MDTNodeDataTypes.UNKNOWN || dataType == MDTNodeDataTypes.TEXT) {
            if (value == null) return;
            if (value.length() < MAX_CHARS) {
                this.value = value;
                return;
            }

            int end = MAX_CHARS;
            if (Character.isHighSurrogate(value.charAt(end - 1))) {
                end--;
            }

            this.value = value.substring(0, end);
        } else {
            this.value = value;
        }
    }

    public double getnValue() {
        return nValue;
    }

    public void setnValue(double nValue) {
        this.nValue = nValue;
    }

    public ReturnTableType getTableType() {
        return tableType;
    }

    public void setTableType(ReturnTableType tableType) {
        this.tableType = tableType;
    }

    public MDTNodeTypes getNodeType() {
        return nodeType;
    }

    public void setNodeType(MDTNodeTypes nodeType) {
        this.nodeType = nodeType;
    }

    public MDTNodeDataTypes getDataType() {
        return dataType;
    }

    public void setDataType(MDTNodeDataTypes dataType) {
        this.dataType = dataType;
    }

    public String getNodeCode() {
        return nodeCode;
    }

    public void setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode;
    }

    public boolean isInit() {
        return init;
    }

    public void setInit(boolean init) {
        this.init = init;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    @Override
    public String toString() {
        return "ReturnItemLite{" +
                "id=" + id +
                ", returnId=" + returnId +
                ", nodeId=" + nodeId +
                ", versionId=" + versionId +
                ", tableId=" + tableId +
                ", rowNumber=" + rowNumber +
                ", nodeCode='" + nodeCode + '\'' +
                '}';
    }
}
