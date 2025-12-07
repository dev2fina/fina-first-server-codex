package net.fina.server.activeuser;

import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.shared.ActiveUserModel;
import net.fina.common.shared.ClientAppName;
import net.fina.server.i18n.api.LanguageLocal;
import net.fina.server.i18n.helper.LanguageModelHelper;
import net.fina.server.security.api.UserLocal;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.logging.Logger;

import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Asynchronous;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Stateless
@SecurityDomain("FinASecurityDomain")
@PermitAll
public class ActiveUsersSession {
    private final Logger log = Logger.getLogger(ActiveUsersSession.class.getName());

    @Resource(lookup = "java:jboss/infinispan/cache/fina-active-users-cache-container/fina-active-users-cache")
    private org.infinispan.Cache<String, ActiveUserModel> activeUsersCache;

    @Inject
    private LanguageLocal languageLocal;

    @Inject
    private UserLocal userLocal;

    @Asynchronous
    public void addUserToCache(ActiveUserModel userModel) {
        try {
            ActiveUserModel existing = activeUsersCache.get(userModel.getSessionId());
            //set session creation Date
            if (existing == null) {
                userModel.setSessionCreateDate(new Date());
            } else {
                userModel.setSessionCreateDate(existing.getSessionCreateDate());
            }

            if (userModel.getClientAppName().equals(ClientAppName.SP)) {
                userModel.setLanguage(LanguageModelHelper.toModel(languageLocal.getLanguageByCodeOrDefault(userModel.getLanguage().getCode())));
                userModel.setName(userLocal.findUserbyLogin(userModel.getLogin()).getDescription().getDescription(userModel.getLanguage().getId()));
            }
            activeUsersCache.put(userModel.getSessionId(), userModel);
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
    }

    public ActiveUserModel get(String sessionId) {
        return activeUsersCache.get(sessionId);
    }

    public void removeUserFromCache(String sessionId) {
        activeUsersCache.remove(sessionId);
    }

    @RolesAllowed(PermissionIdNames.ACTIVE_USERS_REVIEW)
    public List<ActiveUserModel> loadActiveUsers() {
        List<ActiveUserModel> result = new ArrayList<>();
        for (Map.Entry<String, ActiveUserModel> entry : activeUsersCache.entrySet()) {
            result.add(entry.getValue());
        }
        return result;
    }

    @Asynchronous
    public void clearSpActiveUsersCache() {
        for (Map.Entry<String, ActiveUserModel> entry : activeUsersCache.entrySet()) {
            if (ClientAppName.SP.equals(entry.getValue().getClientAppName())) {
                activeUsersCache.remove(entry.getKey());
            }
        }
    }

}
