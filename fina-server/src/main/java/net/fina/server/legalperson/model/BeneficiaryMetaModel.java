package net.fina.server.legalperson.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.fina.server.fi.entity.Currency;
import net.fina.server.person.model.PersonMetaModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BeneficiaryMetaModel {
    protected long id;
    protected double share;
    protected double nominal;
    protected Date creationDate;
    protected boolean active;
    protected Currency currency;
    protected PersonMetaModel physicalPerson;
    protected LegalPersonMetaModel legalPerson;
    protected List<FinalBeneficiaryMetaModel> finalBeneficiaries;

    public BeneficiaryMetaModel() {
    }
    
    public BeneficiaryMetaModel(long id, double share) {
        this.id = id;
        this.share = share;
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

    public void setShare(double share) {
        this.share = share;
    }

    public PersonMetaModel getPhysicalPerson() {
        return physicalPerson;
    }

    public void setPhysicalPerson(PersonMetaModel physicalPerson) {
        this.physicalPerson = physicalPerson;
    }

    public LegalPersonMetaModel getLegalPerson() {
        return legalPerson;
    }

    public void setLegalPerson(LegalPersonMetaModel legalPerson) {
        this.legalPerson = legalPerson;
    }

    public List<FinalBeneficiaryMetaModel> getFinalBeneficiaries() {
        return finalBeneficiaries == null ? new ArrayList<>() : finalBeneficiaries;
    }

    public void setFinalBeneficiaries(List<FinalBeneficiaryMetaModel> finalBeneficiaries) {
        this.finalBeneficiaries = finalBeneficiaries;
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
}
