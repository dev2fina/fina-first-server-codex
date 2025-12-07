package net.fina.server.returns.util;

import net.fina.common.client.returns.OverdueReturnModel;
import net.fina.messages.MessagesUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class OverdueReturnExportUtil {

    private static final String DEFAULT_DATE_PATTERN = "dd/MM/yyyy";

    public static byte[] exportToExcel(List<OverdueReturnModel> fiMetaModels) throws IOException {
        return exportToExcel(fiMetaModels, DEFAULT_DATE_PATTERN);
    }

    public static byte[] exportToExcel(List<OverdueReturnModel> fiMetaModels, String simpleDatePattern) throws IOException {
        simpleDatePattern = (simpleDatePattern != null && !simpleDatePattern.trim().isEmpty() ? simpleDatePattern : DEFAULT_DATE_PATTERN);
        final DateFormat UPLOADED_TIME_DATE_FORMAT = new SimpleDateFormat(simpleDatePattern + " HH:mm:ss");
        final DateFormat DATE_FORMAT = new SimpleDateFormat(simpleDatePattern);

        int startCol = 1;
        int startRow = 5;
        Workbook wb = new XSSFWorkbook();

        Sheet sheet = wb.createSheet(MessagesUtil.getString("net.fina.menu.returns.overdue"));

        Row row = sheet.createRow(startRow++);

        createHeader(wb, row, startCol);
        row.setHeight((short) (row.getHeight() * 2));

        CellStyle rowStyle = wb.createCellStyle();
        rowStyle.setWrapText(true);

        for (int i = 0; i < fiMetaModels.size(); i++) {
            OverdueReturnModel model = fiMetaModels.get(i);
            row = sheet.createRow(startRow++);
            row.setRowStyle(rowStyle);

            Date fromDate = model.getFromDate();
            Date toDate = model.getToDate();
            Date uploadedTime = model.getUploadedTime();

            String[] values = {
                    String.valueOf(i + 1),
                    model.getName(),
                    model.getFiCode(),
                    model.getFiName(),
                    model.getFiType(),
                    model.getRegion(),
                    model.getAddress(),
                    model.getFiIdentificationCode(),
                    model.getFiLegalForm(),
                    model.getPeriodType(),
                    model.getReturnVersion(),
                    fromDate != null ? DATE_FORMAT.format(fromDate) : "",
                    toDate != null ? DATE_FORMAT.format(toDate) : "",
                    Integer.toString(model.getDueDate()),
                    Integer.toString(model.getDueDateHour()),
                    Integer.toString(model.getDueDateMinute()),
                    uploadedTime != null ? UPLOADED_TIME_DATE_FORMAT.format(uploadedTime) : "",
                    Double.toString(model.getDelay())
            };

            createCell(wb, row, startCol, false, values);
        }

        setAutoSizeColumn(sheet, 1, 16);

        // Write the output to a file
        ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
        wb.write(fileOut);
        fileOut.close();
        return fileOut.toByteArray();
    }

    private static void createHeader(Workbook wb, Row row, int cellIdex) {
        List<String> headerCodes = Arrays.asList("#",
                "net.fina.name",
                "net.fina.fiCode",
                "net.fina.fi.name",
                "net.fina.fiType",
                "net.fina.fi.region",
                "net.fina.address",
                "net.fina.identificationCode",
                "net.fina.legalForm",
                "net.fina.periodType",
                "net.fina.server.returns.entity.ReturnVersion",
                "net.fina.fromDate",
                "net.fina.toDate",
                "net.fina.dueDate",
                "net.fina.dueDateHour",
                "net.fina.dueDateMinute",
                "net.fina.returns.status.uploaded",
                "net.fina.delay"
        );

        for (String headerName : headerCodes) {
            createCell(wb, row, cellIdex++, true, MessagesUtil.getString(headerName));
        }

    }

    private static void createCell(Workbook wb, Row row, int cellIdex, boolean header, String... values) {
        for (int i = 0; i < values.length; i++) {
            Cell cell = row.createCell(cellIdex + i);
            cell.setCellValue(values[i]);
            setCellStyle(wb, cell, header);
        }
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

    private static void setAutoSizeColumn(Sheet sheet, int from, int to) {
        for (int i = from; i <= to; i++) {
            sheet.autoSizeColumn(i);
        }
    }

}
