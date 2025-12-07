package net.fina.server.test.ejb;

import junit.framework.Assert;
import net.fina.common.client.exception.FinATypeException;
import net.fina.server.fi.entity.Fi;
import net.fina.server.fi.entity.FiType;
import net.fina.server.fi.entity.PeerGroup;
import net.fina.server.i18n.helper.Description;
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
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.io.File;
import java.util.Collections;

/**
 * User: vako
 * Date: 9/12/13
 * Time: 2:19 PM
 */
@Ignore
@RunWith(Arquillian.class)
public class FiTest {
    private Logger log = Logger.getLogger(getClass());


    @Deployment
    public static Archive<?> createTestArchive() {

        File[] libs = new File[0];
//        Maven.resolver().offline().loadPomFromFile("pom.xml").resolve("net.fina:fina-common:jar:4.2.3").withoutTransitivity().asFile();

        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addAsLibraries(libs)
//                .addPackages(true, "net.fina.*")
                .addPackages(true, "net.fina.common.client")
                .addPackages(true, "net.fina.server.i18n")
                .addPackages(true, "net.fina.server.dcs.entity")
                .addPackages(true, "net.fina.server.security")
                .addPackages(true, "net.fina.server.security.impl")
                .addPackages(true, "net.fina.security")
                .addPackages(true, "net.fina.server.active")
                .addPackages(true, "net.fina.server.system")
                .addPackages(true, "net.fina.server.interceptors")
                .addPackages(true, "net.fina.server.returns.entity")
                .addPackages(true, "net.fina.server.mdt.entity")
                .addPackages(true, "net.fina.server.fi")
                .addPackages(true, "net.fina.server.reports.entity")
                .addPackages(true, "net.fina.server.regions.entity")
                .addPackages(true, "net.fina.server.period.entity")
                .addPackages(true, "net.fina.server.dcs.entity")
                .addPackages(true, "net.fina.server.util")
                .addPackages(true, "net.fina.server.fi.api")
                .addClass(JBossLoginContextFactory.class)
                .addClass(net.fina.common.client.exception.FinATypeException.class)
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                .addAsManifestResource("MANIFEST.MF")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "faces-config.xml");
    }

    @Inject
    net.fina.server.fi.api.FiLocal fiLocal;

    //    @Test(expected = LoginException.class)
    @Test
    public void testSaveFi() throws LoginException, FinATypeException {

        logInfo("before Login");

        LoginContext loginContext = JBossLoginContextFactory.createLoginContext(Util.DEFAULT_USERNAME, Util.DEFAULT_PASSWORD);
        loginContext.login();

        logInfo("start Testing");

        Description desc = new Description();
        desc.addDescription(1, "JUnitTest FiType Name");

        FiType fiType = new FiType("JUnitTest");
        fiType.setDescription(desc);

        try {
            fiLocal.save(fiType);
            logInfo("FiType Saved");
        } finally {
            fiLocal.save(fiType);
            logInfo("FiType Deleted");
        }


        Fi fi = new Fi();
        fi.setId(9999);
        fi.setPeerGroup(Collections.<PeerGroup>emptyList());
        fi.setRegionId(Long.valueOf(9));
        fi.setCode("JUnitTest2");
        fi.setFiType(fiType);

        Description description = new Description();
        description.addDescription(1, "JUnitTest Fi Name2");
        fi.setDescription(description);

        try {
            fiLocal.save(fi,"en_US");
            logInfo("Fi Saved");
            Assert.assertEquals(fi.getCode(), "JUnitTest2");
        } finally {
            fiLocal.delete(fi.getId());
            logInfo("Fi Deleted");
        }

        loginContext.logout();


        logInfo("End Testing");
    }

    private void logInfo(String message) {
        log.info("");
        log.info("");
        log.info("-----------------------------------------------------------------------");
        log.info("-----------------------------------------------------------------------");
        log.info("========================  " + message + "  ========================");
        log.info("-----------------------------------------------------------------------");
        log.info("-----------------------------------------------------------------------");
        log.info("");
        log.info("");
    }
}
