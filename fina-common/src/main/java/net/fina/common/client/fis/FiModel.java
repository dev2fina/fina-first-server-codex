package net.fina.common.client.fis;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.fina.common.shared.i18n.DescriptionMetaModel;
import net.fina.common.shared.legalperson.metainfo.LegalPersonMetaInfoModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FiModel implements Serializable {

    private long id;
    private int version;
    private String code;

    // descriptions
    private long nameStrId;
    private String name;
    private long shortNameStrId;
    private String shortNameString;
    private long addressStrId;
    private String addressString;

    private String phone;
    private String fax;
    private String email;
    private String swiftCode;
    private Long regionId;
    private RegionModel region;
    private String identificationCode;
    private String legalForm;

    private FiTypeModel fiTypeModel;
    private List<FiGroupModel> fiGroupModels;
    private List<String> userLoginModels;

    //--
    private List<Long> permittedUserIds;

    private boolean hasFiModel;
    private boolean halfChecked;

    private int level;

    private boolean disable;
    private boolean isRoleFi;
    private DescriptionMetaModel descriptionModel;
    private String contactPerson;
    private Date createdAt;
    private Date modifiedAt;
    private Date registrationDate;
    private Date closeDate;
    private int numberOfMobileOffices;

    private String LicenseCode;
    private FiReorganization reorganization;
    private String representativePerson;
    private String webSite;
    private int numberOfEmploys;

    private List<FiBranchTypeCounterModel> branchTypeCounterList;

    private LegalPersonMetaInfoModel additionalInfo;

    private String decreeNumber;
    private Date inspectionEndDate;


    public FiModel(long id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    public FiModel() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public String getShortNameString() {
        return shortNameString;
    }

    public void setShortNameString(String shortNameString) {
        this.shortNameString = shortNameString;
    }

    public long getAddressStrId() {
        return addressStrId;
    }

    public void setAddressStrId(long addressStrId) {
        this.addressStrId = addressStrId;
    }

    public String getAddressString() {
        return addressString;
    }

    public void setAddressString(String addressString) {
        this.addressString = addressString;
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

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public FiTypeModel getFiTypeModel() {
        return fiTypeModel;
    }

    public void setFiTypeModel(FiTypeModel fiTypeModel) {
        this.fiTypeModel = fiTypeModel;
    }

    public List<FiGroupModel> getFiGroupModels() {
        return fiGroupModels;
    }

    public void setFiGroupModels(List<FiGroupModel> fiGroupModels) {
        this.fiGroupModels = fiGroupModels;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isHasFiModel() {
        return hasFiModel;
    }

    public void setHasFiModel(boolean hasFiModel) {
        this.hasFiModel = hasFiModel;
    }

    public boolean isHalfChecked() {
        return halfChecked;
    }

    public void setHalfChecked(boolean halfChecked) {
        this.halfChecked = halfChecked;
    }

    public List<Long> getPermittedUserIds() {
        return permittedUserIds;
    }

    public void setPermittedUserIds(List<Long> permittedUserIds) {
        this.permittedUserIds = permittedUserIds;
    }

    public List<String> getUserLoginModels() {
        return userLoginModels;
    }

    public void setUserLoginModels(List<String> userLoginModels) {
        this.userLoginModels = userLoginModels;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public String getLicenseCode() {
        return LicenseCode;
    }

    public void setLicenseCode(String licenseCode) {
        LicenseCode = licenseCode;
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

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    public boolean isRoleFi() {
        return isRoleFi;
    }

    public void setRoleFi(boolean roleFi) {
        isRoleFi = roleFi;
    }

    public DescriptionMetaModel getDescriptionModel() {
        return descriptionModel;
    }

    public void setDescriptionModel(DescriptionMetaModel descriptionModel) {
        this.descriptionModel = descriptionModel;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Date getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(Date closeDate) {
        this.closeDate = closeDate;
    }

    public int getNumberOfMobileOffices() {
        return numberOfMobileOffices;
    }

    public void setNumberOfMobileOffices(int numberOfMobileOffices) {
        this.numberOfMobileOffices = numberOfMobileOffices;
    }

    public FiReorganization getReorganization() {
        return reorganization;
    }

    public void setReorganization(FiReorganization reorganization) {
        this.reorganization = reorganization;
    }

    public String getRepresentativePerson() {
        return representativePerson;
    }

    public void setRepresentativePerson(String representativePerson) {
        this.representativePerson = representativePerson;
    }

    public List<FiBranchTypeCounterModel> getBranchTypeCounterList() {
        return branchTypeCounterList;
    }

    public void setBranchTypeCounterList(List<FiBranchTypeCounterModel> branchTypeCounterList) {
        this.branchTypeCounterList = branchTypeCounterList;
    }

    public String getWebSite() {
        return webSite;
    }

    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }

    public int getNumberOfEmploys() {
        return numberOfEmploys;
    }

    public void setNumberOfEmploys(int numberOfEmploys) {
        this.numberOfEmploys = numberOfEmploys;
    }

    public LegalPersonMetaInfoModel getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(LegalPersonMetaInfoModel additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public RegionModel getRegion() {
        return region;
    }

    public void setRegion(RegionModel region) {
        this.region = region;
    }

    public String getDecreeNumber() {
        return decreeNumber;
    }

    public void setDecreeNumber(String decreeNumber) {
        this.decreeNumber = decreeNumber;
    }

    public Date getInspectionEndDate() {
        return inspectionEndDate;
    }

    public void setInspectionEndDate(Date inspectionEndDate) {
        this.inspectionEndDate = inspectionEndDate;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + level;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FiModel other = (FiModel) obj;
        if (id != other.id)
            return false;
        if (level != other.level)
            return false;
        return true;
    }
}
