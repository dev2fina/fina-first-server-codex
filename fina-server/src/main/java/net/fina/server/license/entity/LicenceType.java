package net.fina.server.license.entity;

import net.fina.auditlog.api.Audited;
import net.fina.server.i18n.helper.Description;
import org.hibernate.annotations.Type;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "IN_LICENCE_TYPES")
@Table(name = "IN_LICENCE_TYPES")
@NamedQueries({
        @NamedQuery(name = "loadlicensetype", query = "select r from IN_LICENCE_TYPES as r "),
        @NamedQuery(name = "checkLicenseTypeCodeUnique", query = "select l from IN_LICENCE_TYPES as l WHERE trim(l.code)=:code and l.id<>:id "),
        @NamedQuery(name = "getLicenceTypeByCode", query = "select l from IN_LICENCE_TYPES as l where trim(l.code)=:code")
})
public class LicenceType implements Serializable, Audited {

    @Id
    @SequenceGenerator(name = "in_license_types_sequence", sequenceName = "in_license_types_sequence", allocationSize = 1)
    @GeneratedValue(generator = "in_license_types_sequence", strategy = GenerationType.SEQUENCE)
    @Column(name = "ID")
    private long id;

    @Version
    @Column(name = "OPTLOCK")
    private Integer version;

    @Column(name = "CODE")
    private String code;

    @Column(name = "NAMESTRID")
    @Type(value = net.fina.server.i18n.helper.DescriptionUserType.class)
    protected Description description;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "LICENSE_TYPE_ID",referencedColumnName = "ID")
    private List<BankingOperation> operations;
    public LicenceType() {
    }

    public LicenceType(String code) {
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

    public List<BankingOperation> getOperations() {
        return operations == null ? new ArrayList<>() : operations;
    }

    public void setOperations(List<BankingOperation> operations) {
        this.operations = operations;
    }

    @Override
    public String toString() {
        return "LicenceType{" +
                "id=" + id +
                ", version=" + version +
                ", code='" + code + '\'' +
                ", description=" + description +
                '}';
    }


}
