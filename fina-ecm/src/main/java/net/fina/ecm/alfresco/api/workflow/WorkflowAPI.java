package net.fina.ecm.alfresco.api.workflow;

import net.fina.ecm.alfresco.api.common.constant.APIConstants;
import net.fina.ecm.alfresco.api.common.parameters.OrderByParam;
import net.fina.ecm.alfresco.api.common.parameters.PropertiesParam;
import net.fina.ecm.alfresco.api.common.representation.ResultPaging;
import net.fina.ecm.alfresco.api.workflow.body.TaskItemBodyCreate;
import net.fina.ecm.alfresco.api.workflow.body.WorkflowProcessBodyCreate;
import net.fina.ecm.alfresco.api.workflow.model.*;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

public interface WorkflowAPI {

    @GET
    @Path(APIConstants.WORKFLOW_PUBLIC_API_V1 + "/processes")
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<WorkflowProcessRepresentation> getProcessesCall();

    @GET
    @Path(APIConstants.WORKFLOW_PUBLIC_API_V1 + "/processes")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    ResultPaging<WorkflowProcessRepresentation> getProcessesCall(@QueryParam(APIConstants.SKIP_COUNT_VALUE) Integer skipCount,
                                                                 @QueryParam(APIConstants.MAX_ITEMS_VALUE) Integer maxItems,
                                                                 @QueryParam(APIConstants.PROPERTIES_VALUE) PropertiesParam properties,
                                                                 @QueryParam(APIConstants.ORDER_BY_VALUE) OrderByParam orderBy,
                                                                 @QueryParam(APIConstants.WHERE_VALUE) String where);

    @GET
    @Path(APIConstants.WORKFLOW_PUBLIC_API_V1 + "/processes/{processId}")
    @Produces(MediaType.APPLICATION_JSON)
    WorkflowProcessRepresentation getProcessesCall(@PathParam("processId") String processId);

    @DELETE
    @Path(APIConstants.WORKFLOW_PUBLIC_API_V1 + "/processes/{processId}")
    void deleteProcessCall(@PathParam("processId") String processId);

    @GET
    @Path(APIConstants.WORKFLOW_PUBLIC_API_V1 + "/processes/{processId}/variables")
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<VariableRepresentationSeparate> getProcessesVariablesCall(@PathParam("processId") String processId);

    @POST
    @Path(APIConstants.WORKFLOW_PUBLIC_API_V1 + "/processes/{processId}/variables")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<VariableRepresentationSeparate> createProcessesVariablesCall(@PathParam("processId") String processId, List<VariableRepresentation> variables);

    @POST
    @Path(APIConstants.WORKFLOW_PUBLIC_API_V1 + "/processes")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response createProcessCall(WorkflowProcessBodyCreate bodyCreate);

    @GET
    @Path(APIConstants.WORKFLOW_PUBLIC_API_V1 + "/processes/{processId}/items")
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<TaskItemRepresentation> getProcessItemsCall(@PathParam("processId") String processId,
                                                             @QueryParam(APIConstants.SKIP_COUNT_VALUE) Integer skipCount,
                                                             @QueryParam(APIConstants.MAX_ITEMS_VALUE) Integer maxItems,
                                                             @QueryParam(APIConstants.PROPERTIES_VALUE) PropertiesParam properties);

    @DELETE
    @Path(APIConstants.WORKFLOW_PUBLIC_API_V1 + "/processes/{processId}/items/{itemId}")
    void deleteProcessItemCall(@PathParam("processId") String processId, @PathParam("itemId") String itemId);

    @POST
    @Path(APIConstants.WORKFLOW_PUBLIC_API_V1 + "/processes/{processId}/items")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    ResultPaging<TaskItemRepresentation> createProcessItemsCall(@PathParam("processId") String processid, List<TaskItemBodyCreate> itemBody);

    @POST
    @Path(APIConstants.WORKFLOW_PUBLIC_API_V1 + "/processes/{processId}/items")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    TaskItemRepresentation createProcessItemCall(@PathParam("processId") String processId, TaskItemBodyCreate itemBody);

    @GET
    @Path(APIConstants.WORKFLOW_PUBLIC_API_V1 + "/processes/{processId}/tasks")
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<TaskRepresentation> getProcessActiveTasks(@PathParam("processId") String processId);

    @GET
    @Path(APIConstants.WORKFLOW_PUBLIC_API_V1 + "/process-definitions/{processDefinitionId}/start-form-model")
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<FormRepresentation> getProcessDefinitionStartFormModelCall(@PathParam(APIConstants.PROCESS_DEFINITION_ID) String processDefinitionId);

    @GET
    @Path(APIConstants.WORKFLOW_PUBLIC_API_V1 + "/process-definitions")
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<WorkflowProcessDefinitionRepresentation> getProcessDefinitionsCall(@QueryParam(APIConstants.SKIP_COUNT_VALUE) Integer skipCount,
                                                                                    @QueryParam(APIConstants.MAX_ITEMS_VALUE) Integer maxItems,
                                                                                    @QueryParam(APIConstants.PROPERTIES_VALUE) PropertiesParam properties,
                                                                                    @QueryParam(APIConstants.ORDER_BY_VALUE) OrderByParam orderBy,
                                                                                    @QueryParam(APIConstants.WHERE_VALUE) String where);

