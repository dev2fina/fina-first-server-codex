package net.fina.server.fi.impl;

import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import net.fina.common.client.exception.FinATypeException;
import net.fina.server.fi.api.PeerGroupLocal;
import net.fina.server.fi.entity.Criterion;
import net.fina.server.fi.entity.PeerGroup;
import net.fina.server.interceptors.RecordingAuditor;

import java.util.ArrayList;
import java.util.List;

@Stateless
@Local(PeerGroupLocal.class)
@Interceptors(RecordingAuditor.class)
public class PeerGroupSession implements PeerGroupLocal {

    @Inject
    private EntityManager em;

    @Override
    public List<Criterion> loadNodes() {
        return em.createQuery("select c from IN_CRITERION c order by c.id", Criterion.class).getResultList();
    }

    @Override
    public List<PeerGroup> loadChildren(long parentId) {
        return em.createQuery("select c from IN_BANK_GROUPS c where c.parentId=:parentId", PeerGroup.class)
                .setParameter("parentId", parentId).
                getResultList();
    }


    @Override
    public PeerGroup savePeerGroup(PeerGroup peerGroup) throws FinATypeException {
        if (peerGroup == null) throw new FinATypeException(FinATypeException.Type.INVALID_VALUE);
        if (peerGroup.getCode() == null || peerGroup.getCode().isBlank()) throw new FinATypeException(FinATypeException.Type.INVALID_CODE);

        boolean codeUnique = em.createQuery("select c.id from IN_BANK_GROUPS c where lower(trim(c.code))=:code and c.id <>:id")
                .setParameter("code", peerGroup.getCode().trim().toLowerCase())
                .setParameter("id", peerGroup.getId())
                .getResultList().isEmpty();

        if (!codeUnique) {
            throw new FinATypeException(FinATypeException.Type.CODE_UNIQUE, "Group code is not unique");
        }
        if (peerGroup.getId() == 0) {
            em.persist(peerGroup);
        } else {
            int existingVersion = em.createQuery("select pg.version from IN_BANK_GROUPS pg where pg.id=:id", Integer.class)
                    .setParameter("id", peerGroup.getId())
                    .getSingleResult();
            peerGroup.setVersion(existingVersion);
            peerGroup = em.merge(peerGroup);
        }
        return peerGroup;

    }

    @Override
    public Criterion saveCriterion(Criterion criterion) throws FinATypeException {
        if (criterion == null) throw new FinATypeException(FinATypeException.Type.INVALID_VALUE);
        if (criterion.getCode() == null || criterion.getCode().isBlank()) throw new FinATypeException(FinATypeException.Type.INVALID_CODE);

        boolean codeUnique = em.createQuery("select c.id from IN_CRITERION c where lower(trim(c.code))=:code and c.id <>:id")
                .setParameter("code", criterion.getCode().trim().toLowerCase())
                .setParameter("id", criterion.getId())
                .getResultList().isEmpty();

        if (!codeUnique) {
            throw new FinATypeException(FinATypeException.Type.CODE_UNIQUE, "Criterion code is not unique");
        }
        if (criterion.getId() == 0) {
            em.persist(criterion);
        } else {
            int existingVersion = em.createQuery("select c.version from IN_CRITERION c where c.id=:id", Integer.class)
                    .setParameter("id", criterion.getId())
                    .getSingleResult();
            criterion.setVersion(existingVersion);
            criterion = em.merge(criterion);
        }
        return criterion;
    }

    @Override
    public List<String> setDefault(Long id) {

        List<String> bankCodes = null;
        // Get Criteria children id
        List<Long> childrenIds = em.createQuery("select c.id from IN_BANK_GROUPS c where c.parentId=:id", Long.class)
                .setParameter("id", id)
                .getResultList();
        // Get All Bank id
        List<Long> fiIds = em.createQuery("select c.id from IN_BANKS c", Long.class).getResultList();
        // Get Bank_Id ,PeerGroup ID
        List<Object[]> result = em.createQuery("select bg.bankGroupPK.bankId,bg.bankGroupPK.bankGroupId from MM_BANK_GROUP bg", Object[].class).getResultList();

        for (Object[] objects : result) {
            long bankId = (long) objects[0];
            long bankGroupId = (long) objects[1];
            if (childrenIds.contains(bankGroupId)) {
                fiIds.remove(bankId);
            }
        }

        if (!fiIds.isEmpty()) {
            bankCodes = em.createQuery("select c.code from IN_BANKS c where c.id in(:ids)", String.class)
                    .setParameter("ids", fiIds)
                    .getResultList();
        } else {
            em.createQuery("update IN_CRITERION c set  c.isDefault=false where c.isDefault=true").executeUpdate();
            em.createQuery("update IN_CRITERION c set c.isDefault=true where c.id=:id").setParameter("id", id).executeUpdate();
        }

        return bankCodes;
    }

    @Override
    public List<PeerGroup> loadPeerGroupByCodes(List<String> codes) {
        if (codes.isEmpty()) {
            return new ArrayList<>();
        }
        return em.createQuery("select pg from IN_BANK_GROUPS pg where pg.code in(:codes)", PeerGroup.class)
                .setParameter("codes", codes)
                .getResultList();
    }

    @Override
    public Long getDefaultCriteriaId() {
        List<Long> defaultId = em.createQuery("select c.id from IN_CRITERION c where c.isDefault=true", Long.class).getResultList();

        if (!defaultId.isEmpty()) {
            return defaultId.get(0);
        }
        return null;
    }

    @Override
    public PeerGroup getPeerGroupByCode(String code) {
        List<PeerGroup> pgList = em.createQuery("SELECT pg FROM IN_BANK_GROUPS pg WHERE trim(pg.code)=:code", PeerGroup.class).setParameter("code", code.trim()).getResultList();
        if (!pgList.isEmpty()) {
            return pgList.get(0);
        }
        return null;
    }

    @Override
    public Criterion loadCriterionById(long id) {
        return em.find(Criterion.class, id);
    }

    @Override
    public PeerGroup loadGroupById(long id) {
        return em.find(PeerGroup.class, id);
    }


    @Override
    public void deletePeerGroup(long id) throws FinATypeException {
        boolean hasDependency = !em.createQuery("select mmbg.bankGroupPK.bankId from MM_BANK_GROUP mmbg where mmbg.bankGroupPK.bankGroupId=:id")
                .setParameter("id", id)
                .getResultList().isEmpty();
        if (hasDependency) {
            throw new FinATypeException(FinATypeException.Type.DEPENDENCY_ERROR);
        }
        em.createQuery("delete from IN_BANK_GROUPS where id=:id ")
                .setParameter("id", id)
                .executeUpdate();
    }

    @Override
    public void deleteCriterion(long id) throws FinATypeException {
        Long childrenCount = em.createQuery("select count(ibg) from IN_BANK_GROUPS ibg join MM_BANK_GROUP mm on mm.bankGroupPK.bankGroupId=ibg.id where ibg.parentId=:id", Long.class)
                .setParameter("id", id)
                .getSingleResult();
        if (childrenCount > 0) {
            throw new FinATypeException(FinATypeException.Type.DEPENDENCY_ERROR);
        }

        em.createQuery("delete from IN_BANK_GROUPS c where c.parentId=:id")
                .setParameter("id", id)
                .executeUpdate();

        em.createQuery("delete from IN_CRITERION where id=:id ")
                .setParameter("id", id)
                .executeUpdate();
    }
}