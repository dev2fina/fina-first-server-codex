package net.fina.first.ecm.version.model;

import net.fina.ecm.alfresco.api.core.model.representation.ContentInfoRepresentation;
import net.fina.ecm.alfresco.api.core.model.representation.UserInfoRepresentation;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class VersionMetaModel {
    private String id;
    private String versionComment;
    private String name;
    private String nodeType;
    private boolean isFolder;
    private boolean isFile;
    private Date modifiedAt;
    private UserInfoRepresentation modifiedByUser;
    private ContentInfoRepresentation content;
    private List<String> aspectNames;
    private Map<String, Object> properties;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersionComment() {
        return versionComment;
    }

    public void setVersionComment(String versionComment) {
        this.versionComment = versionComment;
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

    public boolean isFolder() {
        return isFolder;
    }

    public void setFolder(boolean folder) {
        isFolder = folder;
    }

    public boolean isFile() {
        return isFile;
    }

    public void setFile(boolean file) {
        isFile = file;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public UserInfoRepresentation getModifiedByUser() {
        return modifiedByUser;
    }

    public void setModifiedByUser(UserInfoRepresentation modifiedByUser) {
        this.modifiedByUser = modifiedByUser;
    }

    public ContentInfoRepresentation getContent() {
        return content;
    }

    public void setContent(ContentInfoRepresentation content) {
        this.content = content;
    }

    public List<String> getAspectNames() {
        return aspectNames;
    }

    public void setAspectNames(List<String> aspectNames) {
        this.aspectNames = aspectNames;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}
