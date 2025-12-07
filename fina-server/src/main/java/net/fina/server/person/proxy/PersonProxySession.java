package net.fina.server.person.proxy;

import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.fis.FiModel;
import net.fina.common.server.util.PagingUtil;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.fi.entity.Fi;
import net.fina.server.fi.entity.FiBranch;
import net.fina.server.fi.entity.FiManagement;
import net.fina.server.fi.entity.FiPersonConnectionType;
import net.fina.server.legalperson.api.LegalPersonLocal;
import net.fina.server.legalperson.entity.Beneficiary;
import net.fina.server.legalperson.model.BeneficiaryMetaModel;
import net.fina.server.legalperson.model.LegalPersonMetaModel;
import net.fina.server.legalperson.model.PersonFilter;
import net.fina.server.legalperson.model.helper.BeneficiaryModelHelper;
import net.fina.server.legalperson.model.helper.LegalPersonModelHelper;
import net.fina.server.person.api.PersonLocal;
import net.fina.server.person.entity.Person;
import net.fina.server.person.model.PersonMetaModel;
import net.fina.server.person.model.PersonPositionMetaModel;
import net.fina.server.person.model.PersonStatus;
import net.fina.server.person.model.ShareMetaModel;
import net.fina.server.person.model.connection.BranchPersonConnectionModel;
import net.fina.server.person.model.connection.FilterConnectionType;
import net.fina.server.person.model.connection.PersonConnectionType;
import net.fina.server.person.model.connection.PersonConnectionsModel;
import net.fina.server.person.model.helper.PersonModelHelper;
import org.jboss.ejb3.annotation.SecurityDomain;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Stateless
@SecurityDomain("FinASecurityDomain")
public class PersonProxySession {

    @Inject
    private PersonLocal personLocal;
    @Inject
    private LegalPersonLocal legalPersonLocal;

    public PaginatedListWrapper<PersonMetaModel> loadAllPerson(Map<PersonFilter, Object> filter) {
        PaginatedListWrapper<PersonMetaModel> result = new PaginatedListWrapper<>();
        long langId = ThreadLocalHolder.getLanguage().getId();

        long count = personLocal.countAll(langId, filter);
        List<Person> personList = personLocal.loadAllPersonSimple(filter, langId);
        List<PersonMetaModel> models = PersonModelHelper.toModels(personList, langId);

        models.forEach(m -> m.setConnectedFis(personLocal.getPersonConnectedFis(m.getId()).stream().map(p -> new FiModel(p.getId(), p.getCode(), p.getDescription().getDescription(langId))).collect(Collectors.toList())));

        result.setList(models);
        result.setPageSize((Integer) filter.getOrDefault(PersonFilter.LIMIT,0));
        result.setCurrentPage((Integer) filter.getOrDefault(PersonFilter.OFFSET,0));
        result.setTotalResults(count);

        return result;
    }

    public PaginatedListWrapper<PersonMetaModel> loadFiPersonsSimple(long fiId, int page, int limit, String filter) throws FinATypeException {
        PaginatedListWrapper<PersonMetaModel> result = new PaginatedListWrapper<>();
        long langId = ThreadLocalHolder.getLanguage().getId();

        int offset = PagingUtil.getOffsetFromPage(page, limit);

        long count = personLocal.countFiPersons(fiId, langId, filter);

        List<Person> personList = personLocal.loadFiPersonsSimple(offset, limit, fiId, langId, filter);
        result.setList(PersonModelHelper.toModels(personList, langId));
        result.setPageSize(limit);
        result.setTotalResults(count);

        return result;
    }

    public PersonMetaModel createPerson(PersonMetaModel personMetaModel, long fiId) throws FinATypeException {
        return createOrRestoreDeletedPerson(personMetaModel, fiId, false);
    }

    public PersonMetaModel updatePerson(PersonMetaModel personMetaModel, long fiId) throws FinATypeException {
        long langId = ThreadLocalHolder.getLanguage().getId();
        List<Beneficiary> beneficiaries = personMetaModel.getShares().stream().map(Beneficiary::new).collect(Collectors.toList());

        Person person = personLocal.updatePerson(PersonModelHelper.toEntity(personMetaModel, langId), fiId, beneficiaries);
        List<ShareMetaModel> personShares = personLocal.getPersonOtherShares(person.getId()).stream().map(share -> new ShareMetaModel(share.getId(), share.getShare(), share.getCreationDate(), LegalPersonModelHelper.toModel(share.getLegalPerson(), langId))).collect(Collectors.toList());

        PersonMetaModel result = PersonModelHelper.toModel(person, langId);
        result.setShares(personShares);
        return result;
    }

