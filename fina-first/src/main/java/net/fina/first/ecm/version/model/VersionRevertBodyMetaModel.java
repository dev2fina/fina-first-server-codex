package net.fina.first.ecm.version.model;

public class VersionRevertBodyMetaModel {
    private String comment;
    private boolean majorVersion;

    public VersionRevertBodyMetaModel() {
    }

    public VersionRevertBodyMetaModel(String comment, boolean majorVersion) {
        this.comment = comment;
        this.majorVersion = majorVersion;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isMajorVersion() {
        return majorVersion;
    }

    public void setMajorVersion(boolean majorVersion) {
        this.majorVersion = majorVersion;
    }
}
