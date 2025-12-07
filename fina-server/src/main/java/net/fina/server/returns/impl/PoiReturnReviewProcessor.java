package net.fina.server.returns.impl;

import net.fina.common.client.mdt.MDTNodeDataTypes;
import net.fina.common.client.mdt.MDTNodeTypes;
import net.fina.common.client.returns.ProcessReturnInfo;
import net.fina.common.server.StatisticsLogger;
import net.fina.server.returns.converter.ConvertOptions;
import net.fina.server.returns.converter.PoiConverterFactory;
import net.fina.server.returns.converter.api.PoiConverter;
import net.fina.server.returns.model.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.jboss.logging.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.util.*;

public class PoiReturnReviewProcessor {
    private static final Logger log = Logger.getLogger(PoiReturnReviewProcessor.class.getName());
    private ReturnReviewConfigModel configModel;
    private Sheet currentSheet;
    private ProcessReturnInfo currentProcessReturnInfo;
    private Workbook workbook;

    public PoiReturnReviewProcessor(ReturnReviewConfigModel configModel) {
        this.configModel = configModel;
    }

    private void copyTemplateRow(XSSFSheet sheet, int templateRowIdx, int destStartIdx, int times, int maxColumn) {
        XSSFRow srcRow = sheet.getRow(templateRowIdx);
        if (srcRow == null) return;

        // Cache styles: srcStyleIdx -> clonedStyle (avoid creating tons of styles)
        Map<Short, XSSFCellStyle> styleCache = new HashMap<>();

        // Insert destination rows first so merged regions stay consistent
        if (times > 0) {
            sheet.shiftRows(destStartIdx, sheet.getLastRowNum(), times, true, false);
        }

        for (int k = 0; k < times; k++) {
            int destRowIdx = destStartIdx + k;
            XSSFRow destRow = sheet.createRow(destRowIdx);
            destRow.setHeight(srcRow.getHeight());

            for (int c = srcRow.getFirstCellNum(); c >= 0 && c <= maxColumn; c++) {
                XSSFCell srcCell = srcRow.getCell(c);
                if (srcCell == null) continue;

                boolean hasValue = srcCell.getCellType() != CellType.BLANK || (srcCell.getCellType() == CellType.STRING && srcCell.getStringCellValue() != null && !srcCell.getStringCellValue().isEmpty());

                boolean hasNonDefaultStyle = srcCell.getCellStyle().getIndex() != 0;

                // Skip true blanks with default style
                if (!hasValue && !hasNonDefaultStyle) continue;

                XSSFCell destCell = destRow.createCell(c);

                // Copy value/formula only when present
                switch (srcCell.getCellType()) {
                    case STRING:
                        destCell.setCellValue(srcCell.getStringCellValue());
                        break;
                    case NUMERIC:
                        destCell.setCellValue(srcCell.getNumericCellValue());
                        break;
                    case BOOLEAN:
                        destCell.setCellValue(srcCell.getBooleanCellValue());
                        break;
                    case FORMULA:
                        destCell.setCellFormula(srcCell.getCellFormula());
                        break;
                    case ERROR:
                        destCell.setCellErrorValue(srcCell.getErrorCellValue());
                        break;
                    default: /* BLANK */
                        break;
                }

                // Copy style only if non-default (saves tons of time & memory)
                if (hasNonDefaultStyle) {
                    short idx = srcCell.getCellStyle().getIndex();
                    XSSFCellStyle cloned = styleCache.computeIfAbsent(idx, i -> {
                        XSSFCellStyle cs = sheet.getWorkbook().createCellStyle();
                        cs.cloneStyleFrom(srcCell.getCellStyle());
                        return cs;
                    });
                    destCell.setCellStyle(cloned);
                }
            }
        }
    }

