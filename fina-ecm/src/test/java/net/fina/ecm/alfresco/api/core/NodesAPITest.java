package net.fina.ecm.alfresco.api.core;

import com.google.gson.internal.LinkedTreeMap;
import net.fina.ecm.alfresco.api.AlfrescoAPITestCase;
import net.fina.ecm.alfresco.api.common.constant.APIConstants;
import net.fina.ecm.alfresco.api.common.constant.ContentModel;
import net.fina.ecm.alfresco.api.common.parameters.OrderByParam;
import net.fina.ecm.alfresco.api.common.representation.ResultPaging;
import net.fina.ecm.alfresco.api.core.model.body.AssociationBody;
import net.fina.ecm.alfresco.api.core.model.body.NodeBodyCreate;
import net.fina.ecm.alfresco.api.core.model.body.NodeBodyUpdate;
import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import net.fina.ecm.alfresco.api.search.body.QueryBody;
import net.fina.ecm.alfresco.api.search.body.RequestPagination;
import net.fina.ecm.alfresco.api.search.body.RequestQuery;
import net.fina.ecm.alfresco.api.search.model.ResultNodeRepresentation;
import net.fina.ecm.alfresco.api.search.model.ResultSetRepresentation;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class NodesAPITest extends AlfrescoAPITestCase {

    @Before
    public void init() {
        client = getClient(AlfrescoAPITestCase.TEST_ENDPOINT,AlfrescoAPITestCase.TEST_USERHASHSALT);
    }

    @After
    public void cleanUp() {
        client.getRestClient().client.close();
    }

    @Test
    public void createFiTypeTest() {
        TreeMap<String, Object> properties = new TreeMap<>();
        properties.put("fina:fiTypeCode", "BNK");
        properties.put("fina:fiTypeDescription", "BANKS");

        // create
        properties.put(APIConstants.PROP_AUTO_VERSION_ON_UPDATE_PROPS, true);
        //String fiTypeRootFolderPath = AlfrescoConfiguration.get().getAlfrescoProperty("fiTypeRootFolderPath");
        NodeRepresentation fiTypeRootNode = client.getNodesAPI().getNodeCall(APIConstants.FOLDER_ROOT, null, "fina2first/FI Types", null);

        NodeBodyCreate nodeBodyCreate = new NodeBodyCreate(UUID.randomUUID().toString(), "fina:fiType", properties, null);
        NodeRepresentation fiTypeNode = client.getNodesAPI().createNodeCall(fiTypeRootNode.getId(), nodeBodyCreate, true, null, null).readEntity(NodeRepresentation.class);

        System.out.println(fiTypeNode);
    }

    @Test
    public void createQuestionnaire() {

        // create group
        TreeMap<String, Object> groupProperties = new TreeMap<>();
        groupProperties.put("fina:questionnaireGroupCode", "Group 1");
        groupProperties.put("fina:questionnaireGroupDescription", "Group 1 Desc");
        groupProperties.put(APIConstants.PROP_AUTO_VERSION_ON_UPDATE_PROPS, true);

        NodeRepresentation groupRootNode = client.getNodesAPI().getNodeCall(APIConstants.FOLDER_ROOT, null, "fina2first/Questionnaire/Groups", null);
        NodeBodyCreate groupNodeBodyCreate = new NodeBodyCreate(UUID.randomUUID().toString(), "fina:questionnaireGroup", groupProperties, null);
        NodeRepresentation groupNode = client.getNodesAPI().createNodeCall(groupRootNode.getId(), groupNodeBodyCreate, true, null, null).readEntity(NodeRepresentation.class);

        // create fi type
        TreeMap<String, Object> fiTypeProperties = new TreeMap<>();
        fiTypeProperties.put("fina:fiTypeCode", "NBG");
        fiTypeProperties.put("fina:fiTypeDescription", "National Bank of Georgia");
        fiTypeProperties.put(APIConstants.PROP_AUTO_VERSION_ON_UPDATE_PROPS, true);

        NodeRepresentation fiTypeRootNode = client.getNodesAPI().getNodeCall(APIConstants.FOLDER_ROOT, null, "fina2first/FI Types", null);
        NodeBodyCreate fiTypeNodeBodyCreate = new NodeBodyCreate(UUID.randomUUID().toString(), "fina:fiType", fiTypeProperties, null);
        NodeRepresentation fiTypeNode = client.getNodesAPI().createNodeCall(fiTypeRootNode.getId(), fiTypeNodeBodyCreate, true, null, null).readEntity(NodeRepresentation.class);

        // create questionnaire
        TreeMap<String, Object> questionnaireProperties = new TreeMap<>();
        questionnaireProperties.put("fina:questionnaireQuestion", "Question 1");
        questionnaireProperties.put(APIConstants.PROP_AUTO_VERSION_ON_UPDATE_PROPS, true);

        NodeRepresentation questionnaireRootNode = client.getNodesAPI().getNodeCall(APIConstants.FOLDER_ROOT, null, "fina2first/Questionnaire", null);
        NodeBodyCreate questionnaireNodeBodyCreate = new NodeBodyCreate(UUID.randomUUID().toString(), "fina:questionnaire", questionnaireProperties, null);

        // create associations
        AssociationBody associationFiTypeBody = new AssociationBody();
        associationFiTypeBody.setAssocType("fina:questionnaireAssocFiType");
        associationFiTypeBody.setTargetId(fiTypeNode.getId());

        AssociationBody associationGroupBody = new AssociationBody();
        associationGroupBody.setAssocType("fina:questionnaireAssocGroup");
        associationGroupBody.setTargetId(groupNode.getId());

        List<AssociationBody> associationBodyList = new ArrayList<>();
        associationBodyList.add(associationFiTypeBody);
        associationBodyList.add(associationGroupBody);

        //ResultPaging<AssociationRepresentation> x = client.getNodesAPI().createAssocationCall(questionnaireNode.getId(), associationBodyList, null);
        //System.out.println(x.getObjects());

        questionnaireNodeBodyCreate.setTargets(associationBodyList);
        NodeRepresentation questionnaireNode = client.getNodesAPI().createNodeCall(questionnaireRootNode.getId(), questionnaireNodeBodyCreate, true, null, null).readEntity(NodeRepresentation.class);

    }

    @Test
    public void getNodeCallTest() {
        NodeRepresentation node = client.getNodesAPI().getNodeCall(APIConstants.FOLDER_ROOT);

        System.out.println(node);

        Assert.assertEquals("Company Home", node.getName());
        Assert.assertNotNull("Node cannot be null!", node);
    }

    @Test
    public void listNodeChildrenCallTest() {
        ResultPaging<NodeRepresentation> nodes = client.getNodesAPI().listNodeChildrenCall(APIConstants.FOLDER_ROOT);

        Assert.assertNotNull(nodes);
        Assert.assertNotEquals(nodes.getCount(), 0);
        System.out.println("Number of nodes: " + nodes.getCount());

        List<NodeRepresentation> resultNodes = nodes.getObjects();
        for (int i = 0; i < resultNodes.size(); i++) {
            NodeRepresentation nodeRepresentation = resultNodes.get(i);
            Assert.assertNotNull(nodeRepresentation);
            System.out.println(" ========= node: " + i + " ========= ");
            System.out.println(nodeRepresentation);
            System.out.println(" ================================ ");
        }

        // Retrieve 5 children
        nodes = client.getNodesAPI().listNodeChildrenCall(APIConstants.FOLDER_ROOT, 0, 5, new OrderByParam(Collections.singletonList("name ASC")));
        Assert.assertEquals(nodes.getCount(), 5);

        // Retrieve Sites folder only
        nodes = client.getNodesAPI().listNodeChildrenCall(APIConstants.FOLDER_ROOT, null, null, null, "(nodeType=st:sites)", null, null, null, null);
        Assert.assertEquals(nodes.getObjects().get(0).getName(), "Sites");
    }

    @Test
    public void createNodeCallTest() {

        // Create Test Folder
        NodeBodyCreate request = new NodeBodyCreate("TEST_FOLDER", "cm:folder");
        NodeRepresentation createdNode = client.getNodesAPI().createNodeCall(APIConstants.FOLDER_ROOT, request).readEntity(NodeRepresentation.class);

        Assert.assertNotNull(createdNode);
        client.getNodesAPI().deleteNodeCall(createdNode.getId());

        //Create Empty Node with Properties
        LinkedTreeMap<String, Object> properties = new LinkedTreeMap<>();
        properties.put(ContentModel.PROP_TITLE, "Test Title");

        NodeBodyCreate emptyFileBody = new NodeBodyCreate("my-file.txt", ContentModel.TYPE_CONTENT, properties, null);
        NodeRepresentation emptyNode = client.getNodesAPI().createNodeCall(APIConstants.FOLDER_ROOT, emptyFileBody, true, null, null).readEntity(NodeRepresentation.class);

        Assert.assertNotNull(emptyNode);
        client.getNodesAPI().deleteNodeCall(emptyNode.getId());
    }

    @Test
    public void updateNodeCallTest() {
        // Create Test Folder
        NodeBodyCreate request = new NodeBodyCreate("TEST_FOLDER", "cm:folder");
        NodeRepresentation createdNode = client.getNodesAPI().createNodeCall(APIConstants.FOLDER_ROOT, request).readEntity(NodeRepresentation.class);

        Assert.assertNotNull(createdNode);

        NodeBodyUpdate nodeBodyUpdate = new NodeBodyUpdate("TEST_FOLDER_V2");
        NodeRepresentation updatedNode = client.getNodesAPI().updateNodeCall(createdNode.getId(), nodeBodyUpdate).readEntity(NodeRepresentation.class);

        System.out.println(updatedNode);
        Assert.assertEquals(updatedNode.getName(), "TEST_FOLDER_V2");
        Assert.assertEquals(updatedNode.getId(), createdNode.getId());

        client.getNodesAPI().deleteNodeCall(createdNode.getId());
    }

    @Test
    public void deleteNodeCallTest() {

        // Create Test Folder
        NodeBodyCreate request = new NodeBodyCreate("TEST_FOLDER", "cm:folder");
        NodeRepresentation createdNode = client.getNodesAPI().createNodeCall(APIConstants.FOLDER_ROOT, request).readEntity(NodeRepresentation.class);

        Assert.assertNotNull(createdNode);

        // Delete Test Folder
        client.getNodesAPI().deleteNodeCall(createdNode.getId());
    }

    @Test
    public void test(){
        ResultPaging<NodeRepresentation> searchResult = client.getNodesAPI().listNodeChildrenCall("-root-", 0, 1000, null, null, null, "fina2first/Regional Structure", null, null);

        System.out.println(searchResult.getObjects());
    }

}
