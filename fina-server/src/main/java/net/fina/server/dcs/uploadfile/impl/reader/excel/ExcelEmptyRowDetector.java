package net.fina.server.dcs.uploadfile.impl.reader.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

public final class ExcelEmptyRowDetector {

    public static boolean isEmpty(Cell cell) {
        if (cell == null) return true;

        CellType type = cell.getCellType();
        return switch (type) {
            case BLANK -> true;
            case STRING -> {
                String s = cell.getStringCellValue();
                yield s == null || s.trim().isEmpty();
            }
            case NUMERIC, BOOLEAN, FORMULA -> false;

            default -> false;
        };
    }

    /**
     * Check if a row is empty by scanning a column range.
     */
    public static boolean isRowEmpty(Row row, int firstCol, int lastCol) {
        if (row == null) return true;
        for (int c = firstCol; c <= lastCol; c++) {
            if (!isEmpty(row.getCell(c, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL))) {
                return false;
            }
        }
        return true;
    }
}
