package net.fina.server.matrix.model;

import net.fina.common.client.returns.ReturnDefinitionModel;
import net.fina.server.matrix.entity.Matrix;
import net.fina.server.matrix.entity.MatrixTableType;

public class SubMatrixModel {
    private long id;
    private ReturnDefinitionModel returnDefinition;
    private MatrixTableType matrixTableType;
    private boolean isProtected;
    private MatrixModel mainMatrix;
    private String sheetName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ReturnDefinitionModel getReturnDefinition() {
        return returnDefinition;
    }

    public void setReturnDefinition(ReturnDefinitionModel returnDefinition) {
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

    public MatrixModel getMainMatrix() {
        return mainMatrix;
    }

    public void setMainMatrix(MatrixModel mainMatrix) {
        this.mainMatrix = mainMatrix;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }
}
