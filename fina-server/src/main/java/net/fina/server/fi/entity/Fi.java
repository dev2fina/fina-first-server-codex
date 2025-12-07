package net.fina.server.fi.entity;

import jakarta.persistence.*;
import net.fina.auditlog.api.Audited;
import net.fina.common.client.fis.FiReorganization;
import net.fina.server.i18n.helper.Description;
import net.fina.server.legalperson.entity.metainfo.LegalPersonMetaInfo;
import net.fina.server.license.entity.Licence;
import net.fina.server.mdt.entity.MDTNode;
import net.fina.server.person.entity.CriminalRecord;
import net.fina.server.security.entity.User;
import org.hibernate.annotations.Type;
import org.hibernate.envers.AuditJoinTable;
import org.hibernate.envers.RelationTargetAuditMode;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

@Entity(name = "IN_BANKS")
@Table(name = "IN_BANKS")
@NamedQueries({
        @NamedQuery(name = "loadFis", query = "select b from IN_BANKS as b "),
        @NamedQuery(name = "loadFisByType", query = "select b from IN_BANKS as b where b.fiType.id=:typeId "),
        @NamedQuery(name = "findFiByCode", query = "select b from IN_BANKS as b where trim(b.code)=:code "),
        @NamedQuery(name = "checkFiCodeUnique", query = "select b from IN_BANKS as b WHERE trim(b.code)=:code and b.id<>:id "),
        @NamedQuery(name = "checkFiEmailUnique", query = "select b from IN_BANKS b where trim(LOWER(b.email))=:email and b.id<>:id "),
        @NamedQuery(name = "checkFiIdentificationCodeUnique", query = "select b from IN_BANKS b where trim(LOWER(b.identificationCode))=:identificationCode and b.id<>:id ")
})
@org.hibernate.envers.Audited
public class Fi implements Serializable, Audited {

    @Id
    @SequenceGenerator(name = "in_banks_sequence", sequenceName = "in_banks_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "in_banks_sequence")
    @Column(name = "ID")
    private long id;

    @Version
    @Column(name = "OPTLOCK")
    @org.hibernate.envers.NotAudited
    private int version;

    @Column(name = "CODE", length = 12, nullable = false, unique = true)
    private String code;

    @Column(name = "NAMESTRID")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    private Description description;

    @Column(name = "SHORTNAMESTRID")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    private Description shortName;

    @Column(name = "ADDRESSSTRID")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    private Description addressDescription;

    @Column(name = "PHONE", length = 40)
    private String phone;

    @Column(name = "FAX", length = 40)
    private String fax;

    @Column(name = "EMAIL", length = 40)
    private String email;

    @Column(name = "SWIFTCODE", length = 11)
    private String swiftCode;

    @Column(name = "REGIONID")
    private Long regionId;

    @Column(name = "IDENTIFICATION_CODE", unique = true)
    private String identificationCode;

    @Column(name = "LEGAL_FORM")
    private String legalForm;

    @Column(name = "DISABLE")
    private boolean disable;

    @OneToOne
    @JoinColumn(name = "TYPEID")
    @org.hibernate.envers.Audited(targetAuditMode = NOT_AUDITED)
    private FiType fiType;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "MM_BANK_GROUP", joinColumns = @JoinColumn(name = "BANKID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "BANKGROUPID", referencedColumnName = "ID"))
    @org.hibernate.envers.NotAudited
    private Collection<PeerGroup> peerGroup;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "fi")
    @org.hibernate.envers.NotAudited
    private Collection<Licence> licences;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "SYS_BANK_USERS", joinColumns = @JoinColumn(name = "BANKID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "USERID", referencedColumnName = "ID"))
    @org.hibernate.envers.NotAudited
    private Collection<User> users;

    @OneToOne
    @JoinColumn(name = "MDT_DATA_NODE_ID")
    @org.hibernate.envers.NotAudited
    private MDTNode mdtNode;

    @Column(name = "CONTACT_PERSON")
    private String contactPerson;

    @Column(name = "CREATED_AT")
    private Date createdAt;

    @Column(name = "MODIFIED_AT")
    private Date modifiedAt;

    @Column(name = "REGISTRATION_DATE")
    private Date registrationDate;

    @Column(name = "CLOSE_DATE")
    private Date closeDate;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "REORGANISATION")
    private FiReorganization reorganization;

    @Column(name = "REPRESENTATIVE")
    private String representativePerson;

    @Column(name = "MOBILE_OFFICES")
    private int numberOfMobileOffices;

    @Column(name = "EMPLOYES")
    private int numberOfEmploys;

    @Column(name = "WEB_SITE")
    private String webSite;
    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @JoinTable(name = "IN_BANK_CRIMINAL_RECORDS", joinColumns = @JoinColumn(name = "BANK_ID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "CRIMINAL_RECORD_ID", referencedColumnName = "ID"))
    @AuditJoinTable(name = "IN_BANK_CRIMINAL_RECORDS_AUD", inverseJoinColumns = @JoinColumn(name = "CRIMINAL_RECORD_ID", referencedColumnName = "ID"))
    @org.hibernate.envers.Audited(targetAuditMode = RelationTargetAuditMode.AUDITED)
    private List<CriminalRecord> criminalRecords;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "ADDITIONAL_INFO_ID")
    @org.hibernate.envers.Audited(targetAuditMode = RelationTargetAuditMode.AUDITED)
    private LegalPersonMetaInfo fiAdditionalInfo;

    @Column(name = "DECREE_NUMBER")
    private String decreeNumber;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "INSPECTION_END_DATE")
    private Date inspectionEndDate;

    @Transient
    private String defaultLicenceCode;

    public Fi() {
    }

    public Fi(long id) {
        this.id = id;
    }

    public Fi(long id, String code, Description description) {
        this.id = id;
        this.code = code;
        this.description = description;
    }

    public Fi(long id, String code, Description description, Description addressDescription, Timestamp createdAt, Timestamp modifiedAt, FiType fiType, String licenceCode) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.addressDescription = addressDescription;
        this.createdAt = createdAt;
        this.fiType = fiType;
        this.modifiedAt = modifiedAt;
        this.defaultLicenceCode = licenceCode;
    }

