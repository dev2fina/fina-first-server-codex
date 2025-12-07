package net.fina.common.shared.dashboard;

public class ReturnStatisticsMetaModel {

    private long submittedFiles;
    private long notSubmittedFiles;

    public ReturnStatisticsMetaModel() {}

    public long getSubmittedFiles() {
        return submittedFiles;
    }

    public void setSubmittedFiles(long submittedFiles) {
        this.submittedFiles = submittedFiles;
    }

    public long getNotSubmittedFiles() {
        return notSubmittedFiles;
    }

    public void setNotSubmittedFiles(long notSubmittedFiles) {
        this.notSubmittedFiles = notSubmittedFiles;
    }

}
