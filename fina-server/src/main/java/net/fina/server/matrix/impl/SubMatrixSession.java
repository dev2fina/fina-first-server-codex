package net.fina.server.matrix.impl;


import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Local;
import jakarta.ejb.SessionContext;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.returns.ReturnTableType;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.matrix.api.SubMatrixLocal;
import net.fina.server.matrix.entity.*;
import net.fina.server.mdt.api.MDTNodeLocal;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.returns.entity.DefinitionTable;
import net.fina.server.returns.entity.ReturnDefinition;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Stateless
@Local(SubMatrixLocal.class)
@Interceptors(RecordingAuditor.class)
@SecurityDomain("FinASecurityDomain")
@RolesAllowed(PermissionIdNames.MENU_MATRIX)
@Transactional(rollbackOn = FinATypeException.class)
public class SubMatrixSession implements SubMatrixLocal {

    private final Logger log = Logger.getLogger(getClass());

    @Inject
    private MDTNodeLocal mdtNodeLocal;
    @Inject
    private EntityManager em;

    @Resource
    private SessionContext sessionContext;

    @Override
    public List<SubMatrix> loadSubMatrix(long mainMatrixId) {
        return em.createQuery("select sm from SYS_SUB_MATRIX sm where sm.mainMatrix.id=:mainMatrixId", SubMatrix.class)
                .setParameter("mainMatrixId", mainMatrixId)
                .getResultList();
    }

    @Override
    public SubMatrix save(SubMatrix entity, boolean importMode) throws FinATypeException {
        validateSubMatrixFields(entity, false);

        entity.setReturnDefinition(em.find(ReturnDefinition.class, entity.getReturnDefinition().getId()));

        if (entity.getReturnDefinition() == null) {
            throw new FinATypeException("Return definition is required, Main Matrix : " + entity.getMainMatrix().getPattern());
        }

        entity.setMainMatrix(em.find(Matrix.class, entity.getMainMatrix().getId()));
        if (checkUniqueSheetName(entity)) {
            throw new FinATypeException("Sheet Name [" + entity.getSheetName() + "] is not unique");
        }
        if (entity.getId() > 0) {
            SubMatrix existing = getSubMatrixById(entity.getId());
            //throw exception if return definition changed on already saved instance
            if (!existing.getReturnDefinition().equals(entity.getReturnDefinition())) {
                throw new FinATypeException("Return Definition Change is not allowed...");
            }
            entity = em.merge(entity);
        } else {
            em.persist(entity);
            if (!importMode) {
                createSubMatrixTables(entity);
            }
        }
        return entity;
    }

    @Override
    public SubMatrix getSubMatrixById(long id) {
        return em.find(SubMatrix.class, id);
    }

    @Override
    public void delete(long id) throws FinATypeException {
        SubMatrix subMatrix = em.find(SubMatrix.class, id);

        if (!checkSubMatrixTableMappings(id)) {
            throw new FinATypeException(FinATypeException.Type.DEPENDENCY_ERROR);
        }

        em.createNativeQuery("delete from SYS_MATRIX_TABLE_CONDITION where SYS_MATRIX_TABLE_CONDITION.TABLE_ID in (select id from SYS_SUB_MATRIX_TABLE where SUB_MATRIX_ID=:subMatrixId )")
                .setParameter("subMatrixId", id)
                .executeUpdate();

        em.createQuery("delete from SYS_SUB_MATRIX_TABLE where subMatrix.id=:subMatrixId")
                .setParameter("subMatrixId", id)
                .executeUpdate();
        em.remove(subMatrix);
    }

    @Override
    public SubMatrix loadById(long id) {
        return em.find(SubMatrix.class, id);
    }

    @Override
    public SubMatrixTable saveMatrixTable(SubMatrixTable table) throws FinATypeException {
        if (table.getId() > 0) {
            table = em.merge(table);
        } else {
            em.persist(table);
        }
        return table;
    }

    @Override
    public List<SubMatrixTable> loadTables(long subMatrixId) {
        return em.createQuery("SELECT t from SYS_SUB_MATRIX_TABLE t where t.subMatrix.id=:subMatrixId", SubMatrixTable.class)
                .setParameter("subMatrixId", subMatrixId)
                .getResultList();
    }

    @Override
    public List<SubMatrixTableMapping> loadTableMappings(long tableId) {
        return em.createQuery("select tm from SYS_SUB_MATRIX_TABLE_MAPPING tm where tm.subMatrixTable.id=:tableId order by tm.sequence", SubMatrixTableMapping.class)
                .setParameter("tableId", tableId)
                .getResultList();
    }

