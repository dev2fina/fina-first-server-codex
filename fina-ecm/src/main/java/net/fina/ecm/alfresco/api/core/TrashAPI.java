package net.fina.ecm.alfresco.api.core;

import net.fina.ecm.alfresco.api.common.constant.APIConstants;
import net.fina.ecm.alfresco.api.common.parameters.FieldsParam;
import net.fina.ecm.alfresco.api.common.parameters.IncludeParam;
import net.fina.ecm.alfresco.api.common.representation.ResultPaging;
import net.fina.ecm.alfresco.api.core.model.representation.DeletedNodeRepresentation;
import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

public interface TrashAPI {
    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/deleted-nodes")
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<DeletedNodeRepresentation> listDeletedNodes();

    /**
     * Get deleted nodes Returns a list of deleted nodes for the current user.
     * If the current user is an administrator deleted nodes for all users will
     * be returned. The list of deleted nodes will be ordered with the most
     * recently deleted node at the top of the list.
     *
     * @param skipCount The number of entities that exist in the collection
     *                  before those included in this list. (optional)
     * @param maxItems  The maximum number of items to return in the list.
     *                  (optional)
     * @param include   Returns additional information about the node. The
     *                  following optional fields can be requested: * properties *
     *                  aspects * path * isLink * allowableOperations * association
     *                  (optional)
     * @return ResultPaging<DeletedNodeRepresentation>
     */
    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/deleted-nodes")
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<DeletedNodeRepresentation> listDeletedNodesCall(
            @QueryParam(APIConstants.SKIP_COUNT_VALUE) Integer skipCount,
            @QueryParam(APIConstants.MAX_ITEMS_VALUE) Integer maxItems,
            @QueryParam(APIConstants.INCLUDE_VALUE) IncludeParam include);

    @DELETE
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/deleted-nodes/{nodeId}")
    @Produces(MediaType.APPLICATION_JSON)
    void purgeDeletedNodeCall(@PathParam("nodeId") String nodeId);

    @POST
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/deleted-nodes/{nodeId}/restore")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    NodeRepresentation restoreDeletedNodeCall(@PathParam("nodeId") String nodeId,
                                              @QueryParam(APIConstants.FIELDS_VALUE) FieldsParam fields);

}
