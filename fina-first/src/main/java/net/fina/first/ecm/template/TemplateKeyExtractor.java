package net.fina.first.ecm.template;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.jboss.logging.Logger;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateKeyExtractor {

    Logger log = Logger.getLogger(getClass().getName());

    public Set<String> getKeysFromDocument(byte[] documentContent) {
        Set<String> keySet = new HashSet<>();

        try (InputStream in = new ByteArrayInputStream(documentContent)) {
            XWPFDocument xdoc = new XWPFDocument(OPCPackage.open(in));

            XWPFWordExtractor extractor = new XWPFWordExtractor(xdoc);
            Matcher m = Pattern.compile("\\[=(.*?)\\]").matcher(extractor.getText());

            while (m.find()) {
                keySet.add(m.group(1));
            }


        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }

        return keySet;
    }
}
