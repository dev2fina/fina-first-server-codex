package net.fina.ecm.alfresco.api.core.model.representation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import net.fina.ecm.alfresco.api.common.representation.AbstractRepresentation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@JsonTypeName(value = "entry")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SharedLinkRepresentation extends AbstractRepresentation {
    private String id;
    private Date expiresAt;
    private String nodeId;
    private String name;
    private Date modifiedAt;
    private UserInfoRepresentation modifiedByUser;
    private UserInfoRepresentation sharedByUser;
    private ContentInfoRepresentation content;
    private List<String> allowableOperations = new ArrayList<>();
    private PathInfoRepresentation path;

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
     * Get nodeId
     *
     * @return nodeId
     **/
    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    /**
     * Get expiresAt
     *
     * @return expiresAt
     **/
    public Date getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
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
     * Get sharedByUser
     *
     * @return sharedByUser
     **/
    public UserInfoRepresentation getSharedByUser() {
        return sharedByUser;
    }

    public void setSharedByUser(UserInfoRepresentation sharedByUser) {
        this.sharedByUser = sharedByUser;
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

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SharedLinkRepresentation nodeSharedLink = (SharedLinkRepresentation) o;
        return Objects.equals(this.id, nodeSharedLink.id) && Objects.equals(this.nodeId, nodeSharedLink.nodeId)
                && Objects.equals(this.name, nodeSharedLink.name)
                && Objects.equals(this.expiresAt, nodeSharedLink.expiresAt)
                && Objects.equals(this.modifiedAt, nodeSharedLink.modifiedAt)
                && Objects.equals(this.modifiedByUser, nodeSharedLink.modifiedByUser)
                && Objects.equals(this.sharedByUser, nodeSharedLink.sharedByUser)
                && Objects.equals(this.content, nodeSharedLink.content)
                && Objects.equals(this.allowableOperations, nodeSharedLink.allowableOperations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, expiresAt, nodeId, name, modifiedAt, modifiedByUser, sharedByUser, content,
                allowableOperations);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class NodeSharedLink {, ");

        sb.append("    id: ").append(toIndentedString(id)).append(", ");
        sb.append("    expiresAt: ").append(toIndentedString(expiresAt)).append(", ");
        sb.append("    nodeId: ").append(toIndentedString(nodeId)).append(", ");
        sb.append("    name: ").append(toIndentedString(name)).append(", ");
        sb.append("    modifiedAt: ").append(toIndentedString(modifiedAt)).append(", ");
        sb.append("    modifiedByUser: ").append(toIndentedString(modifiedByUser)).append(", ");
        sb.append("    sharedByUser: ").append(toIndentedString(sharedByUser)).append(", ");
        sb.append("    content: ").append(toIndentedString(content)).append(", ");
        sb.append("    allowableOperations: ").append(toIndentedString(allowableOperations)).append(", ");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    public String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace(", ", ",     ");
    }

    public PathInfoRepresentation getPath() {
        return path;
    }

    public void setPath(PathInfoRepresentation path) {
        this.path = path;
    }
}
