package net.fina.server.store.impl;

import net.fina.server.store.api.RepositoryLocal;
import net.fina.server.store.model.RepositoryFile;
import org.apache.jackrabbit.commons.JcrUtils;
import org.junit.*;

import javax.jcr.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JackrabbitRepositorySessionTest {

    private Session session;
    private String fullNodePath;

    @Before
    public void initSession() throws RepositoryException {
        Map<String, String> parameters = new HashMap<String, String>();

        File path = new File("./target/Jackrabbit_repos/test_jack");
        path.mkdirs();

        parameters.put("org.apache.jackrabbit.repository.home", path.getPath());

        Repository repository = JcrUtils.getRepository(parameters);
        session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
    }

    @Test
    @Ignore
    public void initSessionWithUri() throws RepositoryException {
        try {
            Repository repository = JcrUtils.getRepository("http://localhost:8081/rmi");
            Session s = repository.login(new SimpleCredentials("admin", "admin".toCharArray()), "default");
            System.out.println(s.getWorkspace().getName());
        } catch (Throwable t) {
            System.err.println(t.getMessage());
        }
    }

    @After
    public void deleteFile() throws RepositoryException {
        if (fullNodePath != null) {
            Node node = JcrUtils.getNodeIfExists(fullNodePath, session);
            node.remove();

            Node deletedNode = JcrUtils.getNodeIfExists(fullNodePath, session);

            session.logout();

            Assert.assertNotNull(node);
            Assert.assertNull(deletedNode);
        }
    }

    @Test
    public void saveFile() throws IOException, RepositoryException {
        RepositoryFile rf = new RepositoryFile();
        rf.setContent(new byte[0]);
        rf.setFileName("test.xml");
        rf.setPath("/demo");

        RepositoryLocal repositoryLocal = new JackrabbitRepositorySession();
        repositoryLocal.saveFile(session, rf);

        fullNodePath = rf.getPath() + "/" + rf.getFileName();
        Node newNode = JcrUtils.getNodeIfExists(fullNodePath, session);
        Assert.assertNotNull(newNode);

        String versionId = repositoryLocal.getLatestVersionId(session, rf.getPath() + "/" + rf.getFileName());
        Assert.assertNotNull(versionId);
    }

}
