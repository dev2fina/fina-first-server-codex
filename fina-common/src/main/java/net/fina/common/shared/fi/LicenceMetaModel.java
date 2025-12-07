package net.fina.common.shared.fi;

import net.fina.common.client.fis.FiModel;
import net.fina.common.client.fis.LicenceStatus;
import net.fina.common.shared.i18n.DescriptionMetaModel;

import java.io.Serializable;
import java.util.Date;

public class LicenceMetaModel implements Serializable {
    private long id;
    private Integer version;
    private String code;
    private Date creationDate;
    private Date dateOfChange;
    private DescriptionMetaModel reasons;
    private LicenceStatus licenceStatus;
    private LicenceTypeMetaModel licenseType;
    private FiModel fi;
    private Boolean isDefault;

    private transient String change;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getDateOfChange() {
        return dateOfChange;
    }

    public void setDateOfChange(Date dateOfChange) {
        this.dateOfChange = dateOfChange;
    }

    public DescriptionMetaModel getReasons() {
        return reasons;
    }

    public void setReasons(DescriptionMetaModel reasons) {
        this.reasons = reasons;
    }

    public LicenceStatus getLicenceStatus() {
        return licenceStatus;
    }

    public void setLicenceStatus(LicenceStatus licenceStatus) {
        this.licenceStatus = licenceStatus;
    }

    public LicenceTypeMetaModel getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(LicenceTypeMetaModel licenseType) {
        this.licenseType = licenseType;
    }

    public FiModel getFi() {
        return fi;
    }

    public void setFi(FiModel fi) {
        this.fi = fi;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    @Override
    public String toString() {
        return "License [id=" + id + ", version=" + version + ", code=" + code + "]";
    }
}
