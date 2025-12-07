package net.fina.server.legalperson.proxy;

import net.fina.common.client.exception.FinATypeException;
import net.fina.common.server.util.PagingUtil;
import net.fina.common.shared.PaginatedListWrapper;
import net.fina.server.ThreadLocalHolder;
import net.fina.server.fi.api.FiLocal;
import net.fina.server.fi.api.RegionLocal;
import net.fina.server.fi.entity.Fi;
import net.fina.server.fi.entity.FiManagement;
import net.fina.server.fi.model.RegionMetaModelHelper;
import net.fina.server.legalperson.api.LegalPersonLocal;
import net.fina.server.legalperson.entity.Beneficiary;
import net.fina.server.legalperson.entity.ConnectedCOmpanyConnectionType;
import net.fina.server.legalperson.entity.ConnectedCompanyConnection;
import net.fina.server.legalperson.entity.LegalPerson;
import net.fina.server.legalperson.model.ConnectedCompanyConnectionModel;
import net.fina.server.legalperson.model.LegalPersonContactInfoMetaModel;
import net.fina.server.legalperson.model.LegalPersonMetaModel;
import net.fina.server.legalperson.model.PersonFilter;
import net.fina.server.legalperson.model.connection.ConnectedCompanyModel;
import net.fina.server.legalperson.model.helper.ConnectedCompanyConnectionModelHelper;
import net.fina.server.legalperson.model.helper.LegalPersonMetaInfoModelHelper;
import net.fina.server.legalperson.model.helper.LegalPersonModelHelper;
import net.fina.server.person.model.PersonPositionMetaModel;
import net.fina.server.person.model.ShareMetaModel;
import net.fina.server.person.model.helper.CriminalRecordModelHelper;
import net.fina.server.person.model.helper.PersonModelHelper;
import org.jboss.ejb3.annotation.SecurityDomain;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Stateless
@SecurityDomain("FinASecurityDomain")
public class LegalPersonProxySession {

    @Inject
    private LegalPersonLocal legalPersonLocal;
    @Inject
    private RegionLocal regionLocal;
    @Inject
    private FiLocal fiLocal;

    public PaginatedListWrapper<LegalPersonMetaModel> loadFiLegalPersons(int page, int limit, long fiId, Map<PersonFilter, Object> filter) throws FinATypeException {
        long langId = ThreadLocalHolder.getLanguage().getId();
        int offset = PagingUtil.getOffsetFromPage(page, limit);

        List<LegalPerson> legalPersons = legalPersonLocal.loadFiLegalPersons(offset, limit, fiId, filter);

        long count = legalPersonLocal.countFiLegalPersons(fiId, filter);

        return new PaginatedListWrapper<>(LegalPersonModelHelper.toModels(legalPersons, langId), limit, count);
    }

    public LegalPersonMetaModel create(LegalPersonMetaModel legalPersonMetaModel, long fiId) throws FinATypeException {
        return createOrRestoreDeletedLegalPerson(legalPersonMetaModel, fiId, false);
    }

    public LegalPersonMetaModel update(LegalPersonMetaModel legalPersonMetaModel, long fiId) throws FinATypeException {
        long langId = ThreadLocalHolder.getLanguage().getId();
        List<Beneficiary> beneficiaries = legalPersonMetaModel.getShares().stream().map(Beneficiary::new).collect(Collectors.toList());

        LegalPerson legalPerson = legalPersonLocal.update(LegalPersonModelHelper.toEntity(legalPersonMetaModel, langId), fiId, beneficiaries);
        LegalPersonMetaModel result = LegalPersonModelHelper.toModel(legalPerson, langId);

        if (!beneficiaries.isEmpty()) {

            List<Beneficiary> shares = legalPersonLocal.loadLegalPersonOtherShares(legalPersonMetaModel.getId());

            List<ShareMetaModel> shareMetaModels = shares.stream().map(share -> new ShareMetaModel(share.getId(), share.getShare(), share.getCreationDate(), LegalPersonModelHelper.toModel(share.getLegalPerson(), langId))).collect(Collectors.toList());

            result.setShares(shareMetaModels);
        }


        return result;
    }

