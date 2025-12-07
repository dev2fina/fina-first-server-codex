package net.fina.server.fi.entity;

import java.io.Serializable;

import jakarta.persistence.*;

import net.fina.auditlog.api.Audited;
import org.hibernate.annotations.Type;

import net.fina.server.i18n.helper.Description;

@Entity(name = "IN_BANK_TYPES")
@Table(name = "IN_BANK_TYPES")
@NamedQueries({
        @NamedQuery(name = "loadbanktypes", query = "select r from IN_BANK_TYPES as r "),
        @NamedQuery(name = "checkFiTypeCodeUnique", query = "select c from IN_BANK_TYPES as c WHERE trim(c.code)=:code and c.id<>:id ")
})
public class FiType implements Serializable, Audited {

    @Id
    @SequenceGenerator(name = "in_bank_types_sequence", sequenceName = "in_bank_types_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "in_bank_types_sequence")
    @Column(name = "ID")
    private long id;

    @Version
    @Column(name = "OPTLOCK")
    private int version;

    @Column(name = "CODE", length = 12, unique = true, nullable = false)
    private String code;

    @Column(name = "NAMESTRID")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    protected Description description;

    public FiType() {
    }

    public FiType(long id) {
        this.id = id;
    }

    public FiType(String code) {
        this.code = code;
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

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "FiType{" +
                "id=" + id +
                ", version=" + version +
                ", code='" + code + '\'' +
                ", description=" + description +
                '}';
    }
}
