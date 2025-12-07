package net.fina.server.reg.impl;

import jakarta.annotation.Resource;
import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import net.fina.common.shared.reg.GenerateSourceResult;
import net.fina.common.shared.reg.GenerateSourceType;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.reg.api.RegFileLocal;
import net.fina.server.reg.entity.RegFileSchedule;
import net.fina.server.reg.entity.RegFileStage;
import net.fina.server.reg.model.InputsMetaModel;
import net.fina.server.reg.util.RegQueryGeneratorUtil;
import net.fina.server.util.FinaDS;
import org.jboss.logging.Logger;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;


@Stateless
@Local(RegFileLocal.class)
@Interceptors(RecordingAuditor.class)
public class RegFileSession implements RegFileLocal {
    @Inject
    private Logger log;

    @Inject
    @FinaDS
    private EntityManager em;

    @Resource(mappedName = "java:jboss/datasources/FinaRegDS")
    private DataSource dataSource;

    @Override
    public void createRegFileSchedulesRelation(long fileId, List<Long> scheduleIds) {
        if (!scheduleIds.isEmpty()) {
            for (long schId : scheduleIds) {
                List<RegFileSchedule> existing = em.createQuery("select rf from IN_REG_FILE_SCHEDULES rf where rf.fileId = :fileId and rf.scheduleId = :scheduleId", RegFileSchedule.class)
                        .setParameter("fileId", fileId)
                        .setParameter("scheduleId", schId)
                        .getResultList();
                if (!existing.isEmpty()) {
                    continue;
                }

                RegFileSchedule fileSchedule = new RegFileSchedule();
                fileSchedule.setFileId(fileId);
                fileSchedule.setScheduleId(schId);
                em.persist(fileSchedule);
            }

        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public GenerateSourceResult generateTable(GenerateSourceType type, List<InputsMetaModel> inputsMetaModels) {
        try {
            return RegQueryGeneratorUtil.generate(type, inputsMetaModels, dataSource.getConnection());
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Long> loadUniqueScheduleIds() {
        return em.createQuery("select distinct fs.scheduleId from IN_REG_FILE_SCHEDULES fs", Long.class).getResultList();
    }

    @Override
    public List<RegFileSchedule> loadByScheduleId(long scheduleId) {
        return em.createQuery("select r from IN_REG_FILE_SCHEDULES r where r.scheduleId = :scheduleId", RegFileSchedule.class)
                .setParameter("scheduleId", scheduleId)
                .getResultList();
    }

    @Override
    public RegFileStage loadByFileId(long fileId) {
        try {
            return em.createQuery("select uqf from IN_REG_FILE_STAGE uqf where uqf.fileId = :fileId ", RegFileStage.class)
                    .setParameter("fileId", fileId).getSingleResult();

        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public void createQueueFile(long fileId) {
        RegFileStage queue = new RegFileStage();
        queue.setFileId(fileId);
        em.merge(queue);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteQueueFile(long fileId) {
        em.createQuery("delete from IN_REG_FILE_STAGE where fileId = :fileId")
                .setParameter("fileId", fileId).executeUpdate();
    }
}
