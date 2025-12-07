package net.fina.server.legalperson.entity;

import net.fina.server.i18n.helper.Description;
import net.fina.server.legalperson.entity.metainfo.LegalPersonMetaInfo;
import net.fina.server.person.entity.CriminalRecord;
import net.fina.server.person.entity.PersonBaseEntity;
import net.fina.server.person.entity.PersonPosition;
import net.fina.server.person.model.LegalPersonResidentStatus;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity(name = "IN_LEGAL_PERSONS")
@Table(name = "IN_LEGAL_PERSONS")
public class LegalPerson extends PersonBaseEntity {
    @Id
    @SequenceGenerator(name = "legal_persons_sequence", sequenceName = "legal_persons_sequence", allocationSize = 1)
    @GeneratedValue(generator = "legal_persons_sequence", strategy = GenerationType.SEQUENCE)
    private long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "META_INFO_ID")
    private LegalPersonMetaInfo metaInfo;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "CONTACT_INFO_ID")
    private LegalPersonContactInfo contactInfo;

    @Column(name = "FI_ID")
    private long fiId;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "IN_LEGAL_PERSONS_CRIMINAL_RECORDS", joinColumns = @JoinColumn(name = "LEGAL_PERSON_ID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "CRIMINAL_RECORD_ID", referencedColumnName = "ID"))
    private List<CriminalRecord> criminalRecords;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinTable(name = "IN_LEGAL_PERSON_BENEFICIARIES_TABLE", joinColumns = @JoinColumn(name = "LEGAL_PERSON_ID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "BENEFICIARY_ID", referencedColumnName = "ID"))
    private List<Beneficiary> beneficiaries;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "LEGAL_PERSON_ID")
    private List<PersonPosition> positions;

    @Column(name = "REGISTRATION_NUMBER")
    private String registrationNumber;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "RESIDENT_STATUS")
    private LegalPersonResidentStatus residentStatus;

    @Transient
    private List<FiLegalPersonConnection> connections;

    @Transient
    private long fiLegalPersonId;

    public LegalPerson() {
    }


    public LegalPerson(long id, String identificationNumber, Description name, long fiId) {
        this.id = id;
        this.identificationNumber = identificationNumber;
        this.name = name;
        this.fiId = fiId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getFiId() {
        return fiId;
    }

    public void setFiId(long fiIdl) {
        this.fiId = fiIdl;
    }

    public LegalPersonMetaInfo getMetaInfo() {
        return metaInfo;
    }

    public void setMetaInfo(LegalPersonMetaInfo metaInfo) {
        this.metaInfo = metaInfo;
    }

    public LegalPersonContactInfo getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(LegalPersonContactInfo contactInfo) {
        this.contactInfo = contactInfo;
    }

    public List<CriminalRecord> getCriminalRecords() {
        return criminalRecords == null ? new ArrayList<>() : criminalRecords;
    }

    public void setCriminalRecords(List<CriminalRecord> criminalRecords) {
        this.criminalRecords = criminalRecords;
    }

    public List<Beneficiary> getBeneficiaries() {
        if (beneficiaries == null) {
            beneficiaries = new ArrayList<>();
        }
        return beneficiaries;
    }

    public void setBeneficiaries(List<Beneficiary> beneficiaries) {
        this.beneficiaries = beneficiaries;
    }

    public List<PersonPosition> getPositions() {
        if (positions == null) {
            positions = new ArrayList<>();
        }
        return positions;
    }

    public void setPositions(List<PersonPosition> positions) {
        this.positions = positions;
    }

    public long getFiLegalPersonId() {
        return fiLegalPersonId;
    }

    public void setFiLegalPersonId(long fiLegalPersonId) {
        this.fiLegalPersonId = fiLegalPersonId;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public List<FiLegalPersonConnection> getConnections() {
        if (connections == null) {
            connections = new ArrayList<>();
        }
        return connections;
    }

    public LegalPersonResidentStatus getResidentStatus() {
        return residentStatus;
    }

    public void setResidentStatus(LegalPersonResidentStatus personResidentStatus) {
        this.residentStatus = personResidentStatus;
    }

    public void setConnections(List<FiLegalPersonConnection> connections) {
        this.connections = connections;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LegalPerson that = (LegalPerson) o;
        return getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "LegalPerson{" +
                "id=" + id +
                ", name=" + name +
                ", identificationNumber='" + identificationNumber + '\'' +
                '}';
    }
}
