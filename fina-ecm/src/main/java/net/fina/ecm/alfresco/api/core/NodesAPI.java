package net.fina.ecm.alfresco.api.core;

import net.fina.ecm.alfresco.api.common.constant.APIConstants;
import net.fina.ecm.alfresco.api.common.constant.PublicAPIConstant;
import net.fina.ecm.alfresco.api.common.parameters.FieldsParam;
import net.fina.ecm.alfresco.api.common.parameters.IncludeParam;
import net.fina.ecm.alfresco.api.common.parameters.OrderByParam;
import net.fina.ecm.alfresco.api.common.representation.ResultPaging;
import net.fina.ecm.alfresco.api.core.model.body.*;
import net.fina.ecm.alfresco.api.core.model.representation.AssociationRepresentation;
import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import java.util.List;

public interface NodesAPI {

    /**
     * Get a node Get information for the node with identifier **nodeId**.
     *
     * @param nodeId The identifier of a node. You can also use one of these
     *               well-known aliases: * -my- * -shared- * -root- (required)
     * @return NodeRepresentation
     */
    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/nodes/{nodeId}")
    @Produces(MediaType.APPLICATION_JSON)
    NodeRepresentation getNodeCall(@PathParam("nodeId") String nodeId);

    /**
     * Get a node Get information for the node with identifier **nodeId**.
     *
     * @param nodeId       The identifier of a node. You can also use one of these
     *                     well-known aliases: * -my- * -shared- * -root- (required)
     * @param include      Returns additional information about the node. The
     *                     following optional fields can be requested: * path * isLink *
     *                     allowableOperations (optional)
     * @param relativePath If specified, returns information on the node
     *                     resolved by this path. The path is relative to the specified
     *                     **nodeId** (optional)
     * @param fields       A list of field names. You can use this parameter to
     *                     restrict the fields returned within a response if, for
     *                     example, you want to save on overall bandwidth. The list
     *                     applies to a returned individual entity or entries within a
     *                     collection. If the API method also supports the **include**
     *                     parameter, then the fields specified in the **include**
     *                     parameter are returned in addition to those specified in the
     *                     **fields** parameter. (optional)
     * @return NodeRepresentation
     */
    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/nodes/{nodeId}")
    @Produces(MediaType.APPLICATION_JSON)
    NodeRepresentation getNodeCall(@PathParam("nodeId") String nodeId,
                                   @QueryParam(APIConstants.INCLUDE_VALUE) String include,
                                   @QueryParam(APIConstants.RELATIVE_PATH_VALUE) String relativePath,
                                   @QueryParam(APIConstants.FIELDS_VALUE) List<String> fields);


    /**
     * Get node children Returns the children of the parent node with identifier
     * **nodeId**. Minimal information for each child is returned by default.
     * You can use the **include** parameter to return addtional information.
     * The list of child nodes includes primary children and also secondary
     * children, if any. You can use the **include** parameter
     * (include&#x3D;association) to return child association details for each
     * child, including the assocType and the isPrimary flag.
     *
     * @param nodeId The identifier of a node. You can also use one of these
     *               well-known aliases: * -my- * -shared- * -root- (required)
     * @return ResultPaging<NodeRepresentation>
     */
    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/nodes/{nodeId}/children")
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<NodeRepresentation> listNodeChildrenCall(@PathParam("nodeId") String nodeId);

    /**
     * Get node children Returns the children of the parent node with identifier
     * **nodeId**. Minimal information for each child is returned by default.
     * You can use the **include** parameter to return addtional information.
     * The list of child nodes includes primary children and also secondary
     * children, if any. You can use the **include** parameter
     * (include&#x3D;association) to return child association details for each
     * child, including the assocType and the isPrimary flag.
     *
     * @param nodeId    The identifier of a node. You can also use one of these
     *                  well-known aliases: * -my- * -shared- * -root- (required)
     * @param skipCount The number of entities that exist in the collection
     *                  before those included in this list. (optional)
     * @param maxItems  The maximum number of items to return in the list.
     *                  (optional)
     * @param orderBy   If not specified then default sort is for folders to be
     *                  sorted before files, and by ascending name i.e.
     *                  \&quot;orderBy&#x3D;isFolder DESC,name ASC\&quot;. This
     *                  default can be completely overridden by specifying a specific
     *                  orderBy consisting of one, two or three comma-separated list
     *                  of properties (with optional ASCending or DESCending), for
     *                  example, specifying “orderBy&#x3D;name DESC” would return a
     *                  mixed folder/file list. The following properties can be used
     *                  to order the results: * isFolder * name * mimeType * nodeType
     *                  * sizeInBytes * modifiedAt * createdAt * modifiedByUser *
     *                  createdByUser (optional)
     * @return ResultPaging<NodeRepresentation>
     */
    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/nodes/{nodeId}/children")
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<NodeRepresentation> listNodeChildrenCall(@PathParam("nodeId") String nodeId,
                                                          @QueryParam(APIConstants.SKIP_COUNT_VALUE) Integer skipCount,
                                                          @QueryParam(APIConstants.MAX_ITEMS_VALUE) Integer maxItems,
                                                          @QueryParam(APIConstants.ORDER_BY_VALUE) OrderByParam orderBy);

