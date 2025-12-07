package net.fina.server.cems.entity;

import jakarta.persistence.*;

@Entity(name = "CEMS_RESPONSIBLE_PERSONS")
@Table(name = "CEMS_RESPONSIBLE_PERSONS")
public class CEMSResponsiblePerson {

    @Id
    @SequenceGenerator(name = "cems_responsible_persons_sequence", sequenceName = "cems_responsible_persons_sequence", allocationSize = 1)
    @GeneratedValue(generator = "cems_responsible_persons_sequence", strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(name = "FULL_NAME")
    private String fullName;
    @Column(name = "POSITION")
    private String position;

    public CEMSResponsiblePerson() {
    }

    public CEMSResponsiblePerson(long id, String fullName, String position) {
        this.id = id;
        this.fullName = fullName;
        this.position = position;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
