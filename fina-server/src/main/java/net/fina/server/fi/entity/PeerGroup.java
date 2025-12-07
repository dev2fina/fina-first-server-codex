package net.fina.server.fi.entity;

import java.io.Serializable;
import jakarta.persistence.*;

import net.fina.auditlog.api.Audited;
import net.fina.server.i18n.helper.Description;

import org.hibernate.annotations.Type;

@Entity(name = "IN_BANK_GROUPS")
@Table(name = "IN_BANK_GROUPS")
@NamedQueries({
        @NamedQuery(name = "checkPeerGroupCodeUnique", query = "select c from IN_BANK_GROUPS as c WHERE trim(c.code)=:code and c.id<>:id ")
})
public class PeerGroup implements Serializable, Audited {
    @Id
    @SequenceGenerator(name = "in_bank_groups_sequence", sequenceName = "in_bank_groups_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "in_bank_groups_sequence")
    @Column(name = "ID")
    private long id;

    @Version
    @Column(name = "OPTLOCK")
    private int version;

    @Column(name = "CODE", length = 12, nullable = false, unique = true)
    private String code;

    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    @Column(name = "NAMESTRID")
    private Description description;

    @Column(name = "CRITERIONID")
    private long parentId;

    @Transient
    private boolean isCurrentFisGroup;

    public PeerGroup() {
    }

    public PeerGroup(long id, String code, Description description) {
        this.id = id;
        this.code = code;
        this.description = description;
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

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public boolean isCurrentFisGroup() {
        return isCurrentFisGroup;
    }

    public void setCurrentFisGroup(boolean isCurrentFisGroup) {
        this.isCurrentFisGroup = isCurrentFisGroup;
    }

    @Override
    public String toString() {
        return "PeerGroup{" +
                "id=" + id +
                ", version=" + version +
                ", code='" + code + '\'' +
                ", description=" + description +
                ", parentId=" + parentId +
                ", isCurrentFisGroup=" + isCurrentFisGroup +
                '}';
    }
}
