package net.fina.common.shared.comunicator;

import net.fina.common.client.constants.CommunicatorReadStatus;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

public class NotificationToUserModel implements Serializable {
    private long id;
    private String title;
    private String content;
    private Date publishDate;
    private String from;
    private CommunicatorReadStatus status;
    private Collection<CommunicatorAttachmentModel> attachments;
    private CommunicatorReadStatus readStatus;

    public NotificationToUserModel() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public CommunicatorReadStatus getStatus() {
        return status;
    }

    public void setStatus(CommunicatorReadStatus status) {
        this.status = status;
    }

    public Collection<CommunicatorAttachmentModel> getAttachments() {
        return attachments;
    }

    public void setAttachments(Collection<CommunicatorAttachmentModel> attachments) {
        this.attachments = attachments;
    }

    public CommunicatorReadStatus getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(CommunicatorReadStatus readStatus) {
        this.readStatus = readStatus;
    }
}
