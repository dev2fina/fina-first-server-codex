package net.fina.server.fi.entity;

import net.fina.server.person.entity.Person;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "IN_BANK_PERSONS")
@Table(name = "IN_BANK_PERSONS")
public class FiPerson {
    @Id
    @SequenceGenerator(name = "bank_person_sequence", sequenceName = "bank_person_sequence", allocationSize = 1)
    @GeneratedValue(generator = "bank_person_sequence", strategy = GenerationType.SEQUENCE)
    private long id;

    @OneToOne
    @JoinColumn(name = "FI_ID", referencedColumnName = "ID")
    private Fi fi;

    @OneToOne
    @JoinColumn(name = "PERSON_ID", referencedColumnName = "ID")
    private Person person;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "FI_PERSON_ID", referencedColumnName = "ID")
    private List<FiPersonConnection> connections;

    public FiPerson() {
    }

    public FiPerson(long id, Fi fi, Person person) {
        this.id = id;
        this.fi = fi;
        this.person = person;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Fi getFi() {
        return fi;
    }

    public void setFi(Fi fi) {
        this.fi = fi;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public List<FiPersonConnection> getConnections() {
        return connections == null ? new ArrayList<>() : connections;
    }

    public void setConnections(List<FiPersonConnection> connections) {
        this.connections = connections;
    }
}
