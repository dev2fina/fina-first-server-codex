package net.fina.server.returns.impl;

import global.namespace.truelicense.api.LicenseManagementException;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import net.fina.auditlog.event.AuditLogAddManualRecordIdsEvent;
import net.fina.common.client.constants.ImportStatus;
import net.fina.common.client.constants.OperationType;
import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.client.dcs.UploadFileStatus;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.filter.OverdueReturnFilter;
import net.fina.common.client.filter.ReturnItemsSearchFilter;
import net.fina.common.client.returns.*;
import net.fina.common.server.StatisticsLogger;
import net.fina.common.shared.FilterConfig;
import net.fina.common.shared.FilterConfigKey;
import net.fina.common.shared.SortField;
import net.fina.common.shared.mi.MiChartDataModel;
import net.fina.messages.MessagesUtil;
import net.fina.security.api.AuthorizationLocal;
import net.fina.security.auth.CustomPrincipal;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.dcs.uploadfile.entity.SortInfo;
import net.fina.server.dcs.uploadfile.entity.UploadFile;
import net.fina.server.fi.api.FiLocal;
import net.fina.server.fi.api.RegionLocal;
import net.fina.server.fi.entity.*;
import net.fina.server.fsop.api.FsopImportStoreLocal;
import net.fina.server.fsop.api.FsopTemplateLocal;
import net.fina.server.i18n.entity.Language;
import net.fina.server.i18n.helper.Description;
import net.fina.server.i18n.impl.DescriptionManager;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.mdt.api.MDTNodeLocal;
import net.fina.server.mdt.entity.MDTComparison;
import net.fina.server.mdt.entity.MDTDependentNode;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.processing.api.ProcessingLocal;
import net.fina.server.processing.api.ProcessingStoreLocal;
import net.fina.server.processing.impl.ReturnLockSingleton;
import net.fina.server.processing.model.ProcessItem;
import net.fina.server.reg.api.RegFileLocal;
import net.fina.server.reg.impl.RegProcessStatus;
import net.fina.server.reg.model.RegProcessConfig;
import net.fina.server.reg.util.processor.RegFileProcessorUtil;
import net.fina.server.returns.api.*;
import net.fina.server.returns.entity.*;
import net.fina.server.returns.util.ImportedReturnErrorTextUtil;
import net.fina.server.returns.xml.Body;
import net.fina.server.returns.xml.Header;
import net.fina.server.returns.xml.Item;
import net.fina.server.rvc.event.ReturnItemsStoreEvent;
import net.fina.server.rvc.event.ReturnStatusStoreEvent;
import net.fina.server.security.api.UserLocal;
import net.fina.server.security.entity.User;
import net.fina.server.security.product.Product;
import net.fina.server.util.DBUtil;
import net.fina.server.util.RegionUtil;
import org.apache.commons.collections.FastHashMap;
import org.jboss.logging.Logger;
import org.joda.time.DateTime;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
@Local(ReturnLocal.class)
@Interceptors(RecordingAuditor.class)
@SuppressWarnings("JpaQlInspection")
public class ReturnSession implements ReturnLocal {
    private static final Integer BEFORE_END_DATE = 5;

    private final Logger log = Logger.getLogger(getClass());
    @Inject
    private EntityManager em;

    @EJB
    private FiLocal fiLocal;
    @EJB
    private ReturnVersionLocal retVersionLocal;
    @EJB
    private MDTNodeLocal mdtNodeLocal;
    @EJB
    private ProcessingStoreLocal processingStoreLocal;
    @EJB
    private ProcessingLocal processLocal;
    @EJB
    private UserLocal userLocal;
    @EJB
    private ScheduleLocal scheduleLocal;
    @EJB
    private FsopTemplateLocal xmlImportLocal;
    @EJB
    private ReturnDefinitionLocal returnDefinitionLocal;
    @EJB
    private ReturnCacheManager returnCacheManager;
    @EJB
    private ReturnLockSingleton returnLock;
    @EJB
    private RegionLocal regionLocal;
    @Inject
    private Event<ReturnStatusStoreEvent> returnStatusStoreEvent;
    @Inject
    private Event<ReturnItemsStoreEvent> returnStoreEvent;
    @Inject
    private Event<AuditLogAddManualRecordIdsEvent> auditLogAddManualRecordIdsEvent;
    @Inject
    private RegFileLocal regFileLocal;
    @Inject
    private FsopImportStoreLocal fsopImportStoreLocal;

    @Inject
    private AuthorizationLocal authorizationLocal;

    @Inject
    private ReturnNotificationLocal returnNotificationLocal;

