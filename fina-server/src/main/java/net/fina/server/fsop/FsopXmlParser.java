package net.fina.server.fsop;

import net.fina.server.returns.xml.ObjectFactory;
import net.fina.server.returns.xml.Return;
import org.jboss.logging.Logger;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.ValidationEventHandler;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.ByteArrayInputStream;
import java.net.URL;

public class FsopXmlParser {

    private static volatile FsopXmlParser _instance;

    private Logger log = Logger.getLogger(getClass());

    private JAXBContext context;
    private Schema schema;

    private FsopXmlParser() {
        try {
            this.context = JAXBContext.newInstance(ObjectFactory.class.getPackage().getName());
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            String schemaFile = getClass().getPackage().getName().replace('.', '/') + "/fina-returns.xsd";
            URL url = getClass().getClassLoader().getResource(schemaFile);
            if (url != null) {
                this.schema = sf.newSchema(url);
            }
        } catch (JAXBException | SAXException e) {
            Logger log = Logger.getLogger(getClass());
            log.error(e.getMessage(), e);
        }
    }

    public static FsopXmlParser getInstance() {
        if (_instance == null) {
            synchronized (FsopXmlParser.class) {
                if (_instance == null) {
                    _instance = new FsopXmlParser();
                }
            }
        }
        return _instance;
    }

    public Return convert(byte[] importedReturnContent, ValidationEventHandler handler) {
        try {
            Unmarshaller unmarshaller = this.context.createUnmarshaller();
            unmarshaller.setEventHandler(handler);
            if (this.schema != null) {
                unmarshaller.setSchema(this.schema);
            }
            return (net.fina.server.returns.xml.Return) unmarshaller.unmarshal(new ByteArrayInputStream(importedReturnContent));
        } catch (JAXBException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
}
