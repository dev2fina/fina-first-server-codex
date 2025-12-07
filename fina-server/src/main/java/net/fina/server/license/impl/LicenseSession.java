package net.fina.server.license.impl;

import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import net.fina.common.client.exception.FinATypeException;
import net.fina.server.license.api.LicenseLocal;
import net.fina.server.license.entity.*;

import java.util.List;

@Local(LicenseLocal.class)
@Stateless
public class LicenseSession implements LicenseLocal {
    @Inject
    private EntityManager em;

    @Override
    public List<LicenceType> loadLicenseTypes() {
        Query query = em.createNamedQuery("loadlicensetype", LicenceType.class);
        return query.getResultList();
    }

    @Override
    public LicenceType save(LicenceType licenseType) throws FinATypeException {
        if (licenseType.getCode() == null || licenseType.getCode().isEmpty()) {
            throw new FinATypeException(FinATypeException.Type.CODE_NULL);
        }
        if (!isCodeUnique(licenseType.getCode(), licenseType.getId())) {
            throw new FinATypeException(FinATypeException.Type.CODE_UNIQUE);
        }
        if (licenseType.getId() > 0) {

            LicenceType existing = em.find(LicenceType.class, licenseType.getId());
            licenseType.setVersion(existing.getVersion());
            licenseType.getOperations().forEach(oi -> oi.setLicenceType(existing));
            for (BankingOperation bo : existing.getOperations()) {
                if (!licenseType.getOperations().contains(bo)) {
                    em.remove(bo);
                }
            }

            licenseType = em.merge(licenseType);
        } else {
            em.persist(licenseType);
        }
        return licenseType;
    }

    @Override
    public void deleteLicenceType(long licenseTypeId) throws FinATypeException {
        List<Long> bankIds = em.createQuery("select l.fi.id from IN_LICENCES l where l.licenseType.id=:id", Long.class)
                .setParameter("id", licenseTypeId).getResultList();
        if (!bankIds.isEmpty()) {
            throw new FinATypeException(FinATypeException.Type.DEPENDENCY_ERROR);
        }
        LicenceType licenseType = em.find(LicenceType.class, licenseTypeId);
        if (licenseType != null) {
            em.createQuery("delete from IN_BANKING_OPERATIONS where licenceType.id = :typeId").setParameter("typeId", licenseTypeId).executeUpdate();
            em.remove(licenseType);
        }
    }

    @Override
    public LicenceType loadLicenseType(long id) {
        return em.find(LicenceType.class, id);
    }

    @Override
    public List<BankingOperation> loadBankingOperation(long licenseTypeId) {
        return em.createQuery("select o from IN_BANKING_OPERATIONS o where o.licenceType.id=:licensTypeId and o.parentId=0", BankingOperation.class).setParameter("licensTypeId", licenseTypeId).getResultList();
    }

    @Override
    public List<BankingOperation> loadByParentBankingOperation(long licenseTypeId, long parentId) {
        return em.createQuery("select o from IN_BANKING_OPERATIONS o where o.licenceType.id=:licensTypeId and o.parentId=:parentId", BankingOperation.class).setParameter("licensTypeId", licenseTypeId).setParameter("parentId", parentId).getResultList();
    }

    @Override
    public BankingOperation createBankingOperation(BankingOperation operation) throws FinATypeException {
        if (operation.getLicenceType() == null)
            throw new FinATypeException(FinATypeException.Type.INVALID_VALUE, "License Type is required");

        LicenceType licenceType = loadLicenseType(operation.getLicenceType().getId());
        if (licenceType == null)
            throw new FinATypeException(FinATypeException.Type.INVALID_VALUE, "License Type is required");
        operation.setLicenceType(licenceType);

        validateBankingOperationCode(operation.getId(), operation.getCode(), licenceType.getId());

        em.persist(operation);

        return operation;
    }

    @Override
    public BankingOperation updateBankingOperation(BankingOperation operation) throws FinATypeException {
        if (operation.getLicenceType() == null)
            throw new FinATypeException(FinATypeException.Type.INVALID_VALUE, "License Type is required");

        LicenceType licenceType = loadLicenseType(operation.getLicenceType().getId());
        if (licenceType == null)
            throw new FinATypeException(FinATypeException.Type.INVALID_VALUE, "License Type is required");

        operation.setLicenceType(licenceType);

        validateBankingOperationCode(operation.getId(), operation.getCode(), licenceType.getId());

        return em.merge(operation);
    }

    private void validateBankingOperationCode(long operationId, String code, long licenseTypeId) throws FinATypeException {
        List<BankingOperation> existing = em.createQuery("select bo from IN_BANKING_OPERATIONS bo where bo.id<>:id and lower(bo.code)=:code and bo.licenceType.id=:licenseTypeId", BankingOperation.class)
                .setParameter("id", operationId)
                .setParameter("code", code.trim().toLowerCase())
                .setParameter("licenseTypeId", licenseTypeId)
                .getResultList();

        if (!existing.isEmpty()) {
            throw new FinATypeException(FinATypeException.Type.CODE_UNIQUE, "Banking Operation Code is not Unique");
        }
    }

    @Override
    public void deleteBankingOperation(long operationId) throws FinATypeException {
        boolean hasDependency = !em.createQuery("select lop from IN_LICENSE_BANKING_OPERATIONS lop where lop.bankingOperation.id=:operationId").setParameter("operationId", operationId).getResultList().isEmpty();

        BankingOperation bo = em.find(BankingOperation.class, operationId);
        boolean hasChildOperations = false;
        if (bo.getParentId() == 0) {
            hasChildOperations = !em.createQuery("select bo from IN_BANKING_OPERATIONS bo where bo.parentId=:parentId").setParameter("parentId", operationId).getResultList().isEmpty();
        }

        if (hasDependency || hasChildOperations) {
            throw new FinATypeException(FinATypeException.Type.DEPENDENCY_ERROR);
        }

        em.remove(bo);
    }


    @Override
    public LicenceType getLicenseTypeById(long typeId) {
        return em.find(LicenceType.class, typeId);
    }

    @Override
    public LicenseComment saveLicenseComment(long licenseId, LicenseComment comment) {

        if (comment.getId() > 0) {
            comment = em.merge(comment);
        } else {
            Licence licence = em.find(Licence.class, licenseId);
            em.persist(comment);

            licence.getComments().add(comment);
            em.merge(licence);
        }

        return comment;
    }

    @Override
    public void deleteLicenseComment(long commentId) {
        em.remove(em.find(LicenseComment.class, commentId));
    }

    @Override
    public BankingOperationComment saveBankingOperationComment(long bankingOperationId, BankingOperationComment comment) {
        if (comment.getId() > 0) {
            comment = em.merge(comment);
        } else {
            LicenseBankingOperation bankingOperation = em.find(LicenseBankingOperation.class, bankingOperationId);
            em.persist(comment);

            bankingOperation.getComments().add(comment);
            em.merge(bankingOperation);
        }

        return comment;
    }

    @Override
    public void deleteBankingOperationComment(long commentId) {
        em.remove(em.find(BankingOperationComment.class, commentId));
    }

    public boolean isCodeUnique(String code, long id) {
        Query codeUniqueQuery = em.createQuery("select c.id from IN_LICENCE_TYPES c where trim(c.code)=:code and c.id<>:id");
        codeUniqueQuery.setParameter("id", id);
        codeUniqueQuery.setParameter("code", code);
        return codeUniqueQuery.getResultList().isEmpty();

    }
}
