package net.fina.ecm.alfresco.api.core;

import net.fina.ecm.alfresco.api.common.constant.APIConstants;
import net.fina.ecm.alfresco.api.common.parameters.FieldsParam;
import net.fina.ecm.alfresco.api.common.parameters.IncludeParam;
import net.fina.ecm.alfresco.api.common.representation.ResultPaging;
import net.fina.ecm.alfresco.api.core.model.body.SharedLinkBodyCreate;
import net.fina.ecm.alfresco.api.core.model.representation.RenditionRepresentation;
import net.fina.ecm.alfresco.api.core.model.representation.SharedLinkRepresentation;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Date;

public interface SharedLinksAPI {
    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/shared-links")
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<SharedLinkRepresentation> listSharedLinks();


    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/shared-links")
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<SharedLinkRepresentation> listSharedLinks(
            @QueryParam(APIConstants.SKIP_COUNT_VALUE) Integer skipCount,
            @QueryParam(APIConstants.MAX_ITEMS_VALUE) Integer maxItems,
            @QueryParam(APIConstants.WHERE_VALUE) String where,
            @QueryParam(APIConstants.INCLUDE_VALUE) IncludeParam include,
            @QueryParam(APIConstants.FIELDS_VALUE) FieldsParam fields);


    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/shared-links/{sharedId}")
    @Produces(MediaType.APPLICATION_JSON)
    SharedLinkRepresentation getSharedLink(@PathParam("sharedId") String sharedId);


    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/shared-links/{sharedId}")
    @Produces(MediaType.APPLICATION_JSON)
    SharedLinkRepresentation getSharedLink(@PathParam("sharedId") String sharedId,
                                           @QueryParam(APIConstants.INCLUDE_VALUE) IncludeParam include,
                                           @QueryParam(APIConstants.FIELDS_VALUE) FieldsParam fields);


    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/shared-links/{sharedId}/content")
    Response getSharedLinkContent(@PathParam("sharedId") String sharedId);


    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/shared-links/{sharedId}/content")
    Response getSharedLinkContent(@PathParam("sharedId") String sharedId,
                                  @QueryParam("attachment") Boolean attachment, @HeaderParam("If-Modified-Since") Date ifModifiedSince);


    @POST
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/shared-links")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    SharedLinkRepresentation createSharedLink(SharedLinkBodyCreate sharedLinkBody);


    @POST
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/shared-links")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    SharedLinkRepresentation createSharedLink(SharedLinkBodyCreate sharedLinkBody,
                                              @QueryParam(APIConstants.INCLUDE_VALUE) IncludeParam include,
                                              @QueryParam(APIConstants.FIELDS_VALUE) FieldsParam fields);


    @DELETE
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/shared-links/{sharedId}")
    void deleteSharedLink(@PathParam("sharedId") String sharedId);


    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/shared-links/{sharedId}/renditions")
    @Produces(MediaType.APPLICATION_JSON)
    RenditionRepresentation listSharedLinkRenditions(@PathParam("sharedId") String sharedId);


    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/shared-links/{sharedId}/renditions/{renditionId}")
    @Produces(MediaType.APPLICATION_JSON)
    RenditionRepresentation getSharedLinkRendition(@PathParam("sharedId") String sharedId,
                                                   @PathParam("renditionId") String renditionId);


    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/shared-links/{sharedId}/renditions/{renditionId}/content")
    Response getSharedLinkRenditionContent(@PathParam("sharedId") String sharedId,
                                           @PathParam("renditionId") String renditionId);


    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/shared-links/{sharedId}/renditions/{renditionId}/content")
    Response getSharedLinkRenditionContent(@PathParam("sharedId") String sharedId,
                                           @PathParam("renditionId") String renditionId, @QueryParam("attachment") Boolean attachment,
                                           @HeaderParam("If-Modified-Since") Date ifModifiedSince);


}
