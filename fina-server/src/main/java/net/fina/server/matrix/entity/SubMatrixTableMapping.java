package net.fina.server.matrix.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import net.fina.server.dcs.uploadfile.impl.reader.excel.util.NodeDataType;
import net.fina.server.mdt.entity.MDTNode;

import java.util.Objects;

@Entity(name = "SYS_SUB_MATRIX_TABLE_MAPPING")
@Table(name = "SYS_SUB_MATRIX_TABLE_MAPPING")
public class SubMatrixTableMapping {
    @Id
    @SequenceGenerator(name = "sub_matrix_table_mapping_sequence", sequenceName = "sub_matrix_table_mapping_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sub_matrix_table_mapping_sequence")
    private long id;

    @OneToOne
    @JoinColumn(name = "NODE_ID", unique = true, nullable = false)
    private MDTNode mdtNode;

    @Column(name = "CELL", unique = true, nullable = false)
    @NotNull(message = "Cell is required")
    private String cell;

    @Enumerated(EnumType.STRING)
    @Column(name = "DATA_TYPE")
    private NodeDataType dataType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUB_MATRIX_TABLE_ID")
    private SubMatrixTable subMatrixTable;


    @Column(name = "SEQUENCE")
    private long sequence;

    @Column(name = "PRECISION")
    private Integer precision;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public MDTNode getMdtNode() {
        return mdtNode;
    }

    public void setMdtNode(MDTNode mdtNode) {
        this.mdtNode = mdtNode;
    }

    public String getCell() {
        return cell;
    }

    public void setCell(String cell) {
        this.cell = cell;
    }

    public NodeDataType getDataType() {
        return dataType;
    }

    public void setDataType(NodeDataType dataType) {
        this.dataType = dataType;
    }

    public SubMatrixTable getSubMatrixTable() {
        return subMatrixTable;
    }

    public void setSubMatrixTable(SubMatrixTable subMatrixTable) {
        this.subMatrixTable = subMatrixTable;
    }


    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubMatrixTableMapping that = (SubMatrixTableMapping) o;
        return Objects.equals(getMdtNode(), that.getMdtNode()) && Objects.equals(getCell(), that.getCell()) && Objects.equals(getSubMatrixTable(), that.getSubMatrixTable());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMdtNode(), getCell(), getSubMatrixTable());
    }

    public Integer getPrecision() {
        return precision;
    }

    public void setPrecision(Integer precision) {
        this.precision = precision;
    }
}
