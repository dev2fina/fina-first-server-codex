package net.fina.server.returns.impl;

import jakarta.annotation.Resource;
import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionManagement;
import jakarta.ejb.TransactionManagementType;
import net.fina.common.client.exception.FinATypeException;
import net.fina.server.fi.entity.Fi;
import net.fina.server.returns.api.ScheduleBatchLocal;
import net.fina.server.returns.entity.Period;
import net.fina.server.returns.entity.ReturnDefinition;
import net.fina.server.returns.entity.Schedule;
import net.fina.server.returns.entity.ScheduleParameter;
import net.fina.server.util.DBUtil;
import org.jboss.logging.Logger;
import org.joda.time.DateTime;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Stateless
@Local(ScheduleBatchLocal.class)
@TransactionManagement(TransactionManagementType.BEAN)
public class ScheduleBatchSession implements ScheduleBatchLocal {
    private final Logger log = Logger.getLogger(getClass());

    @Resource(mappedName = "java:jboss/datasources/FinADS")
    private DataSource dataSource;

    @Override
    public void saveSchedules(List<ScheduleParameter> sublist, List<ScheduleParameter> parameters, AtomicInteger progress, Map<Long, Fi> fiMap, Map<Long, Period> periodMap, Map<Long, ReturnDefinition> returnDefinitionHashMap, List<Schedule> notSaved, String currentUser, Set<String> existingKeys) {
        String sqlServerInsert = "INSERT INTO IN_SCHEDULES (ID,BANKID,DEFINITIONID,PERIODID,DELAY,COMMENT,DELAY_HOUR,DELAY_MINUTE,DELAY_TO_DATE) " +
                "VALUES (NEXT VALUE FOR in_schedules_sequence,?,?,?,?,?,?,?,?)";
        String oracleServerInsert = "INSERT INTO IN_SCHEDULES (ID,BANKID,DEFINITIONID,PERIODID,\"DELAY\",\"COMMENT\",DELAY_HOUR,DELAY_MINUTE,DELAY_TO_DATE) VALUES (in_schedules_sequence.NEXTVAL,?,?,?,?,?,?,?,?)";

        String sql = DBUtil.get().isMsSql() ? sqlServerInsert : oracleServerInsert;

        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql);) {
            conn.setAutoCommit(false);


            int batchSize = 5000;
            int count = 0;


            for (ScheduleParameter schedule : sublist) {
                try {
                    int pr = progress.getAndIncrement();
                    if (schedule.getDelay() == null || schedule.getDelay() < 0 || schedule.getDelayHour() == null || schedule.getDelayHour() < 0 || schedule.getDelayHour() > 23
                            || schedule.getDelayMinute() == null || schedule.getDelayMinute() < 0 || schedule.getDelayMinute() > 59
                            || (schedule.getDelay() < 1 && (schedule.getDelayHour() == null || schedule.getDelayHour() < 1) && (schedule.getDelayMinute() == null || schedule.getDelayMinute() < 1))) {
                        throw new FinATypeException(FinATypeException.Type.INVALID_DUE_DATE);
                    } else if (existingKeys.contains(schedule.getFiId() + "-" + schedule.getReturnDefinitionId() + "-" + schedule.getPeriodId())) {
                        throw new FinATypeException(FinATypeException.Type.SCHEDULE_UNIQUE_ERROR);
                    }
                    ps.setLong(1, schedule.getFiId());
                    ps.setLong(2, schedule.getReturnDefinitionId());
                    ps.setLong(3, schedule.getPeriodId());
                    ps.setInt(4, schedule.getDelay());
                    ps.setString(5, schedule.getComment());
                    ps.setInt(6, schedule.getDelayHour());
                    ps.setInt(7, schedule.getDelayMinute());
                    Period period = periodMap.get(schedule.getPeriodId());
                    java.util.Date utilDate = generateDelayToDate(period, schedule.getDelay(), schedule.getDelayHour(), schedule.getDelayMinute());
                    java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(utilDate.getTime());
                    ps.setTimestamp(8, sqlTimestamp);

                    ps.addBatch();

                    if (++count % batchSize == 0) {
                        ps.executeBatch();
                        conn.commit();
                        ps.clearBatch();
                    }

                    if (pr % 10_000 == 0) {
                        log.info(pr + " of " + parameters.size() + " Schedule Created ");
                    }


                } catch (Throwable ex) {
                    Schedule notSavedSchedule = getScheduleFromParameter(schedule, fiMap.get(schedule.getFiId()), periodMap.get(schedule.getPeriodId()), returnDefinitionHashMap.get(schedule.getReturnDefinitionId()));
                    notSaved.add(notSavedSchedule);
                    log.error(ex.getMessage() + "[fiId : " + schedule.getFiId() + ", RdId : " + schedule.getReturnDefinitionId() + ", PeriodID : " + schedule.getPeriodId() + "]");
                }
            }

            ps.executeBatch();
            conn.commit();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private Schedule getScheduleFromParameter(ScheduleParameter schedule, Fi fi, Period period, ReturnDefinition returnDefinition) {
        Schedule entity = new Schedule();
        entity.setId(schedule.getId());
        entity.setVersion(schedule.getVersion());
        entity.setDelay(schedule.getDelay());
        entity.setDelayHour(schedule.getDelayHour());
        entity.setDelayMinute(schedule.getDelayMinute());
        entity.setFi(fi);
        entity.setPeriod(period);
        entity.setReturnDefinition(returnDefinition);
        entity.setComment(schedule.getComment());
        entity.setDelayToDate(generateDelayToDate(entity));
        return entity;
    }

    private Date generateDelayToDate(Schedule entity) {
        if (entity.getPeriod() == null) {
            return null;
        }
        DateTime dateTime = new DateTime(entity.getPeriod().getToDate());

        dateTime = dateTime.
                plusDays(entity.getDelay()).
                plusHours(entity.getDelayHour()).
                plusMinutes(entity.getDelayMinute());

        return dateTime.toDate();
    }

    private Date generateDelayToDate(Period period, int delau, int delayHour, int delauMinute) {

        DateTime dateTime = new DateTime(period.getToDate());

        dateTime = dateTime.
                plusDays(delau).
                plusHours(delayHour).
                plusMinutes(delauMinute);

        return dateTime.toDate();
    }
}
