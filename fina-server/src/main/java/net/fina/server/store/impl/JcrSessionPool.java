package net.fina.server.store.impl;


import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.fina.common.server.util.ConfigurationUtil;
import net.fina.server.store.impl.oak.OakRepositoryProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

@ApplicationScoped
public class JcrSessionPool {

    private static final Logger log = LoggerFactory.getLogger(JcrSessionPool.class);

    private static final int MAX_POOL_SIZE = 10;
    private static final int MAX_USES = 10;
    private final Map<FinaRepository, BlockingQueue<Session>> poolMap = new ConcurrentHashMap<>();
    private final ConcurrentMap<Session, Integer> usageMap = new ConcurrentHashMap<>();
    private final AtomicInteger totalCreated = new AtomicInteger(0);

    @Inject
    JcrRepositoryProvider repositoryProvider;

    @Inject
    NodeTypeRegistrar nodeTypeRegistrar;


    /**
     * Borrow a live Session from the pool.
     * Creates a new one if none are available or if an existing one exceeded MAX_USES.
     */
    public Session borrow(FinaRepository repo) throws RepositoryException {
        BlockingQueue<Session> queue = poolMap.computeIfAbsent(repo, r -> new LinkedBlockingQueue<>());

        while (true) {
            Session session = queue.poll();

            if (session == null) {
                Session newSession = createNewSession(repo);
                usageMap.put(newSession, 1);
                return newSession;
            }

            // Validate existing session
            int uses = usageMap.getOrDefault(session, 0);
            if (session.isLive() && uses < MAX_USES) {
                usageMap.merge(session, 1, Integer::sum);
                log.info("Reused JCR session for {} ({} uses)", repo, uses + 1);
                return session;
            }

            // Overused or dead — close it
            safeLogout(session);
            usageMap.remove(session);
            log.debug("Closed overused JCR session for {}", repo);
        }
    }


    /**
     * Return session to pool.
     * If pool is full, or session is invalid, logout immediately.
     */
    public void release(FinaRepository repo, Session session) {
        if (session == null || !session.isLive()) {
            safeLogout(session);
            usageMap.remove(session);
            return;
        }

        BlockingQueue<Session> queue = poolMap.computeIfAbsent(repo, r -> new LinkedBlockingQueue<>());

        if (queue.size() >= MAX_POOL_SIZE) {
            safeLogout(session);
            usageMap.remove(session);
            log.trace("Pool full, closed JCR session for {}", repo);
        } else {
            boolean added = queue.offer(session);
            log.debug("Released JCR session for {} ({} uses): {}", repo, usageMap.getOrDefault(session, 0), added ? "added" : "rejected");
        }
    }

    private Session createNewSession(FinaRepository repo) throws RepositoryException {
        Repository repository = repositoryProvider.get(repo);
        Credentials credentials = getCredentials(repo);
        Session session = repository.login(credentials);
        totalCreated.incrementAndGet();

        try {
            nodeTypeRegistrar.ensureRegistered(session, repo);
        } catch (Exception e) {
            log.warn("Node-type registration failed for {}: {}", repo, e.getMessage());
        }

        log.debug("Created new JCR session for {} (total created: {})", repo, totalCreated.get());
        return session;
    }


    @PreDestroy
    public void shutdown() {
        log.info("Shutting down JCR session pool...");
        poolMap.values().forEach(queue -> queue.forEach(this::safeLogout));
        poolMap.clear();
        usageMap.clear();
    }

    private void safeLogout(Session s) {
        try {
            if (s != null && s.isLive()) {
                s.logout();
            }
        } catch (Exception e) {
            log.trace("Error closing JCR session: {}", e.getMessage());
        }
    }

    private Credentials getCredentials(FinaRepository repo) {
        String user = ConfigurationUtil.get().get("JCR." + repo.name().toLowerCase() + ".userId");
        String pass = ConfigurationUtil.get().get("JCR." + repo.name().toLowerCase() + ".password");
        return new SimpleCredentials(user, pass.toCharArray());
    }

}
