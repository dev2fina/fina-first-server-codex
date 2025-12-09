package net.fina.ecm.alfresco.api.core;

import net.fina.ecm.alfresco.api.AlfrescoAPITestCase;
import net.fina.ecm.alfresco.api.common.representation.ResultPaging;
import net.fina.ecm.alfresco.api.core.model.body.GroupBodyCreate;
import net.fina.ecm.alfresco.api.core.model.body.GroupMembershipBodyCreate;
import net.fina.ecm.alfresco.api.core.model.representation.GroupMemberRepresentation;
import net.fina.ecm.alfresco.api.core.model.representation.GroupRepresentation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import jakarta.ws.rs.ClientErrorException;
import java.util.Arrays;

public class GroupsAPITest extends AlfrescoAPITestCase {

    @Before
    public void init() {
        client = getClient("jango","changeme");
    }

    @After
    public void cleanUp() {
        client.getRestClient().client.close();
    }


    @Test
    public void listGroups() {
        ResultPaging<GroupRepresentation> groups = client.getGroupsAPI().loadGroups();

        groups.getObjects().forEach(gr -> {
            System.out.println(gr.toString());
        });
    }


    @Test
    public void testCreateGroup() {
        GroupBodyCreate groupBodyCreate = new GroupBodyCreate("group2", "group2", Arrays.asList("GROUP_group1"));
        GroupRepresentation result = client.getGroupsAPI().createGroup(groupBodyCreate);
        System.out.println(result.toString());
    }


    @Test
    public void testGroupMembers() {
        ResultPaging<GroupMemberRepresentation> members = client.getGroupsAPI().loadGroupMembers("GROUP_ALFRESCO_ADMINISTRATORS");

        members.getObjects().forEach(u -> {
            System.out.println(u.toString());
        });
    }

    @Test
    public void testAddmemberToGroup() {
        GroupMembershipBodyCreate member = new GroupMembershipBodyCreate("NBG.5", GroupMemberRepresentation.MemberTypeEnum.PERSON);
        try {

            GroupMemberRepresentation result = client.getGroupsAPI().addMemberToGroup("GROUP_ALFRESCO_ADMINISTRATORS", member);

            System.out.println(result);
        } catch (ClientErrorException er) {
            System.err.println(er);
        }
    }

    @Test
    public void listUserGroups() {
        ResultPaging<GroupRepresentation> results = client.getGroupsAPI().listUserGroups("NBG.5");

        results.getObjects().forEach(gr ->
                System.out.println(gr.toString()));
    }

}
