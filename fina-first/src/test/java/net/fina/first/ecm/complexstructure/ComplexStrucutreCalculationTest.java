package net.fina.first.ecm.complexstructure;

import net.fina.AlfrescoAPITestCase;
import net.fina.ecm.alfresco.api.core.model.representation.NodeRepresentation;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

@Ignore
public class ComplexStrucutreCalculationTest extends AlfrescoAPITestCase {
    @Before
    public void init() {
        client = getClient();
    }

    @After
    public void cleanUp() {
        client.getRestClient().client.close();
    }

    @Test
    public void testCalculateBeneficiariesPercentage() {
        String fiRegistryId = "a8cbe10c-f7cf-48ab-a24b-93c3401407c4";
        List<NodeRepresentation> result = ComplexStructureBeneficiaryCalculator.getInstance().getMainBeneficiaries(fiRegistryId, client);
        result.forEach(r -> {
            System.out.println(r.getProperties());
        });
    }
}
