package net.fina.first.ecm.people.api;

import net.fina.common.client.exception.FinATypeException;
import net.fina.common.shared.ecm.model.ECMPersonMetaModel;
import net.fina.ecm.alfresco.api.common.parameters.IncludeParam;
import net.fina.ecm.alfresco.api.core.model.body.PersonBodyUpdate;
import net.fina.ecm.alfresco.api.core.model.representation.PersonRepresentation;

import java.util.List;

public interface PeopleLocal {
    ECMPersonMetaModel getCurrentPerson();

    ECMPersonMetaModel getPersonById(String personId, IncludeParam includeParam);

    List<PersonRepresentation> getAllUsers();

    PersonRepresentation createPerson(PersonRepresentation person, String password) throws FinATypeException;

    PersonRepresentation updatePerson(PersonBodyUpdate update) throws FinATypeException;

    PersonRepresentation updateOrCreatePerson(PersonBodyUpdate bodyUpdate);

    List<ECMPersonMetaModel> getUsersWithEditingCapabilities();
}
