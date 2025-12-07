package net.fina.server.returns.impl;

import fina2.ui.returns.ValuesTableRow;
import net.fina.common.client.mdt.MDTNodeDataTypes;
import net.fina.common.client.mdt.MDTNodeTypes;
import net.fina.common.client.returns.ProcessReturnInfo;
import net.fina.common.client.returns.ReturnTableType;
import net.fina.odstoolkit.writer.AooWriterBase;
import net.fina.server.returns.entity.DefinitionTable;
import net.fina.server.returns.model.RDataMetaModel;
import net.fina.server.returns.model.RItemMetaModel;
import net.fina.server.returns.model.RTableMetaModel;
import net.fina.server.returns.model.RTableRowMetaModel;
import net.fina.server.util.ReturnFormatHelper;
import org.jboss.logging.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: nikoloz
 * Date: 8/2/13
 * Time: 10:49 PM
 */
public class ReturnSheetManager {
    private Logger log = Logger.getLogger(getClass());

    private AooWriterBase aooWriterBase;
    private RDataMetaModel rdm;
    private ProcessReturnInfo returnInfo;

    private DateFormat df;

    private DateFormat dateTimeFormat;

    private List<DefinitionTable> tables;

    private long langId;

    private ReturnFormatHelper returnFormatHelper;

    private CellCoordinate coordinate;

    public ReturnSheetManager(AooWriterBase aooWriterBase, RDataMetaModel rdm, ProcessReturnInfo returnInfo) {
        this.aooWriterBase = aooWriterBase;
        this.rdm = rdm;
        this.returnInfo = returnInfo;
        df = new SimpleDateFormat(returnInfo.getDateFormat());
        dateTimeFormat = new SimpleDateFormat(returnInfo.getDateTimeFormat());
    }

    public ReturnSheetManager(AooWriterBase aooWriterBase, List<DefinitionTable> tables, List<String> returnHeader, ProcessReturnInfo returnInfo, long langId) {
        this.aooWriterBase = aooWriterBase;
        this.tables = tables;
        this.returnInfo = returnInfo;
        this.langId = langId;
        df = new SimpleDateFormat(returnInfo.getDateFormat());
        dateTimeFormat = new SimpleDateFormat(returnInfo.getDateTimeFormat());
    }

    public void execute() {
        // Header Data
        Map<String, String> headerDataMap = getHeaderData();

        //Remove comment located in 0x0 cell
        aooWriterBase.setCellValue(0, 0, " ");

        if (setHeader(headerDataMap)) {
            aooWriterBase.setCellValue(1, 1, "[" + rdm.getReturnCode() + "]" + rdm.getReturnDescription());
            aooWriterBase.setCellValue(1, 2, "Bank Code: " + rdm.getFiCode() + "     Bank Name: " + rdm.getFiDescription());
            aooWriterBase.setCellValue(1, 3, "As at: " + rdm.getToDate());
        }

        CellCoordinate coordinate = new CellCoordinate(1, 10);

        for (RTableMetaModel table : rdm.getTables()) {
            placeTable(table, coordinate);
        }
    }

    public void executeForTemplate() {
        aooWriterBase.setCellValue(0, 0, " ");
        coordinate = new CellCoordinate(1, 10);

        for (DefinitionTable table : tables) {
            placeTableForTemplate(table, langId);
        }
    }


    private Map<String, String> getHeaderData() {
        Map<String, String> headerDataMap = new HashMap<>();

        headerDataMap.put(ReturnFormatConstants.FI_CODE, rdm.getFiCode());
        headerDataMap.put(ReturnFormatConstants.FI_NAME, rdm.getFiDescription());

        headerDataMap.put(ReturnFormatConstants.RETURN_CODE, rdm.getReturnCode());
        headerDataMap.put(ReturnFormatConstants.RETURN_NAME, rdm.getReturnDescription());
        headerDataMap.put(ReturnFormatConstants.RETURN_STATUS, rdm.getStatusName());

        headerDataMap.put(ReturnFormatConstants.RETURN_TYPE_CODE, rdm.getReturnTypeCode());

        headerDataMap.put(ReturnFormatConstants.RETURN_VERSION_CODE, rdm.getVersionCode());
        headerDataMap.put(ReturnFormatConstants.RETURN_VERSION_NAME, rdm.getVersionDescription());

        headerDataMap.put(ReturnFormatConstants.PERIOD_FROM, df.format(rdm.getFromDate()));
        headerDataMap.put(ReturnFormatConstants.PERIOD_TO, df.format(rdm.getToDate()));
        headerDataMap.put(ReturnFormatConstants.PERIOD_TYPE_CODE, rdm.getPeriodTypeCode());
        headerDataMap.put(ReturnFormatConstants.PERIOD_TYPE_NAME, rdm.getPeriodTypeDescription());

        headerDataMap.put(ReturnFormatConstants.USER_LOGIN, rdm.getUserLogin());
        headerDataMap.put(ReturnFormatConstants.USER_NAME, rdm.getUserName());

        return headerDataMap;
    }

