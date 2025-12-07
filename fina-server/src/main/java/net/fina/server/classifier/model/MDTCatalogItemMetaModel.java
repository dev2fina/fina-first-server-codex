package net.fina.server.classifier.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MDTCatalogItemMetaModel {
    private long id;
    private int rowNumber;
    private MDTCatalogColumnMetaModel column;
    private long nodeId;
    private long versionId;
    private String versionCode;
    private Object value;
    private Map<Long, String> valuesI18n = new HashMap<>();
    private long nameStrId;
    private boolean isDeleted;

    public MDTCatalogItemMetaModel() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public MDTCatalogColumnMetaModel getColumn() {
        return column;
    }

    public void setColumn(MDTCatalogColumnMetaModel column) {
        this.column = column;
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

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public long getNameStrId() {
        return nameStrId;
    }

    public void setNameStrId(long nameStrId) {
        this.nameStrId = nameStrId;
    }

    public Map<Long, String> getValuesI18n() {
        return valuesI18n == null ? new HashMap<>() : valuesI18n;
    }

    public void setValuesI18n(Map<Long, String> valuesI18n) {
        this.valuesI18n = valuesI18n;
    }
}
