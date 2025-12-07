package net.fina.server.test.ejb;

import net.fina.common.client.exception.FinATypeException;
import net.fina.common.client.property.PropertyKeys;
import net.fina.security.util.SecurityUtil;
import net.fina.server.i18n.helper.Description;
import net.fina.server.security.api.UserLocal;
import net.fina.server.security.entity.User;
import net.fina.server.security.api.PropertyLocal;
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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Created with IntelliJ IDEA.
 * User: nikoloz
 * Date: 7/18/13
 * Time: 2:45 PM
 * To change this template use File | Settings | File Templates.
 */

@Ignore
@RunWith(Arquillian.class)
public class UserTest {

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
                .addPackages(true, "net.fina.server.period.entity")
                .addPackages(true, "net.fina.server.util")
                .addClass(JBossLoginContextFactory.class)
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                .addAsManifestResource("MANIFEST.MF")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "faces-config.xml");
    }

    @Inject
    UserLocal userLocal;

    @Inject
    PropertyLocal propertyLocal;

    @Test
    public void testUserBlock() throws LoginException, FinATypeException {
        LoginContext loginContext = JBossLoginContextFactory.createLoginContext(Util.DEFAULT_USERNAME, Util.DEFAULT_PASSWORD);
        loginContext.login();

        User user = new User();

        user.setLogin("TEST");
        user.setPassword(SecurityUtil.encodePassword("TEST"));

        user.setVersion(0);
        user.setBlocked(false);
        user.setChangePassword(false);

        Description description = new Description();
        description.addDescription(1, "Test User");
        user.setDescription(description);

        Description titleDescription = new Description();
        titleDescription.addDescription(1, "Test User");
        user.setTitledescription(titleDescription);

        user.setEmail("Email");
        user.setPhone("Phone");
        user.setPermissions(userLocal.loadPermissions());

        try {
            /**
             * Save user
             */
            userLocal.saveUser(user);

            assertNotNull(user.getId());

            int allowedLoginAttempt = Integer.parseInt(propertyLocal.getSystemProperty(PropertyKeys.ALLOWED_LOGIN_ATTEMPT_NUMBER));

            for (int i = 0; i < allowedLoginAttempt + 2; i++) {
                try {
                    LoginContext lc = JBossLoginContextFactory.createLoginContext("TEST", "WRONG");
                    lc.login();
                } catch (Exception e) {
                    //TODO Ignore Exception
                }
            }

            User blockedUser = userLocal.findUserbyLogin("TEST");

            assertEquals(blockedUser.getBlocked(), true);

        } finally {
            /**
             * Delete User
             */
            userLocal.deleteUser(user.getId());
        }

    }
}
