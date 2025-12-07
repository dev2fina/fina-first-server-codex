package net.fina.ecm.alfresco.api.core;

import net.fina.ecm.alfresco.api.common.constant.APIConstants;
import net.fina.ecm.alfresco.api.common.parameters.FieldsParam;
import net.fina.ecm.alfresco.api.common.parameters.IncludeParam;
import net.fina.ecm.alfresco.api.common.representation.ResultPaging;
import net.fina.ecm.alfresco.api.core.model.body.RevertBody;
import net.fina.ecm.alfresco.api.core.model.representation.VersionRepresentation;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public interface VersionAPI {

    /**
     * List version history **Note:** this endpoint is available in Alfresco 5.2
     * and newer versions. Gets the version history as an ordered list of
     * versions for the specified **nodeId**. The list is ordered in descending
     * modified order. So the most recent version is first and the original
     * version is last in the list.
     *
     * @param nodeId The identifier of a node. (required)
     * @return VersionPaging
     */
    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/nodes/{nodeId}/versions")
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<VersionRepresentation> listVersionHistoryCall(@PathParam("nodeId") String nodeId);

    /**
     * List version history **Note:** this endpoint is available in Alfresco 5.2
     * and newer versions. Gets the version history as an ordered list of
     * versions for the specified **nodeId**. The list is ordered in descending
     * modified order. So the most recent version is first and the original
     * version is last in the list.
     *
     * @param nodeId    The identifier of a node. (required)
     * @param include   Returns additional information about the version node. The
     *                  following optional fields can be requested: * properties *
     *                  aspectNames (optional)
     * @param fields    A list of field names. You can use this parameter to
     *                  restrict the fields returned within a response if, for
     *                  example, you want to save on overall bandwidth. The list
     *                  applies to a returned individual entity or entries within a
     *                  collection. If the API method also supports the **include**
     *                  parameter, then the fields specified in the **include**
     *                  parameter are returned in addition to those specified in the
     *                  **fields** parameter. (optional)
     * @param skipCount The number of entities that exist in the collection
     *                  before those included in this list. (optional)
     * @param maxItems  The maximum number of items to return in the list.
     *                  (optional)
     * @return VersionPaging
     */
    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/nodes/{nodeId}/versions")
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<VersionRepresentation> listVersionHistoryCall(@PathParam("nodeId") String nodeId,
                                                               @QueryParam(APIConstants.SKIP_COUNT_VALUE) Integer skipCount,
                                                               @QueryParam(APIConstants.MAX_ITEMS_VALUE) Integer maxItems,
                                                               @QueryParam(APIConstants.INCLUDE_VALUE) IncludeParam include,
                                                               @QueryParam(APIConstants.FIELDS_VALUE) FieldsParam fields);

    /**
     * List version history **Note:** this endpoint is available in Alfresco 5.2
     * and newer versions. Gets the version history as an ordered list of
     * versions for the specified **nodeId**. The list is ordered in descending
     * modified order. So the most recent version is first and the original
     * version is last in the list.
     *
     * @param nodeId The identifier of a node. (required)
     * @return VersionPaging
     */
    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/nodes/{nodeId}/versions/{versionId}")
    @Produces(MediaType.APPLICATION_JSON)
    VersionRepresentation getVersionCall(@PathParam("nodeId") String nodeId,
                                         @PathParam("versionId") String versionId);

    /**
     * List version history **Note:** this endpoint is available in Alfresco 5.2
     * and newer versions. Gets the version history as an ordered list of
     * versions for the specified **nodeId**. The list is ordered in descending
     * modified order. So the most recent version is first and the original
     * version is last in the list.
     *
     * @param nodeId    The identifier of a node. (required)
     * @param include   Returns additional information about the version node. The
     *                  following optional fields can be requested: * properties *
     *                  aspectNames (optional)
     * @param fields    A list of field names. You can use this parameter to
     *                  restrict the fields returned within a response if, for
     *                  example, you want to save on overall bandwidth. The list
     *                  applies to a returned individual entity or entries within a
     *                  collection. If the API method also supports the **include**
     *                  parameter, then the fields specified in the **include**
     *                  parameter are returned in addition to those specified in the
     *                  **fields** parameter. (optional)
     * @param skipCount The number of entities that exist in the collection
     *                  before those included in this list. (optional)
     * @param maxItems  The maximum number of items to return in the list.
     *                  (optional)
     * @return VersionPaging
     */
    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/nodes/{nodeId}/versions/{versionId}")
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<VersionRepresentation> getVersionCall(@PathParam("nodeId") String nodeId,
                                                       @PathParam("versionId") String versionId,
                                                       @QueryParam(APIConstants.SKIP_COUNT_VALUE) Integer skipCount,
                                                       @QueryParam(APIConstants.MAX_ITEMS_VALUE) Integer maxItems,
                                                       @QueryParam(APIConstants.INCLUDE_VALUE) IncludeParam include,
                                                       @QueryParam(APIConstants.FIELDS_VALUE) FieldsParam fields);

    /**
     * Note: this endpoint is available in Alfresco 5.2 and newer versions.
     * Attempts to revert the version identified by **versionId** and **nodeId**
     * to the live node. If the node is successfully reverted then the content
     * and metadata for that versioned node will be promoted to the live node
     * and a new version will appear in the version history.
     *
     * @param nodeId     The identifier of a node. (required)
     * @param versionId  The identifier of a version, ie. version label, within
     *                   the version history of a node. (required)
     * @param revertBody Optionally, specify a version comment and whether this
     *                   should be a major version, or not.
     */
    @POST
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/nodes/{nodeId}/versions/{versionId}/revert")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    VersionRepresentation revertVersionCall(@PathParam("nodeId") String nodeId,
                                            @PathParam("versionId") String versionId,
                                            RevertBody revertBody);

    /**
     * Note: this endpoint is available in Alfresco 5.2 and newer versions.
     * Attempts to revert the version identified by **versionId** and **nodeId**
     * to the live node. If the node is successfully reverted then the content
     * and metadata for that versioned node will be promoted to the live node
     * and a new version will appear in the version history.
     *
     * @param nodeId     The identifier of a node. (required)
     * @param versionId  The identifier of a version, ie. version label, within
     *                   the version history of a node. (required)
     * @param revertBody Optionally, specify a version comment and whether this
     *                   should be a major version, or not.
     * @param fields     A list of field names. You can use this parameter to
     *                   restrict the fields returned within a response if, for
     *                   example, you want to save on overall bandwidth. The list
     *                   applies to a returned individual entity or entries within a
     *                   collection. If the API method also supports the **include**
     *                   parameter, then the fields specified in the **include**
     *                   parameter are returned in addition to those specified in the
     *                   **fields** parameter. (optional)
     */
    @POST
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/nodes/{nodeId}/versions/{versionId}/revert")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    VersionRepresentation revertVersionCall(@PathParam("nodeId") String nodeId,
                                            @PathParam("versionId") String versionId,
                                            RevertBody revertBody,
                                            @QueryParam(APIConstants.FIELDS_VALUE) FieldsParam fields);

    /**
     * Delete a version **Note:** this endpoint is available in Alfresco 5.2 and
     * newer versions. Delete the version identified by **versionId** and
     * **nodeId*. If the version is successfully deleted then the content and
     * metadata for that versioned node will be deleted and will no longer
     * appear in the version history. This operation cannot be undone. If the
     * most recent version is deleted the live node will revert to the next most
     * recent version. We currently do not allow the last version to be deleted.
     * If you wish to clear the history then you can remove the
     * \&quot;cm:versionable\&quot; aspect (via update node) which will also
     * disable versioning. In this case, you can re-enable versioning by adding
     * back the \&quot;cm:versionable\&quot; aspect or using the version params
     * (majorVersion and comment) on a subsequent file content update.
     *
     * @param nodeId    The identifier of a node. (required)
     * @param versionId The identifier of a version, ie. version label, within
     *                  the version history of a node. (required)
     */
    @DELETE
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/nodes/{nodeId}/versions/{versionId}")
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<VersionRepresentation> deleteVersionCall(@PathParam("nodeId") String nodeId,
                                                          @PathParam("versionId") String versionId);

    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/nodes/{nodeId}/versions/{versionId}/content")
    Response getNodeVersionContent(@PathParam("nodeId") String nodeId,
                                   @PathParam("versionId") String versionId,
                                   @QueryParam("attachment") Boolean attachment);
}
