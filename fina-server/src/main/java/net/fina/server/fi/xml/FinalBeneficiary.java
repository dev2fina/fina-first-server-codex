package net.fina.server.fi.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "physicalPerson"
})
public class FinalBeneficiary {
    @XmlElement(required = true)
    protected Person physicalPerson;

    public Person getPhysicalPerson() {
        return physicalPerson;
    }

    public void setPhysicalPerson(Person physicalPerson) {
        this.physicalPerson = physicalPerson;
    }
}
