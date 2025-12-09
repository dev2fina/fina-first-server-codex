package net.fina.ecm.alfresco.api.core;

import net.fina.ecm.alfresco.api.common.constant.APIConstants;
import net.fina.ecm.alfresco.api.common.representation.ResultPaging;
import net.fina.ecm.alfresco.api.core.model.body.GroupBodyCreate;
import net.fina.ecm.alfresco.api.core.model.body.GroupMembershipBodyCreate;
import net.fina.ecm.alfresco.api.core.model.representation.GroupMemberRepresentation;
import net.fina.ecm.alfresco.api.core.model.representation.GroupRepresentation;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

public interface GroupsAPI {

    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/groups")
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<GroupRepresentation> loadGroups();


    @POST
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/groups")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    GroupRepresentation createGroup(GroupBodyCreate groupBodyCreate);

    @PUT
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/groups{groupId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    GroupRepresentation updateGroup(@PathParam("groupId") String groupId, GroupBodyCreate groupBodyCreate);


    @DELETE
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/groups/{groupId}")
    void delete(@PathParam("groupId") String groupId);

    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/groups/{groupId}/members")
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<GroupMemberRepresentation> loadGroupMembers(@PathParam("groupId") String groupId);

    @POST
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/groups/{groupId}/members")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    GroupMemberRepresentation addMemberToGroup(@PathParam("groupId") String groupId, GroupMembershipBodyCreate groupMembershipBodyCreate);


    @DELETE
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/groups/{groupId}/members/{groupMemberId}")
    void deleteGroupMember(@PathParam("groupId") String groupId, @PathParam("groupMemberId") String groupMemberId);

    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1+"/people/{personId}/groups")
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<GroupRepresentation> listUserGroups(@PathParam("personId")String personId);

}
