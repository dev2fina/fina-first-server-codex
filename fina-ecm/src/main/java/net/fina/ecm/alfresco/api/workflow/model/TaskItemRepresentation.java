package net.fina.ecm.alfresco.api.workflow.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

import java.util.Date;

@JsonTypeName(value = "entry")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskItemRepresentation implements BaseRepresentation {

    @JsonProperty("id")
    private String id;

    @JsonProperty("createdAt")
    private Date createdAt;

    @JsonProperty("size")
    private long size;

    @JsonProperty("createdBy")
    private String createdBy;

    @JsonProperty("modifiedAt")
    private Date modifiedAt;

    @JsonProperty("name")
    private String name;

    @JsonProperty("modifiedBy")
    private String modifiedBy;

    @JsonProperty("mimeType")
    private String mimeType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class TaskItemRepresentation {, ");

        sb.append("    id: ").append(toIndentedString(id)).append(", ");
        sb.append("    createdAt: ").append(toIndentedString(createdAt)).append(", ");
        sb.append("    size: ").append(toIndentedString(size)).append(", ");
        sb.append("    createdBy: ").append(toIndentedString(createdBy)).append(", ");
        sb.append("    modifiedAt: ").append(toIndentedString(modifiedAt)).append(", ");
        sb.append("    name: ").append(toIndentedString(name)).append(", ");
        sb.append("    modifiedBy: ").append(toIndentedString(modifiedBy)).append(", ");
        sb.append("    mimeType: ").append(toIndentedString(mimeType)).append(", ");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace(", ", ",     ");
    }
}
