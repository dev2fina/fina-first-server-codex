package net.fina.server.matrix.model;

import net.fina.server.matrix.entity.Matrix;
import net.fina.server.matrix.entity.SubMatrix;

import java.util.List;

public class MatrixImportModel {

    private List<Matrix> mainMatrices;

    private List<SubMatrix> subMatrices;


    public MatrixImportModel(){

    }

    public List<Matrix> getMainMatrices() {
        return mainMatrices;
    }

    public void setMainMatrices(List<Matrix> mainMatrices) {
        this.mainMatrices = mainMatrices;
    }

    public List<SubMatrix> getSubMatrices() {
        return subMatrices;
    }

    public void setSubMatrices(List<SubMatrix> subMatrices) {
        this.subMatrices = subMatrices;
    }
}
