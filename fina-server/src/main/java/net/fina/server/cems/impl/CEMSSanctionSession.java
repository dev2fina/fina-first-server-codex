package net.fina.server.cems.impl;

import net.fina.common.client.exception.FinATypeException;
import net.fina.common.shared.SortField;
import net.fina.server.cems.api.CEMSInspectionLocal;
import net.fina.server.cems.api.CEMSSanctionLocal;
import net.fina.server.cems.entity.CEMSInspection;
import net.fina.server.cems.entity.CEMSInspection_;
import net.fina.server.cems.entity.sanction.*;
import net.fina.server.cems.model.CEMSSanctionFilterType;

import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Local(CEMSSanctionLocal.class)
@Stateless
public class CEMSSanctionSession implements CEMSSanctionLocal {
    @Inject
    private EntityManager em;
    @Inject
    private CEMSInspectionLocal inspectionLocal;

    @Override
    public CEMSSanction findById(long sanctionId) throws FinATypeException {
        CEMSSanction sanction =  em.find(CEMSSanction.class, sanctionId);
        if (sanction == null) throw new FinATypeException(FinATypeException.Type.INVALID_VALUE, "Sanction not found");
        return sanction;
    }

    @Override
    public List<CEMSSanction> load(String inspectionId, int offset, int limit, Map<CEMSSanctionFilterType, Object> filterMap, SortField sortField, boolean loadAllEntityProps) {
        if (sortField == null) {
            sortField = new SortField("recordCreateDate", "desc");
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CEMSSanction> query = cb.createQuery(CEMSSanction.class);
        Root<CEMSSanction> root = query.from(CEMSSanction.class);

        if (!loadAllEntityProps) {

            Selection[] selections = new Selection[]{
                    root.get(CEMSSanction_.id),
                    root.get(CEMSSanction_.ORGANIZATION_TYPE),
                    root.get(CEMSSanction_.SUBJECT_LEGAL_NAME),
                    root.get(CEMSSanction_.SUBJECT_ID),
                    root.get(CEMSSanction_.LICENSE_NUMBER),
                    root.get(CEMSSanction_.REGISTRATION_LETTER_NUMBER),
                    root.get(CEMSSanction_.DECISION_MAKING_BODY_CATALOG),
                    root.get(CEMSSanction_.ACTION_DATE),
                    root.get(CEMSSanction_.DOCUMENT_NUMBER),
                    root.get(CEMSSanction_.EXECUTION_PERIOD),
                    root.get(CEMSSanction_.VALIDITY_PERIOD_FROM),
                    root.get(CEMSSanction_.VALIDITY_PERIOD_TO),
                    root.get(CEMSSanction_.INITIAL_COURT_APPEAL_DATE),
                    root.get(CEMSSanction_.FINAL_COURT_APPEAL_DATE),
                    root.get(CEMSSanction_.STATUS),
            };

            query.multiselect(selections);
        }

        List<Predicate> predicates = getFilterPredicate(cb, root, filterMap);
        predicates.add(cb.equal(root.get(CEMSSanction_.inspection).get(CEMSInspection_.id), inspectionId));
        query.distinct(true);
        query.where(predicates.toArray(new Predicate[0]));
        query.orderBy(sortField.isAsc() ? cb.asc(root.get(sortField.getProperty())) : cb.desc(root.get(sortField.getProperty())));

        TypedQuery<CEMSSanction> loadQuery = em.createQuery(query);

        if (offset >= 0 && limit > 0) {
            loadQuery.setFirstResult(offset).setMaxResults(limit);
        }

        return loadQuery.getResultList();
    }

    @Override
    public long count(String inspectionId, Map<CEMSSanctionFilterType, Object> filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<CEMSSanction> root = query.from(CEMSSanction.class);

        query.select(cb.countDistinct(root.get(CEMSSanction_.id)));

        List<Predicate> predicates = getFilterPredicate(cb, root, filter);
        predicates.add(cb.equal(root.get(CEMSSanction_.inspection).get(CEMSInspection_.id), inspectionId));

        query.where(predicates.toArray(new Predicate[0]));

        TypedQuery<Long> loadQuery = em.createQuery(query);

        return loadQuery.getSingleResult();
    }

    @Override
    public CEMSSanction save(CEMSSanction sanction, String inspectionId) throws FinATypeException {
        CEMSInspection inspection = inspectionLocal.findById(inspectionId);

        if (inspection == null) {
            throw new FinATypeException("Inspection Not Found");
        }

        CEMSSanctionStatus status = getStatusByCode(sanction.getStatus().getCode());
        List<CEMSSanctionMeasureInfluence> influences = findInfluencesByCodes(sanction.getMeasureOfInfluence().stream().map(CEMSSanctionMeasureInfluence::getCode).collect(Collectors.toList()));
        List<CEMSSanctionMeasureReasons> measures = findMeasuresByCodes(sanction.getMeasureReasonCatalog().stream().map(CEMSSanctionMeasureReasons::getCode).collect(Collectors.toList()));

        sanction.getRegulationList().forEach(regulation -> {
            CEMSSanctionRegulationTable table = findRegulationByCode(regulation.getRegulationCatalog().getCode());
            if (table != null) {
                regulation.setRegulationCatalog(table);
            }
        });

        if (status != null) {
            sanction.setStatus(status);
        }

        sanction.setMeasureOfInfluence(influences);
        sanction.setMeasureReasonCatalog(measures);

        if (sanction.getId() > 0) {
            sanction.setInspection(inspection);
            sanction = em.merge(sanction);
        } else {
            em.persist(sanction);
            inspection.getSanctionList().add(sanction);
        }
        return sanction;
    }

    @Override
    public CEMSSanctionRegulationTable findRegulationByCode(String code) {
        try {
            return em.createQuery("select r from CEMS_SANCTION_REGULATION_TABLE r where r.code=:code", CEMSSanctionRegulationTable.class).setParameter("code", code).getSingleResult();
        } catch (NullPointerException | NoResultException ignored) {
            return null;
        }
    }

    @Override
    public List<CEMSSanctionMeasureReasons> findMeasuresByCodes(List<String> codes) {
        if (codes != null && !codes.isEmpty()) {
            return em.createQuery("select mi from CEMS_SANCTION_MEASURE_OF_REASONS mi where code in (:codes)", CEMSSanctionMeasureReasons.class).setParameter("codes", codes).getResultList();
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<CEMSSanctionMeasureInfluence> findInfluencesByCodes(List<String> codes) {
        if (codes != null && !codes.isEmpty()) {
            return em.createQuery("select mi from CEMS_SANCTION_MEASURE_INFLUENCE mi where code in (:codes)", CEMSSanctionMeasureInfluence.class).setParameter("codes", codes).getResultList();
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void delete(List<Long> ids) {
        if (!ids.isEmpty()) {
            //remove from other tables
            em.createNativeQuery("delete from CEMS_SANCTION_MEASURE_INFLUENCE_TABLE where SANCTION_ID in (:ids)").setParameter("ids", ids).executeUpdate();
            em.createNativeQuery("delete from CEMS_SANCTION_MEASURE_REASONS_TABLE where SANCTION_ID in (:ids)").setParameter("ids", ids).executeUpdate();
            em.createNativeQuery("delete from CEMS_SANCTION_REGULATIONS  where SANCTION_ID in (:ids)").setParameter("ids", ids).executeUpdate();
            em.createNativeQuery("delete from CEMS_SANCTIONED_EMPLOYEES  where SANCTION_ID in (:ids)").setParameter("ids", ids).executeUpdate();
            em.createQuery("delete from CEMS_SANCTIONS where id in (:ids)").setParameter("ids", ids).executeUpdate();
        }
    }

    @Override
    public List<CEMSSanctionRegulationTable> loadRegulations() {
        return em.createQuery("select r from CEMS_SANCTION_REGULATION_TABLE r", CEMSSanctionRegulationTable.class).getResultList();
    }

    @Override
    public List<CEMSSanctionMeasureInfluence> loadMeasureInfluences() {
        return em.createQuery("select mi from CEMS_SANCTION_MEASURE_INFLUENCE mi", CEMSSanctionMeasureInfluence.class).getResultList();
    }

    @Override
    public List<CEMSSanctionMeasureReasons> loadMeasureReasons() {
        return em.createQuery("select mt from CEMS_SANCTION_MEASURE_OF_REASONS mt", CEMSSanctionMeasureReasons.class).getResultList();
    }

    @Override
    public List<CEMSSanctionStatus> loadStatuses() {
        return em.createQuery("select st from CEMS_SANCTION_STATUS_TABLE st", CEMSSanctionStatus.class).getResultList();
    }

    @Override
    public List<CEMSSanction> loadByIds(List<Long> sanctionIds) {
        return em.createQuery("select s from CEMS_SANCTIONS s where s.id in (:ids)", CEMSSanction.class)
                .setParameter("ids", sanctionIds)
                .getResultList();
    }

    @Override
    public CEMSSanctionStatus getStatusByCode(String code) {
        try {
            return em.createQuery("select s from CEMS_SANCTION_STATUS_TABLE s where s.code=:code", CEMSSanctionStatus.class).setParameter("code", code).getSingleResult();
        } catch (NullPointerException | NoResultException e) {
            return null;
        }
    }

    private List<Predicate> getFilterPredicate(CriteriaBuilder
                                                       cb, Root<CEMSSanction> root, Map<CEMSSanctionFilterType, Object> filter) {
        List<Predicate> predicates = new ArrayList<>();

        if (filter != null) {
            for (Map.Entry<CEMSSanctionFilterType, Object> entry : filter.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }
                switch (entry.getKey()) {
                    case ORGANIZATION_TYPE:
                        predicates.add(cb.equal(root.get(CEMSSanction_.ORGANIZATION_TYPE), entry.getValue()));
                        break;
                    case NAME:
                        predicates.add(cb.like(root.get(CEMSSanction_.subjectLegalName), "%" + entry.getValue() + "%"));
                        break;
                    case ID:
                        predicates.add(cb.like(root.get(CEMSSanction_.SUBJECT_ID), "%" + entry.getValue() + "%"));
                        break;
                    case LICENSE_NUMBER:
                        predicates.add(cb.like(root.get(CEMSSanction_.LICENSE_NUMBER), "%" + entry.getValue() + "%"));
                        break;
                    case REGISTRATION_LETTER_NUMBER:
                        predicates.add(cb.like(root.get(CEMSSanction_.REGISTRATION_LETTER_NUMBER), "%" + entry.getValue() + "%"));
                        break;
                    case DECISION_MAKING_BODY:
                        predicates.add(cb.equal(root.get(CEMSSanction_.DECISION_MAKING_BODY_CATALOG), entry.getValue()));
                        break;
                    case ACTION_DATE_FROM:
                        predicates.add(cb.greaterThanOrEqualTo(root.get(CEMSSanction_.ACTION_DATE), (Date) entry.getValue()));
                        break;
                    case ACTION_DATE_TO:
                        predicates.add(cb.lessThanOrEqualTo(root.get(CEMSSanction_.ACTION_DATE), (Date) entry.getValue()));
                        break;
                    case DOCUMENT_NUMBER:
                        predicates.add(cb.like(root.get(CEMSSanction_.DOCUMENT_NUMBER), "%" + entry.getValue() + "%"));
                        break;
                    case EXECUTION_PERIOD_FROM:
                        predicates.add(cb.greaterThanOrEqualTo(root.get(CEMSSanction_.EXECUTION_PERIOD), (Date) entry.getValue()));
                        break;
                    case EXECUTION_PERIOD_TO:
                        predicates.add(cb.lessThanOrEqualTo(root.get(CEMSSanction_.EXECUTION_PERIOD), (Date) entry.getValue()));
                        break;
                    case INITIAL_APPEAL_DATE_FROM:
                        predicates.add(cb.greaterThanOrEqualTo(root.get(CEMSSanction_.INITIAL_COURT_APPEAL_DATE), (Date) entry.getValue()));
                        break;
                    case INITIAL_APPEAL_DATE_TO:
                        predicates.add(cb.lessThanOrEqualTo(root.get(CEMSSanction_.INITIAL_COURT_APPEAL_DATE), (Date) entry.getValue()));
                        break;
                    case FINAL_APPEAL_DATE_FROM:
                        predicates.add(cb.greaterThanOrEqualTo(root.get(CEMSSanction_.FINAL_COURT_APPEAL_DATE), (Date) entry.getValue()));
                        break;
                    case FINAL_APPEAL_DATE_TO:
                        predicates.add(cb.lessThanOrEqualTo(root.get(CEMSSanction_.FINAL_COURT_APPEAL_DATE), (Date) entry.getValue()));
                        break;
                    case STATUS:
                        predicates.add(cb.equal(root.get(CEMSSanction_.status).get(CEMSSanctionStatus_.CODE), entry.getValue()));
                        break;
                }
            }
        }

        return predicates;
    }


}
