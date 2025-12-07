package net.fina.ecm.alfresco.api.core;

import net.fina.ecm.alfresco.api.AlfrescoAPITestCase;
import net.fina.ecm.alfresco.api.common.parameters.IncludeParam;
import net.fina.ecm.alfresco.api.common.representation.ResultPaging;
import net.fina.ecm.alfresco.api.core.model.representation.DeletedNodeRepresentation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

public class TrashApiTest extends AlfrescoAPITestCase {
    @Before
    public void init() {
        client = getClient();
    }

    @After
    public void cleanUp() {
        client.getRestClient().client.close();
    }

    @Test
    public void testTrashedItems() {
        ResultPaging<DeletedNodeRepresentation> deletedNodes = client.getTrashAPI().listDeletedNodesCall(0, 100, new IncludeParam(Arrays.asList("path", "properties", "allowableOperations", "permissions", "aspectNames")));

        deletedNodes.getObjects().forEach(node -> {
            System.out.println(node.toString());
        });
    }
}