    /**
     * Use DCS & FinA
     *
     * @param filterConfig
     * @param limit
     * @param offset
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<ReturnModel> loadPackages(FilterConfig filterConfig, int limit, int offset, SortField sortField) {
        long langId = ThreadLocalHolder.getLanguage().getId();

        try {
            Product.getInstance().check();
        } catch (LicenseManagementException e) {
            throw new RuntimeException(e.getMessage());
        }

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> criteriaQuery = cb.createQuery(Object[].class);

        Root<ReturnMaterializedView> root = criteriaQuery.from(ReturnMaterializedView.class);

        Selection[] selections = new Selection[]{
                root.get(ReturnMaterializedView_.bankId),
                root.get(ReturnMaterializedView_.bankCode),
                root.get(ReturnMaterializedView_.namestrId),

                root.get(ReturnMaterializedView_.periodId),
                root.get(ReturnMaterializedView_.fromDate),
                root.get(ReturnMaterializedView_.toDate),

                root.get(ReturnMaterializedView_.periodTypeId),
                root.get(ReturnMaterializedView_.periodTypeCode),

                root.get(ReturnMaterializedView_.returnTypeId),
                root.get(ReturnMaterializedView_.returnTypeCode),
                root.get(ReturnMaterializedView_.excelTemplate),

                root.get(ReturnMaterializedView_.versionId),
                root.get(ReturnMaterializedView_.versionCode),

                cb.max(root.get(ReturnMaterializedView_.scheduleId)),
                cb.max(root.get(ReturnMaterializedView_.ID))
        };

        criteriaQuery.multiselect(selections).groupBy(
                root.get(ReturnMaterializedView_.bankId),
                root.get(ReturnMaterializedView_.bankCode),
                root.get(ReturnMaterializedView_.namestrId),

                root.get(ReturnMaterializedView_.periodId),
                root.get(ReturnMaterializedView_.fromDate),
                root.get(ReturnMaterializedView_.toDate),

                root.get(ReturnMaterializedView_.periodTypeId),
                root.get(ReturnMaterializedView_.periodTypeCode),

                root.get(ReturnMaterializedView_.returnTypeId),
                root.get(ReturnMaterializedView_.returnTypeCode),
                root.get(ReturnMaterializedView_.excelTemplate),

                root.get(ReturnMaterializedView_.versionId),
                root.get(ReturnMaterializedView_.versionCode)
        );

        criteriaQuery.distinct(true);
        criteriaQuery.multiselect(selections);

        List<Predicate> predicates = new ArrayList<>();

        // Return Definition Id
        Object retDefinitionId = filterConfig.getFilterParam(FilterConfigKey.RETURN_DEFINITION_ID);
        if (isValidParam(retDefinitionId)) {
            predicates.add(cb.equal(root.get(ReturnMaterializedView_.definitionId), retDefinitionId));
        } else {
            Collection<Long> returnDefinitionIds = userLocal.getCallerPrincipal().getReturnDefinitions();
            List<Predicate> definitionPredicates = DBUtil.get().buildAndSplitPredicates(cb, root.get(ReturnMaterializedView_.definitionId), Long.class, returnDefinitionIds);
            predicates.add(cb.and(cb.or(definitionPredicates.toArray(new Predicate[0]))));
        }

        // Period From and To
        Date from = (Date) filterConfig.getFilterParam(FilterConfigKey.PERIOD_FROM_DATE);
        Date to = (Date) filterConfig.getFilterParam(FilterConfigKey.PERIOD_TO_DATE);

        if (from != null) {
            filterConfig.setFilterParam(FilterConfigKey.LOAD_ALL_PERIOD_DATA, true);
        }

        if (filterConfig.getFilterParam(FilterConfigKey.LOAD_ALL_PERIOD_DATA) != null && !(Boolean) filterConfig.getFilterParam(FilterConfigKey.LOAD_ALL_PERIOD_DATA)) {
            DateTime dateTime = DateTime.now().minusYears(1).withDayOfMonth(1).withMonthOfYear(1);
            if (to != null) {
                from = from == null ? dateTime.toDate() : from;
                predicates.add(cb.between(root.get(ReturnMaterializedView_.FROM_DATE), from, to));
                predicates.add(cb.between(root.get(ReturnMaterializedView_.TO_DATE), from, to));
            } else {
                predicates.add(cb.greaterThanOrEqualTo(root.get(ReturnMaterializedView_.FROM_DATE), dateTime.toDate()));
            }
        } else {
            if (from != null && to != null) {
                predicates.add(cb.between(root.get(ReturnMaterializedView_.FROM_DATE), from, to));
                predicates.add(cb.between(root.get(ReturnMaterializedView_.TO_DATE), from, to));
            } else if (to == null && from != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(ReturnMaterializedView_.FROM_DATE), from));
            } else if (to != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(ReturnMaterializedView_.TO_DATE), to));
            }
        }

        if (filterConfig != null && Boolean.parseBoolean(String.valueOf(filterConfig.getFilterParam(FilterConfigKey.RETURN_DEFINITION_MANUAL_INPUT)))) {
            predicates.add(cb.equal(root.get(ReturnMaterializedView_.manualInput), true));
        }

        // Return version Id
        Object versionId = filterConfig.getFilterParam(FilterConfigKey.RETURN_VERSION_ID);
        if (isValidParam(versionId)) {
            predicates.add(cb.equal(root.get(ReturnMaterializedView_.VERSION_ID), versionId));
        } else {
            Collection<Long> returnVersionIds = userLocal.getCallerPrincipal().getReturnVersions().keySet();
            predicates.add(root.get(ReturnMaterializedView_.VERSION_ID).in(returnVersionIds));
        }

        // Return status
        Object status = filterConfig.getFilterParam(FilterConfigKey.RETURN_STATUS);
        if (isValidParam(status)) {
            predicates.add(cb.equal(root.get(ReturnMaterializedView_.STATUS), status));
        }

        // Return type id
        Object returnTypeId = filterConfig.getFilterParam(FilterConfigKey.RETURN_TYPE_ID);
        if (isValidParam(returnTypeId)) {
            predicates.add(cb.equal(root.get(ReturnMaterializedView_.RETURN_TYPE_ID), returnTypeId));
        }

        // FI Id
        Collection<Long> fiIds;
        Object fiIdsObject = filterConfig.getFilterParam(FilterConfigKey.FI_ID_LIST);
        if (fiIdsObject != null) {
            fiIds = (Collection<Long>) fiIdsObject;
            if (fiIds.isEmpty()) {
                fiIds.add(0L);
            }
        } else {
            fiIds = userLocal.getCallerPrincipal().getFis();
        }

        List<Predicate> fiPredList = new ArrayList<>();

        if (fiIdsObject == null && fiIds.size() >= DBUtil.IN_CLAUSE_MAX_PARAMS) {
            CriteriaQuery<UserFi> fiCriteriaQuery = cb.createQuery(UserFi.class);
            Subquery<Long> fiUserSubquery = fiCriteriaQuery.subquery(Long.class);
            Root<UserFi> fiUserQRoot = fiUserSubquery.from(UserFi.class);
            fiUserSubquery.select(fiUserQRoot.get(UserFi_.BANK_ID));
            fiUserSubquery.where(cb.equal(fiUserQRoot.get(UserFi_.USER_ID), userLocal.getCurrentUserId()));
            fiPredList.add(root.get(ReturnMaterializedView_.bankId).in(fiUserSubquery));

            List<Long> roleIds = userLocal.getCallerPrincipal().getRoles();

            CriteriaQuery<RoleFi> fiRoleQuery = cb.createQuery(RoleFi.class);
            Subquery<Long> fiRoleSubquery = fiRoleQuery.subquery(Long.class);
            Root<RoleFi> fiRoleQRoot = fiRoleSubquery.from(RoleFi.class);
            fiRoleSubquery.select(fiRoleQRoot.get(RoleFi_.BANK_ID));
            fiRoleSubquery.where(fiRoleQRoot.get(RoleFi_.ROLE_ID).in(roleIds));
            fiPredList.add(root.get(ReturnMaterializedView_.bankId).in(fiRoleSubquery));
        } else {
            fiPredList = DBUtil.get().buildAndSplitPredicates(cb, root.get(ReturnMaterializedView_.bankId), Long.class, fiIds);
        }
        predicates.add(cb.and(cb.or(fiPredList.toArray(new Predicate[0]))));

        criteriaQuery.where(predicates.toArray(new Predicate[0]));

        if (sortField != null) {
            switch (sortField.getProperty().toUpperCase()) {
                case "FROMDATE":
                    criteriaQuery.orderBy(sortField.getDirection().equalsIgnoreCase("desc") ? cb.desc(root.get(ReturnMaterializedView_.FROM_DATE)) : cb.asc(root.get(ReturnMaterializedView_.FROM_DATE)));
                    break;
                case "TODATE":
                    criteriaQuery.orderBy(sortField.getDirection().equalsIgnoreCase("desc") ? cb.desc(root.get(ReturnMaterializedView_.TO_DATE)) : cb.asc(root.get(ReturnMaterializedView_.TO_DATE)));
                    break;
                case "VERSIONCODE":
                    criteriaQuery.orderBy(sortField.getDirection().equalsIgnoreCase("desc") ? cb.desc(root.get(ReturnMaterializedView_.VERSION_CODE)) : cb.asc(root.get(ReturnMaterializedView_.VERSION_CODE)));
                    break;
                case "RETURNTYPECODE":
                    criteriaQuery.orderBy(sortField.getDirection().equalsIgnoreCase("desc") ? cb.desc(root.get(ReturnMaterializedView_.RETURN_TYPE_CODE)) : cb.asc(root.get(ReturnMaterializedView_.RETURN_TYPE_CODE)));
                    break;
                default:
                    criteriaQuery.orderBy(cb.desc(root.get(ReturnMaterializedView_.TO_DATE)));
                    break;
            }
        } else {
            criteriaQuery.orderBy(cb.desc(root.get(ReturnMaterializedView_.TO_DATE)));
        }


        Query loadReturnsQuery = em.createQuery(criteriaQuery);
        if (limit > 0) {
            loadReturnsQuery.setFirstResult(offset);
            loadReturnsQuery.setMaxResults(limit);
        }

        List<Object[]> objectList = loadReturnsQuery.getResultList();
        List<ReturnModel> resultList = new ArrayList<>();

        List<Long> regScheduleIds = regFileLocal.loadUniqueScheduleIds();

        for (Object[] o : objectList) {
            long fiId = ((Number) o[0]).longValue();
            String fiCode = (String) o[1];
            Description fiDescription = DescriptionManager.getInstance().getDescription(((Number) o[2]).longValue());

            long periodId = ((Number) o[3]).longValue();
            Date fromDate = (Date) o[4];
            Date toDate = (Date) o[5];
            long periodTypeId = ((Number) o[6]).longValue();
            String periodTypeCode = (String) o[7];
            long retTypeId = ((Number) o[8]).longValue();
            String returnTypeCode = (String) o[9];
            boolean excelTemplate = (boolean) o[10];
            long versId = ((Number) o[11]).longValue();
            String versionCode = (String) o[12];
            long scheduleId = ((Number) o[13]).longValue();

            ReturnModel model = new ReturnModel(fiId, fiCode, periodId, toDate, periodTypeId,
                    periodTypeCode, retTypeId, returnTypeCode, versId, versionCode);
            model.setExcelTemplate(excelTemplate);
            model.setReg(regScheduleIds.contains(scheduleId));

            Map<Long, String> descriptionMap = new HashMap<>(fiDescription.getDescriptions());
            model.setFiDescription(fiDescription.getDescription(langId));
            model.setFiDescriptionMap(descriptionMap);
            model.setFromDate(fromDate);
            resultList.add(model);
        }

        return resultList;
    }

    /**
     * Use DCS
     *
     * @param filterConfig
     * @return
     */
    public long getPackagesCount(FilterConfig filterConfig) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);

        Root<ReturnMaterializedView> root = criteriaQuery.from(ReturnMaterializedView.class);


        Selection[] selections = new Selection[]{
                cb.max(root.get(ReturnMaterializedView_.BANK_ID)),
        };

        criteriaQuery.multiselect(selections).groupBy(
                root.get(ReturnMaterializedView_.BANK_ID),
                root.get(ReturnMaterializedView_.BANK_CODE),
                root.get(ReturnMaterializedView_.NAMESTR_ID),

                root.get(ReturnMaterializedView_.PERIOD_ID),
                root.get(ReturnMaterializedView_.FROM_DATE),
                root.get(ReturnMaterializedView_.TO_DATE),

                root.get(ReturnMaterializedView_.PERIOD_TYPE_ID),
                root.get(ReturnMaterializedView_.PERIOD_TYPE_CODE),

                root.get(ReturnMaterializedView_.RETURN_TYPE_ID),
                root.get(ReturnMaterializedView_.RETURN_TYPE_CODE),
                root.get(ReturnMaterializedView_.EXCEL_TEMPLATE),

                root.get(ReturnMaterializedView_.VERSION_ID),
                root.get(ReturnMaterializedView_.VERSION_CODE)
        );

        criteriaQuery.multiselect(selections);

        List<Predicate> predicates = new ArrayList<>();

        // Return Definition Id
        Object retDefinitionId = filterConfig.getFilterParam(FilterConfigKey.RETURN_DEFINITION_ID);
        if (isValidParam(retDefinitionId)) {
            predicates.add(cb.equal(root.get(ReturnMaterializedView_.DEFINITION_ID), retDefinitionId));
        } else {
            Collection<Long> returnDefinitionIds = userLocal.getCallerPrincipal().getReturnDefinitions();
            predicates.add(root.get(ReturnMaterializedView_.DEFINITION_ID).in(returnDefinitionIds));
        }

        // Period From and To
        Date from = (Date) filterConfig.getFilterParam(FilterConfigKey.PERIOD_FROM_DATE);
        Date to = (Date) filterConfig.getFilterParam(FilterConfigKey.PERIOD_TO_DATE);

        if (filterConfig.getFilterParam(FilterConfigKey.LOAD_ALL_PERIOD_DATA) != null && !(Boolean) filterConfig.getFilterParam(FilterConfigKey.LOAD_ALL_PERIOD_DATA)) {
            DateTime dateTime = DateTime.now().minusYears(1).withDayOfMonth(1).withMonthOfYear(1);
            predicates.add(cb.greaterThanOrEqualTo(root.get(ReturnMaterializedView_.FROM_DATE), dateTime.toDate()));
        } else {
            if (from != null && to != null) {
                predicates.add(cb.between(root.get(ReturnMaterializedView_.FROM_DATE), from, to));
                predicates.add(cb.between(root.get(ReturnMaterializedView_.TO_DATE), from, to));
            } else if (to == null && from != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(ReturnMaterializedView_.FROM_DATE), from));
            } else if (to != null && from == null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(ReturnMaterializedView_.TO_DATE), to));
            }
        }

        if (filterConfig != null && Boolean.parseBoolean(String.valueOf(filterConfig.getFilterParam(FilterConfigKey.RETURN_DEFINITION_MANUAL_INPUT)))) {
            predicates.add(cb.equal(root.get(ReturnMaterializedView_.manualInput), true));
        }

        // Return version Id
        Object versionId = filterConfig.getFilterParam(FilterConfigKey.RETURN_VERSION_ID);
        if (isValidParam(versionId)) {
            predicates.add(cb.equal(root.get(ReturnMaterializedView_.VERSION_ID), versionId));
        } else {
            Collection<Long> returnVersionIds = userLocal.getCallerPrincipal().getReturnVersions().keySet();
            predicates.add(root.get(ReturnMaterializedView_.VERSION_ID).in(returnVersionIds));
        }

        // Return status
        Object status = filterConfig.getFilterParam(FilterConfigKey.RETURN_STATUS);
        if (isValidParam(status)) {
            predicates.add(cb.equal(root.get(ReturnMaterializedView_.STATUS), status));
        }

        // Return type id
        Object returnTypeId = filterConfig.getFilterParam(FilterConfigKey.RETURN_TYPE_ID);
        if (isValidParam(returnTypeId)) {
            predicates.add(cb.equal(root.get(ReturnMaterializedView_.RETURN_TYPE_ID), returnTypeId));
        }

        // FI Id
        Collection<Long> fiIds = new ArrayList<>();
        Object fiIdsObject = filterConfig.getFilterParam(FilterConfigKey.FI_ID_LIST);
        if (fiIdsObject != null) {
            fiIds = (Collection<Long>) fiIdsObject;
            if (fiIds.isEmpty()) {
                fiIds.add(0L);
            }
        } else {
            fiIds = userLocal.getCallerPrincipal().getFis();
        }


        List<Predicate> fiPredList = new ArrayList<>();
        if (fiIdsObject == null && fiIds.size() >= DBUtil.IN_CLAUSE_MAX_PARAMS) {
            CriteriaQuery<UserFi> fiCriteriaQuery = cb.createQuery(UserFi.class);
            Subquery<Long> fiUserSubquery = fiCriteriaQuery.subquery(Long.class);
            Root<UserFi> fiUserQRoot = fiUserSubquery.from(UserFi.class);
            fiUserSubquery.select(fiUserQRoot.get(UserFi_.BANK_ID));
            fiUserSubquery.where(cb.equal(fiUserQRoot.get(UserFi_.USER_ID), userLocal.getCurrentUserId()));
            fiPredList.add(root.get(ReturnMaterializedView_.bankId).in(fiUserSubquery));

            List<Long> roleIds = userLocal.getCallerPrincipal().getRoles();

            CriteriaQuery<RoleFi> fiRoleQuery = cb.createQuery(RoleFi.class);
            Subquery<Long> fiRoleSubquery = fiRoleQuery.subquery(Long.class);
            Root<RoleFi> fiRoleQRoot = fiRoleSubquery.from(RoleFi.class);
            fiRoleSubquery.select(fiRoleQRoot.get(RoleFi_.BANK_ID));
            fiRoleSubquery.where(fiRoleQRoot.get(RoleFi_.ROLE_ID).in(roleIds));
            fiPredList.add(root.get(ReturnMaterializedView_.bankId).in(fiRoleSubquery));
        } else {
            fiPredList = DBUtil.get().buildAndSplitPredicates(cb, root.get(ReturnMaterializedView_.bankId), Long.class, fiIds);
        }
        predicates.add(cb.and(cb.or(fiPredList.toArray(new Predicate[0]))));

        criteriaQuery.where(predicates.toArray(new Predicate[0]));

        Query loadReturnsQuery = em.createQuery(criteriaQuery);


        List<Long> objectList = loadReturnsQuery.getResultList();

        return objectList.size();
    }

    /**
     * Use DCS
     *
     * @param fiId
     * @param periodId
     * @param returnTypeId
     * @param returnVersionId
     * @param filterConfig
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Map<String, Object>> laodPackageReturns(long fiId, long periodId, long returnTypeId,
                                                        long returnVersionId, FilterConfig filterConfig) {
        StringBuffer buff = new StringBuffer();

        buff.append("select r.id, ");
        buff.append("r.schedule.period.fromDate, ");
        buff.append("r.schedule.period.toDate, ");
        buff.append("r.schedule.returnDefinition.code, ");
        buff.append("r.schedule.returnDefinition.description, ");
        buff.append("r.schedule.fi.code, ");
        buff.append("r.returnVersion.code, ");
        buff.append("r.schedule.returnDefinition.returnType.code, ");
        buff.append("rs.status, ");
        buff.append("rs.statusDate, ");
        buff.append("r.schedule.fi.description, ");
        buff.append("r.schedule.returnDefinition.id, ");
        buff.append("r.schedule.returnDefinition.returnType.excelTemplate, ");
        buff.append("r.schedule.period.periodType.code, ");
        buff.append("r.schedule.id ");

        buff.append("from IN_RETURNS as r, IN_RETURN_STATUSES as rs ");

        buff.append("where r.id=rs.returns.id ");
        buff.append(" AND rs.id=(select MAX(irs.id) from IN_RETURN_STATUSES as irs where irs.returns.id=r.id) AND ");

        buff.append("(r.schedule.fi.id=:fiId) AND ");
        buff.append("(r.schedule.period.id=:periodId) AND ");
        buff.append("(r.schedule.returnDefinition.returnType.id=:returnTypeId) AND ");
        buff.append("(r.returnVersion.id=:returnVersionId) AND ")
                .append(" r.schedule.returnDefinition.id in (:definitionIds)");

        // Return Definition Id
        Object retDefinitionId = filterConfig.getFilterParam(FilterConfigKey.RETURN_DEFINITION_ID);
        if (isValidParam(retDefinitionId)) {
            buff.append(" and (r.schedule.returnDefinition.id=:returnDefinitionId) ");
        }

        // Return status
        Object status = filterConfig.getFilterParam(FilterConfigKey.RETURN_STATUS);
        if (isValidParam(status)) {
            buff.append(" AND ");
            buff.append(" (rs.status=:status) ");
        }

        if (filterConfig != null && Boolean.parseBoolean(String.valueOf(filterConfig.getFilterParam(FilterConfigKey.RETURN_DEFINITION_MANUAL_INPUT)))) {
            buff.append(" AND (r.schedule.returnDefinition.manualInput = true) ");
        }

        buff.append(" ORDER BY r.schedule.returnDefinition.code");

        Query laodPackageReturnsQuery = em.createQuery(buff.toString());

        laodPackageReturnsQuery.setParameter("fiId", fiId);
        laodPackageReturnsQuery.setParameter("periodId", periodId);
        laodPackageReturnsQuery.setParameter("returnTypeId", returnTypeId);
        laodPackageReturnsQuery.setParameter("returnVersionId", returnVersionId);
        laodPackageReturnsQuery.setParameter("definitionIds", userLocal.getCallerPrincipal().getReturnDefinitions());

        // Set Parameter Return Definition
        if (isValidParam(retDefinitionId)) {
            laodPackageReturnsQuery.setParameter("returnDefinitionId", retDefinitionId);
        }

        // Set Return Status Parameter
        if (isValidParam(status)) {
            laodPackageReturnsQuery.setParameter("status", (ProcessStatus) status);
        }

        List<Map<String, Object>> props = new ArrayList<Map<String, Object>>();

        List<Object[]> result = laodPackageReturnsQuery.getResultList();

        for (Object[] objects : result) {

            Map<String, Object> prop = new HashMap<String, Object>();

            prop.put("id", objects[0]);
            prop.put("periodFromDate", objects[1]);
            prop.put("periodToDate", objects[2]);
            prop.put("retrunDefinitionCode", objects[3]);
            prop.put("retrunDefinitionDescription", objects[4]);
            prop.put("fiCode", objects[5]);
            prop.put("returnVersionCode", objects[6]);

            prop.put("returnTypeCode", objects[7]);

            prop.put("returnStatus", objects[8]);
            prop.put("returnStatusDate", objects[9]);

            prop.put("fiName", objects[10]);
            prop.put("retrunDefinitionId", objects[11]);
            prop.put("excelTemplate", objects[12]);
            prop.put("periodTypeCode", objects[13]);
            prop.put("scheduleId", objects[14]);

            props.add(prop);

        }

        return props;
    }

    /**
     * Use DCS
     *
     * @param filterConfig
     * @return
     */
    @Override

    public List<ReturnModel> loadNotCreatedPackages(FilterConfig filterConfig) {
        //ignore query for performance reasons if user is internal user
        if (authorizationLocal.hasUserPermission(userLocal.getCurrentUserLogin(), PermissionIdNames.FINA_WEB_INTERNAL_USER)) {
            return new ArrayList<>();
        }

        boolean manualInput = filterConfig != null && Boolean.parseBoolean(String.valueOf(filterConfig.getFilterParam(FilterConfigKey.RETURN_DEFINITION_MANUAL_INPUT)));

        Set<Long> userVersionsIds = userLocal.getCallerPrincipal().getReturnVersions().keySet();
        List<Schedule> schedules = loadCurrentSubmissionPeriodSchedules(manualInput);
        String schedulesInStatement = DBUtil.get().generateConcatenatedInStatements("s.id", (schedules.isEmpty() ? Collections.singletonList(new Schedule(-1, -1)) : schedules), "id");

        String sql = "Select DISTINCT " +
                " new " +
                ReturnModel.class.getName() +
                " ( " +
                "s.delay, " +
                "s.delayHour, " +
                "s.delayMinute, " +
                "s.fi.id, " +
                "s.fi.code, " +
                "s.period.id, " +
                "s.period.toDate, " +
                "s.period.periodType.id, " +
                "s.period.periodType.code, " +
                "s.returnDefinition.returnType.id, " +
                "s.returnDefinition.returnType.code, " +
                "rv.id, " +
                "rv.code " +
                ")" +
                " from  IN_SCHEDULES as s, IN_RETURN_VERSIONS as rv " +
                " WHERE (" + schedulesInStatement + ") " +
                " AND s.returnDefinition.id IN(:userReturnDefinitions) " +
                // TODO multiple return versions logic
                " AND rv.id IN(:userVersions) " +
                " AND s.id NOT IN( SELECT r.schedule.id FROM IN_RETURNS as r where r.schedule.id is not null) ";

        if (manualInput) {
            sql += " AND s.returnDefinition.manualInput = true ";
        }

        List<Long> fiIds = new ArrayList<>(userLocal.getCallerPrincipal().getFis());
        String fiQuery = DBUtil.get().generateConcatenatedInStatementWithIds("s.fi.id", fiIds.isEmpty() ? Collections.singletonList(-1L) : fiIds);

        sql = sql + " AND (" + fiQuery + ")";
        Query query = em.createQuery(sql, ReturnModel.class);

        query.setParameter("userVersions", userVersionsIds.isEmpty() ? Collections.singletonList(-1L) : userVersionsIds);
        query.setParameter("userReturnDefinitions", userLocal.getCallerPrincipal().getReturnDefinitions().isEmpty() ?
                Collections.singletonList(-1L) :
                userLocal.getCallerPrincipal().getReturnDefinitions());

//        query.setParameter("scheduleIds", schedules.isEmpty() ? Collections.singletonList(-1L) : schedules);


        List<ReturnModel> list = query.getResultList();


        return list.stream().map(ReturnModel::create).map(ReturnModel::group).collect(Collectors.toList());
    }

    private boolean validateScheduledReturn(int due, int dueHour, int dueMinute, int beforeEndDate, Calendar currentCalendar, Date toDate) {
        /*
        Let's say that EndDate is 5 Feb. and TODAY is 5 Feb.
        In the endCalendar variable value will be 5 Feb 0 h, 0 min, 0 sec.
        currentCalendar variable value will be 5 feb hour >= 0, min >= 0, sec >= 0, milliSecond >0;
        (currentCalendar.compareTo(endCalendar) <= 0) will be always FALSE.
        So I decided increment due.
         */
        due += 1;

        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(toDate);

        Calendar startCalendar = (Calendar) toCalendar.clone();
        startCalendar.set(Calendar.DAY_OF_YEAR, startCalendar.get(Calendar.DAY_OF_YEAR) - beforeEndDate);

        Calendar endCalendar = (Calendar) toCalendar.clone();
        endCalendar.set(Calendar.DAY_OF_YEAR, toCalendar.get(Calendar.DAY_OF_YEAR) + due);
        endCalendar.add(Calendar.HOUR, dueHour);
        endCalendar.add(Calendar.MINUTE, dueMinute);

        return (currentCalendar.compareTo(startCalendar) >= 0) && (currentCalendar.compareTo(endCalendar) <= 0);
    }

    /**
     * Use DCS
     *
     * @param fiId
     * @param periodId
     * @param returnTypeId
     * @param returnVersionId
     * @param filterConfig
     * @param returnDefinitionCodes
     * @return
     */
    @Override
    public List<Map<String, Object>> loadNotCreatedPackageReturns(long fiId, long periodId, long returnTypeId, long returnVersionId, FilterConfig filterConfig, List<String> returnDefinitionCodes) {
        StringBuffer buff = new StringBuffer();

        buff.append("Select ");
        buff.append("s.id, ");
        buff.append("s.delay, ");
        buff.append("s.fi.id, ");
        buff.append("s.fi.code, ");
        buff.append("s.period.id, ");
        buff.append("s.period.toDate, ");
        buff.append("s.returnDefinition.returnType.id, ");
        buff.append("s.returnDefinition.returnType.code, ");
        buff.append("s.returnDefinition.code, ");
        buff.append("s.returnDefinition.description, ");
        buff.append("rv.id, ");
        buff.append("rv.code, ");
        buff.append("s.returnDefinition.returnType.excelTemplate, ");
        buff.append("s.period.periodType.code, ");
        buff.append("s.delayHour, ");
        buff.append("s.delayMinute ");

        buff.append(" from  IN_SCHEDULES as s, IN_RETURN_VERSIONS as rv ");

        buff.append(" WHERE s.period.id=:periodId ");

        buff.append(" AND s.id NOT IN( SELECT r.schedule.id FROM IN_RETURNS as r where r.schedule.id is not null and r.returnVersion.id=:versionId) ");

        buff.append(" AND s.fi.id=:fiId ");
        buff.append(" AND s.returnDefinition.returnType.id=:returnTypeId ");
        buff.append(" AND rv.id=:versionId ");
        buff.append(" AND s.returnDefinition.id in(:userReturnDefinitions) ");

        if (!returnDefinitionCodes.isEmpty()) {
            buff.append(" AND s.returnDefinition.code NOT IN(:returnDefinitionCodes) ");
        }

        if (filterConfig != null && Boolean.parseBoolean(String.valueOf(filterConfig.getFilterParam(FilterConfigKey.RETURN_DEFINITION_MANUAL_INPUT)))) {
            buff.append(" AND s.returnDefinition.manualInput = true ");
        }

        Query loadQuery = em.createQuery(buff.toString());

        loadQuery.setParameter("periodId", periodId);
        loadQuery.setParameter("fiId", fiId);
        loadQuery.setParameter("returnTypeId", returnTypeId);
        loadQuery.setParameter("versionId", returnVersionId);
        loadQuery.setParameter("userReturnDefinitions", userLocal.getCallerPrincipal().getReturnDefinitions());

        if (!returnDefinitionCodes.isEmpty()) {
            loadQuery.setParameter("returnDefinitionCodes", returnDefinitionCodes);
        }

        @SuppressWarnings("unchecked")
        List<Object[]> result = loadQuery.getResultList();

        List<Map<String, Object>> props = new ArrayList<>();

        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTime(new Date());

        for (Object[] objects : result) {

            Date toDate = (Date) objects[5];
            int due = (Integer) objects[1];
            int dueHour = (Integer) objects[14];
            int dueMinute = (Integer) objects[15];

            if (validateScheduledReturn(due, dueHour, dueMinute, BEFORE_END_DATE, currentCalendar, toDate)) {

                Map<String, Object> prop = new HashMap<String, Object>();

                prop.put("id", 0L);
                prop.put("create", true);
                prop.put("scheduleId", objects[0]);
                prop.put("due", objects[1]);
                prop.put("fiCode", objects[3]);
                prop.put("periodToDate", objects[5]);
                prop.put("returnTypeCode", objects[7]);
                prop.put("retrunDefinitionCode", objects[8]);
                prop.put("retrunDefinitionDescription", objects[9]);
                prop.put("returnVersionCode", objects[11]);
                prop.put("excelTemplate", objects[12]);
                prop.put("periodTypeCode", objects[13]);
                prop.put("dueHour", objects[14]);
                prop.put("dueMinute", objects[15]);

                props.add(prop);
            }
        }
        return props;
    }

    @SuppressWarnings("unchecked")

    public TreeMap<Integer, Integer> loadReturnsStatisticInfo(Date statusDateFrom, Date statusDateTo) {
        TreeMap<Integer, Integer> returnInfo = new TreeMap<Integer, Integer>();
        try {
            log.info("Loading statistic for returns");
            Query infoQuery = getReturnsStatisticsQuery(statusDateFrom, statusDateTo);
            List<Object[]> info = infoQuery.getResultList();
            if (info != null) {
                for (int i = 0; i < info.size(); i++) {
                    Object arr[] = info.get(i);
                    Integer status = ((ProcessStatus) arr[0]).ordinal();/*Integer.parseInt(arr[0].toString());*/
                    Integer quantity = Integer.parseInt(arr[1].toString());
                    returnInfo.put(status, quantity);
                }
            }
            log.info(info);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return returnInfo;
    }

    private Query getReturnsStatisticsQuery(Date statusDateFrom, Date statusDateTo) {
        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        StringBuffer sb = new StringBuffer(" ");
        sb.append("SELECT ");
        sb.append("  retStats.status, ");
        sb.append("  COUNT(retStats.status) as QUANTITY ");
        sb.append("from IN_RETURN_STATUSES retStats,");
        sb.append("     IN_RETURNS rets ,");
        sb.append("     IN_SCHEDULES schedules,");
        sb.append("     IN_PERIODS periods,");
        sb.append("     IN_PERIOD_TYPES periodTypes ");
        sb.append(" WHERE ");
        sb.append(" retStats.returns.id=rets.id ");
        sb.append(" and rets.schedule.id=schedules.id ");
        sb.append(" and schedules.period.id=periods.id ");
        if (statusDateFrom != null) {
            sb.append(" and retStats.statusDate>=:statusFromDate");
            paramsMap.put("statusFromDate", statusDateFrom);
        }
        if (statusDateTo != null) {
            sb.append(" and retStats.statusDate<=:statusToDate");
            paramsMap.put("statusToDate", statusDateTo);
        }
        sb.append("  and periods.periodType.id=periodTypes.id ");
        sb.append(" and retStats.statusDate =");
        sb.append(" (select MAX(r.statusDate) from IN_RETURN_STATUSES r where r.returns.id=retStats.returns.id) ");
        sb.append(" group by retStats.status");
        Query q = em.createQuery(sb.toString());

        for (Map.Entry<String, Object> e : paramsMap.entrySet()) {
            Object val = e.getValue();
            if (val instanceof Date)
                q.setParameter(e.getKey(), (Date) e.getValue(), TemporalType.TIMESTAMP);
        }

        return q;
    }

    private boolean isValidParam(Object param) {
        return param != null;
    }

    @Override
    public Return createReturn(long scheduleId, long versionId, long userId, final List<MDTDependentNode> mdtDependentNodes, Map<Long, List<MDTNode>> allMdtNodesByParentId, final List<MDTComparison> comparisons) throws FinATypeException {
        return createReturn(scheduleId, versionId, userId, true, mdtDependentNodes, allMdtNodesByParentId, comparisons);
    }


    @Override
    public Return createReturn(long scheduleId, long versionId, long userId, boolean checkDependency, final List<MDTDependentNode> mdtDependentNodes, Map<Long, List<MDTNode>> allMdtNodesByParentId, final List<MDTComparison> comparisons) throws FinATypeException {
        return createReturn(scheduleId, versionId, userId, checkDependency, true, true, mdtDependentNodes, allMdtNodesByParentId, comparisons);
    }

    private Return createReturn(long scheduleId, long versionId, long userId, boolean checkDependency, boolean lock, boolean insertDefaultValues, final List<MDTDependentNode> mdtDependentNodes, Map<Long, List<MDTNode>> allMdtNodesByParentId, final List<MDTComparison> comparisons) throws FinATypeException {
        if (lock && returnLock.isLock(scheduleId, versionId)) {
            throw new ConcurrentModificationException("Return is lock. schedule id:" + scheduleId + ", version Id:" + versionId);
        }

        try {
            if (lock) {
                returnLock.lock(scheduleId, versionId);
            }

            //Check Return dependencies
            if (checkDependency) {
                hasDependentReturns(scheduleId);
            }

            Query existReturnQuery = em.createQuery("select r from  IN_RETURNS as r where r.schedule.id=:scheduleId and r.returnVersion.id=:versionId");
            existReturnQuery.setParameter("versionId", versionId);
            existReturnQuery.setParameter("scheduleId", scheduleId);

            List<Return> list = existReturnQuery.getResultList();

            Return r = new Return();
            if (!list.isEmpty()) {
                throw new FinATypeException("Return already exists.");
            } else {
                String processId = UUID.randomUUID().toString();

                r.setSchedule(scheduleLocal.loadSimpleSchedule(scheduleId));

                ReturnVersion rv = retVersionLocal.loadSimpleReturnVersion(versionId);
                r.setReturnVersion(rv);
                r.setLatestVersion(versionId);
                r.setStatus(ProcessStatus.STATUS_CREATED);

                em.persist(r);

                // Set Default Values
                if (insertDefaultValues) {
                    createReturnDefaultValues(r, processId, mdtDependentNodes, allMdtNodesByParentId, comparisons);
                }

                addReturnStatus(r.getId(), r.getStatus(), versionId, userId, "Create Return.", processId);
            }

            Return ret = new Return();
            ret.setId(r.getId());
            ReturnVersion rv = new ReturnVersion();
            rv.setId(r.getReturnVersion().getId());
            ret.setReturnVersion(rv);
            ret.setStatus(r.getStatus());

            return ret;
        } finally {
            if (lock) {
                returnLock.unlock(scheduleId, versionId);
            }
        }
    }

    @Override

    public void saveAsReturn(long returnId, long versionId, long userId, long langId, String note) throws FinATypeException {
        Return ret = em.find(Return.class, returnId);
        saveAsReturn(ret, versionId, userId, langId, note);
        updateLastVersion(ret.getSchedule().getId(), versionId);
    }

    @Override
    public void updateLastVersion(long scheduleId, long lastVersionId) {
        em.createQuery("update IN_RETURNS r set r.latestVersion=:lastVersionId where r.schedule.id=:scheduleId")
                .setParameter("lastVersionId", lastVersionId)
                .setParameter("scheduleId", scheduleId)
                .executeUpdate();
    }

    private void saveAsReturn(Return ret, long versionId, long userId, long langId, String note) throws FinATypeException {
        StatisticsLogger sLog = new StatisticsLogger("Return save as. return:" + ret.getId() + ", version:" + versionId);
        sLog.logMessage("Start return save as");
        try {
            sLog.logStage("Check lock");
            if (returnLock.isLock(ret.getSchedule().getId(), ret.getReturnVersion().getId())) {
                throw new ConcurrentModificationException("Return is lock. schedule id:" + ret.getSchedule().getId() + ", version Id:" + ret.getReturnVersion().getId());
            }

            if (returnLock.isLock(ret.getSchedule().getId(), versionId)) {
                throw new ConcurrentModificationException("Return is lock. schedule id:" + ret.getSchedule().getId() + ", version Id:" + versionId);
            }

            //Lock return
            sLog.logStage("Lock return");
            returnLock.lock(ret.getSchedule().getId(), ret.getReturnVersion().getId());
            returnLock.lock(ret.getSchedule().getId(), versionId);
            final List<MDTDependentNode> allMdtDependentNodes = processingStoreLocal.loadAllMdtDependentNodes();
            final List<MDTComparison> comparisons = mdtNodeLocal.loadComparisons(new HashMap<>(), null);
            final Map<Long, List<MDTNode>> allMdtNodesByParentId = mdtNodeLocal.loadAllNodesByParentId();

            //Create new return
            sLog.logStage("Create new return");
            Return newVersionOfReturn = createReturn(ret.getSchedule().getId(), versionId, userId, false, false, false, allMdtDependentNodes, allMdtNodesByParentId, comparisons);
            newVersionOfReturn = em.find(Return.class, newVersionOfReturn.getId());

            //Load Return items
            sLog.logStage("Load all dependencies");
            List<MDTDependentNode> mdtDependentNodes = mdtNodeLocal.loadAllMdtDependentNodes();

            sLog.logStage("Load return items");
            Collection<ProcessItem> items = processLocal.loadReturnNodesValue(ret.getId(), ret.getReturnVersion().getId(), mdtDependentNodes, langId, allMdtNodesByParentId).values();

            //Change return and version
            sLog.logStage("Set return items return id and version id");
            for (ProcessItem item : items) {
                item.versionId = versionId;
                item.returnId = newVersionOfReturn.getId();
            }

            //Save return items
            sLog.logStage("Update return items");
            processLocal.updateProcessItems(items, newVersionOfReturn.getId(), mdtDependentNodes, allMdtNodesByParentId, comparisons);

            //Update return status
            sLog.logStage("Change return status");
            addReturnStatus(newVersionOfReturn.getId(), ProcessStatus.STATUS_AMENDED, versionId, userId, note, null);
            newVersionOfReturn.setStatus(ProcessStatus.STATUS_AMENDED);
        } finally {
            returnLock.unlock(ret.getSchedule().getId(), ret.getReturnVersion().getId());
            returnLock.unlock(ret.getSchedule().getId(), versionId);
            sLog.logStage("Unlock return");
            sLog.close();
        }
    }

    private void hasDependentReturns(long scheduleId) throws FinATypeException {
        ReturnDefinition returnDefinition = em.createQuery("select new " + ReturnDefinition.class.getName() + "(s.returnDefinition.id,s.returnDefinition.code) from IN_SCHEDULES s where s.id=:scheduleId", ReturnDefinition.class)
                .setParameter("scheduleId", scheduleId)
                .getSingleResult();

        Collection<Long> returnDefinitionIds = new ArrayList<>();
        Collection<Long> dependencyIds = returnDefinitionLocal.loadReturnDefinitionDependenciesIds().get(returnDefinition.getId());
        if (dependencyIds != null) {
            returnDefinitionIds.addAll(dependencyIds);
        }
        if (!returnDefinitionIds.isEmpty()) {
            List<Long> definitionIds = em.createQuery("select r.schedule.returnDefinition.id from  IN_RETURNS r,IN_SCHEDULES s where s.id=:scheduleId and r.schedule.fi.id=s.fi.id and r.schedule.returnDefinition.id in(:returnDefinitionIds) and r.schedule.period.fromDate=s.period.fromDate and r.schedule.period.toDate=s.period.toDate", Long.class)
                    .setParameter("scheduleId", scheduleId)
                    .setParameter("returnDefinitionIds", returnDefinitionIds)
                    .getResultList();

            for (long definitionId : definitionIds) {
                returnDefinitionIds.remove(definitionId);
            }

            List<ReturnDefinition> definitionList = new ArrayList<>();
            for (ReturnDefinition rd : returnCacheManager.loadReturnDefinitionDependencies().get(returnDefinition.getId())) {
                for (Long returnDefinitionId : returnDefinitionIds) {
                    if (rd.getId() == returnDefinitionId) {
                        definitionList.add(rd);
                    }
                }
            }
            if (!definitionList.isEmpty()) {
                throw new FinATypeException(" Unable to create (import) return \"" + returnDefinition.getCode() + "\" which depends on following return(s):" + definitionList + ". Please create and/or import source returns  and repeat the action.");
            }
        }
    }

    @Override

    public void addReturnStatus(long returnId, ProcessStatus status, long versionId, long userId, String note, String processId) {
        try (StatisticsLogger statLog = new StatisticsLogger("Add Return Statuses")) {

            ReturnStatusLite returnStatus = new ReturnStatusLite();
            returnStatus.setReturnId(returnId);
            returnStatus.setStatus(status);
            returnStatus.setUserId(userId);
            returnStatus.setVersionId(versionId);
            returnStatus.setNote(note);
            returnStatus.setStatusDate(new Date());
            statLog.logStage("Persist status");
            em.persist(returnStatus);
            statLog.logStage("find return : " + returnId);
            Return ret = em.find(Return.class, returnId);
            statLog.logStage("set return status " + returnId);
            ret.setStatus(returnStatus.getStatus());
            statLog.logStage("merge return " + returnId);
            em.merge(ret);
            statLog.logStage("flush return " + returnStatus);
            em.flush();
            statLog.logStage("clear return " + returnStatus);
            em.clear();


            if (processId != null) {
                statLog.logStage("storeReturnStatus " + returnId);
                ReturnStatusStoreEvent statusStoreEvent = new ReturnStatusStoreEvent(processId, returnStatus.getId(), returnId, versionId, status, returnStatus.getStatusDate(), userId, note);
                returnStatusStoreEvent.fire(statusStoreEvent);
            }
        }

    }

    private void createReturnDefaultValues(Return r, String processId, final List<MDTDependentNode> allMdtDependentNodes, Map<Long, List<MDTNode>> allMdtNodesByParentId, final List<MDTComparison> comparisons) {
        Map<String, ReturnItemLite> itemLiteMap = xmlImportLocal.getReturnTemplate(r, allMdtNodesByParentId);
        for (ReturnItemLite item : itemLiteMap.values()) {
            em.persist(item);
        }

        if (processId != null) {
            ReturnItemsStoreEvent storeEvent = new ReturnItemsStoreEvent(null, itemLiteMap.values(), r.getId(), processId, allMdtDependentNodes, allMdtNodesByParentId, comparisons);
            returnStoreEvent.fire(storeEvent);
        }
    }

    @Override
    public void saveReturnXml(long returnId, long versionId, long langId, String name, String outFolderLocation) {
        net.fina.server.returns.xml.Return result = returnToXml(returnId, versionId, langId);

        try {

            JAXBContext context = JAXBContext.newInstance(net.fina.server.returns.xml.ObjectFactory.class.getPackage().getName());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            File file = new File(outFolderLocation + File.separator + name + ".xml");

            if (file.exists()) {
                file = File.createTempFile(name + "_", ".xml", new File(outFolderLocation));
            }

            marshaller.marshal(result, file);

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    @Override

    public net.fina.server.returns.xml.Return returnToXml(long returnId, long versionId, long langId) {
        net.fina.server.returns.xml.Return result = new net.fina.server.returns.xml.Return();

        Header header = loadReturnHeader(returnId, langId);
        result.setHeader(header);

        final List<MDTDependentNode> mdtDependentNodes = processingStoreLocal.loadAllMdtDependentNodes();
        final Map<Long, List<MDTNode>> allMdtNodesByParentId = processingStoreLocal.loadMdtNodesByParentId();

        Map<Long, ProcessItem> nodes = processingStoreLocal.loadReturnNodesValue(Collections.singletonList(returnId), versionId, mdtDependentNodes, langId, allMdtNodesByParentId);

        List<Item> items = new ArrayList<>();

        for (ProcessItem pItem : nodes.values()) {
            for (Map.Entry<Integer, Long> e : pItem.idByRowNumber.entrySet()) {
                Item item = new Item();
                item.setItemCode(pItem.code);
                item.setRow(e.getKey());
                item.setValue(pItem.values.get(e.getKey()));
                items.add(item);
            }
        }

        Body body = new Body();
        body.setItems(items);

        result.setBody(body);

        return result;
    }

    private Header loadReturnHeader(long returnId, long langId) {

        StringBuffer qlString = new StringBuffer();
        qlString.append("SELECT ");
        qlString.append(" r.schedule.fi.code, ");
        qlString.append(" r.schedule.fi.description, ");
        qlString.append(" r.schedule.returnDefinition.code, ");
        qlString.append(" r.schedule.returnDefinition.description, ");
        qlString.append(" r.schedule.period.fromDate, ");
        qlString.append(" r.schedule.period.toDate, ");
        qlString.append(" r.returnVersion.code ");
        qlString.append(" FROM IN_RETURNS as r WHERE r.id=:returnId ");

        Query query = em.createQuery(qlString.toString());
        query.setParameter("returnId", returnId);

        @SuppressWarnings("unchecked")
        List<Object[]> objects = query.getResultList();

        Header header = new Header();

        Language language = em.find(Language.class, langId);
        header.setLng(language.getCode() != null ? language.getCode().trim() : language.getCode());

        DateFormat dateFormat = new SimpleDateFormat(language.getDateFormat() != null ? language.getDateFormat().trim() : "dd/MM/yyyy");

        for (Object[] object : objects) {

            header.setBankCode(object[0] != null ? object[0].toString().trim() : null);

            Description bankName = object[1] != null ? (Description) object[1] : null;
            header.setBankName(bankName != null ? bankName.getDescription(langId) : null);

            header.setReturnCode(object[2] != null ? object[2].toString() : null);

            Description returnName = object[3] != null ? (Description) object[3] : null;
            header.setReturnName(returnName != null ? returnName.getDescription(langId) : null);

            header.setPeriodFrom(object[4] != null ? dateFormat.format((Date) object[4]) : null);

            header.setPeriodEnd(object[5] != null ? dateFormat.format((Date) object[5]) : null);

            header.setVer(object[6] != null ? object[6].toString() : null);

            header.setSigned("");
        }

        return header;

    }

    @Override

    public ReturnStatus loadReturnCurrentStatus(long returnId) {
        List<ReturnStatus> statuses = loadReturnStatuses(returnId, true);
        return statuses.size() > 0 ? statuses.get(0) : null;
    }

    @Override

    public List<ReturnStatus> loadReturnStatuses(long returnId) {
        return loadReturnStatuses(returnId, false);
    }

    @SuppressWarnings("unchecked")
    private List<ReturnStatus> loadReturnStatuses(long returnId, boolean current) {

        StringBuffer qlString = new StringBuffer();

        qlString.append("SELECT ");
        qlString.append(" NEW ");
        qlString.append(ReturnStatus.class.getName());
        qlString.append(" ( ");
        qlString.append(" rs.id, ");
        qlString.append(" rs.returnVersion.id, ");
        qlString.append(" rs.returnVersion.code, ");
        qlString.append(" rs.returnVersion.description, ");
        qlString.append(" rs.status, ");
        qlString.append(" rs.statusDate, ");
        qlString.append(" rs.user.login, ");
        qlString.append(" rs.note ");
        qlString.append(" ) ");
        qlString.append(" FROM ");
        qlString.append(" IN_RETURNS as r, IN(r.statuses) rs ");
        qlString.append(" WHERE r.id=:returnId ");

        if (current) {
            qlString.append(" AND rs.statusDate=(select MAX(irs.statusDate) from IN_RETURN_STATUSES as irs where irs.returns.id=r.id) ");
        }
        qlString.append(" ORDER BY rs.statusDate DESC ");

        Query query = em.createQuery(qlString.toString());
        query.setParameter("returnId", returnId);

        return query.getResultList();
    }

    @Override
    public List<String> getReturnHeader(long returnId, long langId) {
        StringBuffer qlString = new StringBuffer();

        qlString.append("SELECT ");
        qlString.append(" r.schedule.returnDefinition.code, ");
        qlString.append(" r.schedule.returnDefinition.description, ");
        qlString.append(" r.schedule.fi.code, ");
        qlString.append(" r.schedule.fi.description,");
        qlString.append(" r.schedule.period.fromDate,");
        qlString.append(" r.schedule.period.toDate, ");
        qlString.append(" r.returnVersion.code, ");
        qlString.append(" rs.status, ");
        qlString.append(" r.schedule.period.periodType.code, ");
        qlString.append(" r.schedule.period.periodType.description,");
        qlString.append(" r.returnVersion.description, ")
                .append(" r.schedule.returnDefinition.returnType.code ")
                .append(" FROM IN_RETURNS as r, IN_RETURN_STATUSES as rs");
        qlString.append(" WHERE ");
        qlString.append(" r.id=:returnId ");
        qlString.append(" AND rs.id=(select MAX(irs.id) from IN_RETURN_STATUSES as irs where irs.returns.id=r.id) ");

        Query query = em.createQuery(qlString.toString());
        query.setParameter("returnId", returnId);

        Language language = em.find(Language.class, langId);

        DateFormat df = new SimpleDateFormat(language.getDateFormat().trim());

        List<String> header = new ArrayList<>();

        @SuppressWarnings("unchecked")
        List<Object[]> list = query.getResultList();

        if (list.size() > 0) {

            MessagesUtil.initMessageBundle(language.getCode());

            Object[] objects = list.get(0);

            header.add(objects[0] != null ? objects[0].toString() : null);

            if (objects[1] != null) {
                header.add(((Description) objects[1]).getDescription(langId));
            } else {
                header.add(null);
            }

            header.add(objects[2] != null ? objects[2].toString() : null);

            if (objects[3] != null) {
                header.add(((Description) objects[3]).getDescription(langId));
            } else {
                header.add(null);
            }

            if (objects[4] != null) {
                header.add(df.format((Date) objects[4]));
            } else {
                header.add(null);
            }

            if (objects[5] != null) {
                header.add(df.format((Date) objects[5]));
            } else {
                header.add(null);
            }

            if (objects[6] != null) {
                header.add(objects[6] != null ? objects[6].toString() : null);
            } else {
                header.add(null);
            }

            if (objects[7] != null) {
                header.add(MessagesUtil.getString(((ProcessStatus) objects[7]).getCode()));
            } else {
                header.add(null);
            }

            if (objects[8] != null) {
                header.add(objects[8] != null ? objects[8].toString() : null);
            } else {
                header.add(null);
            }

            if (objects[9] != null) {
                header.add(((Description) objects[9]).getDescription(langId));
            } else {
                header.add(null);
            }
            if (objects[10] != null) {
                header.add(((Description) objects[10]).getDescription(langId));
            } else {
                header.add(null);
            }
            if (objects[11] != null) {
                header.add(objects[11].toString());
            } else {
                header.add(null);
            }
            User currentUser = userLocal.getCurrentUser();
            header.add(currentUser.getLogin());
            header.add(currentUser.getDescription().getDescription(langId));
        }

        return header;
    }

    @Override
    public void delete(Collection<Long> returnIds) {
        if (returnIds != null && !returnIds.isEmpty()) {
            List<Long> deleteReturnIds = null;

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);

            Root<Return> root = criteriaQuery.from(Return.class);
            Join<Return, Schedule> scheduleJoin = root.join(Return_.schedule);
            Join<Schedule, ReturnDefinition> returnDefinitionJoin = scheduleJoin.join(Schedule_.returnDefinition);
            Join<Schedule, Fi> fiJoin = scheduleJoin.join(Schedule_.fi);
            Join<Return, ReturnVersion> returnVersionJoin = root.join(Return_.returnVersion);

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(root.get(Return_.id).in(returnIds));

            Collection<Long> returnDefinitionIds = userLocal.getCallerPrincipal().getReturnDefinitions();
            List<Predicate> definitionPredicates = DBUtil.get().buildAndSplitPredicates(cb, returnDefinitionJoin.get(ReturnDefinition_.id), Long.class, returnDefinitionIds);
            predicates.add(cb.and(cb.or(definitionPredicates.toArray(new Predicate[0]))));

            predicates.add(returnVersionJoin.get(ReturnVersion_.id).in(userLocal.getCallerPrincipal().getReturnVersions().keySet()));

            List<Predicate> fiPredList = DBUtil.get().buildAndSplitPredicates(cb, fiJoin.get(Fi_.id), Long.class, userLocal.getCallerPrincipal().getFis());
            predicates.add(cb.and(cb.or(fiPredList.toArray(new Predicate[0]))));

            criteriaQuery.where(predicates.toArray(new Predicate[0]));
            criteriaQuery.select(root.get(Return_.id));

            TypedQuery<Long> query = em.createQuery(criteriaQuery);
            deleteReturnIds = query.getResultList();

            if (!deleteReturnIds.isEmpty()) {
                //Correcting return save as last versions
                for (long returnId : deleteReturnIds) {
                    correctingReturnLastVersion(returnId);
                }

                //Delete return(s) Items
                em.createQuery("delete from IN_RETURN_ITEMS  ri where ri.returns.id in(:returnIds) ")
                        .setParameter("returnIds", deleteReturnIds)
                        .executeUpdate();

                //Delete return(s) Statuses
                em.createQuery("delete from IN_RETURN_STATUSES rs where rs.returns.id in(:returnIds) ")
                        .setParameter("returnIds", deleteReturnIds)
                        .executeUpdate();

                //Delete return(s)
                em.createQuery("delete from IN_RETURNS r where r.id in(:returnIds) ")
                        .setParameter("returnIds", deleteReturnIds)
                        .executeUpdate();

                //Delete XML's return(s)
                em.createQuery("delete from IN_IMPORTED_XML_RETURN ixr where ixr.returnId in(:returnId) ")
                        .setParameter("returnId", deleteReturnIds)
                        .executeUpdate();

                // audit log
                auditLogAddManualRecordIdsEvent.fire(new AuditLogAddManualRecordIdsEvent(deleteReturnIds, OperationType.DELETE, new Return()));
            }
        }
    }

    /**
     * @param returnId
     */
    private void correctingReturnLastVersion(long returnId) {
        List<Object[]> returnObjectsList = em.createQuery("select r.schedule.id,r.returnVersion.id from IN_RETURNS r where r.id=:returnId", Object[].class)
                .setParameter("returnId", returnId)
                .getResultList();
        for (Object[] returnObjects : returnObjectsList) {
            long scheduleId = (long) returnObjects[0];
            long versionId = (long) returnObjects[1];
            em.createQuery("update IN_RETURNS r set r.latestVersion=r.returnVersion.id where r.latestVersion<>r.returnVersion.id and r.id<>:returnId and r.schedule.id=:scheduleId and r.latestVersion=:versionId ")
                    .setParameter("returnId", returnId)
                    .setParameter("scheduleId", scheduleId)
                    .setParameter("versionId", versionId)
                    .executeUpdate();
        }
    }

    @Override

    public List<ImportedXmlReturnModel> loadImportedXmlReturns(long returnId) {
        Query query = em.createQuery("Select x,r from IN_IMPORTED_XML_RETURN x, IN_IMPORTED_RETURNS r where x.returnId=:returnId and x.importedReturnId = r.id ").setParameter("returnId", returnId);
        List list = query.getResultList();
        List<ImportedXmlReturnModel> importedXmlReturn = new ArrayList<>();
        for (Object o : list) {
            Object[] result = (Object[]) o;
            ImportedReturn aReturn = (ImportedReturn) result[1];
            ImportModel importModel = new ImportModel();
            importModel.setId(aReturn.getId());
            importModel.setBankCode(aReturn.getBankCode());
            importModel.setVersionCode(aReturn.getVersionCode());
            importModel.setPeriodStart(aReturn.getPeriodStart());
            importModel.setPeriodEnd(aReturn.getPeriodEnd());

            //TODO
            if (aReturn.getUploadFile() != null) {
                importModel.setFileName(aReturn.getUploadFile().getFileName());
                importModel.setFileId(aReturn.getUploadFile().getId());
            } else {
                importModel.setFileName("Undefined");
                importModel.setFileId(0);
            }

            importModel.setUploadTime(aReturn.getUploadTime());

            ImportedXmlReturn xmlReturn = (ImportedXmlReturn) result[0];

            ImportedXmlReturnModel model = new ImportedXmlReturnModel();
            model.setReturnId(returnId);
            model.setImportModel(importModel);
            model.setImportTime(xmlReturn.getImportTime());
            model.setStatus(xmlReturn.getStatus());


            importedXmlReturn.add(model);
        }

        importedXmlReturn.sort((a, b) -> b.getImportTime().compareTo(a.getImportTime()));
        return importedXmlReturn;
    }

    @Override

    public Map<Long, Long> gerReturnReturnDefinitionIds(List<Long> retruns) {
        List<Object[]> objectList = em.createQuery("select r.schedule.returnDefinition.id,r.id from IN_RETURNS r where r.id in(:returns)", Object[].class)
                .setParameter("returns", retruns)
                .getResultList();
        Map<Long, Long> result = new HashMap<>();
        for (Object[] objects : objectList) {
            result.put((long) objects[0], (Long) objects[1]);
        }
        return result;
    }


    @Override

    public List<ReturnStatusesModel> getReturnStatuses(long periodId, List<Long> fiIds, List<Long> definitionIds, long returnVersionId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ReturnStatusesModel> query = cb.createQuery(ReturnStatusesModel.class);
        Root<ReturnStatus> status = query.from(ReturnStatus.class);

        Join<ReturnStatus, Return> returnStatusReturnJoin = status.join(ReturnStatus_.returns, JoinType.INNER);
        Join<ReturnStatus, ReturnVersion> statusVersionJoin = status.join(ReturnStatus_.returnVersion, JoinType.INNER);
        Join<Return, Schedule> returnScheduleJoin = returnStatusReturnJoin.join(Return_.schedule, JoinType.INNER);
        Join<Schedule, Period> schedulePeriodJoin = returnScheduleJoin.join(Schedule_.period, JoinType.INNER);
        Join<Schedule, ReturnDefinition> scheduleReturnDefinitionJoin = returnScheduleJoin.join(Schedule_.returnDefinition, JoinType.INNER);
        Join<Schedule, Fi> scheduleFiJoin = returnScheduleJoin.join(Schedule_.fi);

        Subquery<Long> subQuery = query.subquery(Long.class);
        Root<ReturnStatus> subStatus = subQuery.from(ReturnStatus.class);
        subQuery.select(cb.max(subStatus.get(ReturnStatus_.id)));
        subQuery.where(cb.equal(status.get(ReturnStatus_.returns).get(Return_.id), subStatus.get(ReturnStatus_.returns).get(Return_.id)));

        Selection[] selections = new Selection[]{
                status.get(ReturnStatus_.id),
                scheduleFiJoin.get(Fi_.id),
                scheduleFiJoin.get(Fi_.code),
                scheduleReturnDefinitionJoin.get(ReturnDefinition_.id),
                scheduleReturnDefinitionJoin.get(ReturnDefinition_.code),
                status.get(ReturnStatus_.status),
                status.get(ReturnStatus_.statusDate)
        };
        query.select(cb.construct(ReturnStatusesModel.class, selections));


        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(status.get(ReturnStatus_.id), subQuery));
        predicates.add(cb.equal(schedulePeriodJoin.get(Period_.id), periodId));
        predicates.add(cb.equal(statusVersionJoin.get(ReturnVersion_.id), returnVersionId));
        if (definitionIds != null && !definitionIds.isEmpty()) {
            List<Predicate> definitionPredicates = DBUtil.get().buildAndSplitPredicates(cb, scheduleReturnDefinitionJoin.get(ReturnDefinition_.id), Long.class, definitionIds);
            predicates.add(cb.and(cb.or(definitionPredicates.toArray(new Predicate[0]))));
        }
        if (fiIds != null && !fiIds.isEmpty()) {
            List<Predicate> fiPredList = DBUtil.get().buildPredicate(cb, scheduleFiJoin, fiIds);
            predicates.add(cb.and(cb.or(fiPredList.toArray(new Predicate[0]))));
        }

        query.where(predicates.toArray(new Predicate[0]));

        return em.createQuery(query).getResultList();
    }

    @Override

    public long getReturnsCount(List<Long> fiIds, long definitionId, long typeId, long versionId, ProcessStatus status, Date periodFrom, Date periodTo) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Return> root = query.from(Return.class);

        query.select(cb.count(root.get(Return_.id)));

        Join<Return, Schedule> returnScheduleJoin = root.join(Return_.schedule, JoinType.INNER);
        Join<Return, ReturnVersion> returnVersionJoin = root.join(Return_.returnVersion, JoinType.INNER);
        Join<Schedule, Fi> scheduleFiJoin = returnScheduleJoin.join(Schedule_.fi, JoinType.INNER);
        Join<Schedule, ReturnDefinition> scheduleDefinitionJoin = returnScheduleJoin.join(Schedule_.returnDefinition, JoinType.INNER);
        Join<Schedule, Period> schedulePeriodJoin = returnScheduleJoin.join(Schedule_.period, JoinType.INNER);
        Join<ReturnDefinition, ReturnType> definitionTypeJoin = scheduleDefinitionJoin.join(ReturnDefinition_.returnType, JoinType.INNER);

        List<Predicate> predicates = new ArrayList<>();

        if (status != null) {
            Join<Return, ReturnStatus> returnStatusJoin = root.join(Return_.statuses, JoinType.INNER);

            //Return statuses sub query
            Subquery<Long> subQuery = query.subquery(Long.class);
            Root<ReturnStatus> subStatus = subQuery.from(ReturnStatus.class);
            subQuery.select(cb.max(subStatus.get(ReturnStatus_.id)));

            Predicate[] subPredicates = new Predicate[2];
            subPredicates[0] = cb.equal(returnStatusJoin.get(ReturnStatus_.returns).get(Return_.id), subStatus.get(ReturnStatus_.returns).get(Return_.id));
            subPredicates[1] = cb.equal(returnStatusJoin.get(ReturnStatus_.status), status);
            subQuery.where(subPredicates);

            predicates.add(cb.equal(returnStatusJoin.get(ReturnStatus_.id), subQuery));
        }

        if (fiIds != null && !fiIds.isEmpty()) {
            predicates.add(scheduleFiJoin.get(Fi_.id).in(fiIds));
        }
        if (definitionId > 0) {
            predicates.add(cb.equal(scheduleDefinitionJoin.get(ReturnDefinition_.id), definitionId));
        }
        if (versionId > 0) {
            predicates.add(cb.equal(returnVersionJoin.get(ReturnVersion_.id), versionId));
        }
        if (typeId > 0) {
            predicates.add(cb.equal(definitionTypeJoin.get(ReturnType_.id), typeId));
        }
        if (periodFrom != null) {
            predicates.add(cb.equal(schedulePeriodJoin.get(Period_.fromDate), periodFrom));
        }
        if (periodTo != null) {
            predicates.add(cb.equal(schedulePeriodJoin.get(Period_.toDate), periodTo));
        }

        if (!predicates.isEmpty()) {
            query.where(predicates.toArray(new Predicate[0]));
        }

        return em.createQuery(query).getSingleResult();
    }

    @Override

    public long getReturnScheduleId(long returnId) {
        return em.createQuery("select r.schedule.id from IN_RETURNS r where r.id=:returnId", Long.class).setParameter("returnId", returnId).getSingleResult();
    }

    @Override

    public ProcessStatus getXmlReturnProcessStatus(int importedReturnId, String returnVersionCode) {
        ProcessStatus result = null;
        try {
            Query query = em.createQuery("select irs.status from IN_IMPORTED_XML_RETURN ixr, " +
                            "IN_RETURN_STATUSES irs where ixr.status=:status and ixr.returnId=irs.returns.id and ixr.importedReturnId=:importedReturnId " +
                            "and irs.returnVersion.code=:returnVersionCode order by irs.statusDate desc ", ProcessStatus.class)
                    .setFirstResult(0)
                    .setMaxResults(1)
                    .setParameter("importedReturnId", importedReturnId)
                    .setParameter("returnVersionCode", returnVersionCode)
                    .setParameter("status", ImportedXmlReturnStatus.CURRENT);
            List<ProcessStatus> resultList = query.getResultList();
            result = resultList.isEmpty() ? null : resultList.get(0);
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
        return result;
    }

    @Override

    public long getReturnId(long importedXlretunId) {
        List<Long> ids = em.createQuery("select ixr.returnId from IN_IMPORTED_XML_RETURN  ixr where ixr.importedReturnId=:id", Long.class)
                .setParameter("id", (int) importedXlretunId).getResultList();
        if (ids.isEmpty()) {
            return -1L;
        }
        return ids.get(0);
    }

    @Override
    public List<Object[]> searchReturnItems(Map<ReturnItemsSearchFilter, Object> filterObjectMap, int limit, int offset) {
        StringBuilder query = new StringBuilder();

        query.append("SELECT DISTINCT ");
        query.append("ri.returns.schedule.returnDefinition.code, ");
        query.append("ri.returns.schedule.fi.code, ");
        query.append("ri.returns.schedule.period.fromDate, ");
        query.append("ri.returns.schedule.period.toDate, ");
        query.append("ri.returnVersion.code, ");
        query.append("ri.mdtNode.code, ");
        query.append("ri.mdtNode.dataType, ");
        query.append("ri.nValue, ");
        query.append("ri.value, ");

        StringBuilder fromQuery = new StringBuilder();
        fromQuery.append("FROM IN_RETURN_ITEMS ri, ");
        fromQuery.append("IN_DEFINITION_TABLES t ");

        StringBuilder whereQuery = new StringBuilder();
        whereQuery.append("WHERE ");
        whereQuery.append("ri.returns.schedule.returnDefinition.id=:defId ");
        whereQuery.append("AND t.id=:tableId ");
        whereQuery.append("AND t.returnDefinition.id=ri.returns.schedule.returnDefinition.id ");
        whereQuery.append("AND ri.tableId=t.id ");
        whereQuery.append("AND ri.returns.schedule.fi.id in(:fis) ");
        whereQuery.append("AND trim(ri.mdtNode.code)=:nodeCode ");
        whereQuery.append("AND ri.returns.schedule.period.fromDate=:periodFrom ");
        whereQuery.append("AND ri.returns.schedule.period.toDate=:periodTo ");
        whereQuery.append("AND ri.returnVersion.id=:versionId ");
        whereQuery.append("AND ri.value like :searchVal ");

        List<String> selectedFields = (List<String>) filterObjectMap.get(ReturnItemsSearchFilter.selectedFields);
        for (int i = 1; i <= selectedFields.size(); i++) {
            query.append("ri").append(i).append(".mdtNode.code, ");
            query.append("ri").append(i).append(".mdtNode.dataType, ");
            query.append("ri").append(i).append(".nValue, ");
            query.append("ri").append(i).append(".value, ");

            fromQuery.append(", IN_RETURN_ITEMS ri").append(i).append(" ");
            whereQuery.append("AND ri.returns.id = ri").append(i).append(".returns.id ");
            whereQuery.append("AND ri.tableId = ri").append(i).append(".tableId ");
            whereQuery.append("AND ri.rowNumber = ri").append(i).append(".rowNumber ");
            whereQuery.append("AND ri.returnVersion.id = ri").append(i).append(".returnVersion.id ");
            whereQuery.append("AND trim(ri").append(i).append(".mdtNode.code) = '").append(selectedFields.get(i - 1).trim()).append("' ");
        }

        query.append("t.code ");

        Query q = em.createQuery(query.toString() + fromQuery.toString() + whereQuery.toString(), Object[].class);
        q.setParameter("defId", filterObjectMap.get(ReturnItemsSearchFilter.returnId));
        q.setParameter("tableId", filterObjectMap.get(ReturnItemsSearchFilter.table));
        q.setParameter("fis", filterObjectMap.get(ReturnItemsSearchFilter.fis));
        q.setParameter("nodeCode", filterObjectMap.get(ReturnItemsSearchFilter.field));
        q.setParameter("periodFrom", filterObjectMap.get(ReturnItemsSearchFilter.periodFrom));
        q.setParameter("periodTo", filterObjectMap.get(ReturnItemsSearchFilter.periodTo));
        q.setParameter("versionId", filterObjectMap.get(ReturnItemsSearchFilter.version));
        q.setParameter("searchVal", filterObjectMap.get(ReturnItemsSearchFilter.searchValue));
        q.setFirstResult(offset);
        q.setMaxResults(limit);

        return q.getResultList();
    }

    @Override
    public long getSearchReturnItemsCount(Map<ReturnItemsSearchFilter, Object> filterObjectMap) {
        return em.createQuery("SELECT COUNT(ri) FROM IN_RETURN_ITEMS ri WHERE ri.returns.schedule.returnDefinition.id=:definitionId AND ri.tableId=:tableId AND ri.returns.schedule.fi.id in (:fis) AND trim(ri.mdtNode.code)=:mdtCode AND ri.returns.schedule.period.fromDate=:periodFrom AND ri.returns.schedule.period.toDate=:periodTo AND ri.returnVersion.id=:versionId AND ri.value like :searchVal", Long.class)
                .setParameter("definitionId", filterObjectMap.get(ReturnItemsSearchFilter.returnId))
                .setParameter("tableId", filterObjectMap.get(ReturnItemsSearchFilter.table))
                .setParameter("fis", filterObjectMap.get(ReturnItemsSearchFilter.fis))
                .setParameter("mdtCode", filterObjectMap.get(ReturnItemsSearchFilter.field))
                .setParameter("periodFrom", filterObjectMap.get(ReturnItemsSearchFilter.periodFrom))
                .setParameter("periodTo", filterObjectMap.get(ReturnItemsSearchFilter.periodTo))
                .setParameter("versionId", filterObjectMap.get(ReturnItemsSearchFilter.version))
                .setParameter("searchVal", filterObjectMap.get(ReturnItemsSearchFilter.searchValue))
                .getSingleResult();
    }


    @Override
    public List<OverdueReturnModel> loadOverdueReturnModels(Map<OverdueReturnFilter, Object> filter, long langId) {
        return loadOverdueReturnModels(filter, langId, -1, -1, null);
    }

    @Override
    public List<OverdueReturnModel> loadOverdueReturnModels(Map<OverdueReturnFilter, Object> filter, long langId, int offset, int limit, SortInfo sortInfo) {

        StringBuilder selectQuery = new StringBuilder();
        selectQuery.append("select distinct new net.fina.common.client.returns.OverdueReturnModel(")
                .append("b.code,s.delay,s.delayHour,s.delayMinute,p.fromDate,p.toDate,pt.code,irv.code,suf.fileName,suf.uploadedTime,suf.id, b.fiType.code) ");


        StringBuilder fromQuery = new StringBuilder();
        fromQuery.append("from ")
                .append("IN_SCHEDULES s,")
                .append("IN_PERIODS p,")
                .append("IN_PERIOD_TYPES pt,")
                .append("IN_BANKS b,")
                .append("IN_RETURN_DEFINITIONS ird,")
                .append("IN_RETURNS r,")
                .append("IN_IMPORTED_RETURNS ir,")
                .append("IN_RETURN_VERSIONS irv,")
                .append("SYS_UPLOADEDFILE suf ");

        StringBuilder whereQuery = new StringBuilder();
        whereQuery.append("where ")
                .append("s.period.id=p.id ")
                .append("and s.fi.id=b.id ")
                .append("and s.returnDefinition.id=ird.id ")
                .append("and r.schedule.id=s.id ")
                .append("and r.returnVersion.id=irv.id ")
                .append("and ird.code=ir.returnCode ")
                .append("and p.fromDate=ir.periodStart ")
                .append("and p.toDate=ir.periodEnd ")
                .append("and p.periodType.id=pt.id ")
                .append("and b.code=ir.bankCode ")
                .append("and ir.versionCode=irv.code ")
                .append("and ir.uploadFile.id=suf.id ")
                .append("and suf.status=:uploadFileStatus ")
                .append(" and suf.uploadedTime > s.delayToDate ")
                .append(" and b.disable=false ");

        StringBuilder groupQuery = new StringBuilder();
        groupQuery.append(" group by ")
                .append("b.code, s.delay, s.delayHour, s.delayMinute, p.fromDate, p.toDate, pt.code,")
                .append("ir.status, irv.code, suf.fileName, suf.uploadedTime, suf.id, b.fiType.code ")
                .append("order by suf.uploadedTime");

        // dynamic filter and sort
        constructOverdueReturnsFilter(filter, whereQuery);
        constructOverdueReturnsSortPath(sortInfo, groupQuery);


        TypedQuery<OverdueReturnModel> query = em.createQuery(selectQuery.toString() + fromQuery.toString() + whereQuery.toString() + groupQuery.toString(), OverdueReturnModel.class);
        query.setParameter("uploadFileStatus", "18");
        if (filter != null && !filter.isEmpty()) {
            if (filter.containsKey(OverdueReturnFilter.fiCode)) {
                query.setParameter(OverdueReturnFilter.fiCode.name(), "%" + filter.get(OverdueReturnFilter.fiCode).toString() + "%");
                filter.remove(OverdueReturnFilter.fiCode);
            }
            for (Map.Entry<OverdueReturnFilter, Object> entry : filter.entrySet()) {
                if (entry.getValue() != null && !entry.getValue().toString().isEmpty()) {
                    query.setParameter(entry.getKey().name(), entry.getValue());
                }
            }
        }

        List<OverdueReturnModel> queryResultList = query.getResultList();

        if (limit <= 0) {
            limit = queryResultList.size();
        }

        if (!queryResultList.isEmpty()) {
            Map<Long, Region> regionIds = regionLocal.loadRegionsIdMap();
            RegionUtil regionUtil = new RegionUtil();
            for (int i = offset; i < limit + offset && i < queryResultList.size(); i++) {
                OverdueReturnModel model = queryResultList.get(i);
                model.setDelay(scheduleLocal.getOverdueNumberOfDays(model.getDueDate(), model.getDueDateHour(), model.getDueDateMinute(), model.getToDate(), model.getUploadedTime()));
                Fi fi = fiLocal.findFiByCode(model.getFiCode());
                if (fi != null) {
                    model.setAddress(fi.getAddressDescription().getDescription(langId));
                    model.setFiName(fi.getDescription().getDescription(langId));
                    model.setFiType(fi.getFiType().getCode());
                    model.setRegion(regionUtil.getFullRegionName(regionIds, fi.getRegionId(), langId, null));
                    model.setFiLegalForm(fi.getLegalForm());
                    model.setFiIdentificationCode(fi.getIdentificationCode());
                }
            }
        }

        return queryResultList;
    }

    @Override
    public List<ReturnSubmissionPackageModel> loadReturnSubmissionPackages(ReturnSubmissionPackageModel filterModel, long langId, int offset, int limit) {
        StringBuilder sb = new StringBuilder();
        List<Integer> importedReturnIds = em.createQuery("select  max(iixr.importedReturnId)  from IN_IMPORTED_XML_RETURN iixr" +
                        " left join IN_IMPORTED_RETURNS iir on iixr.importedReturnId = iir.id" +
                        " where iixr.status = 0 group by iir.uploadFile.id", Integer.class)
                .getResultList();
        importedReturnIds = importedReturnIds.isEmpty() ? Collections.singletonList(0) : importedReturnIds;
        String orderByParam = " order by iir.uploadTime desc";
        if (filterModel.getSortField() != null) {
            SortField sortField = filterModel.getSortField();
            switch (sortField.getProperty()) {
                case "bankCode":
                case "versionCode":
                case "uploadTime":
                    orderByParam = " order by iir." + sortField.getProperty() + " " + sortField.getDirection();
                    break;
                case "toDate":
                    orderByParam = " order by iir.periodEnd " + sortField.getDirection();
                    break;
                case "bankName":
                    orderByParam = " order by iir.bankCode " + sortField.getDirection();
                    break;
                case "fileName":
                case "status":
                    orderByParam = " order by iir.uploadFile." + sortField.getProperty() + " " + sortField.getDirection();
                    break;
                default:
                    orderByParam = " order by iir.uploadTime desc";
                    break;
            }
        }

        sb.append("select iir,ib.description from IN_IMPORTED_RETURNS iir,IN_BANKS ib ")
                .append(" where iir.id in (:importedReturnIds) and iir.bankCode=ib.code")
                .append(filterModel.getFiCodes() != null && !filterModel.getFiCodes().isEmpty() ? " and iir.bankCode in (:bankCodes)" : "")
                .append(filterModel.getFromDate() != null ? " and iir.periodStart>=:fromDate" : "")
                .append(filterModel.getToDate() != null ? " and iir.periodEnd<=:toDate" : "")
                .append(filterModel.getVersionCode() != null ? " and iir.versionCode=:versionCode" : "")
                .append(filterModel.getFileName() != null && !filterModel.getFileName().trim().isEmpty() ? " and iir.uploadFile.fileName like :fileName" : "")
                .append(orderByParam);

        Query query = em.createQuery(sb.toString());

        query.setParameter("importedReturnIds", importedReturnIds);
        if (filterModel.getFiCodes() != null && !filterModel.getFiCodes().isEmpty()) {
            query.setParameter("bankCodes", filterModel.getFiCodes());
        }
        if (filterModel.getFromDate() != null) {
            query.setParameter("fromDate", filterModel.getFromDate());
        }
        if (filterModel.getToDate() != null) {
            query.setParameter("toDate", filterModel.getToDate());
        }
        if (filterModel.getVersionCode() != null) {
            query.setParameter("versionCode", filterModel.getVersionCode());
        }
        if (filterModel.getFileName() != null && !filterModel.getFileName().trim().isEmpty()) {
            query.setParameter("fileName", "%" + filterModel.getFileName() + "%");
        }

        query.setFirstResult(offset);
        query.setMaxResults(limit);

        List<Object[]> queryResultList = query.getResultList();
        List<ReturnSubmissionPackageModel> result = new ArrayList<>();


        queryResultList.forEach(obj -> {
            ReturnSubmissionPackageModel model = new ReturnSubmissionPackageModel();
            ImportedReturn importedReturn = (ImportedReturn) obj[0];
            Description fiDescription = (Description) obj[1];
            model.setId(importedReturn.getId());
            model.setBankCode(importedReturn.getBankCode());
            model.setBankName(fiDescription.getDescription(langId));
            model.setVersionCode(importedReturn.getVersionCode());
            model.setFileName(importedReturn.getUploadFile().getFileName());
            model.setFromDate(importedReturn.getPeriodEnd());
            model.setToDate(importedReturn.getPeriodEnd());
            model.setUploadTime(importedReturn.getUploadTime());
            model.setStatus(UploadFileStatus.getStatus(importedReturn.getUploadFile().getStatus()));
            result.add(model);
        });

        return result;
    }

    @Override
    public int countReturnSubmissionPackages(ReturnSubmissionPackageModel filterModel) {
        StringBuilder sb = new StringBuilder();
        sb.append("select  count(iixr.importedReturnId)  from IN_IMPORTED_XML_RETURN iixr left join IN_IMPORTED_RETURNS iir on iixr.importedReturnId = iir.id where iixr.status = 0")
                .append(filterModel.getFiCodes() != null && !filterModel.getFiCodes().isEmpty() ? " and iir.bankCode in (:bankCodes)" : "")
                .append(filterModel.getFromDate() != null ? " and iir.periodStart>=:fromDate" : "")
                .append(filterModel.getToDate() != null ? " and iir.periodEnd<=:toDate" : "")
                .append(filterModel.getVersionCode() != null ? " and iir.versionCode=:versionCode" : "")
                .append("  group by iir.uploadFile.id");

        Query query = em.createQuery(sb.toString());

        if (filterModel.getFiCodes() != null && !filterModel.getFiCodes().isEmpty()) {
            query.setParameter("bankCodes", filterModel.getFiCodes());
        }
        if (filterModel.getFromDate() != null) {
            query.setParameter("fromDate", filterModel.getFromDate());
        }
        if (filterModel.getToDate() != null) {
            query.setParameter("toDate", filterModel.getToDate());
        }
        if (filterModel.getVersionCode() != null) {
            query.setParameter("versionCode", filterModel.getVersionCode());
        }

        return query.getResultList().size();

    }

    @Override
    public Long getUploadedFileIdByPackage(long fiId, long periodId, long returnTypeId, long returnVersionId) {

        TypedQuery<Long> query = em.createQuery("select MAX(suf.id) from " +
                "IN_SCHEDULES s,IN_PERIODS p,IN_PERIOD_TYPES pt,IN_BANKS b,IN_RETURN_DEFINITIONS ird,IN_RETURNS r," +
                "IN_IMPORTED_RETURNS ir,IN_RETURN_VERSIONS irv,SYS_UPLOADEDFILE suf,IN_IMPORTED_XML_RETURN ixr " +
                "where b.id=:fiId and p.id=:periodId and ird.returnType.id=:returnTypeId and " +
                "irv.id=:versionId and ixr.importedReturnId=ir.id and ixr.returnId=r.id and " +
                "ixr.status=:importedXmlReturnStatus and s.period.id=p.id and s.fi.id=b.id and " +
                "s.returnDefinition.id=ird.id and r.schedule.id=s.id and r.returnVersion.id=irv.id " +
                "and ird.code=ir.returnCode and p.fromDate=ir.periodStart and p.toDate=ir.periodEnd " +
                "and p.periodType.id=pt.id and b.code=ir.bankCode and ir.versionCode=irv.code and " +
                "ir.uploadFile.id=suf.id", Long.class);
        query.setParameter("fiId", fiId);
        query.setParameter("periodId", periodId);
        query.setParameter("returnTypeId", returnTypeId);
        query.setParameter("versionId", returnVersionId);
        query.setParameter("importedXmlReturnStatus", ImportedXmlReturnStatus.CURRENT);

        List<Long> result = query.getResultList();
        if (result != null && !result.isEmpty()) {
            return result.get(0);
        }
        return null;
    }

    @Override
    public long getNotCreatedPackagesCount(FilterConfig config) {
        // TODO - rewrite
        return loadNotCreatedPackages(config).size();
    }

    @Override
    public MiChartDataModel loadMiChartData() {
        MiChartDataModel result = new MiChartDataModel();

        long totalSchedules = countSchedules();
        result.setTotal((int) totalSchedules);

        if (totalSchedules > 0) {
            Map<ProcessStatus, Long> statusCounts = countReturnsByStatus();

            long inProgressCount = statusCounts.getOrDefault(ProcessStatus.STATUS_CREATED, 0L);
            long totalReturnsExisting = statusCounts.values().stream().mapToLong(Long::longValue).sum();
            long awaitingCount = totalSchedules - totalReturnsExisting;


            result.setInProgress((int) inProgressCount);
            result.setAwaiting((int) awaitingCount);
        }

        result.setTotalFormsSubmitted(countManualInputSubmittedReturns());

        return result;
    }

    @Override
    public List<ReturnModel> loadFiAwaitingReturns(List<Schedule> schedules) {
        schedules = schedules == null ? loadCurrentSubmissionPeriodSchedules(true) : schedules;
        if (schedules.isEmpty()) {
            return new ArrayList<>();
        }
        CustomPrincipal principal = userLocal.getCallerPrincipal();

        Set<Long> userVersionsIds = principal.getReturnVersions().keySet();
        String schedulesInStatement = DBUtil.get().generateConcatenatedInStatements("r.schedule.id", schedules, "id");

        List<Schedule> createdReturnSchedules = em.createQuery("select r.schedule from IN_RETURNS r,IN(r.statuses) rs where  rs.id=(select MAX(rs.id) from IN_RETURN_STATUSES rs where rs.returns.id=r.id) and rs.status not in(:statuses) and " + schedulesInStatement, Schedule.class)
                .setParameter("statuses", Arrays.asList(ProcessStatus.STATUS_CREATED))
                .getResultList();


        List<ReturnModel> returnModels = schedules.stream().filter(s -> !createdReturnSchedules.contains(s)).map(schedule -> {
            ReturnModel r = new ReturnModel();
            r.setFiId(schedule.getFi().getId());
            r.setFiCode(schedule.getFi().getCode());
            r.setDelay(schedule.getDelay());
            r.setDelayHour(schedule.getDelayHour());
            r.setDelayMinute(schedule.getDelayMinute());
            r.setPeriodId(schedule.getPeriod().getId());
            r.setToDate(schedule.getPeriod().getToDate());
            r.setReturnTypeCode(schedule.getReturnDefinition().getReturnType().getCode());
            r.setReturnTypeId(schedule.getReturnDefinition().getId());
            r.setDefinitionCode(schedule.getReturnDefinition().getCode());
            return r;
        }).collect(Collectors.toList());

        return returnModels;
    }

    public long loadFiAwaitingReturnsCount(List<Schedule> schedules) {
        schedules = schedules == null ? loadCurrentSubmissionPeriodSchedules(true) : schedules;
        if (schedules.isEmpty()) {
            return 0;
        }
        CustomPrincipal principal = userLocal.getCallerPrincipal();

        Set<Long> userVersionsIds = principal.getReturnVersions().keySet();
        String schedulesInStatement = DBUtil.get().generateConcatenatedInStatements("r.schedule.id", schedules, "id");

        List<Long> createdReturnSchedules = em.createQuery("select r.schedule.id from IN_RETURNS r,IN(r.statuses) rs where  " +
                        "rs.id=(select MAX(rs.id) from IN_RETURN_STATUSES rs where rs.returns.id=r.id)" +
                        " and rs.status not in(:statuses) and " + schedulesInStatement, Long.class)
                .setParameter("statuses", List.of(ProcessStatus.STATUS_CREATED))
                .getResultList();

        return schedules.stream().filter(s -> !createdReturnSchedules.contains(s.getId())).count();
    }

    @Override
    public List<ReturnModel> loadFiInProgressReturns(List<Schedule> schedules) {
        schedules = schedules == null ? loadCurrentSubmissionPeriodSchedules(true) : schedules;

        if (!schedules.isEmpty()) {

            CustomPrincipal principal = userLocal.getCallerPrincipal();
            Set<Long> userVersionsIds = principal.getReturnVersions().keySet();

            String fiQuery = DBUtil.get().generateConcatenatedInStatementWithIds("s.fi.id", new ArrayList<Long>(principal.getFis()));
            Collection<Long> rDefIds = principal.getReturnDefinitions();
            rDefIds = rDefIds.isEmpty() ? Collections.singletonList(-1L) : rDefIds;

            String schedulesInStatement = DBUtil.get().generateConcatenatedInStatements("s.id", schedules, "id");


            List<ReturnModel> inProgressReturns =
                    em.createQuery("select distinct new " +
                                    ReturnModel.class.getName() +
                                    " ( " +
                                    "s.delay, " +
                                    "s.delayHour, " +
                                    "s.delayMinute, " +
                                    "s.fi.id, " +
                                    "s.fi.code, " +
                                    "s.period.id, " +
                                    "s.period.toDate, " +
                                    "s.period.periodType.id, " +
                                    "s.period.periodType.code, " +
                                    "s.returnDefinition.returnType.id, " +
                                    "s.returnDefinition.returnType.code, " +
                                    "rv.id, " +
                                    "rv.code, " +
                                    "s.returnDefinition.code " +
                                    ")" +
                                    " from IN_RETURNS r inner join r.returnVersion rv inner join r.schedule s,IN(r.statuses) rs" +
                                    " where (" + schedulesInStatement + ")" +
                                    " and rs.id=(select MAX(rs.id) from IN_RETURN_STATUSES rs where rs.returns.id=r.id) and rs.status in(:statuses) " +
                                    " AND s.returnDefinition.id IN(:userReturnDefinitions) " +
                                    " AND s.returnDefinition.manualInput = true " +
                                    " AND rv.id IN(:userVersions) " +
                                    "AND (" + fiQuery + ")", ReturnModel.class)
                            .setParameter("statuses", Arrays.asList(ProcessStatus.STATUS_CREATED))
                            .setParameter("userVersions", userVersionsIds)
                            .setParameter("userReturnDefinitions", rDefIds)
                            .getResultList();

            return inProgressReturns;
        }

        return new ArrayList<>();
    }

    @Override
    public long countManualInputSubmittedReturns() {
        CustomPrincipal principal = userLocal.getCallerPrincipal();

        Set<Long> userVersionsIds = principal.getReturnVersions().keySet();

        Collection<Long> defIds = principal.getReturnDefinitions();
        defIds = defIds == null || defIds.isEmpty() ? Collections.singletonList(-1L) : defIds;

        String fiQuery = DBUtil.get().generateConcatenatedInStatementWithIds("s.fi.id", new ArrayList<Long>(principal.getFis()));

        return em.createQuery("select count(r.id)  from IN_RETURNS r inner join r.returnVersion rv inner join r.schedule s,IN(r.statuses) rs" +
                        " where rs.id=(select MAX(rs.id) from IN_RETURN_STATUSES rs where rs.returns.id=r.id) and rs.status not in(:statuses) " +
                        " AND s.returnDefinition.id IN(:userReturnDefinitions) " +
                        " AND rv.id IN(:userVersions)" +
                        " And s.returnDefinition.manualInput=true " +
                        " And " + fiQuery, Long.class)
                .setParameter("statuses", List.of(ProcessStatus.STATUS_CREATED))
                .setParameter("userVersions", userVersionsIds)
                .setParameter("userReturnDefinitions", defIds)
                .getSingleResult();

    }

    @Override
    public List<ReturnModel> loadReturnsBySchedules(List<Long> schedules) {
        if (schedules == null || schedules.isEmpty()) {
            return new ArrayList<>();
        }

        List<Return> returns = em.createQuery("select r from IN_RETURNS r where r.schedule.id in (:schedules)", Return.class)
                .setParameter("schedules", schedules)
                .getResultList();


        List<ReturnModel> result = new ArrayList<>();

        returns.forEach(entity -> {
            ReturnModel r = new ReturnModel();
            List<ReturnStatus> statuses = new ArrayList<>(entity.getStatuses());
            ReturnStatus rs = !statuses.isEmpty() ? statuses.get(0) : null;

            r.setId(entity.getId());
            r.setFiId(entity.getSchedule().getId());
            r.setFiCode(entity.getSchedule().getFi().getCode());
            r.setDelay(entity.getSchedule().getDelay());
            r.setDelayHour(entity.getSchedule().getDelayHour());
            r.setDelayMinute(entity.getSchedule().getDelayMinute());
            r.setPeriodId(entity.getSchedule().getPeriod().getId());
            r.setToDate(entity.getSchedule().getPeriod().getToDate());
            r.setReturnTypeCode(entity.getSchedule().getReturnDefinition().getReturnType().getCode());
            r.setReturnTypeId(entity.getSchedule().getReturnDefinition().getId());
            r.setDefinitionCode(entity.getSchedule().getReturnDefinition().getCode());
            r.setStatusDate(rs != null ? rs.getStatusDate() : null);
            r.setStatus(rs != null ? rs.getStatus() : null);
            r.setVersionCode(entity.getReturnVersion().getCode());

            result.add(r);
        });

        return result;
    }

    @Override
    public void createReturns(Map<Long, RegProcessStatus> scheduleImportMap,
                              RegProcessConfig regProcessConfig,
                              String versionCode,
                              long uploadFileId,
                              Language language,
                              String fiCode) {

        List<String> errorFiles = getPackageRejectErrorMessageList(scheduleImportMap, regProcessConfig.getShceduleMap());
        boolean hasError = !errorFiles.isEmpty();

        ReturnVersion returnVersion = em.createQuery("select r from IN_RETURN_VERSIONS r where r.code=:versionCode", ReturnVersion.class)
                .setParameter("versionCode", versionCode)
                .getSingleResult();

        UploadFile uploadFile = em.createQuery("select uf from SYS_UPLOADEDFILE uf where uf.id=:id", UploadFile.class).setParameter("id", uploadFileId).getSingleResult();

        User user = userLocal.findUserbyLogin(regProcessConfig.getUser());

        List<ImportedReturn> savedImportedReturns = new ArrayList<>();

        List<ImportedReturn> existingImportedReturns = em.createQuery("select r from IN_IMPORTED_RETURNS r where r.uploadFile.id=:fileId", ImportedReturn.class)
                .setParameter("fileId", uploadFileId)
                .getResultList();

        for (Map.Entry<Long, RegProcessStatus> entry : scheduleImportMap.entrySet()) {

            Return ret = null;

            List<Return> existedReturns = em.createQuery("select r from IN_RETURNS r where r.schedule.id=:scheduleId", Return.class)
                    .setParameter("scheduleId", entry.getKey())
                    .getResultList();

            boolean updateReturn = !existedReturns.isEmpty();

            if (updateReturn) {
                ret = existedReturns.get(0);
                ProcessStatus status = entry.getValue().getProcessStatus();

                if (regProcessConfig.isPackageRejectEnabled() && !regProcessConfig.getAcceptedDefinitionCodes().isEmpty()) {
                    status = ret.getStatus();
                }

                em.createQuery("update IN_RETURN_STATUSES set status=:status, statusDate=:sDate where returns.id=:returnId")
                        .setParameter("status", status)
                        .setParameter("sDate", new Date())
                        .setParameter("returnId", ret.getId())
                        .executeUpdate();

                ret.setStatus(status);
                em.merge(ret);

            } else {

                ret = new Return();
                ret.setReturnVersion(returnVersion);
                ret.setLatestVersion(returnVersion.getId());
                ret.setSchedule(em.find(Schedule.class, entry.getKey()));
                ret.setStatus(entry.getValue().getProcessStatus());

                em.persist(ret);

                ReturnStatusLite returnStatus = new ReturnStatusLite();

                returnStatus.setReturnId(ret.getId());
                returnStatus.setStatus(ret.getStatus());
                returnStatus.setUserId(user.getId());
                returnStatus.setVersionId(returnVersion.getId());
                returnStatus.setNote("Created From REG");
                returnStatus.setStatusDate(new Date());

                em.persist(returnStatus);

            }

            // create imported return
            ImportStatus importStatus = entry.getValue().getImportStatus();
            String importMessage = importStatus.equals(ImportStatus.DECLINED) ? "Warn: Unable to import accepted return" : "Process OK";

            if (regProcessConfig.isPackageRejectEnabled() && !regProcessConfig.getAcceptedDefinitionCodes().isEmpty()) {
                importMessage = "Package isn't full valid. Error file(s): " + regProcessConfig.getAcceptedDefinitionCodes();
                importStatus = ImportStatus.DECLINED;
            } else if (regProcessConfig.isPackageRejectEnabled() && hasError && entry.getValue().getProcessStatus() != ProcessStatus.STATUS_ERRORS) {
                entry.getValue().setProcessStatus(ProcessStatus.STATUS_ERRORS);
                entry.getValue().setImportStatus(ImportStatus.ERRORS);
                importStatus = ImportStatus.ERRORS;
                importMessage = "Reject Package. Error file(s): " + String.join(", ", errorFiles);
            }


            String returnDefCode = ret.getSchedule().getReturnDefinition().getCode();
            Map<String, List<String>> resultErrors = RegFileProcessorUtil.getErrorList(regProcessConfig.getInputValidatorMap());
            List<String> sheetErrors = resultErrors.get(returnDefCode);
            if (sheetErrors != null && !sheetErrors.isEmpty()) {
                importStatus = ImportStatus.ERRORS;
                StringBuilder sb = new StringBuilder();
                sheetErrors.forEach(e -> sb.append(e.trim()).append("\n"));
                importMessage = sb.toString();
            }

            Schedule schedule = ret.getSchedule();

            ImportedReturn importedReturn = getImportedReturn(schedule, existingImportedReturns, versionCode, uploadFile, user, importStatus, importMessage, language, fiCode, entry.getValue().getImportStart(), entry.getValue().getImportEnd());

            em.merge(importedReturn);

            savedImportedReturns.add(importedReturn);
            //save return errors
            ImportedReturnError importedReturnError = new ImportedReturnError();
            importedReturnError.setImportedReturnId(importedReturn.getId());
            byte[] message = ImportedReturnErrorTextUtil.buildErrorMessage(importedReturn);

            importedReturnError.setErrorContent(message);
            em.persist(importedReturnError);

            fsopImportStoreLocal.saveReturnXmlRelation(importedReturn.getId(), ret.getId());
        }

        if (!savedImportedReturns.isEmpty()) {
            ImportedReturnGroup group = new ImportedReturnGroup(savedImportedReturns.get(0));
            em.persist(group);
        }
    }

    @Override
    public List<Long> getAcceptedSchedules(List<Long> scheduleIds) {

        List<Long> result = new ArrayList<>();

        for (Long scheduleId : scheduleIds) {
            List<Return> existedReturns = em.createQuery("select r from IN_RETURNS r where r.schedule.id=:scheduleId", Return.class)
                    .setParameter("scheduleId", scheduleId)
                    .getResultList();

            if (!existedReturns.isEmpty()) {
                ReturnStatus returnStatus = processingStoreLocal.loadReturnCurrentStatus(existedReturns.get(0).getId());
                if (returnStatus != null && (returnStatus.getStatus() == ProcessStatus.STATUS_ACCEPTED || returnStatus.getStatus() == ProcessStatus.STATUS_VALIDATED)) {
                    result.add(scheduleId);
                }
            }
        }

        return result;
    }

    @Override
    public byte[] getImportedReturnErrorContent(long returnId) {
        List<ImportedReturnError> errors = em.createQuery("select e from IN_IMPORTED_RETURN_ERRORS e where e.importedReturnId=:returnId", ImportedReturnError.class)
                .setParameter("returnId", returnId)
                .getResultList();
        return errors.isEmpty() ? new byte[0] : errors.get(0).getErrorContent();
    }

    @Override
    public List<ImportedReturnError> getImportedReturnErrorsByFileId(long fileId) {
        return em.createQuery("select e from IN_IMPORTED_RETURN_ERRORS e left join IN_IMPORTED_RETURNS ir on ir.id=e.importedReturnId where ir.uploadFile.id=:xlsxId", ImportedReturnError.class)
                .setParameter("xlsxId", fileId)
                .getResultList();
    }

    @Override
    public ImportedReturn getImportedReturnById(int returnId) throws FinATypeException {
        ImportedReturn importedReturn = em.find(ImportedReturn.class, returnId);
        List<String> fiCOdes = fiLocal.loadFiCodes();
        if (!fiCOdes.contains(importedReturn.getBankCode())) {
            throw new FinATypeException(FinATypeException.Type.INVALID_PERMISSIONS);
        }
        return importedReturn;
    }

    @Override
    public List<ReturnModel> loadReturnSimple(Date fromDate, Date toDate) {
        return em.createQuery("select new " + ReturnModel.class.getName() + " (r.id,r.schedule.id,r.returnVersion.id) from IN_RETURNS r where r.schedule.period.toDate between :fromDate and :toDate", ReturnModel.class)
                .setParameter("fromDate", fromDate)
                .setParameter("toDate", toDate)
                .getResultList();

    }

    @Override
    public List<Return> loadReturnsById(Long... returnIds) {
        return em.createQuery("select r from IN_RETURNS r where r.id in (:ids)", Return.class)
                .setParameter("ids", Arrays.asList(returnIds))
                .getResultList();
    }

    @Override
    public void sendAcceptRejectNotificationToUsers(List<Long> returnIds, ProcessStatus status, String note) {
        if (returnIds == null || returnIds.isEmpty()) {
            return;
        }

        String langCode = ThreadLocalHolder.getLanguage().getCode();
        //retrieve current imported xml returns
        List<Long> activeImportedReturnIds = em.createQuery("select distinct iixr.importedReturnId from IN_IMPORTED_XML_RETURN iixr where iixr.returnId in (:returnIds) and iixr.status = 0", Long.class)
                .setParameter("returnIds", returnIds)
                .getResultList();

        if (activeImportedReturnIds.isEmpty()) {
            return;
        }
        // retrieve uploadedFiles on activeImportedReturns
        List<UploadFile> uploadFiles = em.createQuery("select new net.fina.server.dcs.uploadfile.entity.UploadFile(ir.uploadFile.fileName, ir.uploadFile.user.id, ir.uploadFile.user.login, ir.uploadFile.user.email, ir.uploadFile.user.version, ir.uploadFile.uploadedTime)  from IN_IMPORTED_RETURNS ir where ir.id in (:importedReturnIds)", UploadFile.class)
                .setParameter("importedReturnIds", activeImportedReturnIds)
                .getResultList();

        if (uploadFiles.isEmpty()) {
            return;
        }

        returnNotificationLocal.sendReturnStatusChangeNotification(uploadFiles, status, note, langCode);

    }

    @Override
    public List<ImportedXmlReturnModel> loadReturnsXMLsByImportedReturnId(List<Integer> importedReturnIds) {
        Query query = em.createQuery("Select x,r from IN_IMPORTED_XML_RETURN x, IN_IMPORTED_RETURNS r where x.importedReturnId in(:importedReturnIds) and x.importedReturnId = r.id ")
                .setParameter("importedReturnIds", importedReturnIds);
        List list = query.getResultList();
        List<ImportedXmlReturnModel> importedXmlReturn = new ArrayList<>();
        for (Object o : list) {
            Object[] result = (Object[]) o;

            ImportedReturn aReturn = (ImportedReturn) result[1];
            ImportModel importModel = new ImportModel();
            importModel.setId(aReturn.getId());
            importModel.setBankCode(aReturn.getBankCode());
            importModel.setVersionCode(aReturn.getVersionCode());
            importModel.setPeriodStart(aReturn.getPeriodStart());
            importModel.setPeriodEnd(aReturn.getPeriodEnd());
            importModel.setReturnCode(aReturn.getReturnCode());

            if (aReturn.getUploadFile() != null) {
                importModel.setFileName(aReturn.getUploadFile().getFileName());
                importModel.setFileId(aReturn.getUploadFile().getId());
            } else {
                importModel.setFileName("Undefined");
                importModel.setFileId(0);
            }

            importModel.setUploadTime(aReturn.getUploadTime());

            ImportedXmlReturn xmlReturn = (ImportedXmlReturn) result[0];

            ImportedXmlReturnModel model = new ImportedXmlReturnModel();
            model.setReturnId(xmlReturn.getReturnId());
            model.setImportModel(importModel);
            model.setImportTime(xmlReturn.getImportTime());
            model.setStatus(xmlReturn.getStatus());


            importedXmlReturn.add(model);
        }

        importedXmlReturn.sort((a, b) -> b.getImportTime().compareTo(a.getImportTime()));
        return importedXmlReturn;
    }

    @Override
    public ReturnModel getReturnById(long returnId) {
        Return r = em.find(Return.class, returnId);

        ReturnModel returnModel = new ReturnModel();
        returnModel.setId(r.getId());
        returnModel.setFiId(r.getSchedule().getId());
        returnModel.setFiCode(r.getSchedule().getFi().getCode());
        returnModel.setDelay(r.getSchedule().getDelay());
        returnModel.setDelayHour(r.getSchedule().getDelayHour());
        returnModel.setDelayMinute(r.getSchedule().getDelayMinute());
        returnModel.setPeriodId(r.getSchedule().getPeriod().getId());
        returnModel.setToDate(r.getSchedule().getPeriod().getToDate());
        returnModel.setReturnTypeCode(r.getSchedule().getReturnDefinition().getReturnType().getCode());
        returnModel.setReturnTypeId(r.getSchedule().getReturnDefinition().getId());
        returnModel.setVersionId(r.getReturnVersion().getId());
        returnModel.setVersionCode(r.getReturnVersion().getCode());
        returnModel.setStatus(r.getStatus());

        return returnModel;
    }

    private List<Schedule> loadCurrentSubmissionPeriodSchedules(boolean manualInput) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Schedule> query = cb.createQuery(Schedule.class);
        Root<Schedule> schedule = query.from(Schedule.class);

        CustomPrincipal principal = userLocal.getCallerPrincipal();

        Join<Schedule, Fi> scheduleFiJoin = schedule.join(Schedule_.fi);
        Join<Schedule, ReturnDefinition> scheduleReturnDefinitionJoin = schedule.join(Schedule_.returnDefinition);


        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.greaterThanOrEqualTo(schedule.get(Schedule_.delayToDate), new Date()));
        predicates.add(cb.lessThanOrEqualTo(schedule.get(Schedule_.period).get(Period_.toDate), new Date()));

        Collection<Long> fiIds = principal.getFis();
        Collection<Long> rDefIds = principal.getReturnDefinitions();

        Collection<Long> fis = fiIds == null || fiIds.isEmpty() ? Collections.singletonList(-1L) : fiIds;
        List<Predicate> fiPredList = DBUtil.get().buildAndSplitPredicates(cb, scheduleFiJoin.get(Fi_.id), Long.class, fis);

        Collection<Long> rDefs = rDefIds == null || rDefIds.isEmpty() ? Collections.singletonList(-1L) : rDefIds;
        predicates.add(scheduleReturnDefinitionJoin.get(ReturnDefinition_.id).in(rDefs));
        if (manualInput) {
            predicates.add(cb.equal(scheduleReturnDefinitionJoin.get(ReturnDefinition_.manualInput), true));
        }
        predicates.add(cb.and(cb.or(fiPredList.toArray(new Predicate[0]))));

        query.where(predicates.toArray(new Predicate[0]));
        Query loadQuery = em.createQuery(query);


        return loadQuery.getResultList();

    }


    private Map<ReturnModel, Map<String, ProcessStatus>> loadDependentReturnsStatuses(ReturnModel model) {
        Map<ReturnModel, Map<String, ProcessStatus>> result = new HashMap<>();
        Collection<Long> defIds = processingStoreLocal.getReturnDefinitionDependencies(model.getDefinitionId());
        if (defIds.isEmpty()) {
            return result;
        }
        Query query = em.createQuery("select r.schedule.returnDefinition.code,irs.status from IN_RETURNS r,IN_RETURN_STATUSES irs where r.returnVersion.id=:versionId and r.schedule.id in(select s.id from IN_SCHEDULES s where s.period.id in(select p.id from IN_PERIODS as p where p.toDate=(select pe.toDate from IN_PERIODS as pe where pe.id=:periodId)) and s.fi.id=:fiId and s.returnDefinition.id in(:definitionIDs)) and irs.id=(select MAX(rs.id) from IN_RETURN_STATUSES rs where rs.returns.id=r.id) and irs.status not in(:ps)")
                .setParameter("periodId", model.getPeriodId())
                .setParameter("fiId", model.getFiId())
                .setParameter("definitionIDs", defIds)
                .setParameter("versionId", model.getVersionId())
                .setParameter("ps", ProcessStatus.STATUS_ACCEPTED);
        List<Object[]> resultList = query.getResultList();
        HashMap<String, ProcessStatus> m = new FastHashMap();
        for (Object[] objects : resultList) {
            m.put((String) objects[0], (ProcessStatus) objects[1]);
        }
        if (!m.isEmpty()) {
            result.put(model, m);
        }
        return result;
    }

    private ImportedReturn getImportedReturn(Schedule schedule, List<ImportedReturn> existingImportedReturns, String versionCode,
                                             UploadFile uploadFile, User user, ImportStatus importStatus, String importMessage,
                                             Language language, String fiCode, Date importStart, Date importEnd) {
        ImportedReturn importedReturn = new ImportedReturn();
        importedReturn.setReturnCode(schedule.getReturnDefinition().getCode());
        importedReturn.setVersionCode(versionCode);
        importedReturn.setPeriodStart(schedule.getPeriod().getFromDate());
        importedReturn.setPeriodEnd(schedule.getPeriod().getToDate());
        importedReturn.setUser(user);
        importedReturn.setUploadFile(uploadFile);
        importedReturn.setLanguage(language);
        importedReturn.setBankCode(fiCode);
        importedReturn.setStatus(importStatus);
        importedReturn.setMessage(importMessage == null ? "" : importMessage);
        importedReturn.setType(ImportedFileType.DCS);
        importedReturn.setImportStart(importStart);
        importedReturn.setImportEnd(importEnd);
        importedReturn.setUploadTime(uploadFile.getUploadedTime());

        int existingImportedReturnId = existingImportedReturns.stream().filter(ir -> ir.getReturnCode() != null && ir.getReturnCode().trim().equalsIgnoreCase(importedReturn.getReturnCode().trim()))
                .findFirst()
                .orElseGet(ImportedReturn::new)
                .getId();

        importedReturn.setId(existingImportedReturnId);

        return importedReturn;
    }

    private void constructOverdueReturnsFilter(Map<OverdueReturnFilter, Object> filter, StringBuilder whereQuery) {
        if (filter != null && !filter.isEmpty()) {
            for (Map.Entry<OverdueReturnFilter, Object> entry : filter.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }
                switch (entry.getKey()) {
                    case from:
                        whereQuery.append(" and p.fromDate>=:")
                                .append(entry.getKey().name())
                                .append(" ");
                        break;
                    case to:
                        whereQuery.append(" and p.toDate<=:")
                                .append(entry.getKey().name())
                                .append(" ");
                        break;
                    case periodType:
                        whereQuery.append(" and pt.periodType=:")
                                .append(entry.getKey().name())
                                .append(" ");
                        break;
                    case fiType:
                        whereQuery.append(" and b.fiType.id=:")
                                .append(entry.getKey().name())
                                .append(" ");
                        break;

                    case fiCode:
                        whereQuery.append(" and b.code like :")
                                .append(entry.getKey().name())
                                .append(" ");
                        break;
                    case dueDate:
                        whereQuery.append(" and s.delay=:")
                                .append(entry.getKey().name())
                                .append(" ");
                }
            }
        }
    }

    private void constructOverdueReturnsSortPath(SortInfo sortInfo, StringBuilder groupQuery) {
        if (sortInfo != null) {
            switch (sortInfo.getSortField()) {
                case "fiCode":
                    groupQuery.append(", b.code ").append(sortInfo.getSortDir());
                    break;
                case "fiType":
                    groupQuery.append(", b.fiType.code ").append(sortInfo.getSortDir());
                    break;
                case "fromDate":
                    groupQuery.append(", p.fromDate ").append(sortInfo.getSortDir());
                    break;
                case "toDate":
                    groupQuery.append(", p.toDate ").append(sortInfo.getSortDir());
                    break;
                case "dueDate":
                    groupQuery.append(", s.delay ").append(sortInfo.getSortDir());
                    break;
            }
        }
    }

    private List<String> getPackageRejectErrorMessageList(Map<Long, RegProcessStatus> scheduleImportMap, Map<String, Long> scheduleMap) {
        List<String> result = new ArrayList<>();
        Map<Long, String> scheduleIdReturnCodeMap = new HashMap<>();

        for (Map.Entry<String, Long> entry : scheduleMap.entrySet()) {
            scheduleIdReturnCodeMap.put(entry.getValue(), entry.getKey());
        }

        for (Map.Entry<Long, RegProcessStatus> entry : scheduleImportMap.entrySet()) {
            Long returnId = entry.getKey();
            RegProcessStatus status = entry.getValue();

            if (status == null) continue;

            if (status.getProcessStatus() == ProcessStatus.STATUS_ERRORS) {
                String returnCode = scheduleIdReturnCodeMap.get(returnId);
                if (returnCode != null && !result.contains(returnCode)) {
                    result.add(returnCode);
                }
            }
        }

        return result;
    }
    private Long countSchedules() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Schedule> schedule = query.from(Schedule.class);

        query.select(cb.count(schedule));

        Join<Schedule, Fi> scheduleFiJoin = schedule.join(Schedule_.fi);
        Join<Schedule, ReturnDefinition> scheduleReturnDefinitionJoin = schedule.join(Schedule_.returnDefinition);

        List<Predicate> predicates = buildSchedulePredicates(cb, schedule, scheduleFiJoin, scheduleReturnDefinitionJoin);

        query.where(predicates.toArray(new Predicate[0]));

        return em.createQuery(query).getSingleResult();
    }

    private Map<ProcessStatus, Long> countReturnsByStatus() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<Return> returnRoot = query.from(Return.class);
        Join<Return, Schedule> schedule = returnRoot.join(Return_.schedule);
        Join<Schedule, Fi> scheduleFiJoin = schedule.join(Schedule_.fi);
        Join<Schedule, ReturnDefinition> scheduleReturnDefinitionJoin = schedule.join(Schedule_.returnDefinition);

        List<Predicate> predicates = buildSchedulePredicates(cb, schedule, scheduleFiJoin, scheduleReturnDefinitionJoin);

        query.where(predicates.toArray(new Predicate[0]));

        query.groupBy(returnRoot.get(Return_.status));
        query.multiselect(returnRoot.get(Return_.status), cb.count(returnRoot));

        List<Tuple> results = em.createQuery(query).getResultList();

        Map<ProcessStatus, Long> statusMap = new EnumMap<>(ProcessStatus.class);
        for (Tuple t : results) {
            ProcessStatus status = t.get(0, ProcessStatus.class);
            Long count = t.get(1, Long.class);
            statusMap.put(status, count);
        }
        return statusMap;
    }

    private List<Predicate> buildSchedulePredicates(CriteriaBuilder cb,
                                                    From<?, Schedule> schedule,
                                                    Join<Schedule, Fi> scheduleFiJoin,
                                                    Join<Schedule, ReturnDefinition> scheduleReturnDefinitionJoin) {
        CustomPrincipal principal = userLocal.getCallerPrincipal();
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.greaterThanOrEqualTo(schedule.get(Schedule_.delayToDate), new Date()));
        predicates.add(cb.lessThanOrEqualTo(schedule.get(Schedule_.period).get(Period_.toDate), new Date()));

        Collection<Long> fiIds = principal.getFis();
        Collection<Long> rDefIds = principal.getReturnDefinitions();

        Collection<Long> fis = fiIds == null || fiIds.isEmpty() ? Collections.singletonList(-1L) : fiIds;
        List<Predicate> fiPredList = DBUtil.get().buildAndSplitPredicates(cb, scheduleFiJoin.get(Fi_.id), Long.class, fis);

        Collection<Long> rDefs = rDefIds == null || rDefIds.isEmpty() ? Collections.singletonList(-1L) : rDefIds;
        predicates.add(scheduleReturnDefinitionJoin.get(ReturnDefinition_.id).in(rDefs));

        predicates.add(cb.equal(scheduleReturnDefinitionJoin.get(ReturnDefinition_.manualInput), true));

        predicates.add(cb.and(cb.or(fiPredList.toArray(new Predicate[0]))));

        return predicates;
    }

}
