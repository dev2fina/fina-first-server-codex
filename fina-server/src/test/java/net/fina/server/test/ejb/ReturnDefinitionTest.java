package net.fina.server.test.ejb;

import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.mdt.MDTNodeTypes;
import net.fina.common.client.returns.ReturnTableType;
import net.fina.server.i18n.helper.Description;
import net.fina.server.mdt.api.MDTNodeLocal;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.returns.api.ReturnDefinitionLocal;
import net.fina.server.returns.entity.DefinitionTable;
import net.fina.server.returns.entity.ReturnDefinition;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import jakarta.inject.Inject;
import javax.security.auth.login.LoginException;
import java.util.Arrays;
import java.util.Collections;

/**
 * User: Chelomisha@fina2.net
 * Date: 9/11/13
 * Time: 10:29 AM
 */

@Ignore
@RunWith(Arquillian.class)
public class ReturnDefinitionTest {

    private Logger log = Logger.getLogger(getClass());

    @Deployment
    public static Archive<?> createTestArchive() {

        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addPackages(true, "net.fina.common.client")
                .addPackages(true, "net.fina.server.i18n")
                .addPackages(true, "net.fina.server.dcs.entity")
                .addPackages(true, "net.fina.server.security")
                .addPackages(true, "net.fina.security")
                .addPackages(true, "net.fina.server.active")
                .addPackages(true, "net.fina.server.system")
                .addPackages(true, "net.fina.server.interceptors")
                .addPackages(true, "net.fina.server.returns.entity")
                .addPackages(true, "net.fina.server.mdt.entity")
                .addPackages(true, "net.fina.server.fi.entity")
                .addPackages(true, "net.fina.server.reports.entity")
                .addPackages(true, "net.fina.server.regions.entity")
                .addPackages(true, "net.fina.server.period")
                .addPackages(true, "net.fina.server.util")
                .addPackages(true, "net.fina.server.returns.api")
                .addPackages(true, "net.fina.server.mdt.api")
                .addClass(JBossLoginContextFactory.class)
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                .addAsManifestResource("MANIFEST.MF")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "faces-config.xml");
    }


    @Inject
    ReturnDefinitionLocal rdLocal;

    @Inject
    MDTNodeLocal mdtLocal;

    @Test
    public void testSaveAndDelete() throws LoginException, FinATypeException {

        log.info("-------------------------------------------------------");
        log.info("");
        log.info("");
        log.info("-------------------------------------------------------");
        log.info("-------------------------------------------------------");
        log.info("");
        log.info("===== start testing Return Definition Save & Delete ---");

        JBossLoginContextFactory.createLoginContext(Util.DEFAULT_USERNAME, Util.DEFAULT_PASSWORD).login();
        log.info("===== login ---");

        // MDT Node
        MDTNode mdtNode = new MDTNode(0L, MDTNodeTypes.NODE);
        mdtNode.setCode("TEST_RD_MDT");

        Description mdtDesc = new Description();
        mdtDesc.addDescription(1L, "TESTING RETURN DEFINITION");
        mdtNode.setDescription(mdtDesc);

        mdtNode = mdtLocal.save(mdtNode);
        log.info("--- saved MDT Node ---");

        // RD Table
        DefinitionTable rdTable = new DefinitionTable(0L, mdtNode, ReturnTableType.MCT);
        rdTable.setCode("TEST_RD_TABLE");

        // RD
        ReturnDefinition rd = new ReturnDefinition(0L, 0);
        rd.setCode("TEST_RD_RD");
        Description rdDesc = new Description();
        rdDesc.addDescription(1L, "TESTING RETURN DEFINITION");
        rd.setDescription(rdDesc);
        rd.setDefinitionTables(Arrays.asList(rdTable));

        rdTable.setReturnDefinition(rd);

        rd = rdLocal.save(rd);
        log.info("--- saved return Definition Width Table ---");

        rdLocal.delete(Collections.singletonList(rd));
        log.info("--- deleted return Definition Width Table ---");

        mdtLocal.delete(Collections.singletonList(mdtNode.getId()));
        log.info("--- delete MDT Node ---");


        JBossLoginContextFactory.createLoginContext(Util.DEFAULT_USERNAME, Util.DEFAULT_PASSWORD).logout();
        log.info("--- logOut =====");
        log.info("--- Finish Testing return definition Save & Delete =====");
        log.info("");
        log.info("-------------------------------------------------------");
        log.info("-------------------------------------------------------");
        log.info("");
        log.info("");
        log.info("-------------------------------------------------------");
    }

}
