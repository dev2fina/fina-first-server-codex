package net.fina.server.i18n.impl;

import net.fina.security.util.SecurityUtil;
import net.fina.server.i18n.api.DescriptionLocal;
import net.fina.server.i18n.helper.Description;
import org.jboss.logging.Logger;

import javax.naming.InitialContext;

public class DescriptionManager {
    private Logger log = Logger.getLogger(DescriptionManager.class);

    private static volatile DescriptionManager _instance;

    private DescriptionLocal descriptionLocal;

    private DescriptionManager() {
        injectDescriptionLocal();
    }

    public static DescriptionManager getInstance() {
        if (_instance == null) {
            synchronized (DescriptionManager.class) {
                if (_instance == null) {
                    _instance = new DescriptionManager();
                }
            }
        }
        return _instance;
    }

    public Description getDescription(long nameStrId) {
        injectDescriptionLocal();
        return descriptionLocal.getDescription(nameStrId);
    }

    public long getNameStrId(String value, long langId) {
        injectDescriptionLocal();
        return descriptionLocal.getNameStrId(value, langId);
    }

    public void updateSysString(long nameStrId, long langId, Object value) {
        injectDescriptionLocal();
        if (value != null) {
            descriptionLocal.updateSysString(nameStrId, langId, value.toString());
        }
    }

    private void injectDescriptionLocal() {
        if (descriptionLocal == null) {
            try {
                InitialContext ic = new InitialContext();
                this.descriptionLocal = (DescriptionLocal) ic.lookup(SecurityUtil.generateGlobalJndiName(ic, DescriptionBean.class, DescriptionLocal.class));
            } catch (Exception exception) {
                log.error(exception.getMessage(), exception);
            }
        }
    }

    public void removeSysString(long nameStrId) {
        descriptionLocal.removeSysString(nameStrId);
    }
}
