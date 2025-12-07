package net.fina.server.returns.impl;

import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import net.fina.common.client.constants.ImportStatus;
import net.fina.common.client.filter.ImportFilter;
import net.fina.common.client.returns.ImportModel;
import net.fina.server.dcs.uploadfile.entity.UploadFile;
import net.fina.server.dcs.uploadfile.entity.UploadFile_;
import net.fina.server.fi.api.FiLocal;
import net.fina.server.fi.entity.Fi;
import net.fina.server.interceptors.LogDescription;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.jcr.impl.FileContentManagementSession;
import net.fina.server.returns.api.ImportLocal;
import net.fina.server.returns.entity.ImportedReturn;
import net.fina.server.returns.entity.ImportedReturnGroup;
import net.fina.server.returns.entity.ImportedReturnGroup_;
import net.fina.server.returns.entity.ImportedReturn_;
import net.fina.server.security.api.UserLocal;
import net.fina.server.security.entity.User_;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
@Local(ImportLocal.class)
@Interceptors(RecordingAuditor.class)
public class ImportSession implements ImportLocal {

    @Inject
    private EntityManager em;

    @EJB
    private UserLocal userLocal;
    @EJB
    private FiLocal fiLocal;
    @EJB
    private FileContentManagementSession fileContentManagementSession;

    /*
    * TODO use this method only for status and import time filtering
    * */
    @Override
    @Deprecated
    public List<ImportModel> loadPackages(Map<ImportFilter, Object> filterMap, List<String> fiCodes) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ImportModel> query = cb.createQuery(ImportModel.class);
        Root<ImportedReturn> root = query.from(ImportedReturn.class);
        Join<ImportedReturn, UploadFile> join = root.join(ImportedReturn_.uploadFile, JoinType.LEFT);

        Selection[] selections = new Selection[]{
                root.get(ImportedReturn_.bankCode),
                root.get(ImportedReturn_.versionCode),
                root.get(ImportedReturn_.periodStart),
                root.get(ImportedReturn_.periodEnd),
                join.get(UploadFile_.fileName),
                join.get(UploadFile_.id),
                root.get(ImportedReturn_.user).get(User_.login),
        };

        query.select(cb.construct(ImportModel.class, selections));

        List<Expression<?>> groupings = new ArrayList<>();

        groupings.add(root.get(ImportedReturn_.bankCode));
        groupings.add(root.get(ImportedReturn_.periodStart));
        groupings.add(root.get(ImportedReturn_.periodEnd));
        groupings.add(root.get(ImportedReturn_.versionCode));
        groupings.add(join.get(UploadFile_.fileName));
        groupings.add(join.get(UploadFile_.id));
        groupings.add(root.get(ImportedReturn_.user).get(User_.login));

        query.groupBy(groupings);

        query.orderBy(cb.desc(root.get(ImportedReturn_.periodEnd)));


        List<Predicate> predicates = getFilterPredicates(cb, root, join, filterMap);
        List<Predicate> fiPredicates = new ArrayList<>();
        for (String fiCode : fiCodes) {
            fiPredicates.add(cb.or(cb.like(root.get(ImportedReturn_.bankCode), fiCode.trim())));
        }

        query.where(predicates.toArray(new Predicate[0]));

        if (!fiPredicates.isEmpty()) {
            query.where(
                    cb.or(fiPredicates.toArray(new Predicate[0]))
            );
        }

        int limit = (int) filterMap.getOrDefault(ImportFilter.LIMIT, 0);
        int offset = (int) filterMap.getOrDefault(ImportFilter.OFFSET, 0);

        TypedQuery<ImportModel> loadQuery = em.createQuery(query);
        if (offset >= 0) {
            loadQuery.setFirstResult(offset);
        }
        if (limit > 0) {
            loadQuery.setMaxResults(limit);
        }

        List<ImportModel> result = loadQuery.getResultList();

        Map<Long, Date> fileUploadTimes = getFileUploadTimes(result);

        List<String> fileNames = result.stream().map(ImportModel::getFileName).collect(Collectors.toList());
        Map<String, Date> latestUploadTimes = getLatestUploadTimesForFileNames(fileNames);
        for (ImportModel m : result) {
            Date uploadTime = fileUploadTimes.get(m.getFileId());
            m.setUploadTime(uploadTime);
            if (uploadTime != null) {
                Date latestUploadTime = latestUploadTimes.get(m.getFileName());
                if (latestUploadTime != null) {
                    m.setLatest(uploadTime.compareTo(latestUploadTime) == 0);
                }
            }
        }

