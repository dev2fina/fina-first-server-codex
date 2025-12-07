package net.fina.server.inputs.impl;

import net.fina.common.client.constants.ImportStatus;
import net.fina.common.client.dcs.UploadFileStatus;
import net.fina.common.client.dcs.UploadType;
import net.fina.common.client.returns.ImportedFileType;
import net.fina.server.dcs.uploadfile.entity.UploadFile;
import net.fina.server.fi.entity.Fi;
import net.fina.server.fi.entity.FiType;
import net.fina.server.inputs.api.InputManagerLocal;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.returns.entity.ImportedReturn;
import net.fina.server.returns.entity.Period;
import net.fina.server.returns.entity.ReturnStatus;
import net.fina.server.returns.entity.Schedule;

import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Stateless
@Local(InputManagerLocal.class)
@Interceptors(RecordingAuditor.class)
public class InputManagerSession implements InputManagerLocal {

    @Inject
    private EntityManager em;

    @Override
    public List<Fi> loadFis(long periodId, int delay, long fiTypeId, List<Long> fiIds, long userId) {
        String sql = "select distinct s.fi from IN_SCHEDULES s " +
                "where s.period.id = :periodId and s.delay = :delay " +
                "and s.fi.fiType.id = :fiTypeId and s.fi.id in (select ub.bankId from SYS_USER_BANKS ub " +
                "where ub.userId = :userId)";

        if (fiIds != null) {
            sql += "and s.fi.id in :fiIds ";
        }

        TypedQuery<Fi> query = em.createQuery(sql, Fi.class)
                .setParameter("periodId", periodId)
                .setParameter("delay", delay)
                .setParameter("fiTypeId", fiTypeId)
                .setParameter("userId", userId);

        if (fiIds != null) {
            query.setParameter("fiIds", fiIds);
        }

        return query.getResultList();
    }

    @Override
    public List<FiType> loadFiTypes(long periodId, int delay, int delayHour, int delayMinute, List<Long> fiTypeIds) {
        String sql = "select distinct s.fi.fiType from IN_SCHEDULES s " +
                "where s.period.id = :periodId and s.delay = :delay and s.delayHour = :delayHour and s.delayMinute =:delayMinute";

        if (fiTypeIds != null) {
            sql += "and s.fi.fiType.id in :fiTypeIds ";
        }

        TypedQuery<FiType> query = em.createQuery(sql, FiType.class)
                .setParameter("periodId", periodId)
                .setParameter("delayHour", delayHour)
                .setParameter("delayMinute", delayMinute)
                .setParameter("delay", delay);

        if (fiTypeIds != null) {
            query.setParameter("fiTypeIds", fiTypeIds);
        }

        return query.getResultList();
    }

    @Override
    public Fi getFiById(long fiId) {
        return em.find(Fi.class, fiId);
    }

    @Override
    public UploadFile getUploadedFileById(long fileId) {
        return em.find(UploadFile.class, fileId);
    }

    @Override
    public List<Schedule> loadDistinctSchedules(int start, int limit, Map<String, Object> filter) {
        List<Long> scheduleIds = em.createQuery("select min(s.id) from IN_SCHEDULES s " +
                "group by s.period.id, s.delay", Long.class).getResultList();

        String sql = "select ss from IN_SCHEDULES ss " +
                "where ss.id in :scheduleIds ";

        if (filter != null) {
            sql += filter.containsKey("periodFrom") ? "and ss.period.fromDate >= :fromDate " : "";
            sql += filter.containsKey("periodTo") ? "and ss.period.toDate <= :toDate " : "";
            sql += filter.containsKey("delay") ? "and ss.delay = :delay " : "";
            sql += filter.containsKey("periodType") ? "and ss.period.periodType.code = :periodType " : "";
        }

        TypedQuery<Schedule> query = em.createQuery(sql, Schedule.class);
        query.setParameter("scheduleIds", scheduleIds);

        if (filter != null) {
            if (filter.containsKey("periodFrom"))
                query.setParameter("fromDate", new Date((Long) filter.get("periodFrom")));
            if (filter.containsKey("periodTo")) query.setParameter("toDate", new Date((Long) filter.get("periodTo")));
            if (filter.containsKey("delay")) query.setParameter("delay", filter.get("delay"));
            if (filter.containsKey("periodType")) query.setParameter("periodType", filter.get("periodType"));
        }

        if (start > 0) {
            query.setFirstResult(start);
        }

        if (limit > 0) {
            query.setMaxResults(limit);
        }

        return query.getResultList();
    }

