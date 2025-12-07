package net.fina.server.reg.util;

import fina2.period.PeriodPK;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import net.fina.common.client.exception.FinATypeException;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.processing.helper.ProcessingGlobalHelper;
import net.fina.server.processing.script.js.JSTreeBase;
import net.fina.server.processing.script.js.JSTreeGlobal;
import net.fina.server.returns.entity.Period;
import net.fina.server.returns.entity.Schedule;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class RegJSTreeGlobal implements JSTreeBase {
    private final Logger log = Logger.getLogger(RegJSTreeGlobal.class);
    private final long currentScheduleId;
    private final long returnVersionId;
    private final EntityManager regEm;
    private final EntityManager finaEm;
    private final ProcessingGlobalHelper processingGlobalHelper;
    private final Function<String, MDTNode> getMdtNodeByCode;

    public RegJSTreeGlobal(long currentScheduleId, long returnVersionId, Function<String, MDTNode> getMdtNodeByCode, ProcessingGlobalHelper processingGlobalHelper, EntityManager regEm, EntityManager finaEm) {
        this.currentScheduleId = currentScheduleId;
        this.returnVersionId = returnVersionId;
        this.getMdtNodeByCode = getMdtNodeByCode;
        this.processingGlobalHelper = processingGlobalHelper;
        this.regEm = regEm;
        this.finaEm = finaEm;
    }


    /* String mdt Node Code
     * Period Offset Pattern is either 0 or periodOffsetNumber + one of the periodType codes [d,m,w,s,a,y,c] i.e 5d means period offset - 5 day
     * */
    public double lookup(String nodeCode, String periodOffsetPattern) {
        try {

            StringBuilder qb = new StringBuilder();
            if (isRegNode(nodeCode)) {
                qb.append("select sum(").append(nodeCode).append(") from ").append(processingGlobalHelper.getRegMdtCodeTableNameMap().get(nodeCode)).append(" where ");


                long scheduleId = calculateScheduleId(nodeCode, periodOffsetPattern);

                qb.append(" schedule_id=").append(scheduleId);


            } else {
                return getFinaJsTree().lookup(nodeCode, periodOffsetPattern);
            }
            Object result = regEm.createNativeQuery(qb.toString()).getSingleResult();

            return result == null ? 0.0 : ((Number) result).doubleValue();
        } catch (Throwable ignore) {
        }

        return 0;
    }

    private long calculateScheduleId(String nodeCode, String periodOffsetPattern) {
        Schedule schedule = processingGlobalHelper.getScheduleIdMap().get(currentScheduleId);
        Period period = schedule.getPeriod();
        PeriodPK periodPk = processingGlobalHelper.calcOffset(period, periodOffsetPattern);

        Schedule sch = processingGlobalHelper.getUniqueScheduleMap().get(String.format("%d%d%s", periodPk.getId(), schedule.getFi().getId(), processingGlobalHelper.getMdtCodeReturnCodeMap().get(nodeCode)));

        return sch == null ? -1 : sch.getId();
    }

    public Set<Object> values(String nodeCode) {
        if (isRegNode(nodeCode)) {

            StringBuilder qb = new StringBuilder();
            qb.append("select ").append(nodeCode).append(" from ").append(processingGlobalHelper.getRegMdtCodeTableNameMap().get(nodeCode)).append(" where schedule_id =")
                    .append(currentScheduleId);

            List result = regEm.createNativeQuery(qb.toString()).getResultList();
            return new HashSet<>(result);
        } else {
            getFinaJsTree().values(nodeCode);
        }

        return new HashSet<>();
    }

    public Set<Object> values(String nodeCode, String periodOffsetPattern) {
        try {

            StringBuilder qb = new StringBuilder();
            if (isRegNode(nodeCode)) {
                long scheduleId = calculateScheduleId(nodeCode, periodOffsetPattern);

                qb.append("select ").append(nodeCode).append(" from ").append(processingGlobalHelper.getRegMdtCodeTableNameMap().get(nodeCode)).append(" where schedule_id=").append(scheduleId);

                List result = regEm.createNativeQuery(qb.toString()).getResultList();
                return new HashSet<>(result);
            } else {
                return getFinaJsTree().values(nodeCode, periodOffsetPattern);
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

            String tableName = processingGlobalHelper.getRegMdtCodeTableNameMap().get(sumNodeCode);

            if (isRegNode(sumNodeCode)) {
                String periodOffsetPattern = args[args.length - 1];
                long scheduleId = calculateScheduleId(sumNodeCode, periodOffsetPattern);

                StringBuilder qb = new StringBuilder();
                qb.append("select sum(").append(sumNodeCode).append(") from ").append(tableName).append(" where schedule_id=").append(scheduleId);

                List<Object> criterionValues = new ArrayList<>();

                for (int i = 0; i < args.length - 1; i = i + 3) {
                    String criterionNodeCode = args[i].trim();
                    String criterionOperation = args[i + 1].trim();
                    String criterionValue = args[i + 2].trim();
                    criterionValues.add(criterionValue);

                    qb.append(" and ")
                            .append(criterionNodeCode)
                            .append(criterionOperation)
                            .append("?");

                }

                Query query = regEm.createNativeQuery(qb.toString());
                for (int i = 0; i < criterionValues.size(); i++) {
                    query.setParameter(i + 1, criterionValues.get(i));
                }
                Object result = query.getSingleResult();

                return result != null ? ((Number) result).doubleValue() : 0.0;
            } else {
                return getFinaJsTree().sumifs(sumNodeCode, args);
            }
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }

        return 0.0;
    }

    private boolean isRegNode(String nodeCode) {
        return processingGlobalHelper.getRegMdtCodes().contains(nodeCode);
    }


    private JSTreeGlobal getFinaJsTree() {
        return new JSTreeGlobal(finaEm, regEm, processingGlobalHelper, getMdtNodeByCode, currentScheduleId, returnVersionId);
    }
}
