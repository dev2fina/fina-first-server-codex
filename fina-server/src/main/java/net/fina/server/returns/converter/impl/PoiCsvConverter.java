package net.fina.server.returns.converter.impl;

import net.fina.server.returns.converter.api.PoiConverter;
import org.apache.poi.ss.usermodel.*;
import org.jboss.logging.Logger;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class PoiCsvConverter implements PoiConverter {
    private Logger log = Logger.getLogger(getClass().getName());

    @Override
    public byte[] convert(Workbook wb) {
        return convertExcelToCSV(wb.getSheetAt(0));
    }


    public byte[] convertExcelToCSV(Sheet sheet) {
        StringBuilder data = new StringBuilder();
        try {
            Iterator<Row> rowIterator = sheet.iterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                for (int i = 0; i < row.getLastCellNum(); i++) {
                    Cell cell = row.getCell(i);
                    if (cell != null) {

                        CellType type = cell.getCellType();
                        switch (type) {
                            case BOOLEAN:
                                data.append(cell.getBooleanCellValue());
                                break;
                            case NUMERIC:
                                data.append(cell.getNumericCellValue());
                                break;
                            case STRING:
                                data.append(cell.getStringCellValue());
                                break;
                            case BLANK:
                            case _NONE:
                                data.append(" ");
                                break;
                            case FORMULA:
                                data.append(cell.getCellFormula());
                                break;
                        }
                    }

                    data.append(";");
                }
                data.append('\n');
            }
            return data.toString().getBytes(StandardCharsets.UTF_8);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return new byte[0];
    }
}
