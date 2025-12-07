package net.fina.server.returns.entity;

import jakarta.persistence.*;
import net.fina.auditlog.api.Audited;
import net.fina.server.i18n.helper.Description;
import org.hibernate.annotations.Type;

import java.io.Serializable;
import java.util.Objects;

@Entity(name = "IN_RETURN_TYPES")
@Table(name = "IN_RETURN_TYPES")
@NamedQueries({@NamedQuery(name = "loadReturnTypes", query = "select r from IN_RETURN_TYPES as r ")})
public class ReturnType implements Serializable, Audited {

    @Id
    @SequenceGenerator(name = "in_return_types_sequence", sequenceName = "in_return_types_sequence", allocationSize = 1)
    @GeneratedValue(generator = "in_return_types_sequence", strategy = GenerationType.SEQUENCE)
    @Column(name = "ID")
    private long id;

    @Version
    @Column(name = "OPTLOCK")
    private Integer version;

    @Column(name = "CODE", length = 12, unique = true, nullable = false)
    private String code;

    @Column(name = "NAMESTRID")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    protected Description description;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "FORMAT")
    private byte[] format;

    private boolean excelTemplate;

    public ReturnType() {
    }

    public ReturnType(String code) {
        this.code = code;
    }

    public ReturnType(long id, String code, int version) {
        this.id = id;
        this.code = code;
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

    public byte[] getFormat() {
        return format;
    }

    public void setFormat(byte[] format) {
        this.format = format;
    }

    public boolean isExcelTemplate() {
        return excelTemplate;
    }

    public void setExcelTemplate(boolean excelTemplate) {
        this.excelTemplate = excelTemplate;
    }

    @Override
    public String toString() {
        return "ReturnType{" +
                "id=" + id +
                ", version=" + version +
                ", code='" + code + '\'' +
                ", description=" + description +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ReturnType that = (ReturnType) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
