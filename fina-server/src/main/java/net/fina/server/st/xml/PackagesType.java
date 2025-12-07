
package net.fina.server.st.xml;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.*;


/**
 * <p>Java class for packagesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="packagesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="versions" type="{}versionsType"/>
 *         &lt;element name="package" type="{}packageType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "packagesType", propOrder = {
    "versions",
    "_package"
})
public class PackagesType {

    @XmlElement(required = true)
    protected VersionsType versions;
    @XmlElement(name = "package")
    protected List<PackageType> _package;

    /**
     * Gets the value of the versions property.
     * 
     * @return
     *     possible object is
     *     {@link VersionsType }
     *     
     */
    public VersionsType getVersions() {
        return versions;
    }

    /**
     * Sets the value of the versions property.
     * 
     * @param value
     *     allowed object is
     *     {@link VersionsType }
     *     
     */
    public void setVersions(VersionsType value) {
        this.versions = value;
    }

    /**
     * Gets the value of the package property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the package property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPackage().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PackageType }
     * 
     * 
     */
    public List<PackageType> getPackage() {
        if (_package == null) {
            _package = new ArrayList<PackageType>();
        }
        return this._package;
    }

}
