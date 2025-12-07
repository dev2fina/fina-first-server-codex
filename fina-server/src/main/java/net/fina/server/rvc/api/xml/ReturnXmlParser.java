package net.fina.server.rvc.api.xml;

import net.fina.server.returns.xml.Return;
import org.jboss.logging.Logger;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ReturnXmlParser {

    private static volatile ReturnXmlParser _instance;

    private Logger log = Logger.getLogger(getClass());

    private JAXBContext context;

    private ReturnXmlParser() {
        try {
            this.context = JAXBContext.newInstance(net.fina.server.returns.xml.ObjectFactory.class.getPackage().getName());

            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        } catch (JAXBException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static ReturnXmlParser getInstance() {
        if (_instance == null) {
            synchronized (ReturnXmlParser.class) {
                if (_instance == null) {
                    _instance = new ReturnXmlParser();
                }
            }
        }
        return _instance;
    }

    public net.fina.server.returns.xml.Return convert(InputStream inputStream) {
        try {
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (Return) unmarshaller.unmarshal(inputStream);
        } catch (JAXBException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public byte[] convert(net.fina.server.returns.xml.Return ret) {
        try {
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                marshaller.marshal(ret, out);
                return out.toByteArray();
            }
        } catch (IOException | JAXBException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
}
