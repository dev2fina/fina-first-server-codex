package net.fina.server.fi.entity;

import net.fina.auditlog.api.Audited;
import net.fina.server.i18n.helper.Description;
import org.hibernate.annotations.Type;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity(name = "IN_MANAGING_BODIES")
@Table(name = "IN_MANAGING_BODIES")
@NamedQueries({
        @NamedQuery(name = "loadmanagement", query = "select r from IN_MANAGING_BODIES as r "),
        @NamedQuery(name = "checkManagementCodeUnique", query = "select c from IN_MANAGING_BODIES as c WHERE trim(c.code)=:code and c.id<>:id ")
})
public class Management implements Serializable, Audited {

    @Id
    @SequenceGenerator(name = "in_managing_bodies_sequence", sequenceName = "in_managing_bodies_sequence", allocationSize = 1)
    @GeneratedValue(generator = "in_managing_bodies_sequence", strategy = GenerationType.SEQUENCE)
    @Column(name = "ID")
    private long id;

    @Version
    @Column(name = "OPTLOCK")
    private Integer version;

    @Column(name = "CODE", length = 12, unique = true, nullable = false)
    private String code;

    @Column(name = "POSTSTRID")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    protected Description description;

    @Column(name = "JSON_CONFIG")
    private String jsonConfig;

    public Management() {
    }

    public Management(String code) {
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

    public String getJsonConfig() {
        return jsonConfig;
    }

    public void setJsonConfig(String jsonConfig) {
        this.jsonConfig = jsonConfig;
    }

    @Override
    public String toString() {
        return "Management{" +
                "id=" + id +
                ", version=" + version +
                ", code='" + code + '\'' +
                ", description=" + description +
                '}';
    }
}
