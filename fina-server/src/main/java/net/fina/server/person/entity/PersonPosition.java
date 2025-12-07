package net.fina.server.person.entity;

import net.fina.server.i18n.helper.Description;
import net.fina.server.legalperson.entity.LegalPerson;
import org.hibernate.annotations.Type;

import jakarta.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity(name = "IN_PERSON_POSITIONS")
@Table(name = "IN_PERSON_POSITIONS")
public class PersonPosition {
    @Id
    @SequenceGenerator(name = "person_position_sequence", sequenceName = "person_position_sequence", allocationSize = 1)
    @GeneratedValue(generator = "person_position_sequence", strategy = GenerationType.SEQUENCE)
    private long id;

    @OneToOne
    @JoinColumn(name = "LEGAL_PERSON_ID")
    private LegalPerson company;

    @ManyToOne
    @JoinColumn(name = "PERSON_ID")
    private Person person;

    @Column(name = "POSITION_STR_ID")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    private Description position;

    @Column(name = "ELECTION_DATE")
    private Date electionDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LegalPerson getCompany() {
        return company;
    }

    public void setCompany(LegalPerson company) {
        this.company = company;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Description getPosition() {
        return position;
    }

    public void setPosition(Description position) {
        this.position = position;
    }

    public Date getElectionDate() {
        return electionDate;
    }

    public void setElectionDate(Date electionDate) {
        this.electionDate = electionDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonPosition position = (PersonPosition) o;
        return getId() == position.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
