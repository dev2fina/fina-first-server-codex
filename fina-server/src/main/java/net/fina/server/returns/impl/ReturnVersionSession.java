package net.fina.server.returns.impl;

import jakarta.persistence.Tuple;
import net.fina.common.client.exception.FinATypeException;
import net.fina.server.i18n.api.SysStringLocal;
import net.fina.server.i18n.helper.Description;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.returns.api.ReturnVersionLocal;
import net.fina.server.returns.entity.*;
import net.fina.server.security.api.UserLocal;
import net.fina.server.security.entity.User;

import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;

import java.util.*;
import java.util.stream.Collectors;

@Stateless
@Local(ReturnVersionLocal.class)
@Interceptors(RecordingAuditor.class)
public class ReturnVersionSession implements ReturnVersionLocal {
    @Inject
    private EntityManager em;

    @EJB
    private SysStringLocal sysStringLocal;

    @EJB
    private UserLocal current;


    public ReturnVersion findByCode(String code) {
        Query versionQuery = em.createNamedQuery("ReturnVersion.findByCode");
        versionQuery.setParameter("code", code.trim());
        ReturnVersion retVersion = (ReturnVersion) versionQuery.getSingleResult();
        return retVersion;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ReturnVersion> loadReturnVersions(boolean loadAll) {

        long userId = current.getCurrentUserId();

        Query loadReturnVersionsQuery;
        if (loadAll) {
            loadReturnVersionsQuery = em.createQuery("Select new " + ReturnVersion.class.getName() + "( rv.returnVersionId.returnVersion.id, rv.returnVersionId.returnVersion.version, rv.returnVersionId.returnVersion.code, rv.returnVersionId.returnVersion.description ) from SYS_USERS as u, IN(u.roles) r, IN(r.returnVersions) rv");

        } else {
            loadReturnVersionsQuery = em.createQuery("Select new " + ReturnVersion.class.getName() + "( rv.returnVersionId.returnVersion.id, rv.returnVersionId.returnVersion.version, rv.returnVersionId.returnVersion.code, rv.returnVersionId.returnVersion.description ) from SYS_USERS as u, IN(u.roles) r, IN(r.returnVersions) rv where u.id=:userId ");
            loadReturnVersionsQuery.setParameter("userId", userId);
        }
        Set<ReturnVersion> returnVersions = new HashSet<ReturnVersion>(loadReturnVersionsQuery.getResultList());

        if (loadAll) {
            loadReturnVersionsQuery = em.createQuery("select new " + ReturnVersion.class.getName() + "( rv.returnVersionId.returnVersion.id, rv.returnVersionId.returnVersion.version, rv.returnVersionId.returnVersion.code, rv.returnVersionId.returnVersion.description ) from SYS_USERS as u, IN(u.returnVersions) rv");
        } else {
            loadReturnVersionsQuery = em.createQuery("select new " + ReturnVersion.class.getName() + "( rv.returnVersionId.returnVersion.id, rv.returnVersionId.returnVersion.version, rv.returnVersionId.returnVersion.code, rv.returnVersionId.returnVersion.description ) from SYS_USERS as u, IN(u.returnVersions) rv  where u.id=:userId ");
            loadReturnVersionsQuery.setParameter("userId", userId);
        }
        returnVersions.addAll(loadReturnVersionsQuery.getResultList());

        return new ArrayList<>(returnVersions);
    }

    @Override
    public ReturnVersion save(ReturnVersion returnVersion) throws FinATypeException {
        if (getReturnVersionByCode(returnVersion.getCode(), returnVersion.getId()) > 0) {
            throw new FinATypeException(FinATypeException.Type.CODE_UNIQUE);
        }
        if (returnVersion.getId() > 0) {
            returnVersion = em.merge(returnVersion);
        } else {
            em.persist(returnVersion);

            UserReturnVersion urv = new UserReturnVersion();
            urv.setCanAmend(true);
            UserReturnVersionId userReturnVersionId = new UserReturnVersionId();
            userReturnVersionId.setUser(em.find(User.class, current.getCurrentUserId()));
            userReturnVersionId.setReturnVersion(returnVersion);
            urv.setReturnVersionId(userReturnVersionId);
            em.persist(urv);

            //Add caller principal
            current.getCallerPrincipal().getReturnVersions().put(returnVersion.getId(), returnVersion.isCanAmend());
        }
        return returnVersion;
    }

    @Override
    public void delete(long id) throws FinATypeException {

        ReturnVersion returnVersion = em.find(ReturnVersion.class, id);

        // GET dependencies
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<ReturnStatus> returnStatusRoot = cq.from(ReturnStatus.class);
        Join<ReturnStatus, ReturnVersion> statusVersionJoin = returnStatusRoot.join(ReturnStatus_.returnVersion);
        cq.where(cb.equal(statusVersionJoin.get(ReturnVersion_.id), id));
        cq.select(returnStatusRoot.get(ReturnStatus_.id));
        if (!em.createQuery(cq).getResultList().isEmpty()) {
            throw new FinATypeException(FinATypeException.Type.DEPENDENCY_ERROR);
        }

        cb = em.getCriteriaBuilder();
        cq = cb.createQuery(Long.class);
        Root<Return> returnRoot = cq.from(Return.class);
        Join<Return, ReturnVersion> returnReturnVersionJoin = returnRoot.join(Return_.returnVersion);
        cq.where(cb.equal(returnReturnVersionJoin.get(ReturnVersion_.id), id));
        cq.select(returnRoot.get(Return_.id));
        if (!em.createQuery(cq).getResultList().isEmpty()) {
            throw new FinATypeException(FinATypeException.Type.DEPENDENCY_ERROR);
        }

        // remove user permissions to this return version
        Query deleteUserPerms = em.createNativeQuery("DELETE FROM SYS_USER_RETURN_VERSIONS WHERE VERSION_ID=:id");
        deleteUserPerms.setParameter("id", id);
        deleteUserPerms.executeUpdate();

        // remove role permissions to this return version
        Query deleteRolePerms = em.createNativeQuery("DELETE FROM SYS_ROLE_RETURN_VERSIONS WHERE VERSION_ID =:id");
        deleteRolePerms.setParameter("id", id);
        deleteRolePerms.executeUpdate();

        if (returnVersion != null) {
            Description description = returnVersion.getDescription();
            if (description != null) {
                sysStringLocal.delete(description.getNameStrId());
            }
            em.remove(returnVersion);
        }
    }

    private long getReturnVersionByCode(String code, long id) {
        Query query = em.createQuery("SELECT r from IN_RETURN_VERSIONS r WHERE trim(r.code)=:code and r.id<>:id");
        query.setParameter("code", code);
        query.setParameter("id", id);
        return query.getResultList().size();
    }

    @Override
    public List<ReturnVersion> loadAllReturnVersion() {
        return em.createNamedQuery("ReturnVersion.findAll", ReturnVersion.class).getResultList();
    }

    @Override
    public ReturnVersion loadSimpleReturnVersion(long id) {
        StringBuilder qlString = new StringBuilder();
        qlString.append("SELECT ");
        qlString.append(" NEW ");
        qlString.append(ReturnVersion.class.getName());
        qlString.append("(");
        qlString.append("rv.id, ");
        qlString.append("rv.version ");
        qlString.append(")");
        qlString.append(" from IN_RETURN_VERSIONS rv where rv.id=:id ");
        Query query = em.createQuery(qlString.toString());
        query.setParameter("id", id);
        return (ReturnVersion) query.getSingleResult();
    }

    @Override
    public boolean checkReturnVersionCodeUnique(ReturnVersion returnVersion) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<ReturnVersion> versionRoot = cq.from(ReturnVersion.class);
        cq.where(cb.equal(versionRoot.get(ReturnVersion_.code), returnVersion.getCode()), cb.notEqual(versionRoot.get(ReturnVersion_.id), returnVersion.getId()));
        cq.select(versionRoot.get(ReturnVersion_.id));

        return (em.createQuery(cq).getResultList().isEmpty());
    }

    @Override
    public List<ReturnVersion> loadReturnVersionsByCodes(List<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return new ArrayList<>();
        }
        return em.createQuery("select rv from IN_RETURN_VERSIONS rv where rv.code in(:codes)", ReturnVersion.class)
                .setParameter("codes", codes)
                .getResultList();
    }

    @Override
    public Map<String, Long> loadRetrunVersionCodeIdMap(){
        return em.createQuery("select code,id  from IN_RETURN_VERSIONS ", Tuple.class)
                .getResultStream().collect(
                        Collectors.toMap(
                                tuple -> (String) tuple.get(0),
                                tuple -> ((Number) tuple.get(1)).longValue()
                        )
                );
    }
}
