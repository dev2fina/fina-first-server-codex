package net.fina.server.dcs.uploadfile.impl.reader.excel;


import net.fina.common.client.exception.ConverterDcsTypeException;
import net.fina.common.client.exception.ConverterDcsTypeException.ExcelCellType;
import net.fina.common.client.exception.DcsTypeException;
import net.fina.server.dcs.uploadfile.impl.reader.excel.util.Type;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ExcelMappingReader extends ExcelBaseReader {

    private List<Option> options = null;

    public ExcelMappingReader(String excelFile, Map<String, Object> properties) throws IOException, InvalidFormatException {
        try {
            initWorkBook(excelFile, properties);
            checkOptionsIndex();
        } catch (InvalidFormatException | IOException ex) {
            log.error(ex.getMessage(), ex);
            log.error(DcsTypeException.Type.MATRIX_NOT_SET.getCode());
            log.error(ex.getMessage(), ex);
            log.error("excelFilePath = " + excelFile);
            throw ex;
        }
    }

    protected List<Option> getwholeOprions() throws DcsTypeException {
        if (this.options == null) {
            this.initOPtions();
        }
        return this.options;
    }

    private List<Option> getDistinctSheetOprions() throws DcsTypeException {
        List<Option> distinctSheetOptions = new ArrayList<>();
        if (this.options == null) {
            this.initOPtions();
        }
        List<Option> multiTables = new ArrayList<>();

        for (Option option : this.options) {
            if (option.getType() == Type.MCT) {
                distinctSheetOptions.add(option);
                continue;
            }
            multiTables.add(option);
        }

        while (multiTables.size() > 0) {
            Option distinct = multiTables.remove(0);
            distinctSheetOptions.add(distinct);

            for (int i = 0; i < multiTables.size(); i++) {
                String sheetName = distinct.getSheetName();
                if (sheetName.equalsIgnoreCase(multiTables.get(i).getSheetName())) {
                    multiTables.remove(i);
                    --i;
                }
            }
        }
        return distinctSheetOptions;
    }

    private void initOPtions() throws DcsTypeException {
        options = new ArrayList<Option>();
        StringBuilder exceptionString = new StringBuilder();

        Sheet sheet = workbook.getSheetAt(super.optionsSheetindex);
        int lastRowNum = lastRowNumb(sheet);

        for (int currentRow = 1; currentRow <= lastRowNum; currentRow++) {// zero is names;
            Row row = sheet.getRow(currentRow);
            int LastCellNum = row.getLastCellNum();
            Cell[] cells = new Cell[columnNames.getColumnsAmount()];
            for (int currentColumn = 0; currentColumn < LastCellNum; currentColumn++) {
                cells[currentColumn] = row.getCell(currentColumn);
            }

            try {
                ExcelMappingReader.Option option = new Option(cells, currentRow);
                this.options.add(option);
            } catch (DcsTypeException dcsTypeException) {
                reasons.add(dcsTypeException.getMessage());
                exceptionString.append(dcsTypeException.getMessage()).append('\n');
            }
        }
        if (exceptionString.length() != 0) {
            log.error(exceptionString.toString());
        }
    }

    private List<String> incorrectSheetNames() {
        /**
         * Options Sheets names
         */
        List<String> sheetsFromArray = new ArrayList<String>();

        /**
         * File Real Sheets names
         */
        List<String> sheetsFromWBook = new ArrayList<String>();

        for (int i = 0; i < this.options.size(); i++) {
            String sheetName = this.options.get(i).getSheetName().trim();
            if (sheetsFromArray.contains(sheetName)) {
                continue;
            }
            sheetsFromArray.add(sheetName.replace("*", ""));
        }

        int sheetsAmount = this.workbook.getNumberOfSheets();
        for (int i = 0; i < sheetsAmount; i++) {// String sheetName = this.workbook.getSheetName(i);
            if (i == super.optionsSheetindex) {
                continue;
            }
            sheetsFromWBook.add(this.workbook.getSheetName(i).trim());
        }
        if (sheetsFromArray.containsAll(sheetsFromWBook) && sheetsFromWBook.containsAll(sheetsFromArray)) {
            sheetsFromArray = null;
        } else {
            for (int i = 0; i < sheetsFromArray.size(); i++) {
                String sheet = sheetsFromArray.get(i);
                if (sheetsFromWBook.contains(sheet)) {
                    sheetsFromArray.remove(i);
                    sheetsFromWBook.remove(sheet);
                    i--;
                }
            }
        }
        if (sheetsFromArray != null)
            sheetsFromArray.addAll(sheetsFromWBook);
        return sheetsFromArray;
    }


    public List<Option> getOptions() throws DcsTypeException {
        initOPtions();
        List<String> incorrectSheets = this.incorrectSheetNames();
        if (incorrectSheets != null) {
            reasons.add("${net.fina.dcs.converter.sheetsAremissingOrIncorrect}: " + incorrectSheets.toString());
            //throw new DcsTypeException("Following sheets are missing: " + incorrectSheets.toString());
        }
        if (!reasons.isEmpty()) {
            throw new DcsTypeException(reasons);
        }
        return getDistinctSheetOprions();
    }

    @Override
    public List<String> getColumnNames(String sheetName) {
        return super.getColumnNames(sheetName);
    }


    // DO NOT MODIFY
    private enum columnNames {
        ID,
        RETURN_CODE,
        NAME,
        TYPE,
        START_COLUMN,
        START_ROW,
        SUB_MATRIX_FIRST_ROW,
        IS_PROTECTED,
        VCT_TABLE_END_FIRST_CONDITION_COLUMN_NAME,
        VCT_TABLE_END_FIRST_CONDITION_COLUMN_VALUE,
        VCT_TABLE_END_SECOND_CONDITION_COLUMN_NAME,
        VCT_TABLE_END_SECOND_CONDITION_COLUMN_VALUE,
        SHEET_NAME,
        VCT_TABLE_HEADER,
        AFTER_HEADER_ROWAMOUNT,
        MIXED_TABLE_SUB_TABLE,
        OFFSET,
        FI_CODE_REFERENCE;

        private static int getColumnsAmount() {
            return columnNames.values().length;
        }
    }

    public class Option {

        private int rowNumber;
        private StringBuilder exceptionReason = new StringBuilder();

        private int id;
        private String returnCode;
        private String name;
        private Type type;
        private String startColumn;
        private int startRow;
        private int subMatrixFirstRow;
        private boolean isProtected;
        private String[] VCTTableEndFirstCondition;
        private String[] VCTTableEndSecondCondition;
        private String sheetName;
        private String VCTTableHeader;
        private int afterHeaderRowAmount;
        private Type mixedTableSubTable;
        private int offset;
        private String fiCodeReference;
        private Map<String, String> mdtCodeCellReferenceMap = new HashMap<>();
        private Map<String, Integer> columnPrecisionMap = new HashMap<>();


        private Option(Cell[] cells, int rowIndex) throws DcsTypeException {
            this.rowNumber = rowIndex;

            this.setID(cells);
            this.setReturnCode(cells);
            this.setName(cells);
            this.setType(cells);
            this.setStartColumn(cells);
            this.setStartRow(cells);
            this.setSubMatrixFirstRow(cells);
            this.setisProtected(cells);
            this.setVCTTableEndFirstCondition(cells);
            this.setVCTTableEndSecondCondition(cells);
            this.setSheetName(cells);
            this.setVCTTableHeader(cells);
            this.setAfterHeaderRowAmount(cells);
            this.setMixedTableSubTable(cells);
            this.setOffset(cells);
            this.setFiCodeReference(cells);

            if (this.exceptionReason.length() > 0) {
                throw new DcsTypeException(exceptionReason.toString());
            }

//            if (this.type != null && this.type.equals(Type.VCT)) {
//            }
            this.setMdtCodeCellReferenceMap(this.getSheetName());
            this.setColumnPrecisionMap(this.getSheetName());

        }

        public int getID() {
            return id;
        }

        private void setID(Cell[] cells) {
            try {
                int columnIndex = columnNames.ID.ordinal();
                Cell id = cells[columnIndex];
                if (id == null) {
                    // XXX setID
                    throw generateOptionsSheetEmptyCellTypeCase(rowNumber, columnIndex);
                }
                try {
                    this.id = (int) id.getNumericCellValue();
                } catch (IllegalStateException | NumberFormatException exception) {
                    log.error(exception.getMessage(), exception);
                    throw generateOptionsSheetIllegalCellTypeCase(id, ExcelCellType.NUMERIC);
                }
            } catch (ConverterDcsTypeException ex) {
                logAndAppendExceptionMessage(ex);
            }

        }

        public String getReturnCode() {
            return returnCode;
        }

        private void setReturnCode(Cell[] cells) {
            try {
                int columnIndex = columnNames.RETURN_CODE.ordinal();
                Cell returnCode = cells[columnIndex];
                if (returnCode == null) {
                    // XXX setReturnCode
                    throw generateOptionsSheetEmptyCellTypeCase(rowNumber, columnIndex);
                }
                try {
                    this.returnCode = returnCode.getStringCellValue();
                } catch (IllegalStateException stateException) {
                    log.error(stateException.getMessage(), stateException);
                    throw generateOptionsSheetIllegalCellTypeCase(returnCode, ExcelCellType.TEXT);
                }
            } catch (ConverterDcsTypeException ex) {
                logAndAppendExceptionMessage(ex);
            }
        }

        public String getName() {
            return name;
        }

        private void setName(Cell[] cells) throws ConverterDcsTypeException {
            try {
                int columnIndex = columnNames.NAME.ordinal();
                Cell name = cells[columnIndex];
                if (name == null) {
                    // throw new NullPointerException(nullpointerMessage +
                    // ConverterDcsTypeException.columnNameAsInExcel(columnIndex));
                    return;
                }
                try {
                    // XXX setName
                    this.name = name.getStringCellValue();
                } catch (IllegalStateException stateException) {
                    log.error(stateException.getMessage(), stateException);
                    this.name = null;
                    // throw generateOptionsSheetIllegalCellTypeCase(name,
                    // ExcelCellType.TEXT);
                }
            } catch (ConverterDcsTypeException ex) {
                logAndAppendExceptionMessage(ex);
            }
        }

        public Type getType() {
            return type;
        }

        private void setType(Cell[] cells) {
            try {
                int columnIndex = columnNames.TYPE.ordinal();
                Cell type = cells[columnIndex];
                if (type == null) {
                    // XXX setType (MCT,VCT,MULTIVCT,MICED)
                    this.type = null;
                    throw generateOptionsSheetEmptyCellTypeCase(rowNumber, columnIndex);
                }
                try {
                    this.type = guessType(type);
                } catch (IllegalStateException stateException) {
                    log.error(stateException.getMessage(), stateException);
                    this.type = null;
                    throw generateOptionsSheetIllegalCellTypeCase(type, ExcelCellType.TEXT);
                }
            } catch (ConverterDcsTypeException ex) {
                logAndAppendExceptionMessage(ex);
            }
        }

        public String getStartColumn() {
            return startColumn;
        }

        private void setStartColumn(Cell[] cells) {
            try {
                int columnIndex = columnNames.START_COLUMN.ordinal();
                Cell startColumn = cells[columnIndex];
                if (startColumn == null) {
                    // throw new NullPointerException(nullpointerMessage +
                    // ConverterDcsTypeException.columnNameAsInExcel(columnIndex));
                    return;
                }
                try {
                    startColumn.setCellType(CellType.STRING);
                    this.startColumn = startColumn.getStringCellValue();
                } catch (IllegalStateException stateException) {
                    log.error(stateException.getMessage(), stateException);
                    // throw this.generateIllegalCellTypeCase(startColumn,
                    // ExcelCellType.TEXT);
                }
            } catch (ConverterDcsTypeException ex) {
                logAndAppendExceptionMessage(ex);
            }

        }

        public int getStartRow() {
            return startRow;
        }

        private void setStartRow(Cell[] cells) {
            try {
                int columnIndex = columnNames.START_ROW.ordinal();
                Cell startRow = cells[columnIndex];
                if (startRow == null) {
                    // XXX setStartRow
                    if (!(this.type == null || this.type == Type.MCT || (this.type == Type.MIXED && (guessType(cells[columnNames.MIXED_TABLE_SUB_TABLE
                            .ordinal()]) == Type.MCT)))) {
                        throw generateOptionsSheetEmptyCellTypeCase(rowNumber, columnIndex);

                    }
                } else {
                    this.startRow = (int) startRow.getNumericCellValue();
                }
            } catch (ConverterDcsTypeException ex) {
                logAndAppendExceptionMessage(ex);
                this.startRow = 0;
            }
        }

        public int getSubMatrixFirstRow() {
            return subMatrixFirstRow;
        }

        private void setSubMatrixFirstRow(Cell[] cells) {
            try {
                int columnIndex = columnNames.SUB_MATRIX_FIRST_ROW.ordinal();
                Cell subMatrixFirstRow = cells[columnIndex];
                if (this.type == null || this.type == Type.MULTIVCT || this.type == Type.MIXED || this.type == Type.COMBINED) {
                    if (subMatrixFirstRow == null) {
                        // XXX setSubMAtrixFirstRow
                        throw generateOptionsSheetEmptyCellTypeCase(rowNumber, columnIndex);
                    }
                    try {
                        this.subMatrixFirstRow = (int) subMatrixFirstRow.getNumericCellValue();
                    } catch (IllegalStateException | NumberFormatException exception) {
                        log.error(exception.getMessage(), exception);
                        throw generateOptionsSheetIllegalCellTypeCase(subMatrixFirstRow, ExcelCellType.NUMERIC);
                    }
                }
            } catch (ConverterDcsTypeException ex) {
                logAndAppendExceptionMessage(ex);
                this.startRow = 0;
            }
        }

        public boolean IsProtected() {
            return isProtected;
        }

        private void setisProtected(Cell[] cells) throws ConverterDcsTypeException {
            try {
                int columnIndex = columnNames.IS_PROTECTED.ordinal();
                Cell isProtected = cells[columnIndex];
                if (isProtected == null) {
                    // XXX isProtected
                    throw generateOptionsSheetEmptyCellTypeCase(rowNumber, columnIndex);
                }
                try {
                    this.isProtected = isProtected.getBooleanCellValue();
                } catch (IllegalStateException illegalStateException) {
                    log.error(illegalStateException.getMessage(), illegalStateException);
                    throw generateOptionsSheetIllegalCellTypeCase(isProtected, ExcelCellType.LOGICAL);
                }
            } catch (ConverterDcsTypeException ex) {
                logAndAppendExceptionMessage(ex);
            }
        }

        public String[] getVCTTableEndFirstCondition() {
            return VCTTableEndFirstCondition;
        }

        private void setVCTTableEndFirstCondition(Cell[] cells) throws ConverterDcsTypeException {
            try {
                int columnIndex1 = columnNames.VCT_TABLE_END_FIRST_CONDITION_COLUMN_NAME.ordinal();
                int columnIndex2 = columnNames.VCT_TABLE_END_FIRST_CONDITION_COLUMN_VALUE.ordinal();

                Cell vctTableEndFirstCondition_1 = cells[columnIndex1];
                Cell vctTableEndFirstCondition_2 = cells[columnIndex2];
                if (!(this.type == null || this.type == Type.MCT || (this.type == Type.MIXED && (guessType(cells[columnNames.MIXED_TABLE_SUB_TABLE
                        .ordinal()]) == Type.MCT)) || (this.type == Type.COMBINED && (guessType(cells[columnNames.MIXED_TABLE_SUB_TABLE.ordinal()]) == Type.MCT)))) {
                    if (vctTableEndFirstCondition_1 == null || vctTableEndFirstCondition_1.toString().trim().length() == 0) {
                        // XXX setVCTTableEndFirstCondition

                        throw generateOptionsSheetEmptyCellTypeCase(rowNumber, columnIndex1);
                    }

                    VCTTableEndFirstCondition = new String[2];
                    try {
                        this.VCTTableEndFirstCondition[0] = vctTableEndFirstCondition_1.getStringCellValue().trim();
                    } catch (IllegalStateException illegalStateException) {
                        log.error(illegalStateException.getMessage(), illegalStateException);
                        throw generateOptionsSheetIllegalCellTypeCase(vctTableEndFirstCondition_1, ExcelCellType.TEXT);
                    }
                    this.VCTTableEndFirstCondition[1] = checkCellType(vctTableEndFirstCondition_2).toString().trim();
                }
            } catch (ConverterDcsTypeException ex) {
                logAndAppendExceptionMessage(ex);
            }
        }

        public String[] getVCTTableEndSecondCondition() {
            return VCTTableEndSecondCondition;
        }

        private void setVCTTableEndSecondCondition(Cell[] cells) throws ConverterDcsTypeException {
            try {
                int columnIndex1 = columnNames.VCT_TABLE_END_SECOND_CONDITION_COLUMN_NAME.ordinal();
                int columnIndex2 = columnNames.VCT_TABLE_END_SECOND_CONDITION_COLUMN_VALUE.ordinal();

                Cell vctTableEndSecondCondition_1 = cells[columnIndex1];
                Cell vctTableEndSecondCondition_2 = cells[columnIndex2];
                if (!(this.type == null || this.type == Type.MCT || (this.type == Type.MIXED && (guessType(cells[columnNames.MIXED_TABLE_SUB_TABLE
                        .ordinal()]) == Type.MCT)) || (this.type == Type.COMBINED && (guessType(cells[columnNames.MIXED_TABLE_SUB_TABLE.ordinal()]) == Type.MCT)))) {
                    if (vctTableEndSecondCondition_1 == null || vctTableEndSecondCondition_1.toString().trim() == "") {
                        // XXX setVCTTableEndSecondCondition
                        throw generateOptionsSheetEmptyCellTypeCase(rowNumber, columnIndex1);
                    }

                    this.VCTTableEndSecondCondition = new String[2];
                    try {
                        this.VCTTableEndSecondCondition[0] = vctTableEndSecondCondition_1.toString().trim();
                    } catch (IllegalStateException illegalStateException) {
                        log.error(illegalStateException.getMessage(), illegalStateException);
                        throw generateOptionsSheetIllegalCellTypeCase(vctTableEndSecondCondition_1, ExcelCellType.TEXT);
                    }
                    this.VCTTableEndSecondCondition[1] = checkCellType(vctTableEndSecondCondition_2).toString().trim();
                }
            } catch (ConverterDcsTypeException ex) {
                logAndAppendExceptionMessage(ex);
            }
        }

        public String getSheetName() {
            return sheetName;
        }

        private void setSheetName(Cell[] cells) throws ConverterDcsTypeException {
            try {
                int columnIndex = columnNames.SHEET_NAME.ordinal();
                Cell sheetName = cells[columnIndex];
                if (sheetName == null || sheetName.toString().trim().length() == 0) {
                    this.sheetName = null;
                    throw generateOptionsSheetEmptyCellTypeCase(rowNumber, columnIndex);
                }
                try {
                    this.sheetName = sheetName.getStringCellValue();
                } catch (IllegalStateException illegalStateException) {
                    log.error(illegalStateException.getMessage(), illegalStateException);
                    throw generateOptionsSheetIllegalCellTypeCase(sheetName, ExcelCellType.TEXT);
                }
            } catch (ConverterDcsTypeException ex) {
                logAndAppendExceptionMessage(ex);
            }
        }

        public String getVCTTableHeader() {
            return VCTTableHeader;
        }

        private void setVCTTableHeader(Cell[] cells) throws ConverterDcsTypeException {
            try {
                int columnIndex = columnNames.VCT_TABLE_HEADER.ordinal();
                Cell VCTTable = cells[columnIndex];
                if (this.type == null || this.type == Type.MULTIVCT || this.type == Type.COMBINED
                        || (this.type == Type.MIXED && guessType(cells[columnNames.MIXED_TABLE_SUB_TABLE.ordinal()]) == Type.VCT)) {
                    if (VCTTable == null) {
                        // XXX serVCTtableHeader
                        throw generateOptionsSheetEmptyCellTypeCase(rowNumber, columnIndex);
                    }
                    this.VCTTableHeader = checkCellType(VCTTable).toString().trim();
                }
            } catch (ConverterDcsTypeException ex) {
                logAndAppendExceptionMessage(ex);
            }
        }

        public int getAfterHeaderRowAmount() {
            return afterHeaderRowAmount;
        }

        private void setAfterHeaderRowAmount(Cell[] cells) throws ConverterDcsTypeException {
            int columnIndex = columnNames.AFTER_HEADER_ROWAMOUNT.ordinal();
            Cell afterHeaderRowAmount = cells[columnIndex];
            if (this.type == null || this.type == Type.MULTIVCT || this.type == Type.COMBINED
                    || (this.type == Type.MIXED && guessType(cells[columnNames.MIXED_TABLE_SUB_TABLE.ordinal()]) == Type.VCT)) {
                if (afterHeaderRowAmount == null) {
                    // XXX afterHeaderRowAmount
                    throw generateOptionsSheetEmptyCellTypeCase(rowNumber, columnIndex);
                }
                try {
                    this.afterHeaderRowAmount = (int) afterHeaderRowAmount.getNumericCellValue();
                } catch (NumberFormatException | IllegalStateException exception) {
                    log.error(exception.getMessage(), exception);
                    throw generateOptionsSheetIllegalCellTypeCase(afterHeaderRowAmount, ExcelCellType.NUMERIC);
                }
            } else {
                this.afterHeaderRowAmount = 0;
            }
        }

        public Type getMixedTableSubTable() {
            return this.mixedTableSubTable;
        }

        private void setMixedTableSubTable(Cell[] cells) throws ConverterDcsTypeException {
            try {
                int columnIndex = columnNames.MIXED_TABLE_SUB_TABLE.ordinal();
                Cell mixedTableSubTable = cells[columnIndex];
                if (this.type == null || this.type == Type.MIXED || this.type == Type.COMBINED) {
                    if (mixedTableSubTable == null || mixedTableSubTable.toString().trim().length() == 0) {
                        // XXX setMixedTableSubTable
                        throw generateOptionsSheetEmptyCellTypeCase(rowNumber, columnIndex);
                    }
                    try {
                        this.mixedTableSubTable = guessType(mixedTableSubTable);
                    } catch (IllegalStateException illegalStateException) {
                        log.error(illegalStateException.getMessage(), illegalStateException);
                        throw generateOptionsSheetIllegalCellTypeCase(mixedTableSubTable, ExcelCellType.TEXT);
                    }
                } else {
                    this.mixedTableSubTable = null;
                }
            } catch (ConverterDcsTypeException ex) {
                logAndAppendExceptionMessage(ex);
            }
        }

        public int getOffset() {
            return this.offset;
        }

        private void setOffset(Cell[] cells) throws ConverterDcsTypeException {
            try {
                int columnIndex = columnNames.OFFSET.ordinal();
                Cell offset = cells[columnIndex];
                if (this.type == null || (this.type == Type.MIXED && this.mixedTableSubTable == Type.MCT)) {
                    if (offset == null) {
                        // XXX setOffset
                        throw generateOptionsSheetEmptyCellTypeCase(rowNumber, columnIndex);
                    }
                    try {
                        this.offset = (int) offset.getNumericCellValue();
                    } catch (IllegalStateException | NumberFormatException exception) {
                        log.error(exception.getMessage(), exception);
                        throw generateOptionsSheetIllegalCellTypeCase(offset, ExcelCellType.NUMERIC);
                    }
                } else {
                    this.offset = 0;
                }
            } catch (ConverterDcsTypeException ex) {
                logAndAppendExceptionMessage(ex);
            }
        }

        public String getFiCodeReference() {
            return this.fiCodeReference;
        }

        public void setFiCodeReference(Cell[] cells) {
            int columnIndex = columnNames.FI_CODE_REFERENCE.ordinal();
            if (columnIndex < cells.length) {
                Cell fiCodeReference = cells[columnIndex];
                if (fiCodeReference != null) {
                    try {
                        this.fiCodeReference = fiCodeReference.getStringCellValue();
                    } catch (Throwable t) {
                        log.error(t.getMessage(), t);
                    }
                }
            }
        }

        private Type guessType(Cell cell) {
            Type type;
            String temp = cell.getStringCellValue().trim();
            type = temp.equalsIgnoreCase(Type.MULTIVCT.name()) ? (Type.MULTIVCT) : temp.equalsIgnoreCase(Type.MCT.name()) ? (Type.MCT) : temp
                    .equalsIgnoreCase(Type.VCT.name()) ? (Type.VCT) : temp.equalsIgnoreCase(Type.MIXED.name()) ? (Type.MIXED) : temp.equalsIgnoreCase(Type.COMBINED.name()) ? (Type.COMBINED) : (null);
            return type;
        }

        private void logAndAppendExceptionMessage(ConverterDcsTypeException exception) {
            log.error(exception.getMessage(), exception);
            reasons.add(exception.getMessage());
            this.exceptionReason.append(exception.getMessage());
        }

        public Map<String, String> getMdtCodeCellReferenceMap() {
            return mdtCodeCellReferenceMap;
        }

        private void setMdtCodeCellReferenceMap(String sheetName) {
            Map<String, String> result = new HashMap<>();
            Sheet sheet = workbook.getSheet(sheetName.replace("*", ""));
            if (sheet != null) {
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) {
                        continue;
                    }
                    Cell codeCell = row.getCell(0);
                    String mdtCode = codeCell != null ? codeCell.getStringCellValue() : null;
                    Cell referenceCell = row.getCell(2);
                    String cellReference = referenceCell != null ? referenceCell.getStringCellValue() : null;
                    if (mdtCode != null && !mdtCode.trim().isEmpty() && cellReference != null) {
                        result.put(mdtCode.trim(), cellReference.trim());
                    }
                }
            }
            this.mdtCodeCellReferenceMap = result;
        }

        public Map<String, Integer> getColumnPrecisionMap() {
            return columnPrecisionMap;
        }

        private void setColumnPrecisionMap(String sheetName) {
            Sheet sheet = workbook.getSheet(sheetName.replace("*", ""));
            if (sheet != null) {
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) {
                        continue;
                    }
                    Cell referenceCell = row.getCell(2);
                    String cellReference = referenceCell != null ? referenceCell.getStringCellValue() : null;

                    Cell precisionCell = row.getCell(4);
                    if (cellReference != null && !cellReference.isBlank() && precisionCell != null && precisionCell.getCellType().equals(CellType.NUMERIC)) {

                        this.columnPrecisionMap.put(cellReference.trim(), ((Number)precisionCell.getNumericCellValue()).intValue());
                    }

                }
            }
        }

    }
}
