package net.fina.server.person.impl;

import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import net.fina.common.client.exception.FinATypeException;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.fi.api.FiLocal;
import net.fina.server.fi.entity.*;
import net.fina.server.interceptors.RecordingAuditor;
import net.fina.server.legalperson.entity.Beneficiary;
import net.fina.server.legalperson.entity.LegalPerson;
import net.fina.server.legalperson.model.PersonFilter;
import net.fina.server.person.api.PersonLocal;
import net.fina.server.person.entity.*;
import net.fina.server.person.model.PersonStatus;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.logging.Logger;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("JpaQlInspection")
@Stateless
@Local(PersonLocal.class)
@SecurityDomain("FinASecurityDomain")
@Interceptors(RecordingAuditor.class)
public class PersonSession implements PersonLocal {
    private final Logger log = Logger.getLogger(getClass().getName());
    @Inject
    private EntityManager em;

    @Inject
    private FiLocal fiLocal;

    @Override
    public List<Person> loadAllPersonSimple(Map<PersonFilter, Object> filter, long langId) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Person> cq = builder.createQuery(Person.class);
        Root<Person> root = cq.from(Person.class);
        cq.multiselect(root.get(Person_.id), root.get(Person_.NAME), root.get(Person_.IDENTIFICATION_NUMBER), root.get(Person_.PASSPORT_NUMBER), root.get(Person_.RESIDENT_STATUS), root.get(Person_.COUNTRY), root.get(Person_.STATUS));

        List<Predicate> predicates = getPredicates(builder, root, filter);
        cq.where(predicates.toArray(new Predicate[0]));
        cq.orderBy(builder.desc(root.get(Person_.id)));
        TypedQuery<Person> query = em.createQuery(cq);

        int offset = (int) filter.getOrDefault(PersonFilter.OFFSET, -1);
        int limit = (int) filter.getOrDefault(PersonFilter.LIMIT, -1);

        if (offset >= 0) {
            query.setFirstResult(offset);
        }
        if (limit > 0) {
            query.setMaxResults(limit);
        }

        return query.getResultList();


