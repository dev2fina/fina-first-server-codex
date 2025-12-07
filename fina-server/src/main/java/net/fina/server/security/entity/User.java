package net.fina.server.security.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import net.fina.auditlog.api.Audited;
import net.fina.common.shared.UserType;
import net.fina.server.fi.entity.Fi;
import net.fina.server.i18n.helper.Description;
import net.fina.server.matrix.entity.Matrix;
import net.fina.server.mdt.entity.UserMdt;
import net.fina.server.reports.entity.Report;
import net.fina.server.returns.entity.ReturnDefinition;
import net.fina.server.returns.entity.UserReturnVersion;
import org.hibernate.annotations.Type;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity(name = "SYS_USERS")
@Table(name = "SYS_USERS")
@NamedQueries({
        @NamedQuery(name = "loadUsers", query = "select u from SYS_USERS as u where u.deleted=:deleted order by u.id desc "),
        @NamedQuery(name = "loadUserByUserName", query = "select u from SYS_USERS as u where trim(LOWER(u.login))=:login"),
        @NamedQuery(name = "isChangePassword", query = "select u.changePassword from SYS_USERS as u where trim(LOWER(u.login))=:login"),
        @NamedQuery(name = "getUserIdByLogin", query = "select u.id from SYS_USERS as u where trim(LOWER(u.login))=:login"),
        @NamedQuery(name = "checkLoginUnique", query = "select u.id from SYS_USERS u WHERE trim(LOWER(u.login))=:login and u.id<>:id"),
        @NamedQuery(name = "checkEmailUnique", query = "select u.id from SYS_USERS u where trim(LOWER(u.email))=:email and u.id<>:id "),
        @NamedQuery(name = "checkPhoneUnique", query = "select u.id from SYS_USERS u where trim(LOWER(u.phone))=:phone and u.id<>:id "),
        @NamedQuery(name = "getUserTypeByLogin", query = "select u.userType from SYS_USERS u where trim(LOWER(u.login))=:login")
})
public class User implements Serializable, Audited {

    @Id
    @SequenceGenerator(name = "sys_users_sequence", sequenceName = "sys_users_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sys_users_sequence")
    @Column(name = "ID")
    private long id;

    @Version
    @Column(name = "OPTLOCK")
    private Integer version;

    @Column(name = "LOGIN", length = 15, unique = true, nullable = false)
    private String login;

    @Column(name = "PASSWORD", length = 40)
    private String password;

    @Column(name = "CHANGEPASSWORD")
    private boolean changePassword;

    @Column(name = "NAMESTRID")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    private Description description;

    @Column(name = "TITLESTRID")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    private Description titledescription;

    @Column(name = "PHONE", length = 40)
    private String phone;

    @Column(name = "EMAIL", length = 40)
    private String email;

    @Column(name = "BLOCKED")
    private boolean blocked;

