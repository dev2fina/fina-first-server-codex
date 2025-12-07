package net.fina.ecm.alfresco.api.core;

import net.fina.ecm.alfresco.api.common.constant.APIConstants;
import net.fina.ecm.alfresco.api.common.parameters.FieldsParam;
import net.fina.ecm.alfresco.api.common.representation.ResultPaging;
import net.fina.ecm.alfresco.api.core.model.body.SiteBodyCreate;
import net.fina.ecm.alfresco.api.core.model.body.SiteMembershipBodyCreate;
import net.fina.ecm.alfresco.api.core.model.body.SiteMembershipBodyUpdate;
import net.fina.ecm.alfresco.api.core.model.representation.*;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

public interface SitesAPI {

    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/sites")
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<SiteRepresentationEntry> listSites();

    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/sites")
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<SiteRepresentationEntry> listSites(@QueryParam(APIConstants.SKIP_COUNT_VALUE) Integer skipCount,
                                                    @QueryParam(APIConstants.MAX_ITEMS_VALUE) Integer maxItems,
                                                    @QueryParam(APIConstants.WHERE_VALUE) String where);


    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/people/{personId}/sites")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<SiteRoleRepresentation> listPersonSites(@PathParam("personId") String personId,
                                                         @QueryParam(APIConstants.SKIP_COUNT_VALUE) Integer skipCount,
                                                         @QueryParam(APIConstants.MAX_ITEMS_VALUE) Integer maxItems);


    @POST
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/sites")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    SiteRepresentation createSite(SiteBodyCreate siteBodyCreate);


    @DELETE
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/sites/{siteId}")
    void deleteSite(@PathParam("siteId") String siteId);

    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/sites/{siteId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    SiteRepresentationEntry getSite(@PathParam("siteId") String siteId);

    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/sites/{siteId}/members")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<SiteMemberRepresentation> listSiteMembershipsCall(@PathParam("siteId") String siteId,
                                                                   @QueryParam(APIConstants.SKIP_COUNT_VALUE) Integer skipCount,
                                                                   @QueryParam(APIConstants.MAX_ITEMS_VALUE) Integer maxItems,
                                                                   @QueryParam(APIConstants.FIELDS_VALUE) FieldsParam fields);


    @POST
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/sites/{siteId}/members")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    SiteMemberRepresentation createSiteMembershipCall(@PathParam("siteId") String siteId,
                                                      SiteMembershipBodyCreate siteMembershipBodyCreate,
                                                      @QueryParam(APIConstants.FIELDS_VALUE) FieldsParam fields);

    @PUT
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/sites/{siteId}/members/{personId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    SiteMemberRepresentation updateSiteMembershipCall(@PathParam("siteId") String siteId,
                                                      @PathParam("personId") String personId,
                                                      SiteMembershipBodyUpdate siteMemberRoleBody,
                                                      @QueryParam(APIConstants.FIELDS_VALUE) FieldsParam fields);

    @DELETE
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/sites/{siteId}/members/{personId}")
    void deleteSiteMembershipCall(@PathParam("siteId") String siteId, @PathParam("personId") String personId);

    @GET
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/people/{personId}/site-membership-requests")
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<SiteMembershipRequestRepresentation> listSiteMembershipRequestsForPersonCall(
            @PathParam("personId") String personId);


}
