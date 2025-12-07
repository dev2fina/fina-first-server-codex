package net.fina.first.ecm.template;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.*;
import org.jboss.logging.Logger;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TemplateProcessor {
    private final Logger log = Logger.getLogger(TemplateProcessor.class);
    private final Configuration cfg;

    public TemplateProcessor() {
        this.cfg = new Configuration(Configuration.VERSION_2_3_29);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(true);
        cfg.setWrapUncheckedExceptions(true);
        cfg.setFallbackOnNullLoopVariable(false);
        cfg.setInterpolationSyntax(Configuration.SQUARE_BRACKET_INTERPOLATION_SYNTAX);
        cfg.setClassicCompatible(true);
    }

    public byte[] process(byte[] documentContent, Map<String, Object> data, List<Map<String, Object>> dataList) throws IOException, InvalidFormatException {

        try (InputStream in = new ByteArrayInputStream(documentContent)) {
            XWPFDocument xdoc = new XWPFDocument(OPCPackage.open(in));

            // detect and create lists
            new TemplateListGenerator().detectAndCreateLists(xdoc.getParagraphs(), data);

            for (XWPFParagraph paragraph : xdoc.getParagraphs()) {
                replaceParagraphValues(paragraph, data);
            }

            for (XWPFTable table : xdoc.getTables()) {
                iterateTable(table, data, dataList);
            }


            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                xdoc.write(out);
                return out.toByteArray();
            }
        }
    }

    private void iterateTable(XWPFTable table, Map<String, Object> data, List<Map<String, Object>> dataList) {
        dataList = dataList != null ? dataList : Collections.emptyList();
        data.put("DATALENGTH", dataList.size());

        int i = 0;
        while (i < table.getNumberOfRows()) {
            XWPFTableRow row = table.getRow(i++);

            boolean shouldIterate = false;
            for (XWPFTableCell cell : row.getTableCells()) {
                if (cell.getText().contains("[=IT_")) {
                    shouldIterate = true;
                    break;
                }
            }

            if (shouldIterate) {
                if (dataList.size() > 0) {
                    for (int j = 0; j < dataList.size(); j++) {
                        if (j < dataList.size() - 1) {
                            XWPFTableRow newRow = cloneTableRow(row);
                            if (newRow != null) {
                                replaceTableRowValues(newRow, mergeIteratorData(data, dataList.get(j), j + 1));
                                table.addRow(newRow, i++ - 1);
                            }
                        } else {
                            replaceTableRowValues(row, mergeIteratorData(data, dataList.get(j), j + 1));
                        }
                    }
                } else {
                    row.getTableCells().forEach(cell -> IntStream.range(0, cell.getParagraphs().size())
                            .forEach(value -> cell.removeParagraph(0)));
                }
            }

            replaceTableRowValues(row, data);
        }
    }

    private XWPFTableRow cloneTableRow(XWPFTableRow row) {
        try {
            CTRow ctrow = CTRow.Factory.parse(row.getCtRow().newInputStream());
            return new XWPFTableRow(ctrow, row.getTable());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    private void replaceTableRowValues(XWPFTableRow row, Map<String, Object> data) {
        for (XWPFTableCell cell : row.getTableCells()) {
            for (XWPFParagraph paragraph : cell.getParagraphs()) {
                replaceParagraphValues(paragraph, data);
            }
        }
    }

    public void replaceParagraphValues(XWPFParagraph paragraph, Map<String, Object> data) {
        int numRuns = paragraph.getRuns().size();
        StringBuilder curTemplate = new StringBuilder();
        XWPFRun templateRun = null;
        for (int i = 0; i < numRuns; i++) {
            XWPFRun run = paragraph.getRuns().get(i);
            String text = run.getText(0);

            while (text != null && !text.isEmpty()) {

                if (curTemplate.length() > 1 && !curTemplate.toString().startsWith("[=")) {
                    curTemplate = new StringBuilder();
                }

                int templateStartPos = text.indexOf("[");
                int templateEndPos = text.indexOf("]");

                if (curTemplate.length() == 0) {
                    if (templateStartPos >= 0) {
                        createRun(paragraph, run, templateProcessor(text.substring(0, templateStartPos), data));
                        curTemplate = new StringBuilder("[");
                        templateRun = run;
                        text = text.length() > templateStartPos + 1 ? text.substring(templateStartPos + 1) : "";
                    } else {
                        createRun(paragraph, run, templateProcessor(text, data));
                        break;
                    }
                } else if (templateEndPos >= 0) {
                    createRun(paragraph, templateRun, templateProcessor(curTemplate + text.substring(0, templateEndPos + 1), data));
                    curTemplate = new StringBuilder();
                    text = text.length() > templateEndPos + 1 ? text.substring(templateEndPos + 1) : "";
                } else {
                    curTemplate.append(text);
                    break;
                }
            }
        }

        IntStream.range(0, numRuns).forEach(i -> removeRun(paragraph, 0));
    }

    private void removeRun(XWPFParagraph paragraph, int pos) {
        try {
            paragraph.removeRun(pos);
        } catch (Exception t) {
            log.error(t.getMessage(),t);
        }
    }

    private void createRun(XWPFParagraph paragraph, XWPFRun src, String text) {
        XWPFRun newRun = paragraph.createRun();
        if (src != null) {
            newRun.getCTR().set(src.getCTR());
        }
        newRun.setText(text, 0);
    }

    private Map<String, Object> mergeIteratorData(Map<String, Object> data, Map<String, Object> extraData, int position) {
        Map<String, Object> newData = new HashMap<>();
        newData.putAll(data);
        newData.putAll(extraData.entrySet().stream().collect(Collectors.toMap(o -> "IT_" + o.getKey(), Map.Entry::getValue)));
        newData.put("IT_ROWNUM", position);
        return newData;
    }

    @SuppressWarnings("unused")
    private void changeText(XWPFParagraph p, String newText) {
        List<XWPFRun> runs = p.getRuns();

        if (!runs.isEmpty()) {
            for (int i = runs.size() - 1; i > 0; i--) {
                p.removeRun(i);
            }
            XWPFRun run = runs.get(0);
            run.setText(newText, 0);
        }
    }

    private String templateProcessor(String templateStr, Map<String, Object> model) {
        try {
            if (templateStr.startsWith("[=") && templateStr.endsWith("]") && templateStr.contains("/")) {
                String fullKey = templateStr.substring(2, templateStr.length() - 1);
                String key = fullKey.substring(0, fullKey.indexOf('/'));

                if (model.containsKey(key) && model.get(key) instanceof Date) {
                    String format = fullKey.substring(fullKey.indexOf('/') + 1);
                    SimpleDateFormat df = new SimpleDateFormat(format.isEmpty() ? "yyyy-MM-dd" : format);
                    model.put(key, df.format(model.get(key)));
                }
                templateStr = "[=" + key + ']';
            }

            Template t = new Template("name", new StringReader(templateStr), cfg);

            try (Writer out = new StringWriter()) {
                t.process(model, out);
                return out.toString();
            }
        } catch (Exception t) {
            log.error(t.getMessage(),t);
        }

        return templateStr;
    }
}