   /*     StringBuilder sb = new StringBuilder("select new net.fina.server.person.entity.Person(p.id,p.name,p.identificationNumber,p.passportNumber,p.residentStatus,r,p.status) from IN_PERSONS p left join p.country r where p.deleted=false ");

        List<Long> personIds = new ArrayList<>();

        if (filterParam != null && !filterParam.trim().isEmpty()) {
            personIds = getFilteredPersonIds(langId, filterParam);
            sb.append(" and p.identificationNumber like :identificationNumber or p.id in (:personIds) ");
        }

        sb.append(" order by p.id desc ");

        TypedQuery<Person> query = em.createQuery(sb.toString(), Person.class);

        if (filterParam != null && !filterParam.trim().isEmpty()) {
            query.setParameter("identificationNumber", "%" + filterParam + "%");
            query.setParameter("personIds", personIds.isEmpty() ? Collections.singleton(-1L) : personIds);
        }

        if (start >= 0 && limit > 0) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }*/

//        return query.getResultList();
    }


    private List<Predicate> getPredicates(CriteriaBuilder cb, Root<Person> root, Map<PersonFilter, Object> filter) {
        List<Predicate> predicates = new ArrayList<>();
        long langId = ThreadLocalHolder.getLanguage().getId();

        if (filter != null) {
            for (Map.Entry<PersonFilter, Object> entry : filter.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }
                switch (entry.getKey()) {
                    case NAME:
                        List<Long> descriptionIds = em.createQuery("select p.id from SYS_STRINGS s inner join IN_PERSONS p on p.name=s.id and s.langId =:langId and s.value like :value", Long.class)
                                .setParameter("langId", langId)
                                .setParameter("value", "%" + entry.getValue().toString() + "%")
                                .getResultList();

                        predicates.add(root.get(Person_.id).in(descriptionIds.isEmpty() ? Collections.singletonList(-1L) : descriptionIds));

                        break;
                    case RESIDENT_STATUS:
                        predicates.add(cb.equal(root.get(Person_.RESIDENT_STATUS), entry.getValue()));
                        break;
                    case STATUS:
                        predicates.add(cb.equal(root.get(Person_.STATUS), entry.getValue()));
                        break;
                    case ID_NUMBER:
                        predicates.add(cb.like(root.get(Person_.IDENTIFICATION_NUMBER), "%" + entry.getValue() + "%"));
                        break;
                    case EXCLUDE_DELETED:
                        predicates.add(cb.equal(root.get(Person_.deleted), false));
                        break;
                    case EXCLUDE_INACTIVE:
                        predicates.add(cb.equal(root.get(Person_.status), PersonStatus.ACTIVE));
                        break;
                    case COUNTRY_ID:
                        Join<Person, Region> regionJoin = root.join(Person_.country, JoinType.INNER);

                        predicates.add(cb.equal(regionJoin.get(Region_.ID), entry.getValue()));
                        break;
                }
            }
        }
        return predicates;
    }

    @Override
    public long countAll(long langId, Map<PersonFilter, Object> filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Person> root = query.from(Person.class);

        query.select(cb.countDistinct(root.get(Person_.id)));

        List<Predicate> predicates = getPredicates(cb, root, filter);

        query.where(predicates.toArray(new Predicate[0]));

        TypedQuery<Long> loadQuery = em.createQuery(query);

        return loadQuery.getSingleResult();
//
//        StringBuilder sb = new StringBuilder("select count(p.id) from IN_PERSONS p where p.deleted=false ");
//
//        List<Long> personIds = new ArrayList<>();
//
//        if (filterParam != null && !filterParam.trim().isEmpty()) {
//            personIds = getFilteredPersonIds(langId, filterParam);
//            sb.append(" and p.identificationNumber like :identificationNumber or p.id in (:personIds) ");
//        }
//
//        TypedQuery<Long> query = em.createQuery(sb.toString(), Long.class);
//
//        if (filterParam != null && !filterParam.trim().isEmpty()) {
//            query.setParameter("identificationNumber", "%" + filterParam + "%");
//            query.setParameter("personIds", personIds.isEmpty() ? Collections.singleton(-1L) : personIds);
//        }
//
//        return query.getSingleResult();
    }

    @Override
    public List<Person> loadFiPersonsSimple(int start, int limit, long fiId, long langId, String filterParam) throws FinATypeException {
        fiLocal.checkUserHasFiAccess(fiId);

        StringBuilder sb = new StringBuilder("select bp from IN_BANK_PERSONS bp where bp.fi.id=:fiId ");

        List<Long> personIds = new ArrayList<>();

        if (filterParam != null && !filterParam.trim().isEmpty()) {
            personIds = getFilteredPersonIds(langId, filterParam);

            sb.append(" and (bp.person.identificationNumber like :identificationNumber or bp.person.id in (:personIds))");
        }

        sb.append(" order by bp.id desc ");

        TypedQuery<FiPerson> query = em.createQuery(sb.toString(), FiPerson.class).
                setParameter("fiId", fiId);

        if (filterParam != null && !filterParam.trim().isEmpty()) {
            query.setParameter("identificationNumber", "%" + filterParam + "%");
            query.setParameter("personIds", personIds.isEmpty() ? Collections.singleton(-1L) : personIds);
        }

        if (start >= 0 && limit > 0) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }

        return query.getResultList().stream().map(bp -> {
            Person p = bp.getPerson();
            p.setFiPersonId(bp.getId());
            p.setFiPersonConnections(bp.getConnections());
            return p;
        }).collect(Collectors.toList());
    }

    @Override
    public long countFiPersons(long fiId, long langId, String filterParam) {

        StringBuilder sb = new StringBuilder("select count(bp.person) from IN_BANK_PERSONS bp where bp.fi.id=:fiId ");

        List<Long> personIds = new ArrayList<>();

        if (filterParam != null && !filterParam.trim().isEmpty()) {
            personIds = getFilteredPersonIds(langId, filterParam);
            sb.append(" and (bp.person.identificationNumber like :identificationNumber or bp.person.id in (:personIds)) ");
        }

        TypedQuery<Long> query = em.createQuery(sb.toString(), Long.class);
        query.setParameter("fiId", fiId);

        if (filterParam != null && !filterParam.trim().isEmpty()) {
            query.setParameter("identificationNumber", "%" + filterParam + "%");
            query.setParameter("personIds", personIds.isEmpty() ? Collections.singleton(-1L) : personIds);
        }

        return query.getSingleResult();
    }

    @Override
    public Person createPerson(Person person, long fiId, List<Beneficiary> beneficiaries) throws FinATypeException {
        //check programmatically deleted
        checkProgrammaticallyDeleted(person.getIdentificationNumber());

        fiLocal.checkUserHasFiAccess(fiId);
        //check unique & invalid identification number
        validateAndCheckUnique(person);

        if (person.getCitizenship() != null) {
            Region region = em.find(Region.class, person.getCitizenship().getId());
            person.setCitizenship(region);
        }

        person = em.merge(person);

        //save person fi relation
        if (fiId > 0) {
            FiPerson fiPerson = createOrUpdateFiPersonRelation(fiId, person.getId(), null);
            person.setFiPersonId(fiPerson.getId());
        }

        manageOtherShares(beneficiaries, person);

        return person;
    }

    @Override
    public Person updatePerson(Person person, long fiId, List<Beneficiary> beneficiaries) throws FinATypeException {
        fiLocal.checkUserHasFiAccess(fiId);

        Person existing = em.find(Person.class, person.getId());

        person.setFiPersonConnections(existing.getFiPersonConnections());

        if (!existing.getIdentificationNumber().equals(person.getIdentificationNumber()) && existing.getCountry().equals(person.getCitizenship())) {
            validateAndCheckUnique(person);
        }

        if (person.getCitizenship() != null) {
            Region region = em.find(Region.class, person.getCitizenship().getId());
            person.setCitizenship(region);
        }


        for (PersonEducation education : existing.getEducation()) {
            if (!person.getEducation().contains(education)) {
                em.remove(education);
            }
        }


        for (PersonPosition position : existing.getPositions()) {
            if (!person.getPositions().contains(position)) {
                em.remove(position);
            }
        }

        for (CriminalRecord criminalRecord : existing.getCriminalRecords()) {
            if (!person.getCriminalRecords().contains(criminalRecord)) {
                em.remove(criminalRecord);
            }
        }


        for (Recommendation recommendation : existing.getRecommendations()) {
            if (!person.getRecommendations().contains(recommendation)) {
                em.remove(recommendation);
            }
        }


        manageOtherShares(beneficiaries, person);

        List<PersonPosition> positions = new ArrayList<>();
        for (PersonPosition position : person.getPositions()) {
            position.setPerson(existing);
            positions.add(em.merge(position));
        }

        person.setPositions(positions);

        return em.merge(person);

    }

    @Override
    public Person getBankPersonInfo(long fiId, long fiPersonId) throws FinATypeException {
        fiLocal.checkUserHasFiAccess(fiId);
        return em.createQuery("select bp.person from IN_BANK_PERSONS bp where bp.id=:fiPersonId", Person.class)
                .setParameter("fiPersonId", fiPersonId)
                .getSingleResult();
    }

    @Override
    public Person getPersonInfo(long personId) {
        return em.find(Person.class, personId);
    }

    @Override
    public void delete(List<Long> personIds) throws FinATypeException {
        List<FiPerson> fiPersons = em.createQuery("select bp from IN_BANK_PERSONS bp where bp.person.id in(:personIds)", FiPerson.class)
                .setParameter("personIds", personIds)
                .getResultList();

        List<Long> beneficiaries = em.createQuery("select b.id from IN_BENEFICIARIES b where b.physicalPerson.id in(:personIds)", Long.class)
                .setParameter("personIds", personIds)
                .getResultList();

        if (fiPersons.isEmpty() && beneficiaries.isEmpty()) {
            em.createQuery("update IN_PERSONS set deleted=true where id in(:personIds)")
                    .setParameter("personIds", personIds)
                    .executeUpdate();
        } else {
            throw new FinATypeException(FinATypeException.Type.DEPENDENCY_ERROR);
        }
    }

    @Override
    public void deleteFiPerson(long fiPersonId, long fiId) throws FinATypeException {
        fiLocal.checkUserHasFiAccess(fiId);
        FiPerson fiPerson = em.find(FiPerson.class, fiPersonId);

        //Branch dependency
        Long branchCount = em.createQuery("select count(bp.id) from IN_BANK_BRANCHES bp where bp.manager.id=:fiPersonId or bp.chiefAccountant.id=:fiPersonId", Long.class)
                .setParameter("fiPersonId", fiPersonId)
                .getSingleResult();

        if (branchCount > 0) {
            throw new FinATypeException(FinATypeException.Type.DEPENDENCY_ERROR, new String[]{"Has Branch Dependencies"});
        }

        long managerCount = em.createQuery("select count(bm.id) from IN_BANK_MANAGEMENT bm where bm.fiPerson.id=:fiPersonId", Long.class)
                .setParameter("fiPersonId", fiPersonId)
                .getSingleResult();

        if (managerCount > 0) {
            throw new FinATypeException(FinATypeException.Type.DEPENDENCY_ERROR, new String[]{"Has Management Dependencies"});
        }


        long beneficiaryCount = em.createQuery("select count(bt.id) from IN_LEGAL_PERSONS lp, in(lp.beneficiaries) bt where bt.physicalPerson.id=:personId and lp.fiId=:fiId ", Long.class)
                .setParameter("personId", fiPerson.getPerson().getId())
                .setParameter("fiId", fiId)
                .getSingleResult();

        if (beneficiaryCount > 0) {
            throw new FinATypeException(FinATypeException.Type.DEPENDENCY_ERROR, new String[]{"Has Beneficiaries Dependencies"});
        }

        em.remove(fiPerson);

    }

    @Override
    public List<PersonPosition> getBankPersonPositions(long fiPersonId) {
        FiPerson fiPerson = em.find(FiPerson.class, fiPersonId);
        return em.createQuery("select p from IN_PERSON_POSITIONS p where p.person.id=:personId", PersonPosition.class)
                .setParameter("personId", fiPerson.getPerson().getId())
                .getResultList();

    }

    @Override
    public List<PersonPosition> getPersonPositions(long personId) {
        return em.createQuery("select p from IN_PERSON_POSITIONS p where p.person.id=:personId", PersonPosition.class)
                .setParameter("personId", personId)
                .getResultList();
    }

    @Override
    public List<Fi> getPersonConnectedFis(long personId) {
        return em.createQuery("select distinct new net.fina.server.fi.entity.Fi(bp.fi.id,bp.fi.code,bp.fi.description) from IN_BANK_PERSONS bp where bp.person.id=:personId", Fi.class)
                .setParameter("personId", personId)
                .getResultList();
    }

    @Override
    public void deleteFiPersons(List<Long> fiPersonIds, long fiId) throws FinATypeException {
        if (!fiPersonIds.isEmpty()) {
            //filter
            List<Long> filteredIds = em.createQuery("select bp.id from IN_BANK_PERSONS bp where bp.fi.id=:bankId and bp.id in(:personIds)", Long.class)
                    .setParameter("bankId", fiId)
                    .setParameter("personIds", fiPersonIds)
                    .getResultList();

            if (!Objects.equals(fiPersonIds, filteredIds)) {
                throw new FinATypeException(FinATypeException.Type.INVALID_PERMISSIONS);
            }

            for (long id : fiPersonIds) {
                deleteFiPerson(id, fiId);
            }
        }

    }

    @Override
    public List<FiPerson> getFiPersons(long personId) {
        return em.createQuery("select bp from IN_BANK_PERSONS bp where bp.person.id=:personId", FiPerson.class)
                .setParameter("personId", personId)
                .getResultList();
    }

    @Override
    public List<FiBranch> getFiPersonBranches(long fiId, long personId) {
        return em.createQuery("select b from IN_BANK_BRANCHES b left join b.manager bm left join b.chiefAccountant bc where b.bankId=:fiId and b.deleted=false and (bm.person.id=:personId or bc.person.id=:personId)", FiBranch.class).setParameter("fiId", fiId)
                .setParameter("personId", personId)
                .getResultList();
    }

    @Override
    public List<FiManagement> getFiPersonManagement(long fiId, long personId) {
        return em.createQuery("select bm from IN_BANK_MANAGEMENT bm where bm.fiId=:fiId and bm.fiPerson.person.id=:personId", FiManagement.class)
                .setParameter("fiId", fiId)
                .setParameter("personId", personId)
                .getResultList();
    }

    @Override
    public List<Beneficiary> getFiBeneficiaries(long fiId, long personId) {
        return em.createQuery("select b from IN_LEGAL_PERSONS lp,IN(lp.beneficiaries) b where b.physicalPerson.id=:personId and lp.fiId=:fiId", Beneficiary.class)
                .setParameter("personId", personId)
                .setParameter("fiId", fiId)
                .getResultList();
    }

    @Override
    public FiPerson createOrUpdateFiPersonRelation(long fiId, long personId, FiPersonConnectionType connectionType) {

        FiPerson fiPerson = getFiPerson(fiId, personId);
        if (fiPerson == null) {
            fiPerson = new FiPerson();
            fiPerson.setFi(fiLocal.getFiById(fiId));
            fiPerson.setPerson(getPersonInfo(personId));

            if (connectionType != null) {
                List<FiPersonConnection> conn = new ArrayList<>();
                conn.add(new FiPersonConnection(connectionType));
                fiPerson.setConnections(conn);
            }

            em.persist(fiPerson);
        } else if (connectionType != null) {
            fiPerson.getConnections().add(new FiPersonConnection(connectionType));
            em.merge(fiPerson);
        }

        return fiPerson;
    }

    @Override
    public List<Beneficiary> getPersonOtherShares(long personId) {
        return em.createQuery("select new net.fina.server.legalperson.entity.Beneficiary(b.id,b.share,b.creationDate,lp.id,lp.name,lp.identificationNumber,lp.fiId) " +
                        "from IN_LEGAL_PERSONS lp,IN(lp.beneficiaries) b where b.physicalPerson.id=:personId", Beneficiary.class)
                .setParameter("personId", personId)
                .getResultList();
    }

    @Override
    public FiPerson getFiPerson(long fiId, long personId) {
        List<FiPerson> fiPersonList = em.createQuery("select fp from IN_BANK_PERSONS fp where fp.person.id=:personInd and fp.fi.id=:fiId", FiPerson.class)
                .setParameter("fiId", fiId)
                .setParameter("personInd", personId)
                .getResultList();

        return fiPersonList.isEmpty() ? null : fiPersonList.get(0);
    }

    @Override
    public void removeFiPersonConnection(FiPerson fiPerson, FiPersonConnectionType type) {
        fiPerson = em.find(FiPerson.class, fiPerson.getId());
        Optional<FiPersonConnection> connection = fiPerson.getConnections().stream().filter(c -> c.getConnectionType() == type).findFirst();
        if (connection.isPresent()) {
            FiPersonConnection connectionType = connection.get();
            fiPerson.getConnections().remove(connectionType);
            em.remove(em.find(FiPersonConnection.class, connectionType.getId()));
        }

        em.merge(fiPerson);
    }

    @Override
    public Person activateDeletedPerson(Person person, long fiId, List<Beneficiary> beneficiaries) throws FinATypeException {
        Person existing = em.createQuery("select p from IN_PERSONS p where trim(p.identificationNumber)=:idNumber and p.deleted=true", Person.class)
                .setParameter("idNumber", person.getIdentificationNumber().trim())
                .getSingleResult();

        existing.setDeleted(false);
        em.merge(existing);
        person.setId(existing.getId());
        return updatePerson(person, fiId, beneficiaries);
    }

    @Override
    public Person findByIdentificationCodeAndRegion(String identificationCode, long regionId) {
        try {
            return em.createQuery("select p from IN_PERSONS p where trim(p.identificationNumber)=:idNumber and p.country.id=:countryId", Person.class)
                    .setParameter("idNumber", identificationCode.trim())
                    .setParameter("countryId", regionId)
                    .getSingleResult();
        } catch (Throwable t) {
            log.warn(t.getMessage());
        }
        return null;
    }

    private void validateAndCheckUnique(Person person) throws FinATypeException {
        if (person.getIdentificationNumber() == null || person.getIdentificationNumber().trim().isEmpty()) {
            throw new FinATypeException(FinATypeException.Type.CODE_UNIQUE, new String[]{"Identification Number Is Required"});
        } else if (person.getCitizenship() == null || person.getCitizenship().getId() == 0) {
            throw new FinATypeException("Citizenship Is Required!");
        }


        List<Person> persons = em.createQuery("select p from IN_PERSONS p where p.identificationNumber=:idNumber and p.country.id=:country and p.id<>:personId", Person.class)
                .setParameter("idNumber", person.getIdentificationNumber())
                .setParameter("personId", person.getId())
                .setParameter("country", person.getCountry().getId())
                .getResultList();
        if (!persons.isEmpty()) {
            throw new FinATypeException("Identification Number And Citizenship Is Not Unique");
        }
    }


    private List<Long> getFilteredPersonIds(long langId, String filterParam) {
        return em.createQuery("select p.id from SYS_STRINGS s inner join IN_PERSONS p on p.name=s.id and s.langId =:langId and s.value like :value", Long.class)
                .setParameter("langId", langId)
                .setParameter("value", "%" + filterParam + "%")
                .getResultList();

    }

    private void manageOtherShares(List<Beneficiary> otherShares, Person person) throws FinATypeException {

        List<Beneficiary> personBeneficiaries = em.createQuery("select b from IN_BENEFICIARIES b where b.physicalPerson.id=:personId", Beneficiary.class).setParameter("personId", person.getId()).getResultList();

        List<Long> ids = new ArrayList<>();

        //delete beneficiaries
        personBeneficiaries.stream().filter(pb -> !otherShares.contains(pb)).forEach(b -> ids.add(b.getId()));
        if (!ids.isEmpty()) {
            em.createNativeQuery("delete from IN_LEGAL_PERSON_BENEFICIARIES_TABLE where BENEFICIARY_ID in (:ids)").setParameter("ids", ids).executeUpdate();
            em.createQuery("delete from IN_BENEFICIARIES where id in (:ids)").setParameter("ids", ids).executeUpdate();
        }

        LegalPerson lp;
        if (otherShares != null) {
            for (Beneficiary share : otherShares) {

                share.setActive(true);
                lp = em.find(LegalPerson.class, share.getLegalPerson().getId());
                share.setPhysicalPerson(person);
                share.setLegalPerson(null);

                if (share.getShare() <= 0) {
                    throw new FinATypeException(FinATypeException.Type.INVALID_VALUE);
                }

                Beneficiary finalShare = share;
                if (lp.getBeneficiaries().stream().filter(b -> b.getId() != finalShare.getId()).mapToDouble(Beneficiary::getShare).sum() + share.getShare() > 100) {
                    throw new FinATypeException(FinATypeException.Type.INVALID_VALUE);
                }

                share = em.merge(share);

                if (lp.getFiId() > 0) {
                    createOrUpdateFiPersonRelation(lp.getFiId(), person.getId(), FiPersonConnectionType.BENEFICIARY);
                }

                if (!lp.getBeneficiaries().contains(share)) {
                    lp.getBeneficiaries().add(share);
                }
            }
        }


    }

    private void checkProgrammaticallyDeleted(String identificationNumber) throws FinATypeException {
        if (identificationNumber == null || identificationNumber.trim().isEmpty()) {
            throw new FinATypeException("Identification Number Is Required");
        }

        Long count = em.createQuery("select count(p.id) from IN_PERSONS p where trim(p.identificationNumber)=:idNumber and p.deleted=true ", Long.class).setParameter("idNumber", identificationNumber.trim()).getSingleResult();
        if (count > 0) {
            throw new FinATypeException(FinATypeException.Type.ENTITY_PROGRAMMATICALLY_DELETED);
        }
    }

}
