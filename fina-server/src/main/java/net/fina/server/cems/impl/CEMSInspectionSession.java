package net.fina.server.cems.impl;

import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.shared.SortField;
import net.fina.server.cems.api.CEMSInspectionLocal;
import net.fina.server.cems.entity.*;
import net.fina.server.cems.entity.sanction.CEMSSanction;
import net.fina.server.cems.entity.sanction.CEMSSanctionStatus_;
import net.fina.server.cems.entity.sanction.CEMSSanction_;
import net.fina.server.cems.model.CEMSInspectionFilterType;
import net.fina.server.fi.entity.Fi;
import net.fina.server.fi.entity.Fi_;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.security.api.UserLocal;
import net.fina.server.security.entity.User_;
import org.jboss.ejb3.annotation.SecurityDomain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Local(CEMSInspectionLocal.class)
@Stateless
@Interceptors(RecordingAuditor.class)
@SecurityDomain("FinASecurityDomain")
public class CEMSInspectionSession implements CEMSInspectionLocal {

    @Inject
    private EntityManager em;

    @Inject
    private UserLocal userLocal;

    @Override
    public List<CEMSInspection> loadInspections(int offset, int limit, Map<CEMSInspectionFilterType, Object> filterMap, SortField sortField) {
        if (sortField == null) {
            sortField = new SortField("recordCreateDate", "desc");
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CEMSInspection> query = cb.createQuery(CEMSInspection.class);
        Root<CEMSInspection> root = query.from(CEMSInspection.class);
        Join<CEMSInspection, CEMSRecommendation> recommendationJoin = root.join(CEMSInspection_.recommendations, JoinType.LEFT);

        List<Predicate> predicates = getFilterPredicate(cb, root, recommendationJoin, filterMap);
        query.distinct(true);
        query.where(predicates.toArray(new Predicate[0]));
        query.orderBy(sortField.isAsc() ? cb.asc(root.get(sortField.getProperty())) : cb.desc(root.get(sortField.getProperty())));

        TypedQuery<CEMSInspection> loadQuery = em.createQuery(query);

        if (offset >= 0 && limit > 0) {
            loadQuery.setFirstResult(offset).setMaxResults(limit);
        }

        return loadQuery.getResultList();
    }

    @Override
    public long countInspections(Map<CEMSInspectionFilterType, Object> filterMap) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<CEMSInspection> root = query.from(CEMSInspection.class);
        Join<CEMSInspection, CEMSRecommendation> recommendationJoin = root.join(CEMSInspection_.recommendations, JoinType.LEFT);

        query.select(cb.countDistinct(root.get(CEMSInspection_.id)));

        List<Predicate> predicates = getFilterPredicate(cb, root, recommendationJoin, filterMap);

        query.where(predicates.toArray(new Predicate[0]));

        TypedQuery<Long> loadQuery = em.createQuery(query);

        return loadQuery.getSingleResult();
    }

    @Override
    public CEMSInspection save(CEMSInspection inspection) throws FinATypeException {
        if (inspection.getFi() == null) {
            throw new FinATypeException("Fi Is Required!");
        }

        inspection.setFi(em.find(Fi.class, inspection.getFi().getId()));
        if (inspection.getFi() == null) {
            throw new FinATypeException("Fi Is Required!");
        }

        if (inspection.getType() == null) {
            throw new FinATypeException("Type Is Required!");
        }

        if (inspection.getReportingYearInspection() != null) {
            CEMSReportingYearInspection reporting = inspection.getReportingYearInspection();
            if (reporting.getManager() != null) {
                reporting.setManager(userLocal.findUserbyId(reporting.getManager().getId()));
            }
        }

        if (inspection.getManager() != null) {
            inspection.setManager(userLocal.findUserbyId(inspection.getManager().getId()));
        }

        //Existing inspection
        if (inspection.getId() != null && !inspection.getId().trim().isEmpty()) {
            CEMSInspection existing = em.find(CEMSInspection.class, inspection.getId());
            if (existing == null) {
                throw new FinATypeException(FinATypeException.Type.INVALID_CODE);
            }
            inspection.setSanctionList(existing.getSanctionList());
            inspection.setRecordCreateDate(existing.getRecordCreateDate());
            inspection.setRecommendations(existing.getRecommendations());

            em.merge(inspection);
        } else {
            inspection.setId(generateId());
            inspection.setRecordCreateDate(new Date());
            em.persist(inspection);
        }


        return inspection;
    }

