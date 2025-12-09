package net.fina.ecm.alfresco.api.core;

import com.google.gson.internal.LinkedTreeMap;
import net.fina.ecm.alfresco.api.AlfrescoAPITestCase;
import net.fina.ecm.alfresco.api.common.constant.APIConstants;
import net.fina.ecm.alfresco.api.common.constant.ContentModel;
import net.fina.ecm.alfresco.api.common.representation.ResultPaging;
import net.fina.ecm.alfresco.api.core.model.body.NodeBodyCreate;
import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import net.fina.ecm.alfresco.api.core.model.representation.VersionRepresentation;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

public class VersionAPITest extends AlfrescoAPITestCase {

    @Before
    public void init() {
        client = getClient();
    }

    @After
    public void cleanUp() {
        client.getRestClient().client.close();
    }

    @Test
    public void listVersionHistoryCallTest() {

        //Create tmp Node with Properties and aspects
        LinkedTreeMap<String, Object> properties = new LinkedTreeMap<>();
        properties.put(ContentModel.PROP_TITLE, "Test Title Tmp");

        List<String> aspectNames = new ArrayList<>();
        aspectNames.add(ContentModel.ASPECT_VERSIONABLE);

        NodeBodyCreate tmpFileBody = new NodeBodyCreate("tmp-file.txt", ContentModel.TYPE_CONTENT, properties, aspectNames);
        Response response = client.getNodesAPI().createNodeCall(APIConstants.FOLDER_ROOT, tmpFileBody, true, null, null);
        NodeRepresentation tmpNode = response.readEntity(NodeRepresentation.class);
        // Retrieve history
        ResultPaging<VersionRepresentation> versionRepresentationResultPaging = client.getVersionAPI().listVersionHistoryCall(tmpNode.getId());

        Assert.assertNotNull(versionRepresentationResultPaging);
        Assert.assertEquals(1, versionRepresentationResultPaging.getCount());
        Assert.assertNotNull(versionRepresentationResultPaging.getObjects().get(0));
        Assert.assertEquals("1.0", versionRepresentationResultPaging.getObjects().get(0).getId());

        //Delete tmp node
        client.getNodesAPI().deleteNodeCall(tmpNode.getId(), true);
    }
}
