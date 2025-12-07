package net.fina.server.dcs.uploadfile.impl.reader.excel;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.fina.common.client.exception.ConverterDcsTypeException;
import net.fina.common.client.exception.DcsTypeException;
import net.fina.server.dcs.uploadfile.impl.reader.excel.util.Conditions;
import net.fina.server.dcs.uploadfile.impl.reader.excel.util.ExcelPasswordValidator;
import net.fina.server.dcs.uploadfile.impl.reader.excel.util.NodeDataType;
import net.fina.server.dcs.uploadfile.impl.reader.excel.util.Type;
import net.fina.server.returns.xml.Item;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExcelDataFileReader extends ExcelMappingReader {

    protected final String DEFAULT_SHEET_PROTECTION;
    private final Workbook masterWorkbook;
    protected Map sheetProtectionByFiType;
    protected MatrixOptionBase mainOption;
    private List<Item> mctItems;
    private List<Item> vctItems;
    private List<Item> mixedItems;
    private SpreadsheetVersion ssVersion;
    private int dividingRowNumber = 0;

    /**
     * @param masterFileContent  master file content
     * @param selectedMatrixPath primary matrix math
     * @param condition          sheet check condition
     * @param properties         properties
     * @throws Exception
     */
    public ExcelDataFileReader(byte[] masterFileContent, String selectedMatrixPath, Conditions condition, Map<String, Object> properties) throws Exception {
        super(selectedMatrixPath, properties);

        masterWorkbook = checkAndCreateEncryptedWorkBookByPassword(masterFileContent, properties, true);

        incorrectSheetNames(condition);

        initSpreadsheetVersion(masterWorkbook);
        validateExcelContentWithExtension(masterWorkbook, (String) properties.get("dcs.file.extension"));

        Object vctEmptyLineObject = properties.get("converter.VCT.emptyLine");
        if (vctEmptyLineObject != null && (!vctEmptyLineObject.toString().isEmpty())) {
            String vctEmptyLine = vctEmptyLineObject.toString().trim();
            dividingRowNumber = Integer.valueOf(vctEmptyLine);
            log.info("VCT Empty Line: " + vctEmptyLine);
        } else {
            log.warn("VCT Empty Line is empty");
        }

        Object property = properties.get("dcs.primary.matrix.option");
        if (property != null) {
            mainOption = (MatrixOptionBase) property;
        }

        DEFAULT_SHEET_PROTECTION = properties.get("dcs.excel.sheetProtection.password.string").toString();

        Object protectionProperty = properties.get("dcs.excel.sheetProtection.passwordByFiType.json");
        if (protectionProperty != null && !protectionProperty.toString().isEmpty()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                sheetProtectionByFiType = mapper.readValue(protectionProperty.toString(), Map.class);
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
            }
        }

        //Check workbook sheet passwords
        checkPasswordCorrection();

        //Check protection errors
        if (reasons.size() > 0) {
            DcsTypeException ex = new DcsTypeException(reasons);
            ex.setType(net.fina.common.client.exception.DcsTypeException.Type.INVALID_PASSWORD);
            throw ex;
        }
    }

    private void initSpreadsheetVersion(Workbook workbook) throws InvalidFormatException {
        if (masterWorkbook instanceof HSSFWorkbook) {
            ssVersion = SpreadsheetVersion.EXCEL97;
        } else if (masterWorkbook instanceof XSSFWorkbook) {
            ssVersion = SpreadsheetVersion.EXCEL2007;
        } else {
            throw new InvalidFormatException("no excel file");
        }
    }

    /**
     * @param option
     * @return The list of xml items for MCT Table
     */
    public List<Item> getMCTresult(Option option) {
        mctItems = new ArrayList<Item>();
        int subMatrixStartRowNumber = Math.max(option.getSubMatrixFirstRow() - 1, 1);
        this.initMCTResults(option, subMatrixStartRowNumber, 0);
        if (!reasons.isEmpty()) {
            DcsTypeException ex = new DcsTypeException(reasons);
            emptyReasons();
            throw ex;
        }
        return this.mctItems;
    }

    /**
     * fills MCT data from master files sheet, which name is in option. fills
     * only values that is described in the primary sheet.
     *
     * @param option
     * @param subMatrixStartRowNumber
     * @param offset
     */
    private void initMCTResults(Option option, int subMatrixStartRowNumber, int offset) {
        int codeColumnIndex = 0;
        int valueColumnIndex = 2;
        StringBuilder exceptionMessage = new StringBuilder("");

        String sheetName = option.getSheetName();
        Sheet primarySheet = super.workbook.getSheet(sheetName);
        Sheet masterSheet = this.masterWorkbook.getSheet(sheetName);

        int lastRowNumb = 0;
        if (option.getType() == Type.MIXED || option.getType() == Type.COMBINED) {
            lastRowNumb = lastRowNumb(primarySheet, subMatrixStartRowNumber);
        } else {
            lastRowNumb = lastRowNumb(primarySheet);
        }

        for (int currentRow = subMatrixStartRowNumber; currentRow <= lastRowNumb; currentRow++) {
            boolean exceptionOccurred = false;
            Row primaryRow = primarySheet.getRow(currentRow);

            if (primaryRow != null) {
                Cell codeValueCell = primaryRow.getCell(codeColumnIndex);
                try {
                    if (checkCellType(codeValueCell).toString().isEmpty()) {
                        throw new ConverterDcsTypeException(sheetName, currentRow, codeColumnIndex, DcsTypeException.Type.CELL_IS_NULL_OR_IS_EMPTY);
                    }
                } catch (ConverterDcsTypeException e) {
                    String message = e.getMessage();
                    reasons.add(message);
                    exceptionMessage.append(message);
                    exceptionMessage.append('\n');
                    exceptionOccurred = true;
                }

                Cell valueLocationCell = primaryRow.getCell(valueColumnIndex);
                try {
                    if (checkCellType(valueLocationCell).toString().isEmpty()) {
                        throw new ConverterDcsTypeException(sheetName, currentRow, valueColumnIndex,
                                DcsTypeException.Type.CELL_IS_NULL_OR_IS_EMPTY);
                    }
                } catch (ConverterDcsTypeException e) {
                    String message = e.getMessage();
                    reasons.add(message);
                    exceptionMessage.append(message);
                    exceptionMessage.append('\n');
                    exceptionOccurred = true;
                }

                if (exceptionOccurred) {
                    continue;
                }

                String code = checkCellType(codeValueCell).toString();
                String codeValueLocation = checkCellType(valueLocationCell).toString();

                Cell valueCell;
                try {
                    try {
                        valueCell = masterSheet.getRow(CellRow(codeValueLocation) + offset).getCell(CellColumn(codeValueLocation));
                    } catch (ConverterDcsTypeException ex) {
                        throw new ConverterDcsTypeException(sheetName, currentRow, valueColumnIndex, ex.getMessage());
                    }
                } catch (ConverterDcsTypeException e) {
                    String message = e.getMessage();
                    reasons.add(message);
                    exceptionMessage.append(message);
                    exceptionMessage.append('\n');
                    continue;
                } catch (NullPointerException e) {
                    valueCell = null;
                    log.error("Cell " + codeValueLocation + " is NULL or Out Of Range!");
                }
                Integer precision = getNodePrecision(primaryRow);

                mctItems.add(new Item(code, 0, checkCellType(valueCell, getNodeDataType(primaryRow), precision).toString()));
            }
        }
        if (exceptionMessage.length() != 0) {
            log.error(exceptionMessage.toString());
        }
    }

    private void checkPasswordCorrection() throws InvalidFormatException, IOException, DcsTypeException {
        List<ExcelMappingReader.Option> options = super.getOptions();
        for (ExcelMappingReader.Option option : options) {
            if (option.IsProtected()) {
                Sheet primarySheet = null;
                Sheet masterSheet = null;
                try {
                    String sheetName = option.getSheetName();
                    primarySheet = super.workbook.getSheet(sheetName);
                    masterSheet = this.masterWorkbook.getSheet(sheetName);
                } catch (IllegalArgumentException | NullPointerException Ex) {
                    continue;
                }

                String password = DEFAULT_SHEET_PROTECTION;

                if (this.sheetProtectionByFiType != null && mainOption != null) {
                    Object tmpPassword = sheetProtectionByFiType.get(mainOption.getFIType().trim());
                    if (tmpPassword != null) {
                        password = tmpPassword.toString();
                    }
                }

                if (ExcelPasswordValidator.checkPasswordCorrection(primarySheet, password) && ExcelPasswordValidator.checkPasswordCorrection(masterSheet, password)) {
                    boolean primarySheetIsProtected = primarySheet.getProtect();
                    boolean masterSheetIsProtected = masterSheet.getProtect();
                    if (!primarySheetIsProtected || !masterSheetIsProtected) {
                        reasons.add("${net.fina.dcs.converter.checkProtection} " + primarySheet.getSheetName());
                    }
                } else {
                    reasons.add("${net.fina.dcs.converter.noPasswordMatch}: " + primarySheet.getSheetName());
                }
            }
        }
    }

    /**
     * @param primaryOption options of return which must be generated
     * @return The list of xml items for VCT Table
     */
    public List<Item> getVCTResult(ExcelMappingReader.Option primaryOption) {
        vctItems = new ArrayList<Item>();

        String sheetName = primaryOption.getSheetName();
        String[] firstCondition = primaryOption.getVCTTableEndFirstCondition();
        String[] secondCondition = primaryOption.getVCTTableEndSecondCondition();
        int startRowNumber = primaryOption.getStartRow() - 1;
        int endRowNumber = returnEndRowNumber(startRowNumber, sheetName, CellColumn(firstCondition[0]), firstCondition[1], CellColumn(secondCondition[0]), secondCondition[1]);

        initVCTResults(sheetName, startRowNumber, endRowNumber, 1);

        if (reasons.size() > 0) {
            DcsTypeException ex = new DcsTypeException(reasons);
            emptyReasons();
            log.error(ex.getMessage());
            throw ex;
        }

        return vctItems;
    }

    private void initVCTResults(String sheetName, int masterStartRowNumber, int masterEndRowNumber, int primaryMarixStartRowIndex) {
        int codeColumnIndex = 0;
        int valueColumnIndex = 2;

        Sheet primarySheet = super.workbook.getSheet(sheetName);
        Sheet masterSheet = this.masterWorkbook.getSheet(sheetName);

        int lastRowNumb = lastRowNumb(primarySheet, primaryMarixStartRowIndex);

        for (int primaryMatrixRowIndex = primaryMarixStartRowIndex; primaryMatrixRowIndex <= lastRowNumb; primaryMatrixRowIndex++) {
            Row primaryRow = primarySheet.getRow(primaryMatrixRowIndex);
            Cell codeValuecell = primaryRow.getCell(codeColumnIndex);

            Cell valueRefernceCell = primaryRow.getCell(valueColumnIndex);
            int column;
            try {
                try {
                    column = CellColumn(checkCellType(valueRefernceCell).toString().trim());
                } catch (DcsTypeException ex) {
                    throw new ConverterDcsTypeException(sheetName, primaryMatrixRowIndex, valueColumnIndex, ex.getMessage());
                }
            } catch (ConverterDcsTypeException ex) {
                String message = ex.getMessage();
                reasons.add(message);
                continue;
            }

            String code = codeValuecell.getStringCellValue().trim();
            for (int masterMatrixRowIndex = masterStartRowNumber; masterMatrixRowIndex <= masterEndRowNumber; masterMatrixRowIndex++) {
                Row masterRow = masterSheet.getRow(masterMatrixRowIndex);
                if (isRowEmpty(masterRow)) {
                    vctItems.add(new Item(code, masterMatrixRowIndex - masterStartRowNumber, ""));
                    continue;
                }
                Cell valueCell = masterRow.getCell(column);

                Integer precision = null;
                if (valueCell != null) {
                    precision = getNodePrecision(primaryRow, valueCell.getColumnIndex());
                }
                vctItems.add(new Item(code, masterMatrixRowIndex - masterStartRowNumber, checkCellType(valueCell, getNodeDataType(primaryRow), precision).toString()));
            }
        }
    }

    public List<Item> getMultiVCT(ExcelMappingReader.Option option) throws DcsTypeException {

        List<ExcelMappingReader.Option> sameSheetOptions = this.getSameSheetOptions(option);

        List<Item> multiVCTItems = new ArrayList<>();
        for (Option multiVCTOption : sameSheetOptions) {
            vctItems = new ArrayList<Item>();
            initMultiVCT(multiVCTOption);
            multiVCTItems.addAll(vctItems);
        }

        if (!reasons.isEmpty()) {
            DcsTypeException ex = new DcsTypeException(reasons);
            emptyReasons();
            throw ex;
        }

        return multiVCTItems;
    }

    private void initMixed(ExcelMappingReader.Option option) throws DcsTypeException {
        if (option.getMixedTableSubTable() == Type.MCT) {
            this.mctItems = new ArrayList<>();
            int subMatrixStartRowNumber = option.getSubMatrixFirstRow() - 1;
            this.initMCTResults(option, subMatrixStartRowNumber, option.getOffset());
            this.mixedItems.addAll(this.mctItems);
            this.mctItems = null;
        }
        if (option.getMixedTableSubTable() == Type.VCT) {
            this.vctItems = new ArrayList<>();
            initMultiVCT(option);
            this.mixedItems.addAll(this.vctItems);
            this.vctItems = null;
        }
    }

    private List<ExcelMappingReader.Option> getSameSheetOptions(ExcelMappingReader.Option option) throws DcsTypeException {
        List<ExcelMappingReader.Option> wholeOptions = super.getwholeOprions();
        List<ExcelMappingReader.Option> sameSheetOptions = new ArrayList<>();
        for (Option sameSheetOption : wholeOptions) {
            if (sameSheetOption.getSheetName().equals(option.getSheetName()) && sameSheetOption.getType() == option.getType()) {
                sameSheetOptions.add(sameSheetOption);
            }
        }
        return sameSheetOptions;

    }

    public List<Item> getMixedResults(ExcelMappingReader.Option option) throws DcsTypeException {
        this.mixedItems = new ArrayList<Item>();
        List<ExcelMappingReader.Option> sameSheetOptions = this.getSameSheetOptions(option);

        for (Option mixedTablesOption : sameSheetOptions) {
            this.initMixed(mixedTablesOption);
        }
        if (!reasons.isEmpty()) {
            DcsTypeException ex = new DcsTypeException(reasons);
            emptyReasons();
            throw ex;
        }

        return mixedItems;
    }

    private void initMultiVCT(ExcelMappingReader.Option primaryOption) throws DcsTypeException {
        String sheetName = primaryOption.getSheetName();
        String[] firstCondition = primaryOption.getVCTTableEndFirstCondition();
        String[] secondCondition = primaryOption.getVCTTableEndSecondCondition();

        int masterRowAfterHeader = primaryOption.getAfterHeaderRowAmount();
        int masterStartRow = primaryOption.getStartRow() + masterRowAfterHeader;
        String beginText = primaryOption.getVCTTableHeader().trim();

        int primarySheetStartRow = primaryOption.getSubMatrixFirstRow() - 1;
        primarySheetStartRow = (primarySheetStartRow <= 0) ? (1) : (primarySheetStartRow);

        if (masterStartRow <= masterRowAfterHeader) {
            masterStartRow = getTableStartRowNumber(sheetName, beginText) + masterRowAfterHeader + 1;
        }
        try {
            int masterLastRowNumber = returnEndRowNumber(masterStartRow, sheetName, CellColumn(firstCondition[0]), firstCondition[1], CellColumn(secondCondition[0]), secondCondition[1]);

            initVCTResults(sheetName, masterStartRow, masterLastRowNumber, primarySheetStartRow);
        } catch (Exception ex) {
            log.error("at sheet " + sheetName, ex);
            throw ex;
        }
    }

    public List<Item> getCombined(ExcelMappingReader.Option option) throws DcsTypeException {

        List<ExcelMappingReader.Option> sameSheetOptions = this.getSameSheetOptions(option);

        List<Item> combinedItems = new ArrayList<>();
        for (Option combinedOption : sameSheetOptions) {
            switch (combinedOption.getMixedTableSubTable()) {
                case MCT:
                    this.mctItems = new ArrayList<>();
                    int subMatrixStartRowNumber = combinedOption.getSubMatrixFirstRow() - 1;
                    int mctOffset = this.getMctOffset(combinedOption);
                    if (mctOffset >= 0) {
                        this.initMCTResults(combinedOption, subMatrixStartRowNumber, mctOffset);
                        combinedItems.addAll(this.mctItems);
                        this.mctItems = null;
                    }
                    break;
                case VCT:
                    this.vctItems = new ArrayList<>();
                    initMultiVCT(combinedOption);
                    combinedItems.addAll(vctItems);
                    break;
            }
        }

        if (reasons.size() > 0) {
            DcsTypeException ex = new DcsTypeException(reasons);
            emptyReasons();
            throw ex;
        }

        return combinedItems;
    }

    private int getMctOffset(Option option) {
        int subMatrixStartRowNumber = option.getSubMatrixFirstRow() - 1;
        String sheetName = option.getSheetName();
        Sheet primarySheet = super.workbook.getSheet(sheetName);
        StringBuilder exceptionMessage = new StringBuilder("");

        int lastRowNumb = lastRowNumb(primarySheet, subMatrixStartRowNumber);
        List<String> valueCells = new ArrayList<>();
        for (int curRow = subMatrixStartRowNumber; curRow <= lastRowNumb; curRow++) {
            boolean exceptionOccurred = false;
            Row primaryRow = primarySheet.getRow(curRow);
            if (primaryRow != null) {
                Cell valueLocationCell = primaryRow.getCell(2);

                try {
                    if (checkCellType(valueLocationCell).toString().isEmpty()) {
                        throw new ConverterDcsTypeException(sheetName, curRow, 2,
                                DcsTypeException.Type.CELL_IS_NULL_OR_IS_EMPTY);
                    }
                } catch (ConverterDcsTypeException e) {
                    String message = e.getMessage();
                    reasons.add(message);
                    exceptionMessage.append(message);
                    exceptionMessage.append('\n');
                    exceptionOccurred = true;
                }

                if (exceptionOccurred) {
                    continue;
                }

                valueCells.add(checkCellType(valueLocationCell).toString());
            }
        }

        if (exceptionMessage.length() != 0) {
            log.error(exceptionMessage.toString());
            return -1;
        }

        valueCells.sort(String::compareToIgnoreCase);
        int mctStartRowNumber = getTableStartRowNumber(sheetName, option.getVCTTableHeader());

        return mctStartRowNumber + option.getAfterHeaderRowAmount();
    }

    private List<String> incorrectSheetNames(Conditions condition) throws Exception {
        /**
         * Options Sheets names
         */
        List<Option> option = super.getOptions();
        List<String> sheetsFromArray = new ArrayList<>();
        String message = "";

        /**
         * File Real Sheets names
         */
        List<String> sheetsFromWBook = new ArrayList<>();

        for (Option anOption : option) {
            String sheetName = anOption.getSheetName().trim();
            if (sheetsFromArray.contains(sheetName)) {
                continue;
            }
            sheetsFromArray.add(sheetName);
        }

        int sheetsAmount = this.masterWorkbook.getNumberOfSheets();
        for (int i = 0; i < sheetsAmount; i++) {
            sheetsFromWBook.add(this.masterWorkbook.getSheetName(i));
        }

        if (condition == Conditions.EQUALS) {
            if (sheetsFromArray.containsAll(sheetsFromWBook) && sheetsFromWBook.containsAll(sheetsFromArray)) {
                sheetsFromArray = null;
                sheetsFromWBook.clear();
            } else {
                for (int i = 0; i < sheetsFromArray.size(); i++) {
                    String sheet = sheetsFromArray.get(i);
                    if (sheetsFromWBook.contains(sheet)) {
                        sheetsFromArray.remove(i);
                        sheetsFromWBook.remove(sheet);
                        i--;
                    }
                }
                message = net.fina.common.client.exception.DcsTypeException.Type.INVALID_STRUCRURE.getCode();
                // "Following sheets from master file do not equal sheets in matrix ";
            }
        } else if (condition == Conditions.SUBSET) {
            if (sheetsFromWBook.containsAll(sheetsFromArray)) {
                sheetsFromArray = null;
                sheetsFromWBook.clear();
            } else {
                for (int i = 0; i < sheetsFromArray.size(); i++) {
                    String sheet = sheetsFromWBook.get(i);
                    if (sheetsFromArray.contains(sheet)) {
                        sheetsFromWBook.remove(i);
                        sheetsFromWBook.remove(sheet);
                        i--;
                    }
                }
                message = net.fina.common.client.exception.DcsTypeException.Type.INVALID_STRUCRURE.getCode();
            }
        } else if (condition == Conditions.SUBSET_INV) {
            if (sheetsFromArray.containsAll(sheetsFromWBook)) {
                sheetsFromArray = null;
                sheetsFromWBook.clear();
            } else {
                for (int i = 0; i < sheetsFromWBook.size(); i++) {
                    String sheet = sheetsFromWBook.get(i);
                    if (sheetsFromArray.contains(sheet)) {
                        sheetsFromArray.remove(sheet);
                        sheetsFromWBook.remove(i);
                        i--;
                    }
                }
                sheetsFromArray.clear();
                message = net.fina.common.client.exception.DcsTypeException.Type.INVALID_STRUCRURE.getCode();
            }
        }

        if (sheetsFromArray != null && !sheetsFromArray.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("${net.fina.dcs.converter.sheetsAreMissing}: ");
            sb.append(String.join(",", sheetsFromArray));
            if (!sheetsFromWBook.isEmpty()) {
                sb.append(" AND ");
            }
            reasons.add(sb.toString());
        }
        if (!sheetsFromWBook.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("${net.fina.dcs.converter.sheetsAreIncorrect}: ");
            for (String sheet : sheetsFromWBook) {
                sb.append(sheet).append(",");
            }
            reasons.add(sb.toString());
        }

        if (reasons.size() > 0) {
            DcsTypeException ex = new DcsTypeException(reasons);
            ex.setType(net.fina.common.client.exception.DcsTypeException.Type.INVALID_STRUCRURE);
            throw ex;
        }
        return sheetsFromArray;
    }

    private int getTableStartRowNumber(String sheetName, String tableHeader) {
        Sheet masterSheet = this.masterWorkbook.getSheet(sheetName);
        int lastRow = masterSheet.getLastRowNum();
        int startRowNumber = lastRow;
        for (int i = 1; i <= lastRow; i++) {
            Row row = masterSheet.getRow(i);
            if (row == null) {
                continue;
            }
            Cell cell = row.getCell(0);
            if (cell == null) {
                continue;
            }
            if (cell.toString().trim().equals(tableHeader)) {
                startRowNumber = i;
                break;
            }
        }
        return startRowNumber;
    }

    /**
     * @param startAt        index
     * @param sheetName      name of sheet
     * @param firstColInd    <b>first</b> condition column <b>index</b>
     * @param firstColValue  <b>first</b> condition <b>Value</b>
     * @param secondColInd   <b>second</b> condition column <b>index</b>
     * @param secondColValue <b>second</b> condition <b>Value</b>
     * @return index of the last row of VCT table
     */
    private int returnEndRowNumber(int startAt, String sheetName, int firstColInd, String firstColValue, int secondColInd, String secondColValue) {
        Sheet sheet = this.masterWorkbook.getSheet(sheetName);
        int lastRowNumber = sheet.getLastRowNum();

        int emptyRowAmount = 0;

        for (int i = startAt + 1; i <= lastRowNumber; i++) {
            Row row = sheet.getRow(i);

            if (isRowEmpty(row) && (firstColValue.isEmpty() && secondColValue.isEmpty()) && (++emptyRowAmount >= dividingRowNumber)) {
                lastRowNumber = i - emptyRowAmount;
                break;
            } else if (!isRowEmpty(row)) {
                /**
                 * if row is empty and both conditions are also empty text this
                 * condition is already checked;
                 */
                emptyRowAmount = 0;

                Cell first = row.getCell(firstColInd);
                Cell second = row.getCell(secondColInd);

                if (firstColValue.equalsIgnoreCase(checkCellType(first).toString().trim()) && secondColValue.equalsIgnoreCase(checkCellType(second).toString().trim())) {
                    lastRowNumber = i - 1;
                    break;
                }
            }
        }
        return lastRowNumber;
    }


    public boolean hasSheet(String sheetName) {
        return masterWorkbook.getSheet(sheetName) != null;
    }

    public String getDataFileFiCode(Option option) {
        try {
            Sheet masterSheet = this.masterWorkbook.getSheet(option.getSheetName());
            CellReference cellReference = new CellReference(option.getFiCodeReference());
            Row row = masterSheet.getRow(cellReference.getRow());
            if (!isRowEmpty(row)) {
                Cell cell = row.getCell(cellReference.getCol());
                return checkCellType(cell).toString();
            }
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
        return null;
    }


    private NodeDataType getNodeDataType(Row primaryRow) {
        int dataTypeColumnIndex = 3;
        Cell dataTypeCell = primaryRow.getCell(dataTypeColumnIndex);
        if (dataTypeCell != null && dataTypeCell.getCellType() == CellType.STRING) {
            String dataTypeString = dataTypeCell.getStringCellValue();
            if (dataTypeString != null && !dataTypeString.trim().isEmpty()) {
                return NodeDataType.valueOf(dataTypeString.trim().toUpperCase());
            }
        }
        return null;
    }

}
