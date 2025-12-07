/*
 *   Copyright (C) 2005-2016 Alfresco Software Limited.
 *
 *   This file is part of Alfresco Java Client.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package net.fina.ecm.alfresco.api.core;


import net.fina.ecm.alfresco.api.common.constant.APIConstants;
import net.fina.ecm.alfresco.api.common.constant.PublicAPIConstant;
import net.fina.ecm.alfresco.api.common.representation.ResultPaging;
import net.fina.ecm.alfresco.api.common.parameters.FieldsParam;
import net.fina.ecm.alfresco.api.common.parameters.IncludeParam;
import net.fina.ecm.alfresco.api.common.parameters.OrderByParam;
import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import net.fina.ecm.alfresco.api.core.model.representation.PersonRepresentation;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

public interface QueriesAPI
{
    // ///////////////////////////////////////////////////////////////////////////
    // SEARCH PEOPLE
    // ///////////////////////////////////////////////////////////////////////////
    /**
     * Live search for people Returns a list of people that match the given
     * search criteria. The search term is used to look for matches against
     * person id, firstname and lastname. The search term must contain a minimum
     * of 2 alphanumeric characters and can optionally use '*' for wildcard
     * matching
     *
     * @param term The term to search for. (required)
     * @return ResultPaging<PersonRepresentation>
     */
    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/queries/people")
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<PersonRepresentation> findPeople(@QueryParam("term") String term);


    /**
     * Live search for people Returns a list of people that match the given
     * search criteria. The search term is used to look for matches against
     * person id, firstname and lastname. The search term must contain a minimum
     * of 2 alphanumeric characters and can optionally use '*' for wildcard
     * matching
     *
     * @param term The term to search for. (required)
     * @param skipCount The number of entities that exist in the collection
     *            before those included in this list. (optional)
     * @param maxItems The maximum number of items to return in the list.
     *            (optional)
     * @param orderBy The list of results can be ordered by the following: * id
     *            * firstName * lastName (optional)
     * @param fields A list of field names. You can use this parameter to
     *            restrict the fields returned within a response if, for
     *            example, you want to save on overall bandwidth. The list
     *            applies to a returned individual entity or entries within a
     *            collection. If the API method also supports the **include**
     *            parameter, then the fields specified in the **include**
     *            parameter are returned in addition to those specified in the
     *            **fields** parameter. (optional)
     * @return ResultPaging<PersonRepresentation>
     */
    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/queries/people")
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<PersonRepresentation> findPeople(@QueryParam("term") String term,
                                                            @QueryParam(PublicAPIConstant.SKIP_COUNT_VALUE) Integer skipCount,
                                                            @QueryParam(PublicAPIConstant.MAX_ITEMS_VALUE) Integer maxItems,
                                                            @QueryParam(PublicAPIConstant.ORDER_BY_VALUE) OrderByParam orderBy,
                                                            @QueryParam(PublicAPIConstant.FIELDS_VALUE) FieldsParam fields);


    // ///////////////////////////////////////////////////////////////////////////
    // SEARCH NODE
    // ///////////////////////////////////////////////////////////////////////////
    /**
     * Live search for nodes Returns a list of nodes that match the given search
     * criteria. The search term is used to look for nodes that match against
     * name, title, description, full text content and tags. The search term -
     * must contain a minimum of 3 alphanumeric characters - allows
     * \&quot;quoted term\&quot; - can optionally use &#39;*&#39; for wildcard
     * matching By default, file and folder types will be searched unless a
     * specific type is provided as a QueryParam parameter. By default, the search
     * will be across the repository unless a specific root node id is provided
     * to start the search from.
     *
     * @param term The term to search for. (required)
     * @return NodePaging
     */
    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/queries/nodes")
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<NodeRepresentation> findNodes(@QueryParam("term") String term);

    /**
     * Live search for nodes Returns a list of nodes that match the given search
     * criteria. The search term is used to look for nodes that match against
     * name, title, description, full text content and tags. The search term -
     * must contain a minimum of 3 alphanumeric characters - allows
     * \&quot;quoted term\&quot; - can optionally use &#39;*&#39; for wildcard
     * matching By default, file and folder types will be searched unless a
     * specific type is provided as a QueryParam parameter. By default, the search
     * will be across the repository unless a specific root node id is provided
     * to start the search from.
     *
     * @param term The term to search for. (required)
     * @param skipCount The number of entities that exist in the collection
     *            before those included in this list. (optional)
     * @param maxItems The maximum number of items to return in the list.
     *            (optional)
     * @param orderBy The list of results can be ordered by the following: *
     *            name * modifiedAt * createdAt (optional)
     * @return NodePaging
     */
    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/queries/nodes")
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<NodeRepresentation> findNodes(@QueryParam("term") String term,
                                                         @QueryParam(PublicAPIConstant.SKIP_COUNT_VALUE) Integer skipCount,
                                                         @QueryParam(PublicAPIConstant.MAX_ITEMS_VALUE) Integer maxItems,
                                                         @QueryParam(PublicAPIConstant.ORDER_BY_VALUE) OrderByParam orderBy);


    /**
     * Live search for nodes Returns a list of nodes that match the given search
     * criteria. The search term is used to look for nodes that match against
     * name, title, description, full text content and tags. The search term -
     * must contain a minimum of 3 alphanumeric characters - allows
     * \&quot;quoted term\&quot; - can optionally use &#39;*&#39; for wildcard
     * matching By default, file and folder types will be searched unless a
     * specific type is provided as a QueryParam parameter. By default, the search
     * will be across the repository unless a specific root node id is provided
     * to start the search from.
     *
     * @param term The term to search for. (required)
     * @param skipCount The number of entities that exist in the collection
     *            before those included in this list. (optional)
     * @param maxItems The maximum number of items to return in the list.
     *            (optional)
     * @param rootNodeId The id of the node to start the search from. Supports
     *            the aliases -my-, -root- and -shared-. (optional)
     * @param nodeType Restrict the returned results to only those of the given
     *            node type and it&#39;s sub-types (optional)
     * @param include Returns additional information about the node. The
     *            following optional fields can be requested: * properties *
     *            aspectNames * path * isLink * allowableOperations (optional)
     * @param orderBy The list of results can be ordered by the following: *
     *            name * modifiedAt * createdAt (optional)
     * @param fields A list of field names. You can use this parameter to
     *            restrict the fields returned within a response if, for
     *            example, you want to save on overall bandwidth. The list
     *            applies to a returned individual entity or entries within a
     *            collection. If the API method also supports the **include**
     *            parameter, then the fields specified in the **include**
     *            parameter are returned in addition to those specified in the
     *            **fields** parameter. (optional)
     * @return NodePaging
     */
    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/queries/nodes")
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<NodeRepresentation> findNodes(@QueryParam("term") String term,
                                                         @QueryParam(PublicAPIConstant.SKIP_COUNT_VALUE) Integer skipCount,
                                                         @QueryParam(PublicAPIConstant.MAX_ITEMS_VALUE) Integer maxItems, @QueryParam("rootNodeId") String rootNodeId,
                                                         @QueryParam("nodeType") String nodeType, @QueryParam(PublicAPIConstant.INCLUDE_VALUE) IncludeParam include,
                                                         @QueryParam(PublicAPIConstant.ORDER_BY_VALUE) OrderByParam orderBy,
                                                         @QueryParam(PublicAPIConstant.FIELDS_VALUE) FieldsParam fields);

    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/queries/nodes")
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<NodeRepresentation> findNodes(@QueryParam("term") String term,
                                               @QueryParam(PublicAPIConstant.INCLUDE_VALUE) IncludeParam include);

}
