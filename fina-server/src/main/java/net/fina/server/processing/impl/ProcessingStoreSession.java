package net.fina.server.processing.impl;

import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.interceptor.ExcludeClassInterceptors;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import net.fina.common.client.mdt.MDTComparisonConditions;
import net.fina.common.client.mdt.MDTNodeTypes;
import net.fina.common.server.StatisticsLogger;
import net.fina.common.server.util.CommonUtil;
import net.fina.common.shared.mdt.ProcessStage;
import net.fina.server.i18n.entity.Language;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.mdt.api.MDTNodeLocal;
import net.fina.server.mdt.entity.MDTComparison;
import net.fina.server.mdt.entity.MDTDependentNode;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.mdt.impl.MDTCacheManager;
import net.fina.server.processing.ProcessingUtil;
import net.fina.server.processing.api.ProcessingStoreLocal;
import net.fina.server.processing.model.ComparisonItem;
import net.fina.server.processing.model.ProcessItem;
import net.fina.server.processing.model.ProcessItemUtil;
import net.fina.server.processing.model.ProcessReturnModel;
import net.fina.server.returns.api.ReturnDefinitionLocal;
import net.fina.server.returns.api.ReturnLocal;
import net.fina.server.returns.entity.ReturnDefinition;
import net.fina.server.returns.entity.ReturnItemLite;
import net.fina.server.returns.entity.ReturnStatus;
import net.fina.server.returns.entity.Schedule;
import net.fina.server.returns.impl.ReturnCacheManager;
import net.fina.server.rvc.event.ReturnItemsStoreEvent;
import org.jboss.logging.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * nikoloz on 8/17/15.
 */
@Stateless
@Local(ProcessingStoreLocal.class)
@Interceptors(RecordingAuditor.class)
public class ProcessingStoreSession implements ProcessingStoreLocal {

    private Logger log = Logger.getLogger(getClass());

    @Inject
    private EntityManager em;

    @EJB
    private ReturnLocal returnLocal;
    @EJB
    private ReturnDefinitionLocal returnDefinitionLocal;
    @EJB
    private ReturnCacheManager returnCacheManager;
    @EJB
    private MDTNodeLocal mdtNodeLocal;

    @Inject
    private Event<ReturnItemsStoreEvent> returnStoreEvent;
    @Inject
    private MDTCacheManager mdtCacheManager;

    @Override
    public Language getLanguage(long langId) {
        return em.find(Language.class, langId);
    }

    @Override
    public ProcessReturnModel getReturnModel(long returnId) {
        return em.createQuery("select new " + ProcessReturnModel.class.getName() + "( r.id,r.returnVersion.id,r.schedule.period.id,r.schedule.period.fromDate,r.schedule.period.toDate,r.schedule.fi.id,r.schedule.returnDefinition.id,r.schedule.returnDefinition.code,r.schedule.period.periodType.code )" + " from IN_RETURNS r where r.id=:returnId ", ProcessReturnModel.class)
                .setParameter("returnId", returnId)
                .getSingleResult();
    }

    @Override

    public Map<Long, List<MDTNode>> loadMdtNodesByParentId() {
        return mdtNodeLocal.loadAllNodesByParentId();
    }

    @Override

    public ReturnStatus loadReturnCurrentStatus(long returnId) {
        return returnLocal.loadReturnCurrentStatus(returnId);
    }

    @Override

    public List<Long> getScheduleReturnDefinitionTableNodeIds(long scheduleId) {
        return em.createQuery("select dt.node.id from IN_SCHEDULES s inner join s.returnDefinition rd, IN(rd.definitionTables) dt where s.id=:scheduleId", Long.class).setParameter("scheduleId", scheduleId).getResultList();
    }

