package net.fina.server.legalperson.api;

import net.fina.common.client.exception.FinATypeException;
import net.fina.server.fi.entity.Fi;
import net.fina.server.legalperson.entity.*;
import net.fina.server.legalperson.model.PersonFilter;
import net.fina.server.legalperson.model.connection.ConnectedCompanyModel;
import net.fina.server.person.entity.PersonPosition;

import java.util.List;
import java.util.Map;

public interface LegalPersonLocal {
    List<LegalPerson> loadFiLegalPersons(int start, int limit, long fiId, Map<PersonFilter, Object> filter) throws FinATypeException;
    long countFiLegalPersons(long fiId, Map<PersonFilter, Object> filter);

    List<LegalPerson> loadLegalPersons(int page, int limit, long langId, Map<PersonFilter, Object> filter);

    LegalPerson create(LegalPerson legalPerson, long fiId, List<Beneficiary> otherShares) throws FinATypeException;

    LegalPerson update(LegalPerson legalPerson, long fiId, List<Beneficiary> otherShares) throws FinATypeException;

    LegalPerson findByIdentificationNumber(String identificationCode);

    long countLegalPersons(Map<PersonFilter, Object> filter, long langId);

    List<LegalPerson> loadAllPersonSimple(Map<PersonFilter, Object> filter);

    LegalPerson getLegalPersonById(long id);

    LegalPerson getLegalPersonByFiId(long fiId);

    void deleteFiLegalPersonsConnection(long fiId, List<Long> legalPersonIds) throws FinATypeException;

    void deleteLegalPerson(List<Long> legalPersonIds) throws FinATypeException;

    void deleteFiLegalPerson(long fiId, long legalPersonId) throws FinATypeException;

    FiLegalPerson getFiLegalPerson(long id);

    List<PersonPosition> loadLegalPersonPositions(long legalPersonId);

    List<Beneficiary> getLegalPersonBeneficiariesByPersonId(long personId);

    void createFiAsLegalPerson(Fi fi);

    List<Beneficiary> loadLegalPersonOtherShares(long legalPersonId);

    FiLegalPerson createFiLegalPersonRelation(long legalPersonId, long fiId, FiLegalPersonConnectionType connectionType) throws FinATypeException;

    FiLegalPerson findFiLegalPerson(long fiId, long legalPersonId);

    List<ConnectedCompanyModel> calculateCompanyConnections(LegalPerson company);

    ConnectedCompanyConnection saveConnectionInfo(ConnectedCompanyConnection entity) throws FinATypeException;

    ConnectedCompanyConnection getConnectionInfo(long sourceId, long destinationId, ConnectedCOmpanyConnectionType type);

    LegalPerson activateDeletedLegalPerson(LegalPerson legalPerson, long fiId, List<Beneficiary> otherShares) throws FinATypeException;

    List<LegalPerson> getLegalPersonDependencies(long legalPersonId);
}