    public Fi(long id, String code, Description description, Description shortName) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.shortName = shortName;
    }

    public Fi(long id, String code, Description description, Description shortName, String identificationCode) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.shortName = shortName;
        this.identificationCode = identificationCode;
    }

    public Fi(long id, String code, Description description, Description shortName, FiType fiType) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.shortName = shortName;
        this.fiType = fiType;
    }


    public Fi(long id, String code) {
        this(id, code, null);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public FiType getFiType() {
        return fiType;
    }

    public void setFiType(FiType fiType) {
        this.fiType = fiType;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSwiftCode() {
        return swiftCode;
    }

    public void setSwiftCode(String swiftCode) {
        this.swiftCode = swiftCode;
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionID) {
        this.regionId = regionID;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public Description getShortName() {
        return shortName;
    }

    public void setShortName(Description shortName) {
        this.shortName = shortName;
    }

    public Description getAddressDescription() {
        return addressDescription;
    }

    public void setAddressDescription(Description addressDescription) {
        this.addressDescription = addressDescription;
    }

    public Collection<PeerGroup> getPeerGroup() {
        return peerGroup;
    }

    public void setPeerGroup(Collection<PeerGroup> peerGroup) {
        this.peerGroup = peerGroup;
    }

    public Collection<Licence> getLicences() {
        return licences;
    }

    public void setLicences(Collection<Licence> licences) {
        this.licences = licences;
    }

    public Collection<User> getUsers() {
        return users;
    }

    public void setUsers(Collection<User> users) {
        this.users = users;
    }

    public String getIdentificationCode() {
        return identificationCode;
    }

    public void setIdentificationCode(String identificationCode) {
        this.identificationCode = identificationCode;
    }

    public String getLegalForm() {
        return legalForm;
    }

    public void setLegalForm(String legalForm) {
        this.legalForm = legalForm;
    }

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    public MDTNode getMdtNode() {
        return mdtNode;
    }

    public void setMdtNode(MDTNode mdtNode) {
        this.mdtNode = mdtNode;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Date getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(Date closeDate) {
        this.closeDate = closeDate;
    }

    public FiReorganization getReorganization() {
        return reorganization;
    }

    public void setReorganization(FiReorganization reorganization) {
        this.reorganization = reorganization;
    }

    public String getRepresentativePerson() {
        return representativePerson;
    }

    public void setRepresentativePerson(String representativePerson) {
        this.representativePerson = representativePerson;
    }

    public int getNumberOfMobileOffices() {
        return numberOfMobileOffices;
    }

    public void setNumberOfMobileOffices(int numberOfMobileOffices) {
        this.numberOfMobileOffices = numberOfMobileOffices;
    }

    public String getWebSite() {
        return webSite;
    }

    public void setWebSite(String webSide) {
        this.webSite = webSide;
    }

    public int getNumberOfEmploys() {
        return numberOfEmploys;
    }

    public void setNumberOfEmploys(int numberOfEmploys) {
        this.numberOfEmploys = numberOfEmploys;
    }


    public List<CriminalRecord> getCriminalRecords() {
        return criminalRecords == null ? new ArrayList<>() : criminalRecords;
    }

    public void setCriminalRecords(List<CriminalRecord> criminalRecords) {
        this.criminalRecords = criminalRecords;
    }

    public LegalPersonMetaInfo getFiAdditionalInfo() {
        return fiAdditionalInfo;
    }

    public void setFiAdditionalInfo(LegalPersonMetaInfo fiAdditionalInfo) {
        this.fiAdditionalInfo = fiAdditionalInfo;
    }

    public String getDecreeNumber() {
        return decreeNumber;
    }

    public void setDecreeNumber(String decreeNumber) {
        this.decreeNumber = decreeNumber;
    }

    public Date getInspectionEndDate() {
        return inspectionEndDate;
    }

    public void setInspectionEndDate(Date inspectionEndDate) {
        this.inspectionEndDate = inspectionEndDate;
    }

    public String getDefaultLicenceCode() {
        return defaultLicenceCode;
    }

    public void setDefaultLicenceCode(String defaultLicenceCode) {
        this.defaultLicenceCode = defaultLicenceCode;
    }

    @Override
    public String toString() {
        return "Fi{" +
                "fiType=" + fiType +
                ", regionId=" + regionId +
                ", swiftCode='" + swiftCode + '\'' +
                ", email='" + email + '\'' +
                ", fax='" + fax + '\'' +
                ", phone='" + phone + '\'' +
                ", addressDescription=" + addressDescription +
                ", shortName=" + shortName +
                ", description=" + description +
                ", code='" + code + '\'' +
                ", version=" + version +
                ", id=" + id +
                ", contactPerson='" + contactPerson + '\'' +
                ", createdAt=" + createdAt +
                ", modifiedAt=" + modifiedAt +
                '}';
    }

}
