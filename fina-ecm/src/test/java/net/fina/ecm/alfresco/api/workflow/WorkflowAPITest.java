package net.fina.ecm.alfresco.api.workflow;

import net.fina.ecm.alfresco.api.AlfrescoAPITestCase;
import net.fina.ecm.alfresco.api.common.parameters.OrderByParam;
import net.fina.ecm.alfresco.api.common.representation.ResultPaging;
import net.fina.ecm.alfresco.api.workflow.body.WorkflowProcessBodyCreate;
import net.fina.ecm.alfresco.api.workflow.model.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import jakarta.ws.rs.core.Response;
import java.util.*;

public class WorkflowAPITest extends AlfrescoAPITestCase {

    @Before
    public void init() {
        client = getClient();
    }

    @After
    public void cleanUp() {
        client.getRestClient().client.close();
    }

    @Test
    public void getProcessesCallTest() {
        String definitionkey = "activitiFiRegistrationReview";
        ResultPaging<WorkflowProcessRepresentation> workflowProcesses = client.getWorkflowAPI().getProcessesCall(null, null, null, null, " (processDefinitionKey='" + definitionkey + "')");

        List<WorkflowProcessRepresentation> entries = workflowProcesses.getObjects();
        System.out.println(entries);
    }

    @Test
    public void deleteProcessCallTest() {
        String[] processIds = {"12419", "12338"};
        for (String processId : processIds) {
            client.getWorkflowAPI().deleteProcessCall(processId);
        }
    }

    @Test
    public void getProcessesVariablesCallTest() {
        ResultPaging<VariableRepresentationSeparate> workflowProcessVariables = client.getWorkflowAPI().getProcessesVariablesCall("12015");

        List<VariableRepresentationSeparate> entries = workflowProcessVariables.getObjects();
        System.out.println(entries);
    }

    @Test
    public void createProcessCallTest() {

        String processDefinitionKey = "activitiFiRegistrationReview";

        Map<String, Object> variables = new HashMap<>();
        variables.put("bpm_assignee", "admin");
        variables.put("bpm_sendEMailNotifications", false);
        variables.put("bpm_workflowPriority", 0);
        variables.put("bpm_workflowDescription", "Create from test case");
        variables.put("fwf_fiCode", "BNK-12000");
        variables.put("fwf_fiName", "Bank 12000");
        variables.put("fwf_fiType", "BNK");
        variables.put("fwf_fiRegistry", "workspace://SpacesStore/c3aec48f-8d8d-4039-9731-97a040a22fcd");

        Response response = client.getWorkflowAPI().createProcessCall(new WorkflowProcessBodyCreate(processDefinitionKey, variables));
        WorkflowProcessRepresentation workflowProcessRepresentation = response.readEntity(WorkflowProcessRepresentation.class);
        System.out.println(workflowProcessRepresentation);
    }

    @Test
    public void getProcessDefinitionStartFormModelCallTest() {
        ResultPaging<FormRepresentation> result = client.getWorkflowAPI().getProcessDefinitionStartFormModelCall("activitiFiRegistrationReview:10:12712");
        System.out.println(result.getObjects());
    }

    @Test
    public void getProcessDefinitionsCallTest() {
        ResultPaging<WorkflowProcessDefinitionRepresentation> processDefinitions = client.getWorkflowAPI().getProcessDefinitionsCall(null, 1, null, new OrderByParam(Collections.singletonList("deploymentId desc")), "(key='activitiFiRegistrationReview')");
        System.out.println(processDefinitions.getObjects());
    }

    @Test
    public void getTasksCallTest() {
        ResultPaging<TaskRepresentation> tasks = client.getWorkflowAPI().getTasksCall(null, null, null, new OrderByParam(Collections.singletonList("id desc")), "(processId=213 and status='any' and includeTaskVariables=true)");
        System.out.println(tasks.getObjects());
    }

    @Test
    public void getTaskFormModelCallTest() {
        ResultPaging<FormRepresentation> taskForm = client.getWorkflowAPI().getTaskFormModelCall("267", null, null, null);
        System.out.println(taskForm.getObjects());
    }

    @Test
    public void createOrUpdateTaskVariablesCallTest() {

        VariableRepresentation vr1 = new VariableRepresentation();
        vr1.setName("bpm_status");
        vr1.setType("d:text");
        vr1.setValue("In Progress");
        vr1.setScope("local");

        VariableRepresentation vr2 = new VariableRepresentation();
        vr2.setName("bpm_percentComplete");
        vr2.setType("d:int");
        vr2.setValue(65);
        vr2.setScope("local");

        List<VariableRepresentation> variables = new ArrayList<>();
        variables.add(vr1);
        variables.add(vr2);

        ResultPaging<VariableRepresentationSeparate> result = client.getWorkflowAPI().createOrUpdateTaskVariablesCall("973", variables);
        System.out.println(result.getObjects());
    }

    @Test
    public void updateTaskStateCallTest() {
        Map<String, Object> taskBody = new HashMap<>();
        taskBody.put("state", "completed");

        TaskRepresentation taskRepresentation = client.getWorkflowAPI().updateTaskStateCall("457", "state", taskBody);
        System.out.println(taskRepresentation);
    }

    @Test
    public void createOrUpdateTaskVariableAssociationCallTest() {
        Map<String, Object> associations = new HashMap<>();
        associations.put("assoc_fwf_fiBranches_added", "workspace://SpacesStore/b3a650c6-9768-4879-915c-336d98e300fa");
        associations.put("assoc_fwf_fiBranches_removed", "workspace://SpacesStore/b3a650c6-9768-4879-915c-336d98e300fa");

        Map<String, Object> result = client.getWorkflowAPI().addOrRemoveTaskVariableAssociationCall("activiti$708", associations);
        System.out.println(result);
    }
}
