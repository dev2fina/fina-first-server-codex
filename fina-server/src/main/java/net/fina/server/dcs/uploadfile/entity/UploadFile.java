package net.fina.server.dcs.uploadfile.entity;

import net.fina.auditlog.api.Audited;
import net.fina.common.client.dcs.UploadFileStatus;
import net.fina.common.client.dcs.UploadType;
import net.fina.server.dcs.uploadfile.util.UploadFileUtil;
import net.fina.server.dcs.uploadfile.model.ProcessEngine;
import net.fina.server.security.entity.User;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(name = "SYS_UPLOADEDFILE")
@Table(name = "SYS_UPLOADEDFILE")
public class UploadFile implements Serializable, Audited {
    /**
     * Upload File
     */
    @Serial
    private static final long serialVersionUID = -2222836361154992065L;

    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "upload_file_sequence", sequenceName = "upload_file_sequence", allocationSize = 1)
    @GeneratedValue(generator = "upload_file_sequence", strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(name = "FILENAME", length = 256)
    private String fileName;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "TYPE")
    private UploadType type;

//    @Basic(fetch = FetchType.LAZY)
    @Column(name = "UPLOADEDFILE")
    @Transient
    private byte[] uploadedFile;

    @Column(name = "UPLOADEDTIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date uploadedTime;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "reason", length = 255)
    private String reason;

    @Column(name = "PROTECTIONINFO", length = 255)
    private String protectioninfo;

    @Column(name = "BANKCODE", length = 20)
    private String bankCode;

    @ManyToOne
    @JoinColumn(name = "USERNAME")
    private User user;

    @Column(name = "HASUSERBANK")
    private Boolean hasUserBank;

    @Column(name = "NAMEVALID")
    private Boolean nameValid;

    @Column(name = "VERSIONVALID")
    private Boolean versionValid;

    @Column(name = "MATRIXVALID")
    private Boolean matrixValid;

    @Column(name = "REPOSITORY_FILE_ID")
    private String repositoryFileId;

    @Column(name = "REPOSITORY_FILE_VERSION_ID")
    private String repositoryFileVersionId;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "PROCESS_ENGINE")
    private ProcessEngine processEngine;

    @Column(name = "MATRIX_ID")
    private Long matrixId;

    @Transient
    private Date fromDate;
    @Transient
    private Date toDate;

    @Transient
    private List<String> bankCodes;

    @Transient
    private List<Long> userIds;

    public UploadFile() {
    }

    public UploadFile(UploadType type, String fileName) {
        this.fileName = fileName;
        this.type = type;
    }

    public UploadFile(long id, long userId, String login, String bankCode, String fileName, Date uploadedTime, String status, Boolean hasUserBank, Boolean nameValid, Boolean versionValid, Boolean matrixValid, UploadType type, String reason, String protectioninfo, ProcessEngine processEngine) {
        super();
        this.id = id;
        this.fileName = fileName;
        this.status = status;
        this.bankCode = bankCode;

        User user = new User();
        user.setId(userId);
        user.setLogin(login);

        this.user = user;

        this.hasUserBank = hasUserBank;
        this.nameValid = nameValid;
        this.versionValid = versionValid;
        this.matrixValid = matrixValid;
        this.uploadedTime = uploadedTime;
        this.type = type;

        setReason(reason);

        this.protectioninfo = protectioninfo;
        this.processEngine = processEngine;
    }

    public UploadFile(String fileName, long userId, String login, String email,  int userVersion, Timestamp uploadedTime) {
        this.fileName = fileName;
        this.uploadedTime = uploadedTime;

        User user = new User();
        user.setId(userId);
        user.setLogin(login);
        user.setEmail(email);
        user.setVersion(userVersion);

        this.user = user;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public UploadType getType() {
        return type;
    }

    public void setType(UploadType type) {
        this.type = type;
    }

    public byte[] getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(byte[] uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public Date getUploadedTime() {
        return uploadedTime;
    }

    public void setUploadedTime(Date uploadedTime) {
        this.uploadedTime = uploadedTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setStatus(UploadFileStatus status) {
        if (status != null) {
            this.status = Integer.toString(status.ordinal());
        }
    }

    public String getProtectioninfo() {
        return protectioninfo;
    }

    public void setProtectioninfo(String protectioninfo) {
        this.protectioninfo = protectioninfo;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getHasUserBank() {
        return hasUserBank;
    }

    public void setHasUserBank(Boolean hasUserBank) {
        this.hasUserBank = hasUserBank;
    }

    public Boolean isNameValid() {
        return nameValid;
    }

    public void setNameValid(Boolean nameValid) {
        this.nameValid = nameValid;
    }

    public Boolean getVersionValid() {
        return versionValid;
    }

    public void setVersionValid(Boolean versionValid) {
        this.versionValid = versionValid;
    }

    public Boolean getMatrixValid() {
        return matrixValid;
    }

    public void setMatrixValid(Boolean matrixValid) {
        this.matrixValid = matrixValid;
    }

    public String getRepositoryFileId() {
        return repositoryFileId;
    }

    public void setRepositoryFileId(String repositoryFileId) {
        this.repositoryFileId = repositoryFileId;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public String getReason() {
        return reason;
    }

    public List<String> getBankCodes() {
        return bankCodes;
    }

    public void setBankCodes(List<String> bankCodes) {
        this.bankCodes = bankCodes != null ? new ArrayList<>(bankCodes) : new ArrayList<>();
    }

    public String getRepositoryFileVersionId() {
        return repositoryFileVersionId;
    }

    public void setRepositoryFileVersionId(String repositoryFileVersionId) {
        this.repositoryFileVersionId = repositoryFileVersionId;
    }

    public ProcessEngine getProcessEngine() {
        return processEngine == null ? ProcessEngine.FINA : processEngine;
    }

    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    public void setReason(String reason) {
        if (reason != null) {
            if (reason.length() > 255) {
                StringBuilder sb = new StringBuilder(reason);
                sb.replace(251, sb.length(), "...");
                reason = sb.toString();
            }
        }
        this.reason = reason;
    }

    public Long getMatrixId() {
        return matrixId;
    }

    public void setMatrixId(Long matrixId) {
        this.matrixId = matrixId;
    }

    @Override
    public String toString() {
        return "UploadFile [id=" + id + ", fileName=" + fileName + ", SHA1=" + UploadFileUtil.sha1(this) + "]";
    }

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }
}
