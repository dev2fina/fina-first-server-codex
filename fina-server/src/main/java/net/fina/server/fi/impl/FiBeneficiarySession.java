package net.fina.server.fi.impl;

import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import net.fina.common.client.exception.FinATypeException;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.fi.api.FiBeneficiaryLocal;
import net.fina.server.fi.api.FiLocal;
import net.fina.server.fi.entity.Fi;
import net.fina.server.fi.entity.FiPerson;
import net.fina.server.fi.entity.FiPersonConnectionType;
import net.fina.server.fi.util.FiBeneficiaryFilterType;
import net.fina.server.legalperson.api.LegalPersonLocal;
import net.fina.server.legalperson.entity.Beneficiary;
import net.fina.server.legalperson.entity.FiLegalPersonConnectionType;
import net.fina.server.legalperson.entity.FinalBeneficiary;
import net.fina.server.legalperson.entity.LegalPerson;
import net.fina.server.person.api.PersonLocal;
import net.fina.server.person.entity.Person;
import org.jboss.ejb3.annotation.SecurityDomain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
@Local(FiBeneficiaryLocal.class)
@SecurityDomain("FinASecurityDomain")
public class FiBeneficiarySession implements FiBeneficiaryLocal {

    @Inject
    private EntityManager em;

    @Inject
    private FiLocal fiLocal;

    @Inject
    private LegalPersonLocal legalPersonLocal;
    @Inject
    private PersonLocal personLocal;

    @Override
    public long count(long fiId) {
        return em.createQuery("select count(b.id) from IN_BENEFICIARIES b where b.legalPerson.fiId=:fiId", Long.class)
                .setParameter("fiId", fiId).getSingleResult();
    }

    @Override
    @SuppressWarnings("JpaQlInspection")

    public List<Beneficiary> load(long fiId, int offset, int limit, FiBeneficiaryFilterType filterType, String filterString) {
        long langId = ThreadLocalHolder.getLanguage().getId();
        filterType = filterType != null ? filterType : FiBeneficiaryFilterType.ALL;

        TypedQuery<Beneficiary> query = switch (filterType) {
            case ALL -> {
                if (filterString == null || filterString.trim().isEmpty()) {
                    yield em.createQuery(
                                    "select b from IN_LEGAL_PERSONS lp, IN(lp.beneficiaries) b " +
                                            "where lp.fiId = :fiId order by b.share desc", Beneficiary.class)
                            .setParameter("fiId", fiId);
                } else {
                    String queryString = "select b from IN_LEGAL_PERSONS lp " +
                            "join lp.beneficiaries b " +
                            "left join b.legalPerson l " +
                            "left join b.physicalPerson p " +
                            "left join SYS_STRINGS s on ((s.id = l.name or s.id = p.name) and s.langId=:langId) " +
                            "where lp.fiId = :fiId " +
                            "and (lower(s.value) like lower(:filterString) " +
                            "or lower(p.identificationNumber) like lower(:filterString) " +
                            "or lower(l.identificationNumber) like lower(:filterString)) " +
                            "order by b.share desc";

                    yield em.createQuery(queryString, Beneficiary.class)
                            .setParameter("fiId", fiId)
                            .setParameter("filterString", "%" + filterString + "%")
                            .setParameter("langId", langId);
                }
            }
            case PHYSICAL ->
                    em.createQuery("select b from IN_LEGAL_PERSONS lp,IN(lp.beneficiaries) b where lp.fiId=:fiId and b.legalPerson is null order by b.share desc ", Beneficiary.class)
                            .setParameter("fiId", fiId);
            case LEGAL ->
                    em.createQuery("select b from IN_LEGAL_PERSONS lp,IN(lp.beneficiaries) b where lp.fiId=:fiId and b.physicalPerson is null order by b.share desc ", Beneficiary.class)
                            .setParameter("fiId", fiId);
        };

        if (offset >= 0 && limit > 0) {
            query.setFirstResult(offset);
            query.setMaxResults(limit);
        }

        return query.getResultList();
    }


    @Override
    public Beneficiary create(Beneficiary beneficiary, long fiId) throws FinATypeException {
        if (fiId <= 0) {
            throw new FinATypeException("Fi Id Is Required!");
        }
        Fi fi = em.find(Fi.class, fiId);
        String fiCode = fi != null ? fi.getCode() : null;

        if (beneficiary.getLegalPerson() == null && beneficiary.getPhysicalPerson() == null) {
            throw new FinATypeException(constructErrorMessageWithFiCode("Legal Or Physical Person Not Present On Beneficiary", fiCode));
        }

        fiLocal.checkUserHasFiAccess(fiId);

        double totalShare = sumFiBeneficiaryShare(fiId);
        if (totalShare + beneficiary.getShare() > 100) {
            throw new FinATypeException(constructErrorMessageWithFiCode("Beneficiary share is more than 100%", fiCode));
        }

        if (beneficiary.getLegalPerson() != null && beneficiary.getLegalPerson().getFiId() == fiId){
            throw new FinATypeException("Fi cannot create a beneficiary for itself");
        }

        if (beneficiary.getPhysicalPerson() != null) {
            Person person = em.find(Person.class, beneficiary.getPhysicalPerson().getId());
            beneficiary.setPhysicalPerson(person);
            personLocal.createOrUpdateFiPersonRelation(fiId, person.getId(), FiPersonConnectionType.BENEFICIARY);
        }

        em.persist(beneficiary);
        LegalPerson legalPerson = legalPersonLocal.getLegalPersonByFiId(fiId);

        legalPerson.getBeneficiaries().add(beneficiary);

        if (beneficiary.getLegalPerson() != null) {
            legalPersonLocal.createFiLegalPersonRelation(beneficiary.getLegalPerson().getId(), fiId, FiLegalPersonConnectionType.BENEFICIARY);
            for (FinalBeneficiary finalBeneficiary : beneficiary.getFinalBeneficiaries()) {
                personLocal.createOrUpdateFiPersonRelation(fiId, finalBeneficiary.getPerson().getId(), FiPersonConnectionType.FINAL_BENEFICIARY);
            }
        }

        return beneficiary;
    }