    public PaginatedListWrapper<LegalPersonMetaModel> loadLegalPersons(int page, int limit, Map<PersonFilter, Object> filter) {
        long langId = ThreadLocalHolder.getLanguage().getId();
        int offset = PagingUtil.getOffsetFromPage(page, limit);

        long count = legalPersonLocal.countLegalPersons(filter, langId);
        List<LegalPerson> legalPersons = legalPersonLocal.loadLegalPersons(offset, limit, langId, filter);

        return new PaginatedListWrapper<>(LegalPersonModelHelper.toModels(legalPersons, langId), limit, count);
    }

    public List<LegalPersonMetaModel> loadAllPersonSimple(Map<PersonFilter, Object> filter) {
        long langId = ThreadLocalHolder.getLanguage().getId();
        List<LegalPerson> legalPersons = legalPersonLocal.loadAllPersonSimple(filter);
        return LegalPersonModelHelper.toModelsSimple(legalPersons, langId);
    }

    public LegalPersonMetaModel getLegalPersonById(long legalPersonId) {
        long langId = ThreadLocalHolder.getLanguage().getId();
        LegalPersonMetaModel lp = LegalPersonModelHelper.toModel(legalPersonLocal.getLegalPersonById(legalPersonId), langId);

        List<Beneficiary> shares = legalPersonLocal.loadLegalPersonOtherShares(legalPersonId);

        List<ShareMetaModel> shareMetaModels = shares.stream().map(share -> new ShareMetaModel(share.getId(), share.getShare(), share.getCreationDate(), LegalPersonModelHelper.toModel(share.getLegalPerson(), langId))).collect(Collectors.toList());
        lp.setShares(shareMetaModels);

        if (lp.isBank()) {
            Fi fi = fiLocal.getFiById(lp.getFiId());
            if (fi != null) {

                lp.setRegistrationNumber(fi.getCode());

                if (fi.getRegionId() != null) {
                    lp.setCountry(RegionMetaModelHelper.toModel(regionLocal.getRegionWithId(fi.getRegionId()), langId));
                }

                lp.setContactInfo(new LegalPersonContactInfoMetaModel(lp.getCountry(), fi.getAddressDescription().getDescription(langId), fi.getPhone(), fi.getWebSite()));
                if (fi.getFiAdditionalInfo() != null) {
                    lp.setMetaInfo(LegalPersonMetaInfoModelHelper.toModel(fi.getFiAdditionalInfo()));
                }

                lp.setCriminalRecords(CriminalRecordModelHelper.toModels(fi.getCriminalRecords(), langId));
                List<FiManagement> fiManagements = fiLocal.loadFiManagement(fi.getId());
                List<PersonPositionMetaModel> personPositions = fiManagements.stream().map(fm -> {
                    PersonPositionMetaModel pp = new PersonPositionMetaModel();
                    pp.setId(fm.getId());
                    pp.setPosition(fm.getPosition());
                    if (fm.getFiPerson() != null) {
                        pp.setPerson(PersonModelHelper.toModel(fm.getFiPerson().getPerson(), langId));
                    }
                    return pp;
                }).collect(Collectors.toList());

                lp.setManagers(personPositions);

            }
        }


        return lp;
    }

    public void deleteFiLegalPersonsConnection(List<Long> legalPersonIds, long fiId) throws FinATypeException {
        legalPersonLocal.deleteFiLegalPersonsConnection(fiId, legalPersonIds);
    }


    public void deleteLegalPerson(List<Long> legalPersonIds) throws FinATypeException {
        legalPersonLocal.deleteLegalPerson(legalPersonIds);
    }

    public List<LegalPersonMetaModel> getLegalPersonDependencies(long legalPersonId) {
        return LegalPersonModelHelper.toModelsSimple(legalPersonLocal.getLegalPersonDependencies(legalPersonId), ThreadLocalHolder.getLanguage().getId());
    }


    public List<ConnectedCompanyModel> calculateCompanyConnections(long companyId) {
        return legalPersonLocal.calculateCompanyConnections(legalPersonLocal.getLegalPersonById(companyId));
    }

