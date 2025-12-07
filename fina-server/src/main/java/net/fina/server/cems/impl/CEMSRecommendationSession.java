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
import net.fina.server.cems.api.CEMSRecommendationLocal;
import net.fina.server.cems.api.CEMSSanctionLocal;
import net.fina.server.cems.entity.*;
import net.fina.server.cems.entity.sanction.CEMSSanctionStatus;
import net.fina.server.cems.entity.sanction.CEMSSanctionStatus_;
import net.fina.server.cems.model.CEMSRecommendationFilterType;
import net.fina.server.interceptors.RecordingAuditor;
import org.jboss.ejb3.annotation.SecurityDomain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Local(CEMSRecommendationLocal.class)
@Stateless
@Interceptors(RecordingAuditor.class)
@SecurityDomain("FinASecurityDomain")
public class CEMSRecommendationSession implements CEMSRecommendationLocal {
    @Inject
    private EntityManager em;

    @Inject
    private CEMSInspectionLocal inspectionLocal;

    @Inject
    private CEMSSanctionLocal sanctionLocal;

    @Override
    public List<CEMSRecommendation> loadRecommendations(String inspectionId, int offset, int limit, Map<CEMSRecommendationFilterType, Object> filter, SortField sortField) {
        if (sortField == null) {
            sortField = new SortField("recordCreateDate", "desc");
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CEMSRecommendation> query = cb.createQuery(CEMSRecommendation.class);
        Root<CEMSRecommendation> root = query.from(CEMSRecommendation.class);
        Join<CEMSRecommendation, CEMSResponsiblePerson> join = root.join(CEMSRecommendation_.fiResponsiblePersons, JoinType.LEFT);

        List<Predicate> predicates = getFilterPredicate(cb, root, join, filter);
        if (inspectionId != null) {
            predicates.add(cb.equal(root.get(CEMSRecommendation_.inspection).get(CEMSInspection_.id), inspectionId));
        }
        query.distinct(true);
        query.where(predicates.toArray(new Predicate[0]));

        Order orderBy;
        if (sortField.getProperty().equalsIgnoreCase(CEMSRecommendationStatusInfo_.STATUS)) {
            Path<Object> path = root.get(CEMSRecommendation_.STATUS_INFO).get(CEMSRecommendationStatusInfo_.STATUS);
            orderBy = sortField.isAsc() ? cb.asc(path) : cb.desc(path);
        } else {
            orderBy = sortField.isAsc() ? cb.asc(root.get(sortField.getProperty())) : cb.desc(root.get(sortField.getProperty()));
        }

        query.orderBy(orderBy);

        TypedQuery<CEMSRecommendation> loadQuery = em.createQuery(query);

        if (offset >= 0 && limit > 0) {
            loadQuery.setFirstResult(offset).setMaxResults(limit);
        }

        return loadQuery.getResultList();
    }

    @Override
    public long countRecommendations(String inspectionId, Map<CEMSRecommendationFilterType, Object> filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<CEMSRecommendation> root = query.from(CEMSRecommendation.class);
        Join<CEMSRecommendation, CEMSResponsiblePerson> join = root.join(CEMSRecommendation_.fiResponsiblePersons, JoinType.LEFT);

        query.select(cb.countDistinct(root.get(CEMSRecommendation_.id)));

        List<Predicate> predicates = getFilterPredicate(cb, root, join, filter);
        predicates.add(cb.equal(root.get(CEMSRecommendation_.inspection).get(CEMSInspection_.id), inspectionId));

        query.where(predicates.toArray(new Predicate[0]));

        TypedQuery<Long> loadQuery = em.createQuery(query);

        return loadQuery.getSingleResult();
    }

    @Override
    public CEMSRecommendation save(String inspectionId, CEMSRecommendation recommendation) throws FinATypeException {
        CEMSInspection inspection = inspectionLocal.findById(inspectionId);
        CEMSSanctionStatus status = sanctionLocal.getStatusByCode(recommendation.getStatusInfo().getStatus().getCode());

        if (inspection == null) {
            throw new FinATypeException("Invalid Inspection ID");
        }

        if (recommendation.getType() == null) {
            throw new FinATypeException("Type Is Required");
        }

        if (recommendation.getStatusInfo().getStatus() == null) {
            throw new FinATypeException("Status Is Required");
        }

        recommendation.setInspection(inspection);

        if (status != null) {
            recommendation.getStatusInfo().setStatus(status);
        }

        if (recommendation.getId() != null && !recommendation.getId().trim().isEmpty()) {
            CEMSRecommendation existing = em.find(CEMSRecommendation.class, recommendation.getId().trim());
            if (existing == null) {
                throw new FinATypeException(FinATypeException.Type.INVALID_CODE);
            }
            recommendation.setRecordCreateDate(existing.getRecordCreateDate());

            if (!existing.getStatusInfo().equals(recommendation.getStatusInfo())) {
                saveStatusHistory(recommendation, new Date());
            }

            recommendation = em.merge(recommendation);

        } else {
            recommendation.setId(generateId());
            recommendation.setRecordCreateDate(new Date());
            em.persist(recommendation);

            saveStatusHistory(recommendation, recommendation.getRecordCreateDate());
        }

        return recommendation;
    }

    @Override
    public void delete(List<String> recommendationIds) {
        if (recommendationIds != null && !recommendationIds.isEmpty()) {
            em.createNativeQuery("delete from CEMS_RESPONSIBLE_PERSONS where RECOMMENDATION_ID in (:ids) ").setParameter("ids", recommendationIds).executeUpdate();
            em.createQuery("delete from CEMS_RECOMMENDATIONS_STATUS_HISTORY h where h.recommendation.id in (:ids)").setParameter("ids", recommendationIds).executeUpdate();
            em.createQuery("delete  from CEMS_RECOMMENDATIONS where id in (:ids)").setParameter("ids", recommendationIds).executeUpdate();
        }
    }

    @Override
    public CEMSRecommendation findById(String recommendationId) throws FinATypeException {
        CEMSRecommendation recommendation =  em.find(CEMSRecommendation.class, recommendationId);
        if (recommendation == null) throw new FinATypeException(FinATypeException.Type.INVALID_VALUE, "Recommendation not found");
        return recommendation;
    }

    @Override
    public List<CEMSRecommendationStatusHistory> loadStatusHistory(String recommendationId) {
        return em.createQuery("select h from CEMS_RECOMMENDATIONS_STATUS_HISTORY h where h.recommendation.id=:recommendationId order by h.id desc ", CEMSRecommendationStatusHistory.class).setParameter("recommendationId", recommendationId).getResultList();
    }


    private String generateId() {
        LocalDate localDate = LocalDate.now();

        StringBuilder sb = new StringBuilder();
        sb.append(localDate.getYear()).append("-").append(localDate.getMonthValue()).append("-");

        List<String> ids = em.createQuery("select ins.id from CEMS_RECOMMENDATIONS ins where ins.id like :id order by ins.recordCreateDate desc ", String.class).setParameter("id", sb.toString() + "%").getResultList();

        String existingID = ids.isEmpty() ? null : ids.get(0);
        int counter = existingID == null ? 1 : Integer.parseInt(existingID.substring(existingID.lastIndexOf("-") + 1));
        sb.append(counter + 1);

        return sb.toString();
    }

    private void saveStatusHistory(CEMSRecommendation recommendation, Date recordDate) {
        int version = em.createQuery("select count(cmh.id) from CEMS_RECOMMENDATIONS_STATUS_HISTORY cmh where cmh.recommendation.id=:recommendatioId", Long.class).setParameter("recommendatioId", recommendation.getId().trim()).getSingleResult().intValue();

        //save status history
        CEMSRecommendationStatusHistory statusHistory = new CEMSRecommendationStatusHistory();
        statusHistory.setStatusInfo(recommendation.getStatusInfo());
        statusHistory.setRecordDate(recordDate);
        statusHistory.setRecommendation(recommendation);
        statusHistory.setVersion(version + 1);
        em.persist(statusHistory);
    }

    private List<Predicate> getFilterPredicate(CriteriaBuilder cb, Root<CEMSRecommendation> root, Join<CEMSRecommendation, CEMSResponsiblePerson> personJoin, Map<CEMSRecommendationFilterType, Object> filter) {
        List<Predicate> predicates = new ArrayList<>();

        if (filter != null) {
            for (Map.Entry<CEMSRecommendationFilterType, Object> entry : filter.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }
                switch (entry.getKey()) {
                    case CODE:
                        predicates.add(cb.like(root.get(CEMSRecommendation_.id), "%" + entry.getValue() + "%"));
                        break;
                    case EXECUTION_DATE_FROM:
                        predicates.add(cb.greaterThanOrEqualTo(root.get(CEMSRecommendation_.EXECUTION_PERIOD), (Date) entry.getValue()));
                        break;
                    case EXECUTION_DATE_TO:
                        predicates.add(cb.lessThanOrEqualTo(root.get(CEMSRecommendation_.EXECUTION_PERIOD), (Date) entry.getValue()));
                        break;
                    case RESPONSIBLE_PERSON:
                        predicates.add(cb.like(personJoin.get(CEMSResponsiblePerson_.FULL_NAME), "%" + entry.getValue() + "%"));
                        break;
                    case STATUS:
                        predicates.add(cb.equal(root.get(CEMSRecommendation_.statusInfo).get(CEMSRecommendationStatusInfo_.status).get(CEMSSanctionStatus_.CODE), entry.getValue()));
                        break;
                    case TYPE:
                        predicates.add(cb.equal(root.get(CEMSRecommendation_.TYPE), entry.getValue()));
                        break;
                    case RECOMMENDATION_IDS:
                        List<Long> ids = (List<Long>) entry.getValue();
                        predicates.add(root.get(CEMSRecommendation_.id).in(ids));
                        break;
                }
            }
        }

        return predicates;
    }
}
