package net.fina.server.legalperson.impl;

import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.fis.FiTypeSimpleModel;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.fi.api.FiLocal;
import net.fina.server.fi.entity.*;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.legalperson.api.LegalPersonLocal;
import net.fina.server.legalperson.entity.*;
import net.fina.server.legalperson.model.LegalPersonMetaModel;
import net.fina.server.legalperson.model.PersonFilter;
import net.fina.server.legalperson.model.connection.CompanyDependencyModel;
import net.fina.server.legalperson.model.connection.ConnectedCompanyModel;
import net.fina.server.legalperson.model.helper.LegalPersonModelHelper;
import net.fina.server.person.entity.CriminalRecord;
import net.fina.server.person.entity.PersonPosition;
import net.fina.server.person.model.PersonMetaModel;
import net.fina.server.person.model.PersonStatus;
import net.fina.server.person.model.helper.PersonModelHelper;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jboss.ejb3.annotation.SecurityDomain;

import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("JpaQlInspection")
@Stateless
@Local(LegalPersonLocal.class)
@SecurityDomain("FinASecurityDomain")
@Interceptors(RecordingAuditor.class)
public class LegalPersonSession implements LegalPersonLocal {
    @Inject
    private EntityManager em;

    @Inject
    private FiLocal fiLocal;

    @Override
    public List<LegalPerson> loadFiLegalPersons(int start, int limit, long fiId, Map<PersonFilter, Object> filter) throws FinATypeException {
        fiLocal.checkUserHasFiAccess(fiId);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<FiLegalPerson> cq = cb.createQuery(FiLegalPerson.class);

        Root<FiLegalPerson> root = cq.from(FiLegalPerson.class);
        List<Predicate> predicates = getFiLegalPersonPredicates(cb, root, filter, fiId);
        cq.where(predicates.toArray(new Predicate[0]));

//        TypedQuery<FiLegalPerson> query = em.createQuery("select blp.legalPerson.id from IN_BANK_LEGAL_PERSONS blp  where blp.fi.id=:fiId and blp.legalPerson.deleted=false order by blp.legalPerson.id desc", FiLegalPerson.class);
        TypedQuery<FiLegalPerson> query = em.createQuery(cq);
//        query.setParameter("fiId", fiId);

        if (start >= 0 && limit > 0) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }

        return query.getResultList().stream().map(blp -> {
            LegalPerson lp = blp.getLegalPerson();
            lp.setFiLegalPersonId(blp.getId());
            lp.setConnections(blp.getConnections());
            return lp;
        }).collect(Collectors.toList());
    }

    @Override
    public List<LegalPerson> loadLegalPersons(int page, int limit, long langId, Map<PersonFilter, Object> filter) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<LegalPerson> cq = cb.createQuery(LegalPerson.class);

        Root<LegalPerson> root = cq.from(LegalPerson.class);
        List<Predicate> predicates = getPredicates(cb, root, filter);

