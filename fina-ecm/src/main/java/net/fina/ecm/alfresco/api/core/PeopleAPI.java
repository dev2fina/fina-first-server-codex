package net.fina.ecm.alfresco.api.core;

import net.fina.ecm.alfresco.api.common.constant.APIConstants;
import net.fina.ecm.alfresco.api.common.parameters.IncludeParam;
import net.fina.ecm.alfresco.api.common.representation.ResultPaging;
import net.fina.ecm.alfresco.api.core.model.body.PersonBodyCreate;
import net.fina.ecm.alfresco.api.core.model.body.PersonBodyUpdate;
import net.fina.ecm.alfresco.api.core.model.representation.PersonRepresentation;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

public interface PeopleAPI {

    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/people")
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<PersonRepresentation> loadAllUsers();

    @POST
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/people")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    PersonRepresentation createPerson(PersonBodyCreate personRepresentation);

    @PUT
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/people/{personId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    PersonRepresentation updatePerson(@PathParam("personId") String personId,
                                      PersonBodyUpdate personBodyUpdate);

    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/people/{personId}")
    @Produces(MediaType.APPLICATION_JSON)
    PersonRepresentation getPersonById(@PathParam("personId") String personId);

    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/people/{personId}")
    @Produces(MediaType.APPLICATION_JSON)
    PersonRepresentation getPersonById(@PathParam("personId") String personId,
                                       @QueryParam(APIConstants.INCLUDE_VALUE) IncludeParam include);


}
