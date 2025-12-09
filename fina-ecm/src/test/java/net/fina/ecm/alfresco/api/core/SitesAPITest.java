package net.fina.ecm.alfresco.api.core;

import net.fina.ecm.alfresco.api.AlfrescoAPITestCase;
import net.fina.ecm.alfresco.api.common.representation.ResultPaging;
import net.fina.ecm.alfresco.api.core.model.body.SiteMembershipBodyCreate;
import net.fina.ecm.alfresco.api.core.model.representation.FavoriteRepresentation;
import net.fina.ecm.alfresco.api.core.model.representation.SiteMemberRepresentation;
import net.fina.ecm.alfresco.api.core.model.representation.SiteRepresentationEntry;
import net.fina.ecm.alfresco.api.core.model.representation.SiteRoleRepresentation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SitesAPITest extends AlfrescoAPITestCase {
    @Before
    public void init() {
        client = getClient();
    }

    @After
    public void cleanUp() {
        client.getRestClient().client.close();
    }

    @Test
    public void testLoadSites() {
        ResultPaging<SiteRoleRepresentation> result = client.getSitesAPI().listPersonSites("-me-", 0, 100);
        System.out.println(result.getObjects().size());
    }

    @Test
    public void testLoadPublicSites() {
        ResultPaging<SiteRepresentationEntry> result = client.getSitesAPI().listSites(0, 100,"(visibility='PUBLIC')\n");
        System.out.println(result.getObjects().size());
    }


    @Test
    public void testLoadFavorites() {
        ResultPaging<FavoriteRepresentation> result = client.getFavoritesAPI().listFavoritesCall("-me-", 0, 100, "(EXISTS(target/site))", null);
        System.out.println(result.getObjects().size());
    }


    @Test
    public void listSiteMembers() {
        Object siteMembers = client.getSitesAPI().listSiteMembershipsCall("Otos-Library", 0, 100, null);
        System.out.println(siteMembers);
    }

    @Test
    public void testAddMemberToSite(){

        SiteMembershipBodyCreate siteMembershipBodyCreate=new SiteMembershipBodyCreate("oto","SiteConsumer");
        SiteMemberRepresentation result = client.getSitesAPI().createSiteMembershipCall("nbsgd", siteMembershipBodyCreate, null);
        System.out.println(result);
    }


}