    @Override
    public List<SubMatrixTableMapping> saveTableMappings(List<SubMatrixTableMapping> mappings) throws FinATypeException {
        if (mappings == null || mappings.isEmpty()) {
            return mappings;
        }
        SubMatrixTable table = em.find(SubMatrixTable.class, mappings.get(0).getSubMatrixTable().getId());

        List<String> warnings = new ArrayList<>();

        for (SubMatrixTableMapping m : mappings) {
            MDTNode mdtNode = mdtNodeLocal.findByCode(m.getMdtNode().getCode());
            if (mdtNode == null) {
                throw new FinATypeException(String.format("Invalid Mdt Node Code [%s]", m.getMdtNode().getCode()));
            }
            if (!isValidExcelCell(m.getCell())) {
                throw new FinATypeException("Invalid cell");
            }
            m.setMdtNode(mdtNode);
            m.setSubMatrixTable(table);
            boolean ignore = checkUniqueMapping(m, warnings);
            if (ignore) {
                continue;
            }

            if (m.getId() > 0) {
                em.merge(m);
            } else {
                m.setSequence(getNextSequenceByTable(m.getSubMatrixTable().getId()));
                em.persist(m);
            }
        }

        if (!warnings.isEmpty()) {
            throw new FinATypeException(FinATypeException.Type.CODE_UNIQUE, warnings.toArray(new String[0]));
        }

        return mappings;
    }

    @Override
    @PermitAll
    public void createTable(long returnDefinitionId, DefinitionTable definitionTable) {
        List<SubMatrix> subMatrixList = loadSubMatrixByReturnDefinitionId(returnDefinitionId);
        for (SubMatrix subMatrix : subMatrixList) {
            SubMatrixTable table = new SubMatrixTable();
            table.setId(0);
            table.setSubMatrix(subMatrix);
            table.setDefinitionTable(definitionTable);
            em.persist(table);
        }
    }

    @Override
    public SubMatrixTable updateTable(long subMatrixId, SubMatrixTable table) throws FinATypeException {
        if (table.getId() <= 0) {
            throw new FinATypeException(FinATypeException.Type.INVALID_VALUE);
        }
        SubMatrixTable exitsing = em.find(SubMatrixTable.class, table.getId());
        table.setSubMatrix(em.find(SubMatrix.class, subMatrixId));
        table.setDefinitionTable(exitsing.getDefinitionTable());
        em.merge(table);
        return table;
    }

    @Override
    public void deleteTable(long tableId) throws FinATypeException {
        SubMatrixTable table = em.find(SubMatrixTable.class, tableId);
        if (!table.getTableMappings().isEmpty()) {
            throw new FinATypeException(FinATypeException.Type.DEPENDENCY_ERROR);
        }
        em.remove(table);
    }

    @Override
    public void deleteTableMapping(long mappingId) {
        em.remove(em.find(SubMatrixTableMapping.class, mappingId));
    }

    @Override
    public void deleteTableMappings(List<Long> mappingIds) {
        em.createQuery("delete from SYS_SUB_MATRIX_TABLE_MAPPING where id in (:ids)")
                .setParameter("ids", mappingIds)
                .executeUpdate();
    }

    @Override
    public void deleteTableMappingsByTableId(long tableId) {
        em.createQuery("delete from SYS_SUB_MATRIX_TABLE_MAPPING m where m.subMatrixTable.id=:tableId")
                .setParameter("tableId", tableId)
                .executeUpdate();
    }

    @Override
    public List<String> importSubMatrices(List<SubMatrix> subMatricesList) {
        List<String> errorMessages = new ArrayList<>();
        if (subMatricesList == null || subMatricesList.isEmpty()) {
            return new ArrayList<>();
        }

        for (SubMatrix subMatrix : subMatricesList) {
            try {
                validateSubMatrixFields(subMatrix, true);
                if (subMatrix.getMainMatrix().getId() <= 0) {
                    errorMessages.add("Invalid sub matrix, Return definition: " + subMatrix.getReturnDefinition().getCode());
                    continue;
                }

                save(subMatrix, true);

                for (SubMatrixTable table : subMatrix.getTables()) {
                    saveMatrixTable(table);
                    saveTableMappings(table.getTableMappings());
                }


            } catch (FinATypeException e) {
                log.error(e.getMessage(), e);
                if (e.getMessage() == null) {
                    errorMessages.addAll(Arrays.asList(e.getParams()));
                } else {
                    errorMessages.add(e.getMessage());
                }
            }

        }

        if (!errorMessages.isEmpty()) {
            sessionContext.setRollbackOnly();
        }
        return errorMessages;
    }

