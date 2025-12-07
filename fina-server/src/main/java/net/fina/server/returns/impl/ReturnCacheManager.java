package net.fina.server.returns.impl;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import jakarta.ejb.*;
import net.fina.common.client.property.PropertyKeys;
import net.fina.common.server.StatisticsLogger;
import net.fina.server.returns.api.ReturnDefinitionLocal;
import net.fina.server.returns.entity.ReturnDefinition;
import net.fina.server.security.api.PropertyLocal;
import org.jboss.logging.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
@Startup
@DependsOn({"MDTCacheManager"})
@Lock(LockType.READ)
public class ReturnCacheManager {

    private final Logger log = Logger.getLogger(getClass());
    private final Map<Long, List<Long>> dependentReturnTreeCache = new ConcurrentHashMap<>();
    @Resource(lookup = "java:jboss/infinispan/cache/fina-returnDependency-cache-container/fina-returnDependency-cache")
    private org.infinispan.Cache<Long, Collection<ReturnDefinition>> dependenciesCache;
    private boolean init;

    @EJB
    private ReturnDefinitionLocal returnDefinitionLocal;

    @EJB
    private PropertyLocal propertyLocal;

    @PostConstruct
    public void autoStart() {
        start();
    }


    public void start() {
        log.info("Constructing object of " + this.getClass().getName());
        try (StatisticsLogger statLog = new StatisticsLogger("Return Cache Manager");) {

            if (this.dependenciesCache.isEmpty()) {
                statLog.logStage("Init Dependencies Cache");
                dependenciesCache.putAll(returnDefinitionLocal.getReturnDefinitionsDependencies(isReturnDefinitionDisableFeatureEnable(), -1L));
            }
        }
        init = true;
    }

    @PreDestroy
    public void stop() {
        init = false;
        dependenciesCache.clear();
        dependentReturnTreeCache.clear();
        log.info(getClass().getName() + " Cache Cleared.");
    }


    public Map<Long, Collection<ReturnDefinition>> loadReturnDefinitionDependencies() {
        return Collections.unmodifiableMap(dependenciesCache);
    }

    public boolean isInit() {
        return init;
    }

    public int getCacheSize() {
        return dependenciesCache.size();
    }

    public void update(Long key, Collection<ReturnDefinition> newValue) {
        dependenciesCache.put(key, newValue);
    }

    public void remove(Long key) {
        dependenciesCache.remove(key);
    }

    public void remove(List<Long> keys) {
        if (keys != null) {
            keys.forEach(id -> dependenciesCache.remove(id));
        }
    }

    private boolean isReturnDefinitionDisableFeatureEnable() {
        try {
            String enableString = propertyLocal.getSystemProperty(PropertyKeys.RETURN_DEFINITION_DISABLE_FEATURE_ENABLE);
            if (enableString != null && (!enableString.isEmpty())) {
                return Integer.parseInt(enableString) > 0;
            }
        } catch (Exception t) {
            log.error(t.getMessage(), t);
        }
        return false;
    }

    public void updateReturnDependencyTreeCache(Long returnId, List<Long> dependentReturnIds) {
        if (dependentReturnIds == null && returnId != null) {
            dependentReturnTreeCache.remove(returnId);
        } else if (returnId != null) {
            dependentReturnTreeCache.put(returnId, dependentReturnIds == null ? new ArrayList<>() : dependentReturnIds);
        }
    }

    public List<Long> getReturnDependencyTreeByReturnId(long returnId) {
        return dependentReturnTreeCache.getOrDefault(returnId, null);
    }
}