    public byte[] process() {
        ensureTemplateNotNull();

        try (StatisticsLogger statisticsLogger = new StatisticsLogger("Poi Return Review"); InputStream in = new ByteArrayInputStream(configModel.getTemplate()); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            statisticsLogger.logMessage("Start Return Review Process");
            statisticsLogger.logStage("Opening Template");

            workbook = WorkbookFactory.create(in);
            List<String> usedSheetNames = new ArrayList<>();

            statisticsLogger.logStage("process templates");
            for (Map.Entry<ProcessReturnInfo, RDataMetaModel> entry : configModel.getInfoRDataMetaModelMap().entrySet()) {
                statisticsLogger.logStage("Processing " + entry.getValue().getReturnCode() + " Return Tables");
                RDataMetaModel currentRdataMetaModel = entry.getValue();
                currentProcessReturnInfo = entry.getKey();
                currentSheet = workbook.getSheet(entry.getValue().getReturnCode());
                currentSheet = currentSheet == null ? workbook.createSheet(currentRdataMetaModel.getReturnCode()) : currentSheet;
                usedSheetNames.add(currentRdataMetaModel.getReturnCode().toLowerCase());

                fillReturnSheet(currentRdataMetaModel);

            }

            //remove sheets
            List<Integer> toBeRemovedSheetsIndexes = new ArrayList<>();
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                if (!usedSheetNames.contains(sheet.getSheetName().toLowerCase())) {
                    toBeRemovedSheetsIndexes.add(i);
                }
            }
            toBeRemovedSheetsIndexes.sort(new Comparator<Integer>() {
                @Override
                public int compare(Integer t1, Integer t2) {
                    return t2.compareTo(t1);
                }
            });
            toBeRemovedSheetsIndexes.forEach(workbook::removeSheetAt);

            if (configModel.getConvertOptions() != null) {
                statisticsLogger.logStage("Converting To HTML ");
                return convert(configModel.getConvertOptions());
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (Exception t) {
            log.error(t.getMessage(), t);
        }
        return null;
    }

    public byte[] convert(ConvertOptions options) {
        PoiConverter poiConverter = PoiConverterFactory.create(options);
        return poiConverter.convert(this.workbook);
    }

    private void ensureTemplateNotNull() {
        Workbook wb;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            if (configModel.getTemplate() == null) {
                wb = new XSSFWorkbook();
                for (Map.Entry<ProcessReturnInfo, RDataMetaModel> entry : configModel.getInfoRDataMetaModelMap().entrySet()) {
                    wb.createSheet(entry.getValue().getReturnCode());
                }
                wb.write(out);
                byte[] emptyTemplate = out.toByteArray();
                configModel = new ReturnReviewConfigModel.Builder()
                        .infoRDataMetaModelMap(configModel.getInfoRDataMetaModelMap()).langId(configModel.getLangId()).df(configModel.getDf(), configModel.getDateTimeFormat())
                        .tables(configModel.getTables())
                        .template(emptyTemplate)
                        .convertOptions(configModel.getConvertOptions())
                        .build();
                wb.close();

            }
        } catch (Exception t) {
            log.error(t.getMessage(), t);
        }
    }

    private void fillReturnSheet(RDataMetaModel rDataMetaModel) {
        setHeader(rDataMetaModel);
        CellCoordinate cellCoordinate = new CellCoordinate(1, 10);
        for (RTableMetaModel table : rDataMetaModel.getTables()) {
            placeTable(table, cellCoordinate);
        }
    }

    private void placeTable(RTableMetaModel table, CellCoordinate cellCoordinate) {
        switch (table.getType()) {
            case NT,MCT:
                placeMCT(table, cellCoordinate);
                break;
            case VCT:
                placeVCT(table, cellCoordinate);
                break;
            default:
                break;
        }
    }

