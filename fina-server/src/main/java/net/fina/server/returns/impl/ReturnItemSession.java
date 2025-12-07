package net.fina.server.returns.impl;

import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import net.fina.common.client.returns.ProcessStatus;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.processing.api.ProcessingLocal;
import net.fina.server.processing.impl.ReturnLockSingleton;
import net.fina.server.processing.model.ProcessItem;
import net.fina.server.returns.api.ReturnDataStoreLocal;
import net.fina.server.returns.api.ReturnItemLocal;
import net.fina.server.returns.api.ReturnLocal;
import net.fina.server.returns.entity.ReturnItemLite;
import net.fina.server.returns.entity.ReturnStatus;
import net.fina.server.security.api.UserLocal;
import org.jboss.logging.Logger;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.List;

@Stateless
@Local(ReturnItemLocal.class)
@Interceptors(RecordingAuditor.class)
public class ReturnItemSession implements ReturnItemLocal {
    @Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @EJB
    private ReturnLocal returnLocal;
    @EJB
    private UserLocal userLocal;
    @EJB
    private ReturnLockSingleton returnLock;
    @EJB
    private ProcessingLocal processLocal;
    @EJB
    private ReturnDataStoreLocal returnDataStoreLocal;

    @Override

    public void insertReturnItems(List<ReturnItemLite> items) {
        returnDataStoreLocal.checkReturnPermission(items.get(0).getReturnId());

        for (ReturnItemLite itemLite : items) {
            em.persist(itemLite);
        }
    }

    @Override

    public void deleteReturnItems(List<ReturnItemLite> items) {
        for (ReturnItemLite itemLite : items) {

            returnDataStoreLocal.checkReturnPermission(itemLite.getReturnId());

            em.createQuery("DELETE from ReturnItemLite ri WHERE ri.returnId=:returnId and ri.nodeId=:nodeId and ri.versionId=:versionId and ri.tableId=:tableId and ri.rowNumber=:rowNumber ")
                    .setParameter("returnId", itemLite.getReturnId())
                    .setParameter("nodeId", itemLite.getNodeId())
                    .setParameter("versionId", itemLite.getVersionId())
                    .setParameter("tableId", itemLite.getTableId())
                    .setParameter("rowNumber", itemLite.getRowNumber())
                    .executeUpdate();
        }
    }

    @Override
    public void deleteReturnItemsBatch(long returnId, long versionId, long tableId, long rowNUmber, List<Long> nodeIds) {
        returnDataStoreLocal.checkReturnPermission(returnId);

        em.createQuery("DELETE from ReturnItemLite ri WHERE ri.returnId=:returnId and ri.nodeId in :nodeIds and ri.versionId=:versionId and ri.tableId=:tableId and ri.rowNumber=:rowNumber")
                .setParameter("returnId", returnId)
                .setParameter("versionId", versionId)
                .setParameter("rowNumber", rowNUmber)
                .setParameter("tableId", tableId)
                .setParameter("nodeIds", nodeIds)
                .executeUpdate();
    }

    @Override
    public void updateReturnItemRowNumber(ReturnItemLite item, long rowNumber) {

        returnDataStoreLocal.checkReturnPermission(item.getReturnId());

        em.createQuery("UPDATE ReturnItemLite ri SET ri.rowNumber=:newRowNumber WHERE ri.returnId=:returnId and ri.nodeId=:nodeId and ri.versionId=:versionId and ri.tableId=:tableId and  ri.rowNumber=:oldRowNumber ")
                .setParameter("returnId", item.getReturnId())
                .setParameter("nodeId", item.getNodeId())
                .setParameter("versionId", item.getVersionId())
                .setParameter("tableId", item.getTableId())
                .setParameter("oldRowNumber", item.getRowNumber())
                .setParameter("newRowNumber", rowNumber)
                .executeUpdate();
    }

    @Override
    public void updateReturnItemRowNumberBatch(long returnId, long versionId, long tableId, long fromRowNUmber, List<Long> nodeIds, boolean increase) {
        returnDataStoreLocal.checkReturnPermission(returnId);

        String queryTemplate = "UPDATE ReturnItemLite ri SET ri.rowNumber=ri.rowNumber{0}1 WHERE ri.returnId=:returnId and ri.nodeId in :nodeIds and ri.versionId=:versionId and ri.tableId=:tableId and  ri.rowNumber>=:oldRowNumber";

        em.createQuery(MessageFormat.format(queryTemplate, increase ? "+" : "-"))
                .setParameter("returnId", returnId)
                .setParameter("nodeIds", nodeIds)
                .setParameter("versionId", versionId)
                .setParameter("tableId", tableId)
                .setParameter("oldRowNumber", fromRowNUmber)
                .executeUpdate();
    }

    @Override
    public void updateReturnItems(Collection<ReturnItemLite> values, long returnId, long versionId, boolean lock, String note, String processId) {

        returnDataStoreLocal.checkReturnPermission(returnId);

        ReturnStatus rs = returnLocal.loadReturnCurrentStatus(returnId);
        long scheduleId = returnLocal.getReturnScheduleId(returnId);

        assert rs.getStatus() != ProcessStatus.STATUS_ACCEPTED : "Unable to save accepted return. Return Id:" + returnId;

        Boolean canAmend = userLocal.getCallerPrincipal().getReturnVersions().get(versionId);

        if (canAmend != null && canAmend) {
            try {
                if (lock) {
                    if (returnLock.isLock(scheduleId, versionId)) {
                        throw new ConcurrentModificationException("Return is lock. schedule id:" + scheduleId + ", version Id:" + versionId);
                    }
                    //Lock return
                    returnLock.lock(scheduleId, versionId);
                }

                List<ProcessItem> processItems = new ArrayList<>();
                for (ReturnItemLite lite : values) {
                    ProcessItem pItem = new ProcessItem();
                    int rowNumber = (int) lite.getRowNumber();
                    pItem.values.put(rowNumber, lite.getValue());
                    pItem.idByRowNumber.put(rowNumber, lite.getId());
                    pItem.returnId = lite.getReturnId();
                    pItem.nodeId = lite.getNodeId();
                    pItem.versionId = lite.getVersionId();
                    pItem.tableId = lite.getTableId();
                    pItem.code = lite.getNodeCode();
                    processItems.add(pItem);
                }
                processLocal.updateProcessItems(processItems, processId);

                returnLocal.addReturnStatus(returnId, ProcessStatus.STATUS_AMENDED, versionId, userLocal.getCurrentUserId(), note, processId);
            } finally {
                if (lock) {
                    returnLock.unlock(scheduleId, versionId);
                }
            }
        } else {
            log.error("User Does Not have Amend Permission for current return version.....");
        }
    }

    @Override
    public long getItemMaxId(long returnId, long versionId) {
        try {
            return em.createQuery("select max(ri.id) from ReturnItemLite ri where ri.returnId=:returnId and ri.versionId=:versionId", Long.class)
                    .setParameter("returnId", returnId)
                    .setParameter("versionId", versionId)
                    .getSingleResult();
        } catch (Throwable t) {
            return 0;
        }
    }
}
