package net.fina.ecm.alfresco.api.core.model.body;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

import java.util.List;
import java.util.TreeMap;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NodeBodyUpdate implements BaseRepresentation {
    private final String REGISTRY_FORCE_UPDATE_SCRIPT_EXECUTION = "fina:fiRegistryForceUpdateScriptExecution";
    @JsonProperty("name")
    private String name;

    @JsonProperty("nodeType")
    private String nodeType;

    @JsonProperty("aspectNames")
    private List<String> aspectNames;

    @JsonProperty("properties")
    private TreeMap<String, Object> properties;

    @JsonProperty("permissions")
    private PermissionsBodyUpdate permissions;

    public NodeBodyUpdate() {
    }

    public NodeBodyUpdate(String name) {
        this.name = name;
        this.nodeType = null;
        this.properties = null;
        this.aspectNames = null;
        this.permissions = null;
    }

    public NodeBodyUpdate(TreeMap<String, Object> properties) {
        this.name = null;
        this.nodeType = null;
        if (properties != null && !properties.isEmpty()) {
            this.properties = properties;
            this.properties.put(REGISTRY_FORCE_UPDATE_SCRIPT_EXECUTION, UUID.randomUUID().toString());
        } else {
            this.properties = null;
        }
        this.aspectNames = null;
        this.permissions = null;
    }

    public NodeBodyUpdate(PermissionsBodyUpdate permissions) {
        this.name = null;
        this.nodeType = null;
        this.properties = null;
        this.aspectNames = null;
        this.permissions = permissions;
    }

    public NodeBodyUpdate(String name, String nodeType, TreeMap<String, Object> properties,
                          List<String> aspectNames) {
        this.name = name;
        this.nodeType = nodeType;
        if (properties != null && !properties.isEmpty()) {
            this.properties = properties;
            this.properties.put(REGISTRY_FORCE_UPDATE_SCRIPT_EXECUTION, UUID.randomUUID().toString());
        } else {
            this.properties = null;
        }
        this.aspectNames = aspectNames;
        this.permissions = null;
    }

    public NodeBodyUpdate(String name, String nodeType, TreeMap<String, Object> properties,
                          List<String> aspectNames, PermissionsBodyUpdate permissions) {
        this.name = name;
        this.nodeType = nodeType;
        if (properties != null && !properties.isEmpty()) {
            this.properties = properties;
            this.properties.put(REGISTRY_FORCE_UPDATE_SCRIPT_EXECUTION, UUID.randomUUID().toString());
        } else {
            this.properties = null;
        }
        this.aspectNames = aspectNames;
        this.permissions = permissions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public List<String> getAspectNames() {
        return aspectNames;
    }

    public void setAspectNames(List<String> aspectNames) {
        this.aspectNames = aspectNames;
    }

    public TreeMap<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(TreeMap<String, Object> properties) {
        this.properties = properties;
    }

    public PermissionsBodyUpdate getPermissions() {
        return permissions;
    }

    public void setPermissions(PermissionsBodyUpdate permissions) {
        this.permissions = permissions;
    }
}
