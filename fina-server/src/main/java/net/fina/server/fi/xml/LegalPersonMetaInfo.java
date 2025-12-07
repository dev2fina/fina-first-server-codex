package net.fina.server.fi.xml;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "businessEntityType",
        "economicEntityType",
        "equityFormType",
        "managementFormType",
})
public class LegalPersonMetaInfo {
    @XmlElement(name = "businessEntityType")
    protected CodeDescription businessEntityType;
    @XmlElement(name = "economicEntityType")
    protected CodeDescription economicEntityType;
    @XmlElement(name = "equityFormType")
    protected CodeDescription equityFormType;
    @XmlElement(name = "managementFormType")
    protected CodeDescription managementFormType;

    public CodeDescription getBusinessEntityType() {
        return businessEntityType;
    }

    public void setBusinessEntityType(CodeDescription businessEntityType) {
        this.businessEntityType = businessEntityType;
    }

    public CodeDescription getEconomicEntityType() {
        return economicEntityType;
    }

    public void setEconomicEntityType(CodeDescription economicEntityType) {
        this.economicEntityType = economicEntityType;
    }

    public CodeDescription getEquityFormType() {
        return equityFormType;
    }

    public void setEquityFormType(CodeDescription equityFormType) {
        this.equityFormType = equityFormType;
    }

    public CodeDescription getManagementFormType() {
        return managementFormType;
    }

    public void setManagementFormType(CodeDescription managementFormType) {
        this.managementFormType = managementFormType;
    }
}
