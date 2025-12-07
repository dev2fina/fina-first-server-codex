package net.fina.server.reg.impl;


import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import net.fina.common.client.exception.DcsTypeException;
import net.fina.common.client.exception.FinATypeException;
import net.fina.server.dcs.uploadfile.api.UploadFileStreamable;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.jcr.impl.FileContentManagementSession;
import net.fina.server.reg.api.RegProcessor;
import net.fina.server.reg.model.RegProcessConfig;
import net.fina.server.reg.qualifier.RegXmlProcessor;
import net.fina.server.reg.util.RegXmlFileXsdHelper;
import net.fina.server.reg.util.processor.RegFileProcessorUtilBase;
import net.fina.server.util.TempFileUtil;
import org.apache.commons.io.FileUtils;
import org.jboss.logging.Logger;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.apache.xerces.jaxp.JAXPConstants.*;


@Stateless
@Local(RegProcessor.class)
@RegXmlProcessor
@Interceptors(RecordingAuditor.class)
public class RegXmlFileProcessor implements RegProcessor {
    @Inject
    private Logger log;
    @Inject
    private FileContentManagementSession fileContentManagementSession;


    @Override
    public List<String> process(RegProcessConfig config,
                                Connection conn,
                                RegFileProcessorUtilBase regFileProcessorUtil) {
        DefaultHandler xmlHandler = new RegXmlHandler(config, config.getAcceptedDefinitionCodes(), conn, regFileProcessorUtil);
        File tmpFile = null;

        try {
            tmpFile = TempFileUtil.createTempFile(config.getFileName());
            final File finalTmpFile = tmpFile;

            fileContentManagementSession.loadUploadFileStreamInto(config.getUploadFile(), new UploadFileStreamable() {
                @Override
                public void readFileStream(InputStream inputStream) throws Exception {
                    FileUtils.copyInputStreamToFile(inputStream, finalTmpFile);
                }
            });

            InputStream inputStream = new FileInputStream(finalTmpFile);

            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(true);
            factory.setNamespaceAware(true);

            SAXParser saxParser = factory.newSAXParser();
            saxParser.setProperty(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);

            saxParser.setProperty(JAXP_SCHEMA_SOURCE, new RegXmlFileXsdHelper().getXsdFile());


            saxParser.parse(inputStream, xmlHandler);

        } catch (DcsTypeException ex) {
            log.error(ex.getMessage(), ex);
            if (!config.getInputValidatorMap().isEmpty()) {
                return new ArrayList<>();
            }
            if (ex.getReasonsList() != null && !ex.getReasonsList().isEmpty()) {
                return ex.getReasonsList();
            }
            return Collections.singletonList("${" + DcsTypeException.Type.GENERAL_ERROR.getCode() + "}");
        } catch (FinATypeException ex) {
            log.error(ex.getMessage(), ex);
            if (!config.getInputValidatorMap().isEmpty()) {
                return new ArrayList<>();
            }
            return Collections.singletonList(ex.getMessage());
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | IOException |
                 CertificateException | NoSuchProviderException sigEx) {
            log.error(sigEx.getMessage(), sigEx);
            return Collections.singletonList("${" + DcsTypeException.Type.INVALID_DIGITAL_SIGNATURE.getCode() + "}");
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            if (!config.getInputValidatorMap().isEmpty()) {
                return new ArrayList<>();
            }
            return Collections.singletonList("${" + FinATypeException.Type.GENERAL_ERROR.getCode() + "}");
        } finally {
            if (tmpFile != null) {
                tmpFile.delete();
            }
        }

        return new ArrayList<>();
    }


}
