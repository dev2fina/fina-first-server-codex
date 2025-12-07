package net.fina.server.store.impl.oak;


import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import net.fina.common.server.util.ConfigurationUtil;
import net.fina.server.store.impl.FinaRepository;
import net.fina.server.store.impl.JcrRepositoryProvider;
import org.apache.jackrabbit.oak.Oak;
import org.apache.jackrabbit.oak.jcr.Jcr;
import org.apache.jackrabbit.oak.segment.SegmentNodeStoreBuilders;
import org.apache.jackrabbit.oak.segment.file.FileStore;
import org.apache.jackrabbit.oak.segment.file.FileStoreBuilder;
import org.apache.jackrabbit.oak.segment.file.InvalidFileStoreVersionException;
import org.jboss.logging.Logger;

import javax.jcr.Repository;
import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

/**
 * Author: Oto Iantbelidze
 * Created: 24.10.25
 */
@ApplicationScoped
@Alternative
public class OakRepositoryProvider implements JcrRepositoryProvider {
    private static final Logger log = Logger.getLogger(OakRepositoryProvider.class);
    private final Map<FinaRepository, Repository> repositories = new EnumMap<>(FinaRepository.class);

    @PostConstruct
    public void init() {
        log.info("Initializing Oak repositories...");
        try {
            for (FinaRepository repo : FinaRepository.values()) {
                Repository r = createRepository(repo);

                log.info("Initialized JackRabbit repository for " + r.getClass().getName());
                repositories.put(repo, r);
                log.info("Initialized Oak repository for " + repo);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize Oak repositories", e);
        }
    }

    private Repository createRepository(FinaRepository repo) throws InvalidFileStoreVersionException, IOException {

        String repoHome = ConfigurationUtil.get().get("JCR." + repo.name().toLowerCase() + ".home");
        String repoName = ConfigurationUtil.get().get("JCR." + repo.name().toLowerCase() + ".name");

        if (repoHome == null) {
            throw new IllegalStateException("Missing Oak home for repo: " + repo);
        }


        File baseDir = new File(repoHome, (repoName != null ? repoName : repo.name().toLowerCase()));
        if (!baseDir.exists() && !baseDir.mkdirs()) {
            throw new IllegalStateException("Cannot create repo home: " + repoHome);
        }


        FileStore fileStore = FileStoreBuilder.fileStoreBuilder(baseDir)
                .withMaxFileSize(256)      // MB per segment
                .withMemoryMapping(true)
                .withSegmentCacheSize(1024)
                .build();

        return new Jcr(new Oak(SegmentNodeStoreBuilders.builder(fileStore).build()))
                .createRepository();
    }

    @Override
    public Repository get(FinaRepository repo) {
        return repositories.get(repo);
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down Oak repositories...");
        repositories.clear();
    }
}
