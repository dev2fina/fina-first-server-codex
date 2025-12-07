package net.fina.ecm.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.fina.ecm.util.AlfrescoConfiguration;
import org.junit.Ignore;

/**
 * Unit test for simple App.
 */
@Ignore
public class ConfigurationTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ConfigurationTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(ConfigurationTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {
        System.out.println("Start Test!");
        // D:\Dev\FinA\repo1\Configurations
        System.setProperty("jboss.server.config.dir", "D:\\Development\\FinA\\sources\\repo1\\Configurations");
        String testConfig = AlfrescoConfiguration.get().getAlfrescoProperty("atompub");
        assertEquals(testConfig, "http://localhost:8080/alfresco/cmisatom");
    }

    public void testID() {
        System.setProperty("jboss.server.config.dir", "D:\\Development\\FinA\\sources\\repo1\\Configurations");

        String testFiTypesID = AlfrescoConfiguration.get().getAlfrescoProperty("fiTypesID");
        String testManagementID = AlfrescoConfiguration.get().getAlfrescoProperty("managementModelID");
        String testLicenseTypeID = AlfrescoConfiguration.get().getAlfrescoProperty("licenseTypeID");

        assertEquals(testFiTypesID, "workspace://SpacesStore/397ca35c-9f67-46fe-a0c5-c7842c992959");
        assertEquals(testManagementID, "workspace://SpacesStore/48f4b57d-14b5-423e-8b9d-c073fb2608a0");
        assertEquals(testLicenseTypeID, "workspace://SpacesStore/573b9b72-1b21-4823-9949-117768553e16");

    }

    public void testCodeDescription() {
        System.setProperty("jboss.server.config.dir", "D:\\Development\\FinA\\sources\\repo1\\Configurations");

        String licenseTypeCode = AlfrescoConfiguration.get().getAlfrescoProperty("licenseTypeCode");
        String licenseTypeDescription = AlfrescoConfiguration.get().getAlfrescoProperty("licenseTypeDescription");

        System.out.println("LicenseType Code: " + licenseTypeCode);
        System.out.println("LicenseType Description: " + licenseTypeDescription);
    }

}
