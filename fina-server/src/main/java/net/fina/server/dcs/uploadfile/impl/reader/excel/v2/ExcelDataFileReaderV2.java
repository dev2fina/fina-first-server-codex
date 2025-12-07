package net.fina.server.dcs.uploadfile.impl.reader.excel.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.fina.common.client.exception.ConverterDcsTypeException;
import net.fina.common.client.exception.DcsTypeException;
import net.fina.common.client.returns.ReturnTableType;
import net.fina.server.dcs.uploadfile.impl.reader.excel.ExcelBaseReader;
import net.fina.server.dcs.uploadfile.impl.reader.excel.MatrixMappingOption;
import net.fina.server.dcs.uploadfile.impl.reader.excel.MatrixOptionBase;
import net.fina.server.dcs.uploadfile.impl.reader.excel.util.Conditions;
import net.fina.server.dcs.uploadfile.impl.reader.excel.util.ExcelPasswordValidator;
import net.fina.server.matrix.entity.SubMatrixTable;
import net.fina.server.matrix.entity.SubMatrixTableMapping;
import net.fina.server.matrix.entity.TableEndCondition;
import net.fina.server.returns.xml.Item;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.*;

public class ExcelDataFileReaderV2 extends ExcelBaseReader {
    protected final byte[] masterFileContent;
    protected final List<MatrixMappingOption> primaryOptions;
    private final Workbook masterWorkbook;
    protected String DEFAULT_SHEET_PROTECTION;
    protected Map sheetProtectionByFiType;
    protected MatrixOptionBase mainOption;
    private List<Item> mctItems;
    private List<Item> vctItems;
    private List<Item> mixedItems;
    private SpreadsheetVersion ssVersion;
    private int dividingRowNumber = 0;

