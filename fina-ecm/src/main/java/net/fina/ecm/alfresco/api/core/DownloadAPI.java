package net.fina.ecm.alfresco.api.core;

import net.fina.ecm.alfresco.api.common.constant.APIConstants;
import net.fina.ecm.alfresco.api.core.model.body.DownloadBodyCreate;
import net.fina.ecm.alfresco.api.core.model.representation.DownloadRepresentation;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

public interface DownloadAPI {

    @POST
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/downloads")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    DownloadRepresentation createDownloadRepresentation(DownloadBodyCreate downloadBodyCreate);

    @DELETE
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/downloads/{downloadId}")
    int cancelDownload(@PathParam("downloadId") String downloadId);

    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/downloads/{downloadId}")
    DownloadRepresentation getDownloadRepresentation(@PathParam("downloadId") String downloadId);

}
