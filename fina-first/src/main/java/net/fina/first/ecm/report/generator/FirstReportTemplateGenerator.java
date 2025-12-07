package net.fina.first.ecm.report.generator;

import net.fina.first.ecm.report.model.FirstReportConfig;
import net.fina.messages.MessagesUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.jboss.logging.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

public class FirstReportTemplateGenerator {
    private final Logger log = Logger.getLogger(getClass());

    private ResourceBundle messageBundle;

    /**
     * Generate report based on configuration.
     *
     * @param config configuration
     * @return generated report as byte array
     * @throws Exception IO Exception
     */
    public byte[] generate(FirstReportConfig config) throws Exception {

        InputStream in = new ByteArrayInputStream(config.getTemplate());
        Workbook workbook = WorkbookFactory.create(in);
        messageBundle = MessagesUtil.loadMessageBundle(config.getLangCode());

        Sheet sheet = workbook.getSheetAt(0);

        replaceKeys(sheet);

        generateTemplate(workbook, sheet, config.getData(), config);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        workbook.write(bos);

        return bos.toByteArray();
    }

    private void replaceKeys(Sheet sheet) {
        for (Row row : sheet) {
            for (Cell cell : row) {
                initGeneratedDate(cell);
            }
        }
    }

    private void initGeneratedDate(Cell cell) {
        try {
            if ("[=generatedDate]".equals(cell.getStringCellValue())) {
                String formattedDate = new SimpleDateFormat("dd/MM/yyy").format(new Date());
                cell.setCellValue(formattedDate);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void generateTemplate(Workbook workbook, Sheet sheet, List<Map<String, Object>> data, FirstReportConfig config) {

        int rowIndex = config.getStartRow();
        int celIndex;
        Row startRow = sheet.getRow(config.getStartRow()) == null ? sheet.createRow(config.getStartRow()) : sheet.getRow(config.getStartRow());
        for (Map<String, Object> map : data) {
            Row row = startRow.getRowNum() == rowIndex ? startRow : sheet.createRow(rowIndex);
            rowIndex++;
            celIndex = config.getStartColumn();

            Cell cell;
            Cell oldCell;
            if (config.isEnableRowNumbering()) {
                cell = row.getCell(celIndex) != null ? row.getCell(celIndex) : row.createCell(celIndex);
                oldCell = startRow.getCell(celIndex) == null ? row.createCell(celIndex) : startRow.getCell(celIndex);
                // Copy style from old cell and apply to new cell
                CellStyle newCellStyle = workbook.createCellStyle();
                newCellStyle.cloneStyleFrom(oldCell.getCellStyle());

                cell.setCellStyle(newCellStyle);
                cell.setCellType(oldCell.getCellType());

                celIndex++;
                cell.setCellValue(rowIndex - config.getStartRow());
            }

            for (Map.Entry<String, Object> e : map.entrySet()) {
                cell = row.getCell(celIndex) != null ? row.getCell(celIndex) : row.createCell(celIndex);
                oldCell = startRow.getCell(celIndex) == null ? row.createCell(celIndex) : startRow.getCell(celIndex);
                // Copy style from old cell and apply to new cell
                CellStyle newCellStyle = workbook.createCellStyle();
                newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
                cell.setCellStyle(newCellStyle);
                cell.setCellType(oldCell.getCellType());

                celIndex++;
                setCellValue(cell, e.getValue());
            }
        }

        if (config.getDateCellAddress() != null && !config.getDateCellAddress().equals("")) {
            CellReference cr = new CellReference(config.getDateCellAddress().trim());
            Row row = sheet.getRow(cr.getRow());
            Cell cell = row.getCell(cr.getCol());
            cell.setCellValue(new SimpleDateFormat("dd/MM/yyy").format(new Date()));
        }


    }

    private void setCellValue(Cell cell, Object value) {
        if (value != null) {
            if (value instanceof String) {
                cell.setCellValue(getString((String) value));
            } else if (value instanceof Integer) {
                cell.setCellValue((Integer) value);
            } else if (value instanceof Date) {
                String formatedDate = new SimpleDateFormat("dd/MM/yyy").format((Date) value);
                cell.setCellValue(formatedDate);
            } else if (value instanceof Long) {
                cell.setCellValue((Long) value);
            } else if (value instanceof Double) {
                cell.setCellValue((Double) value);
            } else if (value instanceof Boolean) {
                cell.setCellValue(((Boolean) value) ?
                        getString("OK") : getString("NO"));
            } else if (value instanceof Float) {
                cell.setCellValue((Float) value);
            }
        } else {
            cell.setCellValue("");
        }

    }

    public String getString(String key) {
        String value;
        try {
            value = messageBundle.getString(key);
        } catch (MissingResourceException e) {
            value = key;
        }
        return value;
    }
}