    @GET
    @Path(APIConstants.WORKFLOW_PUBLIC_API_V1 + "/process-definitions/{processDefinitionId}")
    @Produces(MediaType.APPLICATION_JSON)
    WorkflowProcessDefinitionRepresentation getProcessDefinitionsCall(@PathParam("processDefinitionId") String processDefinitionId);

    @GET
    @Path(APIConstants.WORKFLOW_PUBLIC_API_V1 + "/process-definitions/{processDefinitionId}/image")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    byte[] getProcessDefinitionImageCall(@PathParam("processDefinitionId") String processDefinitionId);


    @GET
    @Path(APIConstants.SERVICE_API + "/workflow-instances/{processId}/diagram")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    byte[] getProcessImageCall(@PathParam("processId") String processId);

    // tasks
    @GET
    @Path(APIConstants.WORKFLOW_PUBLIC_API_V1 + "/tasks")
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<TaskRepresentation> getTasksCall(@QueryParam(APIConstants.SKIP_COUNT_VALUE) Integer skipCount,
                                                  @QueryParam(APIConstants.MAX_ITEMS_VALUE) Integer maxItems,
                                                  @QueryParam(APIConstants.PROPERTIES_VALUE) PropertiesParam properties,
                                                  @QueryParam(APIConstants.ORDER_BY_VALUE) OrderByParam orderBy,
                                                  @QueryParam(APIConstants.WHERE_VALUE) String where);

    @GET
    @Path(APIConstants.WORKFLOW_PUBLIC_API_V1 + "/tasks/{taskId}/items")
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<TaskItemRepresentation> getTaskItemsCall(@PathParam("taskId") String taskId,
                                                          @QueryParam(APIConstants.SKIP_COUNT_VALUE) Integer skipCount,
                                                          @QueryParam(APIConstants.MAX_ITEMS_VALUE) Integer maxItems,
                                                          @QueryParam(APIConstants.PROPERTIES_VALUE) PropertiesParam properties);

    @DELETE
    @Path(APIConstants.WORKFLOW_PUBLIC_API_V1 + "/tasks/{taskId}/items/{itemId}")
    void deleteTaskItemCall(@PathParam("taskId") String taskId, @PathParam("itemId") String itemId);

    @POST
    @Path(APIConstants.WORKFLOW_PUBLIC_API_V1 + "/tasks/{taskId}/items")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    ResultPaging<TaskItemRepresentation> createTaskItemsCall(@PathParam("taskId") String taskId, List<TaskItemBodyCreate> itemBody);

    @POST
    @Path(APIConstants.WORKFLOW_PUBLIC_API_V1 + "/tasks/{taskId}/items")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    TaskItemRepresentation createTaskItemsCall(@PathParam("taskId") String taskId, TaskItemBodyCreate itemBody);

    @GET
    @Path(APIConstants.WORKFLOW_PUBLIC_API_V1 + "/tasks/{taskId}/task-form-model")
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<FormRepresentation> getTaskFormModelCall(@PathParam("taskId") String taskId,
                                                          @QueryParam(APIConstants.SKIP_COUNT_VALUE) Integer skipCount,
                                                          @QueryParam(APIConstants.MAX_ITEMS_VALUE) Integer maxItems,
                                                          @QueryParam(APIConstants.PROPERTIES_VALUE) PropertiesParam properties);

    @PUT
    @Path(APIConstants.WORKFLOW_PUBLIC_API_V1 + "/tasks/{taskId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    TaskRepresentation updateTaskStateCall(@PathParam("taskId") String taskId,
                                           @QueryParam("select") String select,
                                           Map<String, Object> taskBody);

    @POST
    @Path(APIConstants.WORKFLOW_PUBLIC_API_V1 + "/tasks/{taskId}/variables")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<VariableRepresentationSeparate> createOrUpdateTaskVariablesCall(@PathParam("taskId") String taskId, List<VariableRepresentation> variables);

    @POST
    @Path(APIConstants.WORKFLOW_PUBLIC_API_V1 + "/tasks/{taskId}/variables")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    VariableRepresentationSeparate createOrUpdateTaskVariablesCall(@PathParam("taskId") String taskId, VariableRepresentation variable);

    @POST
    @Path("/s/api/task/{taskId}/formprocessor")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Map<String, Object> addOrRemoveTaskVariableAssociationCall(@PathParam("taskId") String taskId, Map<String, Object> associations);

    @GET
    @Path(APIConstants.WORKFLOW_PUBLIC_API_V1 + "/tasks/{taskId}")
    @Produces(MediaType.APPLICATION_JSON)
    TaskRepresentation getTaskCall(@PathParam("taskId") String taskId,
                                   @QueryParam(APIConstants.PROPERTIES_VALUE) PropertiesParam properties);

    @GET
    @Path(APIConstants.WORKFLOW_PUBLIC_API_V1 + "/tasks/{taskId}/variables")
    @Produces(MediaType.APPLICATION_JSON)
    ResultPaging<VariableRepresentationSeparate> getTaskVariables(@PathParam("taskId") String taskId);

}
