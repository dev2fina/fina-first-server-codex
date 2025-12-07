package net.fina.server.rvc.api.xml;

import net.fina.server.rvc.xml.ReturnStatusesType;
import org.jboss.logging.Logger;

import jakarta.xml.bind.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StatusXmlParser {

    private static volatile StatusXmlParser _instance;

    private Logger log = Logger.getLogger(getClass());

    private JAXBContext context;

    private StatusXmlParser() {
        try {
            this.context = JAXBContext.newInstance(net.fina.server.rvc.xml.ObjectFactory.class.getPackage().getName());

            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        } catch (JAXBException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static StatusXmlParser getInstance() {
        if (_instance == null) {
            synchronized (StatusXmlParser.class) {
                if (_instance == null) {
                    _instance = new StatusXmlParser();
                }
            }
        }
        return _instance;
    }

    public ReturnStatusesType convert(InputStream inputStream) {
        try {
            Unmarshaller unmarshaller = context.createUnmarshaller();
            jakarta.xml.bind.JAXBElement<ReturnStatusesType> element = (JAXBElement<ReturnStatusesType>) unmarshaller.unmarshal(inputStream);
            return element.getValue();
        } catch (JAXBException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public byte[] convert(ReturnStatusesType statusesType) {
        try {
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                marshaller.marshal(statusesType, out);
                return out.toByteArray();
            }
        } catch (IOException | JAXBException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
}
