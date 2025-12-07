package net.fina.server.matrix.impl;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.client.exception.FinATypeException;
import net.fina.server.fi.entity.FiType;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.matrix.api.MatrixLocal;
import net.fina.server.matrix.entity.Matrix;
import net.fina.server.matrix.entity.Matrix_;
import net.fina.server.returns.entity.PeriodType;
import net.fina.server.returns.entity.ReturnVersion;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.logging.Logger;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@Stateless
@Interceptors(RecordingAuditor.class)
@Local(MatrixLocal.class)
@SecurityDomain("FinASecurityDomain")
@RolesAllowed(PermissionIdNames.MENU_MATRIX)
public class MatrixSession implements MatrixLocal {
    private final Logger log = Logger.getLogger(getClass());

    @Inject
    private EntityManager em;

    @Override
    public List<Matrix> load() {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Matrix> query = cb.createQuery(Matrix.class);
        Root<Matrix> root = query.from(Matrix.class);

        query.select(root);
        query.orderBy(cb.desc(root.get(Matrix_.ID)));

        TypedQuery<Matrix> loadQuery = em.createQuery(query);

        return loadQuery.getResultList();
    }


    @Override
    public Matrix loadMatrixById(long id) {
        return em.find(Matrix.class, id);
    }

    @Override
    public List<String> importMatrices(List<Matrix> mainMatrices) throws FinATypeException {
        List<String> warnings = new ArrayList<>();
        if (mainMatrices != null && !mainMatrices.isEmpty()) {
            for (int i = 0; i < mainMatrices.size(); i++) {
                Matrix matrix = mainMatrices.get(i);

                if (checkPatternNotUnique(matrix, false)) {
                    matrix.setId(getMatrixIdByPattern(matrix.getPattern()));
                    mainMatrices.set(i, matrix);

                    String message = MessageFormat.format("Matrix with pattern {0} already exists, skipping it!", matrix.getPattern());
                    warnings.add(message);
                    log.warn(message);
                } else if (checkRequiredFields(matrix)) {
                    String message = "Some of required fields value is invalid, skipping it!";
                    warnings.add(message);
                    log.warn(message);
                } else {
                    matrix = save(matrix);
                    mainMatrices.set(i, matrix);
                }
            }
        }

        return warnings;

    }

    @Override
    public Matrix save(Matrix matrix) throws FinATypeException {
        if (checkPatternNotUnique(matrix, true)) {
            throw new FinATypeException("Pattern is not unique!");
        }
        if (checkRequiredFields(matrix)) {
            throw new FinATypeException("Required fields are empty!");
        }

        Matrix existing = loadMatrixById(matrix.getId());
        matrix.setReturnVersion(em.find(ReturnVersion.class, matrix.getReturnVersion().getId()));
        matrix.setFiType(em.find(FiType.class, matrix.getFiType().getId()));

        matrix.setPeriodType(em.find(PeriodType.class, matrix.getPeriodType().getId()));
        if (existing != null) {
            return em.merge(matrix);
        } else {
            em.persist(matrix);
        }

        return matrix;
    }


    @Override
    public void delete(long id) throws FinATypeException {
        if (hasDependency(id)) {
            throw new FinATypeException(FinATypeException.Type.DEPENDENCY_ERROR);
        }
        em.createQuery("delete from SYS_MATRIX where id=:id").setParameter("id", id).executeUpdate();
    }

    @Override
    public List<Matrix> loadMatrixByIds(List<Long> permittedMatrixIds) {
        return em.createQuery("select m from SYS_MATRIX m where m.id in (:ids)", Matrix.class)
                .setParameter("ids", permittedMatrixIds)
                .getResultList();
    }

    private boolean checkPatternNotUnique(Matrix matrix, boolean withId) {
        TypedQuery<Boolean> query = em.createQuery("select m.id from SYS_MATRIX m where lower(trim(m.pattern))=:pattern " + (withId ? " and m.id <>:id" : ""), Boolean.class);

        if (withId) {
            query.setParameter("id", matrix.getId());
        }
        query.setParameter("pattern", matrix.getPattern().toLowerCase().trim());

        return !query.getResultList().isEmpty();
    }


    private boolean checkRequiredFields(Matrix matrix) {
        return matrix.getReturnVersion().getId() <= 0 || matrix.getPeriodType().getId() <= 0 || matrix.getFiType().getId() <= 0;
    }

    private long getMatrixIdByPattern(String pattern) {
        try {
            return em.createQuery("select m.id from SYS_MATRIX m where m.pattern=:pattern", Long.class)
                    .setParameter("pattern", pattern)
                    .getSingleResult();
        } catch (Exception e) {
            return -1;
        }
    }

    private boolean hasDependency(long matrixId) {
        List<Long> subMatrixIds = em.createQuery("select sm.id from SYS_SUB_MATRIX sm where sm.mainMatrix.id=:mainMatrixId", Long.class)
                .setParameter("mainMatrixId", matrixId)
                .getResultList();

        List<Long> uploadFileIds = em.createQuery("select uf.id from SYS_UPLOADEDFILE uf where uf.matrixId = :mainMatrixId", Long.class)
                .setParameter("mainMatrixId", matrixId)
                .getResultList();

        List<Long> userMatrixIds = em.createQuery("select u.id from SYS_USERS u inner join u.permittedMatrixList um where um.id = :mainMatrixId", Long.class)
                .setParameter("mainMatrixId", matrixId)
                .getResultList();

        return !subMatrixIds.isEmpty() || !uploadFileIds.isEmpty() || !userMatrixIds.isEmpty();
    }
}
