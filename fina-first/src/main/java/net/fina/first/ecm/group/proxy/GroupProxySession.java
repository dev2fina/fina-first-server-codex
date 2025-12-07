package net.fina.first.ecm.group.proxy;

import net.fina.common.client.constants.PermissionIdNames;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.server.util.ConfigurationUtil;
import net.fina.common.shared.ecm.model.ECMGroupMetaModel;
import net.fina.common.shared.ecm.model.ECMPersonMetaModel;
import net.fina.ecm.alfresco.AlfrescoClient;
import net.fina.ecm.alfresco.api.common.representation.ResultPaging;
import net.fina.ecm.alfresco.api.core.model.body.GroupBodyCreate;
import net.fina.ecm.alfresco.api.core.model.body.GroupMembershipBodyCreate;
import net.fina.ecm.alfresco.api.core.model.body.PersonBodyCreate;
import net.fina.ecm.alfresco.api.core.model.body.PersonBodyUpdate;
import net.fina.ecm.alfresco.api.core.model.representation.GroupMemberRepresentation;
import net.fina.ecm.alfresco.api.core.model.representation.GroupRepresentation;
import net.fina.ecm.alfresco.api.core.model.representation.PersonRepresentation;
import net.fina.ecm.util.AlfrescoPropConstants;
import net.fina.first.FirstUtil;
import net.fina.first.ecm.client.proxy.EcmClientProxySession;
import net.fina.first.ecm.group.model.ECMGroupModelHelper;
import net.fina.first.ecm.people.api.PeopleLocal;
import net.fina.first.interceptors.FirstRecordingAuditor;
import org.apache.http.HttpStatus;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.logging.Logger;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.ClientErrorException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Stateless
@RolesAllowed({PermissionIdNames.FIRST_REVIEW})
@SecurityDomain("FinASecurityDomain")
@Interceptors(FirstRecordingAuditor.class)
public class GroupProxySession {

    private static final Logger log = Logger.getLogger(GroupProxySession.class);

    @Inject
    private EcmClientProxySession clientProxySession;

    @Inject
    private PeopleLocal peopleLocal;

    public List<ECMGroupMetaModel> loadGroups() {
        List<GroupRepresentation> allGroups = clientProxySession.getAlfrescoClientByUser(ConfigurationUtil.get().get("ECM.adminUsername")).getGroupsAPI().loadGroups().getObjects();
        List<GroupRepresentation> filteredGroups = allGroups.stream().filter(group -> !group.getId().startsWith("GROUP_site")).collect(Collectors.toList());
        return ECMGroupModelHelper.getGroupMetaModels(filteredGroups);
    }

    public List<ECMGroupMetaModel> loadUserGroups(String login) {
        try {
            List<GroupRepresentation> allGroups = clientProxySession.getAlfrescoClientByUser(ConfigurationUtil.get().get("ECM.adminUsername")).getGroupsAPI().listUserGroups(login).getObjects();
            if (allGroups != null) {
                List<GroupRepresentation> filteredGroups = allGroups.stream().filter(group -> !group.getId().startsWith("GROUP_site")).collect(Collectors.toList());
                return ECMGroupModelHelper.getGroupMetaModels(filteredGroups);
            }
        } catch (ClientErrorException ex) {
            if (ex.getResponse().getStatus() == HttpStatus.SC_NOT_FOUND) {
                log.error("Person [" + login + "]not found");
            }
        }
        return new ArrayList<>();
    }

    public List<ECMPersonMetaModel> loadGroupById(String groupId) {
        try {
            List<GroupMemberRepresentation> members = clientProxySession.getAlfrescoClientByUser(
                    ConfigurationUtil.get().get("ECM.adminUsername")).getGroupsAPI().loadGroupMembers(groupId).getObjects();
            return members.stream().map(item -> peopleLocal.getPersonById(item.getId(), null)).collect(Collectors.toList());
        } catch (ClientErrorException ex) {
            if (ex.getResponse().getStatus() == HttpStatus.SC_NOT_FOUND) {
                log.error("Group [" + groupId + "] not found!");
            }
        }
        return new ArrayList<>();
    }

    @RolesAllowed(PermissionIdNames.FINA_USER_AMEND)
    public void saveUserGroups(PersonRepresentation person, String password, List<ECMGroupMetaModel> ecmGroups) throws FinATypeException {
        AlfrescoClient client = clientProxySession.getAlfrescoClientByUser(ConfigurationUtil.get().get("ECM.adminUsername"));
        checkAndCreateECMUser(client, person, password);

        try {
            clearEcmUserFromGroups(client, person.getId());
            ecmGroups.forEach(gr -> {
                client.getGroupsAPI().addMemberToGroup(gr.getId(), new GroupMembershipBodyCreate(person.getId(), GroupMemberRepresentation.MemberTypeEnum.PERSON));
            });
        } catch (ClientErrorException ex) {
            FirstUtil.handleClientException(ex);
        }
    }