    private void placeMCT(RTableMetaModel table, CellCoordinate cellCoordinate) {
        for (int rowIndex = 0; rowIndex < table.getRows().size(); rowIndex++) {
            RTableRowMetaModel tableRow = table.getRows().get(rowIndex);
            for (int columnIndex = 0; columnIndex < tableRow.getRowItems().size(); columnIndex++) {
                RItemMetaModel pItem = tableRow.getRowItems().get(columnIndex);
                if (pItem != null) {
                    if (rowIndex == 0 || columnIndex == 0) {
                        String value = pItem.getDescription() != null ? pItem.getDescription() : "";
                        Cell cell = getSafeCell(cellCoordinate.getRow() + rowIndex, cellCoordinate.getColumn() + columnIndex);
                        cell.setCellValue(value);
                    } else {
                        CellCoordinate offset = cellCoordinate.offset(columnIndex, rowIndex);
                        Cell cell = getSafeCell(offset.getRow(), offset.getColumn());
                        setCellValue(pItem, cell);
                    }
                }
            }
        }
        cellCoordinate.setRow(cellCoordinate.getRow() + table.getRows().size() + 4);
    }

    private void placeVCT(RTableMetaModel table, CellCoordinate coordinate) {
        boolean totalTop = table.getVisibleLevel() > 10;

        if (table.getRows().size() > 2) {
            int existingRows = currentSheet.getLastRowNum();

            int rowsToAddFrom = Math.min(existingRows, coordinate.getRow() + 2);

            int rowsToAdd = table.getRows().size() - 2;

            if (rowsToAddFrom < coordinate.getRow() + 2) {
                rowsToAdd += (coordinate.getRow() + 2) - rowsToAddFrom;
            }

            shiftAndCOpyRows(table, rowsToAddFrom - 1, rowsToAdd, totalTop);
        }

        if (totalTop) {
            setVctTotal(table, new CellCoordinate(coordinate.getColumn(), coordinate.getRow() + 1));
            coordinate.setRow(coordinate.getRow() + 1);
        }

        for (int rowIndex = 0; rowIndex < table.getRows().size(); rowIndex++) {
            RTableRowMetaModel tableRow = table.getRows().get(rowIndex);
            for (int columnIndex = 0; columnIndex < tableRow.getRowItems().size(); columnIndex++) {
                RItemMetaModel pItem = tableRow.getRowItems().get(columnIndex);
                if (pItem != null) {
                    if (rowIndex == 0) {
                        String value = pItem.getDescription() != null ? pItem.getDescription() : "";

                        int titleRow = coordinate.getRow() + rowIndex;

                        if (totalTop) {
                            titleRow -= 1;
                        }
                        setCellStringValue(new CellCoordinate(coordinate.getColumn() + columnIndex, titleRow), value);
                    } else {
                        CellCoordinate offset = coordinate.offset(columnIndex, rowIndex);
                        Cell cell = getSafeCell(offset.getRow(), offset.getColumn());
                        setCellValue(pItem, cell);
                    }
                }
            }
        }

        coordinate.setRow(coordinate.getRow() + table.getRows().size());


        if (!totalTop) {
            setVctTotal(table, coordinate);
        }

        coordinate.setRow(coordinate.getRow() + (!totalTop ? 4 : 3));
    }

    private void shiftAndCOpyRows(RTableMetaModel table, int templateRowIdx, int rowsToAdd, boolean totalTop) {

        XSSFSheet xssfSheet = (XSSFSheet) this.currentSheet;
        CellCopyPolicy cellCopyPolicy = new CellCopyPolicy();
        cellCopyPolicy.setCondenseRows(true);

        int insertAt = templateRowIdx + 1;
        /*
         * if formatted bad there may be hundreds of empty cells with style,which then slows down copying rows
         * we assume and cut columns after column size + 10 columns
         * */
        int maxColIndex = table.getRows().getFirst().getRowItems().size() + 10;

        copyTemplateRow(xssfSheet, totalTop ? insertAt : templateRowIdx, insertAt, rowsToAdd, maxColIndex);

        for (int i = templateRowIdx - 1; i <= templateRowIdx - 1 + rowsToAdd; i++) {
            CellStyle rowStyle = this.currentSheet.getRow(i) != null ? this.currentSheet.getRow(i).getRowStyle() : null;
            if (rowStyle != null) {
                Row currentSheetRow = this.currentSheet.getRow(i + templateRowIdx);
                if (currentSheetRow != null) {
                    currentSheetRow.setRowStyle(rowStyle);
                }
            }
        }
    }

