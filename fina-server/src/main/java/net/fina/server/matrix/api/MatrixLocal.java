package net.fina.server.matrix.api;

import net.fina.common.client.exception.FinATypeException;
import net.fina.server.matrix.entity.Matrix;

import java.util.List;

public interface MatrixLocal {

    List<Matrix> load();

    Matrix loadMatrixById(long id);

    List<String> importMatrices(List<Matrix> mainMatrices) throws FinATypeException;

    Matrix save(Matrix matrix) throws FinATypeException;

    void delete(long id) throws FinATypeException;

    List<Matrix> loadMatrixByIds(List<Long> permittedMatrixIds);
}
