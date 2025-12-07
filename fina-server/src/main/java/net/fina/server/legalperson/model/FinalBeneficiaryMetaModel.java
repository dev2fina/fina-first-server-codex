package net.fina.server.legalperson.model;

import net.fina.server.person.model.PersonMetaModel;

public class FinalBeneficiaryMetaModel {
    private long id;
    private PersonMetaModel person;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public PersonMetaModel getPerson() {
        return person;
    }

    public void setPerson(PersonMetaModel person) {
        this.person = person;
    }
}
