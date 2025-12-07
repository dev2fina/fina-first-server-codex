package net.fina.server.returns.entity;

import net.fina.auditlog.api.Audited;
import net.fina.common.client.constants.ImportStatus;
import net.fina.common.client.returns.ImportedFileType;
import net.fina.common.server.util.CommonUtil;
import net.fina.server.dcs.uploadfile.entity.UploadFile;
import net.fina.server.i18n.entity.Language;
import net.fina.server.security.entity.User;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity(name = "IN_IMPORTED_RETURNS")
@Table(name = "IN_IMPORTED_RETURNS")
@SuppressWarnings("serial")
public class ImportedReturn implements Serializable, Comparable<ImportedReturn>, Audited {

    @Id
    @SequenceGenerator(name = "IN_IMPORTED_RETURNS_sequence", sequenceName = "IN_IMPORTED_RETURNS_sequence", allocationSize = 1)
    @GeneratedValue(generator = "IN_IMPORTED_RETURNS_sequence", strategy = GenerationType.SEQUENCE)
    @Column(name = "ID")
    private int id;

    @Column(name = "RETURNCODE")
    private String returnCode;

    @Column(name = "BANKCODE")
    private String bankCode;

    @Column(name = "VERSIONCODE")
    private String versionCode;

    @Temporal(TemporalType.DATE)
    @Column(name = "PERIODSTART")
    private Date periodStart;

    @Temporal(TemporalType.DATE)
    @Column(name = "PERIODEND")
    private Date periodEnd;

    @ManyToOne()
    @JoinColumn(name = "USERID")
    private User user;

    @ManyToOne
    @JoinColumn(name = "LANGID")
    private Language language;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPLOADTIME")
    private Date uploadTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "IMPORTSTART")
    private Date importStart;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "IMPORTEND")
    private Date importEnd;

    @Column(name = "STATUS")
    @Enumerated(EnumType.ORDINAL)
    private ImportStatus status;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "CONTENT")
    private byte[] content;

    @Column(name = "MESSAGE")
    private String message;

    @Column(name = "TYPE")
    @Enumerated(EnumType.ORDINAL)
    private ImportedFileType type;

    @ManyToOne(optional = true)
    @JoinColumn(name = "xlsId", nullable = true)
    private UploadFile uploadFile;

    @Column(name = "REPOSITORY_FILE_ID")
    private String repositoryFileId;

    @Column(name = "REPOSITORY_FILE_VERSION_ID")
    private String repositoryFileVersionId;

    @Transient
    private int sequence;

    @Transient
    private String fullMessage;

    public ImportedReturn() {
    }

    public ImportedReturn(int id, String returnCode, String versionCode, Date importStart, Date importEnd, Date periodStart, Date periodEnd, String message, ImportedFileType type, ImportStatus status) {
        this.id = id;
        this.returnCode = returnCode;
        this.versionCode = versionCode;
        this.importStart = importStart;
        this.importEnd = importEnd;
        this.periodEnd = periodEnd;
        this.periodStart = periodStart;
        this.message = message;
        this.type = type;
        this.status = status;
    }

    public ImportedReturn(int id, String returnCode, String versionCode, Date importStart, Date importEnd, Date periodStart, Date periodEnd, String message, ImportedFileType type, ImportStatus status, String bankCode) {
        this.id = id;
        this.returnCode = returnCode;
        this.versionCode = versionCode;
        this.importStart = importStart;
        this.importEnd = importEnd;
        this.periodEnd = periodEnd;
        this.periodStart = periodStart;
        this.message = message;
        this.type = type;
        this.status = status;
        this.bankCode = bankCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public Date getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(Date periodStart) {
        this.periodStart = periodStart;
    }

    public Date getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(Date periodEnd) {
        this.periodEnd = periodEnd;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    public Date getImportStart() {
        return importStart;
    }

    public void setImportStart(Date importStart) {
        this.importStart = importStart;
    }

    public Date getImportEnd() {
        return importEnd;
    }

    public void setImportEnd(Date importEnd) {
        this.importEnd = importEnd;
    }

    public ImportStatus getStatus() {
        return status;
    }

    public void setStatus(ImportStatus status) {
        this.status = status;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = CommonUtil.left(message, 2000);
        this.fullMessage = message;
    }

    public ImportedFileType getType() {
        return type;
    }

    public void setType(ImportedFileType type) {
        this.type = type;
    }

    public UploadFile getUploadFile() {
        return uploadFile;
    }

    public void setUploadFile(UploadFile uploadFile) {
        this.uploadFile = uploadFile;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getRepositoryFileId() {
        return repositoryFileId;
    }

    public void setRepositoryFileId(String repositoryFileId) {
        this.repositoryFileId = repositoryFileId;
    }

    public String getRepositoryFileVersionId() {
        return repositoryFileVersionId;
    }

    public void setRepositoryFileVersionId(String repositoryFileVersionId) {
        this.repositoryFileVersionId = repositoryFileVersionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImportedReturn that = (ImportedReturn) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String getFullMessage() {
        return fullMessage;
    }

    @Override
    public String toString() {
        return "ImportedReturn{" +
                "id=" + id +
                ", returnCode='" + returnCode + '\'' +
                ", bankCode='" + bankCode + '\'' +
                ", versionCode='" + versionCode + '\'' +
                ", periodStart=" + periodStart +
                ", periodEnd=" + periodEnd +
                ", uploadTime=" + uploadTime +
                ", importStart=" + importStart +
                ", importEnd=" + importEnd +
                ", status=" + status +
                ", message='" + message + '\'' +
                ", type=" + type +
                ", repositoryFileId='" + repositoryFileId + '\'' +
                '}';
    }

    @Override
    public int compareTo(ImportedReturn o) {
        return Integer.compare(sequence, o.sequence);
    }
}