    private boolean setHeader(Map<String, String> headerDataMap) {
        boolean empty = true;
        for (int i = 0; i < ReturnFormatConstants.MAX_COLUMN; i++) {
            for (int j = 0; j < ReturnFormatConstants.MAX_ROW; j++) {
                String value = aooWriterBase.getCellStringValue(i, j);
                if (value != null && (!value.isEmpty())) {
                    for (Map.Entry<String, String> e : headerDataMap.entrySet()) {
                        if (value.contains(e.getKey())) {
                            value = value.replace(e.getKey(), e.getValue());
                            aooWriterBase.setCellValue(i, j, value);
                            empty = false;
                        }
                    }
                }
            }
        }

        try {
            aooWriterBase.setHeaderData(headerDataMap);
            aooWriterBase.setFooterData(headerDataMap);
        } catch (Exception e) {
            log.error("Failed to replace constants in document header/footer.", e);
        }

        return empty;
    }

    private List<RItemMetaModel> getColumnProcessItems(RTableMetaModel table, int columnIndex) {
        List<RItemMetaModel> items = new ArrayList<>();
        for (int i = 1; i < table.getRows().size(); i++) {
            RTableRowMetaModel row = table.getRows().get(i);
            items.add(row.getRowItems().get(columnIndex));
        }
        return items;
    }

    private void placeTable(RTableMetaModel table, CellCoordinate coordinate) {
        switch (table.getType()) {
            case NT:
            case MCT:
                placeMCT(table, coordinate);
                break;
            case VCT:
                placeVCT(table, coordinate);
                break;
            default:
                break;
        }
    }

    private void placeMCT(RTableMetaModel table, CellCoordinate coordinate) {
        for (int rowIndex = 0; rowIndex < table.getRows().size(); rowIndex++) {
            RTableRowMetaModel tableRow = table.getRows().get(rowIndex);
            for (int columnIndex = 0; columnIndex < tableRow.getRowItems().size(); columnIndex++) {
                RItemMetaModel pItem = tableRow.getRowItems().get(columnIndex);
                if (pItem != null) {
                    if (rowIndex == 0 || columnIndex == 0) {
                        String value = pItem.getDescription() != null ? pItem.getDescription() : "";
                        aooWriterBase.setCellValue(coordinate.getColumn() + columnIndex, coordinate.getRow() + rowIndex, value);
                    } else {
                        setCellValue(pItem, coordinate.offset(columnIndex, rowIndex));
                    }
                }
            }
        }
        coordinate.setRow(coordinate.getRow() + table.getRows().size() + 4);
    }

    public void placeTableForTemplate(DefinitionTable table, long langId) {
        returnFormatHelper = new ReturnFormatHelper();
        List<ValuesTableRow> rows = returnFormatHelper.getReviewTableFormatRows(table, langId);

        int row = coordinate.getRow();
        int column = coordinate.getColumn();

        aooWriterBase.setCellValue(1, 6, table.getNode().getDescription().getDescription(langId) + " | Return Type: " + table.getType().name());

        int tableStartRow = row;
        int rc = 0;

        if (!rows.isEmpty()) {
            ValuesTableRow title = rows.get(0);
            Object[][] data = new Object[rows.size()][title.getColumnCount()];
            rows.remove(title);

            for (int i = 0; i < title.getColumnCount(); i++) {
                data[rc][i] = title.getValue(i);
            }
            rc++;
            row += 2;

            for (ValuesTableRow roww : rows) {
                for (int i = 0; i < title.getColumnCount(); i++) {
                    if (i > 0) {
                        data[rc][i] = roww.getCode(i) + " (" + roww.getDataType(i) + "|" + roww.getType(i) + ")";
                    } else {
                        data[rc][i] = roww.getValue(i);
                    }
                }
                row++;
                rc++;
            }

            int rowOnSheet = tableStartRow;
            for (int j = 0; j < rc; j++) {
                for (int i = 0; i < title.getColumnCount(); i++) {
                    Object value = data[j][i] == null ? "" : data[j][i];
                    aooWriterBase.setCellValue(i + 1, rowOnSheet, (String) value);
                }
                rowOnSheet++;
            }

            if (table.getType() == ReturnTableType.VCT) {
                for (int i = 0; i < title.getColumnCount(); i++) {
                    aooWriterBase.setCellValue(i + 1, rowOnSheet, "");
                }
            }
            row += 3;

            coordinate.setRow(row);
            coordinate.setColumn(column);
        }
    }

