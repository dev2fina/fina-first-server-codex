package net.fina.server.person.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.fina.server.legalperson.model.LegalPersonMetaModel;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonPositionMetaModel {
    private long id;
    private LegalPersonMetaModel company;
    private PersonMetaModel person;
    private String position;
    private long positionStrId;
    private Date electionDate;

    public PersonPositionMetaModel() {
    }

    public PersonPositionMetaModel(long id, String position, Date electionDate) {
        this.id = id;
        this.position = position;
        this.electionDate = electionDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LegalPersonMetaModel getCompany() {
        return company;
    }

    public void setCompany(LegalPersonMetaModel company) {
        this.company = company;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public long getPositionStrId() {
        return positionStrId;
    }

    public void setPositionStrId(long positionStrId) {
        this.positionStrId = positionStrId;
    }

    public Date getElectionDate() {
        return electionDate;
    }

    public void setElectionDate(Date electionDate) {
        this.electionDate = electionDate;
    }

    public PersonMetaModel getPerson() {
        return person;
    }

    public void setPerson(PersonMetaModel person) {
        this.person = person;
    }
}