    public PersonMetaModel getBankPersonInfo(long fiId, long fiPersonId) throws FinATypeException {
        long langId = ThreadLocalHolder.getLanguage().getId();
        PersonMetaModel model = PersonModelHelper.toModel(personLocal.getBankPersonInfo(fiId, fiPersonId), langId);
        model.setFiPersonId(fiPersonId);

        List<ShareMetaModel> personShares = personLocal.getPersonOtherShares(model.getId()).stream().map(share -> new ShareMetaModel(share.getId(), share.getShare(), share.getCreationDate(), LegalPersonModelHelper.toModel(share.getLegalPerson(), langId))).collect(Collectors.toList());
        model.setShares(personShares);

        return model;
    }

    public PersonMetaModel getPersonInfo(long personId, boolean active, List<PersonConnectionType> organizationTypes, List<FilterConnectionType> pages) {
        long langId = ThreadLocalHolder.getLanguage().getId();
        PersonMetaModel personModel = PersonModelHelper.toModel(personLocal.getPersonInfo(personId), langId);
        List<ShareMetaModel> personShares = personLocal.getPersonOtherShares(personId).stream().map(share -> new ShareMetaModel(share.getId(), share.getShare(), share.getCreationDate(), LegalPersonModelHelper.toModel(share.getLegalPerson(), langId))).collect(Collectors.toList());
        personModel.setShares(personShares);

        List<Fi> connectedFis = personLocal.getPersonConnectedFis(personId);
        List<PersonConnectionsModel> connectionsModels = new ArrayList<>();

        for (Fi fi : connectedFis) {
            //load branches
            List<FiBranch> branches = personLocal.getFiPersonBranches(fi.getId(), personId);
            List<BranchPersonConnectionModel> branchPositions = new ArrayList<>();
            branches.stream().filter(b -> b.isDisable() == !active).forEach(b -> {
                List<FiPersonConnectionType> branchPersonPositions = new ArrayList<>();
                if (b.getChiefAccountant() != null && b.getChiefAccountant().getPerson().getId() == personId) {
                    branchPersonPositions.add(FiPersonConnectionType.BRANCH_CHIEF_ACCOUNTANT);
                }
                if (b.getManager() != null && b.getManager().getPerson().getId() == personId) {
                    branchPersonPositions.add(FiPersonConnectionType.MANAGER);
                }
                branchPositions.add(new BranchPersonConnectionModel(b.getCode(), b.getName().getDescription(langId), branchPersonPositions));
            });
            List<String> managements = personLocal.getFiPersonManagement(fi.getId(), personId).stream().filter(m -> personModel.getStatus().equals(active ? PersonStatus.ACTIVE : PersonStatus.INACTIVE)).map(FiManagement::getPosition).collect(Collectors.toList());
            List<BeneficiaryMetaModel> fiBeneficiaries = personLocal.getFiBeneficiaries(fi.getId(), personId).stream().filter(b -> b.isActive() == active).map(fb -> BeneficiaryModelHelper.toModel(fb, langId)).collect(Collectors.toList());

            if (!branches.isEmpty() || !managements.isEmpty() || !fiBeneficiaries.isEmpty()) {
                connectionsModels.add(new PersonConnectionsModel(fi.getDescription().getDescription(langId), PersonConnectionType.FI, branchPositions, managements, fiBeneficiaries));
            }
        }


        List<PersonPositionMetaModel> personPositionsInLegalPersons = personModel.getPositions();

        Set<LegalPersonMetaModel> legalPersonMetaModels = new HashSet<>();

        Map<LegalPersonMetaModel, List<PersonPositionMetaModel>> positionMap = new HashMap<>();

        personPositionsInLegalPersons.forEach(pp -> {
            if (pp.getCompany() != null && pp.getCompany().getFiId() <= 0) {
                legalPersonMetaModels.add(pp.getCompany());

                positionMap.putIfAbsent(pp.getCompany(), new ArrayList<>());
                positionMap.get(pp.getCompany()).add(new PersonPositionMetaModel(pp.getId(), pp.getPosition(), pp.getElectionDate()));
            }
        });

        Map<LegalPersonMetaModel, List<ShareMetaModel>> sharesMap = new HashMap<>();

        personShares.forEach(sh -> {
            if (sh.getCompany() != null && sh.getCompany().getFiId() <= 0) {

                legalPersonMetaModels.add(sh.getCompany());

                sharesMap.putIfAbsent(sh.getCompany(), new ArrayList<>());
                sharesMap.get(sh.getCompany()).add(new ShareMetaModel(sh.getId(), sh.getSharePercentage(), sh.getShareDate()));
            }
        });

        legalPersonMetaModels.forEach(legalPerson -> {
            connectionsModels.add(new PersonConnectionsModel(legalPerson.getName(), positionMap.get(legalPerson), sharesMap.get(legalPerson)));
        });

        List<PersonConnectionsModel> filteredConnections = connectionsModels;

        if (organizationTypes != null && organizationTypes.size() == 1) {
            filteredConnections = connectionsModels.stream().filter(c -> c.getConnectionType().equals(organizationTypes.get(0))).collect(Collectors.toList());
        }

        if (pages != null && !pages.isEmpty()) {
            Set<PersonConnectionsModel> filteredByBranches = new HashSet<>();
            Set<PersonConnectionsModel> filteredByShareHolders = new HashSet<>();
            Set<PersonConnectionsModel> filteredByManagement = new HashSet<>();
            if (pages.contains(FilterConnectionType.SHAREHOLDERS)) {
                filteredByShareHolders = filteredConnections.stream().filter(c -> !c.getFiBeneficiaries().isEmpty() || !c.getPersonShares().isEmpty()).collect(Collectors.toSet());
            }
            if (pages.contains(FilterConnectionType.BRANCHES)) {
                filteredByBranches = filteredConnections.stream().filter(c -> !c.getFiBranchPositions().isEmpty()).collect(Collectors.toSet());
            }
            if (pages.contains(FilterConnectionType.MANAGEMENT)) {
                filteredByManagement = filteredConnections.stream().filter(c -> !c.getFiManagements().isEmpty() || !c.getPersonPositions().isEmpty()).collect(Collectors.toSet());
            }


            filteredConnections = Stream.concat(filteredByManagement.stream(), filteredByBranches.stream()).collect(Collectors.toList());
            filteredConnections.addAll(filteredByShareHolders);
        }

        personModel.setConnections(filteredConnections);


        return personModel;
    }

