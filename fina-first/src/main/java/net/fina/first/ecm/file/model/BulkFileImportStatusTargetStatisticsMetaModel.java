package net.fina.first.ecm.file.model;

public class BulkFileImportStatusTargetStatisticsMetaModel {
    private byte spaceNodesCreated;
    private byte spaceNodesReplaced;
    private byte spaceNodesSkipped;
    private byte spacePropertiesWritten;
    private byte contentNodesCreated;
    private byte contentNodesReplaced;
    private byte contentNodesSkipped;
    private int contentBytesWritten;
    private short contentPropertiesWritten;
    private byte contentVersionsCreated;
    private byte contentVersionsBytesWritten;
    private byte contentVersionsPropertiesWritten;

    public byte getSpaceNodesCreated() {
        return spaceNodesCreated;
    }

    public void setSpaceNodesCreated(byte spaceNodesCreated) {
        this.spaceNodesCreated = spaceNodesCreated;
    }

    public byte getSpaceNodesReplaced() {
        return spaceNodesReplaced;
    }

    public void setSpaceNodesReplaced(byte spaceNodesReplaced) {
        this.spaceNodesReplaced = spaceNodesReplaced;
    }

    public byte getSpaceNodesSkipped() {
        return spaceNodesSkipped;
    }

    public void setSpaceNodesSkipped(byte spaceNodesSkipped) {
        this.spaceNodesSkipped = spaceNodesSkipped;
    }

    public byte getSpacePropertiesWritten() {
        return spacePropertiesWritten;
    }

    public void setSpacePropertiesWritten(byte spacePropertiesWritten) {
        this.spacePropertiesWritten = spacePropertiesWritten;
    }

    public byte getContentNodesCreated() {
        return contentNodesCreated;
    }

    public void setContentNodesCreated(byte contentNodesCreated) {
        this.contentNodesCreated = contentNodesCreated;
    }

    public byte getContentNodesReplaced() {
        return contentNodesReplaced;
    }

    public void setContentNodesReplaced(byte contentNodesReplaced) {
        this.contentNodesReplaced = contentNodesReplaced;
    }

    public byte getContentNodesSkipped() {
        return contentNodesSkipped;
    }

    public void setContentNodesSkipped(byte contentNodesSkipped) {
        this.contentNodesSkipped = contentNodesSkipped;
    }

    public int getContentBytesWritten() {
        return contentBytesWritten;
    }

    public void setContentBytesWritten(int contentBytesWritten) {
        this.contentBytesWritten = contentBytesWritten;
    }

    public short getContentPropertiesWritten() {
        return contentPropertiesWritten;
    }

    public void setContentPropertiesWritten(short contentPropertiesWritten) {
        this.contentPropertiesWritten = contentPropertiesWritten;
    }

    public byte getContentVersionsCreated() {
        return contentVersionsCreated;
    }

    public void setContentVersionsCreated(byte contentVersionsCreated) {
        this.contentVersionsCreated = contentVersionsCreated;
    }

    public byte getContentVersionsBytesWritten() {
        return contentVersionsBytesWritten;
    }

    public void setContentVersionsBytesWritten(byte contentVersionsBytesWritten) {
        this.contentVersionsBytesWritten = contentVersionsBytesWritten;
    }

    public byte getContentVersionsPropertiesWritten() {
        return contentVersionsPropertiesWritten;
    }

    public void setContentVersionsPropertiesWritten(byte contentVersionsPropertiesWritten) {
        this.contentVersionsPropertiesWritten = contentVersionsPropertiesWritten;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ECM Bulk File Import Status Target Statistics ... {\n");
        sb.append("    Space Nodes Created: ").append(spaceNodesCreated).append("\n");
        sb.append("    Space Nodes Replaced: ").append(spaceNodesReplaced).append("\n");
        sb.append("    Space Nodes Skipped: ").append(spaceNodesSkipped).append("\n");
        sb.append("    Space Properties Written: ").append(spacePropertiesWritten).append("\n");
        sb.append("    Content Nodes Created: ").append(contentNodesCreated).append("\n");
        sb.append("    Content Nodes Replaced: ").append(contentNodesReplaced).append("\n");
        sb.append("    Content Nodes Skipped: ").append(contentNodesSkipped).append("\n");
        sb.append("    Content Bytes Written: ").append(contentBytesWritten).append("\n");
        sb.append("    Content Properties Written: ").append(contentPropertiesWritten).append("\n");
        sb.append("    Content Versions Created: ").append(contentVersionsCreated).append("\n");
        sb.append("    Content Versions Bytes Written: ").append(contentVersionsBytesWritten).append("\n");
        sb.append("    Content Versions Properties Written: ").append(contentVersionsPropertiesWritten).append("\n");

        return sb.toString();
    }
}