    private void placeVCT(RTableMetaModel table, CellCoordinate coordinate) {
        boolean totalTop = table.getVisibleLevel() > 10;

        if (table.getRows().size() > 2) {
            int existingRows = aooWriterBase.getRowCount();

            int rowsToAddFrom = Math.min(existingRows, coordinate.getRow() + (totalTop ? 3 : 2));

            int rowsToAdd = table.getRows().size() - 2;

            if (rowsToAddFrom < coordinate.getRow() + 2) {
                rowsToAdd += (coordinate.getRow() + 2) - rowsToAddFrom;
            }

            if (rowsToAddFrom == existingRows) {
                aooWriterBase.appendRows(rowsToAdd);
            } else {
                aooWriterBase.insertRow(rowsToAddFrom, rowsToAdd);
            }
        }

        if (totalTop) {
            setVctTotal(table, new CellCoordinate(coordinate.getColumn(), coordinate.getRow() + 1), true);
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

                        aooWriterBase.setCellValue(coordinate.getColumn() + columnIndex, titleRow, value);
                    } else {
                        if (aooWriterBase.isPercentageValueType(coordinate.getColumn() + columnIndex, coordinate.getRow() + 1)) {
                            aooWriterBase.setPercentageValueType(coordinate.getColumn() + columnIndex, coordinate.getRow() + rowIndex);
                            if (rowIndex == 1 && (pItem.getValue() == null || pItem.getValue().trim().isEmpty())) {
                                pItem.setValue("0.0");
                            }
                        }
                        setCellValue(pItem, coordinate.offset(columnIndex, rowIndex));
                    }
                }
            }
        }

        coordinate.setRow(coordinate.getRow() + table.getRows().size());


        if (!totalTop) {
            setVctTotal(table, coordinate, false);
        }

        coordinate.setRow(coordinate.getRow() + (!totalTop ? 4 : 3));
    }

    private void setVctTotal(RTableMetaModel table, CellCoordinate coordinate, boolean totalTop) {
        JSTreeAggregate jsTree = new JSTreeAggregate(returnInfo);
        if (table.getEvalMethod() != null) {
            for (int i = 0; i < table.getRows().get(0).getRowItems().size(); i++) {
                RItemMetaModel pItem = table.getRows().get(0).getRowItems().get(i);

                aooWriterBase.setCellValue(coordinate.getColumn() + i, coordinate.getRow(), (String) null);

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

                        if (aooWriterBase.isPercentageValueType(coordinate.getColumn() + i, totalTop ? coordinate.getRow() : (coordinate.getRow() - table.getRows().size() + 1))) {
                            aooWriterBase.setPercentageValueType(coordinate.getColumn() + i, coordinate.getRow());
                        }

                        aooWriterBase.setCellValue(coordinate.getColumn() + i, coordinate.getRow(), value);
                    }
                }
            }
        }
    }


    private void setCellValue(RItemMetaModel pItem, CellCoordinate coordinate) {
        String value = pItem.getValue();
        if ((pItem.getDataType() == MDTNodeDataTypes.NUMERIC || pItem.getNodeType() == MDTNodeTypes.VARIABLE.ordinal()) && value != null) {
            try {
                aooWriterBase.setCellValue(coordinate.getColumn(), coordinate.getRow(), Double.parseDouble(value));
            } catch (NumberFormatException ex) {
                aooWriterBase.setCellValue(coordinate.getColumn(), coordinate.getRow(), value);
            }
        } else if (pItem.getDataType() == MDTNodeDataTypes.DATE && value != null) {
            try {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(df.parse(value));
                TimeZone tz = calendar.getTimeZone();
                long d = calendar.getTimeInMillis();
                Date date = new Date(d + tz.getOffset(d));
                calendar.setTime(date);
                aooWriterBase.setCellValue(coordinate.getColumn(), coordinate.getRow(), calendar, false);
            } catch (ParseException e) {
                aooWriterBase.setCellValue(coordinate.getColumn(), coordinate.getRow(), value);
            }
        } else if (pItem.getDataType() == MDTNodeDataTypes.DATE_TIME && value != null) {
            try {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dateTimeFormat.parse(value));
                aooWriterBase.setCellValue(coordinate.getColumn(), coordinate.getRow(), calendar, true);
            } catch (ParseException e) {
                aooWriterBase.setCellValue(coordinate.getColumn(), coordinate.getRow(), value);
            }
        } else {
            aooWriterBase.setCellValue(coordinate.getColumn(), coordinate.getRow(), value);
        }
    }

}

class CellCoordinate {
    private int row, column;

    public CellCoordinate(int column, int row) {
        setColumn(column);
        setRow(row);
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public CellCoordinate offset(int columnOffset, int rowOffset) {
        return new CellCoordinate(getColumn() + columnOffset, getRow() + rowOffset);
    }
}