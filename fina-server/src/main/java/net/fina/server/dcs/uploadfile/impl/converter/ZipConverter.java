package net.fina.server.dcs.uploadfile.impl.converter;

import net.fina.common.client.dcs.UploadFileStatus;
import net.fina.common.client.exception.DcsTypeException;
import net.fina.server.dcs.uploadfile.impl.reader.DocumentReader;
import net.fina.server.dcs.uploadfile.entity.UploadFile;

import org.jboss.logging.Logger;

public class ZipConverter extends AbstractConverter {

    private DocumentReader documentReader;
    private Logger log = Logger.getLogger(getClass());

    public ZipConverter(DocumentReader documentReader) throws DcsTypeException {
        super(documentReader);
        this.documentReader = documentReader;
        log.info("Zip document reader has been created");
    }

    @Override
    public ConverterInfo convert() throws DcsTypeException {
        UploadFile file = documentReader.getFile();
        log.info("Converting " + file.getFileName() + " files");
        ConverterInfo info = new ConverterInfo();
        info.setReturns(documentReader.getReturns());
        info.setStatus(UploadFileStatus.CONVERTED);
        log.info("Convert completed successfully");
        return info;

    }

}
