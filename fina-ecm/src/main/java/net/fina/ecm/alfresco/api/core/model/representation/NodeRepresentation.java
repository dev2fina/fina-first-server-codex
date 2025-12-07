package net.fina.ecm.alfresco.api.core.model.representation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import net.fina.ecm.alfresco.api.common.representation.AbstractRepresentation;

import java.util.*;

@JsonTypeName(value = "entry")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NodeRepresentation extends AbstractRepresentation {
    @JsonProperty("id")
    protected String id = null;

    @JsonProperty("parentId")
    protected String parentId = null;

    @JsonProperty("name")
    protected String name = null;

    @JsonProperty("nodeType")
    protected String nodeType = null;

    @JsonProperty("isFolder")
    protected Boolean isFolder = null;

    @JsonProperty("isFile")
    protected Boolean isFile = null;

    @JsonProperty("isLocked")
    protected Boolean isLocked = null;

    @JsonProperty("modifiedAt")
    protected Date modifiedAt = null;

    @JsonProperty("modifiedByUser")
    protected UserInfoRepresentation modifiedBy = null;

    @JsonProperty("createdAt")
    protected Date createdAt = null;

    @JsonProperty("createdByUser")
    protected UserInfoRepresentation createdBy = null;

    @JsonProperty("isLink")
    protected Boolean isLink = null;

    @JsonProperty("content")
    protected ContentInfoRepresentation content = null;

    @JsonProperty("aspectNames")
    protected List<String> aspects = new ArrayList<>();

    @JsonProperty("properties")
    protected Map<String, Object> properties;

    @JsonProperty("allowableOperations")
    protected List<String> allowableOperations = new ArrayList<String>();

    @JsonProperty("path")
    protected PathInfoRepresentation path;

    @JsonProperty("association")
    protected AssociationInfoRepresentation association;

    @JsonProperty("permissions")
    private PermissionsInfoRepresentation permissions = null;

    // ///////////////////////////////////////////////////////////////////////////
    // GETTERS & SETTERS
    // ///////////////////////////////////////////////////////////////////////////

    /**
     * Get id
     *
     * @return id
     **/
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get parentId
     *
     * @return parentId
     **/
    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    /**
     * Get name
     *
     * @return name
     **/
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get nodeType
     *
     * @return nodeType
     **/
    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    /**
     * Get isFolder
     *
     * @return isFolder
     **/
    public Boolean isFolder() {
        return isFolder;
    }

    public void setIsFolder(Boolean isFolder) {
        this.isFolder = isFolder;
    }

    /**
     * Get isFile
     *
     * @return isFile
     **/
    public Boolean isFile() {
        return isFile;
    }

    public void setIsFile(Boolean isFile) {
        this.isFile = isFile;
    }

    public Boolean isLocked() {
        return isLocked;
    }

    public void setIsLocked(Boolean locked) {
        isLocked = locked;
    }

    /**
     * Get modifiedAt
     *
     * @return modifiedAt
     **/
    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    /**
     * Get modifiedByUser
     *
     * @return modifiedByUser
     **/
    public UserInfoRepresentation getModifiedByUser() {
        return modifiedBy;
    }

    public void setModifiedByUser(UserInfoRepresentation modifiedByUser) {
        this.modifiedBy = modifiedByUser;
    }

    /**
     * Get createdAt
     *
     * @return createdAt
     **/
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Get createdByUser
     *
     * @return createdByUser
     **/
    public UserInfoRepresentation getCreatedByUser() {
        return createdBy;
    }

    public void setCreatedByUser(UserInfoRepresentation createdByUser) {
        this.createdBy = createdByUser;
    }

    /**
     * Get content
     *
     * @return content
     **/
    public ContentInfoRepresentation getContent() {
        return content;
    }

    public void setContent(ContentInfoRepresentation content) {
        this.content = content;
    }

    /**
     * Get aspects
     *
     * @return aspects
     **/
    public List<String> getAspects() {
        return aspects;
    }

    public void setAspects(List<String> aspects) {
        this.aspects = aspects;
    }

    public boolean hasAspects(String aspectName) {
        return aspects != null && aspects.contains(aspectName);
    }

    /**
     * Get properties
     *
     * @return properties
     **/
    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    /**
     * Get association
     *
     * @return Associations
     **/
    public AssociationInfoRepresentation getAssociation() {
        return association;
    }

    public void setAssociation(AssociationInfoRepresentation association) {
        this.association = association;
    }

    /**
     * Get allowableOperations
     *
     * @return allowableOperations
     **/
    public List<String> getAllowableOperations() {
        return allowableOperations;
    }

    public void setAllowableOperations(List<String> allowableOperations) {
        this.allowableOperations = allowableOperations;
    }

    public Boolean isLink() {
        return isLink;
    }

    public void setLink(Boolean link) {
        isLink = link;
    }

    public PathInfoRepresentation getPath() {
        return path;
    }

    public void setPath(PathInfoRepresentation path) {
        this.path = path;
    }

    /**
     * Get permissions
     *
     * @return permissions
     **/
    public PermissionsInfoRepresentation getPermissions() {
        return permissions;
    }

    public void setPermissions(PermissionsInfoRepresentation permissions) {
        this.permissions = permissions;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NodeRepresentation node = (NodeRepresentation) o;
        return Objects.equals(this.id, node.id) && Objects.equals(this.parentId, node.parentId)
                && Objects.equals(this.name, node.name) && Objects.equals(this.nodeType, node.nodeType)
                && Objects.equals(this.isFolder, node.isFolder) && Objects.equals(this.isFile, node.isFile)
                && Objects.equals(this.isLocked, node.isLocked) && Objects.equals(this.isLink, node.isLink)
                && Objects.equals(this.modifiedAt, node.modifiedAt) && Objects.equals(this.modifiedBy, node.modifiedBy)
                && Objects.equals(this.createdAt, node.createdAt) && Objects.equals(this.createdBy, node.createdBy)
                && Objects.equals(this.content, node.content) && Objects.equals(this.aspects, node.aspects)
                && Objects.equals(this.allowableOperations, node.allowableOperations)
                && Objects.equals(this.permissions, node.permissions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, parentId, name, nodeType, isFolder, isFile, isLocked, isLink, modifiedAt, modifiedBy,
                createdAt, createdBy, content, aspects, properties, allowableOperations, association, permissions);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class NodeRepresentation {, ");

        sb.append("    id: ").append(toIndentedString(id)).append(", ");
        sb.append("    parentId: ").append(toIndentedString(parentId)).append(", n");
        sb.append("    name: ").append(toIndentedString(name)).append(", ");
        sb.append("    nodeType: ").append(toIndentedString(nodeType)).append(", ");
        sb.append("    isFolder: ").append(toIndentedString(isFolder)).append(", ");
        sb.append("    isFile: ").append(toIndentedString(isFile)).append(", ");
        sb.append("    properties: ").append(toIndentedString(properties)).append(", ");
        sb.append("}");
        return sb.toString();
    }
}
