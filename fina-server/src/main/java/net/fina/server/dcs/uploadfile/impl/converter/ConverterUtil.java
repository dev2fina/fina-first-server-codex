package net.fina.server.dcs.uploadfile.impl.converter;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import net.fina.common.client.dcs.DocumentType;
import net.fina.common.client.exception.DcsTypeException;
import net.fina.server.returns.xml.*;
import org.jboss.logging.Logger;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created with IntelliJ IDEA. User: nick Date: 1/4/13 Time: 7:07 PM To change
 * this template use File | Settings | File Templates.
 */
public class ConverterUtil {
    private static final Logger log = Logger.getLogger(ConverterUtil.class);

    private ConverterUtil() {
    }

    public static Return xmlToReturn(byte[] xmlContent) throws DcsTypeException {

        Return r = null;

        try {
            JAXBContext context = JAXBContext.newInstance(ObjectFactory.class.getPackage().getName());
            Unmarshaller unmarshaller = context.createUnmarshaller();

            r = (Return) unmarshaller.unmarshal(new ByteArrayInputStream(xmlContent));

        } catch (Exception ex) {
            throw new DcsTypeException(ex, DcsTypeException.Type.INVALID_XML_STRUCTURE);
        }

        return r;
    }

    public static Map<String, byte[]> extractZip(byte[] zipFileContent) throws Exception {
        Map<String, byte[]> files = new HashMap<>();

        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipFileContent))) {

            ZipEntry ze;
            while ((ze = zis.getNextEntry()) != null) {

                if (!ze.isDirectory()) {

                    int n;
                    byte[] buf = new byte[1024];
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    while ((n = zis.read(buf, 0, 1024)) > -1) {
                        out.write(buf, 0, n);
                    }

                    files.put(ze.getName(), out.toByteArray());

                }

                zis.closeEntry();
            }
        }
        return files;
    }

    public static boolean isExtension(String source, String extension) {
        return source.toLowerCase().endsWith(extension.toLowerCase());
    }

    public static boolean isActivateEncrypt(Map<String, Object> properties) {
        return (boolean) properties.get("net.fina.dcs.security.encrypt");
    }

    public static boolean isActivateSign(Map<String, Object> properties) {
        return (boolean) properties.get("net.fina.dcs.security.sign");
    }

    public static DocumentType detectDocumentType(String extension) {
        DocumentType type = DocumentType.UNKNOWN;
        switch (extension) {
            case ".xls":
            case ".xlsm":
            case ".xlsb":
            case ".xlsx": {
                type = DocumentType.EXCEL;
                break;
            }
            case ".reg.xml":
            case ".xml":
                type = DocumentType.XML;
                break;
            case ".zip": {
                type = DocumentType.ZIP;
                break;
            }
            case ".reg.fina":
            case ".fina":
                type = DocumentType.FINA;
                break;
            case ".xlsx.sig": {
                type = DocumentType.VIP_NET;
                break;
            }
        }
        return type;
    }

    public static Return xmlToReturnV2(byte[] xmlContent) throws DcsTypeException {
        Return r = null;
        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = factory.createXMLStreamReader(new ByteArrayInputStream(xmlContent));


            // Iterate through the XML content
            while (reader.hasNext()) {
                int event = reader.next();

                switch (event) {
                    case XMLStreamConstants.START_ELEMENT:
                        String elementName = reader.getLocalName();
                        if ("RETURN".equals(elementName)) {
                            r = new Return();
                        }
                        // Process HEADER elements
                        if ("HEADER".equals(elementName)) {
                            r.setHeader(processHeader(reader));
                        }
                        // Process BODY elements
                        else if ("BODY".equals(elementName)) {
                            r.setBody(processBody(reader, r.getHeader()));
                        }
                        break;
                }
            }

            reader.close();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new DcsTypeException(ex, DcsTypeException.Type.INVALID_XML_STRUCTURE);
        }

        return r;
    }

    private static Header processHeader(XMLStreamReader reader) throws Exception {
        Header header = new Header();

        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                String elementName = reader.getLocalName();
                if ("BANKCODE".equals(elementName)) {
                    String bankCode = reader.getElementText();
                    header.setBankCode(bankCode);
                } else if ("BANKNAME".equals(elementName)) {
                    String bankName = reader.getElementText();
                    header.setBankName(bankName);
                } else if ("RETURNCODE".equals(elementName)) {
                    String returnCode = reader.getElementText();
                    header.setReturnCode(returnCode);
                } else if ("RETURNNAME".equals(elementName)) {
                    String returnName = reader.getElementText();
                    header.setReturnName(returnName);
                } else if ("PERIODFROM".equals(elementName)) {
                    header.setPeriodFrom(reader.getElementText());
                } else if ("PERIODEND".equals(elementName)) {
                    header.setPeriodEnd(reader.getElementText());
                } else if ("VER".equals(elementName)) {
                    header.setVer(reader.getElementText());
                } else if ("SIGNED".equals(elementName)) {
                    header.setSigned(reader.getElementText());

                } else if ("LNG".equals(elementName)) {
                    header.setLng(reader.getElementText());

                }
            }
            if (event == XMLStreamConstants.END_ELEMENT && "HEADER".equals(reader.getLocalName())) {
                break; // Exit the loop when the HEADER section ends
            }

        }

        return header;
    }

    // Process the BODY section and its items
    private static Body processBody(XMLStreamReader reader, Header header) throws Exception {
        Body body = new Body();
        List<Item> items = new ArrayList<>();
        body.setItems(items);
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT && "ITEM".equals(reader.getLocalName())) {
                items.add(processItem(reader, header));
            }
            if (event == XMLStreamConstants.END_ELEMENT && "BODY".equals(reader.getLocalName())) {
                break; // Exit the loop when the BODY section ends
            }
        }

        return body;
    }

    // Process individual ITEM elements inside the BODY section
    private static Item processItem(XMLStreamReader reader, Header header) throws Exception {
        String itemCode = null;
        String row = null;
        String value = null;

        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                String elementName = reader.getLocalName();
                if ("ITEMCODE".equals(elementName)) {
                    itemCode = reader.getElementText();
                } else if ("ROW".equals(elementName)) {
                    row = reader.getElementText();
                } else if ("VALUE".equals(elementName)) {
                    value = reader.getElementText();
                }
            }
            if (event == XMLStreamConstants.END_ELEMENT && "ITEM".equals(reader.getLocalName())) {
                break; // Exit the loop when the ITEM section ends
            }
        }

        if (row == null) {
            log.error("Invalid row : " + header.getReturnCode());
            throw new DcsTypeException(DcsTypeException.Type.INVALID_XML_STRUCTURE);
        }
        return new Item(itemCode, Integer.parseInt(row.trim()), value);
        // Print item details
    }


}
