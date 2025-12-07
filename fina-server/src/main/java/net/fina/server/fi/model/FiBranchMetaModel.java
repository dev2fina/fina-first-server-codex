package net.fina.server.fi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.fina.server.person.model.PersonMetaModel;
import net.fina.server.util.UIConfigurationAttribute;

import java.util.Date;

public class FiBranchMetaModel {
    private long id;

    @UIConfigurationAttribute
    private Date createDate;

    @UIConfigurationAttribute
    private Date changeDate;
    private long bankId;
    private int version;
    private long nameStrId;

    @UIConfigurationAttribute
    private String name;
    private long shortNameStrId;

    @UIConfigurationAttribute
    private String shortName;
    private long addressStrId;

    @UIConfigurationAttribute
    private String address;
    private long commentStrId;

    @UIConfigurationAttribute
    private String comment;

    @UIConfigurationAttribute
    private boolean disable;

    @UIConfigurationAttribute
    private Date closeDate;

    @UIConfigurationAttribute
    private Date suspensionDate;

    @UIConfigurationAttribute
    private Date renewalDate;

    @UIConfigurationAttribute
    private String email;

    @UIConfigurationAttribute
    private String phone;

    @UIConfigurationAttribute
    private String registrationNumber;

    @UIConfigurationAttribute
    @JsonProperty("isStorageAvailable")
    private Boolean isStorageAvailable;

    @UIConfigurationAttribute
    private String code;

    @UIConfigurationAttribute
    private RegionMetaModel regionModel;
    private long region;
    private long fiBranchTypeId;
    private String fiBranchTypeName;
    @UIConfigurationAttribute
    private PersonMetaModel manager;

    @UIConfigurationAttribute
    private Date managerAppointmentDate;

    @UIConfigurationAttribute
    private PersonMetaModel chiefAccountant;

    @UIConfigurationAttribute
    private Date chiefAccountantAppointmentDate;
    private boolean deleted;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }

    public long getBankId() {
        return bankId;
    }

    public void setBankId(long bankId) {
        this.bankId = bankId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public long getNameStrId() {
        return nameStrId;
    }

    public void setNameStrId(long nameStrId) {
        this.nameStrId = nameStrId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getShortNameStrId() {
        return shortNameStrId;
    }

    public void setShortNameStrId(long shortNameStrId) {
        this.shortNameStrId = shortNameStrId;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public long getAddressStrId() {
        return addressStrId;
    }

    public void setAddressStrId(long addressStrId) {
        this.addressStrId = addressStrId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getCommentStrId() {
        return commentStrId;
    }

    public void setCommentStrId(long commentStrId) {
        this.commentStrId = commentStrId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    public RegionMetaModel getRegionModel() {
        return regionModel;
    }

    public void setRegionModel(RegionMetaModel regionModel) {
        this.regionModel = regionModel;
    }

    public Date getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(Date closeDate) {
        this.closeDate = closeDate;
    }

    public Date getSuspensionDate() {
        return suspensionDate;
    }

    public void setSuspensionDate(Date suspensionDate) {
        this.suspensionDate = suspensionDate;
    }

    public Date getRenewalDate() {
        return renewalDate;
    }

    public void setRenewalDate(Date renewalDate) {
        this.renewalDate = renewalDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public Boolean getIsStorageAvailable() {
        return isStorageAvailable;
    }

    public void setIsStorageAvailable(Boolean storageAvailable) {
        isStorageAvailable = storageAvailable;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getRegion() {
        return region;
    }

    public void setRegion(long region) {
        this.region = region;
    }

    public long getFiBranchTypeId() {
        return fiBranchTypeId;
    }

    public void setFiBranchTypeId(long fiBranchTypeId) {
        this.fiBranchTypeId = fiBranchTypeId;
    }

    public Boolean getStorageAvailable() {
        return isStorageAvailable;
    }

    public void setStorageAvailable(Boolean storageAvailable) {
        isStorageAvailable = storageAvailable;
    }

    public PersonMetaModel getManager() {
        return manager;
    }

    public void setManager(PersonMetaModel manager) {
        this.manager = manager;
    }

    public Date getManagerAppointmentDate() {
        return managerAppointmentDate;
    }

    public void setManagerAppointmentDate(Date managerAppointmentDate) {
        this.managerAppointmentDate = managerAppointmentDate;
    }

    public PersonMetaModel getChiefAccountant() {
        return chiefAccountant;
    }

    public void setChiefAccountant(PersonMetaModel chiefAccountant) {
        this.chiefAccountant = chiefAccountant;
    }

    public Date getChiefAccountantAppointmentDate() {
        return chiefAccountantAppointmentDate;
    }

    public void setChiefAccountantAppointmentDate(Date chiefAccountantAppointmentDate) {
        this.chiefAccountantAppointmentDate = chiefAccountantAppointmentDate;
    }

    public String getFiBranchTypeName() {
        return fiBranchTypeName;
    }

    public void setFiBranchTypeName(String fiBranchTypeName) {
        this.fiBranchTypeName = fiBranchTypeName;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
