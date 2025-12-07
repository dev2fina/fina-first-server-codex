package net.fina.server.person.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.fina.server.fi.entity.Currency;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CriminalRecordMetaModel {
    private long id;
    private String courtDecisionNumber;
    private Date courtDecisionDate;
    private String courtDecision;
    private long courtDecisionNameStrId;
    private Date punishmentDate;
    private String type;
    private long typeStrId;
    private double fineAmount;
    private Date punishmentStartDate;
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

    public String getCourtDecision() {
        return courtDecision;
    }

    public void setCourtDecision(String courtDecision) {
        this.courtDecision = courtDecision;
    }

    public long getCourtDecisionNameStrId() {
        return courtDecisionNameStrId;
    }

    public void setCourtDecisionNameStrId(long courtDecisionNameStrId) {
        this.courtDecisionNameStrId = courtDecisionNameStrId;
    }

    public Date getPunishmentDate() {
        return punishmentDate;
    }

    public void setPunishmentDate(Date punishmentDate) {
        this.punishmentDate = punishmentDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTypeStrId() {
        return typeStrId;
    }

    public void setTypeStrId(long typeStrId) {
        this.typeStrId = typeStrId;
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
}
