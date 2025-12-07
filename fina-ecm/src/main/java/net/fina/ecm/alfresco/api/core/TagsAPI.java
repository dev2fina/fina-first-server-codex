package net.fina.ecm.alfresco.api.core;

import net.fina.ecm.alfresco.api.common.constant.APIConstants;
import net.fina.ecm.alfresco.api.common.constant.PublicAPIConstant;
import net.fina.ecm.alfresco.api.common.parameters.FieldsParam;
import net.fina.ecm.alfresco.api.common.representation.ResultPaging;
import net.fina.ecm.alfresco.api.core.model.body.TagBody;
import net.fina.ecm.alfresco.api.core.model.representation.TagRepresentation;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

public interface TagsAPI {

    /**
     * Get tags Returns a list of tags for node **nodeId**.
     *
     * @param nodeId The identifier of a node. (required)
     * @return ResultPaging<TagRepresentation>
     */
    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/nodes/{nodeId}/tags")
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<TagRepresentation> listTagsForNodeCall(@PathParam("nodeId") String nodeId);

    /**
     * Get tags Returns a list of tags for node **nodeId**.
     *
     * @param nodeId    The identifier of a node. (required)
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
     * @return ResultPaging<TagRepresentation>
     */
    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/nodes/{nodeId}/tags")
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<TagRepresentation> listTagsForNodeCall(@PathParam("nodeId") String nodeId,
                                                        @QueryParam(PublicAPIConstant.SKIP_COUNT_VALUE) Integer skipCount,
                                                        @QueryParam(PublicAPIConstant.MAX_ITEMS_VALUE) Integer maxItems,
                                                        @QueryParam(PublicAPIConstant.FIELDS_VALUE) FieldsParam fields);


    /**
     * Get tags Returns a list of tags in this repository.
     *
     * @return ResultPaging<TagRepresentation>
     */
    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/tags")
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<TagRepresentation> listTagsCall();

    /**
     * Get tags Returns a list of tags in this repository.
     *
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
     * @return ResultPaging<TagRepresentation>
     */
    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/tags")
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<TagRepresentation> listTagsCall(@QueryParam(PublicAPIConstant.SKIP_COUNT_VALUE) Integer skipCount,
                                                 @QueryParam(PublicAPIConstant.MAX_ITEMS_VALUE) Integer maxItems,
                                                 @QueryParam(PublicAPIConstant.FIELDS_VALUE) FieldsParam fields);

    /**
     * Add a tag Adds a tag to the node **nodeId**. You specify the tag in a
     * JSON body like this: &#x60;&#x60;&#x60;JSON {
     * \&quot;tag\&quot;:\&quot;test-tag-1\&quot; } &#x60;&#x60;&#x60; **Note:**
     * You can create more than one tag by specifying a list of tags in the JSON
     * body like this: &#x60;&#x60;&#x60;JSON [ {
     * \&quot;tag\&quot;:\&quot;test-tag-1\&quot; }, {
     * \&quot;tag\&quot;:\&quot;test-tag-2\&quot; } ] &#x60;&#x60;&#x60; If you
     * specify a list as input, then a paginated list rather than an entry is
     * returned in the response body. For example: &#x60;&#x60;&#x60;JSON {
     * \&quot;list\&quot;: { \&quot;pagination\&quot;: { \&quot;count\&quot;: 2,
     * \&quot;hasMoreItems\&quot;: false, \&quot;totalItems\&quot;: 2,
     * \&quot;skipCount\&quot;: 0, \&quot;maxItems\&quot;: 100 },
     * \&quot;entries\&quot;: [ { \&quot;entry\&quot;: { ... } }, {
     * \&quot;entry\&quot;: { ... } } ] } } &#x60;&#x60;&#x60;
     *
     * @param nodeId  The identifier of a node. (required)
     * @param tagBody The new tag (required)
     * @return TagRepresentation
     */
    @POST
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/nodes/{nodeId}/tags")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    TagRepresentation createTagForNodeCall(@PathParam("nodeId") String nodeId, TagBody tagBody);

