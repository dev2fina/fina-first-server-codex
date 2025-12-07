package net.fina.server.fi.impl;

import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import net.fina.common.client.exception.FinATypeException;
import net.fina.server.fi.api.FiBranchTypeLocal;
import net.fina.server.fi.api.FiLocal;
import net.fina.server.fi.entity.FiBranch;
import net.fina.server.fi.entity.FiBranchType;
import net.fina.server.fi.util.FiBranchAndManagementTypeValidator;
import net.fina.server.i18n.helper.Description;
import net.fina.server.interceptors.RecordingAuditor;

import java.util.List;
import java.util.Set;

@Stateless
@Local(FiBranchTypeLocal.class)
@Interceptors(RecordingAuditor.class)
public class FiBranchTypeSession implements FiBranchTypeLocal {

    @Inject
    private EntityManager em;

    @Inject
    private FiLocal fiLocal;

    @Override
    public List<FiBranchType> load() {
        return em.createQuery("select bbt from IN_BANK_BRANCH_TYPES bbt", FiBranchType.class).getResultList();
    }

    @Override
    public List<FiBranchType> loadWithCount(long fiId, boolean includeAll) {
        List<FiBranchType> result = em.createQuery("select new net.fina.server.fi.entity.FiBranchType(bbt.id, bbt.code, bbt.name, bbt.jsonConfig," +
                        " (select count(b.fiBranchType.id) from IN_BANK_BRANCHES b where b.fiBranchType.id=bbt.id and b.bankId =:fiId and (b.deleted=false or b.deleted is null)) ) " +
                        "from IN_BANK_BRANCH_TYPES bbt", FiBranchType.class)
                .setParameter("fiId", fiId)
                .getResultList();

        if (includeAll) {
            long count = em.createQuery("select count(b.id) from IN_BANK_BRANCHES b where b.bankId =:fiId and (b.deleted=false or b.deleted is null)", Long.class)
                    .setParameter("fiId", fiId)
                    .getSingleResult();
            result.add(0, new FiBranchType(-1, "all", new Description(), null, count));
        }

        return result;
    }

    @Override
    public FiBranchType get(long id) {
        return em.find(FiBranchType.class, id);
    }

    @Override
    public FiBranchType save(FiBranchType fiBranchType) throws FinATypeException {

        FiBranchAndManagementTypeValidator.validateFields(fiBranchType.getJsonConfig(), Set.of("code", "regionModel"));

        if (!isCodeUnique(fiBranchType.getCode(), fiBranchType.getId())) {
            throw new FinATypeException(FinATypeException.Type.CODE_UNIQUE);
        }

        if (fiBranchType.getId() > 0) {
            fiBranchType = em.merge(fiBranchType);
        } else {
            em.persist(fiBranchType);
        }

        return fiBranchType;
    }


    @Override
    public void delete(long fiBranchTypeId) throws FinATypeException {
        List<FiBranch> fiBranches = fiLocal.loadFiBranchesByType(fiBranchTypeId);
        if (!fiBranches.isEmpty()) {
            throw new FinATypeException(FinATypeException.Type.DEPENDENCY_ERROR);
        }

        FiBranchType fiBranchType = em.find(FiBranchType.class, fiBranchTypeId);
        em.remove(fiBranchType);
    }

    private boolean isCodeUnique(String code, long id) {
        TypedQuery<Long> codeUniqueQuery = em.createQuery("select bbt.id from IN_BANK_BRANCH_TYPES bbt where trim(bbt.code)=:code and bbt.id <>:id", Long.class)
                .setParameter("id", id)
                .setParameter("code", code);
        List<Long> ids = codeUniqueQuery.getResultList();
        return ids.isEmpty();
    }


}
