package net.fina.server.test.ejb;

import net.fina.common.client.exception.FinATypeException;
import net.fina.server.dcs.uploadfile.entity.UploadFile;
import net.fina.server.i18n.api.LanguageLocal;
import net.fina.server.i18n.entity.Language;
import net.fina.server.i18n.impl.LanguageListSingleton;
import net.fina.server.i18n.impl.LanguageSession;
import net.fina.server.returns.api.ReturnSubmissionNotificationType;
import net.fina.server.security.crypto.signature.FileSignerFactorySession;
import net.fina.server.util.ServerStartFinishService;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import jakarta.inject.Inject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import jakarta.validation.Validator;
import java.io.File;

import static junit.framework.Assert.assertNotNull;

/**
 * Created with IntelliJ IDEA.
 * User: nikoloz
 * Date: 7/17/13
 * Time: 6:53 PM
 */
@Ignore
@RunWith(Arquillian.class)
public class LanguageTest {
    private Logger log = Logger.getLogger(getClass());

    //change location
    private static final String PROJECT_ROOT_PATH = "";


    @Deployment
    public static Archive<?> createTestArchive() {

        File[] files = new File[0];
                /*
                Maven.resolver()
                .loadPomFromFile(PROJECT_ROOT_PATH + "/fina/fina-core/fina/fina-server/pom.xml")
                .importRuntimeDependencies()
                .resolve()
                .withTransitivity()
                .asFile();*/

        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addClass(LanguageLocal.class)
                .addClass(LanguageSession.class)
                .addClass(Language.class)
                .addClass(LanguageListSingleton.class)
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
                .addPackages(true, Filters.exclude(ServerStartFinishService.class), "net.fina.server.util")
                .addPackages(true, "net.fina.common.shared")
                .addPackages(true, "com.fasterxml.jackson")
                .addClass(ReturnSubmissionNotificationType.class)
                .addClass(FileSignerFactorySession.class)
                .addClass(UploadFile.class)
                .addAsLibraries(files)
                .addAsLibrary(new File(PROJECT_ROOT_PATH + "/fina/fina-core/fina/fina-auditlog/target/fina-auditlog-7.0.5-SNAPSHOT.jar"))
                .addAsLibrary(new File(PROJECT_ROOT_PATH + "/fina/fina-core/fina/fina-common/target/fina-common-7.0.5-SNAPSHOT.jar"))
                .addClass(JBossLoginContextFactory.class)
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                .addAsManifestResource("MANIFEST.MF")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "faces-config.xml");
    }

    @Inject
    LanguageLocal languageLocal;

    @Inject
    Validator validator;

    @Test
    public void testSaveAndFind() throws FinATypeException, LoginException {
        LoginContext loginContext = JBossLoginContextFactory.createLoginContext(Util.DEFAULT_USERNAME, Util.DEFAULT_PASSWORD);
        loginContext.login();

        Language l = new Language();
        l.setVersion(0);
        l.setCode("test");
        l.setName("English");
        l.setDateFormat("dd/mm/yy");
        l.setFontFace("Arial");
        l.setFontSize(12);
        l.setHtmlCharSet("UTF-8");
        l.setNumberFormat("#.#");
        l.setXmlEncoding("UTF-8");

        languageLocal.save(l);

        assertNotNull(l.getId());

        languageLocal.delete(l.getId());
/*
        try {
            l = languageLocal.getLanguageByCode("test");
        } catch (Exception ex) {
            l = null;
        }
        assertNull(l);*/
    }

    @Test
    public void loadAllTest() throws LoginException {
        LoginContext loginContext = JBossLoginContextFactory.createLoginContext(Util.DEFAULT_USERNAME, Util.DEFAULT_PASSWORD);
        loginContext.login();

        log.info("Test loadLanguages - " + languageLocal.loadLanguages());
        log.info("Test getLanguageIds - " + languageLocal.getLanguageIds());
        log.info("Test getLanguagesCodeNameMap - " + languageLocal.getLanguagesCodeNameMap());

    }
}
