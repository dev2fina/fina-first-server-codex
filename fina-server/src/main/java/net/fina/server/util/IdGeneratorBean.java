package net.fina.server.util;


import org.jboss.logging.Logger;

import jakarta.ejb.*;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@Stateless
public class IdGeneratorBean {
    private Logger log = Logger.getLogger(getClass());

    @Inject
    private EntityManager em;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void fixId(String table, String pkColumn) {
        synchronized (this) {
            try {
                Object maxId = em.createNativeQuery("select max(id)+1 from " + table).getSingleResult();
                em.createNativeQuery("update SYS_ID_GENERATOR set value=:maxId where PK_COLUMN_NAME=:columnName").setParameter("maxId", maxId == null ? 0 : maxId).setParameter("columnName", pkColumn)
                        .executeUpdate();
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
            }
        }
    }
}
