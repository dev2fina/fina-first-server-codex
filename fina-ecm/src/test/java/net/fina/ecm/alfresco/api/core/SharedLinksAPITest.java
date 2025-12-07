package net.fina.ecm.alfresco.api.core;

import net.fina.ecm.alfresco.api.AlfrescoAPITestCase;
import net.fina.ecm.alfresco.api.common.parameters.IncludeParam;
import net.fina.ecm.alfresco.api.common.representation.ResultPaging;
import net.fina.ecm.alfresco.api.core.model.representation.SharedLinkRepresentation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

public class SharedLinksAPITest extends AlfrescoAPITestCase {

    @Before
    public void init() {
        client = getClient(AlfrescoAPITestCase.TEST_ENDPOINT, "admin","admin");
    }

    @After
    public void cleanUp() {
        client.getRestClient().client.close();
    }

    @Test
    public void listSHaredFilesTest() {
        ResultPaging<SharedLinkRepresentation> retulst = client.getSharedLinksAPI().
                listSharedLinks(0, 1, null, new IncludeParam(Arrays.asList("path", "allowableOperations", "permissions")), null);
        System.out.println(retulst.getObjects());
    }

    @Test
    public void deleteSharedLinkTest(){
        client.getSharedLinksAPI().deleteSharedLink("7EYdbpfbRzyubsnzzr4fuA");
    }
}
