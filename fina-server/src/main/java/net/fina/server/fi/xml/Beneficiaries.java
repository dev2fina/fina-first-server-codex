package net.fina.server.fi.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "beneficiary"
})
public class Beneficiaries {

    protected List<Beneficiary> beneficiary;

    public List<Beneficiary> getBeneficiary() {
        if (beneficiary == null) {
            beneficiary = new ArrayList<>();
        }
        return beneficiary;
    }

    public void setBeneficiary(List<Beneficiary> beneficiary) {
        this.beneficiary = beneficiary;
    }
}
