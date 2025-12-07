package net.fina.server.returns.impl;

import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import net.fina.common.client.exception.FinATypeException;
import net.fina.server.i18n.api.SysStringLocal;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.returns.api.ReturnTypeLocal;
import net.fina.server.returns.entity.Return;
import net.fina.server.returns.entity.ReturnType;
import net.fina.server.returns.entity.ReturnType_;
import net.fina.server.returns.util.ReturnTypeReservedCodesUtil;

import java.util.List;

@Stateless
@Local(ReturnTypeLocal.class)
@Interceptors(RecordingAuditor.class)
public class ReturnTypeSession implements ReturnTypeLocal {

    @Inject
    private EntityManager em;

    @EJB
    private SysStringLocal sysStringLocal;

    @Override
    public ReturnType getReturnTypeByCode(String code) {
        List<ReturnType> returnTypes = em.createQuery("select rt from IN_RETURN_TYPES rt " +
                        "where rt.code = :code", ReturnType.class)
                .setParameter("code", code)
                .getResultList();
        if (returnTypes.size() == 0) {
            return null;
        }
        return returnTypes.get(0);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ReturnType> loadReturnTypes() {
        Query query = em.createNamedQuery("loadReturnTypes", ReturnType.class);
        return query.getResultList();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void delete(long id) throws FinATypeException {
        // GET dependencies
        Query query = em.createNativeQuery("SELECT id FROM IN_RETURN_DEFINITIONS  WHERE typeID=:id");
        query.setParameter("id", id);
        List integers = query.getResultList();

        if (integers.size() > 0) {
            throw new FinATypeException(FinATypeException.Type.DEPENDENCY_ERROR);
        }
        ReturnType returnType = em.find(ReturnType.class, id);
        if (returnType != null) {
            Long descriptionId = returnType.getDescription().getNameStrId();
            if (descriptionId != null) {
                sysStringLocal.delete(descriptionId);
            }
            em.remove(returnType);
        }
    }

    @Override
    public boolean checkReturnTypeCodeUnique(ReturnType entity) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root typeRoot = cq.from(ReturnType.class);
        cq.where(cb.equal(typeRoot.get(ReturnType_.code), entity.getCode()), cb.notEqual(typeRoot.get(ReturnType_.id), entity.getId()));
        cq.select(typeRoot.get(ReturnType_.id));

        return (em.createQuery(cq).getResultList().size() == 0);
    }

    @Override
    public ReturnType getReturnTypeByReturnId(long returnId) {
        return em.find(Return.class, returnId).getSchedule().getReturnDefinition().getReturnType();
    }

    @Override
    public byte[] getFormat(long id) {
        return em.find(ReturnType.class, id).getFormat();
    }

    @Override
    public void saveFormat(long id, byte[] format) {
        ReturnType returnType = em.find(ReturnType.class, id);
        returnType.setFormat(format);
        returnType.setExcelTemplate(true);
    }

    @Override
    public String getReturnCodeById(long id) {
        return em.find(ReturnType.class, id).getCode();
    }

    @Override
    public ReturnType save(ReturnType returnType) throws FinATypeException {
        if (!isCodeUnique(returnType.getCode(), returnType.getId())) {
            throw new FinATypeException(FinATypeException.Type.CODE_UNIQUE);
        }
        if (returnType.getCode().length() > 12) {
            throw new FinATypeException(FinATypeException.Type.INVALID_VALUE, "Code length should be less than 12");
        }
        if (returnType.getId() > 0) {
            ReturnType existed = em.find(ReturnType.class, returnType.getId());
            // update only if not reserved code
            if (!ReturnTypeReservedCodesUtil.getInstance().isCregCode(existed.getCode())) {
                existed.setCode(returnType.getCode());
            }
            existed.setDescription(returnType.getDescription());
        } else {
            em.persist(returnType);
        }
        return returnType;
    }

    @SuppressWarnings("rawtypes")
    public boolean isCodeUnique(String code, long id) {
        Query codeUniqueQuery = em.createQuery("select c.id from IN_RETURN_TYPES c where trim(c.code)=:code and c.id !=:id");
        codeUniqueQuery.setParameter("id", id);
        codeUniqueQuery.setParameter("code", code);
        List ids = codeUniqueQuery.getResultList();
        return ids.isEmpty();
    }
}
