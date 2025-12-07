package net.fina.server.classifier.entity;

import jakarta.persistence.*;
import net.fina.auditlog.api.Audited;
import net.fina.server.i18n.helper.Description;
import org.hibernate.annotations.Type;

import java.util.Date;

@Entity(name = "IN_MDT_CATALOG_ITEMS")
@Table(name = "IN_MDT_CATALOG_ITEMS")
public class MDTCatalogItem implements Audited {
    @Id
    @SequenceGenerator(name = "mdt_catalog_item_seq", sequenceName = "mdt_catalog_item_seq", allocationSize = 1)
    @GeneratedValue(generator = "mdt_catalog_item_seq", strategy = GenerationType.SEQUENCE)
    private long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COLUMN_ID")
    private MDTCatalogColumn mdtCatalogColumn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VERSION_ID")
    private MDTCatalogItemRowVersion version;

    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    @Column(name = "VALUETRID")
    private Description value;

    @Column(name = "NVALUE")
    private double nvalue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROW_ID")
    private MDTCatalogItemRow row;

    @Transient
    private boolean isDeleted = false;

    @Column(name = "LATEST_VERSION")
    private boolean latest;
    @Transient
    private Date createdAt;

    public MDTCatalogItem() {
    }

    public MDTCatalogItem(long id, MDTCatalogColumn mdtCatalogColumn, MDTCatalogItemRowVersion version, Description value, double nvalue, MDTCatalogItemRow row, boolean isDeleted, Date createdAt) {
        this.id = id;
        this.mdtCatalogColumn = mdtCatalogColumn;
        this.version = version;
        this.value = value;
        this.nvalue = nvalue;
        this.row = row;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public MDTCatalogColumn getMdtCatalogColumn() {
        return mdtCatalogColumn;
    }

    public void setMdtCatalogColumn(MDTCatalogColumn mdtCatalogColumn) {
        this.mdtCatalogColumn = mdtCatalogColumn;
    }

    public MDTCatalogItemRowVersion getVersion() {
        return version;
    }

    public void setVersion(MDTCatalogItemRowVersion version) {
        this.version = version;
    }

    public Description getValue() {
        return value;
    }

    public void setValue(Description value) {
        this.value = value;
    }

    public double getNvalue() {
        return nvalue;
    }

    public void setNvalue(double nvalue) {
        this.nvalue = nvalue;
    }

    public MDTCatalogItemRow getRow() {
        return row;
    }

    public void setRow(MDTCatalogItemRow row) {
        this.row = row;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isLatest() {
        return latest;
    }

    public void setLatest(boolean latest) {
        this.latest = latest;
    }

}
