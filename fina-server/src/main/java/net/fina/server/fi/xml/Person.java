package net.fina.server.fi.xml;

import jakarta.xml.bind.annotation.*;
import net.fina.server.person.model.PersonResidentStatus;
import net.fina.server.person.model.PersonStatus;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")

public class Person {
    @XmlAttribute(required = true, name = "personIdNumber")
    protected String personIdNumber;
    @XmlAttribute(required = true, name = "countryCode")
    protected String countryCode;
    @XmlAttribute(required = true, name = "passportNumber")
    protected String passportNumber;
    @XmlAttribute(required = true, name = "status")
    protected PersonStatus activeStatus;
    @XmlAttribute(name = "legalStatus")
    protected PersonResidentStatus status;
    @XmlElement(name = "name")
    protected Descriptions name;
    @XmlElement(name = "positions")
    protected PersonPositions personPositions;
    public Person() {
    }

    public Person(net.fina.server.person.entity.Person person) {
        this.personIdNumber = person.getIdentificationNumber();
        if (person.getCountry() != null) {
            this.countryCode = person.getCountry().getCode();
        }
        this.status = person.getResidentStatus();
        this.activeStatus = person.getStatus();
        this.passportNumber = person.getPassportNumber();
    }

    public Person(String personIdNumber) {
        this.personIdNumber = personIdNumber;
    }

    public String getPersonIdNumber() {
        return personIdNumber;
    }

    public void setPersonIdNumber(String personIdNumber) {
        this.personIdNumber = personIdNumber;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Descriptions getName() {
        return name;
    }

    public void setName(Descriptions name) {
        this.name = name;
    }

    public PersonResidentStatus getStatus() {
        return status;
    }

    public void setStatus(PersonResidentStatus status) {
        this.status = status;
    }


    public PersonPositions getPersonPositions() {
        if (personPositions == null) {
            personPositions = new PersonPositions();
        }
        return personPositions;
    }

    public PersonStatus getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(PersonStatus activeStatus) {
        this.activeStatus = activeStatus;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public void setPersonPositions(PersonPositions personPositions) {
        this.personPositions = personPositions;
    }
}