    @Override
    public void deleteTableByDefinitionTableId(long definitionTableId) {
        List<SubMatrixTable> submatrixTables = em.createQuery("select t from SYS_SUB_MATRIX_TABLE t where t.definitionTable.id=:defTableId", SubMatrixTable.class)
                .setParameter("defTableId", definitionTableId)
                .getResultList();
        for (SubMatrixTable table : submatrixTables) {
            deleteTableMappingsByTableId(table.getId());
            em.remove(table);
        }
    }


    @Override
    public void moveNodeMappingItem(long itemId, boolean moveUp) {
        SubMatrixTableMapping item = em.find(SubMatrixTableMapping.class, itemId);

        if (item != null) {
            moveNodeMappingItem(item, moveUp);
        }
    }

    @Override
    public List<SubMatrix> loadSubMatrixByReturnDefinitionId(long returnDefinitionId) {
        return em.createQuery("select sm from SYS_SUB_MATRIX sm where sm.returnDefinition.id=:definitionId", SubMatrix.class)
                .setParameter("definitionId", returnDefinitionId)
                .getResultList();
    }


    private void validateSubMatrixFields(SubMatrix subMatrix, boolean importMode) throws FinATypeException {
        if (subMatrix.getReturnDefinition() == null) {
            throw new FinATypeException("Return definition is required, Main Matrix: " + subMatrix.getMainMatrix().getPattern());
        }

        if (subMatrix.getMatrixTableType() == null) {
            throw new FinATypeException("Sub matrix table type is invalid, Main Matrix: " + subMatrix.getMainMatrix().getPattern());
        }

        if (importMode) {
            if (subMatrix.getTables() != null && !subMatrix.getTables().isEmpty()) {
                validateSubMatrixTable(subMatrix.getTables(), subMatrix.getMatrixTableType());
            } else {
                throw new FinATypeException("Sub matrix has invalid tables, Main Matrix: " + subMatrix.getMainMatrix().getPattern());
            }
        }
    }

    private void validateSubMatrixTable(List<SubMatrixTable> tables, MatrixTableType tableType) throws FinATypeException {
        for (int i = 0; i < tables.size(); i++) {
            SubMatrixTable table = tables.get(i);
            boolean isFirstRow = i == 0;

            validateTable(table, tableType, isFirstRow);
            validateTableMappings(table);
        }
    }

    private void validateTable(SubMatrixTable table, MatrixTableType tableType, boolean isFirstRow) throws FinATypeException {
        switch (tableType) {
            case VCT -> validateVctTable(table);
            case MULTIVCT -> validateMultiVctTable(table, isFirstRow);
            case MIXED -> validateMixedTable(table, isFirstRow);
        }
    }

    private void validateVctTable(SubMatrixTable table) throws FinATypeException {
        validateEndConditions(table);
    }

    private void validateMultiVctTable(SubMatrixTable table, boolean isFirstRow) throws FinATypeException {
        if (isFirstRow) {
            validateStartRow(table);
        } else {
            validateVctTableHeader(table);
        }
        validateEndConditions(table);
    }

    private void validateMixedTable(SubMatrixTable table, boolean isFirstRow) throws FinATypeException {
        validateMixedTableType(table);

        if (table.getDefinitionTable().getType() == ReturnTableType.VCT && isFirstRow) {
            validateStartRowAndColumn(table);
            validateEndConditions(table);
            validateVctTableHeader(table);
        }
    }

    private void validateStartRow(SubMatrixTable table) throws FinATypeException {
        if (table.getStartRow() <= 0) {
            throwValidationException("Invalid start row", table);
        }
    }

    private void validateStartRowAndColumn(SubMatrixTable table) throws FinATypeException {
        boolean isValid = table.getStartRow() > 0 &&
                table.getStartColumn() != null &&
                !table.getStartColumn().isBlank();

        if (!isValid) {
            throwValidationException("Invalid start row or start column", table);
        }
    }

    private void validateEndConditions(SubMatrixTable table) throws FinATypeException {
        if (table.getVctTableEndConditions() == null || table.getVctTableEndConditions().isEmpty()) {
            throwValidationException("Missing end conditions", table);
        }
    }

    private void validateVctTableHeader(SubMatrixTable table) throws FinATypeException {
        if (table.getVctTableHeader() == null || table.getVctTableHeader().isBlank()) {
            throwValidationException("Invalid VCT table header", table);
        }
    }

