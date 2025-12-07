package net.fina.server.matrix.api;

import net.fina.common.client.exception.FinATypeException;
import net.fina.server.matrix.entity.SubMatrix;
import net.fina.server.matrix.entity.SubMatrixTable;
import net.fina.server.matrix.entity.SubMatrixTableMapping;
import net.fina.server.returns.entity.DefinitionTable;

import java.util.List;

public interface SubMatrixLocal {

    List<SubMatrix> loadSubMatrix(long mainMatrixId);

    SubMatrix save(SubMatrix entity, boolean importMode) throws FinATypeException;

    SubMatrix getSubMatrixById(long id);

    void delete(long id) throws FinATypeException;

    SubMatrix loadById(long id);

    SubMatrixTable saveMatrixTable(SubMatrixTable table) throws FinATypeException;

    List<SubMatrixTable> loadTables(long subMatrixId);

    List<SubMatrixTableMapping> loadTableMappings(long tableId);

    List<SubMatrixTableMapping> saveTableMappings(List<SubMatrixTableMapping> mappings) throws FinATypeException;

    void createTable(long returnDefinitionId, DefinitionTable definitionTable) throws FinATypeException;

    SubMatrixTable updateTable(long subMatrixId, SubMatrixTable table) throws FinATypeException;

    void deleteTable(long tableId) throws FinATypeException;

    void deleteTableMapping(long mappingId);

    void deleteTableMappings(List<Long> mappingIds);

    void deleteTableMappingsByTableId(long tableId);

    List<String> importSubMatrices(List<SubMatrix> subMatricesList);

    void deleteTableByDefinitionTableId(long definitionTableId);

    void moveNodeMappingItem(long itemId, boolean moveUp);

    List<SubMatrix> loadSubMatrixByReturnDefinitionId(long returnDefinitionId);
}