    public ExcelDataFileReaderV2(byte[] masterFileContent, List<MatrixMappingOption> primaryOptions, Conditions condition, Map<String, Object> properties) throws Exception {
        this.masterFileContent = masterFileContent;
        this.primaryOptions = primaryOptions;
        masterWorkbook = checkAndCreateEncryptedWorkBookByPassword(masterFileContent, properties, true);

        incorrectSheetNames(condition);

        initSpreadsheetVersion(masterWorkbook);
        validateExcelContentWithExtension(masterWorkbook, (String) properties.get("dcs.file.extension"));

        Object vctEmptyLineObject = properties.get("converter.VCT.emptyLine");
        if (vctEmptyLineObject != null && (!vctEmptyLineObject.toString().isEmpty())) {
            String vctEmptyLine = vctEmptyLineObject.toString().trim();
            dividingRowNumber = Integer.parseInt(vctEmptyLine);
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

    private List<String> incorrectSheetNames(Conditions condition) throws Exception {
        /**
         * Options Sheets names
         */
        List<String> sheetsFromArray = new ArrayList<>();

        /**
         * File Real Sheets names
         */
        List<String> sheetsFromWBook = new ArrayList<>();

        for (MatrixMappingOption anOption : primaryOptions) {
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

        if (!reasons.isEmpty()) {
            DcsTypeException ex = new DcsTypeException(reasons);
            ex.setType(net.fina.common.client.exception.DcsTypeException.Type.INVALID_STRUCRURE);
            throw ex;
        }
        return sheetsFromArray;
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

    private void checkPasswordCorrection() throws DcsTypeException {
        for (MatrixMappingOption option : primaryOptions) {
            if (option.isProtected()) {
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

    public boolean hasSheet(String sheetName) {
        return masterWorkbook.getSheet(sheetName) != null;
    }

    public List<Item> getMCTresult(MatrixMappingOption option) {
        mctItems = new ArrayList<>();
        this.initMCTResults(option.getSheetName(), option.getTableList());
        if (!reasons.isEmpty()) {
            DcsTypeException ex = new DcsTypeException(reasons);
            emptyReasons();
            throw ex;
        }
        return this.mctItems;
    }

    public List<Item> getVCTResult(MatrixMappingOption option) {
        vctItems = new ArrayList<Item>();
        if (option.getTableList().isEmpty()) {
            log.warn("ReturnDefinition [" + option.getReturnCode() + "] has no tables defined....");
            return new ArrayList<>();
        }
        SubMatrixTable vctTable = option.getTableList().get(0);
        int startRowNumber = vctTable.getStartRow() - 1;

        initVCTResults(option, option.getTableList().get(0), startRowNumber);

        if (!reasons.isEmpty()) {
            DcsTypeException ex = new DcsTypeException(reasons);
            emptyReasons();
            log.error(ex.getMessage());
            throw ex;
        }

        return vctItems;
    }

    public List<Item> getMultiVCT(MatrixMappingOption option) {

        List<Item> multiVCTItems = new ArrayList<>();
        for (SubMatrixTable table : option.getTableList()) {
            vctItems = new ArrayList<Item>();
            initMultiVCT(option, table);
            multiVCTItems.addAll(vctItems);
        }

        if (!reasons.isEmpty()) {
            DcsTypeException ex = new DcsTypeException(reasons);
            emptyReasons();
            throw ex;
        }

        return multiVCTItems;
    }


    public List<Item> getMixedResults(MatrixMappingOption option) {
        this.mixedItems = new ArrayList<Item>();
        option.getTableList().sort(Comparator.comparingInt(o -> o.getDefinitionTable().getType().ordinal()));

        for (SubMatrixTable table : option.getTableList()) {
            this.initMixed(option, table);
        }
        if (!reasons.isEmpty()) {
            DcsTypeException ex = new DcsTypeException(reasons);
            emptyReasons();
            throw ex;
        }

        return mixedItems;
    }

    private void initMixed(MatrixMappingOption option, SubMatrixTable table) {
        if (table.getDefinitionTable().getType() == ReturnTableType.MCT) {
            this.mctItems = new ArrayList<>();
            this.initMCTResults(option.getSheetName(), Collections.singletonList(table));
            this.mixedItems.addAll(this.mctItems);
            this.mctItems = null;
        }
        if (table.getDefinitionTable().getType() == ReturnTableType.VCT) {
            this.vctItems = new ArrayList<>();
            initMultiVCT(option, table);
            this.mixedItems.addAll(this.vctItems);
            this.vctItems = null;
        }
    }

    //TODO Implement
    public List<Item> getCombined(MatrixMappingOption option) {
        throw new UnsupportedOperationException("Combined Type Is not implemented...");
    }

    private void initMCTResults(String sheetName, List<SubMatrixTable> tableList) {
        StringBuilder exceptionMessage = new StringBuilder();

        Sheet masterSheet = this.masterWorkbook.getSheet(sheetName);


        for (SubMatrixTable table : tableList) {

            for (SubMatrixTableMapping m : table.getTableMappings()) {
                Cell valueCell;
                String code = m.getMdtNode().getCode();
                String cellLocation = m.getCell();
                try {
                    try {
                        valueCell = masterSheet.getRow(CellRow(cellLocation)).getCell(CellColumn(cellLocation));
                    } catch (ConverterDcsTypeException ex) {
                        throw new ConverterDcsTypeException(sheetName, new CellReference(cellLocation).getCol(), ex.getMessage());
                    }
                } catch (ConverterDcsTypeException e) {
                    String message = e.getMessage();
                    reasons.add(message);
                    exceptionMessage.append(message);
                    exceptionMessage.append('\n');
                    continue;
                } catch (NullPointerException e) {
                    valueCell = null;
                    log.error("Cell " + cellLocation + " is NULL or Out Of Range!");
                }

                mctItems.add(new Item(code, 0, checkCellType(valueCell, m.getDataType(), m.getPrecision()).toString()));
            }
        }

        if (!exceptionMessage.isEmpty()) {
            log.error(exceptionMessage.toString());
        }
    }


    private void initVCTResults(MatrixMappingOption option, SubMatrixTable table, int startRowNumber) {
        Sheet masterSheet = this.masterWorkbook.getSheet(option.getSheetName());
        if (table.getTableMappings().isEmpty()) {
            log.error("ReturnDefinition [" + option.getReturnCode() + "] has no tables mappings defined....");
            throw new ConverterDcsTypeException(DcsTypeException.Type.GENERAL_ERROR);
        }

        int sheetLastRowNum = returnEndRowNumber(startRowNumber, option, table);

        for (int j = 0; j < table.getTableMappings().size(); j++) {
            SubMatrixTableMapping m = table.getTableMappings().get(j);
            int column;
            try {
                column = CellColumn(m.getCell().trim());
            } catch (ConverterDcsTypeException ex) {
                String message = ex.getMessage();
                reasons.add(message);
                continue;
            }

            String mdtCode = m.getMdtNode().getCode();

            for (int i = startRowNumber; i <= sheetLastRowNum; i++) {
                Row masterRow = masterSheet.getRow(i);

                if (isRowEmpty(masterRow)) {
                    vctItems.add(new Item(mdtCode, i - startRowNumber, ""));
                    continue;
                }
                Cell valueCell = masterRow.getCell(column);
                vctItems.add(new Item(mdtCode, i - startRowNumber, checkCellType(valueCell, m.getDataType(), m.getPrecision()).toString()));

            }
        }

    }


    private void initMultiVCT(MatrixMappingOption option, SubMatrixTable table) {
        String sheetName = option.getSheetName();

        int masterRowAfterHeader = table.getAfterHeaderRowAmount();
        int masterStartRow = table.getStartRow() + masterRowAfterHeader - 1;
        String beginText = table.getVctTableHeader() != null ? table.getVctTableHeader().trim() : null;

        if (masterStartRow <= masterRowAfterHeader) {
            masterStartRow = getTableStartRowNumber(sheetName, beginText) + masterRowAfterHeader + 1;
        }
        try {
            initVCTResults(option, table, masterStartRow);
        } catch (Exception ex) {
            log.error("at sheet " + sheetName, ex);
            throw ex;
        }
    }

    private boolean checkVctStopConditions(Row row, List<TableEndCondition> vctTableEndConditions) {
        boolean stop = false;
        for (TableEndCondition tec : vctTableEndConditions) {
            if (tec.getColumn() != null && tec.getCondition() != null) {
                Cell cell = row.getCell(CellColumn(tec.getColumn().trim()));
                stop = tec.getCondition().trim().equals(checkCellType(cell));
            }
        }
        return stop;
    }

    private boolean isVctTableEndConditionsEmpty(List<TableEndCondition> vctTableEndConditions) {
        boolean result = true;
        for (TableEndCondition tec : vctTableEndConditions) {
            result &= tec.getCondition().isEmpty();
        }
        return result;
    }


    private int returnEndRowNumber(int startRowNumber, MatrixMappingOption option, SubMatrixTable table) {
        Sheet sheet = this.masterWorkbook.getSheet(option.getSheetName());
        int lastRowNum = sheet.getLastRowNum();
        int emptyRowAmount = 0;

        for (int i = startRowNumber + 1; i <= lastRowNum; i++) {
            Row row = sheet.getRow(i);

            if (isRowEmpty(row) && (isVctTableEndConditionsEmpty(table.getVctTableEndConditions())) && (++emptyRowAmount >= dividingRowNumber)) {
                lastRowNum = i - emptyRowAmount;
                break;
            } else if (!isRowEmpty(row)) {
                /**
                 * if row is empty and both conditions are also empty text this
                 * condition is already checked;
                 */
                emptyRowAmount = 0;
                if (checkVctStopConditions(row, table.getVctTableEndConditions())) {
                    lastRowNum = i - 1;
                    break;
                }
            }
        }
        return lastRowNum;
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

}
