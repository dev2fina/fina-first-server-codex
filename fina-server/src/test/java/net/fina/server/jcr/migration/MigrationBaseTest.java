package net.fina.server.jcr.migration;

import net.fina.common.client.exception.FinATypeException;
import net.fina.common.shared.jcr.RepositoryNodeMetaModel;
import net.fina.server.jcr.impl.AlfrescoFileRepositorySession;
import net.fina.server.jcr.impl.JackRabbitFileRepositorySession;
import net.fina.server.store.impl.FinaRepository;
import net.fina.server.store.impl.NodeTypeRegistrar;
import net.fina.server.store.impl.SessionProducer;
import org.apache.jackrabbit.commons.JcrUtils;
import org.junit.Before;
import org.junit.Ignore;

import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

@Ignore
public class MigrationBaseTest extends AlfrescoAPITestCase {
    private final String connectionUrl = "jdbc:sqlserver://localhost:1433;databaseName=fina2nbg;";
    private final String dbUser = "sa";
    private final String dbPassword = "fina2demo_";
    private final boolean connectAlfresco;
    private final boolean connectJackRabbit;
    private final String jackRabbitUser = "admin";
    private final String jackRabbitUserPass = "admin";
    private final String jackRabbiRepo = "/home/oto/Dev/runtime/fina-server/fina_workspace/Jackrabbit_repos/fina_repo";

    protected Connection connection;

    public MigrationBaseTest() {
        this.connectAlfresco = true;
        this.connectJackRabbit = true;
    }

    public MigrationBaseTest(boolean connectAlfresco, boolean connectJackRabbit) {
        this.connectAlfresco = connectAlfresco;
        this.connectJackRabbit = connectJackRabbit;
    }

    @Before
    public void init() {
        if (connectAlfresco) {
            client = getClient();
        }
    }

    protected CachedRowSet executeQuery(String sql) throws SQLException {
        initConnection();
        CachedRowSet crs = RowSetProvider.newFactory().createCachedRowSet();
        try (Connection con = connection; Statement stmt = con.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            crs.populate(rs);
        } catch (Throwable t) {
            System.err.println(t.getMessage());
        }
        return crs;
    }

    protected void executeUpdateQuery(String sql) throws SQLException {
        initConnection();
        try (Connection con = connection; Statement stmt = con.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (Throwable t) {
            System.err.println(t.getMessage());
        }
    }

    protected RepositoryNodeMetaModel saveUploadFileToAlfresco(RepositoryNodeMetaModel repositoryNodeMetaModel) throws IOException, FinATypeException {
        RepositoryNodeMetaModel result = null;
        if (connectAlfresco) {
            AlfrescoFileRepositorySession afrs = new AlfrescoFileRepositorySession();
            result = afrs.saveFile(client, repositoryNodeMetaModel);
        }
        return result;
    }

    protected RepositoryNodeMetaModel saveUploadFileToJackRabbit(RepositoryNodeMetaModel repositoryNodeMetaModel) throws Exception {
        RepositoryNodeMetaModel result = null;
        if (connectJackRabbit) {
            SessionProducer sessionProducer = new SessionProducer();
            JackRabbitFileRepositorySession jackRabbitFileRepositorySession = new JackRabbitFileRepositorySession();
            result = jackRabbitFileRepositorySession.saveFile(getJackRabbitSession(sessionProducer, jackRabbiRepo, FinaRepository.FINA_REPO), repositoryNodeMetaModel);
        }
        return result;
    }

    private void initConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(connectionUrl, dbUser, dbPassword);
        }
    }

    private Session getJackRabbitSession(SessionProducer sessionProducer, String repositoryHome, FinaRepository finaRepository) throws Exception {
        Map<String, String> parameters = new HashMap<>();

        parameters.put("org.apache.jackrabbit.repository.home", repositoryHome);

        Repository repository = JcrUtils.getRepository(parameters);

        Session session = repository.login(new SimpleCredentials(jackRabbitUser, jackRabbitUserPass.toCharArray()));
        NodeTypeRegistrar nodeTypeRegistrar = new NodeTypeRegistrar();
        nodeTypeRegistrar.ensureRegistered(session, finaRepository);


        return session;

    }

}