    private void shiftAndCOpyRows(int rowsToAddFrom, int rowsToAdd) {
        currentSheet.shiftRows(rowsToAddFrom, currentSheet.getLastRowNum(), rowsToAdd, true, false);

        XSSFSheet xssfSheet = (XSSFSheet) this.currentSheet;
        CellCopyPolicy cellCopyPolicy = new CellCopyPolicy();
        cellCopyPolicy.setCondenseRows(true);
        for (int i = rowsToAddFrom - 1; i < rowsToAddFrom - 1 + rowsToAdd; i++) {
            xssfSheet.copyRows(rowsToAddFrom + rowsToAdd, rowsToAddFrom + rowsToAdd, i + 1, cellCopyPolicy);
        }

        for (int i = rowsToAddFrom - 1; i <= rowsToAddFrom - 1 + rowsToAdd; i++) {
            CellStyle rowStyle = this.currentSheet.getRow(i) != null ? this.currentSheet.getRow(i).getRowStyle() : null;
            if (rowStyle != null) {
                Row currentSheetRow = this.currentSheet.getRow(i + rowsToAddFrom);
                if (currentSheetRow != null) {
                    currentSheetRow.setRowStyle(rowStyle);
                }
            }
        }


    }

    private void setCellStringValue(CellCoordinate coordinate, String value) {
        Cell cell = getSafeCell(coordinate.getRow(), coordinate.getColumn());
        cell.setCellValue(value);
    }

    private void setVctTotal(RTableMetaModel table, CellCoordinate coordinate) {
        JSTreeAggregate jsTree = new JSTreeAggregate(currentProcessReturnInfo);
        if (table.getEvalMethod() != null) {
            for (int i = 0; i < table.getRows().get(0).getRowItems().size(); i++) {
                RItemMetaModel pItem = table.getRows().get(0).getRowItems().get(i);

                if (pItem.getDataType() == MDTNodeDataTypes.NUMERIC || pItem.getNodeType() == MDTNodeTypes.VARIABLE.ordinal()) {
                    List<RItemMetaModel> columnItems = getColumnProcessItems(table, i);

                    Double value = null;

                    switch (table.getEvalMethod()) {
                        case SUM:
                            value = jsTree.evalSum(columnItems);
                            break;
                        case AVERAGE:
                            value = jsTree.evalAverage(columnItems);
                            break;
                        case MAX:
                            value = jsTree.evalMax(columnItems);
                            break;
                        case MIN:
                            value = jsTree.evalMin(columnItems);
                            break;
                    }
                    if (value != null) {
                        Cell cell = getSafeCell(coordinate.getRow(), coordinate.getColumn() + i);
                        cell.setCellValue(value);
                    }
                }
            }
        }
    }

    private void setCellValue(RItemMetaModel pItem, Cell cell) {
        String value = pItem.getValue();
        if ((pItem.getDataType() == MDTNodeDataTypes.NUMERIC || pItem.getNodeType() == MDTNodeTypes.VARIABLE.ordinal()) && value != null) {
            try {
                double parsedValue = Double.parseDouble(pItem.getValue());
                cell.setCellValue(Double.isNaN(parsedValue) ? 0.0 : parsedValue);
            } catch (NumberFormatException ex) {
                cell.setCellValue(pItem.getNvalue());
            }
        } else if (pItem.getDataType() == MDTNodeDataTypes.DATE && value != null) {
            try {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(configModel.getDf().parse(value));
                cell.setCellValue(calendar);
            } catch (ParseException e) {
                cell.setCellValue(value);
            }
        } else if (pItem.getDataType() == MDTNodeDataTypes.DATE_TIME && value != null) {
            try {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(configModel.getDateTimeFormat().parse(value));
                cell.setCellValue(calendar);
            } catch (ParseException e) {
                cell.setCellValue(value);
            }
        } else {
            cell.setCellValue(value);
        }
    }

