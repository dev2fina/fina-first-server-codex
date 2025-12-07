package net.fina.server.classifier.entity;

import net.fina.auditlog.api.Audited;
import net.fina.server.mdt.entity.MDTNode;

import jakarta.persistence.*;

@Entity(name = "IN_MDT_CATALOG_ITEM_ROWS")
@Table(name = "IN_MDT_CATALOG_ITEM_ROWS")
public class MDTCatalogItemRow implements Audited {
    @Id
    @SequenceGenerator(name = "mdt_catalog_item_row_seq", sequenceName = "mdt_catalog_item_row_seq", allocationSize = 1)
    @GeneratedValue(generator = "mdt_catalog_item_row_seq", strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(name = "ROW_NUMBER")
    private int rowNumber;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATALOG_ID")
    private MDTCatalog catalog;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MDT_NODE_ID")
    private MDTNode dataNode;

    @Column(name = "PARENT_ID")
    private long parentId;

    @Column(name = "LEAF")
    private boolean leaf;

    @Column(name = "IS_DELETED")
    private boolean isDeleted = false;

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

    public MDTCatalog getCatalog() {
        return catalog;
    }

    public void setCatalog(MDTCatalog catalog) {
        this.catalog = catalog;
    }

    public MDTNode getDataNode() {
        return dataNode;
    }

    public void setDataNode(MDTNode dataNode) {
        this.dataNode = dataNode;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
