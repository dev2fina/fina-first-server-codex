package net.fina.server.legislative.entity;

import jakarta.validation.constraints.Size;
import net.fina.auditlog.api.Audited;
import net.fina.server.fi.entity.FiType;
import net.fina.server.i18n.helper.Description;
import net.fina.server.security.entity.User;
import org.hibernate.annotations.Type;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity(name = "IN_LAW_DOCUMENT")
@Table(name = "IN_LAW_DOCUMENT")
public class LegislativeDocument implements Serializable, Audited {

    @Id
    @SequenceGenerator(name = "law_doc_sequence", sequenceName = "law_doc_sequence", allocationSize = 1)
    @GeneratedValue(generator = "law_doc_sequence", strategy = GenerationType.SEQUENCE)
    private long id;

    @Version
    @Column(name = "OPTLOCK")
    private Integer version;

    private String fileName;

    @Basic(fetch = FetchType.LAZY)
    private byte[] content;

    @Column(name = "NAMESTRID")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    protected Description description;

    @Temporal(TemporalType.TIMESTAMP)
    private Date publish;

    @Column
    private Boolean sign;

    @Column
    private Boolean notify;

    @OneToOne
    @JoinColumn(name = "USER_ID")
    private User publisher;

    @OneToOne
    @JoinColumn(name = "FI_TYPE_ID")
    private FiType fiType;

    @ManyToOne
    @JoinColumn(name = "CATEGORY_ID")
    private LegislativeDocumentCategory category;


    @Column(name = "CONTENT_SIZE")
    private int contentSize;

    public LegislativeDocument() {
    }

    public LegislativeDocument(long id, String fileName, Date publish, Boolean sign, Boolean notify, User publisher,
                               FiType fiType, LegislativeDocumentCategory category,
                               int contentSize, Integer version, Description description) {
        this.id = id;
        this.fileName = fileName;
        this.publish = publish;
        this.sign = sign;
        this.notify = notify;
        this.publisher = publisher;
        this.fiType = fiType;
        this.category = category;
        this.contentSize = contentSize;
        this.version = version;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
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

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public Date getPublish() {
        return publish;
    }

    public void setPublish(Date publish) {
        this.publish = publish;
    }

    public User getPublisher() {
        return publisher;
    }

    public void setPublisher(User publisher) {
        this.publisher = publisher;
    }

    public FiType getFiType() {
        return fiType;
    }

    public void setFiType(FiType fiType) {
        this.fiType = fiType;
    }

    public LegislativeDocumentCategory getCategory() {
        return category;
    }

    public void setCategory(LegislativeDocumentCategory category) {
        this.category = category;
    }

    public Boolean getSign() {
        return sign;
    }

    public void setSign(Boolean sign) {
        this.sign = sign;
    }

    public Boolean getNotify() {
        return notify;
    }

    public void setNotify(Boolean notify) {
        this.notify = notify;
    }

    public int getContentSize() {
        return contentSize;
    }

    public void setContentSize(int contentSize) {
        this.contentSize = contentSize;
    }
}
