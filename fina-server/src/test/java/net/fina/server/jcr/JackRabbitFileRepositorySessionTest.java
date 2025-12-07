package net.fina.server.jcr;

import net.fina.common.shared.jcr.DescriptionModel;
import net.fina.common.shared.jcr.JcrCustomConstants;
import net.fina.server.jcr.model.RepositoryNodeMetaModelHelper;
import net.fina.server.store.impl.JackrabbitRepositorySession;
import net.fina.server.store.model.RepositoryFile;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.core.query.QueryImpl;
import org.apache.jackrabbit.util.ISO9075;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import javax.jcr.*;
import javax.jcr.nodetype.NodeTypeManager;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;
import javax.jcr.version.VersionManager;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.IntStream;

import static net.fina.server.util.JcrUtil.registerCustomMixin;
import static net.fina.server.util.JcrUtil.registerFinaCustomNodeType;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JackRabbitFileRepositorySessionTest {
    private Session session;

    @Before
    public void initSession() throws Exception {
        Map<String, String> parameters = new HashMap<String, String>();

        File path = new File("./target/Jackrabbit_repos/user_files");
        path.mkdirs();

        parameters.put("org.apache.jackrabbit.repository.home", path.getPath());

        Repository repository = JcrUtils.getRepository(parameters);
        session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));

        NamespaceRegistry ns = session.getWorkspace().getNamespaceRegistry();
        Optional<String> finaPrefixOptional = Arrays.stream(ns.getPrefixes()).filter(name -> name.equalsIgnoreCase(JcrCustomConstants.NAMESPACE_PREFIX)).findFirst();
        if (!finaPrefixOptional.isPresent()) {
            ns.registerNamespace(JcrCustomConstants.NAMESPACE_PREFIX, JcrCustomConstants.NAMESPACE_URI);
        }

        NodeTypeManager nodeTypeManager = session.getWorkspace().getNodeTypeManager();

        registerFinaCustomNodeType(nodeTypeManager);
        registerCustomMixin(nodeTypeManager);

        session.save();

    }

    @After
    public void deleteFile() throws RepositoryException {
        session.logout();
    }


    @Test
    public void A_testCreateFolders() throws Exception {
        VersionManager vm = session.getWorkspace().getVersionManager();

        String rootFolderPath = "/" + JcrCustomConstants.USER_ROOT_FOLDER;

        // Create folder node
        Node node = JcrUtils.getOrCreateByPath(rootFolderPath, JcrConstants.NT_FOLDER, session);

        IntStream.range(1, 5).forEach(i -> {
            try {
                Node fileNode = JcrUtils.getOrCreateByPath(rootFolderPath + "/test-" + i, JcrConstants.NT_FOLDER, session);
                fileNode.addMixin(JcrCustomConstants.FINA_NT_FOLDER_MIXIN);
                List<DescriptionModel> descriptionMetaModels = new ArrayList<>();
                DescriptionModel model = new DescriptionModel();
                model.setDescription("Description " + i);
                model.setLangCode("en_US");
                descriptionMetaModels.add(model);

                model = new DescriptionModel();
                model.setLangCode("ka_GE");
                model.setDescription("განსაზღვრება" + i);
                descriptionMetaModels.add(model);

                fileNode.setProperty(JcrCustomConstants.PROP_DESCRIPTION, RepositoryNodeMetaModelHelper.getJsonDescription(descriptionMetaModels));

                fileNode.setProperty(JcrConstants.JCR_LASTMODIFIED, Calendar.getInstance());
                if (fileNode.isNew()) {
                    //Add versional property
                    fileNode.addMixin(JcrConstants.MIX_VERSIONABLE);
                }
                Thread.sleep(2000);
            } catch (RepositoryException | InterruptedException e) {
                System.err.println(e.getMessage());
            }
        });

        session.save();

    }

    @Test
    public void B_testFileUpload() throws Exception {
        //create versionable node
        String rootFolderPath = "/" + JcrCustomConstants.USER_ROOT_FOLDER + "/test-1";

        Node parentFolder = JcrUtils.getOrCreateByPath(ISO9075.encodePath(rootFolderPath), JcrConstants.NT_FOLDER, session);

        if (parentFolder.isNew()) {
            parentFolder.addMixin(JcrConstants.MIX_VERSIONABLE);
        }

        Node fileNode = JcrUtils.getOrAddNode(parentFolder, JcrConstants.JCR_CONTENT, JcrCustomConstants.FINA_FILE_NODE_TYPE);

        VersionManager vm = session.getWorkspace().getVersionManager();

        if (fileNode.isNew()) {
            //Add versional property
            fileNode.addMixin(JcrConstants.MIX_VERSIONABLE);
        }
        if (!fileNode.isCheckedOut()) {
            vm.checkout(fileNode.getPath());
        }

        fileNode.setProperty(JcrCustomConstants.PROP_MARK_DELETED, false);
        List<DescriptionModel> descriptionMetaModels = new ArrayList<>();

        DescriptionModel model = new DescriptionModel();
        model.setDescription("Description");
        model.setLangCode("en_US");
        descriptionMetaModels.add(model);

        model = new DescriptionModel();
        model.setLangCode("ka_GE");
        model.setDescription("განსაზღვრება");
        descriptionMetaModels.add(model);

        fileNode.setProperty(JcrCustomConstants.PROP_DESCRIPTION, RepositoryNodeMetaModelHelper.getJsonDescription(descriptionMetaModels));

        try (InputStream stream = new ByteArrayInputStream("".getBytes())) {
            Node contentNode = JcrUtils.getOrAddNode(fileNode, JcrConstants.JCR_CONTENT, JcrConstants.NT_RESOURCE);
            Binary binary = session.getValueFactory().createBinary(stream);
            contentNode.setProperty(JcrConstants.JCR_DATA, binary);
        }

        session.save();
        if (!fileNode.isNew()) {
            vm.checkin(fileNode.getPath());
        }

    }

    @Test
    public void C_listNodesInRoot() throws RepositoryException {
        Node rootNode = session.getNode("/" + JcrCustomConstants.USER_ROOT_FOLDER + "/test-1");
        NodeIterator nodeIter = rootNode.getNodes();
        while (nodeIter.hasNext()) {
            Node currentNode = nodeIter.nextNode();
            System.out.println("============================");
            if (currentNode.hasProperty(JcrCustomConstants.PROP_DESCRIPTION)) {
                System.out.println(currentNode.getProperty(JcrCustomConstants.PROP_DESCRIPTION).getString());
            }
            System.out.println(currentNode.getName());
            System.out.println("============================");
        }
    }


    @Test
    public void D_testQuery() throws Exception {
        QueryManager queryManager = session.getWorkspace().getQueryManager();

        Node node = session.getNode("/" + JcrCustomConstants.USER_ROOT_FOLDER);
        String queryString = "select * from [nt:base] as a where ischildnode(a,[{0}]) order by [jcr:lastModified] desc";
        queryString = MessageFormat.format(queryString, node.getPath());
        Query query = queryManager.createQuery(queryString, Query.JCR_SQL2);
        QueryImpl q = (QueryImpl) query;
        q.setLimit(10);
        q.setOffset(0); // Start from the 10:th file
        QueryResult result = q.execute();
        NodeIterator nodeIter = result.getNodes();
        while (nodeIter.hasNext()) {
            Node currentNode = nodeIter.nextNode();
            System.out.println(currentNode.getName() + "   " + currentNode.getProperty(JcrConstants.JCR_LASTMODIFIED).getDate().getTime());
            System.out.println(currentNode.getProperty(JcrCustomConstants.PROP_DESCRIPTION).getString());
            System.out.println("============================");
        }
    }


    @Test
    public void E_testList() throws Exception {
        VersionManager vm = session.getWorkspace().getVersionManager();
        Node node = session.getNode("/" + JcrCustomConstants.USER_ROOT_FOLDER + "/test-1/");
        VersionHistory history = vm.getVersionHistory(node.getPath());
        VersionIterator iter = history.getAllVersions();

        System.out.println(node.getIdentifier());
        System.out.println("=======================");
        while (iter.hasNext()) {
            Version cur = iter.nextVersion();
            System.out.println(cur.getIdentifier());
        }
    }

    @Test
    public void F_createFile() throws Exception {
        JackrabbitRepositorySession jackrabbitRepositorySession = new JackrabbitRepositorySession();
        RepositoryFile repositoryFile = new RepositoryFile();
        repositoryFile.setPath("/" + JcrCustomConstants.USER_ROOT_FOLDER + "/test-1");
        repositoryFile.setFileName("test.xml");
        repositoryFile.setContent("test xml".getBytes());
        jackrabbitRepositorySession.saveFile(session, repositoryFile);
    }

    @Test
    public void G_testGetFileContent() throws Exception {
        Node rootNode = session.getNode("/" + JcrCustomConstants.USER_ROOT_FOLDER + "/test-1/test.xml");
        JackrabbitRepositorySession jackrabbitRepositorySession = new JackrabbitRepositorySession();
        InputStream insream = jackrabbitRepositorySession.getFileStreamByVersion(session, rootNode.getParent().getPath(), rootNode.getName(), session.getWorkspace().getVersionManager().getBaseVersion(rootNode.getPath()).getIdentifier());
        System.out.println(insream);
    }


}