
package net.fina.ecm.alfresco.api.core;

import net.fina.ecm.alfresco.api.common.constant.APIConstants;
import net.fina.ecm.alfresco.api.common.constant.PublicAPIConstant;
import net.fina.ecm.alfresco.api.common.parameters.FieldsParam;
import net.fina.ecm.alfresco.api.common.representation.ResultPaging;
import net.fina.ecm.alfresco.api.core.model.body.FavoriteBodyCreate;
import net.fina.ecm.alfresco.api.core.model.representation.FavoriteRepresentation;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

public interface FavoritesAPI {

    //    @GET(CoreConstant.CORE_PUBLIC_API_V1 + "/people/{personId}/favorites")
    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/people/{personId}/favorites")
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<FavoriteRepresentation> listFavoritesCall(@PathParam("personId") String personId);

    /**
     * Get favorites Returns a list of favorites for person **personId**. You
     * can use the &#x60;-me-&#x60; string in place of
     * &#x60;&lt;personId&gt;&#x60; to specify the currently authenticated user.
     * You can use the **where** parameter to restrict the list in the response
     * to entries of a specific kind. The **where** parameter takes a value. The
     * value is a single predicate that can include one or more **EXISTS**
     * conditions. The **EXISTS** condition uses a single operand to limit the
     * list to include entries that include that one property. The property
     * values are: * &#x60;target/file&#x60; * &#x60;target/folder&#x60; *
     * &#x60;target/site&#x60; For example, the following **where** parameter
     * restricts the returned list to the file favorites for a person:
     * &#x60;&#x60;&#x60;SQL (EXISTS(target/file)) &#x60;&#x60;&#x60; You can
     * specify more than one condition using **OR**. The predicate must be
     * enclosed in parentheses. For example, the following **where** parameter
     * restricts the returned list to the file and folder favorites for a
     * person: &#x60;&#x60;&#x60;SQL (EXISTS(target/file) OR
     * EXISTS(target/folder)) &#x60;&#x60;&#x60;
     *
     * @param personId  The identifier of a person. (required)
     * @param skipCount The number of entities that exist in the collection
     *                  before those included in this list. (optional)
     * @param maxItems  The maximum number of items to return in the list.
     *                  (optional)
     * @param where     A string to restrict the returned objects by using a
     *                  predicate. (optional)
     * @param fields    A list of field names. You can use this parameter to
     *                  restrict the fields returned within a response if, for
     *                  example, you want to save on overall bandwidth. The list
     *                  applies to a returned individual entity or entries within a
     *                  collection. If the API method also supports the **include**
     *                  parameter, then the fields specified in the **include**
     *                  parameter are returned in addition to those specified in the
     *                  **fields** parameter. (optional)
     * @return FavoritePaging
     */
    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/people/{personId}/favorites")
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<FavoriteRepresentation> listFavoritesCall(@PathParam("personId") String personId,
                                                           @QueryParam(PublicAPIConstant.SKIP_COUNT_VALUE) Integer skipCount,
                                                           @QueryParam(PublicAPIConstant.MAX_ITEMS_VALUE) Integer maxItems,
                                                           @QueryParam(PublicAPIConstant.WHERE_VALUE) String where,
                                                           @QueryParam(APIConstants.FIELDS_VALUE) List<String> fields);


    // ///////////////////////////////////////////////////////////////////////////
    // INFO
    // ///////////////////////////////////////////////////////////////////////////

    /**
     * Get a favorite Returns favorite **favoriteId** for person **personId**.
     * You can use the &#x60;-me-&#x60; string in place of
     * &#x60;&lt;personId&gt;&#x60; to specify the currently authenticated user.
     *
     * @param personId   The identifier of a person. (required)
     * @param favoriteId The identifier of a favorite. (required)
     * @return FavoriteRepresentation
     */
    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/people/{personId}/favorites/{favoriteId}")
    @Produces(MediaType.APPLICATION_JSON)
    FavoriteRepresentation getFavoriteCall(@PathParam("personId") String personId,
                                           @PathParam("favoriteId") String favoriteId);