    /**
     * Add a tag Adds a tag to the node **nodeId**. You specify the tag in a
     * JSON body like this: &#x60;&#x60;&#x60;JSON {
     * \&quot;tag\&quot;:\&quot;test-tag-1\&quot; } &#x60;&#x60;&#x60; **Note:**
     * You can create more than one tag by specifying a list of tags in the JSON
     * body like this: &#x60;&#x60;&#x60;JSON [ {
     * \&quot;tag\&quot;:\&quot;test-tag-1\&quot; }, {
     * \&quot;tag\&quot;:\&quot;test-tag-2\&quot; } ] &#x60;&#x60;&#x60; If you
     * specify a list as input, then a paginated list rather than an entry is
     * returned in the response body. For example: &#x60;&#x60;&#x60;JSON {
     * \&quot;list\&quot;: { \&quot;pagination\&quot;: { \&quot;count\&quot;: 2,
     * \&quot;hasMoreItems\&quot;: false, \&quot;totalItems\&quot;: 2,
     * \&quot;skipCount\&quot;: 0, \&quot;maxItems\&quot;: 100 },
     * \&quot;entries\&quot;: [ { \&quot;entry\&quot;: { ... } }, {
     * \&quot;entry\&quot;: { ... } } ] } } &#x60;&#x60;&#x60;
     *
     * @param nodeId  The identifier of a node. (required)
     * @param tagBody The new tag (required)
     * @param fields  A list of field names. You can use this parameter to
     *                restrict the fields returned within a response if, for
     *                example, you want to save on overall bandwidth. The list
     *                applies to a returned individual entity or entries within a
     *                collection. If the API method also supports the **include**
     *                parameter, then the fields specified in the **include**
     *                parameter are returned in addition to those specified in the
     *                **fields** parameter. (optional)
     * @return TagRepresentation
     */