    /**
     * Get node children Returns the children of the parent node with identifier
     * **nodeId**. Minimal information for each child is returned by default.
     * You can use the **include** parameter to return addtional information.
     * The list of child nodes includes primary children and also secondary
     * children, if any. You can use the **include** parameter
     * (include&#x3D;association) to return child association details for each
     * child, including the assocType and the isPrimary flag.
     *
     * @param nodeId        The identifier of a node. You can also use one of these
     *                      well-known aliases: * -my- * -shared- * -root- (required)
     * @param skipCount     The number of entities that exist in the collection
     *                      before those included in this list. (optional)
     * @param maxItems      The maximum number of items to return in the list.
     *                      (optional)
     * @param orderBy       If not specified then default sort is for folders to be
     *                      sorted before files, and by ascending name i.e.
     *                      \&quot;orderBy&#x3D;isFolder DESC,name ASC\&quot;. This
     *                      default can be completely overridden by specifying a specific
     *                      orderBy consisting of one, two or three comma-separated list
     *                      of properties (with optional ASCending or DESCending), for
     *                      example, specifying “orderBy&#x3D;name DESC” would return a
     *                      mixed folder/file list. The following properties can be used
     *                      to order the results: * isFolder * name * mimeType * nodeType
     *                      * sizeInBytes * modifiedAt * createdAt * modifiedByUser *
     *                      createdByUser (optional)
     * @param where         Optionally filter the list. Here are some examples: *
     *                      where&#x3D;(isFolder&#x3D;true) *
     *                      where&#x3D;(isFile&#x3D;true) *
     *                      where&#x3D;(nodeType&#x3D;&#39;my:specialNodeType&#39;) *
     *                      where&#x3D;(nodeType&#x3D;&#39;my:specialNodeType&#39;
     *                      INCLUDESUBTYPES) * where&#x3D;(isPrimary&#x3D;true) *
     *                      where&#x3D;(assocType&#x3D;&#39;my:specialAssocType&#39;)
     *                      (optional)
     * @param include       Returns additional information about the node. The
     *                      following optional fields can be requested: * properties *
     *                      aspectNames * path * isLink * allowableOperations *
     *                      association (optional)
     * @param relativePath  Return information on children within the folder
     *                      resolved by this path (relative to specified nodeId as the
     *                      starting parent folder) (optional)
     * @param includeSource Also include \&quot;source\&quot; (in addition to
     *                      \&quot;entries\&quot;) with folder information on parent node
     *                      (either the specified parent \&quot;nodeId\&quot; or as
     *                      resolved by \&quot;relativePath\&quot;) (optional)
     * @param fields        A list of field names. You can use this parameter to
     *                      restrict the fields returned within a response if, for
     *                      example, you want to save on overall bandwidth. The list
     *                      applies to a returned individual entity or entries within a
     *                      collection. If the API method also supports the **include**
     *                      parameter, then the fields specified in the **include**
     *                      parameter are returned in addition to those specified in the
     *                      **fields** parameter. (optional)
     * @return ResultPaging<NodeRepresentation>
     */
    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/nodes/{nodeId}/children")
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<NodeRepresentation> listNodeChildrenCall(@PathParam("nodeId") String nodeId,
                                                          @QueryParam(APIConstants.SKIP_COUNT_VALUE) Integer skipCount,
                                                          @QueryParam(APIConstants.MAX_ITEMS_VALUE) Integer maxItems,
                                                          @QueryParam(APIConstants.ORDER_BY_VALUE) OrderByParam orderBy,
                                                          @QueryParam(APIConstants.WHERE_VALUE) String where,
                                                          @QueryParam(APIConstants.INCLUDE_VALUE) IncludeParam include,
                                                          @QueryParam(APIConstants.RELATIVE_PATH_VALUE) String relativePath,
                                                          @QueryParam(APIConstants.INCLUDE_SOURCE_VALUE) String includeSource,
                                                          @QueryParam(APIConstants.FIELDS_VALUE) FieldsParam fields);

