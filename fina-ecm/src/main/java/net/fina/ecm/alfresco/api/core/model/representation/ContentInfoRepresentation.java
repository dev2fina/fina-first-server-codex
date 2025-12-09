package net.fina.ecm.alfresco.api.core.model.representation;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.fina.ecm.alfresco.api.common.representation.AbstractRepresentation;

import java.util.Objects;

public class ContentInfoRepresentation extends AbstractRepresentation {
    @JsonProperty("mimeType")
    private String mimeType = null;

    @JsonProperty("mimeTypeName")
    private String mimeTypeName = null;

    @JsonProperty("sizeInBytes")
    private Long sizeInBytes = null;

    @JsonProperty("encoding")
    private String encoding = null;

    // ///////////////////////////////////////////////////////////////////////////
    // GETTERS & SETTERS
    // ///////////////////////////////////////////////////////////////////////////

    /**
     * Get mimeType
     *
     * @return mimeType
     **/
    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * Get mimeTypeName
     *
     * @return mimeTypeName
     **/
    public String getMimeTypeName() {
        return mimeTypeName;
    }

    public void setMimeTypeName(String mimeTypeName) {
        this.mimeTypeName = mimeTypeName;
    }

    /**
     * Get sizeInBytes
     *
     * @return sizeInBytes
     **/
    public long getSizeInBytes() {
        return sizeInBytes;
    }

    public void setSizeInBytes(long sizeInBytes) {
        this.sizeInBytes = sizeInBytes;
    }

    /**
     * Get encoding
     *
     * @return encoding
     **/
    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ContentInfoRepresentation contentInfo = (ContentInfoRepresentation) o;
        return Objects.equals(this.mimeType, contentInfo.mimeType)
                && Objects.equals(this.mimeTypeName, contentInfo.mimeTypeName)
                && Objects.equals(this.sizeInBytes, contentInfo.sizeInBytes)
                && Objects.equals(this.encoding, contentInfo.encoding);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mimeType, mimeTypeName, sizeInBytes, encoding);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ContentInfo {");

        sb.append("    mimeType: ").append(toIndentedString(mimeType)).append(", ");
        sb.append("    mimeTypeName: ").append(toIndentedString(mimeTypeName)).append(", ");
        sb.append("    sizeInBytes: ").append(toIndentedString(sizeInBytes)).append(", ");
        sb.append("    encoding: ").append(toIndentedString(encoding));
        sb.append("}");
        return sb.toString();
    }
}
