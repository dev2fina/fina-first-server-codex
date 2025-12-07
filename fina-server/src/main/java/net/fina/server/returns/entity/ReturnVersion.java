package net.fina.server.returns.entity;

import net.fina.auditlog.api.Audited;
import net.fina.server.i18n.helper.Description;
import org.hibernate.annotations.Type;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity(name = "IN_RETURN_VERSIONS")
@Table(name = "IN_RETURN_VERSIONS")
@NamedQueries({
        @NamedQuery(name = "ReturnVersion.findAll", query = "select r from IN_RETURN_VERSIONS as r "),
        @NamedQuery(name = "ReturnVersion.findByCode", query = "select r FROM IN_RETURN_VERSIONS as r WHERE trim(r.code)=:code")
})
public class ReturnVersion implements Serializable, Audited {

    @Id
    @SequenceGenerator(name = "in_return_versions_sequence", sequenceName = "in_return_versions_sequence", allocationSize = 1)
    @GeneratedValue(generator = "in_return_versions_sequence", strategy = GenerationType.SEQUENCE)
    @Column(name = "ID")
    private long id;

    @Version
    @Column(name = "OPTLOCK")
    private Integer version;

    @Column(name = "CODE", length = 12, unique = true, nullable = false)
    private String code;

    @Column(name = "SEQUENCE")
    private long sequence;

    @Column(name = "DESCSTRID")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    private Description description;

    @Transient
    private Boolean canAmend;

    @Transient
    private boolean isUsersVersion;

    public ReturnVersion() {

    }

    public ReturnVersion(long id){
        this.id = id;
    }

    public ReturnVersion(ReturnVersion returnVersion, Boolean canAmend) {
        this(returnVersion.getId(), returnVersion.getVersion(), returnVersion.getCode(), returnVersion.getDescription());
        this.canAmend = canAmend;
    }

    public ReturnVersion(long id, Integer version, String code, Description description) {
        this(id, version);
        this.code = code;
        this.description = description;
    }

    public ReturnVersion(String code, Description description) {
        this.code = code;
        this.description = description;
    }

    public ReturnVersion(long id, Integer version) {
        this.id = id;
        this.version = version;
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

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Boolean isCanAmend() {
        return canAmend;
    }

    public void setCanAmend(Boolean canAmend) {
        this.canAmend = canAmend;
    }

    public boolean isUsersVersion() {
        return isUsersVersion;
    }

    public void setUsersVersion(boolean isUsersVersion) {
        this.isUsersVersion = isUsersVersion;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
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
        ReturnVersion other = (ReturnVersion) obj;
        if (id != other.id)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ReturnVersion{" +
                "id=" + id +
                ", version=" + version +
                ", code='" + code + '\'' +
                ", sequence=" + sequence +
                ", description=" + description +
                ", canAmend=" + canAmend +
                ", isUsersVersion=" + isUsersVersion +
                '}';
    }
}