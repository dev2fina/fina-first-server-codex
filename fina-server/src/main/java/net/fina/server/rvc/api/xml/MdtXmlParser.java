package net.fina.server.rvc.api.xml;

import net.fina.server.mdt.xml.v2.Node;
import org.jboss.logging.Logger;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MdtXmlParser {

    private static volatile MdtXmlParser _instance;

    private Logger log = Logger.getLogger(getClass());

    private JAXBContext context;

    private MdtXmlParser() {
        try {
            this.context = JAXBContext.newInstance(Node.class);

            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        } catch (JAXBException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static MdtXmlParser getInstance() {
        if (_instance == null) {
            synchronized (MdtXmlParser.class) {
                if (_instance == null) {
                    _instance = new MdtXmlParser();
                }
            }
        }
        return _instance;
    }

    public Node convert(InputStream inputStream) {
        try {
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (Node) unmarshaller.unmarshal(inputStream);
        } catch (JAXBException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public byte[] convert(Node node) {
        try {
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                marshaller.marshal(node, out);
                return out.toByteArray();
            }
        } catch (IOException | JAXBException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
}
