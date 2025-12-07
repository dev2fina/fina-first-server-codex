package net.fina.first.ecm.file.model;

import java.util.Date;

public class BulkFileImportStatusMetaModel {
    private String currentStatus;
    private String resultOfLastExecution;
    private String sourceDirectory;
    private String targetSpace;
    private Date startDate;
    private Date endDate;
    private int durationInNS;
    private byte completedBatches;
    private BulkFileImportStatusSourceStatisticsMetaModel sourceStatistics;
    private BulkFileImportStatusTargetStatisticsMetaModel targetStatistics;

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getResultOfLastExecution() {
        return resultOfLastExecution;
    }

    public void setResultOfLastExecution(String resultOfLastExecution) {
        this.resultOfLastExecution = resultOfLastExecution;
    }

    public String getSourceDirectory() {
        return sourceDirectory;
    }

    public void setSourceDirectory(String sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }

    public String getTargetSpace() {
        return targetSpace;
    }

    public void setTargetSpace(String targetSpace) {
        this.targetSpace = targetSpace;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getDurationInNS() {
        return durationInNS;
    }

    public void setDurationInNS(int durationInNS) {
        this.durationInNS = durationInNS;
    }

    public byte getCompletedBatches() {
        return completedBatches;
    }

    public void setCompletedBatches(byte completedBatches) {
        this.completedBatches = completedBatches;
    }

    public BulkFileImportStatusSourceStatisticsMetaModel getSourceStatistics() {
        return sourceStatistics;
    }

    public void setSourceStatistics(BulkFileImportStatusSourceStatisticsMetaModel sourceStatistics) {
        this.sourceStatistics = sourceStatistics;
    }

    public BulkFileImportStatusTargetStatisticsMetaModel getTargetStatistics() {
        return targetStatistics;
    }

    public void setTargetStatistics(BulkFileImportStatusTargetStatisticsMetaModel targetStatistics) {
        this.targetStatistics = targetStatistics;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ECM Bulk File Import Status ... \n");
        sb.append("    Current Status: ").append(currentStatus).append("\n");
        sb.append("    Result Of Last Execution: ").append(resultOfLastExecution).append("\n");
        sb.append("    Duration In NS: ").append(durationInNS).append("\n");

        return sb.toString();
    }
}
