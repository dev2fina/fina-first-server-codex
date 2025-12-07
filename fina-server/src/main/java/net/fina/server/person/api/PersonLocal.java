package net.fina.server.person.api;

import net.fina.common.client.exception.FinATypeException;
import net.fina.server.fi.entity.*;
import net.fina.server.legalperson.entity.Beneficiary;
import net.fina.server.legalperson.model.PersonFilter;
import net.fina.server.person.entity.Person;
import net.fina.server.person.entity.PersonPosition;

import java.util.List;
import java.util.Map;

public interface PersonLocal {

    List<Person> loadAllPersonSimple(Map<PersonFilter, Object> filter, long langId);

    long countAll(long langId, Map<PersonFilter,Object> filter);

    List<Person> loadFiPersonsSimple(int start, int limit, long fiId, long langId, String filterParam) throws FinATypeException;

    long countFiPersons(long fiId, long langId, String filterParam);

    Person createPerson(Person person, long fiId, List<Beneficiary> beneficiaries) throws FinATypeException;

    Person updatePerson(Person person, long fiId, List<Beneficiary> beneficiaries) throws FinATypeException;

    Person getBankPersonInfo(long fiId, long fiPersonId) throws FinATypeException;

    Person getPersonInfo(long personId);

    void delete(List<Long> personIds) throws FinATypeException;

    void deleteFiPerson(long fiPersonId, long fiId) throws FinATypeException;

    List<PersonPosition> getBankPersonPositions(long fiPersonId);

    List<PersonPosition> getPersonPositions(long personId);

    List<Fi> getPersonConnectedFis(long personId);

    void deleteFiPersons(List<Long> fiPersonIds, long fiId) throws FinATypeException;

    List<FiPerson> getFiPersons(long personId);

    List<FiBranch> getFiPersonBranches(long fiId, long personId);

    List<FiManagement> getFiPersonManagement(long fiId, long personId);

    List<Beneficiary> getFiBeneficiaries(long fiId, long personId);

    FiPerson createOrUpdateFiPersonRelation(long fiId, long personId, FiPersonConnectionType connectionType);

    List<Beneficiary> getPersonOtherShares(long personId);

    FiPerson getFiPerson(long fiId, long personId);

    void removeFiPersonConnection(FiPerson fiPerson, FiPersonConnectionType connectionType);

    Person activateDeletedPerson(Person person, long fiId, List<Beneficiary> beneficiaries) throws FinATypeException;

    Person findByIdentificationCodeAndRegion(String identificationCode,long regionId);
}
