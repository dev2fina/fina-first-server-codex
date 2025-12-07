package net.fina.server.communicator.entity;

import net.fina.common.client.constants.CommunicatorAttachmentType;

import jakarta.persistence.*;

@Entity
@Table(name = "IN_COMMUNICATOR_ATTACHEMENTS")
public class CommunicatorAttachment {

    @Id
    @SequenceGenerator(name = "com_attachement_sequence", sequenceName = "com_attachement_sequence", allocationSize = 1)
    @GeneratedValue(generator = "com_attachement_sequence", strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(name = "MESSAGE_ID")
    private long messageId;

    private String fileName;
    private byte[] content;
    @Column(name = "CONTENT_SIZE")
    private int contentSize;

    private CommunicatorAttachmentType type;

    private Boolean sign;

    public CommunicatorAttachment() {
    }

    public CommunicatorAttachment(long id, long messageId, String fileName, CommunicatorAttachmentType type, Boolean sign,int contentSize) {
        this.id = id;
        this.messageId = messageId;
        this.fileName = fileName;
        this.type = type;
        this.sign = sign;
        this.contentSize = contentSize;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
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

    public CommunicatorAttachmentType getType() {
        return type;
    }

    public void setType(CommunicatorAttachmentType type) {
        this.type = type;
    }

    public Boolean getSign() {
        return sign;
    }

    public void setSign(Boolean sign) {
        this.sign = sign;
    }

    public int getContentSize() {
        return contentSize;
    }

    public void setContentSize(int contentSize) {
        this.contentSize = contentSize;
    }
}
