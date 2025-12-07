package net.fina.server.fi.entity;

import java.io.Serializable;
import jakarta.persistence.*;

import net.fina.auditlog.api.Audited;
import net.fina.server.i18n.helper.Description;

import org.hibernate.annotations.Type;

@Entity(name = "IN_CRITERION")
@Table(name = "IN_CRITERION")
@NamedQueries({
        @NamedQuery(name = "checkCriterionCodeUnique", query = "select c from IN_CRITERION as c WHERE trim(c.code)=:code and c.id<>:id ")
})
public class Criterion implements Serializable, Audited {
    @Id
    @SequenceGenerator(name = "in_criterion_sequence", sequenceName = "in_criterion_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "in_criterion_sequence")
    @Column(name = "ID")
    private long id;

    @Column(name = "CODE", length = 12, nullable = false, unique = true)
    private String code;

    @Column(name = "NAMESTRID")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    private Description description;

    @Column(name = "ISDEFAULT")
    private Boolean isDefault;

    @Version
    @Column(name = "OPTLOCK")
    private Integer version;

    public Criterion() {
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

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    @Override
    public String toString() {
        return "Criterion{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", description=" + description +
                ", isDefault=" + isDefault +
                ", version=" + version +
                '}';
    }
}
