package net.fina.first.ecm.file.model;

public class BulkFileImportStatusSourceStatisticsMetaModel {
    private String lastFileOrFolderProcessed;
    private byte filesScanned;
    private byte foldersScanned;
    private byte unreadableEntries;
    private byte contentFilesRead;
    private int contentBytesRead;
    private byte metadataFilesRead;
    private byte metadataBytesRead;
    private byte contentVersionFilesRead;
    private byte contentVersionBytesRead;
    private byte metadataVersionFilesRead;
    private byte metadataVersionBytesRead;

    public String getLastFileOrFolderProcessed() {
        return lastFileOrFolderProcessed;
    }

    public void setLastFileOrFolderProcessed(String lastFileOrFolderProcessed) {
        this.lastFileOrFolderProcessed = lastFileOrFolderProcessed;
    }

    public byte getFilesScanned() {
        return filesScanned;
    }

    public void setFilesScanned(byte filesScanned) {
        this.filesScanned = filesScanned;
    }

    public byte getFoldersScanned() {
        return foldersScanned;
    }

    public void setFoldersScanned(byte foldersScanned) {
        this.foldersScanned = foldersScanned;
    }

    public byte getUnreadableEntries() {
        return unreadableEntries;
    }

    public void setUnreadableEntries(byte unreadableEntries) {
        this.unreadableEntries = unreadableEntries;
    }

    public byte getContentFilesRead() {
        return contentFilesRead;
    }

    public void setContentFilesRead(byte contentFilesRead) {
        this.contentFilesRead = contentFilesRead;
    }

    public int getContentBytesRead() {
        return contentBytesRead;
    }

    public void setContentBytesRead(int contentBytesRead) {
        this.contentBytesRead = contentBytesRead;
    }

    public byte getMetadataFilesRead() {
        return metadataFilesRead;
    }

    public void setMetadataFilesRead(byte metadataFilesRead) {
        this.metadataFilesRead = metadataFilesRead;
    }

    public byte getMetadataBytesRead() {
        return metadataBytesRead;
    }

    public void setMetadataBytesRead(byte metadataBytesRead) {
        this.metadataBytesRead = metadataBytesRead;
    }

    public byte getContentVersionFilesRead() {
        return contentVersionFilesRead;
    }

    public void setContentVersionFilesRead(byte contentVersionFilesRead) {
        this.contentVersionFilesRead = contentVersionFilesRead;
    }

    public byte getContentVersionBytesRead() {
        return contentVersionBytesRead;
    }

    public void setContentVersionBytesRead(byte contentVersionBytesRead) {
        this.contentVersionBytesRead = contentVersionBytesRead;
    }

    public byte getMetadataVersionFilesRead() {
        return metadataVersionFilesRead;
    }

    public void setMetadataVersionFilesRead(byte metadataVersionFilesRead) {
        this.metadataVersionFilesRead = metadataVersionFilesRead;
    }

    public byte getMetadataVersionBytesRead() {
        return metadataVersionBytesRead;
    }

    public void setMetadataVersionBytesRead(byte metadataVersionBytesRead) {
        this.metadataVersionBytesRead = metadataVersionBytesRead;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ECM Bulk File Import Status Source Statistics ... \n");
        sb.append("    Last File Or Folder Processed: ").append(lastFileOrFolderProcessed).append("\n");
        sb.append("    Files Scanned: ").append(filesScanned).append("\n");
        sb.append("    Folders Scanned: ").append(foldersScanned).append("\n");
        sb.append("    Unreadable Entries: ").append(unreadableEntries).append("\n");
        sb.append("    Content Files Read: ").append(contentFilesRead).append("\n");
        sb.append("    Content Bytes Read: ").append(contentBytesRead).append("\n");
        sb.append("    Metadata Files Read: ").append(metadataFilesRead).append("\n");
        sb.append("    Metadata Bytes Read: ").append(metadataBytesRead).append("\n");
        sb.append("    Content Version Files Read: ").append(contentVersionFilesRead).append("\n");
        sb.append("    Content Version Bytes Read: ").append(contentVersionBytesRead).append("\n");
        sb.append("    Metadata Version Files Read: ").append(metadataVersionFilesRead).append("\n");
        sb.append("    Metadata Version Bytes Read: ").append(metadataVersionBytesRead).append("\n");

        return sb.toString();
    }
}
