package net.fina.first.ecm.config.proxy;

import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.shared.config.ConfigMetaModel;
import net.fina.first.ecm.config.api.FirstConfigLocal;
import org.jboss.ejb3.annotation.SecurityDomain;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

@Stateless
@SecurityDomain("FinASecurityDomain")
@RolesAllowed({PermissionIdNames.FIRST_REVIEW})
public class FirstConfigProxySession {

    @Inject
    private FirstConfigLocal firstConfigLocal;

    public ConfigMetaModel getConfig() {
        return firstConfigLocal.getConfig();
    }

    public String getProperty(String key) {
        return firstConfigLocal.getProperty(key);
    }
}