    @RolesAllowed(PermissionIdNames.FINA_USER_AMEND)
    public ECMGroupMetaModel createGroup(ECMGroupMetaModel model) throws FinATypeException {
        GroupBodyCreate groupBodyCreate = new GroupBodyCreate(model.getId(), model.getDisplayName(), model.getParentIds());
        try {
            return ECMGroupModelHelper.getGroupMetaModel(clientProxySession.getAlfrescoClientByUser(ConfigurationUtil.get().get("ECM.adminUsername")).getGroupsAPI().createGroup(groupBodyCreate));
        } catch (ClientErrorException ex) {
            FirstUtil.handleClientException(ex);
        }
        return model;
    }

    @RolesAllowed(PermissionIdNames.FINA_USER_AMEND)
    public List<GroupMemberRepresentation> loadGroupMembers(String groupId) {
        ResultPaging<GroupMemberRepresentation> groupMembers = clientProxySession.getAlfrescoClientByUser(ConfigurationUtil.get().get("ECM.adminUsername")).getGroupsAPI().loadGroupMembers(groupId);
        return groupMembers.getObjects();
    }

    @RolesAllowed(PermissionIdNames.FINA_USER_AMEND)
    public Set<GroupMemberRepresentation> loadGroupMemberPeople(String groupId) {
        ResultPaging<GroupMemberRepresentation> groupMembers = clientProxySession.getAlfrescoClientByUser(ConfigurationUtil.get().get("ECM.adminUsername")).getGroupsAPI().loadGroupMembers(groupId);
        Set<GroupMemberRepresentation> members = new HashSet<>();
        groupMembers.getObjects().forEach(groupMemberRepresentation -> {
            switch (groupMemberRepresentation.getMemberType()) {
                case PERSON:
                    members.add(groupMemberRepresentation);
                    break;
                case GROUP:
                    loadGroupMemberPeopleRecursive(groupMemberRepresentation, members);
                    break;
            }
        });
        return members;
    }


    private void loadGroupMemberPeopleRecursive(GroupMemberRepresentation groupMemberRepresentation, Set<GroupMemberRepresentation> resultSet) {
        ResultPaging<GroupMemberRepresentation> groupMembers = clientProxySession.getAlfrescoClientByUser(ConfigurationUtil.get().get("ECM.adminUsername")).getGroupsAPI().loadGroupMembers(groupMemberRepresentation.getId());

        groupMembers.getObjects().forEach(m -> {
            switch (m.getMemberType()) {
                case GROUP:
                    loadGroupMemberPeopleRecursive(m, resultSet);
                    break;
                case PERSON:
                    resultSet.add(m);
                    break;
            }
        });
    }

    private void checkAndCreateECMUser(AlfrescoClient client, PersonRepresentation person, String password) throws FinATypeException {
        try {
            client.getPeopleAPI().getPersonById(person.getId());
            PersonBodyUpdate personBodyUpdate = person.toPersonBodyUpdate();
            if (password != null && !password.trim().isEmpty()) {
                personBodyUpdate.setPassword(password);
            }
            client.getPeopleAPI().updatePerson(personBodyUpdate.getId(), personBodyUpdate);
        } catch (ClientErrorException ex) {
            //create user if not exists
            if (ex.getResponse().getStatus() == HttpStatus.SC_NOT_FOUND) {
                client.getPeopleAPI().createPerson(new PersonBodyCreate(person.getId(), person.getFirstName(), person.getLastName(), person.getEmail(), person.getMobile(), true, password));
            }
        }
    }

    private void clearEcmUserFromGroups(AlfrescoClient client, String memberId) {
        List<GroupRepresentation> userGroups = client.getGroupsAPI().listUserGroups(memberId).getObjects();
        if (userGroups != null) {
            userGroups.stream().filter(group -> !group.getId().startsWith("GROUP_site") && !group.getId().equalsIgnoreCase(AlfrescoPropConstants.ALFRESCO_DEFAULT_GROUP_NAME)).forEach(gr -> {
                try {
                    client.getGroupsAPI().deleteGroupMember(gr.getId(), memberId);
                } catch (Exception ex) {
                    log.error(ex.getMessage(), ex);
                }
            });
        }
    }

    public boolean userIsInGroup(String userId, String group) {
        boolean inGroup = false;
        String groupName = ("GROUP_" + group);

        List<ECMGroupMetaModel> ecmGroupMetaModels = loadUserGroups(userId);
        if (ecmGroupMetaModels != null && !ecmGroupMetaModels.isEmpty()) {
            for (ECMGroupMetaModel ecmGroupMetaModel : ecmGroupMetaModels) {
                if (ecmGroupMetaModel.getId().equalsIgnoreCase(groupName)) {
                    inGroup = true;
                    break;
                }
            }
        }

        return inGroup;
    }

}
