package net.fina.server.classifier.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MDTCatalogMetaModel implements Serializable {
    private long id;
    private String name;
    private long nameStrId;
    private String abbreviation;
    private String referenceNumber;
    private String source;
    private Date createdAt;
    private Date modifiedAt;
    private String mdtCode;
    private String attachmentName;
    private byte[] attachment;
    private String ancestorCatalogInfo;
    private long legislativeDocumentId;
    private String legislativeDocumentName;
    private Date validTo;
    private List<MDTCatalogColumnMetaModel> catalogColumns = new ArrayList<>();
    private String code;

    public MDTCatalogMetaModel() {
    }

    public MDTCatalogMetaModel(long id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getNameStrId() {
        return nameStrId;
    }

    public void setNameStrId(long nameStrId) {
        this.nameStrId = nameStrId;
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

    public List<MDTCatalogColumnMetaModel> getCatalogColumns() {
        return catalogColumns;
    }

    public void setCatalogColumns(List<MDTCatalogColumnMetaModel> catalogColumns) {
        this.catalogColumns = catalogColumns;
    }

    public byte[] getAttachment() {
        return attachment;
    }

    public void setAttachment(byte[] attachment) {
        this.attachment = attachment;
    }

    public String getAttachmentName() {
        return attachmentName;
    }

    public void setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
    }

    public String getAncestorCatalogInfo() {
        return ancestorCatalogInfo;
    }

    public void setAncestorCatalogInfo(String ancestorCatalogInfo) {
        this.ancestorCatalogInfo = ancestorCatalogInfo;
    }

    public long getLegislativeDocumentId() {
        return legislativeDocumentId;
    }

    public void setLegislativeDocumentId(long legislativeDocumentId) {
        this.legislativeDocumentId = legislativeDocumentId;
    }

    public String getLegislativeDocumentName() {
        return legislativeDocumentName;
    }

    public void setLegislativeDocumentName(String legislativeDocumentName) {
        this.legislativeDocumentName = legislativeDocumentName;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MDTCatalogMetaModel that = (MDTCatalogMetaModel) o;
        return getId() == that.getId() && getName().equals(that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName());
    }

    public String getMdtCode() {
        return mdtCode;
    }

    public void setMdtCode(String mdtCode) {
        this.mdtCode = mdtCode;
    }

    @Override
    public String toString() {
        return this.code + " - " + this.name;
    }
}
