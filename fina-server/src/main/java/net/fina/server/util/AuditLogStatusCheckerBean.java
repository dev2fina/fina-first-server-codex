package net.fina.server.util;

import net.fina.common.client.property.PropertyKeys;
import net.fina.server.security.api.PropertyLocal;
import org.jboss.logging.Logger;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

@Startup
@Singleton
public class AuditLogStatusCheckerBean {

    private final Logger log = Logger.getLogger(getClass());

    @EJB
    private PropertyLocal propertyLocal;

    @PostConstruct
    public void checkAuditStatus() {
        try {
            System.setProperty(PropertyKeys.AUDIT_LOG_ENABLE, propertyLocal.getSystemProperty(PropertyKeys.AUDIT_LOG_ENABLE));
            System.setProperty(PropertyKeys.AUDIT_LOG_SERVICE_INTERVAL, String.valueOf(getServiceInterval()));
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
    }

    private int getServiceInterval() {
        try {
            return Integer.parseInt(propertyLocal.getSystemProperty(PropertyKeys.AUDIT_LOG_SERVICE_INTERVAL));
        } catch (Throwable t) {
            log.warn("audit log service interval property not set, default value will be used : 20 minute");
        }

        return 20;
    }

}
