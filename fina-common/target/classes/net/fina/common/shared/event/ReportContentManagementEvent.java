package net.fina.common.shared.event;

import net.fina.common.client.reports.ReportType;

public class ReportContentManagementEvent {
    private String repositoryNodeId;
    private String fileName;
    private ReportType reportType;
    private String notificationMails;
    private String userLogin;
    private byte[] content;

    public ReportContentManagementEvent(String repositoryNodeId, ReportType reportType, String fileName, String notificationMails,String userlogin, byte[] content) {
        this.repositoryNodeId = repositoryNodeId;
        this.fileName = fileName;
        this.content = content;
        this.reportType = reportType;
        this.notificationMails = notificationMails;
        this.userLogin=userlogin;
    }

    public String getRepositoryNodeId() {
        return repositoryNodeId;
    }

    public void setRepositoryNodeId(String repositoryNodeId) {
        this.repositoryNodeId = repositoryNodeId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }

    public String getNotificationMails() {
        return notificationMails;
    }

    public void setNotificationMails(String notificationMails) {
        this.notificationMails = notificationMails;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }
}
