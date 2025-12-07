package net.fina.first.ha.ejb;

import net.fina.common.server.util.ConfigurationUtil;
import org.jboss.logging.Logger;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

@Startup
@Singleton
public class ClusteringSingleton {
    private final Logger log = Logger.getLogger(getClass());

    @Resource(lookup = "java:jboss/infinispan/cache/fina-singleton-service-cache-container/fina-singleton-service-cache")
    private org.infinispan.Cache<String, String> singletonServiceCache;

    @PostConstruct
    public void start() {
        log.info("ClusteringSingleton STARTING UP");
        log.info("ClusteringSingleton " + singletonServiceCache.toString());
        log.info(System.getProperty("jboss.node.name"));
    }

    @Lock(LockType.WRITE)
    public void putCache(String keu, String value) {
        singletonServiceCache.put(keu, value);
    }

    public boolean isActiveSingletonServiceNode() {
        try {
            if (ConfigurationUtil.get().isStandaloneMode()) {
                return true;
            }

            String serviceNode = singletonServiceCache.get("SINGLETON_SERVICE_NODE");
            return serviceNode != null && serviceNode.equals(System.getProperty("jboss.node.name"));
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            return false;
        }
    }
}
