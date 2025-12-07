package net.fina.server.dcs.uploadfile.impl.converter;

import net.fina.common.client.dcs.DocumentType;
import net.fina.common.client.exception.DcsTypeException;
import net.fina.common.client.exception.DcsTypeException.Type;
import net.fina.server.dcs.uploadfile.entity.UploadFile;
import net.fina.server.dcs.uploadfile.impl.reader.DocumentReader;
import net.fina.server.dcs.uploadfile.impl.reader.excel.ExcelDocumentReaderFactory;
import net.fina.server.dcs.uploadfile.impl.reader.fina.FinADocumentReader;
import net.fina.server.dcs.uploadfile.impl.reader.vipnet.VipNetDocumentReader;
import net.fina.server.dcs.uploadfile.impl.reader.xml.XmlDocumentReader;
import net.fina.server.dcs.uploadfile.impl.reader.zip.ZipDocumentReader;
import org.jboss.logging.Logger;

import java.util.Map;

/**
 * Class is used to create any converter object
 */
public class AbstractConverterFactory {
    private static AbstractConverterFactory instance = null;

    private final Logger log = Logger.getLogger(getClass());

    private AbstractConverterFactory() {

    }

    /**
     * @return the current instance of AbstractConverterFactory class
     */
    public static AbstractConverterFactory getInstance() {
        if (instance == null)
            instance = new AbstractConverterFactory();
        return instance;
    }

    /**
     * Creates AbstractConverter object
     *
     * @param file       UploadedFile
     * @param properties Meta Properties
     * @param type       defines file extension/type
     * @return created AbstractConverter object
     * @throws DcsTypeException if type is illegal
     */
    public AbstractConverter createAbstractConverter(UploadFile file, Map<String, Object> properties, DocumentType type) throws DcsTypeException {
        log.info("Trying to get converter instance for type " + type);
        AbstractConverter converter;
        DocumentReader documentReader;

        switch (type) {
            case EXCEL: {
                log.info("Document reader is creating for excel document");
                documentReader = ExcelDocumentReaderFactory.getInstance().createDocumentReader(properties);
                documentReader.setFile(file);
                log.info("Converter is creating for excel document");
                converter = new ExcelConverter(documentReader);
                break;
            }
            case ZIP: {
                log.info("Document reader is creating for zip document");
                documentReader = new ZipDocumentReader(properties);
                documentReader.setFile(file);
                log.info("Converter is creating for zip document");
                converter = new ZipConverter(documentReader);
                break;
            }
            case XML: {
                log.info("Document reader is creating for xml document");
                documentReader = new XmlDocumentReader(properties);
                documentReader.setFile(file);
                log.info("Converter is creating for xml document");
                converter = new XmlConverter(documentReader);
                break;
            }
            case FINA: {
                log.info("Document reader is creating for FinA document");
                documentReader = new FinADocumentReader(properties);
                documentReader.setFile(file);
                log.info("Converter is creating for FinA document");
                converter = new FinAConverter(documentReader);
                break;
            }
            case VIP_NET: {
                log.info("Document reader is creating for VipNet document");
                documentReader = new VipNetDocumentReader(properties);
                documentReader.setFile(file);
                log.info("Converter is creating for VipNet document");
                converter = new VipNetConverter(documentReader);
                break;
            }
            default: {
                log.error("Unknown content " + type);
                throw new DcsTypeException(Type.UNKNOWN_CONTENT);
            }
        }
        return converter;
    }
}