    @POST
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/nodes/{nodeId}/children")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response createNodeCall(@PathParam("nodeId") String nodeId, NodeBodyCreate nodeBody);


    @POST
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/nodes/{nodeId}/children")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response createNodeCall(@PathParam("nodeId") String nodeId, NodeBodyCreate nodeBody,
                                      @QueryParam(APIConstants.AUTO_RENAME_VALUE) boolean autoRename,
                                      @QueryParam(APIConstants.INCLUDE_VALUE) IncludeParam include,
                                      @QueryParam(APIConstants.FIELDS_VALUE) FieldsParam fields);


    @POST
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/nodes/{nodeId}/children")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    Response createNodeCall(@PathParam("nodeId") String ndoeId, MultipartFormDataInput multipartForm);


    @POST
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/nodes/{nodeId}/children")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    Response createUploadNodeCall(@PathParam("nodeId") String nodeId, MultipartFormDataOutput output,
                                            @QueryParam(APIConstants.AUTO_RENAME_VALUE) boolean autoRename,
                                            @QueryParam(APIConstants.INCLUDE_VALUE) IncludeParam include,
                                            @QueryParam(APIConstants.FIELDS_VALUE) FieldsParam fields);


    /**
     * Update a node Updates the node with identifier **nodeId**. For example,
     * you can rename a file or folder: &#x60;&#x60;&#x60;JSON {
     * \&quot;name\&quot;:\&quot;My new name\&quot;, } &#x60;&#x60;&#x60; You
     * can also set or update one or more properties: &#x60;&#x60;&#x60;JSON {
     * \&quot;properties\&quot;: { \&quot;cm:title\&quot;:\&quot;Folder
     * title\&quot; } } &#x60;&#x60;&#x60; **Note:** if you want to add or
     * remove aspects, then you must use **GET /nodes/{nodeId}** first to get
     * the complete set of *aspectNames*. **Note:** Currently there is no
     * optimistic locking for updates, so they are applied in \&quot;last one
     * wins\&quot; order.
     *
     * @param nodeId   The identifier of a node. You can also use one of these
     *                 well-known aliases: * -my- * -shared- * -root- (required)
     * @param nodeBody The node information to update. (required)
     * @return NodeRepresentation
     */
    @PUT
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/nodes/{nodeId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response updateNodeCall(@PathParam("nodeId") String nodeId, NodeBodyUpdate nodeBody);

    /**
     * Update a node Updates the node with identifier **nodeId**. For example,
     * you can rename a file or folder: &#x60;&#x60;&#x60;JSON {
     * \&quot;name\&quot;:\&quot;My new name\&quot;, } &#x60;&#x60;&#x60; You
     * can also set or update one or more properties: &#x60;&#x60;&#x60;JSON {
     * \&quot;properties\&quot;: { \&quot;cm:title\&quot;:\&quot;Folder
     * title\&quot; } } &#x60;&#x60;&#x60; **Note:** if you want to add or
     * remove aspects, then you must use **GET /nodes/{nodeId}** first to get
     * the complete set of *aspectNames*. **Note:** Currently there is no
     * optimistic locking for updates, so they are applied in \&quot;last one
     * wins\&quot; order.
     *
     * @param nodeId   The identifier of a node. You can also use one of these
     *                 well-known aliases: * -my- * -shared- * -root- (required)
     * @param nodeBody The node information to update. (required)
     * @param include  Returns additional information about the node. The
     *                 following optional fields can be requested: * path * isLink *
     *                 allowableOperations (optional)
     * @param fields   A list of field names. You can use this parameter to
     *                 restrict the fields returned within a response if, for
     *                 example, you want to save on overall bandwidth. The list
     *                 applies to a returned individual entity or entries within a
     *                 collection. If the API method also supports the **include**
     *                 parameter, then the fields specified in the **include**
     *                 parameter are returned in addition to those specified in the
     *                 **fields** parameter. (optional)
     * @return NodeRepresentation
     */
    @PUT
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/nodes/{nodeId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response updateNodeCall(@PathParam("nodeId") String nodeId, NodeBodyUpdate nodeBody,
                                      @QueryParam(APIConstants.INCLUDE_VALUE) IncludeParam include,
                                      @QueryParam(APIConstants.FIELDS_VALUE) FieldsParam fields);


    @PUT
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/nodes/{nodeId}/content")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.APPLICATION_JSON)
    NodeRepresentation updateUploadNodeCall(@PathParam("nodeId") String nodeId, StreamingOutput output,
                                            @QueryParam(APIConstants.MAJOR_VERSION_VALUE) boolean majorVersion,
                                            @QueryParam(APIConstants.INCLUDE_VALUE) IncludeParam include,
                                            @QueryParam(APIConstants.FIELDS_VALUE) FieldsParam fields);

    /**
     * Delete a node Deletes the node with identifier **nodeId**. If the
     * **nodeId** is a folder, then its children are also deleted. Deleted nodes
     * moveNodeCall to the trashcan unless the **permanent** query parameter is
     * true, and the current user is the owner or an admin. Deleting a node
     * removes it from it&#39;s primary parent and also from any other secondary
     * parents. Also, peer association are removed, where the deleted node is
     * either a source or target. The same also applies recursively to any
     * hierarchy of primary children of the deleted node. It should be noted
     * that if the node is not permanently deleted and later successfully
     * restored to it&#39;s former primary parent, then only the primary child
     * association will be restored, including recursively for any primary
     * children. It should be noted that no other secondary child association or
     * peer association will be restored, for any of the nodes within the
     * primary parent-child hierarchy of restored nodes, irrespective of whether
     * these association were to nodes within or outside of the restored
     * hierarchy.
     *
     * @param nodeId The identifier of a node. (required)
     */
    @DELETE
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/nodes/{nodeId}")
    Response deleteNodeCall(@PathParam("nodeId") String nodeId);

    /**
     * Delete a node Deletes the node with identifier **nodeId**. If the
     * **nodeId** is a folder, then its children are also deleted. Deleted nodes
     * moveNodeCall to the trashcan unless the **permanent** query parameter is
     * true, and the current user is the owner or an admin. Deleting a node
     * removes it from it&#39;s primary parent and also from any other secondary
     * parents. Also, peer association are removed, where the deleted node is
     * either a source or target. The same also applies recursively to any
     * hierarchy of primary children of the deleted node. It should be noted
     * that if the node is not permanently deleted and later successfully
     * restored to it&#39;s former primary parent, then only the primary child
     * association will be restored, including recursively for any primary
     * children. It should be noted that no other secondary child association or
     * peer association will be restored, for any of the nodes within the
     * primary parent-child hierarchy of restored nodes, irrespective of whether
     * these association were to nodes within or outside of the restored
     * hierarchy.
     *
     * @param nodeId    The identifier of a node. (required)
     * @param permanent If **true** then the node is deleted permanently,
     *                  without it moving to the trashcan. You must be the owner or an
     *                  admin to permanently delete the node. (optional, default to
     *                  false)
     */
    @DELETE
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/nodes/{nodeId}")
    Response deleteNodeCall(@PathParam("nodeId") String nodeId,
                        @QueryParam(APIConstants.PERMANENT_VALUE) Boolean permanent);

    /**
     * List parents Returns a list of parent nodes that point to (ie. are
     * associated with) the current child **nodeId**. This inclues both the
     * primary parent and also secondary parents, if any.
     *
     * @param nodeId The identifier of a node. (required)
     * @return ApiResponse&lt;NodeAssocPaging&gt;
     */
    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/nodes/{nodeId}/parents")
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<NodeRepresentation> listParentsCall(@PathParam("nodeId") String nodeId);

    /**
     * List parents Returns a list of parent nodes that point to (ie. are
     * associated with) the current child **nodeId**. This inclues both the
     * primary parent and also secondary parents, if any.
     *
     * @param nodeId    The identifier of a node. (required)
     * @param where     Optionally filter the list by assocType and/or isPrimary.
     *                  Here are some examples: *
     *                  where&#x3D;(assocType&#x3D;&#39;my:specialAssocType&#39;) *
     *                  where&#x3D;(isPrimary&#x3D;true) *
     *                  where&#x3D;(isPrimary&#x3D;false and
     *                  assocType&#x3D;&#39;my:specialAssocType&#39;) (optional)
     * @param include   Returns additional information about the node. The
     *                  following optional fields can be requested: * properties *
     *                  aspectNames * path * isLink * allowableOperations (optional)
     * @param skipCount The number of entities that exist in the collection
     *                  before those included in this list. (optional)
     * @param maxItems  The maximum number of items to return in the list.
     *                  (optional)
     * @param fields    A list of field names. You can use this parameter to
     *                  restrict the fields returned within a response if, for
     *                  example, you want to save on overall bandwidth. The list
     *                  applies to a returned individual entity or entries within a
     *                  collection. If the API method also supports the **include**
     *                  parameter, then the fields specified in the **include**
     *                  parameter are returned in addition to those specified in the
     *                  **fields** parameter. (optional)
     * @return ApiResponse&lt;NodeAssocPaging&gt;
     */
    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/nodes/{nodeId}/parents")
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<NodeRepresentation> listParentsCall(@PathParam("nodeId") String nodeId,
                                                     @QueryParam(APIConstants.SKIP_COUNT_VALUE) Integer skipCount,
                                                     @QueryParam(APIConstants.MAX_ITEMS_VALUE) Integer maxItems,
                                                     @QueryParam(APIConstants.WHERE_VALUE) String where,
                                                     @QueryParam(APIConstants.INCLUDE_VALUE) IncludeParam include,
                                                     @QueryParam(APIConstants.FIELDS_VALUE) FieldsParam fields);


    /**
     * List node association Returns a list of target nodes that are pointed to
     * (ie. are associated with) the current source **nodeId**.
     *
     * @param nodeId The identifier of a source node. (required)
     * @return NodeAssocPaging
     */
    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/nodes/{nodeId}/targets")
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<NodeRepresentation> listTargetAssociationsCall(@PathParam("nodeId") String nodeId);

    /**
     * List node association Returns a list of target nodes that are pointed to
     * (ie. are associated with) the current source **nodeId**.
     *
     * @param nodeId  The identifier of a source node. (required)
     * @param where   Optionally filter the list by assocType. Here&#39;s an
     *                example: *
     *                where&#x3D;(assocType&#x3D;&#39;my:specialAssocType&#39;)
     *                (optional)
     * @param include Returns additional information about the node. The
     *                following optional fields can be requested: * properties *
     *                aspectNames * path * isLink * allowableOperations (optional)
     * @param fields  A list of field names. You can use this parameter to
     *                restrict the fields returned within a response if, for
     *                example, you want to save on overall bandwidth. The list
     *                applies to a returned individual entity or entries within a
     *                collection. If the API method also supports the **include**
     *                parameter, then the fields specified in the **include**
     *                parameter are returned in addition to those specified in the
     *                **fields** parameter. (optional)
     * @return NodeAssocPaging
     */
    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/nodes/{nodeId}/targets")
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<NodeRepresentation> listTargetAssociationsCall(@PathParam("nodeId") String nodeId,
                                                                @QueryParam(APIConstants.WHERE_VALUE) String where,
                                                                @QueryParam(APIConstants.INCLUDE_VALUE) IncludeParam include,
                                                                @QueryParam(APIConstants.FIELDS_VALUE) FieldsParam fields);

    /**
     * List node association Returns a list of source nodes that point to (ie.
     * are associated with) the current target **nodeId**.
     *
     * @param nodeId The identifier of a target node. (required)
     * @return NodeAssocPaging
     */
    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/nodes/{nodeId}/sources")
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<NodeRepresentation> listSourceAssociationsCall(@PathParam("nodeId") String nodeId);


    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/nodes/{nodeId}/content")
    Response getNodeContent(@PathParam("nodeId") String nodeId,
                            @QueryParam("attachment") Boolean attachment);

    /**
     * Add node association Add association, with given association type,
     * between source **nodeId** and target node.
     *
     * @param nodeId          The identifier of a source node. (required)
     * @param associationBody The target node id and assoc type. (required)
     * @return NodeAssocEntry
     */
    @POST
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/nodes/{nodeId}/targets")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    AssociationRepresentation createAssocationCall(@PathParam("nodeId") String nodeId,
                                                   AssociationBody associationBody,
                                                   @QueryParam(PublicAPIConstant.FIELDS_VALUE) FieldsParam fields);

    @POST
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/nodes/{nodeId}/targets")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<AssociationRepresentation> createAssocationCall(@PathParam("nodeId") String nodeId,
                                                                 List<AssociationBody> associationBodies,
                                                                 @QueryParam(PublicAPIConstant.FIELDS_VALUE) FieldsParam fields);

    /**
     * Remove node association(s) Remove association(s) from source **nodeId* to
     * target node for given association type. If association type is not
     * specified then multiple peer association, of any type, will be removed
     * specifically in the direction from source to target. It should be noted
     * that after removal of the peer association(s) from source to target, the
     * two nodes may still have peer association in the other direction.
     *
     * @param nodeId    The identifier of a source node. (required)
     * @param targetId  The identifier of a target node. (required)
     * @param assocType Restrict the delete to only those of the given
     *                  association type (optional)
     */
    @DELETE
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/nodes/{nodeId}/targets/{targetId}")
    void deleteAssocationCall(@PathParam("nodeId") String nodeId,
                              @PathParam("targetId") String targetId,
                              @QueryParam("assocType") String assocType);

    /**
     * Copy a node Copy the node **nodeId** to the parent folder node
     * **targetParentId**. The **targetParentId** is specified in the body body.
     * The new node has the same name as the source node unless you specify a
     * new **name** in the body body. If the source **nodeId** is a folder, then
     * all of its children are also copied.
     *
     * @param nodeId   The identifier of a node. You can also use one of these
     *                 well-known aliases: * -my- * -shared- * -root- (required)
     * @param copyBody The targetParentId and, optionally, a new name.
     *                 (required)
     * @return NodeRepresentation
     */
    @POST
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/nodes/{nodeId}/copy")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    NodeRepresentation copyNodeCall(@PathParam("nodeId") String nodeId, NodeBodyCopy copyBody);


    /**
     * Move a node Move the node **nodeId** to the parent folder node
     * **targetParentId**. in body body. The **targetParentId** is specified in
     * the in body body. The moved node retains its name unless you specify a
     * new **name** in the body body. If the source **nodeId** is a folder, then
     * all of its children are also moved. The moveNodeCall will effectively
     * change the primary parent
     *
     * @param nodeId   The identifier of a node. You can also use one of these
     *                 well-known aliases: * -my- * -shared- * -root- (required)
     * @param moveBody The targetParentId and, optionally, a new name.
     *                 (required)
     * @return ApiResponse&lt;NodeRepresentation&gt;
     */
    @POST
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/nodes/{nodeId}/move")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    NodeRepresentation moveNodeCall(@PathParam("nodeId") String nodeId, NodeBodyCopy moveBody);

    /**
     * Lock a node Places a lock on node **nodeId**. If **includeChildren** is
     * true, locks are also recursively placed on all of **nodeId**&#39;s
     * children. If any one of the child locks can&#39;t be taken then an
     * exception is raised and all locks canceled. The lock is owned by the
     * current user, and prevents other users or processes from making updates
     * to the node until the lock is released. If the **timeToExpire** is not
     * sent or is zero, then the lock never expires. Otherwise, the
     * **timeToExpire** is the number of seconds before the lock expires. When a
     * lock expires, the lock is released. If the node is already locked, and
     * the user is the lock owner, then the lock is renewed with the new
     * **timeToExpire**. By default, a lock is applied that allows the owner to
     * update, delete, and add children to the node. You can use **type** to
     * change the lock type to one of the following: * **FULL** no changes by
     * any user are allowed * **ALLOW_ADD_CHILDREN** children can be added but
     * the node itself cannot be updated or deleted by any user *
     * **ALLOW_OWNER_CHANGES** changes to the node can be made only by the lock
     * owner By default, a lock is persisted in the database. You can create a
     * volatile in-memory lock by setting the **lifetime** property to
     * EPHEMERAL.
     *
     * @param nodeId   The identifier of a node. (required)
     * @param lockBody Lock details. (required)
     * @return NodeRepresentation
     */
    @POST
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/nodes/{nodeId}/lock")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    NodeRepresentation lockNodeCall(@PathParam("nodeId") String nodeId, NodeBodyLock lockBody);

    /**
     * Lock a node Places a lock on node **nodeId**. If **includeChildren** is
     * true, locks are also recursively placed on all of **nodeId**&#39;s
     * children. If any one of the child locks can&#39;t be taken then an
     * exception is raised and all locks canceled. The lock is owned by the
     * current user, and prevents other users or processes from making updates
     * to the node until the lock is released. If the **timeToExpire** is not
     * sent or is zero, then the lock never expires. Otherwise, the
     * **timeToExpire** is the number of seconds before the lock expires. When a
     * lock expires, the lock is released. If the node is already locked, and
     * the user is the lock owner, then the lock is renewed with the new
     * **timeToExpire**. By default, a lock is applied that allows the owner to
     * update, delete, and add children to the node. You can use **type** to
     * change the lock type to one of the following: * **FULL** no changes by
     * any user are allowed * **ALLOW_ADD_CHILDREN** children can be added but
     * the node itself cannot be updated or deleted by any user *
     * **ALLOW_OWNER_CHANGES** changes to the node can be made only by the lock
     * owner By default, a lock is persisted in the database. You can create a
     * volatile in-memory lock by setting the **lifetime** property to
     * EPHEMERAL.
     *
     * @param nodeId   The identifier of a node. (required)
     * @param lockBody Lock details. (required)
     * @param include  Returns additional information about the node. The
     *                 following optional fields can be requested: *
     *                 allowableOperations * isLink * isLocked * path (optional)
     * @param fields   A list of field names. You can use this parameter to
     *                 restrict the fields returned within a response if, for
     *                 example, you want to save on overall bandwidth. The list
     *                 applies to a returned individual entity or entries within a
     *                 collection. If the API method also supports the **include**
     *                 parameter, then the fields specified in the **include**
     *                 parameter are returned in addition to those specified in the
     *                 **fields** parameter. (optional)
     * @return NodeRepresentation
     */
    @POST
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/nodes/{nodeId}/lock")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    NodeRepresentation lockNodeCall(@PathParam("nodeId") String nodeId, NodeBodyLock lockBody,
                                    @QueryParam(PublicAPIConstant.INCLUDE_VALUE) IncludeParam include,
                                    @QueryParam(PublicAPIConstant.FIELDS_VALUE) FieldsParam fields);

    /**
     * Unlock a node Removes a lock on node **nodeId**. If **includeChildren**
     * is true, all **nodeId&#39;s** children are also unlocked recursively. Any
     * children without locks are ignored, and the operation continues for all
     * other children. If the node or any of its children are currently
     * checked-out, then an error is returned. You can override this behaviour
     * by setting **allowCheckedOut**. The current user must be the owner of the
     * locks or have admin rights, otherwise an error is returned. If a lock on
     * the node, or on any child node if unlocking children, cannot be released,
     * then an error is returned.
     *
     * @param nodeId     The identifier of a node. (required)
     * @param unlockBody Unlock details. (required)
     * @return NodeRepresentation
     */
    @POST
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/nodes/{nodeId}/unlock")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    NodeRepresentation unlockNodeCall(@PathParam("nodeId") String nodeId, NodeBodyUnLock unlockBody);

    /**
     * Unlock a node Removes a lock on node **nodeId**. If **includeChildren**
     * is true, all **nodeId&#39;s** children are also unlocked recursively. Any
     * children without locks are ignored, and the operation continues for all
     * other children. If the node or any of its children are currently
     * checked-out, then an error is returned. You can override this behaviour
     * by setting **allowCheckedOut**. The current user must be the owner of the
     * locks or have admin rights, otherwise an error is returned. If a lock on
     * the node, or on any child node if unlocking children, cannot be released,
     * then an error is returned.
     *
     * @param nodeId     The identifier of a node. (required)
     * @param unlockBody Unlock details. (required)
     * @param include    Returns additional information about the node. The
     *                   following optional fields can be requested: *
     *                   allowableOperations * isLink * isLocked * path (optional)
     * @param fields     A list of field names. You can use this parameter to
     *                   restrict the fields returned within a response if, for
     *                   example, you want to save on overall bandwidth. The list
     *                   applies to a returned individual entity or entries within a
     *                   collection. If the API method also supports the **include**
     *                   parameter, then the fields specified in the **include**
     *                   parameter are returned in addition to those specified in the
     *                   **fields** parameter. (optional)
     * @return NodeRepresentation
     */
    @POST
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/nodes/{nodeId}/unlock")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    NodeRepresentation unlockNodeCall(@PathParam("nodeId") String nodeId, NodeBodyUnLock unlockBody,
                                      @QueryParam(PublicAPIConstant.INCLUDE_VALUE) IncludeParam include,
                                      @QueryParam(PublicAPIConstant.FIELDS_VALUE) FieldsParam fields);
}
