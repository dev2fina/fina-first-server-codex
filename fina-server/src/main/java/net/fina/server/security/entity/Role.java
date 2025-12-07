package net.fina.server.security.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import net.fina.auditlog.api.Audited;
import net.fina.server.fi.entity.Fi;
import net.fina.server.i18n.helper.Description;
import net.fina.server.matrix.entity.Matrix;
import net.fina.server.mdt.entity.RoleMdt;
import net.fina.server.reports.entity.Report;
import net.fina.server.returns.entity.ReturnDefinition;
import net.fina.server.returns.entity.RoleReturnVersion;
import org.hibernate.annotations.Type;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@Entity(name = "SYS_ROLES")
@Table(name = "SYS_ROLES")
@NamedQueries({
        @NamedQuery(name = "loadRoles", query = "select r from SYS_ROLES as r order by r.code asc "),
        @NamedQuery(name = "checkCodeUnique", query = "select r from SYS_ROLES r WHERE trim(r.code)=:code and r.id<>:id "),
})
public class Role implements Serializable, Audited {

    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "sys_roles_sequence", sequenceName = "sys_roles_sequence", allocationSize = 1)
    @GeneratedValue(generator = "sys_roles_sequence", strategy = GenerationType.SEQUENCE)
    private long id;

    @Version
    @Column(name = "OPTLOCK")
    private Integer version;

    @Column(name = "CODE", length = 12, unique = true, nullable = false)
    private String code;

    @Column(name = "NAMESTRID")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    private Description description;

    @OneToMany
    @JoinTable(name = "SYS_ROLE_PERMISSIONS", joinColumns = @JoinColumn(name = "ROLEID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "PERMISSIONID", referencedColumnName = "ID"))
    private Collection<Permission> permissions;

    @ManyToMany
    @JoinTable(name = "SYS_USERS_ROLES", joinColumns = @JoinColumn(name = "ROLEID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "USERID", referencedColumnName = "ID"))
    private Collection<User> users;

    @JsonIgnore
    @OneToMany(mappedBy = "returnVersionId.role")
    private Collection<RoleReturnVersion> returnVersions;

    @OneToMany
    @JoinTable(name = "SYS_ROLE_RETURNS", joinColumns = @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "DEFINITION_ID", referencedColumnName = "ID"))
    private Collection<ReturnDefinition> returnDefinitions;

    @OneToMany
    @JoinTable(name = "SYS_ROLE_REPORTS", joinColumns = @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "REPORT_ID", referencedColumnName = "ID"))
    private Collection<Report> reports;

    @OneToMany
    @JoinTable(name = "SYS_ROLE_BANKS", joinColumns = @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "BANK_ID", referencedColumnName = "ID"))
    private Collection<Fi> fis;

    @JsonIgnore
    @OneToMany(mappedBy = "roleMdtId.role")
    private Collection<RoleMdt> roleMdts;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "SYS_ROLE_MATRIX_FILE_MAPPING", joinColumns = @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "MATRIX_ID", referencedColumnName = "ID"))
    private List<Matrix> permittedMatrixList;

    @Transient
    private long userCount;

    public Role() {
    }

    public Role(long id, Integer version, String code, Description description, long userCount) {
        this.id = id;
        this.version = version;
        this.code = code;
        this.description = description;
        this.userCount = userCount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public Collection<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Collection<Permission> permissions) {
        this.permissions = permissions;
    }

    public Collection<User> getUsers() {
        return users;
    }

    public void setUsers(Collection<User> users) {
        this.users = users;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Collection<RoleReturnVersion> getReturnVersions() {
        return returnVersions;
    }

    public void setReturnVersions(Collection<RoleReturnVersion> returnVersions) {
        this.returnVersions = returnVersions;
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

    public Collection<RoleMdt> getRoleMdts() {
        return roleMdts;
    }

    public void setRoleMdts(Collection<RoleMdt> roleMdts) {
        this.roleMdts = roleMdts;
    }

    public long getUserCount() {
        return userCount;
    }

    public void setUserCount(long userCount) {
        this.userCount = userCount;
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
        result = prime * result + ((code == null) ? 0 : code.hashCode());
        result = prime * result + (int) (id ^ (id >>> 32));
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
        Role other = (Role) obj;
        if (code == null) {
            if (other.code != null)
                return false;
        } else if (!code.equals(other.code))
            return false;
        if (id != other.id)
            return false;
        return true;
    }
}
