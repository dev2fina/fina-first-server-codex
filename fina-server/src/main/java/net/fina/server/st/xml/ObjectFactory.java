
package net.fina.server.st.xml;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the net.fina.server.st.xml package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Packages_QNAME = new QName("", "packages");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.fina.server.st.xml
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link PackagesType }
     * 
     */
    public PackagesType createPackagesType() {
        return new PackagesType();
    }

    /**
     * Create an instance of {@link PackageType }
     * 
     */
    public PackageType createPackageType() {
        return new PackageType();
    }

    /**
     * Create an instance of {@link VersionsType }
     * 
     */
    public VersionsType createVersionsType() {
        return new VersionsType();
    }

    /**
     * Create an instance of {@link ReturnsType }
     * 
     */
    public ReturnsType createReturnsType() {
        return new ReturnsType();
    }

    /**
     * Create an instance of {@link ReturnType }
     * 
     */
    public ReturnType createReturnType() {
        return new ReturnType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PackagesType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "packages")
    public JAXBElement<PackagesType> createPackages(PackagesType value) {
        return new JAXBElement<PackagesType>(_Packages_QNAME, PackagesType.class, null, value);
    }

}
