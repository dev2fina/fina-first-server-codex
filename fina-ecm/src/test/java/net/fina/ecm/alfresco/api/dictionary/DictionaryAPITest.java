package net.fina.ecm.alfresco.api.dictionary;

import net.fina.ecm.alfresco.api.AlfrescoAPITestCase;
import net.fina.ecm.alfresco.api.dictionary.model.ClassPropertyRepresentation;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class DictionaryAPITest extends AlfrescoAPITestCase {

    @Before
    public void init() {
        client = getClient();
    }

    @After
    public void cleanUp() {
        client.getRestClient().client.close();
    }

    @Test
    public void getDictionaryCallTest() {
        String dictionaryCallResult = client.getDictionaryAPI().getDictionaryCall();

        Assert.assertNotNull(dictionaryCallResult);
        System.out.println(dictionaryCallResult);
    }

    @Test
    public void getClassesCallTest() {
        String classesAllCallResult = client.getDictionaryAPI().getClassesCall();
        Assert.assertNotNull(classesAllCallResult);
        System.out.println(classesAllCallResult);

        String actionClassCallResult = client.getDictionaryAPI().getClassesCall("fina_fiRegistryBranch");
        Assert.assertNotNull(actionClassCallResult);
        System.out.println(actionClassCallResult);
    }

    @Test
    public void getClassPropertiesCallTest() {
        List<ClassPropertyRepresentation> classPropertiesResult = client.getDictionaryAPI().getClassPropertiesCall("fina_fiRegistryBranch");

        Assert.assertNotNull(classPropertiesResult);
        System.out.println(classPropertiesResult);
    }
}
