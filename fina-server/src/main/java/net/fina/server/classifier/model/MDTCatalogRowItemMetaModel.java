package net.fina.server.classifier.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MDTCatalogRowItemMetaModel {
    private long rowId;
    private long parentRowId;
    private int rowNumber;
    private long nodeId;
    private String version;
    private boolean leaf;
    private Date createdAt;
    private Date modifiedAt;
    private List<MDTCatalogItemMetaModel> rowItems = new ArrayList<>();
    private boolean isDeleted;

    public MDTCatalogRowItemMetaModel() {
    }

    public long getRowId() {
        return rowId;
    }

    public void setRowId(long rowId) {
        this.rowId = rowId;
    }

    public long getParentRowId() {
        return parentRowId;
    }

    public void setParentRowId(long parentRowId) {
        this.parentRowId = parentRowId;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public List<MDTCatalogItemMetaModel> getRowItems() {
        return rowItems;
    }

    public void setRowItems(List<MDTCatalogItemMetaModel> rowItems) {
        this.rowItems = rowItems;
    }

    public long getNodeId() {
        return nodeId;
    }

    public void setNodeId(long nodeId) {
        this.nodeId = nodeId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
