package net.fina.server.matrix.util;

import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.returns.ReturnTableType;
import net.fina.server.dcs.uploadfile.impl.reader.excel.ExcelMatrixReader;
import net.fina.server.dcs.uploadfile.impl.reader.excel.MatrixOptionBase;
import net.fina.server.dcs.uploadfile.impl.reader.excel.util.NodeDataType;
import net.fina.server.matrix.entity.*;
import net.fina.server.matrix.model.MatrixImportModel;
import net.fina.server.matrix.model.helper.MatrixModelHelper;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.returns.entity.DefinitionTable;
import net.fina.server.returns.entity.ReturnDefinition;
import org.apache.poi.ss.usermodel.*;
import org.jboss.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class MatrixImportUtil {
    private static final Logger log = Logger.getLogger(MatrixImportUtil.class);

    private static final String MAIN_MATRIX_FILE = "Matrix.xls";
    private static final String OPTIONS_SHEET = "Options";


    public static MatrixImportModel convertToEntities(
            String path,
            Map<String, MDTNode> nodesCodeIdMap,
            Map<String, ReturnDefinition> definitionsCodeObjectMap,
            Map<String, Long> fiTypeCodeIdMap,
            Map<String, Long> versionCodeIdMap,
            Map<String, Long> periodTypeCodeIdMap
    ) throws FinATypeException {
        MatrixImportModel result = new MatrixImportModel();

        Path directoryPath = Paths.get(path);
        List<MatrixOptionBase> mainMatrixOptions;
        List<SubMatrix> subMatrices = new ArrayList<>();

        Path matrixFilePath = directoryPath.resolve(MAIN_MATRIX_FILE);

        if (Files.exists(matrixFilePath)) {
            ExcelMatrixReader matrixReader = new ExcelMatrixReader(matrixFilePath.toFile().getAbsolutePath(), null);
            mainMatrixOptions = matrixReader.getOptions();
        } else {
            throw new FinATypeException("Invalid path");
        }
        Map<String, Matrix> mainMatrices = MatrixModelHelper.toEntities(mainMatrixOptions, fiTypeCodeIdMap, versionCodeIdMap, periodTypeCodeIdMap);

        List<Matrix> mainMatrixEntities = new ArrayList<>(mainMatrices.values());
        result.setMainMatrices(mainMatrixEntities);

        List<String> subMatrixFileNames = mainMatrixOptions.stream().map(MatrixOptionBase::getMatrixForEachType).toList();
        List<Path> excelFiles;

        try (var walkStream = Files.walk(directoryPath)) {
            excelFiles = walkStream
                    .filter(Files::isRegularFile)
                    .filter(p -> subMatrixFileNames.contains(p.getFileName().toString()))
                    .toList();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new FinATypeException("Invalid path");
        }

        for (Path file : excelFiles) {
            subMatrices.addAll(parseSubMatrixFile(file, nodesCodeIdMap, definitionsCodeObjectMap, mainMatrices.get(file.getFileName().toString())));
        }

        result.setSubMatrices(subMatrices);
        return result;
    }


    private static List<SubMatrix> parseSubMatrixFile(Path filePath, Map<String, MDTNode> nodesCodeIdMap, Map<String, ReturnDefinition> definitionsCodeIObjectMap, Matrix mainMatrix) {
        List<SubMatrix> result = new ArrayList<>();
        Map<String, List<Row>> multiVctAndMixedRowMap = new HashMap<>();

        try (Workbook workbook = WorkbookFactory.create(new File(filePath.toString()))) {
            Sheet sheet = workbook.getSheet(OPTIONS_SHEET);
            if (sheet == null) {
                return result;
            }

            for (int i = sheet.getFirstRowNum() + 1; i < sheet.getLastRowNum() + 1; i++) {
                Row row = sheet.getRow(i);

                if (row == null || row.getCell(1, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL) == null) {
                    break;
                }

                if (Arrays.asList(MatrixTableType.MULTIVCT, MatrixTableType.COMBINED, MatrixTableType.MIXED).contains(getTableType(row.getCell(3)))) {
                    handleMultiVCTAndMixed(row, multiVctAndMixedRowMap);
                } else {
                    SubMatrix subMatrix = createSubMatrix(row, definitionsCodeIObjectMap, mainMatrix);
                    switch (subMatrix.getMatrixTableType()) {
                        case MCT:
                            SubMatrixTable subMatrixTableMCT = createSubMatrixTable(row, subMatrix);
                            setMappings(subMatrix, row, workbook, nodesCodeIdMap, subMatrixTableMCT);
                            break;
                        case VCT:
                            SubMatrixTable subMatrixTableVCT = createSubMatrixTable(row, subMatrix);
                            constructConditions(row, subMatrixTableVCT);
                            setMappings(subMatrix, row, workbook, nodesCodeIdMap, subMatrixTableVCT);
                            break;
                    }

                    result.add(subMatrix);
                }
            }

            for (Map.Entry<String, List<Row>> entry : multiVctAndMixedRowMap.entrySet()) {
                SubMatrix subMatrix = createSubMatrix(entry.getValue().get(0), definitionsCodeIObjectMap, mainMatrix);
                int rowIndex = 0;
                for (Row row : entry.getValue()) {
                    SubMatrixTable subMatrixTable = createSubMatrixTableForMultiVCTOrMixed(row, subMatrix, rowIndex);
                    if (subMatrixTable != null) {
                        constructTableMappings(row, subMatrixTable, workbook, nodesCodeIdMap);
                        subMatrix.getTables().add(subMatrixTable);
                    }
                    rowIndex++;
                }
                result.add(subMatrix);
            }

        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    private static SubMatrix createSubMatrix(Row row, Map<String, ReturnDefinition> definitionCodeObjectMap, Matrix mainMatrix) {
        SubMatrix subMatrix = new SubMatrix();
        String code = (row.getCell(1, Row.MissingCellPolicy.RETURN_NULL_AND_BLANK).getStringCellValue());

        subMatrix.setReturnDefinition(definitionCodeObjectMap.getOrDefault(code, null));

        subMatrix.setMatrixTableType(getTableType(row.getCell(3)));
        subMatrix.setSheetName((row.getCell(12, Row.MissingCellPolicy.RETURN_NULL_AND_BLANK).getStringCellValue()));
        subMatrix.setProtected(getSafeBooleanCellValue(row.getCell(7)));
        subMatrix.setMainMatrix(mainMatrix);
        return subMatrix;
    }

    private static SubMatrixTable createSubMatrixTable(Row row, SubMatrix subMatrix) {
        if (subMatrix.getReturnDefinition() == null) {
            return null;
        }

        return switch (subMatrix.getMatrixTableType()) {
            case MCT -> createSubMatrixTable(row, subMatrix, ReturnTableType.MCT);
            case VCT -> createSubMatrixTable(row, subMatrix, ReturnTableType.VCT);
            default -> null;
        };
    }

    private static SubMatrixTable createSubMatrixTable(Row row, SubMatrix subMatrix, ReturnTableType tableType) {
        Collection<DefinitionTable> definitionTables = subMatrix.getReturnDefinition().getDefinitionTables();

        if (checkDefinitionTableMapping(subMatrix, definitionTables)) {
            return null;
        }

        DefinitionTable table = definitionTables.stream()
                .filter(dt -> dt.getType() == tableType)
                .min(Comparator.comparingLong(DefinitionTable::getSequence))
                .orElse(null);

        if (table == null) {
            return null;
        }

        SubMatrixTable subMatrixTable = new SubMatrixTable();
        subMatrixTable.setDefinitionTable(table);
        subMatrixTable.setStartColumn(row.getCell(4, Row.MissingCellPolicy.RETURN_NULL_AND_BLANK).getStringCellValue());
        subMatrixTable.setStartRow(getSafeNumericCellValue(row.getCell(5)));
        subMatrixTable.setOffset(getSafeNumericCellValue(row.getCell(16)));
        subMatrixTable.setSubMatrix(subMatrix);

        if (tableType == ReturnTableType.VCT) {
            subMatrixTable.setVctTableHeader(row.getCell(13, Row.MissingCellPolicy.RETURN_NULL_AND_BLANK).getStringCellValue());
            subMatrixTable.setAfterHeaderRowAmount(getSafeNumericCellValue(row.getCell(14)));
        }

        return subMatrixTable;
    }

    private static SubMatrixTable createSubMatrixTableForMultiVCTOrMixed(Row row, SubMatrix subMatrix, int rowIndex) {
        Collection<DefinitionTable> definitionTables = subMatrix.getReturnDefinition().getDefinitionTables();

        if (checkDefinitionTableMapping(subMatrix, definitionTables)) {
            return null;
        }

        List<DefinitionTable> sortedTables = definitionTables.stream()
                .sorted(Comparator.comparingLong(DefinitionTable::getSequence))
                .toList();

        if (rowIndex >= sortedTables.size()) {
            return null;
        }

        DefinitionTable table = sortedTables.get(rowIndex);

        SubMatrixTable subMatrixTable = new SubMatrixTable();
        subMatrixTable.setVctTableHeader(row.getCell(13, Row.MissingCellPolicy.RETURN_NULL_AND_BLANK).getStringCellValue());
        subMatrixTable.setAfterHeaderRowAmount(getSafeNumericCellValue(row.getCell(14)));
        subMatrixTable.setDefinitionTable(table);
        subMatrixTable.setOffset(getSafeNumericCellValue(row.getCell(16)));
        subMatrixTable.setStartColumn(row.getCell(4, Row.MissingCellPolicy.RETURN_NULL_AND_BLANK).getStringCellValue());
        subMatrixTable.setStartRow(getSafeNumericCellValue(row.getCell(5)));
        subMatrixTable.setSubMatrix(subMatrix);
        constructConditions(row, subMatrixTable);

        return subMatrixTable;
    }

    private static boolean checkDefinitionTableMapping(SubMatrix subMatrix, Collection<DefinitionTable> definitionTables) {
        boolean valid = true;

        switch (subMatrix.getMatrixTableType()) {
            case MULTIVCT, VCT ->
                    valid = definitionTables.stream().noneMatch(table -> table.getType() == ReturnTableType.MCT);
            case MCT -> valid = definitionTables.stream().noneMatch(table -> table.getType() == ReturnTableType.VCT);
        }

        return !valid;
    }


    private static void setMappings(SubMatrix subMatrix, Row row, Workbook workbook, Map<String, MDTNode> nodeCodeIdMap, SubMatrixTable subMatrixTable) {
        String sheetName = row.getCell(12, Row.MissingCellPolicy.RETURN_NULL_AND_BLANK).getStringCellValue();
        if (sheetName.contains("*")) {
            sheetName = sheetName.substring(0, sheetName.indexOf("*"));
        }
        Sheet tableSheet = workbook.getSheet(sheetName);
        if (tableSheet != null) {
            if (subMatrixTable != null) {
                subMatrixTable.setTableMappings(constructTableMappings(tableSheet, 1, tableSheet.getLastRowNum(), nodeCodeIdMap, subMatrixTable));
                subMatrix.setTables(Collections.singletonList(subMatrixTable));
                subMatrixTable.setSubMatrix(subMatrix);
            }
        }
    }


    private static void handleMultiVCTAndMixed(Row row, Map<String, List<Row>> multiVctAndMixedRowMap) {
        String returnCode = row.getCell(1, Row.MissingCellPolicy.RETURN_NULL_AND_BLANK).getStringCellValue();
        multiVctAndMixedRowMap.putIfAbsent(returnCode, new ArrayList<>());
        multiVctAndMixedRowMap.get(returnCode).add(row);
    }

    private static void constructTableMappings(Row row, SubMatrixTable subMatrixTable, Workbook workbook, Map<String, MDTNode> nodeCodeIdMap) {
        String sheetName = row.getCell(12, Row.MissingCellPolicy.RETURN_NULL_AND_BLANK).getStringCellValue();
        Sheet tableSheet = workbook.getSheet(sheetName);

        if (tableSheet != null) {
            int startRow = getSafeNumericCellValue(row.getCell(6));
            subMatrixTable.setTableMappings(constructTableMappings(tableSheet, startRow - 1, -1, nodeCodeIdMap, subMatrixTable));
        }
    }

    private static void constructConditions(Row row, SubMatrixTable subMatrixTable) {
        if (subMatrixTable == null || subMatrixTable.getDefinitionTable().getType() != ReturnTableType.VCT) {
            return;
        }
        subMatrixTable.setVctTableEndConditions(Arrays.asList(
                createTableEndCondition(row, 8, 9),
                createTableEndCondition(row, 10, 11)
        ));

    }

    private static TableEndCondition createTableEndCondition(Row row, int columnIndex, int conditionIndex) {
        TableEndCondition condition = new TableEndCondition();
        condition.setColumn(row.getCell(columnIndex, Row.MissingCellPolicy.RETURN_NULL_AND_BLANK).getStringCellValue());
        condition.setCondition(row.getCell(conditionIndex, Row.MissingCellPolicy.RETURN_NULL_AND_BLANK).getStringCellValue());
        return condition;
    }


    private static List<SubMatrixTableMapping> constructTableMappings(Sheet tableSheet, int startRow, int finishRow, Map<String, MDTNode> nodeCodeIdMap, SubMatrixTable subMatrixTable) {
        List<SubMatrixTableMapping> matrixTableMappings = new ArrayList<>();
        parseDefinitionMappingTable(startRow, finishRow, tableSheet, matrixTableMappings, nodeCodeIdMap, subMatrixTable);
        return matrixTableMappings;
    }

    private static void parseDefinitionMappingTable(int startRow, int finishRow, Sheet tableSheet, List<SubMatrixTableMapping> matrixTableMappings, Map<String, MDTNode> nodeCodeIdMap, SubMatrixTable subMatrixTable) {
        if (finishRow < 0) {
            finishRow = tableSheet.getLastRowNum();
        }
        for (int i = startRow; i < finishRow + 1; i++) {
            Row row = tableSheet.getRow(i);
            if (row == null || (row.getCell(0) == null || row.getCell(0).getCellType() == CellType.BLANK)) {
                break;
            }
            SubMatrixTableMapping mappingTable = new SubMatrixTableMapping();
            Cell cell = row.getCell(0, Row.MissingCellPolicy.RETURN_NULL_AND_BLANK);
            String code = (cell == null) ? "" : cell.getStringCellValue().trim();

            mappingTable.setMdtNode(nodeCodeIdMap.getOrDefault(code, null));

            mappingTable.setCell((row.getCell(2, Row.MissingCellPolicy.RETURN_NULL_AND_BLANK).getStringCellValue()));
            mappingTable.setDataType(getNodeDataType(row.getCell(3, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL)));

            mappingTable.setSubMatrixTable(subMatrixTable);
            mappingTable.setSequence(i - startRow + 1);
            matrixTableMappings.add(mappingTable);
        }
    }

    private static int getSafeNumericCellValue(Cell cell) {
        try {
            Double val = (Double) getCellValue(cell);
            return val != null ? val.intValue() : 0;
        } catch (Throwable t) {
            log.error(cell.getRow().getSheet().getSheetName() + " Row : " + cell.getRow().getRowNum() + " Cell : " + cell.getRowIndex() + " Value : " + cell);
            log.error(t.getMessage());
        }
        return 0;
    }

    private static boolean getSafeBooleanCellValue(Cell cell) {
        try {
            Object val = getCellValue(cell);
            return val != null ? (Boolean) val : false;
        } catch (Throwable t) {
            log.error(cell.getRow().getSheet().getSheetName() + " Row : " + cell.getRow().getRowNum() + " Cell : " + cell.getRowIndex() + " Value : " + cell);
            log.error(t.getMessage());
        }
        return false;
    }


    private static NodeDataType getNodeDataType(Cell cell) {
        if (cell == null || cell.getCellType() != CellType.STRING) {
            return null;
        }
        String cellValue = cell.getStringCellValue();
        try {
            return NodeDataType.valueOf(cellValue.toUpperCase());
        } catch (Throwable ex) {
            log.error("Invalid Data Type [" + cellValue + "] Cell : " + cell.getAddress().toString());
            return null;
        }
    }

    private static Object getCellValue(Cell cell) {
        if (cell != null) {
            return switch (cell.getCellType()) {
                case STRING -> cell.getStringCellValue();
                case BLANK -> "";
                case BOOLEAN -> cell.getBooleanCellValue();
                case NUMERIC -> cell.getNumericCellValue();
                default -> "N/A";
            };
        }

        return null;
    }

    private static MatrixTableType getTableType(Cell cell) {
        String cellValue = cell.getStringCellValue();
        try {
            return MatrixTableType.valueOf(cellValue.toUpperCase());
        } catch (Throwable ex) {
            log.error("Invalid Data Type [" + cellValue + "] Cell : " + cell.getAddress().toString());
            return null;

        }
    }


}