    @Override
    @SuppressWarnings("JpaQlInspection")
    public List<MDTNode> getDependentReturnsNotExistingNodes(final List<Long> nodes, final long scheduleId, final String versionCode) {
        List<List<Long>> partitions = CommonUtil.partitions(nodes);
        Schedule schedule = em.find(Schedule.class, scheduleId);
        TypedQuery<MDTNode> query = em.createQuery("select new " + MDTNode.class.getName() + " ( mn.id,mn.version,mn.parentId,mn.code,mn.description,mn.type ) from IN_MDT_NODES mn where mn.required=true and mn.disabled=:disabled and mn.id not in (:nodes1) and mn.id in(select mdn.depNode.nodeId from IN_MDT_DEPENDENT_NODES mdn where mdn.depNode.dependentNodeId in (:nodes2)) and mn.id not in( select rv.viewPk.nodeId from RESULT_VIEW  rv  where rv.viewPk.periodId=:periodId and rv.viewPk.bankId=:bankId and rv.viewPk.versionCode=:versionCode)", MDTNode.class);

        List<MDTNode> mdtNodes = new ArrayList<>();

        for (List<Long> nodeList : partitions) {
            query.setParameter("versionCode", versionCode);
            query.setParameter("nodes1", nodeList);
            query.setParameter("nodes2", nodeList);
            query.setParameter("disabled", false);
            query.setParameter("bankId", schedule.getFi().getId());
            query.setParameter("periodId", ((Number) schedule.getPeriod().getId()).intValue());
            mdtNodes.addAll(query.getResultList());
        }

        return mdtNodes;
    }

    @Override

    public Map<Long, Long> gerReturnReturnDefinitionIds(List<Long> returnIds) {
        return returnLocal.gerReturnReturnDefinitionIds(returnIds);
    }

    @Override

    public Map<Long, Collection<ReturnDefinition>> loadReturnDefinitionDependencies() {
        return returnCacheManager.loadReturnDefinitionDependencies();
    }

    @Override

    public List<Long> loadDependentReturns(long fiId, long periodId, Collection<Long> returnDefinitions, long versionId) {
        Query loadReturnIdsQuery = em.createQuery("select r.id from IN_RETURNS as r where r.returnVersion.id=:versionId and r.schedule.id in(select s.id from IN_SCHEDULES s where s.period.id in(select p.id from IN_PERIODS as p where p.toDate=(select pe.toDate from IN_PERIODS as pe where pe.id=:periodId)) and s.fi.id=:fiId and s.returnDefinition.id in(:returnDefinitions) ) ", Long.class)
                .setParameter("periodId", periodId)
                .setParameter("fiId", fiId)
                .setParameter("returnDefinitions", returnDefinitions)
                .setParameter("versionId", versionId);
        return loadReturnIdsQuery.getResultList();
    }


    @Override
    public Map<Long, Long> loadDependentReturnIdDefinitionIdMap(long fiId, long periodId, Collection<Long> returnDefinitions, long versionId) {
        return em.createQuery("select r.id,r.schedule.returnDefinition.id from IN_RETURNS as r where r.returnVersion.id=:versionId and r.schedule.id in(select s.id from IN_SCHEDULES s where s.period.id in(select p.id from IN_PERIODS as p where p.toDate=(select pe.toDate from IN_PERIODS as pe where pe.id=:periodId)) and s.fi.id=:fiId and s.returnDefinition.id in(:returnDefinitions) ) ", Tuple.class)
                .setParameter("periodId", periodId)
                .setParameter("fiId", fiId)
                .setParameter("returnDefinitions", returnDefinitions)
                .setParameter("versionId", versionId)
                .getResultList().stream().collect(Collectors.toMap(
                        tuple -> ((Number) tuple.get(0)).longValue(),
                        tuple -> ((Number) tuple.get(1)).longValue()
                ));

    }

    @Override
    public Collection<Long> getReturnDefinitionDependencies(long returnDefinitionId) {
        return returnDefinitionLocal.getReturnDefinitionDependencies(returnDefinitionId);
    }

