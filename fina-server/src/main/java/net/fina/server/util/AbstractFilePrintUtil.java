package net.fina.server.util;


import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.exception.OfficeTypeException;
import net.fina.common.shared.ContentModel;
import net.fina.server.reports.util.ReportPrintUtil;
import net.fina.server.returns.converter.ConvertOptions;
import net.fina.server.returns.converter.PoiConverterFactory;
import net.fina.server.returns.converter.api.PoiConverter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jboss.logging.Logger;

import javax.swing.table.TableModel;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractFilePrintUtil {
    private static final Map<String, CellStyle> styleCache = new HashMap<>();
    private static final Logger log = Logger.getLogger(AbstractFilePrintUtil.class.getName());
    private static final String DEFAULT_SHEET_NAME = "Data";

    public static ContentModel generateFile(String fileType, TableModel tableModel, String contentPath, String reportName) throws FinATypeException {
        byte[] content;
        try {
            switch (fileType.toLowerCase()) {
                case "html" -> content = generateHTML(tableModel);
                case "csv" -> content = generateCSV(tableModel);
                default -> content = generateXLSX(tableModel, DEFAULT_SHEET_NAME);
            }

            return ReportPrintUtil.print(null, content, fileType, reportName, contentPath);
        } catch (IOException | OfficeTypeException e) {
            log.error(e.getMessage(), e);
        }
        throw new FinATypeException(FinATypeException.Type.GENERAL_ERROR);
    }

    protected static byte[] generateHTML(TableModel tableModel) {
        PoiConverter converter = PoiConverterFactory.create(ConvertOptions.HTML);
        if (converter != null) {
            return converter.convert(generateWorkbook(tableModel));
        }
        return new byte[0];
    }

    public static byte[] generateXLSX(TableModel tableModel, String sheetName) throws IOException {
        try (Workbook workbook = generateWorkbook(tableModel, sheetName);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            workbook.write(baos);
            return baos.toByteArray();
        }
    }

    protected static byte[] generateCSV(TableModel tableModel) {
        PoiConverter converter = PoiConverterFactory.create(ConvertOptions.CSV);
        if (converter != null) {
            return converter.convert(generateWorkbook(tableModel));
        }
        return new byte[0];
    }

    protected static Workbook generateWorkbook(TableModel tableModel) {
        return generateWorkbook(tableModel, DEFAULT_SHEET_NAME);
    }

    protected static Workbook generateWorkbook(TableModel tableModel, String sheetName) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(sheetName);

        CellStyle headerStyle = createCellStyle(workbook, true);
        styleCache.put("header", headerStyle);
        CellStyle normalStyle = createCellStyle(workbook, false);
        styleCache.put("row", normalStyle);

        Row headerRow = sheet.createRow(0);
        for (int col = 0; col < tableModel.getColumnCount(); col++) {
            Cell cell = headerRow.createCell(col);
            cell.setCellValue(tableModel.getColumnName(col));
            cell.setCellStyle(styleCache.get("header"));
        }

        for (int row = 0; row < tableModel.getRowCount(); row++) {
            Row dataRow = sheet.createRow(row + 1);
            for (int col = 0; col < tableModel.getColumnCount(); col++) {
                Cell cell = dataRow.createCell(col);
                String cellValue = getSafeValueAt(tableModel, row, col);
                cell.setCellValue(cellValue);
                cell.setCellStyle(styleCache.get("row"));
            }
        }
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            sheet.autoSizeColumn(i);
        }
        return workbook;
    }

    public static CellStyle createCellStyle(Workbook wb, boolean header) {
        CellStyle style = wb.createCellStyle();
        style.setWrapText(true);
        if (header) {
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font font = wb.createFont();
            font.setBold(true);
            style.setFont(font);
        } else {
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
        }
        return style;
    }

    protected static String getSafeValueAt(TableModel tableModel, int row, int col) {
        Object value = tableModel.getValueAt(row, col);
        return value != null ? value.toString() : "";
    }
}