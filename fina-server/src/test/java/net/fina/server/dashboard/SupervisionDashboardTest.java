package net.fina.server.dashboard;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class SupervisionDashboardTest {

    private final String configurationFilePath = "./src/test/resources/dashboard/supervision_dashboard.json";

    @Test
    public void loadDashboardConfigurationFileTest() throws IOException {
        Map<String, Object> configMap = new ObjectMapper().readValue(new File(configurationFilePath), new TypeReference<Map<String, Object>>() {
        });

        System.out.println(configMap);
        Assert.assertNotNull(configMap);
    }
}
