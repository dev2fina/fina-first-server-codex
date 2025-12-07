package net.fina.ecm.alfresco.api;

import net.fina.ecm.alfresco.AlfrescoClient;
import net.fina.ecm.util.AlfrescoConfiguration;
import org.junit.Ignore;

import java.io.File;
@Ignore
public abstract class AlfrescoAPITestCase {

    // Endpoint must end with "/" like https://localhost:8080/alfresco/
    public static final String TEST_ENDPOINT = "http://localhost:8080/alfresco/";

    public static final String TEST_USERNAME = "admin";

    public static final String TEST_PASSWORD = "fina2demo";

    public static String TEST_USERHASHSALT = "changeme";

    protected AlfrescoClient client;

    public AlfrescoClient getClient() {
        return getClient(TEST_ENDPOINT, TEST_USERNAME, TEST_PASSWORD);
    }

    public AlfrescoClient getClient(String endpoint, String username, String password) {
        return new AlfrescoClient.Builder()
                .connect(endpoint, username, password)
                .build();
    }

    public AlfrescoClient getExternalClient(String endpoint, String username, String password) {
        return new AlfrescoClient.Builder()
                .connectExternal(endpoint, username, TEST_USERHASHSALT)
                .build();
    }

    public AlfrescoClient getClient(String remoteUSer,String userHashSalt) {
        TEST_USERHASHSALT=userHashSalt;
        return getExternalClient(TEST_ENDPOINT, remoteUSer, null);
    }

    public File getResourceFile(String path) {
        return new File(getClass().getResource(path).getFile());
    }
}