    @Column(name = "LASTLOGINDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLoginDate;

    @Column(name = "LASTPASSWORDCHANGEDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastPasswordChangeDate;

    @OneToMany
    @JoinTable(name = "SYS_USER_PERMISSIONS", joinColumns = @JoinColumn(name = "USERID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "PERMISSIONID", referencedColumnName = "ID"))
    private Collection<Permission> permissions;

    @ManyToMany
    @JoinTable(name = "SYS_USERS_ROLES", joinColumns = @JoinColumn(name = "USERID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "ROLEID", referencedColumnName = "ID"))
    private Collection<Role> roles;

    @OneToMany
    @JoinTable(name = "SYS_USER_BANKS", joinColumns = @JoinColumn(name = "USERID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "BANKID", referencedColumnName = "ID"))
    private Collection<Fi> fis;

    @OneToMany
    @JoinTable(name = "SYS_USER_RETURNS", joinColumns = @JoinColumn(name = "USER_ID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "DEFINITION_ID", referencedColumnName = "ID"))
    private Collection<ReturnDefinition> returnDefinitions;

    @OneToMany
    @JoinTable(name = "SYS_USER_REPORTS", joinColumns = @JoinColumn(name = "USERID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "REPORTID", referencedColumnName = "ID"))
    private Collection<Report> reports;

    @JsonIgnore
    @OneToMany(mappedBy = "returnVersionId.user")
    private Collection<UserReturnVersion> returnVersions;

    @JsonIgnore
    @OneToMany(mappedBy = "userMdtId.user")
    private Collection<UserMdt> userMdts;

    @Column(name = "USERTYPE")
    @Enumerated(EnumType.ORDINAL)
    private UserType userType;

    @Column(name = "DELETED")
    private boolean deleted;

    @Column(name = "CONTACTPERSONSTRID")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    private Description contactPersonDescription;

    @Column(name = "CONTACT_PERSON_POSITION_STRID")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    private Description contactPersonPosition;

    @Column(name = "DISABLED")
    private boolean disabled;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "SYS_USER_MATRIX_FILE_MAPPING", joinColumns = @JoinColumn(name = "USER_ID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "MATRIX_ID", referencedColumnName = "ID"))
    private List<Matrix> permittedMatrixList;

    public User() {
    }

    public User(long id) {
        this.id = id;
    }

    public User(long id, Integer version) {
        this.id = id;
        this.version = version;
    }

    public User(long id, Integer version, boolean blocked, Date lastLoginDate, Date lastPasswordChangeDate) {
        this(id, version);
        this.blocked = blocked;
        this.lastLoginDate = lastLoginDate;
        this.lastPasswordChangeDate = lastPasswordChangeDate;
    }

    public User(long id, String login, Description description) {
        this.id = id;
        this.login = login;
        this.description = description;
    }

    public User(long id, String login, Description description, Description titleDescription, Description contactPerson, String phone, String mail, UserType userType, boolean blocked, boolean disabled, boolean changePassword, boolean deleted) {
        this.id = id;
        this.login = login;
        this.description = description;
        this.contactPersonDescription = contactPerson;
        this.titledescription = titleDescription;
        this.phone = phone;
        this.email = mail;
        this.userType = userType;
        this.blocked = blocked;
        this.disabled = disabled;
        this.changePassword = changePassword;
        this.deleted = deleted;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean getChangePassword() {
        return changePassword;
    }

    public void setChangePassword(boolean changePassword) {
        this.changePassword = changePassword;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public Description getTitledescription() {
        return titledescription;
    }

    public void setTitledescription(Description titledescription) {
        this.titledescription = titledescription;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean getBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public Date getLastPasswordChangeDate() {
        return lastPasswordChangeDate;
    }

    public void setLastPasswordChangeDate(Date lastPasswordChangeDate) {
        this.lastPasswordChangeDate = lastPasswordChangeDate;
    }

    public Collection<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Collection<Permission> permissions) {
        this.permissions = permissions;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Collection<ReturnDefinition> getReturnDefinitions() {
        return returnDefinitions;
    }

    public void setReturnDefinitions(Collection<ReturnDefinition> returnDefinitions) {
        this.returnDefinitions = returnDefinitions;
    }

    public Collection<Report> getReports() {
        return reports;
    }

    public void setReports(Collection<Report> reports) {
        this.reports = reports;
    }

    public Collection<Fi> getFis() {
        return fis;
    }

    public void setFis(Collection<Fi> fis) {
        this.fis = fis;
    }

    public Collection<UserReturnVersion> getReturnVersions() {
        return returnVersions;
    }

    public void setReturnVersions(Collection<UserReturnVersion> returnVersions) {
        this.returnVersions = returnVersions;
    }

    public Collection<UserMdt> getUserMdts() {
        return userMdts;
    }

    public void setUserMdts(Collection<UserMdt> userMdts) {
        this.userMdts = userMdts;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Description getContactPersonDescription() {
        return contactPersonDescription;
    }

    public void setContactPersonDescription(Description contactPerson) {
        this.contactPersonDescription = contactPerson;
    }

    public Description getContactPersonPosition() {
        return contactPersonPosition;
    }

    public void setContactPersonPosition(Description contactPersonPosition) {
        this.contactPersonPosition = contactPersonPosition;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public List<Matrix> getPermittedMatrixList() {
        return permittedMatrixList;
    }

    public void setPermittedMatrixList(List<Matrix> permittedMatrixList) {
        this.permittedMatrixList = permittedMatrixList;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((login == null) ? 0 : login.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        if (id != other.id)
            return false;
        if (login == null) {
            if (other.login != null)
                return false;
        } else if (!login.equals(other.login))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                '}';
    }
}
