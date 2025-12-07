package net.fina.server.returns.entity;

import net.fina.auditlog.api.Audited;
import net.fina.server.fi.entity.FiType;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Collection;

@Entity(name = "IN_PACKAGES")
@Table(name = "IN_PACKAGES")
@NamedQueries({
        @NamedQuery(name = "checkPackageCodeUnique", query = "select p from IN_PACKAGES as p WHERE trim(p.code)=:code and p.id<>:id")
})
public class ReturnPackage implements Serializable, Audited {

    @Id
    @SequenceGenerator(name = "in_packages_sequence", sequenceName = "in_packages_sequence", allocationSize = 1)
    @GeneratedValue(generator = "in_packages_sequence", strategy = GenerationType.SEQUENCE)
    @Column(name = "ID")
    private long id;
    private String code;
    private String note;

    @OneToMany
    @JoinTable(name = "IN_PACKAGE_RETURNS", joinColumns = @JoinColumn(name = "PACKAGE_ID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "RETURN_DEFINITION_ID", referencedColumnName = "ID"))
    private Collection<ReturnDefinition> returnDefinitions;

    @OneToMany
    @JoinTable(name = "IN_PACKAGE_FITYPES", joinColumns = @JoinColumn(name = "PACKAGE_ID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "FI_TYPE_ID", referencedColumnName = "ID"))
    private Collection<FiType> fiTypes;

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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Collection<ReturnDefinition> getReturnDefinitions() {
        return returnDefinitions;
    }

    public void setReturnDefinitions(Collection<ReturnDefinition> returnDefinitions) {
        this.returnDefinitions = returnDefinitions;
    }

    public Collection<FiType> getFiTypes() {
        return fiTypes;
    }

    public void setFiTypes(Collection<FiType> fiTypes) {
        this.fiTypes = fiTypes;
    }

}
