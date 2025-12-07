
package net.fina.server.rvc.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;


/**
 * <p>Java class for fileType complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="fileType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="returnFileVersionId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="mdtFileVersionId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "fileType", propOrder = {
        "value"
})
public class FileType {

    @XmlValue
    protected String value;

    @XmlAttribute(name = "returnFileVersionId")
    protected String returnFileVersionId;

    @XmlAttribute(name = "mdtFileVersionId")
    protected String mdtFileVersionId;

    @XmlAttribute(name = "returnTemplateVersionId")
    protected String returnTemplateVersionId;

    /**
     * Gets the value of the value property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the returnFileVersionId property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getReturnFileVersionId() {
        return returnFileVersionId;
    }

    /**
     * Sets the value of the returnFileVersionId property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setReturnFileVersionId(String value) {
        this.returnFileVersionId = value;
    }

    /**
     * Gets the value of the mdtFileVersionId property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getMdtFileVersionId() {
        return mdtFileVersionId;
    }

    /**
     * Sets the value of the mdtFileVersionId property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setMdtFileVersionId(String value) {
        this.mdtFileVersionId = value;
    }

    public String getReturnTemplateVersionId() {
        return returnTemplateVersionId;
    }

    public void setReturnTemplateVersionId(String returnTemplateVersionId) {
        this.returnTemplateVersionId = returnTemplateVersionId;
    }
}