    @POST
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/nodes/{nodeId}/tags")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    TagRepresentation createTagForNodeCall(@PathParam("nodeId") String nodeId, TagBody tagBody,
                                           @QueryParam(PublicAPIConstant.FIELDS_VALUE) FieldsParam fields);

    /**
     * Add tags Adds a tag to the node **nodeId**. You specify the tag in a JSON
     * body like this: &#x60;&#x60;&#x60;JSON {
     * \&quot;tag\&quot;:\&quot;test-tag-1\&quot; } &#x60;&#x60;&#x60; **Note:**
     * You can create more than one tag by specifying a list of tags in the JSON
     * body like this: &#x60;&#x60;&#x60;JSON [ {
     * \&quot;tag\&quot;:\&quot;test-tag-1\&quot; }, {
     * \&quot;tag\&quot;:\&quot;test-tag-2\&quot; } ] &#x60;&#x60;&#x60; If you
     * specify a list as input, then a paginated list rather than an entry is
     * returned in the response body. For example: &#x60;&#x60;&#x60;JSON {
     * \&quot;list\&quot;: { \&quot;pagination\&quot;: { \&quot;count\&quot;: 2,
     * \&quot;hasMoreItems\&quot;: false, \&quot;totalItems\&quot;: 2,
     * \&quot;skipCount\&quot;: 0, \&quot;maxItems\&quot;: 100 },
     * \&quot;entries\&quot;: [ { \&quot;entry\&quot;: { ... } }, {
     * \&quot;entry\&quot;: { ... } } ] } } &#x60;&#x60;&#x60;
     *
     * @param nodeId  The identifier of a node. (required)
     * @param tagBody List of new tags (required)
     * @return TagRepresentation
     */
    @POST
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/nodes/{nodeId}/tags")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<TagRepresentation> createTagsForNodeCall(@PathParam("nodeId") String nodeId, TagBody[] tagBody);

    /**
     * Add tags Adds a tag to the node **nodeId**. You specify the tag in a JSON
     * body like this: &#x60;&#x60;&#x60;JSON {
     * \&quot;tag\&quot;:\&quot;test-tag-1\&quot; } &#x60;&#x60;&#x60; **Note:**
     * You can create more than one tag by specifying a list of tags in the JSON
     * body like this: &#x60;&#x60;&#x60;JSON [ {
     * \&quot;tag\&quot;:\&quot;test-tag-1\&quot; }, {
     * \&quot;tag\&quot;:\&quot;test-tag-2\&quot; } ] &#x60;&#x60;&#x60; If you
     * specify a list as input, then a paginated list rather than an entry is
     * returned in the response body. For example: &#x60;&#x60;&#x60;JSON {
     * \&quot;list\&quot;: { \&quot;pagination\&quot;: { \&quot;count\&quot;: 2,
     * \&quot;hasMoreItems\&quot;: false, \&quot;totalItems\&quot;: 2,
     * \&quot;skipCount\&quot;: 0, \&quot;maxItems\&quot;: 100 },
     * \&quot;entries\&quot;: [ { \&quot;entry\&quot;: { ... } }, {
     * \&quot;entry\&quot;: { ... } } ] } } &#x60;&#x60;&#x60;
     *
     * @param nodeId  The identifier of a node. (required)
     * @param tagBody List of new tags (required)
     * @param fields  A list of field names. You can use this parameter to
     *                restrict the fields returned within a response if, for
     *                example, you want to save on overall bandwidth. The list
     *                applies to a returned individual entity or entries within a
     *                collection. If the API method also supports the **include**
     *                parameter, then the fields specified in the **include**
     *                parameter are returned in addition to those specified in the
     *                **fields** parameter. (optional)
     * @return TagRepresentation
     */
    @POST
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/nodes/{nodeId}/tags")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<TagRepresentation> createTagsForNodeCall(@PathParam("nodeId") String nodeId, TagBody[] tagBody,
                                                          @QueryParam(PublicAPIConstant.FIELDS_VALUE) FieldsParam fields);

    /**
     * Get a tag Return a specific tag with **tagId**.
     *
     * @param tagId The identifier of a tag. (required)
     * @return TagRepresentation
     */
    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/tags/{tagId}")
    @Produces(MediaType.APPLICATION_JSON)
    TagRepresentation getTagCall(@PathParam("tagId") String tagId);

    /**
     * Get a tag Return a specific tag with **tagId**.
     *
     * @param tagId  The identifier of a tag. (required)
     * @param fields A list of field names. You can use this parameter to
     *               restrict the fields returned within a response if, for
     *               example, you want to save on overall bandwidth. The list
     *               applies to a returned individual entity or entries within a
     *               collection. If the API method also supports the **include**
     *               parameter, then the fields specified in the **include**
     *               parameter are returned in addition to those specified in the
     *               **fields** parameter. (optional)
     * @return TagRepresentation
     */
    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/tags/{tagId}")
    @Produces(MediaType.APPLICATION_JSON)
    TagRepresentation getTagCall(@PathParam("tagId") String tagId,
                                 @QueryParam(PublicAPIConstant.FIELDS_VALUE) FieldsParam fields);

    /**
     * Update a tag Updates the tag **tagId**.
     *
     * @param tagId   The identifier of a tag. (required)
     * @param tagBody The updated tag (required)
     * @return TagRepresentation
     */
    @POST
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/tags/{tagId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    TagRepresentation updateTagCall(@PathParam("tagId") String tagId, TagBody tagBody,
                                    @QueryParam(PublicAPIConstant.FIELDS_VALUE) FieldsParam fields);

    /**
     * Delete a tag Removes tag **tagId** from node **nodeId**.
     *
     * @param nodeId The identifier of a node. (required)
     * @param tagId  The identifier of a tag. (required)
     */
    @DELETE
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/nodes/{nodeId}/tags/{tagId}")
    void deleteTagFromNodeCall(@PathParam("nodeId") String nodeId, @PathParam("tagId") String tagId);
}
