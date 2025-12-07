package net.fina.common.server.fi.sync.model;

import java.util.Date;
import java.util.List;

public class FiBranchSyncMetaModel {

    private String regionName;
    private List<FiDescriptionSyncMetaModel> nameDescriptions;
    private List<FiDescriptionSyncMetaModel> shortNameDescription;
    private List<FiDescriptionSyncMetaModel> addressDescriptions;
    private String status;
    private Date registrationDate;
    private Date legalActDate;

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public List<FiDescriptionSyncMetaModel> getNameDescriptions() {
        return nameDescriptions;
    }

    public void setNameDescriptions(List<FiDescriptionSyncMetaModel> nameDescriptions) {
        this.nameDescriptions = nameDescriptions;
    }

    public List<FiDescriptionSyncMetaModel> getShortNameDescription() {
        return shortNameDescription;
    }

    public void setShortNameDescription(List<FiDescriptionSyncMetaModel> shortNameDescription) {
        this.shortNameDescription = shortNameDescription;
    }

    public List<FiDescriptionSyncMetaModel> getAddressDescriptions() {
        return addressDescriptions;
    }

    public void setAddressDescriptions(List<FiDescriptionSyncMetaModel> addressDescriptions) {
        this.addressDescriptions = addressDescriptions;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Date getLegalActDate() {
        return legalActDate;
    }

    public void setLegalActDate(Date legalActDate) {
        this.legalActDate = legalActDate;
    }
}
