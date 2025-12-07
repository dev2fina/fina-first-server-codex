package net.fina.server.returns.util;

import fina2.ui.returns.ValuesTableRow;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.returns.ReturnTableType;
import net.fina.server.returns.entity.DefinitionTable;
import net.fina.server.returns.entity.ReturnDefinition;
import net.fina.server.util.ReturnFormatHelper;
import org.apache.poi.ss.usermodel.*;
import org.jboss.logging.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReturnFormatTemplatePOIHelper {

    private static final Logger log = Logger.getLogger(ReturnFormatTemplatePOIHelper.class.getName());
    private final long langId;
    private final List<ReturnDefinition> returnDefinitions;
    int row = 10;
    int column = 1;
    private byte[] returnFormat;

    public ReturnFormatTemplatePOIHelper(List<ReturnDefinition> returnDefinitions, byte[] returnFormat, long langId) {
        this.returnDefinitions = returnDefinitions;
        this.returnFormat = returnFormat;
        this.langId = langId;
    }

    public byte[] executeReturnTemplateProcessor() throws FinATypeException {
        List<String> returnCodes = returnDefinitions.stream().map(ReturnDefinition::getCode).toList();
        if (returnFormat == null) {
            returnFormat = createEmptyTemplate();
        }

        try (Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(returnFormat));
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Set<String> sheetNames = new HashSet<>();
            for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                String sheetName = wb.getSheetName(i).trim();
                if (!returnCodes.contains(sheetName)) {
                    sheetNames.add(sheetName);
                }
            }


            for (String sheetName : sheetNames) {
                wb.removeSheetAt(wb.getSheetIndex(sheetName));
            }

            if (wb.getNumberOfSheets() == 0) {
                wb.createSheet("Empty");
            }

            wb.write(out);

            return out.toByteArray();
        } catch (Exception t) {
            log.error(t.getMessage(), t);
            throw new FinATypeException("Failed to extract sheets by return codes.");
        }

    }


    private byte[] createEmptyTemplate() throws FinATypeException {

        try (Workbook wb = WorkbookFactory.create(true); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            //create sheets
            for (ReturnDefinition definition : returnDefinitions) {
                row = 10;
                column = 1;

                Sheet sheet = wb.createSheet(definition.getCode());
                for (DefinitionTable table : definition.getDefinitionTables()) {
                    placeTableForTemplate(table, sheet, langId);
                }
            }

            wb.write(out);
            return out.toByteArray();

        } catch (Exception t) {
            log.error(t.getMessage(), t);
        }
        throw new FinATypeException("Failed to create empty template.");
    }


    private void placeTableForTemplate(DefinitionTable table, Sheet sheet, long langId) {
        ReturnFormatHelper returnFormatHelper = new ReturnFormatHelper();
        List<ValuesTableRow> rows = returnFormatHelper.getReviewTableFormatRows(table, langId);

        createHeader(table, sheet, langId);

        int tableStartRow = row;
        int rc = 0;

        if (!rows.isEmpty()) {
            ValuesTableRow title = rows.getFirst();
            Object[][] data = new Object[rows.size()][title.getColumnCount()];
            rows.remove(title);

            for (int i = 0; i < title.getColumnCount(); i++) {
                data[rc][i] = title.getValue(i);
            }
            rc++;
            row += 2;

            for (ValuesTableRow roww : rows) {
                for (int i = 0; i < title.getColumnCount(); i++) {
                    if (i > 0) {
                        data[rc][i] = roww.getCode(i) + " (" + roww.getDataType(i) + "|" + roww.getType(i) + ")";
                    } else {
                        data[rc][i] = roww.getValue(i);
                    }
                }
                row++;
                rc++;
            }

            int rowOnSheet = tableStartRow;
            for (int j = 0; j < rc; j++) {
                for (int i = 0; i < title.getColumnCount(); i++) {
                    Object value = data[j][i] == null ? "" : data[j][i];

                    setCellValue(sheet, i + 1, rowOnSheet, (String) value);
                }
                rowOnSheet++;
            }

            if (table.getType() == ReturnTableType.VCT) {
                for (int i = 0; i < title.getColumnCount(); i++) {
                    setCellValue(sheet, i + 1, rowOnSheet, "");
                }
            }
            row += 3;
        }
    }

    private void createHeader(DefinitionTable table, Sheet sheet, long langId) {
        Row headerRow = sheet.createRow(6);
        Cell headerCell = headerRow.createCell(1);

        headerCell.setCellValue(table.getNode().getDescription().getDescription(langId) + " | Return Type: " + table.getType().name());
    }

    private void setCellValue(Sheet sheet, int colNUm, int rowNum, String value) {
        Row curRow = sheet.getRow(rowNum);
        if (curRow == null) {
            curRow = sheet.createRow(rowNum);
        }

        Cell cell = curRow.getCell(colNUm);
        if (cell == null) {
            cell = curRow.createCell(colNUm);
        }

        cell.setCellValue(value);
    }

}
