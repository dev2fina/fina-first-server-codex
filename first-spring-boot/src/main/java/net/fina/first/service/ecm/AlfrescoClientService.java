package net.fina.first.service.ecm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.fina.first.config.EcmConfig;
import net.fina.first.dto.ecm.NodeMetaModel;
import net.fina.first.dto.ecm.PaginatedListWrapper;
import net.fina.first.dto.ecm.VersionMetaModel;
import net.fina.first.exception.BusinessException;
import net.fina.first.exception.FileProcessingException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

@Slf4j
@Service
@ConditionalOnProperty(name = "ecm.alfresco.enabled", havingValue = "true", matchIfMissing = true)
public class AlfrescoClientService {

    private final EcmConfig ecmConfig;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public AlfrescoClientService(EcmConfig ecmConfig, ObjectMapper objectMapper) {
        this.ecmConfig = ecmConfig;
        this.objectMapper = objectMapper;
        this.restTemplate = createRestTemplate();
    }

    private RestTemplate createRestTemplate() {
        RestTemplate template = new RestTemplate();
        return template;
    }

    public NodeMetaModel getNode(String nodeId) {
        String url = buildUrl("/nodes/{nodeId}", Map.of("nodeId", nodeId));
        ResponseEntity<JsonNode> response = restTemplate.exchange(
                url, HttpMethod.GET, createHttpEntity(), JsonNode.class);
        return parseNodeResponse(response.getBody());
    }

    public NodeMetaModel getNodeByPath(String relativePath) {
        String url = buildUrl("/nodes/-root-?relativePath={path}", Map.of("path", relativePath));
        ResponseEntity<JsonNode> response = restTemplate.exchange(
                url, HttpMethod.GET, createHttpEntity(), JsonNode.class);
        return parseNodeResponse(response.getBody());
    }

    public PaginatedListWrapper<NodeMetaModel> getNodeChildren(String nodeId, int start, int limit,
                                                                String orderBy, String where) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(
                ecmConfig.getEndpoint() + "/alfresco/api/-default-/public/alfresco/versions/1/nodes/{nodeId}/children")
                .queryParam("skipCount", start)
                .queryParam("maxItems", limit);

        if (orderBy != null) {
            builder.queryParam("orderBy", orderBy);
        }
        if (where != null) {
            builder.queryParam("where", where);
        }

        String url = builder.buildAndExpand(nodeId).toUriString();
        ResponseEntity<JsonNode> response = restTemplate.exchange(
                url, HttpMethod.GET, createHttpEntity(), JsonNode.class);

