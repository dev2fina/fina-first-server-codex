package net.fina.ecm.alfresco.api.core;

import net.fina.ecm.alfresco.api.AlfrescoAPITestCase;
import net.fina.ecm.alfresco.api.common.constant.APIConstants;
import net.fina.ecm.alfresco.api.common.representation.ResultPaging;
import net.fina.ecm.alfresco.api.core.model.body.FavoriteBodyCreate;
import net.fina.ecm.alfresco.api.core.model.body.NodeBodyCreate;
import net.fina.ecm.alfresco.api.core.model.representation.FavoriteRepresentation;
import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

public class FavoritesAPITest extends AlfrescoAPITestCase {

    @Before
    public void init() {
        client = getClient();
    }

    @After
    public void cleanUp() {
        client.getRestClient().client.close();
    }


    @Test
    public void listUserFavorites() {
        ResultPaging<FavoriteRepresentation> favorites = client.getFavoritesAPI().listFavoritesCall("-me-", 0, 100, "(EXISTS(target/file) OR EXISTS(target/folder))", null);
        System.out.println(favorites.getObjects().size());
    }

    @Test
    public void testAddFavorites() {
        NodeBodyCreate request = new NodeBodyCreate("TEST_FOLDER_" + new Date().getTime(), "cm:folder");
        NodeRepresentation createdNode = client.getNodesAPI().createNodeCall(APIConstants.FOLDER_ROOT, request).readEntity(NodeRepresentation.class);

        FavoriteBodyCreate favoriteBodyCreate = new FavoriteBodyCreate(FavoriteBodyCreate.FavoriteTypeEnum.FOLDER, createdNode.getId());

        FavoriteRepresentation favoriteRepresentation = client.getFavoritesAPI().createFavoriteCall("-me-", favoriteBodyCreate, null);

        System.out.println(favoriteRepresentation);
    }
}