//        predicates.add(cb.equal(root.get(LegalPerson_.deleted), false));

        cq.where(predicates.toArray(new Predicate[0]));
        cq.orderBy(cb.desc(root.get(LegalPerson_.id)));


        TypedQuery<LegalPerson> query = em.createQuery(cq);

        if (page >= 0 && limit > 0) {
            query.setFirstResult(page);
            query.setMaxResults(limit);
        }

        return query.getResultList();
    }

    @Override
    public long countFiLegalPersons(long fiId, Map<PersonFilter, Object> filter) {
//        return em.createQuery("select count (blp.legalPerson.id) from IN_BANK_LEGAL_PERSONS blp where blp.fi.id=:fiId and blp.legalPerson.deleted=false ", Long.class).setParameter("fiId", fiId).getSingleResult();

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<FiLegalPerson> root = cq.from(FiLegalPerson.class);
        cq.select(cb.countDistinct(root.get(FiLegalPerson_.id)));

        List<Predicate> predicates = getFiLegalPersonPredicates(cb, root, filter, fiId);
//        predicates.add(cb.equal(root.get(LegalPerson_.deleted), false));

        cq.where(predicates.toArray(new Predicate[0]));

        return em.createQuery(cq).getSingleResult();
    }

    @Override
    @Transactional(rollbackOn = FinATypeException.class)
    public LegalPerson create(LegalPerson legalPerson, long fiId, List<Beneficiary> otherShares) throws FinATypeException {
        checkProgrammaticallyDeleted(legalPerson.getIdentificationNumber());

        fiLocal.checkUserHasFiAccess(fiId);

        legalPerson.setId(0);
        validateIdentificationNumberAndCheckUnique(legalPerson.getIdentificationNumber());

        if (legalPerson.getCountry() != null) {
            Region region = em.find(Region.class, legalPerson.getCountry().getId());
            legalPerson.setCountry(region);
        }

        if (legalPerson.getContactInfo() != null) {
            if (legalPerson.getContactInfo().getRegion() != null) {
                legalPerson.getContactInfo().setRegion(em.find(Region.class, legalPerson.getContactInfo().getRegion().getId()));
            }
            em.persist(legalPerson.getContactInfo());
        }

        double totalShare = legalPerson.getBeneficiaries().stream().mapToDouble(Beneficiary::getShare).sum();

        if (totalShare > 100) {
            throw new FinATypeException(FinATypeException.Type.INVALID_VALUE);
        }
        List<Beneficiary> beneficiaries = legalPerson.getBeneficiaries();

        for (Beneficiary beneficiary : beneficiaries) {
            if (beneficiary.getShare() <= 0 || (beneficiary.getLegalPerson() == null && beneficiary.getPhysicalPerson() == null)) {
                throw new FinATypeException(FinATypeException.Type.INVALID_VALUE);
            } else if (beneficiary.getLegalPerson().getId() == legalPerson.getId()) {
                throw new FinATypeException("Legal person can not be its own shareholder");
            }

            if (legalPerson.getBeneficiaries().contains(beneficiary)) {
                em.merge(beneficiary);
            } else {
                em.persist(beneficiary);
            }
        }
        if (!legalPerson.getBeneficiaries().isEmpty()) {
            legalPerson.getBeneficiaries().removeIf(existingBeneficiary -> !beneficiaries.contains(existingBeneficiary));
        }

        if (otherShares != null) {
            manageOtherShares(otherShares, legalPerson);
        }
        legalPerson = em.merge(legalPerson);

        //fi legal person persist
        if (findFiLegalPerson(fiId, legalPerson.getId()) == null) {
            createFiLegalPersonRelation(legalPerson.getId(), fiId, null);
        }

        return legalPerson;
    }

    @Override
    public LegalPerson update(LegalPerson legalPerson, long fiId, List<Beneficiary> otherShares) throws FinATypeException {
        fiLocal.checkUserHasFiAccess(fiId);

        LegalPerson existing = em.find(LegalPerson.class, legalPerson.getId());
        legalPerson.setFiId(existing.getFiId());

        if (!existing.getIdentificationNumber().equals(legalPerson.getIdentificationNumber())) {
            validateIdentificationNumberAndCheckUnique(legalPerson.getIdentificationNumber());
        }

        if (legalPerson.getCountry() != null) {
            Region region = em.find(Region.class, legalPerson.getCountry().getId());
            legalPerson.setCountry(region);
        }

        if (legalPerson.getContactInfo() != null) {
            if (legalPerson.getContactInfo().getRegion() != null) {
                legalPerson.getContactInfo().setRegion(em.find(Region.class, legalPerson.getContactInfo().getRegion().getId()));
            }
            em.merge(legalPerson.getContactInfo());
        }
        for (Beneficiary beneficiary : existing.getBeneficiaries()) {
            if (!legalPerson.getBeneficiaries().contains(beneficiary)) {
                em.remove(beneficiary);
            }
        }


        for (CriminalRecord criminalRecord : existing.getCriminalRecords()) {
            if (!legalPerson.getCriminalRecords().contains(criminalRecord)) {
                em.remove(criminalRecord);
            }
        }

        for (PersonPosition position : existing.getPositions()) {
            if (!legalPerson.getPositions().contains(position)) {
                em.remove(position);
            }
        }

        double totalShare = legalPerson.getBeneficiaries().stream().mapToDouble(Beneficiary::getShare).sum();

        if (totalShare > 100) {
            throw new FinATypeException(FinATypeException.Type.INVALID_VALUE);
        }

        List<Beneficiary> beneficiaries = legalPerson.getBeneficiaries();

        for (Beneficiary beneficiary : beneficiaries) {
            if (beneficiary.getShare() <= 0 || (beneficiary.getLegalPerson() == null && beneficiary.getPhysicalPerson() == null)) {
                throw new FinATypeException(FinATypeException.Type.INVALID_VALUE);

            } else if (beneficiary.getLegalPerson() != null && (beneficiary.getLegalPerson().getId() == legalPerson.getId())) {
                throw new FinATypeException("Legal person can not be its own shareholder");
            } else if ((beneficiary.getLegalPerson() != null && beneficiary.getLegalPerson().getId() <= 0) || (beneficiary.getPhysicalPerson() != null && beneficiary.getPhysicalPerson().getId() <= 0)) {
                throw new FinATypeException("Invalid value for shareholder's name");
            }

            if (existing.getBeneficiaries().contains(beneficiary)) {
                em.merge(beneficiary);
            } else {
                em.persist(beneficiary);
            }
        }
        if (!existing.getBeneficiaries().isEmpty()) {
            existing.getBeneficiaries().removeIf(existingBeneficiary -> !beneficiaries.contains(existingBeneficiary));
        }

        if (otherShares != null) {
            manageOtherShares(otherShares, legalPerson);
        }

        //fi legal person persist
        if (findFiLegalPerson(fiId, legalPerson.getId()) == null) {
            createFiLegalPersonRelation(legalPerson.getId(), fiId, null);
        }

        if (legalPerson.getFiId() > 0) {
            Fi fi = fiLocal.getFiById(legalPerson.getFiId());
            fi.setDisable(legalPerson.getStatus() != PersonStatus.ACTIVE);
            em.merge(fi);
        }

        return em.merge(legalPerson);
    }


    @Override
    public LegalPerson findByIdentificationNumber(String identificationCode) {
        List<LegalPerson> result = em.createQuery("select lp from IN_LEGAL_PERSONS lp where lp.identificationNumber=:identification", LegalPerson.class)
                .setParameter("identification", identificationCode.trim())
                .getResultList();

        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public long countLegalPersons(Map<PersonFilter, Object> filter, long langId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<LegalPerson> root = cq.from(LegalPerson.class);
        cq.select(cb.countDistinct(root.get(LegalPerson_.id)));

        List<Predicate> predicates = getPredicates(cb, root, filter);
//        predicates.add(cb.equal(root.get(LegalPerson_.deleted), false));

        cq.where(predicates.toArray(new Predicate[0]));

        return em.createQuery(cq).getSingleResult();
    }

    @Override
    public List<LegalPerson> loadAllPersonSimple(Map<PersonFilter, Object> filter) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<LegalPerson> cq = builder.createQuery(LegalPerson.class);
        Root<LegalPerson> root = cq.from(LegalPerson.class);
        cq.multiselect(root.get(LegalPerson_.id), root.get(LegalPerson_.IDENTIFICATION_NUMBER), root.get(LegalPerson_.NAME), root.get(LegalPerson_.fiId));

        List<Predicate> predicates = getPredicates(builder, root, filter);
        cq.where(predicates.toArray(new Predicate[0]));
        cq.orderBy(builder.desc(root.get(LegalPerson_.id)));

        return em.createQuery(cq).getResultList();
    }

    @Override
    public LegalPerson getLegalPersonById(long id) {
        return em.find(LegalPerson.class, id);
    }

    @Override
    public LegalPerson getLegalPersonByFiId(long fiId) {
        try {
            return em.createQuery("select lp from IN_LEGAL_PERSONS lp where lp.fiId=:fiId", LegalPerson.class).setParameter("fiId", fiId).getSingleResult();

        } catch (NoResultException | NullPointerException e) {
            return null;
        }
    }

    @Override
    public void deleteFiLegalPersonsConnection(long fiId, List<Long> legalPersonIds) throws FinATypeException {
        if (!legalPersonIds.isEmpty()) {
            fiLocal.checkUserHasFiAccess(fiId);

            LegalPerson fiAsLegalPerson = getLegalPersonByFiId(fiId);

            List<Long> filteredIds = em.createQuery("select lp.id from IN_BANK_LEGAL_PERSONS lp where lp.fi.id=:bankId and lp.id in(:legalPersonIds)", Long.class).setParameter("bankId", fiId).setParameter("legalPersonIds", legalPersonIds).getResultList();

            if (!Objects.equals(legalPersonIds, filteredIds)) {
                throw new FinATypeException(FinATypeException.Type.INVALID_PERMISSIONS);
            }

            for (Beneficiary beneficiary : fiAsLegalPerson.getBeneficiaries()) {
                for (long id : legalPersonIds) {
                    if (beneficiary.getLegalPerson() != null && beneficiary.getLegalPerson().getId() == em.find(FiLegalPerson.class, id).getLegalPerson().getId()) {
                        throw new FinATypeException(FinATypeException.Type.DEPENDENCY_ERROR, new String[]{"Has Beneficiary Dependency"});
                    }
                }
            }

            for (long id : legalPersonIds) {

                List<Beneficiary> otherShares = loadLegalPersonOtherShares(em.find(FiLegalPerson.class, id).getLegalPerson().getId());
                if (!otherShares.isEmpty()) {
                    throw new FinATypeException(FinATypeException.Type.DEPENDENCY_ERROR, new String[]{"Has Other Share Dependency"});
                }
            }

            em.createQuery("delete from IN_BANK_LEGAL_PERSONS lp where lp.id in(:ids) ").setParameter("ids", legalPersonIds.isEmpty() ? Collections.singletonList(-1L) : legalPersonIds).executeUpdate();
        }
    }

    @Override
    public void deleteLegalPerson(List<Long> legalPersonIds) throws FinATypeException {

        List<Long> filteredIds = em.createQuery("select lp.id from IN_BANK_LEGAL_PERSONS lp where lp.legalPerson.id in(:legalPersonIds)", Long.class).setParameter("legalPersonIds", legalPersonIds).getResultList();

        if (!filteredIds.isEmpty()) {
            throw new FinATypeException(FinATypeException.Type.DEPENDENCY_ERROR, new String[]{"Has Fi Legal Person Dependency"});
        }

        List<Long> beneficiaries = em.createQuery("select b.id from IN_BENEFICIARIES b where b.legalPerson.id in(:ids)", Long.class).setParameter("ids", legalPersonIds).getResultList();

        if (!beneficiaries.isEmpty()) {
            throw new FinATypeException(FinATypeException.Type.DEPENDENCY_ERROR, new String[]{"Has Beneficiary Dependency"});
        }

        List<Long> personPositions = em.createQuery("select p.id from IN_PERSON_POSITIONS p where p.company.id in (:ids)", Long.class).setParameter("ids", legalPersonIds).getResultList();
        if (!personPositions.isEmpty()) {
            throw new FinATypeException(FinATypeException.Type.DEPENDENCY_ERROR, new String[]{"Persons Has Positions in this company"});
        }

        em.createQuery("update IN_LEGAL_PERSONS set deleted=true where id in(:ids)").setParameter("ids", legalPersonIds).executeUpdate();
    }

    @Override
    public void deleteFiLegalPerson(long fiId, long legalPersonId) throws FinATypeException {
        fiLocal.checkUserHasFiAccess(fiId);
        FiLegalPerson fiLegalPerson = em.find(FiLegalPerson.class, legalPersonId);
        em.remove(fiLegalPerson);
    }

    @Override
    public FiLegalPerson getFiLegalPerson(long id) {
        return em.createQuery("select lp from IN_BANK_LEGAL_PERSONS lp where lp.legalPerson.id=:personId", FiLegalPerson.class).setParameter("personId", id).getSingleResult();
    }

    @Override
    public List<PersonPosition> loadLegalPersonPositions(long legalPersonId) {
        return em.createQuery("select pp from IN_PERSON_POSITIONS pp where pp.company.id=:id", PersonPosition.class).setParameter("id", legalPersonId).getResultList();
    }

    @Override
    public List<Beneficiary> getLegalPersonBeneficiariesByPersonId(long personId) {
        return em.createQuery("select lb from IN_LEGAL_PERSON_BENEFICIARIES lb where lb.physicalPerson.id=:personId and lb.legalPerson.fiId=false ", Beneficiary.class).setParameter("personId", personId).getResultList();
    }

    @Override
    public void createFiAsLegalPerson(Fi fi) {
        List<LegalPerson> legalPersons = em.createQuery("select lp from IN_LEGAL_PERSONS lp where lp.fiId=:fiId", LegalPerson.class).setParameter("fiId", fi.getId()).getResultList();
        if (legalPersons.isEmpty()) {
            LegalPerson lp = new LegalPerson();
            lp.setIdentificationNumber(fi.getIdentificationCode().trim());
            lp.setCountry(em.find(Region.class, fi.getRegionId()));
            lp.setFiId(fi.getId());
            lp.setName(fi.getDescription());
            lp.setStatus(fi.isDisable() ? PersonStatus.INACTIVE : PersonStatus.ACTIVE);

            em.persist(lp);

        } else {
            LegalPerson lp = legalPersons.get(0);
            lp.setIdentificationNumber(fi.getIdentificationCode().trim());
            lp.setName(fi.getDescription());
            if (fi.getRegionId() != null) {
                lp.setCountry(em.find(Region.class, fi.getRegionId()));
            }
            lp.setStatus(fi.isDisable() ? PersonStatus.INACTIVE : PersonStatus.ACTIVE);
        }

    }

    @Override
    public List<Beneficiary> loadLegalPersonOtherShares(long legalPersonId) {
        return em.createQuery("select new net.fina.server.legalperson.entity.Beneficiary(b.id,b.share,b.creationDate,lp.id,lp.name,lp.identificationNumber,lp.fiId,lc) from IN_LEGAL_PERSONS lp,IN(lp.beneficiaries) b left join lp.country lc on lc.id=lp.country.id where b.legalPerson.id=:legalPersonId order by b.creationDate desc ", Beneficiary.class).setParameter("legalPersonId", legalPersonId).getResultList();
    }

    @Override
    public FiLegalPerson createFiLegalPersonRelation(long legalPersonId, long fiId, FiLegalPersonConnectionType connectionType) throws FinATypeException {
        if (fiId <= 0) {
            return null;
        }

        Fi fi = fiLocal.getFiById(fiId);
        LegalPerson legalPerson = getLegalPersonById(legalPersonId);

        if (legalPerson == null) {
            throw new FinATypeException("Legal Person Not Found");
        }
        if (fi == null) {
            throw new FinATypeException("Fi Not Found");
        }

        if (fi.getId() == legalPerson.getFiId()) {
            throw new FinATypeException(FinATypeException.Type.GENERAL_ERROR);
        }

        //check existed
        FiLegalPerson fiLegalPerson = findFiLegalPerson(fiId, legalPersonId);

        if (fiLegalPerson == null) {
            fiLegalPerson = new FiLegalPerson(fi, legalPerson);
            if (connectionType != null) {
                fiLegalPerson.setConnections(Collections.singletonList(new FiLegalPersonConnection(connectionType)));
            }

            em.persist(fiLegalPerson);
        } else if (connectionType != null) {
            List<FiLegalPersonConnection> connections = fiLegalPerson.getConnections();

            connections.add(new FiLegalPersonConnection(connectionType));

            fiLegalPerson.setConnections(connections);

        }


        return fiLegalPerson;
    }

    @Override
    public FiLegalPerson findFiLegalPerson(long fiId, long legalPersonId) {
        List<FiLegalPerson> legalPersons = em.createQuery("select blp from IN_BANK_LEGAL_PERSONS blp where blp.fi.id=:fiId and blp.legalPerson.id=:legalPersonId", FiLegalPerson.class).setParameter("fiId", fiId).setParameter("legalPersonId", legalPersonId).getResultList();

        if (!legalPersons.isEmpty()) {
            return legalPersons.get(0);
        }

        return null;
    }


    private void validateIdentificationNumberAndCheckUnique(String identificationNumber) throws FinATypeException {
        if (identificationNumber == null || identificationNumber.trim().isEmpty()) {
            throw new FinATypeException(FinATypeException.Type.CODE_UNIQUE, new String[]{"Identification Number Is Required"});
        }

        List<Long> legalPersons = em.createQuery("select p.id from IN_LEGAL_PERSONS p where p.identificationNumber=:idNumber", Long.class).setParameter("idNumber", identificationNumber.trim()).getResultList();


        if (!legalPersons.isEmpty()) {
            throw new FinATypeException(FinATypeException.Type.CODE_UNIQUE, new String[]{"Identification Number Is Not Unique"});
        }
    }

    private void manageOtherShares(List<Beneficiary> otherShares, LegalPerson legalPerson) throws FinATypeException {

        List<Beneficiary> personBeneficiaries = em.createQuery("select b from IN_BENEFICIARIES b where b.legalPerson.id=:personId", Beneficiary.class).setParameter("personId", legalPerson.getId()).getResultList();

        List<Long> ids = new ArrayList<>();

        personBeneficiaries.stream().filter(lb -> !otherShares.contains(lb)).forEach(b -> ids.add(b.getId()));

        LegalPerson lp;
        for (Beneficiary share : otherShares) {

            lp = em.find(LegalPerson.class, share.getLegalPerson().getId());
            share.setActive(true);
            share.setPhysicalPerson(null);
            share.setLegalPerson(legalPerson);

            if (lp.getId() == legalPerson.getId()) {
                throw new FinATypeException("Legal person can not be its own shareholder");
            }

            if (share.getShare() <= 0) {
                throw new FinATypeException(FinATypeException.Type.INVALID_VALUE);
            }

            if (lp.getBeneficiaries().stream().mapToDouble(Beneficiary::getShare).sum() + share.getShare() > 100) {
                throw new FinATypeException(FinATypeException.Type.INVALID_VALUE);
            }

            share = em.merge(share);

            if (lp.getFiId() > 0) {
                createFiLegalPersonRelation(legalPerson.getId(), lp.getFiId(), FiLegalPersonConnectionType.BENEFICIARY);
            }

            if (!lp.getBeneficiaries().contains(share)) {
                lp.getBeneficiaries().add(share);
            }
        }

        if (!ids.isEmpty()) {
            em.createNativeQuery("delete from IN_LEGAL_PERSON_BENEFICIARIES_TABLE where BENEFICIARY_ID in (:ids)").setParameter("ids", ids).executeUpdate();
            em.createQuery("delete from IN_BENEFICIARIES where id in (:ids)").setParameter("ids", ids).executeUpdate();
        }

    }

    @Override
    public List<ConnectedCompanyModel> calculateCompanyConnections(LegalPerson company) {
        long langId = ThreadLocalHolder.getLanguage().getId();
        Map<Long, FiTypeSimpleModel> fiIdToTypeMap = fiLocal.loadFiIDTypeMap();

        List<LegalPerson> allLegalPersons = em.createQuery("select l from IN_LEGAL_PERSONS l", LegalPerson.class).getResultList();

        Map<LegalPersonMetaModel, Set<CompanyDependencyModel>> result = new HashMap<>();
        Map<Long, LegalPerson> fiLegalPersonMap = allLegalPersons.stream().filter(f -> f.getFiId() > 0).collect(Collectors.toMap(LegalPerson::getFiId, lp -> lp));

        List<FiManagement> fiManagements = em.createQuery("select  bm from IN_BANK_MANAGEMENT bm where bm.disable=false and bm.fiPerson is not null and bm.fiPerson.person.deleted=false", FiManagement.class).getResultList();

        Map<Long, FiManagement> fiManagementMap = fiManagements.stream().collect(Collectors.toMap(FiManagement::getId, mm -> mm));

        List<PersonPosition> personPositionList = em.createQuery("select p from IN_PERSON_POSITIONS p where p.person.deleted=false and p.company is not null ", PersonPosition.class).getResultList();

        for (LegalPerson lp : allLegalPersons) {
            LegalPersonMetaModel lpModel = LegalPersonModelHelper.toModelSimple(lp, langId);

            if (lpModel.isBank()) {
                lpModel.setFiType(fiIdToTypeMap.get(lpModel.getFiId()));
            }

            result.putIfAbsent(lpModel, new HashSet<>());

            Pair<Set<CompanyDependencyModel>, Set<PersonMetaModel>> connections = calculateConnections(lp, fiManagements, langId, fiIdToTypeMap);
            result.get(lpModel).addAll(connections.getKey());

            List<Long> personIds = connections.getValue().stream().map(PersonMetaModel::getId).collect(Collectors.toList());

            if (!personIds.isEmpty()) {

                List<Long> fiIds = new ArrayList<>();
                for (Map.Entry<Long, FiManagement> entry : fiManagementMap.entrySet()) {
                    if (entry.getValue().getFiId() != lp.getFiId() && personIds.contains(entry.getValue().getFiPerson().getPerson().getId())) {
                        fiIds.add(entry.getValue().getFiId());
                    }
                }

                for (Long fiId : fiIds) {
                    LegalPerson legalPe = fiLegalPersonMap.get(fiId);
                    CompanyDependencyModel model = new CompanyDependencyModel(LegalPersonModelHelper.toModelSimple(legalPe, langId), ConnectedCOmpanyConnectionType.ASSOCIATED);
                    if (lpModel.getId() != legalPe.getId()) {
                        result.get(lpModel).add(model);
                    }

                }

                List<LegalPerson> positionLegalPersons = personPositionList.stream().filter(p -> personIds.contains(p.getPerson().getId())).map(PersonPosition::getCompany).collect(Collectors.toList());

                for (LegalPerson legalPerson : positionLegalPersons) {
                    CompanyDependencyModel model = new CompanyDependencyModel(LegalPersonModelHelper.toModelSimple(legalPerson, ThreadLocalHolder.getLanguage().getId()), ConnectedCOmpanyConnectionType.ASSOCIATED);
                    boolean contains = result.get(lpModel).contains(model);
                    if (contains) {
                        result.get(lpModel).stream().filter(d -> d.equals(model)).forEach(p -> p.setDependencyType(ConnectedCOmpanyConnectionType.BOTH));
                    } else {
                        if (lpModel.getId() != legalPerson.getId()) {
                            result.get(lpModel).add(model);
                        }
                    }
                }

            }

        }
        if (company != null) {

            return result.entrySet().stream().filter(f -> f.getKey().getId() == company.getId() || f.getValue().contains(new CompanyDependencyModel(LegalPersonModelHelper.toModelSimple(company, langId), ConnectedCOmpanyConnectionType.AFFILIATED))).map(e -> new ConnectedCompanyModel(e.getKey(), e.getValue())).collect(Collectors.toList());
        }
        return result.entrySet().stream().map(e -> new ConnectedCompanyModel(e.getKey(), e.getValue())).collect(Collectors.toList());

    }

    @Override
    public ConnectedCompanyConnection saveConnectionInfo(ConnectedCompanyConnection connection) throws FinATypeException {

        if (connection.getSource() == null || connection.getDestination() == null || connection.getConnectionType() == null) {
            throw new FinATypeException(FinATypeException.Type.INVALID_VALUE, new String[]{"Source,Destination and ConnectionType is required"});
        }

        if (connection.getId() <= 0) {
            List<ConnectedCompanyConnection> existedConnections = em.createQuery("select c from IN_CONNECTED_COMPANIES_INFO c where c.source.id=:sourceId and c.destination.id=:destinationId and c.connectionType=:type", ConnectedCompanyConnection.class).setParameter("type", connection.getConnectionType()).setParameter("sourceId", connection.getSource().getId()).setParameter("destinationId", connection.getDestination().getId()).getResultList();

            if (!existedConnections.isEmpty()) {
                throw new FinATypeException("Already Exists");
            }
        }

        connection.setSource(getLegalPersonById(connection.getSource().getId()));
        connection.setDestination(getLegalPersonById(connection.getDestination().getId()));

        if (connection.getId() > 0) {
            em.merge(connection);
        } else {
            em.persist(connection);
        }

        return connection;
    }

    @Override
    public ConnectedCompanyConnection getConnectionInfo(long sourceId, long destinationId, ConnectedCOmpanyConnectionType type) {

        List<ConnectedCompanyConnection> connections = em.createQuery("select c from IN_CONNECTED_COMPANIES_INFO c where c.source.id=:sourceId and c.destination.id=:destinationId and c.connectionType=:type", ConnectedCompanyConnection.class).setParameter("type", type).setParameter("sourceId", sourceId).setParameter("destinationId", destinationId).getResultList();

        if (connections.isEmpty()) {
            return null;
        }

        return connections.get(0);
    }

    @Override
    public LegalPerson activateDeletedLegalPerson(LegalPerson legalPerson, long fiId, List<Beneficiary> otherShares) throws FinATypeException {
        LegalPerson existing = em.createQuery("select l from IN_LEGAL_PERSONS l where trim(l.identificationNumber)=:idNumber and l.deleted=true", LegalPerson.class).setParameter("idNumber", legalPerson.getIdentificationNumber().trim()).getSingleResult();

        existing.setDeleted(false);
        em.merge(existing);
        legalPerson.setId(existing.getId());
        return update(legalPerson, fiId, otherShares);
    }

    @Override
    public List<LegalPerson> getLegalPersonDependencies(long legalPersonId) {
        Set<LegalPerson> result = new HashSet<>();
        LegalPerson person = getLegalPersonById(legalPersonId);

        List<Fi> fiConnections = em.createQuery("select b.fi from IN_BANK_LEGAL_PERSONS b where b.legalPerson.id=:personId", Fi.class).setParameter("personId", legalPersonId).getResultList();

        List<Beneficiary> beneficiaries = person.getBeneficiaries();
        List<Beneficiary> otherShares = loadLegalPersonOtherShares(legalPersonId);

        fiConnections.forEach(fc -> {
            LegalPerson lp = new LegalPerson();
            lp.setFiId(fc.getId());
            lp.setIdentificationNumber(fc.getIdentificationCode());
            lp.setName(fc.getDescription());
            result.add(lp);
        });

        result.addAll(beneficiaries.stream().filter(b -> b.getLegalPerson() != null).map(Beneficiary::getLegalPerson).collect(Collectors.toList()));
        result.addAll(otherShares.stream().filter(b -> b.getLegalPerson() != null).map(Beneficiary::getLegalPerson).collect(Collectors.toList()));

        return new ArrayList<>(result);
    }

    private Pair<Set<CompanyDependencyModel>, Set<PersonMetaModel>> calculateConnections(LegalPerson lp, List<FiManagement> fiManagements, long langId, Map<Long, FiTypeSimpleModel> fiIdToTypeMap) {
        Set<CompanyDependencyModel> connections = new HashSet<>();
        Set<PersonMetaModel> personConnections = new HashSet<>();

        Pair<Set<CompanyDependencyModel>, Set<PersonMetaModel>> pair = new ImmutablePair<>(connections, personConnections);
        for (Beneficiary beneficiary : lp.getBeneficiaries()) {
            if (beneficiary.getLegalPerson() != null) {
                LegalPersonMetaModel company = LegalPersonModelHelper.toModelSimple(beneficiary.getLegalPerson(), langId);
                company.setFiType(fiIdToTypeMap.get(company.getFiId()));
                if (company.getId() != lp.getId()) {
                    connections.add(new CompanyDependencyModel(company, ConnectedCOmpanyConnectionType.AFFILIATED));
                }
            } else {
                personConnections.add(PersonModelHelper.toModelSimple(beneficiary.getPhysicalPerson(), langId));
            }
        }

        if (lp.getFiId() > 0) {
            personConnections.addAll(fiManagements.stream().filter(m -> m.getFiId() == lp.getFiId()).map(m -> PersonModelHelper.toModelSimple(m.getFiPerson().getPerson(), langId)).collect(Collectors.toList()));
        } else {
            lp.getPositions().stream().filter(position -> position.getPerson() != null).forEach(position -> personConnections.add(PersonModelHelper.toModelSimple(position.getPerson(), langId)));
        }

        return pair;
    }

    private void checkProgrammaticallyDeleted(String identificationNumber) throws FinATypeException {
        if (identificationNumber == null || identificationNumber.trim().isEmpty()) {
            throw new FinATypeException("Identification Number Is Required");
        }
        Long count = em.createQuery("select count(l.id) from IN_LEGAL_PERSONS l where trim(l.identificationNumber)=:idNumber and l.deleted=true ", Long.class).setParameter("idNumber", identificationNumber.trim()).getSingleResult();

        if (count > 0) {
            throw new FinATypeException(FinATypeException.Type.ENTITY_PROGRAMMATICALLY_DELETED);
        }
    }

    private List<Predicate> getFiLegalPersonPredicates(CriteriaBuilder cb, Root<FiLegalPerson> root, Map<PersonFilter, Object> filter, long fiID) {

        List<Predicate> predicates = new ArrayList<>();
        long langId = ThreadLocalHolder.getLanguage().getId();
        predicates.add(cb.equal(root.get(FiLegalPerson_.fi).get(Fi_.id), fiID));

        if (filter != null) {
            for (Map.Entry<PersonFilter, Object> entry : filter.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }
                switch (entry.getKey()) {
                    case NAME:

                        List<Long> descriptionIds = em.createQuery("select lp.id from SYS_STRINGS s inner join IN_LEGAL_PERSONS lp on lp.name=s.id and s.langId =:langId and s.value like :value", Long.class)
                                .setParameter("langId", langId)
                                .setParameter("value", "%" + entry.getValue().toString() + "%")
                                .getResultList();

                        predicates.add(root.get(FiLegalPerson_.legalPerson).get(LegalPerson_.id).in(descriptionIds.isEmpty() ? Collections.singletonList(-1L) : descriptionIds));
                        break;
                    case RESIDENT_STATUS:
                        predicates.add(cb.equal(root.get(FiLegalPerson_.legalPerson).get(LegalPerson_.RESIDENT_STATUS), entry.getValue()));
                        break;
                    case STATUS:
                        predicates.add(cb.equal(root.get(FiLegalPerson_.legalPerson).get(LegalPerson_.STATUS), entry.getValue()));
                        break;
                    case ID_NUMBER:
                        predicates.add(cb.like(root.get(FiLegalPerson_.legalPerson).get(LegalPerson_.IDENTIFICATION_NUMBER), "%" + entry.getValue() + "%"));
                        break;
                    case EXCLUDE_DELETED:
                        predicates.add(cb.equal(root.get(FiLegalPerson_.legalPerson).get(LegalPerson_.deleted), false));
                        break;
                    case EXCLUDE_INACTIVE:
                        predicates.add(cb.equal(root.get(FiLegalPerson_.legalPerson).get(LegalPerson_.status), PersonStatus.ACTIVE));
                        break;
                    case COUNTRY_ID:
                        Join<FiLegalPerson, LegalPerson> lpJoin = root.join(FiLegalPerson_.legalPerson, JoinType.LEFT);
                        Join<LegalPerson, Region> regionJoin = lpJoin.join(LegalPerson_.country, JoinType.LEFT);

                        predicates.add(cb.equal(regionJoin.get(Region_.ID), entry.getValue()));
                        break;
                }
            }
        }
        return predicates;
    }

    private List<Predicate> getPredicates(CriteriaBuilder cb, Root<LegalPerson> root, Map<PersonFilter, Object> filter) {
        List<Predicate> predicates = new ArrayList<>();
        long langId = ThreadLocalHolder.getLanguage().getId();

        if (filter != null) {
            for (Map.Entry<PersonFilter, Object> entry : filter.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }
                switch (entry.getKey()) {
                    case NAME:
                        List<Long> descriptionIds = em.createQuery("select lp.id from SYS_STRINGS s inner join IN_LEGAL_PERSONS lp on lp.name=s.id and s.langId =:langId and s.value like :value", Long.class)
                                .setParameter("langId", langId)
                                .setParameter("value", "%" + entry.getValue().toString() + "%")
                                .getResultList();

                        predicates.add(root.get(LegalPerson_.id).in(descriptionIds.isEmpty() ? Collections.singletonList(-1L) : descriptionIds));
                        break;
                    case RESIDENT_STATUS:
                        predicates.add(cb.equal(root.get(LegalPerson_.RESIDENT_STATUS), entry.getValue()));
                        break;
                    case STATUS:
                        predicates.add(cb.equal(root.get(LegalPerson_.STATUS), entry.getValue()));
                        break;
                    case ID_NUMBER:
                        predicates.add(cb.like(root.get(LegalPerson_.IDENTIFICATION_NUMBER), "%" + entry.getValue() + "%"));
                        break;
                    case EXCLUDE_DELETED:
                        predicates.add(cb.equal(root.get(LegalPerson_.deleted), false));
                        break;
                    case EXCLUDE_INACTIVE:
                        predicates.add(cb.equal(root.get(LegalPerson_.status), PersonStatus.ACTIVE));
                        break;
                    case COUNTRY_ID:
                        Join<LegalPerson, Region> regionJoin = root.join(LegalPerson_.country, JoinType.LEFT);

                        predicates.add(cb.equal(regionJoin.get(Region_.ID), entry.getValue()));
                        break;
                }
            }
        }
        return predicates;
    }

}