    @Override
    public Schedule getScheduleById(long scheduleId) {
        return em.find(Schedule.class, scheduleId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ImportedReturn> loadImportedReturns(long fileId, Map<String, Object> filter) {
        String sql = "select new net.fina.server.returns.entity.ImportedReturn(" +
                "r.id, r.returnCode, r.versionCode, r.importStart, r.importEnd, " +
                "r.periodStart, r.periodEnd, r.message, r.type, r.status) from IN_IMPORTED_RETURNS r " +
                "where r.uploadFile.id = :fileId ";

        if (filter != null) {
            sql += filter.containsKey("code") ? "and lower(r.returnCode) like :code " : "";
            sql += filter.containsKey("returnCodes") ? "and r.returnCode in :returnCodes " : "";
            sql += filter.containsKey("importStart") ? "and r.importStart >= :importStart " : "";
            sql += filter.containsKey("importEnd") ? "and r.importEnd <= :importEnd " : "";
            sql += filter.containsKey("importTypes") ? "and r.type in :importTypes " : "";
            sql += filter.containsKey("importStatuses") ? "and r.status in :importStatuses " : "";
            // TODO - filter by returnType
        }

        sql += "order by r.returnCode asc ";

        TypedQuery<ImportedReturn> query = em.createQuery(sql, ImportedReturn.class)
                .setParameter("fileId", fileId);

        if (filter != null) {
            if (filter.containsKey("code"))
                query.setParameter("code", "%" + ((String) filter.get("code")).toLowerCase() + "%");
            if (filter.containsKey("returnCodes")) query.setParameter("returnCodes", filter.get("returnCodes"));
            if (filter.containsKey("importStart"))
                query.setParameter("importStart", new Date((Long) filter.get("importStart")));
            if (filter.containsKey("importEnd"))
                query.setParameter("importEnd", new Date((Long) filter.get("importEnd")));
            if (filter.containsKey("importTypes")) query.setParameter("importTypes",
                    ((List<Integer>) filter.get("importTypes")).stream().map(integer ->
                            Arrays.stream(ImportedFileType.values()).filter(importType ->
                                    importType.ordinal() == integer).toArray()[0]).collect(Collectors.toList()));
            if (filter.containsKey("importStatuses")) query.setParameter("importStatuses",
                    ((List<Integer>) filter.get("importStatuses")).stream().map(integer ->
                            Arrays.stream(ImportStatus.values()).filter(importStatus ->
                                    importStatus.ordinal() == integer).toArray()[0]).collect(Collectors.toList()));
            // TODO - filter by returnType
        }

        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<UploadFile> loadUploadedFiles(String fiCode, Period period, Map<String, Object> filter) {
        String sql = "select new net.fina.server.dcs.uploadfile.entity.UploadFile(" +
                "f.id, f.user.id, f.user.login, f.bankCode, f.fileName, f.uploadedTime, " +
                "f.status, f.hasUserBank, f.nameValid, f.versionValid, f.matrixValid, " +
                "f.type, f.reason, f.protectioninfo) from SYS_UPLOADEDFILE f " +
                "where f.id in (select s.uploadFile.id from IN_IMPORTED_RETURNS s " +
                "where s.bankCode = :fiCode and s.periodStart = :periodStart " +
                "and s.periodEnd = :periodEnd) " +
                "and f.status <> :statusDeleted ";

        if (filter != null) {
            sql += filter.containsKey("name") ? "and lower(f.fileName) like :name " : "";
            sql += filter.containsKey("uploadFrom") ? "and f.uploadedTime >= :uploadFrom " : "";
            sql += filter.containsKey("uploadTo") ? "and f.uploadedTime <= :uploadTo " : "";
            sql += filter.containsKey("uploadTypes") ? "and f.type in :uploadTypes " : "";
            sql += filter.containsKey("uploadStatuses") ? "and f.status in :uploadStatuses " : "";
            sql += filter.containsKey("users") ? "and f.user.id in :users " : "";
        }

        sql += "order by f.uploadedTime desc ";

        TypedQuery<UploadFile> query = em.createQuery(sql, UploadFile.class)
                .setParameter("fiCode", fiCode)
                .setParameter("periodStart", period.getFromDate())
                .setParameter("periodEnd", period.getToDate())
                .setParameter("statusDeleted", String.valueOf(UploadFileStatus.DELETE.ordinal()));

        if (filter != null) {
            if (filter.containsKey("name"))
                query.setParameter("name", "%" + ((String) filter.get("name")).toLowerCase() + "%");
            if (filter.containsKey("uploadFrom"))
                query.setParameter("uploadFrom", new Date((Long) filter.get("uploadFrom")));
            if (filter.containsKey("uploadTo")) query.setParameter("uploadTo", new Date((Long) filter.get("uploadTo")));
            if (filter.containsKey("uploadTypes")) query.setParameter("uploadTypes",
                    ((List<Integer>) filter.get("uploadTypes")).stream().map(integer ->
                            Arrays.stream(UploadType.values()).filter(uploadType ->
                                    uploadType.ordinal() == integer).toArray()[0]).collect(Collectors.toList()));
            if (filter.containsKey("uploadStatuses")) query.setParameter("uploadStatuses",
                    ((List<Integer>) filter.get("uploadStatuses")).stream().map(String::valueOf).collect(Collectors.toList()));
            if (filter.containsKey("users")) query.setParameter("users",
                    ((List<Integer>) filter.get("users")).stream().map(Long::new).collect(Collectors.toList()));
        }

        return query.getResultList();
    }

    @Override
    public ReturnStatus getReturnByCode(long scheduleId, String returnCode) {
        return em.createQuery("select s from IN_RETURN_STATUSES s join s.returns r " +
                        "where r.schedule.id = :scheduleId and " +
                        "r.schedule.returnDefinition.code = :returnCode " +
                        "order by s.statusDate desc", ReturnStatus.class)
                .setParameter("scheduleId", scheduleId)
                .setParameter("returnCode", returnCode)
                .setMaxResults(1)
                .getSingleResult();
    }

    @Override
    public long countDistinctSchedules(Map<String, Object> filter) {
        List<Long> scheduleIds = em.createQuery("select min(s.id) from IN_SCHEDULES s " +
                "group by s.period.id, s.delay", Long.class).getResultList();

        String sql = "select count(ss) from IN_SCHEDULES ss " +
                "where ss.id in :scheduleIds ";

        if (filter != null) {
            sql += filter.containsKey("periodFrom") ? "and ss.period.fromDate >= :fromDate " : "";
            sql += filter.containsKey("periodTo") ? "and ss.period.toDate <= :toDate " : "";
            sql += filter.containsKey("delay") ? "and ss.delay = :delay " : "";
            sql += filter.containsKey("periodType") ? "and ss.period.periodType.code = :periodType " : "";
        }

        TypedQuery<Long> query = em.createQuery(sql, Long.class);
        query.setParameter("scheduleIds", scheduleIds);

        if (filter != null) {
            if (filter.containsKey("periodFrom"))
                query.setParameter("fromDate", new Date((Long) filter.get("periodFrom")));
            if (filter.containsKey("periodTo")) query.setParameter("toDate", new Date((Long) filter.get("periodTo")));
            if (filter.containsKey("delay")) query.setParameter("delay", filter.get("delay"));
            if (filter.containsKey("periodType")) query.setParameter("periodType", filter.get("periodType"));
        }

        return query.getSingleResult();
    }

    @Override
    public List<Fi> loadFisByIds(List<Long> idFilter) {
        return em.createQuery("select f from IN_BANKS f " +
                        "where f.id in :idFilter", Fi.class)
                .setParameter("idFilter", idFilter)
                .getResultList();
    }

    @Override
    public List<Object[]> loadFileUploadData(List<String> returnTypeCodes, Date periodStart, Date periodEnd,
                                             String periodTypeCode, List<Long> fiIds) {

        String sql = "select s.fi.id, s.period.id, max(s.delay), max(ir.uploadFile.uploadedTime), s.returnDefinition.returnType.code " +
                "from IN_IMPORTED_RETURNS ir, IN_SCHEDULES s " +
                "where s.period.fromDate = ir.periodStart and s.period.toDate = ir.periodEnd and s.fi.code = ir.bankCode " +
                "and s.returnDefinition.returnType.code in :returnTypeCodes and ir.uploadFile.status = :importedStatus ";

        sql += periodTypeCode != null && !periodTypeCode.isEmpty() ? "and s.period.periodType.code = :periodTypeCode " : "";
        sql += periodStart != null ? "and s.period.fromDate >= :periodStart " : "";
        sql += periodEnd != null ? "and s.period.toDate <= :periodEnd " : "";

        sql += "group by s.returnDefinition.returnType.code, s.fi.id, s.period.id ";
        sql += "order by s.returnDefinition.returnType.code, s.fi.id, s.period.id ";

        TypedQuery<Object[]> query = em.createQuery(sql, Object[].class)
                .setParameter("returnTypeCodes", returnTypeCodes)
                .setParameter("importedStatus", "" + UploadFileStatus.IMPORTED.ordinal());

        if (periodTypeCode != null && !periodTypeCode.isEmpty()) {
            query.setParameter("periodTypeCode", periodTypeCode);
        }
        if (periodStart != null) {
            query.setParameter("periodStart", periodStart);
        }
        if (periodEnd != null) {
            query.setParameter("periodEnd", periodEnd);
        }

        List<Object[]> result = query.getResultList();
        if (fiIds != null && !fiIds.isEmpty()) {
            result = result.stream().filter(obj -> fiIds.contains((Long) obj[0])).collect(Collectors.toList());
        }

        return result;
    }

}
