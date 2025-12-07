package net.fina.server.fi.xml;

import net.fina.common.client.exception.FinATypeException;
import org.jboss.logging.Logger;

import javax.xml.XMLConstants;
import jakarta.xml.bind.*;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * vamekh on 9/19/16.
 */
public class FiXmlParser {

    private static volatile FiXmlParser _instance;

    private Logger log = Logger.getLogger(getClass());
    private JAXBContext context;

    private FiXmlParser() {
        try {
            this.context = JAXBContext.newInstance(Fis.class);
        } catch (JAXBException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static FiXmlParser getInstance() {
        if (_instance == null) {
            synchronized (FiXmlParser.class) {
                if (_instance == null) {
                    _instance = new FiXmlParser();
                }
            }
        }
        return _instance;
    }

    public Fis importFis(byte[] content) throws Exception {
        Unmarshaller unmarshaller = context.createUnmarshaller();
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        String schemaFile = Fis.class.getPackage().getName().replace('.', '/') + "/fina-fi.xsd";
        URL schemaUrl = Fis.class.getClassLoader().getResource(schemaFile);
        Schema schema = schemaUrl != null ? sf.newSchema(schemaUrl) : null;
        FiXmlValidationHandler handler = new FiXmlValidationHandler();
        unmarshaller.setSchema(schema);
        unmarshaller.setEventHandler(handler);
        Fis result = null;
        try {
            result = (Fis) unmarshaller.unmarshal(new ByteArrayInputStream(content));
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
        List<String> messages = handler.getMessages();

        if (!messages.isEmpty()) {
            StringBuilder message = new StringBuilder();
            for (String s : messages) {
                message.append(s);
                message.append("\n");
            }
            throw new FinATypeException(message.toString());
        }

        return result;

    }

    public byte[] exportFis(Fis fis) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(fis, out);

        } catch (JAXBException e) {
            log.error(e.getMessage(), e);
        }

        return out.toByteArray();
    }

    static class FiXmlValidationHandler implements ValidationEventHandler {

        List<String> messages;

        public FiXmlValidationHandler() {
            messages = new ArrayList<>();
        }

        public List<String> getMessages() {
            return messages;
        }

        @Override
        public boolean handleEvent(ValidationEvent event) {

            String message = "\nEVENT" +
                    "SEVERITY:  " + event.getSeverity() +
                    "MESSAGE:  " + event.getMessage() +
                    "LINKED EXCEPTION:  " + event.getLinkedException() +
                    "LOCATOR" +
                    "    LINE NUMBER:  " + event.getLocator().getLineNumber() +
                    "    COLUMN NUMBER:  " + event.getLocator().getColumnNumber() +
                    "    OFFSET:  " + event.getLocator().getOffset() +
                    "    OBJECT:  " + event.getLocator().getObject() +
                    "    NODE:  " + event.getLocator().getNode() +
                    "    URL:  " + event.getLocator().getURL();

            messages.add(message);
            return event.getSeverity() == ValidationEvent.WARNING;
        }
    }

}
