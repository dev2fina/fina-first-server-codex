package net.fina.common.shared.comunicator;

import net.fina.common.client.constants.CommunicatorMessageStatus;

import java.io.Serializable;
import java.util.*;

public class CommunicatorMessageModel implements Serializable {

    private long id;
    private long replyToId;
    private long replyToUserId;
    private String title;
    private String content;
    private Date creationDate;
    private Date sendDate;
    private String user;
    private CommunicatorMessageStatus status;
    private List<Long> userIds;
    private boolean self;
    private int newMessages;
    private boolean isNew;
    private boolean hasAcceptPermission;
    private Boolean sign;
    private String rejectionNote;
    private String userLogin;
    private Date lastConversationMessageDate;
    private boolean hasPendingMessage;
    private Date readDate;
    private Long userId;
    private int recipientCount;
    private boolean hasAttachments;
    private CommunicatorMessageMarkType markType;

    private Collection<CommunicatorAttachmentModel> attachments;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getReplyToId() {
        return replyToId;
    }

    public void setReplyToId(long replyToId) {
        this.replyToId = replyToId;
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

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public CommunicatorMessageStatus getStatus() {
        return status;
    }

    public void setStatus(CommunicatorMessageStatus status) {
        this.status = status;
    }

    public Collection<Long> getUserIds() {
        return userIds == null ? new ArrayList<Long>() : new HashSet<Long>(userIds);
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }

    public boolean isSelf() {
        return self;
    }

    public void setSelf(boolean self) {
        this.self = self;
    }

    public int getNewMessages() {
        return newMessages;
    }

    public void setNewMessages(int newMessages) {
        this.newMessages = newMessages;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public Collection<CommunicatorAttachmentModel> getAttachments() {
        return attachments == null ? new ArrayList<CommunicatorAttachmentModel>() : attachments;
    }

    public void setAttachments(Collection<CommunicatorAttachmentModel> attachments) {
        this.attachments = attachments;
    }

    public boolean isHasAcceptPermission() {
        return hasAcceptPermission;
    }

    public void setHasAcceptPermission(boolean hasAcceptPermission) {
        this.hasAcceptPermission = hasAcceptPermission;
    }

    public Boolean getSign() {
        return sign;
    }

    public void setSign(Boolean sign) {
        this.sign = sign;
    }

    public String getRejectionNote() {
        return rejectionNote;
    }

    public void setRejectionNote(String rejectionNote) {
        this.rejectionNote = rejectionNote;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public Date getLastConversationMessageDate() {
        return lastConversationMessageDate;
    }

    public void setLastConversationMessageDate(Date lastConversationMessageDate) {
        this.lastConversationMessageDate = lastConversationMessageDate;
    }

    public long getReplyToUserId() {
        return replyToUserId;
    }

    public void setReplyToUserId(long replyToUserId) {
        this.replyToUserId = replyToUserId;
    }

    public boolean isHasPendingMessage() {
        return hasPendingMessage;
    }

    public void setHasPendingMessage(boolean hasPendingMessage) {
        this.hasPendingMessage = hasPendingMessage;
    }

    public Date getReadDate() {
        return readDate;
    }

    public void setReadDate(Date readDate) {
        this.readDate = readDate;
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

    public CommunicatorMessageMarkType getMarkType() {
        return markType;
    }

    public void setMarkType(CommunicatorMessageMarkType markType) {
        this.markType = markType;
    }
}
