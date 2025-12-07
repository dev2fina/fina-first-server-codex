package net.fina.common.shared.jcr;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class RepositoryNodeMetaModel implements Serializable {
    private String id;
    private String parentId;
    private String path;
    private String name;
    private List<DescriptionModel> descriptions;
    private byte[] content;
    private Date lastModified;
    private boolean folder;
    private boolean file;
    private Boolean deleted;
    private String versionId;
    private String externalId;

    public RepositoryNodeMetaModel() {
    }

    public RepositoryNodeMetaModel(String path, String name) {
        this.path = path;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public boolean isFolder() {
        return folder;
    }

    public void setFolder(boolean folder) {
        this.folder = folder;
    }

    public List<DescriptionModel> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(List<DescriptionModel> descriptions) {
        this.descriptions = descriptions;
    }

    public Boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public boolean isFile() {
        return file;
    }

    public void setFile(boolean file) {
        this.file = file;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }
}
