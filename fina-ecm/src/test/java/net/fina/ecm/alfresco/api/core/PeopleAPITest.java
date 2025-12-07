package net.fina.ecm.alfresco.api.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.fina.ecm.alfresco.AlfrescoClient;
import net.fina.ecm.alfresco.api.AlfrescoAPITestCase;
import net.fina.ecm.alfresco.api.common.representation.ResultPaging;
import net.fina.ecm.alfresco.api.core.model.body.PersonBodyCreate;
import net.fina.ecm.alfresco.api.core.model.body.PersonBodyUpdate;
import net.fina.ecm.alfresco.api.core.model.representation.PersonRepresentation;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

public class PeopleAPITest extends AlfrescoAPITestCase {


    private AlfrescoClient client;

    @Before
    public void init() {
        client = getClient();
    }

    @After
    public void cleanUp() {
        client.getRestClient().client.close();
    }

    @Test
    public void testListPersons() {
        ResultPaging<PersonRepresentation> users = client.getPeopleAPI().loadAllUsers();

        Assert.assertNotNull(users);
        Assert.assertNotNull(users.getObjects());
        Assert.assertTrue("Users must be more then 0", users.getObjects().size() > 0);

        users.getObjects().forEach(person -> {
            System.out.println(person.toString());
        });
        System.out.println("Size = "+users.getObjects().size());
    }

    @Test
    public void testCreatePerson() {
        PersonBodyCreate person = new PersonBodyCreate();
        String id = UUID.randomUUID().toString();
        person.setId(id);
        person.setFirstName("person1");
        person.setLastName("personashvili");
        person.setEmail("person1@personashvili.com");
        person.setPassword("fina2demo");

        PersonRepresentation result = client.getPeopleAPI().createPerson(person);
        System.out.println(result.toString());

        Assert.assertEquals(result.getId(), id);
        Assert.assertEquals(result.getFirstName(), "person1");
        Assert.assertEquals(result.getLastName(), "personashvili");
        Assert.assertEquals(result.getEmail(), "person1@personashvili.com");
    }


    @Test
    public void testUpdate() throws JsonProcessingException {
        PersonBodyUpdate person = new PersonBodyUpdate().firstName("eeeee").mobile("44444");

        System.out.println(new ObjectMapper().writeValueAsString(person));
        PersonRepresentation personRepresentation = client.getPeopleAPI().updatePerson("oto1", person);

        System.out.println(personRepresentation);

    }

    @Test
    public void testGetCallerInfo(){
        PersonRepresentation me= client.getPeopleAPI().getPersonById("-me-");
        System.out.println(me);
    }

}