    @ExcludeClassInterceptors
    @Override
    public Map<Long, ProcessItem> loadReturnNodesValue(List<Long> returnIds, long versionId, List<MDTDependentNode> mdtDependentNodes, long langId, Map<Long, List<MDTNode>> allMdtNodesByParentId) {
        Map<Long, ProcessItem> nodes = new HashMap<>();

        boolean singleReturn = returnIds.size() == 1;

        String qlString = "select " +
                " new " +
                ProcessItem.class.getName() +
                "(" +
                "ri.returns.id, " +          //0
                "ri.mdtNode.id, " +          //1
                "ri.mdtNode.type," +         //2
                "ri.rowNumber, " +           //3
                "ri.value, " +               //4
                "dt.type, " +                //5
                "ri.tableId, " +             //6
                "ri.mdtNode.equation, " +    //7
                "ri.mdtNode.code, " +        //8
                "dt.evalType, " +            //9
                "ri.mdtNode.dataType, " +    //10
                "ri.id, " +                  //11
                "ri.nValue, " +              //12
                "ri.mdtNode.parentId, " +    //13
                "ri.mdtNode.evalMethod " +   //14
                ")" +
                " from IN_RETURN_ITEMS as ri, IN(ri.returns.schedule.returnDefinition.definitionTables) dt  where " +
                " ri.tableId=dt.id and ";


        if (singleReturn) {
            qlString += "ri.returns.id=:returnId " +
                    " and ri.returnVersion.id=:versionId " +
                    " and ri.mdtNode.disabled=false ";
        } else {
            qlString += "ri.returns.id in(:returnIds)" +
                    " and ri.returnVersion.id=:versionId " +
                    " and ri.mdtNode.disabled=false ";
        }

        List<ProcessItem> items;

        long startTime = System.currentTimeMillis();
        if (singleReturn) {
            items = em.createQuery(qlString, ProcessItem.class)
                    .setParameter("returnId", returnIds.get(0))
                    .setParameter("versionId", versionId)
                    .getResultList();
            log.info("loadReturnNodesValue Query exec time :" + (System.currentTimeMillis() - startTime));
        } else {
            items = em.createQuery(qlString, ProcessItem.class)
                    .setParameter("returnIds", returnIds)
                    .setParameter("versionId", versionId)
                    .getResultList();
        }

        startTime = System.currentTimeMillis();
        DependentProcessor dependentProcessor = new DependentProcessor(mdtDependentNodes);

        ProcessItemUtil util = new ProcessItemUtil();
        Map<String, String> nodeDEscriptionMap = new HashMap<>();
        for (ProcessItem item : items) {
            if (!nodeDEscriptionMap.containsKey(item.code)) {
                nodeDEscriptionMap.put(item.code, mdtCacheManager.getNode(item.code).getDescription().getDescription(langId));
            }

            ProcessItem temp = nodes.get(item.nodeId);
            if (nodes.get(item.nodeId) == null) {
                temp = item;

                if (mdtDependentNodes != null) {
                    temp.dependent = dependentProcessor.getNodeDependent(item.nodeId);
                }

                temp.versionId = versionId;

                temp.description = nodeDEscriptionMap.get(item.code);

                temp.code = temp.code.trim();

                nodes.put(temp.nodeId, temp);
            }

            temp.nValues.put((int) item.getRowNumber(), item.getnValue());
            temp.values.put((int) item.getRowNumber(), item.getValue());
            temp.idByRowNumber.put((int) item.getRowNumber(), item.getId());

            if (temp.nodeType == MDTNodeTypes.LIST) {
                List<ProcessItem> processItems = new ArrayList<>();
                util.loadListElementNodes(processItems, temp, allMdtNodesByParentId, langId);
                temp.listElementValues = processItems;
            }
        }
        log.info("loadReturnNodesValue Response process time : " + (System.currentTimeMillis() - startTime));
        return nodes;
    }

    @Override
    public Map<Long, List<ComparisonItem>> loadComparisons() {
        Map<Long, List<ComparisonItem>> comparisons = new HashMap<>();

        List<Object[]> result = em.createQuery("select c.node.id,c.condition,c.leftEquation, c.rightEquation,c.template,c.numberPattern,c.processStage from IN_MDT_COMPARISON c where c.node.disabled=false ", Object[].class).getResultList();

        for (Object[] objects : result) {

            ComparisonItem compItem = new ComparisonItem();

            compItem.nodeId = (long) objects[0];
            compItem.condition = (MDTComparisonConditions) objects[1];
            compItem.leftEquation = objects[2] != null ? objects[2].toString() : null;
            compItem.equation = objects[3] != null ? objects[3].toString() : null;
            compItem.messageTemplate = objects[4] != null ? objects[4].toString() : null;
            compItem.numberPattern = objects[5] != null ? objects[5].toString() : null;
            compItem.processStage = (ProcessStage) objects[6];

            List<ComparisonItem> nodeComparisons = comparisons.get(compItem.nodeId);

            if (comparisons.get(compItem.nodeId) == null) {
                nodeComparisons = new ArrayList<>();
                comparisons.put(compItem.nodeId, nodeComparisons);
            }

            nodeComparisons.add(compItem);

        }
        return comparisons;
    }

