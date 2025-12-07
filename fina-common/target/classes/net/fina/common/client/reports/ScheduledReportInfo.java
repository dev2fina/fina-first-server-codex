package net.fina.common.client.reports;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@SuppressWarnings("serial")
public class ScheduledReportInfo implements Serializable {

    private long id;
    private int reportId;
    private int parentId;
    private String name;
    private String code;
    private String languageName;
    private String creatorUser;
    private boolean folder;
    private Date scheduleTime;
    private int reportInfoHashCode;
    private int langId;
    private long userId;
    private boolean onDemand;
    private int hashcode;
    private int status;
    private Map<Long, String> description;
    private int count;
    private ReportType reportType;
    private String fileStorageLocation;
    private String repositoryNodeId;
    private String repositoryFolderName;
    private String notificationMails;

    public ScheduledReportInfo() {
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCreatorUser() {
        return creatorUser;
    }

    public boolean isFolder() {
        return folder;
    }

    public String getLanguageName() {
        return languageName;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public int getReportInfoHashCode() {
        return reportInfoHashCode;
    }

    public int getLangId() {
        return langId;
    }

    public int getReportId() {
        return reportId;
    }

    public Date getScheduleTime() {
        return scheduleTime;
    }

    public int getStatus() {
        return status;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public boolean isOnDemand() {
        return onDemand;
    }

    public void setCreatorUser(String creatorUser) {
        this.creatorUser = creatorUser;
    }

    public void setFolder(boolean folder) {
        this.folder = folder;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setReportInfoHashCode(int reportInfoHashCode) {
        this.reportInfoHashCode = reportInfoHashCode;
    }

    public void setLangId(int langId) {
        this.langId = langId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public void setScheduleTime(Date scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setOnDemand(boolean onDemand) {
        this.onDemand = onDemand;
    }

    public String toString() {
        return this.name;
    }

    public int getHashcode() {
        return hashcode;
    }

    public void setHashcode(int hashcode) {
        this.hashcode = hashcode;
    }

    public int hashCode() {
        return reportId + langId + reportInfoHashCode + (onDemand ? 1 : 0) + ((scheduleTime != null) ? scheduleTime.hashCode() : 0);
    }

    public Map<Long, String> getDescription() {
        return description;
    }

    public void setDescription(Map<Long, String> description) {
        this.description = description;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }

    public String getFileStorageLocation() {
        return fileStorageLocation;
    }

    public void setFileStorageLocation(String fileStorageLocation) {
        this.fileStorageLocation = fileStorageLocation;
    }

    public String getRepositoryNodeId() {
        return repositoryNodeId;
    }

    public void setRepositoryNodeId(String repositoryNodeId) {
        this.repositoryNodeId = repositoryNodeId;
    }

    public String getNotificationMails() {
        return notificationMails;
    }

    public void setNotificationMails(String notificationMails) {
        this.notificationMails = notificationMails;
    }

    public String getRepositoryFolderName() {
        return repositoryFolderName;
    }

    public void setRepositoryFolderName(String repositoryFolderName) {
        this.repositoryFolderName = repositoryFolderName;
    }

    public boolean equals(Object obj) {

        boolean equals = false;
        if (obj instanceof ScheduledReportInfo) {

            ScheduledReportInfo sri = (ScheduledReportInfo) obj;

            if (isFolder() && folder == sri.folder && reportId == sri.reportId) {

                equals = true;
            } else if (reportId == sri.reportId && langId == sri.langId && reportInfoHashCode == sri.reportInfoHashCode && equalsScheduledTimes(sri)) {

                equals = true;
            }
        }
        return equals;
    }

    public int StoredReportshashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + langId;
        result = prime * result + reportId;
        return result;
    }

    private boolean equalsScheduledTimes(ScheduledReportInfo sri) {

        boolean equals = false;

        if (onDemand == sri.onDemand && (onDemand || scheduleTime.equals(sri.scheduleTime))) {
            equals = true;
        }

        return equals;
    }
}
