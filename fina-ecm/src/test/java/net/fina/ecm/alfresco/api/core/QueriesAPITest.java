package net.fina.ecm.alfresco.api.core;

import net.fina.ecm.alfresco.api.AlfrescoAPITestCase;
import net.fina.ecm.alfresco.api.common.representation.ResultPaging;
import net.fina.ecm.alfresco.api.common.parameters.IncludeParam;
import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import net.fina.ecm.alfresco.api.core.model.representation.PersonRepresentation;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

public class QueriesAPITest extends AlfrescoAPITestCase {

    @Before
    public void init() {
        client = getClient();
    }

    @After
    public void cleanUp() {
        client.getRestClient().client.close();
    }


    @Test
    public void testFindNodes() {
        ResultPaging<NodeRepresentation> resultNodes = client.getQueriseAPI().findNodes("002 - Jackie Rustaveli", new IncludeParam(Collections.singletonList("properties")));

        System.out.println(resultNodes.getObjects().size());
    }

    @Test
    public void testFindPeople() {
        ResultPaging<PersonRepresentation> resultNodes = client.getQueriseAPI().findPeople("admidn");

        Assert.assertTrue("Person not found", resultNodes.getObjects().size() == 1);

        System.out.println(resultNodes.getObjects().get(0).toString());

    }


}