        return result;
    }

    @Override
    public List<ImportModel> loadPackagesV2(Map<ImportFilter, Object> filterMap, List<String> fiCodes) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ImportModel> query = cb.createQuery(ImportModel.class);
        Root<ImportedReturnGroup> root = query.from(ImportedReturnGroup.class);
        Join<ImportedReturnGroup, UploadFile> join = root.join(ImportedReturnGroup_.uploadFile, JoinType.LEFT);

        Selection[] selections = new Selection[]{
                root.get(ImportedReturnGroup_.bankCode),
                root.get(ImportedReturnGroup_.versionCode),
                root.get(ImportedReturnGroup_.periodStart),
                root.get(ImportedReturnGroup_.periodEnd),
                join.get(UploadFile_.fileName),
                join.get(UploadFile_.id),
                join.get(UploadFile_.uploadedTime),
                root.get(ImportedReturnGroup_.user).get(User_.login),
        };

        query.select(cb.construct(ImportModel.class, selections));

        List<Expression<?>> groupings = new ArrayList<>();


        query.groupBy(groupings);

        query.orderBy(cb.desc(root.get(ImportedReturnGroup_.periodEnd)));


        List<Predicate> predicates = getGroupFilterPredicates(cb, root, join, filterMap);
        List<Predicate> fiPredicates = new ArrayList<>();
        for (String fiCode : fiCodes) {
            fiPredicates.add(cb.or(cb.like(root.get(ImportedReturnGroup_.bankCode), fiCode.trim())));
        }

        query.where(predicates.toArray(new Predicate[0]));

        if (!fiPredicates.isEmpty()) {
            query.where(
                    cb.or(fiPredicates.toArray(new Predicate[0]))
            );
        }

        int limit = (int) filterMap.getOrDefault(ImportFilter.LIMIT, 0);
        int offset = (int) filterMap.getOrDefault(ImportFilter.OFFSET, 0);

        TypedQuery<ImportModel> loadQuery = em.createQuery(query);
        if (offset >= 0) {
            loadQuery.setFirstResult(offset);
        }
        if (limit > 0) {
            loadQuery.setMaxResults(limit);
        }

        return loadQuery.getResultList();
    }

    @Override
    public List<ImportModel> load(Map<ImportFilter, Object> filterMap) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ImportModel> query = cb.createQuery(ImportModel.class);
        Root<ImportedReturn> root = query.from(ImportedReturn.class);
        Join<ImportedReturn, UploadFile> join = root.join(ImportedReturn_.uploadFile, JoinType.LEFT);

        Selection[] selections = new Selection[]{
                root.get(ImportedReturn_.id),
                root.get(ImportedReturn_.returnCode),
                root.get(ImportedReturn_.bankCode),
                root.get(ImportedReturn_.versionCode),
                root.get(ImportedReturn_.periodStart),
                root.get(ImportedReturn_.periodEnd),
                root.get(ImportedReturn_.user).get(User_.login),
                root.get(ImportedReturn_.user).get(User_.id),
                root.get(ImportedReturn_.uploadTime),
                root.get(ImportedReturn_.importStart),
                root.get(ImportedReturn_.importEnd),
                root.get(ImportedReturn_.status),
                root.get(ImportedReturn_.type),
                root.get(ImportedReturn_.message),
                join.get(UploadFile_.fileName),
                join.get(UploadFile_.id)
        };

        query.select(cb.construct(ImportModel.class, selections));

        query.orderBy(cb.desc(root.get(ImportedReturn_.periodEnd)));

        List<Predicate> predicates = getFilterPredicates(cb, root, join, filterMap);
        if (!predicates.isEmpty()) {
            query.where(predicates.toArray(new Predicate[0]));
        }

        int limit = 0;
        int offset = 0;

        if (filterMap != null) {
            for (Map.Entry<ImportFilter, Object> entry : filterMap.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }
                switch (entry.getKey()) {
                    case OFFSET:
                        offset = (int) entry.getValue();
                        break;
                    case LIMIT:
                        limit = (int) entry.getValue();
                        break;
                }
            }
        }

        TypedQuery<ImportModel> loadQuery = em.createQuery(query);
        if (offset > 0) {
            loadQuery.setFirstResult(offset);
        }
        if (limit > 0) {
            loadQuery.setMaxResults(limit);
        }

        return loadQuery.getResultList();
    }


    @Override

    public long count(Map<ImportFilter, Object> filterMap, boolean countPackages, List<String> fiCodes) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ImportModel> query = cb.createQuery(ImportModel.class);
        Root<ImportedReturn> root = query.from(ImportedReturn.class);
        Join<ImportedReturn, UploadFile> join = root.join(ImportedReturn_.uploadFile, JoinType.LEFT);

        Selection[] selections = new Selection[]{
                root.get(ImportedReturn_.bankCode),
                root.get(ImportedReturn_.versionCode),
                root.get(ImportedReturn_.periodStart),
                root.get(ImportedReturn_.periodEnd),
                join.get(UploadFile_.fileName),
                join.get(UploadFile_.id),
                root.get(ImportedReturn_.user).get(User_.login)
        };

        query.select(cb.construct(ImportModel.class, selections));

        List<Expression<?>> groupings = new ArrayList<>();

        groupings.add(root.get(ImportedReturn_.bankCode));
        groupings.add(root.get(ImportedReturn_.periodStart));
        groupings.add(root.get(ImportedReturn_.periodEnd));
        groupings.add(root.get(ImportedReturn_.versionCode));
        groupings.add(join.get(UploadFile_.fileName));
        groupings.add(join.get(UploadFile_.id));
        groupings.add(root.get(ImportedReturn_.user).get(User_.login));

        query.groupBy(groupings);

        query.orderBy(cb.desc(root.get(ImportedReturn_.periodEnd)));

        List<Predicate> predicates = getFilterPredicates(cb, root, join, filterMap);
        List<Predicate> fiPredicates = new ArrayList<>();
        for (String s : fiCodes) {
            fiPredicates.add(cb.or(cb.like(root.get(ImportedReturn_.bankCode), s)));
        }
        query.where(predicates.toArray(new Predicate[0]));

        if (!fiPredicates.isEmpty()) {
            query.where(
                    cb.or(fiPredicates.toArray(new Predicate[0]))
            );
        }

        return em.createQuery(query).getResultList().size();
    }

    @Override

    public String getImportMessage(long importedReturnId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> query = cb.createQuery(String.class);
        Root<ImportedReturn> root = query.from(ImportedReturn.class);
        query.select(root.get(ImportedReturn_.message)).where(cb.equal(root.get(ImportedReturn_.id), importedReturnId));
        return em.createQuery(query).getSingleResult();
    }

    @Override

    public void saveXmls(List<ImportedReturn> xmls) {
        for (ImportedReturn xml : xmls) {
            try {
                fileContentManagementSession.saveImportedReturn(xml);
            } catch (Throwable t) {
                em.persist(xml);
            }
        }
        //save Imported Return Group
        if (!xmls.isEmpty()) {
            ImportedReturnGroup group = new ImportedReturnGroup(xmls.get(0));
            em.persist(group);
        }

    }

    @Override

    public void reImport(int importedReturnId) {
        //TODO move xml file to queue
//        xmlImportLocal.processImportedReturn(importedReturnId);
    }

    @Override

    @Deprecated
    public void deleteReturn(int importedReturnId) {
        /*
        em.createQuery("DELETE FROM IN_IMPORTED_RETURNS ir WHERE ir.id=:irId")
                .setParameter("irId", importedReturnId)
                .executeUpdate();
        */
    }

    @Override

    @Deprecated
    public void deleteReturns(String fiCode, String versionCode, Date periodStart, Date periodEnd, Date uploadTime,
                              long uploadFileId, String uploadFileName) {
        /*
        em.createQuery("DELETE FROM IN_IMPORTED_RETURNS ir WHERE ir.bankCode=:fiCode and ir.versionCode=:versionCode and ir.periodStart=:periodStart and ir.periodEnd=:periodEnd and ir.status<>:status")
                .setParameter("fiCode", fiCode)
                .setParameter("versionCode", versionCode)
                .setParameter("periodStart", periodStart)
                .setParameter("periodEnd", periodEnd)
                .setParameter("status", ImportStatus.IMPORTED)
                .executeUpdate();
        */
    }


    @Override
    @LogDescription(ignore = true)
    public String getReturnXML(int importId) {
        ImportedReturn importedReturn = em.find(ImportedReturn.class, importId);
        byte[] content = fileContentManagementSession.loadImportedReturnContent(importedReturn);
        return content != null ? new String(content, StandardCharsets.UTF_8) : "";
    }


    @Override
    public Map<ImportStatus, Integer> loadImportStatusStatistic(Date fromDate, Date toDate) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ImportStatus> query = cb.createQuery(ImportStatus.class);
        Root<ImportedReturn> root = query.from(ImportedReturn.class);

        query.select(root.get(ImportedReturn_.status));

        List<Predicate> predicates = new ArrayList<>();
        if (fromDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get(ImportedReturn_.importStart), fromDate));
        }
        if (toDate != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get(ImportedReturn_.importEnd), toDate));
        }

        if (!predicates.isEmpty()) {
            query.where(predicates.toArray(new Predicate[0]));
        }

        List<ImportStatus> statusList = em.createQuery(query).getResultList();

        Map<ImportStatus, Integer> statisticMap = new HashMap<>();
        for (ImportStatus status : ImportStatus.values()) {
            statisticMap.put(status, 0);
        }

        for (ImportStatus status : statusList) {
            statisticMap.put(status, statisticMap.get(status) + 1);
        }

        return statisticMap;
    }

    @Override
    public ImportedReturn findById(int id) {
        ImportedReturn importedReturn = em.find(ImportedReturn.class, id);

        //Check Permission
        Fi fi = fiLocal.findFiByCode(importedReturn.getBankCode());
        if (userLocal.getCallerPrincipal().getFis().contains(fi.getId())) {
            return importedReturn;
        }
        return null;
    }

    private List<Predicate> getFilterPredicates(CriteriaBuilder cb, Root<ImportedReturn> root, Join<ImportedReturn, UploadFile> join, Map<ImportFilter, Object> filterMap) {
        List<Predicate> predicates = new ArrayList<>();
        if (filterMap != null) {
            for (Map.Entry<ImportFilter, Object> entry : filterMap.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }
                switch (entry.getKey()) {
                    case TYPE:
                        predicates.add(cb.equal(root.get(ImportedReturn_.type), entry.getValue()));
                        break;
                    case ID:
                        predicates.add(cb.equal(root.get(ImportedReturn_.id), entry.getValue()));
                        break;
                    case RETURN_CODE:
                        predicates.add(cb.like(root.get(ImportedReturn_.returnCode), entry.getValue().toString()));
                        break;
                    case FI_CODE:
                        predicates.add(cb.equal(root.get(ImportedReturn_.bankCode), entry.getValue().toString().trim()));
                        break;
                    case VERSION_CODE:
                        predicates.add(cb.like(root.get(ImportedReturn_.versionCode), entry.getValue().toString()));
                        break;
                    case PERIOD_START:
                        predicates.add(cb.greaterThanOrEqualTo(root.get(ImportedReturn_.periodStart), (Date) entry.getValue()));
                        break;
                    case PERIOD_END:
                        predicates.add(cb.lessThanOrEqualTo(root.get(ImportedReturn_.periodEnd), (Date) entry.getValue()));
                        break;
                    case USER_LOGIN:
                        predicates.add(cb.equal(root.get(ImportedReturn_.user).get(User_.login), entry.getValue().toString()));
                        break;
                    case USER_ID:
                        predicates.add(cb.equal(root.get(ImportedReturn_.user).get(User_.id), entry.getValue()));
                        break;
                    case UPLOADED_AFTER, IMPORT_START:
                        predicates.add(cb.greaterThanOrEqualTo(root.get(ImportedReturn_.uploadTime), (Date) entry.getValue()));
                        break;
                    case UPLOADED_BEFORE, IMPORT_END:
                        predicates.add(cb.lessThanOrEqualTo(root.get(ImportedReturn_.uploadTime), (Date) entry.getValue()));
                        break;
                    case UPLOAD_TIME:
                        predicates.add(cb.equal(root.get(ImportedReturn_.uploadTime), (Date) entry.getValue()));
                        break;
                    case STATUS:
                        predicates.add(cb.equal(root.get(ImportedReturn_.status), entry.getValue()));
                        break;
                    case MESSAGE:
                        predicates.add(cb.like(root.get(ImportedReturn_.message), entry.getValue().toString().trim()));
                        break;
                    case FILE_NAME:
                        predicates.add(cb.equal(join.get(UploadFile_.fileName), entry.getValue().toString().trim()));
                        break;
                    case FILE_ID:
                        predicates.add(cb.equal(join.get(UploadFile_.id), entry.getValue()));
                        break;
                    case OFFSET, LIMIT:
                        break;
                }
            }
        }
        return predicates;
    }

    private List<Predicate> getGroupFilterPredicates(CriteriaBuilder cb, Root<ImportedReturnGroup> root, Join<ImportedReturnGroup, UploadFile> join, Map<ImportFilter, Object> filterMap) {
        List<Predicate> predicates = new ArrayList<>();
        if (filterMap != null) {
            for (Map.Entry<ImportFilter, Object> entry : filterMap.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }
                switch (entry.getKey()) {
                    case TYPE:
                        predicates.add(cb.equal(root.get(ImportedReturnGroup_.type), entry.getValue()));
                        break;
                    case RETURN_CODE:
                        predicates.add(cb.like(root.get(ImportedReturnGroup_.returnCode), entry.getValue().toString()));
                        break;
                    case FI_CODE:
                        predicates.add(cb.equal(root.get(ImportedReturnGroup_.bankCode), entry.getValue().toString().trim()));
                        break;
                    case VERSION_CODE:
                        predicates.add(cb.like(root.get(ImportedReturnGroup_.versionCode), entry.getValue().toString()));
                        break;
                    case PERIOD_START:
                        predicates.add(cb.greaterThanOrEqualTo(root.get(ImportedReturnGroup_.periodStart), (Date) entry.getValue()));
                        break;
                    case PERIOD_END:
                        predicates.add(cb.lessThanOrEqualTo(root.get(ImportedReturnGroup_.periodEnd), (Date) entry.getValue()));
                        break;
                    case USER_LOGIN:
                        predicates.add(cb.equal(root.get(ImportedReturnGroup_.user).get(User_.login), entry.getValue().toString()));
                        break;
                    case USER_ID:
                        predicates.add(cb.equal(root.get(ImportedReturnGroup_.user).get(User_.id), entry.getValue()));
                        break;
                    case FILE_NAME:
                        predicates.add(cb.equal(join.get(UploadFile_.fileName), entry.getValue().toString().trim()));
                        break;
                    case FILE_ID:
                        predicates.add(cb.equal(join.get(UploadFile_.id), entry.getValue()));
                        break;
                    case OFFSET, LIMIT:
                        break;
                }
            }
        }
        return predicates;
    }

    private Map<Long, Date> getFileUploadTimes(List<ImportModel> importModels) {
        Map<Long, Date> fileIdToUploadTime = new HashMap<>();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);

        Root<UploadFile> root = cq.from(UploadFile.class);
        Selection[] selections = new Selection[]{
                root.get(UploadFile_.id),
                root.get(UploadFile_.uploadedTime)
        };

        if (importModels != null && !importModels.isEmpty()) {
            List<Long> fileIds = importModels.stream().map(ImportModel::getFileId).collect(Collectors.toList());
            List<Predicate> fileIdPredicates = new ArrayList<>();
            for (Long fileId : fileIds) {
                fileIdPredicates.add(cb.or(cb.equal(root.get(UploadFile_.id), fileId)));
            }
            cq.where(cb.or(fileIdPredicates.toArray(new Predicate[0])));
        }

        cq.multiselect(selections);

        Query q = em.createQuery(cq);

        List<Object[]> uploadTimes = q.getResultList();
        for (Object[] o : uploadTimes) {
            fileIdToUploadTime.put((Long) o[0], (Date) o[1]);
        }

        return fileIdToUploadTime;
    }

    private Map<String, Date> getLatestUploadTimesForFileNames(List<String> fileNames) {
        Map<String, Date> result = new HashMap<>();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);

        Root<ImportedReturn> root = cq.from(ImportedReturn.class);
        Join<ImportedReturn, UploadFile> join = root.join(ImportedReturn_.uploadFile, JoinType.LEFT);

        Selection[] selections = new Selection[]{
                join.get(UploadFile_.fileName),
                cb.greatest(join.get(UploadFile_.uploadedTime))
        };

        cq.multiselect(selections);

        List<Expression<?>> groupings = new ArrayList<>();

        groupings.add(root.get(ImportedReturn_.bankCode));
        groupings.add(root.get(ImportedReturn_.periodStart));
        groupings.add(root.get(ImportedReturn_.periodEnd));
        groupings.add(root.get(ImportedReturn_.versionCode));
        groupings.add(join.get(UploadFile_.fileName));

        cq.groupBy(groupings);

        if (fileNames != null && !fileNames.isEmpty()) {
            List<Predicate> fileNamePredicates = new ArrayList<>();
            for (String fileName : fileNames) {
                fileNamePredicates.add(cb.or(cb.equal(join.get(UploadFile_.fileName), fileName)));
            }

            cq.where(cb.or(fileNamePredicates.toArray(new Predicate[0])));
        }

        Query q = em.createQuery(cq);

        List<Object[]> fileUploadTimes = q.getResultList();

        for (Object[] o : fileUploadTimes) {
            result.put((String) o[0], (Date) o[1]);
        }

        return result;
    }

}