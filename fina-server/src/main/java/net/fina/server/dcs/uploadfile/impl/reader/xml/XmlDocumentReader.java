package net.fina.server.dcs.uploadfile.impl.reader.xml;

import net.fina.server.dcs.uploadfile.impl.reader.DocumentReader;
import net.fina.server.dcs.uploadfile.impl.converter.ConverterUtil;
import net.fina.common.client.exception.DcsTypeException;
import net.fina.server.dcs.uploadfile.entity.UploadFile;
import net.fina.server.returns.xml.Return;
import org.jboss.logging.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import net.fina.server.dcs.uploadfile.impl.reader.DocumentReader;

/**
 * Class is used to read xml document in order to generate respective xml files
 */
public class XmlDocumentReader extends DocumentReader {

    private final Logger log = Logger.getLogger(getClass());

    public XmlDocumentReader(Map<String, Object> properties) throws DcsTypeException {
        super(properties);
        log.info("Xml document reader has been created");
    }

    /**
     * @return This list of xml files that have to be imported
     * @throws DCSException if any error/exception occurs
     */
    @Override
    public List<Return> getReturns() throws DcsTypeException {
        return Arrays.asList(ConverterUtil.xmlToReturn(file.getUploadedFile()));
    }
}