    private void validateMixedTableType(SubMatrixTable table) throws FinATypeException {
        if (table.getDefinitionTable().getType() == null) {
            throwValidationException("Invalid mixed table type", table);
        }
    }

    private void validateTableMappings(SubMatrixTable table) throws FinATypeException {
        for (SubMatrixTableMapping mapping : table.getTableMappings()) {
            if (mapping.getMdtNode() == null) {
                throwValidationException("Invalid MDT Node type", table);
            }
        }
    }

    private void throwValidationException(String message, SubMatrixTable table) throws FinATypeException {
        String code = table.getSubMatrix().getReturnDefinition().getCode();
        throw new FinATypeException(message + ". Sub matrix: " + code);
    }

    private boolean checkUniqueSheetName(SubMatrix subMatrix) {
        Long count = em.createQuery("select count(sm.id) from SYS_SUB_MATRIX sm where sm.id<>:id and sm.sheetName=:sheetName and sm.mainMatrix.id=:matrixId", Long.class)
                .setParameter("id", subMatrix.getId())
                .setParameter("sheetName", subMatrix.getSheetName())
                .setParameter("matrixId", subMatrix.getMainMatrix().getId())
                .getSingleResult();

        return count != 0;
    }

    private boolean checkUniqueMapping(SubMatrixTableMapping mapping, List<String> warnings) {
        Long count = em.createQuery("select count(m.id) from SYS_SUB_MATRIX_TABLE_MAPPING m where m.id<>:id and m.cell=:cell and m.mdtNode.code=:nodeCode and m.subMatrixTable.id=:tableId", Long.class)
                .setParameter("id", mapping.getId())
                .setParameter("cell", mapping.getCell())
                .setParameter("nodeCode", mapping.getMdtNode().getCode())
                .setParameter("tableId", mapping.getSubMatrixTable().getId())
                .getSingleResult();
        if (count > 0) {
            String s = "Following Mapping Already Exists - Cell [%s], node [%s]";
            warnings.add(String.format(s, mapping.getCell(), mapping.getMdtNode().getCode()));
        }
        return count > 0;
    }

    private boolean checkSubMatrixTableMappings(long id) {
        return em.createQuery("select count (tm.id) from SYS_SUB_MATRIX_TABLE_MAPPING tm where tm.subMatrixTable.subMatrix.id=:subMatrixId", Long.class)
                .setParameter("subMatrixId", id)
                .getSingleResult() == 0;
    }

    private void createSubMatrixTables(SubMatrix entity) throws FinATypeException {
        //save return definition mapping Tables
        for (DefinitionTable defTable : entity.getReturnDefinition().getDefinitionTables()) {
            SubMatrixTable table = new SubMatrixTable();
            table.setSubMatrix(entity);
            table.setDefinitionTable(defTable);
            saveMatrixTable(table);
        }
    }


    private void moveNodeMappingItem(SubMatrixTableMapping m, boolean moveUp) {
        List<SubMatrixTableMapping> mappings = m.getSubMatrixTable().getTableMappings();
        sortNodeMappingItems(mappings);

        int targetItemIndex = moveUp ? mappings.indexOf(m) - 1 : mappings.indexOf(m) + 1;
        if (targetItemIndex < 0 || targetItemIndex >= mappings.size()) {
            return;
        }
        long currentSequence = m.getSequence();
        SubMatrixTableMapping toSwap = mappings.get(targetItemIndex);
        //swap sequences
        m.setSequence(toSwap.getSequence());
        toSwap.setSequence(currentSequence);

    }

    private void sortNodeMappingItems(List<SubMatrixTableMapping> tableMappings) {
        Comparator<SubMatrixTableMapping> comparator = Comparator.comparingLong(SubMatrixTableMapping::getSequence);
        tableMappings.sort(comparator);
    }

    private long getNextSequenceByTable(long id) {
        return em.createQuery("select coalesce(max(m.sequence + 1), 0) from SYS_SUB_MATRIX_TABLE_MAPPING m where m.subMatrixTable.id=:tableId", Long.class)
                .setParameter("tableId", id)
                .getSingleResult();

    }

    private boolean isValidExcelCell(String cell) {
        if (cell == null || cell.isEmpty()) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[A-Z]{1,3}([1-9][0-9]*)?$", Pattern.CASE_INSENSITIVE);

        Matcher matcher = pattern.matcher(cell.trim());
        return matcher.matches();
    }

}