    @Override
    public void delete(List<String> ids) throws FinATypeException {
        if (ids != null && !ids.isEmpty()) {
            // check dependencies
            List<String> recommendationIds = em.createQuery("select id from CEMS_RECOMMENDATIONS r where r.inspection.id in (:ids)", String.class).setParameter("ids", ids).getResultList();

            List<Long> sanctionIds = em.createQuery("select id from CEMS_SANCTIONS s where s.inspection.id in (:ids)", Long.class).setParameter("ids", ids).getResultList();

            if (!recommendationIds.isEmpty() || !sanctionIds.isEmpty()) {
                throw new FinATypeException(FinATypeException.Type.DEPENDENCY_ERROR);
            } else {
                em.createQuery("delete from CEMS_INSPECTIONS_META_INFO where id in(select ci.metaInfo.id from CEMS_INSPECTIONS ci where ci.id in (:ids))").setParameter("ids", ids);

                em.createQuery("delete from CEMS_REPORTING_YEAR_INSPECTIONS where id in(select ci.reportingYearInspection.id from CEMS_INSPECTIONS ci where ci.id in (:ids))").setParameter("ids", ids);

                em.createQuery("delete  from CEMS_INSPECTIONS  where id in (:ids)")
                        .setParameter("ids", ids)
                        .executeUpdate();
            }
        }

    }

    @Override
    public CEMSInspection findById(String inspectionId) throws FinATypeException {
        CEMSInspection inspection = em.find(CEMSInspection.class, inspectionId);
        if (inspection == null) {
            throw new FinATypeException(FinATypeException.Type.INVALID_VALUE, "Inspection Not Found");
        }
        return inspection;
    }

    private String generateId() {
        LocalDate localDate = LocalDate.now();

        StringBuilder sb = new StringBuilder();
        sb.append(localDate.getYear())
                .append("-")
                .append(localDate.getMonthValue())
                .append("-");

        List<String> ids = em.createQuery("select ins.id from CEMS_INSPECTIONS ins where ins.id like :id order by ins.recordCreateDate desc ", String.class)
                .setParameter("id", sb.toString() + "%")
                .getResultList();

        String existingID = ids.isEmpty() ? null : ids.get(0);
        int counter = existingID == null ? 1 : Integer.parseInt(existingID.substring(existingID.lastIndexOf("-") + 1));
        sb.append(counter + 1);

        return sb.toString();
    }

