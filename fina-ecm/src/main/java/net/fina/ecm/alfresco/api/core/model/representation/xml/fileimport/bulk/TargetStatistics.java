package net.fina.ecm.alfresco.api.core.model.representation.xml.fileimport.bulk;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class TargetStatistics {

    @XmlElement(name = "SpaceNodesCreated")
    protected byte spaceNodesCreated;
    @XmlElement(name = "SpaceNodesReplaced")
    protected byte spaceNodesReplaced;
    @XmlElement(name = "SpaceNodesSkipped")
    protected byte spaceNodesSkipped;
    @XmlElement(name = "SpacePropertiesWritten")
    protected byte spacePropertiesWritten;
    @XmlElement(name = "ContentNodesCreated")
    protected byte contentNodesCreated;
    @XmlElement(name = "ContentNodesReplaced")
    protected byte contentNodesReplaced;
    @XmlElement(name = "ContentNodesSkipped")
    protected byte contentNodesSkipped;
    @XmlElement(name = "ContentBytesWritten")
    protected int contentBytesWritten;
    @XmlElement(name = "ContentPropertiesWritten")
    protected short contentPropertiesWritten;
    @XmlElement(name = "ContentVersionsCreated")
    protected byte contentVersionsCreated;
    @XmlElement(name = "ContentVersionsBytesWritten")
    protected byte contentVersionsBytesWritten;
    @XmlElement(name = "ContentVersionsPropertiesWritten")
    protected byte contentVersionsPropertiesWritten;

    /**
     * Gets the value of the spaceNodesCreated property.
     */
    public byte getSpaceNodesCreated() {
        return spaceNodesCreated;
    }

    /**
     * Sets the value of the spaceNodesCreated property.
     */
    public void setSpaceNodesCreated(byte value) {
        this.spaceNodesCreated = value;
    }

    /**
     * Gets the value of the spaceNodesReplaced property.
     */
    public byte getSpaceNodesReplaced() {
        return spaceNodesReplaced;
    }

    /**
     * Sets the value of the spaceNodesReplaced property.
     */
    public void setSpaceNodesReplaced(byte value) {
        this.spaceNodesReplaced = value;
    }

    /**
     * Gets the value of the spaceNodesSkipped property.
     */
    public byte getSpaceNodesSkipped() {
        return spaceNodesSkipped;
    }

    /**
     * Sets the value of the spaceNodesSkipped property.
     */
    public void setSpaceNodesSkipped(byte value) {
        this.spaceNodesSkipped = value;
    }

    /**
     * Gets the value of the spacePropertiesWritten property.
     */
    public byte getSpacePropertiesWritten() {
        return spacePropertiesWritten;
    }

    /**
     * Sets the value of the spacePropertiesWritten property.
     */
    public void setSpacePropertiesWritten(byte value) {
        this.spacePropertiesWritten = value;
    }

    /**
     * Gets the value of the contentNodesCreated property.
     */
    public byte getContentNodesCreated() {
        return contentNodesCreated;
    }

    /**
     * Sets the value of the contentNodesCreated property.
     */
    public void setContentNodesCreated(byte value) {
        this.contentNodesCreated = value;
    }

    /**
     * Gets the value of the contentNodesReplaced property.
     */
    public byte getContentNodesReplaced() {
        return contentNodesReplaced;
    }

    /**
     * Sets the value of the contentNodesReplaced property.
     */
    public void setContentNodesReplaced(byte value) {
        this.contentNodesReplaced = value;
    }

    /**
     * Gets the value of the contentNodesSkipped property.
     */
    public byte getContentNodesSkipped() {
        return contentNodesSkipped;
    }

    /**
     * Sets the value of the contentNodesSkipped property.
     */
    public void setContentNodesSkipped(byte value) {
        this.contentNodesSkipped = value;
    }

    /**
     * Gets the value of the contentBytesWritten property.
     */
    public int getContentBytesWritten() {
        return contentBytesWritten;
    }

    /**
     * Sets the value of the contentBytesWritten property.
     */
    public void setContentBytesWritten(int value) {
        this.contentBytesWritten = value;
    }

    /**
     * Gets the value of the contentPropertiesWritten property.
     */
    public short getContentPropertiesWritten() {
        return contentPropertiesWritten;
    }

    /**
     * Sets the value of the contentPropertiesWritten property.
     */
    public void setContentPropertiesWritten(short value) {
        this.contentPropertiesWritten = value;
    }

    /**
     * Gets the value of the contentVersionsCreated property.
     */
    public byte getContentVersionsCreated() {
        return contentVersionsCreated;
    }

    /**
     * Sets the value of the contentVersionsCreated property.
     */
    public void setContentVersionsCreated(byte value) {
        this.contentVersionsCreated = value;
    }

    /**
     * Gets the value of the contentVersionsBytesWritten property.
     */
    public byte getContentVersionsBytesWritten() {
        return contentVersionsBytesWritten;
    }

    /**
     * Sets the value of the contentVersionsBytesWritten property.
     */
    public void setContentVersionsBytesWritten(byte value) {
        this.contentVersionsBytesWritten = value;
    }

    /**
     * Gets the value of the contentVersionsPropertiesWritten property.
     */
    public byte getContentVersionsPropertiesWritten() {
        return contentVersionsPropertiesWritten;
    }

    /**
     * Sets the value of the contentVersionsPropertiesWritten property.
     */
    public void setContentVersionsPropertiesWritten(byte value) {
        this.contentVersionsPropertiesWritten = value;
    }

    @Override
    public String toString() {
        return "TargetStatistics{" +
                "spaceNodesCreated=" + spaceNodesCreated +
                ", spaceNodesReplaced=" + spaceNodesReplaced +
                ", spaceNodesSkipped=" + spaceNodesSkipped +
                ", spacePropertiesWritten=" + spacePropertiesWritten +
                ", contentNodesCreated=" + contentNodesCreated +
                ", contentNodesReplaced=" + contentNodesReplaced +
                ", contentNodesSkipped=" + contentNodesSkipped +
                ", contentBytesWritten=" + contentBytesWritten +
                ", contentPropertiesWritten=" + contentPropertiesWritten +
                ", contentVersionsCreated=" + contentVersionsCreated +
                ", contentVersionsBytesWritten=" + contentVersionsBytesWritten +
                ", contentVersionsPropertiesWritten=" + contentVersionsPropertiesWritten +
                '}';
    }
}