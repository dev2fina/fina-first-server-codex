package net.fina.server.returns.converter.impl;

import net.fina.server.returns.converter.api.HtmlHelper;
import net.fina.server.returns.converter.api.PoiConverter;
import org.apache.poi.ss.format.CellFormat;
import org.apache.poi.ss.format.CellFormatResult;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.jboss.logging.Logger;

import java.io.*;
import java.util.*;

public class PoiHtmlConverter implements PoiConverter {
    private final Logger log = Logger.getLogger(getClass().getName());

    private Workbook wb;
    private Appendable output;
    private boolean completeHTML;
    private Formatter out;
    private boolean gotBounds;
    private int firstColumn;
    private int endColumn;
    private HtmlHelper helper;

    private static final String DEFAULTS_CLASS = "excelDefaults";
    private static final String LINK_DEFAULT_MARGIN = "linkMargins";
    private static final String COL_HEAD_CLASS = "colHeader";
    private static final String ROW_HEAD_CLASS = "rowHeader";
    private Map<CellStyle, String> styleNameMap = new HashMap<>();
    private static final Map<HorizontalAlignment, String> HALIGN = mapFor(
            HorizontalAlignment.LEFT, "left",
            HorizontalAlignment.CENTER, "center",
            HorizontalAlignment.RIGHT, "right",
            HorizontalAlignment.FILL, "left",
            HorizontalAlignment.JUSTIFY, "left",
            HorizontalAlignment.CENTER_SELECTION, "center");

    private static final Map<VerticalAlignment, String> VALIGN = mapFor(
            VerticalAlignment.BOTTOM, "bottom",
            VerticalAlignment.CENTER, "middle",
            VerticalAlignment.TOP, "top");

    private static final Map<BorderStyle, String> BORDER = mapFor(
            BorderStyle.DASH_DOT, "dashed 1pt",
            BorderStyle.DASH_DOT_DOT, "dashed 1pt",
            BorderStyle.DASHED, "dashed 1pt",
            BorderStyle.DOTTED, "dotted 1pt",
            BorderStyle.DOUBLE, "double 3pt",
            BorderStyle.HAIR, "solid 1px",
            BorderStyle.MEDIUM, "solid 2pt",
            BorderStyle.MEDIUM_DASH_DOT, "dashed 2pt",
            BorderStyle.MEDIUM_DASH_DOT_DOT, "dashed 2pt",
            BorderStyle.MEDIUM_DASHED, "dashed 2pt",
            BorderStyle.NONE, "none",
            BorderStyle.SLANTED_DASH_DOT, "dashed 2pt",
            BorderStyle.THICK, "solid 3pt",
            BorderStyle.THIN, "solid 1pt");

    private static final int IDX_TABLE_WIDTH = -2;
    private static final int IDX_HEADER_COL_WIDTH = -1;
    private boolean enableExcelColumnHeaders;
    private boolean enableExcelRowHeaders;


    @SuppressWarnings({"unchecked"})
    private static <K, V> Map<K, V> mapFor(Object... mapping) {
        Map<K, V> map = new HashMap<>();
        for (int i = 0; i < mapping.length; i += 2) {
            map.put((K) mapping[i], (V) mapping[i + 1]);
        }
        return map;
    }


    public PoiHtmlConverter() {
        setupColorMap();
    }

    private void setupColorMap() {
        helper = new XSSFHtmlHelper();
    }

    public void setCompleteHTML(boolean completeHTML) {
        this.completeHTML = completeHTML;
    }

    public void printPage() throws IOException {
        try {
            ensureOut();
            if (completeHTML) {
                out.format(
                        "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>%n");
                out.format("<html>%n");
                out.format("<head>%n");
                out.format("</head>%n");
                out.format("<body>%n");
            }

            print();

            if (completeHTML) {
                out.format("</body>%n");
                out.format("</html>%n");
            }
        } finally {
            IOUtils.closeQuietly(out);
            if (output instanceof Closeable) {
                IOUtils.closeQuietly((Closeable) output);
            }
        }
    }

    public void print() {
        printInlineStyle();
        printSheets();
    }

    private void printInlineStyle() {
        //out.format("<link href=\"excelStyle.css\" rel=\"stylesheet\" type=\"text/css\">%n");
        out.format("<style type=\"text/css\">%n");
        printStyles();
        out.format("</style>%n");
    }

    private void ensureOut() {
        if (out == null) {
            out = new Formatter(output);
        }
    }

