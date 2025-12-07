package net.fina.server.dashboard.dynamic.impl;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.shared.SortField;
import net.fina.server.dashboard.dynamic.api.DashboardLocal;
import net.fina.server.dashboard.dynamic.api.DashletQueryExecutor;
import net.fina.server.dashboard.dynamic.entity.Dashboard;
import net.fina.server.dashboard.dynamic.entity.Dashlet;
import net.fina.server.dashboard.dynamic.entity.Dashlet_;
import net.fina.server.dashboard.dynamic.model.filter.DashletFilter;
import net.fina.server.security.api.UserLocal;
import net.fina.server.security.entity.UserDashboard;
import net.fina.server.security.entity.UserDashboardPK;
import net.fina.server.util.SortUtil;
import net.fina.server.util.SqlInjectionDetector;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.logging.Logger;

import java.util.*;

@Stateless
@SecurityDomain("FinASecurityDomain")
public class DashboardSession implements DashboardLocal {
    private final Logger log = Logger.getLogger(DashboardSession.class);
    @Inject
    private EntityManager em;

    @Inject
    private DashletQueryExecutor dashletQueryExecutor;

    @Inject
    private UserLocal userLocal;


    @Override
    public List<Dashboard> loadAllDashboard() {
        return em.createQuery("select d from IN_DASHBOARDS d", Dashboard.class).getResultList();
    }

    @Override
    public List<Dashboard> loadUserDashboards() {
        return em.createQuery("select d.userDashboardPK.dashboard from SYS_USER_DASHBOARDS d where d.userDashboardPK.userId=:userId", Dashboard.class)
                .setParameter("userId", userLocal.getCurrentUserId())
                .getResultList();
    }

    @Override
    public Dashboard saveDashboard(Dashboard dashboard) throws FinATypeException {
        validateDefaultDashboard(dashboard);
        validateDashboardCode(dashboard);

        if (!dashboard.getDashletList().isEmpty()) {
            List<Dashlet> dashletList = new ArrayList<>();
            for (Dashlet ds : dashboard.getDashletList()) {
                dashletList.add(saveDashlet(ds, new HashMap<>()));
            }
            dashboard.setDashletList(dashletList);
        }

        if (dashboard.getId() <= 0) {
            em.persist(dashboard);
            UserDashboardPK userDashboardPK = new UserDashboardPK(userLocal.getCurrentUserId(), dashboard.getId());
            UserDashboard userDashboard = new UserDashboard(userDashboardPK);
            em.persist(userDashboard);
        } else {
            dashboard = em.merge(dashboard);
        }
        return dashboard;
    }


    @Override
    public void deleteDashboard(long dashboardID) {
        em.createQuery("delete from SYS_USER_DASHBOARDS ud where ud.userDashboardPK.dashboard.id=:dashboardId")
                .setParameter("dashboardId", dashboardID)
                .executeUpdate();
        em.remove(em.find(Dashboard.class, dashboardID));
    }

    @Override
    public Dashlet saveDashlet(Dashlet dashlet, Map<String, Object> queryParams) throws FinATypeException {
        String query = dashlet.getDataQuery();

        if (query == null || query.trim().isEmpty()) {
            throw new FinATypeException("Query is required");
        }
        //check for sql injection
        checkSQLInjection(query);

        //validate code
        validateDashletCode(dashlet);

        //validate Query
        dashletQueryExecutor.parseQueryColumns(query);

        //execute top 10 query
        dashletQueryExecutor.loadDashletData(query, queryParams, 10);

        if (dashlet.getId() > 0) {
            em.merge(dashlet);
        } else {
            dashlet = em.merge(dashlet);
        }

        return dashlet;
    }


    @Override
    public void deleteDashlet(long dashletId) throws FinATypeException {
        List<Long> ids = em.createQuery("select d.id from IN_DASHBOARDS d, IN(d.dashletList) dl where dl.id=:id", Long.class)
                .setParameter("id", dashletId).getResultList();

        if (!ids.isEmpty()) {
            throw new FinATypeException(FinATypeException.Type.DEPENDENCY_ERROR);
        }
        em.remove(em.find(Dashlet.class, dashletId));
    }

    @Override
    public List<Map<String, Object>> loadDashletData(Dashlet dashlet, Map<String, Object> queryParams, int limit) throws FinATypeException {
        String query = dashlet.getDataQuery();

        return dashletQueryExecutor.loadDashletData(query, queryParams, limit);
    }

    @Override
    public List<Map<String, Object>> loadDashletData(long dashletId, Map<String, Object> queryParams, int limit) throws FinATypeException {
        return loadDashletData(em.find(Dashlet.class, dashletId), queryParams, limit);
    }

