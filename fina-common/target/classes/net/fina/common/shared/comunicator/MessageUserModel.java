package net.fina.common.shared.comunicator;


import net.fina.common.client.constants.CommunicatorReadStatus;

import java.io.Serializable;
import java.util.Date;

public class MessageUserModel implements Serializable {

    private long id;
    private String login;
    private String name;
    private CommunicatorReadStatus status;
    private long messageId;
    private boolean isNew;
    private boolean hasPendingMessage;
    private long pendingMessageCount;
    private boolean isNotReliedMessage;
    private String lastConversationMessage;

    private Date readDate;

    public MessageUserModel() {
    }

    public MessageUserModel(long id, long messageId) {
        this.id = id;
        this.messageId = messageId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CommunicatorReadStatus getStatus() {
        return status;
    }

    public void setStatus(CommunicatorReadStatus status) {
        this.status = status;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public boolean isHasPendingMessage() {
        return hasPendingMessage;
    }

    public void setHasPendingMessage(boolean hasPendingMessage) {
        this.hasPendingMessage = hasPendingMessage;
    }

    public long getPendingMessageCount() {
        return pendingMessageCount;
    }

    public void setPendingMessageCount(long pendingMessageCount) {
        this.pendingMessageCount = pendingMessageCount;
        this.hasPendingMessage = this.pendingMessageCount > 0;
    }

    public boolean isNotReliedMessage() {
        return isNotReliedMessage;
    }

    public void setNotReliedMessage(boolean notReliedMessage) {
        isNotReliedMessage = notReliedMessage;
    }

    public String getLastConversationMessage() {
        return lastConversationMessage;
    }

    public void setLastConversationMessage(String lastConversationMessage) {
        this.lastConversationMessage = lastConversationMessage;
    }

    public Date getReadDate() {
        return readDate;
    }

    public void setReadDate(Date readDate) {
        this.readDate = readDate;
    }
}
