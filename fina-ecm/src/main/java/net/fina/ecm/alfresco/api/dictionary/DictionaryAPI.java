package net.fina.ecm.alfresco.api.dictionary;

import net.fina.ecm.alfresco.api.common.constant.APIConstants;
import net.fina.ecm.alfresco.api.dictionary.model.ClassAssociationRepresentation;
import net.fina.ecm.alfresco.api.dictionary.model.ClassPropertyRepresentation;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

public interface DictionaryAPI {

    @GET
    @Path(APIConstants.SERVICE_API + "/dictionary")
    @Produces(MediaType.APPLICATION_JSON)
    String getDictionaryCall();

    @GET
    @Path(APIConstants.SERVICE_API + "/classes")
    @Produces(MediaType.APPLICATION_JSON)
    String getClassesCall();

    @GET
    @Path(APIConstants.SERVICE_API + "/classes/{className}")
    @Produces(MediaType.APPLICATION_JSON)
    String getClassesCall(@PathParam("className") String className);

    @GET
    @Path(APIConstants.SERVICE_API + "/classes/{className}/properties")
    @Produces(MediaType.APPLICATION_JSON)
    List<ClassPropertyRepresentation> getClassPropertiesCall(@PathParam("className") String className);

    @GET
    @Path(APIConstants.SERVICE_API + "/classes/{className}/associations")
    @Produces(MediaType.APPLICATION_JSON)
    List<ClassAssociationRepresentation> getClassAssociationsCall(@PathParam("className") String className,
                                                                  @QueryParam("af") String af,
                                                                  @QueryParam("nsp") String nsp,
                                                                  @QueryParam("n") String n);
}
