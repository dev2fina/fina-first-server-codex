package net.fina.server.classifier.util;

import net.fina.common.shared.LanguageSampleModel;
import net.fina.server.classifier.entity.DataType;
import net.fina.server.classifier.model.MDTCatalogColumnMetaModel;
import net.fina.server.classifier.model.MDTCatalogItemMetaModel;
import net.fina.server.classifier.model.MDTCatalogMetaModel;
import net.fina.server.classifier.model.MDTCatalogRowItemMetaModel;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MDTCatalogExportUtil {
    private final String DATE_FORMAT = "dd/MM/yyyy";

    public byte[] exportToExcelAdvanced(MDTCatalogMetaModel catalog, List<LanguageSampleModel> languages, List<MDTCatalogRowItemMetaModel> rowItemMetaModels) throws Exception {
        Workbook wb = new XSSFWorkbook();

        Sheet catalogSheet = wb.createSheet("CATALOG");
        Row catalogSheetRow = catalogSheet.createRow(1);
        catalogSheetRow.createCell(0).setCellValue("#");
        catalogSheetRow.createCell(1).setCellValue("CODE");
        catalogSheetRow.createCell(2).setCellValue("NAME");
        catalogSheetRow.createCell(3).setCellValue("ABBREVIATION");
        catalogSheetRow.createCell(4).setCellValue("SOURCE");
        catalogSheetRow.createCell(5).setCellValue("REFERENCE_NUMBER");

        catalogSheetRow = catalogSheet.createRow(2);
        catalogSheetRow.createCell(0).setCellValue(1);
        catalogSheetRow.createCell(1).setCellValue(catalog.getCode());
        catalogSheetRow.createCell(2).setCellValue(catalog.getName());
        catalogSheetRow.createCell(3).setCellValue(catalog.getAbbreviation());
        catalogSheetRow.createCell(4).setCellValue(catalog.getSource());
        catalogSheetRow.createCell(5).setCellValue(catalog.getReferenceNumber());
        for (LanguageSampleModel lang : languages) {
            String langCode = lang.getCode().split("_")[0].toUpperCase();
            createColumnSheet(wb, langCode, catalog);
            createDataSheet(wb, lang.getId(), langCode, catalog, rowItemMetaModels);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        wb.write(bos);

        byte[] content = bos.toByteArray();

        bos.close();
        wb.close();
        return content;
    }

    private void createDataSheet(Workbook wb, long langId, String langCode, MDTCatalogMetaModel catalog, List<MDTCatalogRowItemMetaModel> rowItemMetaModels) {
        Sheet sheet = wb.createSheet(catalog.getCode() + "_DATA^" + langCode);

        initDataSheet(wb, sheet, rowItemMetaModels, langId);
    }

    private void createColumnSheet(Workbook workbook, String langCode, MDTCatalogMetaModel catalog) {
        Sheet sheet = workbook.createSheet(catalog.getCode() + "_COLUMNS^" + langCode);
        Row headerRow = sheet.createRow(1);
        headerRow.createCell(0).setCellValue("#");
        headerRow.createCell(1).setCellValue("NAME");
        headerRow.createCell(2).setCellValue("DATA_TYPE");
        headerRow.createCell(3).setCellValue("IS_KEY");
        headerRow.createCell(4).setCellValue("FORMAT");

        for (int i = 0; i < catalog.getCatalogColumns().size(); i++) {
            Row row = sheet.createRow(i + 2);

            MDTCatalogColumnMetaModel col = catalog.getCatalogColumns().get(i);
            row.createCell(0).setCellValue(i + 1);
            row.createCell(1).setCellValue(col.getName());
            row.createCell(2).setCellValue(col.getDataType().name());
            row.createCell(3).setCellValue(col.isKey());
            row.createCell(4).setCellValue(col.getDataFormat());
        }
    }

    public byte[] exportToExcelSimple(MDTCatalogMetaModel catalog, List<MDTCatalogRowItemMetaModel> rowItemMetaModels) throws Exception {
        Workbook wb = new XSSFWorkbook();
        String catalogName = catalog.getName();
        Sheet sheet = wb.createSheet(catalogName.length() > 30 ? catalogName.substring(0, 27) + "..." : catalogName);

        initDataSheet(wb, sheet, rowItemMetaModels, -1);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        wb.write(bos);

        byte[] content = bos.toByteArray();

        bos.close();
        wb.close();
        return content;


    }

    private void initDataSheet(Workbook wb, Sheet sheet, List<MDTCatalogRowItemMetaModel> rowItemMetaModels, long langId) {
        List<MDTCatalogRowItemMetaModel> parentNodes = rowItemMetaModels.stream().filter(r -> r.getParentRowId() == 0).toList();

        Map<Long, Integer> rowIdExcelIdMap = new HashMap<>();

        int startRow = 1;
        int startColumn = 0;
        if (!rowItemMetaModels.isEmpty()) {
            MDTCatalogRowItemMetaModel firstRow = rowItemMetaModels.get(0);
            List<MDTCatalogColumnMetaModel> columns = new ArrayList<>();

            for (MDTCatalogItemMetaModel item : firstRow.getRowItems()) {
                columns.add(item.getColumn());
            }

            //create header
            Row headerRow = sheet.createRow(startRow);
            headerRow.createCell(startColumn++).setCellValue("#");
            for (MDTCatalogColumnMetaModel column : columns) {
                Cell cell = headerRow.createCell(startColumn++);
                cell.setCellType(CellType.STRING);
                cell.setCellValue(column.getName());
            }
            headerRow.createCell(startColumn).setCellValue("Paren Id");
            headerRow.createCell(startColumn + 1).setCellValue("Leaf");
            headerRow.createCell(startColumn + 2).setCellValue("Deleted");

            startColumn = 0;
            startRow = 2;
            AtomicInteger rowNumberer = new AtomicInteger(0);

            AtomicInteger rowCounter = new AtomicInteger(startRow);
            for (int i = startRow; i < parentNodes.size() + startRow; i++) {
                Row row = sheet.createRow(rowCounter.getAndIncrement());
                MDTCatalogRowItemMetaModel rowItem = parentNodes.get(i - startRow);

                Cell rowNumberingCell = row.createCell(startColumn++);
                rowNumberingCell.setCellValue(rowNumberer.incrementAndGet());

                int columnCounter = 0;
                for (MDTCatalogItemMetaModel item : rowItem.getRowItems()) {
                    Cell cell = row.createCell(startColumn++);
                    columnCounter++;
                    switch (item.getColumn().getDataType()) {
                        case STRING:
                            if (item.getColumn().isKey() && rowItem.isLeaf()) {
                                cell.setCellValue(item.getValue() != null ? item.getValue().toString() : "");
                            } else {
                                if (langId <= 0) {
                                    cell.setCellValue(item.getValue() != null ? item.getValue().toString() : "");
                                } else {
                                    cell.setCellValue(item.getValuesI18n().get(langId));
                                }
                            }
                            break;
                        case NUMBER:
                            cell.setCellValue(item.getValue() != null ? ((Number) item.getValue()).doubleValue() : 0);
                            break;
                        case INTEGER:
                            cell.setCellValue(item.getValue() != null ? ((Number) item.getValue()).intValue() : 0);
                            break;
                        case DATE:
                            if (item.getValue() != null) {
                                CellStyle cellStyle = wb.createCellStyle();
                                CreationHelper createHelper = wb.getCreationHelper();
                                cellStyle.setDataFormat(
                                        createHelper.createDataFormat().getFormat(DATE_FORMAT));
                                cell.setCellValue(((Date) item.getValue()));
                                cell.setCellStyle(cellStyle);
                            } else {
                                cell.setCellValue("");
                            }
                    }
                    if (columnCounter == rowItem.getRowItems().size() - 1) {
                        row.createCell(startColumn + 1).setCellValue(0);
                        row.createCell(startColumn + 2).setCellValue(rowItem.isLeaf());
                        row.createCell(startColumn + 3).setCellValue(item.isDeleted());
                    } else if (rowItem.getRowItems().size() == 1) {
                        row.createCell(startColumn).setCellValue(0);
                        row.createCell(startColumn + 1).setCellValue(rowItem.isLeaf());
                        row.createCell(startColumn + 2).setCellValue(item.isDeleted());
                    }

                }
                rowIdExcelIdMap.put(rowItem.getRowId(), rowNumberer.get());
                createHierarchy(rowItem, rowItemMetaModels, sheet, wb, rowCounter, rowNumberer, 1, rowIdExcelIdMap);

                startColumn = 0;

            }

            CellRangeAddress region = new CellRangeAddress(startRow - 1, startRow + rowItemMetaModels.size() - 1, startColumn, startColumn + firstRow.getRowItems().size() + 3);
            RegionUtil.setBorderBottom(BorderStyle.THIN, region, sheet);
            RegionUtil.setBorderTop(BorderStyle.THIN, region, sheet);
            RegionUtil.setBorderLeft(BorderStyle.THIN, region, sheet);
            RegionUtil.setBorderRight(BorderStyle.THIN, region, sheet);

        }
    }

    private void createHierarchy(MDTCatalogRowItemMetaModel rowItem, List<MDTCatalogRowItemMetaModel> rowItemMetaModels, Sheet sheet,
                                 Workbook wb, AtomicInteger rowCounter, AtomicInteger rowNumberer,
                                 int level, Map<Long, Integer> rowIdExcelIdMap) {
        List<MDTCatalogRowItemMetaModel> children = rowItemMetaModels.stream().filter(r -> r.getParentRowId() == rowItem.getRowId()).collect(Collectors.toList());
        StringBuilder space = new StringBuilder();
        for (int i = 0; i < level; i++) {
            space.append("  ");
        }

        for (MDTCatalogRowItemMetaModel m : children) {
            int startColumn = 0;

            Row row = sheet.createRow(rowCounter.getAndIncrement());
            Cell rowNumberingCell = row.createCell(startColumn++);
            rowNumberingCell.setCellValue(rowNumberer.incrementAndGet());

            int columnCounter = 0;

            for (int i = 0; i < m.getRowItems().size(); i++) {
                MDTCatalogItemMetaModel item = m.getRowItems().get(i);
                Cell cell = row.createCell(startColumn++);
                columnCounter++;

                String hierarchyPrefix = i == 0 && item.getColumn().getDataType().equals(DataType.STRING) ? space.toString() + "\u2193 " : space.toString();

                switch (item.getColumn().getDataType()) {
                    case STRING:
                        cell.setCellValue(item.getValue() != null ? hierarchyPrefix + item.getValue().toString() : "");
                        break;
                    case NUMBER:
                        cell.setCellValue(item.getValue() != null ? ((Number) item.getValue()).doubleValue() : 0);
                        break;
                    case INTEGER:
                        cell.setCellValue(item.getValue() != null ? ((Number) item.getValue()).intValue() : 0);
                        break;
                    case DATE:
                        if (item.getValue() != null) {
                            CellStyle cellStyle = wb.createCellStyle();
                            CreationHelper createHelper = wb.getCreationHelper();
                            cellStyle.setDataFormat(
                                    createHelper.createDataFormat().getFormat(DATE_FORMAT));
                            cell.setCellValue(((Date) item.getValue()));
                            cell.setCellStyle(cellStyle);
                        } else {
                            cell.setCellValue("");
                        }
                }
                if (columnCounter == m.getRowItems().size() - 1) {
                    row.createCell(startColumn + 1).setCellValue(rowIdExcelIdMap.get(rowItem.getRowId()));
                    row.createCell(startColumn + 2).setCellValue(m.isLeaf());
                    row.createCell(startColumn + 3).setCellValue(item.isDeleted());
                } else if (m.getRowItems().size() == 1) {
                    row.createCell(startColumn).setCellValue(rowIdExcelIdMap.get(rowItem.getRowId()));
                    row.createCell(startColumn + 1).setCellValue(m.isLeaf());
                    row.createCell(startColumn + 2).setCellValue(item.isDeleted());
                }

            }
            rowIdExcelIdMap.put(m.getRowId(), rowNumberer.get());

            if (!m.isLeaf()) {
                level++;
                createHierarchy(m, rowItemMetaModels, sheet, wb, rowCounter, rowNumberer, level, rowIdExcelIdMap);
            }
        }


    }
}
