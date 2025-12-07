package net.fina.server.classifier.util;

import net.fina.common.client.exception.FinATypeException;
import net.fina.server.classifier.entity.DataType;
import net.fina.server.classifier.model.*;
import net.fina.common.server.StatisticsLogger;
import org.apache.poi.ss.usermodel.*;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class MDTCatalogImportUtil {
    private static final Logger log = Logger.getLogger(MDTCatalogImportUtil.class.getName());

    public static final Map<String, String> languageCodeMap = new HashMap<>() {{
        put("EN", "en_US");
        put("KA", "ka_GE");
        put("RU", "ru_RU");
        put("KG", "kg_KG");
        put("TJ", "tj_TJ");
    }};

    private final int CATALOG_CODE_CELL = 1;
    private final int CATALOG_NAME_CELL = 2;
    private final int CATALOG_ABBREVIATION_CELL = 3;
    private final int CATALOG_SOURCE_CELL = 4;
    private final int REFERENCE_NUMBER = 5;
    private final int COLUMN_NAME_CELL = 1;
    private final int COLUMN_TYPE_CELL = 2;
    private final int COLUMN_KEY_CELL = 3;
    private final int COLUMN_FORMAT_CELL = 4;
    private final int COLUMN_IS_REQUIRED_CELL = 5;
    private final String CATALOG_SHEET_NAME = "CATALOG";


    public MDTCatalogImportStatusMetaModel readExcelFile(InputStream inputStream, long langId, String langCode, StatisticsLogger statLog, Map<MDTCatalogMetaModel, List<MDTCatalogRowItemMetaModel>> fileData, Map<String, Long> langCodeIdMap) throws IOException {
        MDTCatalogImportStatusMetaModel importStatusResult = new MDTCatalogImportStatusMetaModel();
        Workbook workbook = null;
        try {
            statLog.logStage("Open Excel File");
            workbook = WorkbookFactory.create(inputStream);
            statLog.logStage("Read Sheets");
            Sheet catalogSheet = workbook.getSheet(CATALOG_SHEET_NAME);

            if (catalogSheet == null) {
                log.error("CATALOG sheet is not present");
                importStatusResult.getErrors().add("CATALOG sheet is not present ");
                return importStatusResult;
            }
            Set<String> catalogCodes = new HashSet<>();

            for (int i = catalogSheet.getFirstRowNum() + 1; i <= catalogSheet.getLastRowNum(); i++) {
                MDTCatalogMetaModel catalog = new MDTCatalogMetaModel();
                catalog.setCreatedAt(new Date());
                catalog.setModifiedAt(catalog.getCreatedAt());
                String code = getSafeStringCellValue(catalogSheet.getRow(i).getCell(CATALOG_CODE_CELL));
                if (code == null || code.trim().isEmpty()) {
                    continue;
                } else if (catalogCodes.contains(code.trim().toUpperCase())) {
                    importStatusResult.getWarnings().add("Catalog CODE Is Not Unique Code [" + code + "] CELL : " + catalogSheet.getRow(i).getCell(CATALOG_CODE_CELL).getAddress().toString());
                }
                catalogCodes.add(code.trim().toUpperCase());
                catalog.setName(getSafeStringCellValue(catalogSheet.getRow(i).getCell(CATALOG_NAME_CELL)));
                catalog.setAbbreviation(getSafeStringCellValue(catalogSheet.getRow(i).getCell(CATALOG_ABBREVIATION_CELL)));
                catalog.setSource(getSafeStringCellValue(catalogSheet.getRow(i).getCell(CATALOG_SOURCE_CELL)));
                catalog.setReferenceNumber(getSafeStringCellValue(catalogSheet.getRow(i).getCell(REFERENCE_NUMBER)));
                catalog.setCode(getSafeStringCellValue(catalogSheet.getRow(i).getCell(CATALOG_CODE_CELL)));

                catalog.setCatalogColumns(readCatalogColumns(code, workbook, importStatusResult, langCodeIdMap, langCode));
                if (catalog.getCatalogColumns().isEmpty()) {
                    continue;
                }

                List<MDTCatalogRowItemMetaModel> rowItems = readCatalogSheetData(code, catalog, workbook, langId, importStatusResult, langCodeIdMap);

                fileData.put(catalog, rowItems);

            }

            statLog.logStage("Save Catalog & Data");

        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            importStatusResult.getErrors().clear();
            importStatusResult.getErrors().add("General Error Contact Administrator!");
        } finally {
            workbook.close();
            inputStream.close();
        }

        return importStatusResult;
    }

    private List<MDTCatalogRowItemMetaModel> readCatalogSheetData(String code, MDTCatalogMetaModel catalog, Workbook workbook, long langId, MDTCatalogImportStatusMetaModel importStatus, Map<String, Long> langCodeIdMap) throws Exception {
        List<MDTCatalogRowItemMetaModel> rows = new ArrayList<>();
        String dataSheetName = code + "_" + "DATA^EN";
        Sheet dataSheet = workbook.getSheet(dataSheetName);

        if (dataSheet == null) {
            log.warn("[" + dataSheetName + "] sheet does not exists. ignoring!");
            importStatus.getWarnings().add("[" + dataSheetName + "] sheet does not exists. ignoring!");
            return rows;
        }

        log.info("Reading sheet : " + dataSheet.getSheetName());

        Set<String> keys = new HashSet<>();

        try {

            for (int i = dataSheet.getFirstRowNum() + 1; i <= dataSheet.getLastRowNum(); i++) {
                Row row = dataSheet.getRow(i);

                MDTCatalogRowItemMetaModel rowItem = new MDTCatalogRowItemMetaModel();
                double parentId = getSafeNumericCellValue(row.getCell(catalog.getCatalogColumns().size() + 1));
                boolean leaf = getSafeBooleanCellValue(row.getCell(catalog.getCatalogColumns().size() + 2));
                boolean deleted = getSafeBooleanCellValue(row.getCell(catalog.getCatalogColumns().size() + 3));
                rowItem.setLeaf(leaf);
                rowItem.setDeleted(deleted);
                rowItem.setParentRowId((int) parentId);
                rowItem.setRowId(((Double) getSafeNumericCellValue(row.getCell(0))).longValue());

                for (int j = 0; j < catalog.getCatalogColumns().size(); j++) {

                    MDTCatalogItemMetaModel item = new MDTCatalogItemMetaModel();
                    item.setColumn(catalog.getCatalogColumns().get(j));
                    Cell cell = row.getCell(j + 1);
                    if (cell == null) {
                        rowItem.getRowItems().add(item);
                        continue;
                    }
                    switch (catalog.getCatalogColumns().get(j).getDataType()) {
                        case STRING:
                            String value = getSafeStringCellValue(cell);
                            if (item.getColumn().isKey()) {
                                cell.setCellType(CellType.STRING);
                                if (rowItem.isLeaf() && keys.contains(value)) {
                                    throw new FinATypeException("Key is not Unique,  Sheet : " + dataSheet.getSheetName() + ", Key : " + value);
                                }
                                Map<Long, String> langIdValueMap = langCodeIdMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, l -> value));
                                if (rowItem.isLeaf()) {
                                    keys.add(value);
                                    item.setValuesI18n(langIdValueMap);
                                } else {
                                    Map<Long, String> i18nValues = getDataI18nValueMap(code, workbook, i, cell.getColumnIndex(), langCodeIdMap);
                                    item.setValuesI18n(i18nValues);
                                }

                            } else {
                                Map<Long, String> i18nValues = getDataI18nValueMap(code, workbook, i, cell.getColumnIndex(), langCodeIdMap);
                                item.setValuesI18n(i18nValues);
                            }
                            item.setValue(value);
                            break;
                        case NUMBER:
                            item.setValue(BigDecimal.valueOf(cell.getNumericCellValue()));
                            break;
                        case INTEGER:
                            item.setValue(((Number) cell.getNumericCellValue()).intValue());
                            break;
                        case DATE:
                            item.setValue(BigDecimal.valueOf(cell.getDateCellValue().getTime()));
                            break;
                    }
                    rowItem.getRowItems().add(item);
                }

                rows.add(rowItem);
            }
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            importStatus.getErrors().add("Error Reading Data Sheet [" + dataSheet.getSheetName() + "] : " + t.getMessage());
        }

        return rows;
    }

    private List<MDTCatalogColumnMetaModel> readCatalogColumns(String code, Workbook wb, MDTCatalogImportStatusMetaModel importStatus, Map<String, Long> langCodeIdMap, String langCode) throws Exception {
        List<MDTCatalogColumnMetaModel> columns = new ArrayList<>();


        String catalogSheetName = code + "_" + "COLUMNS^EN";
        Sheet columnsSheet = wb.getSheet(catalogSheetName);
        if (columnsSheet == null) {
            log.warn("Sheet [" + catalogSheetName + "] does not exist");
            importStatus.getWarnings().add("Sheet [" + catalogSheetName + "] does not exist");
            return columns;
        }

        String currentLangCode = "EN";
        if (langCode != null && !langCode.isBlank()) {
            if (langCode.contains("_")) {
                currentLangCode = langCode.split("_")[0];
            } else {
                currentLangCode = langCode;
            }
        }

        if (!currentLangCode.equals("EN")) {
            String currentLangCodeSheetName = code + "_" + "COLUMNS^" + currentLangCode;

            if (wb.getSheet(currentLangCodeSheetName) == null) {
                log.error("Sheet [" + currentLangCodeSheetName + "] does not exist for current language");
                importStatus.getErrors().add("Sheet [" + currentLangCodeSheetName + "] does not exist for current language");
                return columns;
            }

        }


        for (int i = columnsSheet.getFirstRowNum() + 1; i <= columnsSheet.getLastRowNum(); i++) {
            Row row = columnsSheet.getRow(i);
            if (row.getCell(1) == null || row.getCell(1).getStringCellValue().trim().isEmpty()) {
                break;
            }

            MDTCatalogColumnMetaModel columnModel = new MDTCatalogColumnMetaModel();
            columnModel.setDataType(validateAndGetDataType(row.getCell(COLUMN_TYPE_CELL), importStatus));
            columnModel.setKey(getSafeBooleanCellValue(row.getCell(COLUMN_KEY_CELL)));
            columnModel.setDataFormat(getSafeStringCellValue(row.getCell(COLUMN_FORMAT_CELL)));
            columnModel.setIsRequired(getSafeBooleanCellValue(row.getCell(COLUMN_IS_REQUIRED_CELL)));

            Cell columnNameCell = row.getCell(COLUMN_NAME_CELL);
            String columnName = getSafeStringCellValue(columnNameCell);
            if (columnName == null || columnName.trim().isEmpty()) {
                importStatus.getErrors().add("Column name is required. Cell: " + columnNameCell.getAddress().toString());
            }
            columnModel.setName(columnName);
            columnModel.setNames(getColumNamesI18nMap(code, wb, i, langCodeIdMap));
            columns.add(columnModel);
        }

        return columns;
    }

    private Map<Long, String> getColumNamesI18nMap(String code, Workbook wb, int index, Map<String, Long> langCodeIdMap) {
        Map<Long, String> result = new HashMap<>();

        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            Sheet columnSheet = wb.getSheetAt(i);
            if (columnSheet.getSheetName().contains(code + "_COLUMNS^")) {
                String langCode = columnSheet.getSheetName().split("\\^")[1].toUpperCase();

                // if active languages does not contain sheet lang code, skip it
                if (!langCodeIdMap.containsKey(languageCodeMap.get(langCode))) {
                    log.debug("Skipping unsupported language: " + langCode + " for sheet: " + columnSheet.getSheetName());
                    continue;
                }

                Row row = columnSheet.getRow(index);
                result.put(langCodeIdMap.get(languageCodeMap.get(langCode)), getSafeStringCellValue(row.getCell(COLUMN_NAME_CELL)));

            }
        }

        return result;
    }

    private Map<Long, String> getDataI18nValueMap(String code, Workbook wb, int rowIndex, int columnIndex, Map<String, Long> langCodeIdMap) {
        Map<Long, String> result = new HashMap<>();

        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            Sheet columnSheet = wb.getSheetAt(i);
            if (columnSheet.getSheetName().contains(code + "_DATA^")) {
                String langCode = columnSheet.getSheetName().split("\\^")[1].toUpperCase();
                String fullLangCode = languageCodeMap.get(langCode);

                // if active languages does not contain sheet lang code, skip it
                if (fullLangCode == null || !langCodeIdMap.containsKey(fullLangCode)) {
                    log.debug("Skipping unsupported language: " + langCode + " for sheet: " + columnSheet.getSheetName());
                    continue;
                }
                Row row = columnSheet.getRow(rowIndex);
                result.put(langCodeIdMap.get(languageCodeMap.get(langCode)), getSafeStringCellValue(row.getCell(columnIndex)));
            }
        }

        return result;
    }

    private DataType validateAndGetDataType(Cell cell, MDTCatalogImportStatusMetaModel importStatus) {
        String cellValue = getSafeStringCellValue(cell);
        try {
            return DataType.valueOf(cellValue.toUpperCase());
        } catch (Throwable ex) {
            log.error("Invalid Data Type [" + cellValue + "] Cell : " + cell.getAddress().toString());
            importStatus.getErrors().add("Invalid Data Type [" + cellValue + "] Cell : " + cell.getAddress().toString());
            throw ex;
        }

    }

    private String getSafeStringCellValue(Cell cell) {
        try {
            Object val = getCellValue(cell);
            return val != null ? val.toString().replace("\u2193", "").trim() : null;
        } catch (Throwable t) {
            log.error(cell.getRow().getSheet().getSheetName() + " Row : " + cell.getRow().getRowNum() + " Cell : " + cell.getRowIndex() + " Value : " + cell.toString());
            log.error(t.getMessage());
        }
        return "";
    }

    private double getSafeNumericCellValue(Cell cell) {
        try {
            Object val = getCellValue(cell);
            return val != null ? (Double) val : 0;
        } catch (Throwable t) {
            log.error(cell.getRow().getSheet().getSheetName() + " Row : " + cell.getRow().getRowNum() + " Cell : " + cell.getRowIndex() + " Value : " + cell.toString());
            log.error(t.getMessage());
        }
        return 0;
    }

    private boolean getSafeBooleanCellValue(Cell cell) {
        try {
            Object val = getCellValue(cell);
            return val != null ? (Boolean) val : false;
        } catch (Throwable t) {
            log.error(cell.getRow().getSheet().getSheetName() + " Row : " + cell.getRow().getRowNum() + " Cell : " + cell.getRowIndex() + " Value : " + cell.toString());
            log.error(t.getMessage());
        }
        return false;
    }

    private Object getCellValue(Cell cell) {
        if (cell != null) {

            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue();
                case BLANK:
                    return "";
                case BOOLEAN:
                    return cell.getBooleanCellValue();
                case NUMERIC:
                    return cell.getNumericCellValue();
                default:
                    return "N/A";
            }
        }

        return null;
    }

}