    @Override
    public void updateProcessItems(Collection<ProcessItem> values, long returnId, String processId, List<MDTDependentNode> mdtDependentNodes, final Map<Long, List<MDTNode>> allMdtNodesByParentId, final List<MDTComparison> comparisons) {
        try (StatisticsLogger statLog = new StatisticsLogger("Update Process Items");) {

            statLog.logStage("Delete Return Items return id : " + returnId);
            em.createQuery("delete from ReturnItemLite  ri where ri.returnId=:returnId").setParameter("returnId", returnId).executeUpdate();
            statLog.logStage("insert return items return id : " + returnId);

            //clear persistence context
            em.clear();

            for (ProcessItem item : values) {

                for (Map.Entry<Integer, String> entry : item.values.entrySet()) {

                    double nValue = ProcessingUtil.stringTodouble(entry.getValue());
                    if (Double.isNaN(nValue) || Double.isInfinite(nValue)) {
                        nValue = .0;
                    }

                    long id = 0;
                    Object idObject = item.idByRowNumber.get(entry.getKey());
                    if (idObject != null) {
                        id = (long) idObject;
                    } else {
                        throw new RuntimeException("id is null, row number - " + entry.getKey());
                    }

                    ReturnItemLite lite = new ReturnItemLite();
                    lite.setId(id);
                    lite.setReturnId(item.returnId);
                    lite.setNodeId(item.nodeId);
                    lite.setVersionId(item.versionId);
                    lite.setTableId(item.tableId);
                    lite.setRowNumber(entry.getKey());
                    lite.setValue(entry.getValue() == null ? " " : entry.getValue(), item.dataType);
                    lite.setnValue(nValue);

                    em.persist(lite);
                }
            }
//        statLog.logStage("flush return id : " + returnId);
//        em.flush();
            statLog.logMessage("Process Id : " + processId);
            if (processId != null) {

                ReturnItemsStoreEvent storeEvent = new ReturnItemsStoreEvent(values, null, returnId, processId, mdtDependentNodes, allMdtNodesByParentId, comparisons);
                returnStoreEvent.fire(storeEvent);
            }
        }
    }

    @Override
    public void updateProcessItems(Collection<ProcessItem> values, String processId) {
        Set<Long> returnIds = new HashSet<>();
        Query updateQuery = em.createQuery("UPDATE ReturnItemLite ri SET ri.value=:value, ri.nValue=:nValue WHERE ri.id=:id and ri.returnId=:returnId and ri.nodeId=:nodeId and ri.versionId=:versionId and ri.rowNumber=:rowNumber and ri.tableId=:tableId");
        for (ProcessItem item : values) {

            for (Map.Entry<Integer, String> entry : item.values.entrySet()) {

                double nValue = ProcessingUtil.stringTodouble(entry.getValue());

                if (Double.isNaN(nValue) || Double.isInfinite(nValue)) {
                    nValue = .0;
                }

                long id;
                Object idObject = item.idByRowNumber.get(entry.getKey());
                if (idObject != null) {
                    id = (long) idObject;
                } else {
                    throw new RuntimeException("id is null, row number - " + entry.getKey());
                }

                long rowNumber = entry.getKey();
                String value = entry.getValue() == null ? " " : entry.getValue();

                if (id == 0L) {

                    //Add new return item
                    ReturnItemLite itemLite = new ReturnItemLite();
                    itemLite.setId(rowNumber + 1);
                    itemLite.setRowNumber(rowNumber);

                    itemLite.setReturnId(item.returnId);
                    itemLite.setNodeId(item.nodeId);
                    itemLite.setVersionId(item.versionId);
                    itemLite.setTableId(item.tableId);

                    itemLite.setValue(value, item.dataType);
                    itemLite.setDataType(item.dataType);
                    itemLite.setnValue(nValue);

                    em.persist(itemLite);

                } else {
                    //Update existing
                    updateQuery.setParameter("id", id);
                    updateQuery.setParameter("value", value);
                    updateQuery.setParameter("nValue", nValue);
                    updateQuery.setParameter("returnId", item.returnId);
                    updateQuery.setParameter("nodeId", item.nodeId);
                    updateQuery.setParameter("versionId", item.versionId);
                    updateQuery.setParameter("rowNumber", (long) entry.getKey());
                    updateQuery.setParameter("tableId", item.tableId);
                    updateQuery.executeUpdate();
                }

                returnIds.add(item.returnId);
            }
        }

        if (processId != null) {
            final List<MDTDependentNode> allMdtDependentNodes = loadAllMdtDependentNodes();
            final List<MDTComparison> comparisons = mdtNodeLocal.loadComparisons(new HashMap<>(), null);
            Map<Long, List<MDTNode>> allMdtNodesByParentId = mdtNodeLocal.loadAllNodesByParentId();
            for (long returnId : returnIds) {
                ReturnItemsStoreEvent storeEvent = new ReturnItemsStoreEvent(values, null, returnId, processId, allMdtDependentNodes, allMdtNodesByParentId, comparisons);
                returnStoreEvent.fire(storeEvent);
            }
        }
    }

