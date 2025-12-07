package net.fina.server.dcs.uploadfile.impl.converter;

import net.fina.common.client.dcs.UploadFileStatus;
import net.fina.common.client.exception.DcsTypeException;
import net.fina.server.dcs.uploadfile.impl.reader.DocumentReader;
import net.fina.server.dcs.uploadfile.entity.UploadFile;
import org.jboss.logging.Logger;

/**
 * Class extends AbstractConverter in order to process excel files
 *
 * @author dato.java
 */
public class ExcelConverter extends AbstractConverter {

    private Logger log = Logger.getLogger(getClass());

    private final DocumentReader reader;

    /**
     * @param reader DocumentReader implementation class
     * @throws DcsTypeException in any error occurs
     */
    public ExcelConverter(DocumentReader reader) throws DcsTypeException {
        super(reader);
        this.reader = reader;
        log.info("Excel file converter has been created");
    }

    /**
     * Tries to convert uploaded file into xml files
     *
     * @return ConverterInfo object which wrappes information about converted
     * files
     */
    @Override
    public ConverterInfo convert() throws DcsTypeException {
        log.info("Trying to convert");

        UploadFile file = reader.getFile();
        log.info("Converting " + file.getFileName() + " file");

        ConverterInfo info = new ConverterInfo();
        info.setReturns(reader.getReturns());
        info.setStatus(UploadFileStatus.CONVERTED);

        log.info("Converted completed successfully");
        return info;
    }
}
