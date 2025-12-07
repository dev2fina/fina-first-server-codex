package net.fina.server.license.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fina.auditlog.api.Audited;
import net.fina.common.client.fis.LicenceStatus;
import net.fina.server.fi.entity.Fi;
import net.fina.server.i18n.helper.Description;
import org.hibernate.annotations.Type;
import org.hibernate.envers.AuditJoinTable;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(name = "IN_LICENCES")
@Table(name = "IN_LICENCES")
@NamedQueries({
        @NamedQuery(name = "checkLicenseCodeUnique", query = "select l from IN_LICENCES as l WHERE trim(l.code)=:code and l.id<>:id ")
})
@org.hibernate.envers.Audited
public class Licence implements Serializable, Audited {

    @Id
    @SequenceGenerator(name = "in_licences_sequence", sequenceName = "in_licences_sequence", allocationSize = 1)
    @GeneratedValue(generator = "in_licences_sequence", strategy = GenerationType.SEQUENCE)
    @Column(name = "ID")
    private long id;

    @Version
    @Column(name = "OPTLOCK")
    @org.hibernate.envers.NotAudited
    private Integer version;

    @Column(name = "CODE", length = 12, nullable = false, unique = true)
    private String code;

    @Column(name = "CREATIONDATE")
    private Date creationDate;

    @Column(name = "DATEOFCHANGE")
    private Date dateOfChange;

    @Column(name = "REASONSTRID")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    private Description reasons;

    @Column(name = "OPERATIONAL")
    private LicenceStatus licenceStatus;

    @OneToOne
    @JoinColumn(name = "TYPEID")
    @NotAudited
    private LicenceType licenseType;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "BANKID")
    @NotAudited
    private Fi fi;

    @Column(name = "isdefault")
    private Boolean isDefault;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "LICENSE_ID")
    @AuditJoinTable(name = "IN_LICENSE_BANKING_OPERATIONS_JOIN_AUD", inverseJoinColumns = @JoinColumn(name = "OPERATION_ID"))
    @org.hibernate.envers.Audited(targetAuditMode = RelationTargetAuditMode.AUDITED)
    private List<LicenseBankingOperation> operations;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "LICENSE_ID")
    @AuditJoinTable(name = "IN_BANK_LICENSE_COMMENTS_AUD", inverseJoinColumns = @JoinColumn(name = "COMMENT_ID"))
    @org.hibernate.envers.Audited(targetAuditMode = RelationTargetAuditMode.AUDITED)
    private List<LicenseComment> comments;
    private transient String change;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getDateOfChange() {
        return dateOfChange;
    }

    public void setDateOfChange(Date dateOfChange) {
        this.dateOfChange = dateOfChange;
    }

    public Description getReasons() {
        return reasons;
    }

    public void setReasons(Description reasons) {
        this.reasons = reasons;
    }

    public LicenceStatus getLicenceStatus() {
        return licenceStatus;
    }

    public void setLicenceStatus(LicenceStatus licenceStatus) {
        this.licenceStatus = licenceStatus;
    }

    public LicenceType getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(LicenceType licenseType) {
        this.licenseType = licenseType;
    }

    public Fi getFi() {
        return fi;
    }

    public void setFi(Fi fi) {
        this.fi = fi;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public Boolean getIsDefault() {
        if (isDefault != null) {
            return isDefault;
        }
        isDefault = false;
        return false;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public List<LicenseBankingOperation> getOperations() {
        return operations == null ? new ArrayList<>() : operations;
    }

    public void setOperations(List<LicenseBankingOperation> operations) {
        this.operations = operations;
    }

    public List<LicenseComment> getComments() {
        return comments == null ? new ArrayList<>() : comments;
    }

    public void setComments(List<LicenseComment> comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "License [id=" + id + ", version=" + version + ", code=" + code + "]";
    }
}
