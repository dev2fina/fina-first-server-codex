package net.fina.server.matrix.proxy;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import net.fina.common.client.exception.FinATypeException;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.matrix.api.SubMatrixLocal;
import net.fina.server.matrix.entity.SubMatrix;
import net.fina.server.matrix.entity.SubMatrixTable;
import net.fina.server.matrix.entity.SubMatrixTableMapping;
import net.fina.server.matrix.model.SubMatrixModel;
import net.fina.server.matrix.model.SubMatrixTableMappingModel;
import net.fina.server.matrix.model.SubMatrixTableModel;
import net.fina.server.matrix.model.helper.SubMatrixModelHelper;
import net.fina.server.matrix.model.helper.SubMatrixTableMappingModelHelper;
import net.fina.server.matrix.model.helper.SubMatrixTableModelHelper;
import org.jboss.ejb3.annotation.SecurityDomain;

import java.util.List;

@Stateless
@SecurityDomain("FinASecurityDomain")
public class SubMatrixProxySession {

    @Inject
    private SubMatrixLocal subMatrixLocal;

    public List<SubMatrixModel> loadSubMatrices(long mainMatrixId) {
        return SubMatrixModelHelper.toList(subMatrixLocal.loadSubMatrix(mainMatrixId));
    }

    public SubMatrixModel save(SubMatrixModel model) throws FinATypeException {
        SubMatrix result = subMatrixLocal.save(SubMatrixModelHelper.toEntity(model), false);

        return SubMatrixModelHelper.toModel(result);
    }

    public void delete(long id) throws FinATypeException {
        subMatrixLocal.delete(id);
    }

    public List<SubMatrixTableModel> loadMatrixTables(long subMatrixId) {
        List<SubMatrixTable> tables = subMatrixLocal.loadTables(subMatrixId);

        return SubMatrixTableModelHelper.toList(tables);
    }

    public SubMatrixTableModel updateTable(long subMatrixId, SubMatrixTableModel table) throws FinATypeException {
        SubMatrixTable result = subMatrixLocal.updateTable(subMatrixId, SubMatrixTableModelHelper.toEntity(table));
        return SubMatrixTableModelHelper.toModel(result);
    }

    public void deleteTable(long tableId) throws FinATypeException {
        subMatrixLocal.deleteTable(tableId);
    }

    public List<SubMatrixTableMappingModel> loadTableMappings(long tableId) {
        long langId = ThreadLocalHolder.getLanguage().getId();
        return SubMatrixTableMappingModelHelper.toModels(subMatrixLocal.loadTableMappings(tableId), langId);
    }

    public List<SubMatrixTableMappingModel> saveTableMappings(long tableId, List<SubMatrixTableMappingModel> models) throws FinATypeException {
        List<SubMatrixTableMapping> result = subMatrixLocal.saveTableMappings(SubMatrixTableMappingModelHelper.toEntities(models, tableId));

        return SubMatrixTableMappingModelHelper.toModels(result, ThreadLocalHolder.getLanguage().getId());
    }

    public void deleteTableMapping(long mappingId) {
        subMatrixLocal.deleteTableMapping(mappingId);
    }

    public void deleteTableMappings(List<Long> mappingIds) {
        subMatrixLocal.deleteTableMappings(mappingIds);
    }

    public void moveNodeMappingItem(long itemId, boolean moveUp){
        subMatrixLocal.moveNodeMappingItem(itemId, moveUp);
    }

}
