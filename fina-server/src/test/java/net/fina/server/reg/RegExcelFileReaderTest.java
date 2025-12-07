package net.fina.server.reg;

import net.fina.common.client.exception.DcsTypeException;
import net.fina.server.reg.impl.RegXSSFSheetXMLHandler;
import net.fina.server.reg.model.CellConfigModel;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.junit.Test;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RegExcelFileReaderTest {

    @Test
    public void readExcelFile() throws Exception {
        String file = getClass().getPackage().getName().replace('.', '/') + "/regTest.xlsx";

        InputStream tmpFile = this.getClass().getClassLoader().getResourceAsStream(file);
        ZipSecureFile.setMinInflateRatio(0);

        OPCPackage container = OPCPackage.open(tmpFile);


        XSSFReader xssfReader = new XSSFReader(container);
        SharedStringsTable strings = (SharedStringsTable) xssfReader.getSharedStringsTable();
        StylesTable styles = xssfReader.getStylesTable();


        Iterator<InputStream> sheets = xssfReader.getSheetsData();


        if (sheets instanceof XSSFReader.SheetIterator) {

            XSSFReader.SheetIterator sheetIterator = (XSSFReader.SheetIterator) sheets;

            while (sheetIterator.hasNext()) {

                try (InputStream stream = sheetIterator.next()) {
                    String sheetName = sheetIterator.getSheetName();
                    System.out.println("Process Sheet : " + sheetName);
                    processSheet(styles, strings, stream, sheetName);
                }

            }
        }
    }

    private void processSheet(StylesTable styles, SharedStringsTable strings, InputStream stream, String sheetName) throws Exception {

        final InputSource sheetSource = new InputSource(stream);
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        saxParserFactory.setNamespaceAware(true);
        SAXParser saxParser = saxParserFactory.newSAXParser();
        XMLReader sheetParser = saxParser.getXMLReader();

        final ContentHandler handler = new RegXSSFSheetXMLHandler(styles, strings, new RegXSSFSheetXMLHandler.SheetContentsHandler() {
            private final Map<String, CellConfigModel> currentRowValues = new HashMap<>();
            private int currentRow;
            private int emptyRowSize;
            private int startRowNum = 1;

            @Override
            public void startRow(int rowNum) {
                this.currentRow = rowNum;
            }

            @Override
            public void endRow() {
                if (currentRow >= startRowNum) {
                    try {
                        if (currentRowValues.isEmpty()) {
                            emptyRowSize++;
                            if (emptyRowSize >= 15) {
                                // Stop reading file
                                throw new DcsTypeException("Reached Max Empty Rows Size" + emptyRowSize + " SheetName " + sheetName);
                            }
                        } else {
                            emptyRowSize = 0;
                        }
                        System.out.println(currentRowValues);

                        currentRowValues.clear();
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }

            @Override
            public void cell(String cellReference, CellConfigModel cellConfigModel) {
                if (currentRow >= startRowNum) {
                    currentRowValues.put(cellReference, cellConfigModel);
                }
            }
        }, false, "dd/MM/yyyy", new HashMap<>());
        sheetParser.setContentHandler(handler);
        try {
            sheetParser.parse(sheetSource);
        } catch (DcsTypeException ex) {
            //ignore it
            System.err.println(ex.getMessage());
        }
    }
}
