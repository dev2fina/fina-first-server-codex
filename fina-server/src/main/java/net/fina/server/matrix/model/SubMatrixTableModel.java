package net.fina.server.matrix.model;

import net.fina.common.client.returns.DefinitionTableModel;

import java.util.List;

public class SubMatrixTableModel {
    private long id;
    private String startColumn;
    private int startRow;
    private int offset;
    private List<TableEndConditionModel> vctTableEndConditions;
    private String vctTableHeader;
    private int afterHeaderRowAmount;

    private DefinitionTableModel definitionTable;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStartColumn() {
        return startColumn;
    }

    public void setStartColumn(String startColumn) {
        this.startColumn = startColumn;
    }

    public int getStartRow() {
        return startRow;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public List<TableEndConditionModel> getVctTableEndConditions() {
        return vctTableEndConditions;
    }

    public void setVctTableEndConditions(List<TableEndConditionModel> vctTableEndConditions) {
        this.vctTableEndConditions = vctTableEndConditions;
    }

    public String getVctTableHeader() {
        return vctTableHeader;
    }

    public void setVctTableHeader(String vctTableHeader) {
        this.vctTableHeader = vctTableHeader;
    }

    public int getAfterHeaderRowAmount() {
        return afterHeaderRowAmount;
    }

    public void setAfterHeaderRowAmount(int afterHeaderRowAmount) {
        this.afterHeaderRowAmount = afterHeaderRowAmount;
    }

    public DefinitionTableModel getDefinitionTable() {
        return definitionTable;
    }

    public void setDefinitionTable(DefinitionTableModel definitionTable) {
        this.definitionTable = definitionTable;
    }
}