    public void deletePerson(long personId) throws FinATypeException {
        personLocal.delete(Collections.singletonList(personId));
    }

    public void deleteFiPersons(List<Long> fiPersonIds, long fiId) throws FinATypeException {
        personLocal.deleteFiPersons(fiPersonIds, fiId);

    }

    public void deletePersons(List<Long> personIds) throws FinATypeException {
        personLocal.delete(personIds);
    }

    public List<PersonConnectionsModel> filterPersonConnections(long personId, boolean active, List<PersonConnectionType> organizationTypes, List<FilterConnectionType> pages) {
        PersonMetaModel person = getPersonInfo(personId, active, organizationTypes, pages);

        return person.getConnections();
    }

    public PersonMetaModel activateDeletedPerson(long fiId, PersonMetaModel personMetaModel) throws FinATypeException {
        return createOrRestoreDeletedPerson(personMetaModel, fiId, true);
    }

    private PersonMetaModel createOrRestoreDeletedPerson(PersonMetaModel personMetaModel, long fiId, boolean restore) throws FinATypeException {
        long langId = ThreadLocalHolder.getLanguage().getId();
        List<Beneficiary> beneficiaries = personMetaModel.getShares().stream().map(Beneficiary::new).collect(Collectors.toList());

        Person person;
        if (restore) {
            person = personLocal.activateDeletedPerson(PersonModelHelper.toEntity(personMetaModel, langId), fiId, beneficiaries);
        } else {
            person = personLocal.createPerson(PersonModelHelper.toEntity(personMetaModel, langId), fiId, beneficiaries);
        }
        List<ShareMetaModel> personShares = personLocal.getPersonOtherShares(person.getId()).stream().map(share -> new ShareMetaModel(share.getId(), share.getShare(), share.getCreationDate(), LegalPersonModelHelper.toModel(share.getLegalPerson(), langId))).collect(Collectors.toList());

        PersonMetaModel result = PersonModelHelper.toModel(person, langId);
        result.setShares(personShares);
        return result;
    }
}
