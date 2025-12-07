package net.fina.server.classifier.entity;


import net.fina.auditlog.api.Audited;
import net.fina.server.security.entity.User;

import jakarta.persistence.*;
import java.util.Date;

@Entity(name = "IN_MDT_CATALOG_ITEM_ROW_VERSION")
@Table(name = "IN_MDT_CATALOG_ITEM_ROW_VERSION")
public class MDTCatalogItemRowVersion implements Audited {

    @Id
    @SequenceGenerator(name = "mdt_catalog_item_ver_seq", sequenceName = "mdt_catalog_item_ver_seq", allocationSize = 1)
    @GeneratedValue(generator = "mdt_catalog_item_ver_seq", strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(name = "VERSION_CODE")
    private String versionCode;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATION_TIME")
    private Date createdAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROW_ID")
    private MDTCatalogItemRow row;

    @Column(name = "IS_DELETED")
    private boolean isDeleted = false;

    public MDTCatalogItemRowVersion() {}
    public MDTCatalogItemRowVersion(String versionCode, Date createdAt, Boolean isDeleted) {
        this.versionCode = versionCode;
        this.createdAt = createdAt;
        this.isDeleted = isDeleted;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
}
