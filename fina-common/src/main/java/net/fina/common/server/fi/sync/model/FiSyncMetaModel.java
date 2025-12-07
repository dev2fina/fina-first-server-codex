package net.fina.common.server.fi.sync.model;

import net.fina.common.client.fis.LicenceStatus;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class FiSyncMetaModel implements Serializable {

    private String code;
    private List<FiDescriptionSyncMetaModel> descriptions;
    private List<FiDescriptionSyncMetaModel> addressDescriptions;
    private String phone;
    private String fax;
    private String email;
    private String swiftCode;
    private String regionName;
    private String identificationCode;
    private String legalForm;
    private String fiTypeCode;
    private LicenceStatus status;
    private Date registrationDate;
    private Date legalActDate;
    private List<FiBranchSyncMetaModel> branches;
    private List<FiManagementSyncMetaModel> management;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<FiDescriptionSyncMetaModel> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(List<FiDescriptionSyncMetaModel> descriptions) {
        this.descriptions = descriptions;
    }

    public List<FiDescriptionSyncMetaModel> getAddressDescriptions() {
        return addressDescriptions;
    }

    public void setAddressDescriptions(List<FiDescriptionSyncMetaModel> addressDescriptions) {
        this.addressDescriptions = addressDescriptions;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSwiftCode() {
        return swiftCode;
    }

    public void setSwiftCode(String swiftCode) {
        this.swiftCode = swiftCode;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getIdentificationCode() {
        return identificationCode;
    }

    public void setIdentificationCode(String identificationCode) {
        this.identificationCode = identificationCode;
    }

    public String getLegalForm() {
        return legalForm;
    }

    public void setLegalForm(String legalForm) {
        this.legalForm = legalForm;
    }

    public String getFiTypeCode() {
        return fiTypeCode;
    }

    public void setFiTypeCode(String fiTypeCode) {
        this.fiTypeCode = fiTypeCode;
    }

    public LicenceStatus getStatus() {
        return status;
    }

    public void setStatus(LicenceStatus status) {
        this.status = status;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public List<FiBranchSyncMetaModel> getBranches() {
        return branches;
    }

    public void setBranches(List<FiBranchSyncMetaModel> branches) {
        this.branches = branches;
    }

    public List<FiManagementSyncMetaModel> getManagement() {
        return management;
    }

    public void setManagement(List<FiManagementSyncMetaModel> management) {
        this.management = management;
    }

    public Date getLegalActDate() {
        return legalActDate;
    }

    public void setLegalActDate(Date legalActDate) {
        this.legalActDate = legalActDate;
    }
}
