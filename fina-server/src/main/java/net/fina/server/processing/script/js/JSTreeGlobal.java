package net.fina.server.processing.script.js;

import fina2.period.PeriodPK;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.mdt.MDTNodeDataTypes;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.processing.helper.ProcessingGlobalHelper;
import net.fina.server.reg.util.RegJSTreeGlobal;
import net.fina.server.returns.entity.Period;
import net.fina.server.returns.entity.Schedule;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class JSTreeGlobal implements JSTreeBase {
    private final Logger logger = Logger.getLogger(this.getClass());
    private final EntityManager finEm;
    private final EntityManager regEm;
    private final ProcessingGlobalHelper processingGlobalHelper;
    private final long currentScheduleId;
    private final long returnVersionId;
    private final Function<String, MDTNode> getMdtNodeByCode;


    public JSTreeGlobal(EntityManager finEm, EntityManager regEm, ProcessingGlobalHelper processingGlobalHelper, Function<String, MDTNode> getMdtNodeByCode, long currentScheduleId, long returnVersionId) {
        this.finEm = finEm;
        this.regEm = regEm;
        this.getMdtNodeByCode = getMdtNodeByCode;
        this.processingGlobalHelper = processingGlobalHelper;
        this.currentScheduleId = currentScheduleId;
        this.returnVersionId = returnVersionId;
    }

    public double lookup(String nodeCode, String periodOffsetPattern) {
        double result = .0;
        try {

            if (isFinaNode(nodeCode)) {
                long scheduleId = calculateScheduleId(nodeCode, periodOffsetPattern);
                long returnId = calculateReturnId(scheduleId);
                MDTNode node = getMdtNodeByCode.apply(nodeCode);
                long nodeId = node != null ? node.getId() : -1;

                String query = "select sum(nvalue) from IN_RETURN_ITEMS where RETURNID=%d and VERSIONID=%d and NODEID=%d";

                Object queryResult = finEm.createNativeQuery(String.format(query, returnId, returnVersionId, nodeId)).getSingleResult();

                return queryResult == null ? 0.0 : ((Number) queryResult).doubleValue();

            } else {
                return getRegJsTree().lookup(nodeCode, periodOffsetPattern);
            }
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
        }


        return result;
    }

    public Set<Object> values(String nodeCode) {
        if (isFinaNode(nodeCode)) {
            return values(nodeCode, "0");
        } else {
            getRegJsTree().values(nodeCode);
        }
        return new HashSet<>();
    }

    public Set<Object> values(String nodeCode, String periodOffsetPattern) {
        try {
            if (isFinaNode(nodeCode)) {
                long scheduleId = calculateScheduleId(nodeCode, periodOffsetPattern);
                long returnId = calculateReturnId(scheduleId);
                MDTNode node = getMdtNodeByCode.apply(nodeCode);
                long nodeId = node != null ? node.getId() : -1;

                String query = "select nvalue from IN_RETURN_ITEMS where RETURNID=%d and VERSIONID=%d and NODEID=%d";

                List queryResult = finEm.createNativeQuery(String.format(query, returnId, returnVersionId, nodeId)).getResultList();

                return new HashSet<>(queryResult);

            } else {
                getRegJsTree().values(nodeCode, periodOffsetPattern);
            }

        } catch (Throwable ignore) {
        }
        return new HashSet<>();
    }

    public double sumifs(String sumNodeCode, String... args) {
        try {

            if (args.length <= 1) {
                return 0.0;
            }
            if ((args.length - 1) % 3 != 0) {
                throw new FinATypeException("Invalid criterion argument length provided in vctvaluesumifs formula!");
            }

            if (isFinaNode(sumNodeCode)) {
                String periodOffsetPattern = args[args.length - 1];
                long scheduleId = calculateScheduleId(sumNodeCode, periodOffsetPattern);
                long returnId = calculateReturnId(scheduleId);
                MDTNode node = getMdtNodeByCode.apply(sumNodeCode);
                long nodeId = node != null ? node.getId() : -1;

                StringBuilder qb = new StringBuilder();
                qb.append("select sum(nvalue) from IN_RETURN_ITEMS")
                        .append(" where RETURNID=")
                        .append(returnId)
                        .append(" and VERSIONID=")
                        .append(returnVersionId)
                        .append(" and NODEID=")
                        .append(nodeId);

                List<String> subQueries = new ArrayList<>();

                for (int i = 0; i < args.length - 1; i = i + 3) {
                    String criterionNodeCode = args[i].trim();
                    MDTNode mdtNode = getMdtNodeByCode.apply(criterionNodeCode);
                    if (mdtNode == null) {
                        continue;
                    }

                    String criterionOperation = args[i + 1].trim();
                    String criterionValue = args[i + 2].trim();
                    String columnName = mdtNode.getDataType().equals(MDTNodeDataTypes.TEXT) ? "value" : "nvalue";

                    if (mdtNode.getDataType().equals(MDTNodeDataTypes.TEXT)) {
                        criterionValue = "'" + criterionValue + "'";
                    }

                    String subQuery = "select ID from IN_RETURN_ITEMS where NODEID=%d and VERSIONID=%d and RETURNID=%d and %s%s%s";
                    String s = String.format(subQuery, mdtNode.getId(), returnVersionId, returnId, columnName, criterionOperation, criterionValue);
                    subQueries.add(s);
                }

                for (String subQuery : subQueries) {
                    qb.append(" and ID in(")
                            .append(subQuery)
                            .append(")");
                }
                Query query = finEm.createNativeQuery(qb.toString());
                Object result = query.getSingleResult();

                return result != null ? ((Number) result).doubleValue() : 0.0;

            } else {
                return getRegJsTree().sumifs(sumNodeCode, args);
            }
        } catch (Throwable ignore) {
        }

        return 0.0;


    }

    private boolean isFinaNode(String nodeCode) {
        return !processingGlobalHelper.getRegMdtCodes().contains(nodeCode);
    }

    private long calculateScheduleId(String nodeCode, String periodOffsetPattern) {
        Schedule schedule = processingGlobalHelper.getScheduleIdMap().get(currentScheduleId);
        if (schedule == null) {
            return -1;
        }
        Period period = schedule.getPeriod();
        PeriodPK periodPk = processingGlobalHelper.calcOffset(period, periodOffsetPattern);

        Schedule sch = processingGlobalHelper.getUniqueScheduleMap().get(String.format("%d%d%s", periodPk.getId(), schedule.getFi().getId(), processingGlobalHelper.getMdtCodeReturnCodeMap().get(nodeCode)));

        return sch == null ? -1 : sch.getId();
    }

    private long calculateReturnId(long scheduleId) {
        return processingGlobalHelper.getScheduleVersionReturnMap().getOrDefault(scheduleId + "-" + returnVersionId, -1L);
    }

    private RegJSTreeGlobal getRegJsTree() {
        return new RegJSTreeGlobal(currentScheduleId, returnVersionId, getMdtNodeByCode, processingGlobalHelper, regEm, finEm);
    }

}
