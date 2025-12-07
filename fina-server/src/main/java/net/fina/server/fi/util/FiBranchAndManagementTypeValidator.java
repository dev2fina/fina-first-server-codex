package net.fina.server.fi.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.fina.common.client.exception.FinATypeException;

import java.util.HashSet;
import java.util.Set;

public class FiBranchAndManagementTypeValidator {

    public static void validateFields(String jsonConfig, Set<String> requiredKeys) throws FinATypeException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonConfig);


            Set<String> foundKeys = new HashSet<>();
            for (JsonNode stepNode : root) {
                JsonNode columns = stepNode.get("columns");
                if (columns != null && columns.isArray()) {
                    for (JsonNode column : columns) {
                        JsonNode keyNode = column.get("key");
                        if (keyNode != null && keyNode.isTextual()) {
                            foundKeys.add(keyNode.asText());
                        }
                    }
                }
            }

            for (String required : requiredKeys) {
                if (!foundKeys.contains(required)) {
                    throw new FinATypeException("Missing required field in JSON_CONFIG: " + required);
                }
            }

        } catch (JsonProcessingException e) {
            throw new FinATypeException("Invalid Json Format");
        }
    }
}
