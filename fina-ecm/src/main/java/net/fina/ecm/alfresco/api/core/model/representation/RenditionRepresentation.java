package net.fina.ecm.alfresco.api.core.model.representation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import net.fina.ecm.alfresco.api.common.representation.AbstractRepresentation;

import java.util.Objects;

@JsonTypeName(value = "entry")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RenditionRepresentation extends AbstractRepresentation {
    @JsonProperty("id")
    private String id = null;

    @JsonProperty("content")
    private ContentInfoRepresentation content = null;

    @JsonProperty("status")
    private RenditionStatusEnum status = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ContentInfoRepresentation getContent() {
        return content;
    }

    public void setContent(ContentInfoRepresentation content) {
        this.content = content;
    }

    public RenditionStatusEnum getStatus() {
        return status;
    }

    public void setStatus(RenditionStatusEnum status) {
        this.status = status;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RenditionRepresentation rendition = (RenditionRepresentation) o;
        return Objects.equals(this.id, rendition.id) && Objects.equals(this.content, rendition.content)
                && Objects.equals(this.status, rendition.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, content, status);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Rendition {");

        sb.append("    id: ").append(toIndentedString(id)).append(", ");
        sb.append("    content: ").append(toIndentedString(content)).append(", ");
        sb.append("    status: ").append(toIndentedString(status)).append(" ");
        sb.append("}");
        return sb.toString();
    }
}
