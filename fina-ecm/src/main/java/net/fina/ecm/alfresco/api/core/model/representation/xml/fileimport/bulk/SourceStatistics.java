package net.fina.ecm.alfresco.api.core.model.representation.xml.fileimport.bulk;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class SourceStatistics {

    @XmlElement(name = "LastFileOrFolderProcessed")
    protected String lastFileOrFolderProcessed;
    @XmlElement(name = "FilesScanned")
    protected byte filesScanned;
    @XmlElement(name = "FoldersScanned")
    protected byte foldersScanned;
    @XmlElement(name = "UnreadableEntries")
    protected byte unreadableEntries;
    @XmlElement(name = "ContentFilesRead")
    protected byte contentFilesRead;
    @XmlElement(name = "ContentBytesRead")
    protected int contentBytesRead;
    @XmlElement(name = "MetadataFilesRead")
    protected byte metadataFilesRead;
    @XmlElement(name = "MetadataBytesRead")
    protected byte metadataBytesRead;
    @XmlElement(name = "ContentVersionFilesRead")
    protected byte contentVersionFilesRead;
    @XmlElement(name = "ContentVersionBytesRead")
    protected byte contentVersionBytesRead;
    @XmlElement(name = "MetadataVersionFilesRead")
    protected byte metadataVersionFilesRead;
    @XmlElement(name = "MetadataVersionBytesRead")
    protected byte metadataVersionBytesRead;

    /**
     * Gets the value of the lastFileOrFolderProcessed property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getLastFileOrFolderProcessed() {
        return lastFileOrFolderProcessed;
    }

    /**
     * Sets the value of the lastFileOrFolderProcessed property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setLastFileOrFolderProcessed(String value) {
        this.lastFileOrFolderProcessed = value;
    }

    /**
     * Gets the value of the filesScanned property.
     */
    public byte getFilesScanned() {
        return filesScanned;
    }

    /**
     * Sets the value of the filesScanned property.
     */
    public void setFilesScanned(byte value) {
        this.filesScanned = value;
    }

    /**
     * Gets the value of the foldersScanned property.
     */
    public byte getFoldersScanned() {
        return foldersScanned;
    }

    /**
     * Sets the value of the foldersScanned property.
     */
    public void setFoldersScanned(byte value) {
        this.foldersScanned = value;
    }

    /**
     * Gets the value of the unreadableEntries property.
     */
    public byte getUnreadableEntries() {
        return unreadableEntries;
    }

    /**
     * Sets the value of the unreadableEntries property.
     */
    public void setUnreadableEntries(byte value) {
        this.unreadableEntries = value;
    }

    /**
     * Gets the value of the contentFilesRead property.
     */
    public byte getContentFilesRead() {
        return contentFilesRead;
    }

    /**
     * Sets the value of the contentFilesRead property.
     */
    public void setContentFilesRead(byte value) {
        this.contentFilesRead = value;
    }

    /**
     * Gets the value of the contentBytesRead property.
     */
    public int getContentBytesRead() {
        return contentBytesRead;
    }

    /**
     * Sets the value of the contentBytesRead property.
     */
    public void setContentBytesRead(int value) {
        this.contentBytesRead = value;
    }

    /**
     * Gets the value of the metadataFilesRead property.
     */
    public byte getMetadataFilesRead() {
        return metadataFilesRead;
    }

    /**
     * Sets the value of the metadataFilesRead property.
     */
    public void setMetadataFilesRead(byte value) {
        this.metadataFilesRead = value;
    }

    /**
     * Gets the value of the metadataBytesRead property.
     */
    public byte getMetadataBytesRead() {
        return metadataBytesRead;
    }

    /**
     * Sets the value of the metadataBytesRead property.
     */
    public void setMetadataBytesRead(byte value) {
        this.metadataBytesRead = value;
    }

    /**
     * Gets the value of the contentVersionFilesRead property.
     */
    public byte getContentVersionFilesRead() {
        return contentVersionFilesRead;
    }

    /**
     * Sets the value of the contentVersionFilesRead property.
     */
    public void setContentVersionFilesRead(byte value) {
        this.contentVersionFilesRead = value;
    }

    /**
     * Gets the value of the contentVersionBytesRead property.
     */
    public byte getContentVersionBytesRead() {
        return contentVersionBytesRead;
    }

    /**
     * Sets the value of the contentVersionBytesRead property.
     */
    public void setContentVersionBytesRead(byte value) {
        this.contentVersionBytesRead = value;
    }

    /**
     * Gets the value of the metadataVersionFilesRead property.
     */
    public byte getMetadataVersionFilesRead() {
        return metadataVersionFilesRead;
    }

    /**
     * Sets the value of the metadataVersionFilesRead property.
     */
    public void setMetadataVersionFilesRead(byte value) {
        this.metadataVersionFilesRead = value;
    }

    /**
     * Gets the value of the metadataVersionBytesRead property.
     */
    public byte getMetadataVersionBytesRead() {
        return metadataVersionBytesRead;
    }

    /**
     * Sets the value of the metadataVersionBytesRead property.
     */
    public void setMetadataVersionBytesRead(byte value) {
        this.metadataVersionBytesRead = value;
    }

    @Override
    public String toString() {
        return "SourceStatistics{" +
                "lastFileOrFolderProcessed='" + lastFileOrFolderProcessed + '\'' +
                ", filesScanned=" + filesScanned +
                ", foldersScanned=" + foldersScanned +
                ", unreadableEntries=" + unreadableEntries +
                ", contentFilesRead=" + contentFilesRead +
                ", contentBytesRead=" + contentBytesRead +
                ", metadataFilesRead=" + metadataFilesRead +
                ", metadataBytesRead=" + metadataBytesRead +
                ", contentVersionFilesRead=" + contentVersionFilesRead +
                ", contentVersionBytesRead=" + contentVersionBytesRead +
                ", metadataVersionFilesRead=" + metadataVersionFilesRead +
                ", metadataVersionBytesRead=" + metadataVersionBytesRead +
                '}';
    }
}
