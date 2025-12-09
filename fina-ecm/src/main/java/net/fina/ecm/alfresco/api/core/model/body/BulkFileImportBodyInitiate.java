package net.fina.ecm.alfresco.api.core.model.body;

import net.fina.ecm.alfresco.api.common.representation.BaseRepresentation;

import jakarta.ws.rs.FormParam;

public class BulkFileImportBodyInitiate implements BaseRepresentation {

    @FormParam("sourceDirectory")
    private String sourceDirectory;

    @FormParam("targetPath")
    private String targetPath;

    @FormParam("targetNodeRef")
    private String targetNodeRef;

    @FormParam("existingFileMode")
    private BulkFileImportExistingFileMode existingFileMode;

    @FormParam("batchSize")
    private int batchSize;

    @FormParam("numThreads")
    private int numThreads;

    public String getSourceDirectory() {
        return sourceDirectory;
    }

    public void setSourceDirectory(String sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    public String getTargetNodeRef() {
        return targetNodeRef;
    }

    public void setTargetNodeRef(String targetNodeRef) {
        this.targetNodeRef = targetNodeRef;
    }

    public BulkFileImportExistingFileMode getExistingFileMode() {
        return existingFileMode;
    }

    public void setExistingFileMode(BulkFileImportExistingFileMode existingFileMode) {
        this.existingFileMode = existingFileMode;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public int getNumThreads() {
        return numThreads;
    }

    public void setNumThreads(int numThreads) {
        this.numThreads = numThreads;
    }

}
