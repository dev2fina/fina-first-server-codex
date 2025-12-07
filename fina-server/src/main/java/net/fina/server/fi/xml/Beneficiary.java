package net.fina.server.fi.xml;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "finalBeneficiaries"
})
public class Beneficiary {
    @XmlAttribute(name = "personIdNumber")
    protected String identificationNumber;
    @XmlAttribute(name = "countryCode")
    protected String countryCode;
    @XmlElement
    protected FinalBeneficiaries finalBeneficiaries;
    @XmlAttribute
    private double share;
    @XmlAttribute
    private double nominal;
    @XmlAttribute
    private String creationDate;
    @XmlAttribute
    private boolean active = true;
    @XmlAttribute
    private String currency;
    @XmlAttribute
    private String beneficiaryType;


    public double getShare() {
        return share;
    }

    public void setShare(double share) {
        this.share = share;
    }

    public double getNominal() {
        return nominal;
    }

    public void setNominal(double nominal) {
        this.nominal = nominal;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public void setIdentificationNumber(String identificationNumber) {
        this.identificationNumber = identificationNumber;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public FinalBeneficiaries getFinalBeneficiaries() {
        return finalBeneficiaries;
    }

    public void setFinalBeneficiaries(FinalBeneficiaries finalBeneficiaries) {
        this.finalBeneficiaries = finalBeneficiaries;
    }

    public String getBeneficiaryType() {
        return beneficiaryType;
    }

    public void setBeneficiaryType(String beneficiaryType) {
        this.beneficiaryType = beneficiaryType;
    }
}