    /**
     * Get a favorite Returns favorite **favoriteId** for person **personId**.
     * You can use the &#x60;-me-&#x60; string in place of
     * &#x60;&lt;personId&gt;&#x60; to specify the currently authenticated user.
     *
     * @param personId   The identifier of a person. (required)
     * @param favoriteId The identifier of a favorite. (required)
     * @param fields     A list of field names. You can use this parameter to
     *                   restrict the fields returned within a response if, for
     *                   example, you want to save on overall bandwidth. The list
     *                   applies to a returned individual entity or entries within a
     *                   collection. If the API method also supports the **include**
     *                   parameter, then the fields specified in the **include**
     *                   parameter are returned in addition to those specified in the
     *                   **fields** parameter. (optional)
     * @return FavoriteRepresentation
     */
    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/people/{personId}/favorites/{favoriteId}")
    @Produces(MediaType.APPLICATION_JSON)
    FavoriteRepresentation getFavoriteCall(@PathParam("personId") String personId,
                                           @PathParam("favoriteId") String favoriteId, @QueryParam(APIConstants.FIELDS_VALUE) List<String> fields);


    // ///////////////////////////////////////////////////////////////////////////
    // CREATE
    // ///////////////////////////////////////////////////////////////////////////

    /**
     * Add a favorite Favorite a **site**, **file**, or **folder** in the
     * repository. You can use the &#x60;-me-&#x60; string in place of
     * &#x60;&lt;personId&gt;&#x60; to specify the currently authenticated user.
     * **Note:** You can favorite more than one entity by specifying a list of
     * objects in the JSON body like this: &#x60;&#x60;&#x60;JSON [ {
     * \&quot;target\&quot;: { \&quot;file\&quot;: { \&quot;guid\&quot;:
     * \&quot;abcde-01234-....\&quot; } } }, { \&quot;target\&quot;: {
     * \&quot;file\&quot;: { \&quot;guid\&quot;: \&quot;abcde-09863-....\&quot;
     * } } }, ] &#x60;&#x60;&#x60; If you specify a list as input, then a
     * paginated list rather than an entry is returned in the response body. For
     * example: &#x60;&#x60;&#x60;JSON { \&quot;list\&quot;: {
     * \&quot;pagination\&quot;: { \&quot;count\&quot;: 2,
     * \&quot;hasMoreItems\&quot;: false, \&quot;totalItems\&quot;: 2,
     * \&quot;skipCount\&quot;: 0, \&quot;maxItems\&quot;: 100 },
     * \&quot;entries\&quot;: [ { \&quot;entry\&quot;: { ... } }, {
     * \&quot;entry\&quot;: { ... } } ] } } &#x60;&#x60;&#x60;
     *
     * @param personId           The identifier of a person. (required)
     * @param favoriteBodyCreate An object identifying the entity to be
     *                           favorited. The object consists of a single property which is
     *                           an object with the name &#x60;site&#x60;, &#x60;file&#x60;, or
     *                           &#x60;folder&#x60;. The content of that object is the
     *                           &#x60;guid&#x60; of the target entity. For example, to
     *                           favorite a file the following body would be used:
     *                           &#x60;&#x60;&#x60;JSON { \&quot;target\&quot;: {
     *                           \&quot;file\&quot;: { \&quot;guid\&quot;:
     *                           \&quot;abcde-01234-....\&quot; } } } &#x60;&#x60;&#x60;
     *                           (required)
     * @return FavoriteRepresentation
     */
    @POST
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/people/{personId}/favorites")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    FavoriteRepresentation createFavoriteCall(@PathParam("personId") String personId,
                                              FavoriteBodyCreate favoriteBodyCreate,
                                              @QueryParam(PublicAPIConstant.FIELDS_VALUE) FieldsParam fields);


    // ///////////////////////////////////////////////////////////////////////////
    // DELETE
    // ///////////////////////////////////////////////////////////////////////////

    /**
     * Delete a favorite Removes **favoriteId** as a favorite of person
     * **personId**. You can use the &#x60;-me-&#x60; string in place of
     * &#x60;&lt;personId&gt;&#x60; to specify the currently authenticated user.
     *
     * @param personId   The identifier of a person. (required)
     * @param favoriteId The identifier of a favorite. (required)
     */
    @DELETE
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/people/{personId}/favorites/{favoriteId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    void deleteFavoriteCall(@PathParam("personId") String personId, @PathParam("favoriteId") String favoriteId);

    @POST
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/people/{personId}/favorite-sites")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    FavoriteRepresentation createFavoriteSitesCall(@PathParam("personId") String personId,
                                                   FavoriteBodyCreate favoriteBodyCreate,
                                                   @QueryParam(PublicAPIConstant.FIELDS_VALUE) FieldsParam fields);

}
