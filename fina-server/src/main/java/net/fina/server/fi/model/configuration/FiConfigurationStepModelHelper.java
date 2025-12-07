package net.fina.server.fi.model.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class FiConfigurationStepModelHelper {

    protected static List<FiConfigurationStepMetaModel> getStepsFromJsonString(String jsonConfig) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return Arrays.asList(objectMapper.readValue(jsonConfig, FiConfigurationStepMetaModel[].class));
    }

    protected static String getJsonStringFromSteps(List<FiConfigurationStepMetaModel> steps) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(steps.toArray(new FiConfigurationStepMetaModel[0]));
    }
}
