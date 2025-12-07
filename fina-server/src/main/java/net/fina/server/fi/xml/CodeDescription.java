package net.fina.server.fi.xml;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "descriptions",
})
public class CodeDescription {
    @XmlAttribute(name = "code")
    private String code;
    @XmlElement(required = true)
    protected Descriptions descriptions;

    public CodeDescription() {
    }

    public CodeDescription(String code, Descriptions descriptions) {
        this.code = code;
        this.descriptions = descriptions;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Descriptions getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(Descriptions descriptions) {
        this.descriptions = descriptions;
    }
}