    public void printStyles() {
        ensureOut();

        // First, copy the base css
        String cssResource = getClass().getPackage().getName().replace('.', '/') + "/excelStyle.css";
        try (BufferedReader in = new BufferedReader(new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream(cssResource)))) {
            String line;
            while ((line = in.readLine()) != null) {
                out.format("%s%n", line);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Reading standard css", e);
        }

        // now add css for each used style
        Set<CellStyle> seen = new HashSet<>();
        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            Sheet sheet = wb.getSheetAt(i);
            Iterator<Row> rows = sheet.rowIterator();
            while (rows.hasNext()) {
                Row row = rows.next();
                for (Cell cell : row) {
                    CellStyle style = cell.getCellStyle();
                    if (!seen.contains(style)) {
                        printStyle(style);
                        seen.add(style);
                    }
                }
            }
        }
    }

    private void printStyle(CellStyle style) {
        String className = styleName(style);
        out.format(".%s .%s {%n", DEFAULTS_CLASS, className);
        styleContents(style);
        out.format("}%n");
        styleNameMap.put(style, className);
    }

    private void styleContents(CellStyle style) {
        styleOut("text-align", style.getAlignment(), HALIGN);
        styleOut("vertical-align", style.getVerticalAlignment(), VALIGN);
        fontStyle(style);
        borderStyles(style);
        helper.colorStyles(style, out);
    }

    private void borderStyles(CellStyle style) {
        styleOut("border-left", style.getBorderLeft(), BORDER);
        styleOut("border-right", style.getBorderRight(), BORDER);
        styleOut("border-top", style.getBorderTop(), BORDER);
        styleOut("border-bottom", style.getBorderBottom(), BORDER);
    }

    private void fontStyle(CellStyle style) {
        Font font = wb.getFontAt(style.getFontIndex());

        if (font.getBold()) {
            out.format("  font-weight: bold;%n");
        }
        if (font.getItalic()) {
            out.format("  font-style: italic;%n");
        }

        int fontheight = font.getFontHeightInPoints();
        if (fontheight == 9) {
            //fix for stupid ol Windows
            fontheight = 10;
        }
        out.format("  font-size: %dpt;%n", fontheight);

        // Font color is handled with the other colors
    }

    private String styleName(CellStyle style) {
        if (style == null) {
            style = wb.getCellStyleAt((short) 0);
        }
        StringBuilder sb = new StringBuilder();
        try (Formatter fmt = new Formatter(sb)) {
            fmt.format("style_%02x", style.getIndex());
            return fmt.toString();
        }
    }

    private <K> void styleOut(String attr, K key, Map<K, String> mapping) {
        String value = mapping.get(key);
        if (value != null) {
            out.format("  %s: %s;%n", attr, value);
        }
    }

    private static CellType ultimateCellType(Cell c) {
        CellType type = c.getCellType();
        if (type == CellType.FORMULA) {
            type = c.getCachedFormulaResultType();
        }
        return type;
    }

    private void printSheets() {
        ensureOut();
        int numberOfSheets = wb.getNumberOfSheets();
        boolean fileNameNavigation = numberOfSheets > 1;
        if (fileNameNavigation) {
            for (int i = 0; i < numberOfSheets; i++) {
                String sheetName = wb.getSheetName(i);
                out.format("<div style=\"text-align: center\"><a href=\"#%s\">%s</a></div>", sheetName, sheetName);
            }
        }
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = wb.getSheetAt(i);
            printSheet(sheet, numberOfSheets > 1);
        }
    }

    public void printSheet(Sheet sheet, boolean lineSeparator) {
        ensureOut();
        Map<Integer, Integer> widths = computeWidths(sheet);
        int tableWidth = widths.get(IDX_TABLE_WIDTH);
        out.format("<table id=\"%s\"class=%s style=\"width:%dpx;\">%n", sheet.getSheetName(), DEFAULTS_CLASS, tableWidth);
        printCols(widths);
        printSheetContent(sheet);
        out.format("</table>%n");
        if (lineSeparator) {
            out.format("<hr>");
        }
    }

    /**
     * computes the column widths, defined by the sheet.
     *
     * @param sheet The sheet for which to compute widths
     * @return Map with key: column index; value: column width in pixels
     * <br>special keys:
     * <br>{@link #IDX_HEADER_COL_WIDTH} - width of the header column
     * <br>{@link #IDX_TABLE_WIDTH} - width of the entire table
     */
    private Map<Integer, Integer> computeWidths(Sheet sheet) {
        Map<Integer, Integer> ret = new TreeMap<>();
        int tableWidth = 0;

        ensureColumnBounds(sheet);

        // compute width of the header column
        int lastRowNum = sheet.getLastRowNum();
        int headerCharCount = String.valueOf(lastRowNum).length();
        int headerColWidth = widthToPixels((headerCharCount + 1) * 256.0);
        ret.put(IDX_HEADER_COL_WIDTH, headerColWidth);
        tableWidth += headerColWidth;

        for (int i = firstColumn; i < endColumn; i++) {
            int colWidth = widthToPixels(sheet.getColumnWidth(i));
            ret.put(i, colWidth);
            tableWidth += colWidth;
        }

        ret.put(IDX_TABLE_WIDTH, tableWidth);
        return ret;
    }

    /**
     * Probably platform-specific, but appears to be a close approximation on some systems
     *
     * @param widthUnits POI's native width unit (twips)
     * @return the approximate number of pixels for a typical display
     */
    protected int widthToPixels(final double widthUnits) {
        return Math.toIntExact(Math.round(widthUnits * 9 / 256));
    }

    private void printCols(Map<Integer, Integer> widths) {
        int headerColWidth = widths.get(IDX_HEADER_COL_WIDTH);
        if (enableExcelColumnHeaders) {
            out.format("<col style=\"width:%dpx\"/>%n", headerColWidth);
        }
        for (int i = firstColumn; i < endColumn; i++) {
            int colWidth = widths.get(i);
            out.format("<col style=\"width:%dpx;\"/>%n", colWidth);
        }
    }

    private void ensureColumnBounds(Sheet sheet) {
        if (gotBounds) {
            return;
        }

        Iterator<Row> iter = sheet.rowIterator();
        firstColumn = (iter.hasNext() ? Integer.MAX_VALUE : 0);
        endColumn = 0;
        while (iter.hasNext()) {
            Row row = iter.next();
            short firstCell = row.getFirstCellNum();
            if (firstCell >= 0) {
                firstColumn = Math.min(firstColumn, firstCell);
                endColumn = Math.max(endColumn, row.getLastCellNum());
            }
        }
        gotBounds = true;
    }

    private void printColumnHeads() {
        out.format("<thead>%n");
        out.format("  <tr class=%s>%n", COL_HEAD_CLASS);
        out.format("    <th class=%s>&#x25CA;</th>%n", COL_HEAD_CLASS);
        //noinspection UnusedDeclaration
        StringBuilder colName = new StringBuilder();
        for (int i = firstColumn; i < endColumn; i++) {
            colName.setLength(0);
            int cnum = i;
            do {
                colName.insert(0, (char) ('A' + cnum % 26));
                cnum /= 26;
            } while (cnum > 0);
            out.format("    <th class=%s>%s</th>%n", COL_HEAD_CLASS, colName);
        }
        out.format("  </tr>%n");
        out.format("</thead>%n");
    }


    private void printSheetContent(Sheet sheet) {
        if (enableExcelColumnHeaders) {
            printColumnHeads();
        }

        out.format("<tbody>%n");
        Iterator<Row> rows = sheet.rowIterator();
        while (rows.hasNext()) {
            Row row = rows.next();

            out.format("  <tr>%n");
            if (enableExcelRowHeaders) {
                out.format("    <td class=%s>%d</td>%n", ROW_HEAD_CLASS, row.getRowNum() + 1);
            }
            for (int i = firstColumn; i < endColumn; i++) {
                String content = "&nbsp;";
                String attrs = "";
                CellStyle style = null;
                if (i >= row.getFirstCellNum() && i < row.getLastCellNum()) {
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        style = cell.getCellStyle();
                        attrs = tagStyle(cell, style);
                        //Set the value that is rendered for the cell
                        //also applies the format
                        CellFormat cf = CellFormat.getInstance(
                                style.getDataFormatString());
                        CellFormatResult result = cf.apply(cell);
                        content = result.text; // never null
                        if (content.isEmpty()) {
                            content = "&nbsp;";
                        }
                    }
                }
                out.format("    <td class=%s %s><div style='%s'>%s</div></td>%n", styleNameMap.getOrDefault(style, ""),
                        attrs, cellFontColorStyle(style), content);
            }
            out.format("  </tr>%n");
        }
        out.format("</tbody>%n");
    }

    private String cellFontColorStyle(CellStyle style) {
        if (style != null) {
            XSSFFont font = (XSSFFont) wb.getFontAt(style.getFontIndex());
            if (font != null && font.getXSSFColor() != null) {
                byte[] rgb = font.getXSSFColor().getRGB();
                return String.format(" color: #%02x%02x%02x; ", rgb[0], rgb[1], rgb[2]);
            }
        }

        return "";
    }

    private String tagStyle(Cell cell, CellStyle style) {
        if (style.getAlignment() == HorizontalAlignment.GENERAL) {
            switch (ultimateCellType(cell)) {
                case STRING:
                    return "style=\"text-align: left;\"";
                case BOOLEAN:
                case ERROR:
                    return "style=\"text-align: center;\"";
                case NUMERIC:
                default:
                    // "right" is the default
                    break;
            }
        }
        return "";
    }


    @Override
    public byte[] convert(Workbook wb) {

        try (
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                PrintWriter pw = new PrintWriter(outputStream);
        ) {
            this.output = pw;
            this.wb = wb;
            this.setCompleteHTML(true);
            this.printPage();

            return outputStream.toByteArray();

        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }

        return null;

    }
}
