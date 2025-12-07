package net.fina.ecm.alfresco.api.core;

import net.fina.ecm.alfresco.api.common.constant.APIConstants;
import net.fina.ecm.alfresco.api.core.model.body.BulkFileImportBodyInitiate;
import net.fina.ecm.alfresco.api.core.model.representation.xml.fileimport.bulk.BulkFilesystemImportStatus;
import org.jboss.resteasy.annotations.Form;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public interface FileImportAPI {

    @POST
    @Path(APIConstants.WEBSCRIPT_API + "/bulkfsimport/initiate")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    Response initiateBulkImport(@Form BulkFileImportBodyInitiate bulkFileImportBodyInitiate);

    @GET
    @Path(APIConstants.WEBSCRIPT_API + "/bulkfsimport/status")
    @Produces({MediaType.APPLICATION_XML, MediaType.TEXT_HTML})
    BulkFilesystemImportStatus getBulkImportStatus(@QueryParam("format") String format);

}
