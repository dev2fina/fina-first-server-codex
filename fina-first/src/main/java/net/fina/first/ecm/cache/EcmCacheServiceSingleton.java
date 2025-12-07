package net.fina.first.ecm.cache;

import net.fina.common.server.util.CommonUtil;
import net.fina.common.shared.ecm.model.ECMPersonMetaModel;
import net.fina.ecm.alfresco.api.common.parameters.IncludeParam;
import net.fina.ecm.alfresco.api.core.model.representation.PersonRepresentation;
import net.fina.first.ecm.people.api.PeopleLocal;
import net.fina.first.ecm.people.model.ECMPersonModelHelper;
import org.jboss.logging.Logger;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Startup
@Singleton
public class EcmCacheServiceSingleton {
    private final Logger log = Logger.getLogger(getClass());

    @Inject
    private PeopleLocal peopleProxySession;

    private final Map<String, ECMPersonMetaModel> ecmUserCache = new HashMap<>();

    @PostConstruct
    private void start() {
        if (CommonUtil.isEcmEnable()) {
            log.info("Start Ecm Cache Service");
            loadAllEcmUsers();
        }
    }

    private void loadAllEcmUsers() {
        try {
            if (CommonUtil.isEcmEnable()) {
                log.info("Load Ecm Users Cache");
                List<PersonRepresentation> userRepresentations = peopleProxySession.getAllUsers();
                log.info("Ecm Users Loaded : " + userRepresentations.size());
                userRepresentations.parallelStream().forEach(u -> {
                    ECMPersonMetaModel userModel = ECMPersonModelHelper.getPersonMetaModel(u);
                    ecmUserCache.put(userModel.getId(), userModel);
                });
                log.info("Ecm Users Cache Loaded Successfully");
            }
        } catch (Throwable t) {
            log.error("COULD NOT LOAD ECM USERS");
            log.error(t.getMessage(), t);
        }
    }

    public ECMPersonMetaModel getUser(String userId) {
        if (ecmUserCache.isEmpty()) {
            loadAllEcmUsers();
        }
        ECMPersonMetaModel person = ecmUserCache.get(userId);
        if (person == null && userId != null) {
            try {
                person = peopleProxySession.getPersonById(userId, new IncludeParam(Collections.singletonList("capabilities")));
                ecmUserCache.put(userId, person);
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
            }
        }
        return person;
    }

}
