package net.fina.server.test.ejb;

import junit.framework.Assert;
import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.filter.PeriodFilter;
import net.fina.server.i18n.helper.Description;
import net.fina.server.returns.api.PeriodLocal;
import net.fina.server.returns.entity.Period;
import net.fina.server.returns.entity.PeriodType;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: nikoloz
 * Date: 7/18/13
 * Time: 7:28 PM
 * To change this template use File | Settings | File Templates.
 */
@Ignore
@RunWith(Arquillian.class)
public class PeriodTest {

    private Logger log = Logger.getLogger(getClass());


    @Deployment
    public static Archive<?> createTestArchive() {

        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addPackages(true, "net.fina.common.client")
                .addPackages(true, "net.fina.server.i18n")
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
                .addClass(JBossLoginContextFactory.class)
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                .addAsManifestResource("MANIFEST.MF")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "faces-config.xml");
    }

    @Inject
    PeriodLocal periodLocal;

    @Test
    public void testSaveAndDelete() throws LoginException, FinATypeException {
        JBossLoginContextFactory.createLoginContext(Util.DEFAULT_USERNAME, Util.DEFAULT_PASSWORD).login();

        PeriodType periodType = new PeriodType();
        periodType.setVersion(0);
        periodType.setCode("TEST");

        Description description = new Description();
        description.addDescription(1, "Test Period Type");

        periodType.setDescription(description);

        periodLocal.save(periodType);


        Period period = new Period();
        period.setVersion(0);
        period.setPeriodNumber(1);
        period.setFromDate(new Date());
        period.setToDate(new Date());

        period.setPeriodType(periodType);

        periodLocal.savePeriod(period);

        periodLocal.delete(period.getId());
        periodLocal.deletePeriodType(periodType.getId());

        JBossLoginContextFactory.createLoginContext(Util.DEFAULT_USERNAME, Util.DEFAULT_PASSWORD).logout();
    }

    @Test
    public void testLoad() throws LoginException, FinATypeException {
        JBossLoginContextFactory.createLoginContext(Util.DEFAULT_USERNAME, Util.DEFAULT_PASSWORD).login();

        Map<PeriodFilter, Object> filterMap = new HashMap<>();
        List<Period> periods = periodLocal.load(filterMap);

        long count = periodLocal.count(filterMap);

        Assert.assertEquals(periods.size(), count);

        JBossLoginContextFactory.createLoginContext(Util.DEFAULT_USERNAME, Util.DEFAULT_PASSWORD).logout();
    }
}
