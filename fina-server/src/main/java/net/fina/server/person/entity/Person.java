package net.fina.server.person.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fina.server.fi.entity.FiPersonConnection;
import net.fina.server.fi.entity.Region;
import net.fina.server.i18n.helper.Description;
import net.fina.server.person.model.PersonResidentStatus;
import net.fina.server.person.model.PersonStatus;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity(name = "IN_PERSONS")
@Table(name = "IN_PERSONS")
public class Person extends PersonBaseEntity {
    @Id
    @SequenceGenerator(name = "persons_sequence", sequenceName = "persons_sequence", allocationSize = 1)
    @GeneratedValue(generator = "persons_sequence", strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(name = "PASSPORT_NUMBER")
    private String passportNumber;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "PERSON_ID")
    private List<PersonEducation> education;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "PERSON_ID")
    private List<Recommendation> recommendations;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "IN_PERSONS_CRIMINAL_RECORDS", joinColumns = @JoinColumn(name = "PERSON_ID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "CRIMINAL_RECORD_ID", referencedColumnName = "ID"))
    private List<CriminalRecord> criminalRecords;

    @OneToMany(mappedBy = "person")
//    @JoinColumn(name = "PERSON_ID")
    @JsonIgnore
    private List<PersonPosition> positions;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "RESIDENT_STATUS")
    private PersonResidentStatus residentStatus;

    @Transient
    private long fiPersonId;
    @Transient
    private List<FiPersonConnection> fiPersonConnections;

    public Person() {
    }

    public Person(long id) {
        this.id = id;
    }

    public Person(long id, Region region) {
        this.id = id;
        this.country = region;
    }


    public Person(long id, Description name, String identificationNumber, String passportNumber, PersonResidentStatus personResidentStatus, Region citizenship, PersonStatus status) {
        this.id = id;
        this.name = name;
        this.identificationNumber = identificationNumber;
        this.passportNumber = passportNumber;
        this.residentStatus = personResidentStatus;
        this.country = citizenship;
        this.status = status;
    }

    public Person(long id, Description name, String identificationNumber, String passportNumber, PersonResidentStatus personResidentStatus, Region citizenship, PersonStatus status, long fiPersonId) {
        this.id = id;
        this.name = name;
        this.identificationNumber = identificationNumber;
        this.passportNumber = passportNumber;
        this.residentStatus = personResidentStatus;
        this.country = citizenship;
        this.status = status;
        this.fiPersonId = fiPersonId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public Region getCitizenship() {
        return country;
    }

    public void setCitizenship(Region citizenship) {
        this.country = citizenship;
    }

    public List<PersonEducation> getEducation() {
        return education == null ? new ArrayList<>() : education;
    }

    public void setEducation(List<PersonEducation> education) {
        this.education = education;
    }

    public List<Recommendation> getRecommendations() {
        return recommendations == null ? new ArrayList<>() : recommendations;
    }

    public void setRecommendations(List<Recommendation> recommendations) {
        this.recommendations = recommendations;
    }

    public List<CriminalRecord> getCriminalRecords() {
        return criminalRecords == null ? new ArrayList<>() : criminalRecords;
    }

    public void setCriminalRecords(List<CriminalRecord> criminalRecords) {
        this.criminalRecords = criminalRecords;
    }

    public long getFiPersonId() {
        return fiPersonId;
    }

    public void setFiPersonId(long fiPersonId) {
        this.fiPersonId = fiPersonId;
    }

    public List<PersonPosition> getPositions() {
        return positions == null ? new ArrayList<>() : positions;
    }

    public void setPositions(List<PersonPosition> positions) {
        this.positions = positions;
    }

    public List<FiPersonConnection> getFiPersonConnections() {
        return fiPersonConnections == null ? new ArrayList<>() : fiPersonConnections;
    }

    public void setFiPersonConnections(List<FiPersonConnection> fiPersonConnections) {
        this.fiPersonConnections = fiPersonConnections;
    }

    public PersonResidentStatus getResidentStatus() {
        return residentStatus;
    }

    public void setResidentStatus(PersonResidentStatus personResidentStatus) {
        this.residentStatus = personResidentStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return getId() == person.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
