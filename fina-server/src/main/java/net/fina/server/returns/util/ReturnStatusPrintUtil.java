package net.fina.server.returns.util;

import net.fina.common.shared.ContentModel;
import net.fina.server.returns.model.ReturnStatusPrintModel;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ReturnStatusPrintUtil {


    public static ContentModel createReturnStatusExcelReport(ReturnStatusPrintModel printModel) throws IOException {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet(printModel.getTitle());

        sheet.setDisplayGridlines(false);

        int curRow = 0;
        Row headerRow = sheet.createRow(curRow++);
        headerRow.setHeight((short) (headerRow.getHeight() * 2));

        for (int i = 0; i < printModel.getColumnCount(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(printModel.getColumnName(i));
            setCellStyle(wb, cell, true);
        }

        for (int i = 0; i < printModel.getRowCount(); i++) {
            Row row = sheet.createRow(curRow++);

            for (int j = 0; j < printModel.getColumnCount(); j++) {
                Cell cell = row.createCell(j);

                Object val = printModel.getValueAt(i, j);
                if (val != null) {
                    cell.setCellValue(val.toString());
                    setCellStyle(wb, cell, false);
                }
            }
        }

        for (int i = 0; i < printModel.getColumnCount(); i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
        wb.write(fileOut);
        fileOut.close();

        return new ContentModel(fileOut.toByteArray(), printModel.getTitle(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    private static void setCellStyle(Workbook wb, Cell cell, boolean header) {
        CellStyle style = wb.createCellStyle();
        style.setWrapText(true);
        if (header) {
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setFillBackgroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            style.setFillPattern(FillPatternType.LEAST_DOTS);
            Font font = wb.createFont();
            font.setBold(true);
            style.setFont(font);
            cell.setCellStyle(style);
        } else {
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            cell.setCellStyle(style);
        }
    }

}
