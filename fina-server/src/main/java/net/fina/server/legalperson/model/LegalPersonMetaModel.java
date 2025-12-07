package net.fina.server.legalperson.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.fina.common.client.fis.FiTypeSimpleModel;
import net.fina.common.shared.legalperson.metainfo.LegalPersonMetaInfoModel;
import net.fina.server.fi.model.RegionMetaModel;
import net.fina.server.legalperson.entity.FiLegalPersonConnectionType;
import net.fina.server.person.model.*;

import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LegalPersonMetaModel {
    protected String identificationNumber;
    protected LegalPersonResidentStatus personResidentStatus;
    protected RegionMetaModel country;
    private long id;
    private String name;
    private long nameStrId;
    private LegalPersonMetaInfoModel metaInfo;
    private LegalPersonContactInfoMetaModel contactInfo;
    private List<CriminalRecordMetaModel> criminalRecords;
    private List<BeneficiaryMetaModel> beneficiaries;
    private List<ShareMetaModel> shares;
    private List<PersonPositionMetaModel> managers;
    private String registrationNumber;

    private boolean bank;
    private long fiId;
    private long fiLegalPersonId;

    private Set<FiLegalPersonConnectionType> connectionTypes;

    private PersonStatus status;

    private FiTypeSimpleModel fiType;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public LegalPersonContactInfoMetaModel getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(LegalPersonContactInfoMetaModel contactInfo) {
        this.contactInfo = contactInfo;
    }

    public List<CriminalRecordMetaModel> getCriminalRecords() {
        return criminalRecords == null ? new ArrayList<>() : criminalRecords;
    }

    public void setCriminalRecords(List<CriminalRecordMetaModel> criminalRecords) {
        this.criminalRecords = criminalRecords;
    }

    public List<BeneficiaryMetaModel> getBeneficiaries() {
        return beneficiaries == null ? new ArrayList<>() : beneficiaries;
    }

    public void setBeneficiaries(List<BeneficiaryMetaModel> beneficiaries) {
        this.beneficiaries = beneficiaries;
    }

    public List<ShareMetaModel> getShares() {
        return shares == null ? new ArrayList<>() : shares;
    }

    public void setShares(List<ShareMetaModel> shares) {
        this.shares = shares;
    }

    public LegalPersonMetaInfoModel getMetaInfo() {
        return metaInfo;
    }

    public void setMetaInfo(LegalPersonMetaInfoModel metaInfo) {
        this.metaInfo = metaInfo;
    }

    public boolean isBank() {
        return bank;
    }

    public void setBank(boolean bank) {
        this.bank = bank;
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

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public void setIdentificationNumber(String identificationNumber) {
        this.identificationNumber = identificationNumber;
    }

    public LegalPersonResidentStatus getResidentStatus() {
        return personResidentStatus;
    }

    public void setResidentStatus(LegalPersonResidentStatus personResidentStatus) {
        this.personResidentStatus = personResidentStatus;
    }

    public RegionMetaModel getCountry() {
        return country;
    }

    public void setCountry(RegionMetaModel country) {
        this.country = country;
    }

    public List<PersonPositionMetaModel> getManagers() {
        return managers == null ? new ArrayList<>() : managers;
    }

    public void setManagers(List<PersonPositionMetaModel> managers) {
        this.managers = managers;
    }

    public long getFiLegalPersonId() {
        return fiLegalPersonId;
    }

    public void setFiLegalPersonId(long fiLegalPersonId) {
        this.fiLegalPersonId = fiLegalPersonId;
    }

    public long getFiId() {
        return fiId;
    }

    public void setFiId(long fiId) {
        this.fiId = fiId;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public Set<FiLegalPersonConnectionType> getConnectionTypes() {
        return connectionTypes == null ? new HashSet<>() : connectionTypes;
    }

    public void setConnectionTypes(Set<FiLegalPersonConnectionType> connectionTypes) {
        this.connectionTypes = connectionTypes;
    }

    public PersonStatus getStatus() {
        return status;
    }

    public void setStatus(PersonStatus status) {
        this.status = status;
    }

    public FiTypeSimpleModel getFiType() {
        return fiType;
    }

    public void setFiType(FiTypeSimpleModel fiType) {
        this.fiType = fiType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LegalPersonMetaModel that = (LegalPersonMetaModel) o;
        return getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
