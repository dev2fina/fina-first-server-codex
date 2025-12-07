package net.fina.first.ecm.node.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.fina.ecm.alfresco.api.core.model.representation.*;
import net.fina.first.ecm.dictionary.model.ClassPropertyMetaModel;

import java.util.Date;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NodeMetaModel {
    private String id;
    private String parentId;
    private String name;
    private String nodeType;
    private Boolean isFolder;
    private Boolean isFile;
    private Boolean isLocked;
    private Date modifiedAt;
    private UserInfoRepresentation modifiedBy;
    private Date createdAt;
    private UserInfoRepresentation createdBy;
    private Boolean isLink;
    private ContentInfoRepresentation content;
    private List<String> aspects;
    private Map<String, Object> properties;
    private List<String> allowableOperations;
    private PathInfoRepresentation path;
    private AssociationInfoRepresentation association;
    private PermissionsInfoRepresentation permissions;
    private List<ClassPropertyMetaModel> classProperties;
    private Object site;
    private String versionComment;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
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

    public Boolean getFolder() {
        return isFolder;
    }

    public void setFolder(Boolean folder) {
        isFolder = folder;
    }

    public Boolean getFile() {
        return isFile;
    }

    public void setFile(Boolean file) {
        isFile = file;
    }

    public Boolean getLocked() {
        return isLocked;
    }

    public void setLocked(Boolean locked) {
        isLocked = locked;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public UserInfoRepresentation getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(UserInfoRepresentation modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public UserInfoRepresentation getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserInfoRepresentation createdBy) {
        this.createdBy = createdBy;
    }

    public Boolean getLink() {
        return isLink;
    }

    public void setLink(Boolean link) {
        isLink = link;
    }

    public ContentInfoRepresentation getContent() {
        return content;
    }

    public void setContent(ContentInfoRepresentation content) {
        this.content = content;
    }

    public List<String> getAspects() {
        return aspects;
    }

    public void setAspects(List<String> aspects) {
        this.aspects = aspects;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public List<String> getAllowableOperations() {
        return allowableOperations;
    }

    public void setAllowableOperations(List<String> allowableOperations) {
        this.allowableOperations = allowableOperations;
    }

    public PathInfoRepresentation getPath() {
        return path;
    }

    public void setPath(PathInfoRepresentation path) {
        this.path = path;
    }

    public AssociationInfoRepresentation getAssociation() {
        return association;
    }

    public void setAssociation(AssociationInfoRepresentation association) {
        this.association = association;
    }

    public PermissionsInfoRepresentation getPermissions() {
        return permissions;
    }

    public void setPermissions(PermissionsInfoRepresentation permissions) {
        this.permissions = permissions;
    }

    public List<ClassPropertyMetaModel> getClassProperties() {
        return classProperties;
    }

    public void setClassProperties(List<ClassPropertyMetaModel> classProperties) {
        this.classProperties = classProperties;
    }

    public Object getSite() {
        return site;
    }

    public void setSite(Object site) {
        this.site = site;
    }

    public String getVersionComment() {
        return versionComment;
    }

    public void setVersionComment(String versionComment) {
        this.versionComment = versionComment;
    }
}
