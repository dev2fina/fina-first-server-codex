package net.fina.server.legalperson.entity;

import net.fina.server.person.entity.Person;

import jakarta.persistence.*;

//TODO fix envers FQN bug in NOT_AUDITED entities
@Entity(name = "net.fina.server.legalperson.entity.FinalBeneficiary")
@Table(name = "IN_FINAL_BENEFICIARIES")
public class FinalBeneficiary {
    @Id
    @SequenceGenerator(name = "final_beneficiary_sequence", sequenceName = "final_beneficiary_sequence", allocationSize = 1)
    @GeneratedValue(generator = "final_beneficiary_sequence", strategy = GenerationType.SEQUENCE)
    private long id;

    @OneToOne
    @JoinColumn(name = "PERSON_ID")
    private Person person;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
