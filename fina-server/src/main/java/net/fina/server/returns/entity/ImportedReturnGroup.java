package net.fina.server.returns.entity;

import jakarta.persistence.*;
import net.fina.common.client.constants.ImportStatus;
import net.fina.common.client.returns.ImportedFileType;
import net.fina.common.server.util.CommonUtil;
import net.fina.server.dcs.uploadfile.entity.UploadFile;
import net.fina.server.security.entity.User;

import java.util.Date;
import java.util.Objects;

@Entity(name = "IN_IMPORTED_RETURNS_GROUP")
@Table(name = "IN_IMPORTED_RETURNS_GROUP")
public class ImportedReturnGroup {

    @Id
    @SequenceGenerator(name = "IN_IMPORTED_RETURNS_GROUP_sequence", sequenceName = "IN_IMPORTED_RETURNS_GROUP_sequence", allocationSize = 1)
    @GeneratedValue(generator = "IN_IMPORTED_RETURNS_GROUP_sequence", strategy = GenerationType.SEQUENCE)
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

    @Column(name = "TYPE")
    @Enumerated(EnumType.ORDINAL)
    private ImportedFileType type;

    @ManyToOne(optional = true)
    @JoinColumn(name = "xlsId", nullable = true)
    private UploadFile uploadFile;

    public ImportedReturnGroup() {
    }


    public ImportedReturnGroup(ImportedReturn importedReturn) {
        this.returnCode = importedReturn.getReturnCode();
        this.bankCode = importedReturn.getBankCode();
        this.versionCode = importedReturn.getVersionCode();
        this.periodEnd = importedReturn.getPeriodEnd();
        this.periodStart = importedReturn.getPeriodStart();
        this.type = importedReturn.getType();
        this.uploadFile = importedReturn.getUploadFile();
        this.user = importedReturn.getUser();
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImportedReturnGroup that = (ImportedReturnGroup) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ImportedReturnGroup{" +
                "id=" + id +
                ", returnCode='" + returnCode + '\'' +
                ", bankCode='" + bankCode + '\'' +
                ", versionCode='" + versionCode + '\'' +
                ", periodStart=" + periodStart +
                ", periodEnd=" + periodEnd +
                ", type=" + type +
                '}';
    }
}
