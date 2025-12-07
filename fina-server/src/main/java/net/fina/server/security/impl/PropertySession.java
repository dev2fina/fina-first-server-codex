package net.fina.server.security.impl;

import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.metamodel.ManagedType;
import jakarta.persistence.metamodel.Metamodel;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.property.PropertyKeys;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.security.api.PropertyLocal;
import net.fina.server.security.entity.Property;
import net.fina.server.util.DatabaseMetaDataInfoWork;
import org.hibernate.Session;
import org.jboss.logging.Logger;

import java.sql.DatabaseMetaData;
import java.util.*;

@Stateless
@Local(PropertyLocal.class)
@Interceptors(RecordingAuditor.class)
public class PropertySession implements PropertyLocal {

    @Inject
    private EntityManager em;

    @Override

    public void setSystemProperty(String key, String value) {
        Property p = em.find(Property.class, key);
        if (p != null) {
            p.setValue(value);
        } else {
            Property newProp = new Property();
            newProp.setPropKey(key);
            newProp.setValue(value);
            em.persist(newProp);
        }
    }

    @Override
    public void setSystemProperty(String key, String value, boolean ignoreAuditLog) {
        if (ignoreAuditLog) {
            Property p = em.find(Property.class, key);
            if (p != null) {
                em.createQuery("update SYS_PROPERTIES set value=:value where propKey=:propKey")
                        .setParameter("value", value)
                        .setParameter("propKey", key)
                        .executeUpdate();
            } else {
                em.persist(new Property(key, value));
            }
        } else {
            setSystemProperty(key, value);
        }
    }

    @Override

    public void saveSystemProperty(String key, String value) throws FinATypeException {

        List list = em.createQuery("select p from SYS_PROPERTIES p where p.propKey=:pKey")
                .setParameter("pKey", key)
                .getResultList();

        if (list.isEmpty()) {
            Property property = new Property();
            property.setPropKey(key);
            property.setValue(value);
            em.persist(property);
        } else {
            throw new FinATypeException(FinATypeException.Type.CODE_UNIQUE);
        }

    }

    @Override

    public int getTransactionIsolationLevel() {
        final int[] transactionIsolationLevel = {-1};
        try {
            Session session = em.unwrap(Session.class);
            session.doWork(connection -> transactionIsolationLevel[0] = connection.getTransactionIsolation());
        } catch (Throwable t) {
            Logger.getLogger(getClass()).error(t.getMessage(), t);
        }
        return transactionIsolationLevel[0];
    }

    @SuppressWarnings("unchecked")
    @Override

    public String getSystemProperty(String key) {
        Query query = em.createQuery("select c.value from SYS_PROPERTIES c where c.propKey=:key");
        query.setParameter("key", key);
        List<String> values = query.getResultList();
        String value = null;
        for (String string : values) {
            value = string;
        }
        return value;
    }

    @Override
    public String getSystemProperty(String key, String defaultValue) {
        String prop = getSystemProperty(key);
        return prop != null && !prop.trim().isEmpty() ? prop : defaultValue;
    }

    @Override

    public Map<String, String> getSystemProperty() {
        List<Property> properties = em.createQuery("select sp from SYS_PROPERTIES sp", Property.class).getResultList();
        Map<String, String> propMaps = new HashMap<>();
        for (Property p : properties) {
            propMaps.put(p.getPropKey(), p.getValue());
        }
        return propMaps;
    }

    @Override

    public void setSystemProperty(Map<String, String> propMap) {
        for (Map.Entry<String, String> entry : propMap.entrySet()) {
            setSystemProperty(entry.getKey(), entry.getValue());
        }
    }

    @Override

    public DatabaseMetaData getDatabaseMetaData() throws Exception {
        Session session = (Session) em.getDelegate();
        DatabaseMetaDataInfoWork infoWork = new DatabaseMetaDataInfoWork();
        session.doWork(infoWork);
        return infoWork.getDatabaseMetaData();
    }

    @Override

    public List<String> getAllEntity() {
        List<String> result = new ArrayList<>();
        Metamodel mm = em.getMetamodel();
        for (ManagedType managedType : mm.getManagedTypes()) {
            result.add(managedType.getJavaType().getName());
        }
        Collections.sort(result);
        return result;
    }

    public List<String[]> getDatabaseProperties() throws Exception {
        List<String[]> versions = new ArrayList<>();

        String dbSchemaVersion = getSystemProperty(PropertyKeys.DATABASE_SCHEMA_VERSION);
        versions.add(new String[]{"Database Schema Version", dbSchemaVersion});

        DatabaseMetaData databaseMetaData = getDatabaseMetaData();
        versions.add(new String[]{databaseMetaData.getDatabaseProductName() + " Version:", databaseMetaData.getDatabaseProductVersion()});

        return versions;
    }
}
