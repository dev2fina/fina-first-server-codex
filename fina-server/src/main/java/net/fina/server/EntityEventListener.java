package net.fina.server;

import net.fina.server.i18n.helper.Description;
import net.fina.server.i18n.impl.DescriptionManager;
import org.jboss.logging.Logger;

import jakarta.persistence.PreRemove;
import java.lang.reflect.Field;

/**
 * This Class Is Responsible For deleting entity sysStrings
 */
public class EntityEventListener {
    private final Logger log = Logger.getLogger(getClass().getName());

    @PreRemove
    public void preDelete(Object entity) {
        removeEntitySysStrings(entity);
    }

    private void removeEntitySysStrings(Object entity) {
        try {

            for (Field f : entity.getClass().getDeclaredFields()) {
                Class type = f.getType();
                if (type == Description.class && entity.getClass().getAnnotation(org.hibernate.envers.Audited.class) == null) {
                    f.setAccessible(true);
                    Description description = (Description) f.get(entity);
                    DescriptionManager.getInstance().removeSysString(description.getNameStrId());
                }
            }
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
    }
}
