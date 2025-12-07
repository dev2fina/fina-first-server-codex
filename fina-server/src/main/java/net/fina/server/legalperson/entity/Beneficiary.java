package net.fina.server.legalperson.entity;

import net.fina.server.fi.entity.Currency;
import net.fina.server.fi.entity.Region;
import net.fina.server.i18n.helper.Description;
import net.fina.server.person.entity.Person;
import net.fina.server.person.model.ShareMetaModel;
import org.hibernate.envers.AuditJoinTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity(name = "IN_BENEFICIARIES")
@Table(name = "IN_BENEFICIARIES")
@Audited
public class Beneficiary {
    @Id
    @SequenceGenerator(name = "beneficiaries_sequence", sequenceName = "beneficiaries_sequence", allocationSize = 1)
    @GeneratedValue(generator = "beneficiaries_sequence", strategy = GenerationType.SEQUENCE)
    private long id;

    @Min(0)
    @Max(100)
    @Column(name = "CAPITAL_SHARE")
    private double share;

    @Column(name = "NOMINAL")
    private double nominal;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATION_DATE")
    private Date creationDate;

    @Column(name = "ACTIVE")
    private boolean active;

    @Enumerated(EnumType.STRING)
    @Column(name = "CURRENCY")
    private Currency currency;
    @OneToOne
    @JoinColumn(name = "PHYSICAL_PERSON_ID")
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private Person physicalPerson;

    @OneToOne
    @JoinColumn(name = "LEGAL_PERSON_ID")
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private LegalPerson legalPerson;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "BENEFICIARY_ID")
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @AuditJoinTable(name = "IN_BENEFICIARIES_JOIN_TABLE_AUD")
    private List<FinalBeneficiary> finalBeneficiaries;

    public Beneficiary() {
    }

    public Beneficiary(long id, double share, Date creationDate, long legalPersonId, Description legalPersonName, String identificationNumber, long fiId) {
        this.id = id;
        this.share = share;
        this.creationDate = creationDate;
        LegalPerson lp = new LegalPerson();
        lp.setId(legalPersonId);
        lp.setName(legalPersonName);
        lp.setIdentificationNumber(identificationNumber);
        lp.setFiId(fiId);
        this.legalPerson = lp;
    }

    public Beneficiary(ShareMetaModel share) {
        this.id = share.getId();
        this.share = share.getSharePercentage();
        this.creationDate = share.getShareDate() == null ? new Date() : share.getShareDate();
        LegalPerson lp = new LegalPerson();
        lp.setId(share.getCompany().getId());
        lp.setIdentificationNumber(share.getCompany().getIdentificationNumber());
        lp.setFiId(share.getCompany().getFiId());
        this.legalPerson = lp;
    }

    public Beneficiary(long id, double share, Date creationDate, long legalPersonId, Description legalPersonName, String identificationNumber, long fiId, Region region) {
        this.id = id;
        this.share = share;
        this.creationDate = creationDate;
        LegalPerson lp = new LegalPerson();
        lp.setId(legalPersonId);
        lp.setName(legalPersonName);
        lp.setIdentificationNumber(identificationNumber);
        lp.setFiId(fiId);
        lp.setCountry(region);
        this.legalPerson = lp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getShare() {
        return share;
    }

    public void setShare(double capitalShare) {
        this.share = capitalShare;
    }

    public double getNominal() {
        return nominal;
    }

    public void setNominal(double nominal) {
        this.nominal = nominal;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Person getPhysicalPerson() {
        return physicalPerson;
    }

    public void setPhysicalPerson(Person physicalPerson) {
        this.physicalPerson = physicalPerson;
    }

    public LegalPerson getLegalPerson() {
        return legalPerson;
    }

    public void setLegalPerson(LegalPerson legalPerson) {
        this.legalPerson = legalPerson;
    }

    public List<FinalBeneficiary> getFinalBeneficiaries() {
        return finalBeneficiaries == null ? new ArrayList<>() : finalBeneficiaries;
    }

    public void setFinalBeneficiaries(List<FinalBeneficiary> finalBeneficiaries) {
        this.finalBeneficiaries = finalBeneficiaries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Beneficiary that = (Beneficiary) o;
        return getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
