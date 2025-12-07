package net.fina.server.store.impl.legacy;


import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import net.fina.common.server.util.ConfigurationUtil;
import net.fina.server.store.impl.FinaRepository;
import net.fina.server.store.impl.JcrRepositoryProvider;
import org.jboss.logging.Logger;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.RepositoryFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author: Oto Iantbelidze
 * Created: 23.10.25
 */
@ApplicationScoped
@Alternative
public class JackRabbitRepositoryProvider implements JcrRepositoryProvider {
    private final Logger log = Logger.getLogger(JackRabbitRepositoryProvider.class);
    private final Map<FinaRepository, Repository> repositories = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        log.info("Initializing JackRabbit repositories...");

        for (FinaRepository repo : FinaRepository.values()) {
            try {
                repositories.put(repo, createRepository(repo));
            } catch (RepositoryException e) {
                log.error("Failed to create JackRabbit repository for " + repo.name(), e);
                throw new RuntimeException("Repository initialization failed", e);
            }
        }
    }

    private Repository createRepository(FinaRepository repo) throws RepositoryException {
        Map<String, String> parameters = new HashMap<>();

        String uri = ConfigurationUtil.get().get("JCR." + repo.name().toLowerCase() + ".uri");
        if (uri != null && !uri.isEmpty()) {
            parameters.put("org.apache.jackrabbit.repository.uri", uri);
        } else {
            String home = ConfigurationUtil.get().get("JCR." + repo.name().toLowerCase() + ".home");
            String name = ConfigurationUtil.get().get("JCR." + repo.name().toLowerCase() + ".name");
            parameters.put("org.apache.jackrabbit.repository.home", home + "/" + (name != null ? name : repo.name().toLowerCase()));
        }

        // Performance tuning
        parameters.put("org.apache.jackrabbit.cache.bundleCacheSize", "16384");
        parameters.put("org.apache.jackrabbit.cache.itemCacheSize", "8192");
        parameters.put("org.apache.jackrabbit.datastore.minRecordLength", "4096");
        parameters.put("org.apache.jackrabbit.searchIndex.cacheSize", "2048");

        RepositoryFactory factory = new org.apache.jackrabbit.core.RepositoryFactoryImpl();
        Repository repository = factory.getRepository(parameters);

        log.info("Initialized JackRabbit repository for " + repository.getClass().getName());
        return repository;
    }

    @Override
    public Repository get(FinaRepository repo) {
        return repositories.get(repo);
    }

    @PreDestroy
    public void shutdown() {
        repositories.clear();
    }
}
