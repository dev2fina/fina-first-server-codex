package net.fina.server.dcs.uploadfile.impl.converter;

import net.fina.common.client.dcs.UploadFileStatus;
import net.fina.common.client.exception.DcsTypeException;
import net.fina.server.dcs.uploadfile.impl.reader.DocumentReader;
import org.jboss.logging.Logger;

public class VipNetConverter extends AbstractConverter {

    private Logger log = Logger.getLogger(getClass());

    private DocumentReader documentReader;

    /**
     * @param documentReader instance of DocumentReader class to define how to
     *                       read file
     * @throws DcsTypeException if any validation error occurs before converting
     */
    public VipNetConverter(DocumentReader documentReader) throws DcsTypeException {
        super(documentReader);
        this.documentReader = documentReader;
    }

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
