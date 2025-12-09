package net.fina.ecm.alfresco.api.core;

import net.fina.ecm.alfresco.api.common.constant.APIConstants;
import net.fina.ecm.alfresco.api.core.model.representation.SiteMemberAuthorityRepresentation;
import net.fina.ecm.alfresco.api.core.model.representation.SiteMemberRole;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

public interface WebScriptApi {

    @GET
    @Path(APIConstants.WEBSCRIPT_API + "/fina/reportGeneration")
    @Produces(MediaType.APPLICATION_JSON)
    List<Map<String, Object>> getReportData(@QueryParam("reportName") String reportName,
                                            @QueryParam("searchQuery") String searchQuery,
                                            @QueryParam("filter") String filter);

    @GET
    @Path(APIConstants.WEBSCRIPT_API + "/fina/reportGeneration")
    @Produces(MediaType.APPLICATION_JSON)
    List<Map<String, Object>> getFiReportData(@QueryParam("reportName") String reportName,
                                              @QueryParam("languageCode") String languageCode,
                                              @QueryParam("fiCode") String fiCode);

    @GET
    @Path(APIConstants.WEBSCRIPT_API + "/fina/sendMessage")
    @Produces(MediaType.APPLICATION_JSON)
    Map<String, Object> sendMessage(@QueryParam("nodeId") String nodeId);

    @GET
    @Path(APIConstants.WEBSCRIPT_API + "/api/sites/{siteId}/memberships")
    @Produces(MediaType.APPLICATION_JSON)
    List<SiteMemberAuthorityRepresentation> listSiteMembersByType(@PathParam("siteId") String siteId,
                                                                  @QueryParam("authorityType") String authorityType);

    @POST
    @Path(APIConstants.WEBSCRIPT_API + "/api/sites/{siteId}/memberships")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    SiteMemberAuthorityRepresentation createSiteMemberGroup(@PathParam("siteId") String siteId,
                                                            SiteMemberRole siteMemberRole);

    @PUT
    @Path(APIConstants.WEBSCRIPT_API + "/api/sites/{siteId}/memberships")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    SiteMemberAuthorityRepresentation updateSiteMemberGroup(@PathParam("siteId") String siteId,
                                                            SiteMemberRole siteMemberRole);

    @DELETE
    @Path(APIConstants.WEBSCRIPT_API + "/api/sites/{siteId}/memberships/{groupId}")
    void removeSiteGroup(@PathParam("siteId") String siteId,
                         @PathParam("groupId") String groupId);

    @GET
    @Path(APIConstants.WEBSCRIPT_API + "/{webScriptPath}/templateGeneration")
    @Produces(MediaType.APPLICATION_JSON)
    String getTemplateName(@PathParam("webScriptPath") String webScriptPath,
                           @QueryParam("fiRegistryId") String registryId,
                           @QueryParam("documentType") String documentType,
                           @QueryParam("branchId") String branchId);

    @GET
    @Path(APIConstants.WEBSCRIPT_API + "/{webScriptPath}/templateData")
    @Produces(MediaType.APPLICATION_JSON)
    Map<String, Object> getTemplateData(@PathParam("webScriptPath") String webScriptPath,
                                        @QueryParam("fiRegistryId") String registryId,
                                        @QueryParam("documentType") String documentType,
                                        @QueryParam("branchId") String branchId,
                                        @QueryParam("templateKeys") String templateKeys,
                                        @QueryParam("timeZoneId") String timeZoneId);
}
