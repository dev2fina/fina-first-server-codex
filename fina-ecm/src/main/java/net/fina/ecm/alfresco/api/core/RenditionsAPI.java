package net.fina.ecm.alfresco.api.core;

import net.fina.ecm.alfresco.api.common.constant.APIConstants;
import net.fina.ecm.alfresco.api.common.representation.ResultPaging;
import net.fina.ecm.alfresco.api.core.model.body.RenditionBodyCreate;
import net.fina.ecm.alfresco.api.core.model.representation.RenditionRepresentation;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public interface RenditionsAPI {

    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/nodes/{nodeId}/renditions")
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<RenditionRepresentation> listRenditionsCall(@PathParam("nodeId") String nodeId);

    @POST
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/nodes/{nodeId}/renditions")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response createRenditionCall(@PathParam("nodeId") String nodeId, RenditionBodyCreate renditionBodyCreate);

    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/nodes/{nodeId}/renditions/{renditionId}")
    @Produces(MediaType.APPLICATION_JSON)
    RenditionRepresentation getRenditionCall(@PathParam("nodeId") String nodeId,
                                             @PathParam("renditionId") String renditionId);

    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/nodes/{nodeId}/renditions/{renditionId}/content")
    Response getRenditionContentCall(@PathParam("nodeId") String nodeId, @PathParam("renditionId") String renditionId);

    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/nodes/{nodeId}/renditions/{renditionId}/content")
    Response getRenditionContentCall(@PathParam("nodeId") String nodeId, @PathParam("renditionId") String renditionId,
                                     @QueryParam("attachment") Boolean attachment);
}
