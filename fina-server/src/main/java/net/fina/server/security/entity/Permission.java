package net.fina.server.security.entity;

import java.io.Serializable;

import jakarta.persistence.*;

import net.fina.auditlog.api.Audited;
import net.fina.server.i18n.helper.Description;

import org.hibernate.annotations.Type;

@Entity(name = "SYS_PERMISSIONS")
@Table(name = "SYS_PERMISSIONS")
@NamedQueries({@NamedQuery(name = "loadPermissions", query = "select p from SYS_PERMISSIONS as p "),
        @NamedQuery(name = "loadPermissionByIdName", query = "select p from SYS_PERMISSIONS as p where p.idName=:idName")})
public class Permission implements Serializable, Audited {

    @Id
    @SequenceGenerator(name = "sys_permissions_sequence", sequenceName = "sys_permissions_sequence", allocationSize = 1)
    @GeneratedValue(generator = "sys_permissions_sequence", strategy = GenerationType.SEQUENCE)
    @Column(name = "ID")
    private long id;

    @Column(name = "NAMESTRID")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    private Description description;

    @Column(name = "IDNAME", length = 80, unique = true, nullable = false)
    private String idName;

    public Permission() {
    }

    public Permission(long id) {
        this.id = id;
    }

    public Permission(String idName) {
        this.idName = idName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public String getIdName() {
        return idName;
    }

    public void setIdName(String idName) {
        this.idName = idName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((idName == null) ? 0 : idName.hashCode());
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
        Permission other = (Permission) obj;
        if (idName == null) {
            if (other.idName != null)
                return false;
        } else if (!idName.equals(other.idName))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Permission [id=" + id + ", description=" + description + ", idName=" + idName + "]";
    }
}
