package net.fina.server.matrix.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import net.fina.server.returns.entity.DefinitionTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity(name = "SYS_SUB_MATRIX_TABLE")
@Table(name = "SYS_SUB_MATRIX_TABLE")
public class SubMatrixTable {
    @Id
    @SequenceGenerator(name = "sub_matrix_table_sequence", sequenceName = "sub_matrix_table_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sub_matrix_table_sequence")
    private long id;
    @ManyToOne
    @JoinColumn(name = "SUB_MATRIX_ID")
    @NotNull(message = "Matrix Should Not Be Null")
    private SubMatrix subMatrix;

    @OneToOne
    @JoinColumns({
            @JoinColumn(name = "DEFINITION_TABLE_ID", referencedColumnName = "ID"),
            @JoinColumn(name = "DEFINITIONID", referencedColumnName = "DEFINITIONID")
    })
    @NotNull(message = "Definition Table Should Not Be Null")
    private DefinitionTable definitionTable;
    @Column(name = "START_COLUMN")
    private String startColumn;

    @Column(name = "START_ROW")
    private int startRow;

    @Column(name = "OFFSET")
    private int offset;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "TABLE_ID")
    private List<TableEndCondition> vctTableEndConditions;

    @Column(name = "VCT_TABLE_HEADER")
    private String vctTableHeader;
    @Column(name = "VCT_TABLE_AFTER_HEADER_ROWS")
    private int afterHeaderRowAmount;

    @OneToMany(mappedBy = "subMatrixTable", fetch = FetchType.LAZY)
    private List<SubMatrixTableMapping> tableMappings;

    public SubMatrixTable() {
    }

    public SubMatrixTable(long id) {
        this.id = id;
    }

    public SubMatrixTable(DefinitionTable definitionTable) {
        this.definitionTable = definitionTable;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public SubMatrix getSubMatrix() {
        return subMatrix;
    }

    public void setSubMatrix(SubMatrix subMatrix) {
        this.subMatrix = subMatrix;
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

    public List<TableEndCondition> getVctTableEndConditions() {
        return vctTableEndConditions == null ? new ArrayList<>() : vctTableEndConditions;
    }

    public void setVctTableEndConditions(List<TableEndCondition> vctTableEndConditions) {
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

    public List<SubMatrixTableMapping> getTableMappings() {
        return tableMappings == null ? new ArrayList<>() : tableMappings;
    }

    public void setTableMappings(List<SubMatrixTableMapping> tableMappings) {
        this.tableMappings = tableMappings;
    }

    public DefinitionTable getDefinitionTable() {
        return definitionTable;
    }

    public void setDefinitionTable(DefinitionTable definitionTable) {
        this.definitionTable = definitionTable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubMatrixTable that = (SubMatrixTable) o;
        return getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
