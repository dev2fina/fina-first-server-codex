package net.fina.server.fi.xml;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class PersonPosition {

    @XmlAttribute(name = "lpIdNumber")
    protected String lpIdNumber;

    @XmlElement(name = "positionDescriptions")
    protected Descriptions positionDescriptions;

    public String getLpIdNumber() {
        return lpIdNumber;
    }

    public void setLpIdNumber(String lpIdNumber) {
        this.lpIdNumber = lpIdNumber;
    }

    public Descriptions getPositionDescriptions() {
        return positionDescriptions;
    }

    public void setPositionDescriptions(Descriptions positionDescriptions) {
        this.positionDescriptions = positionDescriptions;
    }
}