    private Map<String, String> getHeaderData(RDataMetaModel rDataMetaModel) {
        Map<String, String> headerDataMap = new HashMap<>();

        headerDataMap.put(ReturnFormatConstants.FI_CODE, rDataMetaModel.getFiCode());
        headerDataMap.put(ReturnFormatConstants.FI_NAME, rDataMetaModel.getFiDescription());

        headerDataMap.put(ReturnFormatConstants.RETURN_CODE, rDataMetaModel.getReturnCode());
        headerDataMap.put(ReturnFormatConstants.RETURN_NAME, rDataMetaModel.getReturnDescription());
        headerDataMap.put(ReturnFormatConstants.RETURN_STATUS, rDataMetaModel.getStatusName());

        headerDataMap.put(ReturnFormatConstants.RETURN_TYPE_CODE, rDataMetaModel.getReturnTypeCode());

        headerDataMap.put(ReturnFormatConstants.RETURN_VERSION_CODE, rDataMetaModel.getVersionCode());
        headerDataMap.put(ReturnFormatConstants.RETURN_VERSION_NAME, rDataMetaModel.getVersionDescription());

        headerDataMap.put(ReturnFormatConstants.PERIOD_FROM, configModel.getDf().format(rDataMetaModel.getFromDate()));
        headerDataMap.put(ReturnFormatConstants.PERIOD_TO, configModel.getDf().format(rDataMetaModel.getToDate()));
        headerDataMap.put(ReturnFormatConstants.PERIOD_TYPE_CODE, rDataMetaModel.getPeriodTypeCode());
        headerDataMap.put(ReturnFormatConstants.PERIOD_TYPE_NAME, rDataMetaModel.getPeriodTypeDescription());

        headerDataMap.put(ReturnFormatConstants.USER_LOGIN, rDataMetaModel.getUserLogin());
        headerDataMap.put(ReturnFormatConstants.USER_NAME, rDataMetaModel.getUserName());

        return headerDataMap;
    }

    private void setHeader(RDataMetaModel rDataMetaModel) {
        Map<String, String> headerDataMap = getHeaderData(rDataMetaModel);
        for (int i = 0; i < ReturnFormatConstants.MAX_COLUMN; i++) {
            for (int j = 0; j < ReturnFormatConstants.MAX_ROW; j++) {
                String value = getStingCellValue(new CellCoordinate(i, j));
                if (value != null && (!value.isEmpty())) {
                    for (Map.Entry<String, String> e : headerDataMap.entrySet()) {
                        if (value.contains(e.getKey())) {
                            value = value.replace(e.getKey(), e.getValue() == null ? "" : e.getValue());
                            Cell cell = getSafeCell(j, i);
                            cell.setCellValue(value);
                        }
                    }
                }
            }
        }
    }

    String getStingCellValue(CellCoordinate cellCoordinate) {
        Cell cell = getSafeCell(cellCoordinate.getRow(), cellCoordinate.getColumn());
        switch (cell.getCellType()) {
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case _NONE:
            case BLANK:
            case ERROR:
                return "";
            case STRING:
                return cell.getStringCellValue();
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return String.valueOf(cell.getCellFormula());
        }

        return cell.getStringCellValue();
    }

    private List<RItemMetaModel> getColumnProcessItems(RTableMetaModel table, int columnIndex) {
        List<RItemMetaModel> items = new ArrayList<>();
        for (int i = 1; i < table.getRows().size(); i++) {
            RTableRowMetaModel row = table.getRows().get(i);
            items.add(row.getRowItems().get(columnIndex));
        }
        return items;
    }

    private Cell getSafeCell(int rowNum, int columnNum) {
        Row row = currentSheet.getRow(rowNum);
        if (row == null) {
            row = currentSheet.createRow(rowNum);
        }
        Cell cell = row.getCell(columnNum);
        if (cell == null) {
            cell = row.createCell(columnNum);
        }
        return cell;
    }

}
