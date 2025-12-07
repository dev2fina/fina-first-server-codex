package net.fina.server.inputs.util;

import net.fina.common.client.returns.ReturnTypeModel;
import net.fina.server.inputs.model.InputManagerFiMetaModel;
import net.fina.server.returns.model.PeriodMetaModel;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UploadedFileStatusesExportUtil {

    public static byte[] exportUploadedFileStatuses(Map<ReturnTypeModel, List<Object[]>> dataMap, List<PeriodMetaModel> periods, List<InputManagerFiMetaModel> fis) throws IOException {
        Workbook wb = new XSSFWorkbook();

        List<Object[]> returnTypeModel = null;
        for (Map.Entry<ReturnTypeModel, List<Object[]>> entry : dataMap.entrySet()) {
            returnTypeModel = entry.getValue();
            if (returnTypeModel != null && !returnTypeModel.isEmpty()) {
                createSheet(wb, entry.getKey(), returnTypeModel, periods, fis);
            }
        }

        ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
        wb.write(fileOut);
        fileOut.close();
        return fileOut.toByteArray();
    }

    private static void createSheet(Workbook wb, ReturnTypeModel returnTypeModel, List<Object[]> data, List<PeriodMetaModel> periods, List<InputManagerFiMetaModel> fis) {
        Sheet sheet = wb.createSheet(returnTypeModel.getCode());
        sheet.setDisplayGridlines(false);

        int rowIdx = 2;
        createPeriodDimension(wb, sheet, periods, rowIdx++);

        for (InputManagerFiMetaModel fi : fis) {
            List<Object[]> rowData = data.stream().filter(obj -> obj[0].equals(fi.getId())).collect(Collectors.toList());
            if (rowData.size() > 0) {
                createDataRow(wb, sheet, rowIdx++, fi, rowData, periods);
            }
        }

        sheet.setColumnWidth(0, 3 * 256);
        for (int i = 1; i <= periods.size() + 1; i++) {
            sheet.autoSizeColumn(i);
        }

        createTitle(wb, sheet, returnTypeModel);
    }

    private static void createDataRow(Workbook wb, Sheet sheet, int rowIdx, InputManagerFiMetaModel fi, List<Object[]> rowData, List<PeriodMetaModel> periods) {
        Row row = sheet.createRow(rowIdx);

        Cell fiCell = row.createCell(1);
        fiCell.setCellValue(fi.getCode() + " | " + fi.getName());
        setCellStyle(wb, fiCell, false, false);

        for (int i = 2; i <= periods.size() + 1; i++) {
            Cell cell = row.createCell(i);
            setCellStyle(wb, cell, false, true);
        }

        for (Object[] obj : rowData) {
            int colIdx = 2;
            PeriodMetaModel period = periods.get(0);

            for (int i = 0; i < periods.size(); i++) {
                if (periods.get(i).getId() == (Long) obj[1]) {
                    colIdx = i + 2;
                    period = periods.get(i);
                    break;
                }
            }

            Date uploadTime = (Date) obj[3];
            Long delay = Long.valueOf((Integer) obj[2]);
            Date overdueDate = new Date(period.getToDate().getTime() + delay * 24 * 60 * 60 * 1000);

            Cell cell = row.getCell(colIdx);
            cell.setCellValue(uploadTime);

            setCellStyle(wb, cell, uploadTime.after(overdueDate), true);
        }
    }

    private static void setCellStyle(Workbook wb, Cell cell, boolean isOverdue, boolean alignCenter) {
        CellStyle style = wb.createCellStyle();
        CreationHelper createHelper = wb.getCreationHelper();
        style.setDataFormat(
                createHelper.createDataFormat().getFormat("dd/MM/yyyy HH:mm:ss"));

        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        Font font = wb.createFont();
        font.setColor(isOverdue ? IndexedColors.RED.getIndex() : IndexedColors.BLACK.getIndex());
        style.setFont(font);

        if (alignCenter) {
            style.setAlignment(HorizontalAlignment.CENTER);
        }

        cell.setCellStyle(style);
    }

    private static void setPeriodCellStyle(Workbook wb, Cell cell) {
        CellStyle style = wb.createCellStyle();

        style.setWrapText(true);
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
    }

    private static void createPeriodDimension(Workbook wb, Sheet sheet, List<PeriodMetaModel> periods, int rowIdx) {
        Row row = sheet.createRow(rowIdx);
        int colIdx = 1;
        setPeriodCellStyle(wb, row.createCell(colIdx++));
        for (PeriodMetaModel periodModel : periods) {
            Cell cell = row.createCell(colIdx++);
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            cell.setCellValue(df.format(periodModel.getFromDate()) + " - " + df.format(periodModel.getToDate()));
            setPeriodCellStyle(wb, cell);
        }
    }

    private static void createTitle(Workbook wb, Sheet sheet, ReturnTypeModel returnTypeModel) {
        Row row = sheet.createRow(0);
        row.setHeightInPoints(18);
        Cell cell = row.createCell(1);
        cell.setCellValue(returnTypeModel.getCode() + " | " + returnTypeModel.getName());
        setTitleCellStyle(wb, cell);
    }

    private static void setTitleCellStyle(Workbook wb, Cell cell) {
        CellStyle style = wb.createCellStyle();

        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);

        cell.setCellStyle(style);
    }

}
