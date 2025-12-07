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
public class VersionRepresentation extends AbstractRepresentation {
    @JsonProperty("id")
    private String id = null;

    @JsonProperty("versionComment")
    private String versionComment = null;

    @JsonProperty("name")
    private String name = null;

    @JsonProperty("nodeType")
    private String nodeType = null;

    @JsonProperty("isFolder")
    private Boolean isFolder = null;

    @JsonProperty("isFile")
    private Boolean isFile = null;

    @JsonProperty("modifiedAt")
    private Date modifiedAt = null;

    @JsonProperty("modifiedByUser")
    private UserInfoRepresentation modifiedByUser = null;

    @JsonProperty("content")
    private ContentInfoRepresentation content = null;

    @JsonProperty("aspectNames")
    private List<String> aspectNames = new ArrayList<>();

    @JsonProperty("properties")
    private Map<String, Object> properties = new HashMap<String, Object>();

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
     * Get versionComment
     *
     * @return versionComment
     **/
    public String getVersionComment() {
        return versionComment;
    }

    public void setVersionComment(String versionComment) {
        this.versionComment = versionComment;
    }

    /**
     * The name must not contain spaces or the following special characters: *
     * \" < > \\ / ? : and |. The character . must not be used at the end of the
     * name.
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
    public Boolean getIsFolder() {
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
    public Boolean getIsFile() {
        return isFile;
    }

    public void setIsFile(Boolean isFile) {
        this.isFile = isFile;
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
        return modifiedByUser;
    }

    public void setModifiedByUser(UserInfoRepresentation modifiedByUser) {
        this.modifiedByUser = modifiedByUser;
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
     * Get aspectNames
     *
     * @return aspectNames
     **/
    public List<String> getAspectNames() {
        return aspectNames;
    }

    public void setAspectNames(List<String> aspectNames) {
        this.aspectNames = aspectNames;
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

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        VersionRepresentation version = (VersionRepresentation) o;
        return Objects.equals(this.id, version.id) && Objects.equals(this.versionComment, version.versionComment)
                && Objects.equals(this.name, version.name) && Objects.equals(this.nodeType, version.nodeType)
                && Objects.equals(this.isFolder, version.isFolder) && Objects.equals(this.isFile, version.isFile)
                && Objects.equals(this.modifiedAt, version.modifiedAt)
                && Objects.equals(this.modifiedByUser, version.modifiedByUser)
                && Objects.equals(this.content, version.content)
                && Objects.equals(this.aspectNames, version.aspectNames)
                && Objects.equals(this.properties, version.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, versionComment, name, nodeType, isFolder, isFile, modifiedAt, modifiedByUser, content,
                aspectNames, properties);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Version {, ");

        sb.append("    id: ").append(toIndentedString(id)).append(", ");
        sb.append("    versionComment: ").append(toIndentedString(versionComment)).append(", ");
        sb.append("    name: ").append(toIndentedString(name)).append(", ");
        sb.append("    nodeType: ").append(toIndentedString(nodeType)).append(", ");
        sb.append("    isFolder: ").append(toIndentedString(isFolder)).append(", ");
        sb.append("    isFile: ").append(toIndentedString(isFile)).append(", ");
        sb.append("    modifiedAt: ").append(toIndentedString(modifiedAt)).append(", ");
        sb.append("    modifiedByUser: ").append(toIndentedString(modifiedByUser)).append(", ");
        sb.append("    content: ").append(toIndentedString(content)).append(", ");
        sb.append("    aspectNames: ").append(toIndentedString(aspectNames)).append(", ");
        sb.append("    properties: ").append(toIndentedString(properties)).append(", ");
        sb.append("}");
        return sb.toString();
    }

}
