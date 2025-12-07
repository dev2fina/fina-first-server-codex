package net.fina.server.communicator.entity;

import jakarta.persistence.*;
import net.fina.auditlog.api.Audited;
import net.fina.common.client.constants.CommunicatorMessageStatus;
import net.fina.common.shared.comunicator.CommunicatorMessageMarkType;
import net.fina.server.security.entity.User;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity(name = "IN_COMMUNICATOR_MESSAGES")
@Table(name = "IN_COMMUNICATOR_MESSAGES")
public class CommunicatorMessage implements Serializable, Audited {

    @Id
    @SequenceGenerator(name = "com_message_sequence", sequenceName = "com_message_sequence", allocationSize = 1)
    @GeneratedValue(generator = "com_message_sequence", strategy = GenerationType.SEQUENCE)
    private long id;

    private long replyToId;

    private long replyToUserId;

    private String title;

    private String content;

    private Date creationDate;

    private Date sendDate;

    @Column(name = "STATUS")
    @Enumerated(EnumType.ORDINAL)
    private CommunicatorMessageStatus status;

    private Boolean sign;

    @Column(name = "REJECTION_NOTE")
    private String rejectionNote;

    @OneToOne
    @JoinColumn(name = "USERID")
    private User user;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "MESSAGE_ID")
    private List<MessageUser> users;

    @OneToOne
    @JoinColumn(name = "ACCEPTORID")
    private User acceptor;

    @Column(name = "LAST_CONVERSATION_MESSAGE_DATE")
    private Date lastConversationMessageDate;

    @Column(name = "IS_ROOT_BOOKMARKED")
    private boolean isRootBookmarked;

    @Column(name = "IS_DELETED")
    private boolean deleted;

    @Column(name = "HAS_ATTACHMENTS")
    private boolean hasAttachments;
    @Column(name = "RECIPIENTS_SIZE")
    private int recipientSize;
    @Column(name = "RECIPIENT_READ_DATE")
    private Date recipientReadDate;

    @Column(name = "MARK_TYPE")
    @Enumerated(EnumType.ORDINAL)
    private CommunicatorMessageMarkType markType;

    public CommunicatorMessage() {
    }

    public CommunicatorMessage(long id, long replyToId, long replyToUserId, String title, String content, java.sql.Timestamp creationDate, Date sendDate, CommunicatorMessageStatus status, Boolean sign, String rejectionNote, User user, boolean isRootBookmarked, boolean deleted, boolean hasAttachments, int recipientSize, Date recipientReadDate, Date lastConversationMessageDate) {
        this.id = id;
        this.replyToId = replyToId;
        this.replyToUserId = replyToUserId;
        this.title = title;
        this.content = content;
        this.creationDate = creationDate;
        this.sendDate = sendDate;
        this.status = status;
        this.sign = sign;
        this.rejectionNote = rejectionNote;
        this.user = user;
        this.isRootBookmarked = isRootBookmarked;
        this.deleted = deleted;
        this.hasAttachments = hasAttachments;
        this.recipientSize = recipientSize;
        this.recipientReadDate = recipientReadDate;
        this.lastConversationMessageDate = lastConversationMessageDate;
    }

    // USED IN QUERY
    public CommunicatorMessage(Long id, Long replyToId, Long replyToUserId, String title, String content,
                               Date creationDate, Date sendDate, CommunicatorMessageStatus status, Boolean sign, User user,
                               String rejectionNote, Date lastConversationMessageDate, boolean hasAttachments,
                               Date recipientReadDate, CommunicatorMessageMarkType markType, int recipientSize, int statusOrder) {
        this.id = id;
        this.replyToId = replyToId;
        this.replyToUserId = replyToUserId;
        this.title = title;
        this.content = content;
        this.creationDate = creationDate;
        this.sendDate = sendDate;
        this.status = status;
        this.sign = sign;
        this.user = user;
        this.rejectionNote = rejectionNote;
        this.lastConversationMessageDate = lastConversationMessageDate;
        this.hasAttachments = hasAttachments;
        this.recipientReadDate = recipientReadDate;
        this.recipientSize = recipientSize;
        this.markType = markType;
    }


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

    public CommunicatorMessageStatus getStatus() {
        return status;
    }

    public void setStatus(CommunicatorMessageStatus status) {
        this.status = status;
    }

    public Boolean getSign() {
        return sign;
    }

    public void setSign(Boolean sign) {
        this.sign = sign;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<MessageUser> getUsers() {
        return users;
    }

    public void setUsers(List<MessageUser> users) {
        this.users = users;
    }

    public User getAcceptor() {
        return acceptor;
    }

    public void setAcceptor(User acceptor) {
        this.acceptor = acceptor;
    }

    public String getRejectionNote() {
        return rejectionNote;
    }

    public void setRejectionNote(String rejectionNote) {
        this.rejectionNote = rejectionNote;
    }

    public Date getLastConversationMessageDate() {
        return lastConversationMessageDate;
    }

    public void setLastConversationMessageDate(Date lastConversationalMessageDate) {
        this.lastConversationMessageDate = lastConversationalMessageDate;
    }

    public long getReplyToUserId() {
        return replyToUserId;
    }

    public void setReplyToUserId(long replyToUserId) {
        this.replyToUserId = replyToUserId;
    }

    public boolean isRootBookmarked() {
        return isRootBookmarked;
    }

    public void setRootBookmarked(boolean rootBookmarked) {
        isRootBookmarked = rootBookmarked;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isHasAttachments() {
        return hasAttachments;
    }

    public void setHasAttachments(boolean hasAttachments) {
        this.hasAttachments = hasAttachments;
    }

    public int getRecipientSize() {
        return recipientSize;
    }

    public void setRecipientSize(int recipientSize) {
        this.recipientSize = recipientSize;
    }

    public Date getRecipientReadDate() {
        return recipientReadDate;
    }

    public void setRecipientReadDate(Date recipientReadDate) {
        this.recipientReadDate = recipientReadDate;
    }

    public CommunicatorMessageMarkType getMarkType() {
        return markType;
    }

    public void setMarkType(CommunicatorMessageMarkType markType) {
        this.markType = markType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CommunicatorMessage message = (CommunicatorMessage) o;

        return id == message.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
