package net.fina.server.person.entity;

import net.fina.server.fi.entity.Currency;
import net.fina.server.i18n.helper.Description;
import org.hibernate.annotations.Type;

import jakarta.persistence.*;

import java.util.Date;
import java.util.Objects;

@Entity(name = "IN_CRIMINAL_RECORDS")
@Table(name = "IN_CRIMINAL_RECORDS")
@org.hibernate.envers.Audited
public class CriminalRecord {
    @Id
    @SequenceGenerator(name = "criminal_record_sequence", sequenceName = "criminal_record_sequence", allocationSize = 1)
    @GeneratedValue(generator = "criminal_record_sequence", strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(name = "NUMBER_OF_COURT_DECISION")
    private String courtDecisionNumber;

    @Column(name = "DATE_OF_COURT_DECISION")
    private Date courtDecisionDate;

    @Column(name = "DECISION_STR_ID")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    private Description courtDecision;

    @Column(name = "PUNISHMENT_DATE")
    private Date punishmentDate;

    @Column(name = "TYPE_STR_ID")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    private Description type;

    @Column(name = "FINE_AMOUNT")
    private double fineAmount;

    @Column(name = "PUNISHMENT_START_DATE")
    private Date punishmentStartDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "CURRENCY")
    private Currency currency;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCourtDecisionNumber() {
        return courtDecisionNumber;
    }

    public void setCourtDecisionNumber(String courtDecisionNumber) {
        this.courtDecisionNumber = courtDecisionNumber;
    }

    public Date getCourtDecisionDate() {
        return courtDecisionDate;
    }

    public void setCourtDecisionDate(Date courtDecisionDate) {
        this.courtDecisionDate = courtDecisionDate;
    }

    public Description getCourtDecision() {
        return courtDecision;
    }

    public void setCourtDecision(Description courtDecision) {
        this.courtDecision = courtDecision;
    }

    public Date getPunishmentDate() {
        return punishmentDate;
    }

    public void setPunishmentDate(Date punishmentDate) {
        this.punishmentDate = punishmentDate;
    }

    public Description getType() {
        return type;
    }

    public void setType(Description type) {
        this.type = type;
    }

    public double getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(double fineAmount) {
        this.fineAmount = fineAmount;
    }

    public Date getPunishmentStartDate() {
        return punishmentStartDate;
    }

    public void setPunishmentStartDate(Date punishmentStartDate) {
        this.punishmentStartDate = punishmentStartDate;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CriminalRecord that = (CriminalRecord) o;
        return getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
