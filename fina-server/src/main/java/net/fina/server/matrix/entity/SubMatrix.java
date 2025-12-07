package net.fina.server.matrix.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import net.fina.server.returns.entity.ReturnDefinition;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "SYS_SUB_MATRIX")
@Table(name = "SYS_SUB_MATRIX")
public class SubMatrix {
    @Id
    @SequenceGenerator(name = "sub_matrix_sequence", sequenceName = "sub_matrix_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sub_matrix_sequence")
    private long id;

    @OneToOne
    @JoinColumn(name = "RETURN_ID")
    @NotNull(message = "Return Definition Should Not Be Null")
    private ReturnDefinition returnDefinition;

    @Enumerated(EnumType.STRING)
    @Column(name = "TABLE_TYPE")
    @NotNull(message = "Table Type Should Not Be Null")
    private MatrixTableType matrixTableType;

    @Column(name = "IS_PROTECTED")
    private boolean isProtected;

    @ManyToOne
    @JoinColumn(name = "MAIN_MATRIX_ID", referencedColumnName = "ID")
    @NotNull(message = "mainMatrix Should Not Be Null")
    private Matrix mainMatrix;

    @OneToMany(mappedBy = "subMatrix")
    private List<SubMatrixTable> tables = new ArrayList<>();

    @Column(name = "SHEET_NAME", unique = true, nullable = false)
    @NotNull(message = "SHEET_NAME Must Not be Null")
    private String sheetName;

    public SubMatrix() {
    }

    public SubMatrix(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ReturnDefinition getReturnDefinition() {
        return returnDefinition;
    }

    public void setReturnDefinition(ReturnDefinition returnDefinition) {
        this.returnDefinition = returnDefinition;
    }

    public MatrixTableType getMatrixTableType() {
        return matrixTableType;
    }

    public void setMatrixTableType(MatrixTableType matrixTableType) {
        this.matrixTableType = matrixTableType;
    }

    public boolean isProtected() {
        return isProtected;
    }

    public void setProtected(boolean aProtected) {
        isProtected = aProtected;
    }

    public Matrix getMainMatrix() {
        return mainMatrix;
    }

    public void setMainMatrix(Matrix mainMatrix) {
        this.mainMatrix = mainMatrix;
    }

    public List<SubMatrixTable> getTables() {
        return tables == null ? new ArrayList<>() : tables;
    }

    public void setTables(List<SubMatrixTable> tables) {
        this.tables = tables;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }
}
