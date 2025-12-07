package net.fina.ecm.alfresco.api.core;

import net.fina.ecm.alfresco.api.common.constant.APIConstants;
import net.fina.ecm.alfresco.api.core.model.body.ActionBodyCreate;
import net.fina.ecm.alfresco.api.core.model.representation.ActionRepresentation;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

public interface ActionsAPI {

    @POST
    @Path(APIConstants.CORE_PUBLIC_API_V1 + "/action-executions")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    ActionRepresentation executeActionCall(ActionBodyCreate actionBodyExec);

}