    private List<Predicate> getFilterPredicate(CriteriaBuilder cb, Root<CEMSInspection> root, Join<CEMSInspection, CEMSRecommendation> recommendationJoin, Map<CEMSInspectionFilterType, Object> filter) {
        List<Predicate> predicates = new ArrayList<>();

        if (filter != null) {
            for (Map.Entry<CEMSInspectionFilterType, Object> entry : filter.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }
                switch (entry.getKey()) {
                    case CODE:
                        predicates.add(cb.like(root.get(CEMSInspection_.id), "%" + entry.getValue() + "%"));
                        break;
                    case FI_IDS:
                        List<Predicate> fiPredicates = new ArrayList<>();
                        for (Long fiId : (List<Long>) entry.getValue()) {
                            fiPredicates.add(cb.or(cb.equal(root.get(CEMSInspection_.fi).get(Fi_.ID), fiId)));
                        }
                        predicates.add(cb.or(fiPredicates.toArray(new Predicate[0])));
                        break;
                    case MANAGER_IDS:
                        List<Predicate> managerPerdicates = new ArrayList<>();
                        for (Long userId : (List<Long>) entry.getValue()) {
                            managerPerdicates.add(cb.or(cb.equal(root.get(CEMSInspection_.manager).get(User_.ID), userId)));
                        }
                        predicates.add(cb.or(managerPerdicates.toArray(new Predicate[0])));
                        break;
                    case START_DATE:
                        predicates.add(cb.greaterThanOrEqualTo(root.get(CEMSInspection_.START_DATE), (Date) entry.getValue()));
                        break;
                    case END_DATE:
                        predicates.add(cb.lessThanOrEqualTo(root.get(CEMSInspection_.END_DATE), (Date) entry.getValue()));
                        break;
                    case RECOMMENDATION_STATUS:
                        List<String> recommendationStatusCodes = ((List<CEMSRecommendationStatus>) entry.getValue()).stream().map(Enum::name).collect(Collectors.toList());

                        predicates.add(cb.and(cb.equal(recommendationJoin.get(CEMSRecommendation_.TYPE), CEMSRecommendationType.RECOMMENDATION),
                                recommendationJoin.get(CEMSRecommendation_.statusInfo).get(CEMSRecommendationStatusInfo_.status).get(CEMSSanctionStatus_.CODE).in(recommendationStatusCodes)
                        ));
                        break;

                    case DECISION_STATUS:
                        List<String> decisionStatusCodes = ((List<CEMSRecommendationStatus>) entry.getValue()).stream().map(Enum::name).collect(Collectors.toList());

                        predicates.add(cb.and(cb.equal(recommendationJoin.get(CEMSRecommendation_.TYPE), CEMSRecommendationType.DECISION),
                                recommendationJoin.get(CEMSRecommendation_.statusInfo).get(CEMSRecommendationStatusInfo_.status).get(CEMSSanctionStatus_.CODE)
                                        .in(decisionStatusCodes)));
                        break;
                    case SANCTION_STATUS:
                        List<String> sanctionStatusCodes = ((List<CEMSRecommendationStatus>) entry.getValue()).stream().map(Enum::name).collect(Collectors.toList());

                        ListJoin<CEMSInspection, CEMSSanction> sanctionJoin = root.join(CEMSInspection_.sanctionList, JoinType.LEFT);
                        predicates.add(cb.and(sanctionJoin.get(CEMSSanction_.status).get(CEMSSanctionStatus_.CODE)
                                .in(sanctionStatusCodes)));
                        break;
                    case TYPE:
                        predicates.add(cb.equal(root.get(CEMSInspection_.TYPE), entry.getValue()));
                        break;
                    case GROUND_OF_CPU:
                        predicates.add(cb.like(root.get(CEMSInspection_.foundation), "%" + entry.getValue() + "%"));
                        break;
                    case NOTE:
                        predicates.add(cb.like(recommendationJoin.get(CEMSRecommendation_.STATUS_INFO).get(CEMSRecommendationStatusInfo_.NOTE), "%" + entry.getValue() + "%"));
                        break;
                    case ORDER_CONTENT:
                        predicates.add(cb.like(recommendationJoin.get(CEMSRecommendation_.orderContent), "%" + entry.getValue() + "%"));
                        break;
                    case EXECUTION_PERSON:
                        Join<CEMSRecommendation, CEMSResponsiblePerson> personJoin = recommendationJoin.join(CEMSRecommendation_.fiResponsiblePersons, JoinType.LEFT);
                        predicates.add(cb.like(personJoin.get(CEMSResponsiblePerson_.FULL_NAME), "%" + entry.getValue() + "%"));
                        break;
                    case INSPECTION_IDS:
                        List<Long> ids = (List<Long>) entry.getValue();
                        predicates.add(root.get(CEMSInspection_.id).in(ids));
                        break;
                    case PHASE:
                        predicates.add(cb.equal(root.get(CEMSInspection_.phase), entry.getValue()));
                        break;
                }
            }
        }

        return predicates;
    }
}
