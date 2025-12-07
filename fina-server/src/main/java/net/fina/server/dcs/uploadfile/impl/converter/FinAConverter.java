package net.fina.server.dcs.uploadfile.impl.converter;

import org.jboss.logging.Logger;

import net.fina.common.client.dcs.UploadFileStatus;
import net.fina.common.client.exception.DcsTypeException;
import net.fina.server.dcs.uploadfile.impl.reader.DocumentReader;

/**
 * Created with IntelliJ IDEA. User: nick Date: 1/4/13 Time: 6:22 PM To change
 * this template use File | Settings | File Templates.
 */
public class FinAConverter extends AbstractConverter {

    private Logger log = Logger.getLogger(getClass());

    private DocumentReader documentReader;

    /**
     * @param documentReader instance of DocumentReader class to define how to
     * read file
     * @throws DcsTypeException if any validation error occurs before converting
     */
    public FinAConverter(DocumentReader documentReader) throws DcsTypeException {
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
