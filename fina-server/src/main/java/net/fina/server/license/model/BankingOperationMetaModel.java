package net.fina.server.license.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BankingOperationMetaModel {
    protected long id;
    protected long parentId;
    protected String code;
    protected String description;
    protected long descriptionStrId;
    protected boolean nationalCurrency;
    protected boolean foreignCurrency;
    protected long licenseTypeId;

    private List<BankingOperationMetaModel> children;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isNationalCurrency() {
        return nationalCurrency;
    }

    public void setNationalCurrency(boolean nationalCurrency) {
        this.nationalCurrency = nationalCurrency;
    }

    public boolean isForeignCurrency() {
        return foreignCurrency;
    }

    public void setForeignCurrency(boolean foreignCurrency) {
        this.foreignCurrency = foreignCurrency;
    }

    public long getDescriptionStrId() {
        return descriptionStrId;
    }

    public void setDescriptionStrId(long descriptionStrId) {
        this.descriptionStrId = descriptionStrId;
    }

    public List<BankingOperationMetaModel> getChildren() {
        return children == null ? new ArrayList<>() : children;
    }

    public void setChildren(List<BankingOperationMetaModel> children) {
        this.children = children;
    }

    public long getLicenseTypeId() {
        return licenseTypeId;
    }

    public void setLicenseTypeId(long licenseTypeId) {
        this.licenseTypeId = licenseTypeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankingOperationMetaModel that = (BankingOperationMetaModel) o;
        return getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
