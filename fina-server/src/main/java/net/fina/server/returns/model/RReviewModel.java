package net.fina.server.returns.model;

import java.io.Serializable;

public class RReviewModel implements Serializable {
    private byte[] content;
    private String fileName;

    public RReviewModel() {
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
