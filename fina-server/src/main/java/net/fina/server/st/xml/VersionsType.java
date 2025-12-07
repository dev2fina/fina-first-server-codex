
package net.fina.server.st.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for versionsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="versionsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="configuration" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="mdt" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ost" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "versionsType", propOrder = {
    "configuration",
    "mdt",
    "ost"
})
public class VersionsType {

    @XmlElement(required = true)
    protected String configuration;
    @XmlElement(required = true)
    protected String mdt;
    @XmlElement(required = true)
    protected String ost;

    /**
     * Gets the value of the configuration property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConfiguration() {
        return configuration;
    }

    /**
     * Sets the value of the configuration property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConfiguration(String value) {
        this.configuration = value;
    }

    /**
     * Gets the value of the mdt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMdt() {
        return mdt;
    }

    /**
     * Sets the value of the mdt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMdt(String value) {
        this.mdt = value;
    }

    /**
     * Gets the value of the ost property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOst() {
        return ost;
    }

    /**
     * Sets the value of the ost property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOst(String value) {
        this.ost = value;
    }

}
