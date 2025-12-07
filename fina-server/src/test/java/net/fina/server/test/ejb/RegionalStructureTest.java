package net.fina.server.test.ejb;

import net.fina.common.client.exception.FinATypeException;
import net.fina.server.fi.api.RegionLocal;
import net.fina.server.fi.entity.Region;
import net.fina.server.i18n.helper.Description;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
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

import static junit.framework.Assert.assertEquals;

/**
 * User: Baaka
 * Date: 9/13/13
 * Time: 1:51 PM
 */

@Ignore
@RunWith(Arquillian.class)
public class RegionalStructureTest {

    @Deployment
    public static Archive<?> createTestArchive(){

        File[] libs = new File[0];
//                Maven.resolver()
//                .loadPomFromFile("pom.xml").resolve("net.fina:fina-common:jar:4.2.3")
//                .withTransitivity().asFile();

        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addPackages(true, "net.fina.server.dcs.entity")
                .addPackages(true, "net.fina.common.client")
                .addPackages(true, "net.fina.server.i18n")
                .addPackages(true, "net.fina.server.security")
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
                .addPackages(true, "net.fina.server.util")
                .addClass(JBossLoginContextFactory.class)
                .addAsLibraries(libs)
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                .addAsManifestResource("MANIFEST.MF")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "faces-config.xml");
    }


    @Inject
    RegionLocal regionLocal;

    @Test
    public void testAddDeleteNodes() throws LoginException, FinATypeException {
        LoginContext loginContext = JBossLoginContextFactory.createLoginContext(Util.DEFAULT_USERNAME,Util.DEFAULT_PASSWORD);
        loginContext.login();

        for(int i=0;i<10;i++){

            // parents
            parentRegion = new Region();
            parentRegion.setParentId(0);
            parentRegion.setCode("Parent - Test #" + String.valueOf(i));
            description = new Description();
            description.addDescription(1,"California #" + String.valueOf(i));
            parentRegion.setDescription(description);
            regionLocal.save(parentRegion);

            // children
            childRegion = new Region();
            childRegion.setParentId(parentRegion.getId());
            childRegion.setCode("Child - Test #" + String.valueOf(i));
            description = new Description();
            description.addDescription(1,"Western California #" + String.valueOf(i));
            childRegion.setDescription(description);
            regionLocal.save(childRegion);

            // grandchildren
            grandChildRegion = new Region();
            grandChildRegion.setParentId(childRegion.getId());
            grandChildRegion.setCode("Grandchild - Test #" + String.valueOf(i));
            description = new Description();
            description.addDescription(1,"San Francisco #" + String.valueOf(i));
            grandChildRegion.setDescription(description);
            regionLocal.save(grandChildRegion);

            // assertions
            assertEquals(regionLocal.getRegionWithId(parentRegion.getId()).getCode(), parentRegion.getCode());
            assertEquals(regionLocal.getRegionWithId(parentRegion.getId()).getParentId(),parentRegion.getParentId());
            assertEquals(regionLocal.getRegionWithId(parentRegion.getId()).getId(),parentRegion.getId());

            assertEquals(regionLocal.getRegionWithId(childRegion.getId()).getCode(),childRegion.getCode());
            assertEquals(regionLocal.getRegionWithId(childRegion.getId()).getParentId(),childRegion.getParentId());
            assertEquals(regionLocal.getRegionWithId(childRegion.getId()).getId(),childRegion.getId());

            assertEquals(regionLocal.getRegionWithId(grandChildRegion.getId()).getCode(),grandChildRegion.getCode());
            assertEquals(regionLocal.getRegionWithId(grandChildRegion.getId()).getParentId(),grandChildRegion.getParentId());
            assertEquals(regionLocal.getRegionWithId(grandChildRegion.getId()).getId(),grandChildRegion.getId());

            // delete nodes
            regionLocal.delete(grandChildRegion.getId());
            regionLocal.delete(childRegion.getId());
            regionLocal.delete(parentRegion.getId());

        }
     }

    private Description description;
    private Region parentRegion;
    private Region childRegion;
    private Region grandChildRegion;
}