    @Override
    public Beneficiary update(Beneficiary beneficiary, long fiId) throws FinATypeException {

        fiLocal.checkUserHasFiAccess(fiId);

        Beneficiary existed = em.find(Beneficiary.class, beneficiary.getId());
        double totalShare = sumFiBeneficiaryShare(fiId) - existed.getShare();

        if (totalShare + beneficiary.getShare() > 100) {
            throw new FinATypeException(FinATypeException.Type.INVALID_VALUE);
        }

        if (beneficiary.getLegalPerson() != null) {
            beneficiary.setLegalPerson(existed.getLegalPerson());
            for (FinalBeneficiary finalBeneficiary : beneficiary.getFinalBeneficiaries()) {
                personLocal.createOrUpdateFiPersonRelation(fiId, finalBeneficiary.getPerson().getId(), FiPersonConnectionType.FINAL_BENEFICIARY);
            }
        } else {
            beneficiary.setPhysicalPerson(existed.getPhysicalPerson());
        }


        return em.merge(beneficiary);
    }

    @Override
    public void delete(long fiId, long beneficiaryId) {
        Beneficiary beneficiary = em.find(Beneficiary.class, beneficiaryId);

        if (beneficiary.getLegalPerson() != null || beneficiary.getPhysicalPerson() != null) {
            LegalPerson lp = em.createQuery("select distinct lp from IN_LEGAL_PERSONS lp,IN(lp.beneficiaries) b where b.id=:beneficiaryId", LegalPerson.class)
                    .setParameter("beneficiaryId", beneficiaryId)
                    .getSingleResult();
            lp.getBeneficiaries().remove(beneficiary);

        }

        if (beneficiary.getPhysicalPerson() != null) {
            FiPerson fiPerson = personLocal.getFiPerson(fiId, beneficiary.getPhysicalPerson().getId());
            personLocal.removeFiPersonConnection(fiPerson, FiPersonConnectionType.BENEFICIARY);
        }

        if (beneficiary.getFinalBeneficiaries() != null) {
            //remove person fi connections
            for (FinalBeneficiary finalBeneficiary : beneficiary.getFinalBeneficiaries()) {
                if (finalBeneficiary.getPerson() != null && finalBeneficiary.getPerson().getId() > 0) {
                    FiPerson fiPerson = personLocal.getFiPerson(fiId, finalBeneficiary.getPerson().getId());
                    personLocal.removeFiPersonConnection(fiPerson, FiPersonConnectionType.FINAL_BENEFICIARY);
                }

            }
        }

        em.remove(beneficiary);
    }


    @Override
    public Map<FiBeneficiaryFilterType, Long> countByType(long fiId) {
        Map<FiBeneficiaryFilterType, Long> result = new HashMap<>();
        for (FiBeneficiaryFilterType type : FiBeneficiaryFilterType.values()) {
            long count = 0;
            switch (type) {
                case ALL:
                    break;
                case PHYSICAL:
                    count = em.createQuery("select count(b.id) from IN_LEGAL_PERSONS lp,IN(lp.beneficiaries) b where lp.fiId=:fiId and b.legalPerson is null", Long.class)
                            .setParameter("fiId", fiId).getSingleResult();
                    break;
                case LEGAL:
                    count = em.createQuery("select count(b.id) from IN_LEGAL_PERSONS lp,IN(lp.beneficiaries) b where lp.fiId=:fiId and b.physicalPerson is null", Long.class)
                            .setParameter("fiId", fiId).getSingleResult();
                    break;
            }
            result.put(type, count);
        }
        result.put(FiBeneficiaryFilterType.ALL, result.get(FiBeneficiaryFilterType.PHYSICAL) + result.get(FiBeneficiaryFilterType.LEGAL));

        return result;
    }

    @Override
    public Beneficiary getById(long beneficiaryId) throws FinATypeException {
        return em.find(Beneficiary.class, beneficiaryId);
    }


    private double sumFiBeneficiaryShare(long fiId) {
        Double sum = em.createQuery("select sum(b.share) from IN_LEGAL_PERSONS  lp,IN(lp.beneficiaries) b where lp.fiId=:fiId", Double.class)
                .setParameter("fiId", fiId)
                .getSingleResult();
        return sum == null ? 0 : sum;
    }

    private String constructErrorMessageWithFiCode(String mainText, String fiCode) {
        StringBuilder sb = new StringBuilder(mainText);

        if (fiCode != null) {
            sb.append(", FI: ").append(fiCode);
        }
        return sb.toString();
    }

}