    @Override
    public List<MDTDependentNode> loadAllMdtDependentNodes() {
        return mdtNodeLocal.loadAllMdtDependentNodes();
    }

    @Override
    public Collection<Long> getUsedReturnDefinitions(long returnDefinitionId) {
        Set<Long> result = new HashSet<>();
        returnDefinitionLocal.getUsedReturnDefinitions(returnDefinitionId, result);
        return result;
    }

    @Override
    public long getVersionId(long returnId) {
        return em.createQuery("select r.returnVersion.id from IN_RETURNS  r where r.id=:returnId", Long.class).setParameter("returnId", returnId).getSingleResult();
    }

    @Override
    public long getScheduleId(long returnId) {
        return em.createQuery("select  r.schedule.id from IN_RETURNS r where r.id=:returnId", Long.class).setParameter("returnId", returnId).getSingleResult();
    }

    @Override
    public Schedule getSchedule(long returnId) {
        return em.createQuery("select  r.schedule from IN_RETURNS r where r.id=:returnId", Schedule.class).setParameter("returnId", returnId).getSingleResult();
    }

    @Override
    public String getVersionCode(long versionId) {
        return em.createQuery("select rv.code from IN_RETURN_VERSIONS rv where rv.id=:versionId", String.class).setParameter("versionId", versionId).getSingleResult().trim();
    }

    @Override
    public Map<Long, MDTNode> getMdtNodesById() {
        return mdtNodeLocal.getMdtNodesById();
    }

    @Override
    public Map<Long, Long> findStoredDependentReturns(List<Long> returnIds, long returnDefinitionId) {
        Map<Long, Long> result = new HashMap<>();
        if (!returnIds.isEmpty()) {
            List<Object[]> queryResult = em.createQuery("select r.id,r.schedule.returnDefinition.id from IN_RETURNS r where r.schedule.returnDefinition.id= (:returnDefinitionId) and r.schedule.period.id in(select distinct r1.schedule.period.id from IN_RETURNS r1 where r1.id in(:returnIds)) and r.schedule.fi.id in (select distinct r2.schedule.fi.id from IN_RETURNS r2 where r2.id in(:returnIds)) and r.returnVersion.id in (select distinct r3.returnVersion.id from IN_RETURNS r3 where r3.id in(:returnIds) )", Object[].class)
                    .setParameter("returnDefinitionId", returnDefinitionId)
                    .setParameter("returnIds", returnIds).getResultList();
            for (Object[] objects : queryResult) {
                result.put((Long) objects[1], (Long) objects[0]);
            }
        }
        return result;
    }

    @Override
    public double periodValue(String nodeCode, long fiId, long versionId, Date fromDate, Date toDate) {

        List<Object[]> queryResult = em.createQuery("select ri.rowNumber,ri.nValue from IN_RETURN_ITEMS ri where ri.mdtNode.code=:nodeCode and ri.returns.schedule.fi.id=:fId and ri.returns.returnVersion.id=:versionId and ri.returns.schedule.period.fromDate=:fromDate and ri.returns.schedule.period.toDate=:toDate", Object[].class)
                .setParameter("nodeCode", nodeCode)
                .setParameter("fId", fiId)
                .setParameter("versionId", versionId)
                .setParameter("fromDate", fromDate)
                .setParameter("toDate", toDate)
                .getResultList();

        double result = .0;

        for (Object[] objects : queryResult) {
            result += (double) objects[1];
        }

        return result;
    }

    @Override
    public void updateReturnDependencyTreeCache(Long returnId, List<Long> dependentReturnIds) {
        returnCacheManager.updateReturnDependencyTreeCache(returnId, dependentReturnIds);
    }

    @Override
    public List<Long> getReturnDependencyTreeByReturnId(long returnId) {
        return returnCacheManager.getReturnDependencyTreeByReturnId(returnId);
    }
}
