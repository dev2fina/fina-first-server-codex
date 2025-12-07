package net.fina.server.legalperson.entity;

import net.fina.server.fi.entity.Fi;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "IN_BANK_LEGAL_PERSONS")
@Table(name = "IN_BANK_LEGAL_PERSONS")
public class FiLegalPerson {
    @Id
    @SequenceGenerator(name = "bank_legal_person_sequence", sequenceName = "bank_legal_person_sequence", allocationSize = 1)
    @GeneratedValue(generator = "bank_legal_person_sequence", strategy = GenerationType.SEQUENCE)
    private long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FI_ID", referencedColumnName = "ID")
    private Fi fi;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LEGAL_PERSON_ID", referencedColumnName = "ID")
    private LegalPerson legalPerson;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "LEGAL_PERSON_ID")
    private List<FiLegalPersonConnection> connections;

    public FiLegalPerson() {
    }

    public FiLegalPerson(Fi fi, LegalPerson legalPerson) {
        this.fi = fi;
        this.legalPerson = legalPerson;
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

    public LegalPerson getLegalPerson() {
        return legalPerson;
    }

    public void setLegalPerson(LegalPerson legalPerson) {
        this.legalPerson = legalPerson;
    }

    public List<FiLegalPersonConnection> getConnections() {
        return connections == null ? new ArrayList<>() : connections;
    }

    public void setConnections(List<FiLegalPersonConnection> connections) {
        this.connections = connections;
    }
}
