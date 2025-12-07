package net.fina.server.dcs.uploadfile.impl.reader.zip;

import net.fina.common.client.exception.ConverterDcsTypeException;
import net.fina.common.client.exception.DcsTypeException;
import net.fina.common.client.exception.DcsTypeException.Type;
import net.fina.server.dcs.uploadfile.entity.UploadFile;
import net.fina.server.dcs.uploadfile.impl.converter.ConverterUtil;
import net.fina.server.dcs.uploadfile.impl.reader.DocumentReader;
import net.fina.server.dcs.uploadfile.impl.reader.excel.ExcelDocumentReaderFactory;
import net.fina.server.dcs.uploadfile.impl.reader.xml.XmlDocumentReader;
import net.fina.server.returns.xml.Return;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ZipDocumentReader extends DocumentReader {
    private final Logger log = Logger.getLogger(getClass());

    public ZipDocumentReader(Map<String, Object> properties) throws DcsTypeException {
        super(properties);
        log.info("Zip document reader has been created");
    }


    /**
     * @return This list of xml files that have to be imported
     * @throws DcsTypeException if any error/exception occurs
     */
    @Override
    public List<Return> getReturns() throws DcsTypeException {
        String fileName = file.getFileName();
        List<Return> result = new ArrayList<>();

        try {
            Map<String, byte[]> files = ConverterUtil.extractZip(file.getUploadedFile());

            boolean oneFileControl = true;
            Object oneFileControlObject = this.properties.get("dcs.zip.oneFileControl.enable");
            if (oneFileControlObject != null) {
                oneFileControl = ((int) oneFileControlObject) > 1;
            }
            if (files.isEmpty()) {
                log.error("No any file in zip content");
                Type type = Type.EMPTY_ZIP_ERROR;
                throw new ConverterDcsTypeException(type, type.getReplaceableCode());
            }

            if (oneFileControl) {
                if (files.size() == 1) {
                    String zipFileName = files.entrySet().iterator().next().getKey();
                    //TODO remove hardcoded extensions
                    if (!(ConverterUtil.isExtension(zipFileName, "xls") || ConverterUtil.isExtension(zipFileName, "xlsx") || ConverterUtil.isExtension(zipFileName, "xml"))) {
                        Type type = Type.ZIP_AND_CONTENT_NAME_IS_INVALID;
                        throw new ConverterDcsTypeException(type, type.getReplaceableCode());
                    }
                } else {
                    String msg = "Zip content contains " + files + " file(s), must be 1";
                    log.error(msg);
                    throw new ConverterDcsTypeException(Type.TOO_MANY_FILES_ERROR, msg);
                }
            }

            for (Map.Entry<String, byte[]> fileEntry : files.entrySet()) {
                DocumentReader documentReader = null;
                if (ConverterUtil.isExtension(fileEntry.getKey(), "xls") || ConverterUtil.isExtension(fileEntry.getKey(), "xlsx")) {
                    String contentFileName = fileEntry.getKey();
                    properties.put("dcs.file.extension", contentFileName.substring(contentFileName.lastIndexOf('.')));
                    documentReader = ExcelDocumentReaderFactory.getInstance().createDocumentReader(properties);
                } else if (ConverterUtil.isExtension(fileEntry.getKey(), "xml")) {
                    documentReader = new XmlDocumentReader(properties);
                }
                if (documentReader != null) {
                    UploadFile uploadFile = new UploadFile();
                    uploadFile.setUploadedFile(fileEntry.getValue());
                    uploadFile.setFileName(fileEntry.getKey());

                    documentReader.setFile(uploadFile);
                    result.addAll(documentReader.getReturns());
                }
            }

        } catch (DcsTypeException e) {
            log.error(e.getMessage(), e);
            throw e;
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            throw new DcsTypeException(t.getMessage());
        }

        return result;
    }
}
