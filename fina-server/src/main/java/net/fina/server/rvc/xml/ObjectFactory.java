
package net.fina.server.rvc.xml;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the net.fina.server.rvc.xml package. 
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

    private final static QName _ReturnStatuses_QNAME = new QName("", "return-statuses");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.fina.server.rvc.xml
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ReturnStatusesType }
     * 
     */
    public ReturnStatusesType createReturnStatusesType() {
        return new ReturnStatusesType();
    }

    /**
     * Create an instance of {@link StatusType }
     * 
     */
    public StatusType createStatusType() {
        return new StatusType();
    }

    /**
     * Create an instance of {@link FileType }
     * 
     */
    public FileType createFileType() {
        return new FileType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReturnStatusesType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "return-statuses")
    public JAXBElement<ReturnStatusesType> createReturnStatuses(ReturnStatusesType value) {
        return new JAXBElement<ReturnStatusesType>(_ReturnStatuses_QNAME, ReturnStatusesType.class, null, value);
    }

}
