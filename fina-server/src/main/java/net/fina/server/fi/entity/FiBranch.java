package net.fina.server.fi.entity;

import jakarta.persistence.*;
import net.fina.auditlog.api.Audited;
import net.fina.server.i18n.helper.Description;
import net.fina.server.mdt.entity.MDTNode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Type;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.util.Date;

@Entity(name = "IN_BANK_BRANCHES")
@Table(name = "IN_BANK_BRANCHES")
@org.hibernate.envers.Audited
public class FiBranch implements Audited {

    @Column(name = "DISABLE")
    protected boolean disable;
    @Transient
    String regionPath;
    @Id
    @SequenceGenerator(name = "in_bank_branches_sequence", sequenceName = "in_bank_branches_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "in_bank_branches_sequence")
    @Column(name = "ID")
    private long id;
    @OneToOne
    @JoinColumn(name = "BANKREGIONSTRID")
    @org.hibernate.envers.Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private Region region;
    @Column(name = "NAMESTRID")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    private Description name;
    @Column(name = "SHORTNAMESTRID")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    private Description shortName;
    @Column(name = "ADDRESSSTRID")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    private Description address;
    @Column(name = "COMMENTSSTRID")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    private Description comment;
    @Column(name = "CREATIONDATE")
    @Temporal(TemporalType.DATE)
    private Date createDate;
    @Column(name = "DATEOFCHANGE")
    @Temporal(TemporalType.DATE)
    private Date changeDate;
    @Column(name = "BANKID")
    private long bankId;
    @Version
    @Column(name = "OPTLOCK")
    private int version;
    @Column(name = "CODE")
    private String code;
    @Column(name = "CLOSE_DATE")
    @Temporal(TemporalType.DATE)
    private Date closeDate;
    @Column(name = "SUSPENSION_DATE")
    @Temporal(TemporalType.DATE)
    private Date suspensionDate;
    @Column(name = "RENEWAL_DATE")
    @Temporal(TemporalType.DATE)
    private Date renewalDate;
    @Column(name = "EMAIL")
    private String email;
    @Column(name = "PHONE")
    private String phone;
    @Column(name = "REG_NUMBER")
    private String registrationNumber;
    @Column(name = "STORAGE")
    private Boolean isStorageAvailable;
    @Column(name = "DELETED")
    private boolean deleted;
    @OneToOne
    @JoinColumn(name = "MDT_DATA_NODE_ID")
    @NotAudited
    private MDTNode mdtNode;
    @ManyToOne
    @JoinColumn(name = "TYPEID")
    @NotAudited
    private FiBranchType fiBranchType;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "MANAGER_ID")
    @org.hibernate.envers.Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @NotFound(action = NotFoundAction.IGNORE)
    private FiPerson manager;
    @Temporal(TemporalType.DATE)
    @Column(name = "MANAGER_APPOINTMENT_DATE")
    private Date managerAppointmentDate;
    @ManyToOne()
    @JoinColumn(name = "CHIEF_ACCOUNTANT_ID")
    @org.hibernate.envers.Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @NotFound(action = NotFoundAction.IGNORE)
    private FiPerson chiefAccountant;
    @Temporal(TemporalType.DATE)
    @Column(name = "CHIEF_ACCOUNTANT_APPOINTMENT_DATE")
    private Date chiefAccountantAppointmentDate;

    public FiBranch() {
    }

    public FiBranch(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Description getName() {
        return name;
    }

    public void setName(Description name) {
        this.name = name;
    }

    public Description getShortName() {
        return shortName;
    }

    public void setShortName(Description shortName) {
        this.shortName = shortName;
    }

    public Description getAddress() {
        return address;
    }

    public void setAddress(Description address) {
        this.address = address;
    }

    public Description getComment() {
        return comment;
    }

    public void setComment(Description comment) {
        this.comment = comment;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }

    public long getBankId() {
        return bankId;
    }

    public void setBankId(long bankId) {
        this.bankId = bankId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public String getRegionPath() {
        return regionPath;
    }

    public void setRegionPath(String regionPath) {
        this.regionPath = regionPath;
    }

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(Date closeDate) {
        this.closeDate = closeDate;
    }

    public Date getSuspensionDate() {
        return suspensionDate;
    }

    public void setSuspensionDate(Date suspensionDate) {
        this.suspensionDate = suspensionDate;
    }

    public Date getRenewalDate() {
        return renewalDate;
    }

    public void setRenewalDate(Date renewalDate) {
        this.renewalDate = renewalDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public Boolean getStorageAvailable() {
        return isStorageAvailable;
    }

    public void setStorageAvailable(Boolean storageAvailable) {
        isStorageAvailable = storageAvailable;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public MDTNode getMdtNode() {
        return mdtNode;
    }

    public void setMdtNode(MDTNode mdtNode) {
        this.mdtNode = mdtNode;
    }

    public FiBranchType getFiBranchType() {
        return fiBranchType;
    }

    public void setFiBranchType(FiBranchType fiBranchType) {
        this.fiBranchType = fiBranchType;
    }

    public FiPerson getManager() {
        return manager;
    }

    public void setManager(FiPerson manager) {
        this.manager = manager;
    }

    public Date getManagerAppointmentDate() {
        return managerAppointmentDate;
    }

    public void setManagerAppointmentDate(Date managerAppointmentDate) {
        this.managerAppointmentDate = managerAppointmentDate;
    }

    public FiPerson getChiefAccountant() {
        return chiefAccountant;
    }

    public void setChiefAccountant(FiPerson chiefAccountant) {
        this.chiefAccountant = chiefAccountant;
    }

    public Date getChiefAccountantAppointmentDate() {
        return chiefAccountantAppointmentDate;
    }

    public void setChiefAccountantAppointmentDate(Date chiefAccountantAppointmentDate) {
        this.chiefAccountantAppointmentDate = chiefAccountantAppointmentDate;
    }

    @Override
    public String toString() {
        return "FiBranch [id=" + id +
                ", regionId=" +
                ", code=" + code +
                ", name=" + name +
                ", shortName=" + shortName +
                ", address=" + address +
                ", comment=" + comment +
                ", createDate=" + createDate +
                ", changeDate=" + changeDate +
                ", bankId=" + bankId +
                ", version=" + version +
                ", disable=" + disable + "]";
    }

}
