package net.fina.server.store.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import net.fina.server.store.qualifier.FileRepository;
import net.fina.server.store.qualifier.Return;
import net.fina.server.store.qualifier.Template;
import net.fina.common.server.StatisticsLogger;
import org.jboss.logging.Logger;

import javax.jcr.Session;

@ApplicationScoped
public class SessionProducer {

    private static final Logger log = Logger.getLogger(SessionProducer.class);

    @Inject
    JcrSessionPool pool;

    @Produces
    @RequestScoped
    @Return
    public Session produceReturnSession() {
        return borrow(FinaRepository.FSOP_RETURNS);
    }

    public void disposeReturnSession(@Disposes @Return Session session) {
        release(FinaRepository.FSOP_RETURNS, session);
    }

    @Produces
    @RequestScoped
    @Template
    public Session produceTemplateSession() {
        return borrow(FinaRepository.FSOP_TEMPLATES);
    }

    public void disposeTemplateSession(@Disposes @Template Session session) {
        release(FinaRepository.FSOP_TEMPLATES, session);
    }

    @Produces
    @RequestScoped
    @FileRepository
    public Session produceFileRepoSession() {
        return borrow(FinaRepository.FINA_REPO);
    }

    public void disposeFileRepoSession(@Disposes @FileRepository Session session) {
        release(FinaRepository.FINA_REPO, session);
    }

    private Session borrow(FinaRepository repo) {

        try (StatisticsLogger statLog = new StatisticsLogger("JCR Session Creation");) {
            statLog.logStage("Borrowing session");
            return pool.borrow(repo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IllegalStateException("Cannot borrow JCR session", e);
        }
    }

    private void release(FinaRepository repo, Session session) {
        try {
            pool.release(repo, session);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
    }
}