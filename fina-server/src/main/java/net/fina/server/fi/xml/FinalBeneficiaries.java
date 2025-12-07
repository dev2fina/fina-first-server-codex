package net.fina.server.fi.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "finalBeneficiaries"
})
public class FinalBeneficiaries {
    protected List<FinalBeneficiary> finalBeneficiaries;

    public List<FinalBeneficiary> getFinalBeneficiaries() {
        if (finalBeneficiaries == null) {
            finalBeneficiaries = new ArrayList<>();
        }
        return finalBeneficiaries;
    }
}