    @Override
    public List<Dashlet> loadDashlets(int offset, int limit, SortField sortField,
                                      Map<DashletFilter, String> filterMap, long langId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Dashlet> cq = cb.createQuery(Dashlet.class);
        Root<Dashlet> dashlet = cq.from(Dashlet.class);

        List<Predicate> predicates = buildPredicatesWithJsonSupport(cb, dashlet, filterMap, langId);

        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(new Predicate[0]));
        }

        if (sortField != null && sortField.getProperty() != null) {
            String property = sortField.getProperty();

            if (property.equalsIgnoreCase("type") || property.equalsIgnoreCase("chartType")) {
                Expression<String> selectedType = cb.function("JSON_VALUE", String.class,
                        dashlet.get(Dashlet_.metaInfoJson), cb.literal("$.selectedType"));
                Expression<String> selectedChart = cb.function("JSON_VALUE", String.class,
                        dashlet.get(Dashlet_.metaInfoJson), cb.literal("$.selectedChart"));

                Expression<String> combinedSort = cb.function("LOWER", String.class,
                        cb.concat(
                                cb.coalesce(selectedType, cb.literal("")),
                                cb.concat(cb.literal("_"), cb.coalesce(selectedChart, cb.literal("")))
                        )
                );

                cq.orderBy(sortField.isAsc() ? cb.asc(combinedSort) : cb.desc(combinedSort));
            } else {
                if (SortUtil.isPropertyExistOnEntity(dashlet, property)) {
                    cq.orderBy(sortField.isAsc() ? cb.asc(dashlet.get(property)) : cb.desc(dashlet.get(property)));
                }
            }
        }

        TypedQuery<Dashlet> query = em.createQuery(cq);

        if (offset >= 0 && limit >= 0) {
            query.setFirstResult(offset).setMaxResults(limit);
        }

        return query.getResultList();
    }

    public long countDashlets(Map<DashletFilter, String> filterStringMap, long langId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Dashlet> dashlet = cq.from(Dashlet.class);

        List<Predicate> predicates = buildPredicatesWithJsonSupport(cb, dashlet, filterStringMap, langId);

        cq.select(cb.count(dashlet));

        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(new Predicate[0]));
        }

        TypedQuery<Long> query = em.createQuery(cq);

        return query.getSingleResult();
    }


    private void validateDefaultDashboard(Dashboard dashboard) throws FinATypeException {
          /*
            check default dashboard
            */

        TypedQuery<Dashboard> query = em.createQuery("select d from IN_DASHBOARDS d where d.isDefault=true and d.id<>:dashboardId", Dashboard.class)
                .setParameter("dashboardId", dashboard.getId());

        if (dashboard.isIsDefault()) {
            List<Dashboard> defaultDashboards = query.getResultList();
            if (!defaultDashboards.isEmpty()) {
                throw new FinATypeException("Default dashboard already exists!");
            }
        }
    }

    private void checkSQLInjection(String query) throws FinATypeException {
        try {
            SqlInjectionDetector.validateReadOnlyQuery(query);
        } catch (Exception e) {
            log.error("sql injection detected in query!!!");
            throw new FinATypeException(FinATypeException.Type.INVALID_VALUE);
        }
    }

    private void validateDashboardCode(Dashboard dashboard) throws FinATypeException {
        if (dashboard.getCode() == null || dashboard.getCode().isEmpty()) {
            throw new FinATypeException(FinATypeException.Type.CODE_NULL);
        }

        boolean unique = em.createQuery("select sud.userDashboardPK.userId from SYS_USER_DASHBOARDS sud " +
                        "where lower(trim( sud.userDashboardPK.dashboard.code))=:code and sud.userDashboardPK.userId=:userId and sud.userDashboardPK.dashboard.id <> :dashboardId ", Long.class)
                .setParameter("userId", userLocal.getCurrentUserId())
                .setParameter("code", dashboard.getCode().trim().toLowerCase())
                .setParameter("dashboardId", dashboard.getId())
                .getResultList().isEmpty();


        if (!unique) {
            throw new FinATypeException(FinATypeException.Type.CODE_UNIQUE);
        }
    }

    private void validateDashletCode(Dashlet dashlet) throws FinATypeException {
        if (dashlet.getCode() == null || dashlet.getCode().isEmpty()) {
            throw new FinATypeException(FinATypeException.Type.CODE_NULL);
        }
        boolean unique = em.createQuery("select d.id from IN_DASHLETS d where trim(d.code)=:code and d.id<>:id ")
                .setParameter("code", dashlet.getCode())
                .setParameter("id", dashlet.getId())
                .getResultList().isEmpty();

        if (!unique) {
            throw new FinATypeException(FinATypeException.Type.CODE_UNIQUE);
        }
    }

    @SuppressWarnings("JpaQlInspection")
    private List<Predicate> buildPredicatesWithJsonSupport(CriteriaBuilder cb,
                                                           Root<Dashlet> dashlet,
                                                           Map<DashletFilter, String> filterMap,
                                                           long langId) {
        List<Predicate> predicates = new ArrayList<>();
        if (filterMap != null) {
            for (Map.Entry<DashletFilter, String> entry : filterMap.entrySet()) {
                String val = entry.getValue();
                if (val != null && !val.isEmpty()) {
                    switch (entry.getKey()) {
                        case NAME:
                            List<Long> descriptions = em.createQuery("select d.id from SYS_STRINGS s inner join IN_DASHLETS d on d.name=s.id and s.langId =:langId and s.value like :value", Long.class)
                                    .setParameter("langId", langId)
                                    .setParameter("value", "%" + entry.getValue() + "%")
                                    .getResultList();

                            predicates.add(dashlet.get(Dashlet_.id).in(descriptions.isEmpty() ? Collections.singletonList(-1L) : descriptions));
                            break;
                        case CODE:
                            predicates.add(cb.like(dashlet.get(Dashlet_.code), "%" + val + "%"));
                            break;
                        case TYPE:
                            predicates.add(buildJsonFilterPredicate(cb, dashlet, "selectedType", val));
                            break;
                        case CHART_TYPE:
                            predicates.add(buildJsonFilterPredicate(cb, dashlet, "selectedChart", val));
                            break;
                    }
                }
            }
        }

        return predicates;
    }

    private Predicate buildJsonFilterPredicate(CriteriaBuilder cb, Root<Dashlet> dashlet, String jsonPath, String filterValue) {
        return cb.like(dashlet.get(Dashlet_.metaInfoJson),
                "%\"" + jsonPath + "\":\"" + filterValue + "\"%");
    }

}
