package net.fina.ecm.alfresco.api.search;

import net.fina.ecm.alfresco.api.common.constant.APIConstants;
import net.fina.ecm.alfresco.api.search.body.QueryBody;
import net.fina.ecm.alfresco.api.search.model.ResultNodeRepresentation;
import net.fina.ecm.alfresco.api.search.model.ResultSetRepresentation;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

public interface SearchAPI {
    @POST
    @Path(APIConstants.SEARCH_PUBLIC_API_V1 + "/search")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    ResultSetRepresentation<ResultNodeRepresentation> search(QueryBody queryBody);
}
