package net.fina.server.classifier.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fina.auditlog.api.Audited;
import net.fina.server.legislative.entity.LegislativeDocument;
import net.fina.server.mdt.entity.MDTNode;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity(name = "IN_MDT_CATALOG")
@Table(name = "IN_MDT_CATALOG")
public class MDTCatalog implements Audited {

    @Id
    @SequenceGenerator(name = "mdt_catalog_seq", sequenceName = "mdt_catalog_seq", allocationSize = 1)
    @GeneratedValue(generator = "mdt_catalog_seq", strategy = GenerationType.SEQUENCE)
    private long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MDT_NODE_ID")
    private MDTNode catalogNode;

    @JsonIgnore
    @OneToMany
    @JoinColumn(name = "CATALOG_ID", referencedColumnName = "ID")
    private List<MDTCatalogColumn> catalogColumns;

    @Column(name = "ABBREVIATION")
    private String abbreviation;

    @Column(name = "REFERENCE_NUMBER")
    private String referenceNumber;

    @Column(name = "SOURCE")
    private String source;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATION_TIME")
    private Date createdAt;

    @Column(name = "MODIFICATION_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedAt;

    @Column(name = "ATTACHMENT_NAME")
    private String attachmentName;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "ATTACHMENT")
    private byte[] attachment;

    @Column(name = "ANCESTOR_CATALOG_INFO")
    private String ancestorCatalogInfo;

    @ManyToOne
    @JoinColumn(name = "IN_LAW_DOCUMENT_ID")
    private LegislativeDocument legislativeDocument;

    @Column(name = "VALID_TO")
    private Date validTo;

    @Column(name = "CODE")
    private String code;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public MDTNode getCatalogNode() {
        return catalogNode;
    }

    public void setCatalogNode(MDTNode catalogNode) {
        this.catalogNode = catalogNode;
    }

    public List<MDTCatalogColumn> getCatalogColumns() {
        return catalogColumns;
    }

    public void setCatalogColumns(List<MDTCatalogColumn> catalogColumns) {
        this.catalogColumns = catalogColumns;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getAttachmentName() {
        return attachmentName;
    }

    public void setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
    }

    public byte[] getAttachment() {
        return attachment;
    }

    public void setAttachment(byte[] attachment) {
        this.attachment = attachment;
    }

    public String getAncestorCatalogInfo() {
        return ancestorCatalogInfo;
    }

    public void setAncestorCatalogInfo(String ancestorCatalogInfo) {
        this.ancestorCatalogInfo = ancestorCatalogInfo;
    }

    public LegislativeDocument getLegislativeDocument() {
        return legislativeDocument;
    }

    public void setLegislativeDocument(LegislativeDocument legislativeDocument) {
        this.legislativeDocument = legislativeDocument;
    }

    public Date getValidTo() {
        return validTo;
    }

    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
