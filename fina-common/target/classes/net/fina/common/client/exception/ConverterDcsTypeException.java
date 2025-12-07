package net.fina.common.client.exception;

/**
 * Converter type exception.
 *
 * @author Chelomisha.
 */
@SuppressWarnings("serial")
public class ConverterDcsTypeException extends DcsTypeException {

    // public static final String REASON_SEPARATOR = "REASON";
    private String reason;

    public ConverterDcsTypeException() {
        super(Type.GENERAL_ERROR);
    }

    public ConverterDcsTypeException(String message) {
        super(message);
        this.reason = message;
    }

    /**
     * Constructor.
     *
     * @param sheetName        sheet name.
     * @param rowIndex         row index.
     * @param columnIndex      column index.
     * @param preferedCellType cell type.
     */
    public ConverterDcsTypeException(String sheetName, int rowIndex, int columnIndex, ExcelCellType preferedCellType) {
        super(DcsTypeException.Type.ILLEGAL_CELL_TYPE);
        this.sheet = sheetName;
        this.row = rowIndex;
        this.column = columnIndex;
        this.preferedCellType = preferedCellType;
        this.reason = (formatCellReference() + ": ${net.fina.dcs.converter.hasIllegalType}, ${net.fina.dcs.converter.preferredTypeIs}: " + preferedCellType.name());
    }

    /**
     * Constructor.
     *
     * @param sheetName   sheet name.
     * @param rowIndex    row index.
     * @param columnIndex column index.
     * @param type        type.
     */
    public ConverterDcsTypeException(String sheetName, int rowIndex, int columnIndex, DcsTypeException.Type type) {
        super(type);
        this.sheet = sheetName;
        this.row = rowIndex;
        this.column = columnIndex;

        if (type == DcsTypeException.Type.CELL_IS_NULL_OR_IS_EMPTY) {
            this.reason = (formatCellReference() + ": ${net.fina.dcs.converter.isEmpty}");
        }
    }

    /**
     * Constructor.
     *
     * @param sheetName   sheet name.
     * @param rowIndex    row index.
     * @param columnIndex column index.
     * @param message     exception message.
     */
    public ConverterDcsTypeException(String sheetName, int rowIndex, int columnIndex, String message) {
        this.sheet = sheetName;
        this.row = rowIndex;
        this.column = columnIndex;
        this.reason = formatCellReference() + ": " + message;
    }

    /**
     * Constructor.
     *
     * @param sheetName   sheet name.
     * @param columnIndex column index.
     * @param message     exception message.
     */
    public ConverterDcsTypeException(String sheetName, int columnIndex, String message) {
        this.sheet = sheetName;
        this.column = columnIndex;
        this.reason = formatCellReference() + ": " + message;
    }

    private String formatCellReference() {
        return (" '" + sheet + "!" + (getColumnName() + (getRowIndex() + 1)) + "'");
    }

    public ConverterDcsTypeException(Throwable throwable, Type type) {
        super(throwable, type);
    }

    public ConverterDcsTypeException(Type type) {
        super(type);
    }

    public ConverterDcsTypeException(Type type, String reason) {
        super(type);
        this.reason = reason;
    }

    public ConverterDcsTypeException(Throwable throwable, Type type, String reason) {
        this(throwable, type);
        this.reason = reason;
    }

    public ConverterDcsTypeException(Throwable throwable) {
        super(throwable);
    }

    public enum ExcelCellType {
        GENERAL,
        NUMERIC,
        DATE,
        TEXT,
        LOGICAL;
    }

    // private String workbook;
    private String sheet = null;
    private int row = -1;
    private int column = -1;
    private ExcelCellType preferedCellType = ExcelCellType.GENERAL;

    /**
     * Get column name.
     *
     * @param columnIndex index.
     * @return column name.
     */
    public static String columnNameAsInExcel(int columnIndex) {
        ++columnIndex;
        String column = "";
        int twentySix = 26;

        while (columnIndex > 0) {
            columnIndex--;
            column = (char) ('A' + columnIndex % twentySix) + column;
            columnIndex /= twentySix;
        }
        return column;
    }

    private String getSheetName() {
        return this.sheet;
    }

    private int getRowIndex() {
        return this.row;
    }

    private int getColumnIndex() {
        return this.column;
    }

    private String getColumnName() {
        return ConverterDcsTypeException.columnNameAsInExcel(this.getColumnIndex());
    }

    private ExcelCellType getPreferedType() {
        return this.preferedCellType;
    }

    @Override
    public String getMessage() {
        return reason;
    }
}