        return parseNodeListResponse(response.getBody());
    }

    public NodeMetaModel createNode(String parentNodeId, String name, String nodeType,
                                    Map<String, Object> properties) {
        String url = buildUrl("/nodes/{nodeId}/children", Map.of("nodeId", parentNodeId));

        Map<String, Object> body = new HashMap<>();
        body.put("name", name);
        body.put("nodeType", nodeType);
        if (properties != null) {
            body.put("properties", properties);
        }

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, createHeaders());
        ResponseEntity<JsonNode> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, JsonNode.class);

        return parseNodeResponse(response.getBody());
    }

    public NodeMetaModel uploadFile(String parentNodeId, String fileName, MultipartFile file) {
        try {
            String url = buildUrl("/nodes/{nodeId}/children", Map.of("nodeId", parentNodeId));

            HttpHeaders headers = createHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("filedata", new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return fileName;
                }
            });
            body.add("name", fileName);

            HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, JsonNode.class);

            return parseNodeResponse(response.getBody());
        } catch (IOException e) {
            throw new FileProcessingException("Failed to upload file: " + fileName, e);
        }
    }

    public NodeMetaModel updateNode(String nodeId, Map<String, Object> properties) {
        String url = buildUrl("/nodes/{nodeId}", Map.of("nodeId", nodeId));

        Map<String, Object> body = new HashMap<>();
        body.put("properties", properties);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, createHeaders());
        ResponseEntity<JsonNode> response = restTemplate.exchange(
                url, HttpMethod.PUT, entity, JsonNode.class);

        return parseNodeResponse(response.getBody());
    }

    public void deleteNode(String nodeId, boolean permanent) {
        String url = buildUrl("/nodes/{nodeId}?permanent={permanent}",
                Map.of("nodeId", nodeId, "permanent", permanent));
        restTemplate.exchange(url, HttpMethod.DELETE, createHttpEntity(), Void.class);
    }

    public byte[] getNodeContent(String nodeId) {
        String url = buildUrl("/nodes/{nodeId}/content", Map.of("nodeId", nodeId));
        ResponseEntity<byte[]> response = restTemplate.exchange(
                url, HttpMethod.GET, createHttpEntity(), byte[].class);
        return response.getBody();
    }

    public NodeMetaModel copyNode(String nodeId, String targetParentId, String newName) {
        String url = buildUrl("/nodes/{nodeId}/copy", Map.of("nodeId", nodeId));

        Map<String, Object> body = new HashMap<>();
        body.put("targetParentId", targetParentId);
        if (newName != null) {
            body.put("name", newName);
        }

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, createHeaders());
        ResponseEntity<JsonNode> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, JsonNode.class);

        return parseNodeResponse(response.getBody());
    }

    public NodeMetaModel moveNode(String nodeId, String targetParentId, String newName) {
        String url = buildUrl("/nodes/{nodeId}/move", Map.of("nodeId", nodeId));

        Map<String, Object> body = new HashMap<>();
        body.put("targetParentId", targetParentId);
        if (newName != null) {
            body.put("name", newName);
        }

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, createHeaders());
        ResponseEntity<JsonNode> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, JsonNode.class);

        return parseNodeResponse(response.getBody());
    }

    public List<VersionMetaModel> getNodeVersions(String nodeId) {
        String url = buildUrl("/nodes/{nodeId}/versions", Map.of("nodeId", nodeId));
        ResponseEntity<JsonNode> response = restTemplate.exchange(
                url, HttpMethod.GET, createHttpEntity(), JsonNode.class);
        return parseVersionListResponse(response.getBody());
    }

    public VersionMetaModel revertVersion(String nodeId, String versionId, String comment) {
        String url = buildUrl("/nodes/{nodeId}/versions/{versionId}/revert",
                Map.of("nodeId", nodeId, "versionId", versionId));

        Map<String, Object> body = new HashMap<>();
        body.put("majorVersion", false);
        if (comment != null) {
            body.put("comment", comment);
        }

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, createHeaders());
        ResponseEntity<JsonNode> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, JsonNode.class);

        return parseVersionResponse(response.getBody());
    }

    public PaginatedListWrapper<NodeMetaModel> search(String query, int start, int limit,
                                                       String sortField, String sortDir) {
        String url = ecmConfig.getEndpoint() + "/alfresco/api/-default-/public/search/versions/1/search";

        Map<String, Object> queryBody = new HashMap<>();
        queryBody.put("query", Map.of("query", query, "language", "afts"));
        queryBody.put("paging", Map.of("skipCount", start, "maxItems", limit));

        if (sortField != null) {
            queryBody.put("sort", List.of(Map.of(
                    "type", "FIELD",
                    "field", sortField,
                    "ascending", "ASC".equalsIgnoreCase(sortDir)
            )));
        }

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(queryBody, createHeaders());
        ResponseEntity<JsonNode> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, JsonNode.class);

        return parseSearchResponse(response.getBody());
    }

    public NodeMetaModel createFolder(String parentNodeId, String folderName) {
        return createNode(parentNodeId, folderName, "cm:folder", null);
    }

    public boolean nodeExists(String nodeId) {
        try {
            getNode(nodeId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String buildUrl(String path, Map<String, Object> params) {
        String url = ecmConfig.getEndpoint() +
                "/alfresco/api/-default-/public/alfresco/versions/1" + path;

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            url = url.replace("{" + entry.getKey() + "}", String.valueOf(entry.getValue()));
        }
        return url;
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        String auth = ecmConfig.getAdminUsername() + ":" + ecmConfig.getAdminPassword();
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        headers.set("Authorization", "Basic " + encodedAuth);

        return headers;
    }

    private HttpEntity<?> createHttpEntity() {
        return new HttpEntity<>(createHeaders());
    }

    private NodeMetaModel parseNodeResponse(JsonNode response) {
        try {
            JsonNode entry = response.path("entry");
            return objectMapper.treeToValue(entry, NodeMetaModel.class);
        } catch (Exception e) {
            throw new BusinessException("Failed to parse node response", e);
        }
    }

    private PaginatedListWrapper<NodeMetaModel> parseNodeListResponse(JsonNode response) {
        try {
            List<NodeMetaModel> nodes = new ArrayList<>();
            JsonNode list = response.path("list");
            JsonNode entries = list.path("entries");

            for (JsonNode entry : entries) {
                nodes.add(objectMapper.treeToValue(entry.path("entry"), NodeMetaModel.class));
            }

            JsonNode pagination = list.path("pagination");
            int totalItems = pagination.path("totalItems").asInt(0);
            int maxItems = pagination.path("maxItems").asInt(25);
            int skipCount = pagination.path("skipCount").asInt(0);

            return PaginatedListWrapper.of(nodes, totalItems, maxItems, skipCount / maxItems);
        } catch (Exception e) {
            throw new BusinessException("Failed to parse node list response", e);
        }
    }

    private PaginatedListWrapper<NodeMetaModel> parseSearchResponse(JsonNode response) {
        try {
            List<NodeMetaModel> nodes = new ArrayList<>();
            JsonNode list = response.path("list");
            JsonNode entries = list.path("entries");

            for (JsonNode entry : entries) {
                nodes.add(objectMapper.treeToValue(entry.path("entry"), NodeMetaModel.class));
            }

            JsonNode pagination = list.path("pagination");
            int totalItems = pagination.path("totalItems").asInt(0);
            int maxItems = pagination.path("maxItems").asInt(25);
            int skipCount = pagination.path("skipCount").asInt(0);

            return PaginatedListWrapper.of(nodes, totalItems, maxItems, skipCount / maxItems);
        } catch (Exception e) {
            throw new BusinessException("Failed to parse search response", e);
        }
    }

    private List<VersionMetaModel> parseVersionListResponse(JsonNode response) {
        try {
            List<VersionMetaModel> versions = new ArrayList<>();
            JsonNode entries = response.path("list").path("entries");

            for (JsonNode entry : entries) {
                versions.add(objectMapper.treeToValue(entry.path("entry"), VersionMetaModel.class));
            }
            return versions;
        } catch (Exception e) {
            throw new BusinessException("Failed to parse version list response", e);
        }
    }

    private VersionMetaModel parseVersionResponse(JsonNode response) {
        try {
            return objectMapper.treeToValue(response.path("entry"), VersionMetaModel.class);
        } catch (Exception e) {
            throw new BusinessException("Failed to parse version response", e);
        }
    }

    public String hashUserCredentials(String username) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String toHash = username + ecmConfig.getUserHashSalt();
            byte[] hash = digest.digest(toHash.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new BusinessException("Failed to hash user credentials", e);
        }
    }
}