    public ConnectedCompanyConnectionModel saveConnectionInfo(ConnectedCompanyConnectionModel model) throws FinATypeException {
        long langId = ThreadLocalHolder.getLanguage().getId();
        ConnectedCompanyConnection connection = ConnectedCompanyConnectionModelHelper.toEntity(model, langId);
        connection = legalPersonLocal.saveConnectionInfo(connection);
        model.setId(connection.getId());
        return model;
    }

    public ConnectedCompanyConnectionModel getConnectionInfo(long sourceId, long destinationId, ConnectedCOmpanyConnectionType type) {
        ConnectedCompanyConnection connection = legalPersonLocal.getConnectionInfo(sourceId, destinationId, type);
        long langId = ThreadLocalHolder.getLanguage().getId();

        if (connection == null) {
            connection = new ConnectedCompanyConnection();
            connection.setSource(legalPersonLocal.getLegalPersonById(sourceId));
            connection.setDestination(legalPersonLocal.getLegalPersonById(destinationId));
            connection.setConnectionType(type);
        }

        List<ConnectedCompanyModel> sourceConnections = calculateCompanyConnections(sourceId);
        List<ConnectedCompanyModel> destinationConnections = calculateCompanyConnections(destinationId);

        ConnectedCompanyConnectionModel result = ConnectedCompanyConnectionModelHelper.toModel(connection, ThreadLocalHolder.getLanguage().getId());


        Function<LegalPerson, List<PersonPositionMetaModel>> managersFunction = legalPerson -> {
            List<FiManagement> fiManagements = fiLocal.loadFiManagement(legalPerson.getFiId());
            return fiManagements.stream().map(fm -> {
                PersonPositionMetaModel pp = new PersonPositionMetaModel();
                pp.setId(fm.getId());
                pp.setPosition(fm.getPosition());
                pp.setCompany(LegalPersonModelHelper.toModelSimple(legalPerson, langId));
                if (fm.getFiPerson() != null) {
                    pp.setPerson(PersonModelHelper.toModel(fm.getFiPerson().getPerson(), langId));
                }
                return pp;
            }).collect(Collectors.toList());
        };

        if (result.getSource().getFiId() > 0) {
            result.getSource().setManagers(managersFunction.apply(connection.getSource()));
        }
        if (result.getDestination().getFiId() > 0) {
            result.getDestination().setManagers(managersFunction.apply(connection.getDestination()));
        }

        result.setSourceConnectedCompanies(sourceConnections);
        result.setDestinationsConnectedCompanies(destinationConnections);
        return result;
    }

    public LegalPersonMetaModel activateDeletedLegalPerson(LegalPersonMetaModel legalPersonMetaModel, long fiId) throws FinATypeException {
        return createOrRestoreDeletedLegalPerson(legalPersonMetaModel, fiId, true);
    }

    private LegalPersonMetaModel createOrRestoreDeletedLegalPerson(LegalPersonMetaModel legalPersonMetaModel, long fiId, boolean restore) throws FinATypeException {
        long langId = ThreadLocalHolder.getLanguage().getId();

        List<Beneficiary> beneficiaries = legalPersonMetaModel.getShares().stream().map(Beneficiary::new).collect(Collectors.toList());

        LegalPerson legalPerson;
        if (restore) {
            legalPerson = legalPersonLocal.activateDeletedLegalPerson(LegalPersonModelHelper.toEntity(legalPersonMetaModel, langId), fiId, beneficiaries);
        } else {
            legalPerson = legalPersonLocal.create(LegalPersonModelHelper.toEntity(legalPersonMetaModel, langId), fiId, beneficiaries);
        }
        LegalPersonMetaModel result = LegalPersonModelHelper.toModel(legalPerson, langId);

        if (!beneficiaries.isEmpty()) {
            List<Beneficiary> shares = legalPersonLocal.loadLegalPersonOtherShares(legalPersonMetaModel.getId());

            List<ShareMetaModel> shareMetaModels = shares.stream().map(share -> new ShareMetaModel(share.getId(), share.getShare(), share.getCreationDate(), LegalPersonModelHelper.toModel(share.getLegalPerson(), langId))).collect(Collectors.toList());

            result.setShares(shareMetaModels);
        }
        return result;
    }
}
