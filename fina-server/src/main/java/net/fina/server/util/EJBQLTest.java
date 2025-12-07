package net.fina.server.util;

import org.jboss.logging.Logger;

import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import jakarta.persistence.metamodel.ManagedType;
import jakarta.persistence.metamodel.Metamodel;
import java.lang.reflect.Field;

@Singleton
public class EJBQLTest {

    private Logger log = Logger.getLogger(getClass());

    @Inject
    private EntityManager em;

    public void fixIdGenerator() {
        em.createNativeQuery("delete SYS_ID_GENERATOR").executeUpdate();

        Metamodel mm = em.getMetamodel();
        for (ManagedType managedType : mm.getManagedTypes()) {
            try {
                Class<?> clazz = managedType.getJavaType();
                Field field = null;
                try {
                    field = clazz.getDeclaredField("id");
                } catch (NoSuchFieldException e) {
                    //TODO Ignore Exception
                }
                if (field != null) {
                    TableGenerator tableGenerator = (TableGenerator) field.getAnnotation(TableGenerator.class);
                    if (tableGenerator != null) {
                        Table table = (Table) clazz.getAnnotation(Table.class);
                        if (table != null) {
                            Object maxId = em.createNativeQuery("select max(id)+1 from " + table.name()).getSingleResult();
                            em.createNativeQuery("insert into SYS_ID_GENERATOR (PK_COLUMN_NAME,value) values (:columnName,:maxId)").setParameter("maxId", maxId == null ? 0 : maxId).setParameter("columnName", tableGenerator.pkColumnValue()).executeUpdate();
                        }
                    }
                }
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        }
    }
}