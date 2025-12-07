package net.fina.first.ecm.client.proxy;

import net.fina.common.server.util.ConfigurationUtil;
import net.fina.ecm.alfresco.AlfrescoClient;
import org.jboss.ejb3.annotation.SecurityDomain;

import jakarta.annotation.Resource;
import jakarta.ejb.SessionContext;
import jakarta.ejb.Stateless;

@Stateless
@SecurityDomain("FinASecurityDomain")
public class EcmClientProxySession {

    @Resource
    private SessionContext sc;


    public AlfrescoClient getAlfrescoClient(String acceptLanguage) {
        return new AlfrescoClient.Builder().connectExternal(acceptLanguage, ConfigurationUtil.get().get("ECM.endpoint"), sc.getCallerPrincipal().getName(), ConfigurationUtil.get().get("ECM.userHashSalt")).build();
    }

    public AlfrescoClient getAlfrescoClient() {
        return new AlfrescoClient.Builder().connectExternal(ConfigurationUtil.get().get("ECM.endpoint"), sc.getCallerPrincipal().getName(), ConfigurationUtil.get().get("ECM.userHashSalt")).build();
    }

    public AlfrescoClient getAlfrescoClientByUser(String user) {
        return new AlfrescoClient.Builder().connectExternal(ConfigurationUtil.get().get("ECM.endpoint"), user, ConfigurationUtil.get().get("ECM.userHashSalt")).build();
    }

    public AlfrescoClient getAlfrescoClientByUser(String acceptLanguage, String user) {
        return new AlfrescoClient.Builder().connectExternal(acceptLanguage, ConfigurationUtil.get().get("ECM.endpoint"), user, ConfigurationUtil.get().get("ECM.userHashSalt")).build();
    }

}
