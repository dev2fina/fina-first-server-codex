package net.fina.common.shared.comunicator;

import net.fina.common.client.constants.CommunicatorNotificationStatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class CommunicatorNotificationFullModel implements Serializable {

    private long id;
    private String title;
    private String content;
    private Date publishDate;
    private Date creationDate;
    private CommunicatorNotificationStatus status;
    private List<Long> userIds;
    private String user;
    private String userDescription;
    private Collection<CommunicatorAttachmentModel> attachments;
    private Boolean sign;
    private Long userId;
    private int recipientCount;
    private boolean hasAttachments;


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

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public CommunicatorNotificationStatus getStatus() {
        return status;
    }

    public void setStatus(CommunicatorNotificationStatus status) {
        this.status = status;
    }

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Collection<CommunicatorAttachmentModel> getAttachments() {
        return attachments == null ? new ArrayList<CommunicatorAttachmentModel>() : attachments;
    }

    public void setAttachments(Collection<CommunicatorAttachmentModel> attachments) {
        this.attachments = attachments;
    }

    public Boolean getSign() {
        return sign;
    }

    public void setSign(Boolean sign) {
        this.sign = sign;
    }

    public String getUserDescription() {
        return userDescription;
    }

    public void setUserDescription(String userDescription) {
        this.userDescription = userDescription;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getRecipientCount() {
        return recipientCount;
    }

    public void setRecipientCount(int recipientCount) {
        this.recipientCount = recipientCount;
    }

    public boolean isHasAttachments() {
        return hasAttachments;
    }

    public void setHasAttachments(boolean hasAttachments) {
        this.hasAttachments = hasAttachments;
    }
}
