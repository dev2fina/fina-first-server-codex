package net.fina.server.fsop.impl;


import net.fina.server.fsop.FsopUploadFileMonitoringService;
import net.fina.server.fsop.api.UploadFileQueueStoreLocal;
import net.fina.server.fsop.entity.UploadFileQueue;
import net.fina.server.fsop.entity.UploadFileQueueStatus;
import net.fina.server.fsop.event.RemoveUploadFileQueueEvent;
import net.fina.server.interceptors.RecordingAuditor;

import jakarta.ejb.*;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Stateless
@Local(UploadFileQueueStoreLocal.class)
@Interceptors(RecordingAuditor.class)
public class UploadFileQueueStoreSession implements UploadFileQueueStoreLocal {
    private static final Logger log = Logger.getLogger(UploadFileQueueStoreSession.class);

    @Inject
    private EntityManager em;

    @EJB
    private FsopUploadFileMonitoringService fsopUploadFileMonitoringService;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<UploadFileQueue> load(boolean firstStart, int limit) {
        List<UploadFileQueueStatus> statuses = new ArrayList<>();
        statuses.add(UploadFileQueueStatus.UNDEFINED);
        if (firstStart) {
            statuses.add(UploadFileQueueStatus.IN_PROGRESS);
        }

        return em.createQuery("select f from  IN_UPLOADFILE_QUEUE f where f.status in(:status) order by f.fileId ", UploadFileQueue.class)
                .setParameter("status", statuses)
                .setMaxResults(limit)
                .getResultList();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void changeStatus(List<UploadFileQueue> queueList, UploadFileQueueStatus status) {
        for (UploadFileQueue queueFile : queueList) {
            queueFile.setStatus(status);
            em.createQuery("update IN_UPLOADFILE_QUEUE uq set uq.status=:status where uq.fileId=:fileId")
                    .setParameter("status", queueFile.getStatus())
                    .setParameter("fileId", queueFile.getFileId())
                    .executeUpdate();
            log.info("Changed Queue File status - " +queueFile);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void removeUploadFileQueue(@Observes RemoveUploadFileQueueEvent event) {
        em.createQuery("delete from IN_UPLOADFILE_QUEUE where fileId=:fileId")
                .setParameter("fileId", event.getUploadFileQueue().getFileId())
                .executeUpdate();
    }

    @Override
    public Map<String, Long> getWorkingFiles() {
        return fsopUploadFileMonitoringService.getWorkingFiles();
    }

    @Override
    public long getNumberOfFilesInProgress() {
        return em.createQuery("SELECT count(q) FROM IN_UPLOADFILE_QUEUE q WHERE q.status=:status", Long.class)
                .setParameter("status", UploadFileQueueStatus.IN_PROGRESS)
                .getFirstResult();
    }
}
