package net.fina.first.ecm.people.proxy;

import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.shared.ecm.model.ECMPersonMetaModel;
import net.fina.ecm.alfresco.api.common.parameters.IncludeParam;
import net.fina.ecm.alfresco.api.core.model.body.PersonBodyUpdate;
import net.fina.ecm.alfresco.api.core.model.representation.PersonRepresentation;
import net.fina.first.ecm.people.api.PeopleLocal;
import org.jboss.ejb3.annotation.SecurityDomain;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.List;

@Stateless
@RolesAllowed({PermissionIdNames.FIRST_REVIEW,PermissionIdNames.FINA_USER_AMEND})
@SecurityDomain("FinASecurityDomain")
public class PeopleProxySession {

    @Inject
    private PeopleLocal peopleLocal;

    public ECMPersonMetaModel getCurrentPerson() {
        return peopleLocal.getCurrentPerson();
    }

    public ECMPersonMetaModel getPersonById(String personId, IncludeParam includeParam) {
        return peopleLocal.getPersonById(personId, includeParam);
    }

    public List<PersonRepresentation> getAllUsers() {
        return peopleLocal.getAllUsers();
    }

    public PersonRepresentation createPerson(PersonRepresentation person, String password) throws FinATypeException {
        return peopleLocal.createPerson(person, password);
    }

    public PersonRepresentation updatePerson(PersonBodyUpdate update) throws FinATypeException {
        return peopleLocal.updatePerson(update);
    }

    public PersonRepresentation updateOrCreatePerson(PersonBodyUpdate bodyUpdate) throws FinATypeException {
        return peopleLocal.updateOrCreatePerson(bodyUpdate);
    }

    public List<ECMPersonMetaModel> getUsersWithEditingCapabilities() {
        return peopleLocal.getUsersWithEditingCapabilities();
    }
}
