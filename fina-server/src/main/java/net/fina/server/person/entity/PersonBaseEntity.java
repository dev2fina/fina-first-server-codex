package net.fina.server.person.entity;

import jakarta.persistence.*;
import net.fina.server.fi.entity.Region;
import net.fina.server.i18n.helper.Description;
import net.fina.server.person.model.PersonStatus;
import org.hibernate.annotations.Type;

@MappedSuperclass
public class PersonBaseEntity {

    @Column(name = "NAMESTRID")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    protected Description name;

    @Column(name = "IDENTIFICATION_NUMBER", unique = true)
    protected String identificationNumber;


    @OneToOne
    @JoinColumn(name = "REGION_ID")
    protected Region country;

    @Column(name = "DELETED")
    protected boolean deleted;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "STATUS")
    protected PersonStatus status = PersonStatus.INACTIVE;


    public Description getName() {
        return name;
    }

    public void setName(Description name) {
        this.name = name;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public void setIdentificationNumber(String identificationNumber) {
        this.identificationNumber = identificationNumber;
    }

    public Region getCountry() {
        return country;
    }

    public void setCountry(Region country) {
        this.country = country;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public PersonStatus getStatus() {
        return status;
    }

    public void setStatus(PersonStatus status) {
        this.status = status;
    }
}
