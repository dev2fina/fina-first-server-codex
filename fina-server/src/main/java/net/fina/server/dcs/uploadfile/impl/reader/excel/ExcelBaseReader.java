package net.fina.server.dcs.uploadfile.impl.reader.excel;

import net.fina.common.client.exception.ConverterDcsTypeException;
import net.fina.common.client.exception.ConverterDcsTypeException.ExcelCellType;
import net.fina.common.client.exception.DcsTypeException;
import net.fina.common.client.exception.DcsTypeException.Type;
import net.fina.server.dcs.uploadfile.impl.reader.excel.util.NodeDataType;
import net.fina.server.i18n.entity.Language;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.format.CellDateFormatter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jboss.logging.Logger;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class ExcelBaseReader {

    public static final String CELL_REF_PATTERN_STRING = "\\$?([A-Za-z]+)\\$?([0-9]+)";
    public static final String COLUMN_REF_PATTERN_STRING = "\\$?([A-Za-z]+)";
    public static final String ROW_REF_PATTERN_STRING = "\\$?([0-9]+)";

    /*
     * set excel sheet max size to 500 MB
     * */
    static {
        IOUtils.setByteArrayMaxOverride(5 * 100_000_000);
    }

    protected final String mainSheetName = "options";
    private final Map<Integer, Integer> vctColumnPrecisionMap = new HashMap<>();
    protected Logger log = Logger.getLogger(getClass());
    protected List<String> reasons = new ArrayList<>();
    protected Workbook workbook;
    protected int optionsSheetindex;
    protected Map<String, Object> properties;
    protected String dateFormat = "dd, mm. yyyy";
    protected String dateTimeFormat = "dd, mm. yyyy HH:mm";

    public void initWorkBook(String excelFilePath, Map<String, Object> properties) throws IOException, InvalidFormatException {
        this.workbook = checkAndCreateEncryptedWorkBookByPassword(excelFilePath, properties, false);
        this.optionsSheetindex = this.optionSheetIndex();
        this.properties = properties;
        initDateFormatString(properties);
    }

    protected void validateExcelContentWithExtension(Workbook wb, String extension) {
        if ((wb instanceof XSSFWorkbook) && extension.equalsIgnoreCase(".xlsx")) {
            return;
        }

        if ((wb instanceof HSSFWorkbook) && extension.equalsIgnoreCase(".xls")) {
            return;
        }

        if (extension.equalsIgnoreCase(".xlsm")) {
            return;
        }

        Type type = Type.EXCEL_FILE_BUT_WRONG_EXCEL_EXTENSION;
        throw new ConverterDcsTypeException(type, type.getReplaceableCode());
    }

    /**
     * it is strongly recommended use the method as the latest method because
     * all exception messages will be there
     *
     * @return reasons
     */
    public List<String> getReasons() {
        return reasons;
    }

    public void emptyReasons() {
        this.reasons = new ArrayList<String>();
    }

    private void initDateFormatString(Map<String, Object> properties) {
        if (properties != null) {
            this.dateFormat = ((Language) this.properties.get("dcs.language")).getDateFormat();
            this.dateTimeFormat = ((Language) this.properties.get("dcs.language")).getDateTimeFormat();
        }
    }

    private int optionSheetIndex() {
        return this.workbook.getSheetIndex(mainSheetName);
    }

    protected List<String> getColumnNames(String sheetName) {
        List<String> names = new ArrayList<String>();

        Sheet sheet = workbook.getSheet(sheetName);
        Row row = sheet.getRow(0);
        int LastCellNum = row.getLastCellNum();

        for (int currentColumn = 0; currentColumn < LastCellNum; currentColumn++) {
            names.add(row.getCell(currentColumn).getStringCellValue());
        }
        return names;
    }

    protected void checkOptionsIndex() {
        if (this.optionsSheetindex < 0) {
            Type type = Type.OPTIONS_SHEET_FAILURE;
            throw new ConverterDcsTypeException(type, type.getReplaceableCode());
        }
    }

    public int lastRowNumb(Sheet sheet) {
        return lastRowNumb(sheet, 0);
    }

    // use this method when reading primary (not option) sheet of  primaryMatrix
    public int lastRowNumb(Sheet sheet, int startRow) {
        int lastRowNumber = ((sheet == null) ? 0 : sheet.getLastRowNum());
        for (int i = Math.max(0, startRow); i <= lastRowNumber; i++) {
            if (sheet != null) {
                if (isRowEmpty(sheet.getRow(i))) {
                    lastRowNumber = i - 1;
                    break;
                }
            }
        }
        return lastRowNumber;
    }

    public boolean isRowEmpty(Row row) {
        boolean empty = (row == null);
        if (!empty) {
            empty = ExcelEmptyRowDetector.isRowEmpty(row, 0, row.getLastCellNum());
        }
        return empty;
    }

    protected Workbook checkAndCreateEncryptedWorkBookByPassword(String excelFile, Map<String, Object> properties, boolean checkEncryption) throws IOException, InvalidFormatException {
        try (InputStream dataStream = new FileInputStream(excelFile); BufferedInputStream buff = new BufferedInputStream(dataStream)) {
            return checkAndCreateEncryptedWorkBookByPassword(buff, properties, checkEncryption);
        }
    }

    protected Workbook checkAndCreateEncryptedWorkBookByPassword(byte[] fileContent, Map<String, Object> properties, boolean checkEncryption) throws IOException, InvalidFormatException {
        try (InputStream dataStream = new ByteArrayInputStream(fileContent); BufferedInputStream buff = new BufferedInputStream(dataStream)) {
            return checkAndCreateEncryptedWorkBookByPassword(buff, properties, checkEncryption);
        }
    }

    private Workbook checkAndCreateEncryptedWorkBookByPassword(BufferedInputStream buff, Map<String, Object> properties, boolean checkEncryption) throws IOException, InvalidFormatException {
        try {
            if (properties != null) {
                Object property = properties.get("dcs.primary.matrix.option");
                if (property != null) {
                    MatrixOptionBase option = (MatrixOptionBase) property;
                    if (option.getWorkBookPassword() != null && !option.getWorkBookPassword().isEmpty() && checkEncryption) {
                        // Ensure that there is at least some data there
                        byte[] header8 = IOUtils.peekFirst8Bytes(buff);

                        //TODO ....
                        // Try to create
//                        if (!NPOIFSFileSystem.hasPOIFSHeader(header8)) {
//                            throw DcsTypeException.create(DcsTypeException.Type.SECURITY_INVALID_ENCRYPT, "File isn't encrypted");
//                        }
                        return WorkbookFactory.create(buff, option.getWorkBookPassword());
                    }
                }
            }
            return WorkbookFactory.create(buff);
        } catch (EncryptedDocumentException ex) {
            log.error(ex.getMessage(), ex);
            throw DcsTypeException.create(DcsTypeException.Type.SECURITY_INVALID_ENCRYPT, ex.getMessage());
        }
    }

    protected ConverterDcsTypeException generateOptionsSheetIllegalCellTypeCase(Cell cell, ExcelCellType type) {
        ConverterDcsTypeException converterDecsTypeException;
        converterDecsTypeException = new ConverterDcsTypeException(mainSheetName, cell.getRowIndex(), cell.getColumnIndex(), type);
        converterDecsTypeException.setType(DcsTypeException.Type.ILLEGAL_CELL_TYPE);
        return converterDecsTypeException;
    }

    protected ConverterDcsTypeException generateOptionsSheetEmptyCellTypeCase(int rowIndex, int columnIndex) {
        ConverterDcsTypeException converterDecsTypeException;
        converterDecsTypeException = new ConverterDcsTypeException(mainSheetName, rowIndex, columnIndex, DcsTypeException.Type.CELL_IS_NULL_OR_IS_EMPTY);
        return converterDecsTypeException;
    }

    public Object checkCellType(Cell cell) {
        if (cell == null) {
            return "";
        }

        return checkCellType(cell, null, null);
    }

    public Object checkCellType(Cell cell, NodeDataType nodeDataType, Integer precision) {
        if (cell == null) {
            return "";
        }

        if (nodeDataType == NodeDataType.STRING) {
            cell.setCellType(CellType.STRING);
        }

        Object value = null;

        switch (cell.getCellType()) {
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    value = (new CellDateFormatter(nodeDataType != NodeDataType.DATETIME ? dateFormat : dateTimeFormat)).format(cell.getDateCellValue());
                } else {
                    if (precision != null) {
                        value = BigDecimal.valueOf(cell.getNumericCellValue()).setScale(precision, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
                    } else {
                        value = cell.getNumericCellValue();
                    }
                }
                break;
            case STRING:
                value = cell.getStringCellValue();
                break;
            case BLANK:
                value = "";
                break;
            case BOOLEAN:
                value = cell.getBooleanCellValue();
                break;
            case ERROR:
                value = FormulaError.forInt(cell.getErrorCellValue()).getString();
                break;
            case FORMULA: {
                CellType cellType = cell.getCachedFormulaResultType();

                switch (cellType) {
                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(cell)) {
                            value = (new CellDateFormatter(nodeDataType != NodeDataType.DATETIME ? dateFormat : dateTimeFormat)).format(cell.getDateCellValue());
                        } else {
                            value = cell.getNumericCellValue();
                        }
                        break;
                    case STRING:
                        value = cell.getStringCellValue();
                        break;
                    case BOOLEAN:
                        value = cell.getBooleanCellValue();
                        break;
                    case ERROR:
                        value = FormulaError.forInt(cell.getErrorCellValue()).getString();
                        break;
                    default:
                        value = "";
                        break;
                }
            }
            break;
            default:
                value = cell.toString();
                break;
        }
        return value;
    }


    // rom shemova 'C8' daabrunebs 7-s

    /**
     * @param codeValue like 'B5' if parameter equals 'B5' it returns 4
     * @return index of the row
     */
    protected int CellRow(String codeValue) {
        int rowIndex = -1;

        if (Pattern.compile(CELL_REF_PATTERN_STRING).matcher(codeValue).matches()) {
            rowIndex = (new CellReference(codeValue)).getRow();
        } else if (Pattern.compile(ROW_REF_PATTERN_STRING).matcher(codeValue).matches()) {
            rowIndex = (new CellReference("a" + codeValue)).getRow();
        }

        if (rowIndex == -1) {
            if (codeValue.trim().isEmpty()) {
                throw new ConverterDcsTypeException("${net.fina.dcs.converter.isEmpty}");
            }
            throw new ConverterDcsTypeException("${net.fina.dcs.converter.illegalCellReference} " + codeValue);
        }

//        if (!CellReference.isRowWithnRange(rowIndex.toString(), ssVersion)) {
//            throw new ConverterDcsTypeException("${net.fina.dcs.converter.cellReferenceOutOfRange} " + codeValue);
//        }
        return rowIndex;
    }
    // rom shemova 'C8' daabrunebs 2-s

    /**
     * @param codeValue like 'B5' if parameter equals 'B5' it returns 1
     * @return: index of the column
     */
    protected int CellColumn(String codeValue) {
        int colIndex = -1;

        if (Pattern.compile(CELL_REF_PATTERN_STRING).matcher(codeValue).matches()) {
            colIndex = (int) new CellReference(codeValue).getCol();
        } else if (Pattern.compile(COLUMN_REF_PATTERN_STRING).matcher(codeValue).matches()) {
            colIndex = CellReference.convertColStringToIndex(codeValue);
        }

        if (colIndex == -1) {
            if (codeValue.trim().isEmpty()) {
                throw new ConverterDcsTypeException(" ${net.fina.dcs.converter.isEmpty}");
            }
            throw new ConverterDcsTypeException("${net.fina.dcs.converter.illegalCellReference} " + codeValue);
        }

//        if (!CellReference.isColumnWithnRange(colIndex.toString(), ssVersion)) {
//            throw new ConverterDcsTypeException("${net.fina.dcs.converter.cellReferenceOutOfRange} " + codeValue);
//        }
        return colIndex;
    }

    protected Integer getNodePrecision(Row primaryRow, int columnIndex) {
        if (vctColumnPrecisionMap.containsKey(columnIndex)) {
            return vctColumnPrecisionMap.get(columnIndex);
        }

        Integer precision = getNodePrecision(primaryRow);
        if (precision != null) {
            vctColumnPrecisionMap.put(columnIndex, precision);
        }


        return precision;
    }

    protected Integer getNodePrecision(Row primaryRow) {
        if (primaryRow == null) {
            log.error("Primary row is null");
            return null;
        }

        int precisionCellIndex = 4;
        Cell precisionCell = primaryRow.getCell(precisionCellIndex);
        if (precisionCell != null && precisionCell.getCellType().equals(CellType.NUMERIC)) {

            return ((Number) precisionCell.getNumericCellValue()).intValue();
        }
        return null;
    }

}
