package net.fina.first.ecm.people.impl;

import net.fina.common.client.exception.FinATypeException;
import net.fina.common.server.util.ConfigurationUtil;
import net.fina.common.shared.ecm.model.ECMPersonMetaModel;
import net.fina.ecm.alfresco.AlfrescoClient;
import net.fina.ecm.alfresco.api.common.constant.APIConstants;
import net.fina.ecm.alfresco.api.common.parameters.IncludeParam;
import net.fina.ecm.alfresco.api.core.model.body.PersonBodyCreate;
import net.fina.ecm.alfresco.api.core.model.body.PersonBodyUpdate;
import net.fina.ecm.alfresco.api.core.model.representation.GroupMemberRepresentation;
import net.fina.ecm.alfresco.api.core.model.representation.PersonRepresentation;
import net.fina.ecm.util.AlfrescoConfiguration;
import net.fina.ecm.util.AlfrescoPropConstants;
import net.fina.first.FirstUtil;
import net.fina.first.ecm.client.proxy.EcmClientProxySession;
import net.fina.first.ecm.group.proxy.GroupProxySession;
import net.fina.first.ecm.people.api.PeopleLocal;
import net.fina.first.ecm.people.model.ECMPersonModelHelper;
import net.fina.first.interceptors.FirstRecordingAuditor;
import org.apache.http.HttpStatus;

import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.ClientErrorException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Stateless
@Local(PeopleLocal.class)
@Interceptors(FirstRecordingAuditor.class)
public class PeopleSession implements PeopleLocal {

    @Inject
    private GroupProxySession groupProxySession;

    @Inject
    private EcmClientProxySession ecmClientProxySession;

    @Override
    public ECMPersonMetaModel getCurrentPerson() {
        return getPersonById("-me-", new IncludeParam(Collections.singletonList("capabilities")));
    }

    @Override
    public ECMPersonMetaModel getPersonById(String personId, IncludeParam includeParam) {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClient();
        ECMPersonMetaModel ecmPersonMetaModel = ECMPersonModelHelper.getPersonMetaModel(client.getPeopleAPI().getPersonById(personId, includeParam));
        ecmPersonMetaModel.setSuperAdmin(groupProxySession.userIsInGroup(ecmPersonMetaModel.getId(), AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.SUPER_ADMIN_GROUP_NAME_KEY)));
        ecmPersonMetaModel.setEditor(groupProxySession.userIsInGroup(ecmPersonMetaModel.getId(), AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.EDITOR_GROUP_NAME_KEY)));
        return ecmPersonMetaModel;
    }

    @Override
    public List<PersonRepresentation> getAllUsers() {
        return ecmClientProxySession.getAlfrescoClient().getPeopleAPI().loadAllUsers().getObjects();
    }

    @Override
    public PersonRepresentation createPerson(PersonRepresentation person, String password) throws FinATypeException {
        try {
            person = ecmClientProxySession.getAlfrescoClientByUser(ConfigurationUtil.get().get("ECM.adminUsername")).getPeopleAPI().createPerson(new PersonBodyCreate(person.getId(), person.getFirstName(), person.getLastName(), person.getEmail(), person.getMobile(), true, password));
        } catch (ClientErrorException ex) {
            FirstUtil.handleClientException(ex);
        }

        return person;
    }

    @Override
    public PersonRepresentation updatePerson(PersonBodyUpdate update) throws FinATypeException {
        try {
            return ecmClientProxySession.getAlfrescoClientByUser(ConfigurationUtil.get().get("ECM.adminUsername")).getPeopleAPI().updatePerson(update.getId(), update);
        } catch (ClientErrorException ex) {
            FirstUtil.handleClientException(ex);
        }

        return null;
    }

    @Override
    public PersonRepresentation updateOrCreatePerson(PersonBodyUpdate bodyUpdate) {
        AlfrescoClient client = ecmClientProxySession.getAlfrescoClientByUser(ConfigurationUtil.get().get("ECM.adminUsername"));

        try {
            client.getPeopleAPI().getPersonById(bodyUpdate.getId());
            return client.getPeopleAPI().updatePerson(bodyUpdate.getId(), bodyUpdate);
        } catch (ClientErrorException ex) {
            //create user if not exists
            if (ex.getResponse().getStatus() == HttpStatus.SC_NOT_FOUND) {
                return client.getPeopleAPI().createPerson(new PersonBodyCreate(bodyUpdate.getId(), bodyUpdate.getFirstName(), bodyUpdate.getLastName(), bodyUpdate.getEmail(), bodyUpdate.getMobile(), true, bodyUpdate.getPassword()));
            }
        }
        return null;
    }

    @Override
    public List<ECMPersonMetaModel> getUsersWithEditingCapabilities() {
        Set<GroupMemberRepresentation> editorMembers = groupProxySession.loadGroupMemberPeople("GROUP_" + AlfrescoConfiguration.get().getAlfrescoProperty(AlfrescoPropConstants.EDITOR_GROUP_NAME_KEY));
        Set<GroupMemberRepresentation> allMembers = new HashSet<>(editorMembers);
        Set<GroupMemberRepresentation> administratorMembers = groupProxySession.loadGroupMemberPeople(AlfrescoPropConstants.ALFRESCO_ADMIN_GROUP_NAME);
        allMembers.addAll(administratorMembers);

        List<ECMPersonMetaModel> result = new ArrayList<>();
        allMembers.forEach(member ->
                result.add(getPersonById(member.getId(), new IncludeParam(Collections.singletonList(APIConstants.PROPERTIES_VALUE)))));

        return result;
    }

}
