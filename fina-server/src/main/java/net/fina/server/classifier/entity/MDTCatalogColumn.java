package net.fina.server.classifier.entity;

import net.fina.auditlog.api.Audited;
import net.fina.server.i18n.helper.Description;
import org.hibernate.annotations.Type;

import jakarta.persistence.*;

@Entity(name = "IN_MDT_CATALOG_COLUMNS")
@Table(name = "IN_MDT_CATALOG_COLUMNS")
public class MDTCatalogColumn implements Audited {
    @Id
    @SequenceGenerator(name = "mdt_catalog_col_seq", sequenceName = "mdt_catalog_col_seq", allocationSize = 1)
    @GeneratedValue(generator = "mdt_catalog_col_seq", strategy = GenerationType.SEQUENCE)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATALOG_ID")
    private MDTCatalog catalog;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "DATA_TYPE")
    private DataType dataType;

    @Column(name = "NAMESTRID")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    private Description name;

    @Column(name = "IS_KEY")
    private boolean key;

    @Column(name = "SEQUENCE")
    private int sequence;

    @Column(name = "DATA_FORMAT")
    private String dataFormat;

    @Column(name = "IS_REQUIRED")
    private boolean isRequired;

    public MDTCatalogColumn() {
    }

    public MDTCatalogColumn(long id) {
        this.id = id;
    }
    public MDTCatalogColumn(long id, boolean key) {
        this.id = id;
        this.key = key;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public Description getName() {
        return name;
    }

    public void setName(Description name) {
        this.name = name;
    }

    public boolean isKey() {
        return key;
    }

    public void setKey(boolean key) {
        this.key = key;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getDataFormat() {
        return dataFormat;
    }

    public void setDataFormat(String dataFormat) {
        this.dataFormat = dataFormat;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setIsRequired(boolean isRequired) {
        this.isRequired = isRequired;
    }

    public MDTCatalog getCatalog() {
        return catalog;
    }

    public void setCatalog(MDTCatalog catalog) {
        this.catalog = catalog;
    }
}
