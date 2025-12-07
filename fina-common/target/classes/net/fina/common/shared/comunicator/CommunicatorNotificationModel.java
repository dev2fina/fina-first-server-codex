package net.fina.common.shared.comunicator;

import net.fina.common.client.constants.CommunicatorReadStatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/*
 * Created by Oto Iantbelidze on 6/22/17.
 */
public class CommunicatorNotificationModel implements Serializable {
    private long id;
    private String title;
    private String content;
    private Date publishDate;
    private String from;
    private CommunicatorReadStatus status;
    private Collection<CommunicatorAttachmentModel> attachments;
    private Collection<CommunicatorNotificationUserModel> users;

    public CommunicatorNotificationModel() {
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
        return attachments == null ? new ArrayList<CommunicatorAttachmentModel>() : attachments;
    }

    public void setAttachments(Collection<CommunicatorAttachmentModel> attachments) {
        this.attachments = attachments;
    }

    public Collection<CommunicatorNotificationUserModel> getUsers() {
        return users;
    }

    public void setUsers(Collection<CommunicatorNotificationUserModel> users) {
        this.users = users;
    }
}
