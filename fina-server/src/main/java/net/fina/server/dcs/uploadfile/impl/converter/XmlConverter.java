package net.fina.server.dcs.uploadfile.impl.converter;

import net.fina.common.client.dcs.UploadFileStatus;
import org.jboss.logging.Logger;

import net.fina.common.client.exception.DcsTypeException;
import net.fina.server.dcs.uploadfile.impl.reader.DocumentReader;

/**
 * Class is used to convert xml files to respective xml file format
 *
 * @author dato.java
 */
public class XmlConverter extends AbstractConverter {

    private Logger log = Logger.getLogger(getClass());

    private DocumentReader documentReader;

    public XmlConverter(DocumentReader documentReader) throws DcsTypeException {
        super(documentReader);
        this.documentReader = documentReader;
        log.info("Xml Converter has been created");
    }

    /**
     * @return ConverterInfo object which wraps information about converted xml
     * files
     */
    @Override
    public ConverterInfo convert() throws DcsTypeException {
        log.info("Trying to convert ");
        ConverterInfo info = new ConverterInfo();
        info.setReturns(documentReader.getReturns());
        info.setStatus(UploadFileStatus.CONVERTED);
        log.info("Convert completed successfully");
        return info;
    }

}
