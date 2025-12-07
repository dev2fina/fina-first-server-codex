package net.fina.server.security.impl;

import net.fina.server.i18n.helper.Description;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.security.api.PermissionLocal;
import net.fina.server.security.entity.Permission;

import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.List;
import java.util.Map;

@Stateless
@Local(PermissionLocal.class)
@Interceptors(RecordingAuditor.class)
public class PermissionSession implements PermissionLocal {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Permission loadPermissionByIdName(String idName) {
        List<Permission> list = em.createNamedQuery("loadPermissionByIdName", Permission.class)
                .setParameter("idName", idName)
                .getResultList();
        if (!list.isEmpty()) {
            return list.iterator().next();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Permission> loadPermissions() {
        Query query = em.createNamedQuery("loadPermissions", Permission.class);
        return query.getResultList();
    }

    @Override
    public void updateDescription(Map<String, String> keyValues, long langId) {
        for (Map.Entry<String, String> entry : keyValues.entrySet()) {
            Permission permission = loadPermissionByIdName(entry.getKey());
            permission.setDescription(new Description(langId, permission.getDescription().getNameStrId(), entry.getValue()));
            em.merge(permission);
        }
    }

    @Override
    public Permission findById(long id) {
        return em.find(Permission.class, id);
    }

}
